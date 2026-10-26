package me.drake.vieclient.render.nvg;

import java.awt.Color;

public final class NVGIcons {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private NVGIcons() {
   }

   private static float r(double deg) {
      return (float)Math.toRadians(deg);
   }

   public static void chevron(float cx, float cy, float w, boolean down, float thick, Color c) {
      float var6 = w * 0.5F;
      float var7 = w * 0.3F;
      if (down) {
         NanoVGRenderer.drawLine(cx - var6, cy - var7, cx, cy + var7, thick, c);
         NanoVGRenderer.drawLine(cx, cy + var7, cx + var6, cy - var7, thick, c);
      } else {
         NanoVGRenderer.drawLine(cx - var6, cy + var7, cx, cy - var7, thick, c);
         NanoVGRenderer.drawLine(cx, cy - var7, cx + var6, cy + var7, thick, c);
      }
   }

   public static void chevron(float cx, float cy, float w, boolean down, float thick, int c) {
      float var6 = w * 0.5F;
      float var7 = w * 0.3F;
      if (down) {
         NanoVGRenderer.drawLine(cx - var6, cy - var7, cx, cy + var7, thick, c);
         NanoVGRenderer.drawLine(cx, cy + var7, cx + var6, cy - var7, thick, c);
      } else {
         NanoVGRenderer.drawLine(cx - var6, cy + var7, cx, cy - var7, thick, c);
         NanoVGRenderer.drawLine(cx, cy - var7, cx + var6, cy + var7, thick, c);
      }
   }

   public static void arrow(float cx, float cy, float w, boolean right, float thick, Color c) {
      float var6 = w * 0.5F;
      float var7 = w * 0.3F;
      if (right) {
         NanoVGRenderer.drawLine(cx - var7, cy - var6, cx + var7, cy, thick, c);
         NanoVGRenderer.drawLine(cx + var7, cy, cx - var7, cy + var6, thick, c);
      } else {
         NanoVGRenderer.drawLine(cx + var7, cy - var6, cx - var7, cy, thick, c);
         NanoVGRenderer.drawLine(cx - var7, cy, cx + var7, cy + var6, thick, c);
      }
   }

   public static void arrow(float cx, float cy, float w, boolean right, float thick, int c) {
      float var6 = w * 0.5F;
      float var7 = w * 0.3F;
      if (right) {
         NanoVGRenderer.drawLine(cx - var7, cy - var6, cx + var7, cy, thick, c);
         NanoVGRenderer.drawLine(cx + var7, cy, cx - var7, cy + var6, thick, c);
      } else {
         NanoVGRenderer.drawLine(cx + var7, cy - var6, cx - var7, cy, thick, c);
         NanoVGRenderer.drawLine(cx - var7, cy, cx + var7, cy + var6, thick, c);
      }
   }

   public static void cross(float cx, float cy, float size, float thick, Color c) {
      float var5 = size * 0.5F;
      NanoVGRenderer.drawLine(cx - var5, cy - var5, cx + var5, cy + var5, thick, c);
      NanoVGRenderer.drawLine(cx - var5, cy + var5, cx + var5, cy - var5, thick, c);
   }

   public static void cross(float cx, float cy, float size, float thick, int c) {
      float var5 = size * 0.5F;
      NanoVGRenderer.drawLine(cx - var5, cy - var5, cx + var5, cy + var5, thick, c);
      NanoVGRenderer.drawLine(cx - var5, cy + var5, cx + var5, cy - var5, thick, c);
   }

   public static void check(float cx, float cy, float size, float thick, Color c) {
      float var5 = size * 0.5F;
      NanoVGRenderer.drawLine(cx - var5, cy + var5 * 0.1F, cx - var5 * 0.2F, cy + var5 * 0.7F, thick, c);
      NanoVGRenderer.drawLine(cx - var5 * 0.2F, cy + var5 * 0.7F, cx + var5, cy - var5 * 0.6F, thick, c);
   }

