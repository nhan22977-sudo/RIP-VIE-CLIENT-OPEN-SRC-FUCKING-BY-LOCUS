package com.zigythebird.playeranimcore.math;

import team.unnamed.mocha.runtime.standard.MochaMath;

public class MathHelper {
   public static float cos(float a) {
      return (float)Math.cos(a);
   }

   public static float cosFromSin(float sin, float angle) {
      float cos = MochaMath.sqrt(1.0F - sin * sin);
      float a = angle + (float) (Math.PI / 2);
      float b = a - (int)(a / (float) (Math.PI * 2)) * (float) (Math.PI * 2);
      if (b < 0.0) {
         b += (float) (Math.PI * 2);
      }

      return b >= (float) Math.PI ? -cos : cos;
   }

   public static boolean absEqualsOne(float r) {
      return (Float.floatToRawIntBits(r) & 2147483647) == 1065353216;
   }

   public static float safeAsin(float r) {
      return r <= -1.0F ? (float) (-Math.PI / 2) : (float)(r >= 1.0F ? (float) (Math.PI / 2) : Math.asin(r));
   }

   public static float length(float x, float y, float z, float w) {
      return MochaMath.sqrt(Math.fma(x, x, Math.fma(y, y, Math.fma(z, z, w * w))));
   }
}
