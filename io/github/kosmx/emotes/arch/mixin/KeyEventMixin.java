package io.github.kosmx.emotes.arch.mixin;

import io.github.kosmx.emotes.main.EmoteHolder;
import net.minecraft.class_304;
import net.minecraft.class_3675.class_306;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_304.class, priority = 2000)
public class KeyEventMixin {
   @Inject(method = "method_1420(Lnet/minecraft/class_3675$class_306;)V", at = @At("HEAD"))
   private static void keyPressCallback(class_306 key, CallbackInfo ci) {
      EmoteHolder.handleKeyPress(key);
   }
}
