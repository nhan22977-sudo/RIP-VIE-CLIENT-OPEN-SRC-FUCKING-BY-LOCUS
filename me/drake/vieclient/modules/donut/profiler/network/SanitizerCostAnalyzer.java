package me.drake.vieclient.modules.donut.profiler.network;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.drake.vieclient.modules.donut.profiler.EvidenceCandidate;
import me.drake.vieclient.modules.donut.profiler.FinderConfidence;
import me.drake.vieclient.modules.donut.profiler.stats.RollingRobustBaseline;
import me.drake.vieclient.modules.donut.profiler.stats.SpatialCluster;

public final class SanitizerCostAnalyzer {
   private final Map batches = new HashMap();
   private final Map costs = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void startBatch(int batchId) {
      if (this.batches.size() >= (32)) {
         this.batches.remove(this.batches.keySet().iterator().next());
      }

      this.batches.put(batchId, new SanitizerCostAnalyzer.Batch());
   }

   public synchronized void observe(int batchId, long chunkKey, long offsetNanos) {
      SanitizerCostAnalyzer.Batch var6 = (SanitizerCostAnalyzer.Batch)this.batches.get(batchId);
      if (var6 != null) {
         var6.observe(chunkKey, Math.max(0L, offsetNanos));
      }
   }

   public synchronized void endBatch(int batchId) {
      SanitizerCostAnalyzer.Batch var2 = (SanitizerCostAnalyzer.Batch)this.batches.remove(batchId);
      if (var2 != null && var2.gaps.size() >= (4) && !var2.stalled) {
         RollingRobustBaseline var3 = new RollingRobustBaseline(var2.gaps.size(), Math.min(4, var2.gaps.size()));
         var2.gaps.forEach(gap -> var3.observe(gap.gapNanos));

         for (SanitizerCostAnalyzer.Gap var5 : var2.gaps) {
            double var6 = var3.robustZ(var5.gapNanos);
            if (!(var6 < 4.0)) {
               if (this.costs.size() >= (2048) && !this.costs.containsKey(var5.chunkKey)) {
                  this.costs.remove(this.costs.keySet().iterator().next());
               }

               this.costs.computeIfAbsent(var5.chunkKey, ignored -> new SanitizerCostAnalyzer.CostStats()).record(var6);
            }
         }
      }
   }

   public synchronized Optional evidence(long chunkKey) {
      return this.candidates().stream().filter(candidate -> candidate.chunkKey() == chunkKey).findFirst();
   }

   public synchronized List candidates() {
      Set var1 = this.costs
         .entrySet()
         .stream()
         .filter(entry -> ((SanitizerCostAnalyzer.CostStats)entry.getValue()).count >= (2))
         .map(Entry::getKey)
         .collect(Collectors.toSet());
      HashSet var2 = new HashSet();
      SpatialCluster.find(var1, 4).forEach(var2::addAll);
      return var2.stream().map(key -> candidate(key, (SanitizerCostAnalyzer.CostStats)this.costs.get(key))).toList();
   }

   private static EvidenceCandidate candidate(long key, SanitizerCostAnalyzer.CostStats stats) {
      FinderConfidence var10003 = stats.count >= (3) ? FinderConfidence.CONFIRMED : FinderConfidence.POSSIBLE;
      byte[] var10006 = new byte[23];
      var10006[0] = (byte)(72);
      var10006[1] = (byte)(-1);
      var10006[2] = (byte)(112);
      var10006[3] = (byte)(-65);
      var10006[4] = (byte)(-28);
      var10006[5] = (byte)(-39);
      var10006[6] = (byte)(17);
      var10006[7] = (byte)(84);
      var10006[8] = (byte)(127);
      var10006[9] = (byte)(-47);
      var10006[10] = (byte)(-123);
      var10006[11] = (byte)(-104);
      var10006[12] = (byte)(-95);
      var10006[13] = (byte)(28);
      var10006[14] = (byte)(38);
      var10006[15] = (byte)(51);
      var10006[16] = (byte)(15);
      var10006[17] = (byte)(56);
      var10006[18] = (byte)(-31);
      var10006[19] = (byte)(-51);
      var10006[20] = (byte)(79);
      var10006[21] = (byte)(51);
      var10006[22] = (byte)(67);
      return new EvidenceCandidate(key, var10003, stats.maximumZ, stats.count, $$v_s(var10006, 1829708472241082399L));
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

   private static final class Batch {
      private final List gaps = new ArrayList();
      private long previousOffset = -1L;
      private boolean stalled;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private void observe(long chunkKey, long offsetNanos) {
         if (this.previousOffset >= 0L && offsetNanos >= this.previousOffset) {
            long var5 = offsetNanos - this.previousOffset;
            if (var5 > 750000000L) {
               this.stalled = true;
            }

            this.gaps.add(new SanitizerCostAnalyzer.Gap(chunkKey, var5));
         }

         this.previousOffset = offsetNanos;
      }
   }

   private static final class CostStats {
      private int count;
      private double maximumZ;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private void record(double z) {
         this.count++;
         this.maximumZ = Math.max(this.maximumZ, z);
      }
   }

   private record Gap(long chunkKey, long gapNanos) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
