package me.drake.vieclient.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import me.drake.vieclient.CoreEntry;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.auth.AuthManager;
import me.drake.vieclient.core.i18n.Localization;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.modules.client.HUD;
import me.drake.vieclient.modules.client.hud.HudLayoutCodec;
import net.minecraft.class_310;

public final class ConfigSyncManager {
   private static final String API_BASE = "https://vieservices.top/api";
   private static final String FALLBACK_API_BASE = "https://34.126.189.48/api";
   private static final String SAVE_ENDPOINT = "https://vieservices.top/api/v1/config/save";
   private static final String IMPORT_ENDPOINT = "https://vieservices.top/api/v1/config/import";
   private static final String FALLBACK_SAVE_ENDPOINT = "https://34.126.189.48/api/v1/config/save";
   private static final String FALLBACK_IMPORT_ENDPOINT = "https://34.126.189.48/api/v1/config/import";
   private static final String COMMUNITY_LIST_ENDPOINT = "https://vieservices.top/api/v1/community/configs";
   private static final String FALLBACK_COMMUNITY_LIST_ENDPOINT = "https://34.126.189.48/api/v1/community/configs";
   private static final String COMMUNITY_PUBLISH_ENDPOINT = "https://vieservices.top/api/v1/community/config/publish";
   private static final String FALLBACK_COMMUNITY_PUBLISH_ENDPOINT = "https://34.126.189.48/api/v1/community/config/publish";
   private static final String COMMUNITY_LIKE_ENDPOINT = "https://vieservices.top/api/v1/community/config/like";
   private static final String FALLBACK_COMMUNITY_LIKE_ENDPOINT = "https://34.126.189.48/api/v1/community/config/like";
   private static final String COMMUNITY_DOWNLOAD_ENDPOINT = "https://vieservices.top/api/v1/community/config/download/";
   private static final String FALLBACK_COMMUNITY_DOWNLOAD_ENDPOINT = "https://34.126.189.48/api/v1/community/config/download/";
   private static final String COMMUNITY_DELETE_ENDPOINT = "https://vieservices.top/api/v1/community/config/delete";
   private static final String FALLBACK_COMMUNITY_DELETE_ENDPOINT = "https://34.126.189.48/api/v1/community/config/delete";
   private static final String CHIMERA_PROTOCOL = "CHIMERA-PROTECT";
   private static final Duration TIMEOUT = Duration.ofSeconds(15L);
   private static final long RATE_WINDOW_MS = 3600000L;
   private static final int MAX_GENERATES_PER_HOUR = 30;
   private static ConfigSyncManager INSTANCE;
   private volatile String lastSaveError = "";
   private final HttpClient httpClient;
   private final Gson GSON;
   private final ExecutorService executor;
   private final List generateTimestamps = new ArrayList();
   private final Map cloudRecords = new ConcurrentHashMap();
   private volatile AuthManager fallbackAuthManager;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public String getLastSaveError() {
      return this.lastSaveError;
   }

