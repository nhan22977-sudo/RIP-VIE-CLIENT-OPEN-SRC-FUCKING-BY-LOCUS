package me.drake.vieclient.render.nvg;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import me.drake.vieclient.render.FrostedBackground;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.GL30;

public final class NVGRenderer {
   private static final class_310 mc = class_310.method_1551();
   private static final float COLOR_DIVISOR = 255.0F;
   private static final long VG = NanoVGGL3.nvgCreate(3);
   public static final NVGPaint NVG_PAINT = NVGPaint.create();
   public static final NVGColor NVG_COLOR_1 = NVGColor.create();
   public static final NVGColor NVG_COLOR_2 = NVGColor.create();
   private static final NVGColor NVG_TRANSPARENT = NVGColor.create();
   private static final NanoVGFrameLifecycle FRAME_LIFECYCLE = new NanoVGFrameLifecycle(
      NVGRenderer::beginNativeFrame, NVGRenderer::endNativeFrame, NVGRenderer::abortNativeFrame, NVGRenderer::saveNativeState, NVGRenderer::restoreNativeState
   );
   private static boolean frameStarted = false;
   private static boolean framePrepared = false;
   private static boolean initialized = false;
   public static float globalAlpha = 1.0F;
   private static int attachedFbo = 0;
   private static int attachedStencilRbo = 0;
   private static int attachedW = 0;
   private static int attachedH = 0;
   private static final Deque legacyFrames = new ArrayDeque();
   private static final float P = 0.4F;
   private static final int SEG = 8;
   private static final int LUT_N = 9;
   private static final float[] SIN_POW = new float[10];
   private static final float[] COS_POW = new float[10];
   private static final float[] TRANSFORM_XF;
   private static final float[] BYTE_TO_FLOAT;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private NVGRenderer() {
   }

   private static void ensureInitialized() {
      if (!initialized) {
         try {
            GLStateManager.setup();
            NanoVGFontManager.loadFonts();
            initialized = true;
            if (NanoVGFontManager.getRegularFont() < 0) {
               byte[] var10001 = new byte[51];
               var10001[0] = (byte)(97);
               var10001[1] = (byte)(-44);
               var10001[2] = (byte)(74);
               var10001[3] = (byte)(-103);
               var10001[4] = (byte)(-109);
               var10001[5] = (byte)(79);
               var10001[6] = (byte)(-97);
               var10001[7] = (byte)(117);
               var10001[8] = (byte)(55);
               var10001[9] = (byte)(-48);
               var10001[10] = (byte)(-127);
               var10001[11] = (byte)(-124);
               var10001[12] = (byte)(-60);
               var10001[13] = (byte)(-46);
               var10001[14] = (byte)(-114);
               var10001[15] = (byte)(8);
               var10001[16] = (byte)(24);
               var10001[17] = (byte)(36);
               var10001[18] = (byte)(-91);
               var10001[19] = (byte)(-64);
               var10001[20] = (byte)(22);
               var10001[21] = (byte)(-32);
               var10001[22] = (byte)(-127);
               var10001[23] = (byte)(19);
               var10001[24] = (byte)(-25);
               var10001[25] = (byte)(117);
               var10001[26] = (byte)(-31);
               var10001[27] = (byte)(42);
               var10001[28] = (byte)(40);
               var10001[29] = (byte)(-48);
               var10001[30] = (byte)(-87);
               var10001[31] = (byte)(-1);
               var10001[32] = (byte)(-75);
               var10001[33] = (byte)(84);
               var10001[34] = (byte)(8);
               var10001[35] = (byte)(94);
               var10001[36] = (byte)(71);
               var10001[37] = (byte)(-21);
               var10001[38] = (byte)(56);
               var10001[39] = (byte)(-99);
               var10001[40] = (byte)(-105);
               var10001[41] = (byte)(-90);
               var10001[42] = (byte)(100);
               var10001[43] = (byte)(98);
               var10001[44] = (byte)(-106);
               var10001[45] = (byte)(114);
               var10001[46] = (byte)(85);
               var10001[47] = (byte)(-90);
               var10001[48] = (byte)(107);
               var10001[49] = (byte)(-114);
               var10001[50] = (byte)(13);
               System.err.println($$v_s(var10001, 4175668602008227871L));
            }
         } catch (Exception var1) {
            System.err.println("[NVGRenderer] Error during initialization: " + var1.getMessage());
            var1.printStackTrace();
         }
      }
   }

   private static void ensureStencilAttachment(int width, int height) {
      int var2 = GLStateManager.getLastFramebuffer();
      if (var2 != 0) {
         try {
            int var3 = GL30.glGetFramebufferAttachmentParameteri(36160, 36128, 36048);
            if (var3 != 0) {
               return;
            }

            if (attachedStencilRbo == 0 || attachedW != width || attachedH != height) {
               if (attachedStencilRbo != 0) {
                  GL30.glDeleteRenderbuffers(attachedStencilRbo);
                  attachedStencilRbo = 0;
               }

               attachedStencilRbo = GL30.glGenRenderbuffers();
               GL30.glBindRenderbuffer(36161, attachedStencilRbo);
               GL30.glRenderbufferStorage(36161, 36168, width, height);
               GL30.glBindRenderbuffer(36161, 0);
               attachedW = width;
               attachedH = height;
            }

            GL30.glFramebufferRenderbuffer(36160, 36128, 36161, attachedStencilRbo);
            attachedFbo = var2;
         } catch (Throwable var4) {
         }
      }
   }

