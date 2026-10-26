package me.drake.vieclient.modules.donut;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

final class SusChunkGeometry {
   private static final Comparator CHUNK_ORDER = Comparator.comparingInt(SusChunkGeometry::chunkZ).thenComparingInt(SusChunkGeometry::chunkX);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SusChunkGeometry() {
   }

   static List buildStructures(Map hits, Set confirmedGrown, int threshold) {
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList(hits.keySet());
      var4.sort(CHUNK_ORDER);

      for (long var6 : var4) {
         int var8 = chunkX(var6);
         int var9 = chunkZ(var6);
         SusChunkGeometry.MutableStructure var10 = null;

         for (SusChunkGeometry.MutableStructure var12 : var3) {
            if (var12.accepts(var8, var9)) {
               var10 = var12;
               break;
            }
         }

         if (var10 == null) {
            var10 = new SusChunkGeometry.MutableStructure();
            var3.add(var10);
         }

         var10.add(var6, hits.getOrDefault(var6, 0));
      }

      ArrayList var13 = new ArrayList(var3.size());

      for (SusChunkGeometry.MutableStructure var7 : var3) {
         var13.add(var7.freeze(confirmedGrown, threshold));
      }

      var13.sort(
         Comparator.comparingInt(SusChunkGeometry.Structure::minChunkZ)
            .thenComparingInt(SusChunkGeometry.Structure::minChunkX)
            .thenComparingInt(SusChunkGeometry.Structure::maxChunkZ)
            .thenComparingInt(SusChunkGeometry.Structure::maxChunkX)
      );
      return List.copyOf(var13);
   }

   static List calculateRegions(Map hits, Set confirmedGrown, Set ungrown, int threshold, int radius) {
      return calculateAdaptiveRegions(hits, confirmedGrown, ungrown, Map.of(), threshold, radius, false);
   }

   static List calculateAdaptiveRegions(Map hits, Set confirmedGrown, Set ungrown, Map growthScores, int threshold, int radius, boolean dynamicSizing) {
      List var7 = buildStructures(hits, confirmedGrown, threshold);
      List var8 = var7.stream().filter(SusChunkGeometry.Structure::grown).toList();
      if (var8.isEmpty() && growthScores.isEmpty()) {
         return List.of();
      }

      List var9 = dynamicSizing ? adaptiveCandidateRegions(var8, growthScores, radius) : candidateRegions(var8, radius);
      ArrayList var10 = new ArrayList();
      LinkedHashSet var11 = new LinkedHashSet();

      for (SusChunkGeometry.Region var13 : var9) {
         for (SusChunkGeometry.Region var15 : subtractUngrown(var13, ungrown, radius)) {
            if (var11.add(var15.minChunkX() + ":" + var15.minChunkZ() + ":" + var15.maxChunkX() + ":" + var15.maxChunkZ())) {
               var10.add(var15);
            }
         }
      }

      return List.copyOf(var10);
   }

