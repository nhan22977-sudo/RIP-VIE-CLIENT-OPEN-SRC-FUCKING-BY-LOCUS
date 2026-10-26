package me.drake.vieclient.security;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.ProtectionDomain;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import me.drake.vieclient.CoreEntry;
import me.drake.vieclient.auth.AuthManager;

public class SecurityGuard {
   private static boolean enabledCached = false;
   private static boolean enabledInit = false;
   private static boolean strictCached = false;
   private static boolean strictInit = false;
   private static final long[] HASH_MASK_KEYS = new long[]{
      6502245784367714543L,
      8332042553170808160L,
      2183535713822540790L,
      -8129529918963158988L,
      4434387979563929670L,
      7939735205601689715L,
      -7269287561403596746L,
      3052750184978006074L
   };
   private static final long[] HASH_MASKED_BLOCKS = new long[]{
      7785786362463511769L,
      4869300204099697153L,
      3400022334192543680L,
      -4963391211865172478L,
      6694651673975564661L,
      943131569573884945L,
      -397673443790834176L,
      5278199140892571918L
   };
   private static final List FORBIDDEN_DEBUG_PROCESSES = List.of(
      "x64dbg.exe",
      "x32dbg.exe",
      "ida64.exe",
      "ida.exe",
      "idaq64.exe",
      "cheatengine.exe",
      "cheatengine-x86_64.exe",
      "processhacker.exe",
      "wireshark.exe",
      "httpdebuggerpro.exe",
      "charles.exe",
      "dnspy.exe",
      "ilspy.exe",
      "recaf.exe",
      "recaf",
      "jclasslib.exe",
      "jclasslib",
      "bytecode-viewer.exe",
      "bytecode-viewer"
   );
   private static final Set TRUSTED_CLASSES = new HashSet();
   private static String jarChecksumSHA256 = "";
   private static String jarChecksumMD5 = "";
   private static String jarChecksumCRC32 = "";
   private static boolean initialized = false;
   private static volatile boolean compromised = false;
   private static long initialJarSize = 0L;
   private static String authStateHash = "";
   private static Object authStateLock = new Object();
   private static long cachedIsDebugAddr = 0L;
   private static Method cachedInvokeI = null;
   private static final ScheduledExecutorService BG_SECURITY_EXECUTOR = Executors.newSingleThreadScheduledExecutor(r -> {
      byte[] var10003 = new byte[19];
      var10003[0] = (byte)(-106);
      var10003[1] = (byte)(-13);
      var10003[2] = (byte)(121);
      var10003[3] = (byte)(-13);
      var10003[4] = (byte)(87);
      var10003[5] = (byte)(61);
      var10003[6] = (byte)(-10);
      var10003[7] = (byte)(-15);
      var10003[8] = (byte)(-6);
      var10003[9] = (byte)(-53);
      var10003[10] = (byte)(-112);
      var10003[11] = (byte)(-113);
      var10003[12] = (byte)(81);
      var10003[13] = (byte)(-9);
      var10003[14] = (byte)(-46);
      var10003[15] = (byte)(-114);
      var10003[16] = (byte)(-37);
      var10003[17] = (byte)(47);
      var10003[18] = (byte)(-66);
      Thread var1 = new Thread(r, $$v_s(var10003, -6010468642562256699L));
      var1.setDaemon(true);
      var1.setPriority(1);
      return var1;
   });
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private static boolean isEnabled() {
      return false;
   }

   private static boolean isStrict() {
      return false;
   }

   private static String getExpectedJarSha256() {
      byte[] var0 = new byte[64];

      for (int var1 = 0; var1 < (8); var1++) {
         long var2 = HASH_MASKED_BLOCKS[var1] ^ HASH_MASK_KEYS[var1];

         for (int var4 = 7; var4 >= 0; var4--) {
            var0[var1 * (8) + var4] = (byte)(var2 & 255L);
            var2 >>>= 8;
         }
      }

      return new String(var0, StandardCharsets.US_ASCII);
   }

