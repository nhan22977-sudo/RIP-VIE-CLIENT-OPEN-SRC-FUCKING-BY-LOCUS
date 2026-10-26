package me.drake.vieclient.gui.nvg.ui.components;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.modules.donut.TunnelBaseFinder;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_11909;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_332;

public class TunnelFilterScreen extends VieOSScreen {
   private static final float ROW_H = 30.0F;
   private static final float ROW_GAP = 3.0F;
   private static final float TOG_W = 26.0F;
   private static final float TOG_H = 14.0F;
   private static final int MAX_THRESHOLD = 100;
   private static final float CHIP_H_ACT = 22.0F;
   private final TunnelBaseFinder module;
   private final List entries;
   private final float[] toggleAnim;
   private int draggingEntry;
   private float scroll;
   private float targetScroll;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public TunnelFilterScreen(TunnelBaseFinder module) {
      byte[] var10001 = new byte[13];
      var10001[0] = (byte)(97);
      var10001[1] = (byte)(-17);
      var10001[2] = (byte)(114);
      var10001[3] = (byte)(-80);
      var10001[4] = (byte)(57);
      var10001[5] = (byte)(-35);
      var10001[6] = (byte)(-18);
      var10001[7] = (byte)(-50);
      var10001[8] = (byte)(52);
      var10001[9] = (byte)(-50);
      var10001[10] = (byte)(-112);
      var10001[11] = (byte)(-109);
      var10001[12] = (byte)(118);
      super(class_2561.method_43470($$v_s(var10001, -6859703626828099568L)));
      this.entries = new ArrayList();
      this.draggingEntry = -1;
      this.scroll = 0.0F;
      this.targetScroll = 0.0F;
      this.module = modulex;
      this.buildEntries();
      this.toggleAnim = new float[this.entries.size()];

      for (int var2 = 0; var2 < this.entries.size(); var2++) {
         this.toggleAnim[var2] = this.isTypeEnabled(((TunnelFilterScreen.FilterEntry)this.entries.get(var2)).type) ? 1.0F : 0.0F;
      }

      this.openScreen();
   }

   private void buildEntries() {
      this.entries
         .add(
            new TunnelFilterScreen.FilterEntry(
               "Chests",
               TunnelFilterScreen.FilterType.CHESTS,
               new class_1799(class_1802.field_8106)
            )
         );
      this.entries
         .add(
            new TunnelFilterScreen.FilterEntry(
               "Barrels",
               TunnelFilterScreen.FilterType.BARRELS,
               new class_1799(class_1802.field_16307)
            )
         );
      this.entries
         .add(
            new TunnelFilterScreen.FilterEntry(
               "Shulker Boxes",
               TunnelFilterScreen.FilterType.SHULKERS,
               new class_1799(class_1802.field_8545)
            )
         );
      this.entries
         .add(
            new TunnelFilterScreen.FilterEntry(
               "Hoppers",
               TunnelFilterScreen.FilterType.HOPPERS,
               new class_1799(class_1802.field_8239)
            )
         );
      this.entries
         .add(
            new TunnelFilterScreen.FilterEntry(
               "Furnaces",
               TunnelFilterScreen.FilterType.FURNACES,
               new class_1799(class_1802.field_8732)
            )
         );
      this.entries
         .add(
            new TunnelFilterScreen.FilterEntry(
               "Dispensers",
               TunnelFilterScreen.FilterType.DISPENSERS,
               new class_1799(class_1802.field_8357)
            )
         );
      this.entries
         .add(
            new TunnelFilterScreen.FilterEntry(
               "Crafters",
               TunnelFilterScreen.FilterType.CRAFTERS,
               new class_1799(class_1802.field_46791)
            )
         );
      this.entries
         .add(
            new TunnelFilterScreen.FilterEntry(
               "Spawners",
               TunnelFilterScreen.FilterType.SPAWNERS,
               new class_1799(class_1802.field_8849)
            )
         );
   }

   @Override
   protected float contentW(int sw) {
      return Math.min(sw - 32.0F, 430.0F);
   }

   @Override
   protected float contentH(int sw, int sh) {
      return Math.min(sh - 32.0F, 370.0F);
   }

