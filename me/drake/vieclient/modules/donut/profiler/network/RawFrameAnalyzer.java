package me.drake.vieclient.modules.donut.profiler.network;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.drake.vieclient.modules.donut.profiler.EvidenceCandidate;
import me.drake.vieclient.modules.donut.profiler.FinderConfidence;
import me.drake.vieclient.modules.donut.profiler.stats.RollingRobustBaseline;

public final class RawFrameAnalyzer {
   private final Map buckets = new HashMap();
   private final Map outliers = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void observe(RawFrameSample sample) {
      if (sample.compressedBytes() > 0 && sample.decodedBytes() > 0) {
         String var2 = sample.packetClass() + "|" + sample.profileBucket();
         if (this.buckets.size() >= (128) && !this.buckets.containsKey(var2)) {
            this.buckets.remove(this.buckets.keySet().iterator().next());
         }

         RawFrameAnalyzer.Bucket var3 = this.buckets.computeIfAbsent(var2, ignored -> new RawFrameAnalyzer.Bucket());
         if (!var3.ready()) {
            var3.observe(sample);
         } else {
            double var4 = var3.sizes.robustZ(sample.compressedBytes());
            double var6 = var3.ratios.robustZ(sample.compressionRatio());
            double var8 = Math.max(var4, var6);
            if (!(var4 >= 4.0) && !(var6 >= 4.5)) {
               var3.observe(sample);
            } else {
               long var10 = sampleKey(sample);
               if (this.outliers.size() >= (2048) && !this.outliers.containsKey(var10)) {
                  this.outliers.remove(this.outliers.keySet().iterator().next());
               }

               this.outliers.computeIfAbsent(var10, ignored -> new RawFrameAnalyzer.FrameStats()).record(var8);
            }
         }
      }
   }

   public synchronized List candidates() {
      return this.outliers
         .entrySet()
         .stream()
         .filter(entry -> ((RawFrameAnalyzer.FrameStats)entry.getValue()).count >= (3))
         .map(
            entry -> {
               long var10002 = (Long)entry.getKey();
               FinderConfidence var10003 = ((RawFrameAnalyzer.FrameStats)entry.getValue()).count >= (5)
                  ? FinderConfidence.CONFIRMED
                  : FinderConfidence.POSSIBLE;
               byte[] var10006 = new byte[17];
               var10006[0] = (byte)(72);
               var10006[1] = (byte)(-5);
               var10006[2] = (byte)(107);
               var10006[3] = (byte)(-13);
               var10006[4] = (byte)(-79);
               var10006[5] = (byte)(-73);
               var10006[6] = (byte)(94);
               var10006[7] = (byte)(98);
               var10006[8] = (byte)(55);
               var10006[9] = (byte)(-113);
               var10006[10] = (byte)(-106);
               var10006[11] = (byte)(-109);
               var10006[12] = (byte)(-4);
               var10006[13] = (byte)(116);
               var10006[14] = (byte)(115);
               var10006[15] = (byte)(2);
               var10006[16] = (byte)(15);
               return new EvidenceCandidate(
                  var10002,
                  var10003,
                  ((RawFrameAnalyzer.FrameStats)entry.getValue()).maximumScore,
                  ((RawFrameAnalyzer.FrameStats)entry.getValue()).count,
                  $$v_s(var10006, 2827164826591285279L)
               );
            }
         )
         .toList();
   }

   private static long sampleKey(RawFrameSample sample) {
      return sample.chunkX() & 4294967295L | (sample.chunkZ() & 4294967295L) << (32);
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

   private static final class Bucket {
      private final RollingRobustBaseline sizes = new RollingRobustBaseline(128, 32);
      private final RollingRobustBaseline ratios = new RollingRobustBaseline(128, 32);
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private void observe(RawFrameSample sample) {
         this.sizes.observe(sample.compressedBytes());
         this.ratios.observe(sample.compressionRatio());
      }

      private boolean ready() {
         return this.sizes.ready() && this.ratios.ready();
      }
   }

   private static final class FrameStats {
      private int count;
      private double maximumScore;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private void record(double score) {
         this.count++;
         this.maximumScore = Math.max(this.maximumScore, score);
      }
   }
}