   public static synchronized void initialize() {
      if (!initialized && isEnabled()) {
         CoreEntry.LOGGER
            .info(
               "[SecurityGuard] Initializing security checks..."
            );

         try {
            try {
               Class var0 = Class.forName(
                  "org.lwjgl.system.Library"
               );
               Method var1 = var0.getMethod(
                  "loadNative", String.class, String.class
               );
               Object var2 = var1.invoke(
                  null,
                  "org.lwjgl",
                  "kernel32"
               );
               if (var2 != null) {
                  for (Method var6 : var2.getClass().getMethods()) {
                     if (var6.getName()
                           .equals(
                              "getFunctionAddress"
                           )
                        && var6.getParameterCount() == 1
                        && var6.getParameterTypes()[0] == CharSequence.class) {
                        cachedIsDebugAddr = (Long)var6.invoke(
                           var2, "IsDebuggerPresent"
                        );
                        break;
                     }
                  }

                  Class var11 = Class.forName(
                     "org.lwjgl.system.JNI"
                  );
                  cachedInvokeI = var11.getMethod("invokeI", long.class);
               }
            } catch (Throwable var7) {
            }

            checkDebugFlags();
            checkDebuggerAttached();
            checkEnvironment();
            checkAntiHooking();
            performNativeCrossCheck();
            verifyCrossCheckGates();
            Thread var10 = new Thread(
               () -> {
                  try {
                     calculateMultipleChecksums();
                     verifyJarSize();
                  } catch (SecurityGuard.SecurityViolationException var1x) {
                     compromised = true;
                     CoreEntry.LOGGER
                        .error(
                           "[SecurityGuard] Security violation: {}",
                           var1x.getMessage()
                        );
                     crash("Security violation detected: " + var1x.getMessage());
                  } catch (Exception var2x) {
                     compromised = true;
                     CoreEntry.LOGGER
                        .error(
                           "[SecurityGuard] Security check failed: {}",
                           var2x.getMessage(),
                           var2x
                        );
                     crash(
                        "Security check failed"
                     );
                  }
               },
               "vie-security-checksum"
            );
            var10.setDaemon(true);
            var10.start();
            BG_SECURITY_EXECUTOR.scheduleWithFixedDelay(
               () -> {
                  try {
                     if (compromised) {
                        return;
                     }

                     performRedundantLoaderCrossCheck();
                  } catch (Throwable var1x) {
                     compromised = true;
                     CoreEntry.LOGGER
                        .error(
                           "[SecurityGuard] Background process cross-check violation: {}",
                           var1x.getMessage()
                        );
                     crash("Security violation: " + var1x.getMessage());
                  }
               },
               20L,
               45L,
               TimeUnit.SECONDS
            );
            initialized = true;
            CoreEntry.LOGGER
               .info(
                  "[SecurityGuard] Security checks started (async background worker)"
               );
         } catch (SecurityGuard.SecurityViolationException var8) {
            compromised = true;
            CoreEntry.LOGGER
               .error(
                  "[SecurityGuard] Security violation: {}",
                  var8.getMessage()
               );
            crash("Security violation detected: " + var8.getMessage());
         } catch (Exception var9) {
            compromised = true;
            CoreEntry.LOGGER
               .error(
                  "[SecurityGuard] Security check failed: {}",
                  var9.getMessage(),
                  var9
               );
            crash(
               "Security check failed"
            );
         }
      }
   }

   public static void verifyIntegrity() {
      if (isEnabled()) {
         if (compromised) {
            crash(
               "Security violation detected"
            );
         } else {
            try {
               checkDebuggerAttachedFast();
               verifyAuthStateIntegrity();
            } catch (SecurityGuard.SecurityViolationException var1) {
               compromised = true;
               CoreEntry.LOGGER
                  .error(
                     "[SecurityGuard] Runtime violation: {}",
                     var1.getMessage()
                  );
               crash("Security violation: " + var1.getMessage());
            }
         }
      }
   }

   private static void checkDebuggerAttachedFast() throws SecurityGuard.SecurityViolationException {
      if (cachedIsDebugAddr != 0L && cachedInvokeI != null) {
         try {
            int var0 = (Integer)cachedInvokeI.invoke(null, cachedIsDebugAddr);
            if (var0 != 0) {
               throw new SecurityGuard.SecurityViolationException("Native debugger detected (IsDebuggerPresent = " + var0 + ")");
            }
         } catch (SecurityGuard.SecurityViolationException var1) {
            throw var1;
         } catch (Throwable var2) {
         }
      }
   }

   private static void verifyAuthStateIntegrity() throws SecurityGuard.SecurityViolationException {
      try {
         AuthManager var0 = AuthManager.getInstance();
         if (var0 != null && !var0.verifyMemoryIntegrity()) {
            throw new SecurityGuard.SecurityViolationException(
               "Auth state memory integrity HMAC mismatch"
            );
         }
      } catch (SecurityGuard.SecurityViolationException var1) {
         throw var1;
      } catch (Throwable var2) {
         CoreEntry.LOGGER
            .warn(
               "[SecurityGuard] Auth state integrity check error: {}",
               var2.getMessage()
            );
      }
   }

   private static void verifyCallerStack() throws SecurityGuard.SecurityViolationException {
      if (isEnabled()) {
         StackTraceElement[] var0 = Thread.currentThread().getStackTrace();
         boolean var1 = false;

         for (StackTraceElement var5 : var0) {
            String var6 = var5.getClassName();
            if (var6.startsWith(
                  "me.drake.vieclient."
               )
               || var6.startsWith("net.minecraft.")
               || var6.startsWith("org.junit.")) {
               var1 = true;
               break;
            }
         }

         if (!var1) {
            throw new SecurityGuard.SecurityViolationException(
               "Illegal external caller invoking SecurityGuard: "
                  + (var0.length > 2 ? var0[2].getClassName() : "unknown")
            );
         }
      }
   }

   private static void verifyCrossCheckGates() throws SecurityGuard.SecurityViolationException {
      if (isEnabled()) {
         try {
            Class var0 = Class.forName(
               "me.drake.vieclient.loader.CrossCheckManager"
            );
            Method var1 = var0.getMethod("verifyGate", int.class, long.class);
            boolean var2 = (Boolean)var1.invoke(null, 3, 1800000L);
            if (!var2) {
               throw new SecurityGuard.SecurityViolationException(
                  "Loader gate verification failed (missing prior gates)"
               );
            }

            Method var3 = var0.getMethod(
               "markGatePassed", int.class
            );
            var3.invoke(null, 4);
         } catch (ClassNotFoundException var4) {
            if (isStrict()) {
               throw new SecurityGuard.SecurityViolationException(
                  "Missing loader cross-check manager in strict mode"
               );
            }
         } catch (SecurityGuard.SecurityViolationException var5) {
            throw var5;
         } catch (Throwable var6) {
            if (isStrict()) {
               throw new SecurityGuard.SecurityViolationException("Cross-check gate failure: " + var6.getMessage());
            }
         }
      }
   }

