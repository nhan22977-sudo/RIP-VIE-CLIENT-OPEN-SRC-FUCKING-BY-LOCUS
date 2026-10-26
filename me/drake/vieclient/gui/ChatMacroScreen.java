package me.drake.vieclient.gui;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.List;
import me.drake.vieclient.core.i18n.Localization;
import me.drake.vieclient.core.macro.ChatMacroSystem;
import me.drake.vieclient.gui.nvg.adapter.modules.client.Sounds;
import me.drake.vieclient.gui.nvg.ui.components.SoftPalette;
import me.drake.vieclient.gui.nvg.ui.components.VieOSScreen;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.KeyUtils;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;

public final class ChatMacroScreen extends VieOSScreen {
   private static final float ROW_H = 28.0F;
   private static final float CHIP_H = 18.0F;
   private static final float INPUT_H = 24.0F;
   private static final float GAP = 4.0F;
   private static final float ADD_W = 56.0F;
   private static final float DEL_W = 46.0F;
   private static final float EDIT_W = 40.0F;
   private static final float BIND_W = 58.0F;
   private String inputText;
   private int cursorPos;
   private int selStart;
   private boolean inputFocused;
   private boolean capturingBind;
   private int capturingIndex;
   private int editingIndex;
   private long lastBlink;
   private boolean cursorVisible;
   private float scroll;
   private final class_437 parent;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ChatMacroScreen() {
      this(null);
   }