   private ConfigSyncManager() {
      HttpClient var1;
      try {
         TrustManager[] var2 = new TrustManager[]{new X509TrustManager() {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public X509Certificate[] getAcceptedIssuers() {
               return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
         }};
         byte[] var10000 = new byte[3];
         var10000[0] = (byte)(-96);
         var10000[1] = (byte)(-42);
         var10000[2] = (byte)(79);
         SSLContext var3 = SSLContext.getInstance($$v_s(var10000, -6554315857056247599L));
         var3.init(null, var2, new SecureRandom());
         var1 = HttpClient.newBuilder().sslContext(var3).connectTimeout(Duration.ofSeconds(6L)).build();
      } catch (Throwable var4) {
         var1 = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build();
      }

      this.httpClient = var1;
      this.GSON = new GsonBuilder().create();
      this.executor = Executors.newVirtualThreadPerTaskExecutor();
      this.loadRegistry();
   }

   private Path resolveRegistryFile() {
      Path var10000 = me.drake.vieclient.gui.nvg.adapter.ConfigManager.getConfigsDir();
      byte[] var10001 = new byte[20];
      var10001[0] = (byte)(-38);
      var10001[1] = (byte)(-7);
      var10001[2] = (byte)(112);
      var10001[3] = (byte)(-79);
      var10001[4] = (byte)(-72);
      var10001[5] = (byte)(-34);
      var10001[6] = (byte)(86);
      var10001[7] = (byte)(-1);
      var10001[8] = (byte)(-7);
      var10001[9] = (byte)(-59);
      var10001[10] = (byte)(-115);
      var10001[11] = (byte)(-123);
      var10001[12] = (byte)(-31);
      var10001[13] = (byte)(16);
      var10001[14] = (byte)(88);
      var10001[15] = (byte)(-37);
      var10001[16] = (byte)(-50);
      var10001[17] = (byte)(57);
      var10001[18] = (byte)(-93);
      var10001[19] = (byte)(-64);
      return var10000.resolve($$v_s(var10001, -6554315857056247599L));
   }

   public synchronized void loadRegistry() {
      try {
         Path var1 = this.resolveRegistryFile();
         if (!Files.exists(var1)) {
            return;
         }

         String var2 = Files.readString(var1, StandardCharsets.UTF_8);
         JsonObject var3 = (JsonObject)this.GSON.fromJson(var2, JsonObject.class);
         if (var3 == null) {
            return;
         }

         JsonObject var4 = var3.has("configs")
               && var3.get("configs").isJsonObject()
            ? var3.getAsJsonObject("configs")
            : var3;

         for (String var6 : var4.keySet()) {
            JsonElement var7 = var4.get(var6);
            if (var7 != null && var7.isJsonObject()) {
               JsonObject var16 = var7.getAsJsonObject();
               String var9 = var16.has("code")
                  ? var16.get("code").getAsString()
                  : "";
               long var10 = var16.has("uploadedAt")
                  ? var16.get("uploadedAt").getAsLong()
                  : System.currentTimeMillis();
               String var12 = var16.has("authorDiscordId")
                  ? var16.get("authorDiscordId")
                     .getAsString()
                  : "";
               String var13 = var16.has(
                     "authorDiscordName"
                  )
                  ? var16.get("authorDiscordName")
                     .getAsString()
                  : "";
               boolean var14 = !var16.has("isOwner")
                  || var16.get("isOwner").getAsBoolean();
               if (!var9.isBlank()) {
                  this.cloudRecords.put(sanitizeProfileName(var6), new ConfigSyncManager.CloudRecord(var9, var10, var12, var13, var14));
               }
            } else if (var7 != null && var7.isJsonPrimitive()) {
               String var8 = var7.getAsString();
               if (!var8.isBlank()) {
                  this.cloudRecords.put(sanitizeProfileName(var6), new ConfigSyncManager.CloudRecord(var8, System.currentTimeMillis(), "", "", true));
               }
            }
         }
      } catch (Throwable var15) {
         VieClientMod.LOGGER
            .warn(
               "[ConfigSync] Failed to load cloud config registry: {}",
               var15.toString()
            );
      }
   }

   public synchronized void saveRegistry() {
      try {
         Path var1 = this.resolveRegistryFile();
         if (var1.getParent() != null) {
            Files.createDirectories(var1.getParent());
         }

         JsonObject var2 = new JsonObject();
         JsonObject var3 = new JsonObject();

         for (Entry var5 : this.cloudRecords.entrySet()) {
            JsonObject var6 = new JsonObject();
            var6.addProperty("code", ((ConfigSyncManager.CloudRecord)var5.getValue()).getCode());
            var6.addProperty(
               "uploadedAt",
               ((ConfigSyncManager.CloudRecord)var5.getValue()).getUploadedAt()
            );
            var6.addProperty(
               "authorDiscordId",
               ((ConfigSyncManager.CloudRecord)var5.getValue()).getAuthorDiscordId()
            );
            var6.addProperty(
               "authorDiscordName",
               ((ConfigSyncManager.CloudRecord)var5.getValue()).getAuthorDiscordName()
            );
            var6.addProperty(
               "isOwner", ((ConfigSyncManager.CloudRecord)var5.getValue()).isOwner()
            );
            var3.add((String)var5.getKey(), var6);
         }

         var2.add("configs", var3);
         Files.writeString(var1, this.GSON.toJson(var2), StandardCharsets.UTF_8);
      } catch (Throwable var7) {
         VieClientMod.LOGGER
            .warn(
               "[ConfigSync] Failed to save cloud config registry: {}",
               var7.toString()
            );
      }
   }

   public String getCloudCode(String configName) {
      if (configName != null && !configName.isBlank()) {
         String var2 = sanitizeProfileName(configName);
         ConfigSyncManager.CloudRecord var3 = (ConfigSyncManager.CloudRecord)this.cloudRecords.get(var2);
         if (var3 == null) {
            for (Entry var5 : this.cloudRecords.entrySet()) {
               if (((String)var5.getKey()).equalsIgnoreCase(var2)) {
                  var3 = (ConfigSyncManager.CloudRecord)var5.getValue();
                  break;
               }
            }
         }

         return var3 != null && !var3.getCode().isBlank() ? var3.getCode() : null;
      } else {
         return null;
      }
   }

   public boolean hasCloudCode(String configName) {
      return this.getCloudCode(configName) != null;
   }

   public ConfigSyncManager.CloudRecord getCloudRecord(String configName) {
      if (configName != null && !configName.isBlank()) {
         String var2 = sanitizeProfileName(configName);
         ConfigSyncManager.CloudRecord var3 = (ConfigSyncManager.CloudRecord)this.cloudRecords.get(var2);
         if (var3 == null) {
            for (Entry var5 : this.cloudRecords.entrySet()) {
               if (((String)var5.getKey()).equalsIgnoreCase(var2)) {
                  return (ConfigSyncManager.CloudRecord)var5.getValue();
               }
            }
         }

         return var3;
      } else {
         return null;
      }
   }

   public void setCloudRecord(String configName, ConfigSyncManager.CloudRecord record) {
      if (configName != null && !configName.isBlank() && record != null) {
         String var3 = sanitizeProfileName(configName);
         this.cloudRecords.put(var3, record);
         this.saveRegistry();
      }
   }

   public void setCloudCode(String configName, String code) {
      if (configName != null && !configName.isBlank() && code != null && !code.isBlank()) {
         String var3 = "";
         String var4 = "";
         AuthManager var5 = this.resolveAuthManager();
         if (var5 != null) {
            var3 = var5.getDiscordUserId();
            var4 = var5.getDiscordDisplayName();
            if (var4.startsWith("@")) {
               var4 = var4.substring(1);
            }
         }

         this.setCloudRecord(configName, new ConfigSyncManager.CloudRecord(code, System.currentTimeMillis(), var3, var4, true));
      }
   }

   public void removeCloudCode(String configName) {
      if (configName != null) {
         String var2 = sanitizeProfileName(configName);
         this.cloudRecords.remove(var2);
         this.cloudRecords.keySet().removeIf(k -> k.equalsIgnoreCase(var2));
         this.saveRegistry();
      }
   }

   public static synchronized ConfigSyncManager getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ConfigSyncManager();
      }

