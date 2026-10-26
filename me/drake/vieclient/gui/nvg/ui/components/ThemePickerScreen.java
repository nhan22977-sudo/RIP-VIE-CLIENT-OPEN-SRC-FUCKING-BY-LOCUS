package me.drake.vieclient.gui.nvg.ui.components;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.RainbowUtil;
import net.minecraft.class_2561;
import net.minecraft.class_437;

public class ThemePickerScreen extends VieOSScreen {
   public static final String[] NAMES = new String[]{
      "Obsidian",
      "Titanium",
      "Graphite",
      "Sapphire",
      "Aurora",
      "Emerald",
      "Ice",
      "Rose",
      "Amber",
      "Crimson",
      "Violet",
      "Gold",
      "Cyberpunk Neon",
      "Vaporwave",
      "Aurora Night",
      "Purple Void",
      "Sunset Dream",
      "Rainbow"
   };
   protected static final float CARD_W = 135.0F;
   protected static final float CARD_H = 72.0F;
   protected static final float GAP_X = 8.0F;
   protected static final float GAP_Y = 8.0F;
   protected static final int MAX_COLS = 3;
   protected final class_437 parent;
   protected int selectedIndex;
   private float scroll;
   private float targetScroll;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ThemePickerScreen(class_437 parent) {
      byte[] var10001 = new byte[12];
      var10001[0] = (byte)(125);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(112);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(48);
      var10001[5] = (byte)(17);
      var10001[6] = (byte)(-60);
      var10001[7] = (byte)(106);
      var10001[8] = (byte)(46);
      var10001[9] = (byte)(-57);
      var10001[10] = (byte)(-119);
      var10001[11] = (byte)(-109);
      super(class_2561.method_43470($$v_s(var10001, 1650494403400421387L)));
      this.selectedIndex = 0;
      this.scroll = 0.0F;
      this.targetScroll = 0.0F;
      this.parent = parentx;
      this.openScreen();
   }

   @Override
   protected float contentW(int sw) {
      int var2 = Math.max(1, Math.min(3, ThemeManager.THEMES.length));
      return Math.min(sw - 32.0F, 36.0F + var2 * 135.0F + (var2 - 1) * 8.0F);
   }

   @Override
   protected float contentH(int sw, int sh) {
      return Math.min(sh - 32.0F, 380.0F);
   }

   @Override
   protected String title() {
      byte[] var10000 = new byte[12];
      var10000[0] = (byte)(125);
      var10000[1] = (byte)(-1);
      var10000[2] = (byte)(112);
      var10000[3] = (byte)(-69);
      var10000[4] = (byte)(48);
      var10000[5] = (byte)(17);
      var10000[6] = (byte)(-60);
      var10000[7] = (byte)(106);
      var10000[8] = (byte)(46);
      var10000[9] = (byte)(-57);
      var10000[10] = (byte)(-119);
      var10000[11] = (byte)(-109);
      return $$v_s(var10000, 1650494403400421387L);
   }

   @Override
   protected String footerText() {
      byte[] var10000 = new byte[40];
      var10000[0] = (byte)(109);
      var10000[1] = (byte)(-10);
      var10000[2] = (byte)(117);
      var10000[3] = (byte)(-67);
      var10000[4] = (byte)(56);
      var10000[5] = (byte)(69);
      var10000[6] = (byte)(-123);
      var10000[7] = (byte)(30);
      var10000[8] = (byte)(50);
      var10000[9] = (byte)(-54);
      var10000[10] = (byte)(-127);
      var10000[11] = (byte)(-101);
      var10000[12] = (byte)(110);
      var10000[13] = (byte)(-99);
      var10000[14] = (byte)(-72);
      var10000[15] = (byte)(41);
      var10000[16] = (byte)(94);
      var10000[17] = (byte)(43);
      var10000[18] = (byte)(-68);
      var10000[19] = (byte)(-34);
      var10000[20] = (byte)(-113);
      var10000[21] = (byte)(-20);
      var10000[22] = (byte)(-108);
      var10000[23] = (byte)(78);
      var10000[24] = (byte)(84);
      var10000[25] = (byte)(-91);
      var10000[26] = (byte)(-76);
      var10000[27] = (byte)(102);
      var10000[28] = (byte)(-98);
      var10000[29] = (byte)(-66);
      var10000[30] = (byte)(-33);
      var10000[31] = (byte)(-106);
      var10000[32] = (byte)(-70);
      var10000[33] = (byte)(85);
      var10000[34] = (byte)(92);
      var10000[35] = (byte)(29);
      var10000[36] = (byte)(-33);
      var10000[37] = (byte)(-86);
      var10000[38] = (byte)(55);
      var10000[39] = (byte)(-69);
      return $$v_s(var10000, 1650494403400421387L);
   }

