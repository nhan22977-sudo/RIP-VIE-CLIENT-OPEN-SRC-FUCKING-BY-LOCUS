package me.drake.vieclient.render;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1060;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_918;

public final class CustomGlintManager {
   private static CustomGlintManager.GlintTexture currentTexture = CustomGlintManager.GlintTexture.VANILLA;
   private static CustomGlintManager.GlintColorMode currentColorMode = CustomGlintManager.GlintColorMode.ORIGINAL_TEXTURE;
   private static int lastAppliedColor = 0;
   private static boolean isCustomApplied = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private CustomGlintManager() {
   }

   public static synchronized void update(
      boolean enabled, CustomGlintManager.GlintTexture texture, CustomGlintManager.GlintColorMode colorMode, int customColor
   ) {
      class_310 var4 = class_310.method_1551();
      if (var4 != null && var4.method_1531() != null) {
         if (enabled && texture != CustomGlintManager.GlintTexture.VANILLA) {
            int var5 = customColor;
            boolean var6 = colorMode != CustomGlintManager.GlintColorMode.ORIGINAL_TEXTURE && var5 != lastAppliedColor;
            if (!isCustomApplied || currentTexture != texture || currentColorMode != colorMode || var6) {
               currentTexture = texture;
               currentColorMode = colorMode;
               lastAppliedColor = var5;
               boolean var7 = colorMode != CustomGlintManager.GlintColorMode.ORIGINAL_TEXTURE;
               applyTexture(var4, class_918.field_43087, texture, var7, var5);
               applyTexture(var4, class_918.field_43086, texture, var7, var5);
               isCustomApplied = true;
            }
         } else {
            if (isCustomApplied) {
               restoreVanilla(var4);
            }
         }
      }
   }

   public static synchronized void restoreVanilla(class_310 client) {
      if (isCustomApplied) {
         try {
            applyVanillaResource(client, class_918.field_43087);
            applyVanillaResource(client, class_918.field_43086);
         } catch (Throwable var5) {
         } finally {
            isCustomApplied = false;
            currentTexture = CustomGlintManager.GlintTexture.VANILLA;
         }
      }
   }

