package me.drake.vieclient.render.nvg;

import java.awt.Color;
import java.nio.charset.StandardCharsets;

public final class NanoVGRenderer {
   private static final int FALLBACK_FONT_ID = 1;
   private static final ThreadLocal USE_TITLE_FONT = ThreadLocal.withInitial(() -> Boolean.FALSE);
   private static final ThreadLocal HSB_SCRATCH = ThreadLocal.withInitial(() -> new float[3]);
   private static final int VIVID_CACHE_SIZE = 32;
   private static final int[] VIVID_IN = new int[32];
   private static final int[] VIVID_OUT = new int[32];
   private static final boolean[] VIVID_VALID = new boolean[32];
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static void setUseTitleFont(boolean v) {
      USE_TITLE_FONT.set(v);
   }

   public static boolean isUseTitleFont() {
      return Boolean.TRUE.equals(USE_TITLE_FONT.get());
   }

   public static void withTitleFont(Runnable body) {
      boolean var1 = isUseTitleFont();
      USE_TITLE_FONT.set(true);

      try {
         body.run();
      } finally {
         USE_TITLE_FONT.set(var1);
      }
   }

   public static void init() {
      GLStateManager.setup();
      NVGFontManager.loadFonts();
   }

   public static void reinit() {
      init();
   }

   public static boolean isInFrame() {
      return NVGRenderer.isInFrame();
   }

   public static NanoVGFrame frame() {
      return NVGRenderer.frame();
   }

   public static void prepareFrame() {
      NVGRenderer.prepareFrame();
   }

   public static boolean beginFrame() {
      return NVGRenderer.beginFrame();
   }

   public static void endFrame() {
      NVGRenderer.endFrame();
   }

   public static void globalAlpha(float alpha) {
      NVGRenderer.globalAlpha(alpha);
   }

   public static void drawRect(float x, float y, float width, float height, Color color) {
      NVGRenderer.drawRect(x, y, width, height, color);
   }

   public static void drawRect(float x, float y, float width, float height, int argb) {
      NVGRenderer.drawRect(x, y, width, height, argb);
   }

   public static void drawRoundedRect(float x, float y, float width, float height, float radius, Color color) {
      NVGRenderer.drawRoundedRect(x, y, width, height, radius, color);
   }

   public static void drawRoundedRect(float x, float y, float width, float height, float radius, int argb) {
      NVGRenderer.drawRoundedRect(x, y, width, height, radius, argb);
   }

   public static void drawRoundedRectVarying(
      float x, float y, float width, float height, float radiusTopLeft, float radiusTopRight, float radiusBottomRight, float radiusBottomLeft, Color color
   ) {
      NVGRenderer.drawRoundedRectVarying(x, y, width, height, radiusTopLeft, radiusTopRight, radiusBottomRight, radiusBottomLeft, color);
   }

   public static void drawRoundedRectVarying(
      float x, float y, float width, float height, float radiusTopLeft, float radiusTopRight, float radiusBottomRight, float radiusBottomLeft, int argb
   ) {
      NVGRenderer.drawRoundedRectVarying(x, y, width, height, radiusTopLeft, radiusTopRight, radiusBottomRight, radiusBottomLeft, argb);
   }

   public static void drawRoundedRectOutline(float x, float y, float width, float height, float radius, float strokeWidth, Color color) {
      NVGRenderer.drawRoundedRectOutline(x, y, width, height, radius, strokeWidth, color);
   }

   public static void drawRoundedRectOutline(float x, float y, float width, float height, float radius, float strokeWidth, int argb) {
      NVGRenderer.drawRoundedRectOutline(x, y, width, height, radius, strokeWidth, argb);
   }

   public static void drawRoundedRectOutlineVarying(
      float x, float y, float width, float height, float tl, float tr, float br, float bl, float strokeWidth, Color color
   ) {
      NVGRenderer.drawRoundedRectOutlineVarying(x, y, width, height, tl, tr, br, bl, strokeWidth, color);
   }

