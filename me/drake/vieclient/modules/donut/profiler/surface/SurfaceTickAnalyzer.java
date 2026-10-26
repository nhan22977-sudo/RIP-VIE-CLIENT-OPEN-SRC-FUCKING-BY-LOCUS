package me.drake.vieclient.modules.donut.profiler.surface;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.drake.vieclient.modules.donut.profiler.EvidenceCandidate;
import me.drake.vieclient.modules.donut.profiler.FinderConfidence;
import me.drake.vieclient.modules.donut.profiler.stats.RollingRobustBaseline;

public final class SurfaceTickAnalyzer {
   private static final int MIN_COMPARABLE_BLOCKS = 128;
   private static final double MIN_Z = 3.5;
   private final Map baselines = new HashMap();
   private final Map outliers = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void observe(SurfaceProfilerSample.ChunkSurfaceSample sample) {
      if (sample != null && sample.comparableBlocks() >= (128) && sample.contextBucket() != null && !sample.contextBucket().isBlank()) {
         if (this.baselines.size() >= (128) && !this.baselines.containsKey(sample.contextBucket())) {
            this.baselines.remove(this.baselines.keySet().iterator().next());
         }

         SurfaceTickAnalyzer.ContextBaseline var2 = this.baselines
            .computeIfAbsent(sample.contextBucket(), ignored -> new SurfaceTickAnalyzer.ContextBaseline());
         double var3 = ratio(sample.firstFamilyAged(), sample.comparableBlocks());
         double var5 = ratio(sample.secondFamilyAged(), sample.comparableBlocks());
         double var7 = ratio(sample.thirdFamilyAged(), sample.comparableBlocks());
         if (!var2.ready()) {
            var2.observe(var3, var5, var7);
         } else {
            double var9 = var2.first.robustZ(var3);
            double var11 = var2.second.robustZ(var5);
            double var13 = var2.third.robustZ(var7);
            double var15 = Math.min(var9, Math.min(var11, var13));
            if (var9 >= 3.5 && var11 >= 3.5 && var13 >= 3.5) {
               if (this.outliers.size() >= (2048) && !this.outliers.containsKey(sample.chunkKey())) {
                  this.outliers.remove(this.outliers.keySet().iterator().next());
               }

               this.outliers.computeIfAbsent(sample.chunkKey(), ignored -> new SurfaceTickAnalyzer.TickStats()).record(var15);
            } else {
               var2.observe(var3, var5, var7);
            }
         }
      }
   }

   public synchronized List candidates() {
      return this.outliers
         .entrySet()
         .stream()
         .map(
            entry -> {
               long var10002 = (Long)entry.getKey();
               FinderConfidence var10003 = ((SurfaceTickAnalyzer.TickStats)entry.getValue()).passes >= (2)
                  ? FinderConfidence.CONFIRMED
                  : FinderConfidence.POSSIBLE;
               byte[] var10006 = new byte[29];
               var10006[0] = (byte)(73);
               var10006[1] = (byte)(-17);
               var10006[2] = (byte)(110);
               var10006[3] = (byte)(-72);
               var10006[4] = (byte)(12);
               var10006[5] = (byte)(77);
               var10006[6] = (byte)(-85);
               var10006[7] = (byte)(57);
               var10006[8] = (byte)(63);
               var10006[9] = (byte)(-41);
               var10006[10] = (byte)(-120);
               var10006[11] = (byte)(-126);
               var10006[12] = (byte)(92);
               var10006[13] = (byte)(-37);
               var10006[14] = (byte)(-128);
               var10006[15] = (byte)(13);
               var10006[16] = (byte)(7);
               var10006[17] = (byte)(35);
               var10006[18] = (byte)(-96);
               var10006[19] = (byte)(-41);
               var10006[20] = (byte)(-16);
               var10006[21] = (byte)(-86);
               var10006[22] = (byte)(-9);
               var10006[23] = (byte)(39);
               var10006[24] = (byte)(-23);
               var10006[25] = (byte)(63);
               var10006[26] = (byte)(-11);
               var10006[27] = (byte)(33);
               var10006[28] = (byte)(-128);
               return new EvidenceCandidate(
                  var10002,
                  var10003,
                  ((SurfaceTickAnalyzer.TickStats)entry.getValue()).maximumScore,
                  ((SurfaceTickAnalyzer.TickStats)entry.getValue()).passes,
                  $$v_s(var10006, 4381422807074197535L)
               );
            }
         )
         .toList();
   }

   private static double ratio(int count, int total) {
      return (double)Math.max(0, count) / Math.max(1, total);
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

   private static final class ContextBaseline {
      private final RollingRobustBaseline first = new RollingRobustBaseline(64, 6);
      private final RollingRobustBaseline second = new RollingRobustBaseline(64, 6);
      private final RollingRobustBaseline third = new RollingRobustBaseline(64, 6);
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private void observe(double a, double b, double c) {
         this.first.observe(a);
         this.second.observe(b);
         this.third.observe(c);
      }

      private boolean ready() {
         return this.first.ready() && this.second.ready() && this.third.ready();
      }
   }

   private static final class TickStats {
      private int passes;
      private double maximumScore;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private void record(double score) {
         this.passes++;
         this.maximumScore = Math.max(this.maximumScore, score);
      }
   }
}
