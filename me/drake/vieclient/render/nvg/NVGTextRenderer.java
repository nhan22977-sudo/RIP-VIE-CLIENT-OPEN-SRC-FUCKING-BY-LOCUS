package me.drake.vieclient.render.nvg;

import java.awt.Color;
import java.nio.FloatBuffer;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryStack;

public final class NVGTextRenderer {
   private static final float DEFAULT_SHADOW_OFFSET = 1.0F;
   private static final Color DEFAULT_SHADOW_COLOR = new Color(0, 0, 0, 100);
   private static final int DEFAULT_SHADOW_COLOR_INT = 1677721600;
   private static final int TEXT_ALIGN_LEFT_TOP = 9;
   private static final float CENTER_DIVISOR = 2.0F;
   private static final FrameTextMetricCache METRICS = new FrameTextMetricCache(2048);
   private static final float[] TEXT_TRANSFORM = new float[6];
   private static float devicePixelRatio = 1.0F;
   private static boolean fontVerified = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private NVGTextRenderer() {
   }

   public static void drawText(String text, float x, float y, float size, Color color) {
      drawText(text, x, y, size, color, NVGFontManager.getRegularFont());
   }

   public static void drawText(String text, float x, float y, float size, int argb) {
      drawText(text, x, y, size, argb, NVGFontManager.getRegularFont());
   }

   public static void drawText(String text, float x, float y, float size, Color color, int fontId) {
      if (text != null && !text.isEmpty() && color != null && color.getAlpha() != 0) {
         long var6 = NVGRenderer.getContext();
         if (var6 != 0L) {
            setupTextRendering(var6, fontId, size);
            renderText(var6, text, x, y, color);
         }
      }
   }

   public static void drawText(String text, float x, float y, float size, int argb, int fontId) {
      if (text != null && !text.isEmpty() && (argb >>> (24) & (255)) != 0) {
         long var6 = NVGRenderer.getContext();
         if (var6 != 0L) {
            setupTextRendering(var6, fontId, size);
            renderText(var6, text, x, y, argb);
         }
      }
   }

   public static void drawTextBold(String text, float x, float y, float size, Color color) {
      drawText(text, x, y, size, color, NVGFontManager.getBoldFont());
   }

   public static void drawTextBold(String text, float x, float y, float size, int argb) {
      drawText(text, x, y, size, argb, NVGFontManager.getBoldFont());
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, Color color) {
      drawTextWithShadow(text, x, y, size, color, NVGFontManager.getRegularFont(), 1.0F, 1.0F, DEFAULT_SHADOW_COLOR);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, int argb) {
      drawTextWithShadow(text, x, y, size, argb, NVGFontManager.getRegularFont(), 1.0F, 1.0F, 1677721600);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, Color color, int fontId) {
      drawTextWithShadow(text, x, y, size, color, fontId, 1.0F, 1.0F, DEFAULT_SHADOW_COLOR);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, int argb, int fontId) {
      drawTextWithShadow(text, x, y, size, argb, fontId, 1.0F, 1.0F, 1677721600);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, Color color, float shadowOffsetX, float shadowOffsetY, Color shadowColor) {
      drawTextWithShadow(text, x, y, size, color, NVGFontManager.getRegularFont(), shadowOffsetX, shadowOffsetY, shadowColor);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, int argb, float shadowOffsetX, float shadowOffsetY, Color shadowColor) {
      drawTextWithShadow(text, x, y, size, argb, NVGFontManager.getRegularFont(), shadowOffsetX, shadowOffsetY, shadowColor);
   }

   public static void drawTextWithShadow(String text, float x, float y, float size, int argb, float shadowOffsetX, float shadowOffsetY, int shadowColor) {
      drawTextWithShadow(text, x, y, size, argb, NVGFontManager.getRegularFont(), shadowOffsetX, shadowOffsetY, shadowColor);
   }

