package me.drake.vieclient.gui.nvg.ui.components;

import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.render.nvg.NVGIcons;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

public abstract class VieOSScreen extends class_437 {
   protected static final float HEADER_H = 36.0F;
   protected static final float ACTION_H = 28.0F;
   protected static final float FOOTER_H = 34.0F;
   protected static final float INSET = 18.0F;
   protected static final float RADIUS = 14.0F;
   protected static final float GAP = 10.0F;
   protected static final float CHIP_H = 28.0F;
   protected static final float CHIP_W = 88.0F;
   protected static final float INPUT_H = 32.0F;
   protected static final float ROW_H = 36.0F;
   private static final float OPEN_RATE = 20.0F;
   private static final float SCALE_MIN = 0.96F;
   private static final int DIM_ALPHA = 90;
   private static final float SHADOW_BLUR = 28.0F;
   private static final float SHADOW_OFFY = 14.0F;
   private static final float SHADOW_ALPHA = 130.0F;
   protected float pageAnim = 0.0F;
   protected boolean pageOpen = false;
   protected long openedAt = System.currentTimeMillis();
   protected int lastMx;
   protected int lastMy;
   protected float px;
   protected float py;
   protected float pw;
   protected float ph;
   protected int screenW;
   protected int screenH;
   protected VieOSScreen childOverlay = null;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   protected VieOSScreen() {
      super(class_2561.method_43473());
   }

   protected VieOSScreen(class_2561 title) {
      super(title);
   }

   public final boolean isPageOpen() {
      return this.pageOpen || this.pageAnim > 0.01F;
   }

   public final void openScreen() {
      this.pageOpen = true;
      this.openedAt = System.currentTimeMillis();
   }

   public void closeScreen() {
      this.pageOpen = false;
      this.method_25419();
   }

   public final void openChild(VieOSScreen child) {
      this.childOverlay = child;
   }

   public final void closeChild() {
      this.childOverlay = null;
   }

   public void method_25419() {
      this.pageOpen = false;
      super.method_25419();
   }

   protected abstract float contentW(int var1);

   protected abstract float contentH(int var1, int var2);

   protected abstract String title();

   protected abstract String footerText();

   protected abstract void renderContent(int var1, int var2, float var3);

   protected abstract boolean mouseClickedContent(double var1, double var3, int var5);

   protected abstract boolean keyPressedContent(int var1, int var2, int var3);

   protected void renderHeaderExtras(int mx, int my, float alpha) {
   }

   protected void tickAnimation(float delta) {
   }

   protected void drawContextContent(class_332 ctx, int mouseX, int mouseY, float delta) {
   }

   protected boolean charTypedContent(char chr, int mods) {
      return false;
   }

   protected boolean hasDrawContextContent() {
      return false;
   }

   protected boolean closesOnOutsideClick() {
      return true;
   }

   public boolean method_25421() {
      return false;
   }

   public boolean method_25422() {
      return true;
   }

