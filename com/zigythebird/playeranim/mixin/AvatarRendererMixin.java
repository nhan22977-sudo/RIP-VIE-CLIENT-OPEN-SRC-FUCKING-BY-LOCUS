package com.zigythebird.playeranim.mixin;

import com.zigythebird.playeranim.accessors.IAnimatedAvatar;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import net.minecraft.class_10055;
import net.minecraft.class_1007;
import net.minecraft.class_11890;
import net.minecraft.class_591;
import net.minecraft.class_742;
import net.minecraft.class_922;
import net.minecraft.class_5617.class_5618;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_1007.class, priority = 2000)
public abstract class AvatarRendererMixin extends class_922<class_742, class_10055, class_591> {
   public AvatarRendererMixin(class_5618 context, class_591 model, float shadowRadius) {
      super(context, model, shadowRadius);
   }

   @Inject(method = "method_62604(Lnet/minecraft/class_11890;Lnet/minecraft/class_10055;F)V", at = @At("HEAD"))
   private void modifyRenderState(class_11890 avatar, class_10055 avatarRenderState, float f, CallbackInfo ci) {
      if (avatar instanceof IAnimatedAvatar abstractClientPlayer) {
         AvatarAnimManager animation = abstractClientPlayer.playerAnimLib$getAnimManager();
         animation.setTickDelta(f);
         avatarRenderState.playerAnimLib$setAnimManager(animation);
      }
   }
}
