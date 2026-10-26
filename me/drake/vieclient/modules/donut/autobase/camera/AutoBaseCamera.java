package me.drake.vieclient.modules.donut.autobase.camera;

import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class AutoBaseCamera {
   private static final float MAX_YAW_STEP = 25.0F;
   private static final float MAX_PITCH_STEP = 18.0F;
   private static final float ALIGNMENT_EPSILON = 0.75F;
   private float targetYaw;
   private float targetPitch;
   private boolean active = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void setTargetAngles(float yaw, float pitch) {
      this.targetYaw = class_3532.method_15393(yaw);
      this.targetPitch = class_3532.method_15363(pitch, -90.0F, 90.0F);
      this.active = true;
   }

   public void setTargetPoint(class_243 eyePos, class_243 targetPoint) {
      float[] var3 = calculateAngles(eyePos, targetPoint);
      this.setTargetAngles(var3[0], var3[1]);
   }

   public void update(class_746 player) {
      if (this.active && player != null) {
         float var2 = player.method_36454();
         float var3 = player.method_36455();
         float var4 = class_3532.method_15393(this.targetYaw - var2);
         float var5 = this.targetPitch - var3;
         float var6 = Math.min(25.0F, Math.max(1.5F, Math.abs(var4) * 0.45F));
         float var7 = Math.min(18.0F, Math.max(1.5F, Math.abs(var5) * 0.45F));
         float var8 = class_3532.method_15363(var4, -var6, var6);
         float var9 = class_3532.method_15363(var5, -var7, var7);
         float var10 = class_3532.method_15393(var2 + var8);
         float var11 = class_3532.method_15363(var3 + var9, -90.0F, 90.0F);
         player.method_36456(var10);
         player.method_36457(var11);
      }
   }

   public boolean isAligned(class_746 player) {
      if (this.active && player != null) {
         float var2 = Math.abs(class_3532.method_15393(this.targetYaw - player.method_36454()));
         float var3 = Math.abs(this.targetPitch - player.method_36455());
         return var2 <= 0.75F && var3 <= 0.75F;
      } else {
         return true;
      }
   }

   public void reset() {
      this.active = false;
   }

   public static float[] calculateAngles(class_243 eyePos, class_243 targetPoint) {
      double var2 = targetPoint.field_1352 - eyePos.field_1352;
      double var4 = targetPoint.field_1351 - eyePos.field_1351;
      double var6 = targetPoint.field_1350 - eyePos.field_1350;
      double var8 = Math.sqrt(var2 * var2 + var6 * var6);
      float var10 = (float)Math.toDegrees(Math.atan2(var6, var2)) - 90.0F;
      float var11 = (float)(-Math.toDegrees(Math.atan2(var4, var8)));
      float[] var10000 = new float[2];
      var10000[0] = class_3532.method_15393(var10);
      var10000[1] = class_3532.method_15363(var11, -90.0F, 90.0F);
      return var10000;
   }

   public static class_3965 raycast(class_1937 world, class_746 player, double maxReach) {
      class_243 var4 = player.method_33571();
      class_243 var5 = player.method_5720();
      class_243 var6 = var4.method_1019(var5.method_1021(maxReach));
      return world.method_17742(new class_3959(var4, var6, class_3960.field_17559, class_242.field_1348, player));
   }

   public static boolean isLookingAtBlockFace(class_1937 world, class_746 player, class_2338 target, class_2350 expectedFace, double reach) {
      class_3965 var6 = raycast(world, player, reach);
      if (var6 == null || var6.method_17783() != class_240.field_1332) {
         return false;
      } else {
         return !var6.method_17777().equals(target) ? false : expectedFace == null || var6.method_17780() == expectedFace;
      }
   }
}
