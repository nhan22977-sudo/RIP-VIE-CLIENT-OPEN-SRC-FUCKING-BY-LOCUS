package me.drake.vieclient.gui.nvg.ui;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.CoreEntry;
import me.drake.vieclient.auth.AuthManager;
import me.drake.vieclient.gui.nvg.adapter.modules.client.Sounds;
import me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient;
import me.drake.vieclient.gui.nvg.adapter.setting.BindSetting;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.gui.nvg.ui.components.DashboardView;
import me.drake.vieclient.gui.nvg.ui.components.SoftPalette;
import me.drake.vieclient.gui.nvg.ui.components.VieOSPage;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.render.FrostedBackground;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGNatives;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.render.ToastManager;
import net.minecraft.class_11905;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;

public class NewClickGUI extends class_437 {
   private final DashboardView dashboardView;
   private VieOSPage osPage;
   private float animationProgress;
   private boolean closing;
   private long lastFrameTime;
   private long openTime;
   private boolean initialized;
   private float nvgAlpha;
   private float nvgScale;
   private int nvgCx;
   private int nvgCy;
   private int nvgTmx;
   private int nvgTmy;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public NewClickGUI() {
      byte[] var10001 = new byte[11];
      var10001[0] = (byte)(113);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(107);
      var10001[3] = (byte)(-99);
      var10001[4] = (byte)(46);
      var10001[5] = (byte)(-71);
      var10001[6] = (byte)(-113);
      var10001[7] = (byte)(4);
      var10001[8] = (byte)(16);
      var10001[9] = (byte)(-9);
      var10001[10] = (byte)(-83);
      super(class_2561.method_43470($$v_s(var10001, 5183378168575414298L)));
      this.dashboardView = new DashboardView();
      this.animationProgress = 0.0F;
      this.closing = false;
      this.lastFrameTime = System.currentTimeMillis();
      this.openTime = 0L;
      this.initialized = false;
   }

   public void reset() {
      this.closing = false;
      this.animationProgress = 0.0F;
      this.lastFrameTime = System.currentTimeMillis();
      this.openTime = System.currentTimeMillis();
      this.dashboardView.reset();
      if (this.osPage != null) {
         this.osPage.closeImmediate();
      }

      Sounds.instance.playGuiOpen();
   }

