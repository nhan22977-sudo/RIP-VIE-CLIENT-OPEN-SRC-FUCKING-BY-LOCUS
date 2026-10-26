package me.drake.vieclient.gui.nvg.ui.components;

import java.awt.Color;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.render.nvg.NVGRenderer;
import me.drake.vieclient.render.nvg.NanoVGFontManager;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.RainbowUtil;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;

public final class SoftPalette {
   public static final int[] MINT;
   public static final int[] MINT_DEEP;
   public static final int[] BODY_TOP;
   public static final int[] BODY_BOT;
   public static final int[] TEXT;
   public static final int[] TEXT_DIM;
   public static final Color MAC_RED = new Color(255, 95, 86);
   public static final Color MAC_RED_BORDER = new Color(224, 68, 62);
   public static final Color MAC_YELLOW = new Color(255, 189, 46);
   public static final Color MAC_YELLOW_BORDER = new Color(222, 161, 35);
   public static final Color MAC_GREEN = new Color(39, 201, 63);
   public static final Color MAC_GREEN_BORDER = new Color(26, 171, 41);
   public static final int MAC_RED_INT = -41130;
   public static final int MAC_RED_BORDER_INT = -2079682;
   public static final int MAC_YELLOW_INT = -17106;
   public static final int MAC_YELLOW_BORDER_INT = -2186973;
   public static final int MAC_GREEN_INT = -14169793;
   public static final int MAC_GREEN_BORDER_INT = -15029463;
   private static float[] animMint = null;
   private static float[] animMintDeep = null;
   private static float[] animBodyTop = null;
   private static float[] animBodyBot = null;
   private static long lastSyncMs = 0L;
   private static final NVGPaint SHADOW_PAINT = NVGPaint.create();
   private static final NVGColor SHADOW_IN = NVGColor.create();
   private static final NVGColor SHADOW_OUT = NVGColor.create();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SoftPalette() {
   }

   public static Color mint(float a) {
      syncTheme();
      return rgba(MINT, a);
   }

   public static Color mintDeep(float a) {
      syncTheme();
      return rgba(MINT_DEEP, a);
   }

   public static Color text(float a) {
      return rgba(TEXT, a);
   }

   public static Color textDim(float a) {
      return rgba(TEXT_DIM, a);
   }

   public static Color white(float a) {
      return new Color(255, 255, 255, clamp(a));
   }

   public static Color rgba(int[] c, float a) {
      return new Color(c[0], c[1], c[2], clamp(a));
   }

   public static int clamp(float a) {
      return Math.max(0, Math.min(255, Math.round(a)));
   }

   public static int rgbaInt(int r, int g, int b, float a) {
      return clamp(a) << (24)
         | (r & (255)) << (16)
         | (g & (255)) << (8)
         | b & (255);
   }

   public static int rgbaInt(int[] c, float a) {
      return rgbaInt(c[0], c[1], c[2], a);
   }

   public static boolean isRainbow() {
      try {
         ClientSettings var0 = ClientSettings.get();
         if (var0 != null) {
            return var0.isRainbowActive();
         }
      } catch (Throwable var1) {
      }

      return false;
   }

   public static int rainbowInt(float waveOffset, float a) {
      long var2 = (long)(waveOffset * 1800.0F);
      return RainbowUtil.rainbow(1.0, 0.88, var2, clamp(a));
   }

   public static Color rainbow(float waveOffset, float a) {
      return new Color(rainbowInt(waveOffset, a), true);
   }

   public static int mintInt(float a) {
      syncTheme();
      return rgbaInt(MINT, a);
   }

   public static int mintDeepInt(float a) {
      syncTheme();
      return rgbaInt(MINT_DEEP, a);
   }

   public static int mintInt(float a, float waveOffset) {
      return isRainbow() ? rainbowInt(waveOffset, a) : mintInt(a);
   }

   public static int mintDeepInt(float a, float waveOffset) {
      return isRainbow() ? rainbowInt(waveOffset + 0.24F, a) : mintDeepInt(a);
   }

   public static Color mint(float a, float waveOffset) {
      return new Color(mintInt(a, waveOffset), true);
   }

   public static Color mintDeep(float a, float waveOffset) {
      return new Color(mintDeepInt(a, waveOffset), true);
   }

   public static int textInt(float a) {
      return rgbaInt(TEXT, a);
   }

   public static int textDimInt(float a) {
      return rgbaInt(TEXT_DIM, a);
   }

   public static int whiteInt(float a) {
      return clamp(a) << (24) | 16777215;
   }

   public static Color primary(float a) {
      return mint(a);
   }

   public static Color secondary(float a) {
      return mintDeep(a);
   }

   public static int primaryInt(float a) {
      return mintInt(a);
   }

   public static int secondaryInt(float a) {
      return mintDeepInt(a);
   }

   public static int[] bodyTop() {
      syncTheme();
      return BODY_TOP;
   }

   public static int[] bodyBot() {
      syncTheme();
      return BODY_BOT;
   }

   public static Color bodyTop(float a) {
      syncTheme();
      return rgba(BODY_TOP, a);
   }

