package me.drake.vieclient.mixin;

import me.drake.vieclient.modules.render.ItemReplacement;
import net.minecraft.class_10442;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(class_10442.class)
public class MixinItemModelManager {
   @ModifyVariable(method = "method_65598", at = @At("HEAD"), argsOnly = true)
   private class_1799 vie$modifyItemModelStack(class_1799 stack) {
      return ItemReplacement.getReplaced(stack);
   }
}
