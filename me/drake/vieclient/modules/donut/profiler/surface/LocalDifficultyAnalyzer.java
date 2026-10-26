package me.drake.vieclient.modules.donut.profiler.surface;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.drake.vieclient.modules.donut.chunkprobe.ChunkKey;
import me.drake.vieclient.modules.donut.profiler.EvidenceCandidate;
import me.drake.vieclient.modules.donut.profiler.FinderConfidence;
import me.drake.vieclient.modules.donut.profiler.stats.RollingRobustBaseline;

public final class LocalDifficultyAnalyzer {
   private static final int MAX_CONTEXT_BUCKETS = 128;
   private final Map mobs = new HashMap();
   private final Map baselines = new HashMap();
   private final Map controls = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void observe(SurfaceProfilerSample.MobFeatureSample sample) {
      if (sample != null && sample.y() >= 0 && !sample.customNamed() && !sample.playerEquipped()) {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(84);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-83);
         var10000[4] = (byte)(-75);
         var10000[5] = (byte)(117);
         var10000[6] = (byte)(116);
         if ($$v_s(var10000, -2015978242137927652L).equals(sample.spawnGroup()) && sample.contextBucket() != null && !sample.contextBucket().isBlank()) {
            int var2 = difficultyScore(sample);
            String var3 = sample.contextBucket() + "|" + sample.entityType();
            if (this.baselines.size() >= (128) && !this.baselines.containsKey(var3)) {
               String var4 = (String)this.baselines.keySet().iterator().next();
               this.baselines.remove(var4);
               this.controls.remove(var4);
            }

            RollingRobustBaseline var8 = this.baselines.computeIfAbsent(var3, ignored -> new RollingRobustBaseline(64, 6));
            if (!var8.ready()) {
               var8.observe(var2);
               this.recordControl(var3, sample);
               return;
            }

            if (var8.robustZ(var2) < 3.5) {
               var8.observe(var2);
               this.recordControl(var3, sample);
               return;
            }

            long var5 = this.controls
               .getOrDefault(var3, new ArrayDeque<>())
               .stream()
               .filter(control -> nearby(control.chunkKey(), sample.chunkKey()))
               .map(SurfaceProfilerSample.MobFeatureSample::chunkKey)
               .distinct()
               .count();
            if (var5 < 2L) {
               return;
            }

            this.mobs.put(sample.entityId(), sample);
            if (this.mobs.size() > (256)) {
               Integer var7 = this.mobs
                  .entrySet()
                  .stream()
                  .min(Comparator.comparingLong(entry -> ((SurfaceProfilerSample.MobFeatureSample)entry.getValue()).capturedNanos()))
                  .map(Entry::getKey)
                  .orElse(null);
               if (var7 != null) {
                  this.mobs.remove(var7);
               }
            }

            return;
         }
      }
   }

   public synchronized List candidates() {
      HashMap var1 = new HashMap();
      this.mobs.values().forEach(mob -> var1.computeIfAbsent(mob.contextBucket(), ignored -> new ArrayList()).add(mob));
      ArrayList var2 = new ArrayList();

      for (List var4 : var1.values()) {
         var2.addAll(clusterCandidates(var4));
      }

      return List.copyOf(var2);
   }

   private static List clusterCandidates(List samples) {
      HashMap var1 = new HashMap();
      samples.forEach(sample -> var1.computeIfAbsent(sample.chunkKey(), ignored -> new ArrayList()).add(sample));
      HashSet var2 = new HashSet(var1.keySet());
      ArrayList var3 = new ArrayList();

      while (!var2.isEmpty()) {
         long var4 = (Long)var2.iterator().next();
         HashSet var6 = new HashSet();
         ArrayDeque var7 = new ArrayDeque();
         var7.add(var4);
         var2.remove(var4);

         while (!var7.isEmpty()) {
            long var8 = (Long)var7.removeFirst();
            var6.add(var8);
            int var10 = ChunkKey.unpackX(var8);
            int var11 = ChunkKey.unpackZ(var8);

            for (int var12 = -1; var12 <= 1; var12++) {
               for (int var13 = -1; var13 <= 1; var13++) {
                  long var14 = ChunkKey.pack(var10 + var12, var11 + var13);
                  if (var2.remove(var14)) {
                     var7.addLast(var14);
                  }
               }
            }
         }

         List var20 = var6.stream().flatMap(key -> ((List)var1.get(key)).stream()).toList();
         long var9 = var20.stream().map(SurfaceProfilerSample.MobFeatureSample::entityType).distinct().count();
         int var21 = var6.stream().mapToInt(ChunkKey::unpackX).min().orElse(0);
         int var22 = var6.stream().mapToInt(ChunkKey::unpackX).max().orElse(0);
         int var23 = var6.stream().mapToInt(ChunkKey::unpackZ).min().orElse(0);
         int var24 = var6.stream().mapToInt(ChunkKey::unpackZ).max().orElse(0);
         if (var20.size() >= 8 && var9 >= 2L && var22 - var21 <= 2 && var24 - var23 <= 2) {
            double var15 = var20.stream().mapToInt(LocalDifficultyAnalyzer::difficultyScore).average().orElse(0.0);

            for (long var18 : var6) {
               var3.add(
                  new EvidenceCandidate(
                     var18,
                     FinderConfidence.CONFIRMED,
                     var15,
                     var20.size(),
                     "mixed-local-difficulty-cluster"
                  )
               );
            }
         }
      }

      return var3;
   }

   private static int difficultyScore(SurfaceProfilerSample.MobFeatureSample sample) {
      return sample.armorPieces() + sample.enchantedPieces() * (2) + sample.effectCount() * (2);
   }

   private void recordControl(String bucket, SurfaceProfilerSample.MobFeatureSample sample) {
      ArrayDeque var3 = this.controls.computeIfAbsent(bucket, ignored -> new ArrayDeque());
      var3.addLast(sample);

      while (var3.size() > (64)) {
         var3.removeFirst();
      }
   }

   private static boolean nearby(long a, long b) {
      return Math.abs(ChunkKey.unpackX(a) - ChunkKey.unpackX(b)) <= (3)
         && Math.abs(ChunkKey.unpackZ(a) - ChunkKey.unpackZ(b)) <= (3);
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
