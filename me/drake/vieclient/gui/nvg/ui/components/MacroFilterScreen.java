package me.drake.vieclient.gui.nvg.ui.components;

import java.nio.charset.StandardCharsets;
import java.util.List;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.gui.nvg.adapter.setting.MacroSetting;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_2561;

public class MacroFilterScreen extends VieOSScreen {
   private static final float ADD_W = 84.0F;
   private static final float CANCEL_W = 88.0F;
   private static final float SAVE_W = 88.0F;
   private static final float CLEAR_W = 92.0F;
   private static final float DEL_W = 72.0F;
   private static final float CMD_H = 34.0F;
   private final MacroSetting setting;
   private String newCommand;
   private int cursorPos;
   private long lastBlink;
   private boolean cursorVisible;
   private float scroll;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MacroFilterScreen(MacroSetting setting) {
      byte[] var10001 = new byte[12];
      var10001[0] = (byte)(126);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(127);
      var10001[3] = (byte)(-84);
      var10001[4] = (byte)(41);
      var10001[5] = (byte)(-82);
      var10001[6] = (byte)(-121);
      var10001[7] = (byte)(99);
      var10001[8] = (byte)(50);
      var10001[9] = (byte)(-42);
      var10001[10] = (byte)(-117);
      var10001[11] = (byte)(-124);
      super(class_2561.method_43470($$v_s(var10001, 3441120647683756054L)));
      this.newCommand = "";
      this.cursorPos = 0;
      this.lastBlink = 0L;
      this.cursorVisible = true;
      this.scroll = 0.0F;
      this.setting = settingx;
      this.openScreen();
   }

   @Override
   protected float contentW(int sw) {
      return Math.min(sw - 32.0F, 440.0F);
   }

   @Override
   protected float contentH(int sw, int sh) {
      return Math.min(sh - 32.0F, 360.0F);
   }

   @Override
   protected String title() {
      return "Macro Editor: " + this.setting.getName();
   }

   @Override
   protected String footerText() {
      byte[] var10000 = new byte[30];
      var10000[0] = (byte)(118);
      var10000[1] = (byte)(-12);
      var10000[2] = (byte)(104);
      var10000[3] = (byte)(-69);
      var10000[4] = (byte)(52);
      var10000[5] = (byte)(-82);
      var10000[6] = (byte)(-74);
      var10000[7] = (byte)(104);
      var10000[8] = (byte)(123);
      var10000[9] = (byte)(-61);
      var10000[10] = (byte)(-128);
      var10000[11] = (byte)(-110);
      var10000[12] = (byte)(62);
      var10000[13] = (byte)(118);
      var10000[14] = (byte)(40);
      var10000[15] = (byte)(-56);
      var10000[16] = (byte)(67);
      var10000[17] = (byte)(106);
      var10000[18] = (byte)(-119);
      var10000[19] = (byte)(-3);
      var10000[20] = (byte)(-75);
      var10000[21] = (byte)(94);
      var10000[22] = (byte)(-26);
      var10000[23] = (byte)(56);
      var10000[24] = (byte)(-85);
      var10000[25] = (byte)(113);
      var10000[26] = (byte)(-8);
      var10000[27] = (byte)(41);
      var10000[28] = (byte)(-67);
      var10000[29] = (byte)(99);
      return $$v_s(var10000, 3441120647683756054L);
   }

