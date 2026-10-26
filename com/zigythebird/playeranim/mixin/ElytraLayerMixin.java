package com.zigythebird.playeranim.mixin;

import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranim.util.RenderUtil;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.class_10034;
import net.minecraft.class_1007;
import net.minecraft.class_11659;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_583;
import net.minecraft.class_591;
import net.minecraft.class_979;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_979.class)
public abstract class ElytraLayerMixin<S extends class_10034, M extends class_583<S>> extends class_3887<S, M> {
   private ElytraLayerMixin(class_3883<S, M> renderLayerParent) {
      super(renderLayerParent);
   }

   @Inject(
      method = "method_17161(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10034;FF)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_10197;method_64078(Lnet/minecraft/class_10186$class_10190;Lnet/minecraft/class_5321;Lnet/minecraft/class_3879;Ljava/lang/Object;Lnet/minecraft/class_1799;Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_2960;II)V"
      )
   )
   private void inject(class_4587 poseStack, class_11659 submitNodeCollector, int i, S humanoidRenderState, float f, float g, CallbackInfo ci) {
      if (humanoidRenderState instanceof IAvatarAnimationState animationState) {
         AvatarAnimManager emote = animationState.playerAnimLib$getAnimManager();
         if (emote != null && emote.isActive() && this.field_17155 instanceof class_1007<?> playerRenderer) {
            ((class_591)playerRenderer.method_4038()).field_3391.method_22703(poseStack);
            poseStack.method_22904(0.0, 0.0, 0.125);
            PlayerAnimBone bone = emote.get3DTransform(new PlayerAnimBone("elytra"));
            bone.applyOtherBone(emote.get3DTransform(new PlayerAnimBone("cape")));
            bone.positionY *= -1.0F;
            RenderUtil.translateMatrixToBone(poseStack, bone);
            poseStack.method_22904(0.0, 0.0, -0.125);
         }
      }
   }
}
