package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine.Info;

public final class SpectrumAnalyzer {
   private static final int FFT_SIZE = 1024;
   private static final int SAMPLE_RATE = 44100;
   public static final int BINS = 64;
   private static final long IDLE_STOP_NANOS = 1500000000L;
   private static final String[] LOOPBACK_HINTS = new String[]{
      "loopback",
      "stereo mix",
      "what u hear",
      "wave out",
      "wave-out",
      "speakers",
      "primary sound capture",
      "cable output",
      "voicemeeter",
      "line 1"
   };
   private static final Object BIN_LOCK = new Object();
   private static final float[] BINS_OUT = new float[64];
   private static volatile boolean running = false;
   private static volatile boolean available = false;
   private static volatile long lastPollNanos = 0L;
   private static Thread thread;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SpectrumAnalyzer() {
   }

   public static float[] poll() {
      lastPollNanos = System.nanoTime();
      ensureRunning();
      if (!available) {
         return null;
      }

      synchronized (BIN_LOCK) {
         return (float[])BINS_OUT.clone();
      }
   }

   public static boolean isAvailable() {
      return available;
   }

   public static synchronized void stop() {
      running = false;
      available = false;
      if (thread != null) {
         thread.interrupt();
         thread = null;
      }
   }

   private static synchronized void ensureRunning() {
      if (!running) {
         running = true;
         available = false;
         Runnable var10002 = SpectrumAnalyzer::runLoop;
         byte[] var10003 = new byte[12];
         var10003[0] = (byte)(70);
         var10003[1] = (byte)(-13);
         var10003[2] = (byte)(121);
         var10003[3] = (byte)(-13);
         var10003[4] = (byte)(43);
         var10003[5] = (byte)(42);
         var10003[6] = (byte)(-111);
         var10003[7] = (byte)(-124);
         var10003[8] = (byte)(44);
         var10003[9] = (byte)(-48);
         var10003[10] = (byte)(-111);
         var10003[11] = (byte)(-101);
         thread = new Thread(var10002, $$v_s(var10003, -3461151685459324907L));
         thread.setDaemon(true);
         thread.setPriority(4);
         thread.start();
      }
   }

   private static void runLoop() {
      TargetDataLine var0 = null;

      try {
         var0 = openLoopbackLine();
         if (var0 != null) {
            var0.start();
            available = true;
            AudioFormat var1 = var0.getFormat();
            int var2 = Math.max(1, var1.getChannels());
            int var3 = Math.max(2, var1.getFrameSize());
            byte[] var4 = new byte[1024 * var3];
            float[] var5 = hann(1024);
            float[] var6 = new float[1024];
            float[] var7 = new float[1024];
            float[] var8 = new float[64];
            float[] var9 = new float[64];
            int[] var10 = logBinEdges(1024, 44100, 64);
            long var11 = System.nanoTime();

            while (running && !Thread.currentThread().isInterrupted() && System.nanoTime() - lastPollNanos <= 1500000000L) {
               int var13 = 0;

               while (var13 < var4.length) {
                  int var14 = var0.read(var4, var13, var4.length - var13);
                  if (var14 <= 0) {
                     break;
                  }

                  var13 += var14;
               }

               if (var13 >= var4.length) {
                  for (int var38 = 0; var38 < 1024; var38++) {
                     int var15 = var38 * var3;
                     float var16 = sample16(var4, var15);
                     if (var2 >= 2) {
                        var16 = (var16 + sample16(var4, var15 + 2)) * 0.5F;
                     }

                     var6[var38] = var16 * var5[var38];
                     var7[var38] = 0.0F;
                  }

                  fft(var6, var7);
                  short var39 = 512;

                  for (int var40 = 0; var40 < 64; var40++) {
                     int var42 = var10[var40];
                     int var17 = var10[var40 + 1];
                     float var18 = 0.0F;
                     int var19 = 0;

                     for (int var20 = var42; var20 < var17 && var20 < var39; var20++) {
                        var18 += (float)Math.sqrt(var6[var20] * var6[var20] + var7[var20] * var7[var20]);
                        var19++;
                     }

                     float var46 = var19 > 0 ? var18 / var19 : 0.0F;
                     float var21 = (float)(20.0 * Math.log10(var46 + 1.0E-6));
                     float var22 = (var21 + 62.0F) / 62.0F;
                     if (var22 < 0.0F) {
                        var22 = 0.0F;
                     }

                     if (var22 > 1.0F) {
                        var22 = 1.0F;
                     }

                     var8[var40] = (float)Math.pow(var22, 0.78);
                  }

                  long var41 = System.nanoTime();
                  float var43 = Math.min((float)(var41 - var11) / 1.0E9F, 0.05F);
                  var11 = var41;
                  float var44 = 1.0F - (float)Math.exp(-32.0 * var43);
                  float var45 = 1.0F - (float)Math.exp(-9.0 * var43);

                  for (int var47 = 0; var47 < 64; var47++) {
                     float var49 = var8[var47];
                     float var50 = var9[var47];
                     float var23 = var49 > var50 ? var44 : var45;
                     var50 += (var49 - var50) * var23;
                     var9[var47] = var50;
                  }

                  synchronized (BIN_LOCK) {
                     System.arraycopy(var9, 0, BINS_OUT, 0, 64);
                  }
               }
            }

            return;
         }

         available = false;
      } catch (Throwable var36) {
         return;
      } finally {
         available = false;
         running = false;
         if (var0 != null) {
            try {
               var0.stop();
               var0.close();
            } catch (Throwable var34) {
            }
         }
      }
   }

