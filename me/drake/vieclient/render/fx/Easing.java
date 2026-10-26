package me.drake.vieclient.render.fx;

public final class Easing {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private Easing() {
   }

   private static float clamp01(float t) {
      return t < 0.0F ? 0.0F : (t > 1.0F ? 1.0F : t);
   }

   public static float linear(float t) {
      return clamp01(t);
   }

   public static float outCubic(float t) {
      t = clamp01(t);
      float var1 = 1.0F - t;
      return 1.0F - var1 * var1 * var1;
   }

   public static float inCubic(float t) {
      t = clamp01(t);
      return t * t * t;
   }

   public static float inQuad(float t) {
      t = clamp01(t);
      return t * t;
   }

   public static float outQuad(float t) {
      t = clamp01(t);
      return 1.0F - (1.0F - t) * (1.0F - t);
   }

   public static float inOutCubic(float t) {
      t = clamp01(t);
      return t < 0.5F ? 4.0F * t * t * t : 1.0F - (float)Math.pow(-2.0F * t + 2.0F, 3.0) / 2.0F;
   }

   public static float outBack(float t) {
      t = clamp01(t);
      float var1 = 1.70158F;
      float var2 = 2.70158F;
      float var3 = t - 1.0F;
      return 1.0F + 2.70158F * var3 * var3 * var3 + 1.70158F * var3 * var3;
   }

   public static float outElastic(float t) {
      t = clamp01(t);
      if (t != 0.0F && t != 1.0F) {
         float var1 = (float) (Math.PI * 2.0 / 3.0);
         return (float)(Math.pow(2.0, -10.0F * t) * Math.sin((t * 10.0F - 0.75) * (float) (Math.PI * 2.0 / 3.0)) + 1.0);
      } else {
         return t;
      }
   }

   public static float pulse(float t) {
      t = clamp01(t);
      return (float)Math.sin(t * Math.PI);
   }

   public static float lerp(float a, float b, float t) {
      return a + (b - a) * clamp01(t);
   }

   public static double lerpRaw(double a, double b, double t) {
      return a + (b - a) * t;
   }

   public static float smoothFactor(float dt, float halfLife) {
      return halfLife <= 0.0F ? 1.0F : 1.0F - (float)Math.exp(-dt / halfLife);
   }
}
