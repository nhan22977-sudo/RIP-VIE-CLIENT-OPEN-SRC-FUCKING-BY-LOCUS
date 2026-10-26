package com.zigythebird.playeranimcore.easing;

import com.zigythebird.playeranimcore.animation.keyframe.AnimationPoint;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.MochaEngine;
import team.unnamed.mocha.runtime.standard.MochaMath;
import team.unnamed.mocha.runtime.value.ObjectValue.FloatFunction3;

@FunctionalInterface
public interface EasingTypeTransformer extends FloatFunction3 {
   Float2FloatFunction buildTransformer(@Nullable Float var1);

   default float apply(MochaEngine<?> env, AnimationPoint animationPoint) {
      Float easingVariable = null;
      if (animationPoint.easingArgs() != null && !animationPoint.easingArgs().isEmpty()) {
         easingVariable = env.eval(animationPoint.easingArgs().getFirst());
      }

      return this.apply(env, animationPoint, easingVariable, animationPoint.currentTick() / animationPoint.transitionLength());
   }

   default float apply(MochaEngine<?> env, AnimationPoint animationPoint, @Nullable Float easingValue, float lerpValue) {
      if (lerpValue >= 1.0F) {
         return animationPoint.animationEndValue();
      } else {
         return Float.isNaN(lerpValue)
            ? animationPoint.animationStartValue()
            : this.apply(animationPoint.animationStartValue(), animationPoint.animationEndValue(), easingValue, lerpValue);
      }
   }

   default float apply(float startValue, float endValue, float lerpValue) {
      return this.apply(startValue, endValue, null, lerpValue);
   }

   default float apply(float startValue, float endValue, @Nullable Float easingValue, float lerpValue) {
      return MochaMath.lerp(startValue, endValue, (Float)this.buildTransformer(easingValue).apply(lerpValue));
   }
}
