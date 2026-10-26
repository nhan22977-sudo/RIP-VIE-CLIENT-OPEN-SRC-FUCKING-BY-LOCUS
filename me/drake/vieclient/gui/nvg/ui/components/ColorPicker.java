package me.drake.vieclient.gui.nvg.ui.components;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.gui.nvg.adapter.setting.ColorSetting;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.render.nvg.MaterialFX;
import me.drake.vieclient.render.nvg.NanoVGRenderer;

public class ColorPicker {
   private static final Color PANEL_BG = new Color(18, 18, 22, 255);
   private static final Color ACCENT_COLOR = new Color(88, 101, 242, 255);
   private static final Color BORDER_COLOR = new Color(40, 40, 46, 255);
   private static final float CORNER_RADIUS = 4.0F;
   private static final int PADDING = 12;
   private static final int BUTTON_HEIGHT = 22;
   private static final int BUTTON_SPACING = 6;
   private final ColorSetting setting;
   private final int width = 180;
   private final int height = 290;
   private float x;
   private float y;
   private boolean visible = false;
   private float animationProgress = 0.0F;
   private float hue = 0.0F;
   private float saturation = 1.0F;
   private float brightness = 1.0F;
   private int alpha = 255;
   private float animHue = 0.0F;
   private float animSat = 1.0F;
   private float animBri = 1.0F;
   private float animAlpha = 255.0F;
   private boolean draggingSV = false;
   private boolean draggingHue = false;
   private boolean draggingAlpha = false;
   private static Color clipboard = null;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ColorPicker(ColorSetting setting, float x, float y) {
      this.setting = settingx;
      this.x = xx;
      this.y = yx;
      this.colorToHSV(settingx.getValue());
      this.snapAnim();
   }

