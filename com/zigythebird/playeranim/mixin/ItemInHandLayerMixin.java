package com.zigythebird.playeranim.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.class_10426;
import net.minecraft.class_10444;
import net.minecraft.class_11659;
import net.minecraft.class_1306;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import net.minecraft.class_989;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_989.class)
public class ItemInHandLayerMixin {
   @Unique
   private final PlayerAnimBone playerAnimLib$rightItem = new PlayerAnimBone("right_item");
   @Unique
   private final PlayerAnimBone playerAnimLib$leftItem = new PlayerAnimBone("left_item");

   @Inject(
      method = "method_4192(Lnet/minecraft/class_10426;Lnet/minecraft/class_10444;Lnet/minecraft/class_1799;Lnet/minecraft/class_1306;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4587;method_22907(Lorg/joml/Quaternionfc;)V", ordinal = 0)
   )
   private void changeItemLocation(
      class_10426 renderState,
      class_10444 itemStackRenderState,
      class_1799 itemStack,
      class_1306 arm,
      class_4587 matrices,
      class_11659 submitNodeCollector,
      int i,
      CallbackInfo ci,
      @Share("pal_active") LocalBooleanRef active
   ) {
      if (renderState instanceof IAvatarAnimationState state && state.playerAnimLib$getAnimManager() != null && state.playerAnimLib$getAnimManager().isActive()
         )
       {
         AvatarAnimManager anim = state.playerAnimLib$getAnimManager();
         active.set(true);
         PlayerAnimBone bone;
         if (arm == class_1306.field_6182) {
            bone = this.playerAnimLib$leftItem;
         } else {
            bone = this.playerAnimLib$rightItem;
         }

         bone.setToInitialPose();
         anim.get3DTransform(bone);
         matrices.method_46416(bone.getPosX() / 16.0F, -bone.getPosY() / 16.0F, bone.getPosZ() / 16.0F);
      } else {
         active.set(false);
      }
   }

   @Inject(
      method = "method_4192(Lnet/minecraft/class_10426;Lnet/minecraft/class_10444;Lnet/minecraft/class_1799;Lnet/minecraft/class_1306;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;I)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_10444;method_65604(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;III)V")
   )
   private void changeItemRotationAndScale(
      class_10426 renderState,
      class_10444 itemStackRenderState,
      class_1799 itemStack,
      class_1306 arm,
      class_4587 poseStack,
      class_11659 submitNodeCollector,
      int packedLight,
      CallbackInfo ci,
      @Share("pal_active") LocalBooleanRef active
   ) {
      if (active.get()) {
         PlayerAnimBone bone;
         if (arm == class_1306.field_6182) {
            bone = this.playerAnimLib$leftItem;
         } else {
            bone = this.playerAnimLib$rightItem;
         }

         if (bone.getRotY() != 0.0F) {
            poseStack.method_22907(class_7833.field_40718.rotation(-bone.getRotY()));
         }

         if (bone.getRotZ() != 0.0F) {
            poseStack.method_22907(class_7833.field_40716.rotation(-bone.getRotZ()));
         }

         if (bone.getRotX() != 0.0F) {
            poseStack.method_22907(class_7833.field_40714.rotation(-bone.getRotX()));
         }

         poseStack.method_22905(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
      }
   }
}
