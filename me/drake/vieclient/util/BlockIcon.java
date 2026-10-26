package me.drake.vieclient.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NVGRenderer;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.lwjgl.nanovg.NanoVG;

public final class BlockIcon {
   private static final Map CACHE = new HashMap();
   private static final Color DIAMOND_OUTLINE = new Color(0, 0, 0, 180);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private BlockIcon() {
   }

   public static int get(String blockTexturePath) {
      Integer var1 = (Integer)CACHE.get(blockTexturePath);
      if (var1 != null) {
         return var1;
      }

      class_310 var2 = class_310.method_1551();
      if (var2 == null) {
         CACHE.put(blockTexturePath, -1);
         return -1;
      }

      try {
         byte[] var10000 = new byte[9];
         var10000[0] = (byte)(85);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-69);
         var10000[4] = (byte)(98);
         var10000[5] = (byte)(-85);
         var10000[6] = (byte)(40);
         var10000[7] = (byte)(67);
         var10000[8] = (byte)(36);
         class_2960 var3 = class_2960.method_60655($$v_s(var10000, 957586358874132509L), "textures/block/" + blockTexturePath + ".png");
         BufferedImage var4 = ImageIO.read(var2.method_1478().open(var3));
         if (var4 == null) {
            CACHE.put(blockTexturePath, -1);
            return -1;
         } else {
            int var5 = NVGImage.createFromBufferedImage(var4);
            CACHE.put(blockTexturePath, var5);
            return var5;
         }
      } catch (Throwable var6) {
         CACHE.put(blockTexturePath, -1);
         return -1;
      }
   }

   public static int grassTop() {
      byte[] var10000 = new byte[15];
      var10000[0] = (byte)(95);
      var10000[1] = (byte)(-24);
      var10000[2] = (byte)(125);
      var10000[3] = (byte)(-83);
      var10000[4] = (byte)(114);
      var10000[5] = (byte)(-122);
      var10000[6] = (byte)(43);
      var10000[7] = (byte)(73);
      var10000[8] = (byte)(63);
      var10000[9] = (byte)(-63);
      var10000[10] = (byte)(-113);
      var10000[11] = (byte)(-87);
      var10000[12] = (byte)(45);
      var10000[13] = (byte)(110);
      var10000[14] = (byte)(17);
      return get($$v_s(var10000, 957586358874132509L));
   }

   public static int netherrack() {
      byte[] var10000 = new byte[10];
      var10000[0] = (byte)(86);
      var10000[1] = (byte)(-1);
      var10000[2] = (byte)(104);
      var10000[3] = (byte)(-74);
      var10000[4] = (byte)(100);
      var10000[5] = (byte)(-85);
      var10000[6] = (byte)(59);
      var10000[7] = (byte)(68);
      var10000[8] = (byte)(51);
      var10000[9] = (byte)(-55);
      return get($$v_s(var10000, 957586358874132509L));
   }

   public static void drawRounded(int imageId, float x, float y, float size, float radius) {
      NVGImage.drawRoundedImage(imageId, x, y, size, size, radius, 1.0F);
   }

   public static void drawDiamond(float cx, float cy, float size, Color color) {
      if (NVGRenderer.isInFrame()) {
         long var4 = NVGRenderer.getContext();
         NanoVG.nvgSave(var4);

         try {
            NVGRenderer.applyColor(color, NVGRenderer.NVG_COLOR_1);
            NanoVG.nvgBeginPath(var4);
            NanoVG.nvgFillColor(var4, NVGRenderer.NVG_COLOR_1);
            float var6 = size * 0.5F;
            NanoVG.nvgMoveTo(var4, cx, cy - var6);
            NanoVG.nvgLineTo(var4, cx + var6, cy);
            NanoVG.nvgLineTo(var4, cx, cy + var6);
            NanoVG.nvgLineTo(var4, cx - var6, cy);
            NanoVG.nvgClosePath(var4);
            NanoVG.nvgFill(var4);
            NVGRenderer.applyColor(DIAMOND_OUTLINE, NVGRenderer.NVG_COLOR_1);
            NanoVG.nvgStrokeColor(var4, NVGRenderer.NVG_COLOR_1);
            NanoVG.nvgStrokeWidth(var4, 0.7F);
            NanoVG.nvgStroke(var4);
         } finally {
            NanoVG.nvgRestore(var4);
         }
      }
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
