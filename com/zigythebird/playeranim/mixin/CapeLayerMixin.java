package com.zigythebird.playeranim.mixin;

import com.zigythebird.playeranim.animation.AvatarAnimManager;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_3883;
import net.minecraft.class_3887;
import net.minecraft.class_4587;
import net.minecraft.class_591;
import net.minecraft.class_972;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_972.class)
public abstract class CapeLayerMixin extends class_3887<class_10055, class_591> {
   public CapeLayerMixin(class_3883<class_10055, class_591> renderer) {
      super(renderer);
   }

   @Inject(
      method = "method_4177(Lnet/minecraft/class_4587;Lnet/minecraft/class_11659;ILnet/minecraft/class_10055;FF)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_11659;method_73489(Lnet/minecraft/class_3879;Ljava/lang/Object;Lnet/minecraft/class_4587;Lnet/minecraft/class_1921;IIILnet/minecraft/class_11683$class_11792;)V"
      )
   )
   private void render(class_4587 poseStack, class_11659 submitNodeCollector, int i, class_10055 avatarRenderState, float f, float g, CallbackInfo ci) {
      AvatarAnimManager emote = avatarRenderState.playerAnimLib$getAnimManager();
      if (emote != null && emote.isActive()) {
         ((class_591)this.method_17165()).field_3391.method_22703(poseStack);
      }
   }
}