   public static void check(float cx, float cy, float size, float thick, int c) {
      float var5 = size * 0.5F;
      NanoVGRenderer.drawLine(cx - var5, cy + var5 * 0.1F, cx - var5 * 0.2F, cy + var5 * 0.7F, thick, c);
      NanoVGRenderer.drawLine(cx - var5 * 0.2F, cy + var5 * 0.7F, cx + var5, cy - var5 * 0.6F, thick, c);
   }

   public static void star(float cx, float cy, float rad, Color c) {
      float var4 = rad * 0.46F;
      float[] var5 = new float[10];
      float[] var6 = new float[10];

      for (int var7 = 0; var7 < (10); var7++) {
         double var8 = (-Math.PI / 2) + var7 * Math.PI / 5.0;
         float var10 = var7 % (2) == 0 ? rad : var4;
         var5[var7] = cx + (float)Math.cos(var8) * var10;
         var6[var7] = cy + (float)Math.sin(var8) * var10;
      }

      for (int var11 = 0; var11 < (10); var11++) {
         int var12 = (var11 + 1) % (10);
         NanoVGRenderer.drawTriangle(cx, cy, var5[var11], var6[var11], var5[var12], var6[var12], c);
      }
   }

   public static void infinity(float cx, float cy, float w, float thick, Color c) {
      float var5 = w * 0.27F;
      float var6 = var5 * 0.92F;
      NanoVGRenderer.drawCircleOutline(cx - var6, cy, var5, thick, c);
      NanoVGRenderer.drawCircleOutline(cx + var6, cy, var5, thick, c);
   }

   public static void heart(float cx, float cy, float size, Color c) {
      if (c != null && c.getAlpha() > 0) {
         float var4 = size * 0.5F;
         float var5 = var4 * 0.4F;
         float var6 = var4 * 0.36F;
         float var7 = var4 * 0.22F;
         NanoVGRenderer.drawCircle(cx - var6, cy - var7, var5, c);
         NanoVGRenderer.drawCircle(cx + var6, cy - var7, var5, c);
         NanoVGRenderer.drawTriangle(cx - var6, cy - var7, cx + var6, cy - var7, cx, cy + var4 * 0.22F, c);
         NanoVGRenderer.drawTriangle(cx - var4 * 0.74F, cy - var4 * 0.08F, cx + var4 * 0.74F, cy - var4 * 0.08F, cx, cy + var4 * 0.82F, c);
      }
   }

   public static void heart(float cx, float cy, float size, int c) {
      if (c >>> (24) != 0) {
         float var4 = size * 0.5F;
         float var5 = var4 * 0.4F;
         float var6 = var4 * 0.36F;
         float var7 = var4 * 0.22F;
         NanoVGRenderer.drawCircle(cx - var6, cy - var7, var5, c);
         NanoVGRenderer.drawCircle(cx + var6, cy - var7, var5, c);
         NanoVGRenderer.drawTriangle(cx - var6, cy - var7, cx + var6, cy - var7, cx, cy + var4 * 0.22F, c);
         NanoVGRenderer.drawTriangle(cx - var4 * 0.74F, cy - var4 * 0.08F, cx + var4 * 0.74F, cy - var4 * 0.08F, cx, cy + var4 * 0.82F, c);
      }
   }

   public static void cloud(float cx, float cy, float size, Color c) {
      if (c != null && c.getAlpha() > 0) {
         float var4 = size * 0.5F;
         float var5 = cy + var4 * 0.25F;
         float var6 = size * 0.85F;
         float var7 = size * 0.45F;
         NanoVGRenderer.drawRoundedRect(cx - var6 * 0.5F, var5 - var7 * 0.5F, var6, var7, var7 * 0.5F, c);
         NanoVGRenderer.drawCircle(cx - var4 * 0.28F, cy - var4 * 0.05F, var4 * 0.4F, c);
         NanoVGRenderer.drawCircle(cx + var4 * 0.08F, cy - var4 * 0.22F, var4 * 0.46F, c);
         NanoVGRenderer.drawCircle(cx + var4 * 0.38F, cy + var4 * 0.02F, var4 * 0.32F, c);
      }
   }

