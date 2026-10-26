package me.drake.vieclient.modules.donut;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

final class TunnelStorageClusters {
   static final int MAX_CELLS = 2048;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private TunnelStorageClusters() {
   }

   static List cluster(List cells, int radius, int minSize) {
      ArrayList var3 = new ArrayList();
      if (cells != null && !cells.isEmpty() && minSize > 0) {
         int var4 = Math.min(cells.size(), 2048);
         int var5 = Math.max(0, radius);
         int[] var6 = new int[var4];
         int var7 = 0;

         while (var7 < var4) {
            var6[var7] = var7++;
         }

         for (int var19 = 0; var19 < var4; var19++) {
            TunnelStorageClusters.Cell var8 = (TunnelStorageClusters.Cell)cells.get(var19);

            for (int var9 = var19 + 1; var9 < var4; var9++) {
               TunnelStorageClusters.Cell var10 = (TunnelStorageClusters.Cell)cells.get(var9);
               if (Math.abs(var8.x() - var10.x()) <= var5 && Math.abs(var8.y() - var10.y()) <= var5 && Math.abs(var8.z() - var10.z()) <= var5) {
                  union(var6, var19, var9);
               }
            }
         }

         ArrayList var20 = new ArrayList(var4);
         int[] var21 = new int[var4];

         for (int var22 = 0; var22 < var4; var22++) {
            var21[var22] = -1;
         }

         for (int var23 = 0; var23 < var4; var23++) {
            int var25 = find(var6, var23);
            if (var21[var25] == -1) {
               var21[var25] = var20.size();
               var20.add(new ArrayList());
            }

            ((List)var20.get(var21[var25])).add((TunnelStorageClusters.Cell)cells.get(var23));
         }

         for (List var26 : var20) {
            if (var26.size() >= minSize) {
               long var11 = 0L;
               long var13 = 0L;
               long var15 = 0L;

               for (TunnelStorageClusters.Cell var18 : var26) {
                  var11 += var18.x();
                  var13 += var18.y();
                  var15 += var18.z();
               }

               int var27 = var26.size();
               TunnelStorageClusters.Cell var28 = var26.stream()
                  .min(
                     Comparator.comparingInt(TunnelStorageClusters.Cell::y)
                        .thenComparingInt(TunnelStorageClusters.Cell::x)
                        .thenComparingInt(TunnelStorageClusters.Cell::z)
                  )
                  .orElse((TunnelStorageClusters.Cell)var26.get(0));
               var3.add(
                  new TunnelStorageClusters.Cluster(
                     var27,
                     (int)Math.floorDiv((long)var11, (long)var27),
                     (int)Math.floorDiv((long)var13, (long)var27),
                     (int)Math.floorDiv((long)var15, (long)var27),
                     var28
                  )
               );
            }
         }

         var3.sort(Comparator.comparingInt(TunnelStorageClusters.Cluster::size).reversed());
         return var3;
      } else {
         return var3;
      }
   }

   private static int find(int[] parent, int i) {
      while (parent[i] != i) {
         parent[i] = parent[parent[i]];
         i = parent[i];
      }

      return i;
   }

   private static void union(int[] parent, int a, int b) {
      int var3 = find(parent, a);
      int var4 = find(parent, b);
      if (var3 != var4) {
         parent[var4] = var3;
      }
   }

   static int chebyshev(int ax, int ay, int az, int bx, int by, int bz) {
      return Math.max(Math.abs(ax - bx), Math.max(Math.abs(ay - by), Math.abs(az - bz)));
   }

   record Cell(int x, int y, int z) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   record Cluster(int size, int centerX, int centerY, int centerZ, TunnelStorageClusters.Cell seed) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
