package me.drake.vieclient.modules.combat;

import java.util.function.IntPredicate;

final class AnchorDetonationSlot {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private AnchorDetonationSlot() {
   }

   static int choose(int configuredSlot, boolean requireTotem, boolean offhandTotem, int charges, IntPredicate isTotem, IntPredicate isGlowstone) {
      int var6 = Math.clamp(configuredSlot, 0, 8);
      if (isTotem.test(var6)) {
         return var6;
      } else if (requireTotem && !offhandTotem) {
         return -1;
      } else {
         return charges < (4) && isGlowstone.test(var6) ? -1 : var6;
      }
   }

   static int refillSource(int configuredSlot, IntPredicate isTotem) {
      if (isTotem.test(configuredSlot)) {
         return -1;
      }

      for (int var2 = 0; var2 < (36); var2++) {
         if (var2 != configuredSlot && isTotem.test(var2)) {
            return var2;
         }
      }

      return -1;
   }

   static int inventoryScreenSlot(int inventorySlot) {
      return inventorySlot < (9) ? inventorySlot + (36) : inventorySlot;
   }

   static boolean glowstoneBlocksDetonation(int charges, boolean mainGlowstone, boolean offhandGlowstone) {
      return charges < (4) && (mainGlowstone || offhandGlowstone);
   }
}
