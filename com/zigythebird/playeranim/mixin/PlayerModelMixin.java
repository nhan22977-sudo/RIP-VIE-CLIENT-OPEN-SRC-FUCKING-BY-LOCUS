package com.zigythebird.playeranim.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import com.zigythebird.playeranim.accessors.IBoneUpdater;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranim.util.RenderUtil;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import java.util.function.Function;
import net.minecraft.class_10055;
import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_572;
import net.minecraft.class_591;
import net.minecraft.class_630;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_591.class, priority = 2001)
public class PlayerModelMixin extends class_572<class_10055> implements IBoneUpdater {
   @Unique
   private final PlayerAnimBone pal$head = new PlayerAnimBone("head");
   @Unique
   private final PlayerAnimBone pal$torso = new PlayerAnimBone("torso");
   @Unique
   private final PlayerAnimBone pal$rightArm = new PlayerAnimBone("right_arm");
   @Unique
   private final PlayerAnimBone pal$leftArm = new PlayerAnimBone("left_arm");
   @Unique
   private final PlayerAnimBone pal$rightLeg = new PlayerAnimBone("right_leg");
   @Unique
   private final PlayerAnimBone pal$leftLeg = new PlayerAnimBone("left_leg");

   public PlayerModelMixin(class_630 root, Function<class_2960, class_1921> renderType) {
      super(root, renderType);
   }

   @Inject(method = "method_62110(Lnet/minecraft/class_10055;)V", at = @At("HEAD"))
   private void setDefaultBeforeRender(class_10055 avatarRenderState, CallbackInfo ci) {
      this.field_3398.field_3665 = true;
   }

   @Inject(method = "method_62110(Lnet/minecraft/class_10055;)V", at = @At("RETURN"))
   private void setupPlayerAnimation(class_10055 avatarRenderState, CallbackInfo ci) {
      if (avatarRenderState instanceof IAvatarAnimationState state && state.playerAnimLib$getAnimManager() != null) {
         AvatarAnimManager emote = state.playerAnimLib$getAnimManager();
         if (emote.isActive()) {
            this.pal$updatePart(emote, this.field_3398, this.pal$head);
            this.pal$updatePart(emote, this.field_3401, this.pal$rightArm);
            this.pal$updatePart(emote, this.field_27433, this.pal$leftArm);
            this.pal$updatePart(emote, this.field_3392, this.pal$rightLeg);
            this.pal$updatePart(emote, this.field_3397, this.pal$leftLeg);
            this.pal$updatePart(emote, this.field_3391, this.pal$torso);
         } else {
            this.pal$resetAll(emote);
         }

         if (state.playerAnimLib$isFirstPersonPass()) {
            this.field_3398.field_3665 = false;
            this.field_3391.field_3665 = false;
            this.field_3397.field_3665 = false;
            this.field_3392.field_3665 = false;
            FirstPersonConfiguration config = emote.getFirstPersonConfiguration();
            this.field_3401.field_3665 = config.isShowRightArm();
            this.field_27433.field_3665 = config.isShowLeftArm();
         }
      } else {
         this.pal$resetAll(null);
      }
   }

   @Override
   public void pal$updatePart(AvatarAnimManager emote, class_630 part, PlayerAnimBone bone) {
      RenderUtil.copyVanillaPart(part, bone);
      emote.updatePart(part, bone);
   }

   @WrapWithCondition(
      method = "method_72859(Lnet/minecraft/class_10055;Lnet/minecraft/class_1306;Lnet/minecraft/class_4587;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_630;method_22703(Lnet/minecraft/class_4587;)V")
   )
   private boolean translateToHand(class_630 modelPart, class_4587 poseStack, @Local(argsOnly = true) class_10055 avatarRenderState) {
      if (avatarRenderState instanceof IAvatarAnimationState state
         && state.playerAnimLib$getAnimManager() != null
         && state.playerAnimLib$getAnimManager().isActive()) {
         poseStack.method_46416(modelPart.field_3657 / 16.0F, modelPart.field_3656 / 16.0F, modelPart.field_3655 / 16.0F);
         if (modelPart.field_3654 != 0.0F || modelPart.field_3675 != 0.0F || modelPart.field_3674 != 0.0F) {
            RenderUtil.rotateZYX(poseStack.method_23760(), modelPart.field_3674, modelPart.field_3675, modelPart.field_3654);
         }

         poseStack.method_22904(0.0, (modelPart.field_37939 - 1.0F) * 0.609375, (modelPart.field_37940 - 1.0F) * 0.0625);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void pal$resetAll(@Nullable AvatarAnimManager emote) {
   }
}
