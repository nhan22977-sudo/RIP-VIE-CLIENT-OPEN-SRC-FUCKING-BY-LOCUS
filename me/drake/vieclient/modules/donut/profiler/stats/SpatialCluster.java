package me.drake.vieclient.modules.donut.profiler.stats;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import me.drake.vieclient.modules.donut.chunkprobe.ChunkKey;

public final class SpatialCluster {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SpatialCluster() {
   }

   public static List find(Set keys, int minimumSize) {
      int var2 = Math.max(1, minimumSize);
      ArrayList var3 = new ArrayList(keys);
      var3.sort(Comparator.comparingInt(ChunkKey::unpackX).thenComparingInt(ChunkKey::unpackZ));
      HashSet var4 = new HashSet(var3);
      ArrayList var5 = new ArrayList();

      for (long var7 : var3) {
         if (var4.remove(var7)) {
            LinkedHashSet var9 = new LinkedHashSet();
            ArrayDeque var10 = new ArrayDeque();
            var10.add(var7);

            while (!var10.isEmpty()) {
               long var11 = (Long)var10.removeFirst();
               var9.add(var11);
               int var13 = ChunkKey.unpackX(var11);
               int var14 = ChunkKey.unpackZ(var11);

               for (int var15 = -1; var15 <= 1; var15++) {
                  for (int var16 = -1; var16 <= 1; var16++) {
                     if (var15 != 0 || var16 != 0) {
                        long var17 = ChunkKey.pack(var13 + var15, var14 + var16);
                        if (var4.remove(var17)) {
                           var10.addLast(var17);
                        }
                     }
                  }
               }
            }

            if (var9.size() >= var2) {
               var5.add(Set.copyOf(var9));
            }
         }
      }

      return List.copyOf(var5);
   }
}
