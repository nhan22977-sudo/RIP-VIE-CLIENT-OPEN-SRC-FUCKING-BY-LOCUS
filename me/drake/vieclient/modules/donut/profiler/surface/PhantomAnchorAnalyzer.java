package me.drake.vieclient.modules.donut.profiler.surface;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.drake.vieclient.modules.donut.chunkprobe.ChunkKey;
import me.drake.vieclient.modules.donut.profiler.EvidenceCandidate;
import me.drake.vieclient.modules.donut.profiler.FinderConfidence;

public final class PhantomAnchorAnalyzer {
   private static final int MIN_SAMPLES = 12;
   private final Map tracks = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void observe(SurfaceProfilerSample.PhantomMotionSample sample) {
      if (sample != null && Double.isFinite(sample.x()) && Double.isFinite(sample.z())) {
         if (this.tracks.size() >= (256) && !this.tracks.containsKey(sample.entityId())) {
            this.tracks.remove(this.tracks.keySet().iterator().next());
         }

         ArrayDeque var2 = this.tracks.computeIfAbsent(sample.entityId(), ignored -> new ArrayDeque());
         var2.addLast(sample);

         while (var2.size() > (48)) {
            var2.removeFirst();
         }
      }
   }

   public synchronized List candidates() {
      ArrayList var1 = new ArrayList();

      for (ArrayDeque var3 : this.tracks.values()) {
         fit(var3).ifPresent(var1::add);
      }

      return List.copyOf(var1);
   }

   private static Optional fit(ArrayDeque track) {
      if (track.size() < 12) {
         return Optional.empty();
      }

      double[][] var1 = new double[3][4];

      for (SurfaceProfilerSample.PhantomMotionSample var3 : track) {
         double var4 = var3.x();
         double var6 = var3.z();
         double var8 = -(var4 * var4 + var6 * var6);
         double[] var10 = new double[]{var4, var6, 1.0};

         for (int var11 = 0; var11 < 3; var11++) {
            for (int var12 = 0; var12 < 3; var12++) {
               var1[var11][var12] = var1[var11][var12] + var10[var11] * var10[var12];
            }

            var1[var11][3] = var1[var11][3] + var10[var11] * var8;
         }
      }

      double[] var24 = solve(var1);
      if (var24 == null) {
         return Optional.empty();
      }

      double var25 = -var24[0] * 0.5;
      double var5 = -var24[1] * 0.5;
      double var7 = var25 * var25 + var5 * var5 - var24[2];
      if (!(var7 > 0.0)) {
         return Optional.empty();
      }

      double var9 = Math.sqrt(var7);
      if (!(var9 < 8.0) && !(var9 > 48.0)) {
         ArrayList var26 = new ArrayList();
         double var27 = 0.0;

         for (SurfaceProfilerSample.PhantomMotionSample var15 : track) {
            double var16 = var15.x() - var25;
            double var18 = var15.z() - var5;
            double var20 = Math.hypot(var16, var18);
            var27 += (var20 - var9) * (var20 - var9);
            double var22 = Math.atan2(var18, var16);
            var26.add(var22 < 0.0 ? var22 + (Math.PI * 2) : var22);
         }

         var26.sort(Double::compareTo);
         double var28 = 0.0;

         for (int var30 = 1; var30 < var26.size(); var30++) {
            var28 = Math.max(var28, (Double)var26.get(var30) - (Double)var26.get(var30 - 1));
         }

         var28 = Math.max(var28, (Double)var26.getFirst() + (Math.PI * 2) - (Double)var26.getLast());
         double var31 = (Math.PI * 2) - var28;
         double var32 = Math.sqrt(var27 / track.size());
         if (!(var31 < Math.toRadians(120.0)) && !(var32 > 6.0)) {
            long var33 = ChunkKey.pack((int)Math.floor(var25) >> 4, (int)Math.floor(var5) >> 4);
            return Optional.of(
               new EvidenceCandidate(
                  var33,
                  FinderConfidence.CONFIRMED,
                  var31 / (Math.PI * 2),
                  track.size(),
                  "phantom-fitted-anchor"
               )
            );
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   private static double[] solve(double[][] matrix) {
      for (int var1 = 0; var1 < (3); var1++) {
         int var2 = var1;

         for (int var3 = var1 + 1; var3 < (3); var3++) {
            if (Math.abs(matrix[var3][var1]) > Math.abs(matrix[var2][var1])) {
               var2 = var3;
            }
         }

         if (Math.abs(matrix[var2][var1]) < 1.0E-9) {
            return null;
         }

         double[] var10 = matrix[var1];
         matrix[var1] = matrix[var2];
         matrix[var2] = var10;
         double var4 = matrix[var1][var1];

         for (int var6 = var1; var6 < (4); var6++) {
            matrix[var1][var6] = matrix[var1][var6] / var4;
         }

         for (int var11 = 0; var11 < (3); var11++) {
            if (var11 != var1) {
               double var7 = matrix[var11][var1];

               for (int var9 = var1; var9 < (4); var9++) {
                  matrix[var11][var9] = matrix[var11][var9] - var7 * matrix[var1][var9];
               }
            }
         }
      }

      double[] var10000 = new double[3];
      var10000[0] = matrix[0][3];
      var10000[1] = matrix[1][3];
      var10000[2] = matrix[2][3];
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
