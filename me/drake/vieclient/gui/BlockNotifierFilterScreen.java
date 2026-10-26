package me.drake.vieclient.gui;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.gui.nvg.ui.components.SoftPalette;
import me.drake.vieclient.gui.nvg.ui.components.VieOSScreen;
import me.drake.vieclient.modules.render.BlockNotifier;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_2561;
import net.minecraft.class_332;

public class BlockNotifierFilterScreen extends VieOSScreen {
   private static final float CELL_SIZE = 24.0F;
   private static final float CELL_GAP = 2.0F;
   private static final float CELL_STRIDE = 26.0F;
   private static final float CHIP_W = 82.0F;
   private static final float CHIP_H = 26.0F;
   private static final float CHIP_GAP = 6.0F;
   private static final float TOG_H = 22.0F;
   private static final float SEARCH_W = 95.0F;
   private static final float TAB_PAD = 12.0F;
   private static final float TAB_GAP = 4.0F;
   private static final float BATCH_W = 42.0F;
   private final BlockNotifier module;
   private final List allOptions;
   private List filteredOptions;
   private BlockNotifierFilterScreen.CategoryTab currentTab;
   private String searchQuery;
   private float scrollOffset;
   private float targetScroll;
   private long lastScrollMs;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BlockNotifierFilterScreen(BlockNotifier module) {
      byte[] var10001 = new byte[14];
      var10001[0] = (byte)(75);
      var10001[1] = (byte)(-10);
      var10001[2] = (byte)(115);
      var10001[3] = (byte)(-67);
      var10001[4] = (byte)(118);
      var10001[5] = (byte)(-42);
      var10001[6] = (byte)(-82);
      var10001[7] = (byte)(24);
      var10001[8] = (byte)(21);
      var10001[9] = (byte)(-53);
      var10001[10] = (byte)(-126);
      var10001[11] = (byte)(-97);
      var10001[12] = (byte)(32);
      var10001[13] = (byte)(92);
      super(class_2561.method_43470($$v_s(var10001, 6909410939405324332L)));
      this.allOptions = new ArrayList();
      this.filteredOptions = new ArrayList();
      this.currentTab = BlockNotifierFilterScreen.CategoryTab.ALL;
      this.searchQuery = "";
      this.scrollOffset = 0.0F;
      this.targetScroll = 0.0F;
      this.lastScrollMs = 0L;
      this.module = modulex;
      this.allOptions.addAll(modulex.getBlockOptions());
      this.refreshFiltered(true);
      this.openScreen();
   }

   @Override
   protected float contentW(int sw) {
      return Math.min(sw - 32.0F, 516.0F);
   }

   @Override
   protected float contentH(int sw, int sh) {
      return Math.min(sh - 32.0F, 470.0F);
   }

   @Override
   protected String title() {
      byte[] var10000 = new byte[14];
      var10000[0] = (byte)(75);
      var10000[1] = (byte)(-10);
      var10000[2] = (byte)(115);
      var10000[3] = (byte)(-67);
      var10000[4] = (byte)(118);
      var10000[5] = (byte)(-42);
      var10000[6] = (byte)(-82);
      var10000[7] = (byte)(24);
      var10000[8] = (byte)(21);
      var10000[9] = (byte)(-53);
      var10000[10] = (byte)(-126);
      var10000[11] = (byte)(-97);
      var10000[12] = (byte)(32);
      var10000[13] = (byte)(92);
      return $$v_s(var10000, 6909410939405324332L);
   }

