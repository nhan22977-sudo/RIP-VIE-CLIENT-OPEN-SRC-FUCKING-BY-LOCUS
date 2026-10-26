package me.drake.vieclient.modules.donut.chunkprobe;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.LongPredicate;

public final class OverlapBatchTracker {
   private static final int REAL_RADIUS = 4;
   private static final int MIN_SKIPPED_CLOSER = 3;
   private static final int MIN_CLUSTER_SIZE = 4;
   private static final int MAX_RECENT_TRAIL = 128;
   private static final int MAX_RECEIVED = 8192;
   private static final int MAX_HOT_SAMPLES = 512;
   private static final int MAX_BATCH_HISTORY = 32;
   private static final int MAX_GENERATION_AGE = 8;
   private static final int SPAWN_EXCLUSION_RADIUS = 32;
   private static final long MAX_SAMPLE_AGE_NANOS = TimeUnit.MILLISECONDS.toNanos(120000L);
   private final Map pending = new HashMap();
   private final LinkedHashMap received = new LinkedHashMap();
   private final LinkedHashMap hotSamples = new LinkedHashMap();
   private final LinkedHashMap evidence = new LinkedHashMap();
   private final Deque recentTrail = new ArrayDeque();
   private final Deque batchCosts = new ArrayDeque();
   private final List currentBatch = new ArrayList();
   private final Set frontierWindow = new HashSet();
   private int centerX;
   private int centerZ;
   private int radius;
   private int generation;
   private boolean batchOpen;
   private long batchStartNanos;
   private int spawnX;
   private int spawnZ;
   private boolean spawnValid;
   private boolean frontierInitialized;
   private boolean evidenceChanged;
   private LongPredicate lastResidualEvidence = key -> false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void updateFrontier(int centerX, int centerZ, int radius) {
      int var4 = Math.max(1, Math.min(64, radiusx));
      if (!this.frontierInitialized || this.centerX != centerXx || this.centerZ != centerZx || this.radius != var4) {
         this.centerX = centerXx;
         this.centerZ = centerZx;
         this.radius = var4;
         this.generation++;
         HashSet var5 = new HashSet();

         for (int var6 = -this.radius; var6 <= this.radius; var6++) {
            for (int var7 = -this.radius; var7 <= this.radius; var7++) {
               long var8 = ChunkKey.pack(centerXx + var6, centerZx + var7);
               var5.add(var8);
               if (this.frontierInitialized && !this.frontierWindow.contains(var8) && !this.received.containsKey(var8)) {
                  this.pending.put(var8, new OverlapBatchTracker.PendingChunk(var6 * var6 + var7 * var7));
               }
            }
         }

         this.pending.keySet().removeIf(key -> !var5.contains(key));
         this.pending
            .replaceAll(
               (key, ignored) -> new OverlapBatchTracker.PendingChunk(distanceSquared(ChunkKey.unpackX(key), ChunkKey.unpackZ(key), centerXx, centerZx))
            );
         this.frontierWindow.clear();
         this.frontierWindow.addAll(var5);
         this.frontierInitialized = true;
         this.hotSamples.entrySet().removeIf(entry -> this.generation - ((OverlapBatchTracker.HotSample)entry.getValue()).generation() > (8));
         this.rebuildEvidence(this.lastResidualEvidence);
      }
   }

   public void setSpawnChunk(int chunkX, int chunkZ, boolean valid) {
      if (this.spawnX != chunkX || this.spawnZ != chunkZ || this.spawnValid != valid) {
         this.spawnX = chunkX;
         this.spawnZ = chunkZ;
         this.spawnValid = valid;
         this.rebuildEvidence(this.lastResidualEvidence);
      }
   }

   public void recordPlayerChunk(int chunkX, int chunkZ) {
      this.recentTrail.addLast(ChunkKey.pack(chunkX, chunkZ));

      while (this.recentTrail.size() > (128)) {
         this.recentTrail.removeFirst();
      }
   }

   public void startBatch(long nanos) {
      this.batchOpen = true;
      this.batchStartNanos = Math.max(0L, nanos);
      this.currentBatch.clear();
   }

   public void recordChunk(int chunkX, int chunkZ, int payloadBytes, long nanos) {
      long var6 = ChunkKey.pack(chunkX, chunkZ);
      OverlapBatchTracker.PendingChunk var8 = (OverlapBatchTracker.PendingChunk)this.pending.get(var6);
      int var9 = distanceSquared(chunkX, chunkZ, this.centerX, this.centerZ);
      int var10 = var8 == null ? 0 : this.countSkippedCloser(var9, var6);
      this.pending.remove(var6);
      this.received.put(var6, Boolean.TRUE);
      trimMap(this.received, 8192);
      if (this.batchOpen) {
         int var11 = Math.max(Math.abs(chunkX - this.centerX), Math.abs(chunkZ - this.centerZ));
         boolean var12 = var11 > (4) && var8 != null && var10 >= (3);
         this.currentBatch.add(new OverlapBatchTracker.BatchChunk(var6, Math.max(0, payloadBytes), Math.max(0L, nanos), var10, var12));
      }
   }

