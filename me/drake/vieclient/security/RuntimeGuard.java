package me.drake.vieclient.security;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import me.drake.vieclient.CoreEntry;

public final class RuntimeGuard {
   private static final List DEBUG_FLAGS = List.of(
      "-agentlib:jdwp",
      "-xdebug",
      "-xrunjdwp"
   );
   private static final List FORBIDDEN_WINDOWS_PROCESSES = List.of(
      "x64dbg.exe",
      "x32dbg.exe",
      "ida64.exe",
      "ida.exe",
      "idaq64.exe",
      "ghidra.exe",
      "ollydbg.exe",
      "dnspy.exe",
      "processhacker.exe",
      "cheatengine.exe",
      "procmon.exe",
      "procexp.exe",
      "wireshark.exe"
   );
   private static final long BACKGROUND_SCAN_INTERVAL_MS = 45000L;
   private final String expectedJarSha256;
   private final ScheduledExecutorService backgroundExecutor;
   private final AtomicReference backgroundCompromise = new AtomicReference(null);
   private volatile String compromiseReason;
   private volatile boolean backgroundStarted = false;
   private volatile boolean jarIntegrityChecked = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public RuntimeGuard() {
      byte[] var10001 = new byte[17];
      var10001[0] = (byte)(68);
      var10001[1] = (byte)(-45);
      var10001[2] = (byte)(89);
      var10001[3] = (byte)(-127);
      var10001[4] = (byte)(23);
      var10001[5] = (byte)(-60);
      var10001[6] = (byte)(-41);
      var10001[7] = (byte)(-75);
      var10001[8] = (byte)(52);
      var10001[9] = (byte)(-10);
      var10001[10] = (byte)(-69);
      var10001[11] = (byte)(-91);
      var10001[12] = (byte)(68);
      var10001[13] = (byte)(17);
      var10001[14] = (byte)(-124);
      var10001[15] = (byte)(-67);
      var10001[16] = (byte)(116);
      this.expectedJarSha256 = readStringProperty($$v_s(var10001, -2838016613017202633L)).trim().toLowerCase(Locale.ROOT);
      this.backgroundExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
         byte[] var10003 = new byte[17];
         var10003[0] = (byte)(100);
         var10003[1] = (byte)(-13);
         var10003[2] = (byte)(121);
         var10003[3] = (byte)(-13);
         var10003[4] = (byte)(38);
         var10003[5] = (byte)(-3);
         var10003[6] = (byte)(-16);
         var10003[7] = (byte)(-124);
         var10003[8] = (byte)(19);
         var10003[9] = (byte)(-49);
         var10003[10] = (byte)(-127);
         var10003[11] = (byte)(-37);
         var10003[12] = (byte)(107);
         var10003[13] = (byte)(37);
         var10003[14] = (byte)(-41);
         var10003[15] = (byte)(-6);
         var10003[16] = (byte)(38);
         Thread var1 = new Thread(r, $$v_s(var10003, -2838016613017202633L));
         var1.setDaemon(true);
         return var1;
      });
   }

   public synchronized String checkStartup() {
      if (this.compromiseReason != null) {
         return this.compromiseReason;
      }

      String var1 = detectDebugFlag();
      if (var1 != null) {
         return this.remember(var1);
      }

      if (!this.backgroundStarted) {
         this.backgroundStarted = true;
         this.backgroundExecutor.scheduleAtFixedRate(this::runBackgroundChecks, 22500L, 45000L, TimeUnit.MILLISECONDS);
      }

      return null;
   }

   public synchronized String checkPeriodic() {
      if (this.compromiseReason != null) {
         return this.compromiseReason;
      }

      String var1 = (String)this.backgroundCompromise.getAndSet(null);
      return var1 != null ? this.remember(var1) : detectDebugFlag();
   }

   private void runBackgroundChecks() {
      if (this.compromiseReason == null) {
         String var1 = this.verifyJarIntegrity();
         if (var1 != null) {
            this.backgroundCompromise.compareAndSet(null, var1);
         } else {
            var1 = scanForbiddenProcesses();
            if (var1 != null) {
               this.backgroundCompromise.compareAndSet(null, var1);
            }
         }
      }
   }

   private String remember(String reason) {
      this.compromiseReason = reason;
      return reason;
   }

   private static String detectDebugFlag() {
      try {
         for (String var1 : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            String var2 = var1.toLowerCase(Locale.ROOT);

            for (String var4 : DEBUG_FLAGS) {
               if (var2.contains(var4)) {
                  return "Debug argument detected: " + var1;
               }
            }
         }
      } catch (Exception var5) {
      }

      String var6 = System.getProperty("java.vm.info", "");
      if (var6 != null && var6.toLowerCase(Locale.ROOT).contains("debug")) {
         return "Debug VM info detected.";
      }

      String var7 = System.getProperty(
         "jdk.attach.allowAttachSelf",
         "false"
      );
      return "true".equalsIgnoreCase(var7)
         ? "Self-attach debugging is enabled (jdk.attach.allowAttachSelf=true)."
         : null;
   }

   private String verifyJarIntegrity() {
      if (this.expectedJarSha256.isBlank()) {
         return null;
      }

      if (this.jarIntegrityChecked) {
         return null;
      }

      Path var1;
      try {
         CodeSource var2 = CoreEntry.class.getProtectionDomain().getCodeSource();
         if (var2 == null || var2.getLocation() == null) {
            return "Cannot resolve code source for integrity check.";
         }

         URI var3 = var2.getLocation().toURI();
         var1 = Path.of(var3);
      } catch (Exception var5) {
         return "Cannot resolve runtime jar path: " + var5.getMessage();
      }

      if (!Files.isRegularFile(var1)) {
         return "Expected jar file for integrity check but got: " + var1;
      }

      String var6;
      try {
         var6 = sha256File(var1);
      } catch (IOException var4) {
         return "Cannot hash runtime jar: " + var4.getMessage();
      }

      if (!this.expectedJarSha256.equalsIgnoreCase(var6)) {
         return "Jar integrity mismatch.";
      }

      this.jarIntegrityChecked = true;
      return null;
   }

   private static String scanForbiddenProcesses() {
      if (!isWindows()) {
         return null;
      }

      Process var0;
      try {
         String[] var10002 = new String[4];
         byte[] var10005 = new byte[8];
         var10005[0] = (byte)(102);
         var10005[1] = (byte)(-5);
         var10005[2] = (byte)(111);
         var10005[3] = (byte)(-75);
         var10005[4] = (byte)(56);
         var10005[5] = (byte)(-31);
         var10005[6] = (byte)(-19);
         var10005[7] = (byte)(-124);
         var10002[0] = $$v_s(var10005, -2838016613017202633L);
         var10005 = new byte[3];
         var10005[0] = (byte)(61);
         var10005[1] = (byte)(-36);
         var10005[2] = (byte)(83);
         var10002[1] = $$v_s(var10005, -2838016613017202633L);
         int var10004 = 2;
         var10005 = new byte[3];
         var10005[0] = (byte)(81);
         var10005[1] = (byte)(-55);
         var10005[2] = (byte)(74);
         var10002[var10004] = $$v_s(var10005, -2838016613017202633L);
         var10004 = 3;
         var10005 = new byte[3];
         var10005[0] = (byte)(61);
         var10005[1] = (byte)(-44);
         var10005[2] = (byte)(84);
         var10002[var10004] = $$v_s(var10005, -2838016613017202633L);
         var0 = new ProcessBuilder(var10002).redirectErrorStream(true).start();
      } catch (IOException var6) {
         return null;
      }

      String var1;
      try (InputStream var2 = var0.getInputStream()) {
         var1 = new String(var2.readAllBytes(), StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
      } catch (IOException var8) {
         return null;
      }

      for (String var3 : FORBIDDEN_WINDOWS_PROCESSES) {
         if (var1.contains(var3.toLowerCase(Locale.ROOT))) {
            return "Analysis process detected: " + var3;
         }
      }

      return null;
   }

   private static String sha256File(Path path) throws IOException {
      try {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(65);
         var10000[1] = (byte)(-46);
         var10000[2] = (byte)(93);
         var10000[3] = (byte)(-13);
         var10000[4] = (byte)(102);
         var10000[5] = (byte)(-67);
         var10000[6] = (byte)(-88);
         MessageDigest var1 = MessageDigest.getInstance($$v_s(var10000, -2838016613017202633L));

         try (InputStream var2 = Files.newInputStream(path)) {
            byte[] var3 = new byte[8192];

            int var4;
            while ((var4 = var2.read(var3)) >= 0) {
               if (var4 != 0) {
                  var1.update(var3, 0, var4);
               }
            }
         }

         return HexFormat.of().formatHex(var1.digest());
      } catch (NoSuchAlgorithmException var7) {
         byte[] var10002 = new byte[23];
         var10002[0] = (byte)(65);
         var10002[1] = (byte)(-46);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-13);
         var10002[4] = (byte)(102);
         var10002[5] = (byte)(-67);
         var10002[6] = (byte)(-88);
         var10002[7] = (byte)(-48);
         var10002[8] = (byte)(19);
         var10002[9] = (byte)(-47);
         var10002[10] = (byte)(-60);
         var10002[11] = (byte)(-125);
         var10002[12] = (byte)(98);
         var10002[13] = (byte)(49);
         var10002[14] = (byte)(-64);
         var10002[15] = (byte)(-23);
         var10002[16] = (byte)(43);
         var10002[17] = (byte)(38);
         var10002[18] = (byte)(-83);
         var10002[19] = (byte)(-52);
         var10002[20] = (byte)(-120);
         var10002[21] = (byte)(29);
         var10002[22] = (byte)(-32);
         throw new IOException($$v_s(var10002, -2838016613017202633L), var7);
      }
   }

   private static boolean isWindows() {
      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(125);
      var10000[1] = (byte)(-23);
      var10000[2] = (byte)(50);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(53);
      var10000[5] = (byte)(-27);
      var10000[6] = (byte)(-5);
      String var0 = System.getProperty($$v_s(var10000, -2838016613017202633L), "").toLowerCase(Locale.ROOT);
      byte[] var10001 = new byte[7];
      var10001[0] = (byte)(101);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(114);
      var10001[3] = (byte)(-70);
      var10001[4] = (byte)(59);
      var10001[5] = (byte)(-1);
      var10001[6] = (byte)(-19);
      return var0.contains($$v_s(var10001, -2838016613017202633L));
   }

   private static boolean readBooleanProperty(String env, boolean fallback) {
      String var2 = readStringProperty(env);
      if (var2.isBlank()) {
         return fallback;
      }

      String var3 = var2.trim().toLowerCase(Locale.ROOT);
      int var4 = -1;
      switch (var3.hashCode()) {
         case 48:
            if (var3.equals("0")) {
               var4 = 4;
            }
            break;
         case 49:
            if (var3.equals("1")) {
               var4 = 0;
            }
            break;
         case 3521:
            byte[] var9 = new byte[2];
            var9[0] = (byte)(124);
            var9[1] = (byte)(-11);
            if (var3.equals($$v_s(var9, -2838016613017202633L))) {
               var4 = 6;
            }
            break;
         case 3551:
            byte[] var8 = new byte[2];
            var8[0] = (byte)(125);
            var8[1] = (byte)(-12);
            if (var3.equals($$v_s(var8, -2838016613017202633L))) {
               var4 = 3;
            }
            break;
         case 109935:
            byte[] var7 = new byte[3];
            var7[0] = (byte)(125);
            var7[1] = (byte)(-4);
            var7[2] = (byte)(122);
            if (var3.equals($$v_s(var7, -2838016613017202633L))) {
               var4 = 7;
            }
            break;
         case 119527:
            byte[] var6 = new byte[3];
            var6[0] = (byte)(107);
            var6[1] = (byte)(-1);
            var6[2] = (byte)(111);
            if (var3.equals($$v_s(var6, -2838016613017202633L))) {
               var4 = 2;
            }
            break;
         case 3569038:
            byte[] var5 = new byte[4];
            var5[0] = (byte)(102);
            var5[1] = (byte)(-24);
            var5[2] = (byte)(105);
            var5[3] = (byte)(-69);
            if (var3.equals($$v_s(var5, -2838016613017202633L))) {
               var4 = 1;
            }
            break;
         case 97196323:
            byte[] var10001 = new byte[5];
            var10001[0] = (byte)(116);
            var10001[1] = (byte)(-5);
            var10001[2] = (byte)(112);
            var10001[3] = (byte)(-83);
            var10001[4] = (byte)(49);
            if (var3.equals($$v_s(var10001, -2838016613017202633L))) {
               var4 = 5;
            }
      }
      return switch (var4) {
         case 0, 1, 2, 3 -> true;
         case 4, 5, 6, 7 -> false;
         default -> fallback;
      };
   }

   private static String readStringProperty(String env) {
      String var1 = System.getenv(env);
      return var1 != null && !var1.isBlank() ? var1 : "";
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
