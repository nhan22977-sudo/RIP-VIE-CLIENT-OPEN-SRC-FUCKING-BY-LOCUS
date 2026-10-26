package me.drake.vieclient.mixin;

import net.minecraft.class_310;
import net.minecraft.class_742;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_742.class)
public abstract class MixinAbstractClientPlayerEntity {
   @Inject(method = "method_52814", at = @At("HEAD"), cancellable = true)
   private void onGetSkin(CallbackInfoReturnable cir) {
      class_742 var2 = (class_742)this;
      class_310 var3 = class_310.method_1551();
      if (var2.method_5628() == -6969 && var3 != null && var3.field_1724 != null) {
         cir.setReturnValue(var3.field_1724.method_52814());
      }
   }
}
