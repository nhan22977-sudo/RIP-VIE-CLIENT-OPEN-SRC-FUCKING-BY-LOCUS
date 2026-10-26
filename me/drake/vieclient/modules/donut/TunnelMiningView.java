package me.drake.vieclient.modules.donut;

import net.minecraft.class_243;
import net.minecraft.class_3532;

final class TunnelMiningView {
   private static final float MAX_FOCUS_YAW_DELTA = 20.0F;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private TunnelMiningView() {
   }

   static TunnelMiningView.Aim aim(class_243 eye, class_243 focus, float routeYaw, float routePitch) {
      if (focus == null) {
         return new TunnelMiningView.Aim(routeYaw, routePitch);
      }

      double var4 = focus.field_1352 - eye.field_1352;
      double var6 = focus.field_1351 - eye.field_1351;
      double var8 = focus.field_1350 - eye.field_1350;
      double var10 = Math.hypot(var4, var8);
      float var12 = (float)Math.toDegrees(Math.atan2(var8, var4)) - 90.0F;
      var12 = class_3532.method_15393(var12);
      if (Math.abs(class_3532.method_15393(var12 - routeYaw)) > 20.0F) {
         return new TunnelMiningView.Aim(routeYaw, routePitch);
      }

      float var13 = (float)(-Math.toDegrees(Math.atan2(var6, var10)));
      return new TunnelMiningView.Aim(var12, class_3532.method_15363(var13, -90.0F, 90.0F));
   }

   record Aim(float yaw, float pitch) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