   private static float sample16(byte[] buf, int base) {
      if (base + 1 >= buf.length) {
         return 0.0F;
      }

      short var2 = (short)(buf[base] & (255) | buf[base + 1] << (8));
      return var2 / 32768.0F;
   }

   private static TargetDataLine openLoopbackLine() {
      AudioFormat var0 = new AudioFormat(44100.0F, 16, 2, true, false);
      Info var1 = new Info(TargetDataLine.class, var0);

      for (javax.sound.sampled.Mixer.Info var5 : AudioSystem.getMixerInfo()) {
         String var6 = (var5.getName() + " " + var5.getDescription()).toLowerCase(Locale.ROOT);
         boolean var7 = false;

         for (String var11 : LOOPBACK_HINTS) {
            if (var6.contains(var11)) {
               var7 = true;
               break;
            }
         }

         if (var7) {
            try {
               Mixer var13 = AudioSystem.getMixer(var5);
               if (var13.isLineSupported(var1)) {
                  TargetDataLine var14 = (TargetDataLine)var13.getLine(var1);
                  var14.open(var0, (1024) * var0.getFrameSize() * (4));
                  return var14;
               }
            } catch (Throwable var12) {
            }
         }
      }

      return null;
   }

   private static float[] hann(int n) {
      float[] var1 = new float[n];

      for (int var2 = 0; var2 < n; var2++) {
         var1[var2] = (float)(0.5 - 0.5 * Math.cos((Math.PI * 2) * var2 / (n - 1)));
      }

      return var1;
   }

   private static int[] logBinEdges(int fftSize, int sampleRate, int bins) {
      int var3 = fftSize / (2);
      double var4 = 40.0;
      double var6 = Math.min(sampleRate / 2.0, 16000.0);
      int[] var8 = new int[bins + 1];

      for (int var9 = 0; var9 <= bins; var9++) {
         double var10 = var4 * Math.pow(var6 / var4, (double)var9 / bins);
         int var12 = (int)Math.round(var10 * fftSize / sampleRate);
         if (var12 < 1) {
            var12 = 1;
         }

         if (var12 > var3) {
            var12 = var3;
         }

         var8[var9] = var12;
      }

      for (int var13 = 1; var13 <= bins; var13++) {
         if (var8[var13] <= var8[var13 - 1]) {
            var8[var13] = Math.min(var3, var8[var13 - 1] + 1);
         }
      }

      return var8;
   }

   private static void fft(float[] re, float[] im) {
      int var2 = re.length;
      int var3 = 1;
      int var4 = 0;

      while (var3 < var2) {
         int var5;
         for (var5 = var2 >> 1; (var4 & var5) != 0; var5 >>= 1) {
            var4 ^= var5;
         }

         var4 ^= var5;
         if (var3 < var4) {
            float var6 = re[var3];
            re[var3] = re[var4];
            re[var4] = var6;
            float var7 = im[var3];
            im[var3] = im[var4];
            im[var4] = var7;
         }

         var3++;
      }

      for (int var17 = 2; var17 <= var2; var17 <<= 1) {
         double var18 = (-Math.PI * 2) / var17;
         float var19 = (float)Math.cos(var18);
         float var20 = (float)Math.sin(var18);

         for (int var8 = 0; var8 < var2; var8 += var17) {
            float var9 = 1.0F;
            float var10 = 0.0F;

            for (int var11 = 0; var11 < var17 >> 1; var11++) {
               int var12 = var8 + var11;
               int var13 = var12 + (var17 >> 1);
               float var14 = var9 * re[var13] - var10 * im[var13];
               float var15 = var9 * im[var13] + var10 * re[var13];
               re[var13] = re[var12] - var14;
               im[var13] = im[var12] - var15;
               re[var12] += var14;
               im[var12] += var15;
               float var16 = var9 * var19 - var10 * var20;
               var10 = var9 * var20 + var10 * var19;
               var9 = var16;
            }
         }
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
