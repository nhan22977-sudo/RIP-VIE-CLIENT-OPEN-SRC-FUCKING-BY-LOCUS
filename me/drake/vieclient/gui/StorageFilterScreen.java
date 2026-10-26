package me.drake.vieclient.gui;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.gui.nvg.ui.components.SoftPalette;
import me.drake.vieclient.gui.nvg.ui.components.VieOSScreen;
import me.drake.vieclient.modules.render.StorageESP;
import me.drake.vieclient.render.nvg.NVGIcons;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.RainbowUtil;
import net.minecraft.class_11909;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_332;

public class StorageFilterScreen extends VieOSScreen {
   private static final float COLS = 2.0F;
   private static final float CARD_W = 205.0F;
   private static final float CARD_H = 34.0F;
   private static final float GAP_X = 10.0F;
   private static final float GAP_Y = 5.0F;
   private static final float TOG_W = 26.0F;
   private static final float TOG_H = 14.0F;
   private static final float SWATCH_S = 18.0F;
   private static final float CHIP_W = 78.0F;
   private static final float CHIP_H = 24.0F;
   private static final float CHIP_GAP = 6.0F;
   private static final float SEARCH_W = 140.0F;
   private static final int[] DEFAULT_COLORS = new int[]{
      -33876, -23623, -2080517, -5745161, -8635667, -5793025, -4555871, -3885571, -298619, -3703297, -2575106, -45676
   };
   private static final int[] PRESET_COLORS = new int[]{-52429, -30720, -13312, -13369498, -16718337, -13399809, -5618433, -43606};
   private static final class_1792[] ITEMS = new class_1792[]{
      class_1802.field_8106,
      class_1802.field_16307,
      class_1802.field_8545,
      class_1802.field_8466,
      class_1802.field_8849,
      class_1802.field_8239,
      class_1802.field_8732,
      class_1802.field_16306,
      class_1802.field_16309,
      class_1802.field_8357,
      class_1802.field_8878,
      class_1802.field_8740
   };
   private static final String[] NAMES = new String[]{
      "Chest",
      "Barrel",
      "Shulker Box",
      "Ender Chest",
      "Spawner",
      "Hopper",
      "Furnace",
      "Blast Furnace",
      "Smoker",
      "Dispenser",
      "Dropper",
      "Brewing Stand"
   };
   private final StorageESP module;
   private final boolean[] on;
   private final int[] col;
   private final float[] togAnim;
   private float scroll;
   private float targetScroll;
   private int pickerRow;
   private float hue;
   private float sat;
   private float val;
   private boolean dragSv;
   private boolean dragHue;
   private String searchQuery;
   private boolean searchFocused;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public StorageFilterScreen(StorageESP module) {
      byte[] var10001 = new byte[14];
      var10001[0] = (byte)(113);
      var10001[1] = (byte)(-18);
      var10001[2] = (byte)(115);
      var10001[3] = (byte)(-84);
      var10001[4] = (byte)(100);
      var10001[5] = (byte)(55);
      var10001[6] = (byte)(-5);
      var10001[7] = (byte)(100);
      var10001[8] = (byte)(12);
      var10001[9] = (byte)(-53);
      var10001[10] = (byte)(-120);
      var10001[11] = (byte)(-126);
      var10001[12] = (byte)(56);
      var10001[13] = (byte)(-6);
      super(class_2561.method_43470($$v_s(var10001, 7826568666793889799L)));
      this.on = new boolean[12];
      this.col = new int[12];
      this.togAnim = new float[12];
      this.scroll = 0.0F;
      this.targetScroll = 0.0F;
      this.pickerRow = -1;
      this.hue = 0.0F;
      this.sat = 1.0F;
      this.val = 1.0F;
      this.dragSv = false;
      this.dragHue = false;
      this.searchQuery = "";
      this.searchFocused = false;
      this.module = modulex;
      StorageESP.StorageType[] var2 = StorageESP.StorageType.values();

      for (int var3 = 0; var3 < var2.length && var3 < (12); var3++) {
         this.on[var3] = modulex.isTypeEnabled(var2[var3]);
         this.col[var3] = modulex.getTypeColor(var2[var3]) | -16777216;
         this.togAnim[var3] = this.on[var3] ? 1.0F : 0.0F;
      }

      this.openScreen();
   }