   private static List adaptiveCandidateRegions(List grown, Map growthScores, int radius) {
      if (grown.isEmpty()) {
         return growthOnlyRegions(growthScores, radius);
      }

      List var3 = findMacroComplexes(grown, radius);
      ArrayList var4 = new ArrayList();

      for (List var6 : var3) {
         if (var6.size() == 1) {
            SusChunkGeometry.Structure var7 = (SusChunkGeometry.Structure)var6.getFirst();
            Long var8 = findNearestGrowth(var7.centerChunkX(), var7.centerChunkZ(), growthScores, radius + 2);
            if (var8 != null) {
               int var9 = chunkX(var8);
               int var10 = chunkZ(var8);
               int var11 = Math.max(var7.centerChunkX() - radius, var9 - 2);
               int var12 = Math.min(var7.centerChunkX() + radius, var9 + 2);
               int var13 = Math.max(var7.centerChunkZ() - radius, var10 - 2);
               int var14 = Math.min(var7.centerChunkZ() + radius, var10 + 2);
               if (var11 <= var12 && var13 <= var14) {
                  var4.add(new SusChunkGeometry.Region(var11, var13, var12, var14, 1, radius));
                  continue;
               }
            }

            if (var7.totalHits() >= 24) {
               var4.add(square(var7, radius));
            } else if (var7.totalHits() >= 16 || var7.grown()) {
               int var17 = Math.min(2, radius);
               var4.add(
                  new SusChunkGeometry.Region(
                     var7.centerChunkX() - var17, var7.centerChunkZ() - var17, var7.centerChunkX() + var17, var7.centerChunkZ() + var17, 1, radius
                  )
               );
            }
         } else {
            SusChunkGeometry.Region var15 = intersectAll(var6, radius);
            if (var15 != null) {
               Long var16 = findGrowthInOrNear(var15, growthScores);
               if (var16 != null) {
                  int var18 = Math.max(var15.minChunkX(), Math.min(var15.maxChunkX(), chunkX(var16)));
                  int var19 = Math.max(var15.minChunkZ(), Math.min(var15.maxChunkZ(), chunkZ(var16)));
                  int var20 = Math.max(var15.minChunkX(), var18 - 1);
                  int var21 = Math.min(var15.maxChunkX(), var18 + 1);
                  int var22 = Math.max(var15.minChunkZ(), var19 - 1);
                  int var23 = Math.min(var15.maxChunkZ(), var19 + 1);
                  var4.add(new SusChunkGeometry.Region(var20, var22, var21, var23, var6.size(), radius));
               } else {
                  var4.add(var15);
               }
            } else {
               var4.addAll(buildCorridorRegions(var6, growthScores, radius));
            }
         }
      }

      var4.sort(
         Comparator.comparingInt(SusChunkGeometry.Region::minChunkZ)
            .thenComparingInt(SusChunkGeometry.Region::minChunkX)
            .thenComparingInt(SusChunkGeometry.Region::maxChunkZ)
            .thenComparingInt(SusChunkGeometry.Region::maxChunkX)
            .thenComparingInt(SusChunkGeometry.Region::geodeCount)
      );
      return var4;
   }

   private static List findMacroComplexes(List grown, int radius) {
      int var2 = grown.size();
      boolean[] var3 = new boolean[var2];
      ArrayList var4 = new ArrayList();

      for (int var5 = 0; var5 < var2; var5++) {
         if (!var3[var5]) {
            ArrayList var6 = new ArrayList();
            ArrayDeque var7 = new ArrayDeque();
            var7.add(var5);
            var3[var5] = true;

            while (!var7.isEmpty()) {
               int var8 = (Integer)var7.remove();
               var6.add((SusChunkGeometry.Structure)grown.get(var8));

               for (int var9 = 0; var9 < var2; var9++) {
                  if (!var3[var9] && canConnect((SusChunkGeometry.Structure)grown.get(var8), (SusChunkGeometry.Structure)grown.get(var9), radius)) {
                     var3[var9] = true;
                     var7.add(var9);
                  }
               }
            }

            var4.add(var6);
         }
      }

      return var4;
   }

   private static boolean canConnect(SusChunkGeometry.Structure a, SusChunkGeometry.Structure b, int radius) {
      return Math.max(Math.abs(a.centerChunkX() - b.centerChunkX()), Math.abs(a.centerChunkZ() - b.centerChunkZ())) <= (2) * radius + 1;
   }

   private static SusChunkGeometry.Region intersectAll(List complex, int radius) {
      int var2 = -2147483648;
      int var3 = -2147483648;
      int var4 = 2147483647;
      int var5 = 2147483647;

      for (SusChunkGeometry.Structure var7 : complex) {
         var2 = Math.max(var2, var7.centerChunkX() - radius);
         var3 = Math.max(var3, var7.centerChunkZ() - radius);
         var4 = Math.min(var4, var7.centerChunkX() + radius);
         var5 = Math.min(var5, var7.centerChunkZ() + radius);
      }

      return var2 <= var4 && var3 <= var5 ? new SusChunkGeometry.Region(var2, var3, var4, var5, complex.size(), radius) : null;
   }

   private static Long findGrowthInOrNear(SusChunkGeometry.Region region, Map growthScores) {
      Long var2 = null;
      int var3 = 29;

      for (Entry var5 : growthScores.entrySet()) {
         if ((Integer)var5.getValue() >= (30)) {
            int var6 = chunkX((Long)var5.getKey());
            int var7 = chunkZ((Long)var5.getKey());
            if (var6 >= region.minChunkX() - 1
               && var6 <= region.maxChunkX() + 1
               && var7 >= region.minChunkZ() - 1
               && var7 <= region.maxChunkZ() + 1
               && (Integer)var5.getValue() > var3) {
               var3 = (Integer)var5.getValue();
               var2 = (Long)var5.getKey();
            }
         }
      }

      return var2;
   }

