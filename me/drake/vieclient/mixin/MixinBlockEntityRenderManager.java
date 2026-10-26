package me.drake.vieclient.mixin;

import me.drake.vieclient.modules.render.Optimization;
import net.minecraft.class_11659;
import net.minecraft.class_11954;
import net.minecraft.class_12075;
import net.minecraft.class_4587;
import net.minecraft.class_824;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_824.class)
public class MixinBlockEntityRenderManager {
   @Inject(
      method = "method_3555(Lnet/minecraft/class_11954;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void vieclient$cullBlockEntity(class_11954 state, class_4587 matrices, class_11659 queue, class_12075 cameraState, CallbackInfo ci) {
      if (state != null && Optimization.shouldCullBlockEntity(state.field_62673, state.field_62675)) {
         ci.cancel();
      }
   }
}