   public static Color bodyBot(float a) {
      syncTheme();
      return rgba(BODY_BOT, a);
   }

   public static int bodyTopInt(float a) {
      syncTheme();
      return rgbaInt(BODY_TOP, a);
   }

   public static int bodyBotInt(float a) {
      syncTheme();
      return rgbaInt(BODY_BOT, a);
   }

   public static void syncTheme() {
      int var0 = -7643914;
      int var1 = -9623335;

      try {
         ClientSettings var2 = ClientSettings.get();
         if (var2 != null) {
            var0 = var2.getPrimaryColor();
            var1 = var2.getSecondaryColor();
         }
      } catch (Throwable var23) {
      }

      float var24 = var0 >> 16 & 0xFF;
      float var3 = var0 >> 8 & 0xFF;
      float var4 = var0 & 0xFF;
      float var5 = var1 >> 16 & 0xFF;
      float var6 = var1 >> 8 & 0xFF;
      float var7 = var1 & 0xFF;
      int[] var8 = null;
      int[] var9 = null;

      try {
         var8 = ThemeManager.getTop();
         var9 = ThemeManager.getBot();
      } catch (Throwable var22) {
      }

      float var10 = clamp(20.0F + (var8 != null ? var8[0] : var24) * 0.12F);
      float var11 = clamp(21.0F + (var8 != null ? var8[1] : var3) * 0.12F);
      float var12 = clamp(24.0F + (var8 != null ? var8[2] : var4) * 0.12F);
      float var13 = clamp(14.0F + (var9 != null ? var9[0] : var5) * 0.08F);
      float var14 = clamp(15.0F + (var9 != null ? var9[1] : var6) * 0.08F);
      float var15 = clamp(17.0F + (var9 != null ? var9[2] : var7) * 0.08F);
      long var16 = System.currentTimeMillis();
      boolean var18 = false;

      try {
         var18 = AnimationManager.isOff();
      } catch (Throwable var21) {
      }

      if (animMint != null && !var18 && !isRainbow()) {
         float var19 = (float)(var16 - lastSyncMs) / 1000.0F;
         if (var19 > 0.0F) {
            if (var19 > 0.1F) {
               var19 = 0.1F;
            }

            lastSyncMs = var16;
            float var20 = 1.0F - (float)Math.exp(-12.0 * var19);
            animMint[0] = animMint[0] + (var24 - animMint[0]) * var20;
            animMint[1] = animMint[1] + (var3 - animMint[1]) * var20;
            animMint[2] = animMint[2] + (var4 - animMint[2]) * var20;
            animMintDeep[0] = animMintDeep[0] + (var5 - animMintDeep[0]) * var20;
            animMintDeep[1] = animMintDeep[1] + (var6 - animMintDeep[1]) * var20;
            animMintDeep[2] = animMintDeep[2] + (var7 - animMintDeep[2]) * var20;
            animBodyTop[0] = animBodyTop[0] + (var10 - animBodyTop[0]) * var20;
            animBodyTop[1] = animBodyTop[1] + (var11 - animBodyTop[1]) * var20;
            animBodyTop[2] = animBodyTop[2] + (var12 - animBodyTop[2]) * var20;
            animBodyBot[0] = animBodyBot[0] + (var13 - animBodyBot[0]) * var20;
            animBodyBot[1] = animBodyBot[1] + (var14 - animBodyBot[1]) * var20;
            animBodyBot[2] = animBodyBot[2] + (var15 - animBodyBot[2]) * var20;
         }
      } else {
         animMint = new float[]{var24, var3, var4};
         animMintDeep = new float[]{var5, var6, var7};
         animBodyTop = new float[]{var10, var11, var12};
         animBodyBot = new float[]{var13, var14, var15};
         lastSyncMs = var16;
      }

      MINT[0] = clamp(animMint[0]);
      MINT[1] = clamp(animMint[1]);
      MINT[2] = clamp(animMint[2]);
      MINT_DEEP[0] = clamp(animMintDeep[0]);
      MINT_DEEP[1] = clamp(animMintDeep[1]);
      MINT_DEEP[2] = clamp(animMintDeep[2]);
      BODY_TOP[0] = clamp(animBodyTop[0]);
      BODY_TOP[1] = clamp(animBodyTop[1]);
      BODY_TOP[2] = clamp(animBodyTop[2]);
      BODY_BOT[0] = clamp(animBodyBot[0]);
      BODY_BOT[1] = clamp(animBodyBot[1]);
      BODY_BOT[2] = clamp(animBodyBot[2]);
   }

   public static void drawTrafficLights(float x, float y, float alpha, boolean hoverCluster) {
   }