   public static void performNativeCrossCheck() throws SecurityGuard.SecurityViolationException {
      if (isEnabled()) {
         try {
            Class var0 = Class.forName(
               "me.drake.vieclient.loader.NativeGuard"
            );
            Method var4 = var0.getMethod(
               "verifyNativeIntegrity"
            );
            var4.invoke(null);
         } catch (ClassNotFoundException var2) {
            performInlineNativeCrossCheck();
         } catch (Throwable var3) {
            Throwable var1 = var3.getCause() != null ? var3.getCause() : var3;
            if (var1 instanceof SecurityGuard.SecurityViolationException) {
               throw (SecurityGuard.SecurityViolationException)var1;
            }

            performInlineNativeCrossCheck();
         }
      }
   }

   private static void performInlineNativeCrossCheck() throws SecurityGuard.SecurityViolationException {
      String var0 = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
      if (var0.contains("win")) {
         try {
            Class var1 = Class.forName(
               "org.lwjgl.system.windows.Kernel32"
            );
            Class var2 = Class.forName(
               "org.lwjgl.system.JNI"
            );
            Class var3 = Class.forName(
               "org.lwjgl.system.Library"
            );
            Method var4 = var3.getMethod("loadNative", String.class, String.class);
            Object var5 = var4.invoke(
               null,
               "org.lwjgl",
               "kernel32"
            );
            Method var6 = null;

            for (Method var10 : var5.getClass().getMethods()) {
               if (var10.getName()
                     .equals("getFunctionAddress")
                  && var10.getParameterCount() == 1
                  && var10.getParameterTypes()[0] == CharSequence.class) {
                  var6 = var10;
                  break;
               }
            }

            if (var6 != null) {
               long var23 = (Long)var6.invoke(
                  var5, "IsDebuggerPresent"
               );
               if (var23 != 0L) {
                  Method var24 = var2.getMethod("invokeI", long.class);
                  int var25 = (Integer)var24.invoke(null, var23);
                  if (var25 != 0) {
                     throw new SecurityGuard.SecurityViolationException("Native debugger detected (IsDebuggerPresent = " + var25 + ")");
                  }
               }
            }
         } catch (SecurityGuard.SecurityViolationException var13) {
            throw var13;
         } catch (Throwable var14) {
         }

         try {
            ProcessHandle.allProcesses().forEach(p -> p.info().command().ifPresent(cmd -> {
               String var1x = cmd.toLowerCase(Locale.ROOT);

               for (String var3x : FORBIDDEN_DEBUG_PROCESSES) {
                  if (var1x.endsWith(var3x) || var1x.contains("\\" + var3x)) {
                     throw new RuntimeException("Blacklisted debugger process active: " + var3x);
                  }
               }
            }));
         } catch (RuntimeException var11) {
            if (var11.getMessage() != null
               && var11.getMessage().startsWith("Blacklisted")) {
               throw new SecurityGuard.SecurityViolationException(var11.getMessage());
            }
         } catch (Throwable var12) {
         }
      } else if (var0.contains("linux")) {
         Path var17 = Path.of("/proc/self/status");
         if (Files.exists(var17)) {
            try {
               for (String var20 : Files.readAllLines(var17)) {
                  if (var20.startsWith("TracerPid:")) {
                     String var21 = var20.substring(10).trim();
                     int var22 = Integer.parseInt(var21);
                     if (var22 > 0) {
                        throw new SecurityGuard.SecurityViolationException("Linux ptrace tracer detected (TracerPid = " + var22 + ")");
                     }
                     break;
                  }
               }
            } catch (SecurityGuard.SecurityViolationException var15) {
               throw var15;
            } catch (Throwable var16) {
            }
         }
      }
   }

   private static void performRedundantLoaderCrossCheck() {
      try {
         Class var0 = Class.forName(
            "me.drake.vieclient.loader.CrossCheckManager"
         );
         Method var1 = var0.getMethod(
            "performRedundantCrossCheck",
            String.class
         );
         var1.invoke(
            null,
            "SecurityGuard-periodic"
         );
      } catch (ClassNotFoundException var2) {
      } catch (Throwable var3) {
         CoreEntry.LOGGER
            .warn(
               "[SecurityGuard] Redundant cross check warning: {}",
               var3.getMessage()
            );
      }
   }

   private static String calculateAuthStateHash(Object authManager) throws Exception {
      try {
         Class var1 = authManager.getClass();
         Field var2 = var1.getDeclaredField("status");
         var2.setAccessible(true);
         Object var3 = var2.get(authManager);
         Field var4 = var1.getDeclaredField("keyCode");
         var4.setAccessible(true);
         String var5 = (String)var4.get(authManager);
         Field var6 = var1.getDeclaredField("lockedDown");
         var6.setAccessible(true);
         boolean var7 = (Boolean)var6.get(authManager);
         String var8 = var3.toString() + var5 + var7;
         MessageDigest var9 = MessageDigest.getInstance("SHA-256");
         byte[] var10 = var9.digest(var8.getBytes(StandardCharsets.UTF_8));
         StringBuilder var11 = new StringBuilder();

         for (byte var15 : var10) {
            var11.append(String.format("%02x", var15));
         }

         return var11.toString();
      } catch (Exception var16) {
         throw new SecurityGuard.SecurityViolationException("Failed to calculate auth state hash: " + var16.getMessage());
      }
   }