   private static Long findNearestGrowth(int cx, int cz, Map growthScores, int maxDist) {
      Long var4 = null;
      int var5 = 29;
      int var6 = 2147483647;

      for (Entry var8 : growthScores.entrySet()) {
         if ((Integer)var8.getValue() >= (30)) {
            int var9 = chunkX((Long)var8.getKey());
            int var10 = chunkZ((Long)var8.getKey());
            int var11 = Math.max(Math.abs(cx - var9), Math.abs(cz - var10));
            if (var11 <= maxDist && ((Integer)var8.getValue() > var5 || (Integer)var8.getValue() == var5 && var11 < var6)) {
               var5 = (Integer)var8.getValue();
               var4 = (Long)var8.getKey();
               var6 = var11;
            }
         }
      }

      return var4;
   }

   private static List growthOnlyRegions(Map growthScores, int radius) {
      ArrayList var2 = new ArrayList();

      for (Entry var4 : growthScores.entrySet()) {
         if ((Integer)var4.getValue() >= (100)) {
            int var5 = chunkX((Long)var4.getKey());
            int var6 = chunkZ((Long)var4.getKey());
            var2.add(new SusChunkGeometry.Region(var5, var6, var5, var6, 0, radius));
         }
      }

      return var2;
   }

   private static List buildCorridorRegions(List complex, Map growthScores, int radius) {
      ArrayList var3 = new ArrayList();

      for (int var4 = 0; var4 < complex.size(); var4++) {
         SusChunkGeometry.Structure var5 = (SusChunkGeometry.Structure)complex.get(var4);
         boolean var6 = false;

         for (int var7 = var4 + 1; var7 < complex.size(); var7++) {
            SusChunkGeometry.Structure var8 = (SusChunkGeometry.Structure)complex.get(var7);
            if (canConnect(var5, var8, radius)) {
               var6 = true;
               int var9 = Math.max(var5.centerChunkX() - radius, var8.centerChunkX() - radius);
               int var10 = Math.min(var5.centerChunkX() + radius, var8.centerChunkX() + radius);
               int var11 = Math.max(var5.centerChunkZ() - radius, var8.centerChunkZ() - radius);
               int var12 = Math.min(var5.centerChunkZ() + radius, var8.centerChunkZ() + radius);
               if (var9 <= var10 && var11 <= var12) {
                  var3.add(new SusChunkGeometry.Region(var9, var11, var10, var12, 2, radius));
               }

               int var13 = Math.min(var5.centerChunkX(), var8.centerChunkX());
               int var14 = Math.max(var5.centerChunkX(), var8.centerChunkX());
               int var15 = Math.min(var5.centerChunkZ(), var8.centerChunkZ());
               int var16 = Math.max(var5.centerChunkZ(), var8.centerChunkZ());
               int var17 = var14 - var13;
               int var18 = var16 - var15;
               if (var17 > 1 && var18 > 1) {
                  int var19 = (var13 + var14) / (2);
                  var3.add(
                     new SusChunkGeometry.Region(
                        var13, Math.max(var15, var5.centerChunkZ() - 1), var19, Math.min(var16, var5.centerChunkZ() + 1), 2, radius
                     )
                  );
                  var3.add(
                     new SusChunkGeometry.Region(
                        var19, Math.max(var15, var8.centerChunkZ() - 1), var14, Math.min(var16, var8.centerChunkZ() + 1), 2, radius
                     )
                  );
               } else {
                  var3.add(new SusChunkGeometry.Region(var13, var15, var14, var16, 2, radius));
               }
            }
         }

         if (!var6) {
            var3.add(square(var5, radius));
         }
      }

      return var3;
   }

   private static List candidateRegions(List grown, int radius) {
      List var2 = maximalCliques(grown, radius);
      boolean[] var3 = new boolean[grown.size()];
      ArrayList var4 = new ArrayList();

      for (Set var6 : var2) {
         if (var6.size() >= (2)) {
            SusChunkGeometry.Region var7 = intersect(grown, var6, radius);
            if (var7 != null) {
               var4.add(var7);

               for (int var9 : var6) {
                  var3[var9] = true;
               }
            }
         }
      }

      for (int var10 = 0; var10 < grown.size(); var10++) {
         if (!var3[var10]) {
            var4.add(square((SusChunkGeometry.Structure)grown.get(var10), radius));
         }
      }

      var4.sort(
         Comparator.comparingInt(SusChunkGeometry.Region::minChunkZ)
            .thenComparingInt(SusChunkGeometry.Region::minChunkX)
            .thenComparingInt(SusChunkGeometry.Region::maxChunkZ)
            .thenComparingInt(SusChunkGeometry.Region::maxChunkX)
            .thenComparingInt(SusChunkGeometry.Region::geodeCount)
      );
      return var4;
   }

