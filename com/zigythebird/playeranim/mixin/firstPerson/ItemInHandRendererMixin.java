package com.zigythebird.playeranim.mixin.firstPerson;

import com.zigythebird.playeranim.accessors.IAnimatedAvatar;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import net.minecraft.class_11659;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_746;
import net.minecraft.class_759;
import net.minecraft.class_811;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_759.class)
public class ItemInHandRendererMixin {
   @Shadow
   private float field_4043;
   @Shadow
   private float field_4052;
   @Shadow
   @Final
   private class_310 field_4050;

   @Inject(method = "method_22976(FLnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_746;I)V", at = @At("HEAD"), cancellable = true)
   private void disableDefaultItemIfNeeded(float f, class_4587 poseStack, class_11659 submitNodeCollector, class_746 localPlayer, int i, CallbackInfo ci) {
      if (localPlayer instanceof IAnimatedAvatar player
         && player.playerAnimLib$getAnimManager().isActive()
         && player.playerAnimLib$getAnimManager().getFirstPersonTransitionProgress() == 1.0F) {
         ci.cancel();
      }
   }

   @Inject(method = "method_3220()V", at = @At("TAIL"))
   private void firstPersonTransitionLogic(CallbackInfo ci) {
      if (this.field_4050.field_1724 instanceof IAnimatedAvatar player) {
         AvatarAnimManager manager = player.playerAnimLib$getAnimManager();
         if (manager.getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL) {
            float progress = 1.0F - manager.getFirstPersonTransitionProgress();
            progress = 0.4F + 0.6F * progress;
            this.field_4043 *= progress;
            this.field_4052 *= progress;
         }
      }
   }

   @Inject(
      method = "method_3233(Lnet/minecraft/class_1309;Lnet/minecraft/class_1799;Lnet/minecraft/class_811;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_10444;method_65604(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;III)V"),
      cancellable = true
   )
   private void cancelItemRender(
      class_1309 entity, class_1799 itemStack, class_811 transformType, class_4587 poseStack, class_11659 submitNodeCollector, int i, CallbackInfo ci
   ) {
      if (entity instanceof IAnimatedAvatar player
         && player.playerAnimLib$getAnimManager().isActive()
         && entity == class_310.method_1551().method_1560()
         && !class_310.method_1551().field_1773.method_19418().method_19333()
         && player.playerAnimLib$getAnimManager().getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL) {
         FirstPersonConfiguration config = player.playerAnimLib$getAnimManager().getFirstPersonConfiguration();
         if (transformType != class_811.field_4322 && transformType != class_811.field_4320) {
            if (!config.isShowLeftItem()) {
               ci.cancel();
            }
         } else if (!config.isShowRightItem()) {
            ci.cancel();
         }
      }
   }
}