   protected void method_25426() {
      super.method_25426();
      this.lastFrameTime = System.currentTimeMillis();
      this.closing = false;
      if (this.osPage != null) {
         this.osPage.closeImmediate();
      }

      this.nvgCx = this.field_22789 / (2);
      this.nvgCy = this.field_22790 / (2);
      this.nvgScale = this.currentScale();
      this.nvgAlpha = this.animationProgress;
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

   private void lazyInit() {
      if (!this.initialized) {
         this.initialized = true;
         NanoVGNatives.ensureLoaded();
         NanoVGRenderer.init();
         this.osPage = new VieOSPage();
         this.dashboardView.setOsPage(this.osPage);
      }
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      super.method_25394(context, mouseX, mouseY, delta);
      this.lazyInit();
      SoftPalette.syncTheme();
      long var5 = System.currentTimeMillis();
      float var7 = this.lastFrameTime > 0L && var5 >= this.lastFrameTime ? Math.min((float)(var5 - this.lastFrameTime) / 1000.0F, 0.05F) : 0.016F;
      var7 = Math.max(1.0E-4F, var7);
      this.lastFrameTime = var5;
      float var8 = var7 * AnimationManager.speedMul();
      if (this.closing) {
         this.animationProgress -= var8 * 6.0F;
         if (this.animationProgress <= 0.0F) {
            this.animationProgress = 0.0F;
            this.performClose();
            return;
         }
      } else {
         this.animationProgress = Math.min(1.0F, this.animationProgress + var8 * 5.0F);
      }

      if (AnimationManager.isOff()) {
         this.animationProgress = this.closing ? 0.0F : 1.0F;
      }

      this.nvgScale = this.currentScale();
      this.nvgAlpha = this.animationProgress;
      this.nvgCx = this.field_22789 / (2);
      this.nvgCy = this.field_22790 / (2);
      this.nvgTmx = (int)((mouseX - this.nvgCx) / this.nvgScale + this.nvgCx);
      this.nvgTmy = (int)((mouseY - this.nvgCy) / this.nvgScale + this.nvgCy);
      this.dashboardView.update(var8);
      if (this.osPage != null) {
         this.osPage.update(var8);
      }
   }

   public void renderPostBlit() {
      float var1 = this.nvgScale;
      float var2 = this.nvgCx;
      float var3 = this.nvgCy;
      boolean var4 = this.osPage != null && this.osPage.isOverlayOpen();
      this.dashboardView.blitKawase(var1, var2, var3, this.field_22789, this.field_22790);
      if (var4) {
         this.osPage.blitKawaseBackdrop(var1, var2, var3, this.field_22789, this.field_22790);
      }

      ToastManager.blitKawase(this.field_22789, this.field_22790);
      FrostedBackground.drawWidgetBackdrops();

      try (NanoVGFrame var5 = NanoVGRenderer.frame()) {
         if (!var5.isActive()) {
            return;
         }

         NanoVGRenderer.save();
         NanoVGRenderer.translate(this.nvgCx, this.nvgCy);
         NanoVGRenderer.scale(this.nvgScale, this.nvgScale);
         NanoVGRenderer.translate(-this.nvgCx, -this.nvgCy);
         this.dashboardView
            .render(var4 ? -10000 : this.nvgTmx, var4 ? -10000 : this.nvgTmy, this.nvgAlpha, this.field_22789, this.field_22790);
         if (var4) {
            this.osPage.render(this.nvgTmx, this.nvgTmy, this.nvgAlpha, this.field_22789, this.field_22790);
         }

         NanoVGRenderer.restore();
         NanoVGRenderer.resetScissor();
         ToastManager.render(this.field_22789, this.field_22790);
      }
   }

   private float currentScale() {
      float var1 = VieClient.guiScale != null ? (float)VieClient.guiScale.getValue() : 1.0F;
      float var2 = Math.min(var1, Math.min((this.field_22789 - 16.0F) / DashboardView.DW, (this.field_22790 - 16.0F) / DashboardView.DH));
      return Math.max(0.3F, var2) * (0.96F + 0.04F * AnimationManager.easeOutExpo(this.animationProgress));
   }

   private double tx(double mx) {
      return (mx - this.field_22789 / 2.0) / this.currentScale() + this.field_22789 / 2.0;
   }

   private double ty(double my) {
      return (my - this.field_22790 / 2.0) / this.currentScale() + this.field_22790 / 2.0;
   }

   public void method_25419() {
      if (!this.closing) {
         Sounds.instance.playGuiClose();
      }

      if (this.osPage != null) {
         this.osPage.closeOverlay();
      }

      this.closing = true;
      this.saveClientConfig();
   }

   private void performClose() {
      this.saveClientConfig();
      VieClient var1 = VieClient.instance;
      if (var1 != null && var1.isEnabled()) {
         var1.setEnabled(false);
      } else if (this.field_22787 != null) {
         this.field_22787.method_1507(null);
      }
   }

   public void method_25432() {
      super.method_25432();
      if (this.osPage != null) {
         this.osPage.closeImmediate();
      }

      this.saveClientConfig();
   }

   private void saveClientConfig() {
      try {
         CoreEntry var1 = CoreEntry.getInstance();
         if (var1 != null && var1.getConfigManager() != null) {
            var1.getConfigManager().save();
         }
      } catch (Throwable var2) {
      }
   }

   public boolean method_25421() {
      return false;
   }

   public boolean method_25402(class_11909 click, boolean consumed) {
      if (this.closing) {
         return true;
      }

      if (!AuthManager.getInstance().isLockedDown() && AuthManager.getInstance().getStatus() != AuthManager.Status.OUTDATED_CLIENT) {
         double var3 = this.tx(click.comp_4798());
         double var5 = this.ty(click.comp_4799());
         int var7 = click.method_74245();
         if (this.osPage != null && this.osPage.isOverlayOpen()) {
            if (!this.osPage.mouseClicked(var3, var5, var7)) {
               this.osPage.closeOverlay();
            }

            return true;
         } else {
            return this.dashboardView.mouseClicked(var3, var5, var7, this.field_22789, this.field_22790) ? true : super.method_25402(click, consumed);
         }
      } else {
         ToastManager.push(
            "Vie Client",
            false,
            "Client version is outdated! Update required.",
            "warn"
         );
         return true;
      }
   }

   public boolean method_25400(class_11905 input) {
      if (!input.method_74227()) {
         return super.method_25400(input);
      }

      if (this.osPage != null && this.osPage.isOverlayOpen()) {
         return super.method_25400(input);
      }

      char var2 = input.method_74226().charAt(0);
      return this.dashboardView.charTyped(var2, input.comp_4794()) ? true : super.method_25400(input);
   }

   public boolean method_25404(class_11908 key) {
      int var2 = key.comp_4795();
      if (this.osPage != null && this.osPage.isOverlayOpen()) {
         if (this.osPage.keyPressed(var2)) {
            return true;
         }

         if (var2 == (256)) {
            this.osPage.closeOverlay();
            return true;
         }
      }

      if (this.dashboardView.keyPressed(var2, key.comp_4796(), key.comp_4797())) {
         return true;
      }

      if (var2 == (256)) {
         this.method_25419();
         return true;
      }

      ClientSettings var3 = ClientSettings.get();
      int var4 = var3 != null ? var3.getClickGuiKey() : 344;
      if (var4 <= 0) {
         VieClient var5 = VieClient.instance;
         if (var5 != null) {
            var4 = var5.getSettings()
               .stream()
               .filter(s -> s instanceof BindSetting var1 && var1.isModuleKey())
               .mapToInt(s -> ((BindSetting)s).getValue())
               .findFirst()
               .orElse(344);
         }
      }

      if (var2 == var4 && System.currentTimeMillis() - this.openTime > 300L) {
         this.method_25419();
         return true;
      } else if (var2 == (260) && var4 != (260) && System.currentTimeMillis() - this.openTime > 300L) {
         this.method_25419();
         return true;
      } else {
         return super.method_25404(key);
      }
   }

   public boolean method_25406(class_11909 click) {
      if (this.osPage != null && this.osPage.isOverlayOpen()) {
         this.osPage.mouseReleased(this.tx(click.comp_4798()), this.ty(click.comp_4799()), click.method_74245());
      }

      return super.method_25406(click);
   }

   public boolean method_25403(class_11909 click, double dx, double dy) {
      return this.osPage != null
            && this.osPage.isOverlayOpen()
            && this.osPage.mouseDragged(this.tx(click.comp_4798()), this.ty(click.comp_4799()), click.method_74245())
         ? true
         : super.method_25403(click, dx, dy);
   }

   public boolean method_25401(double mx, double my, double ha, double va) {
      if (this.osPage != null && this.osPage.isOverlayOpen()) {
         return this.osPage.mouseScrolled(this.tx(mx), this.ty(my), va) ? true : super.method_25401(mx, my, ha, va);
      } else {
         return this.dashboardView.mouseScrolled(this.tx(mx), this.ty(my), va) ? true : super.method_25401(mx, my, ha, va);
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
