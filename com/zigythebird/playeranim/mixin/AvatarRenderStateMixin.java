package com.zigythebird.playeranim.mixin;

import com.zigythebird.playeranim.accessors.IAvatarAnimationState;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import net.minecraft.class_10055;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(class_10055.class)
public class AvatarRenderStateMixin implements IAvatarAnimationState {
   @Unique
   boolean playerAnimLib$isFirstPersonPass = false;
   @Unique
   AvatarAnimManager playerAnimLib$avatarAnimManager = null;

   @Override
   public boolean playerAnimLib$isFirstPersonPass() {
      return this.playerAnimLib$isFirstPersonPass;
   }

   @Override
   public void playerAnimLib$setFirstPersonPass(boolean value) {
      this.playerAnimLib$isFirstPersonPass = value;
   }

   @Override
   public void playerAnimLib$setAnimManager(AvatarAnimManager manager) {
      this.playerAnimLib$avatarAnimManager = manager;
   }

   @NotNull
   @Override
   public AvatarAnimManager playerAnimLib$getAnimManager() {
      return this.playerAnimLib$avatarAnimManager;
   }
}