   @Override
   protected void tickAnimation(float delta) {
      int var2 = Math.max(1, ThemeManager.THEMES.length);
      int var3 = Math.max(1, Math.min(3, var2));
      int var4 = (var2 + var3 - 1) / var3;
      float var5 = var4 * 80.0F - 8.0F;
      float var6 = Math.max(0.0F, var5 - this.contentListH());
      this.targetScroll = Math.max(0.0F, Math.min(var6, this.targetScroll));
      this.scroll = AnimationManager.isOff() ? this.targetScroll : AnimationManager.damp(this.scroll, this.targetScroll, 20.0F, delta);
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.targetScroll -= (float)verticalAmount * 28.0F;
      return true;
   }

   @Override
   protected void renderContent(int mx, int my, float alpha) {
      int var4 = Math.max(1, ThemeManager.THEMES.length);
      int var5 = Math.max(1, Math.min(3, var4));
      float var6 = var5 * 135.0F + (var5 - 1) * 8.0F;
      float var7 = this.px + (this.pw - var6) / 2.0F;
      float var8 = this.contentY() - this.scroll;
      NanoVGRenderer.save();
      NanoVGRenderer.scissor(this.bodyX(), this.contentY(), this.bodyW(), this.contentListH());

      for (int var9 = 0; var9 < var4; var9++) {
         int var10 = var9 / var5;
         int var11 = var9 % var5;
         float var12 = var7 + var11 * 143.0F;
         float var13 = var8 + var10 * 80.0F;
         if (!(var13 + 72.0F < this.contentY()) && !(var13 > this.contentY() + this.contentListH())) {
            float var14 = this.rowEntrance(var9);
            if (var14 < 0.999F) {
               NanoVGRenderer.globalAlpha(alpha * var14);
               this.drawSwatch(var12, var13, var9, mx, my, 1.0F);
               NanoVGRenderer.globalAlpha(1.0F);
            } else {
               this.drawSwatch(var12, var13, var9, mx, my, alpha);
            }
         }
      }

      NanoVGRenderer.resetScissor();
      NanoVGRenderer.restore();
   }

