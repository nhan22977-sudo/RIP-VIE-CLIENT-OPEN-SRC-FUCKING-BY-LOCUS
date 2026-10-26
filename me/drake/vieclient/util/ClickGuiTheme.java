package me.drake.vieclient.util;

import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.render.TessellatorRenderer;
import net.minecraft.class_332;

public final class ClickGuiTheme {
   public static final int BG = -15657182;
   public static final int PANEL_BORDER = -13880252;
   public static final int ROW_BG = -15196371;
   public static final int ROW_BORDER = -14406598;
   public static final int ROW_HOVER = 536870911;
   public static final int ACCENT = -10897153;
   public static final int ACCENT_SOFT = -7548673;
   public static final int TGL_OFF = -13024427;
   public static final int TGL_ON = -12739585;
   public static final int TXT_ON = -657153;
   public static final int TXT_OFF = -4734767;
   public static final int SEARCH_BG = -15459543;
   public static final int OVERLAY_BG = -1610216942;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private ClickGuiTheme() {
   }

   public static ClickGuiTheme.Palette resolve() {
      ClientSettings var0 = ClientSettings.get();
      if (var0 == null) {
         return new ClickGuiTheme.Palette(
            -10897153,
            -7548673,
            -15657182,
            -13880252,
            -15196371,
            -14406598,
            536870911,
            -13024427,
            -12739585,
            -657153,
            -4734767,
            -15459543,
            -1610216942
         );
      }

      int var1 = opaque(var0.getPrimaryColor());
      int var2 = opaque(var0.getSecondaryColor());
      int var3 = opaque(var0.resolveTextColor(0L));
      int var4 = blend(-4734767, var3, 0.22F);
      int var5 = blend(-15657182, var2, 0.2F);
      int var6 = blend(-13880252, var2, 0.34F);
      int var7 = blend(-15196371, var2, 0.22F);
      int var8 = blend(-14406598, var2, 0.3F);
      int var9 = blend(-15459543, var2, 0.24F);
      int var10 = blend(-13024427, var2, 0.26F);
      int var11 = blend(-12739585, var1, 0.4F);
      return new ClickGuiTheme.Palette(
         var1, blend(-7548673, var1, 0.3F), var5, var6, var7, var8, 536870911, var10, var11, var3, var4, var9, -1610216942
      );
   }

   public static void drawBorder(class_332 ctx, int x, int y, int w, int h, int color) {
      drawBorder(ctx, x, y, w, h, radiusMain(w, h), color);
   }

   public static void drawBorder(class_332 ctx, int x, int y, int w, int h, float radius, int color) {
      if (w > 0 && h > 0) {
         int var7 = color >>> (24) & (255);
         if (var7 != 0) {
            TessellatorRenderer.strokeRoundedRect(ctx, x + 0.5F, y + 0.5F, w - 1.0F, h - 1.0F, radius, 1.0F, color);
         }
      }
   }

   public static float radiusMain(int w, int h) {
      return Math.max(2.0F, Math.min(Math.min(w, h) * 0.5F, 4.2F));
   }

   public static float radiusSmall(int w, int h) {
      return Math.max(1.5F, Math.min(Math.min(w, h) * 0.5F, 3.0F));
   }

   public static float radiusPill(int h) {
      return Math.max(1.0F, Math.min(h * 0.46F, 7.25F));
   }

   public static int withAlpha(int color, int alpha) {
      int var2 = Math.max(0, Math.min(255, alpha));
      return var2 << (24) | color & (16777215);
   }

   private static int opaque(int color) {
      return -16777216 | color & (16777215);
   }

   private static int blend(int a, int b, float t) {
      float var3 = Math.max(0.0F, Math.min(1.0F, t));
      int var4 = a >>> (24) & (255);
      int var5 = a >>> (16) & (255);
      int var6 = a >>> (8) & (255);
      int var7 = a & (255);
      int var8 = b >>> (24) & (255);
      int var9 = b >>> (16) & (255);
      int var10 = b >>> (8) & (255);
      int var11 = b & (255);
      int var12 = (int)(var4 + (var8 - var4) * var3);
      int var13 = (int)(var5 + (var9 - var5) * var3);
      int var14 = (int)(var6 + (var10 - var6) * var3);
      int var15 = (int)(var7 + (var11 - var7) * var3);
      return var12 << (24) | var13 << (16) | var14 << (8) | var15;
   }

   public record Palette(
      int accent,
      int accentSoft,
      int bg,
      int panelBorder,
      int rowBg,
      int rowBorder,
      int rowHover,
      int tglOff,
      int tglOn,
      int txtOn,
      int txtOff,
      int searchBg,
      int overlayBg
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
