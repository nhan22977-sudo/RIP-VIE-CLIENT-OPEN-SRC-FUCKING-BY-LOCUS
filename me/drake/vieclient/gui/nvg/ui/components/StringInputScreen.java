package me.drake.vieclient.gui.nvg.ui.components;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.gui.nvg.adapter.setting.StringSetting;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import org.lwjgl.glfw.GLFW;

public class StringInputScreen extends VieOSScreen {
   private static final float CANVAS_W = 460.0F;
   private static final float CANVAS_H = 168.0F;
   private final StringSetting setting;
   private String content;
   private int cursorPos;
   private int selStart = -1;
   private long lastBlink = 0L;
   private boolean cursorVisible = true;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public StringInputScreen(StringSetting setting) {
      super(class_2561.method_43473());
      this.setting = settingx;
      this.content = settingx.getValue() != null ? settingx.getValue() : "";
      this.cursorPos = this.content.length();
      this.openScreen();
   }

   @Override
   protected float contentW(int sw) {
      return 460.0F;
   }

   @Override
   protected float contentH(int sw, int sh) {
      return 168.0F;
   }

   @Override
   protected String title() {
      return this.setting.getName().toString();
   }

   @Override
   protected String footerText() {
      byte[] var10000 = new byte[38];
      var10000[0] = (byte)(110);
      var10000[1] = (byte)(-24);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-83);
      var10000[4] = (byte)(46);
      var10000[5] = (byte)(-110);
      var10000[6] = (byte)(-112);
      var10000[7] = (byte)(70);
      var10000[8] = (byte)(34);
      var10000[9] = (byte)(-57);
      var10000[10] = (byte)(-106);
      var10000[11] = (byte)(-42);
      var10000[12] = (byte)(113);
      var10000[13] = (byte)(5);
      var10000[14] = (byte)(-35);
      var10000[15] = (byte)(35);
      var10000[16] = (byte)(15);
      var10000[17] = (byte)(60);
      var10000[18] = (byte)(-87);
      var10000[19] = (byte)(-114);
      var10000[20] = (byte)(-51);
      var10000[21] = (byte)(-128);
      var10000[22] = (byte)(50);
      var10000[23] = (byte)(88);
      var10000[24] = (byte)(-90);
      var10000[25] = (byte)(87);
      var10000[26] = (byte)(-57);
      var10000[27] = (byte)(5);
      var10000[28] = (byte)(-11);
      var10000[29] = (byte)(78);
      var10000[30] = (byte)(-62);
      var10000[31] = (byte)(-128);
      var10000[32] = (byte)(-67);
      var10000[33] = (byte)(91);
      var10000[34] = (byte)(18);
      var10000[35] = (byte)(29);
      var10000[36] = (byte)(-40);
      var10000[37] = (byte)(126);
      return $$v_s(var10000, 60355372773527579L);
   }

   @Override
   protected void renderContent(int mx, int my, float alpha) {
      long var4 = System.currentTimeMillis();
      if (var4 - this.lastBlink > 530L) {
         this.cursorVisible = !this.cursorVisible;
         this.lastBlink = var4;
      }

      float var6 = this.bodyX();
      float var7 = this.bodyY() + 12.0F;
      float var8 = this.bodyW();
      float var9 = 32.0F;
      NanoVGRenderer.drawRoundedRect(var6, var7, var8, var9, 8.0F, SoftPalette.clamp(90.0F * alpha) << 24);
      NanoVGRenderer.drawRoundedRectOutline(var6, var7, var8, var9, 8.0F, 1.0F, SoftPalette.mintInt(180.0F * alpha));
      float var10 = var6 + 14.0F;
      float var11 = var7 + (var9 - 9.0F) / 2.0F;
      if (this.selStart != -1 && this.selStart != this.cursorPos) {
         int var19 = Math.min(this.selStart, this.cursorPos);
         int var13 = Math.max(this.selStart, this.cursorPos);
         String var14 = this.content.substring(0, var19);
         String var15 = this.content.substring(var19, var13);
         String var16 = this.content.substring(var13);
         float var17 = SoftPalette.width(var14, 9.0F);
         float var18 = SoftPalette.width(var15, 9.0F);
         SoftPalette.label(var14, var10, var11, 9.0F, SoftPalette.textInt(255.0F * alpha));
         NanoVGRenderer.drawRect(var10 + var17, var7 + 4.0F, var18, var9 - 8.0F, SoftPalette.mintInt(110.0F * alpha));
         SoftPalette.label(var15, var10 + var17, var11, 9.0F, SoftPalette.whiteInt(255.0F * alpha));
         SoftPalette.label(var16, var10 + var17 + var18, var11, 9.0F, SoftPalette.textInt(255.0F * alpha));
      } else {
         SoftPalette.label(this.content, var10, var11, 9.0F, SoftPalette.textInt(255.0F * alpha));
         if (this.cursorVisible) {
            float var12 = var10 + SoftPalette.width(this.content.substring(0, this.cursorPos), 9.0F);
            NanoVGRenderer.drawRect(var12, var7 + 5.0F, 1.0F, var9 - 10.0F, SoftPalette.mintInt(220.0F * alpha));
         }
      }

      float var20 = this.footerY() - 28.0F - 4.0F;
      float var21 = this.px + this.pw - 18.0F - 88.0F;
      float var22 = var21 - 10.0F - 88.0F;
      drawNeutralChip(
         var22, var20, 88.0F, 28.0F, "CANCEL", hit(mx, my, var22, var20, 88.0F, 28.0F), alpha
      );
      drawAccentChip(var21, var20, 88.0F, 28.0F, "SAVE", alpha);
   }

   @Override
   protected boolean mouseClickedContent(double mx, double my, int btn) {
      float var6 = this.footerY() - 28.0F - 4.0F;
      float var7 = this.px + this.pw - 18.0F - 88.0F;
      float var8 = var7 - 10.0F - 88.0F;
      if (hit(mx, my, var7, var6, 88.0F, 28.0F)) {
         this.save();
         return true;
      } else if (hit(mx, my, var8, var6, 88.0F, 28.0F)) {
         this.returnGUI();
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
   protected boolean keyPressedContent(int kc, int sc, int mods) {
      this.cursorVisible = true;
      this.lastBlink = System.currentTimeMillis();
      if (kc == 257 || kc == 335) {
         this.save();
         return true;
      }

      if (isCtrlPaste(kc)) {
         this.insert(class_310.method_1551().field_1774.method_1460());
         return true;
      }

      if (isCtrlCopy(kc)) {
         this.copySelection();
         return true;
      }

      if (isCtrlCut(kc)) {
         this.copySelection();
         this.deleteSelection();
         return true;
      }

      switch (kc) {
         case 259:
            if (this.selStart != -1 && this.selStart != this.cursorPos) {
               this.deleteSelection();
            } else if (this.cursorPos > 0) {
               this.content = this.content.substring(0, this.cursorPos - 1) + this.content.substring(this.cursorPos);
               this.cursorPos--;
            }

            this.selStart = -1;
            return true;
         case 260:
         case 264:
         case 265:
         case 266:
         case 267:
         default:
            return false;
         case 261:
            if (this.selStart != -1 && this.selStart != this.cursorPos) {
               this.deleteSelection();
            } else if (this.cursorPos < this.content.length()) {
               this.content = this.content.substring(0, this.cursorPos) + this.content.substring(this.cursorPos + 1);
            }

            this.selStart = -1;
            return true;
         case 262:
            this.selStart = (mods & 1) != 0 ? (this.selStart == -1 ? this.cursorPos : this.selStart) : -1;
            if (this.cursorPos < this.content.length()) {
               this.cursorPos++;
            }

            return true;
         case 263:
            this.selStart = (mods & 1) != 0 ? (this.selStart == -1 ? this.cursorPos : this.selStart) : -1;
            if (this.cursorPos > 0) {
               this.cursorPos--;
            }

            return true;
         case 268:
            this.selStart = (mods & 1) != 0 ? (this.selStart == -1 ? this.cursorPos : this.selStart) : -1;
            this.cursorPos = 0;
            return true;
         case 269:
            this.selStart = (mods & 1) != 0 ? (this.selStart == -1 ? this.cursorPos : this.selStart) : -1;
            this.cursorPos = this.content.length();
            return true;
      }
   }

   @Override
   protected boolean charTypedContent(char chr, int mods) {
      if (Character.isISOControl(chr)) {
         return false;
      }

      this.insert(String.valueOf(chr));
      return true;
   }

   private void insert(String text) {
      if (this.selStart != (-1) && this.selStart != this.cursorPos) {
         this.deleteSelection();
      }

      this.content = this.content.substring(0, this.cursorPos) + text + this.content.substring(this.cursorPos);
      this.cursorPos = this.cursorPos + text.length();
      this.selStart = -1;
      this.cursorVisible = true;
      this.lastBlink = System.currentTimeMillis();
   }

   private void deleteSelection() {
      int var1 = Math.min(this.selStart, this.cursorPos);
      int var2 = Math.max(this.selStart, this.cursorPos);
      this.content = this.content.substring(0, var1) + this.content.substring(var2);
      this.cursorPos = var1;
      this.selStart = -1;
   }

   private void copySelection() {
      if (this.selStart != (-1) && this.selStart != this.cursorPos && this.field_22787 != null) {
         int var1 = Math.min(this.selStart, this.cursorPos);
         int var2 = Math.max(this.selStart, this.cursorPos);
         GLFW.glfwSetClipboardString(this.field_22787.method_22683().method_4490(), this.content.substring(var1, var2));
      }
   }

   private void save() {
      this.setting.setValue(this.content.trim());
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

   private static boolean isCtrlPaste(int kc) {
      return kc == (86) && ctrl();
   }

   private static boolean isCtrlCopy(int kc) {
      return kc == (67) && ctrl();
   }

   private static boolean isCtrlCut(int kc) {
      return kc == (88) && ctrl();
   }

   private static boolean ctrl() {
      long var0 = class_310.method_1551().method_22683().method_4490();
      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(81);
      var10000[1] = (byte)(-23);
      var10000[2] = (byte)(50);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(60);
      var10000[5] = (byte)(-33);
      var10000[6] = (byte)(-80);
      String var3 = System.getProperty($$v_s(var10000, 60355372773527579L)).toLowerCase();
      byte[] var10001 = new byte[3];
      var10001[0] = (byte)(83);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(127);
      int var2 = var3.contains($$v_s(var10001, 60355372773527579L)) ? 343 : 341;
      return GLFW.glfwGetKey(var0, var2) == 1;
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
