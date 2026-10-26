package me.drake.vieclient.modules.client.hud;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.gui.nvg.ui.components.ThemeManager;
import me.drake.vieclient.modules.client.HUD;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1304;
import net.minecraft.class_1747;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;

public final class ArmorStatusWidget {
   public static final float SLOT_SIZE = 22.0F;
   public static final float SLOT_GAP = 4.0F;
   public static final float PADDING = 4.5F;
   private final class_310 mc = class_310.method_1551();
   private boolean wasVisible = false;
   private long shownMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static float calculateWidth(HUD.ArmorLayout layout, boolean showHands, HUD.DuraMode duraMode) {
      int var3 = showHands ? 6 : 4;
      if (layout == HUD.ArmorLayout.HORIZONTAL) {
         return var3 * 22.0F + (var3 - 1) * 4.0F + 9.0F;
      } else if (duraMode == HUD.DuraMode.NONE) {
         return 31.0F;
      } else {
         return duraMode == HUD.DuraMode.DAMAGE ? 83.0F : 65.0F;
      }
   }

   public static float calculateHeight(HUD.ArmorLayout layout, boolean showHands, HUD.DuraMode duraMode) {
      int var3 = showHands ? 6 : 4;
      if (layout == HUD.ArmorLayout.HORIZONTAL) {
         return duraMode == HUD.DuraMode.NONE ? 31.0F : 41.0F;
      } else {
         return var3 * 22.0F + (var3 - 1) * 4.0F + 9.0F;
      }
   }

   public void draw(HUD hud, HUD.ArmorLayout layout, boolean showHands, HUD.DuraMode duraMode) {
      this.draw(hud, layout, showHands, duraMode, true);
   }

   public void draw(HUD hud, HUD.ArmorLayout layout, boolean showHands, HUD.DuraMode duraMode, boolean showBackdrop) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         List var6 = this.getEquippedItems(showHands);
         boolean var7 = true;

         for (class_1799 var9 : var6) {
            if (var9 != null && !var9.method_7960()) {
               var7 = false;
               break;
            }
         }

