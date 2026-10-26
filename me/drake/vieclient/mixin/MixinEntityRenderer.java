package me.drake.vieclient.mixin;

import me.drake.vieclient.modules.render.Optimization;
import net.minecraft.class_10017;
import net.minecraft.class_1297;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_897.class)
public abstract class MixinEntityRenderer {
   @Inject(method = "method_73154(Lnet/minecraft/class_1297;Lnet/minecraft/class_10017;)V", at = @At("HEAD"), cancellable = true)
   private void vieclient$cullEntityShadow(class_1297 entity, class_10017 state, CallbackInfo ci) {
      if (Optimization.shouldDisableShadows()) {
         state.field_61822 = 0.0F;
         ci.cancel();
      }
   }
}