   private static void verifyRuntimeChecksums() throws SecurityGuard.SecurityViolationException {
      if (!jarChecksumSHA256.isEmpty() && !jarChecksumMD5.isEmpty() && !jarChecksumCRC32.isEmpty()) {
         try {
            Path var0 = resolveJarPath();
            if (var0 != null) {
               byte[] var1 = Files.readAllBytes(var0);
               MessageDigest var2 = MessageDigest.getInstance("SHA-256");
               byte[] var3 = var2.digest(var1);
               StringBuilder var4 = new StringBuilder();

               for (byte var8 : var3) {
                  var4.append(String.format("%02x", var8));
               }

               String var15 = var4.toString();
               if (!var15.equals(jarChecksumSHA256)) {
                  throw new SecurityGuard.SecurityViolationException(
                     "SHA-256 checksum mismatch"
                  );
               }

               MessageDigest var16 = MessageDigest.getInstance("MD5");
               byte[] var17 = var16.digest(var1);
               var4 = new StringBuilder();

               for (byte var11 : var17) {
                  var4.append(String.format("%02x", var11));
               }

               String var19 = var4.toString();
               if (!var19.equals(jarChecksumMD5)) {
                  throw new SecurityGuard.SecurityViolationException(
                     "MD5 checksum mismatch"
                  );
               }

               CRC32 var20 = new CRC32();
               var20.update(var1);
               String var21 = Long.toHexString(var20.getValue());
               if (!var21.equals(jarChecksumCRC32)) {
                  throw new SecurityGuard.SecurityViolationException(
                     "CRC32 checksum mismatch"
                  );
               }
            }
         } catch (SecurityGuard.SecurityViolationException var12) {
            throw var12;
         } catch (Exception var13) {
            CoreEntry.LOGGER
               .warn(
                  "[SecurityGuard] Runtime checksum verification failed: {}",
                  var13.getMessage()
               );
         }
      }
   }

   private static void checkClassIntegrity() throws SecurityGuard.SecurityViolationException {
      for (String var1 : TRUSTED_CLASSES) {
         try {
            Class var2 = Class.forName(var1);
            byte[] var3 = getClassBytes(var2);
            if (var3 == null || var3.length == 0) {
               throw new SecurityGuard.SecurityViolationException("Class tampered: " + var1);
            }

            if (containsSuspiciousPatterns(var3)) {
               throw new SecurityGuard.SecurityViolationException("Suspicious bytecode: " + var1);
            }

            if (!isValidClassStructure(var3)) {
               throw new SecurityGuard.SecurityViolationException("Invalid class structure: " + var1);
            }
         } catch (ClassNotFoundException var4) {
            throw new SecurityGuard.SecurityViolationException("Missing trusted class: " + var1);
         }
      }
   }

   private static boolean isValidClassStructure(byte[] bytecode) {
      return bytecode == null || bytecode.length < (10)
         ? false
         : bytecode[0] == (-54)
            && bytecode[1] == (-2)
            && bytecode[2] == (-70)
            && bytecode[3] == (-66);
   }

   private static void checkDebugFlags() throws SecurityGuard.SecurityViolationException {
      if (isEnabled()) {
         String var10000 = ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
         byte[] var10001 = new byte[14];
         var10001[0] = (byte)(-51);
         var10001[1] = (byte)(-5);
         var10001[2] = (byte)(123);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(74);
         var10001[5] = (byte)(44);
         var10001[6] = (byte)(-7);
         var10001[7] = (byte)(-19);
         var10001[8] = (byte)(-22);
         var10001[9] = (byte)(-104);
         var10001[10] = (byte)(-114);
         var10001[11] = (byte)(-110);
         var10001[12] = (byte)(11);
         var10001[13] = (byte)(-16);
         boolean var0 = var10000.contains($$v_s(var10001, -6010468642562256699L));
         if (var0) {
            byte[] var10002 = new byte[19];
            var10002[0] = (byte)(-92);
            var10002[1] = (byte)(-1);
            var10002[2] = (byte)(126);
            var10002[3] = (byte)(-85);
            var10002[4] = (byte)(67);
            var10002[5] = (byte)(120);
            var10002[6] = (byte)(-8);
            var10002[7] = (byte)(-21);
            var10002[8] = (byte)(-20);
            var10002[9] = (byte)(-57);
            var10002[10] = (byte)(-60);
            var10002[11] = (byte)(-110);
            var10002[12] = (byte)(25);
            var10002[13] = (byte)(-12);
            var10002[14] = (byte)(-40);
            var10002[15] = (byte)(-97);
            var10002[16] = (byte)(-60);
            var10002[17] = (byte)(47);
            var10002[18] = (byte)(-88);
            throw new SecurityGuard.SecurityViolationException($$v_s(var10002, -6010468642562256699L));
         }
      }
   }

