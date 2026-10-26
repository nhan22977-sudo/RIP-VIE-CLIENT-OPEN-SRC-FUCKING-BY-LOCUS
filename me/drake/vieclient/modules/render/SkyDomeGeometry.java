package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_243;

public final class SkyDomeGeometry {
   public static final double MIN_Y = -0.12;
   public static final double LOWER_Y = -0.5;
   public static final double NADIR_Y = -1.0;
   public static final double[] STOP_POSITIONS;
   private static final Map CACHE = new ConcurrentHashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SkyDomeGeometry() {
   }

   public static SkyDomeGeometry.DomeMesh mesh(int rings, int segments) {
      SkyDomeGeometry.Key var2 = new SkyDomeGeometry.Key(clampRings(rings), clampSegments(segments));
      return CACHE.computeIfAbsent(var2, SkyDomeGeometry::build);
   }

   public static int clampRings(int rings) {
      return Math.max(6, Math.min(256, rings));
   }

   public static int clampSegments(int segments) {
      return Math.max(8, Math.min(64, segments));
   }

   public static double ringY(int ring, int rings) {
      int var2 = STOP_POSITIONS.length - 1;
      if (rings <= 0) {
         return -1.0;
      }

      double var3 = Math.max(0.0, Math.min(1.0, (double)ring / rings));
      double var5 = var3 * var2;
      int var7 = (int)Math.floor(var5);
      if (var7 >= var2) {
         var7 = var2 - 1;
         var5 = var2;
      }

      double var8 = var5 - var7;
      double var10 = STOP_POSITIONS[var2 - var7];
      double var12 = STOP_POSITIONS[var2 - var7 - 1];
      return var10 + (var12 - var10) * var8;
   }

   private static SkyDomeGeometry.DomeMesh build(SkyDomeGeometry.Key key) {
      int var1 = key.rings();
      int var2 = key.segments();
      int var3 = var2 + 1;
      int var4 = (var1 + 1) * var3;
      class_243[] var5 = new class_243[var4];
      float[] var6 = new float[var4];
      Random var7 = new Random(23822L ^ (long)var1 << (32) ^ var2);

      for (int var8 = 0; var8 <= var1; var8++) {
         double var9 = Math.max(-1.0, Math.min(1.0, ringY(var8, var1)));
         double var11 = Math.sqrt(Math.max(0.0, 1.0 - var9 * var9));

         for (int var13 = 0; var13 <= var2; var13++) {
            double var14 = (double)var13 / var2 * (Math.PI * 2);
            int var16 = var8 * var3 + var13;
            var5[var16] = new class_243(Math.cos(var14) * var11, var9, Math.sin(var14) * var11);
            var6[var16] = var7.nextFloat() * 2.0F - 1.0F;
         }
      }

      int var17 = var1 * var2;
      int[] var18 = new int[var17 * (6)];
      int var10 = 0;

      for (int var24 = 0; var24 < var1; var24++) {
         for (int var12 = 0; var12 < var2; var12++) {
            int var25 = var24 * var3 + var12;
            int var26 = var24 * var3 + var12 + 1;
            int var15 = (var24 + 1) * var3 + var12 + 1;
            int var27 = (var24 + 1) * var3 + var12;
            var18[var10++] = var25;
            var18[var10++] = var26;
            var18[var10++] = var15;
            var18[var10++] = var25;
            var18[var10++] = var15;
            var18[var10++] = var27;
         }
      }

      return new SkyDomeGeometry.DomeMesh(var5, var6, var18);
   }

   public static int gradientColor(double y, double[] positions, int[] colors) {
      int var4 = positions.length;
      if (y >= positions[0]) {
         return colors[0];
      }

      if (y <= positions[var4 - 1]) {
         return colors[var4 - 1];
      }

      for (int var5 = 0; var5 < var4 - 1; var5++) {
         double var6 = positions[var5];
         double var8 = positions[var5 + 1];
         if (y <= var6 && y >= var8) {
            double var10 = var6 == var8 ? 0.0 : (y - var8) / (var6 - var8);
            return lerpArgbHsv(colors[var5 + 1], colors[var5], smoothstep(var10));
         }
      }

      return colors[var4 - 1];
   }

   public static double smoothstep(double t) {
      double var2 = Math.max(0.0, Math.min(1.0, t));
      return var2 * var2 * (3.0 - 2.0 * var2);
   }

   public static int lerpArgbHsv(int from, int to, double t) {
      double var4 = Math.max(0.0, Math.min(1.0, t));
      int var6 = from >>> (24) & (255);
      int var7 = to >>> (24) & (255);
      int var8 = (int)Math.round(var6 + (var7 - var6) * var4);
      float[] var9 = Color.RGBtoHSB(
         from >>> (16) & (255), from >>> (8) & (255), from & (255), null
      );
      float[] var10 = Color.RGBtoHSB(
         to >>> (16) & (255), to >>> (8) & (255), to & (255), null
      );
      float var11 = var10[0] - var9[0];
      if (var11 > 0.5F) {
         var11--;
      }

      if (var11 < -0.5F) {
         var11++;
      }

      float var12 = var9[0] + var11 * (float)var4;
      if (var12 < 0.0F) {
         var12++;
      }

      if (var12 >= 1.0F) {
         var12--;
      }

      float var13 = var9[1] + (var10[1] - var9[1]) * (float)var4;
      float var14 = var9[2] + (var10[2] - var9[2]) * (float)var4;
      int var15 = Color.HSBtoRGB(var12, var13, var14);
      return var8 << (24) | var15 & (16777215);
   }

   public static double valueNoise(double x, double y) {
      double var4 = Math.floor(x);
      double var6 = Math.floor(y);
      double var8 = x - var4;
      double var10 = y - var6;
      double var12 = hash(var4, var6);
      double var14 = hash(var4 + 1.0, var6);
      double var16 = hash(var4, var6 + 1.0);
      double var18 = hash(var4 + 1.0, var6 + 1.0);
      double var20 = smoothstep(var8);
      double var22 = smoothstep(var10);
      double var24 = var12 + (var14 - var12) * var20;
      double var26 = var16 + (var18 - var16) * var20;
      return var24 + (var26 - var24) * var22;
   }

   private static double hash(double x, double y) {
      double var4 = Math.sin(x * 127.1 + y * 311.7) * 43758.5453123;
      return var4 - Math.floor(var4);
   }

   static {
      double[] var10000 = new double[7];
      var10000[0] = 1.0;
      var10000[1] = 0.55;
      var10000[2] = 0.2;
      var10000[3] = 0.0;
      var10000[4] = -0.12;
      var10000[5] = -0.5;
      var10000[6] = -1.0;
      STOP_POSITIONS = var10000;
   }

   public record DomeMesh(class_243[] directions, float[] dither, int[] triangles) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public int vertexCount() {
         return this.directions.length;
      }

      public int triangleCount() {
         return this.triangles.length / (3);
      }
   }

   private record Key(int rings, int segments) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
