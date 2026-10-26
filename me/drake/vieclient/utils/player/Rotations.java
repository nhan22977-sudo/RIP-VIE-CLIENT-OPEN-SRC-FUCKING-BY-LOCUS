package me.drake.vieclient.utils.player;

import me.drake.vieclient.util.RotationUtil;
import me.drake.vieclient.utils.entity.Target;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_310;

public class Rotations {
   private static final class_310 mc = class_310.method_1551();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private Rotations() {
   }

   public static double getYaw(class_1297 target) {
      return RotationUtil.getYaw(target);
   }

   public static double getPitch(class_1297 target) {
      return RotationUtil.getPitch(target);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static double getPitch(class_1297 target, Target part) {
      double var2 = RotationUtil.getPitch(target);

      double var4 = switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$utils$entity$Target[part.ordinal()]) {
         case 1 -> 0.1;
         case 2 -> -0.25;
         default -> 0.0;
      };
      return Math.max(-90.0, Math.min(90.0, var2 + var4));
   }

   public static double getYaw(class_243 target) {
      if (mc.field_1724 == null) {
         return 0.0;
      }

      class_243 var1 = mc.field_1724.method_33571();
      return Math.toDegrees(Math.atan2(target.field_1350 - var1.field_1350, target.field_1352 - var1.field_1352)) - 90.0;
   }

   public static double getPitch(class_243 target) {
      if (mc.field_1724 == null) {
         return 0.0;
      }

      class_243 var1 = mc.field_1724.method_33571();
      double var2 = target.field_1352 - var1.field_1352;
      double var4 = target.field_1351 - var1.field_1351;
      double var6 = target.field_1350 - var1.field_1350;
      return -Math.toDegrees(Math.atan2(var4, Math.sqrt(var2 * var2 + var6 * var6)));
   }
}
