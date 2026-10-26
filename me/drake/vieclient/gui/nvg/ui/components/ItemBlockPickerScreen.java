package me.drake.vieclient.gui.nvg.ui.components;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.gui.nvg.adapter.setting.BlocksSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.ColorPickerProxy;
import me.drake.vieclient.gui.nvg.adapter.setting.ItemSetting;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.RainbowUtil;
import net.minecraft.class_11909;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_7923;

public class ItemBlockPickerScreen extends VieOSScreen {
   private static final float CELL = 22.0F;
   private static final float COLS = 10.0F;
   private static final float SEARCH_W_S = 200.0F;
   private static final float SEARCH_W_M = 150.0F;
   private static final float BADGE_W = 96.0F;
   private static final float THEME_W = 82.0F;
   private static final float CHIP_W = 84.0F;
   private static final float CHIP_H = 28.0F;
   private static final float GAP = 10.0F;
   private static final float BAR_GAP = 16.0F;
   private static final float INSET = 18.0F;
   private static final float MIN_SEARCH_W = 60.0F;
   private static final float CHIP_GAP = 8.0F;
   private static final int[] DROPDOWN_DOTS;
   private final ItemSetting itemSetting;
   private final BlocksSetting blocksSetting;
   private final boolean multiSelect;
   private final BiConsumer onBlockColorChange;
   private final Map blockColors = new HashMap();
   private final Supplier themeGetter;
   private final Consumer themeSetter;
   private final Consumer onBlocksChanged;
   private final String titleText;
   private ColorPicker colorPicker = null;
   private class_2248 editingBlock = null;
   private ColorPickerProxy editingSetting = null;
   private final List allEntries = new ArrayList();
   private List filtered = new ArrayList();
   private final Set selectedSet = new HashSet();
   private final Map selectionFade = new HashMap();
   private String searchQuery = "";
   private float scrollOffset = 0.0F;
   private float targetScroll = 0.0F;
   private long lastScrollMs = 0L;
   private boolean showOnlySelected = false;
   private boolean themeDropdownOpen = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ItemBlockPickerScreen(ItemSetting is, BlocksSetting bs, BiConsumer onColor, Supplier themeGetter, Consumer themeSetter, Consumer onBlocksChanged) {
      super(class_2561.method_43473());
      this.itemSetting = is;
      this.blocksSetting = bs;
      this.multiSelect = bs != null;
      this.onBlockColorChange = onColor;
      this.themeGetter = themeGetterx;
      this.themeSetter = themeSetterx;
      this.onBlocksChanged = onBlocksChangedx;
      this.titleText = is != null ? "Select Item: " + is.getName() : "Select Blocks: " + bs.getName();
      if (this.multiSelect) {
         HashSet var7 = new HashSet();
         class_7923.field_41175.forEach(b -> {
            if (b != class_2246.field_10124 && b != class_2246.field_10543 && b != class_2246.field_10243) {
               class_1792 var3 = b.method_8389();
               if (var3 == class_1802.field_8162) {
                  var3 = resolveBlockItemFallback(b);
               }

               if (var3 != class_1802.field_8162 && var7.add(var3)) {
                  this.allEntries.add(var3);
               }
            }
         });

         for (class_2248 var9 : bs.getBlocks()) {
            class_1792 var10 = var9.method_8389();
            if (var10 == class_1802.field_8162) {
               var10 = resolveBlockItemFallback(var9);
            }

            if (var10 != class_1802.field_8162) {
               this.selectedSet.add(var10);
            }
         }
      } else {
         HashSet var11 = new HashSet();
         class_7923.field_41178.forEach(item -> {
            if (item != class_1802.field_8162 && var11.add(item)) {
               this.allEntries.add(item);
            }
         });
      }

      this.filtered = new ArrayList(this.allEntries);
      this.openScreen();
   }

   public static ItemBlockPickerScreen forItem(ItemSetting s) {
      return new ItemBlockPickerScreen(s, null, null, null, null, null);
   }

   public static ItemBlockPickerScreen forBlocks(BlocksSetting s) {
      return new ItemBlockPickerScreen(null, s, null, null, null, null);
   }

   public static ItemBlockPickerScreen forBlocks(BlocksSetting s, BiConsumer onBlockColorChange) {
      return new ItemBlockPickerScreen(null, s, onBlockColorChangex, null, null, null);
   }