   private static void applyVanillaResource(class_310 client, class_2960 targetId) {
      if (client != null && client.method_1478() != null && client.method_1531() != null) {
         Optional var2 = client.method_1478().method_14486(targetId);
         if (!var2.isEmpty()) {
            class_1011 var3 = null;
            class_1043 var4 = null;

            try {
               try (InputStream var5 = ((class_3298)var2.get()).method_14482()) {
                  var3 = class_1011.method_4309(var5);
                  if (var3 != null) {
                     var4 = new class_1043(() -> "vanilla_" + targetId.method_12832(), var3);
                     class_1060 var6 = client.method_1531();

                     try {
                        var6.method_4615(targetId);
                     } catch (Throwable var11) {
                     }

                     var6.method_4616(targetId, var4);
                     return;
                  }
               }

               return;
            } catch (Throwable var13) {
               if (var4 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var9) {
                  }
               } else if (var3 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var8) {
                  }
               }
            }
         }
      }
   }

   private static void applyTexture(class_310 client, class_2960 targetId, CustomGlintManager.GlintTexture style, boolean tintEnabled, int tintColor) {
      String var5 = style.name().toLowerCase(Locale.ROOT) + ".png";
      byte[] var10000 = new byte[10];
      var10000[0] = (byte)(76);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-127);
      var10000[4] = (byte)(36);
      var10000[5] = (byte)(-108);
      var10000[6] = (byte)(-126);
      var10000[7] = (byte)(116);
      var10000[8] = (byte)(60);
      var10000[9] = (byte)(-42);
      class_2960 var6 = class_2960.method_60655($$v_s(var10000, 4172627928371417119L), "textures/glints/" + var5);
      Optional var7 = client.method_1478().method_14486(var6);
      if (!var7.isEmpty()) {
         class_1011 var8 = null;
         class_1043 var9 = null;

         try {
            try (InputStream var10 = ((class_3298)var7.get()).method_14482()) {
               var8 = class_1011.method_4309(var10);
               if (var8 != null) {
                  if (tintEnabled) {
                     tintImage(var8, tintColor);
                  }

                  var9 = new class_1043(() -> "customglint_" + style.name().toLowerCase(Locale.ROOT), var8);
                  class_1060 var11 = client.method_1531();

                  try {
                     var11.method_4615(targetId);
                  } catch (Throwable var16) {
                  }

                  var11.method_4616(targetId, var9);
                  return;
               }
            }

            return;
         } catch (Throwable var18) {
            if (var9 != null) {
               try {
                  var9.close();
               } catch (Throwable var14) {
               }
            } else if (var8 != null) {
               try {
                  var8.close();
               } catch (Throwable var13) {
               }
            }
         }
      }
   }

   private static void tintImage(class_1011 image, int color) {
      float var2 = (color >> (16) & (255)) / 255.0F;
      float var3 = (color >> (8) & (255)) / 255.0F;
      float var4 = (color & (255)) / 255.0F;
      float[] var5 = rgbToHsv(var2, var3, var4);
      if (!(var5[1] <= 0.025F)) {
         int var6 = image.method_4307();
         int var7 = image.method_4323();

         for (int var8 = 0; var8 < var7; var8++) {
            for (int var9 = 0; var9 < var6; var9++) {
               int var10 = image.method_61940(var9, var8);
               int var11 = var10 >>> (24) & (255);
               if (var11 != 0) {
                  float var12 = (var10 >> (16) & (255)) / 255.0F;
                  float var13 = (var10 >> (8) & (255)) / 255.0F;
                  float var14 = (var10 & (255)) / 255.0F;
                  float var15 = Math.max(Math.max(var12, var13), var14);
                  if (var15 > 0.001F) {
                     float var16 = var12 * 0.2126F + var13 * 0.7152F + var14 * 0.0722F;
                     float var17 = clampUnit(var15 * 0.55F + var16 * 0.45F);
                     float var18 = var15 - Math.min(Math.min(var12, var13), var14);
                     float var19 = clampUnit(var5[1] * (0.82F + var18 * 0.18F));
                     int var20 = hsvToRgb(var5[0], var19, var17);
                     image.method_61941(var9, var8, var11 << (24) | var20);
                  }
               }
            }
         }
      }
   }

   private static float[] rgbToHsv(float r, float g, float b) {
      float var3 = Math.max(Math.max(r, g), b);
      float var4 = Math.min(Math.min(r, g), b);
      float var5 = var3 - var4;
      float var6;
      if (var5 <= 1.0E-5F) {
         var6 = 0.0F;
      } else if (var3 == r) {
         var6 = (g - b) / var5 % 6.0F;
      } else if (var3 == g) {
         var6 = (b - r) / var5 + 2.0F;
      } else {
         var6 = (r - g) / var5 + 4.0F;
      }

      var6 /= 6.0F;
      if (var6 < 0.0F) {
         var6++;
      }

      float var7 = var3 <= 1.0E-5F ? 0.0F : var5 / var3;
      float[] var10000 = new float[3];
      var10000[0] = var6;
      var10000[1] = var7;
      var10000[2] = var3;
      return var10000;
   }

   private static int hsvToRgb(float hue, float saturation, float value) {
      float var3 = (hue - (float)Math.floor(hue)) * 6.0F;
      int var4 = (int)Math.floor(var3);
      float var5 = var3 - var4;
      float var6 = value * (1.0F - saturation);
      float var7 = value * (1.0F - saturation * var5);
      float var8 = value * (1.0F - saturation * (1.0F - var5));
      float var9;
      float var10;
      float var11;
      switch (var4) {
         case 0:
            var9 = value;
            var10 = var8;
            var11 = var6;
            break;
         case 1:
            var9 = var7;
            var10 = value;
            var11 = var6;
            break;
         case 2:
            var9 = var6;
            var10 = value;
            var11 = var8;
            break;
         case 3:
            var9 = var6;
            var10 = var7;
            var11 = value;
            break;
         case 4:
            var9 = var8;
            var10 = var6;
            var11 = value;
            break;
         default:
            var9 = value;
            var10 = var6;
            var11 = var7;
      }

      int var12 = Math.max(0, Math.min(255, Math.round(var9 * 255.0F)));
      int var13 = Math.max(0, Math.min(255, Math.round(var10 * 255.0F)));
      int var14 = Math.max(0, Math.min(255, Math.round(var11 * 255.0F)));
      return var12 << (16) | var13 << (8) | var14;
   }

   private static float clampUnit(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
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

   public enum GlintColorMode {
      ORIGINAL_TEXTURE("Original Texture"),
      CUSTOM_COLOR("Custom Color");

      private final String displayName;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      GlintColorMode(String displayName) {
         this.displayName = displayNamex;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static CustomGlintManager.GlintColorMode[] $values() {
         CustomGlintManager.GlintColorMode[] var10000 = new CustomGlintManager.GlintColorMode[2];
         var10000[0] = ORIGINAL_TEXTURE;
         var10000[1] = CUSTOM_COLOR;
         return var10000;
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

   public enum GlintTexture {
      VANILLA("Vanilla"),
      GALAXY("Galaxy"),
      RAINBOW("Rainbow"),
      SHOCKBLUE("ShockBlue"),
      SCARLET_CRIMSON("Scarlet Crimson"),
      GOLDEN("Golden"),
      PURPLE_DEFAULT("Purple Default"),
      ICE("Ice"),
      FIRE("Fire"),
      SUNSET("Sunset"),
      JADE("Jade"),
      AQUA("Aqua"),
      BOREALIS_XXVI("Borealis XXVI"),
      COPPER("Copper"),
      PINK("Pink"),
      FANCY("Old 1.12 Glint"),
      GAMMA("Gamma"),
      MIRROR("Mirror"),
      SUMMER("Summer");

      private final String displayName;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      GlintTexture(String displayName) {
         this.displayName = displayNamex;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static CustomGlintManager.GlintTexture[] $values() {
         CustomGlintManager.GlintTexture[] var10000 = new CustomGlintManager.GlintTexture[19];
         var10000[0] = VANILLA;
         var10000[1] = GALAXY;
         var10000[2] = RAINBOW;
         var10000[3] = SHOCKBLUE;
         var10000[4] = SCARLET_CRIMSON;
         var10000[5] = GOLDEN;
         var10000[6] = PURPLE_DEFAULT;
         var10000[7] = ICE;
         var10000[8] = FIRE;
         var10000[9] = SUNSET;
         var10000[10] = JADE;
         var10000[11] = AQUA;
         var10000[12] = BOREALIS_XXVI;
         var10000[13] = COPPER;
         var10000[14] = PINK;
         var10000[15] = FANCY;
         var10000[16] = GAMMA;
         var10000[17] = MIRROR;
         var10000[18] = SUMMER;
         return var10000;
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
}
