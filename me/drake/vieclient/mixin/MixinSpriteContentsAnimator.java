package me.drake.vieclient.mixin;

import me.drake.vieclient.modules.render.Optimization;
import net.minecraft.class_7764.class_12298;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_12298.class)
public class MixinSpriteContentsAnimator {
   @Inject(method = "method_76307", at = @At("HEAD"), cancellable = true)
   private void vieclient$freezeAnimatedTexture(CallbackInfo ci) {
      if (Optimization.shouldFreezeAnimatedTextures()) {
         ci.cancel();
      }
   }
}
