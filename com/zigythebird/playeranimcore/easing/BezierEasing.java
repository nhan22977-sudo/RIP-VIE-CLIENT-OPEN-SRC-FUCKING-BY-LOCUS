package com.zigythebird.playeranimcore.easing;

import com.zigythebird.playeranimcore.animation.keyframe.AnimationPoint;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import team.unnamed.mocha.MochaEngine;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.runtime.standard.MochaMath;

public class BezierEasing implements EasingTypeTransformer {
   @Override
   public Float2FloatFunction buildTransformer(@Nullable Float value) {
      return EasingType.easeIn(EasingType::linear);
   }

   @Override
   public float apply(MochaEngine<?> env, AnimationPoint animationPoint, @Nullable Float easingValue, float lerpValue) {
      if (lerpValue >= 1.0F) {
         return animationPoint.animationEndValue();
      }

      if (!Float.isNaN(lerpValue) && lerpValue != 0.0F) {
         List<List<Expression>> easingArgs = animationPoint.easingArgs();
         if (easingArgs != null && !easingArgs.isEmpty()) {
            float leftValue = env.eval(easingArgs.getFirst());
            float leftTime = env.eval(easingArgs.get(1));
            float rightValue;
            float rightTime;
            if (easingArgs.size() > 3) {
               rightValue = env.eval(easingArgs.get(2));
               rightTime = env.eval(easingArgs.get(3));
            } else {
               rightValue = 0.0F;
               rightTime = 0.1F;
            }

            float transitionLength = animationPoint.transitionLength() / 20.0F;
            float time_handle_before = rightTime / transitionLength;
            float time_handle_after = leftTime / transitionLength;
            if (time_handle_before > 1.0F || time_handle_before < 0.0F) {
               float unclamped = time_handle_before;
               time_handle_before = Math.clamp(time_handle_before, 0.0F, 1.0F);
               rightValue /= 1.0F + Math.abs(time_handle_before - unclamped);
            }

            if (time_handle_after > 0.0F || time_handle_after < -1.0F) {
               float unclamped = time_handle_after;
               time_handle_after = Math.clamp(time_handle_after, -1.0F, 0.0F);
               leftValue /= 1.0F + Math.abs(time_handle_after - unclamped);
            }

            Vector2f P0 = new Vector2f(0.0F, animationPoint.animationStartValue());
            Vector2f P1 = new Vector2f(time_handle_before, animationPoint.animationStartValue() + rightValue);
            Vector2f P2 = new Vector2f(time_handle_after + 1.0F, animationPoint.animationEndValue() + leftValue);
            Vector2f P3 = new Vector2f(1.0F, animationPoint.animationEndValue());
            List<Vector2f> points = new ArrayList<>();
            int divisions = 200;

            for (int d = 0; d <= 200; d++) {
               float t = d / 200.0F;
               points.add(new Vector2f(this.CubicBezier(t, P0.x, P1.x, P2.x, P3.x), this.CubicBezier(t, P0.y, P1.y, P2.y, P3.y)));
            }

            Vector2f closest = new Vector2f();
            float closest_diff = Float.POSITIVE_INFINITY;

            for (Vector2f point : points) {
               float diff = Math.abs(point.x - lerpValue);
               if (diff < closest_diff) {
                  closest_diff = diff;
                  closest = point;
               }
            }

            Vector2f second_closest = new Vector2f();
            closest_diff = Float.POSITIVE_INFINITY;

            for (Vector2f point : points) {
               if (point == closest) {
                  break;
               }

               float diff = Math.abs(point.x - lerpValue);
               if (diff < closest_diff) {
                  closest_diff = diff;
                  second_closest = point;
               }
            }

            return MochaMath.lerp(closest.y, second_closest.y, Math.clamp(MochaMath.lerp(closest.x, second_closest.x, lerpValue), 0.0F, 1.0F));
         } else {
            return MochaMath.lerp(animationPoint.animationStartValue(), animationPoint.animationEndValue(), (Float)this.buildTransformer(null).apply(lerpValue));
         }
      } else {
         return animationPoint.animationStartValue();
      }
   }

   float CubicBezierP0(float t, float p) {
      float k = 1.0F - t;
      return k * k * k * p;
   }

   float CubicBezierP1(float t, float p) {
      float k = 1.0F - t;
      return 3.0F * k * k * t * p;
   }

   float CubicBezierP2(float t, float p) {
      return 3.0F * (1.0F - t) * t * t * p;
   }

   float CubicBezierP3(float t, float p) {
      return t * t * t * p;
   }

   float CubicBezier(float t, float p0, float p1, float p2, float p3) {
      return this.CubicBezierP0(t, p0) + this.CubicBezierP1(t, p1) + this.CubicBezierP2(t, p2) + this.CubicBezierP3(t, p3);
   }
}
