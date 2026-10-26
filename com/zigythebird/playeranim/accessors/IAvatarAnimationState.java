package com.zigythebird.playeranim.accessors;

import com.zigythebird.playeranim.animation.AvatarAnimManager;

public interface IAvatarAnimationState {
   boolean playerAnimLib$isFirstPersonPass();

   void playerAnimLib$setFirstPersonPass(boolean var1);

   void playerAnimLib$setAnimManager(AvatarAnimManager var1);

   AvatarAnimManager playerAnimLib$getAnimManager();
}
