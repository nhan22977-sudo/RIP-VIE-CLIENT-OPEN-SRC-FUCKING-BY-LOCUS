package me.drake.vieclient.gui.nvg.ui.components;

import java.awt.Color;
import me.drake.vieclient.render.nvg.NanoVGRenderer;

public final class GuiStyle {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private GuiStyle() {
   }

   public static Color accent(int a) {
      return SoftPalette.mint(a);
   }

   public static Color accent2(int a) {
      return SoftPalette.mintDeep(a);
   }

   public static void shadow(float x, float y, float w, float h, float radius, float alpha) {
      int var6 = (int)(34.0F * alpha);
      if (var6 > 0) {
         NanoVGRenderer.drawRoundedRect(x - 3.0F, y + 4.0F, w + 6.0F, h + 8.0F, radius + 3.0F, new Color(0, 0, 0, var6));
      }
   }

   public static void shadowSquircle(float x, float y, float w, float h, float radius, float alpha) {
      int var6 = (int)(34.0F * alpha);
      if (var6 > 0) {
         NanoVGRenderer.drawSquircle(x - 3.0F, y + 4.0F, w + 6.0F, h + 8.0F, radius, new Color(0, 0, 0, var6));
      }
   }

   public static void gloss(float x, float y, float w, float h, float radius, float alpha) {
      NanoVGRenderer.drawRoundedRect(x, y, w, h, radius, new Color(255, 255, 255, (int)(4.0F * alpha)));
      NanoVGRenderer.drawRoundedRectOutline(
         x, y, w, h, radius, 0.7F, new Color(255, 255, 255, (int)(10.0F * alpha))
      );
   }

   public static void glossSquircle(float x, float y, float w, float h, float radius, float alpha) {
      NanoVGRenderer.drawSquircle(x, y, w, h, radius, new Color(255, 255, 255, (int)(4.0F * alpha)));
      NanoVGRenderer.drawSquircleOutline(x, y, w, h, radius, 0.7F, new Color(255, 255, 255, (int)(10.0F * alpha)));
   }

   private static int clamp(int v) {
      return Math.max(0, Math.min(255, v));
   }
}