   public static ItemBlockPickerScreen forBlocks(BlocksSetting s, BiConsumer onBlockColorChange, Supplier themeGetter, Consumer themeSetter) {
      return new ItemBlockPickerScreen(null, s, onBlockColorChangex, themeGetterx, themeSetterx, null);
   }

   public static ItemBlockPickerScreen forBlocks(
      BlocksSetting s, BiConsumer onBlockColorChange, Supplier themeGetter, Consumer themeSetter, Consumer onBlocksChanged
   ) {
      return new ItemBlockPickerScreen(null, s, onBlockColorChangex, themeGetterx, themeSetterx, onBlocksChangedx);
   }

   @Override
   protected float contentW(int sw) {
      boolean var2 = this.themeGetter != null;
      float var3 = var2 ? 390.0F : 348.0F;
      int var4 = var2 ? 12 : 10;
      float var5 = var4 * 22.0F + 36.0F;
      float var6 = Math.max(var3, var5);
      return Math.min(sw - (40), Math.max(var6, 320.0F));
   }

   @Override
   protected float contentH(int sw, int sh) {
      return Math.min(sh - (32), 420);
   }

   @Override
   protected float contentListH() {
      float var1 = super.contentListH();
      return Math.max(22.0F, (int)(var1 / 22.0F) * 22.0F);
   }

   @Override
   protected String title() {
      String var1;
      if (this.showOnlySelected) {
         byte[] var10001 = new byte[12];
         var10001[0] = (byte)(58);
         var10001[1] = (byte)(-70);
         var10001[2] = (byte)(-34);
         var10001[3] = (byte)(105);
         var10001[4] = (byte)(-30);
         var10001[5] = (byte)(-50);
         var10001[6] = (byte)(-46);
         var10001[7] = (byte)(-110);
         var10001[8] = (byte)(17);
         var10001[9] = (byte)(-55);
         var10001[10] = (byte)(-127);
         var10001[11] = (byte)(-110);
         var1 = $$v_s(var10001, -3206228153622867905L);
      } else {
         var1 = "";
      }

      return this.titleText + var1;
   }

   @Override
   protected String footerText() {
      return null;
   }

   @Override
   protected void tickAnimation(float delta) {
      this.scrollOffset = AnimationManager.isOff() ? this.targetScroll : AnimationManager.damp(this.scrollOffset, this.targetScroll, 18.0F, delta);
      if (!this.selectionFade.isEmpty()) {
         boolean var2 = AnimationManager.isOff();
         float var3 = var2 ? 1000.0F : 14.0F * AnimationManager.speedMul();
         Iterator var4 = this.selectionFade.entrySet().iterator();

         while (var4.hasNext()) {
            Entry var5 = (Entry)var4.next();
            class_1792 var6 = (class_1792)var5.getKey();
            float var7 = this.selectedSet.contains(var6) ? 1.0F : 0.0F;
            if (var2) {
               var5.setValue(var7);
               if (var7 == 0.0F) {
                  var4.remove();
               }
            } else {
               float var8 = AnimationManager.damp((Float)var5.getValue(), var7, var3, delta);
               if (var7 == 0.0F && var8 < 0.005F) {
                  var4.remove();
               } else {
                  var5.setValue(var8);
               }
            }
         }
      }

      if (this.colorPicker != null) {
         this.colorPicker.update(0.016666668F);
         if (!this.colorPicker.isAnimating()) {
            this.commitColorPicker();
            this.colorPicker = null;
            this.editingBlock = null;
            this.editingSetting = null;
         }
      }
   }

   @Override
   protected void drawContextContent(class_332 ctx, int mouseX, int mouseY, float delta) {
   }