   @Override
   protected float contentW(int sw) {
      return Math.min(sw - 32.0F, 456.0F);
   }

   @Override
   protected float contentH(int sw, int sh) {
      return Math.min(sh - 32.0F, 350.0F);
   }

   @Override
   protected String title() {
      byte[] var10000 = new byte[18];
      var10000[0] = (byte)(113);
      var10000[1] = (byte)(-18);
      var10000[2] = (byte)(115);
      var10000[3] = (byte)(-84);
      var10000[4] = (byte)(100);
      var10000[5] = (byte)(55);
      var10000[6] = (byte)(-5);
      var10000[7] = (byte)(100);
      var10000[8] = (byte)(15);
      var10000[9] = (byte)(-15);
      var10000[10] = (byte)(-76);
      var10000[11] = (byte)(-42);
      var10000[12] = (byte)(27);
      var10000[13] = (byte)(-31);
      var10000[14] = (byte)(-38);
      var10000[15] = (byte)(72);
      var10000[16] = (byte)(23);
      var10000[17] = (byte)(56);
      return $$v_s(var10000, 7826568666793889799L);
   }

   @Override
   protected String footerText() {
      byte[] var10000 = new byte[54];
      var10000[0] = (byte)(97);
      var10000[1] = (byte)(-10);
      var10000[2] = (byte)(117);
      var10000[3] = (byte)(-67);
      var10000[4] = (byte)(110);
      var10000[5] = (byte)(112);
      var10000[6] = (byte)(-19);
      var10000[7] = (byte)(51);
      var10000[8] = (byte)(43);
      var10000[9] = (byte)(-42);
      var10000[10] = (byte)(-121);
      var10000[11] = (byte)(-98);
      var10000[12] = (byte)(125);
      var10000[13] = (byte)(-4);
      var10000[14] = (byte)(-39);
      var10000[15] = (byte)(28);
      var10000[16] = (byte)(17);
      var10000[17] = (byte)(34);
      var10000[18] = (byte)(-83);
      var10000[19] = (byte)(-64);
      var10000[20] = (byte)(-46);
      var10000[21] = (byte)(-59);
      var10000[22] = (byte)(-18);
      var10000[23] = (byte)(119);
      var10000[24] = (byte)(-11);
      var10000[25] = (byte)(126);
      var10000[26] = (byte)(-5);
      var10000[27] = (byte)(52);
      var10000[28] = (byte)(-83);
      var10000[29] = (byte)(-8);
      var10000[30] = (byte)(36);
      var10000[31] = (byte)(123);
      var10000[32] = (byte)(-30);
      var10000[33] = (byte)(26);
      var10000[34] = (byte)(31);
      var10000[35] = (byte)(18);
      var10000[36] = (byte)(-116);
      var10000[37] = (byte)(-109);
      var10000[38] = (byte)(85);
      var10000[39] = (byte)(-124);
      var10000[40] = (byte)(-119);
      var10000[41] = (byte)(-93);
      var10000[42] = (byte)(54);
      var10000[43] = (byte)(114);
      var10000[44] = (byte)(29);
      var10000[45] = (byte)(92);
      var10000[46] = (byte)(57);
      var10000[47] = (byte)(-68);
      var10000[48] = (byte)(102);
      var10000[49] = (byte)(-123);
      var10000[50] = (byte)(75);
      var10000[51] = (byte)(-87);
      var10000[52] = (byte)(121);
      var10000[53] = (byte)(37);
      return $$v_s(var10000, 7826568666793889799L);
   }