         boolean var28 = hud.isEditorMode()
            || hud.isDragging()
            || "armor".equals(hud.getSelectedWidget())
            || "armor".equals(hud.getDraggingWidget());
         if (var7 && !var28) {
            this.onHidden();
            hud.removeExternalBounds("armor");
         } else {
            if (var28) {
               List var29 = this.getPreviewItems(showHands);
               if (var6.isEmpty()) {
                  var6 = var29;
               } else {
                  ArrayList var10 = new ArrayList(var6.size());

                  for (int var11 = 0; var11 < var6.size(); var11++) {
                     class_1799 var12 = (class_1799)var6.get(var11);
                     if ((var12 == null || var12.method_7960()) && var11 < var29.size()) {
                        var10.add((class_1799)var29.get(var11));
                     } else {
                        var10.add(var12);
                     }
                  }

                  var6 = var10;
               }
            }

            if (!this.wasVisible) {
               this.shownMs = System.currentTimeMillis();
            }

            this.wasVisible = true;
            NanoVGRenderer.globalAlpha(hud.fadeIn(this.shownMs));
            int var30 = var6.size();
            float var31 = calculateWidth(layout, showHands, duraMode);
            float var32 = calculateHeight(layout, showHands, duraMode);
            int var33 = this.mc.method_22683().method_4486();
            int var13 = this.mc.method_22683().method_4502();
            float var14 = Math.max(1.0F, Math.min(var33 - var31 - 1.0F, var33 - var31 - 6.0F));
            float var15 = Math.max(1.0F, Math.min(var13 - var32 - 1.0F, var13 / 2.0F - var32 / 2.0F));
            float var16 = hud.wx("armor", var14);
            float var17 = hud.wy("armor", var15);
            hud.cacheBounds("armor", var16, var17, var31, var32);

            try (HUD.WidgetRenderScope var18 = hud.beginWidgetRender("armor", var16, var17)) {
               boolean var19 = ThemeManager.isCleanStyle();
               HUD.drawCardBox(var16, var17, var31, var32, 5.5F);
               float var20 = var16 + 4.5F;
               float var21 = var17 + 4.5F;

               for (int var22 = 0; var22 < var30; var22++) {
                  class_1799 var23 = (class_1799)var6.get(var22);
                  float var24;
                  float var25;
                  if (layout == HUD.ArmorLayout.HORIZONTAL) {
                     var24 = var20 + var22 * 26.0F;
                     var25 = var21;
                  } else {
                     var24 = var20;
                     var25 = var21 + var22 * 26.0F;
                  }

                  this.renderSlot(var23, var24, var25, var19, layout, duraMode);
               }
            }

            hud.drawEditorOutlineForWidget("armor");
            NanoVGRenderer.globalAlpha(1.0F);
         }
      } else {
         this.wasVisible = false;
         hud.removeExternalBounds("armor");
      }
   }

   private void renderSlot(class_1799 stack, float sx, float sy, boolean clean, HUD.ArmorLayout layout, HUD.DuraMode duraMode) {
      boolean var7 = stack == null || stack.method_7960();
      int var8 = PlayerHudWidget.getItemTierColorInt(stack);
      float var9 = 1.0F;
      boolean var10 = false;
      int var11 = 0;
      int var12 = 0;
      int var13 = 0;
      if (!var7 && stack.method_7963()) {
         var10 = true;
         var11 = stack.method_7936();
         var12 = stack.method_7919();
         var13 = Math.max(0, var11 - var12);
         var9 = Math.max(0.0F, Math.min(1.0F, (float)var13 / var11));
      }

      boolean var14 = var10 && var9 < 0.15F;
      int var15;
      if (var7) {
         var15 = clean ? 318767103 : 520093695;
      } else if (var14) {
         float var16 = (float)(0.5 + 0.5 * Math.sin(System.currentTimeMillis() / 130.0));
         var15 = PlayerHudWidget.mixColorInts(var8, -251712211, var16);
      } else {
         var15 = var8;
      }

      float var28 = clean ? 4.5F : 3.5F;
      int var17 = clean ? 218103807 : 1677721600;
      NanoVGRenderer.drawRoundedRect(sx, sy, 22.0F, 22.0F, var28, var17);
      if (!clean || var14) {
         NanoVGRenderer.drawRoundedRectOutline(sx, sy, 22.0F, 22.0F, var28, 0.85F, var15);
      }

      if (!var7) {
         NVGImage.drawMCItem(stack, sx + 2.0F, sy + 2.0F, 18.0F);
         if (var10) {
            float var18 = 18.0F * var9;
            int var19 = PlayerHudWidget.healthColorInt(var9);
            NanoVGRenderer.drawRoundedRect(sx + 2.0F, sy + 22.0F - 2.8F, 18.0F, 1.4F, 0.5F, 2013265920);
            if (var18 > 0.5F) {
               NanoVGRenderer.drawRoundedRect(sx + 2.0F, sy + 22.0F - 2.8F, var18, 1.4F, 0.5F, var19);
            }
         }

         if (stack.method_7947() > 1) {
            String var29 = String.valueOf(stack.method_7947());
            float var31 = NanoVGRenderer.getTextWidth(var29, 6.5F, true);
            NanoVGRenderer.drawText(var29, sx + 22.0F - var31 - 1.5F, sy + 22.0F - 5.5F, 6.5F, -1, true);
         }
      }

      if (duraMode != HUD.DuraMode.NONE) {
         boolean var30 = isBlockItem(stack);
         int var32 = !var7 && !var10 ? countHeldItem(this.mc, stack) : 0;
         String var20 = getSlotDisplayText(stack, duraMode, var32);
         int var21 = -1;
         if (var30) {
            var21 = -2302751;
         } else if (var10) {
            var21 = var14 ? -46261 : PlayerHudWidget.healthColorInt(var9);
         } else if (var20 != null && var20.startsWith("x")) {
            var21 = -2302751;
         } else {
            var21 = -11801746;
         }

         if (var20 != null) {
            if (layout == HUD.ArmorLayout.VERTICAL) {
               float var22 = sy + 7.25F;
               NanoVGRenderer.drawText(var20, sx + 22.0F + 4.5F, var22, 7.5F, var21, true);
            } else {
               float var34 = NanoVGRenderer.getTextWidth(var20, 6.5F, true);
               float var23 = sx + (22.0F - var34) / 2.0F;
               float var24 = sy + 22.0F + 2.0F;
               NanoVGRenderer.drawText(var20, var23, var24, 6.5F, var21, true);
            }
         }
      }
   }

   private List getEquippedItems(boolean showHands) {
      ArrayList var2 = new ArrayList(6);
      if (this.mc.field_1724 == null) {
         return var2;
      }

      var2.add(this.mc.field_1724.method_6118(class_1304.field_6169));
      var2.add(this.mc.field_1724.method_6118(class_1304.field_6174));
      var2.add(this.mc.field_1724.method_6118(class_1304.field_6172));
      var2.add(this.mc.field_1724.method_6118(class_1304.field_6166));
      if (showHands) {
         var2.add(this.mc.field_1724.method_6047());
         var2.add(this.mc.field_1724.method_6079());
      }

      return var2;
   }

   private List getPreviewItems(boolean showHands) {
      ArrayList var2 = new ArrayList(6);
      class_1799 var3 = new class_1799(class_1802.field_22027);
      var3.method_7974(var3.method_7936() / (10));
      var2.add(var3);
      class_1799 var4 = new class_1799(class_1802.field_22028);
      var4.method_7974(var4.method_7936() / (4));
      var2.add(var4);
      class_1799 var5 = new class_1799(class_1802.field_22029);
      var5.method_7974(var5.method_7936() / (2));
      var2.add(var5);
      class_1799 var6 = new class_1799(class_1802.field_22030);
      var6.method_7974((int)(var6.method_7936() * 0.88F));
      var2.add(var6);
      if (showHands) {
         class_1799 var7 = new class_1799(class_1802.field_22022);
         var2.add(var7);
         class_1799 var8 = new class_1799(class_1802.field_8288);
         var2.add(var8);
      }

      return var2;
   }

   public boolean hasAnyItems(boolean showHands) {
      if (this.mc.field_1724 == null) {
         return false;
      }

      if (!this.mc.field_1724.method_6118(class_1304.field_6169).method_7960()) {
         return true;
      }

      if (!this.mc.field_1724.method_6118(class_1304.field_6174).method_7960()) {
         return true;
      }

      if (!this.mc.field_1724.method_6118(class_1304.field_6172).method_7960()) {
         return true;
      }

      if (!this.mc.field_1724.method_6118(class_1304.field_6166).method_7960()) {
         return true;
      }

      if (showHands) {
         if (!this.mc.field_1724.method_6047().method_7960()) {
            return true;
         }

         if (!this.mc.field_1724.method_6079().method_7960()) {
            return true;
         }
      }

      return false;
   }

   public void onHidden() {
      this.wasVisible = false;
   }

   public static boolean isBlockItem(class_1799 stack) {
      return stack != null && !stack.method_7960() && stack.method_7909() instanceof class_1747;
   }

   public static int countHeldItem(class_310 mc, class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         int var2 = mcx != null && mcx.field_1724 != null ? InventoryUtil.countItem(stack.method_7909()) : stack.method_7947();
         return var2 > 0 ? var2 : stack.method_7947();
      } else {
         return 0;
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static String getSlotDisplayText(class_1799 stack, HUD.DuraMode duraMode, int totalCount) {
      if (stack == null || stack.method_7960() || duraMode == HUD.DuraMode.NONE) {
         return null;
      }

      if (isBlockItem(stack)) {
         int var8 = totalCount > 0 ? totalCount : stack.method_7947();
         return "x" + var8;
      }

      if (stack.method_7963()) {
         int var7 = stack.method_7936();
         int var4 = stack.method_7919();
         int var5 = Math.max(0, var7 - var4);
         float var6 = Math.max(0.0F, Math.min(1.0F, (float)var5 / var7));

         return switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$client$HUD$DuraMode[duraMode.ordinal()]) {
            case 1 -> (int)(var6 * 100.0F) + "%";
            case 2 -> var5 + "/" + var7;
            case 3 -> String.valueOf(var5);
            default -> null;
         };
      } else if (totalCount <= 1 && stack.method_7947() <= 1 && stack.method_7914() <= 1) {
         byte[] var10000 = new byte[4];
         var10000[0] = 0;
         var10000[1] = (byte)(-86);
         var10000[2] = (byte)(44);
         var10000[3] = (byte)(-5);
         return $$v_s(var10000, 3343710999863873556L);
      } else {
         int var3 = totalCount > 0 ? totalCount : stack.method_7947();
         return "x" + var3;
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
