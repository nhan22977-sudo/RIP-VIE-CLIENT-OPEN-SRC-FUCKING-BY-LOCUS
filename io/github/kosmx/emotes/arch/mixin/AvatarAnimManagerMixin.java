package io.github.kosmx.emotes.arch.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import io.github.kosmx.emotes.arch.screen.utils.UnsafeMannequin;
import net.minecraft.class_11890;
import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AvatarAnimManager.class)
public class AvatarAnimManagerMixin {
   @Shadow
   @Final
   private class_11890 avatar;

   @WrapOperation(method = "handleAnimations(FZZ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_310;method_1493()Z"))
   private boolean emotecraft$unpause(class_310 instance, Operation<Boolean> original) {
      return this.avatar instanceof UnsafeMannequin ? false : (Boolean)original.call(new Object[]{instance});
   }
}
