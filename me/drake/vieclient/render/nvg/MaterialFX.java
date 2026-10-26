package me.drake.vieclient.render.nvg;

import java.awt.Color;
import me.drake.vieclient.gui.nvg.ui.components.ThemeManager;

public final class MaterialFX {
   public static final int RIM_DEFAULT = 10;
   public static final int GLOW_DEFAULT = 12;
   public static final float GLASS_ALPHA = 1.0F;
   public static final float GLASS_TINT = 1.0F;
   private static final int DEPTH_SHADOW_COLOR = 369098752;
   private static final int DEPTH_LIT_EDGE_COLOR = 520093695;
   private static final int DEPTH_BEVEL_COLOR = 1006632960;
   private static final int SHEEN_FROM_COLOR = 754974719;
   private static final int SHEEN_TO_COLOR = 16777215;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private MaterialFX() {
   }

   public static float bodyTintMul() {
      return 1.0F;
   }

   public static void glassBody(float x, float y, float w, float h, float radius, float alpha) {
      if (!(alpha <= 0.01F)) {
         ;
      }
   }

   public static void glassBodySquircle(float x, float y, float w, float h, float radius, float alpha) {
      if (!(alpha <= 0.01F)) {
         ;
      }
   }

   public static void depthPass(float x, float y, float w, float h, float radius, boolean squircle) {
      long var6 = NVGRenderer.getContext();
      if (var6 != 0L) {
         depthPass(var6, x, y, w, h, radius, squircle);
      }
   }

   public static void depthPass(long vg, float x, float y, float w, float h, float radius, boolean squircle) {
      if (vg != 0L) {
         if (!ThemeManager.isCleanStyle()) {
            if (!(w <= 0.0F) && !(h <= 0.0F) && !(radius <= 0.0F)) {
               float var8 = Math.min(0.55F, radius * 0.5F);
               float var9 = Math.max(0.5F, radius - var8);
               NVGRenderer.save();
               NVGRenderer.scissor(x, y, w, h);
               if (squircle) {
                  NanoVGRenderer.drawSquircleOutline(x + var8, y + var8, w - 2.0F * var8, h - 2.0F * var8, var9, 1.0F, 369098752);
               } else {
                  NanoVGRenderer.drawRoundedRectOutline(x + var8, y + var8, w - 2.0F * var8, h - 2.0F * var8, var9, 1.0F, 369098752);
               }

               NVGRenderer.restore();
               NVGRenderer.save();
               NVGRenderer.scissor(x, y, w, 3.0F);
               if (squircle) {
                  NanoVGRenderer.drawSquircleOutline(x + 0.5F, y + 0.5F, w - 1.0F, h - 1.0F, radius - 0.5F, 1.0F, 520093695);
               } else {
                  NanoVGRenderer.drawRoundedRectOutline(x + 0.5F, y + 0.5F, w - 1.0F, h - 1.0F, radius - 0.5F, 1.0F, 520093695);
               }

               NVGRenderer.restore();
               NVGRenderer.save();
               NVGRenderer.scissor(x, y + h - 3.0F, w, 3.0F);
               float var10 = Math.max(2.0F, w - 2.0F * radius);
               NanoVGRenderer.drawRect(x + (w - var10) * 0.5F, y + h - 1.25F, var10, 1.25F, 1006632960);
               NVGRenderer.restore();
               NVGRenderer.fillDiagonalSheen(x, y, w, h, radius, squircle, 754974719, 16777215);
            }
         }
      }
   }

   public static void innerTopRim(float x, float y, float w, float h, float radius, float strokeWidth, Color color) {
      if (color != null) {
         innerTopRim(x, y, w, h, radius, strokeWidth, color.getRGB());
      }
   }

   public static void innerTopRim(float x, float y, float w, float h, float radius, float strokeWidth, int argb) {
      if (!ThemeManager.isCleanStyle()) {
         long var7 = NVGRenderer.getContext();
         if (var7 != 0L && (argb >>> (24) & (255)) > 0) {
            float var9 = strokeWidth * 0.5F;
            NVGRenderer.save();
            NVGRenderer.scissor(x, y, w, Math.min(8.0F, h * 0.5F));
            NVGRenderer.drawRoundedRectOutline(x + var9, y + var9, w - 2.0F * var9, h - 2.0F * var9, Math.max(0.5F, radius - var9), strokeWidth, argb);
            NVGRenderer.restore();
         }
      }
   }

   public static void ambientGlow(float x, float y, float w, float h, float radius, Color color) {
      long var6 = NVGRenderer.getContext();
      if (var6 != 0L && color.getAlpha() > 0) {
         float var8 = x + w * 0.5F;
         float var9 = y + h * 0.18F;
         NVGRenderer.drawSquircleRadialGlow(x, y, w, h, radius, var8, var9, Math.max(w, h) * 0.6F, color);
      }
   }
}
