package com.zigythebird.playeranimcore.animation;

import com.zigythebird.playeranimcore.enums.AnimationStage;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public final class RawAnimation {
   private final List<RawAnimation.Stage> animationList = new ObjectArrayList();

   private RawAnimation() {
   }

   public static RawAnimation begin() {
      return new RawAnimation();
   }

   public RawAnimation thenPlay(Animation animation) {
      return this.then(animation, Animation.LoopType.DEFAULT);
   }

   public RawAnimation thenLoop(Animation animation) {
      return this.then(animation, Animation.LoopType.LOOP);
   }

   public RawAnimation thenWait(int ticks) {
      this.animationList.add(new RawAnimation.Stage(AnimationStage.WAIT, null, Animation.LoopType.PLAY_ONCE, ticks));
      return this;
   }

   public RawAnimation thenPlayAndHold(Animation animation) {
      return this.then(animation, Animation.LoopType.HOLD_ON_LAST_FRAME);
   }

   public RawAnimation thenPlayXTimes(Animation animation, int playCount) {
      for (int i = 0; i < playCount; i++) {
         this.then(animation, i == playCount - 1 ? Animation.LoopType.DEFAULT : Animation.LoopType.PLAY_ONCE);
      }

      return this;
   }

   public RawAnimation then(Animation animation, Animation.LoopType loopType) {
      this.animationList.add(new RawAnimation.Stage(animation, loopType));
      return this;
   }

   public List<RawAnimation.Stage> getAnimationStages() {
      return this.animationList;
   }

   public static RawAnimation copyOf(RawAnimation other) {
      RawAnimation newInstance = begin();
      newInstance.animationList.addAll(other.animationList);
      return newInstance;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj != null && this.getClass() == obj.getClass() ? this.hashCode() == obj.hashCode() : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.animationList);
   }

   public record Stage(AnimationStage stage, @Nullable Animation animation, Animation.LoopType loopType, int additionalTicks) {
      public Stage(AnimationStage stage, Animation animation, Animation.LoopType loopType) {
         this(stage, animation, loopType, 0);
      }

      public Stage(Animation animation, Animation.LoopType loopType) {
         this(AnimationStage.ANIMATION, animation, loopType);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else {
            return obj != null && this.getClass() == obj.getClass() ? this.hashCode() == obj.hashCode() : false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.stage, this.animation, this.loopType);
      }
   }
}