   public static void drawTextWithShadow(
      String text, float x, float y, float size, Color color, int fontId, float shadowOffsetX, float shadowOffsetY, Color shadowColor
   ) {
      if (text != null && !text.isEmpty()) {
         if (isRenderingValid()) {
            long var9 = NVGRenderer.getContext();
            setupTextRendering(var9, fontId, size);
            renderText(var9, text, x + shadowOffsetX, y + shadowOffsetY, shadowColor);
            renderText(var9, text, x, y, color);
         }
      }
   }

   public static void drawTextWithShadow(
      String text, float x, float y, float size, int argb, int fontId, float shadowOffsetX, float shadowOffsetY, Color shadowColor
   ) {
      if (text != null && !text.isEmpty() && (argb >>> (24) & (255)) != 0) {
         if (isRenderingValid()) {
            long var9 = NVGRenderer.getContext();
            setupTextRendering(var9, fontId, size);
            renderText(var9, text, x + shadowOffsetX, y + shadowOffsetY, shadowColor);
            renderText(var9, text, x, y, argb);
         }
      }
   }

   public static void drawTextWithShadow(
      String text, float x, float y, float size, int argb, int fontId, float shadowOffsetX, float shadowOffsetY, int shadowColor
   ) {
      if (text != null && !text.isEmpty() && (argb >>> (24) & (255)) != 0) {
         if (isRenderingValid()) {
            long var9 = NVGRenderer.getContext();
            setupTextRendering(var9, fontId, size);
            if ((shadowColor >>> (24) & (255)) > 0) {
               renderText(var9, text, x + shadowOffsetX, y + shadowOffsetY, shadowColor);
            }

            renderText(var9, text, x, y, argb);
         }
      }
   }

   public static void drawCenteredText(String text, float x, float y, float size, Color color) {
      drawCenteredText(text, x, y, size, color, NVGFontManager.getRegularFont());
   }

   public static void drawCenteredText(String text, float x, float y, float size, int argb) {
      drawCenteredText(text, x, y, size, argb, NVGFontManager.getRegularFont());
   }

   public static void drawCenteredText(String text, float x, float y, float size, Color color, int fontId) {
      if (text != null && !text.isEmpty() && color != null && color.getAlpha() != 0) {
         float var6 = getTextWidth(text, size, fontId);
         float var7 = x - var6 / 2.0F;
         drawText(text, var7, y, size, color, fontId);
      }
   }

   public static void drawCenteredText(String text, float x, float y, float size, int argb, int fontId) {
      if (text != null && !text.isEmpty() && (argb >>> (24) & (255)) != 0) {
         float var6 = getTextWidth(text, size, fontId);
         float var7 = x - var6 / 2.0F;
         drawText(text, var7, y, size, argb, fontId);
      }
   }

   public static void drawCenteredTextWithShadow(String text, float x, float y, float size, Color color) {
      drawCenteredTextWithShadow(text, x, y, size, color, NVGFontManager.getRegularFont());
   }

   public static void drawCenteredTextWithShadow(String text, float x, float y, float size, int argb) {
      drawCenteredTextWithShadow(text, x, y, size, argb, NVGFontManager.getRegularFont());
   }

   public static void drawCenteredTextWithShadow(String text, float x, float y, float size, Color color, int fontId) {
      if (text != null && !text.isEmpty() && color != null && color.getAlpha() != 0) {
         float var6 = getTextWidth(text, size, fontId);
         float var7 = x - var6 / 2.0F;
         drawTextWithShadow(text, var7, y, size, color, fontId);
      }
   }

   public static void drawCenteredTextWithShadow(String text, float x, float y, float size, int argb, int fontId) {
      if (text != null && !text.isEmpty() && (argb >>> (24) & (255)) != 0) {
         float var6 = getTextWidth(text, size, fontId);
         float var7 = x - var6 / 2.0F;
         drawTextWithShadow(text, var7, y, size, argb, fontId);
      }
   }

   public static float getTextWidth(String text, float size) {
      return getTextWidth(text, size, NVGFontManager.getRegularFont());
   }