   public void finishBatch(int declaredSize, long nanos, LongPredicate residualEvidence) {
      if (this.batchOpen) {
         this.lastResidualEvidence = residualEvidence;
         this.batchOpen = false;
         if (declaredSize == this.currentBatch.size() && declaredSize > 0) {
            long var5 = Math.max(0L, nanos - this.batchStartNanos);
            long var7 = this.currentBatch.stream().mapToLong(OverlapBatchTracker.BatchChunk::payloadBytes).sum();
            double var9 = (double)var5 / Math.max(1L, var7);
            boolean var11 = this.isExpensive(var9);
            this.batchCosts.addLast(var9);

            while (this.batchCosts.size() > (32)) {
               this.batchCosts.removeFirst();
            }

            for (OverlapBatchTracker.BatchChunk var13 : this.currentBatch) {
               if (var13.hot()) {
                  int var14 = ChunkKey.unpackX(var13.chunkKey());
                  int var15 = ChunkKey.unpackZ(var13.chunkKey());
                  if (!this.spawnValid || chebyshev(var14, var15, this.spawnX, this.spawnZ) > (32)) {
                     this.hotSamples
                        .put(var13.chunkKey(), new OverlapBatchTracker.HotSample(var13.skippedCloser(), var11, this.generation, var13.receivedNanos()));
                     trimMap(this.hotSamples, 512);
                  }
               }
            }

            this.currentBatch.clear();
            this.rebuildEvidence(residualEvidence);
         } else {
            this.currentBatch.clear();
         }
      }
   }

   public Map evidence() {
      return Map.copyOf(this.evidence);
   }

   public boolean consumeEvidenceChanged() {
      boolean var1 = this.evidenceChanged;
      this.evidenceChanged = false;
      return var1;
   }

   public void remove(long chunkKey) {
      this.pending.remove(chunkKey);
      this.received.remove(chunkKey);
      this.hotSamples.remove(chunkKey);
      this.rebuildEvidence(this.lastResidualEvidence);
   }

   public void pruneOutside(int centerX, int centerZ, int radius) {
      int var4 = Math.max(0, radiusx);
      boolean var5 = this.hotSamples.keySet().removeIf(key -> chebyshev(ChunkKey.unpackX(key), ChunkKey.unpackZ(key), centerXx, centerZx) > var4);
      if (var5) {
         this.rebuildEvidence(this.lastResidualEvidence);
      }
   }

   public void pruneExpired(long nowNanos) {
      long var3 = Math.max(0L, nowNanos);
      boolean var5 = this.hotSamples
         .entrySet()
         .removeIf(entry -> var3 - ((OverlapBatchTracker.HotSample)entry.getValue()).receivedNanos() > MAX_SAMPLE_AGE_NANOS);
      if (var5) {
         this.rebuildEvidence(this.lastResidualEvidence);
      }
   }

   public void clear() {
      this.pending.clear();
      this.received.clear();
      this.hotSamples.clear();
      this.evidence.clear();
      this.recentTrail.clear();
      this.batchCosts.clear();
      this.currentBatch.clear();
      this.frontierWindow.clear();
      this.batchOpen = false;
      this.generation = 0;
      this.spawnValid = false;
      this.frontierInitialized = false;
      this.evidenceChanged = false;
      this.lastResidualEvidence = key -> false;
   }

   private int countSkippedCloser(int distanceSquared, long currentKey) {
      int var4 = 0;

      for (Entry var6 : this.pending.entrySet()) {
         if ((Long)var6.getKey() != currentKey && ((OverlapBatchTracker.PendingChunk)var6.getValue()).distanceSquared() < distanceSquared) {
            var4++;
         }
      }

      return var4;
   }

   private boolean isExpensive(double cost) {
      if (this.batchCosts.size() < (8)) {
         return false;
      }

      List var3 = this.batchCosts.stream().sorted().toList();
      double var4 = (Double)var3.get(var3.size() / (2));
      return var4 > 0.0 && cost > var4 * 3.0;
   }

