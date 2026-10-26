package com.zigythebird.playeranimcore.easing;

import com.zigythebird.playeranimcore.animation.keyframe.AnimationPoint;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.MochaEngine;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.runtime.standard.MochaMath;

public class CatmullRomEasing implements EasingTypeTransformer {
   public static float getPointOnSpline(float delta, float p0, float p1, float p2, float p3) {
      return 0.5F
         * (
            2.0F * p1
               + (p2 - p0) * delta
               + (2.0F * p0 - 5.0F * p1 + 4.0F * p2 - p3) * delta * delta
               + (3.0F * p1 - p0 - 3.0F * p2 + p3) * delta * delta * delta
         );
   }

   @Override
   public Float2FloatFunction buildTransformer(Float value) {
      return EasingType.easeIn(EasingType::linear);
   }

   @Override
   public float apply(MochaEngine<?> env, AnimationPoint animationPoint, @Nullable Float easingValue, float lerpValue) {
      if (animationPoint.currentTick() >= animationPoint.transitionLength()) {
         return animationPoint.animationEndValue();
      }

      List<List<Expression>> easingArgs = animationPoint.easingArgs();
      return easingArgs.size() < 2
         ? MochaMath.lerp(animationPoint.animationStartValue(), animationPoint.animationEndValue(), (Float)this.buildTransformer(easingValue).apply(lerpValue))
         : getPointOnSpline(
            lerpValue, env.eval(easingArgs.get(0)), animationPoint.animationStartValue(), animationPoint.animationEndValue(), env.eval(easingArgs.get(1))
         );
   }
}
