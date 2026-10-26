package com.zigythebird.playeranim.accessors;

import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.class_630;
import org.jetbrains.annotations.Nullable;

public interface IBoneUpdater {
   default void pal$updatePart(AvatarAnimManager emote, class_630 part, PlayerAnimBone bone) {
   }

   default void pal$resetAll(@Nullable AvatarAnimManager emote) {
   }
}