   private static List maximalCliques(List structures, int radius) {
      LinkedHashSet var2 = new LinkedHashSet();

      for (int var3 = 0; var3 < structures.size(); var3++) {
         var2.add(var3);
      }

      ArrayList var4 = new ArrayList();
      bronKerbosch(new LinkedHashSet(), var2, new LinkedHashSet(), structures, radius, var4);
      return var4;
   }

   private static void bronKerbosch(LinkedHashSet current, LinkedHashSet possible, LinkedHashSet excluded, List structures, int radius, List result) {
      if (possible.isEmpty() && excluded.isEmpty()) {
         result.add(Set.copyOf(current));
      } else {
         for (int var8 : new ArrayList(possible)) {
            possible.remove(var8);
            current.add(var8);
            bronKerbosch(
               current,
               intersectNeighbours(possible, var8, structures, radius),
               intersectNeighbours(excluded, var8, structures, radius),
               structures,
               radius,
               result
            );
            current.remove(var8);
            excluded.add(var8);
         }
      }
   }

   private static LinkedHashSet intersectNeighbours(Collection vertices, int vertex, List structures, int radius) {
      LinkedHashSet var4 = new LinkedHashSet();

      for (int var6 : vertices) {
         if (compatible((SusChunkGeometry.Structure)structures.get(vertex), (SusChunkGeometry.Structure)structures.get(var6), radius)) {
            var4.add(var6);
         }
      }

      return var4;
   }

   private static boolean compatible(SusChunkGeometry.Structure first, SusChunkGeometry.Structure second, int radius) {
      return Math.abs(first.centerChunkX() - second.centerChunkX()) <= (2) * radius
         && Math.abs(first.centerChunkZ() - second.centerChunkZ()) <= (2) * radius;
   }

   private static SusChunkGeometry.Region intersect(List structures, Set clique, int radius) {
      int var3 = -2147483648;
      int var4 = -2147483648;
      int var5 = 2147483647;
      int var6 = 2147483647;

      for (int var8 : clique) {
         SusChunkGeometry.Structure var9 = (SusChunkGeometry.Structure)structures.get(var8);
         var3 = Math.max(var3, var9.centerChunkX() - radius);
         var4 = Math.max(var4, var9.centerChunkZ() - radius);
         var5 = Math.min(var5, var9.centerChunkX() + radius);
         var6 = Math.min(var6, var9.centerChunkZ() + radius);
      }

      return var3 <= var5 && var4 <= var6 ? new SusChunkGeometry.Region(var3, var4, var5, var6, clique.size(), radius) : null;
   }

   private static SusChunkGeometry.Region square(SusChunkGeometry.Structure structure, int radius) {
      int var2 = structure.centerChunkX();
      int var3 = structure.centerChunkZ();
      return new SusChunkGeometry.Region(var2 - radius, var3 - radius, var2 + radius, var3 + radius, 1, radius);
   }

   private static List subtractUngrown(SusChunkGeometry.Region candidate, Set ungrown, int radius) {
      int var3 = candidate.widthChunks();
      int var4 = candidate.heightChunks();
      boolean[][] var5 = new boolean[var3][var4];

      for (int var6 = 0; var6 < var3; var6++) {
         for (int var7 = 0; var7 < var4; var7++) {
            int var8 = candidate.minChunkX() + var6;
            int var9 = candidate.minChunkZ() + var7;
            var5[var6][var7] = !coveredByUngrown(var8, var9, ungrown, radius);
         }
      }

      ArrayList var11 = new ArrayList();

      for (int var12 = 0; var12 < var4; var12++) {
         for (int var13 = 0; var13 < var3; var13++) {
            if (var5[var13][var12]) {
               int var14 = 0;

               while (var13 + var14 < var3 && var5[var13 + var14][var12]) {
                  var14++;
               }

               int var10 = 1;

               while (var12 + var10 < var4 && rowAvailable(var5, var13, var12 + var10, var14)) {
                  var10++;
               }

               markUnavailable(var5, var13, var12, var14, var10);
               var11.add(
                  new SusChunkGeometry.Region(
                     candidate.minChunkX() + var13,
                     candidate.minChunkZ() + var12,
                     candidate.minChunkX() + var13 + var14 - 1,
                     candidate.minChunkZ() + var12 + var10 - 1,
                     candidate.geodeCount(),
                     candidate.radiusUsed()
                  )
               );
            }
         }
      }

      return var11;
   }

