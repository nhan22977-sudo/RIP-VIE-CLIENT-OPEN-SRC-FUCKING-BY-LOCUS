package me.drake.vieclient.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.FloatControl.Type;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

public final class Mp3Player {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private Mp3Player() {
   }

   public static Mp3Player.Handle playOnce(String resource, float volume) {
      return start(resource, volume, false);
   }

   public static Mp3Player.Handle playLoop(String resource, float volume) {
      return start(resource, volume, true);
   }

   private static Mp3Player.Handle start(String resource, float volume, boolean loop) {
      Mp3Player.Handle var3 = new Mp3Player.Handle();
      Runnable var10002 = () -> stream(resource, volume, loop, var3.stop);
      byte[] var10003 = new byte[7];
      var10003[0] = (byte)(76);
      var10003[1] = (byte)(-13);
      var10003[2] = (byte)(121);
      var10003[3] = (byte)(-13);
      var10003[4] = (byte)(39);
      var10003[5] = (byte)(68);
      var10003[6] = (byte)(-115);
      Thread var4 = new Thread(var10002, $$v_s(var10003, -7656705460812918753L));
      var4.setDaemon(true);
      var4.start();
      return var3;
   }

   private static InputStream open(String spec) {
      try {
         File var1 = new File(spec);
         if (var1.isFile()) {
            return new FileInputStream(var1);
         }
      } catch (Throwable var2) {
      }

      InputStream var3 = Mp3Player.class.getClassLoader().getResourceAsStream(spec);
      if (var3 == null) {
         var3 = Thread.currentThread().getContextClassLoader().getResourceAsStream(spec);
      }

      return var3;
   }

   private static void stream(String resource, float volume, boolean loop, AtomicBoolean stop) {
      do {
         SourceDataLine var4 = null;
         Bitstream var5 = null;

         try {
            InputStream var6 = open(resource);
            if (var6 == null) {
               return;
            }

            var5 = new Bitstream(var6);
            Decoder var7 = new Decoder();

            Header var8;
            while (!stop.get() && (var8 = var5.readFrame()) != null) {
               SampleBuffer var9 = (SampleBuffer)var7.decodeFrame(var8, var5);
               if (var4 == null) {
                  AudioFormat var10 = new AudioFormat(var9.getSampleFrequency(), 16, var9.getChannelCount(), true, false);
                  var4 = AudioSystem.getSourceDataLine(var10);
                  var4.open(var10);
                  setVolume(var4, volume);
                  var4.start();
               }

               short[] var30 = var9.getBuffer();
               int var11 = var9.getBufferLength();
               byte[] var12 = new byte[var11 * 2];

               for (int var13 = 0; var13 < var11; var13++) {
                  var12[var13 * 2] = (byte)(var30[var13] & 0xFF);
                  var12[var13 * 2 + 1] = (byte)(var30[var13] >> 8 & 0xFF);
               }

               var4.write(var12, 0, var12.length);
               var5.closeFrame();
            }
         } catch (Throwable var28) {
         } finally {
            try {
               if (var4 != null) {
                  if (!stop.get()) {
                     var4.drain();
                  }

                  var4.stop();
                  var4.close();
               }
            } catch (Throwable var27) {
            }

            try {
               if (var5 != null) {
                  var5.close();
               }
            } catch (Throwable var26) {
            }
         }
      } while (loop && !stop.get());
   }

   private static void setVolume(SourceDataLine line, float vol) {
      try {
         if (line.isControlSupported(Type.MASTER_GAIN)) {
            FloatControl var2 = (FloatControl)line.getControl(Type.MASTER_GAIN);
            float var3 = vol <= 1.0E-4F ? var2.getMinimum() : (float)(20.0 * Math.log10(vol));
            var2.setValue(Math.max(var2.getMinimum(), Math.min(var2.getMaximum(), var3)));
         }
      } catch (Throwable var4) {
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

   public static final class Handle {
      final AtomicBoolean stop = new AtomicBoolean(false);
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public void stop() {
         this.stop.set(true);
      }
   }
}