   public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
   }

   protected void method_57734(class_332 context) {
   }

   protected void method_57735(class_332 context) {
   }

   protected void method_57736(class_332 context, int x, int y, int width, int height) {
   }

   protected void method_57728(class_332 context, float delta) {
   }

   protected void method_25426() {
      super.method_25426();
      this.updateBounds();
   }

   public void updateBounds() {
      this.screenW = this.field_22789;
      this.screenH = this.field_22790;
      float var1 = Math.max(280.0F, this.screenW - 24.0F);
      float var2 = Math.max(130.0F, this.screenH - 24.0F);
      this.pw = Math.min(var1, Math.max(280.0F, this.contentW(this.screenW)));
      this.ph = Math.min(var2, Math.max(130.0F, this.contentH(this.screenW, this.screenH)));
      this.px = Math.round((this.screenW - this.pw) / 2.0F);
      this.py = Math.round((this.screenH - this.ph) / 2.0F);
   }

   public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
      this.pageAnim = AnimationManager.isOff()
         ? (this.pageOpen ? 1.0F : 0.0F)
         : AnimationManager.damp(this.pageAnim, this.pageOpen ? 1.0F : 0.0F, 20.0F, delta);
      if (this.pageOpen && this.pageAnim >= 0.995F) {
         this.pageAnim = 1.0F;
      }

      if (!this.pageOpen && this.pageAnim <= 0.01F) {
         this.method_25419();
      } else {
         this.lastMx = mouseX;
         this.lastMy = mouseY;
         this.tickAnimation(delta);
         this.updateBounds();
         if (this.hasDrawContextContent()) {
            this.drawContextContent(ctx, mouseX, mouseY, delta);
            if (this.childOverlay != null && this.childOverlay.isPageOpen()) {
               this.childOverlay.method_25394(ctx, mouseX, mouseY, delta);
            }
         }
      }
   }

   public void renderPostBlit() {
      if (this.isPageOpen()) {
         this.updateBounds();
         this.drawNanoVgPass();
         if (this.childOverlay != null && this.childOverlay.isPageOpen()) {
            this.childOverlay.renderPostBlit();
         }
      }
   }

   private void drawNanoVgPass() {
      float var1 = 0.96F + 0.04000002F * this.pageAnim;
      boolean var2 = var1 < 0.999F;
      float var3 = this.px + this.pw / 2.0F;
      float var4 = this.py + this.ph / 2.0F;

      try (NanoVGFrame var5 = NanoVGRenderer.frame()) {
         if (var5.isActive()) {
            if (var2) {
               NanoVGRenderer.save();
               NanoVGRenderer.translate(var3, var4);
               NanoVGRenderer.scale(var1, var1);
               NanoVGRenderer.translate(-var3, -var4);
            }

            this.renderChrome();
            this.renderContent(this.lastMx, this.lastMy, this.pageAnim);
            this.renderExtras(this.lastMx, this.lastMy, this.pageAnim);
            if (var2) {
               NanoVGRenderer.restore();
            }
         }
      }
   }

   protected void renderChrome() {
      float var1 = this.pageAnim;
      NanoVGRenderer.drawRect(-this.screenW, -this.screenH, this.screenW * 3.0F, this.screenH * 3.0F, (int)(90.0F * var1) << (24));
      SoftPalette.syncTheme();
      NanoVGRenderer.drawSquircleGradient(
         this.px, this.py, this.pw, this.ph, 14.0F, SoftPalette.bodyTopInt(245.0F * var1), SoftPalette.bodyBotInt(250.0F * var1)
      );
      NanoVGRenderer.drawSquircleOutline(this.px, this.py, this.pw, this.ph, 14.0F, 1.0F, SoftPalette.whiteInt(36.0F * var1));
      NanoVGRenderer.drawRect(this.px + 14.0F, this.py + 1.0F, this.pw - 28.0F, 1.0F, SoftPalette.whiteInt(10.0F * var1));
      this.renderHeader(this.px, this.py, var1);
      this.renderHeaderDivider(this.px, this.py, var1);
      this.renderFooterDivider(this.px, this.py, var1);
   }

   protected void renderHeader(float x, float y, float alpha) {
      float var4 = y + 18.0F;
      float var5 = x + 20.0F;
      boolean var6 = this.lastMx >= var5 - 8.0F && this.lastMx <= var5 + 8.0F && this.lastMy >= var4 - 8.0F && this.lastMy <= var4 + 8.0F;
      boolean var7 = this.lastMx >= var5 + 6.0F && this.lastMx <= var5 + 22.0F && this.lastMy >= var4 - 8.0F && this.lastMy <= var4 + 8.0F;
      boolean var8 = this.lastMx >= var5 + 20.0F && this.lastMx <= var5 + 36.0F && this.lastMy >= var4 - 8.0F && this.lastMy <= var4 + 8.0F;
      int var9 = SoftPalette.clamp(255.0F * alpha);
      NanoVGRenderer.drawCircle(var5, var4, 5.4F, var9 << 24 | 16736086);
      NanoVGRenderer.drawCircleOutline(var5, var4, 5.4F, 0.75F, var9 << 24 | 14697534);
      NanoVGRenderer.drawCircle(var5 + 14.0F, var4, 5.4F, var9 << 24 | 16760110);
      NanoVGRenderer.drawCircleOutline(var5 + 14.0F, var4, 5.4F, 0.75F, var9 << 24 | 14590243);
      NanoVGRenderer.drawCircle(var5 + 28.0F, var4, 5.4F, var9 << 24 | 2607423);
      NanoVGRenderer.drawCircleOutline(var5 + 28.0F, var4, 5.4F, 0.75F, var9 << 24 | 1747753);
      if (var6) {
         NVGIcons.cross(var5, var4, 2.4F, 1.0F, (int)(220.0F * alpha) << 24 | 5046272);
      }

      if (var7) {
         NanoVGRenderer.drawRect(var5 + 11.5F, var4 - 0.5F, 5.0F, 1.1F, (int)(220.0F * alpha) << 24 | 10049280);
      }

      if (var8) {
         NVGIcons.cross(var5 + 28.0F, var4, 2.4F, 1.0F, (int)(220.0F * alpha) << 24 | 25600);
      }

      String var10 = this.title();
      if (var10 != null && !var10.isEmpty()) {
         SoftPalette.labelBold(var10, x + 56.0F, y + 18.0F - 7.0F, 12.0F, SoftPalette.textInt(255.0F * alpha));
      }

      this.renderHeaderExtras(this.lastMx, this.lastMy, alpha);
   }

   private void renderHeaderDivider(float x, float y, float alpha) {
      NanoVGRenderer.drawRect(x + 18.0F, y + 36.0F, this.pw - 36.0F, 1.0F, SoftPalette.whiteInt(10.0F * alpha));
   }

   private void renderFooterDivider(float x, float y, float alpha) {
      float var4 = y + this.ph - 34.0F;
      NanoVGRenderer.drawRect(x + 18.0F, var4, this.pw - 36.0F, 1.0F, SoftPalette.whiteInt(8.0F * alpha));
   }

   protected void renderExtras(int mx, int my, float alpha) {
      String var4 = this.footerText();
      if (var4 != null && !var4.isEmpty()) {
         SoftPalette.label(var4, this.px + 18.0F, this.py + this.ph - 12.0F, 9.0F, SoftPalette.textDimInt(180.0F * alpha));
      }

      if (this.childOverlay != null && this.childOverlay.isPageOpen()) {
         this.childOverlay.renderPostBlit();
      }
   }

   protected float bodyX() {
      return this.px + 18.0F;
   }

   protected float bodyY() {
      return this.py + 36.0F + 6.0F;
   }

   protected float bodyW() {
      return this.pw - 36.0F;
   }

   protected float actionY() {
      return this.bodyY();
   }

   protected float contentY() {
      return this.actionY() + 28.0F + 10.0F;
   }

   protected float footerY() {
      return this.py + this.ph - 34.0F - 4.0F;
   }

   protected float actionH() {
      return 28.0F;
   }

   protected float contentListH() {
      return this.footerY() - this.contentY() - 8.0F;
   }

   public final boolean method_25402(class_11909 click, boolean consumed) {
      if (!this.isPageOpen()) {
         return false;
      }

      if (this.childOverlay != null && this.childOverlay.isPageOpen()) {
         return this.childOverlay.method_25402(click, consumed);
      }

      double var3 = click.comp_4798();
      double var5 = click.comp_4799();
      int var7 = click.method_74245();
      float var8 = this.px + 20.0F;
      float var9 = this.py + 18.0F;
      if (var3 >= var8 - 8.0F && var3 <= var8 + 8.0F && var5 >= var9 - 8.0F && var5 <= var9 + 8.0F) {
         this.closeScreen();
         return true;
      }

      if (!this.closesOnOutsideClick() || !(var3 < this.px) && !(var3 > this.px + this.pw) && !(var5 < this.py) && !(var5 > this.py + this.ph)) {
         return this.mouseClickedContent(var3, var5, var7) ? true : true;
      }

      this.closeScreen();
      return true;
   }

   public final boolean method_25404(class_11908 key) {
      if (!this.isPageOpen()) {
         return false;
      } else if (this.childOverlay != null && this.childOverlay.isPageOpen()) {
         return this.childOverlay.method_25404(key);
      } else if (this.keyPressedContent(key.comp_4795(), key.comp_4796(), key.comp_4797())) {
         return true;
      } else if (key.comp_4795() == (256)) {
         this.closeScreen();
         return true;
      } else {
         return false;
      }
   }

   public final boolean method_25400(class_11905 input) {
      if (!this.isPageOpen()) {
         return false;
      } else if (this.childOverlay != null && this.childOverlay.isPageOpen()) {
         return this.childOverlay.method_25400(input);
      } else {
         return !input.method_74227() ? false : this.charTypedContent(input.method_74226().charAt(0), input.comp_4794());
      }
   }

   protected static void drawAccentChip(float x, float y, float w, float h, String label, float alpha) {
      NanoVGRenderer.drawRoundedRectGradient(x, y, w, h, 6.0F, SoftPalette.mintInt(255.0F * alpha), SoftPalette.mintDeepInt(255.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(x, y, w, h, 6.0F, 1.0F, SoftPalette.mintInt(140.0F * alpha));
      float var6 = SoftPalette.widthBold(label, 10.0F);
      SoftPalette.labelBold(label, x + (w - var6) / 2.0F, y + (h - 10.0F) / 2.0F, 10.0F, SoftPalette.whiteInt(255.0F * alpha));
   }

   protected static void drawNeutralChip(float x, float y, float w, float h, String label, boolean hov, float alpha) {
      NanoVGRenderer.drawRoundedRect(x, y, w, h, 6.0F, hov ? SoftPalette.whiteInt(26.0F * alpha) : SoftPalette.whiteInt(12.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(x, y, w, h, 6.0F, 1.0F, hov ? SoftPalette.whiteInt(44.0F * alpha) : SoftPalette.whiteInt(20.0F * alpha));
      float var7 = SoftPalette.width(label, 10.0F);
      SoftPalette.label(label, x + (w - var7) / 2.0F, y + (h - 10.0F) / 2.0F, 10.0F, SoftPalette.textInt(255.0F * alpha));
   }

   protected static void drawDangerChip(float x, float y, float w, float h, String label, boolean hov, float alpha) {
      int var7 = hov ? (int)(230.0F * alpha) << (24) | 14761020 : (int)(190.0F * alpha) << (24) | 13121335;
      NanoVGRenderer.drawRoundedRect(x, y, w, h, 6.0F, var7);
      NanoVGRenderer.drawRoundedRectOutline(x, y, w, h, 6.0F, 1.0F, (int)(170.0F * alpha) << (24) | 16745090);
      float var8 = SoftPalette.width(label, 10.0F);
      SoftPalette.label(label, x + (w - var8) / 2.0F, y + (h - 10.0F) / 2.0F, 10.0F, SoftPalette.whiteInt(255.0F * alpha));
   }

   protected static boolean hit(double mx, double my, float x, float y, float w, float h) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }

   protected final float rowEntrance(int index) {
      float var2 = (float)(System.currentTimeMillis() - this.openedAt) / 1000.0F - Math.min(index, 12) * 0.018F;
      float var3 = Math.max(0.0F, Math.min(1.0F, var2 / 0.18F));
      return 1.0F - (1.0F - var3) * (1.0F - var3) * (1.0F - var3);
   }
}
