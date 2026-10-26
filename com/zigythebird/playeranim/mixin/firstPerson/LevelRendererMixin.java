package com.zigythebird.playeranim.mixin.firstPerson;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import com.zigythebird.playeranim.util.ClientUtil;
import net.minecraft.class_10017;
import net.minecraft.class_11658;
import net.minecraft.class_1297;
import net.minecraft.class_4184;
import net.minecraft.class_4604;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_761.class)
public class LevelRendererMixin {
   @ModifyExpressionValue(
      method = "method_72917(Lnet/minecraft/class_4184;Lnet/minecraft/class_4604;Lnet/minecraft/class_9779;Lnet/minecraft/class_11658;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4184;method_19333()Z")
   )
   private boolean fakeThirdPersonMode(boolean original, @Local(argsOnly = true) class_4184 camera, @Share("firstPerson") LocalBooleanRef isFirstPerson) {
      if (ClientUtil.shouldBeFirstPersonPass(camera)) {
         isFirstPerson.set(true);
         return true;
      } else {
         return original;
      }
   }

   @Inject(
      method = "method_72917(Lnet/minecraft/class_4184;Lnet/minecraft/class_4604;Lnet/minecraft/class_9779;Lnet/minecraft/class_11658;)V",
      at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z")
   )
   private void setRenderStateToFirstPerson(
      class_4184 camera,
      class_4604 frustum,
      class_9779 deltaTracker,
      class_11658 renderState,
      CallbackInfo ci,
      @Local class_1297 entity,
      @Local class_10017 entityRenderState,
      @Share("firstPerson") LocalBooleanRef isFirstPerson
   ) {
      if (entity == camera.method_19331() && isFirstPerson.get()) {
         ((IAvatarAnimationState)entityRenderState).playerAnimLib$setFirstPersonPass(true);
         entityRenderState.field_61823.clear();
         entityRenderState.field_61822 = 0.0F;
      }
   }
}
