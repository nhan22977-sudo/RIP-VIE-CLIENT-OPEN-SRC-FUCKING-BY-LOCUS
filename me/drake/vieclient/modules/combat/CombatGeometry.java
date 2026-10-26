package me.drake.vieclient.modules.combat;

import java.util.Optional;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;

final class CombatGeometry {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private CombatGeometry() {
   }

   static class_243 faceCenter(class_2338 block, class_2350 face) {
      return class_243.method_24953(block).method_1019(class_243.method_24954(face.method_62675()).method_1021(0.5));
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static boolean rayHitsFace(class_2338 block, class_2350 face, class_243 eye, class_243 look, double reach) {
      Optional var6 = new class_238(block).method_992(eye, eye.method_1019(look.method_1021(reach)));
      if (var6.isEmpty()) {
         return false;
      }

      class_243 var7 = (class_243)var6.get();
      class_243 var8 = faceCenter(block, face);

      return switch (<unrepresentable>.$SwitchMap$net$minecraft$util$math$Direction$Axis[face.method_10166().ordinal()]) {
         case 1 -> Math.abs(var7.field_1352 - var8.field_1352) < 1.0E-5;
         case 2 -> Math.abs(var7.field_1351 - var8.field_1351) < 1.0E-5;
         case 3 -> Math.abs(var7.field_1350 - var8.field_1350) < 1.0E-5;
         default -> throw new MatchException(null, null);
      };
   }

   static boolean betweenPlayerAndAnchor(class_243 player, class_2338 anchor, class_2338 protection) {
      double var3 = anchor.method_10263() + 0.5 - player.field_1352;
      double var5 = anchor.method_10260() + 0.5 - player.field_1350;
      double var7 = var3 * var3 + var5 * var5;
      if (var7 < 1.0E-6) {
         return false;
      }

      double var9 = protection.method_10263() + 0.5 - player.field_1352;
      double var11 = protection.method_10260() + 0.5 - player.field_1350;
      double var13 = (var9 * var3 + var11 * var5) / var7;
      double var15 = var9 * var5 - var11 * var3;
      return var13 > 0.0 && var13 < 1.0 && var15 * var15 / var7 <= 0.5625;
   }

   static boolean crystalOnBase(class_243 crystal, class_2338 base) {
      return Math.abs(crystal.field_1352 - (base.method_10263() + 0.5)) < 0.01
         && Math.abs(crystal.field_1350 - (base.method_10260() + 0.5)) < 0.01
         && Math.abs(crystal.field_1351 - (base.method_10264() + 1.0)) < 0.01;
   }

   static class_243 closestPoint(class_238 box, class_243 eye) {
      return new class_243(
         Math.clamp(eye.field_1352, box.field_1323, box.field_1320),
         Math.clamp(eye.field_1351, box.field_1322, box.field_1325),
         Math.clamp(eye.field_1350, box.field_1321, box.field_1324)
      );
   }

   static boolean aimMatchesCrystal(class_243 crystal, class_238 hitbox, class_243 eye, class_243 look, double attackReach, class_2338 aimedBase) {
      return hitbox.method_992(eye, eye.method_1019(look.method_1021(attackReach))).isPresent() || aimedBase != null && crystalOnBase(crystal, aimedBase);
   }

   static class_243 anchorAimPoint(class_2338 anchor, double lookDownBlocks) {
      double var3 = ((anchor.hashCode() & (15)) - 7.5) * 0.015;
      double var5 = ((anchor.hashCode() >> (4) & (15)) - 7.5) * 0.015;
      return new class_243(
         anchor.method_10263() + 0.5 + var3, anchor.method_10264() + Math.clamp(0.5 - lookDownBlocks, 0.15, 0.85), anchor.method_10260() + 0.5 + var5
      );
   }

   static boolean isInFov(class_243 eye, class_243 look, class_243 target, double maxFov) {
      if (maxFov >= 360.0) {
         return true;
      }

      if (eye != null && look != null && target != null) {
         class_243 var5 = target.method_1020(eye);
         double var6 = var5.method_1027();
         if (var6 < 1.0E-6) {
            return true;
         }

         var5 = var5.method_1021(1.0 / Math.sqrt(var6));
         double var8 = look.method_1026(var5);
         var8 = Math.clamp(var8, -1.0, 1.0);
         double var10 = Math.toDegrees(Math.acos(var8));
         return var10 <= maxFov / 2.0 + 1.0E-5;
      } else {
         return false;
      }
   }
}