   private static boolean coveredByUngrown(int x, int z, Set ungrown, int radius) {
      for (long var5 : ungrown) {
         if (Math.abs(x - chunkX(var5)) <= radius && Math.abs(z - chunkZ(var5)) <= radius) {
            return true;
         }
      }

      return false;
   }

   private static boolean rowAvailable(boolean[][] available, int startX, int z, int width) {
      for (int var4 = 0; var4 < width; var4++) {
         if (!available[startX + var4][z]) {
            return false;
         }
      }

      return true;
   }

   private static void markUnavailable(boolean[][] available, int startX, int startZ, int width, int height) {
      for (int var5 = 0; var5 < width; var5++) {
         for (int var6 = 0; var6 < height; var6++) {
            available[startX + var5][startZ + var6] = false;
         }
      }
   }

   private static int chunkX(long key) {
      return (int)key;
   }

   private static int chunkZ(long key) {
      return (int)(key >> (32));
   }

   static List filterSmartCandidates(List visibleChunks, Map probabilities) {
      return filterSmartCandidates(visibleChunks, probabilities, 6);
   }

   static List filterSmartCandidates(List visibleChunks, Map probabilities, int sensitivity) {
      if (visibleChunks != null && !visibleChunks.isEmpty() && probabilities != null) {
         int var3 = Math.max(1, Math.min(20, sensitivity));
         int var4 = visibleChunks.stream().mapToInt(k -> probabilities.getOrDefault(k, 0)).max().orElse(0);
         int var5 = Math.max(5, (16) - var3);
         int var6 = (36) + var3;
         int var7 = var4 >= (60) ? Math.max(60, var4 - var5) : Math.max(var6, var4 - (7));
         ArrayList var8 = new ArrayList(visibleChunks);
         var8.removeIf(k -> probabilities.getOrDefault(k, 0) < var7);
         if (var8.size() >= (4)) {
            var8.sort((a, b) -> Integer.compare(probabilities.getOrDefault(b, 0), probabilities.getOrDefault(a, 0)));
            float var9 = Math.max(0.4F, Math.min(0.85F, 0.7F - (var3 - (6)) * 0.02F));
            int var10 = Math.max(3, Math.round(var8.size() * var9));
            if (var8.size() > var10) {
               var8.subList(var10, var8.size()).clear();
            }
         }

         return var8;
      } else {
         return visibleChunks == null ? new ArrayList() : new ArrayList(visibleChunks);
      }
   }

   static Map calculateChunkProbabilities(List regions, Map hits, Map growthScores, Set growthSusChunks, Collection candidateChunks) {
      if (candidateChunks != null && !candidateChunks.isEmpty()) {
         HashMap var5 = new HashMap(candidateChunks.size());

         for (long var7 : candidateChunks) {
            int var9 = chunkX(var7);
            int var10 = chunkZ(var7);
            int var11 = 0;
            if (regions != null) {
               for (SusChunkGeometry.Region var13 : regions) {
                  if (var9 >= var13.minChunkX() && var9 <= var13.maxChunkX() && var10 >= var13.minChunkZ() && var10 <= var13.maxChunkZ()) {
                     var11 = Math.max(var11, var13.geodeCount());
                  }
               }
            }

            byte var19;
            if (var11 >= 3) {
               var19 = 65;
            } else if (var11 == 2) {
               var19 = 48;
            } else if (var11 != 1) {
               var19 = 0;
            } else {
               int var20 = Integer.MAX_VALUE;
               int var14 = 0;
               if (hits != null) {
                  for (Entry var16 : hits.entrySet()) {
                     int var17 = Math.max(Math.abs(var9 - chunkX((Long)var16.getKey())), Math.abs(var10 - chunkZ((Long)var16.getKey())));
                     if (var17 < var20) {
                        var20 = var17;
                        var14 = (Integer)var16.getValue();
                     }
                  }
               }

               if (var20 == 0) {
                  var19 = 36;
               } else if (var20 == 1) {
                  var19 = 25;
               } else if (var20 == 2) {
                  var19 = 15;
               } else {
                  var19 = 5;
               }

               if (var14 >= 28) {
                  var19 += 6;
               }
            }

            int var21 = growthScores != null ? growthScores.getOrDefault(var7, 0) : 0;
            boolean var22 = growthSusChunks != null && growthSusChunks.contains(var7);
            int var23 = findMaxNearbyGrowth(var9, var10, growthScores, 1);
            byte var24;
            if (var22 || var21 >= 100) {
               var24 = 32;
            } else if (var21 >= 50) {
               var24 = 22;
            } else if (var23 >= 50) {
               var24 = 14;
            } else if (var21 < 35 && var23 < 35) {
               var24 = 0;
            } else {
               var24 = 8;
            }

            int var25 = var11 < 2 || var21 < 35 && var23 < 35 ? 0 : 10;
            int var18 = var19 + var24 + var25;
            var18 = Math.min(99, Math.max(0, var18));
            var5.put(var7, var18);
         }

         return var5;
      } else {
         return Map.of();
      }
   }