   private static void checkDebuggerAttached() throws SecurityGuard.SecurityViolationException {
      if (isEnabled()) {
         boolean var2;
         label19: {
            String var10000 = ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
            byte[] var10001 = new byte[7];
            var10001[0] = (byte)(-51);
            var10001[1] = (byte)(-62);
            var10001[2] = (byte)(120);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(70);
            var10001[5] = (byte)(45);
            var10001[6] = (byte)(-14);
            if (!var10000.contains($$v_s(var10001, -6010468642562256699L))) {
               String var1 = ManagementFactory.getRuntimeMXBean().getInputArguments().toString();
               var10001 = new byte[9];
               var10001[0] = (byte)(-51);
               var10001[1] = (byte)(-62);
               var10001[2] = (byte)(110);
               var10001[3] = (byte)(-85);
               var10001[4] = (byte)(74);
               var10001[5] = (byte)(50);
               var10001[6] = (byte)(-15);
               var10001[7] = (byte)(-13);
               var10001[8] = (byte)(-8);
               if (!var1.contains($$v_s(var10001, -6010468642562256699L))) {
                  var2 = false;
                  break label19;
               }
            }

            var2 = true;
         }

         boolean var0 = var2;
         if (var0) {
            byte[] var10002 = new byte[17];
            var10002[0] = (byte)(-92);
            var10002[1] = (byte)(-1);
            var10002[2] = (byte)(126);
            var10002[3] = (byte)(-85);
            var10002[4] = (byte)(67);
            var10002[5] = (byte)(63);
            var10002[6] = (byte)(-16);
            var10002[7] = (byte)(-10);
            var10002[8] = (byte)(-88);
            var10002[9] = (byte)(-61);
            var10002[10] = (byte)(-112);
            var10002[11] = (byte)(-126);
            var10002[12] = (byte)(29);
            var10002[13] = (byte)(-29);
            var10002[14] = (byte)(-43);
            var10002[15] = (byte)(-103);
            var10002[16] = (byte)(-44);
            throw new SecurityGuard.SecurityViolationException($$v_s(var10002, -6010468642562256699L));
         }
      }
   }

   private static void checkTampering() throws SecurityGuard.SecurityViolationException {
   }

   private static void checkEnvironment() throws SecurityGuard.SecurityViolationException {
      String var0 = System.getProperty("java.vm.vendor", "")
         .toLowerCase();
      String var1 = System.getProperty("java.vm.name", "").toLowerCase();
      boolean var2 = var0.contains("android")
         || var1.contains("android")
         || var0.contains("dalvik");
      if (var2) {
         CoreEntry.LOGGER
            .info(
               "[SecurityGuard] Android environment detected, skipping VM/sandbox checks"
            );
      } else {
         String var3 = System.getProperty("user.name", "").toLowerCase();
         if (!var3.equals("vboxuser")
            && !var3.equals("vmware")) {
            try {
               if (isVirtualMachine()) {
                  if (isStrict()) {
                     throw new SecurityGuard.SecurityViolationException(
                        "Virtual machine detected"
                     );
                  }

                  CoreEntry.LOGGER
                     .warn(
                        "[SecurityGuard] Virtual machine detected (non-strict mode)"
                     );
               }
            } catch (SecurityGuard.SecurityViolationException var5) {
               throw var5;
            } catch (Exception var6) {
            }
         } else {
            throw new SecurityGuard.SecurityViolationException(
               "Sandbox environment detected"
            );
         }
      }
   }

   private static void calculateMultipleChecksums() {
      try {
         Path var0 = resolveJarPath();
         if (var0 != null) {
            byte[] var1 = Files.readAllBytes(var0);
            initialJarSize = var1.length;
            MessageDigest var2 = MessageDigest.getInstance("SHA-256");
            byte[] var3 = var2.digest(var1);
            StringBuilder var4 = new StringBuilder();

            for (byte var8 : var3) {
               var4.append(String.format("%02x", var8));
            }

            jarChecksumSHA256 = var4.toString();
            MessageDigest var13 = MessageDigest.getInstance("MD5");
            byte[] var14 = var13.digest(var1);
            var4 = new StringBuilder();

            for (byte var10 : var14) {
               var4.append(String.format("%02x", var10));
            }

            jarChecksumMD5 = var4.toString();
            CRC32 var16 = new CRC32();
            var16.update(var1);
            jarChecksumCRC32 = Long.toHexString(var16.getValue());
         }
      } catch (Exception var11) {
         CoreEntry.LOGGER
            .warn(
               "[SecurityGuard] Could not calculate JAR checksums: {}",
               var11.getMessage()
            );
      }
   }

   private static void verifyJarSize() throws SecurityGuard.SecurityViolationException {
      if (initialJarSize != 0L) {
         try {
            Path var0 = resolveJarPath();
            if (var0 != null) {
               long var1 = Files.size(var0);
               if (var1 != initialJarSize) {
                  throw new SecurityGuard.SecurityViolationException("JAR size changed: expected " + initialJarSize + ", got " + var1);
               }
            }
         } catch (IOException var3) {
            byte[] var10001 = new byte[41];
            var10001[0] = (byte)(-69);
            var10001[1] = (byte)(-55);
            var10001[2] = (byte)(121);
            var10001[3] = (byte)(-67);
            var10001[4] = (byte)(81);
            var10001[5] = (byte)(42);
            var10001[6] = (byte)(-4);
            var10001[7] = (byte)(-16);
            var10001[8] = (byte)(-15);
            var10001[9] = (byte)(-27);
            var10001[10] = (byte)(-111);
            var10001[11] = (byte)(-105);
            var10001[12] = (byte)(14);
            var10001[13] = (byte)(-28);
            var10001[14] = (byte)(-32);
            var10001[15] = (byte)(-36);
            var10001[16] = (byte)(-13);
            var10001[17] = (byte)(37);
            var10001[18] = (byte)(-71);
            var10001[19] = (byte)(-62);
            var10001[20] = (byte)(-16);
            var10001[21] = (byte)(-120);
            var10001[22] = (byte)(-85);
            var10001[23] = (byte)(-69);
            var10001[24] = (byte)(44);
            var10001[25] = (byte)(50);
            var10001[26] = (byte)(-30);
            var10001[27] = (byte)(35);
            var10001[28] = (byte)(-34);
            var10001[29] = (byte)(-71);
            var10001[30] = (byte)(-117);
            var10001[31] = (byte)(117);
            var10001[32] = (byte)(32);
            var10001[33] = (byte)(112);
            var10001[34] = (byte)(61);
            var10001[35] = (byte)(44);
            var10001[36] = (byte)(-28);
            var10001[37] = (byte)(-117);
            var10001[38] = (byte)(92);
            var10001[39] = (byte)(30);
            var10001[40] = (byte)(77);
            CoreEntry.LOGGER.warn($$v_s(var10001, -6010468642562256699L));
         }
      }
   }