   @Override
   protected void renderContent(int mx, int my, float alpha) {
      int var4 = this.itemsPerRow();
      boolean var5 = this.themeGetter != null;
      float var6 = this.actionY();
      float var7 = this.actionH();
      float var8 = this.bodyX();
      float var9 = this.searchW();
      NanoVGRenderer.drawRoundedRect(var8, var6, var9, var7, 6.0F, SoftPalette.white(8.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(var8, var6, var9, var7, 6.0F, 1.0F, SoftPalette.white(18.0F * alpha));
      boolean var10 = System.currentTimeMillis() / 530L % 2L == 0L;
      SoftPalette.label(
         this.searchQuery.isEmpty()
            ? "Search..."
            : this.searchQuery + (var10 ? "|" : ""),
         var8 + 12.0F,
         var6 + (var7 - 9.0F) / 2.0F,
         9.0F,
         this.searchQuery.isEmpty() ? SoftPalette.textDim(200.0F * alpha) : SoftPalette.text(255.0F * alpha)
      );
      float var11 = this.px + this.pw - 18.0F;
      if (var5) {
         float var12 = var11 - 82.0F;
         String var13 = (String)this.themeGetter.get();
         if (var13 == null || var13.isEmpty()) {
            var13 = "Warm";
         }

         boolean var14 = "Warm".equalsIgnoreCase(var13);
         boolean var15 = "Cold".equalsIgnoreCase(var13);
         boolean var16 = "Rainbow".equalsIgnoreCase(var13);
         int var17 = var14 ? -38070 : (var15 ? -16718337 : (var16 ? RainbowUtil.rainbow(1.5, 0L) : -5745161));
         boolean var18 = hit(mx, my, var12, var6, 82.0F, var7);
         NanoVGRenderer.drawRoundedRect(var12, var6, 82.0F, var7, 6.0F, SoftPalette.white(var18 ? 22.0F * alpha : 10.0F * alpha));
         NanoVGRenderer.drawRoundedRectOutline(var12, var6, 82.0F, var7, 6.0F, 1.0F, SoftPalette.white(var18 ? 40.0F * alpha : 18.0F * alpha));
         NanoVGRenderer.drawCircle(var12 + 10.0F, var6 + var7 / 2.0F, 3.0F, var17);
         SoftPalette.label(var13, var12 + 19.0F, var6 + (var7 - 9.0F) / 2.0F, 9.0F, SoftPalette.textInt(255.0F * alpha));
         var11 = var12 - 10.0F;
      }

      String var38 = this.badgeText();
      float var39 = var11 - 96.0F;
      boolean var40 = hit(mx, my, var39, var6, 96.0F, var7);
      if (this.showOnlySelected) {
         NanoVGRenderer.drawRoundedRectGradient(var39, var6, 96.0F, var7, 6.0F, SoftPalette.mintInt(220.0F * alpha), SoftPalette.mintDeepInt(220.0F * alpha));
         float var41 = SoftPalette.widthBold(var38, 9.0F);
         SoftPalette.labelBold(var38, var39 + (96.0F - var41) / 2.0F, var6 + (var7 - 9.0F) / 2.0F, 9.0F, SoftPalette.whiteInt(255.0F * alpha));
      } else {
         NanoVGRenderer.drawRoundedRect(var39, var6, 96.0F, var7, 6.0F, SoftPalette.white(var40 ? 22.0F * alpha : 10.0F * alpha));
         NanoVGRenderer.drawRoundedRectOutline(var39, var6, 96.0F, var7, 6.0F, 1.0F, SoftPalette.white(var40 ? 40.0F * alpha : 18.0F * alpha));
         float var42 = SoftPalette.widthBold(var38, 9.0F);
         SoftPalette.labelBold(var38, var39 + (96.0F - var42) / 2.0F, var6 + (var7 - 9.0F) / 2.0F, 9.0F, SoftPalette.textInt(255.0F * alpha));
      }

      float var43 = this.bodyX();
      float var44 = this.contentY();
      float var45 = this.contentListH();
      float var46 = var4 * 22.0F;
      NanoVGRenderer.save();
      NanoVGRenderer.scissor(var43, var44, var46, var45);

      for (int var19 = 0; var19 < this.filtered.size(); var19++) {
         int var20 = var19 / var4;
         int var21 = var19 % var4;
         float var22 = var43 + var21 * 22.0F;
         float var23 = var44 + var20 * 22.0F - this.scrollOffset;
         if (!(var23 + 22.0F < var44) && !(var23 > var44 + var45)) {
            class_1792 var24 = (class_1792)this.filtered.get(var19);
            boolean var25 = mx >= var22 && mx <= var22 + 22.0F && my >= var23 && my <= var23 + 22.0F;
            if (var25) {
               NanoVGRenderer.drawRoundedRect(var22, var23, 22.0F, 22.0F, 4.0F, SoftPalette.whiteInt(24.0F * alpha));
            }

            NVGImage.drawMCItem(new class_1799(var24), var22 + 3.0F, var23 + 3.0F, 16.0F, alpha);
            float var26 = this.selectionFade.getOrDefault(var24, this.isSelected(var24) ? 1.0F : 0.0F);
            if (var26 > 0.01F) {
               int var27 = (int)(90.0F * alpha * var26);
               int var28 = (int)(255.0F * alpha * var26);
               NanoVGRenderer.drawRoundedRect(var22, var23, 22.0F, 22.0F, 4.0F, SoftPalette.mintInt(var27));
               NanoVGRenderer.drawRoundedRectOutline(var22, var23, 22.0F, 22.0F, 4.0F, 1.5F, SoftPalette.mintInt(var28));
               if (this.multiSelect) {
                  class_2248 var29 = class_2248.method_9503(var24);
                  int var30 = this.blockColors.getOrDefault(var29, -1);
                  NanoVGRenderer.drawRoundedRect(
                     var22 + 22.0F - 6.0F, var23 + 22.0F - 6.0F, 4.0F, 4.0F, 1.0F, SoftPalette.clamp(var28) << 24 | var30 & 16777215
                  );
               }
            }
         }
      }

      NanoVGRenderer.resetScissor();
      NanoVGRenderer.restore();
      if (this.filtered.isEmpty()) {
         String var47 = this.showOnlySelected
            ? (
               this.searchQuery.isEmpty()
                  ? "No blocks picked yet — click badge to browse all."
                  : "No picked blocks match \"" + this.searchQuery + "\"."
            )
            : "No blocks found.";
         float var49 = SoftPalette.width(var47, 9.5F);
         SoftPalette.label(var47, this.px + (this.pw - var49) / 2.0F, var44 + var45 / 2.0F - 5.0F, 9.5F, SoftPalette.textDimInt(200.0F * alpha));
      }

      int var48 = (this.filtered.size() + var4 - 1) / var4;
      int var50 = (int)(var45 / 22.0F);
      if (var48 > var50) {
         long var51 = System.currentTimeMillis();
         boolean var54 = Math.abs(this.targetScroll - this.scrollOffset) > 0.5F || var51 - this.lastScrollMs < 700L;
         float var56 = AnimationManager.isOff() ? 1.0F : (var54 ? 1.0F : Math.max(0.0F, 1.0F - (float)(var51 - this.lastScrollMs - 700L) / 400.0F));
         if (var56 > 0.01F) {
            float var58 = var43 + var4 * 22.0F + 2.0F;
            float var60 = Math.max(20.0F, var45 * var50 / var48);
            float var62 = Math.max(1.0F, var48 * 22.0F - var45);
            float var64 = var44 + (var45 - var60) * (this.scrollOffset / var62);
            NanoVGRenderer.drawRoundedRect(var58, var44, 3.0F, var45, 1.5F, SoftPalette.rgbaInt(32, 32, 40, 255.0F * alpha * var56));
            NanoVGRenderer.drawRoundedRect(var58, var64, 3.0F, var60, 1.5F, SoftPalette.mintInt(255.0F * var56));
         }
      }

      float var52 = this.footerY() + 3.0F;
      float var53 = this.px + this.pw - 18.0F - 84.0F;
      float var55 = var53 - 8.0F - 84.0F;
      float var57 = this.px + 18.0F;
      drawDangerChip(
         var57, var52, 84.0F, 28.0F, "Reset", hit(mx, my, var57, var52, 84.0F, 28.0F), alpha
      );
      drawNeutralChip(
         var55, var52, 84.0F, 28.0F, "Cancel", hit(mx, my, var55, var52, 84.0F, 28.0F), alpha
      );
      drawAccentChip(var53, var52, 84.0F, 28.0F, "Save", alpha);
      if (this.themeGetter != null && this.themeDropdownOpen) {
         float var59 = this.px + this.pw - 18.0F - 82.0F;
         String var61 = (String)this.themeGetter.get();
         String[] var63 = new String[]{
            "Warm",
            "Cold",
            "Rainbow",
            "Custom"
         };
         float var65 = var59;
         float var66 = var6 + var7 + 3.0F;
         float var67 = 82.0F;
         float var31 = var63.length * 22.0F + 4.0F;
         float var32 = 6.0F;
         SoftPalette.softShadow(var65, var66, var67, var31, var32, 10.0F, 4.0F, 60.0F * alpha);
         NanoVGRenderer.drawSquircleGradient(
            var65,
            var66,
            var67,
            var31,
            var32,
            SoftPalette.rgbaInt(SoftPalette.BODY_TOP, 245.0F * alpha),
            SoftPalette.rgbaInt(SoftPalette.BODY_BOT, 245.0F * alpha)
         );
         NanoVGRenderer.drawSquircleOutline(var65, var66, var67, var31, var32, 1.0F, SoftPalette.whiteInt(40.0F * alpha));

         for (int var33 = 0; var33 < var63.length; var33++) {
            float var34 = var66 + 2.0F + var33 * 22.0F;
            boolean var35 = hit(mx, my, var65, var34, var67, 22.0F);
            boolean var36 = var63[var33].equalsIgnoreCase(var61);
            if (var35) {
               NanoVGRenderer.drawRoundedRect(var65 + 2.0F, var34, var67 - 4.0F, 20.0F, 4.0F, SoftPalette.whiteInt(14.0F * alpha));
            }

            int var37 = "Rainbow".equalsIgnoreCase(var63[var33])
               ? RainbowUtil.rainbow(1.5, 0L)
               : DROPDOWN_DOTS[var33];
            NanoVGRenderer.drawCircle(var65 + 10.0F, var34 + 11.0F, 3.0F, var37);
            SoftPalette.label(
               var63[var33], var65 + 19.0F, var34 + 6.5F, 9.0F, var36 ? SoftPalette.whiteInt(255.0F * alpha) : SoftPalette.textInt(220.0F * alpha)
            );
         }
      }
   }

   @Override
   protected void renderExtras(int mx, int my, float alpha) {
      super.renderExtras(mx, my, alpha);
      if (this.colorPicker != null && this.colorPicker.isAnimating()) {
         this.colorPicker.render(mx, my, 1.0F);
      } else {
         float var4 = this.bodyX();
         float var5 = this.contentY();
         float var6 = this.contentListH();
         int var7 = this.itemsPerRow();
         if (mx >= var4 && mx <= var4 + var7 * 22.0F && my >= var5 && my <= var5 + var6) {
            int var8 = (int)((mx - var4) / 22.0F);
            int var9 = (int)((my - var5 + this.scrollOffset) / 22.0F);
            int var10 = var9 * var7 + var8;
            if (var10 >= 0 && var10 < this.filtered.size() && var8 >= 0 && var8 < var7) {
               class_1792 var11 = (class_1792)this.filtered.get(var10);
               String var12 = var11.method_63680().getString();
               float var13 = SoftPalette.width(var12, 9.0F);
               float var14 = Math.min(this.px + this.pw - var13 - 14.0F, Math.max(this.px + 10.0F, mx + 10.0F));
               float var15 = Math.max(this.py + 6.0F, my - 18.0F);
               NanoVGRenderer.drawRoundedRect(var14 - 4.0F, var15 - 2.0F, var13 + 8.0F, 15.0F, 4.0F, SoftPalette.rgbaInt(SoftPalette.BODY_BOT, 230.0F * alpha));
               NanoVGRenderer.drawRoundedRectOutline(var14 - 4.0F, var15 - 2.0F, var13 + 8.0F, 15.0F, 4.0F, 1.0F, SoftPalette.whiteInt(35.0F * alpha));
               SoftPalette.label(var12, var14, var15 + 2.0F, 9.0F, SoftPalette.textInt(255.0F * alpha));
            }
         }
      }
   }

   private int itemsPerRow() {
      int var1 = this.themeGetter != null ? 12 : 10;
      return Math.max(1, Math.min(var1, (int)(this.bodyW() / 22.0F)));
   }

   private float searchW() {
      boolean var1 = this.themeGetter != null;
      float var2 = var1 ? 150.0F : 200.0F;
      float var3 = var1 ? 188.0F : 96.0F;
      float var4 = this.bodyW() - 16.0F - var3;
      return Math.max(60.0F, Math.min(var2, var4));
   }

   private boolean isSelected(class_1792 item) {
      return this.multiSelect ? this.selectedSet.contains(item) : this.itemSetting != null && this.itemSetting.getItem() == item;
   }

   private String badgeText() {
      int var1 = this.multiSelect
         ? this.selectedSet.size()
         : (this.itemSetting != null && this.itemSetting.getItem() != null && this.itemSetting.getItem() != class_1802.field_8162 ? 1 : 0);
      String var2;
      if (this.showOnlySelected) {
         byte[] var10000 = new byte[8];
         var10000[0] = (byte)(74);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(127);
         var10000[3] = (byte)(-75);
         var10000[4] = (byte)(-89);
         var10000[5] = (byte)(-118);
         var10000[6] = (byte)(-72);
         var10000[7] = (byte)(-37);
         var2 = $$v_s(var10000, -3206228153622867905L);
      } else {
         byte[] var3 = new byte[10];
         var3[0] = (byte)(73);
         var3[1] = (byte)(-1);
         var3[2] = (byte)(112);
         var3[3] = (byte)(-69);
         var3[4] = (byte)(-95);
         var3[5] = (byte)(-102);
         var3[6] = (byte)(-25);
         var3[7] = (byte)(-97);
         var3[8] = (byte)(72);
         var3[9] = (byte)(-126);
         var2 = $$v_s(var3, -3206228153622867905L);
      }

      return var2 + var1;
   }

   private void refreshFiltered() {
      this.refreshFiltered(true);
   }

   private void refreshFiltered(boolean resetScroll) {
      List var2;
      if (this.showOnlySelected) {
         if (this.multiSelect) {
            var2 = this.allEntries.stream().filter(this.selectedSet::contains).collect(Collectors.toList());
         } else {
            class_1792 var3 = this.itemSetting != null ? this.itemSetting.getItem() : null;
            var2 = var3 != null && var3 != class_1802.field_8162 ? List.of(var3) : Collections.emptyList();
         }
      } else {
         var2 = this.allEntries;
      }

      if (this.searchQuery.isEmpty()) {
         this.filtered = new ArrayList(var2);
      } else {
         String var4 = this.searchQuery.toLowerCase();
         this.filtered = var2.stream()
            .filter(
               item -> {
                  class_2248 var3x = class_2248.method_9503(item);
                  String var4x = this.multiSelect && var3x != class_2246.field_10124
                     ? class_7923.field_41175.method_10221(var3x).method_12832()
                     : class_7923.field_41178.method_10221(item).method_12832();
                  String var5 = item.method_63680().getString().toLowerCase();
                  return var4x.contains(var4) || var5.contains(var4);
               }
            )
            .collect(Collectors.toList());
      }

      if (resetScroll) {
         this.scrollOffset = 0.0F;
         this.targetScroll = 0.0F;
      } else {
         this.clampScroll();
      }
   }

   private void clampScroll() {
      int var1 = this.itemsPerRow();
      int var2 = (this.filtered.size() + var1 - 1) / var1;
      float var3 = Math.max(0.0F, var2 * 22.0F - this.contentListH());
      this.targetScroll = Math.max(0.0F, Math.min(var3, this.targetScroll));
      this.scrollOffset = Math.max(0.0F, Math.min(var3, this.scrollOffset));
   }

   @Override
   protected boolean mouseClickedContent(double mx, double my, int btn) {
      if (this.colorPicker == null || !this.colorPicker.isAnimating()) {
         float var6 = this.footerY() + 3.0F;
         float var7 = this.px + this.pw - 18.0F - 84.0F;
         float var8 = var7 - 8.0F - 84.0F;
         float var9 = this.px + 18.0F;
         float var10 = this.actionY();
         float var11 = this.actionH();
         float var12 = this.bodyX();
         float var13 = this.searchW();
         if (hit(mx, my, var9, var6, 84.0F, 28.0F)) {
            if (this.multiSelect) {
               this.selectedSet.clear();

               for (class_2248 var30 : this.blocksSetting.getDefaultValue()) {
                  class_1792 var32 = var30.method_8389();
                  if (var32 == class_1802.field_8162) {
                     var32 = resolveBlockItemFallback(var30);
                  }

                  if (var32 != class_1802.field_8162) {
                     this.selectedSet.add(var32);
                  }
               }

               this.syncBlocksSetting();
            } else if (this.itemSetting != null) {
               this.itemSetting.resetValue();
            }

            this.refreshFiltered(true);
            return true;
         } else {
            if (hit(mx, my, var8, var6, 84.0F, 28.0F)) {
               this.returnGUI();
               return true;
            }

            if (hit(mx, my, var7, var6, 84.0F, 28.0F)) {
               this.save();
               return true;
            }

            if (this.themeGetter != null && this.themeDropdownOpen) {
               float var14 = this.px + this.pw - 18.0F - 82.0F;
               float var15 = var10 + var11 + 3.0F;
               if (hit(mx, my, var14, var15, 82.0F, 92.0F)) {
                  float var31 = (float)my - var15 - 2.0F;
                  int var33 = (int)(var31 / 22.0F);
                  if (var33 == 0 && this.themeSetter != null) {
                     this.themeSetter.accept("Warm");
                  } else if (var33 == 1 && this.themeSetter != null) {
                     this.themeSetter.accept("Cold");
                  } else if (var33 == 2 && this.themeSetter != null) {
                     this.themeSetter.accept("Rainbow");
                  } else if (var33 == 3 && this.themeSetter != null) {
                     this.themeSetter.accept("Custom");
                  }

                  this.themeDropdownOpen = false;
                  return true;
               }

               this.themeDropdownOpen = false;
            }

            if (this.themeGetter != null) {
               float var26 = this.px + this.pw - 18.0F - 82.0F;
               if (hit(mx, my, var26, var10, 82.0F, var11)) {
                  this.themeDropdownOpen = !this.themeDropdownOpen;
                  if (this.colorPicker != null) {
                     this.colorPicker.hide();
                  }

                  return true;
               }
            }

            float var27 = this.px + this.pw - 18.0F - 96.0F;
            if (this.themeGetter != null) {
               var27 = this.px + this.pw - 18.0F - 82.0F - 10.0F - 96.0F;
            }

            if (hit(mx, my, var27, var10, 96.0F, var11)) {
               this.showOnlySelected = !this.showOnlySelected;
               this.refreshFiltered(true);
               return true;
            }

            if (hit(mx, my, var12, var10, var13, var11)) {
               return true;
            }

            int var29 = this.itemsPerRow();
            float var16 = this.bodyX();
            float var17 = this.contentY();
            float var18 = this.contentListH();

            for (int var19 = 0; var19 < this.filtered.size(); var19++) {
               int var20 = var19 / var29;
               int var21 = var19 % var29;
               float var22 = var16 + var21 * 22.0F;
               float var23 = var17 + var20 * 22.0F - this.scrollOffset;
               if (!(var23 + 22.0F < var17) && !(var23 > var17 + var18) && hit(mx, my, var22, var23, 22.0F, 22.0F)) {
                  class_1792 var24 = (class_1792)this.filtered.get(var19);
                  if (this.multiSelect && btn == 1 && this.onBlockColorChange != null) {
                     class_2248 var25 = class_2248.method_9503(var24);
                     if (var25 != class_2246.field_10124) {
                        this.openColorPicker(var25, var22, var23 + 22.0F);
                     }

                     return true;
                  }

                  if (this.multiSelect) {
                     if (this.selectedSet.contains(var24)) {
                        this.selectedSet.remove(var24);
                        if (!this.selectionFade.containsKey(var24)) {
                           this.selectionFade.put(var24, 1.0F);
                        }
                     } else {
                        this.selectedSet.add(var24);
                        this.selectionFade.put(var24, 0.0F);
                     }

                     this.syncBlocksSetting();
                  } else if (this.itemSetting != null) {
                     this.itemSetting.setItem(var24);
                     if (this.showOnlySelected) {
                        this.refreshFiltered(false);
                     }
                  }

                  return true;
               }
            }

            return false;
         }
      } else {
         if (this.colorPicker.mouseClicked(mx, my, btn)) {
            return true;
         }

         this.colorPicker.hide();
         return true;
      }
   }

   public boolean method_25401(double mx, double my, double h, double v) {
      if (this.colorPicker != null && this.colorPicker.isAnimating()) {
         return true;
      }

      int var9 = this.itemsPerRow();
      int var10 = (this.filtered.size() + var9 - 1) / var9;
      float var11 = Math.max(0.0F, var10 * 22.0F - this.contentListH());
      this.targetScroll = Math.max(0.0F, Math.min(var11, this.targetScroll - (float)v * 22.0F));
      this.lastScrollMs = System.currentTimeMillis();
      return true;
   }

   public boolean method_25403(class_11909 click, double dx, double dy) {
      return this.colorPicker != null && this.colorPicker.isAnimating()
         ? this.colorPicker.mouseDragged(click.comp_4798(), click.comp_4799(), click.method_74245())
         : false;
   }

   public boolean method_25406(class_11909 click) {
      if (this.colorPicker != null && this.colorPicker.isAnimating()) {
         this.colorPicker.mouseReleased(click.comp_4798(), click.comp_4799(), click.method_74245());
         this.commitColorPicker();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyPressedContent(int kc, int sc, int mods) {
      if (kc == (256) || kc == (257)) {
         this.save();
         return true;
      } else if (kc == (259) && !this.searchQuery.isEmpty()) {
         this.searchQuery = this.searchQuery.substring(0, this.searchQuery.length() - 1);
         this.refreshFiltered();
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean charTypedContent(char chr, int mods) {
      if (Character.isISOControl(chr)) {
         return false;
      }

      this.searchQuery = this.searchQuery + chr;
      this.refreshFiltered();
      return true;
   }

   @Override
   protected boolean closesOnOutsideClick() {
      return false;
   }

   @Override
   protected boolean hasDrawContextContent() {
      return false;
   }

   private void syncBlocksSetting() {
      if (this.multiSelect && this.blocksSetting != null) {
         HashSet var1 = new HashSet();

         for (class_1792 var3 : this.selectedSet) {
            class_2248 var4 = class_2248.method_9503(var3);
            if (var4 != class_2246.field_10124) {
               var1.add(var4);
            }

            for (class_2248 var6 : class_7923.field_41175) {
               if (var6 != class_2246.field_10124 && var6.method_8389() == var3) {
                  var1.add(var6);
               }
            }

            if (var3 == class_1802.field_8705) {
               var1.add(class_2246.field_10382);
               var1.add(class_2246.field_27097);
            }

            if (var3 == class_1802.field_8187) {
               var1.add(class_2246.field_10164);
               var1.add(class_2246.field_27098);
            }

            if (var3 == class_1802.field_8281) {
               var1.add(class_2246.field_10316);
            }

            if (var3 == class_1802.field_8884) {
               var1.add(class_2246.field_10036);
               var1.add(class_2246.field_22089);
            }
         }

         this.blocksSetting.setBlocks(var1);
         if (this.onBlocksChanged != null) {
            this.onBlocksChanged.accept(var1);
         }
      }
   }

   public static class_1792 resolveBlockItemFallback(class_2248 b) {
      class_2960 var1 = class_7923.field_41175.method_10221(b);
      String var2 = var1.method_12832();
      if (var2.contains("water")) {
         return class_1802.field_8705;
      } else if (var2.contains("lava")) {
         return class_1802.field_8187;
      } else if (var2.contains("portal")) {
         return class_1802.field_8281;
      } else if (var2.contains("fire")) {
         return class_1802.field_8884;
      } else if (var2.contains("piston")) {
         return class_1802.field_8249;
      } else if (var2.contains("melon")) {
         return class_1802.field_46250;
      } else if (var2.contains("pumpkin")) {
         return class_1802.field_46249;
      } else if (var2.contains("kelp")) {
         return class_1802.field_17532;
      } else if (var2.contains("bamboo")) {
         return class_1802.field_8648;
      } else if (var2.contains("vines")) {
         return class_1802.field_28659;
      } else {
         return var2.contains("potted") ? class_1802.field_8074 : class_1802.field_8162;
      }
   }

   private void save() {
      this.syncBlocksSetting();
      this.returnGUI();
   }

   private void returnGUI() {
      if (this.field_22787 != null) {
         this.field_22787.method_1507(VieClient.INSTANCE.GUI);
      }
   }

   @Override
   public void method_25419() {
      this.pageOpen = false;
      this.save();
   }

   private void openColorPicker(class_2248 b, float anchorX, float anchorY) {
      int var4 = this.blockColors.getOrDefault(b, -1);
      Color var5 = new Color(
         var4 >> (16) & (255),
         var4 >> (8) & (255),
         var4 & (255),
         var4 >> (24) & (255)
      );
      this.editingSetting = new ColorPickerProxy(var5);
      this.colorPicker = new ColorPicker(this.editingSetting, anchorX, anchorY);
      this.editingBlock = b;
      this.colorPicker.show(anchorX, anchorY, this.field_22789, this.field_22790);
   }

   private void commitColorPicker() {
      if (this.editingBlock != null && this.editingSetting != null && this.onBlockColorChange != null) {
         int var1 = this.editingSetting.argb();
         int var2 = var1 & (16777215);
         this.blockColors.put(this.editingBlock, var2);
         this.onBlockColorChange.accept(this.editingBlock, var2);
      }
   }

   public Map getBlockColors() {
      return this.blockColors;
   }

   public void method_25432() {
      super.method_25432();
      this.syncBlocksSetting();
   }

   static {
      int[] var10000 = new int[4];
      var10000[0] = -38070;
      var10000[1] = -16718337;
      var10000[2] = -52344;
      var10000[3] = -5745161;
      DROPDOWN_DOTS = var10000;
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