   public static void cloud(float cx, float cy, float size, int c) {
      if (c >>> (24) != 0) {
         float var4 = size * 0.5F;
         float var5 = cy + var4 * 0.25F;
         float var6 = size * 0.85F;
         float var7 = size * 0.45F;
         NanoVGRenderer.drawRoundedRect(cx - var6 * 0.5F, var5 - var7 * 0.5F, var6, var7, var7 * 0.5F, c);
         NanoVGRenderer.drawCircle(cx - var4 * 0.28F, cy - var4 * 0.05F, var4 * 0.4F, c);
         NanoVGRenderer.drawCircle(cx + var4 * 0.08F, cy - var4 * 0.22F, var4 * 0.46F, c);
         NanoVGRenderer.drawCircle(cx + var4 * 0.38F, cy + var4 * 0.02F, var4 * 0.32F, c);
      }
   }

   public static void sword(float cx, float cy, float size, Color c) {
      if (c != null && c.getAlpha() > 0) {
         float var4 = size * 0.5F;
         float var5 = cx + var4 * 0.78F;
         float var6 = cy - var4 * 0.78F;
         float var7 = cx - var4 * 0.35F;
         float var8 = cy + var4 * 0.35F;
         float var9 = cx - var4 * 0.65F;
         float var10 = cy + var4 * 0.65F;
         NanoVGRenderer.drawLine(var7, var8, var5, var6, Math.max(1.2F, size * 0.15F), c);
         float var11 = var4 * 0.3F;
         NanoVGRenderer.drawLine(var7 - var11, var8 - var11, var7 + var11, var8 + var11, Math.max(1.0F, size * 0.12F), c);
         NanoVGRenderer.drawLine(var7, var8, var9, var10, Math.max(1.0F, size * 0.1F), c);
         NanoVGRenderer.drawCircle(var9, var10, Math.max(0.8F, size * 0.08F), c);
      }
   }

   public static void sword(float cx, float cy, float size, int c) {
      if (c >>> (24) != 0) {
         sword(cx, cy, size, new Color(c, true));
      }
   }

   public static void spotify(float x, float y, float s, int alpha) {
      if (alpha > 0) {
         int var4 = Math.max(0, Math.min(255, alpha));
         Color var5 = new Color(238, 239, 243, var4);
         Color var6 = new Color(20, 20, 24, var4);
         NanoVGRenderer.drawSpotifyLogo(x, y, s, var5, var6);
      }
   }

   public static void spotifyGreen(float x, float y, float s, int alpha) {
      if (alpha > 0) {
         int var4 = Math.max(0, Math.min(255, alpha));
         Color var5 = new Color(30, 215, 96, var4);
         Color var6 = new Color(255, 255, 255, var4);
         NanoVGRenderer.drawSpotifyLogo(x, y, s, var5, var6);
      }
   }

   public static void spotifyLogo(float x, float y, float s, Color discColor, Color waveColor) {
      NanoVGRenderer.drawSpotifyLogo(x, y, s, discColor, waveColor);
   }

   public static void spotifyWaves(float x, float y, float s, Color waveColor) {
      NanoVGRenderer.drawSpotifyWaves(x, y, s, waveColor);
   }

   public static void gear(float cx, float cy, float size, Color c) {
      float var4 = size * 0.5F;
      float var5 = var4 * 0.78F;
      float var6 = var4 * 1.05F;
      float var7 = Math.max(0.7F, size * 0.1F);
      NanoVGRenderer.drawCircleOutline(cx, cy, var4 * 0.7F, var7, c);
      NanoVGRenderer.drawCircleOutline(cx, cy, var4 * 0.3F, var7, c);

      for (int var8 = 0; var8 < (8); var8++) {
         double var9 = var8 * Math.PI / 4.0;
         float var11 = (float)Math.cos(var9);
         float var12 = (float)Math.sin(var9);
         NanoVGRenderer.drawLine(cx + var11 * var5, cy + var12 * var5, cx + var11 * var6, cy + var12 * var6, var7, c);
      }
   }

