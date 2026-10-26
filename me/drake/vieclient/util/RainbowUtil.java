package me.drake.vieclient.util;

public final class RainbowUtil {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private RainbowUtil() {
   }

   public static int rainbow(double speed, double saturation, long offset, int alpha) {
      float var7 = (float)((System.currentTimeMillis() + offset * 200L) % 3600L / 3600.0 * speed) % 1.0F;
      int var8 = hsbToRgb(var7, (float)saturation, 1.0F);
      return (alpha & (255)) << (24) | var8 & (16777215);
   }

   public static int rainbow(double speed, long offset) {
      return rainbow(speed, 1.0, offset, 255);
   }

   public static int lerpColor(int colorA, int colorB, double factor) {
      factor = Math.max(0.0, Math.min(1.0, factor));
      int var4 = colorA >> (24) & (255);
      int var5 = colorA >> (16) & (255);
      int var6 = colorA >> (8) & (255);
      int var7 = colorA & (255);
      int var8 = colorB >> (24) & (255);
      int var9 = colorB >> (16) & (255);
      int var10 = colorB >> (8) & (255);
      int var11 = colorB & (255);
      int var12 = (int)(var4 + (var8 - var4) * factor);
      int var13 = (int)(var5 + (var9 - var5) * factor);
      int var14 = (int)(var6 + (var10 - var6) * factor);
      int var15 = (int)(var7 + (var11 - var7) * factor);
      return var12 << (24) | var13 << (16) | var14 << (8) | var15;
   }

   public static int distanceColor(double distSq, double maxDistSq, int nearColor, int farColor) {
      double var6 = Math.min(1.0, distSq / maxDistSq);
      return lerpColor(nearColor, farColor, var6);
   }

   public static int pulse(int baseColor, double speed, int minAlpha, int maxAlpha) {
      double var5 = System.currentTimeMillis() / 1000.0 * speed;
      double var7 = (Math.sin(var5 * Math.PI * 2.0) + 1.0) / 2.0;
      int var9 = (int)(minAlpha + (maxAlpha - minAlpha) * var7);
      return var9 << (24) | baseColor & (16777215);
   }

   public static int withAlpha(int color, int alpha) {
      return (alpha & (255)) << (24) | color & (16777215);
   }

   public static int hsbToRgb(float hue, float saturation, float brightness) {
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      if (saturation == 0.0F) {
         var3 = var4 = var5 = (int)(brightness * 255.0F + 0.5F);
      } else {
         float var6 = (hue - (float)Math.floor(hue)) * 6.0F;
         float var7 = var6 - (float)Math.floor(var6);
         float var8 = brightness * (1.0F - saturation);
         float var9 = brightness * (1.0F - saturation * var7);
         float var10 = brightness * (1.0F - saturation * (1.0F - var7));
         switch ((int)var6) {
            case 0:
               var3 = (int)(brightness * 255.0F + 0.5F);
               var4 = (int)(var10 * 255.0F + 0.5F);
               var5 = (int)(var8 * 255.0F + 0.5F);
               break;
            case 1:
               var3 = (int)(var9 * 255.0F + 0.5F);
               var4 = (int)(brightness * 255.0F + 0.5F);
               var5 = (int)(var8 * 255.0F + 0.5F);
               break;
            case 2:
               var3 = (int)(var8 * 255.0F + 0.5F);
               var4 = (int)(brightness * 255.0F + 0.5F);
               var5 = (int)(var10 * 255.0F + 0.5F);
               break;
            case 3:
               var3 = (int)(var8 * 255.0F + 0.5F);
               var4 = (int)(var9 * 255.0F + 0.5F);
               var5 = (int)(brightness * 255.0F + 0.5F);
               break;
            case 4:
               var3 = (int)(var10 * 255.0F + 0.5F);
               var4 = (int)(var8 * 255.0F + 0.5F);
               var5 = (int)(brightness * 255.0F + 0.5F);
               break;
            case 5:
               var3 = (int)(brightness * 255.0F + 0.5F);
               var4 = (int)(var8 * 255.0F + 0.5F);
               var5 = (int)(var9 * 255.0F + 0.5F);
         }
      }

      return var3 << (16) | var4 << (8) | var5;
   }
}