   @Override
   protected void renderContent(int mx, int my, float alpha) {
      long var4 = System.currentTimeMillis();
      if (var4 - this.lastBlink > 530L) {
         this.cursorVisible = !this.cursorVisible;
         this.lastBlink = var4;
      }

      float var6 = this.bodyX();
      float var7 = this.bodyW() - 84.0F - 10.0F;
      float var8 = 32.0F;
      float var9 = this.actionY() + (this.actionH() - var8) / 2.0F;
      this.drawInputField(var6, var9, var7, var8, alpha);
      float var10 = var6 + var7 + 10.0F;
      drawAccentChip(var10, var9, 84.0F, var8, "Add", alpha);
      float var11 = this.bodyX();
      float var12 = this.contentY();
      float var13 = this.bodyW();
      float var14 = this.contentListH();
      NanoVGRenderer.drawRoundedRect(var11, var12, var13, var14, 6.0F, SoftPalette.white(5.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(var11, var12, var13, var14, 6.0F, 1.0F, SoftPalette.white(8.0F * alpha));
      List var15 = this.setting.getCommands();
      float var16 = Math.max(0.0F, var15.size() * 34.0F - var14);
      this.scroll = Math.max(0.0F, Math.min(var16, this.scroll));
      NanoVGRenderer.scissor(var11, var12, var13, var14);
      if (var15.isEmpty()) {
         String var17 = "No commands yet — type above and press Add";
         float var18 = SoftPalette.width(var17, 9.0F);
         SoftPalette.label(var17, var11 + (var13 - var18) / 2.0F, var12 + (var14 - 9.0F) / 2.0F, 9.0F, SoftPalette.textDim(200.0F * alpha));
      }

      for (int var21 = 0; var21 < var15.size(); var21++) {
         float var24 = var12 + var21 * 34.0F - this.scroll;
         if (!(var24 + 34.0F < var12) && !(var24 > var12 + var14)) {
            float var19 = this.rowEntrance(var21);
            if (var19 < 0.999F) {
               NanoVGRenderer.save();
               NanoVGRenderer.translate(0.0F, (1.0F - var19) * 6.0F);
               NanoVGRenderer.globalAlpha(alpha * var19);
               this.drawCommand(var11, var24, var13, var21 + 1 + ". " + (String)var15.get(var21), mx, my, 1.0F);
               NanoVGRenderer.globalAlpha(1.0F);
               NanoVGRenderer.restore();
            } else {
               this.drawCommand(var11, var24, var13, var21 + 1 + ". " + (String)var15.get(var21), mx, my, alpha);
            }
         }
      }

      NanoVGRenderer.resetScissor();
      if (var16 > 0.0F) {
         float var22 = Math.max(24.0F, var14 * (var14 / (var15.size() * 34.0F)));
         float var25 = var12 + (var14 - var22) * (this.scroll / var16);
         NanoVGRenderer.drawRoundedRect(var11 + var13 - 5.0F, var12 + 2.0F, 3.0F, var14 - 4.0F, 1.5F, SoftPalette.white(20.0F * alpha));
         NanoVGRenderer.drawRoundedRect(var11 + var13 - 5.0F, var25, 3.0F, var22, 1.5F, SoftPalette.mint(180.0F * alpha));
      }

      float var23 = this.footerY() + 3.0F;
      float var26 = this.px + this.pw - 18.0F - 88.0F;
      float var27 = var26 - 10.0F - 88.0F;
      float var20 = var27 - 10.0F - 92.0F;
      drawDangerChip(
         var20,
         var23,
         92.0F,
         28.0F,
         "Clear All",
         hit(mx, my, var20, var23, 92.0F, 28.0F),
         alpha
      );
      drawNeutralChip(
         var27, var23, 88.0F, 28.0F, "Cancel", hit(mx, my, var27, var23, 88.0F, 28.0F), alpha
      );
      drawAccentChip(var26, var23, 88.0F, 28.0F, "Save", alpha);
   }

   private void drawInputField(float ix, float iy, float iw, float ih, float alpha) {
      NanoVGRenderer.drawRoundedRect(ix, iy, iw, ih, 8.0F, SoftPalette.white(20.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(ix, iy, iw, ih, 8.0F, 1.0F, SoftPalette.mint(180.0F * alpha));
      String var6 = this.newCommand.substring(0, this.cursorPos) + (this.cursorVisible ? "|" : "") + this.newCommand.substring(this.cursorPos);
      SoftPalette.label(var6, ix + 14.0F, iy + (ih - 9.0F) / 2.0F, 9.0F, SoftPalette.text(255.0F * alpha));
   }

   private void drawCommand(float lx, float ly, float lw, String text, int mx, int my, float alpha) {
      float var8 = lx + lw - 72.0F - 6.0F;
      float var9 = var8 - lx - 6.0F;
      NanoVGRenderer.drawRoundedRect(lx + 4.0F, ly + 3.0F, var9, 28.0F, 4.0F, SoftPalette.white(6.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(lx + 4.0F, ly + 3.0F, var9, 28.0F, 4.0F, 1.0F, SoftPalette.white(10.0F * alpha));
      SoftPalette.label(text, lx + 14.0F, ly + 12.5F, 9.0F, SoftPalette.text(255.0F * alpha));
      float var10001 = ly + 3.0F;
      byte[] var10004 = new byte[6];
      var10004[0] = (byte)(119);
      var10004[1] = (byte)(-1);
      var10004[2] = (byte)(112);
      var10004[3] = (byte)(-69);
      var10004[4] = (byte)(50);
      var10004[5] = (byte)(-21);
      drawDangerChip(var8, var10001, 72.0F, 28.0F, $$v_s(var10004, 3441120647683756054L), hit(mx, my, var8, ly + 3.0F, 72.0F, 28.0F), alpha);
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
      float var18 = var17 - 10.0F - 92.0F;
      if (hit(mx, my, var10, var9, 84.0F, var8)) {
         this.addCommand();
         return true;
      }

      List var19 = this.setting.getCommands();

      for (int var20 = 0; var20 < var19.size(); var20++) {
         float var21 = var12 + var20 * 34.0F - this.scroll;
         if (!(var21 + 34.0F < var12) && !(var21 > var12 + var14)) {
            float var22 = var11 + var13 - 72.0F - 6.0F;
            if (hit(mx, my, var22, var21 + 3.0F, 72.0F, 28.0F)) {
               this.setting.removeCommand(var20);
               return true;
            }
         }
      }

      if (hit(mx, my, var16, var15, 88.0F, 28.0F)) {
         this.returnGUI();
         return true;
      } else if (hit(mx, my, var17, var15, 88.0F, 28.0F)) {
         this.returnGUI();
         return true;
      } else if (hit(mx, my, var18, var15, 92.0F, 28.0F)) {
         this.setting.clearCommands();
         return true;
      } else {
         return false;
      }
   }

   public boolean method_25401(double mx, double my, double h, double v) {
      this.scroll = Math.max(0.0F, this.scroll - (float)v * 18.0F);
      return true;
   }

   @Override
   protected boolean keyPressedContent(int kc, int sc, int mods) {
      this.cursorVisible = true;
      this.lastBlink = System.currentTimeMillis();
      if (kc == (256)) {
         this.returnGUI();
         return true;
      } else if (kc == (257) || kc == (335)) {
         this.addCommand();
         return true;
      } else if (kc == (259) && this.cursorPos > 0) {
         this.newCommand = this.newCommand.substring(0, this.cursorPos - 1) + this.newCommand.substring(this.cursorPos);
         this.cursorPos--;
         return true;
      } else if (kc == (262) && this.cursorPos < this.newCommand.length()) {
         this.cursorPos++;
         return true;
      } else if (kc == (263) && this.cursorPos > 0) {
         this.cursorPos--;
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

      this.newCommand = this.newCommand.substring(0, this.cursorPos) + chr + this.newCommand.substring(this.cursorPos);
      this.cursorPos++;
      this.cursorVisible = true;
      this.lastBlink = System.currentTimeMillis();
      return true;
   }

   @Override
   protected boolean closesOnOutsideClick() {
      return false;
   }

   private void addCommand() {
      if (!this.newCommand.trim().isEmpty()) {
         this.setting.addCommand(this.newCommand.trim());
         this.newCommand = "";
         this.cursorPos = 0;
      }
   }

   private void returnGUI() {
      if (this.field_22787 != null) {
         this.field_22787.method_1507(VieClient.INSTANCE.GUI);
      }
   }

   @Override
   public void method_25419() {
      this.pageOpen = false;
      this.returnGUI();
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
