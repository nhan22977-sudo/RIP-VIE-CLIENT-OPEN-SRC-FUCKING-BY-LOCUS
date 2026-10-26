package me.drake.vieclient.modules.donut.profiler.network;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import me.drake.vieclient.modules.donut.profiler.EvidenceCandidate;
import me.drake.vieclient.modules.donut.profiler.FinderConfidence;
import me.drake.vieclient.modules.donut.profiler.stats.RollingRobustBaseline;

public final class RegionLatencyAnalyzer {
   private static final double OUTLIER_Z = 3.5;
   private final RollingRobustBaseline globalResiduals = new RollingRobustBaseline(128, 20);
   private final Map localOutliers = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void observe(RegionLatencySample sample) {
      if (sample != null && sample.roundTripMs() >= 0L && Double.isFinite(sample.connectionBaselineMs())) {
         double var2 = sample.roundTripMs() - sample.connectionBaselineMs();
         if (!this.globalResiduals.ready()) {
            this.globalResiduals.observe(var2);
         } else {
            double var4 = this.globalResiduals.robustZ(var2);
            if (var4 >= 3.5) {
               if (this.localOutliers.size() >= (2048) && !this.localOutliers.containsKey(sample.chunkKey())) {
                  this.localOutliers.remove(this.localOutliers.keySet().iterator().next());
               }

               this.localOutliers.computeIfAbsent(sample.chunkKey(), ignored -> new RegionLatencyAnalyzer.LocalStats()).record(var4);
            } else {
               this.globalResiduals.observe(var2);
            }
         }
      }
   }

   public synchronized Optional evidence(long chunkKey) {
      RegionLatencyAnalyzer.LocalStats var3 = (RegionLatencyAnalyzer.LocalStats)this.localOutliers.get(chunkKey);
      if (var3 != null && var3.count >= (3)) {
         FinderConfidence var10003 = var3.count >= (4) ? FinderConfidence.CONFIRMED : FinderConfidence.POSSIBLE;
         byte[] var10006 = new byte[27];
         var10006[0] = (byte)(72);
         var10006[1] = (byte)(-1);
         var10006[2] = (byte)(123);
         var10006[3] = (byte)(-73);
         var10006[4] = (byte)(-61);
         var10006[5] = (byte)(-15);
         var10006[6] = (byte)(-32);
         var10006[7] = (byte)(85);
         var10006[8] = (byte)('ﺢ' ^ 'ﻝ');
         var10006[9] = (byte)(-50);
         var10006[10] = (byte)(-123);
         var10006[11] = (byte)(-126);
         var10006[12] = (byte)(-111);
         var10006[13] = (byte)(41);
         var10006[14] = (byte)(-54);
         var10006[15] = (byte)(56);
         var10006[16] = (byte)(71);
         var10006[17] = (byte)(46);
         var10006[18] = (byte)(-91);
         var10006[19] = (byte)(-40);
         var10006[20] = (byte)(121);
         var10006[21] = (byte)(29);
         var10006[22] = (byte)(-74);
         var10006[23] = (byte)(12);
         var10006[24] = (byte)(-20);
         var10006[25] = (byte)(113);
         var10006[26] = (byte)(-15);
         return Optional.of(new EvidenceCandidate(chunkKey, var10003, var3.maximumZ, var3.count, $$v_s(var10006, 1261642405316382751L)));
      } else {
         return Optional.empty();
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

   private static final class LocalStats {
      private int count;
      private double maximumZ;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private void record(double z) {
         this.count++;
         this.maximumZ = Math.max(this.maximumZ, z);
      }
   }
}
