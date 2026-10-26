package me.drake.vieclient.render.nvg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;
import me.drake.vieclient.gui.nvg.ui.components.SoftPalette;
import net.minecraft.class_1058;
import net.minecraft.class_1087;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_7923;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public final class NVGImage {
   private static final Map itemTexCache = new HashMap();
   private static final int MAX_IMAGE_DIMENSION = 8192;
   private static final long MAX_IMAGE_PIXELS = 16777216L;
   private static final NVGPaint IMAGE_PAINT = NVGPaint.create();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static void drawMCItem(class_1799 stack, float x, float y, float size) {
      drawMCItem(stack, x, y, size, 0.0F, 1.0F);
   }

   public static void drawMCItem(class_1799 stack, float x, float y, float size, float alpha) {
      drawMCItem(stack, x, y, size, 0.0F, alpha);
   }

   public static void drawMCItem(class_1799 stack, float x, float y, float size, float radius, float alpha) {
      if (NVGRenderer.isInFrame() && stack != null && !stack.method_7960() && !(alpha <= 0.01F)) {
         class_1792 var6 = stack.method_7909();
         if (var6 != class_1802.field_8162) {
            int var7 = getOrLoadItemTexture(var6);
            if (var7 != (-1)) {
               if (radius > 0.05F) {
                  drawRoundedImage(var7, x, y, size, size, radius, alpha);
               } else {
                  drawImage(var7, x, y, size, size, alpha);
               }
            } else {
               int var8 = (int)(180.0F * alpha) << (24) | 2764345;
               int var9 = (int)(220.0F * alpha) << (24) | 4870240;
               float var10 = radius > 0.05F ? radius : 3.0F;
               NanoVGRenderer.drawRoundedRect(x, y, size, size, var10, var8);
               NanoVGRenderer.drawRoundedRectOutline(x, y, size, size, var10, 1.0F, var9);
               String var11 = var6.method_63680().getString();
               String var12 = var11.length() >= (2) ? var11.substring(0, 2).toUpperCase() : var11.toUpperCase();
               float var13 = SoftPalette.width(var12, 7.0F);
               SoftPalette.label(
                  var12, x + (size - var13) / 2.0F, y + (size - 7.0F) / 2.0F, 7.0F, (int)(230.0F * alpha) << (24) | 58879
               );
            }
         }
      }
   }

   private static int getOrLoadItemTexture(class_1792 item) {
      class_2960 var1 = class_7923.field_41178.method_10221(item);
      String var2 = var1.toString();
      Integer var3 = (Integer)itemTexCache.get(var2);
      if (var3 != null) {
         return var3;
      }

      class_310 var4 = class_310.method_1551();
      if (var4 == null) {
         return -1;
      }

      int var5 = -1;
      String var6 = var1.method_12832();
      String var7 = var1.method_12836();
      if (item == class_1802.field_8255 || "shield".equals(var6)) {
         var5 = tryLoadShieldTexture(var4);
      } else if (item == class_1802.field_8106 || "chest".equals(var6)) {
         var5 = tryLoadChestTexture(var4, "normal");
      } else if (item == class_1802.field_8466
         || "ender_chest".equals(var6)) {
         var5 = tryLoadChestTexture(var4, "ender");
      } else if (item == class_1802.field_8247
         || "trapped_chest".equals(var6)) {
         var5 = tryLoadChestTexture(var4, "trapped");
      } else if ("crossbow".equals(var6)) {
         var5 = tryLoadTexture(
            var4,
            "minecraft",
            "item",
            "crossbow_standby"
         );
      } else if ("compass".equals(var6)) {
         var5 = tryLoadTexture(
            var4,
            "minecraft",
            "item",
            "compass_16"
         );
         if (var5 == -1) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "item",
               "compass_00"
            );
         }
      } else if ("recovery_compass".equals(var6)) {
         var5 = tryLoadTexture(
            var4,
            "minecraft",
            "item",
            "recovery_compass_16"
         );
         if (var5 == -1) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "item",
               "recovery_compass_00"
            );
         }
      } else if ("clock".equals(var6)) {
         var5 = tryLoadTexture(
            var4,
            "minecraft",
            "item",
            "clock_00"
         );
      } else if ("tipped_arrow".equals(var6)) {
         var5 = tryLoadTippedArrowTexture(var4);
      } else if ("enchanted_golden_apple"
         .equals(var6)) {
         var5 = tryLoadTexture(
            var4,
            "minecraft",
            "item",
            "golden_apple"
         );
      } else if ("debug_stick".equals(var6)) {
         var5 = tryLoadTexture(
            var4,
            "minecraft",
            "item",
            "stick"
         );
      } else if ("skeleton_skull".equals(var6)) {
         var5 = tryLoadHeadTexture(
            var4,
            "textures/entity/skeleton/skeleton.png"
         );
      } else if ("wither_skeleton_skull"
         .equals(var6)) {
         var5 = tryLoadHeadTexture(
            var4,
            "textures/entity/skeleton/wither_skeleton.png"
         );
      } else if ("zombie_head".equals(var6)) {
         var5 = tryLoadHeadTexture(
            var4,
            "textures/entity/zombie/zombie.png"
         );
      } else if ("creeper_head".equals(var6)) {
         var5 = tryLoadHeadTexture(
            var4,
            "textures/entity/creeper/creeper.png"
         );
      } else if ("piglin_head".equals(var6)) {
         var5 = tryLoadHeadTexture(
            var4,
            "textures/entity/piglin/piglin.png"
         );
      } else if ("player_head".equals(var6)) {
         var5 = tryLoadHeadTexture(
            var4,
            "textures/entity/player/wide/steve.png"
         );
      } else if ("sticky_piston".equals(var6)) {
         var5 = tryLoadTexture(
            var4,
            "minecraft",
            "block",
            "piston_top_sticky"
         );
      } else if ("piston".equals(var6)) {
         var5 = tryLoadTexture(
            var4,
            "minecraft",
            "block",
            "piston_top"
         );
      }

      if (var5 == -1) {
         class_2248 var8 = item instanceof class_1747 var9 ? var9.method_7711() : class_2248.method_9503(item);
         if (var8 != null && var8 != class_2246.field_10124) {
            try {
               class_1087 var24 = var4.method_1554().method_4743().method_3335(var8.method_9564());
               if (var24 != null) {
                  class_1058 var10 = var24.method_68511();
                  if (var10 != null && var10.method_45851() != null) {
                     class_2960 var11 = var10.method_45851().method_45816();
                     var5 = tryLoadTexture(var4, var11.method_12836(), "", var11.method_12832());
                     if (var5 == -1) {
                        var5 = tryLoadTexture(
                           var4, var11.method_12836(), "block", var11.method_12832()
                        );
                     }
                  }
               }
            } catch (Throwable var12) {
            }
         }
      }

      if (var5 == -1) {
         var5 = tryLoadTexture(var4, var7, "item", var6);
      }

      if (var5 == -1) {
         var5 = tryLoadTexture(var4, var7, "block", var6);
      }

      if (var5 == -1) {
         var5 = tryLoadTexture(var4, var7, "block", var6 + "_front");
      }

      if (var5 == -1) {
         var5 = tryLoadTexture(var4, var7, "block", var6 + "_top");
      }

      if (var5 == -1) {
         var5 = tryLoadTexture(var4, var7, "block", var6 + "_side");
      }

      if (var5 == -1) {
         var5 = tryLoadTexture(var4, var7, "block", var6 + "_bottom");
      }

      if (var5 == -1) {
         var5 = tryLoadTexture(var4, var7, "block", var6 + "_front_horizontal");
      }

      if (var5 == -1) {
         var5 = tryLoadTexture(var4, var7, "block", var6 + "_front_off");
      }

      if (var5 == -1) {
         var5 = tryLoadTexture(var4, var7, "block", var6 + "_north");
      }

      if (var5 == -1) {
         class_2248 var13 = item instanceof class_1747 var25 ? var25.method_7711() : class_2248.method_9503(item);
         if (var13 != null && var13 != class_2246.field_10124) {
            class_2960 var26 = class_7923.field_41175.method_10221(var13);
            String var27 = var26.method_12832();
            String var28 = var26.method_12836();
            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var28, "block", var27);
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var28, "block", var27 + "_front");
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var28, "block", var27 + "_top");
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var28, "block", var27 + "_side");
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var28, "block", var27 + "_bottom");
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var28, "block", var27 + "_front_horizontal");
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var28, "block", var27 + "_front_off");
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var28, "block", var27 + "_north");
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var28, "item", var27);
            }
         }
      }

      if (var5 == -1) {
         if (var6.endsWith("_stairs")) {
            String var14 = var6.substring(0, var6.length() - "_stairs".length());
            var5 = tryLoadTexture(var4, var7, "block", var14 + "_planks");
            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var14);
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var14 + "_bricks");
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var14 + "s");
            }
         } else if (var6.endsWith("_slab")) {
            String var15 = var6.substring(0, var6.length() - "_slab".length());
            var5 = tryLoadTexture(var4, var7, "block", var15 + "_planks");
            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var15);
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var15 + "_bricks");
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var15 + "s");
            }
         } else if (var6.endsWith("_wall")) {
            String var16 = var6.substring(0, var6.length() - "_wall".length());
            var5 = tryLoadTexture(var4, var7, "block", var16);
            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var16 + "_bricks");
            }
         } else if (var6.endsWith("_fence_gate")) {
            String var17 = var6.substring(
               0, var6.length() - "_fence_gate".length()
            );
            var5 = tryLoadTexture(var4, var7, "block", var17 + "_planks");
            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var17);
            }
         } else if (var6.endsWith("_fence")) {
            String var18 = var6.substring(0, var6.length() - "_fence".length());
            var5 = tryLoadTexture(var4, var7, "block", var18 + "_planks");
            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var18);
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var18 + "_bricks");
            }
         } else if (var6.endsWith("_pressure_plate")) {
            String var19 = var6.substring(
               0,
               var6.length() - "_pressure_plate".length()
            );
            var5 = tryLoadTexture(var4, var7, "block", var19 + "_planks");
            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var19);
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var19 + "_block");
            }
         } else if (var6.endsWith("_button")) {
            String var20 = var6.substring(0, var6.length() - "_button".length());
            var5 = tryLoadTexture(var4, var7, "block", var20 + "_planks");
            if (var5 == -1) {
               var5 = tryLoadTexture(var4, var7, "block", var20);
            }
         }
      }

      if (var5 == -1) {
         if ("shulker_box".equals(var6)) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               "purpur_block"
            );
         } else if (var6.endsWith("_shulker_box")) {
            String var21 = var6.substring(
               0, var6.length() - "_shulker_box".length()
            );
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               var21 + "_concrete"
            );
            if (var5 == -1) {
               var5 = tryLoadTexture(
                  var4,
                  "minecraft",
                  "block",
                  var21 + "_terracotta"
               );
            }

            if (var5 == -1) {
               var5 = tryLoadTexture(
                  var4,
                  "minecraft",
                  "block",
                  var21 + "_wool"
               );
            }
         } else if (var6.endsWith("_bed")) {
            String var22 = var6.substring(0, var6.length() - "_bed".length());
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "item",
               var6
            );
            if (var5 == -1) {
               var5 = tryLoadTexture(
                  var4,
                  "minecraft",
                  "block",
                  var22 + "_wool"
               );
            }
         } else if (var6.endsWith("_banner")) {
            String var23 = var6.substring(0, var6.length() - "_banner".length());
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               var23 + "_wool"
            );
         } else if (var6.contains("water")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               "water_still"
            );
            if (var5 == -1) {
               var5 = tryLoadTexture(
                  var4,
                  "minecraft",
                  "item",
                  "water_bucket"
               );
            }
         } else if (var6.contains("lava")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               "lava_still"
            );
            if (var5 == -1) {
               var5 = tryLoadTexture(
                  var4,
                  "minecraft",
                  "item",
                  "lava_bucket"
               );
            }
         } else if (var6.contains("portal")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               "nether_portal"
            );
            if (var5 == -1) {
               var5 = tryLoadTexture(
                  var4,
                  "minecraft",
                  "block",
                  "obsidian"
               );
            }
         } else if (var6.contains("fire")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               "fire_0"
            );
            if (var5 == -1) {
               var5 = tryLoadTexture(
                  var4,
                  "minecraft",
                  "item",
                  "flint_and_steel"
               );
            }
         } else if (var6.equals("decorated_pot")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               "terracotta"
            );
         } else if (var6.equals("bell")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               "gold_block"
            );
         } else if (var6.contains("anvil")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               "anvil_top"
            );
         } else if (var6.equals("brewing_stand")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "item",
               "brewing_stand"
            );
         } else if (var6.equals("cauldron")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               "cauldron_side"
            );
         } else if (var6.equals("hopper")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "block",
               "hopper_top"
            );
            if (var5 == -1) {
               var5 = tryLoadTexture(
                  var4,
                  "minecraft",
                  "item",
                  "hopper"
               );
            }
         } else if (var6.contains("lantern")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "item",
               var6
            );
            if (var5 == -1) {
               var5 = tryLoadTexture(
                  var4,
                  "minecraft",
                  "block",
                  var6
               );
            }
         } else if (var6.contains("campfire")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "item",
               var6
            );
         } else if (var6.contains("candle")) {
            var5 = tryLoadTexture(
               var4,
               "minecraft",
               "item",
               var6
            );
         }
      }

      if (var5 != -1) {
         itemTexCache.put(var2, var5);
      }

      return var5;
   }

   private static InputStream openTextureStream(class_310 mc, class_2960 texId) {
      if (texId == null) {
         return null;
      }

      try {
         if (mc != null && mc.method_1478() != null) {
            Optional var2 = mc.method_1478().method_14486(texId);
            if (var2.isPresent()) {
               return ((class_3298)var2.get()).method_14482();
            }
         }
      } catch (Throwable var3) {
      }

      return openStream("assets/" + texId.method_12836() + "/" + texId.method_12832());
   }

   private static int tryLoadShieldTexture(class_310 mc) {
      try {
         class_2960 var1 = class_2960.method_60655(
            "minecraft",
            "textures/entity/shield_base_nopattern.png"
         );
         class_2960 var2 = class_2960.method_60655(
            "minecraft",
            "textures/gui/sprites/container/slot/shield.png"
         );
         InputStream var3 = openTextureStream(mc, var1);
         if (var3 == null) {
            var1 = class_2960.method_60655(
               "minecraft",
               "textures/entity/shield_base.png"
            );
            var3 = openTextureStream(mc, var1);
         }

         if (var3 == null) {
            var1 = class_2960.method_60655(
               "minecraft",
               "textures/entity/shield/base.png"
            );
            var3 = openTextureStream(mc, var1);
         }

         if (var3 == null) {
            return -1;
         }

         BufferedImage var4;
         try (InputStream var5 = var3) {
            var4 = ImageIO.read(var5);
         }

         if (var4 == null) {
            return -1;
         }

         InputStream var21 = openTextureStream(mc, var2);
         BufferedImage var6 = null;
         if (var21 != null) {
            try (InputStream var7 = var21) {
               var6 = ImageIO.read(var7);
            }
         }

         BufferedImage var22 = new BufferedImage(16, 16, 2);
         if (var6 != null) {
            for (int var8 = 0; var8 < 16; var8++) {
               int var9 = -1;
               int var10 = -1;

               for (int var11 = 0; var11 < 16; var11++) {
                  int var12 = var6.getRGB(var11, var8) >> 24 & 0xFF;
                  if (var12 > 50) {
                     if (var9 == -1) {
                        var9 = var11;
                     }

                     var10 = var11;
                  }
               }

               if (var9 != -1 && var10 != -1) {
                  for (int var26 = var9; var26 <= var10; var26++) {
                     boolean var27 = var26 == var9 || var26 == var10 || var8 <= 2 || var8 >= 14 || var26 - var9 <= 1 || var10 - var26 <= 1;
                     if (var27) {
                        int var13 = var4.getRGB(1, Math.min(var4.getHeight() - 1, var8));
                        var22.setRGB(var26, var8, var13);
                     } else {
                        int var28 = var4.getRGB(3 + var26 % 8, Math.min(var4.getHeight() - 1, var8));
                        if (var8 != 5 && var8 != 10 || var26 != var9 + 2 && var26 != var10 - 2) {
                           var22.setRGB(var26, var8, var28);
                        } else {
                           var22.setRGB(var26, var8, -8750470);
                        }
                     }
                  }
               }
            }
         } else {
            Graphics2D var23 = var22.createGraphics();
            var23.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int var24 = Math.min(12, var4.getWidth() - 1);
            int var25 = Math.min(22, var4.getHeight());
            var23.drawImage(var4, 2, 0, 14, 16, 1, 0, 1 + var24, var25, null);
            var23.dispose();
         }

         return createPixelArtFromBufferedImage(var22);
      } catch (Throwable var18) {
         return -1;
      }
   }

   private static int tryLoadHeadTexture(class_310 mc, String texturePath) {
      try {
         byte[] var10000 = new byte[9];
         var10000[0] = (byte)(-78);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-69);
         var10000[4] = (byte)(-100);
         var10000[5] = (byte)(61);
         var10000[6] = (byte)(-81);
         var10000[7] = (byte)(-122);
         var10000[8] = (byte)(-61);
         String var12 = $$v_s(var10000, -3977363164815437574L);
         byte[] var10002 = new byte[9];
         var10002[0] = (byte)(-85);
         var10002[1] = (byte)(-1);
         var10002[2] = (byte)(100);
         var10002[3] = (byte)(-86);
         var10002[4] = (byte)(-118);
         var10002[5] = (byte)(61);
         var10002[6] = (byte)(-85);
         var10002[7] = (byte)(-109);
         var10002[8] = (byte)(-104);
         class_2960 var2 = class_2960.method_60655(
            var12, texturePath.startsWith($$v_s(var10002, -3977363164815437574L)) ? texturePath : "textures/" + texturePath
         );
         InputStream var3 = openTextureStream(mc, var2);
         if (var3 == null) {
            return -1;
         }

         BufferedImage var4;
         try (InputStream var5 = var3) {
            var4 = ImageIO.read(var5);
         }

         if (var4 == null) {
            return -1;
         }

         BufferedImage var11 = new BufferedImage(16, 16, 2);
         Graphics2D var6 = var11.createGraphics();
         var6.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
         var6.drawImage(var4, 0, 0, 16, 16, 8, 8, 16, 16, null);
         if (var4.getWidth() >= (64) && var4.getHeight() >= (32)) {
            var6.drawImage(
               var4, 0, 0, 16, 16, 40, 8, 48, 16, null
            );
         }

         var6.dispose();
         return createPixelArtFromBufferedImage(var11);
      } catch (Throwable var10) {
         return -1;
      }
   }

   private static int tryLoadTippedArrowTexture(class_310 mc) {
      try {
         class_2960 var1 = class_2960.method_60655(
            "minecraft",
            "textures/item/tipped_arrow_base.png"
         );
         class_2960 var2 = class_2960.method_60655(
            "minecraft",
            "textures/item/tipped_arrow_head.png"
         );
         InputStream var3 = openTextureStream(mc, var1);
         if (var3 == null) {
            return tryLoadTexture(
               mc,
               $$v_s(new byte[]{(byte)-78, (byte)-13, (byte)114, (byte)-69, (byte)-100, (byte)61, (byte)-81, (byte)-122, (byte)-61}, -3977363164815437574L),
               $$v_s(new byte[]{(byte)-74, (byte)-18, (byte)121, (byte)-77}, -3977363164815437574L),
               $$v_s(new byte[]{(byte)-66, (byte)-24, (byte)110, (byte)-79, (byte)-120}, -3977363164815437574L)
            );
         }

         BufferedImage var4;
         try (InputStream var5 = var3) {
            var4 = ImageIO.read(var5);
         }

         if (var4 == null) {
            return tryLoadTexture(
               mc,
               $$v_s(new byte[]{(byte)-78, (byte)-13, (byte)114, (byte)-69, (byte)-100, (byte)61, (byte)-81, (byte)-122, (byte)-61}, -3977363164815437574L),
               $$v_s(new byte[]{(byte)-74, (byte)-18, (byte)121, (byte)-77}, -3977363164815437574L),
               $$v_s(new byte[]{(byte)-66, (byte)-24, (byte)110, (byte)-79, (byte)-120}, -3977363164815437574L)
            );
         }

         InputStream var24 = openTextureStream(mc, var2);
         BufferedImage var6 = null;
         if (var24 != null) {
            try (InputStream var7 = var24) {
               var6 = ImageIO.read(var7);
            }
         }

         BufferedImage var25 = new BufferedImage(16, 16, 2);
         Graphics2D var8 = var25.createGraphics();
         var8.drawImage(var4, 0, 0, null);
         if (var6 != null) {
            for (int var9 = 0; var9 < var6.getHeight(); var9++) {
               for (int var10 = 0; var10 < var6.getWidth(); var10++) {
                  int var11 = var6.getRGB(var10, var9);
                  int var12 = var11 >> 24 & 0xFF;
                  if (var12 > 0) {
                     int var13 = var11 >> 16 & 0xFF;
                     int var14 = var11 >> 8 & 0xFF;
                     int var15 = var11 & 0xFF;
                     int var16 = (int)(var13 * 0.25F);
                     int var17 = (int)(var14 * 0.85F);
                     int var18 = (int)(var15 * 1.0F);
                     var25.setRGB(var10, var9, var12 << 24 | var16 << 16 | var17 << 8 | var18);
                  }
               }
            }
         }

         var8.dispose();
         return createPixelArtFromBufferedImage(var25);
      } catch (Throwable var23) {
         return tryLoadTexture(
            mc,
            $$v_s(new byte[]{(byte)-78, (byte)-13, (byte)114, (byte)-69, (byte)-100, (byte)61, (byte)-81, (byte)-122, (byte)-61}, -3977363164815437574L),
            $$v_s(new byte[]{(byte)-74, (byte)-18, (byte)121, (byte)-77}, -3977363164815437574L),
            $$v_s(new byte[]{(byte)-66, (byte)-24, (byte)110, (byte)-79, (byte)-120}, -3977363164815437574L)
         );
      }
   }

   private static int tryLoadChestTexture(class_310 mc, String chestType) {
      try {
         byte[] var10000 = new byte[9];
         var10000[0] = (byte)(-78);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-69);
         var10000[4] = (byte)(-100);
         var10000[5] = (byte)(61);
         var10000[6] = (byte)(-81);
         var10000[7] = (byte)(-122);
         var10000[8] = (byte)(-61);
         class_2960 var2 = class_2960.method_60655($$v_s(var10000, -3977363164815437574L), "textures/entity/chest/" + chestType + ".png");
         InputStream var3 = openTextureStream(mc, var2);
         if (var3 == null) {
            return -1;
         }

         try (InputStream var4 = var3) {
            BufferedImage var5 = ImageIO.read(var4);
            if (var5 == null) {
               return -1;
            }

            BufferedImage var6 = new BufferedImage(16, 16, 2);
            Graphics2D var7 = var6.createGraphics();

            try {
               var7.drawImage(
                  var5, 1, 0, 15, 5, 14, 14, 28, 19, null
               );
               var7.drawImage(
                  var5,
                  1,
                  5,
                  15,
                  15,
                  14,
                  33,
                  28,
                  43,
                  null
               );
               var7.drawImage(
                  var5, 7, 3, 9, 7, 1, 1, 3, 5, null
               );
            } finally {
               var7.dispose();
            }

            return createPixelArtFromBufferedImage(var6);
         }
      } catch (Exception var16) {
         return -1;
      }
   }

   private static int tryLoadTexture(class_310 mc, String ns, String type, String path) {
      if (path != null && !path.isEmpty()) {
         if (ns == null || ns.isEmpty()) {
            ns = "minecraft";
         }

         if (path.startsWith("textures/")) {
            path = path.substring("textures/".length());
         }

         if (path.endsWith(".png")) {
            path = path.substring(0, path.length() - 4);
         }

         try {
            class_2960 var4 = type != null && !type.isEmpty()
               ? class_2960.method_60655(ns, "textures/" + type + "/" + path + ".png")
               : class_2960.method_60655(ns, "textures/" + path + ".png");
            InputStream var5 = openTextureStream(mc, var4);
            if (var5 == null) {
               return -1;
            }

            try (InputStream var6 = var5) {
               BufferedImage var7 = ImageIO.read(var6);
               return var7 == null ? -1 : createPixelArtFromBufferedImage(var7);
            }
         } catch (Exception var11) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public static InputStream openStream(String path) {
      if (path != null && !path.isEmpty()) {
         if (path.startsWith("/")) {
            path = path.substring(1);
         }

         byte[] var10001 = new byte[7];
         var10001[0] = (byte)(-66);
         var10001[1] = (byte)(-23);
         var10001[2] = (byte)(111);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(-117);
         var10001[5] = (byte)(60);
         var10001[6] = (byte)(-31);
         if (path.startsWith($$v_s(var10001, -3977363164815437574L))) {
            int var1 = path.indexOf(47, 7);
            if (var1 != (-1)) {
               String var2 = path.substring(7, var1);
               String var3 = path.substring(var1 + 1);

               try {
                  class_310 var4 = class_310.method_1551();
                  if (var4 != null && var4.method_1478() != null) {
                     Optional var5 = var4.method_1478().method_14486(class_2960.method_60655(var2, var3));
                     if (var5.isPresent()) {
                        return ((class_3298)var5.get()).method_14482();
                     }
                  }
               } catch (Throwable var6) {
               }
            }
         }

         InputStream var7 = NVGImage.class.getClassLoader().getResourceAsStream(path);
         if (var7 == null) {
            var7 = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
         }

         if (var7 == null && !path.startsWith("/")) {
            var7 = NVGImage.class.getResourceAsStream("/" + path);
         }

         return var7;
      } else {
         return null;
      }
   }

   private NVGImage() {
   }

   public static int loadImageFile(String absPath) {
      long var1 = NVGRenderer.getContext();
      if (var1 == 0L) {
         return -1;
      }

      try {
         int var3 = NanoVG.nvgCreateImage(var1, absPath, 1);
         return var3 == 0 ? -1 : var3;
      } catch (Throwable var4) {
         return -1;
      }
   }

   public static int loadImage(String path) {
      return loadImageInternal(path, 1);
   }

   public static int loadImageRepeat(String path) {
      return loadImageInternal(path, 7);
   }

   private static int loadImageInternal(String path, int flags) {
      long var2 = NVGRenderer.getContext();
      if (var2 == 0L) {
         return -1;
      }

      ByteBuffer var4 = null;

      try {
         InputStream var5 = openStream(path);
         if (var5 == null) {
            return -1;
         }

         var4 = MemoryUtil.memAlloc(8192);
         ReadableByteChannel var6 = Channels.newChannel(var5);

         while (var6.read(var4) != (-1)) {
            if (var4.remaining() == 0) {
               ByteBuffer var7 = MemoryUtil.memAlloc(var4.capacity() * (2));
               var4.flip();
               var7.put(var4);
               MemoryUtil.memFree(var4);
               var4 = var7;
            }
         }

         var4.flip();
         boolean var28 = false;
         if (!NVGRenderer.isInFrame()) {
            GLStateManager.setup();
            GLStateManager.push();
            var28 = true;
         }

         try {
            int var8 = NanoVG.nvgCreateImageMem(var2, flags, var4);
            return var8 == 0 ? -1 : var8;
         } finally {
            if (var28) {
               try {
                  GLStateManager.pop();
               } catch (Throwable var24) {
               }
            }
         }
      } catch (Exception var26) {
         return -1;
      } finally {
         if (var4 != null) {
            MemoryUtil.memFree(var4);
         }
      }
   }

   public static int loadImageWhite(String path) {
      long var1 = NVGRenderer.getContext();
      if (var1 == 0L) {
         return -1;
      }

      try (InputStream var3 = openStream(path)) {
         if (var3 == null) {
            return -1;
         }

         BufferedImage var4 = ImageIO.read(var3);
         if (var4 == null) {
            return -1;
         }

         int var5 = var4.getWidth();
         int var6 = var4.getHeight();
         int[] var7 = var4.getRGB(0, 0, var5, var6, null, 0, var5);
         boolean var8 = false;

         for (int var12 : var7) {
            if ((var12 >>> 24 & 0xFF) < 250) {
               var8 = true;
               break;
            }
         }

         int[] var50 = new int[var5 * var6];

         for (int var51 = 0; var51 < var7.length; var51++) {
            int var53 = var7[var51];
            if (var8) {
               var50[var51] = var53 >>> 24 & 0xFF;
            } else {
               int var55 = var53 >> 16 & 0xFF;
               int var13 = var53 >> 8 & 0xFF;
               int var14 = var53 & 0xFF;
               var50[var51] = 255 - (var55 * 30 + var13 * 59 + var14 * 11) / 100;
            }
         }

         int var52 = var5;
         int var54 = var6;
         int var56 = -1;
         int var57 = -1;

         for (int var58 = 0; var58 < var6; var58++) {
            for (int var15 = 0; var15 < var5; var15++) {
               if (var50[var58 * var5 + var15] > 24) {
                  if (var15 < var52) {
                     var52 = var15;
                  }

                  if (var15 > var56) {
                     var56 = var15;
                  }

                  if (var58 < var54) {
                     var54 = var58;
                  }

                  if (var58 > var57) {
                     var57 = var58;
                  }
               }
            }
         }

         if (var56 < var52) {
            var52 = 0;
            var54 = 0;
            var56 = var5 - 1;
            var57 = var6 - 1;
         }

         int var59 = var56 - var52 + 1;
         int var60 = var57 - var54 + 1;
         int var16 = Math.max(var59, var60);
         int var17 = (var16 - var59) / 2;
         int var18 = (var16 - var60) / 2;
         ByteBuffer var19 = MemoryUtil.memAlloc(var16 * var16 * 4);

         try {
            for (int var20 = 0; var20 < var16; var20++) {
               for (int var21 = 0; var21 < var16; var21++) {
                  int var22 = var21 - var17 + var52;
                  int var23 = var20 - var18 + var54;
                  int var24 = var22 >= var52 && var22 <= var56 && var23 >= var54 && var23 <= var57 ? var50[var23 * var5 + var22] : 0;
                  var19.put((byte)-1).put((byte)-1).put((byte)-1).put((byte)var24);
               }
            }

            var19.flip();
            boolean var61 = false;
            if (!NVGRenderer.isInFrame()) {
               GLStateManager.setup();
               GLStateManager.push();
               var61 = true;
            }

            try {
               int var62 = NanoVG.nvgCreateImageRGBA(var1, var16, var16, 1, var19);
               return var62 == 0 ? -1 : var62;
            } finally {
               if (var61) {
                  try {
                     GLStateManager.pop();
                  } catch (Throwable var43) {
                  }
               }
            }
         } finally {
            MemoryUtil.memFree(var19);
         }
      } catch (Exception var47) {
         return -1;
      }
   }

   public static int createFromBufferedImage(BufferedImage img) {
      return createFromBufferedImage(img, 1);
   }

   public static int createPixelArtFromBufferedImage(BufferedImage img) {
      return createFromBufferedImage(img, 32);
   }

   private static int createFromBufferedImage(BufferedImage img, int flags) {
      if (img == null) {
         return -1;
      }

      long var2 = NVGRenderer.getContext();
      if (var2 == 0L) {
         return -1;
      }

      int var4 = img.getWidth();
      int var5 = img.getHeight();
      if (var5 > var4 && var4 > 0 && var5 % var4 == 0) {
         try {
            img = img.getSubimage(0, 0, var4, var4);
            var5 = var4;
         } catch (Throwable var26) {
         }
      }

      if (!isSafeImageSize(var4, var5)) {
         return -1;
      }

      int[] var6 = new int[var4 * var5];
      img.getRGB(0, 0, var4, var5, var6, 0, var4);
      ByteBuffer var7 = MemoryUtil.memAlloc(var4 * var5 * (4));

      try {
         for (int var11 : var6) {
            var7.put((byte)(var11 >> (16) & (255)));
            var7.put((byte)(var11 >> (8) & (255)));
            var7.put((byte)(var11 & (255)));
            var7.put((byte)(var11 >> (24) & (255)));
         }

         var7.flip();
         boolean var29 = false;
         if (!NVGRenderer.isInFrame()) {
            GLStateManager.setup();
            GLStateManager.push();
            var29 = true;
         }

         try {
            int var30 = NanoVG.nvgCreateImageRGBA(var2, var4, var5, flags, var7);
            return var30 == 0 ? -1 : var30;
         } finally {
            if (var29) {
               try {
                  GLStateManager.pop();
               } catch (Throwable var25) {
               }
            }
         }
      } finally {
         MemoryUtil.memFree(var7);
      }
   }

   static boolean isSafeImageSize(int width, int height) {
      if (width <= 0 || height <= 0) {
         return false;
      } else {
         return width <= (8192) && height <= (8192) ? (long)width * height <= 16777216L : false;
      }
   }

   public static void invalidateResourceImages() {
      for (int var1 : itemTexCache.values()) {
         if (var1 != (-1)) {
            deleteImage(var1);
         }
      }

      itemTexCache.clear();
   }

   public static void deleteImage(int imageId) {
      if (imageId != (-1)) {
         long var1 = NVGRenderer.getContext();
         if (var1 != 0L) {
            NanoVG.nvgDeleteImage(var1, imageId);
         }
      }
   }

   public static void drawImage(int imageId, float x, float y, float width, float height) {
      drawImage(imageId, x, y, width, height, 1.0F);
   }

   public static void drawImage(int imageId, float x, float y, float width, float height, float alpha) {
      if (NVGRenderer.isInFrame() && imageId != (-1) && !(alpha <= 0.0F)) {
         long var6 = NVGRenderer.getContext();
         NanoVG.nvgSave(var6);

         try {
            NanoVG.nvgImagePattern(var6, x, y, width, height, 0.0F, imageId, alpha, IMAGE_PAINT);
            NanoVG.nvgBeginPath(var6);
            NanoVG.nvgRect(var6, x, y, width, height);
            NanoVG.nvgFillPaint(var6, IMAGE_PAINT);
            NanoVG.nvgFill(var6);
         } finally {
            NanoVG.nvgRestore(var6);
         }
      }
   }

   public static void drawImage(int imageId, float x, float y, float width, float height, int argb) {
      int var6 = argb >>> (24) & (255);
      if (var6 > 0) {
         drawImage(imageId, x, y, width, height, var6 * 0.003921569F);
      }
   }

   public static void drawImage(int imageId, float x, float y, float width, float height, Color tint) {
      if (tint != null) {
         drawImage(imageId, x, y, width, height, tint.getAlpha() * 0.003921569F);
      }
   }

   public static void drawRoundedImage(int imageId, float x, float y, float width, float height, float radius) {
      drawRoundedImage(imageId, x, y, width, height, radius, 1.0F);
   }

   public static void drawRoundedImage(int imageId, float x, float y, float width, float height, float radius, float alpha) {
      if (NVGRenderer.isInFrame() && imageId != (-1) && !(alpha <= 0.0F)) {
         long var7 = NVGRenderer.getContext();
         NanoVG.nvgSave(var7);

         try {
            NanoVG.nvgImagePattern(var7, x, y, width, height, 0.0F, imageId, alpha, IMAGE_PAINT);
            NanoVG.nvgBeginPath(var7);
            NanoVG.nvgRoundedRect(var7, x, y, width, height, radius);
            NanoVG.nvgFillPaint(var7, IMAGE_PAINT);
            NanoVG.nvgFill(var7);
         } finally {
            NanoVG.nvgRestore(var7);
         }
      }
   }

   public static void drawRoundedImage(int imageId, float x, float y, float width, float height, float radius, int argb) {
      int var7 = argb >>> (24) & (255);
      if (var7 > 0) {
         drawRoundedImage(imageId, x, y, width, height, radius, var7 * 0.003921569F);
      }
   }

   public static void drawRoundedImage(int imageId, float x, float y, float width, float height, float radius, Color tint) {
      if (tint != null) {
         drawRoundedImage(imageId, x, y, width, height, radius, tint.getAlpha() * 0.003921569F);
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