   private static void checkAntiHooking() throws SecurityGuard.SecurityViolationException {
      try {
         String var0 = System.getProperty("os.name", "").toLowerCase();
         String var1 = System.getProperty(
               "java.system.class.loader",
               ""
            )
            .toLowerCase();
         if (var0.contains("iphone")
            || var0.contains("ios")
            || var1.contains("pojav")) {
            return;
         }
      } catch (Exception var10) {
         CoreEntry.LOGGER
            .warn(
               "[VieClient] Anti-agent check failed",
               var10
            );
      }

      try {
         for (String var13 : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (var13.toLowerCase().contains("-javaagent")) {
               String var2 = var13.toLowerCase();
               if (!var2.contains("fabric")
                  && !var2.contains("mixin")
                  && !var2.contains("theseus")
                  && !var2.contains("lunar")
                  && !var2.contains("overwolf")
                  && !var2.contains("curseforge")
                  && !var2.contains("fabric-share")
                  && !var2.contains("movtery")
                  && !var2.contains("zalith")
                  && !var2.contains("miolibpatcher")
                  && !var2.contains("pojav")
                  && !var2.contains("cacio")
                  && !var2.contains("caciocavallo")
                  && !var2.contains("authlib")
                  && !var2.contains("nide8")
                  && !var2.contains("forge")
                  && !var2.contains("patchjna")
                  && !var2.contains("arc_dns")) {
                  throw new SecurityGuard.SecurityViolationException("Java agent detected: " + var13);
               }
            }
         }
      } catch (SecurityGuard.SecurityViolationException var9) {
         CoreEntry.LOGGER
            .warn(
               "[SecurityGuard] Untrusted Java agent detected: {}",
               var9.getMessage()
            );
         if (isStrict()) {
            throw var9;
         }
      }

      try {
         String var12 = System.getProperty("os.name", "").toLowerCase();
         String var14 = System.getProperty(
               "java.system.class.loader",
               ""
            )
            .toLowerCase();
         if (!var12.contains("iphone")
            && !var12.contains("ios")
            && !var12.contains("ipad")
            && !var14.contains("pojav")
            && !var14.contains("zalith")) {
            Instrumentation var15 = Instrumentation.class
               .cast(
                  Class.forName(
                        "me.drake.vieclient.security.SecurityGuard"
                     )
                     .getClassLoader()
                     .loadClass(
                        "java.lang.instrument.Instrumentation"
                     )
                     .getMethod(
                        "getInstrumentation"
                     )
                     .invoke(null)
               );
            if (var15 != null && var15.getAllLoadedClasses().length > 0) {
               for (Class var6 : var15.getAllLoadedClasses()) {
                  String var7 = var6.getName().toLowerCase();
                  if ((
                        var7.contains("agent")
                           || var7.contains("instrument")
                           || var7.contains("transform")
                     )
                     && !var7.contains("movtery")
                     && !var7.contains("zalith")
                     && !var7.contains("miolibpatcher")
                     && !var7.contains("cacio")
                     && !var7.contains("caciocavallo")
                     && !var7.contains("pojav")) {
                     throw new SecurityGuard.SecurityViolationException(
                        "Instrumentation detected"
                     );
                  }
               }
            }
         }
      } catch (Exception var8) {
      }
   }

   private static byte[] getClassBytes(Class clazz) {
      try {
         String var1 = clazz.getName().replace((char)(46), (char)(47)) + ".class";
         InputStream var2 = clazz.getClassLoader().getResourceAsStream(var1);
         if (var2 == null) {
            return null;
         }

         ByteArrayOutputStream var3 = new ByteArrayOutputStream();
         byte[] var4 = new byte[4096];

         int var5;
         while ((var5 = var2.read(var4, 0, var4.length)) != (-1)) {
            var3.write(var4, 0, var5);
         }

         return var3.toByteArray();
      } catch (IOException var6) {
         return null;
      }
   }

   private static boolean containsSuspiciousPatterns(byte[] bytecode) {
      if (bytecode != null && bytecode.length >= (10)) {
         String var1 = new String(bytecode);
         byte[] var10001 = new byte[4];
         var10001[0] = (byte)(-118);
         var10001[1] = (byte)(-2);
         var10001[2] = (byte)(107);
         var10001[3] = (byte)(-82);
         if (!var1.contains($$v_s(var10001, -6010468642562256699L))) {
            var10001 = new byte[8];
            var10001[0] = (byte)(-124);
            var10001[1] = (byte)(-1);
            var10001[2] = (byte)(126);
            var10001[3] = (byte)(-85);
            var10001[4] = (byte)(67);
            var10001[5] = (byte)(63);
            var10001[6] = (byte)(-16);
            var10001[7] = (byte)(-10);
            if (!var1.contains($$v_s(var10001, -6010468642562256699L))) {
               return false;
            }
         }

         return true;
      } else {
         return true;
      }
   }

