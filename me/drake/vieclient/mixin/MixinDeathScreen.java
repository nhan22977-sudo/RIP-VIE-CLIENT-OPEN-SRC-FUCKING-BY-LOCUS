package me.drake.vieclient.mixin;

import me.drake.vieclient.modules.client.NameProtect;
import net.minecraft.class_2561;
import net.minecraft.class_418;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(class_418.class)
public class MixinDeathScreen {
   @ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true, index = 1)
   private static class_2561 vie$protectDeathMessage(class_2561 message) {
      return NameProtect.staticReplaceText(message);
   }
}
