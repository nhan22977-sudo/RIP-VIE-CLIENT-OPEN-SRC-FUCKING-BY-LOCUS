package com.zigythebird.playeranim.accessors;

import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import net.minecraft.class_2960;

public interface IAnimatedAvatar {
   AvatarAnimManager playerAnimLib$getAnimManager();

   IAnimation playerAnimLib$getAnimation(class_2960 var1);
}
