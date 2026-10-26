package me.drake.vieclient.mixin;

import me.drake.vieclient.modules.render.Optimization;
import net.minecraft.class_12077;
import net.minecraft.class_1937;
import net.minecraft.class_243;
import net.minecraft.class_4066;
import net.minecraft.class_4184;
import net.minecraft.class_4597;
import net.minecraft.class_638;
import net.minecraft.class_9976;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_9976.class)
public class MixinWeatherRendering {
   @Inject(method = "method_62315", at = @At("HEAD"), cancellable = true)
   private void vieclient$cullWeatherBuild(class_1937 world, int ticks, float tickDelta, class_243 cameraPos, class_12077 state, CallbackInfo ci) {
      if (Optimization.shouldDisableWeather()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_62320", at = @At("HEAD"), cancellable = true)
   private void vieclient$cullWeatherRender(class_4597 vertexConsumers, class_243 cameraPos, class_12077 state, CallbackInfo ci) {
      if (Optimization.shouldDisableWeather()) {
         ci.cancel();
      }
   }

   @Inject(method = "method_62319", at = @At("HEAD"), cancellable = true)
   private void vieclient$cullWeatherParticlesAndSound(
      class_638 world, class_4184 camera, int ticks, class_4066 particlesMode, int soundChance, CallbackInfo ci
   ) {
      if (Optimization.shouldDisableWeather()) {
         ci.cancel();
      }
   }
}
