package com.zigythebird.playeranim.mixin;

import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranim.util.RenderUtil;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.class_10042;
import net.minecraft.class_11659;
import net.minecraft.class_12075;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import net.minecraft.class_922;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_922.class)
public class LivingEntityRendererMixin<S extends class_10042> {
   @Inject(
      method = "method_4054(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;Lnet/minecraft/class_12075;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_922;method_4042(Lnet/minecraft/class_10042;Lnet/minecraft/class_4587;)V")
   )
   private void doTranslations(S livingEntityRenderState, class_4587 poseStack, class_11659 submitNodeCollector, class_12075 cameraRenderState, CallbackInfo ci) {
      if (livingEntityRenderState instanceof IAvatarAnimationState avatarRenderState) {
         AvatarAnimManager animationPlayer = avatarRenderState.playerAnimLib$getAnimManager();
         if (animationPlayer != null && animationPlayer.isActive()) {
            avatarRenderState.playerAnimLib$getAnimManager()
               .handleAnimations(animationPlayer.getTickDelta(), false, avatarRenderState.playerAnimLib$isFirstPersonPass());
            poseStack.method_22905(-1.0F, -1.0F, 1.0F);
            if (avatarRenderState.playerAnimLib$isFirstPersonPass()) {
               poseStack.method_46416(0.0F, livingEntityRenderState.field_53331, 0.0F);
               if (animationPlayer.isFirstPersonFollowsCamera()) {
                  poseStack.method_22907(class_7833.field_40716.rotationDegrees(-livingEntityRenderState.field_53447));
                  poseStack.method_22907(class_7833.field_40714.rotationDegrees(-livingEntityRenderState.field_53448));
               }

               poseStack.method_46416(0.0F, -livingEntityRenderState.field_53331, 0.0F);
            }

            PlayerAnimBone body = animationPlayer.get3DTransform(new PlayerAnimBone("body"));
            poseStack.method_22904(-body.getPosX() / 16.0F, body.getPosY() / 16.0F + 0.75, body.getPosZ() / 16.0F);
            body.rotX *= -1.0F;
            body.rotY *= -1.0F;
            RenderUtil.rotateMatrixAroundBone(poseStack, body);
            poseStack.method_22905(body.getScaleX(), body.getScaleY(), body.getScaleZ());
            poseStack.method_22904(0.0, -0.75, 0.0);
            poseStack.method_22905(-1.0F, -1.0F, 1.0F);
         }
      }
   }
}
