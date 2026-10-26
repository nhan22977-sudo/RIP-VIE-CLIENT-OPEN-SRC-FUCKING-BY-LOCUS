package me.drake.vieclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.opengl.GlStateManager;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.events.BackdropPrepareEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.PreItemUseEvent;
import me.drake.vieclient.core.event.events.ScreenChangeEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.gui.nvg.ui.CustomTitleScreen;
import me.drake.vieclient.gui.nvg.ui.EmoteWheel;
import me.drake.vieclient.gui.nvg.ui.NewClickGUI;
import me.drake.vieclient.gui.nvg.ui.components.VieOSScreen;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.modules.player.FastUse;
import me.drake.vieclient.render.FrostedBackground;
import me.drake.vieclient.render.nvg.NVGRenderer;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.DebugFrameCapture;
import me.drake.vieclient.util.EntityCache;
import net.minecraft.class_1041;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_310.class)
public class MixinMinecraftClient {
   @Shadow
   @Final
   private class_1041 field_1704;
   @Shadow
   private int field_1752;
   @Unique
   private boolean vieclient$rightClickRequested;
   @Unique
   private static final PostBlitRenderEvent VIE$POST_BLIT_EVENT = new PostBlitRenderEvent(0.0F, false);

   public void vieclient$requestRightClick() {
      this.vieclient$rightClickRequested = true;
   }

   @Inject(method = "method_1574", at = @At("HEAD"))
   private void onTickPre(CallbackInfo ci) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null) {
         boolean var3 = var2.getModuleManager() != null && var2.getModuleManager().hasEnabledEntityCacheConsumers();
         EntityCache.setActive(var3);
         if (var3) {
            EntityCache.update();
         }

         var2.getEventBus().post(TickEvent.PRE);
         EmoteWheel.tick();
      }
   }

   @Inject(method = "method_1574", at = @At("RETURN"))
   private void onTickPost(CallbackInfo ci) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null) {
         var2.getEventBus().post(TickEvent.POST);
      }
   }

   @Inject(method = "method_1507", at = @At("HEAD"))
   private void onSetScreen(class_437 screen, CallbackInfo ci) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null && var3.getModuleManager() != null) {
         var3.getEventBus().post(new ScreenChangeEvent(screen));
         Freecam var4 = (Freecam)var3.getModuleManager().getModule(Freecam.class);
         if (var4 != null && var4.isEnabled()) {
            var4.onScreenChanged(screen);
         }
      }
   }

   @Inject(method = "method_1523(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_276;method_1237()V", shift = Shift.BEFORE))
   private void onPreBlit(boolean tick, CallbackInfo ci) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null && var3.getEventBus() != null && var3.getEventBus().hasSubscribers(BackdropPrepareEvent.class)) {
         var3.getEventBus().post(new BackdropPrepareEvent(0.0F));
      }

      FrostedBackground.beginCapture();
   }

   @Inject(method = "method_1523(Z)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_276;method_1237()V", shift = Shift.AFTER))
   private void onPostBlit(boolean tick, CallbackInfo ci) {
      if (((class_310)this).method_18506() == null) {
         try {
            GlStateManager._glBindFramebuffer(36160, 0);
            GL11.glDrawBuffer(1029);
            GL11.glReadBuffer(1029);
            GL30.glViewport(0, 0, this.field_1704.method_4489(), this.field_1704.method_4506());
            DebugFrameCapture.tryCapture((class_310)this);
            FrostedBackground.apply();

            try (NanoVGFrame var3 = NanoVGRenderer.frame()) {
               if (var3.isActive()) {
                  EmoteWheel.render();
                  VieClientMod var4 = VieClientMod.getInstance();
                  class_310 var5 = (class_310)this;
                  float var6 = var5.method_61966() != null ? var5.method_61966().method_60637(true) : 1.0F;
                  if (var4 != null && var4.getEventBus() != null && var4.getEventBus().hasSubscribers(PostBlitRenderEvent.class)) {
                     VIE$POST_BLIT_EVENT.update(var6, FrostedBackground.hasBlurredBackdrop());
                     var4.getEventBus().post(VIE$POST_BLIT_EVENT);
                  }

                  class_437 var7 = ((class_310)this).field_1755;
                  if (var7 instanceof NewClickGUI var8) {
                     var8.renderPostBlit();
                  } else if (var7 instanceof CustomTitleScreen var9) {
                     var9.renderPostBlit();
                  } else if (var7 instanceof VieOSScreen var10) {
                     var10.renderPostBlit();
                  }
               }
            }
         } catch (Throwable var18) {
            VieClientMod.LOGGER.error("[VieClient] Post-blit render error", var18);
         } finally {
            FrostedBackground.discardQueuedWidgets();
            NVGRenderer.forceCloseFrame();
            GlStateManager._glBindFramebuffer(36160, 0);
            GL11.glDrawBuffer(1029);
            GL11.glReadBuffer(1029);
            GlStateManager._colorMask(true, true, true, true);
         }
      }
   }

   @Inject(method = "method_1583", at = @At("HEAD"), cancellable = true)
   private void onItemUseHead(CallbackInfo ci) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null && var2.getEventBus() != null) {
         PreItemUseEvent var3 = new PreItemUseEvent(this.field_1752);
         var2.getEventBus().post(var3);
         if (var3.isCancelled()) {
            ci.cancel();
         } else {
            this.field_1752 = var3.cooldown;
         }
      }
   }

   @Inject(method = "method_1583", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1799;method_45435(Lnet/minecraft/class_7699;)Z"))
   private void onDoItemUseHand(CallbackInfo ci, @Local class_1799 itemStack) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null && var3.getModuleManager() != null) {
         FastUse var4 = (FastUse)var3.getModuleManager().getModule(FastUse.class);
         if (var4 != null && var4.isEnabled()) {
            this.field_1752 = var4.getItemUseCooldown(itemStack);
         }
      }
   }

   @Inject(method = "method_1574", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_310;method_1508()V", shift = Shift.AFTER))
   private void onTickDrainRightClick(CallbackInfo ci) {
      if (this.vieclient$rightClickRequested) {
         this.vieclient$rightClickRequested = false;
         class_310 var2 = (class_310)this;
         if (var2.field_1724 != null && var2.field_1687 != null && var2.field_1761 != null) {
            ((MinecraftClientAccessor)var2).invokeDoItemUse();
         }
      }
   }
}
