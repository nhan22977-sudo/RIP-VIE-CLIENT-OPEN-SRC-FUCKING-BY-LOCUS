package com.zigythebird.playeranimcore.animation.keyframe;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record BoneAnimation(KeyframeStack rotationKeyFrames, KeyframeStack positionKeyFrames, KeyframeStack scaleKeyFrames, List<Keyframe> bendKeyFrames) {
   public BoneAnimation() {
      this(new KeyframeStack(), new KeyframeStack(), new KeyframeStack(), new ArrayList<>());
   }

   public boolean hasKeyframes() {
      return this.rotationKeyFrames().hasKeyframes()
         || this.positionKeyFrames().hasKeyframes()
         || this.scaleKeyFrames().hasKeyframes()
         || !this.bendKeyFrames.isEmpty();
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof BoneAnimation that)
         ? false
         : Objects.equals(this.scaleKeyFrames, that.scaleKeyFrames)
            && Objects.equals(this.bendKeyFrames, that.bendKeyFrames)
            && Objects.equals(this.rotationKeyFrames, that.rotationKeyFrames)
            && Objects.equals(this.positionKeyFrames, that.positionKeyFrames);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.rotationKeyFrames, this.positionKeyFrames, this.scaleKeyFrames, this.bendKeyFrames);
   }
}
