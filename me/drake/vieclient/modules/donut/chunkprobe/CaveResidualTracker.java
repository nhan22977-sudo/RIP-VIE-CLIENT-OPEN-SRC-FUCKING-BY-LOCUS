package me.drake.vieclient.modules.donut.chunkprobe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.LongPredicate;

public final class CaveResidualTracker {
   private static final int MIN_RESIDUAL_AIR = 64;
   private static final int MAX_RESIDUAL_AIR = 3584;
   private static final int MAX_TRACKED_CHUNKS = 512;
   private final LinkedHashMap volumes = new LinkedHashMap(64, 0.75F, true);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void update(RawChunkProbe probe) {
      ArrayList var2 = new ArrayList();

      for (RawChunkProbe.Section var4 : probe.sections()) {
         int var5 = var4.residualAir();
         if (var4.allMaskedDeepslate() && !var4.canonicalUniformMask() && var5 >= (64) && var5 <= (3584)) {
            var2.add(new CaveResidualTracker.SectionVolume(var4.sectionY(), var5));
         }
      }

      long var7 = probe.chunkKey();
      if (var2.isEmpty()) {
         this.volumes.remove(var7);
      } else {
         var2.sort(Comparator.comparingInt(CaveResidualTracker.SectionVolume::sectionY));
         int var8 = var2.stream().mapToInt(CaveResidualTracker.SectionVolume::residualAir).sum();
         int var6 = countConsistentPairs(var2);
         this.volumes.put(var7, new CaveResidualTracker.ChunkVolume(var8, var6));
         this.trimToCapacity();
      }
   }

   public Map analyze(LongPredicate perfectEvidence) {
      LinkedHashMap var2 = new LinkedHashMap();

      for (Entry var4 : this.volumes.entrySet()) {
         long var5 = (Long)var4.getKey();
         CaveResidualTracker.ChunkVolume var7 = (CaveResidualTracker.ChunkVolume)var4.getValue();
         List var8 = this.compatibleNeighbors(var5, var7);
         CaveResidualTracker.Bounds var9 = bounds(var8);
         double var10 = var9.area() == 0 ? 0.0 : (double)var8.size() / var9.area();
         boolean var12 = var8.size() >= (4) && var10 >= 0.5 && var9.aspectRatio() <= 2.5;
         boolean var13 = var8.stream().mapToLong(Long::longValue).anyMatch(perfectEvidence);
         Confidence var14 = var12 && var13 ? Confidence.HIGH : Confidence.POSSIBLE;
         var2.put(var5, new CaveResidualTracker.CaveEvidence(var5, var14, var7.residualAir(), var7.consistentSections(), var10));
      }

      return Map.copyOf(var2);
   }

   public void remove(long chunkKey) {
      this.volumes.remove(chunkKey);
   }

   public void pruneOutside(int centerX, int centerZ, int radius) {
      int var4 = Math.max(0, radius);
      this.volumes.keySet().removeIf(key -> chebyshev(key, centerX, centerZ) > var4);
   }

   public void clear() {
      this.volumes.clear();
   }

   private List compatibleNeighbors(long centerKey, CaveResidualTracker.ChunkVolume center) {
      int var4 = ChunkKey.unpackX(centerKey);
      int var5 = ChunkKey.unpackZ(centerKey);
      ArrayList var6 = new ArrayList(9);

      for (Entry var8 : this.volumes.entrySet()) {
         long var9 = (Long)var8.getKey();
         if (Math.max(Math.abs(ChunkKey.unpackX(var9) - var4), Math.abs(ChunkKey.unpackZ(var9) - var5)) <= 1) {
            int var11 = center.residualAir();
            int var12 = ((CaveResidualTracker.ChunkVolume)var8.getValue()).residualAir();
            int var13 = Math.min(var11, var12);
            int var14 = Math.max(var11, var12);
            if (var13 * 2L >= var14) {
               var6.add(var9);
            }
         }
      }

      return var6;
   }

   private static CaveResidualTracker.Bounds bounds(List keys) {
      if (keys.isEmpty()) {
         return new CaveResidualTracker.Bounds(0, -1, 0, -1);
      }

      int var1 = 2147483647;
      int var2 = -2147483648;
      int var3 = 2147483647;
      int var4 = -2147483648;

      for (long var6 : keys) {
         int var8 = ChunkKey.unpackX(var6);
         int var9 = ChunkKey.unpackZ(var6);
         var1 = Math.min(var1, var8);
         var2 = Math.max(var2, var8);
         var3 = Math.min(var3, var9);
         var4 = Math.max(var4, var9);
      }

      return new CaveResidualTracker.Bounds(var1, var2, var3, var4);
   }

   private static int countConsistentPairs(List sections) {
      int var1 = 0;

      for (int var2 = 1; var2 < sections.size(); var2++) {
         CaveResidualTracker.SectionVolume var3 = (CaveResidualTracker.SectionVolume)sections.get(var2 - 1);
         CaveResidualTracker.SectionVolume var4 = (CaveResidualTracker.SectionVolume)sections.get(var2);
         if (var4.sectionY() == var3.sectionY() + 1) {
            int var5 = Math.max(var3.residualAir(), var4.residualAir());
            int var6 = Math.abs(var3.residualAir() - var4.residualAir());
            if (var6 * 4L <= var5) {
               var1++;
            }
         }
      }

      return var1;
   }

   private void trimToCapacity() {
      while (this.volumes.size() > (512)) {
         Long var1 = (Long)this.volumes.keySet().iterator().next();
         this.volumes.remove(var1);
      }
   }

   private static int chebyshev(long key, int centerX, int centerZ) {
      return Math.max(Math.abs(ChunkKey.unpackX(key) - centerX), Math.abs(ChunkKey.unpackZ(key) - centerZ));
   }

   private record Bounds(int minX, int maxX, int minZ, int maxZ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      int width() {
         return this.maxX >= this.minX ? this.maxX - this.minX + 1 : 0;
      }

      int height() {
         return this.maxZ >= this.minZ ? this.maxZ - this.minZ + 1 : 0;
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

   public record CaveEvidence(long chunkKey, Confidence confidence, int residualAir, int consistentSections, double neighborhoodDensity) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record ChunkVolume(int residualAir, int consistentSections) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record SectionVolume(int sectionY, int residualAir) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
