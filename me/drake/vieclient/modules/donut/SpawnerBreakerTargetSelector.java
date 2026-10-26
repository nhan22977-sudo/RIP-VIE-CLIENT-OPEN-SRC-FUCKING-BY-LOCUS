package me.drake.vieclient.modules.donut;

import java.util.function.Predicate;
import net.minecraft.class_2338;

final class SpawnerBreakerTargetSelector {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SpawnerBreakerTargetSelector() {
   }

   static class_2338 findNearest(class_2338 center, int radius, Predicate isSpawner) {
      int var3 = Math.max(0, radius);
      class_2338 var4 = null;
      long var5 = Long.MAX_VALUE;

      for (int var7 = -var3; var7 <= var3; var7++) {
         for (int var8 = -var3; var8 <= var3; var8++) {
            for (int var9 = -var3; var9 <= var3; var9++) {
               class_2338 var10 = center.method_10069(var7, var8, var9);
               if (isSpawner.test(var10)) {
                  long var11 = (long)var7 * var7 + (long)var8 * var8 + (long)var9 * var9;
                  if (var11 < var5 || var11 == var5 && compare(var10, var4) < 0) {
                     var4 = var10.method_10062();
                     var5 = var11;
                  }
               }
            }
         }
      }

      return var4;
   }

   static boolean isWithinRadius(class_2338 center, class_2338 target, int radius) {
      int var3 = Math.max(0, radius);
      return Math.abs(target.method_10263() - center.method_10263()) <= var3
         && Math.abs(target.method_10264() - center.method_10264()) <= var3
         && Math.abs(target.method_10260() - center.method_10260()) <= var3;
   }

   private static int compare(class_2338 first, class_2338 second) {
      if (second == null) {
         return -1;
      }

      int var2 = Integer.compare(first.method_10263(), second.method_10263());
      if (var2 != 0) {
         return var2;
      }

      int var3 = Integer.compare(first.method_10264(), second.method_10264());
      return var3 != 0 ? var3 : Integer.compare(first.method_10260(), second.method_10260());
   }
}
