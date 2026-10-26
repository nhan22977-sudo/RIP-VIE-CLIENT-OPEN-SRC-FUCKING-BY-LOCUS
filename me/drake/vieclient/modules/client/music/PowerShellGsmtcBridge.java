package me.drake.vieclient.modules.client.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public final class PowerShellGsmtcBridge implements MediaSessionBridge {
   private static final String SCRIPT_RESOURCE = "/assets/vie_client/music/soundcloud-gsmtc.ps1";
   private static final long MAX_RESTART_DELAY_MS = 30000L;
   private final AtomicReference latest;
   private final AtomicBoolean closed;
   private final GsmtcProcessLauncher launcher;
   private final ScheduledExecutorService restartExecutor;
   private final boolean windows;
   private final long baseRestartDelayMs;
   private final PowerShellGsmtcBridge.Target target;
   private volatile Process process;
   private Path scriptPath;
   private final AtomicInteger restartAttempt;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static MediaSessionBridge open() {
      byte[] var10001 = new byte[28];
      var10001[0] = (byte)(84);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-13);
      var10001[4] = (byte)(5);
      var10001[5] = (byte)(-27);
      var10001[6] = (byte)(-25);
      var10001[7] = (byte)(-34);
      var10001[8] = (byte)(46);
      var10001[9] = (byte)(-63);
      var10001[10] = (byte)(-120);
      var10001[11] = (byte)(-103);
      var10001[12] = (byte)(91);
      var10001[13] = (byte)(54);
      var10001[14] = (byte)(-105);
      var10001[15] = (byte)(-81);
      var10001[16] = 1;
      var10001[17] = (byte)(39);
      var10001[18] = (byte)(-72);
      var10001[19] = (byte)(-51);
      var10001[20] = (byte)(-21);
      var10001[21] = (byte)(8);
      var10001[22] = (byte)(-89);
      var10001[23] = (byte)(-109);
      var10001[24] = (byte)(-18);
      var10001[25] = (byte)(115);
      var10001[26] = (byte)(-26);
      var10001[27] = (byte)(50);
      return open(PowerShellGsmtcBridge.Target.SOUNDCLOUD, $$v_s(var10001, -7453082659037392889L));
   }

   public static MediaSessionBridge openBrowser() {
      byte[] var10001 = new byte[25];
      var10001[0] = (byte)(84);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-13);
      var10001[4] = (byte)(15);
      var10001[5] = (byte)(-27);
      var10001[6] = (byte)(-25);
      var10001[7] = (byte)(-60);
      var10001[8] = (byte)(63);
      var10001[9] = (byte)(-64);
      var10001[10] = (byte)(-127);
      var10001[11] = (byte)(-37);
      var10001[12] = (byte)(73);
      var10001[13] = (byte)(33);
      var10001[14] = (byte)(-41);
      var10001[15] = (byte)(-68);
      var10001[16] = (byte)(17);
      var10001[17] = (byte)(103);
      var10001[18] = (byte)(-66);
      var10001[19] = (byte)(-53);
      var10001[20] = (byte)(-75);
      var10001[21] = (byte)(14);
      var10001[22] = (byte)(-93);
      var10001[23] = (byte)(-110);
      var10001[24] = (byte)(-18);
      return open(PowerShellGsmtcBridge.Target.BROWSER, $$v_s(var10001, -7453082659037392889L));
   }

   private static MediaSessionBridge open(PowerShellGsmtcBridge.Target target, String threadName) {
      ScheduledExecutorService var2 = Executors.newSingleThreadScheduledExecutor(runnable -> {
         Thread var2x = new Thread(runnable, threadName);
         var2x.setDaemon(true);
         return var2x;
      });
      GsmtcProcessLauncher var10002 = command -> new ProcessBuilder(command).redirectErrorStream(false).start();
      byte[] var10004 = new byte[7];
      var10004[0] = (byte)(77);
      var10004[1] = (byte)(-23);
      var10004[2] = (byte)(50);
      var10004[3] = (byte)(-80);
      var10004[4] = (byte)(23);
      var10004[5] = (byte)(-25);
      var10004[6] = (byte)(-9);
      return new PowerShellGsmtcBridge(var10002, var2, System.getProperty($$v_s(var10004, -7453082659037392889L), ""), 1000L, targetx);
   }

   PowerShellGsmtcBridge(GsmtcProcessLauncher launcher, ScheduledExecutorService restartExecutor, String osName, long baseRestartDelayMs) {
      this(launcherx, restartExecutorx, osName, baseRestartDelayMsx, PowerShellGsmtcBridge.Target.SOUNDCLOUD);
   }

   PowerShellGsmtcBridge(
      GsmtcProcessLauncher launcher, ScheduledExecutorService restartExecutor, String osName, long baseRestartDelayMs, PowerShellGsmtcBridge.Target target
   ) {
      boolean var7;
      label21: {
         this.latest = new AtomicReference();
         this.closed = new AtomicBoolean();
         this.restartAttempt = new AtomicInteger();
         this.launcher = launcherx;
         this.restartExecutor = restartExecutorx;
         if (osName != null) {
            String var10001 = osName.toLowerCase(Locale.ROOT);
            byte[] var10002 = new byte[3];
            var10002[0] = (byte)(85);
            var10002[1] = (byte)(-13);
            var10002[2] = (byte)(114);
            if (var10001.contains($$v_s(var10002, -7453082659037392889L))) {
               var7 = true;
               break label21;
            }
         }

         var7 = false;
      }

      this.windows = var7;
      this.baseRestartDelayMs = Math.max(1L, baseRestartDelayMsx);
      this.target = targetx == null ? PowerShellGsmtcBridge.Target.SOUNDCLOUD : targetx;
      if (this.windows) {
         this.startProcess();
      }
   }

   @Override
   public Optional latest() {
      return Optional.ofNullable((WindowsMediaSessionSnapshot)this.latest.get());
   }

   private synchronized void startProcess() {
      if (!this.closed.get() && this.windows && this.process == null) {
         try {
            Process var1 = this.launcher.start(this.buildCommand());
            if (this.closed.get()) {
               stopProcess(var1);
               return;
            }

            this.process = var1;

            try {
               var1.getOutputStream().close();
            } catch (IOException var3) {
            }

            this.startStdoutReader(var1);
            this.startStderrDrainer(var1);
         } catch (IOException | RuntimeException var4) {
            this.scheduleRestart();
         }
      }
   }

   private synchronized Path getOrCreateScriptFile() throws IOException {
      if (this.scriptPath != null && Files.exists(this.scriptPath)) {
         return this.scriptPath;
      }

      String var10000 = "vie-gsmtc-" + this.target.name().toLowerCase(Locale.ROOT) + "-";
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(12);
      var10001[1] = (byte)(-22);
      var10001[2] = (byte)(111);
      var10001[3] = (byte)(-17);
      Path var1 = Files.createTempFile(var10000, $$v_s(var10001, -7453082659037392889L));
      var1.toFile().deleteOnExit();
      String var2 = this.readScript();
      Files.writeString(var1, var2, StandardCharsets.UTF_8);
      this.scriptPath = var1;
      return var1;
   }

   private List buildCommand() throws IOException {
      String var1 = System.getenv("SystemRoot");
      if (var1 == null || var1.isBlank()) {
         var1 = "C:\\Windows";
      }

      String var2 = Path.of(
            var1,
            "System32",
            "WindowsPowerShell",
            "v1.0",
            "powershell.exe"
         )
         .toString();
      Path var3 = this.getOrCreateScriptFile();
      return List.of(
         var2,
         "-NoLogo",
         "-NoProfile",
         "-NonInteractive",
         "-WindowStyle",
         "Hidden",
         "-ExecutionPolicy",
         "Bypass",
         "-File",
         var3.toAbsolutePath().toString(),
         "-target",
         this.target.name()
      );
   }

   private String readScript() throws IOException {
      try (InputStream var1 = PowerShellGsmtcBridge.class
            .getResourceAsStream(
               "/assets/vie_client/music/soundcloud-gsmtc.ps1"
            )) {
         if (var1 == null) {
            throw new IOException(
               "Missing GSMTC helper resource"
            );
         } else {
            return new String(var1.readAllBytes(), StandardCharsets.UTF_8)
               .replace(
                  "__VIE_GSMTC_TARGET__",
                  this.target.name()
               );
         }
      }
   }

   private void startStdoutReader(Process owner) {
      String var2 = "vie-" + this.target.name().toLowerCase(Locale.ROOT) + "-gsmtc-out";
      Thread var3 = new Thread(() -> {
         try {
            String var3x;
            try (BufferedReader var2x = new BufferedReader(new InputStreamReader(owner.getInputStream(), StandardCharsets.UTF_8))) {
               while (!this.closed.get() && (var3x = var2x.readLine()) != null) {
                  this.acceptLine(var3x);
               }
            } catch (IOException var12) {
            }
         } finally {
            this.handleExit(owner);
         }
      }, var2);
      var3.setDaemon(true);
      var3.start();
   }

   private void startStderrDrainer(Process owner) {
      String var2 = "vie-" + this.target.name().toLowerCase(Locale.ROOT) + "-gsmtc-err";
      Thread var3 = new Thread(() -> {
         try (InputStream var1 = owner.getErrorStream()) {
            var1.transferTo(OutputStream.nullOutputStream());
         } catch (IOException var6) {
         }
      }, var2);
      var3.setDaemon(true);
      var3.start();
   }

   private void acceptLine(String line) {
      String var2 = line == null ? "" : line.trim();
      this.restartAttempt.set(0);
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(76);
      var10001[1] = (byte)(-17);
      var10001[2] = (byte)(112);
      var10001[3] = (byte)(-78);
      if (var2.equals($$v_s(var10001, -7453082659037392889L))) {
         this.latest.set(null);
      } else {
         Optional var3 = GsmtcJsonParser.parse(var2)
            .filter(snapshot -> this.target == PowerShellGsmtcBridge.Target.BROWSER ? snapshot.isSupportedBrowser() : snapshot.isSoundCloudApp());
         if (var3.isEmpty()) {
            this.latest.set(null);
         } else {
            WindowsMediaSessionSnapshot var4 = (WindowsMediaSessionSnapshot)var3.get();
            this.latest.updateAndGet(previous -> retainArtwork(previous, var4));
         }
      }
   }

   private static WindowsMediaSessionSnapshot retainArtwork(WindowsMediaSessionSnapshot previous, WindowsMediaSessionSnapshot next) {
      return next.artwork() == null && previous != null && sameTrack(previous, next)
         ? new WindowsMediaSessionSnapshot(
            next.sourceApp(),
            next.title(),
            next.artist(),
            next.durationMs(),
            next.positionMs(),
            next.playing(),
            previous.artwork(),
            next.capturedAtMs(),
            next.rawPositionMs(),
            next.timelineUpdatedAtMs()
         )
         : next;
   }

   private static boolean sameTrack(WindowsMediaSessionSnapshot first, WindowsMediaSessionSnapshot second) {
      return first.sourceApp().equalsIgnoreCase(second.sourceApp()) && first.title().equals(second.title()) && first.artist().equals(second.artist());
   }

   private void handleExit(Process owner) {
      synchronized (this) {
         if (this.process != owner) {
            return;
         }

         this.process = null;
      }

      this.latest.set(null);
      if (!this.closed.get()) {
         this.scheduleRestart();
      }
   }

   private void scheduleRestart() {
      if (!this.closed.get() && !this.restartExecutor.isShutdown()) {
         int var1 = Math.min(5, this.restartAttempt.getAndIncrement());
         long var2 = Math.min(30000L, this.baseRestartDelayMs << var1);

         try {
            this.restartExecutor.schedule(this::startProcess, var2, TimeUnit.MILLISECONDS);
         } catch (RuntimeException var5) {
         }
      }
   }

   @Override
   public void close() {
      if (this.closed.compareAndSet(false, true)) {
         this.latest.set(null);
         this.restartExecutor.shutdownNow();
         Process var1;
         synchronized (this) {
            var1 = this.process;
            this.process = null;
            if (this.scriptPath != null) {
               try {
                  Files.deleteIfExists(this.scriptPath);
               } catch (IOException var5) {
               }

               this.scriptPath = null;
            }
         }

         if (var1 != null) {
            stopProcess(var1);
         }
      }
   }

   private static void stopProcess(Process target) {
      targetx.destroy();
      Runnable var10002 = () -> {
         try {
            if (!targetx.waitFor(500L, TimeUnit.MILLISECONDS) && targetx.isAlive()) {
               targetx.destroyForcibly();
            }
         } catch (InterruptedException var2) {
            Thread.currentThread().interrupt();
            if (targetx.isAlive()) {
               targetx.destroyForcibly();
            }
         }
      };
      byte[] var10003 = new byte[16];
      var10003[0] = (byte)(84);
      var10003[1] = (byte)(-13);
      var10003[2] = (byte)(121);
      var10003[3] = (byte)(-13);
      var10003[4] = (byte)(17);
      var10003[5] = (byte)(-7);
      var10003[6] = (byte)(-1);
      var10003[7] = (byte)(-60);
      var10003[8] = (byte)(41);
      var10003[9] = (byte)(-113);
      var10003[10] = (byte)(-106);
      var10003[11] = (byte)(-109);
      var10003[12] = (byte)(79);
      var10003[13] = (byte)(34);
      var10003[14] = (byte)(-33);
      var10003[15] = (byte)(-70);
      Thread var1 = new Thread(var10002, $$v_s(var10003, -7453082659037392889L));
      var1.setDaemon(true);
      var1.start();
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

   enum Target {
      SOUNDCLOUD,
      BROWSER;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static PowerShellGsmtcBridge.Target[] $values() {
         PowerShellGsmtcBridge.Target[] var10000 = new PowerShellGsmtcBridge.Target[2];
         var10000[0] = SOUNDCLOUD;
         var10000[1] = BROWSER;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[10];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(73);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(67);
         var10002[5] = (byte)(103);
         var10002[6] = (byte)(127);
         var10002[7] = (byte)(2);
         var10002[8] = (byte)(4);
         var10002[9] = (byte)(-26);
         SOUNDCLOUD = new PowerShellGsmtcBridge.Target();
         var10002 = new byte[7];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-119);
         var10002[4] = (byte)(84);
         var10002[5] = (byte)(97);
         var10002[6] = (byte)(97);
         BROWSER = new PowerShellGsmtcBridge.Target();
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
}
