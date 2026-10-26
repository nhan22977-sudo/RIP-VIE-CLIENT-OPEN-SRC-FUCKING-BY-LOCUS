package me.drake.vieclient.auth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import me.drake.vieclient.CoreEntry;
import me.drake.vieclient.core.i18n.Localization;
import me.drake.vieclient.internal.VieWebSocketClient;
import me.drake.vieclient.security.ChimeraEngine;
import net.minecraft.class_2561;
import net.minecraft.class_310;

public class AuthManager {
   public static final String DEFAULT_API_URL = "https://vieservices.top/api";
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(15L);
   private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(20L);
   private static final long REVALIDATE_INTERVAL_MS = 60000L;
   private static final long CHALLENGE_TIMEOUT_TOLERANCE_MS = 30000L;
   private static final long OFFLINE_GRACE_MS = 7200000L;
   private static final long AUTH_SOURCE_PROBE_INTERVAL_MS = 2500L;
   private static final long WS_INIT_COOLDOWN_MS = 10000L;
   private static final SecureRandom RNG = new SecureRandom();
   private static final int STORAGE_SALT_BYTES = 16;
   private static final int STORAGE_IV_BYTES = 12;
   private static final int STORAGE_KEY_BITS = 256;
   private static final int STORAGE_KDF_ITERATIONS = 120000;
   private static final byte[] ED25519_SPKI_PREFIX = HexFormat.of()
      .parseHex(
         "302a300506032b6570032100"
      );
   private static final Pattern KEY_CODE_PATTERN = Pattern.compile(
      "(?i)\\bVIE-[A-Z0-9]+-[A-F0-9]{6}\\b"
   );
   private ChimeraEngine networkChimeraEngine;
   private VieWebSocketClient wsClient;
   private boolean wsEnabled = true;
   private boolean wsConnected = false;
   private final Path authDir;
   private final Path[] authImportDirs;
   private final HttpClient httpClient;
   private final String hwid;
   private final boolean requireSignedSessions;
   private final boolean allowInsecureHttp;
   private final String authSigningPublicKey;
   private final PublicKey verifierPublicKey;
   private static final String MISSING_KEY_MESSAGE = "No local key cached yet. Waiting for HWID authorization.";
   public static final String DEFAULT_PROD_ED25519_PUBLIC_KEY = "e8d4c5797692d8ae885d773687081d15657799c33cd4f98eb1057bff3ceea0dd";
   private static AuthManager INSTANCE;
   private volatile AuthSessionV3.Claims verifiedClaims;
   private final byte[] memoryMacKey = new byte[32];
   private volatile byte[] memoryStateHmac = new byte[32];
   private String apiBaseUrl;
   private String keyCode;
   private AuthManager.Status status = AuthManager.Status.MISSING_KEY;
   private String message = "No local key cached yet. Waiting for HWID authorization.";
   private String plan = "";
   private String customerName = "";
   private String discordUserId = "";
   private String discordUserName = "";
   private String discordUserTag = "";
   private Instant expiresAt;
   private long lastValidatedAtMs = 0L;
   private long lastAttemptAtMs = 0L;
   private boolean initialized = false;
   private boolean wasAuthorized = false;
   private boolean lockedDown = false;
   private volatile boolean requestInFlight = false;
   private String cachedSessionToken = "";
   private Instant cachedSessionExpiresAt;
   private long lastAuthSourceProbeAtMs = 0L;
   private long lastWsInitAttemptAtMs = 0L;
   private String observedDownloadCandidatePath = "";
   private long observedDownloadCandidateModifiedMs = Long.MIN_VALUE;
   private boolean plaintextCompatWarned = false;
   private long lastWallClockMs = System.currentTimeMillis();
   private long lastMonotonicNano = System.nanoTime();
   private final CountDownLatch initialValidationLatch = new CountDownLatch(1);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static synchronized AuthManager getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new AuthManager();
      }

      return INSTANCE;
   }

   public AuthManager() {
      Path var1;
      try {
         class_310 var2 = class_310.method_1551();
         var1 = var2 != null && var2.field_1697 != null ? var2.field_1697.toPath() : Path.of(".");
      } catch (Throwable var3) {
         var1 = Path.of(".");
      }

      this.authDir = var1.resolve("vieclient");
      this.authImportDirs = resolveAuthImportDirs(var1);
      this.httpClient = HttpClient.newBuilder().version(Version.HTTP_1_1).connectTimeout(CONNECT_TIMEOUT).build();
      this.hwid = computeMachineFingerprint();
      this.apiBaseUrl = resolveApiBaseUrl(null);
      this.authSigningPublicKey = resolveAuthSigningPublicKey();
      this.requireSignedSessions = true;
      this.allowInsecureHttp = resolveAllowInsecureHttp();
      this.verifierPublicKey = parseConfiguredPublicKey(this.authSigningPublicKey);
      RNG.nextBytes(this.memoryMacKey);
      this.updateMemoryMac();
      this.networkChimeraEngine = buildNetworkChimeraEngine();
      String var4 = System.getenv(
         "VIE_WEBSOCKET_ENABLED"
      );
      if (var4 != null) {
         this.wsEnabled = Boolean.parseBoolean(var4);
      }
   }

   private static ChimeraEngine buildNetworkChimeraEngine() {
      return ChimeraEngine.configuredMasterKey().map(ChimeraEngine::new).orElse(null);
   }

   public boolean awaitInitialValidation(long timeoutMs) {
      try {
         return this.initialValidationLatch.await(timeoutMs, TimeUnit.MILLISECONDS);
      } catch (InterruptedException var4) {
         Thread.currentThread().interrupt();
         return false;
      }
   }

   public synchronized void initialize() {
      AuthManager.KeyBootstrapCandidate var1 = this.pickNewestKeyBootstrapCandidate(
         this.findLatestLicenseKeyCandidate(), this.findCurrentModJarKeyCandidate(), this.findLatestCoreEntryJarKeyCandidate()
      );
      if (var1 != null && !var1.keyCode().isBlank()) {
         this.keyCode = var1.keyCode();
         this.status = AuthManager.Status.MISSING_KEY;
         this.updateMemoryMac();
         CoreEntry.LOGGER
            .info(
               "[VieClient] Loaded auth key from local source: {}",
               this.keyCode
            );
         Thread var2 = new Thread(() -> {
            try {
               Thread.sleep(1000L);
               this.initializeWebSocket();
            } catch (InterruptedException var2x) {
               Thread.currentThread().interrupt();
            }
         }, "vie-ws-init");
         var2.setDaemon(true);
         var2.start();
         Thread var3 = new Thread(() -> {
            try {
               this.validateStoredKey();
            } finally {
               this.initialValidationLatch.countDown();
            }
         }, "vie-auth-init");
         var3.setDaemon(true);
         var3.start();
         this.initialized = true;
      } else {
         CoreEntry.LOGGER
            .error(
               "[VieClient] No valid key found in JAR filename or license.txt"
            );
         CoreEntry.LOGGER
            .error(
               "[VieClient] License key required for auth, but continuing in degraded mode."
            );
         this.keyCode = "";
         this.status = AuthManager.Status.MISSING_KEY;
         this.message = "No license key found. Please ensure your JAR filename contains a valid key.";
         this.updateMemoryMac();
         this.initialValidationLatch.countDown();
      }
   }

   public synchronized void tick() {
      if (this.initialized) {
         if (!this.lockedDown) {
            this.checkWebSocketConnection();
            long var1 = System.currentTimeMillis();
            long var3 = System.nanoTime();
            if (var1 < this.lastWallClockMs - 5000L) {
               CoreEntry.LOGGER
                  .error(
                     "[VieClient] Clock rollback detected! Wall clock jumped backward: was {}, now {}",
                     this.lastWallClockMs,
                     var1
                  );
               this.forceLockdown(
                  "System clock rollback detected. Client locked."
               );
            } else {
               long var5 = (var3 - this.lastMonotonicNano) / 1000000L;
               long var7 = var1 - this.lastWallClockMs;
               if (var7 >= -2000L && (var5 <= 10000L || var7 >= 0L)) {
                  if (this.verifiedClaims != null && this.verifiedClaims.issuedAt() != null && var1 < this.verifiedClaims.issuedAt().toEpochMilli() - 60000L) {
                     CoreEntry.LOGGER
                        .error(
                           "[VieClient] Clock tampering detected: current time is before token issue time!"
                        );
                     this.forceLockdown(
                        "Clock set before license issuance time. Client locked."
                     );
                  } else {
                     this.lastWallClockMs = var1;
                     this.lastMonotonicNano = var3;
                     if (this.wasAuthorized && this.lastValidatedAtMs > 0L) {
                        if (this.verifiedClaims != null && this.verifiedClaims.isExpiredAt(Instant.ofEpochMilli(var1))) {
                           CoreEntry.LOGGER
                              .warn(
                                 "[VieClient] Server-signed license lease expired at {}. Revoking authorization.",
                                 this.verifiedClaims.expiresAt()
                              );
                           this.applyState(
                              AuthManager.Status.EXPIRED,
                              "License lease expired.",
                              this.plan,
                              this.customerName,
                              this.expiresAt,
                              true
                           );
                        } else if (var1 - this.lastValidatedAtMs > 7200000L) {
                           CoreEntry.LOGGER
                              .warn(
                                 "[VieClient] Offline grace window (2h) expired (last validated {} ms ago). Revoking authorization.",
                                 var1 - this.lastValidatedAtMs
                              );
                           this.applyState(
                              AuthManager.Status.EXPIRED,
                              "Offline grace period (2h) expired. Internet connection required.",
                              this.plan,
                              this.customerName,
                              this.expiresAt,
                              true
                           );
                        }
                     }

                     if (var1 - this.lastAttemptAtMs >= 60000L) {
                        CoreEntry.LOGGER
                           .info(
                              "[VieClient] Dispatching periodic 10-minute auth challenge check to server..."
                           );
                        this.validateStoredKeyAsync();
                     }
                  }
               } else {
                  CoreEntry.LOGGER
                     .error(
                        "[VieClient] Monotonic clock mismatch detected! nanoMs={}, wallMs={}",
                        var5,
                        var7
                     );
                  this.forceLockdown(
                     "System clock manipulation detected. Client locked."
                  );
               }
            }
         }
      }
   }

   public synchronized AuthManager.StatusSnapshot setKeyAndActivate(String rawKey) {
      if (this.lockedDown) {
         this.applyState(
            AuthManager.Status.INVALID,
            "Client is locked by security policy. Restart required.",
            this.plan,
            this.customerName,
            this.expiresAt,
            true
         );
         return this.getStatusSnapshot();
      } else {
         String var2 = normalizeKeyCode(rawKey);
         if (var2.isEmpty()) {
            this.applyState(
               AuthManager.Status.INVALID,
               "Key is empty.",
               "",
               "",
               null,
               true
            );
            return this.getStatusSnapshot();
         } else {
            this.keyCode = var2;
            this.cachedSessionToken = "";
            this.cachedSessionExpiresAt = null;
            this.updateMemoryMac();
            this.disconnectWebSocket();
            this.initializeWebSocket();
            return this.executeAuthRequest(
               "/v1/auth/activate"
            );
         }
      }
   }

   public AuthManager.StatusSnapshot validateStoredKey() {
      if (this.lockedDown) {
         this.applyState(
            AuthManager.Status.INVALID,
            "Client is locked by security policy. Restart required.",
            this.plan,
            this.customerName,
            this.expiresAt,
            true
         );
         return this.getStatusSnapshot();
      }

      if (!this.hasSavedKey()) {
         synchronized (this) {
            this.status = AuthManager.Status.MISSING_KEY;
            this.message = "No license key found. Please ensure your JAR filename contains a valid key.";
            this.updateMemoryMac();
         }

         return this.getStatusSnapshot();
      } else {
         return this.executeAuthRequest(
            "/v1/auth/activate"
         );
      }
   }

   public synchronized void requestImmediateValidation() {
      if (!this.lockedDown && this.initialized && !this.requestInFlight) {
         this.lastAttemptAtMs = 0L;
         this.validateStoredKeyAsync();
      }
   }

   public synchronized void clearKey() {
      this.keyCode = "";
      this.lastValidatedAtMs = 0L;
      this.cachedSessionToken = "";
      this.cachedSessionExpiresAt = null;
      this.verifiedClaims = null;
      this.disconnectWebSocket();
      this.applyState(
         AuthManager.Status.MISSING_KEY,
         "No local key cached yet. Waiting for HWID authorization.",
         "",
         "",
         null,
         true
      );
   }

   public synchronized void forceLockdown(String reason) {
      this.lockedDown = true;
      this.cachedSessionToken = "";
      this.cachedSessionExpiresAt = null;
      this.verifiedClaims = null;
      this.lastValidatedAtMs = 0L;
      this.disconnectWebSocket();
      String var2;
      if (reason == null) {
         byte[] var10002 = new byte[33];
         var10002[0] = (byte)(92);
         var10002[1] = (byte)(-9);
         var10002[2] = (byte)(117);
         var10002[3] = (byte)(-69);
         var10002[4] = (byte)(-62);
         var10002[5] = 1;
         var10002[6] = (byte)(100);
         var10002[7] = (byte)(-14);
         var10002[8] = (byte)(24);
         var10002[9] = (byte)(-64);
         var10002[10] = (byte)(-113);
         var10002[11] = (byte)(-109);
         var10002[12] = (byte)(-112);
         var10002[13] = (byte)(-115);
         var10002[14] = (byte)(14);
         var10002[15] = (byte)(-97);
         var10002[16] = (byte)(111);
         var10002[17] = (byte)(56);
         var10002[18] = (byte)(-87);
         var10002[19] = (byte)(-51);
         var10002[20] = (byte)(105);
         var10002[21] = (byte)(-9);
         var10002[22] = (byte)(125);
         var10002[23] = (byte)(-70);
         var10002[24] = (byte)(-34);
         var10002[25] = (byte)(51);
         var10002[26] = (byte)(-28);
         var10002[27] = (byte)(41);
         var10002[28] = (byte)(72);
         var10002[29] = (byte)(-108);
         var10002[30] = (byte)(95);
         var10002[31] = (byte)(111);
         var10002[32] = (byte)(-47);
         var2 = $$v_s(var10002, -5312089127528443590L);
      } else {
         var2 = reason;
      }

      this.applyState(AuthManager.Status.INVALID, var2, this.plan, this.customerName, this.expiresAt, true);
   }

   public synchronized void kickOutdatedClient(String reason) {
      this.lockedDown = true;
      this.status = AuthManager.Status.OUTDATED_CLIENT;
      this.cachedSessionToken = "";
      this.cachedSessionExpiresAt = null;
      this.verifiedClaims = null;
      this.lastValidatedAtMs = 0L;
      boolean var2 = Localization.isVietnamese();
      this.message = reason != null && !reason.isBlank()
         ? reason
         : (
            var2
               ? "Phiên bản Vie Client của bạn đã cũ! Vui lòng tải bản mới nhất từ website hoặc Discord."
               : "Vie Client version is outdated. Update required to play."
         );
      this.updateMemoryMac();
      CoreEntry var3 = CoreEntry.getInstance();
      if (var3 != null && var3.getModuleManager() != null) {
         var3.getModuleManager().disableAllModules();
      }

      this.disconnectWebSocket();
      class_310 var4 = class_310.method_1551();
      if (var4 != null) {
         var4.execute(
            () -> {
               try {
                  if (var4.method_1562() != null && var4.method_1562().method_48296() != null) {
                     var4.method_1562()
                        .method_48296()
                        .method_10747(
                           class_2561.method_43470(
                              var2
                                 ? "§d§l[Vie Client] §c§lYÊU CẦU CẬP NHẬT PHIÊN BẢN MỚI

§fPhiên bản bạn đang sử dụng đã cũ và không còn được hỗ trợ.
§eMáy chủ vừa phát hành bản cập nhật mới.

§a👉 Vui lòng truy cập website: §bhttps://vieservices.top
§a👉 Hoặc kênh Discord: §bhttps://discord.gg/vieclient §ađể tải bản mới nhất!"
                                 : "§d§l[Vie Client] §c§lUPDATE REQUIRED

§fYour client version is outdated and no longer supported.
§eA new version has been deployed on the server.

§a👉 Please download the latest update at: §bhttps://vieservices.top
§a👉 Or join our Discord: §bhttps://discord.gg/vieclient"
                           )
                        );
                  }
               } catch (Throwable var3x) {
                  CoreEntry.LOGGER
                     .error(
                        "[VieClient] Error during disconnect on outdated client",
                        var3x
                     );
               }
            }
         );
      }
   }

   public synchronized void updateMemoryMac() {
      try {
         byte[] var10000 = new byte[10];
         var10000[0] = (byte)(87);
         var10000[1] = (byte)(-10);
         var10000[2] = (byte)(125);
         var10000[3] = (byte)(-67);
         var10000[4] = (byte)(-1);
         var10000[5] = (byte)(61);
         var10000[6] = (byte)(5);
         var10000[7] = (byte)(-84);
         var10000[8] = (byte)(66);
         var10000[9] = (byte)(-107);
         Mac var1 = Mac.getInstance($$v_s(var10000, -5312089127528443590L));
         byte[] var10004 = new byte[10];
         var10004[0] = (byte)(87);
         var10004[1] = (byte)(-10);
         var10004[2] = (byte)(125);
         var10004[3] = (byte)(-67);
         var10004[4] = (byte)(-1);
         var10004[5] = (byte)(61);
         var10004[6] = (byte)(5);
         var10004[7] = (byte)(-84);
         var10004[8] = (byte)(66);
         var10004[9] = (byte)(-107);
         var1.init(new SecretKeySpec(this.memoryMacKey, $$v_s(var10004, -5312089127528443590L)));
         String var6;
         if (this.verifiedClaims == null) {
            var10000 = new byte[4];
            var10000[0] = (byte)(113);
            var10000[1] = (byte)(-18);
            var10000[2] = (byte)(112);
            var10000[3] = (byte)(-78);
            var6 = $$v_s(var10000, -5312089127528443590L);
         } else {
            var6 = this.verifiedClaims.subjectKey() + "|" + this.verifiedClaims.tier() + "|" + this.verifiedClaims.nonce();
         }

         String var2 = var6;
         String var3 = this.status + "|" + (this.keyCode == null ? "" : this.keyCode) + "|" + this.lockedDown + "|" + var2;
         this.memoryStateHmac = var1.doFinal(var3.getBytes(StandardCharsets.UTF_8));
      } catch (Exception var4) {
         this.lockedDown = true;
      }
   }

   public synchronized boolean verifyMemoryIntegrity() {
      try {
         byte[] var10000 = new byte[10];
         var10000[0] = (byte)(87);
         var10000[1] = (byte)(-10);
         var10000[2] = (byte)(125);
         var10000[3] = (byte)(-67);
         var10000[4] = (byte)(-1);
         var10000[5] = (byte)(61);
         var10000[6] = (byte)(5);
         var10000[7] = (byte)(-84);
         var10000[8] = (byte)(66);
         var10000[9] = (byte)(-107);
         Mac var1 = Mac.getInstance($$v_s(var10000, -5312089127528443590L));
         byte[] var10004 = new byte[10];
         var10004[0] = (byte)(87);
         var10004[1] = (byte)(-10);
         var10004[2] = (byte)(125);
         var10004[3] = (byte)(-67);
         var10004[4] = (byte)(-1);
         var10004[5] = (byte)(61);
         var10004[6] = (byte)(5);
         var10004[7] = (byte)(-84);
         var10004[8] = (byte)(66);
         var10004[9] = (byte)(-107);
         var1.init(new SecretKeySpec(this.memoryMacKey, $$v_s(var10004, -5312089127528443590L)));
         String var7;
         if (this.verifiedClaims == null) {
            var10000 = new byte[4];
            var10000[0] = (byte)(113);
            var10000[1] = (byte)(-18);
            var10000[2] = (byte)(112);
            var10000[3] = (byte)(-78);
            var7 = $$v_s(var10000, -5312089127528443590L);
         } else {
            var7 = this.verifiedClaims.subjectKey() + "|" + this.verifiedClaims.tier() + "|" + this.verifiedClaims.nonce();
         }

         String var2 = var7;
         String var3 = this.status + "|" + (this.keyCode == null ? "" : this.keyCode) + "|" + this.lockedDown + "|" + var2;
         byte[] var4 = var1.doFinal(var3.getBytes(StandardCharsets.UTF_8));
         return MessageDigest.isEqual(this.memoryStateHmac, var4);
      } catch (Exception var5) {
         return false;
      }
   }

   public synchronized boolean isAuthorized() {
      if (!this.verifyMemoryIntegrity()) {
         byte[] var10001 = new byte[30];
         var10001[0] = (byte)(82);
         var10001[1] = (byte)(-2);
         var10001[2] = (byte)(113);
         var10001[3] = (byte)(-79);
         var10001[4] = (byte)(-34);
         var10001[5] = (byte)(12);
         var10001[6] = (byte)(100);
         var10001[7] = (byte)(-9);
         var10001[8] = (byte)(25);
         var10001[9] = (byte)(-41);
         var10001[10] = (byte)(-127);
         var10001[11] = (byte)(-111);
         var10001[12] = (byte)(-122);
         var10001[13] = (byte)(-60);
         var10001[14] = (byte)(24);
         var10001[15] = (byte)(-97);
         var10001[16] = (byte)(111);
         var10001[17] = (byte)(40);
         var10001[18] = (byte)(-92);
         var10001[19] = (byte)(-53);
         var10001[20] = (byte)(127);
         var10001[21] = (byte)(-18);
         var10001[22] = (byte)(52);
         var10001[23] = (byte)(-88);
         var10001[24] = (byte)(-58);
         var10001[25] = (byte)(122);
         var10001[26] = (byte)(-8);
         var10001[27] = (byte)(35);
         var10001[28] = (byte)(64);
         var10001[29] = (byte)(-45);
         this.forceLockdown($$v_s(var10001, -5312089127528443590L));
         return false;
      } else {
         return !this.lockedDown && this.status == AuthManager.Status.VALID;
      }
   }

   public synchronized String getCachedSessionToken() {
      return this.cachedSessionToken;
   }

   public synchronized boolean isCategoryEntitled(String category) {
      return true;
   }

   public boolean canEnableProtectedModules() {
      return true;
   }

   public void checkModuleAccess(String moduleName, Consumer callback) {
      if (this.initialized && !this.lockedDown && this.status == AuthManager.Status.VALID && this.hasSavedKey()) {
         if (this.wsClient != null && this.wsClient.isConnected()) {
            this.wsClient.sendModuleCheck(moduleName, callback);
         } else {
            callback.accept(false);
         }
      } else {
         callback.accept(false);
      }
   }

   public synchronized String getLockMessageForUi() {
      if (this.lockedDown) {
         return "Client is locked by security policy. Restart required.";
      }

      return switch (this.status) {
         case MISSING_KEY -> "Waiting for HWID authorization.";
         case INVALID -> "HWID is not authorized. Redeem/download via Discord.";
         case OUTDATED_CLIENT -> "Client is outdated. Please use the latest build.";
         case EXPIRED -> "License expired.";
         case REVOKED -> "License revoked.";
         case HWID_MISMATCH -> "HWID mismatch. Request reset from support.";
         case RATE_LIMITED -> "Auth rate-limited. Please wait a moment.";
         case SERVER_ERROR -> "Auth server unavailable.";
         default -> "Module is locked by auth policy.";
      };
   }

   public synchronized String getCustomerName() {
      return this.customerName;
   }

   public synchronized String getDiscordUserId() {
      return this.discordUserId;
   }

   public synchronized String getDiscordUserName() {
      return this.discordUserName;
   }

   public synchronized String getDiscordUserTag() {
      return this.discordUserTag;
   }

   public synchronized AuthManager.Status getStatus() {
      return AuthManager.Status.VALID;
   }

   public synchronized boolean isLockedDown() {
      return false;
   }

   public synchronized String getDiscordDisplayName() {
      if (this.discordUserName != null && !this.discordUserName.isBlank()) {
         return "@" + this.discordUserName;
      }

      if (this.discordUserTag != null && !this.discordUserTag.isBlank()) {
         return "@" + this.discordUserTag;
      }

      if (this.customerName != null && !this.customerName.isBlank()) {
         return "@" + this.customerName;
      }

      byte[] var10000 = new byte[9];
      var10000[0] = (byte)(94);
      var10000[1] = (byte)(-11);
      var10000[2] = (byte)(115);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(-43);
      var10000[5] = (byte)(24);
      var10000[6] = (byte)(43);
      var10000[7] = (byte)(-21);
      var10000[8] = (byte)(4);
      return $$v_s(var10000, -5312089127528443590L);
   }

   public synchronized AuthManager.StatusSnapshot getStatusSnapshot() {
      return new AuthManager.StatusSnapshot(
         this.status == AuthManager.Status.VALID,
         this.status.name().toLowerCase(Locale.ROOT),
         this.message,
         this.keyCode == null ? "" : this.keyCode,
         this.hwid,
         this.apiBaseUrl,
         this.customerName,
         this.plan,
         this.expiresAt,
         this.lastValidatedAtMs
      );
   }

   private AuthManager.StatusSnapshot executeAuthRequest(String endpointPath) {
      this.lastAttemptAtMs = System.currentTimeMillis();
      if (this.requireSignedSessions && this.verifierPublicKey == null) {
         this.applyState(
            AuthManager.Status.SERVER_ERROR,
            "Signed auth is required but no public key is configured.",
            this.plan,
            this.customerName,
            this.expiresAt,
            true
         );
         return this.getStatusSnapshot();
      }

      if (!this.isApiTransportAllowed(this.apiBaseUrl)) {
         this.applyState(
            AuthManager.Status.SERVER_ERROR,
            "Blocked insecure auth URL. Use HTTPS (or localhost) for auth API.",
            this.plan,
            this.customerName,
            this.expiresAt,
            true
         );
         return this.getStatusSnapshot();
      }

      try {
         String var2 = generateRandomNonce();
         JsonObject var40 = new JsonObject();
         var40.addProperty("action", endpointPath);
         var40.addProperty("key", this.keyCode);
         var40.addProperty("hwid", this.hwid);
         var40.addProperty(
            "clientVersion",
            "1.0.1"
         );
         var40.addProperty("nonce", var2);
         JsonObject var41 = new JsonObject();
         var41.addProperty("key", this.keyCode);
         var41.addProperty("hwid", this.hwid);
         var41.addProperty(
            "clientVersion",
            "1.0.1"
         );
         var41.addProperty("nonce", var2);
         boolean var5 = isPlaintextCompatAllowed(this.apiBaseUrl);
         if (this.networkChimeraEngine == null && !var5) {
            this.applyState(
               AuthManager.Status.SERVER_ERROR,
               "Encrypted auth requires VIE_CHIMERA_KEY for this endpoint.",
               this.plan,
               this.customerName,
               this.expiresAt,
               true
            );
            return this.getStatusSnapshot();
         }

         boolean var42 = this.networkChimeraEngine != null && !var5;
         if (var5 && !this.plaintextCompatWarned) {
            this.plaintextCompatWarned = true;
            CoreEntry.LOGGER
               .warn(
                  "[VieClient] Using trusted plaintext auth transport for configured endpoint."
               );
         }

         String var7 = var42 ? this.apiBaseUrl + "/v2/chimera" : this.apiBaseUrl + endpointPath;
         String var8 = GSON.toJson(var42 ? var40 : var41);
         if (var42) {
            try {
               ChimeraEngine.GenomePacket var9 = this.networkChimeraEngine.encode(var8);
               JsonObject var10 = new JsonObject();
               var10.addProperty("sequence", var9.sequence());
               JsonObject var11 = new JsonObject();
               var11.addProperty("locus", var9.locus());
               var11.addProperty(
                  "protocol",
                  "CHIMERA-PROTECT"
               );
               var10.add("metadata", var11);
               JsonObject var12 = new JsonObject();
               var12.add("genome_packet", var10);
               var8 = GSON.toJson(var12);
            } catch (Exception var35) {
               throw new SecurityException(
                  "Security violation: Failed to encrypt payload. Aborting request to prevent plaintext leakage: " + var35.getMessage(), var35
               );
            }
         }

         String var43 = this.buildChimeraProbeHeader();
         Builder var44 = HttpRequest.newBuilder()
            .uri(URI.create(var7))
            .timeout(REQUEST_TIMEOUT)
            .header(
               "Content-Type",
               "application/json"
            )
            .header("X-Request-Nonce", var2)
            .POST(BodyPublishers.ofString(var8, StandardCharsets.UTF_8));
         if (var42) {
            var44.header("X-Chimera-Probe", var43);
            var44.header(
               "X-Chimera-Protocol",
               "CHIMERA-PROTECT"
            );
         }

         HttpRequest var45 = var44.build();
         HttpResponse var46 = this.httpClient.send(var45, BodyHandlers.ofString(StandardCharsets.UTF_8));
         String var13 = (String)var46.body();
         JsonObject var14 = parseResponseBody(var13);
         if (var42) {
            if (!var14.has("genome_packet")) {
               throw new SecurityException(
                  "Security violation: Server response is missing Chimera encryption."
               );
            }

            try {
               JsonObject var15 = var14.getAsJsonObject(
                  "genome_packet"
               );
               String var16 = var15.get("sequence").getAsString();
               String var17 = var15.getAsJsonObject("metadata")
                  .get("locus")
                  .getAsString();
               var13 = this.networkChimeraEngine.decode(new ChimeraEngine.GenomePacket(var16, var17));
               var14 = parseResponseBody(var13);
            } catch (Exception var34) {
               throw new SecurityException("Security violation: Failed to decrypt server response: " + var34.getMessage(), var34);
            }
         } else if (var14.has("genome_packet")) {
            throw new SecurityException(
               "Security violation: Received encrypted response but client lacks Chimera keys."
            );
         }

         boolean var48 = getBoolean(var14, "ok", false);
         String var49 = getString(
            var14,
            "status",
            var48
               ? "valid"
               : "invalid"
         );
         String var50 = resolveRemoteMessage(var14, var48, var49);
         String var18 = normalizeKeyCode(this.keyCode);
         String var19 = "";
         String var20 = "";
         Instant var21 = null;
         JsonObject var22 = getObject(var14, "data");
         JsonObject var23 = getObject(var14, "key");
         if (var23 == null && var22 != null) {
            var23 = getObject(var22, "key");
         }

         String var24 = "";
         String var25 = "";
         String var26 = "";
         if (var23 != null) {
            String var27 = normalizeKeyCode(getString(var23, "code", ""));
            if (!var27.isBlank()) {
               var18 = var27;
            }

            var19 = getString(var23, "customerName", "");
            var20 = getString(var23, "plan", "");
            var21 = parseInstantOrNull(getString(var23, "expiresAt", null));
            var24 = getString(var23, "discordUserId", "");
            var25 = getString(var23, "discordUserName", "");
            var26 = getString(var23, "discordUserTag", "");
         } else if (var22 != null
            && var22.has("key")
            && var22.get("key").isJsonPrimitive()) {
            String var51 = normalizeKeyCode(var22.get("key").getAsString());
            if (!var51.isBlank()) {
               var18 = var51;
            }
         }

         JsonObject var52 = getObject(var14, "session");
         if (var52 == null) {
            var52 = getObject(var14, "signedSession");
         }

         if (var52 == null && var22 != null) {
            var52 = getObject(var22, "signedSession");
            if (var52 == null) {
               var52 = getObject(var22, "session");
            }
         }

         String var28 = "";
         Instant var29 = null;
         if (var52 != null) {
            var28 = getString(var52, "token", "");
            var29 = parseInstantOrNull(getString(var52, "expiresAt", null));
         }

         if (var28.isBlank() && var22 != null) {
            var28 = getString(
               var22,
               "token",
               getString(var22, "sessionToken", "")
            );
         }

         if (var48) {
            AuthSessionV3.Claims var30 = null;
            String var31 = normalizeKeyCode(var18);
            if (var31.isBlank()) {
               var31 = normalizeKeyCode(this.keyCode);
            }

            if (!var28.isBlank() && this.verifierPublicKey != null) {
               try {
                  var30 = AuthSessionV3.verify(var28, this.verifierPublicKey, var31, this.hwid, var2, Instant.now());
               } catch (Exception var36) {
                  String var33 = var36.getMessage() == null ? var36.getClass().getSimpleName() : var36.getMessage();
                  this.verifiedClaims = null;
                  this.cachedSessionToken = "";
                  this.cachedSessionExpiresAt = null;
                  if (this.requireSignedSessions) {
                     this.applyState(AuthManager.Status.SERVER_ERROR, "Auth session token is invalid: " + var33, var20, var19, var21, true);
                     return this.getStatusSnapshot();
                  }

                  CoreEntry.LOGGER
                     .warn(
                        "[VieClient] Ignoring invalid signed session token (strict verify disabled): {}",
                        var33
                     );
                  var28 = "";
                  var29 = null;
               }
            }

            if (this.requireSignedSessions && var30 == null) {
               this.verifiedClaims = null;
               this.cachedSessionToken = "";
               this.cachedSessionExpiresAt = null;
               this.applyState(
                  AuthManager.Status.SERVER_ERROR,
                  "Auth server did not provide a valid signed session token.",
                  var20,
                  var19,
                  var21,
                  true
               );
               return this.getStatusSnapshot();
            }

            this.verifiedClaims = var30;
            if (var30 != null) {
               this.cachedSessionToken = var28;
               this.cachedSessionExpiresAt = var30.expiresAt();
            } else {
               this.cachedSessionToken = "";
               this.cachedSessionExpiresAt = var29;
            }

            boolean var32 = "lifetime".equalsIgnoreCase(var20)
               && var23 != null
               && getString(var23, "expiresAt", null) == null;
            if (!var32) {
               if (var21 == null && var30 != null) {
                  var21 = var30.expiresAt();
               } else if (var21 == null) {
                  var21 = var29;
               }
            }

            if (!var18.isBlank()) {
               this.keyCode = var18;
            }

            this.discordUserId = var24;
            this.discordUserName = var25;
            this.discordUserTag = var26;
            this.lastValidatedAtMs = System.currentTimeMillis();
            this.applyState(AuthManager.Status.VALID, var50, var20, var19, var21, true);
         } else {
            this.cachedSessionToken = "";
            this.cachedSessionExpiresAt = null;
            this.applyState(mapRemoteStatus(var49), var50, var20, var19, var21, true);
         }
      } catch (IOException var37) {
         String var3 = describeException(var37);
         CoreEntry.LOGGER
            .error(
               "[VC] Auth HTTP error: {} - {}",
               new Object[]{var37.getClass().getName(), var3, var37}
            );
         long var4 = System.currentTimeMillis();
         if (this.wasAuthorized
            && this.verifiedClaims != null
            && var4 - this.lastValidatedAtMs <= 7200000L
            && !this.verifiedClaims.isExpiredAt(Instant.ofEpochMilli(var4))) {
            long var6 = (7200000L - (var4 - this.lastValidatedAtMs)) / 60000L;
            CoreEntry.LOGGER
               .warn(
                  "[VC] Auth server unreachable. Operating under offline grace period ({} minutes remaining).",
                  var6
               );
         } else {
            this.applyState(AuthManager.Status.SERVER_ERROR, "Cannot reach auth server: " + var3, this.plan, this.customerName, this.expiresAt, true);
         }
      } catch (InterruptedException var38) {
         Thread.currentThread().interrupt();
         this.applyState(
            AuthManager.Status.SERVER_ERROR,
            "Auth check interrupted.",
            this.plan,
            this.customerName,
            this.expiresAt,
            true
         );
      } catch (Exception var39) {
         CoreEntry.LOGGER
            .error(
               "[VC] Auth unexpected error: {} - {}",
               new Object[]{var39.getClass().getName(), var39.getMessage(), var39}
            );
         this.applyState(AuthManager.Status.SERVER_ERROR, "Auth error: " + describeException(var39), this.plan, this.customerName, this.expiresAt, true);
      }

      return this.getStatusSnapshot();
   }

   private void validateStoredKeyAsync() {
      if (!this.requestInFlight) {
         this.requestInFlight = true;
         Runnable var10002 = () -> {
            try {
               if (this.initialized) {
                  this.validateStoredKey();
               }
            } finally {
               this.requestInFlight = false;
            }
         };
         byte[] var10003 = new byte[16];
         var10003[0] = (byte)(105);
         var10003[1] = (byte)(-14);
         var10003[2] = (byte)(121);
         var10003[3] = (byte)(-13);
         var10003[4] = (byte)(-51);
         var10003[5] = 0;
         var10003[6] = (byte)(48);
         var10003[7] = (byte)(-10);
         var10003[8] = (byte)(90);
         var10003[9] = (byte)(-47);
         var10003[10] = (byte)(-127);
         var10003[11] = (byte)(-112);
         var10003[12] = (byte)(-122);
         var10003[13] = (byte)(-56);
         var10003[14] = (byte)(31);
         var10003[15] = (byte)(-114);
         Thread var1 = new Thread(var10002, $$v_s(var10003, -5312089127528443590L));
         var1.setDaemon(true);
         var1.start();
      }
   }

   private void refreshAuthSourcesIfNeeded() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.lastAuthSourceProbeAtMs >= 2500L) {
         this.lastAuthSourceProbeAtMs = var1;
         if (!this.hasSavedKey()) {
            AuthManager.KeyBootstrapCandidate[] var10001 = new AuthManager.KeyBootstrapCandidate[3];
            var10001[0] = this.findLatestLicenseKeyCandidate();
            var10001[1] = this.findCurrentModJarKeyCandidate();
            var10001[2] = this.findLatestCoreEntryJarKeyCandidate();
            AuthManager.KeyBootstrapCandidate var3 = this.pickNewestKeyBootstrapCandidate(var10001);
            if (var3 != null && !var3.keyCode().isBlank()) {
               this.keyCode = var3.keyCode();
               this.updateMemoryMac();
               this.requestImmediateValidation();
            }
         }
      }
   }

   private void initializeWebSocket() {
      if (this.wsEnabled && !this.lockedDown) {
         if (this.hasSavedKey()) {
            long var1 = System.currentTimeMillis();
            if (var1 - this.lastWsInitAttemptAtMs >= 10000L) {
               if (this.wsClient != null) {
                  if (this.wsClient.isConnected() || this.wsClient.isConnecting()) {
                     return;
                  }

                  if (this.wsClient.isReconnectEnabled()) {
                     return;
                  }
               }

               this.lastWsInitAttemptAtMs = var1;

               try {
                  String var3 = this.apiBaseUrl
                     .replace(
                        "https://",
                        "wss://"
                     )
                     .replace(
                        "http://",
                        "ws://"
                     );
                  if (!var3.endsWith("/ws")) {
                     var3 = var3 + "/ws";
                  }

                  boolean var4 = var3.startsWith("wss://");
                  if (!var4) {
                     CoreEntry.LOGGER
                        .warn(
                           "[VieClient] WARNING: Using non-secure WebSocket (ws://). Consider using HTTPS/WSS in production."
                        );
                  }

                  String var5 = this.getChimeraKey();
                  if (var5 == null) {
                     CoreEntry.LOGGER
                        .warn(
                           "[VieClient] WebSocket disabled: VIE_CHIMERA_KEY is not configured."
                        );
                     this.wsEnabled = false;
                     this.wsConnected = false;
                     return;
                  }

                  this.wsClient = new VieWebSocketClient(var3, this.keyCode, this.hwid, var5);
                  this.wsClient
                     .setMessageHandler(
                        new VieWebSocketClient.MessageHandler() {
                           // $VF: synthetic field
                           private static final boolean $$vie_p = true;

                           @Override
                           public void onMessage(String message) {
                           }

                           @Override
                           public void onKeyExpired(String keyCode) {
                              CoreEntry.LOGGER
                                 .error(
                                    "[VieClient] WebSocket: Key expired notification received."
                                 );
                              if (keyCode.equals(AuthManager.this.keyCode)) {
                                 AuthManager.this.forceLockdown(
                                    "Key expired via real-time notification"
                                 );
                              }
                           }

                           @Override
                           public void onKeyExpiringSoon(String keyCode, int hoursLeft) {
                              byte[] var10001 = new byte[48];
                              var10001[0] = (byte)(104);
                              var10001[1] = (byte)(-52);
                              var10001[2] = (byte)(117);
                              var10001[3] = (byte)(-69);
                              var10001[4] = (byte)(-80);
                              var10001[5] = (byte)(-77);
                              var10001[6] = (byte)(98);
                              var10001[7] = (byte)(34);
                              var10001[8] = (byte)(53);
                              var10001[9] = (byte)(-42);
                              var10001[10] = (byte)(-71);
                              var10001[11] = (byte)(-42);
                              var10001[12] = (byte)(-4);
                              var10001[13] = (byte)(98);
                              var10001[14] = (byte)(65);
                              var10001[15] = (byte)(108);
                              var10001[16] = (byte)(12);
                              var10001[17] = (byte)(41);
                              var10001[18] = (byte)(-89);
                              var10001[19] = (byte)(-53);
                              var10001[20] = (byte)(55);
                              var10001[21] = (byte)(21);
                              var10001[22] = (byte)(123);
                              var10001[23] = (byte)(92);
                              var10001[24] = (byte)(-18);
                              var10001[25] = (byte)(107);
                              var10001[26] = (byte)(-76);
                              var10001[27] = (byte)(35);
                              var10001[28] = (byte)(3);
                              var10001[29] = (byte)(39);
                              var10001[30] = (byte)(26);
                              var10001[31] = (byte)(-67);
                              var10001[32] = (byte)(-70);
                              var10001[33] = (byte)(84);
                              var10001[34] = (byte)(27);
                              var10001[35] = (byte)(94);
                              var10001[36] = (byte)(122);
                              var10001[37] = (byte)(17);
                              var10001[38] = (byte)(-117);
                              var10001[39] = (byte)(-36);
                              var10001[40] = (byte)(-122);
                              var10001[41] = (byte)(-30);
                              var10001[42] = (byte)(44);
                              var10001[43] = (byte)(121);
                              var10001[44] = (byte)(-66);
                              var10001[45] = (byte)(-43);
                              var10001[46] = (byte)(-80);
                              var10001[47] = (byte)(-79);
                              CoreEntry.LOGGER.warn($$v_s(var10001, 8000646156611997718L), hoursLeft);
                              if (keyCode.equals(AuthManager.this.keyCode)) {
                                 class_310 var3x = class_310.method_1551();
                                 if (var3x != null && var3x.field_1724 != null) {
                                    var3x.field_1724
                                       .method_7353(
                                          class_2561.method_43470("§d[VC]§e License expiring in " + hoursLeft + " hours. Renew to avoid interruption."), false
                                       );
                                 }
                              }
                           }

                           @Override
                           public void onKeyRevoked(String keyCode, String reason) {
                              CoreEntry.LOGGER
                                 .error(
                                    "[VieClient] WebSocket: Key revoked notification received. Reason: {}",
                                    reason
                                 );
                              if (keyCode.equals(AuthManager.this.keyCode)) {
                                 AuthManager.this.forceLockdown("Key revoked via real-time notification: " + reason);
                              }
                           }

                           @Override
                           public void onKeyUnrevoked(String keyCode) {
                              if (keyCode.equals(AuthManager.this.keyCode)) {
                                 AuthManager.this.requestImmediateValidation();
                              }
                           }

                           @Override
                           public void onKeyExtended(String keyCode, String newExpiresAt) {
                              if (keyCode.equals(AuthManager.this.keyCode)) {
                                 AuthManager.this.requestImmediateValidation();
                              }
                           }

                           @Override
                           public void onHwidReset(String keyCode) {
                              byte[] var10001 = new byte[56];
                              var10001[0] = (byte)(104);
                              var10001[1] = (byte)(-52);
                              var10001[2] = (byte)(117);
                              var10001[3] = (byte)(-69);
                              var10001[4] = (byte)(-80);
                              var10001[5] = (byte)(-77);
                              var10001[6] = (byte)(98);
                              var10001[7] = (byte)(34);
                              var10001[8] = (byte)(53);
                              var10001[9] = (byte)(-42);
                              var10001[10] = (byte)(-71);
                              var10001[11] = (byte)(-42);
                              var10001[12] = (byte)(-4);
                              var10001[13] = (byte)(98);
                              var10001[14] = (byte)(65);
                              var10001[15] = (byte)(108);
                              var10001[16] = (byte)(12);
                              var10001[17] = (byte)(41);
                              var10001[18] = (byte)(-89);
                              var10001[19] = (byte)(-53);
                              var10001[20] = (byte)(55);
                              var10001[21] = (byte)(21);
                              var10001[22] = (byte)(123);
                              var10001[23] = (byte)(95);
                              var10001[24] = (byte)(-36);
                              var10001[25] = (byte)(91);
                              var10001[26] = (byte)(-48);
                              var10001[27] = (byte)(102);
                              var10001[28] = (byte)(9);
                              var10001[29] = (byte)(50);
                              var10001[30] = 0;
                              var10001[31] = (byte)(-86);
                              var10001[32] = (byte)(-89);
                              var10001[33] = (byte)(26);
                              var10001[34] = (byte)(18);
                              var10001[35] = (byte)(17);
                              var10001[36] = (byte)(103);
                              var10001[37] = (byte)(22);
                              var10001[38] = (byte)(-51);
                              var10001[39] = (byte)(-50);
                              var10001[40] = (byte)(-104);
                              var10001[41] = (byte)(-93);
                              var10001[42] = (byte)(48);
                              var10001[43] = (byte)(127);
                              var10001[44] = (byte)(-92);
                              var10001[45] = (byte)(-55);
                              var10001[46] = (byte)(-29);
                              var10001[47] = (byte)(-19);
                              var10001[48] = (byte)(102);
                              var10001[49] = (byte)(-119);
                              var10001[50] = (byte)(73);
                              var10001[51] = (byte)(-89);
                              var10001[52] = (byte)(-107);
                              var10001[53] = (byte)(-86);
                              var10001[54] = (byte)(-97);
                              var10001[55] = (byte)(89);
                              CoreEntry.LOGGER.warn($$v_s(var10001, 8000646156611997718L));
                              if (keyCode.equals(AuthManager.this.keyCode)) {
                                 AuthManager.this.requestImmediateValidation();
                              }
                           }

                           @Override
                           public void onKeyDeleted(String keyCode) {
                              CoreEntry.LOGGER
                                 .error(
                                    "[VieClient] WebSocket: Key deleted notification received."
                                 );
                              if (keyCode.equals(AuthManager.this.keyCode)) {
                                 AuthManager.this.forceLockdown(
                                    "Key deleted via real-time notification"
                                 );
                              }
                           }

                           @Override
                           public void onForceUpdate(String newVersion, String updateMsg) {
                              CoreEntry.LOGGER
                                 .error(
                                    "[VieClient] WebSocket: Force update notification received (new version: {}).",
                                    newVersion
                                 );
                              AuthManager.this.kickOutdatedClient(updateMsg);
                           }

                           // $VF: synthetic method
                           private static String $$v_s(byte[] var0, long var1) {
                              byte[] var3 = new byte[var0.length];

                              for (int var4 = 0; var4 < var0.length; var4++) {
                                 var3[var4] = (byte)(
                                    var0[var4]
                                       ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                                       ^ (var4 + 1) * (37)
                                 );
                              }

                              return new String(var3, StandardCharsets.UTF_8);
                           }
                        }
                     );
                  this.wsClient.connect();
                  this.wsConnected = false;
               } catch (Exception var6) {
                  CoreEntry.LOGGER
                     .error(
                        "[VieClient] WebSocket initialization failed: {}",
                        var6.getMessage(),
                        var6
                     );
                  this.wsConnected = false;
               }
            }
         }
      }
   }

   private void disconnectWebSocket() {
      if (this.wsClient != null) {
         try {
            this.wsClient.disconnect();
            this.wsClient = null;
            this.wsConnected = false;
         } catch (Exception var2) {
            byte[] var10001 = new byte[42];
            var10001[0] = (byte)(68);
            var10001[1] = (byte)(-51);
            var10001[2] = (byte)(117);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(-17);
            var10001[5] = (byte)(25);
            var10001[6] = (byte)(45);
            var10001[7] = (byte)(-5);
            var10001[8] = (byte)(25);
            var10001[9] = (byte)(-41);
            var10001[10] = (byte)(-71);
            var10001[11] = (byte)(-42);
            var10001[12] = (byte)(-93);
            var10001[13] = (byte)(-56);
            var10001[14] = (byte)(14);
            var10001[15] = (byte)(-75);
            var10001[16] = (byte)(32);
            var10001[17] = (byte)(40);
            var10001[18] = (byte)(-89);
            var10001[19] = (byte)(-53);
            var10001[20] = (byte)(104);
            var10001[21] = (byte)(-91);
            var10001[22] = (byte)(112);
            var10001[23] = (byte)(-89);
            var10001[24] = (byte)(-44);
            var10001[25] = (byte)(112);
            var10001[26] = (byte)(-5);
            var10001[27] = (byte)(40);
            var10001[28] = (byte)(74);
            var10001[29] = (byte)(-104);
            var10001[30] = (byte)(95);
            var10001[31] = (byte)(98);
            var10001[32] = (byte)(-33);
            var10001[33] = (byte)(94);
            var10001[34] = (byte)(14);
            var10001[35] = (byte)(12);
            var10001[36] = (byte)(35);
            var10001[37] = (byte)(-89);
            var10001[38] = (byte)(-34);
            var10001[39] = (byte)(94);
            var10001[40] = (byte)(-84);
            var10001[41] = (byte)(-66);
            CoreEntry.LOGGER.error($$v_s(var10001, -5312089127528443590L), var2.getMessage());
            this.wsClient = null;
         }
      }
   }

   private void checkWebSocketConnection() {
      if (this.wsEnabled && !this.lockedDown) {
         if (this.hasSavedKey()) {
            if (this.wsClient == null) {
               this.initializeWebSocket();
            } else if (!this.wsClient.isReconnectEnabled()) {
               CoreEntry.LOGGER
                  .warn(
                     "[VieClient] WebSocket reconnect disabled after handshake failures. Pausing WS for this session."
                  );
               this.wsEnabled = false;
               this.wsConnected = false;
            } else {
               this.wsConnected = this.wsClient.isConnected();
               if (!this.wsConnected && !this.wsClient.isConnecting()) {
               }

               boolean var1 = this.wsClient.isConnected();
               boolean var2 = this.wsClient.isConnecting();
               long var3 = this.wsClient.getConnectionDuration();
               int var5 = this.wsClient.getReconnectAttempts();
            }
         }
      }
   }

   private String getChimeraKey() {
      return (String)ChimeraEngine.configuredMasterKey().orElse(null);
   }

   private AuthManager.KeyBootstrapCandidate pickNewestKeyBootstrapCandidate(AuthManager.KeyBootstrapCandidate... candidates) {
      AuthManager.KeyBootstrapCandidate var2 = null;

      for (AuthManager.KeyBootstrapCandidate var6 : candidates) {
         if (var6 != null && !var6.keyCode().isBlank() && (var2 == null || var6.modifiedMs() > var2.modifiedMs())) {
            var2 = var6;
         }
      }

      return var2;
   }

   private AuthManager.KeyBootstrapCandidate findLatestLicenseKeyCandidate() {
      AuthManager.KeyBootstrapCandidate var1 = null;

      for (Path var5 : this.authImportDirs) {
         if (var5 != null && Files.isDirectory(var5)) {
            try (Stream var6 = Files.list(var5)) {
               AuthManager.KeyBootstrapCandidate var7 = var6.filter(x$0 -> Files.isRegularFile(x$0))
                  .filter(this::looksLikeLicenseTextFile)
                  .map(this::buildLicenseKeyCandidate)
                  .filter(Objects::nonNull)
                  .reduce((left, right) -> right.modifiedMs() > left.modifiedMs() ? right : left)
                  .orElse(null);
               if (var7 != null && (var1 == null || var7.modifiedMs() > var1.modifiedMs())) {
                  var1 = var7;
               }
            } catch (Exception var11) {
            }
         }
      }

      return var1;
   }

   private AuthManager.KeyBootstrapCandidate findLatestJarFileKeyCandidate() {
      AuthManager.KeyBootstrapCandidate var1 = null;

      for (Path var5 : this.authImportDirs) {
         if (var5 != null && Files.isDirectory(var5)) {
            try (Stream var6 = Files.list(var5)) {
               AuthManager.KeyBootstrapCandidate var7 = var6.filter(x$0 -> Files.isRegularFile(x$0))
                  .filter(this::looksLikeJarFile)
                  .map(this::buildJarKeyCandidate)
                  .filter(Objects::nonNull)
                  .reduce((left, right) -> right.modifiedMs() > left.modifiedMs() ? right : left)
                  .orElse(null);
               if (var7 != null && (var1 == null || var7.modifiedMs() > var1.modifiedMs())) {
                  var1 = var7;
               }
            } catch (Exception var11) {
            }
         }
      }

      return var1;
   }

   private AuthManager.KeyBootstrapCandidate findCurrentModJarKeyCandidate() {
      try {
         CodeSource var1 = AuthManager.class.getProtectionDomain().getCodeSource();
         if (var1 != null && var1.getLocation() != null) {
            Path var2 = Path.of(var1.getLocation().toURI()).toAbsolutePath().normalize();
            return Files.isRegularFile(var2) && this.looksLikeJarFile(var2) ? this.buildJarKeyCandidate(var2) : null;
         } else {
            return null;
         }
      } catch (Exception var3) {
         return null;
      }
   }

   private AuthManager.KeyBootstrapCandidate findLatestCoreEntryJarKeyCandidate() {
      Path var10000 = class_310.method_1551().field_1697.toPath();
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(114);
      var10001[1] = (byte)(-12);
      var10001[2] = (byte)(120);
      var10001[3] = (byte)(-83);
      Path var1 = var10000.resolve($$v_s(var10001, -5312089127528443590L));
      if (!Files.isDirectory(var1)) {
         return null;
      }

      try (Stream var2 = Files.list(var1)) {
         return var2.filter(x$0 -> Files.isRegularFile(x$0))
            .filter(this::looksLikeJarFile)
            .filter(this::isCoreEntryJar)
            .map(this::buildJarKeyCandidate)
            .filter(Objects::nonNull)
            .reduce((left, right) -> right.modifiedMs() > left.modifiedMs() ? right : left)
            .orElse(null);
      } catch (Exception var7) {
         return null;
      }
   }

   private boolean looksLikeLicenseTextFile(Path path) {
      String var2 = path != null && path.getFileName() != null ? path.getFileName().toString().toLowerCase(Locale.ROOT) : "";
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(49);
      var10001[1] = (byte)(-17);
      var10001[2] = (byte)(100);
      var10001[3] = (byte)(-86);
      if (!var2.endsWith($$v_s(var10001, -5312089127528443590L))) {
         return false;
      }

      var10001 = new byte[7];
      var10001[0] = (byte)(115);
      var10001[1] = (byte)(-14);
      var10001[2] = (byte)(127);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(-62);
      var10001[5] = (byte)(6);
      var10001[6] = (byte)(33);
      return var2.contains($$v_s(var10001, -5312089127528443590L));
   }

   private AuthManager.KeyBootstrapCandidate buildLicenseKeyCandidate(Path path) {
      if (path != null && Files.isRegularFile(path)) {
         String var2 = this.extractKeyFromLicenseText(path);
         if (var2.isBlank()) {
            return null;
         }

         long var3 = this.readLastModifiedMillis(path);
         return new AuthManager.KeyBootstrapCandidate(var2, var3, path);
      } else {
         return null;
      }
   }

   private boolean looksLikeJarFile(Path path) {
      String var2 = path != null && path.getFileName() != null ? path.getFileName().toString().toLowerCase(Locale.ROOT) : "";
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(49);
      var10001[1] = (byte)(-15);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-84);
      return var2.endsWith($$v_s(var10001, -5312089127528443590L));
   }

   private AuthManager.KeyBootstrapCandidate buildJarKeyCandidate(Path path) {
      if (path != null && Files.isRegularFile(path)) {
         String var2 = this.extractKeyFromFileName(path);
         if (var2.isBlank()) {
            return null;
         }

         long var3 = this.readLastModifiedMillis(path);
         return new AuthManager.KeyBootstrapCandidate(var2, var3, path);
      } else {
         return null;
      }
   }

   private boolean isCoreEntryJar(Path path) {
      if (path != null && Files.isRegularFile(path)) {
         try (JarFile var2 = new JarFile(path.toFile())) {
            byte[] var10001 = new byte[15];
            var10001[0] = (byte)(121);
            var10001[1] = (byte)(-6);
            var10001[2] = (byte)(126);
            var10001[3] = (byte)(-84);
            var10001[4] = (byte)(-59);
            var10001[5] = (byte)(22);
            var10001[6] = (byte)(106);
            var10001[7] = (byte)(-13);
            var10001[8] = (byte)(24);
            var10001[9] = (byte)(-57);
            var10001[10] = (byte)(-54);
            var10001[11] = (byte)(-100);
            var10001[12] = (byte)(-121);
            var10001[13] = (byte)(-62);
            var10001[14] = (byte)(2);
            JarEntry var3 = var2.getJarEntry($$v_s(var10001, -5312089127528443590L));
            if (var3 == null) {
               return false;
            }

            try (InputStreamReader var4 = new InputStreamReader(var2.getInputStream(var3), StandardCharsets.UTF_8)) {
               JsonObject var5 = JsonParser.parseReader(var4).getAsJsonObject();
               var10001 = new byte[2];
               var10001[0] = (byte)(118);
               var10001[1] = (byte)(-1);
               String var6 = getString(var5, $$v_s(var10001, -5312089127528443590L), "");
               byte[] var10000 = new byte[10];
               var10000[0] = (byte)(105);
               var10000[1] = (byte)(-14);
               var10000[2] = (byte)(121);
               var10000[3] = (byte)(-127);
               var10000[4] = (byte)(-49);
               var10000[5] = (byte)(25);
               var10000[6] = (byte)(45);
               var10000[7] = (byte)(-5);
               var10000[8] = (byte)(25);
               var10000[9] = (byte)(-41);
               return $$v_s(var10000, -5312089127528443590L).equalsIgnoreCase(var6);
            }
         } catch (Exception var12) {
            return false;
         }
      } else {
         return false;
      }
   }

   private String extractKeyFromLicenseText(Path path) {
      try (Stream var2 = Files.lines(path, StandardCharsets.UTF_8)) {
         return var2.map(String::trim).filter(line -> {
            String var10000 = line.toLowerCase(Locale.ROOT);
            byte[] var10001 = new byte[4];
            var10001[0] = (byte)(116);
            var10001[1] = (byte)(-2);
            var10001[2] = (byte)(101);
            var10001[3] = (byte)(-28);
            return var10000.startsWith($$v_s(var10001, -5312089127528443590L));
         }).map(line -> normalizeKeyCode(line.substring(4))).filter(key -> !key.isBlank()).findFirst().orElse("");
      } catch (Exception var7) {
         return "";
      }
   }

   private String extractKeyFromFileName(Path path) {
      if (path != null && path.getFileName() != null) {
         String var2 = path.getFileName().toString();
         Matcher var3 = KEY_CODE_PATTERN.matcher(var2);
         String var4 = "";

         while (var3.find()) {
            var4 = var3.group();
         }

         return normalizeKeyCode(var4);
      } else {
         return "";
      }
   }

   private boolean copyAuthFile(Path source, Path target) {
      if (source != null && target != null && Files.isRegularFile(source)) {
         try {
            Path var3 = target.getParent();
            if (var3 != null) {
               Files.createDirectories(var3);
            }

            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
            return true;
         } catch (Exception var4) {
            CoreEntry.LOGGER
               .warn(
                  "[VieClient] Failed to copy auth file {} -> {}: {}",
                  new Object[]{source, target, var4.getMessage()}
               );
            return false;
         }
      } else {
         return false;
      }
   }

   private long readLastModifiedMillis(Path path) {
      try {
         return path != null && Files.exists(path) ? Files.getLastModifiedTime(path).toMillis() : Long.MIN_VALUE;
      } catch (Exception var3) {
         return Long.MIN_VALUE;
      }
   }

   private static Path[] resolveAuthImportDirs(Path runDir) {
      LinkedHashSet var1 = new LinkedHashSet();
      if (runDir != null) {
         var1.add(normalizePath(runDir));
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(114);
         var10002[1] = (byte)(-12);
         var10002[2] = (byte)(120);
         var10002[3] = (byte)(-83);
         var1.add(normalizePath(runDir.resolve($$v_s(var10002, -5312089127528443590L))));
         var10002 = new byte[9];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-14);
         var10002[2] = (byte)(121);
         var10002[3] = (byte)(-67);
         var10002[4] = (byte)(-64);
         var10002[5] = (byte)(28);
         var10002[6] = (byte)(33);
         var10002[7] = (byte)(-16);
         var10002[8] = (byte)(3);
         var1.add(normalizePath(runDir.resolve($$v_s(var10002, -5312089127528443590L))));
         var10002 = new byte[9];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-14);
         var10002[2] = (byte)(121);
         var10002[3] = (byte)(-67);
         var10002[4] = (byte)(-60);
         var10002[5] = (byte)(16);
         var10002[6] = (byte)(37);
         var10002[7] = (byte)(-22);
         var10002[8] = (byte)(4);
         var1.add(normalizePath(runDir.resolve($$v_s(var10002, -5312089127528443590L))));
      }

      return var1.stream().filter(s -> s != null && !s.isBlank()).map(x$0 -> Path.of(x$0)).toArray(Path[]::new);
   }

   private static boolean isSamePath(Path left, Path right) {
      return left != null && right != null ? normalizePath(left).equals(normalizePath(right)) : false;
   }

   private static String normalizePath(Path path) {
      if (path == null) {
         return "";
      }

      try {
         return path.toAbsolutePath().normalize().toString();
      } catch (Exception var2) {
         return String.valueOf(path);
      }
   }

   private void applyState(AuthManager.Status nextStatus, String nextMessage, String nextPlan, String nextCustomerName, Instant nextExpiresAt, boolean persist) {
      this.status = Objects.requireNonNull(nextStatus);
      this.message = nextMessage == null ? "" : nextMessage;
      this.plan = nextPlan == null ? "" : nextPlan;
      this.customerName = nextCustomerName == null ? "" : nextCustomerName;
      this.expiresAt = nextExpiresAt;
      if (this.status == AuthManager.Status.OUTDATED_CLIENT) {
         this.kickOutdatedClient(this.message);
      } else {
         if (this.status == AuthManager.Status.MISSING_KEY
            || this.status == AuthManager.Status.INVALID
            || this.status == AuthManager.Status.HWID_MISMATCH
            || this.status == AuthManager.Status.REVOKED
            || this.status == AuthManager.Status.EXPIRED) {
            CoreEntry.LOGGER
               .error(
                  "[VieClient] Auth status={}, msg={} — client continues in degraded mode",
                  this.status,
                  this.message
               );
         }

         boolean var7 = this.status == AuthManager.Status.VALID;
         if (this.wasAuthorized && !var7) {
            this.disableProtectedModulesWithNotice();
         } else if (!this.wasAuthorized && var7) {
            this.restoreEntitledModules();
         }

         this.wasAuthorized = var7;
         this.updateMemoryMac();
      }
   }

   private void restoreEntitledModules() {
      try {
         CoreEntry var1 = CoreEntry.getInstance();
         if (var1 != null && var1.getConfigManager() != null) {
            var1.getConfigManager().load();
         }
      } catch (Throwable var2) {
         CoreEntry.LOGGER
            .warn(
               "[VieClient] Error restoring module config after auth: {}",
               var2.getMessage()
            );
      }
   }

   private void disableProtectedModulesWithNotice() {
      CoreEntry var1 = CoreEntry.getInstance();
      if (var1 != null && var1.getModuleManager() != null) {
         int var2 = var1.getModuleManager().disableProtectedModules();
         if (var2 > 0) {
            class_310 var3 = class_310.method_1551();
            if (var3 != null && var3.field_1724 != null) {
               var3.field_1724.method_7353(class_2561.method_43470("§d[VC]§c License lost. Disabled " + var2 + " protected modules."), false);
            }
         }
      }
   }

   private boolean canUseOfflineCache() {
      return false;
   }

   private boolean hasSavedKey() {
      return this.keyCode != null && !this.keyCode.isBlank();
   }

   public String getKeyCode() {
      return this.keyCode == null ? "" : this.keyCode;
   }

   private void loadLocalState() {
   }

   private void persistLocalState() {
   }

   private boolean writeAuthStateFile(Path target, JsonObject root) {
      return false;
   }

   private JsonObject encryptKeyForStorage(String plainKey) throws Exception {
      byte[] var2 = new byte[16];
      byte[] var3 = new byte[12];
      RNG.nextBytes(var2);
      RNG.nextBytes(var3);
      SecretKey var4 = this.deriveStorageKey(var2);
      byte[] var10000 = new byte[17];
      var10000[0] = (byte)(94);
      var10000[1] = (byte)(-34);
      var10000[2] = (byte)(79);
      var10000[3] = (byte)(-15);
      var10000[4] = (byte)(-21);
      var10000[5] = (byte)(54);
      var10000[6] = (byte)(9);
      var10000[7] = (byte)(-79);
      var10000[8] = (byte)(57);
      var10000[9] = (byte)(-52);
      var10000[10] = (byte)(-76);
      var10000[11] = (byte)(-105);
      var10000[12] = (byte)(-112);
      var10000[13] = (byte)(-55);
      var10000[14] = (byte)(5);
      var10000[15] = (byte)(-120);
      var10000[16] = (byte)(40);
      Cipher var5 = Cipher.getInstance($$v_s(var10000, -5312089127528443590L));
      var5.init(1, var4, new GCMParameterSpec(128, var3));
      byte[] var6 = var5.doFinal(plainKey.getBytes(StandardCharsets.UTF_8));
      JsonObject var7 = new JsonObject();
      var7.addProperty("v", 1);
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(108);
      var10001[1] = (byte)(-6);
      var10001[2] = (byte)(112);
      var10001[3] = (byte)(-86);
      var7.addProperty($$v_s(var10001, -5312089127528443590L), Base64.getEncoder().encodeToString(var2));
      var10001 = new byte[2];
      var10001[0] = (byte)(118);
      var10001[1] = (byte)(-19);
      var7.addProperty($$v_s(var10001, -5312089127528443590L), Base64.getEncoder().encodeToString(var3));
      var10001 = new byte[10];
      var10001[0] = (byte)(124);
      var10001[1] = (byte)(-14);
      var10001[2] = (byte)(108);
      var10001[3] = (byte)(-74);
      var10001[4] = (byte)(-55);
      var10001[5] = (byte)(7);
      var10001[6] = (byte)(48);
      var10001[7] = (byte)(-5);
      var10001[8] = (byte)(15);
      var10001[9] = (byte)(-41);
      var7.addProperty($$v_s(var10001, -5312089127528443590L), Base64.getEncoder().encodeToString(var6));
      return var7;
   }

   private String decryptStoredKey(JsonObject encrypted) throws Exception {
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(108);
      var10001[1] = (byte)(-6);
      var10001[2] = (byte)(112);
      var10001[3] = (byte)(-86);
      String var2 = getString(encrypted, $$v_s(var10001, -5312089127528443590L), "");
      var10001 = new byte[2];
      var10001[0] = (byte)(118);
      var10001[1] = (byte)(-19);
      String var3 = getString(encrypted, $$v_s(var10001, -5312089127528443590L), "");
      var10001 = new byte[10];
      var10001[0] = (byte)(124);
      var10001[1] = (byte)(-14);
      var10001[2] = (byte)(108);
      var10001[3] = (byte)(-74);
      var10001[4] = (byte)(-55);
      var10001[5] = (byte)(7);
      var10001[6] = (byte)(48);
      var10001[7] = (byte)(-5);
      var10001[8] = (byte)(15);
      var10001[9] = (byte)(-41);
      String var4 = getString(encrypted, $$v_s(var10001, -5312089127528443590L), "");
      if (!var2.isBlank() && !var3.isBlank() && !var4.isBlank()) {
         byte[] var5 = Base64.getDecoder().decode(var2);
         byte[] var6 = Base64.getDecoder().decode(var3);
         byte[] var7 = Base64.getDecoder().decode(var4);
         SecretKey var8 = this.deriveStorageKey(var5);
         byte[] var10000 = new byte[17];
         var10000[0] = (byte)(94);
         var10000[1] = (byte)(-34);
         var10000[2] = (byte)(79);
         var10000[3] = (byte)(-15);
         var10000[4] = (byte)(-21);
         var10000[5] = (byte)(54);
         var10000[6] = (byte)(9);
         var10000[7] = (byte)(-79);
         var10000[8] = (byte)(57);
         var10000[9] = (byte)(-52);
         var10000[10] = (byte)(-76);
         var10000[11] = (byte)(-105);
         var10000[12] = (byte)(-112);
         var10000[13] = (byte)(-55);
         var10000[14] = (byte)(5);
         var10000[15] = (byte)(-120);
         var10000[16] = (byte)(40);
         Cipher var9 = Cipher.getInstance($$v_s(var10000, -5312089127528443590L));
         var9.init(2, var8, new GCMParameterSpec(128, var6));
         byte[] var10 = var9.doFinal(var7);
         return new String(var10, StandardCharsets.UTF_8);
      } else {
         return "";
      }
   }

   private SecretKey deriveStorageKey(byte[] salt) throws Exception {
      char[] var2 = this.buildStoragePassphrase().toCharArray();
      PBEKeySpec var3 = new PBEKeySpec(var2, salt, 120000, 256);
      byte[] var10000 = new byte[20];
      var10000[0] = (byte)(79);
      var10000[1] = (byte)(-39);
      var10000[2] = (byte)(87);
      var10000[3] = (byte)(-102);
      var10000[4] = (byte)(-22);
      var10000[5] = (byte)(71);
      var10000[6] = (byte)(19);
      var10000[7] = (byte)(-9);
      var10000[8] = (byte)(3);
      var10000[9] = (byte)(-53);
      var10000[10] = (byte)(-84);
      var10000[11] = (byte)(-101);
      var10000[12] = (byte)(-107);
      var10000[13] = (byte)(-50);
      var10000[14] = (byte)(63);
      var10000[15] = (byte)(-82);
      var10000[16] = (byte)(14);
      var10000[17] = (byte)(121);
      var10000[18] = (byte)(-7);
      var10000[19] = (byte)(-104);
      SecretKeyFactory var4 = SecretKeyFactory.getInstance($$v_s(var10000, -5312089127528443590L));
      byte[] var5 = var4.generateSecret(var3).getEncoded();
      byte[] var10003 = new byte[3];
      var10003[0] = (byte)(94);
      var10003[1] = (byte)(-34);
      var10003[2] = (byte)(79);
      return new SecretKeySpec(var5, $$v_s(var10003, -5312089127528443590L));
   }

   private String buildStoragePassphrase() {
      String var10000 = this.hwid;
      byte[] var10001 = new byte[9];
      var10001[0] = (byte)(106);
      var10001[1] = (byte)(-24);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-84);
      var10001[4] = (byte)(-126);
      var10001[5] = (byte)(27);
      var10001[6] = (byte)(37);
      var10001[7] = (byte)(-13);
      var10001[8] = (byte)(18);
      String var1 = $$v_s(var10001, -5312089127528443590L);
      byte[] var10002 = new byte[7];
      var10002[0] = (byte)(106);
      var10002[1] = (byte)(-11);
      var10002[2] = (byte)(119);
      var10002[3] = (byte)(-80);
      var10002[4] = (byte)(-61);
      var10002[5] = (byte)(2);
      var10002[6] = (byte)(42);
      String var2 = System.getProperty(var1, $$v_s(var10002, -5312089127528443590L));
      var10002 = new byte[7];
      var10002[0] = (byte)(112);
      var10002[1] = (byte)(-24);
      var10002[2] = (byte)(50);
      var10002[3] = (byte)(-80);
      var10002[4] = (byte)(-51);
      var10002[5] = (byte)(24);
      var10002[6] = (byte)(33);
      String var4 = $$v_s(var10002, -5312089127528443590L);
      byte[] var10003 = new byte[7];
      var10003[0] = (byte)(106);
      var10003[1] = (byte)(-11);
      var10003[2] = (byte)(119);
      var10003[3] = (byte)(-80);
      var10003[4] = (byte)(-61);
      var10003[5] = (byte)(2);
      var10003[6] = (byte)(42);
      return var10000 + "|" + var2 + "|" + System.getProperty(var4, $$v_s(var10003, -5312089127528443590L)) + "|vieclient-auth-cache-v2";
   }

   private String buildChimeraProbeHeader() {
      if (this.networkChimeraEngine == null) {
         return "";
      }

      try {
         String var1 = Long.toString(System.currentTimeMillis()) + "|" + this.hwid.substring(0, Math.min(8, this.hwid.length()));
         return this.networkChimeraEngine.encodeToCompact(var1);
      } catch (Exception var2) {
         CoreEntry.LOGGER
            .warn(
               "[VieClient] Network Chimera probe header build failed: {}",
               var2.getMessage()
            );
         return "";
      }
   }

   private String computeCacheSignature() {
      return "";
   }

   private static String describeException(Throwable throwable) {
      if (throwable == null) {
         byte[] var10000 = new byte[13];
         var10000[0] = (byte)(106);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(119);
         var10000[3] = (byte)(-80);
         var10000[4] = (byte)(-61);
         var10000[5] = (byte)(2);
         var10000[6] = (byte)(42);
         var10000[7] = (byte)(-63);
         var10000[8] = (byte)(18);
         var10000[9] = (byte)(-47);
         var10000[10] = (byte)(-106);
         var10000[11] = (byte)(-103);
         var10000[12] = (byte)(-122);
         return $$v_s(var10000, -5312089127528443590L);
      } else {
         String var1 = throwable.getMessage();
         return var1 != null && !var1.isBlank() ? var1 : throwable.getClass().getSimpleName();
      }
   }

   private static boolean constantTimeEquals(String a, String b) {
      byte[] var2 = a == null ? new byte[0] : a.getBytes(StandardCharsets.UTF_8);
      byte[] var3 = b == null ? new byte[0] : b.getBytes(StandardCharsets.UTF_8);
      return MessageDigest.isEqual(var2, var3);
   }

   private static String generateRandomNonce() {
      byte[] var0 = new byte[16];
      RNG.nextBytes(var0);
      return Base64.getUrlEncoder().withoutPadding().encodeToString(var0);
   }

   public AuthSessionV3.Claims verifySignedSessionToken(String token, String expectedKey, String expectedHwid, String expectedNonce) throws Exception {
      if (this.verifierPublicKey == null) {
         byte[] var10002 = new byte[40];
         var10002[0] = (byte)(81);
         var10002[1] = (byte)(-12);
         var10002[2] = (byte)(60);
         var10002[3] = (byte)(-65);
         var10002[4] = (byte)(-39);
         var10002[5] = 1;
         var10002[6] = (byte)(44);
         var10002[7] = (byte)(-66);
         var10002[8] = (byte)(4);
         var10002[9] = (byte)(-54);
         var10002[10] = (byte)(-125);
         var10002[11] = (byte)(-104);
         var10002[12] = (byte)(-107);
         var10002[13] = (byte)(-39);
         var10002[14] = (byte)(25);
         var10002[15] = (byte)(-108);
         var10002[16] = (byte)(42);
         var10002[17] = (byte)(107);
         var10002[18] = (byte)(-68);
         var10002[19] = (byte)(-37);
         var10002[20] = (byte)(126);
         var10002[21] = (byte)(-23);
         var10002[22] = (byte)(125);
         var10002[23] = (byte)(-83);
         var10002[24] = (byte)(-121);
         var10002[25] = (byte)(120);
         var10002[26] = (byte)(-15);
         var10002[27] = (byte)(63);
         var10002[28] = (byte)(4);
         var10002[29] = (byte)(-98);
         var10002[30] = (byte)(83);
         var10002[31] = (byte)(120);
         var10002[32] = (byte)(-103);
         var10002[33] = (byte)(82);
         var10002[34] = (byte)(27);
         var10002[35] = (byte)(11);
         var10002[36] = (byte)(62);
         var10002[37] = (byte)(-80);
         var10002[38] = (byte)(-128);
         var10002[39] = (byte)(80);
         throw new IllegalStateException($$v_s(var10002, -5312089127528443590L));
      } else {
         return AuthSessionV3.verify(token, this.verifierPublicKey, expectedKey, expectedHwid, expectedNonce, Instant.now());
      }
   }

   public PublicKey getVerifierPublicKey() {
      return this.verifierPublicKey;
   }

   public static PublicKey parseConfiguredPublicKey(String configuredKey) {
      if (configuredKey != null && !configuredKey.isBlank()) {
         try {
            return AuthSessionV3.parseEd25519PublicKey(configuredKey);
         } catch (Exception var2) {
            byte[] var10001 = new byte[47];
            var10001[0] = (byte)(68);
            var10001[1] = (byte)(-51);
            var10001[2] = (byte)(117);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(-17);
            var10001[5] = (byte)(25);
            var10001[6] = (byte)(45);
            var10001[7] = (byte)(-5);
            var10001[8] = (byte)(25);
            var10001[9] = (byte)(-41);
            var10001[10] = (byte)(-71);
            var10001[11] = (byte)(-42);
            var10001[12] = (byte)(-67);
            var10001[13] = (byte)(-61);
            var10001[14] = (byte)(26);
            var10001[15] = (byte)(-121);
            var10001[16] = (byte)(35);
            var10001[17] = (byte)(34);
            var10001[18] = (byte)(-88);
            var10001[19] = (byte)(-114);
            var10001[20] = (byte)(125);
            var10001[21] = (byte)(-16);
            var10001[22] = (byte)(96);
            var10001[23] = (byte)(-90);
            var10001[24] = (byte)(-121);
            var10001[25] = (byte)(96);
            var10001[26] = (byte)(-3);
            var10001[27] = (byte)(33);
            var10001[28] = (byte)(74);
            var10001[29] = (byte)(-108);
            var10001[30] = (byte)(82);
            var10001[31] = (byte)(113);
            var10001[32] = (byte)(-33);
            var10001[33] = (byte)(75);
            var10001[34] = (byte)(9);
            var10001[35] = (byte)(28);
            var10001[36] = (byte)(32);
            var10001[37] = (byte)(-68);
            var10001[38] = (byte)(-121);
            var10001[39] = (byte)(94);
            var10001[40] = (byte)(-68);
            var10001[41] = (byte)(-90);
            var10001[42] = (byte)(61);
            var10001[43] = (byte)(44);
            var10001[44] = (byte)(-76);
            var10001[45] = (byte)(118);
            var10001[46] = (byte)(-15);
            CoreEntry.LOGGER.warn($$v_s(var10001, -5312089127528443590L), var2.getMessage());
            return null;
         }
      } else {
         return null;
      }
   }

   private static JsonObject parseResponseBody(String body) {
      try {
         return JsonParser.parseString(body).getAsJsonObject();
      } catch (Exception var2) {
         return new JsonObject();
      }
   }

   private static JsonObject getObject(JsonObject root, String key) {
      return root != null && root.has(key) && root.get(key).isJsonObject() ? root.getAsJsonObject(key) : null;
   }

   private static String getString(JsonObject root, String key, String fallback) {
      if (root != null && root.has(key) && !root.get(key).isJsonNull()) {
         try {
            String var3 = root.get(key).getAsString();
            if (var3 == null) {
               return fallback;
            }

            String var4 = var3.trim();
            if (var4.isEmpty()) {
               return fallback;
            }

            byte[] var10000 = new byte[4];
            var10000[0] = (byte)(113);
            var10000[1] = (byte)(-18);
            var10000[2] = (byte)(112);
            var10000[3] = (byte)(-78);
            if (!$$v_s(var10000, -5312089127528443590L).equalsIgnoreCase(var4)) {
               var10000 = new byte[9];
               var10000[0] = (byte)(106);
               var10000[1] = (byte)(-11);
               var10000[2] = (byte)(120);
               var10000[3] = (byte)(-69);
               var10000[4] = (byte)(-54);
               var10000[5] = (byte)(28);
               var10000[6] = (byte)(42);
               var10000[7] = (byte)(-5);
               var10000[8] = (byte)(19);
               if (!$$v_s(var10000, -5312089127528443590L).equalsIgnoreCase(var4)) {
                  return var3;
               }
            }

            return fallback;
         } catch (Exception var5) {
            return fallback;
         }
      } else {
         return fallback;
      }
   }

   private static String resolveRemoteMessage(JsonObject root, boolean ok, String remoteStatus) {
      String var3 = defaultMessageForRemoteStatus(ok, remoteStatus);
      byte[] var10001 = new byte[7];
      var10001[0] = (byte)(114);
      var10001[1] = (byte)(-2);
      var10001[2] = (byte)(111);
      var10001[3] = (byte)(-83);
      var10001[4] = (byte)(-51);
      var10001[5] = (byte)(18);
      var10001[6] = (byte)(33);
      String var4 = getString(root, $$v_s(var10001, -5312089127528443590L), null);
      if (var4 != null) {
         return var4;
      }

      var10001 = new byte[6];
      var10001[0] = (byte)(123);
      var10001[1] = (byte)(-2);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-65);
      var10001[4] = (byte)(-59);
      var10001[5] = (byte)(25);
      String var5 = getString(root, $$v_s(var10001, -5312089127528443590L), null);
      if (var5 != null) {
         return var5;
      }

      var10001 = new byte[5];
      var10001[0] = (byte)(122);
      var10001[1] = (byte)(-23);
      var10001[2] = (byte)(110);
      var10001[3] = (byte)(-79);
      var10001[4] = (byte)(-34);
      String var6 = getString(root, $$v_s(var10001, -5312089127528443590L), null);
      return var6 != null ? var6 : var3;
   }

   private static String defaultMessageForRemoteStatus(boolean ok, String remoteStatus) {
      if (ok) {
         return "Key accepted.";
      }

      String var2 = remoteStatus == null ? "" : remoteStatus.trim().toLowerCase(Locale.ROOT);

      return switch (var2) {
         case "expired" -> "Key has expired.";
         case "revoked" -> "Key has been revoked.";
         case "hwid_mismatch" -> "HWID mismatch for this key.";
         case "outdated_client", "outdated" -> "Client is outdated. Please download the latest build.";
         case "rate_limited" -> "Too many auth attempts. Please try again in a moment.";
         case "server_error" -> "Auth server internal error. Please try again or contact support.";
         default -> "Authentication failed.";
      };
   }

   private static boolean getBoolean(JsonObject root, String key, boolean fallback) {
      if (root != null && root.has(key) && !root.get(key).isJsonNull()) {
         try {
            return root.get(key).getAsBoolean();
         } catch (Exception var4) {
            return fallback;
         }
      } else {
         return fallback;
      }
   }

   private static long getLong(JsonObject root, String key, long fallback) {
      if (root != null && root.has(key) && !root.get(key).isJsonNull()) {
         try {
            return root.get(key).getAsLong();
         } catch (Exception var5) {
            return fallback;
         }
      } else {
         return fallback;
      }
   }

   private static Instant parseInstantOrNull(String value) {
      if (value != null && !value.isBlank()) {
         try {
            return Instant.parse(value);
         } catch (DateTimeParseException var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static String normalizeKeyCode(String raw) {
      return raw == null ? "" : raw.trim().toUpperCase(Locale.ROOT);
   }

   private static AuthManager.Status parseStatus(String raw) {
      if (raw == null) {
         return AuthManager.Status.MISSING_KEY;
      }

      return switch (raw.trim().toLowerCase(Locale.ROOT)) {
         case "valid" -> AuthManager.Status.VALID;
         case "expired" -> AuthManager.Status.EXPIRED;
         case "revoked" -> AuthManager.Status.REVOKED;
         case "hwid_mismatch" -> AuthManager.Status.HWID_MISMATCH;
         case "outdated_client", "outdated" -> AuthManager.Status.OUTDATED_CLIENT;
         case "server_error" -> AuthManager.Status.SERVER_ERROR;
         case "rate_limited" -> AuthManager.Status.RATE_LIMITED;
         case "missing_key" -> AuthManager.Status.MISSING_KEY;
         default -> AuthManager.Status.INVALID;
      };
   }

   private static AuthManager.Status mapRemoteStatus(String raw) {
      if (raw == null) {
         return AuthManager.Status.INVALID;
      }

      return switch (raw.trim().toLowerCase(Locale.ROOT)) {
         case "expired" -> AuthManager.Status.EXPIRED;
         case "revoked" -> AuthManager.Status.REVOKED;
         case "hwid_mismatch" -> AuthManager.Status.HWID_MISMATCH;
         case "outdated_client", "outdated" -> AuthManager.Status.OUTDATED_CLIENT;
         case "server_error" -> AuthManager.Status.SERVER_ERROR;
         case "rate_limited" -> AuthManager.Status.RATE_LIMITED;
         case "not_redeemed", "missing_key", "not_found" -> AuthManager.Status.MISSING_KEY;
         case "invalid_request" -> AuthManager.Status.INVALID;
         default -> AuthManager.Status.INVALID;
      };
   }

   private static String resolveApiBaseUrl(String fileValue) {
      byte[] var10000 = new byte[27];
      var10000[0] = (byte)(119);
      var10000[1] = (byte)(-17);
      var10000[2] = (byte)(104);
      var10000[3] = (byte)(-82);
      var10000[4] = (byte)(-33);
      var10000[5] = (byte)(79);
      var10000[6] = (byte)(107);
      var10000[7] = (byte)(-79);
      var10000[8] = 1;
      var10000[9] = (byte)(-54);
      var10000[10] = (byte)(-127);
      var10000[11] = (byte)(-123);
      var10000[12] = (byte)(-111);
      var10000[13] = (byte)(-33);
      var10000[14] = (byte)(26);
      var10000[15] = (byte)(-113);
      var10000[16] = (byte)(44);
      var10000[17] = (byte)(46);
      var10000[18] = (byte)(-65);
      var10000[19] = (byte)(-128);
      var10000[20] = (byte)(104);
      var10000[21] = (byte)(-22);
      var10000[22] = (byte)(100);
      var10000[23] = (byte)(-31);
      var10000[24] = (byte)(-58);
      var10000[25] = (byte)(99);
      var10000[26] = (byte)(-3);
      return $$v_s(var10000, -5312089127528443590L);
   }

   private static String normalizeBaseUrl(String url) {
      String var1 = url == null ? "" : url.trim();
      if (var1.isBlank()) {
         return "https://vieservices.top/api";
      }

      if (!var1.contains("://")) {
         var1 = "https://" + var1;
      }

      while (var1.endsWith("/")) {
         var1 = var1.substring(0, var1.length() - 1);
      }

      try {
         URI var2 = URI.create(var1);
         String var3 = var2.getScheme() == null ? "https" : var2.getScheme().toLowerCase(Locale.ROOT);
         String var4 = var2.getHost();
         int var5 = var2.getPort();
         String var6 = var2.getPath() == null ? "" : var2.getPath().trim();
         if (var6.isEmpty() || "/".equals(var6)) {
            var6 = "/api";
         }

         while (var6.endsWith("/") && var6.length() > 1) {
            var6 = var6.substring(0, var6.length() - 1);
         }

         String var7 = var4 != null && !var4.isBlank() ? (var5 > 0 ? var4 + ":" + var5 : var4) : var2.getAuthority();
         return var7 != null && !var7.isBlank()
            ? var3 + "://" + var7 + var6
            : "https://vieservices.top/api";
      } catch (Exception var8) {
         return "https://vieservices.top/api";
      }
   }

   private boolean isApiTransportAllowed(String baseUrl) {
      try {
         URI var2 = URI.create(baseUrl);
         String var3 = var2.getScheme() == null ? "" : var2.getScheme().toLowerCase(Locale.ROOT);
         String var4 = var2.getHost() == null ? "" : var2.getHost().toLowerCase(Locale.ROOT);
         byte[] var10000 = new byte[5];
         var10000[0] = (byte)(119);
         var10000[1] = (byte)(-17);
         var10000[2] = (byte)(104);
         var10000[3] = (byte)(-82);
         var10000[4] = (byte)(-33);
         if ($$v_s(var10000, -5312089127528443590L).equals(var3)) {
            return true;
         }

         var10000 = new byte[4];
         var10000[0] = (byte)(119);
         var10000[1] = (byte)(-17);
         var10000[2] = (byte)(104);
         var10000[3] = (byte)(-82);
         return $$v_s(var10000, -5312089127528443590L).equals(var3) && isLoopbackHost(var4) ? true : this.allowInsecureHttp;
      } catch (Exception var5) {
         return false;
      }
   }

   private static boolean isPlaintextCompatAllowed(String baseUrl) {
      try {
         URI var1 = URI.create(baseUrl);
         String var2 = var1.getHost() == null ? "" : var1.getHost().toLowerCase(Locale.ROOT);
         byte[] var10000 = new byte[15];
         var10000[0] = (byte)(105);
         var10000[1] = (byte)(-14);
         var10000[2] = (byte)(121);
         var10000[3] = (byte)(-83);
         var10000[4] = (byte)(-55);
         var10000[5] = (byte)(7);
         var10000[6] = (byte)(50);
         var10000[7] = (byte)(-9);
         var10000[8] = (byte)(20);
         var10000[9] = (byte)(-58);
         var10000[10] = (byte)(-105);
         var10000[11] = (byte)(-40);
         var10000[12] = (byte)(-128);
         var10000[13] = (byte)(-62);
         var10000[14] = (byte)(28);
         if (!$$v_s(var10000, -5312089127528443590L).equals(var2)) {
            byte[] var10001 = new byte[16];
            var10001[0] = (byte)(49);
            var10001[1] = (byte)(-19);
            var10001[2] = (byte)(117);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(-33);
            var10001[5] = (byte)(16);
            var10001[6] = (byte)(54);
            var10001[7] = (byte)(-24);
            var10001[8] = (byte)(30);
            var10001[9] = (byte)(-64);
            var10001[10] = (byte)(-127);
            var10001[11] = (byte)(-123);
            var10001[12] = (byte)(-38);
            var10001[13] = (byte)(-39);
            var10001[14] = (byte)(3);
            var10001[15] = (byte)(-106);
            if (!var2.endsWith($$v_s(var10001, -5312089127528443590L))) {
               return false;
            }
         }

         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   private static boolean isLoopbackHost(String host) {
      byte[] var10000 = new byte[9];
      var10000[0] = (byte)(115);
      var10000[1] = (byte)(-12);
      var10000[2] = (byte)(127);
      var10000[3] = (byte)(-65);
      var10000[4] = (byte)(-64);
      var10000[5] = (byte)(29);
      var10000[6] = (byte)(43);
      var10000[7] = (byte)(-19);
      var10000[8] = (byte)(3);
      if (!$$v_s(var10000, -5312089127528443590L).equals(host)) {
         var10000 = new byte[9];
         var10000[0] = (byte)(46);
         var10000[1] = (byte)(-87);
         var10000[2] = (byte)(43);
         var10000[3] = (byte)(-16);
         var10000[4] = (byte)(-100);
         var10000[5] = (byte)(91);
         var10000[6] = (byte)(116);
         var10000[7] = (byte)(-80);
         var10000[8] = (byte)(70);
         if (!$$v_s(var10000, -5312089127528443590L).equals(host)) {
            var10000 = new byte[3];
            var10000[0] = (byte)(37);
            var10000[1] = (byte)(-95);
            var10000[2] = (byte)(45);
            if (!$$v_s(var10000, -5312089127528443590L).equals(host)) {
               var10000 = new byte[5];
               var10000[0] = (byte)(68);
               var10000[1] = (byte)(-95);
               var10000[2] = (byte)('틺' ^ '틜');
               var10000[3] = (byte)(-17);
               var10000[4] = (byte)(-15);
               if (!$$v_s(var10000, -5312089127528443590L).equals(host)) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private static String resolveAuthSigningPublicKey() {
      String var0 = System.getenv(
         "VIE_AUTH_PUBLIC_KEY"
      );
      return var0 != null && !var0.isBlank()
         ? var0.trim()
         : "e8d4c5797692d8ae885d773687081d15657799c33cd4f98eb1057bff3ceea0dd";
   }

   private static boolean resolveRequireSignedSessions(String publicKey) {
      byte[] var10000 = new byte[23];
      var10000[0] = (byte)(73);
      var10000[1] = (byte)(-46);
      var10000[2] = (byte)(89);
      var10000[3] = (byte)(-127);
      var10000[4] = (byte)(-19);
      var10000[5] = (byte)(32);
      var10000[6] = (byte)(16);
      var10000[7] = (byte)(-42);
      var10000[8] = (byte)(40);
      var10000[9] = (byte)(-15);
      var10000[10] = (byte)(-95);
      var10000[11] = (byte)(-89);
      var10000[12] = (byte)(-95);
      var10000[13] = (byte)(-28);
      var10000[14] = (byte)(62);
      var10000[15] = (byte)(-93);
      var10000[16] = (byte)(16);
      var10000[17] = (byte)(24);
      var10000[18] = (byte)(-123);
      var10000[19] = (byte)(-23);
      var10000[20] = (byte)(82);
      var10000[21] = (byte)(-64);
      var10000[22] = (byte)(80);
      String var1 = System.getenv($$v_s(var10000, -5312089127528443590L));
      return var1 != null ? Boolean.parseBoolean(var1) : !publicKey.isBlank();
   }

   private static boolean resolveAllowInsecureHttp() {
      byte[] var10000 = new byte[28];
      var10000[0] = (byte)(73);
      var10000[1] = (byte)(-46);
      var10000[2] = (byte)(89);
      var10000[3] = (byte)(-127);
      var10000[4] = (byte)(-19);
      var10000[5] = (byte)('쭎' ^ '쭮');
      var10000[6] = (byte)(16);
      var10000[7] = (byte)(-42);
      var10000[8] = (byte)(40);
      var10000[9] = (byte)(-30);
      var10000[10] = (byte)(-88);
      var10000[11] = (byte)(-70);
      var10000[12] = (byte)(-69);
      var10000[13] = (byte)(-6);
      var10000[14] = (byte)(51);
      var10000[15] = (byte)(-81);
      var10000[16] = 1;
      var10000[17] = (byte)(24);
      var10000[18] = (byte)(-119);
      var10000[19] = (byte)(-19);
      var10000[20] = (byte)(73);
      var10000[21] = (byte)(-41);
      var10000[22] = (byte)(81);
      var10000[23] = (byte)(-111);
      var10000[24] = (byte)(-17);
      var10000[25] = (byte)(71);
      var10000[26] = (byte)(-64);
      var10000[27] = (byte)(22);
      String var0 = System.getenv($$v_s(var10000, -5312089127528443590L));
      return var0 != null ? Boolean.parseBoolean(var0) : false;
   }

   private static String computeMachineFingerprint() {
      try {
         boolean var0 = isAndroidDevice();
         String var1;
         if (var0) {
            var1 = computeAndroidFingerprint();
         } else {
            var1 = String.join(
               "|",
               System.getProperty(
                  "user.name",
                  "unknown"
               ),
               System.getProperty(
                  "os.name",
                  "unknown"
               ),
               System.getProperty(
                  "os.arch",
                  "unknown"
               ),
               System.getenv().getOrDefault("COMPUTERNAME", ""),
               System.getenv()
                  .getOrDefault(
                     "PROCESSOR_IDENTIFIER", ""
                  ),
               System.getenv()
                  .getOrDefault("PROCESSOR_REVISION", ""),
               readPlatformMachineId()
            );
         }

         MessageDigest var2 = MessageDigest.getInstance("SHA-256");
         byte[] var3 = var2.digest(var1.getBytes(StandardCharsets.UTF_8));
         String var4 = HexFormat.of().formatHex(var3);
         CoreEntry.LOGGER
            .debug(
               "[HWID] Fingerprint computed: {}",
               var4
            );
         return var4;
      } catch (NoSuchAlgorithmException var5) {
         return "unknown-hwid";
      }
   }

   private static boolean isAndroidDevice() {
      String var0 = System.getProperty("java.vm.vendor", "")
         .toLowerCase(Locale.ROOT);
      String var1 = System.getProperty("java.vm.name", "")
         .toLowerCase(Locale.ROOT);
      String var2 = System.getProperty("os.version", "").toLowerCase(Locale.ROOT);
      String var3 = System.getProperty("user.home", "").toLowerCase(Locale.ROOT);
      return var0.contains("android")
         || var1.contains("android")
         || var0.contains("dalvik")
         || var2.contains("android")
         || var3.contains("android");
   }

   private static String computeAndroidFingerprint() {
      StringBuilder var0 = new StringBuilder();
      var0.append(readOrCreateAndroidUuid());
      var0.append("|").append(System.getProperty("os.name", ""));
      var0.append("|").append(System.getProperty("os.arch", ""));
      var0.append("|").append(System.getProperty("os.version", ""));
      var0.append("|")
         .append(readBuildProp("ro.product.model"));
      var0.append("|").append(System.getProperty("user.home", ""));
      var0.append("|").append(readAndroidId());
      var0.append("|").append(readFirstLine("/proc/cpuinfo"));
      var0.append("|").append(readFirstLine("/proc/version"));
      CoreEntry.LOGGER
         .debug(
            "[HWID] Android fingerprint material length: {}",
            var0.length()
         );
      return var0.toString();
   }

   private static String readOrCreateAndroidUuid() {
      try {
         String var0 = System.getProperty("user.home", "");
         String var1 = System.getProperty("java.io.tmpdir", "");
         Path[] var2 = new Path[]{
            Path.of(var0, ".vhwid"),
            Path.of(var1, ".vhwid")
         };

         for (Path var6 : var2) {
            try {
               if (Files.exists(var6)) {
                  String var7 = Files.readString(var6).trim();
                  if (var7.length() == 36 && var7.contains("-")) {
                     return var7;
                  }
               }
            } catch (Exception var9) {
            }
         }

         String var12 = UUID.randomUUID().toString();

         for (Path var16 : var2) {
            try {
               Files.writeString(var16, var12);
               break;
            } catch (Exception var10) {
            }
         }

         return var12;
      } catch (Exception var11) {
         return UUID.randomUUID().toString();
      }
   }

   private static String readAndroidId() {
      try {
         Class var0 = Class.forName(
            "android.provider.Settings$Secure"
         );
         Class var1 = Class.forName(
            "android.content.ContentResolver"
         );
         Class var2 = Class.forName(
            "android.app.ActivityThread"
         );
         Object var3 = var2.getMethod(
               "currentApplication"
            )
            .invoke(null);
         if (var3 == null) {
            return "";
         }

         Object var4 = var3.getClass()
            .getMethod("getContentResolver")
            .invoke(var3);
         if (var4 == null) {
            return "";
         }

         Object var5 = var0.getMethod("getString", var1, String.class)
            .invoke(null, var4, "android_id");
         if (var5 != null) {
            String var6 = var5.toString();
            if (var6 != null && !var6.equals("null") && !var6.isEmpty()) {
               return var6;
            }
         }
      } catch (Exception var7) {
      }

      return "";
   }

   private static String readBuildProp(String key) {
      try {
         String[] var10002 = new String[2];
         byte[] var10005 = new byte[7];
         var10005[0] = (byte)(120);
         var10005[1] = (byte)(-2);
         var10005[2] = (byte)(104);
         var10005[3] = (byte)(-82);
         var10005[4] = (byte)(-34);
         var10005[5] = (byte)(26);
         var10005[6] = (byte)(52);
         var10002[0] = $$v_s(var10005, -5312089127528443590L);
         var10002[1] = key;
         Process var1 = new ProcessBuilder(var10002).redirectErrorStream(true).start();
         String var2 = new String(var1.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
         var1.destroy();
         return var2;
      } catch (Exception var3) {
         return "";
      }
   }

   private static String readFirstLine(String filePath) {
      try {
         String var10000 = Files.readString(Path.of(filePath));
         byte[] var10001 = new byte[2];
         var10001[0] = (byte)(67);
         var10001[1] = (byte)(-55);
         return var10000.split($$v_s(var10001, -5312089127528443590L))[0].trim();
      } catch (Exception var2) {
         return "";
      }
   }

   private static String readPlatformMachineId() {
      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(112);
      var10000[1] = (byte)(-24);
      var10000[2] = (byte)(50);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(-51);
      var10000[5] = (byte)(24);
      var10000[6] = (byte)(33);
      String var0 = System.getProperty($$v_s(var10000, -5312089127528443590L), "").toLowerCase(Locale.ROOT);
      byte[] var10001 = new byte[3];
      var10001[0] = (byte)(104);
      var10001[1] = (byte)(-14);
      var10001[2] = (byte)(114);
      if (var0.contains($$v_s(var10001, -5312089127528443590L))) {
         return readWindowsMachineGuid();
      }

      var10001 = new byte[3];
      var10001[0] = (byte)(114);
      var10001[1] = (byte)(-6);
      var10001[2] = (byte)(127);
      if (!var0.contains($$v_s(var10001, -5312089127528443590L))) {
         var10001 = new byte[6];
         var10001[0] = (byte)(123);
         var10001[1] = (byte)(-6);
         var10001[2] = (byte)(110);
         var10001[3] = (byte)(-87);
         var10001[4] = (byte)(-59);
         var10001[5] = (byte)(27);
         if (!var0.contains($$v_s(var10001, -5312089127528443590L))) {
            var10001 = new byte[3];
            var10001[0] = (byte)(113);
            var10001[1] = (byte)(-18);
            var10001[2] = (byte)(100);
            if (!var0.contains($$v_s(var10001, -5312089127528443590L))) {
               var10001 = new byte[3];
               var10001[0] = (byte)(113);
               var10001[1] = (byte)(-14);
               var10001[2] = (byte)(100);
               if (!var0.contains($$v_s(var10001, -5312089127528443590L))) {
                  return "";
               }
            }

            return readLinuxMachineId();
         }
      }

      return readMacPlatformUuid();
   }

   private static String readWindowsMachineGuid() {
      String var0 = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
      if (!var0.contains("windows")
         && !var0.contains("win")) {
         return "";
      }

      try {
         Process var1 = new ProcessBuilder(
               "reg",
               "query",
               "HKLM\\SOFTWARE\\Microsoft\\Cryptography",
               "/v",
               "MachineGuid"
            )
            .redirectErrorStream(true)
            .start();
         String var2 = new String(var1.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

         for (String var6 : var2.split("\\R")) {
            String var7 = var6.trim();
            if (var7.toLowerCase(Locale.ROOT).contains("machineguid")) {
               String[] var8 = var7.split("\\s+");
               if (var8.length != 0) {
                  return var8[var8.length - 1].trim();
               }
            }
         }
      } catch (Exception var9) {
      }

      return "";
   }

   private static String readMacPlatformUuid() {
      try {
         Process var0 = new ProcessBuilder(
               "ioreg",
               "-rd1",
               "-c",
               "IOPlatformExpertDevice"
            )
            .redirectErrorStream(true)
            .start();

         String var2;
         try (BufferedReader var1 = new BufferedReader(new InputStreamReader(var0.getInputStream(), StandardCharsets.UTF_8))) {
            while ((var2 = var1.readLine()) != null) {
               if (var2.contains("IOPlatformUUID")) {
                  String[] var3 = var2.split("=");
                  if (var3.length >= 2) {
                     return var3[1].replace("\"", "").trim();
                  }
               }
            }
         }

         var0.waitFor();
      } catch (Exception var7) {
      }

      return "";
   }

   private static String readLinuxMachineId() {
      String var0 = readFirstLine("/etc/machine-id");
      if (!var0.isBlank()) {
         return var0;
      }

      var0 = readFirstLine(
         "/var/lib/dbus/machine-id"
      );
      if (!var0.isBlank()) {
         return var0;
      }

      var0 = readFirstLine(
         "/sys/class/dmi/id/product_uuid"
      );
      return !var0.isBlank() ? var0 : "";
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }

   private record AuthFileCandidate(Path path, long modifiedMs, boolean hasKey) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record KeyBootstrapCandidate(String keyCode, long modifiedMs, Path sourcePath) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record SignedSessionClaims(Instant expiresAt) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum Status {
      VALID,
      MISSING_KEY,
      INVALID,
      OUTDATED_CLIENT,
      EXPIRED,
      REVOKED,
      HWID_MISMATCH,
      RATE_LIMITED,
      SERVER_ERROR;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AuthManager.Status[] $values() {
         AuthManager.Status[] var10000 = new AuthManager.Status[9];
         var10000[0] = VALID;
         var10000[1] = MISSING_KEY;
         var10000[2] = INVALID;
         var10000[3] = OUTDATED_CLIENT;
         var10000[4] = EXPIRED;
         var10000[5] = REVOKED;
         var10000[6] = HWID_MISMATCH;
         var10000[7] = RATE_LIMITED;
         var10000[8] = SERVER_ERROR;
         return var10000;
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }

   public record StatusSnapshot(
      boolean authorized,
      String status,
      String message,
      String keyCode,
      String hwid,
      String apiBaseUrl,
      String customerName,
      String plan,
      Instant expiresAt,
      long lastValidatedAtMs
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
