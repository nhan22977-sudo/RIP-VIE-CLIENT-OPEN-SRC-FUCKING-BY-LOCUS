package io.github.kosmx.emotes.arch.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.kosmx.emotes.main.emotePlay.instances.SoundDirectInstance;
import io.github.kosmx.emotes.main.emotePlay.instances.SoundEventInstance;
import net.minecraft.class_1113;
import net.minecraft.class_1140;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(class_1140.class)
public class SoundEngineMixin {
   @WrapOperation(method = "method_4849(Lnet/minecraft/class_1113;)F", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_3532;method_15363(FFF)F"))
   private float emotecraft$extendOctaves(float value, float min, float max, Operation<Float> original, @Local(argsOnly = true) class_1113 sound) {
      return !(sound instanceof SoundEventInstance) && !(sound instanceof SoundDirectInstance) ? (Float)original.call(new Object[]{value, min, max}) : value;
   }
}
