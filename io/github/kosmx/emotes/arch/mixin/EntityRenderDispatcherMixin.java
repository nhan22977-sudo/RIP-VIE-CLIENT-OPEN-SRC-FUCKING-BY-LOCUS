package io.github.kosmx.emotes.arch.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.kosmx.emotes.arch.screen.utils.UnsafeMannequin;
import net.minecraft.class_1297;
import net.minecraft.class_4184;
import net.minecraft.class_898;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_898.class)
public class EntityRenderDispatcherMixin {
   @Shadow
   public class_4184 field_4686;

   @Inject(method = "method_23168(Lnet/minecraft/class_1297;)D", at = @At("HEAD"), cancellable = true)
   private void emotecraft$fixNPE(CallbackInfoReturnable<Double> cir, @Local(argsOnly = true) class_1297 entity) {
      if (this.field_4686 == null || entity instanceof UnsafeMannequin) {
         cir.setReturnValue(Double.MAX_VALUE);
      }
   }
}
