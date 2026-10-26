package com.zigythebird.playeranim.mixin;

import com.zigythebird.playeranim.accessors.IBoneUpdater;
import com.zigythebird.playeranim.accessors.ICapeLayer;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranim.util.RenderUtil;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.class_10055;
import net.minecraft.class_630;
import net.minecraft.class_9950;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_9950.class, priority = 2001)
public class PlayerCapeModelMixin implements IBoneUpdater, ICapeLayer {
   @Shadow
   @Final
   private class_630 field_52927;

   @Inject(method = "method_62110(Lnet/minecraft/class_10055;)V", at = @At("TAIL"))
   private void setupAnim(class_10055 avatarRenderState, CallbackInfo ci) {
      AvatarAnimManager emote = avatarRenderState.playerAnimLib$getAnimManager();
      if (emote != null && emote.isActive()) {
         PlayerAnimBone bone = RenderUtil.copyVanillaPart(this.field_52927, new PlayerAnimBone("cape"));
         bone.rotX -= (float) Math.PI;
         bone.rotZ -= (float) Math.PI;
         bone.rotX *= -1.0F;
         bone.rotY *= -1.0F;
         bone = emote.get3DTransform(bone);
         bone.rotX *= -1.0F;
         bone.rotY *= -1.0F;
         bone.rotX += (float) Math.PI;
         bone.rotZ += (float) Math.PI;
         this.pal$updatePart(emote, this.field_52927, bone);
      } else {
         this.pal$resetAll(emote);
      }
   }

   @Override
   public void pal$updatePart(AvatarAnimManager emote, class_630 part, PlayerAnimBone bone) {
      RenderUtil.translatePartToCape(part, bone, part.method_41921());
      this.applyBend(emote, bone.getBend());
   }

   @Override
   public void pal$resetAll(@Nullable AvatarAnimManager emote) {
      this.resetBend();
   }
}
