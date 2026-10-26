package com.zigythebird.playeranim.mixin.firstPerson;

import com.llamalad7.mixinextras.sugar.Local;
import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import net.minecraft.class_10034;
import net.minecraft.class_11659;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_572;
import net.minecraft.class_970;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_970.class)
public abstract class HumanoidArmorLayerMixin<T extends class_10034, A extends class_572<T>> {
   @Inject(
      method = "method_4169(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_1799;Lnet/minecraft/class_1304;ILnet/minecraft/class_10034;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_10197;method_64077(Lnet/minecraft/class_10186$class_10190;Lnet/minecraft/class_5321;Lnet/minecraft/class_3879;Ljava/lang/Object;Lnet/minecraft/class_1799;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;II)V"
      ),
      cancellable = true
   )
   private void modifyArmorVisibility(
      class_4587 poseStack,
      class_11659 submitNodeCollector,
      class_1799 itemStack,
      class_1304 equipmentSlot,
      int i,
      class_10034 humanoidRenderState,
      CallbackInfo ci,
      @Local(ordinal = 0) class_572<?> humanoidModel
   ) {
      if (humanoidRenderState instanceof IAvatarAnimationState state && state.playerAnimLib$isFirstPersonPass()) {
         humanoidModel.field_3398.field_3665 = false;
         humanoidModel.field_3391.field_3665 = false;
         humanoidModel.field_3397.field_3665 = false;
         humanoidModel.field_3392.field_3665 = false;
         AvatarAnimManager emote = state.playerAnimLib$getAnimManager();
         if (equipmentSlot == class_1304.field_6174 && emote.getFirstPersonConfiguration().isShowArmor()) {
            humanoidModel.field_3401.field_3665 = emote.getFirstPersonConfiguration().isShowRightArm();
            humanoidModel.field_27433.field_3665 = emote.getFirstPersonConfiguration().isShowLeftArm();
         } else {
            ci.cancel();
         }
      }
   }
}