   @Override
   protected void tickAnimation(float delta) {
      for (int var2 = 0; var2 < (12); var2++) {
         float var3 = this.on[var2] ? 1.0F : 0.0F;
         this.togAnim[var2] = AnimationManager.isOff() ? var3 : AnimationManager.damp(this.togAnim[var2], var3, 16.0F, delta);
      }

      List var6 = this.getVisibleIndices();
      int var7 = (var6.size() + 1) / (2);
      float var4 = var7 > 0 ? var7 * 34.0F + (var7 - 1) * 5.0F : 0.0F;
      float var5 = Math.max(0.0F, var4 - this.contentListH());
      this.targetScroll = Math.max(0.0F, Math.min(var5, this.targetScroll));
      this.scroll = AnimationManager.isOff() ? this.targetScroll : AnimationManager.damp(this.scroll, this.targetScroll, 20.0F, delta);
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.targetScroll -= (float)verticalAmount * 24.0F;
      return true;
   }

   @Override
   protected void renderHeaderExtras(int mx, int my, float alpha) {
      int var4 = 0;

      for (boolean var8 : this.on) {
         if (var8) {
            var4++;
         }
      }

      String var10 = var4 + "/12 ENABLED";
      float var11 = SoftPalette.widthBold(var10, 8.5F);
      float var12 = var11 + 16.0F;
      float var13 = this.px + this.pw - 18.0F - var12;
      float var9 = this.py + 9.0F;
      NanoVGRenderer.drawRoundedRectGradient(var13, var9, var12, 18.0F, 5.0F, SoftPalette.mintInt(40.0F * alpha), SoftPalette.mintDeepInt(40.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(var13, var9, var12, 18.0F, 5.0F, 1.0F, SoftPalette.mintInt(140.0F * alpha));
      SoftPalette.labelBold(var10, var13 + (var12 - var11) / 2.0F, var9 + 4.75F, 8.5F, SoftPalette.mintInt(255.0F * alpha));
   }

   @Override
   protected void drawContextContent(class_332 ctx, int mouseX, int mouseY, float delta) {
   }

   @Override
   protected void renderContent(int mx, int my, float alpha) {
      float var4 = this.actionY();
      float var5 = this.actionH();
      float var6 = this.bodyX();
      NanoVGRenderer.drawRoundedRect(var6, var4, 140.0F, var5, 6.0F, SoftPalette.whiteInt(8.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(
         var6, var4, 140.0F, var5, 6.0F, 1.0F, this.searchFocused ? SoftPalette.mintInt(180.0F * alpha) : SoftPalette.whiteInt(16.0F * alpha)
      );
      boolean var7 = System.currentTimeMillis() / 530L % 2L == 0L;
      if (this.searchQuery.isEmpty() && !this.searchFocused) {
         SoftPalette.label(
            "Search storage...",
            var6 + 10.0F,
            var4 + (var5 - 8.5F) / 2.0F,
            8.5F,
            SoftPalette.textDimInt(180.0F * alpha)
         );
      } else {
         SoftPalette.label(
            this.searchQuery + (this.searchFocused && var7 ? "|" : ""), var6 + 10.0F, var4 + (var5 - 8.5F) / 2.0F, 8.5F, SoftPalette.textInt(255.0F * alpha)
         );
      }

      float var8 = 5.0F;
      float var9 = this.px + this.pw - 18.0F;
      float var10 = 46.0F;
      float var11 = 48.0F;
      float var12 = 54.0F;
      float var13 = 48.0F;
      float var14 = 22.0F;
      float var15 = var4 + (var5 - var14) / 2.0F;
      var9 -= var10;
      drawNeutralChip(var9, var15, var10, var14, "RESET", hit(mx, my, var9, var15, var10, var14), alpha);
      var9 -= var11 + var8;
      drawNeutralChip(
         var9, var15, var11, var14, "INVERT", hit(mx, my, var9, var15, var11, var14), alpha
      );
      var9 -= var12 + var8;
      drawNeutralChip(
         var9, var15, var12, var14, "ALL OFF", hit(mx, my, var9, var15, var12, var14), alpha
      );
      var9 -= var13 + var8;
      drawAccentChip(var9, var15, var13, var14, "ALL ON", alpha);
      List var16 = this.getVisibleIndices();
      if (var16.isEmpty()) {
         String var17 = "No storage types match \"" + this.searchQuery + "\"";
         float var18 = SoftPalette.width(var17, 9.5F);
         SoftPalette.label(var17, this.px + (this.pw - var18) / 2.0F, this.contentY() + 30.0F, 9.5F, SoftPalette.textDim(180.0F * alpha));
      } else {
         NanoVGRenderer.save();
         NanoVGRenderer.scissor(this.bodyX(), this.contentY(), this.bodyW(), this.contentListH());

         for (int var25 = 0; var25 < var16.size(); var25++) {
            int var27 = (Integer)var16.get(var25);
            float var19 = this.cardY(var25);
            if (!(var19 + 34.0F < this.contentY()) && !(var19 > this.contentY() + this.contentListH())) {
               float var20 = this.rowEntrance(var25);
               if (var20 < 0.999F) {
                  NanoVGRenderer.globalAlpha(alpha * var20);
                  this.drawCard(var25, var27, mx, my, 1.0F);
                  NanoVGRenderer.globalAlpha(1.0F);
               } else {
                  this.drawCard(var25, var27, mx, my, alpha);
               }
            }
         }

         NanoVGRenderer.resetScissor();
         NanoVGRenderer.restore();
      }

      float var26 = this.footerY() + 5.0F;
      float var28 = this.px + this.pw - 18.0F - 78.0F;
      float var29 = var28 - 6.0F - 78.0F;
      drawNeutralChip(
         var29, var26, 78.0F, 24.0F, "CANCEL", hit(mx, my, var29, var26, 78.0F, 24.0F), alpha
      );
      drawAccentChip(var28, var26, 78.0F, 24.0F, "SAVE", alpha);
   }

   @Override
   protected void renderExtras(int mx, int my, float alpha) {
      super.renderExtras(mx, my, alpha);
      if (this.pickerRow >= 0) {
         this.drawPickerModal(mx, my, alpha);
      }
   }

   private void drawCard(int slot, int idx, int mx, int my, float alpha) {
      float var6 = this.cardX(slot);
      float var7 = this.cardY(slot);
      boolean var8 = this.on[idx];
      float var9 = this.togAnim[idx];
      boolean var10 = hit(mx, my, var6, var7, 205.0F, 34.0F);
      NanoVGRenderer.drawRoundedRect(var6, var7, 205.0F, 34.0F, 6.0F, var10 ? SoftPalette.mintInt(20.0F * alpha) : SoftPalette.clamp(55.0F * alpha) << 24);
      if (var8) {
         NanoVGRenderer.drawRoundedRectOutline(var6, var7, 205.0F, 34.0F, 6.0F, 1.0F, SoftPalette.mintInt(80.0F * alpha));
      } else {
         NanoVGRenderer.drawRoundedRectOutline(var6, var7, 205.0F, 34.0F, 6.0F, 1.0F, SoftPalette.whiteInt(14.0F * alpha));
      }

      float var11 = var6 + 5.0F;
      float var12 = var7 + 5.0F;
      float var13 = 24.0F;
      NanoVGRenderer.drawRoundedRect(var11, var12, var13, var13, 4.0F, SoftPalette.clamp(40.0F * alpha) << 24);
      NanoVGRenderer.drawRoundedRectOutline(var11, var12, var13, var13, 4.0F, 1.0F, SoftPalette.whiteInt(20.0F * alpha));
      NVGImage.drawMCItem(new class_1799(ITEMS[idx]), var11 + 4.0F, var12 + 4.0F, 16.0F, alpha);
      SoftPalette.labelBold(NAMES[idx], var6 + 34.0F, var7 + 6.5F, 8.5F, var8 ? SoftPalette.textInt(255.0F * alpha) : SoftPalette.textDimInt(180.0F * alpha));
      SoftPalette.label(
         var8
            ? "Enabled"
            : "Disabled",
         var6 + 34.0F,
         var7 + 18.5F,
         7.0F,
         var8 ? SoftPalette.mintInt(220.0F * alpha) : SoftPalette.textDimInt(140.0F * alpha)
      );
      float var14 = var6 + 205.0F - 54.0F;
      float var15 = var7 + 8.0F;
      boolean var16 = hit(mx, my, var14, var15, 18.0F, 18.0F);
      int var17 = this.module.getColorTheme() == StorageESP.ColorTheme.RAINBOW ? RainbowUtil.rainbow(1.2, 0.85, idx * 300L, 255) : this.col[idx];
      NanoVGRenderer.drawRoundedRect(var14, var15, 18.0F, 18.0F, 4.0F, var17);
      NanoVGRenderer.drawRoundedRectOutline(
         var14, var15, 18.0F, 18.0F, 4.0F, var16 ? 1.5F : 1.0F, var16 ? SoftPalette.mintInt(255.0F * alpha) : SoftPalette.whiteInt(80.0F * alpha)
      );
      float var18 = var6 + 205.0F - 30.0F;
      float var19 = var7 + 10.0F;
      SoftPalette.toggle(var18, var19, 26.0F, 14.0F, var9, alpha);
   }

   private void drawPickerModal(int mx, int my, float alpha) {
      float var4 = this.pickerPX();
      float var5 = this.pickerPY();
      float var6 = 232.0F;
      float var7 = 234.0F;
      NanoVGRenderer.drawRoundedRect(this.px + 4.0F, this.py + 4.0F, this.pw - 8.0F, this.ph - 8.0F, 4.0F, SoftPalette.clamp(150.0F * alpha) << 24);
      SoftPalette.softShadow(var4, var5, var6, var7, 6.0F, 14.0F, 4.0F, 90.0F * alpha);
      NanoVGRenderer.drawSquircleGradient(
         var4, var5, var6, var7, 6.0F, SoftPalette.rgbaInt(SoftPalette.BODY_TOP, 250.0F * alpha), SoftPalette.rgbaInt(SoftPalette.BODY_BOT, 250.0F * alpha)
      );
      NanoVGRenderer.drawSquircleOutline(var4, var5, var6, var7, 6.0F, 1.0F, SoftPalette.whiteInt(45.0F * alpha));
      SoftPalette.labelBold("Color  ·  " + NAMES[this.pickerRow], var4 + 14.0F, var5 + 12.0F, 9.5F, SoftPalette.textInt(255.0F * alpha));
      boolean var8 = hit(mx, my, var4 + var6 - 26.0F, var5 + 9.0F, 18.0F, 18.0F);
      if (var8) {
         NanoVGRenderer.drawRoundedRect(var4 + var6 - 26.0F, var5 + 9.0F, 18.0F, 18.0F, 4.0F, SoftPalette.whiteInt(18.0F * alpha));
      }

      NVGIcons.cross(
         var4 + var6 - 17.0F, var5 + 18.0F, 4.5F, 1.4F, var8 ? SoftPalette.clamp(255.0F * alpha) << 24 | 16737380 : SoftPalette.textDimInt(220.0F * alpha)
      );
      float var9 = var4 + 14.0F;
      float var10 = var5 + 32.0F;
      float var11 = 156.0F;
      float var12 = 110.0F;
      byte var13 = 20;
      float var14 = var11 / var13;
      float var15 = var12 / var13;
      NanoVGRenderer.save();
      NanoVGRenderer.scissor(var9, var10, var11, var12);

      for (int var16 = 0; var16 < var13; var16++) {
         for (int var17 = 0; var17 < var13; var17++) {
            float var18 = (float)var17 / var13;
            float var19 = 1.0F - (float)var16 / var13;
            NanoVGRenderer.drawRect(var9 + var17 * var14, var10 + var16 * var15, var14 + 0.5F, var15 + 0.5F, Color.HSBtoRGB(this.hue, var18, var19));
         }
      }

      NanoVGRenderer.resetScissor();
      NanoVGRenderer.restore();
      NanoVGRenderer.drawRoundedRectOutline(var9, var10, var11, var12, 4.0F, 1.0F, SoftPalette.whiteInt(20.0F * alpha));
      float var34 = var9 + this.sat * var11;
      float var35 = var10 + (1.0F - this.val) * var12;
      int var36 = SoftPalette.clamp(255.0F * alpha) << 24;
      NanoVGRenderer.drawCircle(var34, var35, 5.0F, var36 | 16777215);
      NanoVGRenderer.drawCircleOutline(var34, var35, 5.0F, 1.5F, SoftPalette.clamp(180.0F * alpha) << 24);
      float var37 = 14.0F;
      float var20 = var9 + var11 + 10.0F;
      byte var21 = 36;
      float var22 = var12 / var21;

      for (int var23 = 0; var23 < var21; var23++) {
         float var24 = (float)var23 / var21;
         NanoVGRenderer.drawRect(var20, var10 + var23 * var22, var37, var22 + 0.5F, Color.HSBtoRGB(var24, 1.0F, 1.0F));
      }

      NanoVGRenderer.drawRoundedRectOutline(var20, var10, var37, var12, 4.0F, 1.0F, SoftPalette.whiteInt(20.0F * alpha));
      float var38 = var10 + this.hue * var12;
      NanoVGRenderer.drawRoundedRect(var20 - 2.0F, var38 - 2.0F, var37 + 4.0F, 4.0F, 2.0F, var36 | 16777215);
      int var39 = Color.HSBtoRGB(this.hue, this.sat, this.val);
      float var25 = var10 + var12 + 10.0F;
      NanoVGRenderer.drawRoundedRect(var9, var25, 50.0F, 18.0F, 4.0F, var39);
      NanoVGRenderer.drawRoundedRectOutline(var9, var25, 50.0F, 18.0F, 4.0F, 1.0F, SoftPalette.whiteInt(60.0F * alpha));
      String var26 = "#" + String.format("%06X", var39 & 16777215);
      SoftPalette.label(var26, var9 + 58.0F, var25 + 4.75F, 8.5F, SoftPalette.textInt(255.0F * alpha));
      float var27 = var25 + 24.0F;
      float var28 = 18.0F;
      float var29 = 4.0F;

      for (int var30 = 0; var30 < PRESET_COLORS.length; var30++) {
         float var31 = var9 + var30 * (var28 + var29);
         int var32 = PRESET_COLORS[var30];
         boolean var33 = hit(mx, my, var31, var27, var28, var28);
         NanoVGRenderer.drawRoundedRect(var31, var27, var28, var28, 4.0F, var32);
         NanoVGRenderer.drawRoundedRectOutline(
            var31, var27, var28, var28, 4.0F, var33 ? 1.5F : 1.0F, var33 ? SoftPalette.mintInt(255.0F * alpha) : SoftPalette.whiteInt(50.0F * alpha)
         );
      }
   }

   private float cardX(int slotIndex) {
      return this.bodyX() + slotIndex % (2) * 215.0F;
   }

   private float cardY(int slotIndex) {
      return this.contentY() + slotIndex / (2) * 39.0F - this.scroll;
   }

   private float pickerPX() {
      return this.px + (this.pw - 232.0F) / 2.0F;
   }

   private float pickerPY() {
      return this.py + (this.ph - 234.0F) / 2.0F;
   }

   private List getVisibleIndices() {
      ArrayList var1 = new ArrayList();
      String var2 = this.searchQuery.trim().toLowerCase();

      for (int var3 = 0; var3 < (12); var3++) {
         if (var2.isEmpty() || NAMES[var3].toLowerCase().contains(var2)) {
            var1.add(var3);
         }
      }

      return var1;
   }

   @Override
   protected boolean mouseClickedContent(double mx, double my, int btn) {
      if (btn != 0) {
         return false;
      }

      if (this.pickerRow >= 0) {
         return this.handlePickerClick(mx, my);
      }

      float var6 = this.actionY();
      float var7 = this.actionH();
      float var8 = 22.0F;
      float var9 = 5.0F;
      float var10 = this.px + this.pw - 18.0F;
      float var11 = var6 + (var7 - var8) / 2.0F;
      float var12 = 46.0F;
      float var13 = 48.0F;
      float var14 = 54.0F;
      float var15 = 48.0F;
      var10 -= var12;
      if (hit(mx, my, var10, var11, var12, var8)) {
         this.resetDefaults();
         return true;
      }

      var10 -= var13 + var9;
      if (hit(mx, my, var10, var11, var13, var8)) {
         this.invertAll();
         return true;
      }

      var10 -= var14 + var9;
      if (hit(mx, my, var10, var11, var14, var8)) {
         this.setAll(false);
         return true;
      }

      var10 -= var15 + var9;
      if (hit(mx, my, var10, var11, var15, var8)) {
         this.setAll(true);
         return true;
      }

      if (hit(mx, my, this.bodyX(), var6, 140.0F, var7)) {
         this.searchFocused = true;
         return true;
      }

      this.searchFocused = false;
      List var16 = this.getVisibleIndices();

      for (int var17 = 0; var17 < var16.size(); var17++) {
         int var18 = (Integer)var16.get(var17);
         float var19 = this.cardX(var17);
         float var20 = this.cardY(var17);
         if (!(var20 + 34.0F < this.contentY()) && !(var20 > this.contentY() + this.contentListH())) {
            float var21 = var19 + 205.0F - 54.0F;
            float var22 = var20 + 8.0F;
            if (hit(mx, my, var21, var22, 18.0F, 18.0F)) {
               this.openPicker(var18);
               return true;
            }

            if (hit(mx, my, var19, var20, 205.0F, 34.0F)) {
               this.on[var18] = !this.on[var18];
               return true;
            }
         }
      }

      float var27 = this.footerY() + 5.0F;
      float var28 = this.px + this.pw - 18.0F - 78.0F;
      float var29 = var28 - 6.0F - 78.0F;
      if (hit(mx, my, var29, var27, 78.0F, 24.0F)) {
         this.returnGUI();
         return true;
      } else if (hit(mx, my, var28, var27, 78.0F, 24.0F)) {
         this.save();
         return true;
      } else {
         return false;
      }
   }

   private boolean handlePickerClick(double mx, double my) {
      float var5 = this.pickerPX();
      float var6 = this.pickerPY();
      float var7 = 232.0F;
      float var8 = 234.0F;
      if (hit(mx, my, var5 + var7 - 26.0F, var6 + 9.0F, 18.0F, 18.0F)) {
         this.pickerRow = -1;
         return true;
      }

      float var9 = var5 + 14.0F;
      float var10 = var6 + 32.0F;
      float var11 = 156.0F;
      float var12 = 110.0F;
      if (hit(mx, my, var9, var10, var11, var12)) {
         this.dragSv = true;
         this.dragHue = false;
         this.pickSV((float)mx, (float)my);
         return true;
      }

      float var13 = var9 + var11 + 10.0F;
      float var14 = 14.0F;
      if (hit(mx, my, var13, var10, var14, var12)) {
         this.dragHue = true;
         this.dragSv = false;
         this.pickHue((float)my);
         return true;
      }

      float var15 = var10 + var12 + 10.0F;
      float var16 = var15 + 24.0F;
      float var17 = 18.0F;
      float var18 = 4.0F;

      for (int var19 = 0; var19 < PRESET_COLORS.length; var19++) {
         float var20 = var9 + var19 * (var17 + var18);
         if (hit(mx, my, var20, var16, var17, var17)) {
            this.setPickerColor(PRESET_COLORS[var19]);
            return true;
         }
      }

      if (!(mx < var5) && !(mx > var5 + var7) && !(my < var6) && !(my > var6 + var8)) {
         return true;
      }

      this.pickerRow = -1;
      return true;
   }

   public boolean method_25403(class_11909 click, double dx, double dy) {
      if (click.method_74245() != 0) {
         return false;
      } else if (this.dragSv) {
         this.pickSV((float)click.comp_4798(), (float)click.comp_4799());
         return true;
      } else if (this.dragHue) {
         this.pickHue((float)click.comp_4799());
         return true;
      } else {
         return false;
      }
   }

   public boolean method_25406(class_11909 click) {
      this.dragSv = false;
      this.dragHue = false;
      return false;
   }

   @Override
   protected boolean keyPressedContent(int kc, int sc, int mods) {
      if (kc == (256)) {
         if (this.pickerRow >= 0) {
            this.pickerRow = -1;
            return true;
         } else {
            this.save();
            return true;
         }
      } else {
         if (this.searchFocused) {
            if (kc == (259) && !this.searchQuery.isEmpty()) {
               this.searchQuery = this.searchQuery.substring(0, this.searchQuery.length() - 1);
               return true;
            }

            if (kc == (257) || kc == (335)) {
               this.searchFocused = false;
               return true;
            }
         }

         return false;
      }
   }

   @Override
   protected boolean charTypedContent(char chr, int mods) {
      if (this.searchFocused && !Character.isISOControl(chr)) {
         this.searchQuery = this.searchQuery + chr;
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean closesOnOutsideClick() {
      return false;
   }

   @Override
   protected boolean hasDrawContextContent() {
      return false;
   }

   private void openPicker(int row) {
      this.pickerRow = row;
      int var2 = this.col[row] & (16777215);
      float[] var3 = Color.RGBtoHSB(
         var2 >> (16) & (255), var2 >> (8) & (255), var2 & (255), null
      );
      this.hue = var3[0];
      this.sat = Math.max(0.0F, Math.min(1.0F, var3[1]));
      this.val = Math.max(0.0F, Math.min(1.0F, var3[2]));
   }

   private void pickSV(float mx, float my) {
      float var3 = this.pickerPX() + 14.0F;
      float var4 = this.pickerPY() + 32.0F;
      float var5 = 156.0F;
      float var6 = 110.0F;
      this.sat = Math.max(0.0F, Math.min(1.0F, (mx - var3) / var5));
      this.val = Math.max(0.0F, Math.min(1.0F, 1.0F - (my - var4) / var6));
      this.applyPickerColor();
   }

   private void pickHue(float my) {
      float var2 = this.pickerPY() + 32.0F;
      float var3 = 110.0F;
      this.hue = Math.max(0.0F, Math.min(1.0F, (my - var2) / var3));
      this.applyPickerColor();
   }

   private void setPickerColor(int rgb) {
      float[] var2 = Color.RGBtoHSB(
         rgb >> (16) & (255), rgb >> (8) & (255), rgb & (255), null
      );
      this.hue = var2[0];
      this.sat = var2[1];
      this.val = var2[2];
      this.applyPickerColor();
   }

   private void applyPickerColor() {
      if (this.pickerRow >= 0) {
         int var1 = Color.HSBtoRGB(this.hue, this.sat, this.val) & (16777215);
         this.col[this.pickerRow] = -16777216 | var1;
      }
   }

   private void setAll(boolean state) {
      for (int var2 = 0; var2 < (12); var2++) {
         this.on[var2] = state;
      }
   }

   private void invertAll() {
      for (int var1 = 0; var1 < (12); var1++) {
         this.on[var1] = !this.on[var1];
      }
   }

   private void resetDefaults() {
      for (int var1 = 0; var1 < (12); var1++) {
         this.on[var1] = var1 != (4);
         this.col[var1] = DEFAULT_COLORS[var1] | -16777216;
      }
   }

   private void save() {
      if (this.module != null) {
         this.module.applyFilterBatch(this.on, this.col);
      }

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