   @Override
   protected String title() {
      byte[] var10000 = new byte[20];
      var10000[0] = (byte)(97);
      var10000[1] = (byte)(-17);
      var10000[2] = (byte)(114);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(57);
      var10000[5] = (byte)(-35);
      var10000[6] = (byte)(-18);
      var10000[7] = (byte)(-50);
      var10000[8] = (byte)(52);
      var10000[9] = (byte)(-52);
      var10000[10] = (byte)(-128);
      var10000[11] = (byte)(-109);
      var10000[12] = (byte)(118);
      var10000[13] = (byte)(73);
      var10000[14] = (byte)(-96);
      var10000[15] = (byte)(-103);
      var10000[16] = (byte)(9);
      var10000[17] = (byte)(62);
      var10000[18] = (byte)(-87);
      var10000[19] = (byte)(-36);
      return $$v_s(var10000, -6859703626828099568L);
   }

   @Override
   protected String footerText() {
      byte[] var10000 = new byte[26];
      var10000[0] = (byte)(118);
      var10000[1] = (byte)(-14);
      var10000[2] = (byte)(125);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(59);
      var10000[5] = (byte)(-44);
      var10000[6] = (byte)(-67);
      var10000[7] = (byte)(-88);
      var10000[8] = (byte)(46);
      var10000[9] = (byte)(-61);
      var10000[10] = (byte)(-110);
      var10000[11] = (byte)(-109);
      var10000[12] = (byte)(36);
      var10000[13] = (byte)(8);
      var10000[14] = (byte)(-109);
      var10000[15] = (byte)(-124);
      var10000[16] = (byte)(10);
      var10000[17] = (byte)(39);
      var10000[18] = (byte)(-83);
      var10000[19] = (byte)(-38);
      var10000[20] = (byte)(-123);
      var10000[21] = (byte)(34);
      var10000[22] = (byte)(-1);
      var10000[23] = (byte)(-76);
      var10000[24] = (byte)(-31);
      var10000[25] = (byte)(107);
      return $$v_s(var10000, -6859703626828099568L);
   }

