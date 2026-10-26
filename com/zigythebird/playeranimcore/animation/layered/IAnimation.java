package com.zigythebird.playeranimcore.animation.layered;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import org.jetbrains.annotations.NotNull;

public interface IAnimation {
   FirstPersonConfiguration DEFAULT_FIRST_PERSON_CONFIG = new FirstPersonConfiguration();

   default void tick(AnimationData state) {
   }

   default void setupAnim(AnimationData state) {
   }

   boolean isActive();

   default PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
      return bone;
   }

   @NotNull
   default FirstPersonMode getFirstPersonMode() {
      return FirstPersonMode.NONE;
   }

   @NotNull
   default FirstPersonConfiguration getFirstPersonConfiguration() {
      return DEFAULT_FIRST_PERSON_CONFIG;
   }

   default boolean isFirstPersonFollowsCamera() {
      return false;
   }

   default int getFirstPersonTransitionLength() {
      return 0;
   }

   default boolean canRemove() {
      return false;
   }
}
