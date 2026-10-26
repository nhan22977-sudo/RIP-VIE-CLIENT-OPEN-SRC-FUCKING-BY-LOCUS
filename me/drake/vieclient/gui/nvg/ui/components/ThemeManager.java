package me.drake.vieclient.gui.nvg.ui.components;

import java.awt.Color;
import me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.util.RainbowUtil;

public final class ThemeManager {
   public static int selectedTheme = 0;
   public static final int THEMES_VISIBLE = 18;
   private static float[] dTop;
   private static float[] dBot;
   private static float dLight = -1.0F;
   private static long lastMs = 0L;
   public static final int[][] THEMES = new int[][]{
      {70, 72, 84, 18, 19, 26},
      {175, 185, 205, 70, 78, 95},
      {110, 116, 130, 40, 44, 54},
      {95, 150, 255, 30, 60, 135},
      {175, 125, 255, 60, 38, 130},
      {70, 215, 150, 20, 90, 66},
      {120, 205, 255, 32, 95, 140},
      {255, 140, 180, 120, 42, 84},
      {245, 185, 95, 125, 82, 26},
      {245, 95, 95, 110, 28, 32},
      {150, 115, 235, 50, 32, 105},
      {240, 200, 90, 128, 92, 20},
      {255, 0, 127, 0, 240, 255},
      {255, 42, 133, 0, 240, 255},
      {11, 61, 46, 53, 230, 176},
      {121, 40, 202, 76, 29, 149},
      {255, 108, 116, 255, 200, 136},
      {255, 0, 128, 0, 230, 255}
   };
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private ThemeManager() {
   }

   public static boolean isDefaultKawase() {
      return true;
   }

   public static void setTheme(int idx) {
      if (idx < 0 || idx >= THEMES.length) {
         idx = 0;
      }

      selectedTheme = idx;

      try {
         ClientSettings var1 = ClientSettings.get();
         if (var1 != null && idx < ClientSettings.ThemePreset.values().length) {
            var1.setThemePreset(ClientSettings.ThemePreset.values()[idx]);
         }
      } catch (Throwable var2) {
      }
   }

   private static void advance() {
      try {
         ClientSettings var0 = ClientSettings.get();
         if (var0 != null && var0.getThemePreset() != null) {
            int var1 = var0.getThemePreset().ordinal();
            if (var1 >= 0 && var1 < THEMES.length && selectedTheme != var1) {
               selectedTheme = var1;
            }
         }
      } catch (Throwable var9) {
      }

      int var10 = Math.max(0, Math.min(THEMES.length - 1, selectedTheme));
      int[] var11;
      if (var10 != 17 && !isRainbowTheme()) {
         var11 = THEMES[var10];
      } else {
         int var2 = RainbowUtil.rainbow(1.0, 0.85, 0L, 255);
         int var3 = RainbowUtil.rainbow(1.0, 0.85, 600L, 255);
         var11 = new int[]{var2 >> 16 & 0xFF, var2 >> 8 & 0xFF, var2 & 0xFF, var3 >> 16 & 0xFF, var3 >> 8 & 0xFF, var3 & 0xFF};
      }

      float var12 = rawLight() ? 1.0F : 0.0F;
      if (dTop == null) {
         dTop = new float[]{var11[0], var11[1], var11[2]};
         dBot = new float[]{var11[3], var11[4], var11[5]};
         dLight = var12;
         lastMs = System.currentTimeMillis();
      } else {
         long var13 = System.currentTimeMillis();
         float var5 = (float)(var13 - lastMs) / 1000.0F;
         if (!(var5 <= 0.0F)) {
            if (var5 > 0.1F) {
               var5 = 0.1F;
            }

            lastMs = var13;

            boolean var6;
            try {
               var6 = AnimationManager.isOff();
            } catch (Throwable var8) {
               var6 = false;
            }

            float var7 = var6 ? 1.0F : 1.0F - (float)Math.exp(-9.0 * var5);
            dTop[0] = dTop[0] + (var11[0] - dTop[0]) * var7;
            dTop[1] = dTop[1] + (var11[1] - dTop[1]) * var7;
            dTop[2] = dTop[2] + (var11[2] - dTop[2]) * var7;
            dBot[0] = dBot[0] + (var11[3] - dBot[0]) * var7;
            dBot[1] = dBot[1] + (var11[4] - dBot[1]) * var7;
            dBot[2] = dBot[2] + (var11[5] - dBot[2]) * var7;
            dLight = dLight + (var12 - dLight) * var7;
         }
      }
   }

   public static boolean isLight() {
      return rawLight();
   }

   private static boolean rawLight() {
      return false;
   }

   private static int clamp(int v) {
      return Math.max(0, Math.min(255, v));
   }

   private static float lerp(float a, float b, float f) {
      return a + (b - a) * f;
   }

   private static int[] mix(float dr, float dg, float db, float lr, float lg, float lb) {
      int[] var10000 = new int[3];
      var10000[0] = clamp((int)lerp(dr, lr, dLight));
      var10000[1] = clamp((int)lerp(dg, lg, dLight));
      var10000[2] = clamp((int)lerp(db, lb, dLight));
      return var10000;
   }