   public static void drawRoundedRectOutlineVarying(
      float x, float y, float width, float height, float tl, float tr, float br, float bl, float strokeWidth, int argb
   ) {
      NVGRenderer.drawRoundedRectOutlineVarying(x, y, width, height, tl, tr, br, bl, strokeWidth, argb);
   }

   public static void drawRoundedRectGradient(float x, float y, float width, float height, float radius, Color colorTop, Color colorBottom) {
      NVGRenderer.drawRoundedRectGradient(x, y, width, height, radius, colorTop, colorBottom);
   }

   public static void drawRoundedRectGradient(float x, float y, float width, float height, float radius, int colorTop, int colorBottom) {
      NVGRenderer.drawRoundedRectGradient(x, y, width, height, radius, colorTop, colorBottom);
   }

   public static void drawVerticalGradientRect(float x, float y, float width, float height, Color colorTop, Color colorBottom) {
      NVGRenderer.drawVerticalGradientRect(x, y, width, height, colorTop, colorBottom);
   }

   public static void drawVerticalGradientRect(float x, float y, float width, float height, int colorTop, int colorBottom) {
      NVGRenderer.drawVerticalGradientRect(x, y, width, height, colorTop, colorBottom);
   }

   public static void drawVerticalGradientRoundedRect(float x, float y, float width, float height, float radius, Color colorTop, Color colorBottom) {
      NVGRenderer.drawVerticalGradientRoundedRect(x, y, width, height, radius, colorTop, colorBottom);
   }

   public static void drawVerticalGradientRoundedRect(float x, float y, float width, float height, float radius, int colorTop, int colorBottom) {
      NVGRenderer.drawVerticalGradientRoundedRect(x, y, width, height, radius, colorTop, colorBottom);
   }

   public static void drawSquircle(float x, float y, float width, float height, float radius, Color color) {
      NVGRenderer.drawSquircle(x, y, width, height, radius, color);
   }

   public static void drawSquircle(float x, float y, float width, float height, float radius, int argb) {
      NVGRenderer.drawSquircle(x, y, width, height, radius, argb);
   }

   public static void drawSquircleOutline(float x, float y, float width, float height, float radius, float strokeWidth, Color color) {
      NVGRenderer.drawSquircleOutline(x, y, width, height, radius, strokeWidth, color);
   }

   public static void drawSquircleOutline(float x, float y, float width, float height, float radius, float strokeWidth, int argb) {
      NVGRenderer.drawSquircleOutline(x, y, width, height, radius, strokeWidth, argb);
   }

   public static void drawSquircleGradient(float x, float y, float width, float height, float radius, Color colorTop, Color colorBottom) {
      NVGRenderer.drawSquircleGradient(x, y, width, height, radius, colorTop, colorBottom);
   }

   public static void drawSquircleGradient(float x, float y, float width, float height, float radius, int colorTop, int colorBottom) {
      NVGRenderer.drawSquircleGradient(x, y, width, height, radius, colorTop, colorBottom);
   }

   public static void drawSquircleRadialGlow(float x, float y, float width, float height, float radius, float gx, float gy, float glowR, Color glow) {
      NVGRenderer.drawSquircleRadialGlow(x, y, width, height, radius, gx, gy, glowR, glow);
   }

   public static void drawSquircleRadialGlow(float x, float y, float width, float height, float radius, float gx, float gy, float glowR, int glowArgb) {
      NVGRenderer.drawSquircleRadialGlow(x, y, width, height, radius, gx, gy, glowR, glowArgb);
   }

   public static void drawSquircleBackdrop(float x, float y, float width, float height, float radius, int img, float scrW, float scrH, float alpha, float mag) {
      NVGRenderer.drawSquircleBackdrop(x, y, width, height, radius, img, scrW, scrH, alpha, mag);
   }

   public static void drawRoundedRectWithShadow(
      float x, float y, float width, float height, float radius, Color color, Color shadowColor, float shadowBlur, float shadowSpread
   ) {
      NVGRenderer.drawRoundedRectWithShadow(x, y, width, height, radius, color, shadowColor, shadowBlur, shadowSpread);
   }