   @Override
   protected void tickAnimation(float delta) {
      for (int var2 = 0; var2 < this.entries.size(); var2++) {
         float var3 = this.isTypeEnabled(((TunnelFilterScreen.FilterEntry)this.entries.get(var2)).type) ? 1.0F : 0.0F;
         this.toggleAnim[var2] = AnimationManager.isOff() ? var3 : AnimationManager.damp(this.toggleAnim[var2], var3, 16.0F, delta);
      }

      float var4 = this.entries.size() * 30.0F + (this.entries.size() - 1) * 3.0F;
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
      long var4 = this.entries.stream().filter(e -> this.isTypeEnabled(e.type)).count();
      String var6 = var4 + "/" + this.entries.size() + " ENABLED";
      float var7 = SoftPalette.widthBold(var6, 8.5F);
      float var8 = var7 + 16.0F;
      float var9 = this.px + this.pw - 18.0F - var8;
      float var10 = this.py + 9.0F;
      NanoVGRenderer.drawRoundedRectGradient(var9, var10, var8, 18.0F, 5.0F, SoftPalette.mintInt(40.0F * alpha), SoftPalette.mintDeepInt(40.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(var9, var10, var8, 18.0F, 5.0F, 1.0F, SoftPalette.mintInt(140.0F * alpha));
      SoftPalette.labelBold(var6, var9 + (var8 - var7) / 2.0F, var10 + 4.75F, 8.5F, SoftPalette.mintInt(255.0F * alpha));
   }

   @Override
   protected void drawContextContent(class_332 ctx, int mouseX, int mouseY, float delta) {
   }

   @Override
   protected void renderContent(int mx, int my, float alpha) {
      float var4 = this.actionY();
      SoftPalette.label(
         "Configure disconnect thresholds",
         this.bodyX(),
         var4 + (this.actionH() - 8.5F) / 2.0F,
         8.5F,
         SoftPalette.textDimInt(200.0F * alpha)
      );
      float var5 = 68.0F;
      float var6 = 62.0F;
      float var7 = 6.0F;
      float var8 = this.px + this.pw - 18.0F - var5;
      float var9 = var8 - var7 - var6;
      float var10 = var4 + (this.actionH() - 22.0F) / 2.0F;
      drawAccentChip(var9, var10, var6, 22.0F, "ALL ON", alpha);
      drawNeutralChip(
         var8, var10, var5, 22.0F, "ALL OFF", hit(mx, my, var8, var10, var5, 22.0F), alpha
      );
      NanoVGRenderer.save();
      NanoVGRenderer.scissor(this.bodyX(), this.contentY(), this.bodyW(), this.contentListH());
      float var11 = this.contentY() - this.scroll;

      for (int var12 = 0; var12 < this.entries.size(); var12++) {
         TunnelFilterScreen.FilterEntry var13 = (TunnelFilterScreen.FilterEntry)this.entries.get(var12);
         if (var11 + 30.0F >= this.contentY() && var11 <= this.contentY() + this.contentListH()) {
            boolean var14 = this.isTypeEnabled(var13.type);
            float var15 = this.toggleAnim[var12];
            float var16 = this.rowEntrance(var12);
            if (var16 < 0.999F) {
               NanoVGRenderer.globalAlpha(alpha * var16);
               this.drawRow(var12, var13, var11, var14, var15, mx, my, 1.0F);
               NanoVGRenderer.globalAlpha(1.0F);
            } else {
               this.drawRow(var12, var13, var11, var14, var15, mx, my, alpha);
            }
         }

         var11 += 33.0F;
      }

      NanoVGRenderer.resetScissor();
      NanoVGRenderer.restore();
   }

   private void drawRow(int i, TunnelFilterScreen.FilterEntry entry, float entryY, boolean enabled, float anim, int mx, int my, float alpha) {
      float var9 = this.bodyX();
      float var10 = this.bodyW();
      boolean var11 = hit(mx, my, var9, entryY, var10, 30.0F);
      NanoVGRenderer.drawRoundedRect(var9, entryY, var10, 30.0F, 6.0F, var11 ? SoftPalette.mintInt(20.0F * alpha) : SoftPalette.clamp(50.0F * alpha) << 24);
      if (enabled) {
         NanoVGRenderer.drawRoundedRectOutline(var9, entryY, var10, 30.0F, 6.0F, 1.0F, SoftPalette.mintInt(80.0F * alpha));
      } else {
         NanoVGRenderer.drawRoundedRectOutline(var9, entryY, var10, 30.0F, 6.0F, 1.0F, SoftPalette.whiteInt(14.0F * alpha));
      }

      float var12 = var9 + 6.0F;
      float var13 = entryY + 4.0F;
      float var14 = 22.0F;
      NanoVGRenderer.drawRoundedRect(var12, var13, var14, var14, 4.0F, SoftPalette.clamp(40.0F * alpha) << 24);
      NanoVGRenderer.drawRoundedRectOutline(var12, var13, var14, var14, 4.0F, 1.0F, SoftPalette.whiteInt(20.0F * alpha));
      NVGImage.drawMCItem(entry.icon, var12 + 3.0F, var13 + 3.0F, 16.0F, alpha);
      SoftPalette.label(entry.name, var9 + 34.0F, entryY + 10.5F, 9.0F, enabled ? SoftPalette.textInt(255.0F * alpha) : SoftPalette.textDimInt(180.0F * alpha));
      if (entry.type != TunnelFilterScreen.FilterType.SPAWNERS) {
         float var15 = 28.0F;
         float var16 = 16.0F;
         float var17 = var9 + var10 - var15;
         float var18 = entryY + (30.0F - var16) / 2.0F;
         String var19 = String.valueOf(this.getThreshold(entry.type));
         float var20 = 8.0F;
         float var21 = SoftPalette.widthBold(var19, var20);
         NanoVGRenderer.drawRoundedRect(var17, var18, var15, var16, 4.0F, SoftPalette.clamp(90.0F * alpha) << 24);
         NanoVGRenderer.drawRoundedRectOutline(var17, var18, var15, var16, 4.0F, 1.0F, SoftPalette.whiteInt(20.0F * alpha));
         SoftPalette.labelBold(var19, var17 + (var15 - var21) / 2.0F, var18 + (var16 - var20) / 2.0F, var20, SoftPalette.textInt(255.0F * alpha));
         float var22 = 56.0F;
         float var23 = 4.0F;
         float var24 = var17 - var22 - 12.0F;
         float var25 = entryY + (30.0F - var23) / 2.0F;
         float var26 = this.getThreshold(entry.type) / 100.0F;
         float var27 = var23 / 2.0F;
         NanoVGRenderer.drawRoundedRect(var24, var25, var22, var23, var27, SoftPalette.clamp(110.0F * alpha) << 24);
         int var28 = Math.max(3, (int)(var26 * var22));
         NanoVGRenderer.drawRoundedRectGradient(var24, var25, var28, var23, var27, SoftPalette.mintInt(240.0F * alpha), SoftPalette.mintDeepInt(240.0F * alpha));
         NanoVGRenderer.drawCircle(var24 + var22 * var26, var25 + var23 / 2.0F, 4.0F, SoftPalette.clamp(255.0F * alpha) << 24 | 16777215);
         NanoVGRenderer.drawCircleOutline(var24 + var22 * var26, var25 + var23 / 2.0F, 4.0F, 1.0F, SoftPalette.clamp(160.0F * alpha) << 24);
         float var29 = var24 - 26.0F - 10.0F;
         float var30 = entryY + 8.0F;
         SoftPalette.toggle(var29, var30, 26.0F, 14.0F, anim, alpha);
      } else {
         float var31 = var9 + var10 - 26.0F - 4.0F;
         float var32 = entryY + 8.0F;
         SoftPalette.toggle(var31, var32, 26.0F, 14.0F, anim, alpha);
      }
   }

   @Override
   protected boolean mouseClickedContent(double mx, double my, int btn) {
      float var6 = this.actionY();
      float var7 = 68.0F;
      float var8 = 62.0F;
      float var9 = 6.0F;
      float var10 = this.px + this.pw - 18.0F - var7;
      float var11 = var10 - var9 - var8;
      float var12 = var6 + (this.actionH() - 22.0F) / 2.0F;
      if (hit(mx, my, var11, var12, var8, 22.0F)) {
         for (TunnelFilterScreen.FilterEntry var25 : this.entries) {
            this.setTypeEnabled(var25.type, true);
         }

         return true;
      } else if (!hit(mx, my, var10, var12, var7, 22.0F)) {
         float var22 = this.bodyX();
         float var24 = this.bodyW();
         float var15 = this.contentY() - this.scroll;

         for (int var16 = 0; var16 < this.entries.size(); var16++) {
            TunnelFilterScreen.FilterEntry var17 = (TunnelFilterScreen.FilterEntry)this.entries.get(var16);
            if (var15 + 30.0F >= this.contentY() && var15 <= this.contentY() + this.contentListH() && hit(mx, my, var22, var15, var24, 30.0F)) {
               if (var17.type != TunnelFilterScreen.FilterType.SPAWNERS) {
                  float var18 = 28.0F;
                  float var19 = var22 + var24 - var18;
                  float var20 = 56.0F;
                  float var21 = var19 - var20 - 12.0F;
                  if (mx >= var21 - 4.0F && mx <= var19 + var18 + 4.0F) {
                     this.draggingEntry = var16;
                     this.updateSliderFromMouse(var17.type, mx, var21, var20);
                     return true;
                  }
               }

               this.setTypeEnabled(var17.type, !this.isTypeEnabled(var17.type));
               return true;
            }

            var15 += 33.0F;
         }

         return false;
      } else {
         for (TunnelFilterScreen.FilterEntry var14 : this.entries) {
            this.setTypeEnabled(var14.type, false);
         }

         return true;
      }
   }

   public boolean method_25403(class_11909 click, double dx, double dy) {
      if (click.method_74245() != 0) {
         return false;
      } else if (this.draggingEntry >= 0 && this.draggingEntry < this.entries.size()) {
         float var6 = this.bodyX();
         float var7 = this.bodyW();
         float var8 = 28.0F;
         float var9 = var6 + var7 - var8;
         float var10 = 56.0F;
         float var11 = var9 - var10 - 12.0F;
         this.updateSliderFromMouse(((TunnelFilterScreen.FilterEntry)this.entries.get(this.draggingEntry)).type, click.comp_4798(), var11, var10);
         return true;
      } else {
         return false;
      }
   }

   public boolean method_25406(class_11909 click) {
      if (click.method_74245() == 0 && this.draggingEntry >= 0) {
         this.draggingEntry = -1;
         return true;
      } else {
         return false;
      }
   }

   private void updateSliderFromMouse(TunnelFilterScreen.FilterType type, double mx, float sliderX, float sliderW) {
      float var6 = (float)((mx - sliderX) / sliderW);
      var6 = Math.max(0.0F, Math.min(1.0F, var6));
      int var7 = Math.round(var6 * 100.0F);
      this.setThreshold(type, var7);
   }

   @Override
   protected boolean keyPressedContent(int kc, int sc, int mods) {
      return false;
   }

   @Override
   protected boolean closesOnOutsideClick() {
      return false;
   }

   @Override
   protected boolean hasDrawContextContent() {
      return false;
   }

   private boolean isTypeEnabled(TunnelFilterScreen.FilterType type) {
      return switch (type) {
         case CHESTS -> this.module.chests.get();
         case BARRELS -> this.module.barrels.get();
         case SHULKERS -> this.module.shulkers.get();
         case HOPPERS -> this.module.hoppers.get();
         case FURNACES -> this.module.furnaces.get();
         case DISPENSERS -> this.module.dispensers.get();
         case CRAFTERS -> this.module.crafters.get();
         case SPAWNERS -> this.module.disconnectSpawner.get();
      };
   }

   private void setTypeEnabled(TunnelFilterScreen.FilterType type, boolean enabled) {
      switch (type) {
         case CHESTS:
            this.module.chests.set(enabled);
            break;
         case BARRELS:
            this.module.barrels.set(enabled);
            break;
         case SHULKERS:
            this.module.shulkers.set(enabled);
            break;
         case HOPPERS:
            this.module.hoppers.set(enabled);
            break;
         case FURNACES:
            this.module.furnaces.set(enabled);
            break;
         case DISPENSERS:
            this.module.dispensers.set(enabled);
            break;
         case CRAFTERS:
            this.module.crafters.set(enabled);
            break;
         case SPAWNERS:
            this.module.disconnectSpawner.set(enabled);
      }
   }

   private int getThreshold(TunnelFilterScreen.FilterType type) {
      return switch (type) {
         case CHESTS -> this.module.chestsThreshold.get();
         case BARRELS -> this.module.barrelsThreshold.get();
         case SHULKERS -> this.module.shulkersThreshold.get();
         case HOPPERS -> this.module.hoppersThreshold.get();
         case FURNACES -> this.module.furnacesThreshold.get();
         case DISPENSERS -> this.module.dispensersThreshold.get();
         case CRAFTERS -> this.module.craftersThreshold.get();
         case SPAWNERS -> 1;
      };
   }

   private void setThreshold(TunnelFilterScreen.FilterType type, int value) {
      value = Math.max(1, Math.min(100, value));
      switch (type) {
         case CHESTS:
            this.module.chestsThreshold.set(value);
            break;
         case BARRELS:
            this.module.barrelsThreshold.set(value);
            break;
         case SHULKERS:
            this.module.shulkersThreshold.set(value);
            break;
         case HOPPERS:
            this.module.hoppersThreshold.set(value);
            break;
         case FURNACES:
            this.module.furnacesThreshold.set(value);
            break;
         case DISPENSERS:
            this.module.dispensersThreshold.set(value);
            break;
         case CRAFTERS:
            this.module.craftersThreshold.set(value);
         case SPAWNERS:
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

   private static class FilterEntry {
      final String name;
      final TunnelFilterScreen.FilterType type;
      final class_1799 icon;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      FilterEntry(String name, TunnelFilterScreen.FilterType type, class_1799 icon) {
         this.name = namex;
         this.type = typex;
         this.icon = iconx;
      }
   }

   private enum FilterType {
      CHESTS,
      BARRELS,
      SHULKERS,
      HOPPERS,
      FURNACES,
      DISPENSERS,
      CRAFTERS,
      SPAWNERS;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TunnelFilterScreen.FilterType[] $values() {
         TunnelFilterScreen.FilterType[] var10000 = new TunnelFilterScreen.FilterType[8];
         var10000[0] = CHESTS;
         var10000[1] = BARRELS;
         var10000[2] = SHULKERS;
         var10000[3] = HOPPERS;
         var10000[4] = FURNACES;
         var10000[5] = DISPENSERS;
         var10000[6] = CRAFTERS;
         var10000[7] = SPAWNERS;
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
