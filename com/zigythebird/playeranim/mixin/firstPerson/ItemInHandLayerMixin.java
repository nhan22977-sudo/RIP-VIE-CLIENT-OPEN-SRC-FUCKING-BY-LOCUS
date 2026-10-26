package com.zigythebird.playeranim.mixin.firstPerson;

import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import net.minecraft.class_10426;
import net.minecraft.class_10444;
import net.minecraft.class_11659;
import net.minecraft.class_1306;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_989;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_989.class)
public class ItemInHandLayerMixin<S extends class_10426> {
   @Inject(
      method = "method_4192(Lnet/minecraft/class_10426;Lnet/minecraft/class_10444;Lnet/minecraft/class_1799;Lnet/minecraft/class_1306;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V",
      at = @At("HEAD"),
      cancellable = true
   )
   private void dontRenderDisabledFirstPersonItem(
      S renderState,
      class_10444 itemStackRenderState,
      class_1799 itemStack,
      class_1306 arm,
      class_4587 poseStack,
      class_11659 submitNodeCollector,
      int i,
      CallbackInfo ci
   ) {
      if (renderState instanceof IAvatarAnimationState state
         && state.playerAnimLib$isFirstPersonPass()
         && state.playerAnimLib$getAnimManager() != null
         && state.playerAnimLib$getAnimManager().getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL) {
         FirstPersonConfiguration config = state.playerAnimLib$getAnimManager().getFirstPersonConfiguration();
         if (!config.isShowLeftItem() && arm == class_1306.field_6182 || !config.isShowRightItem() && arm == class_1306.field_6183) {
            ci.cancel();
         }
      }
   }
}