   @Override
   protected String footerText() {
      byte[] var10000 = new byte[59];
      var10000[0] = (byte)(74);
      var10000[1] = (byte)(-10);
      var10000[2] = (byte)(117);
      var10000[3] = (byte)(-67);
      var10000[4] = (byte)(118);
      var10000[5] = (byte)(-42);
      var10000[6] = (byte)(-126);
      var10000[7] = (byte)(27);
      var10000[8] = (byte)(14);
      var10000[9] = (byte)(-63);
      var10000[10] = (byte)(-113);
      var10000[11] = (byte)(-42);
      var10000[12] = (byte)(49);
      var10000[13] = (byte)(65);
      var10000[14] = (byte)(-24);
      var10000[15] = (byte)(123);
      var10000[16] = (byte)(54);
      var10000[17] = (byte)(45);
      var10000[18] = (byte)(-85);
      var10000[19] = (byte)(-62);
      var10000[20] = (byte)(-56);
      var10000[21] = (byte)(38);
      var10000[22] = (byte)(-47);
      var10000[23] = (byte)(75);
      var10000[24] = (byte)(-44);
      var10000[25] = (byte)(96);
      var10000[26] = (byte)(-32);
      var10000[27] = (byte)(102);
      var10000[28] = (byte)(-75);
      var10000[29] = (byte)(-68);
      var10000[30] = (byte)(47);
      var10000[31] = (byte)(-33);
      var10000[32] = (byte)(-55);
      var10000[33] = (byte)(121);
      var10000[34] = (byte)(20);
      var10000[35] = (byte)(31);
      var10000[36] = (byte)(-109);
      var10000[37] = (byte)(49);
      var10000[38] = (byte)(37);
      var10000[39] = (byte)(-28);
      var10000[40] = (byte)(-31);
      var10000[41] = (byte)(-79);
      var10000[42] = (byte)(37);
      var10000[43] = (byte)(96);
      var10000[44] = (byte)(64);
      var10000[45] = (byte)(-82);
      var10000[46] = (byte)(73);
      var10000[47] = (byte)(-38);
      var10000[48] = (byte)(77);
      var10000[49] = (byte)(-123);
      var10000[50] = (byte)(65);
      var10000[51] = (byte)(-81);
      var10000[52] = (byte)(121);
      var10000[53] = (byte)(-113);
      var10000[54] = (byte)(115);
      var10000[55] = (byte)(38);
      var10000[56] = (byte)(125);
      var10000[57] = (byte)(-34);
      var10000[58] = (byte)(-115);
      return $$v_s(var10000, 6909410939405324332L);
   }

