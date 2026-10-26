package me.drake.vieclient.util;

import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_746;

public class RotationUtil {
   private static final class_310 mc = class_310.method_1551();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static float[] getRotations(class_1297 target) {
      float[] var1 = new float[2];
      getRotations(target, var1);
      return var1;
   }

   public static void getRotations(class_1297 target, float[] out) {
      if (out != null && out.length >= (2)) {
         if (mc.field_1724 != null && target != null) {
            double var2 = target.method_23317() - mc.field_1724.method_23317();
            double var4 = target.method_23320() - mc.field_1724.method_23320();
            double var6 = target.method_23321() - mc.field_1724.method_23321();
            double var8 = Math.sqrt(var2 * var2 + var6 * var6);
            float var10 = (float)Math.toDegrees(Math.atan2(var6, var2)) - 90.0F;
            float var11 = (float)(-Math.toDegrees(Math.atan2(var4, var8)));
            out[0] = class_3532.method_15393(var10);
            out[1] = class_3532.method_15363(var11, -90.0F, 90.0F);
         } else {
            out[0] = 0.0F;
            out[1] = 0.0F;
         }
      }
   }

   public static float getYaw(class_1297 target) {
      if (mc.field_1724 != null && target != null) {
         double var1 = target.method_23317() - mc.field_1724.method_23317();
         double var3 = target.method_23321() - mc.field_1724.method_23321();
         float var5 = (float)Math.toDegrees(Math.atan2(var3, var1)) - 90.0F;
         return class_3532.method_15393(var5);
      } else {
         return 0.0F;
      }
   }

   public static float getPitch(class_1297 target) {
      if (mc.field_1724 != null && target != null) {
         double var1 = target.method_23317() - mc.field_1724.method_23317();
         double var3 = target.method_23320() - mc.field_1724.method_23320();
         double var5 = target.method_23321() - mc.field_1724.method_23321();
         double var7 = Math.sqrt(var1 * var1 + var5 * var5);
         float var9 = (float)(-Math.toDegrees(Math.atan2(var3, var7)));
         return class_3532.method_15363(var9, -90.0F, 90.0F);
      } else {
         return 0.0F;
      }
   }

   public static float[] getRotations(class_2338 target, class_746 player) {
      if (player != null && target != null) {
         double var2 = target.method_10263() + 0.5 - player.method_23317();
         double var4 = target.method_10264() + 0.5 - player.method_23320();
         double var6 = target.method_10260() + 0.5 - player.method_23321();
         double var8 = Math.sqrt(var2 * var2 + var6 * var6);
         float var10 = (float)Math.toDegrees(Math.atan2(var6, var2)) - 90.0F;
         float var11 = (float)(-Math.toDegrees(Math.atan2(var4, var8)));
         float[] var12 = new float[2];
         var12[0] = class_3532.method_15393(var10);
         var12[1] = class_3532.method_15363(var11, -90.0F, 90.0F);
         return var12;
      } else {
         float[] var10000 = new float[2];
         var10000[0] = 0.0F;
         var10000[1] = 0.0F;
         return var10000;
      }
   }

   public static float[] calcAngles(class_1297 target) {
      return getRotations(target);
   }

   public static float gcdFor(float sensitivity) {
      float var1 = sensitivity * 0.6F + 0.2F;
      return var1 * var1 * var1 * 1.2F;
   }

   public static float quantizeToGcd(float step, float remaining, float gcd) {
      if (!(gcd <= 0.0F) && !(Math.abs(remaining) < gcd)) {
         float var3 = Math.round(step / gcd) * gcd;
         if (var3 == 0.0F) {
            var3 = Math.copySign(gcd, remaining);
         }

         if (Math.abs(var3) > Math.abs(remaining)) {
            var3 = Math.copySign((float)Math.floor(Math.abs(remaining) / gcd) * gcd, remaining);
         }

         return var3;
      } else {
         return 0.0F;
      }
   }

   public static float applyGCD(float delta, float sensitivity) {
      float var2 = gcdFor(sensitivity);
      return delta - delta % var2;
   }

   public static float smoothAngle(float current, float target, float speed) {
      float var3 = class_3532.method_15393(target - current);
      return current + var3 * Math.min(1.0F, speed);
   }

   public static float[] smoothRotations(float[] current, float[] target, float speed) {
      float var3 = class_3532.method_15393(target[0] - current[0]);
      float var4 = target[1] - current[1];
      float var5 = current[0] + var3 * speed;
      float var6 = current[1] + var4 * speed;
      float[] var10000 = new float[2];
      var10000[0] = class_3532.method_15393(var5);
      var10000[1] = class_3532.method_15363(var6, -90.0F, 90.0F);
      return var10000;
   }
}
