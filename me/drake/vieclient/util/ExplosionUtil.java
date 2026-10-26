package me.drake.vieclient.util;

public final class ExplosionUtil {
   public static final double ANCHOR_POWER = 5.0;
   public static final double ANCHOR_RADIUS = 4.0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private ExplosionUtil() {
   }

   public static double estimateAnchorDamage(double distance) {
      if (distance >= 4.0) {
         return 0.0;
      }

      if (distance <= 0.0) {
         return 5.0;
      }

      double var2 = 1.0 - distance / 4.0;
      return 5.0 * var2 * var2;
   }

   public static double estimateAnchorDamageWithShield(double distance) {
      return 0.0;
   }
}