   @Override
   protected void renderHeaderExtras(int mx, int my, float alpha) {
      float var4 = this.px + 170.0F;
      float var5 = this.py + 18.0F - 9.0F;
      float var6 = 120.0F;
      float var7 = 18.0F;
      NanoVGRenderer.drawRoundedRect(var4, var5, var6, var7, 4.0F, SoftPalette.whiteInt(12.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(var4, var5, var6, var7, 4.0F, 1.0F, SoftPalette.whiteInt(24.0F * alpha));
      byte[] var10000 = new byte[21];
      var10000[0] = (byte)(72);
      var10000[1] = (byte)(-10);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-84);
      var10000[4] = (byte)(105);
      var10000[5] = (byte)(-123);
      var10000[6] = (byte)(-64);
      var10000[7] = (byte)(81);
      var10000[8] = (byte)(65);
      var10000[9] = (byte)(-32);
      var10000[10] = (byte)(-120);
      var10000[11] = (byte)(-103);
      var10000[12] = (byte)(38);
      var10000[13] = (byte)(69);
      var10000[14] = (byte)(-24);
      var10000[15] = (byte)(73);
      var10000[16] = (byte)(48);
      var10000[17] = (byte)(38);
      var10000[18] = (byte)(-72);
      var10000[19] = (byte)(-53);
      var10000[20] = (byte)(-33);
      SoftPalette.label($$v_s(var10000, 6909410939405324332L), var4 + 7.0F, var5 + 4.0F, 8.5F, SoftPalette.textDimInt(230.0F * alpha));
   }

   private float channelsY() {
      return this.py + 36.0F + 8.0F;
   }

   private float channelsH() {
      return 22.0F;
   }

   private float paramsY() {
      return this.channelsY() + this.channelsH() + 6.0F;
   }

   private float paramsH() {
      return 20.0F;
   }

   private float filterBarY() {
      return this.paramsY() + this.paramsH() + 8.0F;
   }

   private float filterBarH() {
      return 28.0F;
   }

   @Override
   protected float contentY() {
      return this.filterBarY() + this.filterBarH() + 10.0F;
   }

   private int getVisibleRows() {
      float var1 = this.footerY() - this.contentY() - 8.0F;
      return Math.max(1, (int)(var1 / 26.0F));
   }

   @Override
   protected float contentListH() {
      return this.getVisibleRows() * 26.0F;
   }

   private int itemsPerRow() {
      return Math.max(1, (int)(this.bodyW() / 26.0F));
   }

   private boolean matchesCategory(BlockNotifier.BlockOption opt, BlockNotifierFilterScreen.CategoryTab tab) {
      String var3 = opt.id().method_12832().toLowerCase(Locale.ROOT);

      return switch (tab) {
         case ALL -> true;
         case ORES -> var3.contains("ore")
            || var3.contains("ancient_debris")
            || var3.contains("raw_")
            || var3.endsWith("_block")
               && (
                  var3.contains("diamond")
                     || var3.contains("netherite")
                     || var3.contains("emerald")
                     || var3.contains("gold")
                     || var3.contains("iron")
               );
         case BASES -> var3.contains("spawner")
            || var3.contains("vault")
            || var3.contains("portal")
            || var3.equals("ender_chest")
            || var3.equals("enchanting_table")
            || var3.equals("beacon")
            || var3.equals("conduit")
            || var3.equals("lodestone")
            || var3.equals("dragon_egg")
            || var3.equals("respawn_anchor")
            || var3.endsWith("_bed")
            || var3.equals("heavy_core");
         case STORAGE -> var3.contains("chest")
            || var3.contains("barrel")
            || var3.contains("shulker_box")
            || var3.contains("hopper")
            || var3.contains("dispenser")
            || var3.contains("dropper")
            || var3.contains("crafter")
            || var3.contains("furnace")
            || var3.contains("smoker")
            || var3.contains("brewing_stand");
         case ACTIVE -> this.module.isBlockEnabled(opt.id());
      };
   }

   private void refreshFiltered(boolean resetScroll) {
      List var2 = this.allOptions.stream().filter(opt -> this.matchesCategory(opt, this.currentTab)).collect(Collectors.toList());
      if (this.searchQuery.trim().isEmpty()) {
         this.filteredOptions = var2;
      } else {
         String var3 = this.searchQuery.toLowerCase(Locale.ROOT).trim();
         this.filteredOptions = var2.stream().filter(opt -> opt.searchKey().contains(var3)).collect(Collectors.toList());
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
      int var2 = (this.filteredOptions.size() + var1 - 1) / var1;
      float var3 = Math.max(0.0F, var2 * 26.0F - this.contentListH());
      this.targetScroll = Math.max(0.0F, Math.min(var3, this.targetScroll));
      this.scrollOffset = Math.max(0.0F, Math.min(var3, this.scrollOffset));
   }

   @Override
   protected void tickAnimation(float delta) {
      this.scrollOffset = AnimationManager.isOff() ? this.targetScroll : AnimationManager.damp(this.scrollOffset, this.targetScroll, 20.0F, delta);
   }

   @Override
   protected boolean hasDrawContextContent() {
      return false;
   }

   @Override
   protected void drawContextContent(class_332 ctx, int mouseX, int mouseY, float delta) {
   }

   @Override
   protected void renderContent(int mx, int my, float alpha) {
      this.renderChannelsBar(mx, my, alpha);
      this.renderParamsBar(mx, my, alpha);
      this.renderFilterBar(mx, my, alpha);
      this.renderGridBackgroundAndHighlights(mx, my, alpha);
      this.renderScrollbar(alpha);
      this.renderFooterChips(mx, my, alpha);
   }

   private void renderChannelsBar(int mx, int my, float alpha) {
      float var4 = this.channelsY();
      float var5 = this.channelsH();
      float var6 = this.bodyX();
      float var7 = this.bodyW();
      String[] var8 = new String[]{
         "Toast",
         "Sound",
         "Tracers",
         "Box"
      };
      boolean[] var9 = new boolean[]{
         (Boolean)this.module.getToastNotifySetting().get(),
         (Boolean)this.module.getSoundNotifySetting().get(),
         (Boolean)this.module.getTracersSetting().get(),
         (Boolean)this.module.getBoxSetting().get()
      };
      float var10 = (var7 - (var8.length - 1) * 6.0F) / var8.length;

      for (int var11 = 0; var11 < var8.length; var11++) {
         float var12 = var6 + var11 * (var10 + 6.0F);
         boolean var13 = var9[var11];
         boolean var14 = hit(mx, my, var12, var4, var10, var5);
         int var15 = var13 ? SoftPalette.mintInt((int)((var14 ? 210 : 180) * alpha)) : SoftPalette.whiteInt((int)((var14 ? 24 : 10) * alpha));
         int var16 = var13 ? SoftPalette.mintInt((int)(255.0F * alpha)) : SoftPalette.whiteInt((int)((var14 ? 40 : 20) * alpha));
         NanoVGRenderer.drawRoundedRect(var12, var4, var10, var5, 5.0F, var15);
         NanoVGRenderer.drawRoundedRectOutline(var12, var4, var10, var5, 5.0F, 1.0F, var16);
         int var17 = var13 ? -1 : 1728053247;
         NanoVGRenderer.drawCircle(var12 + 8.0F, var4 + var5 / 2.0F, 2.5F, var17);
         float var18 = SoftPalette.widthBold(var8[var11], 9.0F);
         SoftPalette.labelBold(
            var8[var11],
            var12 + 15.0F + (var10 - 15.0F - var18) / 2.0F,
            var4 + (var5 - 9.0F) / 2.0F,
            9.0F,
            var13 ? SoftPalette.whiteInt(255.0F * alpha) : SoftPalette.textInt((int)(220.0F * alpha))
         );
      }
   }

   private void renderParamsBar(int mx, int my, float alpha) {
      float var4 = this.paramsY();
      float var5 = this.paramsH();
      float var6 = this.bodyX();
      int var7 = (int)((Double)this.module.getRangeSetting().get()).doubleValue();
      String var8 = "Range: " + var7 + "m";
      float var9 = 90.0F;
      boolean var10 = hit(mx, my, var6, var4, var9, var5);
      this.drawParamPill(var6, var4, var9, var5, var8, var10, alpha);
      int var11 = (Integer)this.module.getCooldownSetting().get();
      String var12 = var11 == 0
         ? "Cooldown: Once"
         : "Cooldown: " + var11 + "s";
      float var13 = var6 + var9 + 8.0F;
      float var14 = 105.0F;
      boolean var15 = hit(mx, my, var13, var4, var14, var5);
      this.drawParamPill(var13, var4, var14, var5, var12, var15, alpha);
      if ((Boolean)this.module.getTracersSetting().get() || (Boolean)this.module.getBoxSetting().get()) {
         int var16 = (Integer)this.module.getTracerDurationSetting().get();
         String var17 = "Visual: " + var16 + "s";
         float var18 = var13 + var14 + 8.0F;
         float var19 = 85.0F;
         boolean var20 = hit(mx, my, var18, var4, var19, var5);
         this.drawParamPill(var18, var4, var19, var5, var17, var20, alpha);
      }

      float var21 = this.px + this.pw - 18.0F - 100.0F;
      int var22 = this.module.getEnabledBlockTypeCount();
      String var23 = var22 + " Alerting";
      NanoVGRenderer.drawRoundedRect(var21, var4, 100.0F, var5, 4.0F, SoftPalette.mintInt((int)(35.0F * alpha)));
      NanoVGRenderer.drawRoundedRectOutline(var21, var4, 100.0F, var5, 4.0F, 1.0F, SoftPalette.mintInt((int)(90.0F * alpha)));
      float var24 = SoftPalette.widthBold(var23, 8.5F);
      SoftPalette.labelBold(var23, var21 + (100.0F - var24) / 2.0F, var4 + (var5 - 8.5F) / 2.0F, 8.5F, SoftPalette.mintInt((int)(255.0F * alpha)));
   }

   private void drawParamPill(float x, float y, float w, float h, String label, boolean hov, float alpha) {
      NanoVGRenderer.drawRoundedRect(x, y, w, h, 4.0F, SoftPalette.whiteInt((int)((hov ? 24 : 12) * alpha)));
      NanoVGRenderer.drawRoundedRectOutline(x, y, w, h, 4.0F, 1.0F, SoftPalette.whiteInt((int)((hov ? 45 : 22) * alpha)));
      float var8 = SoftPalette.width(label, 8.5F);
      SoftPalette.label(label, x + (w - var8) / 2.0F, y + (h - 8.5F) / 2.0F, 8.5F, SoftPalette.textDimInt((int)(240.0F * alpha)));
   }

   private void renderFilterBar(int mx, int my, float alpha) {
      float var4 = this.filterBarY();
      float var5 = this.filterBarH();
      float var6 = this.bodyX();
      float var7 = 95.0F;
      NanoVGRenderer.drawRoundedRect(var6, var4, var7, var5, 6.0F, SoftPalette.whiteInt((int)(10.0F * alpha)));
      NanoVGRenderer.drawRoundedRectOutline(var6, var4, var7, var5, 6.0F, 1.0F, SoftPalette.whiteInt((int)(22.0F * alpha)));
      boolean var8 = System.currentTimeMillis() / 530L % 2L == 0L;
      String var9 = this.searchQuery.isEmpty()
         ? "Search..."
         : this.searchQuery + (var8 ? "|" : "");
      SoftPalette.label(
         var9,
         var6 + 8.0F,
         var4 + (var5 - 9.0F) / 2.0F,
         9.0F,
         this.searchQuery.isEmpty() ? SoftPalette.textDimInt((int)(190.0F * alpha)) : SoftPalette.textInt((int)(255.0F * alpha))
      );
      float var10 = var6 + var7 + 8.0F;
      BlockNotifierFilterScreen.CategoryTab[] var11 = BlockNotifierFilterScreen.CategoryTab.values();

      for (BlockNotifierFilterScreen.CategoryTab var15 : var11) {
         boolean var16 = var15 == this.currentTab;
         String var17 = var15 == BlockNotifierFilterScreen.CategoryTab.ACTIVE ? "Active (" + this.module.getEnabledBlockTypeCount() + ")" : var15.label;
         float var18 = SoftPalette.widthBold(var17, 8.5F);
         float var19 = var18 + 12.0F;
         boolean var20 = hit(mx, my, var10, var4, var19, var5);
         int var21 = var16 ? SoftPalette.mintInt((int)(200.0F * alpha)) : SoftPalette.whiteInt((int)((var20 ? 24 : 10) * alpha));
         int var22 = var16 ? SoftPalette.mintInt((int)(255.0F * alpha)) : SoftPalette.whiteInt((int)((var20 ? 40 : 18) * alpha));
         NanoVGRenderer.drawRoundedRect(var10, var4, var19, var5, 5.0F, var21);
         NanoVGRenderer.drawRoundedRectOutline(var10, var4, var19, var5, 5.0F, 1.0F, var22);
         SoftPalette.labelBold(
            var17, var10 + 6.0F, var4 + (var5 - 8.5F) / 2.0F, 8.5F, var16 ? SoftPalette.whiteInt(255.0F * alpha) : SoftPalette.textInt((int)(220.0F * alpha))
         );
         var10 += var19 + 4.0F;
      }

      float var23 = this.px + this.pw - 18.0F - 42.0F;
      float var24 = var23 - 42.0F - 5.0F;
      float var25 = var24 - 10.0F;
      if (var25 > var10 + 4.0F) {
         NanoVGRenderer.drawRect(var25, var4 + 3.0F, 1.0F, var5 - 6.0F, SoftPalette.whiteInt((int)(20.0F * alpha)));
      }

      boolean var26 = hit(mx, my, var24, var4, 42.0F, var5);
      NanoVGRenderer.drawRoundedRect(var24, var4, 42.0F, var5, 5.0F, SoftPalette.whiteInt((int)((var26 ? 26 : 12) * alpha)));
      NanoVGRenderer.drawRoundedRectOutline(var24, var4, 42.0F, var5, 5.0F, 1.0F, SoftPalette.whiteInt((int)((var26 ? 45 : 22) * alpha)));
      SoftPalette.labelBold(
         "+ All",
         var24 + (42.0F - SoftPalette.widthBold("+ All", 8.5F)) / 2.0F,
         var4 + (var5 - 8.5F) / 2.0F,
         8.5F,
         SoftPalette.mintInt((int)(255.0F * alpha))
      );
      boolean var27 = hit(mx, my, var23, var4, 42.0F, var5);
      NanoVGRenderer.drawRoundedRect(var23, var4, 42.0F, var5, 5.0F, SoftPalette.whiteInt((int)((var27 ? 26 : 12) * alpha)));
      NanoVGRenderer.drawRoundedRectOutline(var23, var4, 42.0F, var5, 5.0F, 1.0F, SoftPalette.whiteInt((int)((var27 ? 45 : 22) * alpha)));
      SoftPalette.labelBold(
         "- None",
         var23 + (42.0F - SoftPalette.widthBold("- None", 8.5F)) / 2.0F,
         var4 + (var5 - 8.5F) / 2.0F,
         8.5F,
         SoftPalette.textDimInt((int)(240.0F * alpha))
      );
   }

   private void renderGridBackgroundAndHighlights(int mx, int my, float alpha) {
      int var4 = this.itemsPerRow();
      float var5 = this.bodyX();
      float var6 = this.contentY();
      float var7 = this.contentListH();
      float var8 = var4 * 26.0F;
      NanoVGRenderer.drawRoundedRect(var5 - 3.0F, var6 - 3.0F, var8 + 6.0F, var7 + 6.0F, 6.0F, SoftPalette.whiteInt((int)(6.0F * alpha)));
      NanoVGRenderer.drawRoundedRectOutline(var5 - 3.0F, var6 - 3.0F, var8 + 6.0F, var7 + 6.0F, 6.0F, 1.0F, SoftPalette.whiteInt((int)(14.0F * alpha)));
      NanoVGRenderer.save();
      NanoVGRenderer.scissor(var5, var6, var8, var7);

      for (int var9 = 0; var9 < this.filteredOptions.size(); var9++) {
         int var10 = var9 / var4;
         int var11 = var9 % var4;
         float var12 = var5 + var11 * 26.0F;
         float var13 = var6 + var10 * 26.0F - this.scrollOffset;
         if (!(var13 + 24.0F < var6) && !(var13 > var6 + var7)) {
            BlockNotifier.BlockOption var14 = (BlockNotifier.BlockOption)this.filteredOptions.get(var9);
            boolean var15 = this.module.isBlockEnabled(var14.id());
            boolean var16 = mx >= var12 && mx <= var12 + 24.0F && my >= var13 && my <= var13 + 24.0F;
            int var17 = var15 ? (int)(70.0F * alpha) : (int)((var16 ? 24 : 10) * alpha);
            int var18 = var15 ? SoftPalette.mintInt(var17) : SoftPalette.whiteInt(var17);
            NanoVGRenderer.drawRoundedRect(var12, var13, 24.0F, 24.0F, 4.0F, var18);
            int var19 = var15 ? (int)(230.0F * alpha) : (int)((var16 ? 50 : 20) * alpha);
            int var20 = var15 ? SoftPalette.mintInt(var19) : SoftPalette.whiteInt(var19);
            NanoVGRenderer.drawRoundedRectOutline(var12, var13, 24.0F, 24.0F, 4.0F, var15 ? 1.5F : 1.0F, var20);
            if (!var14.icon().method_7960()) {
               NVGImage.drawMCItem(var14.icon(), var12 + 4.0F, var13 + 4.0F, 16.0F, alpha);
            }

            if (var15) {
               NanoVGRenderer.drawCircle(var12 + 24.0F - 4.0F, var13 + 4.0F, 2.2F, -13369464);
            }
         }
      }

      NanoVGRenderer.resetScissor();
      NanoVGRenderer.restore();
      if (this.filteredOptions.isEmpty()) {
         String var21 = this.searchQuery.isEmpty()
            ? "No blocks found in category \"" + this.currentTab.label + "\"."
            : "No blocks match \"" + this.searchQuery + "\".";
         float var22 = SoftPalette.width(var21, 9.5F);
         SoftPalette.label(var21, this.px + (this.pw - var22) / 2.0F, var6 + var7 / 2.0F - 5.0F, 9.5F, SoftPalette.textDimInt((int)(200.0F * alpha)));
      }
   }

   private void renderScrollbar(float alpha) {
      int var2 = this.itemsPerRow();
      float var3 = this.bodyX();
      float var4 = this.contentY();
      float var5 = this.contentListH();
      int var6 = (this.filteredOptions.size() + var2 - 1) / var2;
      int var7 = this.getVisibleRows();
      if (var6 > var7) {
         long var8 = System.currentTimeMillis();
         boolean var10 = Math.abs(this.targetScroll - this.scrollOffset) > 0.5F || var8 - this.lastScrollMs < 700L;
         float var11 = AnimationManager.isOff() ? 1.0F : (var10 ? 1.0F : Math.max(0.0F, 1.0F - (float)(var8 - this.lastScrollMs - 700L) / 400.0F));
         if (var11 > 0.01F) {
            float var12 = var3 + var2 * 26.0F + 4.0F;
            float var13 = Math.max(20.0F, var5 * var7 / var6);
            float var14 = Math.max(1.0F, var6 * 26.0F - var5);
            float var15 = var4 + (var5 - var13) * (this.scrollOffset / var14);
            NanoVGRenderer.drawRoundedRect(
               var12, var4, 3.0F, var5, 1.5F, SoftPalette.rgbaInt(32, 32, 40, (int)(255.0F * alpha * var11))
            );
            NanoVGRenderer.drawRoundedRect(var12, var15, 3.0F, var13, 1.5F, SoftPalette.mintInt((int)(255.0F * var11)));
         }
      }
   }

   private void renderFooterChips(int mx, int my, float alpha) {
      float var4 = this.footerY() + 4.0F;
      float var5 = this.px + this.pw - 18.0F - 82.0F;
      float var6 = var5 - 6.0F - 82.0F;
      float var7 = this.px + 18.0F;
      float var8 = 105.0F;
      byte[] var10004 = new byte[14];
      var10004[0] = (byte)(91);
      var10004[1] = (byte)(-1);
      var10004[2] = (byte)(111);
      var10004[3] = (byte)(-69);
      var10004[4] = (byte)(105);
      var10004[5] = (byte)(-42);
      var10004[6] = (byte)(-92);
      var10004[7] = (byte)(18);
      var10004[8] = (byte)(7);
      var10004[9] = (byte)(-61);
      var10004[10] = (byte)(-111);
      var10004[11] = (byte)(-102);
      var10004[12] = (byte)(49);
      var10004[13] = (byte)(93);
      drawDangerChip(var7, var4, var8, 26.0F, $$v_s(var10004, 6909410939405324332L), hit(mx, my, var7, var4, var8, 26.0F), alpha);
      var10004 = new byte[6];
      var10004[0] = (byte)(74);
      var10004[1] = (byte)(-5);
      var10004[2] = (byte)(114);
      var10004[3] = (byte)(-67);
      var10004[4] = (byte)(120);
      var10004[5] = (byte)(-102);
      drawNeutralChip(var6, var4, 82.0F, 26.0F, $$v_s(var10004, 6909410939405324332L), hit(mx, my, var6, var4, 82.0F, 26.0F), alpha);
      var10004 = new byte[4];
      var10004[0] = (byte)(90);
      var10004[1] = (byte)(-5);
      var10004[2] = (byte)(106);
      var10004[3] = (byte)(-69);
      drawAccentChip(var5, var4, 82.0F, 26.0F, $$v_s(var10004, 6909410939405324332L), alpha);
   }

   @Override
   protected void renderExtras(int mx, int my, float alpha) {
      super.renderExtras(mx, my, alpha);
      int var4 = this.itemsPerRow();
      float var5 = this.bodyX();
      float var6 = this.contentY();
      float var7 = this.contentListH();
      if (mx >= var5 && mx <= var5 + var4 * 26.0F && my >= var6 && my <= var6 + var7) {
         int var8 = (int)((mx - var5) / 26.0F);
         int var9 = (int)((my - var6 + this.scrollOffset) / 26.0F);
         int var10 = var9 * var4 + var8;
         if (var10 >= 0 && var10 < this.filteredOptions.size() && var8 >= 0 && var8 < var4) {
            BlockNotifier.BlockOption var11 = (BlockNotifier.BlockOption)this.filteredOptions.get(var10);
            boolean var12 = this.module.isBlockEnabled(var11.id());
            String var13 = var11.name()
               + (
                  var12
                     ? "  [ALERT ON]"
                     : "  [OFF]"
               );
            float var14 = SoftPalette.width(var13, 9.0F);
            float var15 = Math.min(this.px + this.pw - var14 - 14.0F, Math.max(this.px + 10.0F, mx + 10.0F));
            float var16 = Math.max(this.py + 6.0F, my - 18.0F);
            NanoVGRenderer.drawRoundedRect(
               var15 - 4.0F, var16 - 2.0F, var14 + 8.0F, 15.0F, 4.0F, SoftPalette.rgbaInt(SoftPalette.BODY_BOT, (int)(240.0F * alpha))
            );
            NanoVGRenderer.drawRoundedRectOutline(
               var15 - 4.0F,
               var16 - 2.0F,
               var14 + 8.0F,
               15.0F,
               4.0F,
               1.0F,
               var12 ? SoftPalette.mintInt((int)(180.0F * alpha)) : SoftPalette.whiteInt((int)(40.0F * alpha))
            );
            SoftPalette.label(var13, var15, var16 + 2.0F, 9.0F, var12 ? SoftPalette.mintInt((int)(255.0F * alpha)) : SoftPalette.textInt((int)(240.0F * alpha)));
         }
      }
   }

   @Override
   protected boolean mouseClickedContent(double mx, double my, int btn) {
      float var6 = this.footerY() + 4.0F;
      float var7 = this.px + this.pw - 18.0F - 82.0F;
      float var8 = var7 - 6.0F - 82.0F;
      float var9 = this.px + 18.0F;
      float var10 = 105.0F;
      if (hit(mx, my, var9, var6, var10, 26.0F)) {
         this.module.resetToDefaults();
         this.refreshFiltered(false);
         return true;
      }

      if (hit(mx, my, var8, var6, 82.0F, 26.0F)) {
         this.returnGUI();
         return true;
      }

      if (hit(mx, my, var7, var6, 82.0F, 26.0F)) {
         this.saveAndClose();
         return true;
      }

      float var11 = this.channelsY();
      float var12 = this.channelsH();
      if (my >= var11 && my <= var11 + var12) {
         float var13 = this.bodyX();
         float var14 = (this.bodyW() - 18.0F) / 4.0F;

         for (int var15 = 0; var15 < 4; var15++) {
            float var16 = var13 + var15 * (var14 + 6.0F);
            if (mx >= var16 && mx <= var16 + var14) {
               switch (var15) {
                  case 0:
                     this.module.getToastNotifySetting().toggle();
                     break;
                  case 1:
                     this.module.getSoundNotifySetting().toggle();
                     break;
                  case 2:
                     this.module.getTracersSetting().toggle();
                     break;
                  case 3:
                     this.module.getBoxSetting().toggle();
               }

               return true;
            }
         }
      }

      float var28 = this.paramsY();
      float var29 = this.paramsH();
      if (my >= var28 && my <= var28 + var29) {
         float var30 = this.bodyX();
         float var32 = 90.0F;
         if (hit(mx, my, var30, var28, var32, var29)) {
            double var36 = (Double)this.module.getRangeSetting().get();
            double var42 = var36 < 128.0 ? 128.0 : (var36 < 256.0 ? 256.0 : (var36 < 512.0 ? 512.0 : 64.0));
            this.module.getRangeSetting().set(var42);
            return true;
         }

         float var17 = var30 + var32 + 8.0F;
         float var18 = 105.0F;
         if (hit(mx, my, var17, var28, var18, var29)) {
            int var41 = (Integer)this.module.getCooldownSetting().get();
            int var46 = var41 == 0 ? 5 : (var41 == 5 ? 10 : (var41 == 10 ? 30 : (var41 == 30 ? 60 : 0)));
            this.module.getCooldownSetting().set(var46);
            return true;
         }

         float var19 = var17 + var18 + 8.0F;
         float var20 = 85.0F;
         if (hit(mx, my, var19, var28, var20, var29)) {
            int var49 = (Integer)this.module.getTracerDurationSetting().get();
            int var53 = var49 == 5 ? 10 : (var49 == 10 ? 15 : (var49 == 15 ? 30 : 5));
            this.module.getTracerDurationSetting().set(var53);
            return true;
         }
      }

      float var31 = this.filterBarY();
      float var33 = this.filterBarH();
      if (my >= var31 && my <= var31 + var33) {
         float var34 = this.bodyX();
         float var37 = 95.0F;
         if (hit(mx, my, var34, var31, var37, var33)) {
            return true;
         }

         float var39 = var34 + var37 + 8.0F;

         for (BlockNotifierFilterScreen.CategoryTab var23 : BlockNotifierFilterScreen.CategoryTab.values()) {
            String var24 = var23 == BlockNotifierFilterScreen.CategoryTab.ACTIVE ? "Active (" + this.module.getEnabledBlockTypeCount() + ")" : var23.label;
            float var25 = SoftPalette.widthBold(var24, 8.5F) + 12.0F;
            if (hit(mx, my, var39, var31, var25, var33)) {
               this.currentTab = var23;
               this.refreshFiltered(true);
               return true;
            }

            var39 += var25 + 4.0F;
         }

         float var44 = this.px + this.pw - 18.0F - 42.0F;
         float var47 = var44 - 42.0F - 5.0F;
         if (hit(mx, my, var47, var31, 42.0F, var33)) {
            List var52 = this.filteredOptions.stream().map(BlockNotifier.BlockOption::id).collect(Collectors.toList());
            this.module.setBlocksEnabled(var52, true);
            this.refreshFiltered(false);
            return true;
         }

         if (hit(mx, my, var44, var31, 42.0F, var33)) {
            List var51 = this.filteredOptions.stream().map(BlockNotifier.BlockOption::id).collect(Collectors.toList());
            this.module.setBlocksEnabled(var51, false);
            this.refreshFiltered(false);
            return true;
         }
      }

      int var35 = this.itemsPerRow();
      float var38 = this.bodyX();
      float var40 = this.contentY();
      float var45 = this.contentListH();

      for (int var48 = 0; var48 < this.filteredOptions.size(); var48++) {
         int var50 = var48 / var35;
         int var54 = var48 % var35;
         float var55 = var38 + var54 * 26.0F;
         float var56 = var40 + var50 * 26.0F - this.scrollOffset;
         if (!(var56 + 24.0F < var40) && !(var56 > var40 + var45) && hit(mx, my, var55, var56, 24.0F, 24.0F)) {
            BlockNotifier.BlockOption var26 = (BlockNotifier.BlockOption)this.filteredOptions.get(var48);
            boolean var27 = this.module.isBlockEnabled(var26.id());
            this.module.setBlockEnabled(var26.id(), !var27);
            if (this.currentTab == BlockNotifierFilterScreen.CategoryTab.ACTIVE) {
               this.refreshFiltered(false);
            }

            return true;
         }
      }

      return false;
   }

   public boolean method_25401(double mx, double my, double h, double v) {
      int var9 = this.itemsPerRow();
      int var10 = (this.filteredOptions.size() + var9 - 1) / var9;
      float var11 = Math.max(0.0F, var10 * 26.0F - this.contentListH());
      this.targetScroll = Math.max(0.0F, Math.min(var11, this.targetScroll - (float)v * 26.0F));
      this.lastScrollMs = System.currentTimeMillis();
      return true;
   }

   @Override
   protected boolean keyPressedContent(int kc, int sc, int mods) {
      if (kc == (256) || kc == (257)) {
         this.saveAndClose();
         return true;
      } else if (kc == (259) && !this.searchQuery.isEmpty()) {
         this.searchQuery = this.searchQuery.substring(0, this.searchQuery.length() - 1);
         this.refreshFiltered(true);
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
      this.refreshFiltered(true);
      return true;
   }

   private void saveAndClose() {
      if (VieClientMod.getInstance() != null && VieClientMod.getInstance().getConfigManager() != null) {
         VieClientMod.getInstance().getConfigManager().requestSave();
      }

      this.pageOpen = false;
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
      this.saveAndClose();
   }

   @Override
   protected boolean closesOnOutsideClick() {
      return false;
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

   public enum CategoryTab {
      ALL("All"),
      ORES("Ores"),
      BASES("Bases"),
      STORAGE("Storage"),
      ACTIVE("Active");

      public final String label;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      CategoryTab(String label) {
         this.label = labelx;
      }

      // $VF: synthetic method
      private static BlockNotifierFilterScreen.CategoryTab[] $values() {
         BlockNotifierFilterScreen.CategoryTab[] var10000 = new BlockNotifierFilterScreen.CategoryTab[5];
         var10000[0] = ALL;
         var10000[1] = ORES;
         var10000[2] = BASES;
         var10000[3] = STORAGE;
         var10000[4] = ACTIVE;
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