   public static void dots(float cx, float cy, float spacing, float dotRadius, boolean vertical, Color c) {
      if (vertical) {
         NanoVGRenderer.drawCircle(cx, cy - spacing, dotRadius, c);
         NanoVGRenderer.drawCircle(cx, cy, dotRadius, c);
         NanoVGRenderer.drawCircle(cx, cy + spacing, dotRadius, c);
      } else {
         NanoVGRenderer.drawCircle(cx - spacing, cy, dotRadius, c);
         NanoVGRenderer.drawCircle(cx, cy, dotRadius, c);
         NanoVGRenderer.drawCircle(cx + spacing, cy, dotRadius, c);
      }
   }

   public static void activity(float cx, float cy, float size, float thick, Color c) {
      float var5 = cy;
      float var6 = cx - size * 0.46F;
      float var7 = cx - size * 0.2F;
      float var8 = cx - size * 0.08F;
      float var9 = cy - size * 0.36F;
      float var10 = cx + size * 0.08F;
      float var11 = cy + size * 0.36F;
      float var12 = cx + size * 0.2F;
      float var13 = cy - size * 0.14F;
      float var14 = cx + size * 0.46F;
      NanoVGRenderer.drawLine(var6, var5, var7, var5, thick, c);
      NanoVGRenderer.drawLine(var7, var5, var8, var9, thick, c);
      NanoVGRenderer.drawLine(var8, var9, var10, var11, thick, c);
      NanoVGRenderer.drawLine(var10, var11, var12, var13, thick, c);
      NanoVGRenderer.drawLine(var12, var13, var14, var5, thick, c);
   }

   public static void speedometer(float cx, float cy, float r, float thick, Color c) {
      int var5 = 10;
      double var6 = Math.toRadians(140.0);
      double var8 = Math.toRadians(400.0);

      for (int var10 = 0; var10 < var5; var10++) {
         double var11 = var6 + (var8 - var6) * ((double)var10 / var5);
         double var13 = var6 + (var8 - var6) * ((double)(var10 + 1) / var5);
         NanoVGRenderer.drawLine(
            cx + (float)Math.cos(var11) * r, cy + (float)Math.sin(var11) * r, cx + (float)Math.cos(var13) * r, cy + (float)Math.sin(var13) * r, thick, c
         );
      }

      NanoVGRenderer.drawCircle(cx, cy, Math.max(1.0F, r * 0.22F), c);
      double var15 = Math.toRadians(310.0);
      float var12 = cx + (float)Math.cos(var15) * (r * 0.74F);
      float var16 = cy + (float)Math.sin(var15) * (r * 0.74F);
      NanoVGRenderer.drawLine(cx, cy, var12, var16, thick, c);
   }

   public static void chatBubble(float cx, float cy, float size, float thick, Color c) {
      float var5 = size * 0.88F;
      float var6 = size * 0.58F;
      float var7 = cy - size * 0.1F - var6 / 2.0F;
      NanoVGRenderer.drawRoundedRectOutline(cx - var5 / 2.0F, var7, var5, var6, 2.5F, thick, c);
      float var8 = cx - var5 * 0.2F;
      float var9 = var7 + var6;
      float var10 = cx - var5 * 0.38F;
      float var11 = cy + size * 0.42F;
      float var12 = cx + var5 * 0.05F;
      NanoVGRenderer.drawLine(var8, var9, var10, var11, thick, c);
      NanoVGRenderer.drawLine(var10, var11, var12, var9, thick, c);
   }

