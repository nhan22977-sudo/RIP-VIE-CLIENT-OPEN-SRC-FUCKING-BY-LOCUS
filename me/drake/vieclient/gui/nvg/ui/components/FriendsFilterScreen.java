package me.drake.vieclient.gui.nvg.ui.components;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.gui.nvg.adapter.setting.FriendsSetting;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.render.nvg.NVGIcons;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_2561;

public class FriendsFilterScreen extends VieOSScreen {
   private static final float ADD_W = 84.0F;
   private static final float CANCEL_W = 88.0F;
   private static final float SAVE_W = 88.0F;
   private static final float DEL_W = 26.0F;
   private final FriendsSetting setting;
   private final List friends;
   private String inputText;
   private boolean inputFocused;
   private float scrollPx;
   private float targetScrollPx;
   private long lastScrollMs;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FriendsFilterScreen(FriendsSetting setting) {
      byte[] var10001 = new byte[12];
      var10001[0] = (byte)(117);
      var10001[1] = (byte)(-24);
      var10001[2] = (byte)(117);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(-89);
      var10001[5] = (byte)(-126);
      var10001[6] = (byte)(-74);
      var10001[7] = (byte)(-20);
      var10001[8] = (byte)(23);
      var10001[9] = (byte)(-53);
      var10001[10] = (byte)(-105);
      var10001[11] = (byte)(-126);
      super(class_2561.method_43470($$v_s(var10001, -1961818532736675818L)));
      this.inputText = "";
      this.inputFocused = false;
      this.scrollPx = 0.0F;
      this.targetScrollPx = 0.0F;
      this.lastScrollMs = 0L;
      this.setting = settingx;
      this.friends = new ArrayList(settingx.getFriends());
      this.openScreen();
   }

   @Override
   protected float contentW(int sw) {
      return Math.min(sw - 32.0F, 420.0F);
   }

   @Override
   protected float contentH(int sw, int sh) {
      return Math.min(sh - 32.0F, 360.0F);
   }

   @Override
   protected String title() {
      byte[] var10000 = new byte[12];
      var10000[0] = (byte)(117);
      var10000[1] = (byte)(-24);
      var10000[2] = (byte)(117);
      var10000[3] = (byte)(-69);
      var10000[4] = (byte)(-89);
      var10000[5] = (byte)(-126);
      var10000[6] = (byte)(-74);
      var10000[7] = (byte)(-20);
      var10000[8] = (byte)(23);
      var10000[9] = (byte)(-53);
      var10000[10] = (byte)(-105);
      var10000[11] = (byte)(-126);
      return $$v_s(var10000, -1961818532736675818L);
   }

   @Override
   protected String footerText() {
      byte[] var10000 = new byte[41];
      var10000[0] = (byte)(103);
      var10000[1] = (byte)(-29);
      var10000[2] = (byte)(108);
      var10000[3] = (byte)(-69);
      var10000[4] = (byte)(-23);
      var10000[5] = (byte)(-121);
      var10000[6] = (byte)(-27);
      var10000[7] = (byte)(-94);
      var10000[8] = (byte)(58);
      var10000[9] = (byte)(-49);
      var10000[10] = (byte)(-127);
      var10000[11] = (byte)(-38);
      var10000[12] = (byte)(-79);
      var10000[13] = (byte)(78);
      var10000[14] = (byte)(-97);
      var10000[15] = (byte)(-47);
      var10000[16] = (byte)(16);
      var10000[17] = (byte)(57);
      var10000[18] = (byte)(-20);
      var10000[19] = (byte)(-21);
      var10000[20] = (byte)(23);
      var10000[21] = (byte)(98);
      var10000[22] = (byte)(-16);
      var10000[23] = (byte)(-18);
      var10000[24] = (byte)(-85);
      var10000[25] = (byte)(125);
      var10000[26] = (byte)(-26);
      var10000[27] = (byte)(102);
      var10000[28] = 0;
      var10000[29] = (byte)(42);
      var10000[30] = (byte)(-7);
      var10000[31] = (byte)(100);
      var10000[32] = (byte)(-89);
      var10000[33] = (byte)(85);
      var10000[34] = (byte)(92);
      var10000[35] = (byte)(31);
      var10000[36] = (byte)(89);
      var10000[37] = (byte)(54);
      var10000[38] = 0;
      var10000[39] = (byte)(66);
      var10000[40] = (byte)(-97);
      return $$v_s(var10000, -1961818532736675818L);
   }

   @Override
   protected void tickAnimation(float delta) {
      this.scrollPx = AnimationManager.isOff() ? this.targetScrollPx : AnimationManager.damp(this.scrollPx, this.targetScrollPx, 20.0F, delta);
   }

