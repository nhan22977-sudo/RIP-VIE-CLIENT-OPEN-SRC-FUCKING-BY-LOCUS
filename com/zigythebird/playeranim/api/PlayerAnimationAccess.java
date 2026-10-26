package com.zigythebird.playeranim.api;

import com.zigythebird.playeranim.accessors.IAnimatedAvatar;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.event.Event;
import net.minecraft.class_11890;
import net.minecraft.class_2960;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlayerAnimationAccess {
   public static final Event<PlayerAnimationAccess.AnimationRegister> REGISTER_ANIMATION_EVENT = new Event<>(listeners -> (player, animationStack) -> {
      for (PlayerAnimationAccess.AnimationRegister listener : listeners) {
         listener.registerAnimation(player, animationStack);
      }
   });

   public static AvatarAnimManager getPlayerAnimManager(class_11890 avatar) throws IllegalArgumentException {
      if (avatar instanceof IAnimatedAvatar animatedAvatar) {
         return animatedAvatar.playerAnimLib$getAnimManager();
      } else {
         throw new IllegalArgumentException(avatar + " is not a player or library mixins failed");
      }
   }

   @Nullable
   public static IAnimation getPlayerAnimationLayer(@NotNull class_11890 avatar, @NotNull class_2960 id) {
      if (avatar instanceof IAnimatedAvatar animatedPlayer) {
         return animatedPlayer.playerAnimLib$getAnimation(id);
      } else {
         throw new IllegalArgumentException(avatar + " is not a player or library mixins failed");
      }
   }

   @FunctionalInterface
   public interface AnimationRegister {
      void registerAnimation(@NotNull class_11890 var1, @NotNull AvatarAnimManager var2);
   }
}