   private static boolean beginNativeFrame() {
      if (frameStarted) {
         abortNativeFrame();
      }

      if (!framePrepared) {
         prepareFrame();
      }

      framePrepared = false;
      ensureInitialized();
      class_1041 var0 = mc.method_22683();
      if (VG != 0L && var0 != null && var0.method_4489() > 0 && var0.method_4506() > 0) {
         float var1 = var0.method_4495();
         if (var1 <= 0.0F) {
            return false;
         }

         GLStateManager.push();
         ensureStencilAttachment(var0.method_4489(), var0.method_4506());

         try {
            NanoVG.nvgBeginFrame(VG, var0.method_4489() / var1, var0.method_4506() / var1, var1);
            NVGTextRenderer.setDevicePixelRatio(var1);
            frameStarted = true;
            globalAlpha = 1.0F;
            return true;
         } catch (Throwable var5) {
            try {
               GLStateManager.pop();
            } catch (Throwable var4) {
            }

            frameStarted = false;
            return false;
         }
      } else {
         return false;
      }
   }

   private static void abortNativeFrame() {
      if (frameStarted) {
         try {
            NanoVG.nvgCancelFrame(VG);
         } catch (Throwable var2) {
         }

         try {
            GLStateManager.pop();
         } catch (Throwable var1) {
         }

         frameStarted = false;
         globalAlpha = 1.0F;
      }
   }

   private static void endNativeFrame() {
      if (frameStarted) {
         try {
            NanoVG.nvgEndFrame(VG);
         } finally {
            try {
               GLStateManager.pop();
            } finally {
               frameStarted = false;
               globalAlpha = 1.0F;
            }
         }
      }
   }

   private static void saveNativeState() {
      NanoVG.nvgSave(VG);
   }

   private static void restoreNativeState() {
      NanoVG.nvgRestore(VG);
   }

   public static NanoVGFrame frame() {
      return FRAME_LIFECYCLE.acquire();
   }

   public static synchronized boolean beginFrame() {
      NanoVGFrame var0 = FRAME_LIFECYCLE.acquire();
      if (!var0.isActive()) {
         return false;
      }

      legacyFrames.push(var0);
      return true;
   }

   public static synchronized void endFrame() {
      if (!legacyFrames.isEmpty()) {
         try {
            ((NanoVGFrame)legacyFrames.pop()).close();
         } catch (Throwable var1) {
         }
      }
   }

   public static void prepareFrame() {
      if (!frameStarted && !framePrepared) {
         if (initialized) {
            NVGTextRenderer.beginFrame();
            framePrepared = true;
         } else {
            boolean var0 = false;

            try {
               GLStateManager.setup();
               GLStateManager.push();
               var0 = true;
               ensureInitialized();
               NVGTextRenderer.beginFrame();
               framePrepared = true;
            } catch (Throwable var10) {
               framePrepared = false;
            } finally {
               if (var0) {
                  try {
                     GLStateManager.pop();
                  } catch (Throwable var9) {
                  }
               }
            }
         }
      }
   }

   public static synchronized void forceCloseFrame() {
      legacyFrames.clear();
      framePrepared = false;
      FRAME_LIFECYCLE.forceAbort();
      abortNativeFrame();
   }

   public static void globalAlpha(float alpha) {
      if (globalAlpha != alpha) {
         globalAlpha = alpha;
         NanoVG.nvgGlobalAlpha(VG, alpha);
      }
   }

