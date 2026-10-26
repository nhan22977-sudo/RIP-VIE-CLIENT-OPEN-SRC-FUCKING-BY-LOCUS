package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.modules.client.FakeStats;
import me.drake.vieclient.modules.client.InventoryProtect;
import me.drake.vieclient.modules.render.CustomCrosshair;
import me.drake.vieclient.modules.render.ItemReplacement;
import me.drake.vieclient.render.TessellatorRenderer;
import net.minecraft.class_1799;
import net.minecraft.class_1814;
import net.minecraft.class_2561;
import net.minecraft.class_266;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_329.class, priority = 1)
public abstract class MixinInGameHud {
   @Shadow
   private void method_1757(class_332 context, class_266 objective) {
   }

   @Inject(method = "method_55803(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V", at = @At("HEAD"))
   private void onBeforeRenderScoreboardSidebar(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
      if (FakeStats.isActive()) {
         FakeStats.ensureObjectiveReady();
      }
   }

   @Inject(method = "method_1757(Lnet/minecraft/class_332;Lnet/minecraft/class_266;)V", at = @At("HEAD"), cancellable = true)
   private void onRenderScoreboardSidebarObjective(class_332 context, class_266 objective, CallbackInfo ci) {
      if (FakeStats.isActive()) {
         class_266 var4 = FakeStats.getCustomObjective();
         if (objective != var4) {
            ci.cancel();
            if (var4 != null) {
               this.method_1757(context, var4);
            }
         }
      }
   }

   @Inject(method = "method_1736", at = @At("HEAD"), cancellable = true)
   private void onRenderCrosshair(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
      if (CustomCrosshair.isActive()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_1749", at = @At("HEAD"), cancellable = true)
   private void onRenderHeldItemTooltip(class_332 context, CallbackInfo ci) {
      if (InventoryProtect.shouldHideItemName()) {
         ci.cancel();
      }
   }

   @ModifyVariable(method = "method_1762", at = @At("HEAD"), argsOnly = true)
   private class_1799 onRenderHotbarItem(class_1799 stack) {
      return InventoryProtect.shouldMaskHotbar() ? InventoryProtect.get().getMaskedStack(null, stack) : ItemReplacement.getReplaced(stack);
   }

   @Redirect(method = "method_1749", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1799;method_7964()Lnet/minecraft/class_2561;"))
   private class_2561 onGetHeldItemName(class_1799 stack) {
      return ItemReplacement.getReplaced(stack).method_7964();
   }

   @Redirect(method = "method_1749", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1799;method_7932()Lnet/minecraft/class_1814;"))
   private class_1814 onGetHeldItemRarity(class_1799 stack) {
      return ItemReplacement.getReplaced(stack).method_7932();
   }

   @Inject(method = "method_1753", at = @At("TAIL"))
   private void onRender(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
      VieClientMod var4 = VieClientMod.getInstance();
      if (var4 != null) {
         TessellatorRenderer.setContext(context);
         float var5 = tickCounter.method_60637(true);
         if (var4.getEventBus().hasSubscribers(RenderEvent.class)) {
            RenderEvent var6 = new RenderEvent(RenderEvent.Stage.HUD_2D, var5, 0.0F);
            var6.setDrawContext(context);
            var4.getEventBus().post(var6);
         }
      }
   }
}