   public static void sparkle(float cx, float cy, float r, Color c) {
      float var4 = r * 0.26F;
      float[] var10000 = new float[8];
      var10000[0] = cx;
      var10000[1] = cx + var4;
      var10000[2] = cx + r;
      var10000[3] = cx + var4;
      var10000[4] = cx;
      var10000[5] = cx - var4;
      var10000[6] = cx - r;
      var10000[7] = cx - var4;
      float[] var5 = var10000;
      var10000 = new float[8];
      var10000[0] = cy - r;
      var10000[1] = cy;
      var10000[2] = cy;
      var10000[3] = cy;
      var10000[4] = cy + r;
      var10000[5] = cy;
      var10000[6] = cy;
      var10000[7] = cy;
      float[] var6 = var10000;

      for (byte var7 = 0; var7 < (8); var7 += 2) {
         NanoVGRenderer.drawTriangle(cx, cy, var5[var7], var6[var7], var5[(var7 + 1) % (8)], var6[(var7 + 1) % (8)], c);
         NanoVGRenderer.drawTriangle(
            cx,
            cy,
            var5[(var7 + (7)) % (8)],
            var6[(var7 + (7)) % (8)],
            var5[var7],
            var6[var7],
            c
         );
      }
   }

   public static void sparkle(float cx, float cy, float r, int c) {
      if (c >>> (24) != 0) {
         sparkle(cx, cy, r, new Color(c, true));
      }
   }

   public static void compass(float cx, float cy, float r, float thick, Color c) {
      NanoVGRenderer.drawCircleOutline(cx, cy, r, thick, c);
      float var5 = r * 0.65F;
      float var6 = r * 0.22F;
      float var7 = 0.7071F;
      float var8 = cx + var5 * var7;
      float var9 = cy - var5 * var7;
      float var10 = cx - var5 * var7;
      float var11 = cy + var5 * var7;
      float var12 = cx - var6 * var7;
      float var13 = cy - var6 * var7;
      float var14 = cx + var6 * var7;
      float var15 = cy + var6 * var7;
      NanoVGRenderer.drawTriangle(var8, var9, var12, var13, var14, var15, c);
      Color var16 = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(c.getAlpha() * 0.55F));
      NanoVGRenderer.drawTriangle(var10, var11, var12, var13, var14, var15, var16);
   }

   public static void search(float cx, float cy, float r, float thick, Color c) {
      float var5 = r * 0.72F;
      float var6 = r * 0.22F;
      NanoVGRenderer.drawCircleOutline(cx - var6, cy - var6, var5, thick, c);
      float var7 = r * 0.32F;
      float var8 = r * 0.95F;
      NanoVGRenderer.drawLine(cx + var7, cy + var7, cx + var8, cy + var8, thick * 1.15F, c);
   }

   public static void computer(float cx, float cy, float size, Color c) {
      float var4 = size * 0.92F;
      float var5 = size * 0.62F;
      float var6 = cx - var4 / 2.0F;
      float var7 = cy - size * 0.42F;
      NanoVGRenderer.drawRoundedRectOutline(var6, var7, var4, var5, 2.0F, 1.1F, c);
      float var8 = var7 + var5;
      float var9 = var8 + size * 0.2F;
      NanoVGRenderer.drawLine(cx, var8, cx, var9, 1.2F, c);
      float var10 = size * 0.52F;
      NanoVGRenderer.drawLine(cx - var10 / 2.0F, var9, cx + var10 / 2.0F, var9, 1.2F, c);
   }

   public static void globe(float cx, float cy, float radius, float thick, Color c) {
      NanoVGRenderer.drawCircleOutline(cx, cy, radius, thick, c);
      NanoVGRenderer.drawLine(cx - radius, cy, cx + radius, cy, thick, c);
      NanoVGRenderer.drawLine(cx, cy - radius, cx, cy + radius, thick, c);
      float var5 = radius * 0.55F;
      NanoVGRenderer.drawCircleOutline(cx, cy, var5, thick, c);
   }

   public static void globe(float cx, float cy, float radius, float thick, int c) {
      NanoVGRenderer.drawCircleOutline(cx, cy, radius, thick, c);
      NanoVGRenderer.drawLine(cx - radius, cy, cx + radius, cy, thick, c);
      NanoVGRenderer.drawLine(cx, cy - radius, cx, cy + radius, thick, c);
      float var5 = radius * 0.55F;
      NanoVGRenderer.drawCircleOutline(cx, cy, var5, thick, c);
   }
}
