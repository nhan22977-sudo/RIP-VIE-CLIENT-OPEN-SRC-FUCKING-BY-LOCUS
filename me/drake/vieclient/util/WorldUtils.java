package me.drake.vieclient.util;

import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1675;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class WorldUtils {
   private static final class_310 mc = class_310.method_1551();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private WorldUtils() {
   }

   public static boolean isDeadBodyNearby() {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         for (class_1657 var1 : mc.field_1687.method_18456()) {
            if (var1 != mc.field_1724 && var1.method_29504() && var1.method_5858(mc.field_1724) <= 36.0) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static class_243 getPlayerLookVec(float yaw, float pitch) {
      float var2 = pitch * (float) (Math.PI / 180.0);
      float var3 = -yaw * (float) (Math.PI / 180.0);
      float var4 = class_3532.method_15362(var3);
      float var5 = class_3532.method_15374(var3);
      float var6 = class_3532.method_15362(var2);
      float var7 = class_3532.method_15374(var2);
      return new class_243(var5 * var6, -var7, var4 * var6);
   }

   public static class_243 getPlayerLookVec(class_1657 player) {
      return getPlayerLookVec(player.method_36454(), player.method_36455());
   }

   public static void placeBlock(class_3965 blockHit, boolean swingHand) {
      if (mc.field_1724 != null && mc.field_1761 != null) {
         class_1269 var2 = mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, blockHit);
         if (var2.method_23665() && swingHand) {
            mc.field_1724.method_6104(class_1268.field_5808);
         }
      }
   }

   public static boolean isShieldFacingAway(class_1657 player) {
      if (mc.field_1724 != null && player != null) {
         double var1 = mc.field_1724.method_23317() - player.method_23317();
         double var3 = mc.field_1724.method_23318() - player.method_23318();
         double var5 = mc.field_1724.method_23321() - player.method_23321();
         float var7 = (float)Math.toRadians(player.method_36454());
         float var8 = (float)Math.toRadians(player.method_36455());
         float var9 = class_3532.method_15362(var8);
         double var10 = -class_3532.method_15374(var7) * var9;
         double var12 = -class_3532.method_15374(var8);
         double var14 = class_3532.method_15362(var7) * var9;
         double var16 = var10 * var1 + var12 * var3 + var14 * var5;
         return var16 < 0.0;
      } else {
         return false;
      }
   }

   public static void hitEntity(class_1297 entity, boolean swingHand) {
      if (mc.field_1724 != null && mc.field_1761 != null) {
         mc.field_1761.method_2918(mc.field_1724, entity);
         if (swingHand) {
            mc.field_1724.method_6104(class_1268.field_5808);
         }
      }
   }

   public static class_239 getHitResult(double radius) {
      return mc.field_1724 == null ? null : getHitResult(mc.field_1724, false, mc.field_1724.method_36454(), mc.field_1724.method_36455(), radius);
   }

   public static class_239 getHitResult(class_1657 entity, boolean ignoreInvisibles, float yaw, float pitch, double distance) {
      if (entity != null && mc.field_1687 != null) {
         class_243 var6 = entity.method_5836(1.0F);
         class_243 var7 = getPlayerLookVec(yaw, pitch);
         class_243 var8 = var6.method_1031(var7.field_1352 * distance, var7.field_1351 * distance, var7.field_1350 * distance);
         Object var9 = mc.field_1687.method_17742(new class_3959(var6, var8, class_3960.field_17559, class_242.field_1348, entity));
         double var10 = distance * distance;
         if (var9 != null) {
            var10 = var9.method_17784().method_1025(var6);
         }

         class_243 var12 = var6.method_1031(var7.field_1352 * distance, var7.field_1351 * distance, var7.field_1350 * distance);
         class_238 var13 = entity.method_5829().method_18804(var7.method_1021(distance)).method_1009(1.0, 1.0, 1.0);
         class_3966 var14 = class_1675.method_18075(
            entity, var6, var12, var13, entityx -> !entityx.method_7325() && entityx.method_5863() && (!entityx.method_5767() || !ignoreInvisibles), var10
         );
         if (var14 != null) {
            class_243 var15 = var14.method_17784();
            double var16 = var6.method_1025(var15);
            if (var16 < var10 || var9 == null) {
               var9 = var16 > Math.pow(distance, 2.0)
                  ? class_3965.method_17778(var15, class_2350.method_10142(var7.field_1352, var7.field_1351, var7.field_1350), class_2338.method_49638(var15))
                  : var14;
            }
         }

         return (class_239)var9;
      } else {
         return null;
      }
   }
}
