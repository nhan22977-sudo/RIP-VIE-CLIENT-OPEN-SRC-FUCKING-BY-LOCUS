package me.drake.vieclient.mixin;

import net.minecraft.class_10055;
import net.minecraft.class_591;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_591.class)
public abstract class MixinPlayerEntityModel {
   @Inject(method = "method_62110(Lnet/minecraft/class_10055;)V", at = @At("HEAD"))
   private void vie$restorePartVisibilityBeforeAngles(class_10055 state, CallbackInfo ci) {
      class_591 var3 = (class_591)this;
      boolean var4 = !state.field_53542;
      var3.method_2805(var4);
      var3.method_2838().field_3665 = var4;
   }
}