   @Override
   protected void renderContent(int mx, int my, float alpha) {
      float var4 = this.bodyX();
      float var5 = this.bodyW() - 84.0F - 10.0F;
      float var6 = 32.0F;
      float var7 = this.actionY() + (this.actionH() - var6) / 2.0F;
      this.drawInputField(var4, var7, var5, var6, mx, my, alpha);
      float var8 = var4 + var5 + 10.0F;
      drawAccentChip(var8, var7, 84.0F, var6, "ADD", alpha);
      float var9 = this.bodyX();
      float var10 = this.contentY();
      float var11 = this.bodyW();
      float var12 = this.contentListH();
      NanoVGRenderer.drawRoundedRect(var9, var10, var11, var12, 6.0F, SoftPalette.white(5.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(var9, var10, var11, var12, 6.0F, 1.0F, SoftPalette.white(8.0F * alpha));
      float var13 = Math.max(0.0F, this.friends.size() * 36.0F - var12);
      this.targetScrollPx = Math.max(0.0F, Math.min(var13, this.targetScrollPx));
      NanoVGRenderer.scissor(var9, var10, var11, var12);
      if (this.friends.isEmpty()) {
         String var14 = "No friends yet — type a name and press ADD";
         float var15 = SoftPalette.width(var14, 9.0F);
         SoftPalette.label(var14, var9 + (var11 - var15) / 2.0F, var10 + (var12 - 9.0F) / 2.0F, 9.0F, SoftPalette.textDim(200.0F * alpha));
      }

      for (int var17 = 0; var17 < this.friends.size(); var17++) {
         float var20 = var10 + var17 * 36.0F - this.scrollPx;
         if (!(var20 + 36.0F < var10) && !(var20 > var10 + var12)) {
            float var16 = this.rowEntrance(var17);
            if (var16 < 0.999F) {
               NanoVGRenderer.save();
               NanoVGRenderer.translate(0.0F, (1.0F - var16) * 6.0F);
               NanoVGRenderer.globalAlpha(alpha * var16);
               this.drawRow(var9, var20, var11, (String)this.friends.get(var17), mx, my, 1.0F);
               NanoVGRenderer.globalAlpha(1.0F);
               NanoVGRenderer.restore();
            } else {
               this.drawRow(var9, var20, var11, (String)this.friends.get(var17), mx, my, alpha);
            }
         }
      }

      NanoVGRenderer.resetScissor();
      if (var13 > 0.0F) {
         boolean var18 = Math.abs(this.targetScrollPx - this.scrollPx) > 0.5F || System.currentTimeMillis() - this.lastScrollMs < 700L;
         if (var18) {
            float var21 = Math.max(30.0F, var12 * (var12 / (this.friends.size() * 36.0F)));
            float var23 = var10 + (var12 - var21) * (this.scrollPx / var13);
            NanoVGRenderer.drawRoundedRect(var9 + var11 - 6.0F, var10 + 2.0F, 3.0F, var12 - 4.0F, 1.5F, SoftPalette.white(30.0F * alpha));
            NanoVGRenderer.drawRoundedRect(var9 + var11 - 6.0F, var23, 3.0F, var21, 1.5F, SoftPalette.mint(200.0F * alpha));
         }
      }

      float var19 = this.footerY() + 3.0F;
      float var22 = this.px + this.pw - 18.0F - 88.0F;
      float var24 = var22 - 10.0F - 88.0F;
      drawNeutralChip(
         var24, var19, 88.0F, 28.0F, "CANCEL", hit(mx, my, var24, var19, 88.0F, 28.0F), alpha
      );
      drawAccentChip(var22, var19, 88.0F, 28.0F, "SAVE", alpha);
   }

   private void drawInputField(float ix, float iy, float iw, float ih, int mx, int my, float alpha) {
      NanoVGRenderer.drawRoundedRect(ix, iy, iw, ih, 8.0F, this.inputFocused ? SoftPalette.white(20.0F * alpha) : SoftPalette.white(10.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(ix, iy, iw, ih, 8.0F, 1.0F, this.inputFocused ? SoftPalette.mint(180.0F * alpha) : SoftPalette.white(16.0F * alpha));
      boolean var8 = System.currentTimeMillis() / 530L % 2L == 0L;
      String var10000;
      if (this.inputText.isEmpty() && !this.inputFocused) {
         byte[] var10 = new byte[11];
         var10[0] = (byte)(99);
         var10[1] = (byte)(-10);
         var10[2] = (byte)(125);
         var10[3] = (byte)(-89);
         var10[4] = (byte)(-84);
         var10[5] = (byte)(-108);
         var10[6] = (byte)(-27);
         var10[7] = (byte)(-94);
         var10[8] = (byte)(58);
         var10[9] = (byte)(-49);
         var10[10] = (byte)(-127);
         var10000 = $$v_s(var10, -1961818532736675818L);
      } else {
         var10000 = this.inputText + (this.inputFocused && var8 ? "|" : "");
      }

      String var9 = var10000;
      SoftPalette.label(
         var9,
         ix + 14.0F,
         iy + (ih - 9.0F) / 2.0F,
         9.0F,
         this.inputText.isEmpty() && !this.inputFocused ? SoftPalette.textDim(200.0F * alpha) : SoftPalette.text(255.0F * alpha)
      );
      this.inputFocused = hit(mx, my, ix, iy, iw, ih);
   }

   private void drawRow(float lx, float ly, float lw, String name, int mx, int my, float alpha) {
      boolean var8 = hit(mx, my, lx, ly, lw, 36.0F);
      NanoVGRenderer.drawRoundedRect(lx + 4.0F, ly + 3.0F, lw - 8.0F, 30.0F, 6.0F, var8 ? SoftPalette.mint(20.0F * alpha) : SoftPalette.white(7.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(
         lx + 4.0F, ly + 3.0F, lw - 8.0F, 30.0F, 6.0F, 1.0F, var8 ? SoftPalette.mint(80.0F * alpha) : SoftPalette.white(10.0F * alpha)
      );
      SoftPalette.label(name, lx + 16.0F, ly + 13.25F, 9.5F, SoftPalette.text(255.0F * alpha));
      float var9 = lx + lw - 26.0F - 8.0F;
      float var10 = ly + 5.0F;
      boolean var11 = hit(mx, my, var9, var10, 26.0F, 26.0F);
      NanoVGRenderer.drawRoundedRect(
         var9,
         var10,
         26.0F,
         26.0F,
         5.0F,
         var11
            ? new Color(225, 60, 60, (int)(230.0F * alpha))
            : new Color(200, 55, 55, (int)(160.0F * alpha))
      );
      NanoVGRenderer.drawRoundedRectOutline(
         var9, var10, 26.0F, 26.0F, 5.0F, 1.0F, new Color(255, 130, 130, (int)(160.0F * alpha))
      );
      NVGIcons.cross(var9 + 13.0F, var10 + 13.0F, 5.0F, 1.5F, new Color(255, 255, 255, (int)(255.0F * alpha)));
   }

   @Override
   protected boolean mouseClickedContent(double mx, double my, int btn) {
      if (btn != 0) {
         return false;
      }

      float var6 = this.bodyX();
      float var7 = this.bodyW() - 84.0F - 10.0F;
      float var8 = 32.0F;
      float var9 = this.actionY() + (this.actionH() - var8) / 2.0F;
      float var10 = var6 + var7 + 10.0F;
      float var11 = this.bodyX();
      float var12 = this.contentY();
      float var13 = this.bodyW();
      float var14 = this.contentListH();
      float var15 = this.footerY() + 3.0F;
      float var16 = this.px + this.pw - 18.0F - 88.0F;
      float var17 = var16 - 10.0F - 88.0F;
      if (hit(mx, my, var6, var9, var7, var8)) {
         this.inputFocused = true;
         return true;
      }

      if (hit(mx, my, var10, var9, 84.0F, var8)) {
         this.addFriend();
         return true;
      }

      for (int var18 = 0; var18 < this.friends.size(); var18++) {
         float var19 = var12 + var18 * 36.0F - this.scrollPx;
         if (!(var19 + 36.0F < var12) && !(var19 > var12 + var14)) {
            float var20 = var11 + var13 - 26.0F - 8.0F;
            float var21 = var19 + 5.0F;
            if (hit(mx, my, var20, var21, 26.0F, 26.0F)) {
               this.friends.remove(var18);
               return true;
            }
         }
      }

      if (hit(mx, my, var16, var15, 88.0F, 28.0F)) {
         this.save();
         return true;
      } else if (hit(mx, my, var17, var15, 88.0F, 28.0F)) {
         this.returnGUI();
         return true;
      } else {
         return false;
      }
   }

   public boolean method_25401(double mx, double my, double h, double v) {
      this.targetScrollPx -= (float)v * 36.0F;
      this.lastScrollMs = System.currentTimeMillis();
      return true;
   }

   @Override
   protected boolean keyPressedContent(int kc, int sc, int mods) {
      if (kc == (256)) {
         this.save();
         return true;
      }

      if (this.inputFocused) {
         if (kc == (259) && !this.inputText.isEmpty()) {
            this.inputText = this.inputText.substring(0, this.inputText.length() - 1);
            return true;
         }

         if (kc == (257) || kc == (335)) {
            this.addFriend();
            return true;
         }
      }

      return false;
   }

   @Override
   protected boolean charTypedContent(char chr, int mods) {
      if (this.inputFocused && !Character.isISOControl(chr)) {
         this.inputText = this.inputText + chr;
         return true;
      } else {
         return false;
      }
   }

   @Override
   protected boolean closesOnOutsideClick() {
      return false;
   }

   private void addFriend() {
      if (!this.inputText.trim().isEmpty()) {
         this.friends.add(this.inputText.trim());
         this.inputText = "";
      }
   }

   private void save() {
      this.setting.setFriends(this.friends);
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