   private static int findMaxNearbyGrowth(int cx, int cz, Map growthScores, int radius) {
      if (growthScores != null && !growthScores.isEmpty()) {
         int var4 = 0;

         for (int var5 = -radius; var5 <= radius; var5++) {
            for (int var6 = -radius; var6 <= radius; var6++) {
               if (var5 != 0 || var6 != 0) {
                  long var7 = packKey(cx + var5, cz + var6);
                  Integer var9 = (Integer)growthScores.get(var7);
                  if (var9 != null && var9 > var4) {
                     var4 = var9;
                  }
               }
            }
         }

         return var4;
      } else {
         return 0;
      }
   }

   private static long packKey(int x, int z) {
      return x & 4294967295L | (long)z << (32);
   }

   private static final class MutableStructure {
      private final List chunks = new ArrayList();
      private int minX = 2147483647;
      private int maxX = -2147483648;
      private int minZ = 2147483647;
      private int maxZ = -2147483648;
      private int totalHits;
      private int maxHits;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      boolean accepts(int x, int z) {
         boolean var3 = this.chunks
            .stream()
            .anyMatch(key -> Math.abs(x - SusChunkGeometry.chunkX(key)) <= 1 && Math.abs(z - SusChunkGeometry.chunkZ(key)) <= 1);
         boolean var4 = Math.max(this.maxX, x) - Math.min(this.minX, x) <= 1 && Math.max(this.maxZ, z) - Math.min(this.minZ, z) <= 1;
         return var3 && var4;
      }

      void add(long key, int hits) {
         this.chunks.add(key);
         int var4 = SusChunkGeometry.chunkX(key);
         int var5 = SusChunkGeometry.chunkZ(key);
         this.minX = Math.min(this.minX, var4);
         this.maxX = Math.max(this.maxX, var4);
         this.minZ = Math.min(this.minZ, var5);
         this.maxZ = Math.max(this.maxZ, var5);
         this.totalHits += hits;
         this.maxHits = Math.max(this.maxHits, hits);
      }

      SusChunkGeometry.Structure freeze(Set confirmedGrown, int threshold) {
         this.chunks.sort(SusChunkGeometry.CHUNK_ORDER);
         boolean var3 = this.maxHits >= threshold || this.totalHits >= threshold || this.chunks.stream().anyMatch(confirmedGrown::contains);
         return new SusChunkGeometry.Structure(this.minX, this.maxX, this.minZ, this.maxZ, List.copyOf(this.chunks), this.totalHits, this.maxHits, var3);
      }
   }

   record Region(int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ, int geodeCount, int radiusUsed) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      int widthChunks() {
         return this.maxChunkX - this.minChunkX + 1;
      }

      int heightChunks() {
         return this.maxChunkZ - this.minChunkZ + 1;
      }

      double minBlockX() {
         return this.minChunkX * 16.0;
      }

      double maxBlockX() {
         return (this.maxChunkX + 1) * 16.0;
      }

      double minBlockZ() {
         return this.minChunkZ * 16.0;
      }

      double maxBlockZ() {
         return (this.maxChunkZ + 1) * 16.0;
      }
   }

   record Structure(int minChunkX, int maxChunkX, int minChunkZ, int maxChunkZ, List<Long> chunks, int totalHits, int maxHits, boolean grown) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      int centerChunkX() {
         return (int)Math.round((this.minChunkX + this.maxChunkX) / 2.0);
      }

      int centerChunkZ() {
         return (int)Math.round((this.minChunkZ + this.maxChunkZ) / 2.0);
      }
   }
}