   public static void toggle(float x, float y, float w, float h, float on, float alpha) {
      float var6 = h / 2.0F;
      int var7 = rgbaInt(52, 54, 60, 210.0F * alpha);
      NanoVGRenderer.drawRoundedRect(x, y, w, h, var6, var7);
      NanoVGRenderer.drawRoundedRectOutline(x, y, w, h, var6, 1.0F, whiteInt(24.0F * alpha));
      if (on > 0.005F) {
         int var8;
         int var9;
         int var10;
         if (isRainbow()) {
            float var11 = (x + y) * 0.002F;
            var8 = rainbowInt(var11, 245.0F * on * alpha);
            var9 = rainbowInt(var11 + 0.24F, 245.0F * on * alpha);
            var10 = rainbowInt(var11, 170.0F * on * alpha);
         } else {
            var8 = mintInt(240.0F * on * alpha);
            var9 = mintDeepInt(240.0F * on * alpha);
            var10 = mintInt(160.0F * on * alpha);
         }

         NanoVGRenderer.drawRoundedRectGradient(x, y, w, h, var6, var8, var9);
         NanoVGRenderer.drawRoundedRectOutline(x, y, w, h, var6, 1.0F, var10);
      }

      float var12 = (h - 3.2F) / 2.0F;
      float var13 = x + 1.6F + var12 + (w - 3.2F - var12 * 2.0F) * on;
      float var14 = y + h / 2.0F;
      NanoVGRenderer.drawCircle(var13, var14 + 0.8F, var12, rgbaInt(0, 0, 0, 65.0F * alpha));
      NanoVGRenderer.drawCircle(var13, var14, var12, whiteInt(255.0F * alpha));
   }

   public static void label(String t, float x, float y, float size, Color c) {
      NanoVGRenderer.drawTextWithFont(t, x, y, size, c, NanoVGFontManager.getSfProFont());
   }

   public static void label(String t, float x, float y, float size, int c) {
      NanoVGRenderer.drawTextWithFont(t, x, y, size, c, NanoVGFontManager.getSfProFont());
   }

   public static void labelBold(String t, float x, float y, float size, Color c) {
      NanoVGRenderer.drawTextWithFont(t, x, y, size, c, NanoVGFontManager.getSfProSemiboldFont());
   }

   public static void labelBold(String t, float x, float y, float size, int c) {
      NanoVGRenderer.drawTextWithFont(t, x, y, size, c, NanoVGFontManager.getSfProSemiboldFont());
   }

   public static float width(String t, float size) {
      return NanoVGRenderer.getTextWidthWithFont(t, size, NanoVGFontManager.getSfProFont());
   }

   public static float widthBold(String t, float size) {
      return NanoVGRenderer.getTextWidthWithFont(t, size, NanoVGFontManager.getSfProSemiboldFont());
   }

   public static String fit(String t, float maxW, float size) {
      if (t != null && !t.isEmpty() && !(maxW <= 0.0F)) {
         if (width(t, size) <= maxW) {
            return t;
         }

         int var3 = 0;
         int var4 = t.length() - 1;
         int var5 = 0;

         while (var3 <= var4) {
            int var6 = var3 + var4 >>> 1;
            if (width(t.substring(0, var6) + "..", size) <= maxW) {
               var5 = var6;
               var3 = var6 + 1;
            } else {
               var4 = var6 - 1;
            }
         }

         return var5 > 0 ? t.substring(0, var5) + ".." : "";
      } else {
         return "";
      }
   }

   public static void softShadow(float x, float y, float w, float h, float r, float blur, float offY, float alpha) {
      long var8 = NVGRenderer.getContext();
      if (var8 != 0L && !(alpha <= 0.01F)) {
         NanoVG.nvgRGBA((byte)0, (byte)0, (byte)0, (byte)clamp(alpha), SHADOW_IN);
         NanoVG.nvgBoxGradient(var8, x, y + offY, w, h, r + blur * 0.5F, blur, SHADOW_IN, SHADOW_OUT, SHADOW_PAINT);
         NanoVG.nvgBeginPath(var8);
         NanoVG.nvgRect(var8, x - blur * 2.0F, y - blur * 2.0F + offY, w + blur * 4.0F, h + blur * 4.0F);
         NanoVG.nvgRoundedRect(var8, x, y, w, h, r);
         NanoVG.nvgPathWinding(var8, 2);
         NanoVG.nvgFillPaint(var8, SHADOW_PAINT);
         NanoVG.nvgFill(var8);
      }
   }

   static {
      int[] var10000 = new int[3];
      var10000[0] = 184;
      var10000[1] = 255;
      var10000[2] = 224;
      MINT = var10000;
      var10000 = new int[3];
      var10000[0] = 122;
      var10000[1] = 214;
      var10000[2] = 184;
      MINT_DEEP = var10000;
      var10000 = new int[3];
      var10000[0] = 28;
      var10000[1] = 29;
      var10000[2] = 33;
      BODY_TOP = var10000;
      var10000 = new int[3];
      var10000[0] = 18;
      var10000[1] = 19;
      var10000[2] = 22;
      BODY_BOT = var10000;
      var10000 = new int[3];
      var10000[0] = 245;
      var10000[1] = 252;
      var10000[2] = 249;
      TEXT = var10000;
      var10000 = new int[3];
      var10000[0] = 168;
      var10000[1] = 185;
      var10000[2] = 179;
      TEXT_DIM = var10000;
      NanoVG.nvgRGBA((byte)0, (byte)0, (byte)0, (byte)0, SHADOW_OUT);
   }
}