      return INSTANCE;
   }

   public void saveAndShareAsync(String configName, ConfigSyncManager.SaveCallback callback) {
      if (!this.canGenerateNow()) {
         this.lastSaveError = "Rate limit: " + this.cooldownSecondsLeft() + "s left (30/hour)";
         if (callback != null) {
            class_310.method_1551().execute(() -> callback.onComplete(null));
         }
      } else {
         CompletableFuture.runAsync(() -> {
            String var3 = this.saveAndShare(configName);
            if (var3 != null) {
               this.recordGenerate();
            }

            if (callback != null) {
               class_310.method_1551().execute(() -> callback.onComplete(var3));
            }
         }, this.executor);
      }
   }

   public boolean canGenerateNow() {
      this.pruneOldTimestamps();
      return this.generateTimestamps.size() < (30);
   }

   public int generatesRemaining() {
      this.pruneOldTimestamps();
      return (30) - this.generateTimestamps.size();
   }

   public int cooldownSecondsLeft() {
      this.pruneOldTimestamps();
      if (this.generateTimestamps.size() < (30)) {
         return 0;
      }

      long var1 = (Long)this.generateTimestamps.get(0);
      long var3 = var1 + 3600000L - System.currentTimeMillis();
      return var3 > 0L ? (int)Math.ceil(var3 / 1000.0) : 0;
   }

   private void recordGenerate() {
      this.generateTimestamps.add(System.currentTimeMillis());
   }

   private void pruneOldTimestamps() {
      long var1 = System.currentTimeMillis() - 3600000L;
      this.generateTimestamps.removeIf(ts -> ts <= var1);
   }

   public String saveAndShare(String configName) {
      try {
         ConfigSyncManager.CloudRecord var2 = this.getCloudRecord(configName);
         if (var2 != null && !var2.getCode().isBlank() && !var2.isOwner()) {
            String var9 = var2.getAuthorDiscordName();
            this.lastSaveError = Localization.isVietnamese()
               ? "Chỉ chính chủ upload (@"
                  + (var9.isEmpty() ? "author" : var9)
                  + ") mới có thể cập nhật config này!"
               : "Only the original author (@"
                  + (var9.isEmpty() ? "author" : var9)
                  + ") can update this config!";
            return null;
         }

         JsonObject var3 = this.buildConfigPayload(configName);
         String var4 = this.GSON.toJson(var3);
         HttpResponse var5 = this.executeHttp(
            "https://vieservices.top/api/v1/config/save",
            var4
         );
         if (var5 == null) {
            var5 = this.executeHttp(
               "https://34.126.189.48/api/v1/config/save",
               var4
            );
         }

         if (var5 == null) {
            this.lastSaveError = "Network error - server unreachable";
            return null;
         }

         if (var5.statusCode() == 200 || var5.statusCode() == 201) {
            JsonObject var12 = (JsonObject)this.GSON.fromJson((String)var5.body(), JsonObject.class);
            if (var12 != null && var12.has("code")) {
               this.lastSaveError = "";
               String var7 = var12.get("code").getAsString();
               this.setCloudCode(configName, var7);
               return var7;
            }

            this.lastSaveError = "Server response missing code";
         } else if (var5.statusCode() == 403) {
            String var6 = this.extractServerErrorMessage((String)var5.body());
            this.lastSaveError = var6 != null
               ? var6
               : (
                  Localization.isVietnamese()
                     ? "Chỉ chính chủ upload mới có thể cập nhật config này!"
                     : "Only the original author can update this config!"
               );
         } else if (var5.statusCode() == 400) {
            String var10 = this.extractServerErrorMessage((String)var5.body());
            this.lastSaveError = var10 != null
               ? "Bad request: " + var10
               : "HTTP 400 - server rejected payload";
         } else if (var5.statusCode() == 401) {
            this.lastSaveError = "Invalid license key - cannot share config";
         } else if (var5.statusCode() == 429) {
            this.lastSaveError = "Server rate limit (429)";
         } else if (var5.statusCode() == 500) {
            String var11 = this.extractServerErrorMessage((String)var5.body());
            this.lastSaveError = var11 != null
               ? "Server error (500): " + var11
               : "Server error (500)";
         } else {
            this.lastSaveError = "HTTP " + var5.statusCode();
         }

         VieClientMod.LOGGER
            .warn(
               "[ConfigSync] Save failed: status={}, body={}",
               var5.statusCode(),
               var5.body()
            );
      } catch (Exception var8) {
         this.lastSaveError = "Error: " + var8.toString();
         VieClientMod.LOGGER
            .warn(
               "[ConfigSync] Save error: {}",
               var8.toString()
            );
      }

      return null;
   }

   private HttpResponse executeHttpGet(String url) {
      try {
         HttpRequest var2 = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(TIMEOUT)
            .header(
               "Accept",
               "application/json"
            )
            .header(
               "X-Chimera-Protocol",
               "CHIMERA-PROTECT"
            )
            .GET()
            .build();
         return this.httpClient.send(var2, BodyHandlers.ofString());
      } catch (Throwable var3) {
         VieClientMod.LOGGER
            .warn(
               "[ConfigSync] HTTP GET request to {} failed: {}",
               url,
               var3.toString()
            );
         return null;
      }
   }

   private HttpResponse executeHttp(String url, String json) {
      try {
         HttpRequest var3 = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .timeout(TIMEOUT)
            .header(
               "Content-Type",
               "application/json"
            )
            .header(
               "X-Chimera-Protocol",
               "CHIMERA-PROTECT"
            )
            .POST(BodyPublishers.ofString(json))
            .build();
         return this.httpClient.send(var3, BodyHandlers.ofString());
      } catch (Throwable var4) {
         VieClientMod.LOGGER
            .warn(
               "[ConfigSync] HTTP request to {} failed: {}",
               url,
               var4.toString()
            );
         return null;
      }
   }

   public void importByCodeAsync(String code, ConfigSyncManager.ImportCallback callback) {
      CompletableFuture.runAsync(() -> {
         String var3 = this.importByCode(code);
         if (callback != null) {
            class_310.method_1551().execute(() -> callback.onComplete(var3));
         }
      }, this.executor);
   }

   public boolean probeCodeExists(String code) {
      if (code != null && !code.isBlank()) {
         try {
            JsonObject var2 = new JsonObject();
            var2.addProperty("code", code.toUpperCase().trim());
            HttpRequest var3 = HttpRequest.newBuilder()
               .uri(
                  URI.create(
                     "https://vieservices.top/api/v1/config/import"
                  )
               )
               .timeout(TIMEOUT)
               .header(
                  "Content-Type",
                  "application/json"
               )
               .header(
                  "X-Chimera-Protocol",
                  "CHIMERA-PROTECT"
               )
               .header(
                  "X-Chimera-Probe",
                  "true"
               )
               .POST(BodyPublishers.ofString(this.GSON.toJson(var2)))
               .build();
            HttpResponse var4 = this.httpClient.send(var3, BodyHandlers.ofString());
            if (var4.statusCode() != 200) {
               return false;
            } else {
               JsonObject var5 = (JsonObject)this.GSON.fromJson((String)var4.body(), JsonObject.class);
               if (var5 != null && var5.has("config")) {
                  JsonElement var6 = var5.get("config");
                  return var6 != null && !var6.isJsonNull();
               } else {
                  return false;
               }
            }
         } catch (Exception var7) {
            return false;
         }
      } else {
         return false;
      }
   }

   public void probeCodeExistsAsync(String code, ConfigSyncManager.ProbeCallback callback) {
      CompletableFuture.runAsync(() -> {
         boolean var3 = this.probeCodeExists(code);
         if (callback != null) {
            class_310.method_1551().execute(() -> callback.onComplete(var3));
         }
      }, this.executor);
   }

   public String importByCode(String code) {
      try {
         JsonObject var2 = new JsonObject();
         var2.addProperty("code", code.toUpperCase().trim());
         String var3 = this.GSON.toJson(var2);
         HttpResponse var4 = this.executeHttp(
            "https://vieservices.top/api/v1/config/import",
            var3
         );
         if (var4 == null) {
            var4 = this.executeHttp(
               "https://34.126.189.48/api/v1/config/import",
               var3
            );
         }

         if (var4 == null) {
            this.lastSaveError = "Network error - server unreachable";
            return null;
         }

         if (var4.statusCode() != 200) {
            if (var4.statusCode() == 400) {
               String var18 = this.extractServerErrorMessage((String)var4.body());
               this.lastSaveError = var18 != null
                  ? "Bad request: " + var18
                  : "HTTP 400 - server rejected payload";
            } else if (var4.statusCode() == 404) {
               this.lastSaveError = "Config code not found";
            } else {
               this.lastSaveError = "Import failed: HTTP " + var4.statusCode();
            }
         } else {
            JsonObject var5 = (JsonObject)this.GSON.fromJson((String)var4.body(), JsonObject.class);
            if (var5 != null
               && (
                  var5.has("config")
                     || var5.has("modules")
               )) {
               JsonObject var19 = var5.has("config")
                     && var5.get("config").isJsonObject()
                  ? var5.getAsJsonObject("config")
                  : var5;
               String var7 = var5.has("name")
                  ? var5.get("name").getAsString()
                  : code;
               this.deserializeIntoModules(var19.has("modules") ? var19 : var5);
               JsonElement var8 = var19.has("hud")
                     && var19.get("hud").isJsonObject()
                  ? var19.get("hud")
                  : (
                     var5.has("hud")
                           && var5.get("hud").isJsonObject()
                        ? var5.get("hud")
                        : null
                  );
               if (var8 != null && var8.isJsonObject()) {
                  HUD var9 = HUD.instance();
                  if (var9 != null) {
                     try {
                        HudLayoutCodec.Layout var10 = HudLayoutCodec.decode(var8.getAsJsonObject());
                        var9.setWidgetPositions(var10.positions());
                        var9.setWidgetScales(var10.scales());
                     } catch (Throwable var16) {
                     }
                  }
               }

               if (VieClient.INSTANCE != null) {
                  VieClient.INSTANCE.getConfigManager().saveProfile(var7);
               }

               String var20 = var5.has(
                        "uploaderDiscordId"
                     )
                     && !var5.get("uploaderDiscordId")
                        .isJsonNull()
                  ? var5.get("uploaderDiscordId")
                     .getAsString()
                  : "";
               String var21 = var5.has(
                        "uploaderDiscordName"
                     )
                     && !var5.get(
                           "uploaderDiscordName"
                        )
                        .isJsonNull()
                  ? var5.get(
                        "uploaderDiscordName"
                     )
                     .getAsString()
                  : "Vie User";
               AuthManager var11 = this.resolveAuthManager();
               String var12 = var11 != null ? var11.getDiscordUserId() : "";
               String var13 = this.resolveOwnerKey();
               boolean var14 = !var12.isBlank() && var12.equalsIgnoreCase(var20) || !var13.isBlank() && var13.equalsIgnoreCase(var20);
               this.setCloudRecord(var7, new ConfigSyncManager.CloudRecord(code, System.currentTimeMillis(), var20, var21, var14));
               ModerationConfigManager var15 = VieClientMod.getInstance().getModerationConfigManager();
               var15.saveCurrentAs(code, var7, false);
               return var7;
            }

            String var6 = this.extractServerErrorMessage((String)var4.body());
            this.lastSaveError = var6 != null
               ? var6
               : "Server response missing config";
         }

         VieClientMod.LOGGER
            .warn(
               "[ConfigSync] Import failed: status={}, body={}",
               var4.statusCode(),
               var4.body()
            );
      } catch (Exception var17) {
         VieClientMod.LOGGER
            .warn(
               "[ConfigSync] Import error: {}",
               var17.toString()
            );
      }

      return null;
   }

   private JsonObject buildConfigPayload(String configName) {
      JsonObject var2 = new JsonObject();
      var2.addProperty("name", configName);
      ConfigSyncManager.CloudRecord var3 = this.getCloudRecord(configName);
      if (var3 != null && !var3.getCode().isBlank()) {
         var2.addProperty("code", var3.getCode());
      }

      String var4 = this.resolveOwnerKey();
      if (!var4.isEmpty()) {
         var2.addProperty("ownerKey", var4);
         String var5 = this.resolveSessionToken();
         if (var5 != null && !var5.isBlank()) {
            var2.addProperty("sessionToken", var5);
         }
      }

      JsonObject var10 = this.readProfileFile(configName);
      if (var10 != null
         && var10.has("modules")
         && var10.get("modules").isJsonObject()
         && !var10.getAsJsonObject("modules").isEmpty()) {
         var2.add(
            "modules",
            var10.getAsJsonObject("modules")
         );
         if (var10.has("hud")
            && var10.get("hud").isJsonObject()) {
            var2.add("hud", var10.getAsJsonObject("hud"));
         }

         return var2;
      } else {
         JsonObject var6 = new JsonObject();

         for (Module var8 : VieClientMod.getInstance().getModuleManager().getModules()) {
            var6.add(var8.getName(), this.serializeModule(var8));
         }

         var2.add("modules", var6);
         HUD var11 = HUD.instance();
         if (var11 != null) {
            try {
               var2.add("hud", HudLayoutCodec.encode(var11.getWidgetPositions(), var11.getWidgetScales()));
            } catch (Throwable var9) {
            }
         }

         return var2;
      }
   }

   private JsonObject readProfileFile(String configName) {
      if (configName != null
         && !configName.isBlank()
         && !configName.equalsIgnoreCase("Current")) {
         try {
            Path var2 = me.drake.vieclient.gui.nvg.adapter.ConfigManager.getConfigsDir();
            String var3 = sanitizeProfileName(configName);
            Path var4 = var2.resolve(var3 + ".json");
            if (!Files.exists(var4)) {
               Path var5 = Path.of(
                  "vieclient",
                  "configs",
                  var3 + ".json"
               );
               if (Files.exists(var5)) {
                  var4 = var5;
               } else {
                  Path var6 = Path.of(
                     "DonutBBC",
                     "configs",
                     var3 + ".json"
                  );
                  if (Files.exists(var6)) {
                     var4 = var6;
                  }
               }
            }

            if (Files.exists(var4)) {
               String var8 = Files.readString(var4, StandardCharsets.UTF_8);
               return (JsonObject)this.GSON.fromJson(var8, JsonObject.class);
            }
         } catch (Throwable var7) {
         }

         return null;
      } else {
         return null;
      }
   }

   private static String sanitizeProfileName(String name) {
      StringBuilder var1 = new StringBuilder();

      for (char var5 : name.toCharArray()) {
         if (Character.isLetterOrDigit(var5) || var5 == (45) || var5 == (95) || var5 == (32)) {
            var1.append(var5);
         }
      }

      String var6 = var1.toString().trim();
      String var7;
      if (var6.isEmpty()) {
         byte[] var10000 = new byte[6];
         var10000[0] = (byte)(-105);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-72);
         var10000[4] = (byte)(-92);
         var10000[5] = (byte)(-35);
         var7 = $$v_s(var10000, -6554315857056247599L);
      } else {
         var7 = var6;
      }

      return var7;
   }

   private AuthManager resolveAuthManager() {
      CoreEntry var1 = CoreEntry.getInstance();
      return var1 != null && var1.getAuthManager() != null ? var1.getAuthManager() : this.ensureFallbackAuthManager();
   }

   private String resolveOwnerKey() {
      CoreEntry var1 = CoreEntry.getInstance();
      if (var1 != null) {
         AuthManager var2 = var1.getAuthManager();
         if (var2 != null) {
            String var3 = var2.getKeyCode();
            if (var3 != null && !var3.isBlank()) {
               return var3;
            }
         }
      }

      AuthManager var4 = this.ensureFallbackAuthManager();
      if (var4 != null) {
         String var5 = var4.getKeyCode();
         if (var5 != null && !var5.isBlank()) {
            return var5;
         }
      }

      return "";
   }

   private String resolveSessionToken() {
      CoreEntry var1 = CoreEntry.getInstance();
      if (var1 != null) {
         AuthManager var2 = var1.getAuthManager();
         if (var2 != null) {
            String var3 = var2.getCachedSessionToken();
            if (var3 != null && !var3.isBlank()) {
               return var3;
            }
         }
      }

      return "";
   }

   private AuthManager ensureFallbackAuthManager() {
      if (this.fallbackAuthManager == null) {
         synchronized (this) {
            if (this.fallbackAuthManager == null) {
               try {
                  AuthManager var2 = new AuthManager();
                  var2.initialize();
                  this.fallbackAuthManager = var2;
               } catch (Throwable var4) {
                  byte[] var10001 = new byte[49];
                  var10001[0] = (byte)(-81);
                  var10001[1] = (byte)(-39);
                  var10001[2] = (byte)(115);
                  var10001[3] = (byte)(-80);
                  var10001[4] = (byte)(-85);
                  var10001[5] = (byte)(-45);
                  var10001[6] = (byte)(110);
                  var10001[7] = (byte)(-34);
                  var10001[8] = (byte)(-27);
                  var10001[9] = (byte)(-52);
                  var10001[10] = (byte)(-121);
                  var10001[11] = (byte)(-85);
                  var10001[12] = (byte)(-75);
                  var10001[13] = (byte)(36);
                  var10001[14] = (byte)(64);
                  var10001[15] = (byte)(-103);
                  var10001[16] = (byte)(-56);
                  var10001[17] = (byte)(40);
                  var10001[18] = (byte)(-83);
                  var10001[19] = (byte)(-51);
                  var10001[20] = (byte)(22);
                  var10001[21] = (byte)(106);
                  var10001[22] = (byte)(24);
                  var10001[23] = (byte)(-88);
                  var10001[24] = (byte)(56);
                  var10001[25] = (byte)(122);
                  var10001[26] = (byte)(-39);
                  var10001[27] = (byte)(39);
                  var10001[28] = (byte)(43);
                  var10001[29] = (byte)(83);
                  var10001[30] = (byte)(22);
                  var10001[31] = (byte)(96);
                  var10001[32] = (byte)(102);
                  var10001[33] = (byte)(26);
                  var10001[34] = (byte)(21);
                  var10001[35] = (byte)(16);
                  var10001[36] = (byte)(68);
                  var10001[37] = (byte)(110);
                  var10001[38] = (byte)(-119);
                  var10001[39] = (byte)(11);
                  var10001[40] = (byte)(93);
                  var10001[41] = (byte)(-85);
                  var10001[42] = (byte)(40);
                  var10001[43] = (byte)(115);
                  var10001[44] = (byte)(-111);
                  var10001[45] = (byte)(-8);
                  var10001[46] = (byte)(-31);
                  var10001[47] = (byte)(46);
                  var10001[48] = (byte)(-71);
                  VieClientMod.LOGGER.warn($$v_s(var10001, -6554315857056247599L), var4.toString());
               }
            }
         }
      }

      return this.fallbackAuthManager;
   }

   private JsonObject serializeModule(Module module) {
      JsonObject var2 = new JsonObject();
      byte[] var10001 = new byte[7];
      var10001[0] = (byte)(-111);
      var10001[1] = (byte)(-12);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(-95);
      var10001[5] = (byte)(-33);
      var10001[6] = (byte)(109);
      var2.addProperty($$v_s(var10001, -6554315857056247599L), module.isEnabled());
      var10001 = new byte[7];
      var10001[0] = (byte)(-97);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(101);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(-92);
      var10001[5] = (byte)(-44);
      var10001[6] = (byte)(109);
      var2.add($$v_s(var10001, -6554315857056247599L), module.getKeybind().toJson());
      JsonObject var3 = new JsonObject();

      for (Setting var5 : module.getSettings()) {
         var3.add(var5.getName(), var5.toJson());
      }

      var10001 = new byte[8];
      var10001[0] = (byte)(-121);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-86);
      var10001[4] = (byte)(-92);
      var10001[5] = (byte)(-44);
      var10001[6] = (byte)(110);
      var10001[7] = (byte)(-2);
      var2.add($$v_s(var10001, -6554315857056247599L), var3);
      return var2;
   }

   private void deserializeIntoModules(JsonObject data) {
      if (data != null && data.has("modules")) {
         JsonObject var2 = data.getAsJsonObject("modules");

         for (Module var4 : VieClientMod.getInstance().getModuleManager().getModules()) {
            JsonObject var5 = this.resolveMod(var2, var4.getName());
            if (var5 != null) {
               JsonElement var6 = var5.get("settings");
               JsonObject var7 = var6 != null && var6.isJsonObject() ? var6.getAsJsonObject() : null;
               if (var5.has("enabled")) {
                  boolean var8 = var5.get("enabled").getAsBoolean();
                  boolean var9 = ModuleStateMigration.resolveEnabled(var4.getClass(), var8, var7);
                  if (var9 != var4.isEnabled()) {
                     var4.setEnabled(var9);
                  }
               }

               if (var5.has("keybind")) {
                  var4.getKeybind().fromJson(var5.get("keybind"));
               }

               if (var7 != null) {
                  for (Setting var13 : var4.getSettings()) {
                     try {
                        JsonElement var10 = this.resolveSetting(var7, var13.getName());
                        if (var10 != null) {
                           var13.fromJson(var10);
                        }
                     } catch (Throwable var11) {
                     }
                  }
               }
            }
         }
      }
   }

   private JsonObject resolveMod(JsonObject root, String name) {
      if (root.has(name)) {
         return root.getAsJsonObject(name);
      }

      byte[] var10000 = new byte[8];
      var10000[0] = (byte)(-71);
      var10000[1] = (byte)(-17);
      var10000[2] = (byte)(111);
      var10000[3] = (byte)(-73);
      var10000[4] = (byte)(-82);
      var10000[5] = (byte)(-14);
      var10000[6] = (byte)(92);
      var10000[7] = (byte)(-55);
      if ($$v_s(var10000, -6554315857056247599L).equals(name)) {
         byte[] var10001 = new byte[11];
         var10001[0] = (byte)(-89);
         var10001[1] = (byte)(-22);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(-92);
         var10001[5] = (byte)(-36);
         var10001[6] = (byte)(112);
         var10001[7] = (byte)(-83);
         var10001[8] = (byte)(-44);
         var10001[9] = (byte)(-9);
         var10001[10] = (byte)(-96);
         if (root.has($$v_s(var10001, -6554315857056247599L))) {
            var10001 = new byte[11];
            var10001[0] = (byte)(-89);
            var10001[1] = (byte)(-22);
            var10001[2] = (byte)(115);
            var10001[3] = (byte)(-86);
            var10001[4] = (byte)(-92);
            var10001[5] = (byte)(-36);
            var10001[6] = (byte)(112);
            var10001[7] = (byte)(-83);
            var10001[8] = (byte)(-44);
            var10001[9] = (byte)(-9);
            var10001[10] = (byte)(-96);
            return root.getAsJsonObject($$v_s(var10001, -6554315857056247599L));
         }
      }

      String var3 = normalize(name);

      for (String var5 : root.keySet()) {
         if (normalize(var5).equals(var3)) {
            return root.getAsJsonObject(var5);
         }
      }

      return null;
   }

   private JsonElement resolveSetting(JsonObject obj, String name) {
      if (obj.has(name)) {
         return obj.get(name);
      }

      String var3 = normalize(name);

      for (String var5 : obj.keySet()) {
         if (normalize(var5).equals(var3)) {
            return obj.get(var5);
         }
      }

      return null;
   }

   private static String normalize(String v) {
      if (v == null) {
         return "";
      }

      String var1 = v.toLowerCase();
      StringBuilder var2 = new StringBuilder(var1.length());

      for (int var3 = 0; var3 < var1.length(); var3++) {
         char var4 = var1.charAt(var3);
         if (var4 >= (97) && var4 <= (122) || var4 >= (48) && var4 <= (57)) {
            var2.append(var4);
         }
      }

      return var2.toString();
   }

   public List fetchCommunityConfigs(String sort, String search, int page, int limit) {
      ArrayList var5 = new ArrayList();

      try {
         String var6 = "?sort="
            + URLEncoder.encode(sort != null ? sort : "top", StandardCharsets.UTF_8)
            + "&search="
            + URLEncoder.encode(search != null ? search : "", StandardCharsets.UTF_8)
            + "&page="
            + Math.max(1, page)
            + "&limit="
            + Math.max(1, limit);
         String var7 = this.resolveOwnerKey();
         if (!var7.isEmpty()) {
            var6 = var6 + "&userKey=" + URLEncoder.encode(var7, StandardCharsets.UTF_8);
         }

         HttpResponse var8 = this.executeHttpGet("https://vieservices.top/api/v1/community/configs" + var6);
         if (var8 == null || var8.statusCode() != 200) {
            var8 = this.executeHttpGet("https://34.126.189.48/api/v1/community/configs" + var6);
         }

         if (var8 != null && var8.statusCode() == 200) {
            JsonObject var9 = (JsonObject)this.GSON.fromJson((String)var8.body(), JsonObject.class);
            if (var9 != null
               && var9.has("configs")
               && var9.get("configs").isJsonArray()) {
               for (JsonElement var12 : var9.getAsJsonArray("configs")) {
                  if (var12 != null && var12.isJsonObject()) {
                     JsonObject var13 = var12.getAsJsonObject();
                     String var14 = var13.has("code")
                        ? var13.get("code").getAsString()
                        : "";
                     String var15 = var13.has("name")
                        ? var13.get("name").getAsString()
                        : "";
                     String var16 = var13.has("title")
                        ? var13.get("title").getAsString()
                        : var15;
                     String var17 = var13.has("description")
                        ? var13.get("description").getAsString()
                        : "";
                     ArrayList var18 = new ArrayList();
                     if (var13.has("tags")
                        && var13.get("tags").isJsonArray()) {
                        for (JsonElement var20 : var13.getAsJsonArray("tags")) {
                           var18.add(var20.getAsString());
                        }
                     }

                     String var28 = var13.has(
                           "authorDiscordId"
                        )
                        ? var13.get("authorDiscordId")
                           .getAsString()
                        : "";
                     String var29 = var13.has(
                           "authorDiscordName"
                        )
                        ? var13.get(
                              "authorDiscordName"
                           )
                           .getAsString()
                        : "Vie User";
                     String var21 = var13.has("authorAvatar")
                           && !var13.get("authorAvatar").isJsonNull()
                        ? var13.get("authorAvatar").getAsString()
                        : null;
                     int var22 = var13.has("likesCount")
                        ? var13.get("likesCount").getAsInt()
                        : 0;
                     int var23 = var13.has("downloadsCount")
                        ? var13.get("downloadsCount").getAsInt()
                        : 0;
                     String var24 = var13.has("createdAt")
                        ? var13.get("createdAt").getAsString()
                        : "";
                     boolean var25 = var13.has("hasLiked")
                        && var13.get("hasLiked").getAsBoolean();
                     boolean var26 = var13.has("isOwner")
                        && var13.get("isOwner").getAsBoolean();
                     var5.add(new ConfigSyncManager.CommunityConfig(var14, var15, var16, var17, var18, var28, var29, var21, var22, var23, var24, var25, var26));
                  }
               }
            }
         }
      } catch (Throwable var27) {
         VieClientMod.LOGGER
            .warn(
               "[ConfigSync] Failed to fetch community configs: {}",
               var27.toString()
            );
      }

      return var5;
   }

   public void fetchCommunityConfigsAsync(String sort, String search, int page, int limit, Consumer callback) {
      CompletableFuture.runAsync(() -> {
         List var6 = this.fetchCommunityConfigs(sort, search, page, limit);
         if (callback != null) {
            class_310.method_1551().execute(() -> callback.accept(var6));
         }
      }, this.executor);
   }

   public void toggleLikeCommunityConfigAsync(String code, BiConsumer callback) {
      CompletableFuture.runAsync(
         () -> {
            boolean var3 = false;
            byte var4 = 0;

            try {
               JsonObject var5 = new JsonObject();
               var5.addProperty("code", code.trim().toUpperCase());
               String var6 = this.resolveOwnerKey();
               if (!var6.isEmpty()) {
                  var5.addProperty("ownerKey", var6);
                  String var7 = this.resolveSessionToken();
                  if (var7 != null && !var7.isBlank()) {
                     var5.addProperty("sessionToken", var7);
                  }
               }

               String var13 = this.GSON.toJson(var5);
               HttpResponse var8 = this.executeHttp(
                  "https://vieservices.top/api/v1/community/config/like",
                  var13
               );
               if (var8 == null || var8.statusCode() != 200) {
                  var8 = this.executeHttp(
                     "https://34.126.189.48/api/v1/community/config/like",
                     var13
                  );
               }

               if (var8 != null && var8.statusCode() == 200) {
                  JsonObject var9 = (JsonObject)this.GSON.fromJson((String)var8.body(), JsonObject.class);
                  if (var9 != null) {
                     var3 = var9.has("liked")
                        && var9.get("liked").getAsBoolean();
                     var4 = (byte)(var9.has("likesCount")
                        ? var9.get("likesCount").getAsInt()
                        : 0);
                  }
               }
            } catch (Throwable var10) {
               VieClientMod.LOGGER
                  .warn(
                     "[ConfigSync] Like async error: {}",
                     var10.toString()
                  );
            }

            boolean var11 = var3;
            byte var12 = var4;
            if (callback != null) {
               class_310.method_1551().execute(() -> callback.accept(var11, var12));
            }
         },
         this.executor
      );
   }

   public String publishToCommunity(String configName, String title, String description, List tags) {
      try {
         JsonObject var5 = this.buildConfigPayload(configName);
         var5.remove("code");
         var5.addProperty("title", title != null && !title.isBlank() ? title : configName);
         var5.addProperty(
            "description", description != null ? description : ""
         );
         JsonArray var6 = new JsonArray();
         if (tags != null) {
            for (String var8 : tags) {
               var6.add(var8);
            }
         }

         var5.add("tags", var6);
         String var11 = this.GSON.toJson(var5);
         HttpResponse var12 = this.executeHttp(
            "https://vieservices.top/api/v1/community/config/publish",
            var11
         );
         if (var12 == null) {
            var12 = this.executeHttp(
               "https://34.126.189.48/api/v1/community/config/publish",
               var11
            );
         }

         if (var12 == null) {
            this.lastSaveError = "Network error - server unreachable";
            return null;
         }

         if (var12.statusCode() != 200 && var12.statusCode() != 201) {
            String var13 = this.extractServerErrorMessage((String)var12.body());
            this.lastSaveError = var13 != null ? var13 : "Server error " + var12.statusCode();
            return null;
         }

         JsonObject var9 = (JsonObject)this.GSON.fromJson((String)var12.body(), JsonObject.class);
         if (var9 != null && var9.has("code")) {
            return var9.get("code").getAsString();
         }
      } catch (Throwable var10) {
         this.lastSaveError = "Exception: " + var10.getMessage();
      }

      return null;
   }

   public void publishToCommunityAsync(String configName, String title, String description, List tags, Consumer callback) {
      CompletableFuture.runAsync(() -> {
         String var6 = this.publishToCommunity(configName, title, description, tags);
         if (callback != null) {
            class_310.method_1551().execute(() -> callback.accept(var6));
         }
      }, this.executor);
   }

   public boolean deleteCommunityConfig(String code) {
      if (code != null && !code.isBlank()) {
         try {
            JsonObject var2 = new JsonObject();
            var2.addProperty("code", code.trim().toUpperCase());
            String var3 = this.resolveOwnerKey();
            if (!var3.isEmpty()) {
               var2.addProperty("ownerKey", var3);
               String var4 = this.resolveSessionToken();
               if (var4 != null && !var4.isBlank()) {
                  var2.addProperty("sessionToken", var4);
               }
            }

            String var7 = this.GSON.toJson(var2);
            HttpResponse var5 = this.executeHttp(
               "https://vieservices.top/api/v1/community/config/delete",
               var7
            );
            if (var5 == null) {
               var5 = this.executeHttp(
                  "https://34.126.189.48/api/v1/community/config/delete",
                  var7
               );
            }

            return var5 != null && (var5.statusCode() == 200 || var5.statusCode() == 204);
         } catch (Throwable var6) {
            return false;
         }
      } else {
         return false;
      }
   }

   public void deleteCommunityConfigAsync(String code, Consumer callback) {
      CompletableFuture.runAsync(() -> {
         boolean var3 = this.deleteCommunityConfig(code);
         if (callback != null) {
            class_310.method_1551().execute(() -> callback.accept(var3));
         }
      }, this.executor);
   }

   public String downloadCommunityConfig(String code) {
      if (code != null && !code.isBlank()) {
         try {
            String var2 = code.trim().toUpperCase();
            HttpResponse var3 = this.executeHttpGet("https://vieservices.top/api/v1/community/config/download/" + var2);
            if (var3 == null || var3.statusCode() != 200) {
               var3 = this.executeHttpGet("https://34.126.189.48/api/v1/community/config/download/" + var2);
            }

            if (var3 != null && var3.statusCode() == 200) {
               JsonObject var4 = (JsonObject)this.GSON.fromJson((String)var3.body(), JsonObject.class);
               if (var4 != null && var4.has("config")) {
                  JsonObject var5 = var4.getAsJsonObject("config");
                  String var6 = var4.has("name")
                     ? var4.get("name").getAsString()
                     : var2;
                  this.deserializeIntoModules(var5.has("modules") ? var5 : var4);
                  JsonElement var7 = var5.has("hud")
                        && var5.get("hud").isJsonObject()
                     ? var5.get("hud")
                     : (
                        var4.has("hud")
                              && var4.get("hud").isJsonObject()
                           ? var4.get("hud")
                           : null
                     );
                  if (var7 != null && var7.isJsonObject()) {
                     HUD var8 = HUD.instance();
                     if (var8 != null) {
                        try {
                           HudLayoutCodec.Layout var9 = HudLayoutCodec.decode(var7.getAsJsonObject());
                           var8.setWidgetPositions(var9.positions());
                           var8.setWidgetScales(var9.scales());
                        } catch (Throwable var14) {
                        }
                     }
                  }

                  if (VieClient.INSTANCE != null) {
                     VieClient.INSTANCE.getConfigManager().saveProfile(var6);
                  }

                  String var16 = var4.has("authorDiscordId")
                     ? var4.get("authorDiscordId")
                        .getAsString()
                     : "";
                  String var17 = var4.has(
                        "authorDiscordName"
                     )
                     ? var4.get("authorDiscordName")
                        .getAsString()
                     : "Vie User";
                  AuthManager var10 = this.resolveAuthManager();
                  String var11 = var10 != null ? var10.getDiscordUserId() : "";
                  String var12 = this.resolveOwnerKey();
                  boolean var13 = !var11.isBlank() && var11.equalsIgnoreCase(var16) || !var12.isBlank() && var12.equalsIgnoreCase(var16);
                  this.setCloudRecord(var6, new ConfigSyncManager.CloudRecord(var2, System.currentTimeMillis(), var16, var17, var13));
                  return var6;
               }
            }
         } catch (Throwable var15) {
            VieClientMod.LOGGER
               .warn(
                  "[ConfigSync] Download community config {} failed: {}",
                  code,
                  var15.toString()
               );
         }

         return null;
      } else {
         return null;
      }
   }

   public void downloadCommunityConfigAsync(String code, Consumer callback) {
      CompletableFuture.runAsync(() -> {
         String var3 = this.downloadCommunityConfig(code);
         if (callback != null) {
            class_310.method_1551().execute(() -> callback.accept(var3));
         }
      }, this.executor);
   }

   private String extractServerErrorMessage(String body) {
      if (body != null && !body.isEmpty()) {
         try {
            JsonObject var2 = (JsonObject)this.GSON.fromJson(body, JsonObject.class);
            if (var2 == null) {
               return null;
            }

            if (var2.has("message")
               && !var2.get("message").isJsonNull()) {
               String var3 = var2.get("message").getAsString();
               if (var3 != null && !var3.isBlank()) {
                  return var3;
               }
            }
         } catch (Exception var4) {
         }

         String var5 = body.length() > 200 ? body.substring(0, 200) : body;
         return !var5.contains("Missing")
               && !var5.contains("Malformed")
               && !var5.contains("Invalid")
               && !var5.contains("required")
            ? null
            : var5.replaceAll("[\\r\\n]+", " ").trim();
      } else {
         return null;
      }
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

   public static final class CloudRecord {
      private final String code;
      private final long uploadedAt;
      private final String authorDiscordId;
      private final String authorDiscordName;
      private final boolean isOwner;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public CloudRecord(String code, long uploadedAt) {
         this(codex, uploadedAtx, "", "", true);
      }

      public CloudRecord(String code, long uploadedAt, String authorDiscordId, String authorDiscordName, boolean isOwner) {
         this.code = codex != null ? codex.trim().toUpperCase() : "";
         this.uploadedAt = uploadedAtx;
         this.authorDiscordId = authorDiscordIdx != null ? authorDiscordIdx : "";
         this.authorDiscordName = authorDiscordNamex != null ? authorDiscordNamex : "";
         this.isOwner = isOwnerx;
      }

      public String getCode() {
         return this.code;
      }

      public long getUploadedAt() {
         return this.uploadedAt;
      }

      public String getAuthorDiscordId() {
         return this.authorDiscordId;
      }

      public String getAuthorDiscordName() {
         return this.authorDiscordName;
      }

      public boolean isOwner() {
         return this.isOwner;
      }
   }

   public static final class CommunityConfig {
      private final String code;
      private final String name;
      private final String title;
      private final String description;
      private final List tags;
      private final String authorDiscordId;
      private final String authorDiscordName;
      private final String authorAvatar;
      private int likesCount;
      private int downloadsCount;
      private final String createdAt;
      private boolean hasLiked;
      private final boolean isOwner;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public CommunityConfig(
         String code,
         String name,
         String title,
         String description,
         List tags,
         String authorDiscordId,
         String authorDiscordName,
         String authorAvatar,
         int likesCount,
         int downloadsCount,
         String createdAt,
         boolean hasLiked,
         boolean isOwner
      ) {
         this.code = codex != null ? codex : "";
         this.name = namex != null ? namex : "";
         this.title = titlex != null && !titlex.isBlank() ? titlex : this.name;
         this.description = descriptionx != null ? descriptionx : "";
         this.tags = tagsx != null ? tagsx : Collections.emptyList();
         this.authorDiscordId = authorDiscordIdx != null ? authorDiscordIdx : "";
         String var10001;
         if (authorDiscordNamex != null) {
            var10001 = authorDiscordNamex;
         } else {
            byte[] var14 = new byte[8];
            var14[0] = (byte)(108);
            var14[1] = (byte)(-13);
            var14[2] = (byte)(121);
            var14[3] = (byte)(-2);
            var14[4] = (byte)(-33);
            var14[5] = (byte)(81);
            var14[6] = (byte)(-107);
            var14[7] = (byte)(110);
            var10001 = $$v_s(var14, 3815670606535577631L);
         }

         this.authorDiscordName = var10001;
         this.authorAvatar = authorAvatarx;
         this.likesCount = likesCountx;
         this.downloadsCount = downloadsCountx;
         this.createdAt = createdAtx != null ? createdAtx : "";
         this.hasLiked = hasLikedx;
         this.isOwner = isOwnerx;
      }

      public String getCode() {
         return this.code;
      }

      public String getName() {
         return this.name;
      }

      public String getTitle() {
         return this.title;
      }

      public String getDescription() {
         return this.description;
      }

      public List getTags() {
         return this.tags;
      }

      public String getAuthorDiscordId() {
         return this.authorDiscordId;
      }

      public String getAuthorDiscordName() {
         return this.authorDiscordName;
      }

      public String getAuthorAvatar() {
         return this.authorAvatar;
      }

      public int getLikesCount() {
         return this.likesCount;
      }

      public int getDownloadsCount() {
         return this.downloadsCount;
      }

      public String getCreatedAt() {
         return this.createdAt;
      }

      public boolean hasLiked() {
         return this.hasLiked;
      }

      public boolean isOwner() {
         return this.isOwner;
      }

      public void setLiked(boolean liked) {
         if (this.hasLiked != liked) {
            this.hasLiked = liked;
            this.likesCount = Math.max(0, this.likesCount + (liked ? 1 : -1));
         }
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

   @FunctionalInterface
   public interface ImportCallback {
      void onComplete(String var1);
   }

   @FunctionalInterface
   public interface ProbeCallback {
      void onComplete(boolean var1);
   }

   @FunctionalInterface
   public interface SaveCallback {
      void onComplete(String var1);
   }
}
