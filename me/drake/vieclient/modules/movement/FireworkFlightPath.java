package me.drake.vieclient.modules.movement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

final class FireworkFlightPath {
   static final double DEFAULT_GRAVITY = 0.08;
   static final double DRAG_HORIZONTAL = 0.99;
   static final double DRAG_VERTICAL = 0.98;
   static final int MAX_TICKS = 600;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private FireworkFlightPath() {
   }

   static double[] lookVector(float yawDegrees, float pitchDegrees) {
      double var2 = Math.toRadians(pitchDegrees);
      double var4 = Math.toRadians(-yawDegrees);
      double var6 = Math.cos(var2);
      double[] var10000 = new double[3];
      var10000[0] = Math.sin(var4) * var6;
      var10000[1] = -Math.sin(var2);
      var10000[2] = Math.cos(var4) * var6;
      return var10000;
   }

   static FireworkFlightPath.State step(FireworkFlightPath.State state, float yawDegrees, float pitchDegrees, double gravity) {
      double[] var5 = lookVector(yawDegrees, pitchDegrees);
      double var6 = var5[0];
      double var8 = var5[1];
      double var10 = var5[2];
      double var12 = state.vx();
      double var14 = state.vy();
      double var16 = state.vz();
      double var18 = Math.toRadians(pitchDegrees);
      double var20 = Math.sqrt(var6 * var6 + var10 * var10);
      double var22 = Math.sqrt(var12 * var12 + var16 * var16);
      double var24 = Math.sqrt(var6 * var6 + var8 * var8 + var10 * var10);
      double var26 = Math.cos(var18);
      var26 = var26 * var26 * Math.min(1.0, var24 / 0.4);
      var14 += gravity * (-1.0 + var26 * 0.75);
      if (var14 < 0.0 && var20 > 0.0) {
         double var28 = var14 * -0.1 * var26;
         var12 += var6 * var28 / var20;
         var14 += var28;
         var16 += var10 * var28 / var20;
      }

      if (var18 < 0.0 && var20 > 0.0) {
         double var35 = var22 * -Math.sin(var18) * 0.04;
         var12 += -var6 * var35 / var20;
         var14 += var35 * 3.2;
         var16 += -var10 * var35 / var20;
      }

      if (var20 > 0.0) {
         var12 += (var6 / var20 * var22 - var12) * 0.1;
         var16 += (var10 / var20 * var22 - var16) * 0.1;
      }

      var12 *= 0.99;
      var14 *= 0.98;
      var16 *= 0.99;
      return new FireworkFlightPath.State(state.x() + var12, state.y() + var14, state.z() + var16, var12, var14, var16);
   }

   static List simulate(FireworkFlightPath.State start, float yawDegrees, float pitchDegrees, double gravity, int ticks, Predicate blocked) {
      ArrayList var7 = new ArrayList();
      if (start == null) {
         return var7;
      }

      int var8 = Math.max(0, Math.min(ticks, 600));
      FireworkFlightPath.State var9 = start;

      for (int var10 = 0; var10 < var8; var10++) {
         var9 = step(var9, yawDegrees, pitchDegrees, gravity);
         FireworkFlightPath.Point var11 = new FireworkFlightPath.Point(var9.x(), var9.y(), var9.z());
         var7.add(var11);
         if (blocked != null && blocked.test(var11)) {
            break;
         }
      }

      return var7;
   }

   static boolean belowBand(double y, int stableY, int band) {
      return y < stableY - band;
   }

   record Point(double x, double y, double z) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   record State(double x, double y, double z, double vx, double vy, double vz) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