   public static float getTextWidth(String text, float size, int fontId) {
      long var3 = NVGRenderer.getContext();
      if (var3 != 0L && text != null && !text.isEmpty()) {
         int var5 = fontId >= 0 ? fontId : NVGFontManager.getRegularFont();
         if (var5 < 0) {
            var5 = 0;
         }

         float var6 = quantizeFontSize(size);
         int var7 = var5;
         NanoVG.nvgCurrentTransform(var3, TEXT_TRANSFORM);
         float var8 = fontRasterScale(TEXT_TRANSFORM, devicePixelRatio);
         if (var8 > 0.0F && Float.isFinite(var8)) {
            float var9 = METRICS.get(text, var6, var7, var8);
            if (var9 >= 0.0F) {
               return var9;
            }

            float var10 = measureTextWidthNative(var3, text, var6, var7);
            METRICS.put(text, var6, var7, var8, var10);
            return var10;
         } else {
            return 0.0F;
         }
      } else {
         return 0.0F;
      }
   }

   static void beginFrame() {
      METRICS.beginFrame();
   }

   static void setDevicePixelRatio(float ratio) {
      devicePixelRatio = ratio;
   }

   public static void invalidateMetrics() {
      METRICS.clear();
   }

   static float fontRasterScale(float[] transform, float pixelRatio) {
      float var2 = (float)Math.sqrt(transform[0] * transform[0] + transform[2] * transform[2]);
      float var3 = (float)Math.sqrt(transform[1] * transform[1] + transform[3] * transform[3]);
      float var4 = (var2 + var3) * 0.5F;
      return Math.min((int)(var4 / 0.01F + 0.5F) * 0.01F, 4.0F) * pixelRatio;
   }

   private static float measureTextWidthNative(long context, String text, float size, int fontId) {
      MemoryStack var5 = MemoryStack.stackPush();

      float var7;
      try {
         FloatBuffer var6 = var5.mallocFloat(4);
         NanoVG.nvgSave(context);

         try {
            setupTextRendering(context, fontId, size);
            NanoVG.nvgTextBounds(context, 0.0F, 0.0F, text, var6);
            var7 = var6.get(2) - var6.get(0);
         } finally {
            NanoVG.nvgRestore(context);
         }
      } catch (Throwable var14) {
         if (var5 != null) {
            try {
               var5.close();
            } catch (Throwable var12) {
               var14.addSuppressed(var12);
            }
         }

         throw var14;
      }

      if (var5 != null) {
         var5.close();
      }

      return var7;
   }

   public static float getTextHeight(float size) {
      return size;
   }

   private static boolean isRenderingValid() {
      return NVGRenderer.isInFrame();
   }

   private static float quantizeFontSize(float size) {
      return Math.max(1.0F, Math.round(size));
   }

   private static void setupTextRendering(long context, int fontId, float size) {
      if (!fontVerified) {
         fontVerified = true;
         NVGFontManager.loadFonts();
      }

      int var4 = fontId >= 0 ? fontId : NVGFontManager.getRegularFont();
      if (var4 < 0) {
         var4 = 0;
      }

      NanoVG.nvgFontFaceId(context, var4);
      NanoVG.nvgFontSize(context, quantizeFontSize(size));
      NanoVG.nvgTextAlign(context, 9);
      NanoVG.nvgTextLetterSpacing(context, 0.0F);
      NanoVG.nvgFontBlur(context, 0.0F);
   }

   private static void renderText(long context, String text, float x, float y, Color color) {
      NVGRenderer.applyColor(color, NVGRenderer.NVG_COLOR_1);
      NanoVG.nvgFillColor(context, NVGRenderer.NVG_COLOR_1);
      NanoVG.nvgText(context, x, y, text);
   }

   private static void renderText(long context, String text, float x, float y, int argb) {
      NVGRenderer.applyColor(argb, NVGRenderer.NVG_COLOR_1);
      NanoVG.nvgFillColor(context, NVGRenderer.NVG_COLOR_1);
      NanoVG.nvgText(context, x, y, text);
   }
}
