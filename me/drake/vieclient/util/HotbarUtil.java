package me.drake.vieclient.util;

import java.util.function.Predicate;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_746;

public final class HotbarUtil {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private HotbarUtil() {
   }

   public static int findSlotInHotbar(HotbarUtil.PlayerInventoryLike inv, class_1792 item) {
      if (inv != null && item != null) {
         for (int var2 = 0; var2 < (9); var2++) {
            class_1799 var3 = inv.getStack(var2);
            if (var3.method_31574(item)) {
               return var2;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static int findSlotInHotbar(HotbarUtil.PlayerInventoryLike inv, Predicate predicate) {
      if (inv != null && predicate != null) {
         for (int var2 = 0; var2 < (9); var2++) {
            class_1799 var3 = inv.getStack(var2);
            if (var3 != null && !var3.method_7960() && predicate.test(var3)) {
               return var2;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static boolean interactWithBlock(class_636 gameMode, class_746 player, class_3965 hit, boolean swing) {
      if (gameMode != null && player != null && hit != null) {
         boolean var4 = gameMode.method_2896(player, class_1268.field_5808, hit).method_23665();
         if (swing && var4) {
            player.method_6104(class_1268.field_5808);
         }

         return var4;
      } else {
         return false;
      }
   }

   public interface PlayerInventoryLike {
      class_1799 getStack(int var1);
   }
}
