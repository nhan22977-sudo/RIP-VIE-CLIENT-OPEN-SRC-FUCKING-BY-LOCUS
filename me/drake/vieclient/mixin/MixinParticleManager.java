package me.drake.vieclient.mixin;

import me.drake.vieclient.modules.render.Optimization;
import net.minecraft.class_2394;
import net.minecraft.class_702;
import net.minecraft.class_703;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_702.class)
public class MixinParticleManager {
   @Inject(method = "method_3056(Lnet/minecraft/class_2394;DDDDDD)Lnet/minecraft/class_703;", at = @At("RETURN"))
   private void vieclient$cullParticle(
      class_2394 parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable cir
   ) {
      class_703 var15 = (class_703)cir.getReturnValue();
      if (var15 != null && Optimization.shouldCullParticle(parameters, x, y, z)) {
         var15.method_3085();
      }
   }
}