   public static void drawRoundedRectWithShadow(
      float x, float y, float width, float height, float radius, int color, int shadowColor, float shadowBlur, float shadowSpread
   ) {
      NVGRenderer.drawRoundedRectWithShadow(x, y, width, height, radius, color, shadowColor, shadowBlur, shadowSpread);
   }

   public static void drawCircle(float x, float y, float radius, Color color) {
      NVGRenderer.drawCircle(x, y, radius, color);
   }

   public static void drawCircle(float x, float y, float radius, int argb) {
      NVGRenderer.drawCircle(x, y, radius, argb);
   }

   public static void drawRotatedRoundedRect(float x, float y, float width, float height, float radius, Color color, float rotation) {
      NVGRenderer.drawRotatedRoundedRect(x, y, width, height, radius, color, rotation);
   }

   public static void drawCircleOutline(float x, float y, float radius, float strokeWidth, Color color) {
      NVGRenderer.drawCircleOutline(x, y, radius, strokeWidth, color);
   }

   public static void drawCircleOutline(float x, float y, float radius, float strokeWidth, int argb) {
      NVGRenderer.drawCircleOutline(x, y, radius, strokeWidth, argb);
   }

   public static void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, Color color) {
      NVGRenderer.drawTriangle(x1, y1, x2, y2, x3, y3, color);
   }

   public static void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int argb) {
      NVGRenderer.drawTriangle(x1, y1, x2, y2, x3, y3, argb);
   }

   public static void drawTriangleOutline(float x1, float y1, float x2, float y2, float x3, float y3, float strokeWidth, Color color) {
      NVGRenderer.drawTriangleOutline(x1, y1, x2, y2, x3, y3, strokeWidth, color);
   }

   public static void drawTriangleOutline(float x1, float y1, float x2, float y2, float x3, float y3, float strokeWidth, int argb) {
      NVGRenderer.drawTriangleOutline(x1, y1, x2, y2, x3, y3, strokeWidth, argb);
   }

   public static void drawRadialGlow(float cx, float cy, float radius, Color glow) {
      NVGRenderer.drawRadialGlow(cx, cy, radius, glow);
   }

   public static void drawRadialGlow(float cx, float cy, float radius, int argb) {
      NVGRenderer.drawRadialGlow(cx, cy, radius, argb);
   }

   public static void drawArc(float cx, float cy, float radius, float a0, float a1, float strokeWidth, Color color) {
      NVGRenderer.drawArc(cx, cy, radius, a0, a1, strokeWidth, color);
   }

   public static void drawArc(float cx, float cy, float radius, float a0, float a1, float strokeWidth, int argb) {
      NVGRenderer.drawArc(cx, cy, radius, a0, a1, strokeWidth, argb);
   }

   public static void drawLine(float x1, float y1, float x2, float y2, float strokeWidth, Color color) {
      NVGRenderer.drawLine(x1, y1, x2, y2, strokeWidth, color);
   }

   public static void drawLine(float x1, float y1, float x2, float y2, float strokeWidth, int argb) {
      NVGRenderer.drawLine(x1, y1, x2, y2, strokeWidth, argb);
   }

   public static void drawSpotifyLogo(float x, float y, float size, Color discColor, Color waveColor) {
      NVGRenderer.drawSpotifyLogo(x, y, size, discColor, waveColor);
   }

   public static void drawSpotifyWaves(float x, float y, float size, Color waveColor) {
      NVGRenderer.drawSpotifyLogo(x, y, size, null, waveColor);
   }

   public static void beginLineBatch(Color color, float strokeWidth) {
      NVGRenderer.beginLineBatch(color, strokeWidth);
   }

   public static void beginLineBatch(int argb, float strokeWidth) {
      NVGRenderer.beginLineBatch(argb, strokeWidth);
   }

   public static void batchLine(float x1, float y1, float x2, float y2) {
      NVGRenderer.batchLine(x1, y1, x2, y2);
   }

   public static void endLineBatch() {
      NVGRenderer.endLineBatch();
   }

   private static Color glowOf(Color c) {
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0, Math.min(255, (int)(c.getAlpha() * 0.38F))));
   }

   private static int glowOf(int argb) {
      int var1 = argb >>> (24) & (255);
      int var2 = Math.max(0, Math.min(255, (int)(var1 * 0.38F)));
      return var2 << (24) | argb & (16777215);
   }

   static Color vividCore(Color c) {
      if (c == null) {
         return null;
      }

      float[] var1 = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), (float[])HSB_SCRATCH.get());
      float var2 = var1[1] > 0.05F ? Math.max(var1[1], 0.8F) : var1[1];
      float var3 = Math.max(var1[2], 0.95F);
      int var4 = Color.HSBtoRGB(var1[0], var2, var3);
      return new Color(
         var4 >> (16) & (255), var4 >> (8) & (255), var4 & (255), c.getAlpha()
      );
   }

   static int vividCore(int argb) {
      int var1 = argb >>> (24) & (255);
      if (var1 == 0) {
         return 0;
      }

      int var2 = (argb ^ argb >>> (16)) & (31);
      if (VIVID_VALID[var2] && VIVID_IN[var2] == argb) {
         return VIVID_OUT[var2];
      }

      int var3 = argb >> (16) & (255);
      int var4 = argb >> (8) & (255);
      int var5 = argb & (255);
      float[] var6 = Color.RGBtoHSB(var3, var4, var5, (float[])HSB_SCRATCH.get());
      float var7 = var6[1] > 0.05F ? Math.max(var6[1], 0.8F) : var6[1];
      float var8 = Math.max(var6[2], 0.95F);
      int var9 = Color.HSBtoRGB(var6[0], var7, var8) & (16777215);
      int var10 = var1 << (24) | var9;
      VIVID_IN[var2] = argb;
      VIVID_OUT[var2] = var10;
      VIVID_VALID[var2] = true;
      return var10;
   }

   public static void drawEspBox(double[][] proj, int[][] edges, double guiScale, float width, Color color) {
      if (color != null && color.getAlpha() > 0 && !(width <= 0.0F)) {
         Color var6 = vividCore(color);
         float var7 = Math.max(0.6F, width * 0.85F);
         float var8 = width * 2.6F;
         float var9 = (float)guiScale;
         NVGRenderer.beginLineBatch(glowOf(var6), var8);

         for (int[] var13 : edges) {
            double[] var14 = proj[var13[0]];
            double[] var15 = proj[var13[1]];
            if (var14 != null && var15 != null) {
               NVGRenderer.batchLine((float)(var14[0] / var9), (float)(var14[1] / var9), (float)(var15[0] / var9), (float)(var15[1] / var9));
            }
         }

         NVGRenderer.endLineBatch();
         NVGRenderer.beginLineBatch(var6, var7);

         for (int[] var19 : edges) {
            double[] var20 = proj[var19[0]];
            double[] var21 = proj[var19[1]];
            if (var20 != null && var21 != null) {
               NVGRenderer.batchLine((float)(var20[0] / var9), (float)(var20[1] / var9), (float)(var21[0] / var9), (float)(var21[1] / var9));
            }
         }

         NVGRenderer.endLineBatch();
      }
   }

   public static void drawEspBox(double[][] proj, int[][] edges, double guiScale, float width, int color) {
      if ((color >>> (24) & (255)) > 0 && !(width <= 0.0F)) {
         int var6 = vividCore(color);
         float var7 = Math.max(0.6F, width * 0.85F);
         float var8 = width * 2.6F;
         float var9 = (float)guiScale;
         NVGRenderer.beginLineBatch(glowOf(var6), var8);

         for (int[] var13 : edges) {
            double[] var14 = proj[var13[0]];
            double[] var15 = proj[var13[1]];
            if (var14 != null && var15 != null) {
               NVGRenderer.batchLine((float)(var14[0] / var9), (float)(var14[1] / var9), (float)(var15[0] / var9), (float)(var15[1] / var9));
            }
         }

         NVGRenderer.endLineBatch();
         NVGRenderer.beginLineBatch(var6, var7);

         for (int[] var19 : edges) {
            double[] var20 = proj[var19[0]];
            double[] var21 = proj[var19[1]];
            if (var20 != null && var21 != null) {
               NVGRenderer.batchLine((float)(var20[0] / var9), (float)(var20[1] / var9), (float)(var21[0] / var9), (float)(var21[1] / var9));
            }
         }

         NVGRenderer.endLineBatch();
      }
   }

   public static void drawEspTracer(float x1, float y1, float x2, float y2, float width, Color color) {
      if (color != null && color.getAlpha() > 0 && !(width <= 0.0F)) {
         Color var6 = vividCore(color);
         float var7 = Math.max(0.6F, width * 0.85F);
         float var8 = width * 2.6F;
         NVGRenderer.beginLineBatch(glowOf(var6), var8);
         NVGRenderer.batchLine(x1, y1, x2, y2);
         NVGRenderer.endLineBatch();
         NVGRenderer.beginLineBatch(var6, var7);
         NVGRenderer.batchLine(x1, y1, x2, y2);
         NVGRenderer.endLineBatch();
      }
   }

   public static void drawEspTracer(float x1, float y1, float x2, float y2, float width, int color) {
      if ((color >>> (24) & (255)) > 0 && !(width <= 0.0F)) {
         int var6 = vividCore(color);
         float var7 = Math.max(0.6F, width * 0.85F);
         float var8 = width * 2.6F;
         NVGRenderer.beginLineBatch(glowOf(var6), var8);
         NVGRenderer.batchLine(x1, y1, x2, y2);
         NVGRenderer.endLineBatch();
         NVGRenderer.beginLineBatch(var6, var7);
         NVGRenderer.batchLine(x1, y1, x2, y2);
         NVGRenderer.endLineBatch();
      }
   }

   public static void beginFillBatch(Color color) {
      NVGRenderer.beginFillBatch(vividCore(color));
   }

   public static void beginFillBatch(int color) {
      NVGRenderer.beginFillBatch(vividCore(color));
   }

   public static void batchTri(float x1, float y1, float x2, float y2, float x3, float y3) {
      NVGRenderer.batchTri(x1, y1, x2, y2, x3, y3);
   }

   public static void endFillBatch() {
      NVGRenderer.endFillBatch();
   }

   public static void drawEspFill(double[][] proj, int[][] faces, double guiScale, Color fill) {
      if (fill != null && fill.getAlpha() > 0) {
         float var5 = (float)guiScale;
         NVGRenderer.beginFillBatch(vividCore(fill));

         for (int[] var9 : faces) {
            double[] var10 = proj[var9[0]];
            double[] var11 = proj[var9[1]];
            double[] var12 = proj[var9[2]];
            double[] var13 = proj[var9[3]];
            if (var10 != null && var11 != null && var12 != null && var13 != null) {
               NVGRenderer.batchTri(
                  (float)(var10[0] / var5),
                  (float)(var10[1] / var5),
                  (float)(var11[0] / var5),
                  (float)(var11[1] / var5),
                  (float)(var12[0] / var5),
                  (float)(var12[1] / var5)
               );
               NVGRenderer.batchTri(
                  (float)(var10[0] / var5),
                  (float)(var10[1] / var5),
                  (float)(var12[0] / var5),
                  (float)(var12[1] / var5),
                  (float)(var13[0] / var5),
                  (float)(var13[1] / var5)
               );
            }
         }

         NVGRenderer.endFillBatch();
      }
   }

   public static void drawEspFill(double[][] proj, int[][] faces, double guiScale, int fill) {
      if ((fill >>> (24) & (255)) > 0) {
         float var5 = (float)guiScale;
         NVGRenderer.beginFillBatch(vividCore(fill));

         for (int[] var9 : faces) {
            double[] var10 = proj[var9[0]];
            double[] var11 = proj[var9[1]];
            double[] var12 = proj[var9[2]];
            double[] var13 = proj[var9[3]];
            if (var10 != null && var11 != null && var12 != null && var13 != null) {
               NVGRenderer.batchTri(
                  (float)(var10[0] / var5),
                  (float)(var10[1] / var5),
                  (float)(var11[0] / var5),
                  (float)(var11[1] / var5),
                  (float)(var12[0] / var5),
                  (float)(var12[1] / var5)
               );
               NVGRenderer.batchTri(
                  (float)(var10[0] / var5),
                  (float)(var10[1] / var5),
                  (float)(var12[0] / var5),
                  (float)(var12[1] / var5),
                  (float)(var13[0] / var5),
                  (float)(var13[1] / var5)
               );
            }
         }

         NVGRenderer.endFillBatch();
      }
   }

   public static void drawText(String text, float x, float y, float size, Color color) {
      int var5 = isUseTitleFont() ? NVGFontManager.getTitleFont() : NVGFontManager.getRegularFont();
      if (var5 < 0) {
         var5 = NVGFontManager.getRegularFont();
      }

      NVGTextRenderer.drawText(text, x, y, size, color, var5);
   }

   public static void drawText(String text, float x, float y, float size, int argb) {
      int var5 = isUseTitleFont() ? NVGFontManager.getTitleFont() : NVGFontManager.getRegularFont();
      if (var5 < 0) {
         var5 = NVGFontManager.getRegularFont();
      }

      NVGTextRenderer.drawText(text, x, y, size, argb, var5);
   }

   public static void drawText(String text, float x, float y, float size, Color color, boolean bold) {
      int var6 = isUseTitleFont() ? (bold ? NVGFontManager.getTitleBoldFont() : NVGFontManager.getTitleFont()) : NVGFontManager.getFontId(bold);
      NVGTextRenderer.drawText(text, x, y, size, color, var6);
   }

   public static void drawText(String text, float x, float y, float size, int argb, boolean bold) {
      int var6 = isUseTitleFont() ? (bold ? NVGFontManager.getTitleBoldFont() : NVGFontManager.getTitleFont()) : NVGFontManager.getFontId(bold);
      NVGTextRenderer.drawText(text, x, y, size, argb, var6);
   }

   public static void drawTextWithFont(String text, float x, float y, float size, Color color, int fontId) {
      NVGTextRenderer.drawText(text, x, y, size, color, fontId);
   }

   public static void drawTextWithFont(String text, float x, float y, float size, int argb, int fontId) {
      NVGTextRenderer.drawText(text, x, y, size, argb, fontId);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, Color color) {
      NVGTextRenderer.drawTextWithShadow(text, x, y, size, color);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, int argb) {
      NVGTextRenderer.drawTextWithShadow(text, x, y, size, argb);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, Color color, int fontId) {
      NVGTextRenderer.drawTextWithShadow(text, x, y, size, color, fontId);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, int argb, int fontId) {
      NVGTextRenderer.drawTextWithShadow(text, x, y, size, argb, fontId);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, Color color, float shadowOffsetX, float shadowOffsetY, Color shadowColor) {
      NVGTextRenderer.drawTextWithShadow(text, x, y, size, color, shadowOffsetX, shadowOffsetY, shadowColor);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, int argb, float shadowOffsetX, float shadowOffsetY, Color shadowColor) {
      NVGTextRenderer.drawTextWithShadow(text, x, y, size, argb, shadowOffsetX, shadowOffsetY, shadowColor);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, int argb, float shadowOffsetX, float shadowOffsetY, int shadowColor) {
      NVGTextRenderer.drawTextWithShadow(text, x, y, size, argb, shadowOffsetX, shadowOffsetY, shadowColor);
   }

   public static void drawTextWithShadow(
      String text, float x, float y, float size, Color color, int fontId, float shadowOffsetX, float shadowOffsetY, Color shadowColor
   ) {
      NVGTextRenderer.drawTextWithShadow(text, x, y, size, color, fontId, shadowOffsetX, shadowOffsetY, shadowColor);
   }

   public static void drawTextWithShadow(
      String text, float x, float y, float size, int argb, int fontId, float shadowOffsetX, float shadowOffsetY, Color shadowColor
   ) {
      NVGTextRenderer.drawTextWithShadow(text, x, y, size, argb, fontId, shadowOffsetX, shadowOffsetY, shadowColor);
   }

   public static void drawTextWithShadow(
      String text, float x, float y, float size, int argb, int fontId, float shadowOffsetX, float shadowOffsetY, int shadowColor
   ) {
      NVGTextRenderer.drawTextWithShadow(text, x, y, size, argb, fontId, shadowOffsetX, shadowOffsetY, shadowColor);
   }

   public static void drawCenteredText(String text, float x, float y, float size, Color color) {
      NVGTextRenderer.drawCenteredText(text, x, y, size, color);
   }

   public static void drawCenteredText(String text, float x, float y, float size, int argb) {
      NVGTextRenderer.drawCenteredText(text, x, y, size, argb);
   }

   public static void drawCenteredText(String text, float x, float y, float size, Color color, int fontId) {
      NVGTextRenderer.drawCenteredText(text, x, y, size, color, fontId);
   }

   public static void drawCenteredText(String text, float x, float y, float size, int argb, int fontId) {
      NVGTextRenderer.drawCenteredText(text, x, y, size, argb, fontId);
   }

   public static void drawCenteredTextWithShadow(String text, float x, float y, float size, Color color) {
      NVGTextRenderer.drawCenteredTextWithShadow(text, x, y, size, color);
   }

   public static void drawCenteredTextWithShadow(String text, float x, float y, float size, int argb) {
      NVGTextRenderer.drawCenteredTextWithShadow(text, x, y, size, argb);
   }

   public static void drawCenteredTextWithShadow(String text, float x, float y, float size, Color color, int fontId) {
      NVGTextRenderer.drawCenteredTextWithShadow(text, x, y, size, color, fontId);
   }

   public static void drawCenteredTextWithShadow(String text, float x, float y, float size, int argb, int fontId) {
      NVGTextRenderer.drawCenteredTextWithShadow(text, x, y, size, argb, fontId);
   }

   public static void drawIcon(String icon, float x, float y, float size, Color color) {
      int var5 = NVGFontManager.getIconFont();
      NVGTextRenderer.drawText(icon, x, y, size, color, var5);
   }

   public static float getTextWidth(String text, float size) {
      int var2 = isUseTitleFont() ? NVGFontManager.getTitleFont() : NVGFontManager.getRegularFont();
      if (var2 < 0) {
         var2 = NVGFontManager.getRegularFont();
      }

      return NVGTextRenderer.getTextWidth(text, size, var2);
   }

   public static float getTextWidth(String text, float size, boolean bold) {
      int var3 = isUseTitleFont() ? (bold ? NVGFontManager.getTitleBoldFont() : NVGFontManager.getTitleFont()) : NVGFontManager.getFontId(bold);
      return NVGTextRenderer.getTextWidth(text, size, var3);
   }

   public static float getTextWidthWithFont(String text, float size, int fontId) {
      return NVGTextRenderer.getTextWidth(text, size, fontId);
   }

   public static float getTextHeight(float size) {
      return NVGTextRenderer.getTextHeight(size);
   }

   public static int getRegularFontId() {
      return getFontWithFallback(NVGFontManager.getRegularFont());
   }

   public static int loadImageFile(String absPath) {
      return NVGImage.loadImageFile(absPath);
   }

   public static int loadImage(String path) {
      return NVGImage.loadImage(path);
   }

   public static void drawImage(int imageId, float x, float y, float width, float height) {
      NVGImage.drawImage(imageId, x, y, width, height);
   }

   public static void drawImage(int imageId, float x, float y, float width, float height, float alpha) {
      NVGImage.drawImage(imageId, x, y, width, height, alpha);
   }

   public static void drawImage(int imageId, float x, float y, float width, float height, int argb) {
      NVGImage.drawImage(imageId, x, y, width, height, argb);
   }

   public static void drawImage(int imageId, float x, float y, float width, float height, Color tint) {
      NVGImage.drawImage(imageId, x, y, width, height, tint);
   }

   public static void drawRoundedImage(int imageId, float x, float y, float width, float height, float radius) {
      NVGImage.drawRoundedImage(imageId, x, y, width, height, radius);
   }

   public static void drawRoundedImage(int imageId, float x, float y, float width, float height, float radius, float alpha) {
      NVGImage.drawRoundedImage(imageId, x, y, width, height, radius, alpha);
   }

   public static void drawRoundedImage(int imageId, float x, float y, float width, float height, float radius, int argb) {
      NVGImage.drawRoundedImage(imageId, x, y, width, height, radius, argb);
   }

   public static void drawRoundedImage(int imageId, float x, float y, float width, float height, float radius, Color tint) {
      NVGImage.drawRoundedImage(imageId, x, y, width, height, radius, tint);
   }

   public static void save() {
      NVGRenderer.save();
   }

   public static void restore() {
      NVGRenderer.restore();
   }

   public static void translate(float x, float y) {
      NVGRenderer.translate(x, y);
   }

   public static void scale(float x, float y) {
      NVGRenderer.scale(x, y);
   }

   public static void rotate(float angle) {
      NVGRenderer.rotate(angle);
   }

   public static void scissor(float x, float y, float width, float height) {
      NVGRenderer.scissor(x, y, width, height);
   }

   public static void resetScissor() {
      NVGRenderer.resetScissor();
   }

   public static void cleanup() {
      NVGRenderer.cleanup();
   }

   private static int getFontWithFallback(int fontId) {
      if (fontId > 0) {
         return fontId;
      }

      int var1 = NVGFontManager.getRegularFont();
      return var1 > 0 ? var1 : 1;
   }

   private NanoVGRenderer() {
      byte[] var10002 = new byte[50];
      var10002[0] = (byte)(110);
      var10002[1] = (byte)(-14);
      var10002[2] = (byte)(117);
      var10002[3] = (byte)(-83);
      var10002[4] = (byte)(-93);
      var10002[5] = (byte)(-55);
      var10002[6] = (byte)(51);
      var10002[7] = (byte)(64);
      var10002[8] = (byte)(51);
      var10002[9] = (byte)(-126);
      var10002[10] = (byte)(-111);
      var10002[11] = (byte)(-126);
      var10002[12] = (byte)(-78);
      var10002[13] = (byte)(20);
      var10002[14] = 1;
      var10002[15] = (byte)(108);
      var10002[16] = (byte)(19);
      var10002[17] = (byte)(106);
      var10002[18] = (byte)(-81);
      var10002[19] = (byte)(-62);
      var10002[20] = (byte)(82);
      var10002[21] = (byte)(35);
      var10002[22] = (byte)(99);
      var10002[23] = (byte)(16);
      var10002[24] = (byte)(-29);
      var10002[25] = (byte)(124);
      var10002[26] = (byte)(-16);
      var10002[27] = (byte)(102);
      var10002[28] = (byte)(104);
      var10002[29] = (byte)(73);
      var10002[30] = (byte)(86);
      var10002[31] = (byte)(-122);
      var10002[32] = (byte)(-75);
      var10002[33] = (byte)(78);
      var10002[34] = (byte)(92);
      var10002[35] = (byte)(28);
      var10002[36] = (byte)(6);
      var10002[37] = (byte)(32);
      var10002[38] = (byte)(-119);
      var10002[39] = (byte)(-18);
      var10002[40] = (byte)(-127);
      var10002[41] = (byte)(-74);
      var10002[42] = (byte)(37);
      var10002[43] = (byte)(120);
      var10002[44] = (byte)(-49);
      var10002[45] = (byte)(-79);
      var10002[46] = (byte)(-23);
      var10002[47] = (byte)(-52);
      var10002[48] = (byte)(111);
      var10002[49] = (byte)(-114);
      throw new UnsupportedOperationException($$v_s(var10002, 5207144382992732191L));
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }
}