   public static int[] bodyTop() {
      advance();
      float[] var0 = dTop;
      float[] var1 = dBot;
      if (isCleanStyle()) {
         int var2 = clamp((int)(var1[0] * 0.26F + var0[0] * 0.02F));
         int var3 = clamp((int)(var1[1] * 0.26F + var0[1] * 0.02F));
         int var4 = clamp((int)(var1[2] * 0.26F + var0[2] * 0.02F));
         int[] var10000 = new int[3];
         var10000[0] = Math.max(8, var2);
         var10000[1] = Math.max(8, var3);
         var10000[2] = Math.max(8, var4);
         return var10000;
      } else {
         return mix(
            Math.max(24.0F, var1[0] * 0.6F),
            Math.max(26.0F, var1[1] * 0.6F),
            Math.max(32.0F, var1[2] * 0.6F),
            255.0F - (255.0F - var0[0]) * 0.1F,
            255.0F - (255.0F - var0[1]) * 0.1F,
            255.0F - (255.0F - var0[2]) * 0.1F
         );
      }
   }

   public static int[] bodyBot() {
      advance();
      float[] var0 = dTop;
      float[] var1 = dBot;
      if (isCleanStyle()) {
         int var2 = clamp((int)(var1[0] * 0.13F + var0[0] * 0.01F));
         int var3 = clamp((int)(var1[1] * 0.13F + var0[1] * 0.01F));
         int var4 = clamp((int)(var1[2] * 0.13F + var0[2] * 0.01F));
         int[] var10000 = new int[3];
         var10000[0] = Math.max(5, var2);
         var10000[1] = Math.max(5, var3);
         var10000[2] = Math.max(5, var4);
         return var10000;
      } else {
         return mix(
            Math.max(10.0F, var1[0] * 0.28F),
            Math.max(12.0F, var1[1] * 0.28F),
            Math.max(16.0F, var1[2] * 0.28F),
            255.0F - (255.0F - var0[0]) * 0.2F,
            255.0F - (255.0F - var0[1]) * 0.2F,
            255.0F - (255.0F - var0[2]) * 0.2F
         );
      }
   }

   public static int[] borderRGB() {
      advance();
      if (isCleanStyle()) {
         int[] var10000 = new int[3];
         var10000[0] = 255;
         var10000[1] = 255;
         var10000[2] = 255;
         return var10000;
      } else {
         float[] var0 = dTop;
         return mix(
            Math.min(255.0F, var0[0] * 0.65F + 18.0F),
            Math.min(255.0F, var0[1] * 0.65F + 18.0F),
            Math.min(255.0F, var0[2] * 0.65F + 18.0F),
            120.0F,
            126.0F,
            140.0F
         );
      }
   }

   public static boolean isCleanStyle() {
      try {
         VieClient.GuiStyleMode var0 = (VieClient.GuiStyleMode)VieClient.guiStyle.getValue();
         return var0 == VieClient.GuiStyleMode.CLEAN;
      } catch (Throwable var1) {
         return false;
      }
   }

   public static int bodyAlpha(float a) {
      advance();
      return isCleanStyle() ? (int)(180.0F * a) : (int)(lerp(212.0F, 225.0F, dLight) * a);
   }

   public static Color text(int a) {
      return new Color(textArgb(a), true);
   }

   public static int textArgb(int a) {
      advance();
      int var1 = clamp(a);
      if (isCleanStyle()) {
         return var1 << (24) | 16777215;
      }

      int var2 = clamp((int)lerp(240.0F, 28.0F, dLight));
      int var3 = clamp((int)lerp(245.0F, 31.0F, dLight));
      int var4 = clamp((int)lerp(255.0F, 40.0F, dLight));
      return var1 << (24) | var2 << (16) | var3 << (8) | var4;
   }

   public static Color textDim(int a) {
      return new Color(textDimArgb(a), true);
   }

   public static int textDimArgb(int a) {
      advance();
      int var1 = clamp(a);
      if (isCleanStyle()) {
         return var1 << (24) | 11141120 | 46080 | 198;
      }

      int var2 = clamp((int)lerp(135.0F, 105.0F, dLight));
      int var3 = clamp((int)lerp(155.0F, 110.0F, dLight));
      int var4 = clamp((int)lerp(185.0F, 124.0F, dLight));
      return var1 << (24) | var2 << (16) | var3 << (8) | var4;
   }

   public static boolean isRainbowTheme() {
      try {
         ClientSettings var0 = ClientSettings.get();
         if (var0 != null && var0.isRainbowActive()) {
            return true;
         }
      } catch (Throwable var1) {
      }

      return selectedTheme == (17);
   }

   public static int[] getTop() {
      advance();
      int[] var10000 = new int[3];
      var10000[0] = clamp((int)dTop[0]);
      var10000[1] = clamp((int)dTop[1]);
      var10000[2] = clamp((int)dTop[2]);
      return var10000;
   }

   public static int[] getBot() {
      advance();
      int[] var10000 = new int[3];
      var10000[0] = clamp((int)dBot[0]);
      var10000[1] = clamp((int)dBot[1]);
      var10000[2] = clamp((int)dBot[2]);
      return var10000;
   }
}
