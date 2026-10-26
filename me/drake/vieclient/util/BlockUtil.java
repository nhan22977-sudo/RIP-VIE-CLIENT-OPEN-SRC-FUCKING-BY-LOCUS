package me.drake.vieclient.util;

import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_4969;

public final class BlockUtil {
   private static final class_310 mc = class_310.method_1551();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private BlockUtil() {
   }

   public static boolean isBlockAtPosition(class_2338 pos, class_2248 block) {
      return mc.field_1687 != null && mc.field_1687.method_8320(pos).method_27852(block);
   }

   public static boolean isRespawnAnchorUncharged(class_2338 pos) {
      return getAnchorCharges(pos) == 0;
   }

   public static boolean isRespawnAnchorCharged(class_2338 pos) {
      return getAnchorCharges(pos) > 0;
   }

   public static class_1269 interactWithBlock(class_3965 hitResult, boolean swing) {
      if (mc.field_1724 != null && mc.field_1761 != null) {
         class_1269 var2 = mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, hitResult);
         if (swing) {
            mc.field_1724.method_6104(class_1268.field_5808);
         }

         return var2;
      } else {
         return class_1269.field_5814;
      }
   }

   public static int getAnchorCharges(class_2338 pos) {
      if (mc.field_1687 == null) {
         return 0;
      }

      class_2680 var1 = mc.field_1687.method_8320(pos);
      return !var1.method_27852(class_2246.field_23152) ? 0 : (Integer)var1.method_11654(class_4969.field_23153);
   }

   public static boolean canPlaceBlockClient(class_2338 blockPos) {
      return mc.field_1687 != null && mc.field_1687.method_8320(blockPos).method_26215();
   }

   public static boolean isBlock(class_2338 blockPos, class_2248 block) {
      return isBlockAtPosition(blockPos, block);
   }

   public static boolean isAnchorCharged(class_2338 blockPos) {
      return isRespawnAnchorCharged(blockPos);
   }
}