   private void colorToHSV(Color color) {
      float[] var2 = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), (float[])null);
      this.hue = var2[0];
      this.saturation = var2[1];
      this.brightness = var2[2];
      this.alpha = color.getAlpha();
   }

   private void snapAnim() {
      this.animHue = this.hue;
      this.animSat = this.saturation;
      this.animBri = this.brightness;
      this.animAlpha = this.alpha;
   }

   private Color hsvToColor() {
      int var1 = Color.HSBtoRGB(this.hue, this.saturation, this.brightness);
      Color var2 = new Color(var1);
      return new Color(var2.getRed(), var2.getGreen(), var2.getBlue(), this.alpha);
   }

   public void show(float x, float y, int screenWidth, int screenHeight) {
      this.x = Math.max(0.0F, Math.min(xx, screenWidth - (180)));
      this.y = Math.max(0.0F, Math.min(yx, screenHeight - (290)));
      this.visible = true;
      this.colorToHSV(this.setting.getValue());
      this.snapAnim();
   }

   public void hide() {
      this.visible = false;
   }

   public void update(float delta) {
      if (this.visible) {
         this.animationProgress += delta * 8.0F;
         if (this.animationProgress > 1.0F) {
            this.animationProgress = 1.0F;
         }
      } else {
         this.animationProgress -= delta * 8.0F;
         if (this.animationProgress < 0.0F) {
            this.animationProgress = 0.0F;
         }
      }

      if (AnimationManager.isOff()) {
         this.snapAnim();
      } else {
         float var2 = Math.min(1.0F, delta * 16.0F);
         this.animHue = this.animHue + (this.hue - this.animHue) * var2;
         this.animSat = this.animSat + (this.saturation - this.animSat) * var2;
         this.animBri = this.animBri + (this.brightness - this.animBri) * var2;
         this.animAlpha = this.animAlpha + (this.alpha - this.animAlpha) * var2;
      }
   }

   public void render(int mouseX, int mouseY, float alpha) {
      if (!(this.animationProgress <= 0.0F)) {
         float var4 = alphax * this.animationProgress;
         int var5 = (int)(255.0F * var4);
         boolean var6 = !AnimationManager.isOff();
         if (var6) {
            float var7 = 0.85F + 0.15F * easeOutBack(this.animationProgress);
            NanoVGRenderer.save();
            NanoVGRenderer.translate(this.x, this.y);
            NanoVGRenderer.scale(var7, var7);
            NanoVGRenderer.translate(-this.x, -this.y);
         }

         boolean var15 = ThemeManager.isCleanStyle();
         float var8 = var15 ? 10.0F : 7.0F;
         int[] var9 = ThemeManager.borderRGB();
         int[] var10 = ThemeManager.bodyTop();
         int[] var11 = ThemeManager.bodyBot();
         int var12 = ThemeManager.bodyAlpha(var4);
         if (var15) {
            MaterialFX.glassBody(this.x, this.y, 180.0F, 290.0F, var8, 1.0F * var4);
            NanoVGRenderer.drawRoundedRectGradient(
               this.x,
               this.y,
               180.0F,
               290.0F,
               var8,
               new Color(var10[0], var10[1], var10[2], var12),
               new Color(var11[0], var11[1], var11[2], var12)
            );
         } else {
            NanoVGRenderer.drawRoundedRectGradient(
               this.x,
               this.y,
               180.0F,
               290.0F,
               var8,
               new Color(var10[0], var10[1], var10[2], var12),
               new Color(var11[0], var11[1], var11[2], var12)
            );
            NanoVGRenderer.drawRoundedRectOutline(this.x, this.y, 180.0F, 290.0F, var8, 1.0F, new Color(var9[0], var9[1], var9[2], var5));
            GuiStyle.gloss(this.x, this.y, 180.0F, 290.0F, var8, var4);
         }

         float var13 = this.y + 12.0F;
         int var14 = 156;
         this.renderSVPicker(var13, var4);
         var13 += var14 + (8);
         this.renderHueSlider(var13, var4);
         var13 += 20.0F;
         this.renderAlphaSlider(var13, var4);
         var13 += 20.0F;
         this.renderPreview(var13, var4);
         var13 += 38.0F;
         this.renderButtons(var13, mouseX, mouseY, var4);
         if (var6) {
            NanoVGRenderer.restore();
         }
      }
   }

   private static float easeOutBack(float t) {
      float var1 = 1.70158F;
      float var2 = var1 + 1.0F;
      float var3 = t - 1.0F;
      return 1.0F + var2 * var3 * var3 * var3 + var1 * var3 * var3;
   }

   private void renderSVPicker(float startY, float alpha) {
      int var3 = 156;
      float var4 = this.x + 12.0F;
      float var5 = startY;
      int var6 = (int)(255.0F * alphax);
      int var7 = 32;
      float var8 = (float)var3 / var7;

      for (int var9 = 0; var9 < var7; var9++) {
         for (int var10 = 0; var10 < var7; var10++) {
            float var11 = (float)var9 / var7;
            float var12 = 1.0F - (float)var10 / var7;
            int var13 = Color.HSBtoRGB(this.animHue, var11, var12);
            Color var14 = new Color(var13);
            Color var15 = new Color(var14.getRed(), var14.getGreen(), var14.getBlue(), var6);
            NanoVGRenderer.drawRect(var4 + var9 * var8, var5 + var10 * var8, var8, var8, var15);
         }
      }

      float var16 = var4 + this.animSat * var3;
      float var17 = var5 + (1.0F - this.animBri) * var3;
      Color var18 = new Color(255, 255, 255, var6);
      NanoVGRenderer.drawCircle(var16, var17, 5.0F, var18);
      Color var19 = new Color(0, 0, 0, var6);
      NanoVGRenderer.drawCircle(var16, var17, 3.0F, var19);
   }

   private void renderHueSlider(float startY, float alpha) {
      int var3 = 156;
      float var4 = this.x + 12.0F;
      float var5 = startY;
      float var6 = 12.0F;
      int var7 = (int)(255.0F * alphax);
      int var8 = 60;
      float var9 = (float)var3 / var8;

      for (int var10 = 0; var10 < var8; var10++) {
         float var11 = (float)var10 / var8;
         int var12 = Color.HSBtoRGB(var11, 1.0F, 1.0F);
         Color var13 = new Color(var12);
         Color var14 = new Color(var13.getRed(), var13.getGreen(), var13.getBlue(), var7);
         NanoVGRenderer.drawRect(var4 + var10 * var9, var5, var9, var6, var14);
      }

      float var15 = var4 + this.animHue * var3;
      Color var16 = new Color(255, 255, 255, var7);
      NanoVGRenderer.drawRect(var15 - 2.0F, var5 - 2.0F, 4.0F, var6 + 4.0F, var16);
   }

   private void renderAlphaSlider(float startY, float alpha) {
      int var3 = 156;
      float var4 = this.x + 12.0F;
      float var5 = startY;
      float var6 = 12.0F;
      int var7 = (int)(255.0F * alphax);
      Color var8 = this.hsvToColor();
      int var9 = 60;
      float var10 = (float)var3 / var9;

      for (int var11 = 0; var11 < var9; var11++) {
         int var12 = (int)((float)var11 / var9 * 255.0F);
         Color var13 = new Color(var8.getRed(), var8.getGreen(), var8.getBlue(), (int)(var12 * alphax));
         NanoVGRenderer.drawRect(var4 + var11 * var10, var5, var10, var6, var13);
      }

      float var14 = var4 + this.animAlpha / 255.0F * var3;
      Color var15 = new Color(255, 255, 255, var7);
      NanoVGRenderer.drawRect(var14 - 2.0F, var5 - 2.0F, 4.0F, var6 + 4.0F, var15);
   }

   private void renderPreview(float startY, float alpha) {
      boolean var3 = ThemeManager.isCleanStyle();
      int var4 = 30;
      float var5 = this.x + 12.0F;
      Color var6 = this.hsvToColor();
      int var7 = (int)(255.0F * alphax);
      Color var8 = new Color(var6.getRed(), var6.getGreen(), var6.getBlue(), (int)(var6.getAlpha() * alphax));
      Color var9 = new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), var7);
      NanoVGRenderer.drawRoundedRect(var5, startY, var4, var4, var3 ? 2.5F : 6.0F, var8);
      if (!var3) {
         NanoVGRenderer.drawRoundedRectOutline(var5, startY, var4, var4, 6.0F, 1.5F, var9);
      }

      byte[] var10000 = new byte[17];
      var10000[0] = (byte)(27);
      var10000[1] = (byte)(-65);
      var10000[2] = (byte)(44);
      var10000[3] = (byte)(-20);
      var10000[4] = (byte)(-7);
      var10000[5] = (byte)(14);
      var10000[6] = (byte)(94);
      var10000[7] = (byte)(127);
      var10000[8] = (byte)(8);
      var10000[9] = (byte)(-121);
      var10000[10] = (byte)(-44);
      var10000[11] = (byte)(-60);
      var10000[12] = (byte)(-95);
      var10000[13] = (byte)(-42);
      var10000[14] = (byte)(118);
      var10000[15] = (byte)(7);
      var10000[16] = (byte)(48);
      String var12 = $$v_s(var10000, 7308767254969307165L);
      Object[] var10001 = new Object[4];
      var10001[0] = var6.getRed();
      var10001[1] = var6.getGreen();
      var10001[2] = var6.getBlue();
      var10001[3] = var6.getAlpha();
      String var10 = String.format(var12, var10001);
      Color var11 = new Color(200, 200, 200, var7);
      NanoVGRenderer.drawText(var10, var5 + var4 + 8.0F, startY + 10.0F, 9.5F, var11);
   }

   private void renderButtons(float startY, int mouseX, int mouseY, float alpha) {
      boolean var5 = ThemeManager.isCleanStyle();
      byte var6 = 75;
      float var7 = this.x + 12.0F;
      float var8 = var7 + var6 + 6.0F;
      int var9 = (int)(255.0F * alphax);
      boolean var10 = mouseX >= var7 && mouseX <= var7 + var6 && mouseY >= startY && mouseY <= startY + 22.0F;
      boolean var11 = mouseX >= var8 && mouseX <= var8 + var6 && mouseY >= startY && mouseY <= startY + 22.0F;
      Color var12 = var10 ? new Color(60, 60, 70, var9) : new Color(35, 35, 40, var9);
      Color var13 = var11 ? new Color(60, 60, 70, var9) : new Color(35, 35, 40, var9);
      Color var14 = new Color(ACCENT_COLOR.getRed(), ACCENT_COLOR.getGreen(), ACCENT_COLOR.getBlue(), (int)(100.0F * alphax));
      Color var15 = new Color(200, 200, 200, var9);
      NanoVGRenderer.drawRoundedRect(var7, startY, var6, 22.0F, var5 ? 2.5F : 6.0F, var12);
      if (!var5) {
         NanoVGRenderer.drawRoundedRectOutline(var7, startY, var6, 22.0F, 6.0F, 1.0F, var14);
      }

      String var16 = "Copy";
      float var17 = NanoVGRenderer.getTextWidth(var16, 10.0F);
      NanoVGRenderer.drawText(var16, var7 + (var6 - var17) / 2.0F, startY + 6.0F, 10.0F, var15);
      Color var18 = clipboard == null ? new Color(100, 100, 110, var9) : var15;
      NanoVGRenderer.drawRoundedRect(var8, startY, var6, 22.0F, var5 ? 2.5F : 6.0F, var13);
      if (!var5) {
         NanoVGRenderer.drawRoundedRectOutline(var8, startY, var6, 22.0F, 6.0F, 1.0F, var14);
      }

      String var19 = "Paste";
      float var20 = NanoVGRenderer.getTextWidth(var19, 10.0F);
      NanoVGRenderer.drawText(var19, var8 + (var6 - var20) / 2.0F, startY + 6.0F, 10.0F, var18);
   }

   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (this.animationProgress <= 0.0F || button != 0) {
         return false;
      } else if (!this.isMouseOver(mouseX, mouseY)) {
         return false;
      } else {
         short var6 = 156;
         float var7 = this.x + 12.0F;
         float var8 = this.y + 12.0F;
         byte var9 = 75;
         float var10 = this.x + 12.0F;
         float var11 = var10 + var9 + 6.0F;
         float var12 = this.y + 290.0F - 12.0F - 22.0F;
         if (mouseX >= var10 && mouseX <= var10 + var9 && mouseY >= var12 && mouseY <= var12 + 22.0F) {
            clipboard = this.hsvToColor();
            return true;
         } else if (mouseX >= var11 && mouseX <= var11 + var9 && mouseY >= var12 && mouseY <= var12 + 22.0F && clipboard != null) {
            this.colorToHSV(clipboard);
            this.setting.setValue(clipboard.getRed(), clipboard.getGreen(), clipboard.getBlue(), clipboard.getAlpha());
            return true;
         } else if (mouseX >= var7 && mouseX <= var7 + var6 && mouseY >= var8 && mouseY <= var8 + var6) {
            this.draggingSV = true;
            this.updateSV(mouseX, mouseY, var7, var8, var6);
            return true;
         } else {
            short var13 = 156;
            float var14 = this.x + 12.0F;
            float var15 = this.y + 12.0F + var6 + 8.0F;
            if (mouseX >= var14 && mouseX <= var14 + var13 && mouseY >= var15 && mouseY <= var15 + 12.0F) {
               this.draggingHue = true;
               this.updateHue(mouseX, var14, var13);
               return true;
            } else {
               float var16 = var15 + 20.0F;
               if (mouseX >= var14 && mouseX <= var14 + var13 && mouseY >= var16 && mouseY <= var16 + 12.0F) {
                  this.draggingAlpha = true;
                  this.updateAlpha(mouseX, var14, var13);
                  return true;
               } else {
                  return true;
               }
            }
         }
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button) {
      if (button != 0) {
         return false;
      } else {
         int var6 = 156;
         float var7 = this.x + 12.0F;
         float var8 = this.y + 12.0F;
         int var9 = 156;
         float var10 = this.x + 12.0F;
         if (this.draggingSV) {
            this.updateSV(mouseX, mouseY, var7, var8, var6);
            return true;
         } else if (this.draggingHue) {
            this.updateHue(mouseX, var10, var9);
            return true;
         } else if (this.draggingAlpha) {
            this.updateAlpha(mouseX, var10, var9);
            return true;
         } else {
            return false;
         }
      }
   }

   public void mouseReleased(double mouseX, double mouseY, int button) {
      if (button == 0) {
         this.draggingSV = false;
         this.draggingHue = false;
         this.draggingAlpha = false;
      }
   }

   private void updateSV(double mouseX, double mouseY, float svX, float svY, int svSize) {
      this.saturation = Math.max(0.0F, Math.min(1.0F, (float)(mouseX - svX) / svSize));
      this.brightness = Math.max(0.0F, Math.min(1.0F, 1.0F - (float)(mouseY - svY) / svSize));
      Color var8 = this.hsvToColor();
      this.setting.setValue(var8);
   }

   private void updateHue(double mouseX, float sliderX, int sliderWidth) {
      this.hue = Math.max(0.0F, Math.min(1.0F, (float)(mouseX - sliderX) / sliderWidth));
      Color var5 = this.hsvToColor();
      this.setting.setValue(var5);
   }

   private void updateAlpha(double mouseX, float sliderX, int sliderWidth) {
      this.alpha = (int)Math.max(0.0, Math.min(255.0, (mouseX - sliderX) / sliderWidth * 255.0));
      Color var5 = this.hsvToColor();
      this.setting.setValue(var5);
   }

   private boolean isMouseOver(double mouseX, double mouseY) {
      return mouseX >= this.x && mouseX <= this.x + 180.0F && mouseY >= this.y && mouseY <= this.y + 290.0F;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public boolean isAnimating() {
      return this.animationProgress > 0.0F;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
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