   private void drawSwatch(float sx, float sy, int idx, int mx, int my, float alpha) {
      int[] var7;
      if (idx == 17) {
         int var8 = RainbowUtil.rainbow(1.0, 0.88, 0L, 255);
         int var9 = RainbowUtil.rainbow(1.0, 0.88, 600L, 255);
         var7 = new int[]{var8 >> 16 & 0xFF, var8 >> 8 & 0xFF, var8 & 0xFF, var9 >> 16 & 0xFF, var9 >> 8 & 0xFF, var9 & 0xFF};
      } else {
         var7 = ThemeManager.THEMES[Math.max(0, Math.min(ThemeManager.THEMES.length - 1, idx))];
      }

      boolean var18 = hit(mx, my, sx, sy, 135.0F, 72.0F);
      boolean var19 = idx == this.selectedIndex || this.selectedIndex < 0 && ThemeManager.selectedTheme == idx;
      float var10 = var18 && !var19 ? -2.0F : 0.0F;
      float var11 = sx;
      float var12 = sy + var10;
      NanoVGRenderer.drawRoundedRect(var11, var12, 135.0F, 72.0F, 6.0F, SoftPalette.clamp(55.0F * alpha) << 24);
      float var13 = 67.5F;
      int var14 = SoftPalette.clamp(245.0F * alpha);
      NanoVGRenderer.drawRoundedRectVarying(var11, var12, var13, 72.0F, 6.0F, 0.0F, 0.0F, 6.0F, SoftPalette.rgbaInt(var7[3], var7[4], var7[5], var14));
      NanoVGRenderer.drawRoundedRectVarying(var11 + var13, var12, var13, 72.0F, 0.0F, 6.0F, 6.0F, 0.0F, SoftPalette.rgbaInt(var7[0], var7[1], var7[2], var14));
      NanoVGRenderer.drawRect(var11 + 4.0F, var12 + 1.5F, 127.0F, 1.0F, SoftPalette.whiteInt(40.0F * alpha));
      if (var19) {
         NanoVGRenderer.drawRoundedRectOutline(var11 - 2.0F, var12 - 2.0F, 139.0F, 76.0F, 6.0F, 2.0F, SoftPalette.mintInt(255.0F * alpha));
      } else if (var18) {
         NanoVGRenderer.drawRoundedRectOutline(var11 - 1.5F, var12 - 1.5F, 138.0F, 75.0F, 6.0F, 1.5F, SoftPalette.whiteInt(80.0F * alpha));
      } else {
         NanoVGRenderer.drawRoundedRectOutline(var11, var12, 135.0F, 72.0F, 6.0F, 1.0F, SoftPalette.whiteInt(14.0F * alpha));
      }

      float var15 = 20.0F;
      NanoVGRenderer.drawRect(var11, var12 + 72.0F - var15, 135.0F, var15, SoftPalette.clamp(140.0F * alpha) << 24);
      String var16 = idx < NAMES.length ? NAMES[idx] : "Theme " + idx;
      float var17 = SoftPalette.widthBold(var16, 8.5F);
      SoftPalette.labelBold(
         var16,
         var11 + (135.0F - var17) / 2.0F,
         var12 + 72.0F - var15 + (var15 - 8.5F) / 2.0F,
         8.5F,
         var19 ? SoftPalette.textInt(255.0F * alpha) : SoftPalette.textInt(225.0F * alpha)
      );
   }

   @Override
   protected boolean mouseClickedContent(double mx, double my, int btn) {
      if (btn != 0) {
         return false;
      }

      int var6 = Math.max(1, ThemeManager.THEMES.length);
      int var7 = Math.max(1, Math.min(3, var6));
      float var8 = var7 * 135.0F + (var7 - 1) * 8.0F;
      float var9 = this.px + (this.pw - var8) / 2.0F;
      float var10 = this.contentY() - this.scroll;

      for (int var11 = 0; var11 < var6; var11++) {
         int var12 = var11 / var7;
         int var13 = var11 % var7;
         float var14 = var9 + var13 * 143.0F;
         float var15 = var10 + var12 * 80.0F;
         if (!(var15 + 72.0F < this.contentY()) && !(var15 > this.contentY() + this.contentListH()) && hit(mx, my, var14, var15, 135.0F, 72.0F)) {
            this.applySelection(var11);
            this.closeScreen();
            if (this.field_22787 != null) {
               this.field_22787.method_1507(this.parent);
            }

            return true;
         }
      }

      return false;
   }

   protected void applySelection(int index) {
      ThemeManager.setTheme(index);
   }

   @Override
   protected boolean closesOnOutsideClick() {
      return true;
   }

   @Override
   protected boolean keyPressedContent(int kc, int sc, int mods) {
      return false;
   }

   @Override
   public void method_25419() {
      this.pageOpen = false;
      if (this.field_22787 != null) {
         this.field_22787.method_1507(this.parent);
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
