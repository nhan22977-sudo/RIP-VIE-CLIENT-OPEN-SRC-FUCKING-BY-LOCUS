package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.module.ModuleManager;
import me.drake.vieclient.modules.player.SkinProtect;
import net.minecraft.class_742;
import net.minecraft.class_8685;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_742.class)
public abstract class MixinSkinProtect {
   @Inject(method = "method_52814", at = @At("HEAD"), cancellable = true)
   private void vieSkinProtect(CallbackInfoReturnable cir) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null) {
         ModuleManager var3 = var2.getModuleManager();
         if (var3 != null) {
            SkinProtect var4 = (SkinProtect)var3.getModule(SkinProtect.class);
            if (var4 != null && var4.isEnabled()) {
               class_8685 var5 = var4.replacementSkin();
               if (var5 != null) {
                  class_742 var6 = (class_742)this;
                  if (var4.shouldReplace(var6.method_5667())) {
                     cir.setReturnValue(var5);
                  }
               }
            }
         }
      }
   }
}
