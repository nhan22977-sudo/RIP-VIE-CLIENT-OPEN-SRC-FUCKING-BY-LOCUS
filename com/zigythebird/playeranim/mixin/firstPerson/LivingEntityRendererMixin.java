package com.zigythebird.playeranim.mixin.firstPerson;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import net.minecraft.class_10017;
import net.minecraft.class_11659;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_5697;
import net.minecraft.class_922;
import net.minecraft.class_970;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = class_922.class, priority = 2000)
public class LivingEntityRendererMixin {
   @WrapWithCondition(
      method = "method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_3887;method_4199(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10017;FF)V"
      )
   )
   private boolean filterLayers(class_3887 layer, class_4587 poseStack, class_11659 submitNodeCollector, int i, class_10017 renderState, float v, float vq) {
      return renderState instanceof IAvatarAnimationState state && state.playerAnimLib$isFirstPersonPass()
         ? layer instanceof class_5697 || layer instanceof class_970
         : true;
   }
}
