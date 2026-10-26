package io.github.kosmx.emotes.arch.mixin;

import io.github.kosmx.emotes.arch.screen.utils.UnsafeMannequin;
import net.minecraft.class_1309;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_922.class)
public class LivingEntityRendererMixin {
   @Inject(method = "method_4055(Lnet/minecraft/class_1309;D)Z", at = @At("HEAD"), cancellable = true)
   private void emotecraft$shouldShowName(class_1309 livingEntity, double d, CallbackInfoReturnable<Boolean> cir) {
      if (livingEntity instanceof UnsafeMannequin) {
         cir.setReturnValue(false);
      }
   }
}