   public ChatMacroScreen(class_437 parent) {
      byte[] var10001 = new byte[11];
      var10001[0] = (byte)(101);
      var10001[1] = (byte)(-14);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-86);
      var10001[4] = (byte)(100);
      var10001[5] = (byte)(28);
      var10001[6] = (byte)(-10);
      var10001[7] = (byte)(57);
      var10001[8] = (byte)(60);
      var10001[9] = (byte)(-51);
      var10001[10] = (byte)(-105);
      super(class_2561.method_43470($$v_s(var10001, 8256382334915563523L)));
      this.inputText = "";
      this.cursorPos = 0;
      this.selStart = -1;
      this.inputFocused = false;
      this.capturingBind = false;
      this.capturingIndex = -1;
      this.editingIndex = -1;
      this.lastBlink = 0L;
      this.cursorVisible = true;
      this.scroll = 0.0F;
      this.parent = parentx;
      ChatMacroSystem.INSTANCE.load();
      this.openScreen();
   }

   @Override
   protected float contentW(int sw) {
      return Math.min(sw - 32.0F, 460.0F);
   }

   @Override
   protected float contentH(int sw, int sh) {
      return Math.min(sh - 32.0F, 370.0F);
   }

   @Override
   protected String title() {
      String var1;
      if (Localization.isVietnamese()) {
         byte[] var10000 = new byte[25];
         var10000[0] = (byte)(118);
         var10000[1] = (byte)(-14);
         var10000[2] = (byte)(-33);
         var10000[3] = (byte)(115);
         var10000[4] = (byte)(41);
         var10000[5] = (byte)(113);
         var10000[6] = (byte)(-61);
         var10000[7] = (byte)(-69);
         var10000[8] = (byte)(-12);
         var10000[9] = (byte)(13);
         var10000[10] = (byte)(-112);
         var10000[11] = (byte)(-42);
         var10000[12] = (byte)(95);
         var10000[13] = (byte)(-31);
         var10000[14] = (byte)(-34);
         var10000[15] = (byte)(86);
         var10000[16] = (byte)(86);
         var10000[17] = (byte)(98);
         var10000[18] = (byte)(-127);
         var10000[19] = (byte)(-49);
         var10000[20] = (byte)(-105);
         var10000[21] = (byte)(-45);
         var10000[22] = (byte)(-88);
         var10000[23] = (byte)(121);
         var10000[24] = (byte)(-73);
         var1 = $$v_s(var10000, 8256382334915563523L);
      } else {
         byte[] var2 = new byte[11];
         var2[0] = (byte)(101);
         var2[1] = (byte)(-14);
         var2[2] = (byte)(125);
         var2[3] = (byte)(-86);
         var2[4] = (byte)(100);
         var2[5] = (byte)(28);
         var2[6] = (byte)(-10);
         var2[7] = (byte)(57);
         var2[8] = (byte)(60);
         var2[9] = (byte)(-51);
         var2[10] = (byte)(-105);
         var1 = $$v_s(var2, 8256382334915563523L);
      }

      return var1;
   }

   @Override
   protected String footerText() {
      return Localization.isVietnamese()
         ? "Bấm vào ô phím để gán (hỗ trợ cả chuột) · Phím phải để hủy gán"
         : "Click bind chip to set (mouse buttons supported) · Right-click to unbind";
   }

   @Override
   protected void renderHeaderExtras(int mx, int my, float alpha) {
      if (this.capturingBind) {
         boolean var4 = Localization.isVietnamese();
         String var5 = var4
            ? "Bấm phím bất kỳ hoặc nút chuột  ·  ESC để hủy"
            : "Press any key or mouse button  ·  ESC to cancel";
         float var6 = SoftPalette.width(var5, 10.0F);
         float var7 = this.px + (this.pw - var6) / 2.0F - 8.0F;
         float var8 = this.py + 9.0F;
         float var9 = var6 + 16.0F;
         NanoVGRenderer.drawRoundedRectGradient(var7, var8, var9, 18.0F, 5.0F, SoftPalette.mint(60.0F * alpha), SoftPalette.mintDeep(60.0F * alpha));
         NanoVGRenderer.drawRoundedRectOutline(var7, var8, var9, 18.0F, 5.0F, 1.0F, SoftPalette.mint(180.0F * alpha));
         SoftPalette.label(var5, var7 + (var9 - var6) / 2.0F, var8 + 4.0F, 10.0F, SoftPalette.mint(255.0F * alpha));
      }
   }

   @Override
   protected void renderContent(int mx, int my, float alpha) {
      boolean var4 = Localization.isVietnamese();
      List var5 = ChatMacroSystem.INSTANCE.macros();
      float var6 = this.bodyX();
      float var7 = this.contentY();
      float var8 = this.bodyW();
      float var9 = this.py + this.ph - 34.0F - 24.0F - 10.0F;
      float var10 = Math.max(20.0F, var9 - var7 - 6.0F);
      if (var5.isEmpty()) {
         String var11 = var4
            ? "Chưa có macro nào — nhập lệnh bên dưới và bấm Thêm"
            : "No macros yet — type a command below and click Add";
         float var12 = SoftPalette.width(var11, 9.0F);
         SoftPalette.label(var11, this.px + (this.pw - var12) / 2.0F, var7 + 24.0F, 9.0F, SoftPalette.textDim(200.0F * alpha));
      } else {
         for (int var17 = 0; var17 < var5.size(); var17++) {
            float var19 = var7 + var17 * 32.0F - this.scroll;
            if (!(var19 + 28.0F < var7) && !(var19 > var7 + var10)) {
               float var13 = this.rowEntrance(var17);
               if (var13 < 0.999F) {
                  NanoVGRenderer.save();
                  NanoVGRenderer.translate(0.0F, (1.0F - var13) * 6.0F);
                  NanoVGRenderer.globalAlpha(alpha * var13);
                  this.drawRow(var17, (ChatMacroSystem.Macro)var5.get(var17), var19, mx, my, 1.0F, var4);
                  NanoVGRenderer.globalAlpha(1.0F);
                  NanoVGRenderer.restore();
               } else {
                  this.drawRow(var17, (ChatMacroSystem.Macro)var5.get(var17), var19, mx, my, alpha, var4);
               }
            }
         }
      }

      NanoVGRenderer.drawRect(var6, var9, var8, 1.0F, SoftPalette.white(10.0F * alpha));
      String var18;
      if (this.editingIndex >= 0) {
         var18 = var4 ? "Đang sửa macro #" + (this.editingIndex + 1) + " (ESC để hủy):" : "Editing macro #" + (this.editingIndex + 1) + " (ESC to cancel):";
      } else {
         var18 = var4
            ? "Tạo macro mới:"
            : "New macro:";
      }

      SoftPalette.label(var18, var6, var9 + 6.0F, 8.5F, this.editingIndex >= 0 ? SoftPalette.mint(240.0F * alpha) : SoftPalette.textDim(220.0F * alpha));
      float var20 = var6;
      float var21 = var9 + 18.0F;
      float var14 = var8 - 56.0F - 4.0F;
      this.drawInputField(var20, var21, var14, 24.0F, alpha);
      this.inputFocused = hit(mx, my, var20, var21, var14, 24.0F);
      float var15 = var20 + var14 + 4.0F;
      String var16 = this.editingIndex >= 0
         ? (var4 ? "Lưu" : "Save")
         : (var4 ? "Thêm" : "Add");
      drawAccentChip(var15, var21, 56.0F, 24.0F, var16, alpha);
   }

   private void drawRow(int idx, ChatMacroSystem.Macro m, float ry, int mx, int my, float alpha, boolean vn) {
      float var8 = this.bodyX();
      float var9 = this.bodyW();
      boolean var10 = this.editingIndex == idx;
      NanoVGRenderer.drawRoundedRect(var8, ry, var9, 28.0F, 6.0F, var10 ? SoftPalette.mint(14.0F * alpha) : SoftPalette.white(8.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(var8, ry, var9, 28.0F, 6.0F, 1.0F, var10 ? SoftPalette.mint(120.0F * alpha) : SoftPalette.white(10.0F * alpha));
      float var11 = var8 + var9 - 46.0F - 6.0F;
      float var12 = var11 - 58.0F - 4.0F;
      float var13 = var12 - 40.0F - 4.0F;
      float var14 = var13 - var8 - 14.0F;
      String var15 = m.command();
      String var16 = var15;

      while (var16.length() > 4 && SoftPalette.width(var16 + "...", 10.0F) > var14) {
         var16 = var16.substring(0, var16.length() - 1);
      }

      if (!var16.equals(var15)) {
         var16 = var16 + "...";
      }

      SoftPalette.label(var16, var8 + 12.0F, ry + 9.0F, 10.0F, var10 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(255.0F * alpha));
      float var17 = ry + 5.0F;
      boolean var18 = this.capturingBind && this.capturingIndex == idx;
      drawNeutralChip(
         var13,
         var17,
         40.0F,
         18.0F,
         var10
            ? (vn ? "Hủy" : "Cancel")
            : (vn ? "Sửa" : "Edit"),
         var10,
         alpha
      );
      drawNeutralChip(var12, var17, 58.0F, 18.0F, keyLabel(m.keybind()), var18, alpha);
      drawDangerChip(
         var11,
         var17,
         46.0F,
         18.0F,
         vn ? "Xóa" : "Delete",
         hit(mx, my, var11, var17, 46.0F, 18.0F),
         alpha
      );
   }

   private void drawInputField(float ix, float iy, float iw, float ih, float alpha) {
      NanoVGRenderer.drawRoundedRect(ix, iy, iw, ih, 6.0F, new Color(0, 0, 0, (int)(90.0F * alpha)));
      NanoVGRenderer.drawRoundedRectOutline(ix, iy, iw, ih, 6.0F, 1.0F, this.inputFocused ? SoftPalette.mint(180.0F * alpha) : SoftPalette.white(18.0F * alpha));
      float var6 = ix + 10.0F;
      float var7 = iy + (ih - 9.0F) / 2.0F;
      if (this.selStart != (-1) && this.selStart != this.cursorPos) {
         int var15 = Math.min(this.selStart, this.cursorPos);
         int var9 = Math.max(this.selStart, this.cursorPos);
         String var10 = this.inputText.substring(0, var15);
         String var11 = this.inputText.substring(var15, var9);
         String var12 = this.inputText.substring(var9);
         float var13 = SoftPalette.width(var10, 9.0F);
         float var14 = SoftPalette.width(var11, 9.0F);
         SoftPalette.label(var10, var6, var7, 9.0F, SoftPalette.text(255.0F * alpha));
         NanoVGRenderer.drawRect(var6 + var13, iy + 4.0F, var14, ih - 8.0F, SoftPalette.mint(110.0F * alpha));
         SoftPalette.label(var11, var6 + var13, var7, 9.0F, new Color(255, 255, 255, (int)(255.0F * alpha)));
         SoftPalette.label(var12, var6 + var13 + var14, var7, 9.0F, SoftPalette.text(255.0F * alpha));
      } else {
         SoftPalette.label(this.inputText, var6, var7, 9.0F, SoftPalette.text(255.0F * alpha));
         if (this.inputFocused && this.cursorVisible) {
            float var8 = var6 + SoftPalette.width(this.inputText.substring(0, this.cursorPos), 9.0F);
            NanoVGRenderer.drawRect(var8, iy + 4.0F, 1.0F, ih - 8.0F, SoftPalette.text(255.0F * alpha));
         }
      }
   }

   @Override
   protected boolean mouseClickedContent(double mx, double my, int btn) {
      if (this.capturingBind) {
         if (btn == 1) {
            ChatMacroSystem.INSTANCE.updateKeybind(this.capturingIndex, -1);
            this.capturingBind = false;
            this.capturingIndex = -1;
            return true;
         }

         if (btn >= 2) {
            ChatMacroSystem.INSTANCE.updateKeybind(this.capturingIndex, 1000 + btn);
            this.capturingBind = false;
            this.capturingIndex = -1;
            return true;
         }

         this.capturingBind = false;
         this.capturingIndex = -1;
      }

      if (btn != 0) {
         return false;
      }

      float var6 = this.bodyX();
      float var7 = this.bodyW();
      float var8 = this.contentY();
      float var9 = this.py + this.ph - 34.0F - 24.0F - 10.0F;
      List var10 = ChatMacroSystem.INSTANCE.macros();

      for (int var11 = 0; var11 < var10.size(); var11++) {
         float var12 = var8 + var11 * 32.0F - this.scroll;
         if (!(var12 + 28.0F < var8) && !(var12 > var9)) {
            float var13 = var12 + 5.0F;
            float var14 = var6 + var7 - 46.0F - 6.0F;
            float var15 = var14 - 58.0F - 4.0F;
            float var16 = var15 - 40.0F - 4.0F;
            if (hit(mx, my, var16, var13, 40.0F, 18.0F)) {
               if (this.editingIndex == var11) {
                  this.editingIndex = -1;
                  this.inputText = "";
                  this.cursorPos = 0;
                  this.selStart = -1;
               } else {
                  this.editingIndex = var11;
                  this.inputText = ((ChatMacroSystem.Macro)var10.get(var11)).command();
                  this.cursorPos = this.inputText.length();
                  this.selStart = -1;
                  this.inputFocused = true;
               }

               Sounds.instance.playTap();
               return true;
            }

            if (hit(mx, my, var15, var13, 58.0F, 18.0F)) {
               this.capturingBind = true;
               this.capturingIndex = var11;
               Sounds.instance.playTap();
               return true;
            }

            if (hit(mx, my, var14, var13, 46.0F, 18.0F)) {
               if (this.editingIndex == var11) {
                  this.editingIndex = -1;
                  this.inputText = "";
                  this.cursorPos = 0;
                  this.selStart = -1;
               } else if (this.editingIndex > var11) {
                  this.editingIndex--;
               }

               ChatMacroSystem.INSTANCE.remove(var11);
               Sounds.instance.playTap();
               return true;
            }
         }
      }

      float var17 = this.bodyW();
      float var18 = this.bodyX();
      float var19 = var9 + 18.0F;
      float var20 = var17 - 56.0F - 4.0F;
      float var21 = var18 + var20 + 4.0F;
      if (hit(mx, my, var21, var19, 56.0F, 24.0F)) {
         this.submitInput();
         return true;
      } else {
         return false;
      }
   }

   private void submitInput() {
      String var1 = this.inputText.trim();
      if (!var1.isEmpty()) {
         if (this.editingIndex >= 0) {
            ChatMacroSystem.INSTANCE.updateCommand(this.editingIndex, var1);
            this.editingIndex = -1;
         } else {
            ChatMacroSystem.INSTANCE.add(var1, -1);
         }

         this.inputText = "";
         this.cursorPos = 0;
         this.selStart = -1;
         Sounds.instance.playTap();
      }
   }

   @Override
   protected boolean keyPressedContent(int kc, int sc, int mods) {
      this.cursorVisible = true;
      this.lastBlink = System.currentTimeMillis();
      if (this.capturingBind) {
         if (kc == 256) {
            this.capturingBind = false;
            this.capturingIndex = -1;
            return true;
         } else {
            ChatMacroSystem.INSTANCE.updateKeybind(this.capturingIndex, kc);
            this.capturingBind = false;
            this.capturingIndex = -1;
            return true;
         }
      } else if (kc == 256) {
         if (this.editingIndex >= 0) {
            this.editingIndex = -1;
            this.inputText = "";
            this.cursorPos = 0;
            this.selStart = -1;
            return true;
         } else {
            this.closeScreen();
            return true;
         }
      } else if (!this.inputFocused) {
         ClientSettings var9 = ClientSettings.get();
         int var12 = var9 != null ? var9.getClickGuiKey() : 344;
         if (kc != var12 && kc != 344) {
            return false;
         }

         this.method_25419();
         class_310 var14 = class_310.method_1551();
         if (var14 != null) {
            var14.method_1507(null);
         }

         return true;
      } else {
         if (kc == 65 && this.ctrl()) {
            this.selStart = 0;
            this.cursorPos = this.inputText.length();
            return true;
         }

         if (kc == 86 && this.ctrl()) {
            class_310 var8 = class_310.method_1551();
            String var11 = var8 != null ? var8.field_1774.method_1460() : "";
            if (!var11.isEmpty()) {
               this.insertText(var11);
            }

            return true;
         } else if (kc == 67 && this.ctrl()) {
            class_310 var7 = class_310.method_1551();
            if (this.selStart != -1 && this.selStart != this.cursorPos && var7 != null) {
               int var10 = Math.min(this.selStart, this.cursorPos);
               int var13 = Math.max(this.selStart, this.cursorPos);
               GLFW.glfwSetClipboardString(var7.method_22683().method_4490(), this.inputText.substring(var10, var13));
            }

            return true;
         } else if (kc == 88 && this.ctrl()) {
            class_310 var4 = class_310.method_1551();
            if (this.selStart != -1 && this.selStart != this.cursorPos && var4 != null) {
               int var5 = Math.min(this.selStart, this.cursorPos);
               int var6 = Math.max(this.selStart, this.cursorPos);
               GLFW.glfwSetClipboardString(var4.method_22683().method_4490(), this.inputText.substring(var5, var6));
               this.deleteSelection();
            }

            return true;
         } else {
            switch (kc) {
               case 257:
               case 335:
                  this.submitInput();
                  return true;
               case 259:
                  if (this.selStart != -1 && this.selStart != this.cursorPos) {
                     this.deleteSelection();
                  } else if (this.cursorPos > 0) {
                     this.inputText = this.inputText.substring(0, this.cursorPos - 1) + this.inputText.substring(this.cursorPos);
                     this.cursorPos--;
                  }

                  this.selStart = -1;
                  return true;
               case 261:
                  if (this.selStart != -1 && this.selStart != this.cursorPos) {
                     this.deleteSelection();
                  } else if (this.cursorPos < this.inputText.length()) {
                     this.inputText = this.inputText.substring(0, this.cursorPos) + this.inputText.substring(this.cursorPos + 1);
                  }

                  this.selStart = -1;
                  return true;
               case 262:
                  if ((mods & 1) != 0 && this.selStart == -1) {
                     this.selStart = this.cursorPos;
                  }

                  if (this.cursorPos < this.inputText.length()) {
                     this.cursorPos++;
                  }

                  if ((mods & 1) == 0) {
                     this.selStart = -1;
                  }

                  return true;
               case 263:
                  if ((mods & 1) != 0 && this.selStart == -1) {
                     this.selStart = this.cursorPos;
                  }

                  if (this.cursorPos > 0) {
                     this.cursorPos--;
                  }

                  if ((mods & 1) == 0) {
                     this.selStart = -1;
                  }

                  return true;
               case 268:
                  if ((mods & 1) != 0 && this.selStart == -1) {
                     this.selStart = this.cursorPos;
                  }

                  this.cursorPos = 0;
                  if ((mods & 1) == 0) {
                     this.selStart = -1;
                  }

                  return true;
               case 269:
                  if ((mods & 1) != 0 && this.selStart == -1) {
                     this.selStart = this.cursorPos;
                  }

                  this.cursorPos = this.inputText.length();
                  if ((mods & 1) == 0) {
                     this.selStart = -1;
                  }

                  return true;
               default:
                  return false;
            }
         }
      }
   }

   @Override
   protected boolean charTypedContent(char chr, int mods) {
      if (!this.inputFocused) {
         return false;
      }

      if (Character.isISOControl(chr)) {
         return false;
      }

      this.insertText(String.valueOf(chr));
      return true;
   }

   private void insertText(String text) {
      if (this.selStart != (-1) && this.selStart != this.cursorPos) {
         this.deleteSelection();
      }

      this.inputText = this.inputText.substring(0, this.cursorPos) + text + this.inputText.substring(this.cursorPos);
      this.cursorPos = this.cursorPos + text.length();
      this.selStart = -1;
   }

   private void deleteSelection() {
      if (this.selStart != (-1) && this.selStart != this.cursorPos) {
         int var1 = Math.min(this.selStart, this.cursorPos);
         int var2 = Math.max(this.selStart, this.cursorPos);
         this.inputText = this.inputText.substring(0, var1) + this.inputText.substring(var2);
         this.cursorPos = var1;
         this.selStart = -1;
      }
   }

   private boolean ctrl() {
      class_310 var1 = class_310.method_1551();
      if (var1 == null) {
         return false;
      }

      long var2 = var1.method_22683().method_4490();
      return GLFW.glfwGetKey(var2, 341) == 1 || GLFW.glfwGetKey(var2, 345) == 1;
   }

   private static String keyLabel(int keybind) {
      if (keybind <= 0 && keybind != (-1) && keybind > (-100)) {
         byte[] var12 = new byte[3];
         var12[0] = (byte)(117);
         var12[1] = (byte)(-1);
         var12[2] = (byte)(104);
         return $$v_s(var12, 8256382334915563523L);
      }

      if (keybind == (-1)) {
         byte[] var11 = new byte[3];
         var11[0] = (byte)(117);
         var11[1] = (byte)(-1);
         var11[2] = (byte)(104);
         return $$v_s(var11, 8256382334915563523L);
      }

      if (keybind >= (1000)) {
         int var3 = keybind - (1000);

         return switch (var3) {
            case 0 -> {
               byte[] var10 = new byte[3];
               var10[0] = (byte)(106);
               var10[1] = (byte)(-41);
               var10[2] = (byte)(94);
               yield $$v_s(var10, 8256382334915563523L);
            }
            case 1 -> {
               byte[] var9 = new byte[3];
               var9[0] = (byte)(116);
               var9[1] = (byte)(-41);
               var9[2] = (byte)(94);
               yield $$v_s(var9, 8256382334915563523L);
            }
            case 2 -> {
               byte[] var8 = new byte[3];
               var8[0] = (byte)(107);
               var8[1] = (byte)(-41);
               var8[2] = (byte)(94);
               yield $$v_s(var8, 8256382334915563523L);
            }
            default -> "M" + (var3 + 1);
         };
      } else if (keybind <= (-100)) {
         int var2 = -(keybind + (100));

         return switch (var2) {
            case 0 -> {
               byte[] var6 = new byte[3];
               var6[0] = (byte)(106);
               var6[1] = (byte)(-41);
               var6[2] = (byte)(94);
               yield $$v_s(var6, 8256382334915563523L);
            }
            case 1 -> {
               byte[] var5 = new byte[3];
               var5[0] = (byte)(116);
               var5[1] = (byte)(-41);
               var5[2] = (byte)(94);
               yield $$v_s(var5, 8256382334915563523L);
            }
            case 2 -> {
               byte[] var4 = new byte[3];
               var4[0] = (byte)(107);
               var4[1] = (byte)(-41);
               var4[2] = (byte)(94);
               yield $$v_s(var4, 8256382334915563523L);
            }
            default -> "M" + (var2 + 1);
         };
      } else {
         String var1 = GLFW.glfwGetKeyName(keybind, 0);
         return var1 == null ? KeyUtils.getKey(keybind).toString() : var1.toUpperCase();
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
