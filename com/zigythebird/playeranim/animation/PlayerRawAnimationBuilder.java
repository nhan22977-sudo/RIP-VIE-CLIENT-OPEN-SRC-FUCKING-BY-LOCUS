package com.zigythebird.playeranim.animation;

import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.RawAnimation;
import net.minecraft.class_2960;

public class PlayerRawAnimationBuilder {
   private final RawAnimation rawAnimation = RawAnimation.begin();

   public static PlayerRawAnimationBuilder begin() {
      return new PlayerRawAnimationBuilder();
   }

   public PlayerRawAnimationBuilder thenPlay(class_2960 animation) {
      return this.then(animation, Animation.LoopType.DEFAULT);
   }

   public PlayerRawAnimationBuilder thenLoop(class_2960 animation) {
      return this.then(animation, Animation.LoopType.LOOP);
   }

   public PlayerRawAnimationBuilder thenWait(int ticks) {
      this.rawAnimation.thenWait(ticks);
      return this;
   }

   public PlayerRawAnimationBuilder thenPlayAndHold(class_2960 animation) {
      return this.then(animation, Animation.LoopType.HOLD_ON_LAST_FRAME);
   }

   public PlayerRawAnimationBuilder thenPlayXTimes(class_2960 animation, int playCount) {
      for (int i = 0; i < playCount; i++) {
         this.then(animation, i == playCount - 1 ? Animation.LoopType.DEFAULT : Animation.LoopType.PLAY_ONCE);
      }

      return this;
   }

   public PlayerRawAnimationBuilder then(class_2960 animation, Animation.LoopType loopType) {
      Animation instance = PlayerAnimResources.getAnimation(animation);
      if (instance == null) {
         throw new IllegalArgumentException("Could not find animation with name: " + animation);
      }

      this.rawAnimation.then(instance, loopType);
      return this;
   }

   public RawAnimation build() {
      return this.rawAnimation;
   }
}