   private void rebuildEvidence(LongPredicate residualEvidence) {
      Map var2 = Map.copyOf(this.evidence);
      this.evidence.clear();
      HashSet var3 = new HashSet();

      for (long var5 : this.hotSamples.keySet()) {
         int var7 = ChunkKey.unpackX(var5);
         int var8 = ChunkKey.unpackZ(var5);
         if (!this.spawnValid || chebyshev(var7, var8, this.spawnX, this.spawnZ) > (32)) {
            var3.add(var5);
         }
      }

      while (!var3.isEmpty()) {
         long var17 = (Long)var3.iterator().next();
         List var6 = this.collectCluster(var17, var3);
         OverlapBatchTracker.Bounds var18 = bounds(var6);
         double var19 = var18.area() == 0 ? 0.0 : (double)var6.size() / var18.area();
         if (var6.size() >= (4) && !(var19 < 0.5) && !(var18.aspectRatio() > 2.5) && !this.overlapsRecentTrail(var6)) {
            boolean var10 = var6.stream().map(this.hotSamples::get).anyMatch(OverlapBatchTracker.HotSample::expensiveBatch);
            boolean var11 = var6.stream().mapToLong(Long::longValue).anyMatch(residualEvidence);
            Confidence var12 = !var10 && !var11 ? Confidence.POSSIBLE : Confidence.HIGH;

            for (long var14 : var6) {
               OverlapBatchTracker.HotSample var16 = (OverlapBatchTracker.HotSample)this.hotSamples.get(var14);
               this.evidence.put(var14, new OverlapBatchTracker.OverlapEvidence(var14, var12, var16.skippedCloser(), var6.size(), var16.expensiveBatch()));
            }
         }
      }

      trimMap(this.evidence, 512);
      if (!this.evidence.equals(var2)) {
         this.evidenceChanged = true;
      }
   }

   private List collectCluster(long seed, Set unvisited) {
      ArrayList var4 = new ArrayList();
      ArrayDeque var5 = new ArrayDeque();
      unvisited.remove(seed);
      var5.add(seed);

      while (!var5.isEmpty()) {
         long var6 = (Long)var5.removeFirst();
         var4.add(var6);
         int var8 = ChunkKey.unpackX(var6);
         int var9 = ChunkKey.unpackZ(var6);

         for (long var12 : unvisited.stream().filter(key -> chebyshev(var8, var9, ChunkKey.unpackX(key), ChunkKey.unpackZ(key)) <= 1).toList()) {
            unvisited.remove(var12);
            var5.addLast(var12);
         }
      }

      return var4;
   }

   private boolean overlapsRecentTrail(List cluster) {
      int var2 = 0;

      for (long var4 : cluster) {
         int var6 = ChunkKey.unpackX(var4);
         int var7 = ChunkKey.unpackZ(var4);
         boolean var8 = this.recentTrail
            .stream()
            .anyMatch(trail -> chebyshev(var6, var7, ChunkKey.unpackX(trail), ChunkKey.unpackZ(trail)) <= (2));
         if (var8) {
            var2++;
         }
      }

      return var2 * (2) >= cluster.size();
   }

   private static OverlapBatchTracker.Bounds bounds(List cluster) {
      int var1 = 2147483647;
      int var2 = -2147483648;
      int var3 = 2147483647;
      int var4 = -2147483648;

      for (long var6 : cluster) {
         int var8 = ChunkKey.unpackX(var6);
         int var9 = ChunkKey.unpackZ(var6);
         var1 = Math.min(var1, var8);
         var2 = Math.max(var2, var8);
         var3 = Math.min(var3, var9);
         var4 = Math.max(var4, var9);
      }

      return new OverlapBatchTracker.Bounds(var1, var2, var3, var4);
   }

   private static int distanceSquared(int x, int z, int centerX, int centerZ) {
      int var4 = x - centerXx;
      int var5 = z - centerZx;
      return var4 * var4 + var5 * var5;
   }

   private static int chebyshev(int x, int z, int otherX, int otherZ) {
      return Math.max(Math.abs(x - otherX), Math.abs(z - otherZ));
   }

   private static void trimMap(LinkedHashMap map, int maxSize) {
      while (map.size() > maxSize) {
         Long var2 = (Long)map.keySet().iterator().next();
         map.remove(var2);
      }
   }

   private record BatchChunk(long chunkKey, int payloadBytes, long receivedNanos, int skippedCloser, boolean hot) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record Bounds(int minX, int maxX, int minZ, int maxZ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      int width() {
         return this.maxX - this.minX + 1;
      }

      int height() {
         return this.maxZ - this.minZ + 1;
      }

      int area() {
         return this.width() * this.height();
      }

      double aspectRatio() {
         int var1 = Math.min(this.width(), this.height());
         int var2 = Math.max(this.width(), this.height());
         return var1 == 0 ? Double.POSITIVE_INFINITY : (double)var2 / var1;
      }
   }

   private record HotSample(int skippedCloser, boolean expensiveBatch, int generation, long receivedNanos) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record OverlapEvidence(long chunkKey, Confidence confidence, int skippedCloser, int clusterSize, boolean expensiveBatch) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record PendingChunk(int distanceSquared) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