   private static boolean isVirtualMachine() {
      try {
         String[] var0 = new String[]{
            "vmware",
            "virtualbox",
            "qemu",
            "xen",
            "kvm",
            "parallels",
            "hyperv",
            "bochs"
         };
         String var1 = "";

         try {
            if (System.getProperty("os.name", "")
               .toLowerCase()
               .contains("linux")) {
               Path var2 = Paths.get("/proc/cpuinfo");
               if (Files.exists(var2)) {
                  var1 = Files.readString(var2).toLowerCase();
               }
            }
         } catch (Exception var7) {
         }

         for (String var5 : var0) {
            if (var1.contains(var5)) {
               return true;
            }
         }

         try {
            String var14 = getMacAddress();
            if (var14.startsWith("00:05:69")
               || var14.startsWith("00:0C:29")
               || var14.startsWith("00:50:56")
               || var14.startsWith("00:1C:14")
               || var14.startsWith("00:16:3E")
               || var14.startsWith("08:00:27")
               || var14.startsWith("00:1A:4A")
               || var14.startsWith("00:0F:4B")) {
               return true;
            }
         } catch (Exception var11) {
         }

         String var15 = System.getProperty("os.name", "").toLowerCase();
         if (var15.contains("linux")) {
            try {
               Path var16 = Paths.get(
                  "/sys/class/dmi/id/product_name"
               );
               if (Files.exists(var16)) {
                  String var20 = Files.readString(var16).toLowerCase();
                  if (var20.contains("vmware")
                     || var20.contains("virtualbox")
                     || var20.contains("qemu")
                     || var20.contains("kvm")
                     || var20.contains("xen")
                     || var20.contains("parallels")) {
                     return true;
                  }
               }
            } catch (Exception var10) {
            }

            try {
               Path var17 = Paths.get(
                  "/sys/class/dmi/id/bios_vendor"
               );
               if (Files.exists(var17)) {
                  String var21 = Files.readString(var17).toLowerCase();
                  if (var21.contains("vmware")
                     || var21.contains("virtualbox")
                     || var21.contains("qemu")
                     || var21.contains("bochs")) {
                     return true;
                  }
               }
            } catch (Exception var9) {
            }
         }

         if (var15.contains("windows")) {
            try {
               Process var18 = new ProcessBuilder(
                     "wmic",
                     "computersystem",
                     "get",
                     "manufacturer"
                  )
                  .redirectErrorStream(true)
                  .start();
               String var22 = new String(var18.getInputStream().readAllBytes()).toLowerCase();
               if (var22.contains("vmware")
                  || var22.contains("virtualbox")
                  || var22.contains("qemu")
                  || var22.contains("xen")
                  || var22.contains(
                        "microsoft corporation"
                     )
                     && var22.contains("virtual")) {
                  return true;
               }
            } catch (Exception var8) {
            }
         }

         try {
            if (System.getProperty("os.name", "")
               .toLowerCase()
               .contains("linux")) {
               Path var19 = Paths.get("/proc/cpuinfo");
               if (Files.exists(var19)) {
                  String var23 = Files.readString(var19).toLowerCase();
                  if (var23.contains("hypervisor")) {
                     return true;
                  }
               }
            }
         } catch (Exception var6) {
         }

         return false;
      } catch (Exception var12) {
         return false;
      }
   }

   private static boolean detectVMTiming() {
      try {
         long var0 = System.nanoTime();

         for (int var2 = 0; var2 < (10000); var2++) {
            Math.sqrt(var2);
         }

         long var5 = System.nanoTime() - var0;
         return var5 > 10000000L || var5 < 1000L;
      } catch (Exception var4) {
         return false;
      }
   }

   private static String getMacAddress() {
      try {
         byte[] var10000 = new byte[4];
         var10000[0] = (byte)(-123);
         var10000[1] = (byte)(-18);
         var10000[2] = (byte)(116);
         var10000[3] = (byte)(-18);
         NetworkInterface var0 = NetworkInterface.getByName($$v_s(var10000, -6010468642562256699L));
         if (var0 != null) {
            byte[] var1 = var0.getHardwareAddress();
            if (var1 != null) {
               StringBuilder var2 = new StringBuilder();

               for (int var3 = 0; var3 < var1.length; var3++) {
                  byte[] var10001 = new byte[6];
                  var10001[0] = (byte)(-59);
                  var10001[1] = (byte)(-86);
                  var10001[2] = (byte)(46);
                  var10001[3] = (byte)(-122);
                  var10001[4] = 1;
                  var10001[5] = (byte)(43);
                  String var5 = $$v_s(var10001, -6010468642562256699L);
                  Object[] var10002 = new Object[2];
                  var10002[0] = var1[var3];
                  var10002[1] = var3 < var1.length - 1 ? ":" : "";
                  var2.append(String.format(var5, var10002));
               }

               return var2.toString();
            }
         }
      } catch (Exception var4) {
      }

      return "";
   }

   private static String getJarPath() {
      Path var0 = resolveJarPath();
      return var0 == null ? null : var0.toString();
   }

