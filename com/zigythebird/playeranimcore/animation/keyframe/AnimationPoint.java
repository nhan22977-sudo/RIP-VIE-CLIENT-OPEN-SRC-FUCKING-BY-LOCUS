package com.zigythebird.playeranimcore.animation.keyframe;

import com.zigythebird.playeranimcore.easing.EasingType;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.parser.ast.Expression;

public record AnimationPoint(
   EasingType easingType,
   @Nullable List<List<Expression>> easingArgs,
   float currentTick,
   float transitionLength,
   float animationStartValue,
   float animationEndValue
) {
   public AnimationPoint(Keyframe keyframe, float currentTick, float transitionLength, float animationStartValue, float animationEndValue) {
      this(
         keyframe == null ? EasingType.LINEAR : keyframe.easingType(),
         keyframe == null ? null : keyframe.easingArgs(),
         currentTick,
         transitionLength,
         animationStartValue,
         animationEndValue
      );
   }

   @Override
   public String toString() {
      return "Tick: "
         + this.currentTick
         + " | Transition Length: "
         + this.transitionLength
         + " | Start Value: "
         + this.animationStartValue
         + " | End Value: "
         + this.animationEndValue;
   }
}