   public static void drawRect(float x, float y, float width, float height, Color color) {
      if (color != null && color.getAlpha() > 0) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         NanoVG.nvgRect(VG, x, y, width, height);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawRect(float x, float y, float width, float height, int argb) {
      if ((argb >>> (24) & (255)) != 0) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         NanoVG.nvgRect(VG, x, y, width, height);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawRoundedRect(float x, float y, float width, float height, float radius, Color color) {
      if (color != null && color.getAlpha() > 0) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         NanoVG.nvgRoundedRect(VG, x, y, width, height, radius);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawRoundedRect(float x, float y, float width, float height, float radius, int argb) {
      if ((argb >>> (24) & (255)) != 0) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         NanoVG.nvgRoundedRect(VG, x, y, width, height, radius);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawRotatedRoundedRect(float x, float y, float width, float height, float radius, Color color, float angle) {
      if (VG != 0L) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgSave(VG);
         NanoVG.nvgTranslate(VG, x + width * 0.5F, y + height * 0.5F);
         NanoVG.nvgRotate(VG, angle);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         NanoVG.nvgRoundedRect(VG, -width * 0.5F, -height * 0.5F, width, height, radius);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
         NanoVG.nvgRestore(VG);
      }
   }

   public static void drawRoundedRectVarying(float x, float y, float width, float height, float tl, float tr, float br, float bl, Color color) {
      applyColor(color, NVG_COLOR_1);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillColor(VG, NVG_COLOR_1);
      NanoVG.nvgRoundedRectVarying(VG, x, y, width, height, tl, tr, br, bl);
      NanoVG.nvgFill(VG);
      NanoVG.nvgClosePath(VG);
   }

   public static void drawRoundedRectVarying(float x, float y, float width, float height, float tl, float tr, float br, float bl, int argb) {
      if ((argb >>> (24) & (255)) > 0) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         NanoVG.nvgRoundedRectVarying(VG, x, y, width, height, tl, tr, br, bl);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   private static float hairline(float strokeWidth) {
      return Math.max(0.35F, Math.min(strokeWidth * 0.5F, 0.55F));
   }

   public static void drawRoundedRectOutline(float x, float y, float width, float height, float radius, float strokeWidth, Color color) {
      if (color != null && color.getAlpha() > 0) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, hairline(strokeWidth));
         NanoVG.nvgLineCap(VG, 0);
         NanoVG.nvgLineJoin(VG, 4);
         NanoVG.nvgRoundedRect(VG, x, y, width, height, radius);
         NanoVG.nvgStroke(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawRoundedRectOutline(float x, float y, float width, float height, float radius, float strokeWidth, int argb) {
      if ((argb >>> (24) & (255)) > 0) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, hairline(strokeWidth));
         NanoVG.nvgLineCap(VG, 0);
         NanoVG.nvgLineJoin(VG, 4);
         NanoVG.nvgRoundedRect(VG, x, y, width, height, radius);
         NanoVG.nvgStroke(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawRoundedRectOutlineVarying(
      float x, float y, float width, float height, float tl, float tr, float br, float bl, float strokeWidth, Color color
   ) {
      if (color != null && color.getAlpha() > 0) {
         drawRoundedRectOutlineVarying(x, y, width, height, tl, tr, br, bl, strokeWidth, color.getRGB());
      }
   }

   public static void drawRoundedRectOutlineVarying(
      float x, float y, float width, float height, float tl, float tr, float br, float bl, float strokeWidth, int argb
   ) {
      if ((argb >>> (24) & (255)) > 0) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, hairline(strokeWidth));
         NanoVG.nvgLineCap(VG, 0);
         NanoVG.nvgLineJoin(VG, 4);
         NanoVG.nvgRoundedRectVarying(VG, x, y, width, height, tl, tr, br, bl);
         NanoVG.nvgStroke(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawRoundedRectGradient(float x, float y, float width, float height, float radius, Color color1, Color color2) {
      applyColor(color1, NVG_COLOR_1);
      applyColor(color2, NVG_COLOR_2);
      NanoVG.nvgLinearGradient(VG, x, y, x + width, y, NVG_COLOR_1, NVG_COLOR_2, NVG_PAINT);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillPaint(VG, NVG_PAINT);
      NanoVG.nvgRoundedRect(VG, x, y, width, height, radius);
      NanoVG.nvgFill(VG);
      NanoVG.nvgClosePath(VG);
   }

   public static void drawRoundedRectGradient(float x, float y, float width, float height, float radius, int color1, int color2) {
      applyColor(color1, NVG_COLOR_1);
      applyColor(color2, NVG_COLOR_2);
      NanoVG.nvgLinearGradient(VG, x, y, x + width, y, NVG_COLOR_1, NVG_COLOR_2, NVG_PAINT);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillPaint(VG, NVG_PAINT);
      NanoVG.nvgRoundedRect(VG, x, y, width, height, radius);
      NanoVG.nvgFill(VG);
      NanoVG.nvgClosePath(VG);
   }

   public static void drawVerticalGradientRect(float x, float y, float width, float height, Color colorTop, Color colorBottom) {
      if (colorTop != null && colorTop.getAlpha() > 0 || colorBottom != null && colorBottom.getAlpha() > 0) {
         applyColor(colorTop, NVG_COLOR_1);
         applyColor(colorBottom, NVG_COLOR_2);
         NanoVG.nvgLinearGradient(VG, x, y, x, y + height, NVG_COLOR_1, NVG_COLOR_2, NVG_PAINT);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillPaint(VG, NVG_PAINT);
         NanoVG.nvgRect(VG, x, y, width, height);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawVerticalGradientRect(float x, float y, float width, float height, int colorTop, int colorBottom) {
      if ((colorTop >>> (24) & (255)) != 0 || (colorBottom >>> (24) & (255)) != 0) {
         applyColor(colorTop, NVG_COLOR_1);
         applyColor(colorBottom, NVG_COLOR_2);
         NanoVG.nvgLinearGradient(VG, x, y, x, y + height, NVG_COLOR_1, NVG_COLOR_2, NVG_PAINT);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillPaint(VG, NVG_PAINT);
         NanoVG.nvgRect(VG, x, y, width, height);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawVerticalGradientRoundedRect(float x, float y, float width, float height, float radius, Color colorTop, Color colorBottom) {
      if (colorTop != null && colorTop.getAlpha() > 0 || colorBottom != null && colorBottom.getAlpha() > 0) {
         applyColor(colorTop, NVG_COLOR_1);
         applyColor(colorBottom, NVG_COLOR_2);
         NanoVG.nvgLinearGradient(VG, x, y, x, y + height, NVG_COLOR_1, NVG_COLOR_2, NVG_PAINT);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillPaint(VG, NVG_PAINT);
         NanoVG.nvgRoundedRect(VG, x, y, width, height, radius);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawVerticalGradientRoundedRect(float x, float y, float width, float height, float radius, int colorTop, int colorBottom) {
      if ((colorTop >>> (24) & (255)) != 0 || (colorBottom >>> (24) & (255)) != 0) {
         applyColor(colorTop, NVG_COLOR_1);
         applyColor(colorBottom, NVG_COLOR_2);
         NanoVG.nvgLinearGradient(VG, x, y, x, y + height, NVG_COLOR_1, NVG_COLOR_2, NVG_PAINT);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillPaint(VG, NVG_PAINT);
         NanoVG.nvgRoundedRect(VG, x, y, width, height, radius);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void squirclePath(float x, float y, float w, float h, float radius) {
      float var5 = Math.min(radius, Math.min(w, h) * 0.5F);
      if (var5 < 1.5F) {
         NanoVG.nvgRoundedRect(VG, x, y, w, h, var5);
      } else {
         float var6 = x + w;
         float var7 = y + h;
         NanoVG.nvgMoveTo(VG, x + var5, y);
         NanoVG.nvgLineTo(VG, var6 - var5, y);
         float var8 = var6 - var5;
         float var9 = y + var5;

         for (int var10 = 0; var10 <= (8); var10++) {
            NanoVG.nvgLineTo(VG, var8 + var5 * SIN_POW[var10], var9 - var5 * COS_POW[var10]);
         }

         NanoVG.nvgLineTo(VG, var6, var7 - var5);
         var8 = var6 - var5;
         var9 = var7 - var5;

         for (int var17 = 0; var17 <= (8); var17++) {
            NanoVG.nvgLineTo(VG, var8 + var5 * COS_POW[var17], var9 + var5 * SIN_POW[var17]);
         }

         NanoVG.nvgLineTo(VG, x + var5, var7);
         var8 = x + var5;
         var9 = var7 - var5;

         for (int var18 = 0; var18 <= (8); var18++) {
            NanoVG.nvgLineTo(VG, var8 - var5 * SIN_POW[var18], var9 + var5 * COS_POW[var18]);
         }

         NanoVG.nvgLineTo(VG, x, y + var5);
         var8 = x + var5;
         var9 = y + var5;

         for (int var19 = 0; var19 <= (8); var19++) {
            NanoVG.nvgLineTo(VG, var8 - var5 * COS_POW[var19], var9 - var5 * SIN_POW[var19]);
         }

         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawSquircle(float x, float y, float w, float h, float radius, Color color) {
      if (color != null && color.getAlpha() > 0) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         squirclePath(x, y, w, h, radius);
         NanoVG.nvgFill(VG);
      }
   }

   public static void drawSquircle(float x, float y, float w, float h, float radius, int argb) {
      if ((argb >>> (24) & (255)) > 0) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         squirclePath(x, y, w, h, radius);
         NanoVG.nvgFill(VG);
      }
   }

   public static void drawSquircleOutline(float x, float y, float w, float h, float radius, float strokeWidth, Color color) {
      if (color != null && color.getAlpha() > 0) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, hairline(strokeWidth));
         NanoVG.nvgLineCap(VG, 0);
         NanoVG.nvgLineJoin(VG, 4);
         squirclePath(x, y, w, h, radius);
         NanoVG.nvgStroke(VG);
      }
   }

   public static void drawSquircleOutline(float x, float y, float w, float h, float radius, float strokeWidth, int argb) {
      if ((argb >>> (24) & (255)) > 0) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, hairline(strokeWidth));
         NanoVG.nvgLineCap(VG, 0);
         NanoVG.nvgLineJoin(VG, 4);
         squirclePath(x, y, w, h, radius);
         NanoVG.nvgStroke(VG);
      }
   }

   public static void drawSquircleGradient(float x, float y, float w, float h, float radius, Color color1, Color color2) {
      applyColor(color1, NVG_COLOR_1);
      applyColor(color2, NVG_COLOR_2);
      NanoVG.nvgLinearGradient(VG, x, y, x + w, y, NVG_COLOR_1, NVG_COLOR_2, NVG_PAINT);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillPaint(VG, NVG_PAINT);
      squirclePath(x, y, w, h, radius);
      NanoVG.nvgFill(VG);
   }

   public static void drawSquircleGradient(float x, float y, float w, float h, float radius, int color1, int color2) {
      applyColor(color1, NVG_COLOR_1);
      applyColor(color2, NVG_COLOR_2);
      NanoVG.nvgLinearGradient(VG, x, y, x + w, y, NVG_COLOR_1, NVG_COLOR_2, NVG_PAINT);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillPaint(VG, NVG_PAINT);
      squirclePath(x, y, w, h, radius);
      NanoVG.nvgFill(VG);
   }

   public static void fillDiagonalSheen(float x, float y, float w, float h, float radius, boolean squircle, Color from, Color to) {
      applyColor(from, NVG_COLOR_1);
      applyColor(to, NVG_COLOR_2);
      NanoVG.nvgLinearGradient(VG, x + w * 0.12F, y - h * 0.25F, x + w * 0.55F, y + h * 0.9F, NVG_COLOR_1, NVG_COLOR_2, NVG_PAINT);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillPaint(VG, NVG_PAINT);
      if (squircle) {
         squirclePath(x, y, w, h, radius);
      } else {
         NanoVG.nvgRoundedRect(VG, x, y, w, h, radius);
      }

      NanoVG.nvgFill(VG);
   }

   public static void fillDiagonalSheen(float x, float y, float w, float h, float radius, boolean squircle, int fromArgb, int toArgb) {
      applyColor(fromArgb, NVG_COLOR_1);
      applyColor(toArgb, NVG_COLOR_2);
      NanoVG.nvgLinearGradient(VG, x + w * 0.12F, y - h * 0.25F, x + w * 0.55F, y + h * 0.9F, NVG_COLOR_1, NVG_COLOR_2, NVG_PAINT);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillPaint(VG, NVG_PAINT);
      if (squircle) {
         squirclePath(x, y, w, h, radius);
      } else {
         NanoVG.nvgRoundedRect(VG, x, y, w, h, radius);
      }

      NanoVG.nvgFill(VG);
   }

   public static void queueFrostRect(float x, float y, float w, float h, float radius) {
      queueFrostRect(x, y, w, h, radius, false);
   }

   public static void queueFrostRect(float x, float y, float w, float h, float radius, boolean squircle) {
      if (FrostedBackground.isFrostActive()) {
         float[] var6 = TRANSFORM_XF;
         var6[0] = 1.0F;
         var6[1] = 0.0F;
         var6[2] = 0.0F;
         var6[3] = 1.0F;
         var6[4] = 0.0F;
         var6[5] = 0.0F;
         if (frameStarted) {
            NanoVG.nvgCurrentTransform(VG, var6);
         }

         float var7 = var6[0] * x + var6[2] * y + var6[4];
         float var8 = var6[1] * x + var6[3] * y + var6[5];
         float var9 = var6[0] * (x + w) + var6[2] * y + var6[4];
         float var10 = var6[1] * (x + w) + var6[3] * y + var6[5];
         float var11 = var6[0] * (x + w) + var6[2] * (y + h) + var6[4];
         float var12 = var6[1] * (x + w) + var6[3] * (y + h) + var6[5];
         float var13 = var6[0] * x + var6[2] * (y + h) + var6[4];
         float var14 = var6[1] * x + var6[3] * (y + h) + var6[5];
         float var15 = Math.min(Math.min(var7, var9), Math.min(var11, var13));
         float var16 = Math.min(Math.min(var8, var10), Math.min(var12, var14));
         float var17 = Math.max(Math.max(var7, var9), Math.max(var11, var13));
         float var18 = Math.max(Math.max(var8, var10), Math.max(var12, var14));
         float var19 = radius * (float)Math.hypot(var6[0], var6[1]);
         FrostedBackground.queueLogicalRect(var15, var16, var17 - var15, var18 - var16, var19, squircle);
      }
   }

   public static void drawRoundedBackdrop(float x, float y, float w, float h, float radius, float alpha) {
      queueFrostRect(x, y, w, h, radius);
   }

   public static void drawSquircleFrostBackdrop(float x, float y, float w, float h, float radius, float alpha) {
      queueFrostRect(x, y, w, h, radius, true);
   }

   public static void drawSquircleBackdrop(float x, float y, float w, float h, float radius, int img, float scrW, float scrH, float alpha, float mag) {
      float var10 = x + w * 0.5F;
      float var11 = y + h * 0.5F;
      float var12 = var10 * (1.0F - mag);
      float var13 = var11 * (1.0F - mag);
      NanoVG.nvgImagePattern(VG, var12, var13, scrW * mag, scrH * mag, 0.0F, img, alpha, NVG_PAINT);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillPaint(VG, NVG_PAINT);
      squirclePath(x, y, w, h, radius);
      NanoVG.nvgFill(VG);
   }

   public static void drawSquircleRadialGlow(float x, float y, float w, float h, float radius, float gx, float gy, float glowR, Color glow) {
      applyColor(glow, NVG_COLOR_1);
      NanoVG.nvgRadialGradient(VG, gx, gy, 0.0F, glowR, NVG_COLOR_1, NVG_TRANSPARENT, NVG_PAINT);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillPaint(VG, NVG_PAINT);
      squirclePath(x, y, w, h, radius);
      NanoVG.nvgFill(VG);
   }

   public static void drawSquircleRadialGlow(float x, float y, float w, float h, float radius, float gx, float gy, float glowR, int glowArgb) {
      if ((glowArgb >>> (24) & (255)) > 0) {
         applyColor(glowArgb, NVG_COLOR_1);
         NanoVG.nvgRadialGradient(VG, gx, gy, 0.0F, glowR, NVG_COLOR_1, NVG_TRANSPARENT, NVG_PAINT);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillPaint(VG, NVG_PAINT);
         squirclePath(x, y, w, h, radius);
         NanoVG.nvgFill(VG);
      }
   }

   public static void drawCircle(float x, float y, float radius, Color color) {
      if (color != null && color.getAlpha() > 0) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         NanoVG.nvgCircle(VG, x, y, radius);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawCircle(float x, float y, float radius, int argb) {
      if ((argb >>> (24) & (255)) > 0) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         NanoVG.nvgCircle(VG, x, y, radius);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawCircleOutline(float x, float y, float radius, float strokeWidth, Color color) {
      if (color != null && color.getAlpha() > 0) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, strokeWidth);
         NanoVG.nvgCircle(VG, x, y, radius);
         NanoVG.nvgStroke(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawCircleOutline(float x, float y, float radius, float strokeWidth, int argb) {
      if ((argb >>> (24) & (255)) > 0) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, strokeWidth);
         NanoVG.nvgCircle(VG, x, y, radius);
         NanoVG.nvgStroke(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, Color color) {
      if (color != null && color.getAlpha() > 0) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         NanoVG.nvgMoveTo(VG, x1, y1);
         NanoVG.nvgLineTo(VG, x2, y2);
         NanoVG.nvgLineTo(VG, x3, y3);
         NanoVG.nvgClosePath(VG);
         NanoVG.nvgFill(VG);
      }
   }

   public static void drawTriangle(float x1, float y1, float x2, float y2, float x3, float y3, int argb) {
      if ((argb >>> (24) & (255)) > 0) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillColor(VG, NVG_COLOR_1);
         NanoVG.nvgMoveTo(VG, x1, y1);
         NanoVG.nvgLineTo(VG, x2, y2);
         NanoVG.nvgLineTo(VG, x3, y3);
         NanoVG.nvgClosePath(VG);
         NanoVG.nvgFill(VG);
      }
   }

   public static void drawTriangleOutline(float x1, float y1, float x2, float y2, float x3, float y3, float strokeWidth, Color color) {
      if (color != null && color.getAlpha() > 0 && !(strokeWidth <= 0.0F)) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, strokeWidth);
         NanoVG.nvgLineCap(VG, 1);
         NanoVG.nvgLineJoin(VG, 1);
         NanoVG.nvgMoveTo(VG, x1, y1);
         NanoVG.nvgLineTo(VG, x2, y2);
         NanoVG.nvgLineTo(VG, x3, y3);
         NanoVG.nvgClosePath(VG);
         NanoVG.nvgStroke(VG);
      }
   }

   public static void drawTriangleOutline(float x1, float y1, float x2, float y2, float x3, float y3, float strokeWidth, int argb) {
      if ((argb >>> (24) & (255)) > 0 && !(strokeWidth <= 0.0F)) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, strokeWidth);
         NanoVG.nvgLineCap(VG, 1);
         NanoVG.nvgLineJoin(VG, 1);
         NanoVG.nvgMoveTo(VG, x1, y1);
         NanoVG.nvgLineTo(VG, x2, y2);
         NanoVG.nvgLineTo(VG, x3, y3);
         NanoVG.nvgClosePath(VG);
         NanoVG.nvgStroke(VG);
      }
   }

   public static void drawRadialGlow(float cx, float cy, float radius, Color glow) {
      if (glow != null && glow.getAlpha() > 0) {
         applyColor(glow, NVG_COLOR_1);
         NanoVG.nvgRadialGradient(VG, cx, cy, 0.0F, radius, NVG_COLOR_1, NVG_TRANSPARENT, NVG_PAINT);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillPaint(VG, NVG_PAINT);
         NanoVG.nvgCircle(VG, cx, cy, radius);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawRadialGlow(float cx, float cy, float radius, int argb) {
      if ((argb >>> (24) & (255)) > 0) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgRadialGradient(VG, cx, cy, 0.0F, radius, NVG_COLOR_1, NVG_TRANSPARENT, NVG_PAINT);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillPaint(VG, NVG_PAINT);
         NanoVG.nvgCircle(VG, cx, cy, radius);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawArc(float cx, float cy, float radius, float a0, float a1, float strokeWidth, Color color) {
      if (color != null && color.getAlpha() > 0 && !(strokeWidth <= 0.0F)) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgLineCap(VG, 1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, strokeWidth);
         NanoVG.nvgArc(VG, cx, cy, radius, a0, a1, 2);
         NanoVG.nvgStroke(VG);
      }
   }

   public static void drawArc(float cx, float cy, float radius, float a0, float a1, float strokeWidth, int argb) {
      if ((argb >>> (24) & (255)) > 0 && !(strokeWidth <= 0.0F)) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgLineCap(VG, 1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, strokeWidth);
         NanoVG.nvgArc(VG, cx, cy, radius, a0, a1, 2);
         NanoVG.nvgStroke(VG);
      }
   }

   public static void drawSpotifyLogo(float x, float y, float size, Color discColor, Color waveColor) {
      if (!(size <= 0.0F)) {
         if (discColor != null && discColor.getAlpha() > 0) {
            drawCircle(x + size * 0.5F, y + size * 0.5F, size * 0.5F, discColor);
         }

         if (waveColor != null && waveColor.getAlpha() > 0) {
            applyColor(waveColor, NVG_COLOR_1);
            NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
            NanoVG.nvgLineCap(VG, 1);
            NanoVG.nvgLineJoin(VG, 1);
            float var5 = Math.max(1.35F, size * 0.11F);
            NanoVG.nvgStrokeWidth(VG, var5);
            NanoVG.nvgBeginPath(VG);
            NanoVG.nvgMoveTo(VG, x + size * 0.175F, y + size * 0.355F);
            NanoVG.nvgBezierTo(VG, x + size * 0.38F, y + size * 0.268F, x + size * 0.607F, y + size * 0.282F, x + size * 0.825F, y + size * 0.41F);
            NanoVG.nvgStroke(VG);
            NanoVG.nvgClosePath(VG);
            float var6 = Math.max(1.2F, size * 0.098F);
            NanoVG.nvgStrokeWidth(VG, var6);
            NanoVG.nvgBeginPath(VG);
            NanoVG.nvgMoveTo(VG, x + size * 0.21F, y + size * 0.505F);
            NanoVG.nvgBezierTo(VG, x + size * 0.389F, y + size * 0.422F, x + size * 0.585F, y + size * 0.432F, x + size * 0.77F, y + size * 0.555F);
            NanoVG.nvgStroke(VG);
            NanoVG.nvgClosePath(VG);
            float var7 = Math.max(1.1F, size * 0.086F);
            NanoVG.nvgStrokeWidth(VG, var7);
            NanoVG.nvgBeginPath(VG);
            NanoVG.nvgMoveTo(VG, x + size * 0.25F, y + size * 0.655F);
            NanoVG.nvgBezierTo(VG, x + size * 0.4F, y + size * 0.578F, x + size * 0.561F, y + size * 0.586F, x + size * 0.71F, y + size * 0.695F);
            NanoVG.nvgStroke(VG);
            NanoVG.nvgClosePath(VG);
         }
      }
   }

   public static void drawLine(float x1, float y1, float x2, float y2, float strokeWidth, Color color) {
      if (color != null && color.getAlpha() > 0 && !(strokeWidth <= 0.0F)) {
         applyColor(color, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, strokeWidth);
         NanoVG.nvgMoveTo(VG, x1, y1);
         NanoVG.nvgLineTo(VG, x2, y2);
         NanoVG.nvgStroke(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void drawLine(float x1, float y1, float x2, float y2, float strokeWidth, int argb) {
      if ((argb >>> (24) & (255)) > 0 && !(strokeWidth <= 0.0F)) {
         applyColor(argb, NVG_COLOR_1);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
         NanoVG.nvgStrokeWidth(VG, strokeWidth);
         NanoVG.nvgMoveTo(VG, x1, y1);
         NanoVG.nvgLineTo(VG, x2, y2);
         NanoVG.nvgStroke(VG);
         NanoVG.nvgClosePath(VG);
      }
   }

   public static void beginLineBatch(Color color, float strokeWidth) {
      applyColor(color, NVG_COLOR_1);
      NanoVG.nvgLineCap(VG, 1);
      NanoVG.nvgLineJoin(VG, 1);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
      NanoVG.nvgStrokeWidth(VG, strokeWidth);
   }

   public static void beginLineBatch(int argb, float strokeWidth) {
      applyColor(argb, NVG_COLOR_1);
      NanoVG.nvgLineCap(VG, 1);
      NanoVG.nvgLineJoin(VG, 1);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgStrokeColor(VG, NVG_COLOR_1);
      NanoVG.nvgStrokeWidth(VG, strokeWidth);
   }

   public static void batchLine(float x1, float y1, float x2, float y2) {
      NanoVG.nvgMoveTo(VG, x1, y1);
      NanoVG.nvgLineTo(VG, x2, y2);
   }

   public static void endLineBatch() {
      NanoVG.nvgStroke(VG);
      NanoVG.nvgLineCap(VG, 0);
      NanoVG.nvgLineJoin(VG, 4);
   }

   public static void beginFillBatch(Color color) {
      applyColor(color, NVG_COLOR_1);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillColor(VG, NVG_COLOR_1);
   }

   public static void beginFillBatch(int argb) {
      applyColor(argb, NVG_COLOR_1);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillColor(VG, NVG_COLOR_1);
   }

   public static void batchTri(float x1, float y1, float x2, float y2, float x3, float y3) {
      NanoVG.nvgMoveTo(VG, x1, y1);
      NanoVG.nvgLineTo(VG, x2, y2);
      NanoVG.nvgLineTo(VG, x3, y3);
      NanoVG.nvgClosePath(VG);
      NanoVG.nvgPathWinding(VG, 1);
   }

   public static void endFillBatch() {
      NanoVG.nvgFill(VG);
   }

   public static void drawRoundedRectWithShadow(
      float x, float y, float width, float height, float radius, Color color, Color shadowColor, float shadowBlur, float shadowSpread
   ) {
      applyColor(shadowColor, NVG_COLOR_1);
      NanoVG.nvgBoxGradient(VG, x, y, width, height, radius, shadowBlur, NVG_COLOR_1, NVG_TRANSPARENT, NVG_PAINT);
      NanoVG.nvgBeginPath(VG);
      NanoVG.nvgFillPaint(VG, NVG_PAINT);
      NanoVG.nvgRoundedRect(VG, x - shadowSpread, y - shadowSpread, width + shadowSpread * 2.0F, height + shadowSpread * 2.0F, radius);
      NanoVG.nvgFill(VG);
      NanoVG.nvgClosePath(VG);
      drawRoundedRect(x, y, width, height, radius, color);
   }

   public static void drawRoundedRectWithShadow(
      float x, float y, float width, float height, float radius, int color, int shadowColor, float shadowBlur, float shadowSpread
   ) {
      if ((shadowColor >>> (24) & (255)) > 0) {
         applyColor(shadowColor, NVG_COLOR_1);
         NanoVG.nvgBoxGradient(VG, x, y, width, height, radius, shadowBlur, NVG_COLOR_1, NVG_TRANSPARENT, NVG_PAINT);
         NanoVG.nvgBeginPath(VG);
         NanoVG.nvgFillPaint(VG, NVG_PAINT);
         NanoVG.nvgRoundedRect(VG, x - shadowSpread, y - shadowSpread, width + shadowSpread * 2.0F, height + shadowSpread * 2.0F, radius);
         NanoVG.nvgFill(VG);
         NanoVG.nvgClosePath(VG);
      }

      drawRoundedRect(x, y, width, height, radius, color);
   }

   public static void save() {
      NanoVG.nvgSave(VG);
   }

   public static void restore() {
      NanoVG.nvgRestore(VG);
   }

   public static void translate(float x, float y) {
      NanoVG.nvgTranslate(VG, x, y);
   }

   public static void scale(float x, float y) {
      NanoVG.nvgScale(VG, x, y);
   }

   public static void rotate(float angle) {
      NanoVG.nvgRotate(VG, angle);
   }

   public static void scissor(float x, float y, float width, float height) {
      NanoVG.nvgScissor(VG, x, y, width, height);
   }

   public static void resetScissor() {
      NanoVG.nvgResetScissor(VG);
   }

   public static void applyColor(Color color, NVGColor nvgColor) {
      if (color == null) {
         nvgColor.r(0.0F).g(0.0F).b(0.0F).a(0.0F);
      } else {
         nvgColor.r(BYTE_TO_FLOAT[color.getRed()]).g(BYTE_TO_FLOAT[color.getGreen()]).b(BYTE_TO_FLOAT[color.getBlue()]).a(BYTE_TO_FLOAT[color.getAlpha()]);
      }
   }

   public static void applyColor(int argb, NVGColor nvgColor) {
      nvgColor.r(BYTE_TO_FLOAT[argb >> (16) & (255)])
         .g(BYTE_TO_FLOAT[argb >> (8) & (255)])
         .b(BYTE_TO_FLOAT[argb & (255)])
         .a(BYTE_TO_FLOAT[argb >>> (24) & (255)]);
   }

   public static long getContext() {
      return VG;
   }

   public static boolean isInFrame() {
      return FRAME_LIFECYCLE.isActive() || frameStarted;
   }

   public static void invalidate() {
      if (attachedStencilRbo != 0) {
         try {
            GL30.glDeleteRenderbuffers(attachedStencilRbo);
         } catch (Throwable var1) {
         }

         attachedStencilRbo = 0;
      }

      attachedFbo = 0;
      attachedW = 0;
      attachedH = 0;
      frameStarted = false;
      framePrepared = false;
      NVGTextRenderer.invalidateMetrics();
   }

   public static void cleanup() {
      NVGTextRenderer.invalidateMetrics();
      if (VG != 0L) {
         NanoVGGL3.nvgDelete(VG);
      }
   }

   static {
      NanoVGNatives.ensureLoaded();

      for (int var0 = 0; var0 <= (8); var0++) {
         double var1 = (Math.PI / 2) * var0 / 8.0;
         SIN_POW[var0] = (float)Math.pow(Math.sin(var1), 0.4F);
         COS_POW[var0] = (float)Math.pow(Math.cos(var1), 0.4F);
      }

      TRANSFORM_XF = new float[6];
      BYTE_TO_FLOAT = new float[256];

      for (int var3 = 0; var3 < (256); var3++) {
         BYTE_TO_FLOAT[var3] = var3 * 0.003921569F;
      }

      NanoVG.nvgRGBAf(0.0F, 0.0F, 0.0F, 0.0F, NVG_TRANSPARENT);
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
