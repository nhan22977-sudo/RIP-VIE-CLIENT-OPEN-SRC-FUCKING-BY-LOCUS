package me.drake.vieclient.modules.donut.profiler.surface;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.drake.vieclient.modules.donut.profiler.EvidenceCandidate;
import me.drake.vieclient.modules.donut.profiler.FinderConfidence;
import me.drake.vieclient.modules.donut.profiler.stats.RollingRobustBaseline;

public final class MobCapAnalyzer {
   private final Map baselines = new HashMap();
   private final Map suppressions = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void observe(MobCapAnalyzer.Window window) {
      if (window != null && window.validContext() && !(window.coverage() < 0.75) && window.contextBucket() != null) {
         if (this.baselines.size() >= (128) && !this.baselines.containsKey(window.contextBucket())) {
            this.baselines.remove(this.baselines.keySet().iterator().next());
         }

         RollingRobustBaseline var2 = this.baselines
            .computeIfAbsent(window.contextBucket(), ignored -> new RollingRobustBaseline(64, 8));
         if (!(window.localPlayerExposure() > 0.25)) {
            if (!var2.ready()) {
               var2.observe(window.hostileCount());
            } else {
               double var3 = var2.robustZ(window.hostileCount());
               if (var3 <= -3.5) {
                  if (this.suppressions.size() >= (2048) && !this.suppressions.containsKey(window.chunkKey())) {
                     this.suppressions.remove(this.suppressions.keySet().iterator().next());
                  }

                  this.suppressions.computeIfAbsent(window.chunkKey(), ignored -> new MobCapAnalyzer.SuppressionStats()).record(var3);
               } else {
                  var2.observe(window.hostileCount());
               }
            }
         }
      }
   }

   public synchronized List candidates() {
      return this.suppressions
         .entrySet()
         .stream()
         .filter(entry -> ((MobCapAnalyzer.SuppressionStats)entry.getValue()).windows >= (2))
         .map(
            entry -> {
               long var10002 = (Long)entry.getKey();
               FinderConfidence var10003 = ((MobCapAnalyzer.SuppressionStats)entry.getValue()).windows >= (3)
                  ? FinderConfidence.CONFIRMED
                  : FinderConfidence.POSSIBLE;
               double var10004 = Math.abs(((MobCapAnalyzer.SuppressionStats)entry.getValue()).minimumZ);
               byte[] var10006 = new byte[19];
               var10006[0] = (byte)(87);
               var10006[1] = (byte)(-11);
               var10006[2] = (byte)(126);
               var10006[3] = (byte)(-13);
               var10006[4] = (byte)(-90);
               var10006[5] = (byte)(-27);
               var10006[6] = (byte)(35);
               var10006[7] = (byte)(-49);
               var10006[8] = (byte)(33);
               var10006[9] = (byte)(-41);
               var10006[10] = (byte)(-108);
               var10006[11] = (byte)(-122);
               var10006[12] = (byte)(-17);
               var10006[13] = (byte)(57);
               var10006[14] = (byte)(8);
               var10006[15] = (byte)(-23);
               var10006[16] = (byte)(3);
               var10006[17] = (byte)(37);
               var10006[18] = (byte)(-94);
               return new EvidenceCandidate(
                  var10002, var10003, var10004, ((MobCapAnalyzer.SuppressionStats)entry.getValue()).windows, $$v_s(var10006, -3868492590039707617L)
               );
            }
         )
         .toList();
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

   private static final class SuppressionStats {
      private int windows;
      private double minimumZ;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private void record(double z) {
         this.windows++;
         this.minimumZ = Math.min(this.minimumZ, z);
      }
   }

   public record Window(long chunkKey, String contextBucket, int hostileCount, double coverage, double localPlayerExposure, boolean validContext) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
