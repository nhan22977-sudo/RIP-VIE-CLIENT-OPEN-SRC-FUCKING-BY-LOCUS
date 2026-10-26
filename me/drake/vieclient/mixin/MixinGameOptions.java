package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.module.ModuleManager;
import net.minecraft.class_315;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_315.class)
public class MixinGameOptions {
   @Inject(method = "method_1636", at = @At("RETURN"))
   private void onLoad(CallbackInfo ci) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null) {
         ModuleManager var3 = var2.getModuleManager();
         if (var3 != null) {
            var3.onGameOptionsLoad();
         }
      }
   }
}