   public static void crash(String reason) {
      compromised = true;
      byte[] var10001 = new byte[47];
      var10001[0] = (byte)(-69);
      var10001[1] = (byte)(-55);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-67);
      var10001[4] = (byte)(81);
      var10001[5] = (byte)(42);
      var10001[6] = (byte)(-4);
      var10001[7] = (byte)(-16);
      var10001[8] = (byte)(-15);
      var10001[9] = (byte)(-27);
      var10001[10] = (byte)(-111);
      var10001[11] = (byte)(-105);
      var10001[12] = (byte)(14);
      var10001[13] = (byte)(-28);
      var10001[14] = (byte)(-32);
      var10001[15] = (byte)(-36);
      var10001[16] = (byte)(-29);
      var10001[17] = (byte)(47);
      var10001[18] = (byte)(-81);
      var10001[19] = (byte)(-37);
      var10001[20] = (byte)(-26);
      var10001[21] = (byte)(-63);
      var10001[22] = (byte)(-79);
      var10001[23] = (byte)(-83);
      var10001[24] = (byte)(120);
      var10001[25] = (byte)(100);
      var10001[26] = (byte)(-3);
      var10001[27] = (byte)(41);
      var10001[28] = (byte)(-64);
      var10001[29] = (byte)(-79);
      var10001[30] = (byte)(-103);
      var10001[31] = (byte)(101);
      var10001[32] = (byte)(111);
      var10001[33] = (byte)(84);
      var10001[34] = (byte)(92);
      var10001[35] = (byte)(26);
      var10001[36] = (byte)(-95);
      var10001[37] = (byte)(-116);
      var10001[38] = (byte)(80);
      var10001[39] = (byte)(7);
      var10001[40] = (byte)(92);
      var10001[41] = (byte)(-89);
      var10001[42] = (byte)(32);
      var10001[43] = (byte)(44);
      var10001[44] = (byte)(60);
      var10001[45] = (byte)(91);
      var10001[46] = (byte)(32);
      CoreEntry.LOGGER.error($$v_s(var10001, -6010468642562256699L), reason);

      try {
         AuthManager var1 = AuthManager.getInstance();
         if (var1 != null) {
            var1.forceLockdown("Security integrity violation: " + reason);
         }
      } catch (Throwable var2) {
      }

      if (isStrict()) {
         Runtime.getRuntime().halt(1);
      }
   }

   public static boolean isCompromised() {
      return compromised;
   }

   public static void blockReflectionAccess() {
   }

   public static boolean verifyJarSignature() {
      try {
         Path var0 = resolveJarPath();
         if (var0 == null) {
            return false;
         }

         File var1 = var0.toFile();
         if (!var1.exists()) {
            return false;
         }

         ZipFile var2 = new ZipFile(var1);
         long var3 = 0L;
         Enumeration var5 = var2.entries();

         while (var5.hasMoreElements()) {
            ZipEntry var6 = (ZipEntry)var5.nextElement();
            if (!var6.isDirectory()) {
               var3 += var6.getSize();
            }
         }

         var2.close();
         return var3 > 0L;
      } catch (Exception var7) {
         CoreEntry.LOGGER
            .error(
               "[SecurityGuard] JAR signature check failed: {}",
               var7.getMessage()
            );
         return false;
      }
   }

   private static Path resolveJarPath() {
      try {
         ProtectionDomain var0 = SecurityGuard.class.getProtectionDomain();
         if (var0 != null && var0.getCodeSource() != null && var0.getCodeSource().getLocation() != null) {
            URI var1 = var0.getCodeSource().getLocation().toURI();
            Path var2 = null;
            if ("file".equalsIgnoreCase(var1.getScheme())) {
               var2 = Paths.get(var1).toAbsolutePath().normalize();
            } else {
               String var3 = var1.toString();
               if (var3.startsWith("jar:")) {
                  int var4 = var3.indexOf(33);
                  String var5 = var4 > 4 ? var3.substring(4, var4) : var3.substring(4);
                  URI var6 = URI.create(var5);
                  if ("file".equalsIgnoreCase(var6.getScheme())) {
                     var2 = Paths.get(var6).toAbsolutePath().normalize();
                  }
               }
            }

            if (var2 == null) {
               return null;
            } else {
               String var8 = var2.toString().toLowerCase(Locale.ROOT);
               if (!var8.endsWith(".jar")) {
                  return null;
               } else {
                  return Files.exists(var2) && Files.isRegularFile(var2) ? var2 : null;
               }
            }
         } else {
            return null;
         }
      } catch (Exception var7) {
         CoreEntry.LOGGER
            .warn(
               "[VieClient] JAR path resolution failed",
               var7
            );
         return null;
      }
   }

   static {
      TRUSTED_CLASSES.add(
         "me.drake.vieclient.CoreEntry"
      );
      TRUSTED_CLASSES.add(
         "me.drake.vieclient.auth.AuthManager"
      );
      TRUSTED_CLASSES.add(
         "me.drake.vieclient.security.ChimeraEngine"
      );
      TRUSTED_CLASSES.add(
         "me.drake.vieclient.internal.VieWebSocketClient"
      );
      TRUSTED_CLASSES.add(
         "me.drake.vieclient.security.SecurityGuard"
      );
      TRUSTED_CLASSES.add(
         "me.drake.vieclient.security.RuntimeGuard"
      );
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

   private static class SecurityViolationException extends Exception {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public SecurityViolationException(String message) {
         super(message);
      }
   }
}
