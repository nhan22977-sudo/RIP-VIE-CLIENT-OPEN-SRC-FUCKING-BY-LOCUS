package com.zigythebird.playeranimcore.easing;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.zigythebird.playeranimcore.animation.keyframe.AnimationPoint;
import com.zigythebird.playeranimcore.math.MathHelper;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.MochaEngine;
import team.unnamed.mocha.runtime.standard.MochaMath;

public enum EasingType implements EasingTypeTransformer {
   LINEAR(0, "linear", value -> easeIn(EasingType::linear)),
   CONSTANT(1, "constant", value -> value1 -> 0.0F),
   STEP(37, "step", value -> easeIn(step(value))),
   EASE_IN_SINE(6, "easeinsine", value -> easeIn(EasingType::sine)),
   EASE_OUT_SINE(7, "easeoutsine", value -> easeOut(EasingType::sine)),
   EASE_IN_OUT_SINE(8, "easeinoutsine", value -> easeInOut(EasingType::sine)),
   EASE_IN_QUAD(12, "easeinquad", value -> easeIn(EasingType::quadratic)),
   EASE_OUT_QUAD(13, "easeoutquad", value -> easeOut(EasingType::quadratic)),
   EASE_IN_OUT_QUAD(14, "easeinoutquad", value -> easeInOut(EasingType::quadratic)),
   EASE_IN_CUBIC(9, "easeincubic", value -> easeIn(EasingType::cubic)),
   EASE_OUT_CUBIC(10, "easeoutcubic", value -> easeOut(EasingType::cubic)),
   EASE_IN_OUT_CUBIC(11, "easeinoutcubic", value -> easeInOut(EasingType::cubic)),
   EASE_IN_QUART(15, "easeinquart", value -> easeIn(pow(4.0F))),
   EASE_OUT_QUART(16, "easeoutquart", value -> easeOut(pow(4.0F))),
   EASE_IN_OUT_QUART(17, "easeinoutquart", value -> easeInOut(pow(4.0F))),
   EASE_IN_QUINT(18, "easeinquint", value -> easeIn(pow(5.0F))),
   EASE_OUT_QUINT(19, "easeoutquint", value -> easeOut(pow(5.0F))),
   EASE_IN_OUT_QUINT(20, "easeinoutquint", value -> easeInOut(pow(5.0F))),
   EASE_IN_EXPO(21, "easeinexpo", value -> easeIn(EasingType::exp)),
   EASE_OUT_EXPO(22, "easeoutexpo", value -> easeOut(EasingType::exp)),
   EASE_IN_OUT_EXPO(23, "easeinoutexpo", value -> easeInOut(EasingType::exp)),
   EASE_IN_CIRC(24, "easeincirc", value -> easeIn(EasingType::circle)),
   EASE_OUT_CIRC(25, "easeoutcirc", value -> easeOut(EasingType::circle)),
   EASE_IN_OUT_CIRC(26, "easeinoutcirc", value -> easeInOut(EasingType::circle)),
   EASE_IN_BACK(27, "easeinback", value -> easeIn(back(value))),
   EASE_OUT_BACK(28, "easeoutback", value -> easeOut(back(value))),
   EASE_IN_OUT_BACK(29, "easeinoutback", value -> easeInOut(back(value))),
   EASE_IN_ELASTIC(30, "easeinelastic", value -> easeIn(elastic(value))),
   EASE_OUT_ELASTIC(31, "easeoutelastic", value -> easeOut(elastic(value))),
   EASE_IN_OUT_ELASTIC(32, "easeinoutelastic", value -> easeInOut(elastic(value))),
   EASE_IN_BOUNCE(33, "easeinbounce", value -> easeIn(bounce(value))),
   EASE_OUT_BOUNCE(34, "easeoutbounce", value -> easeOut(bounce(value))),
   EASE_IN_OUT_BOUNCE(35, "easeinoutbounce", value -> easeInOut(bounce(value))),
   CATMULLROM(36, "catmullrom", new CatmullRomEasing()),
   BEZIER(38, "bezier", new BezierEasing());

   public final byte id;
   public final String name;
   private final EasingTypeTransformer transformer;
   private static final Map<String, EasingType> BY_NAME = new ConcurrentHashMap<>(64);
   private static final Map<Byte, EasingType> BY_ID = new ConcurrentHashMap<>(64);

   EasingType(int id, String name, EasingTypeTransformer transformer) {
      this.id = (byte)id;
      this.name = name;
      this.transformer = transformer;
   }

   @Override
   public Float2FloatFunction buildTransformer(@Nullable Float value) {
      return this.transformer.buildTransformer(value);
   }

   public static float lerpWithOverride(MochaEngine<?> env, AnimationPoint animationPoint, @Nullable EasingType override) {
      EasingType easingType = override;
      if (override == null) {
         easingType = animationPoint.easingType();
      }

      return easingType.apply(env, animationPoint);
   }

   @Override
   public float apply(MochaEngine<?> env, AnimationPoint animationPoint) {
      return this.transformer.apply(env, animationPoint);
   }

   @Override
   public float apply(MochaEngine<?> env, AnimationPoint animationPoint, @Nullable Float easingValue, float lerpValue) {
      return this.transformer.apply(env, animationPoint, easingValue, lerpValue);
   }

   @Override
   public float apply(float startValue, float endValue, float lerpValue) {
      return this.transformer.apply(startValue, endValue, lerpValue);
   }

   @Override
   public float apply(float startValue, float endValue, @Nullable Float easingValue, float lerpValue) {
      return this.transformer.apply(startValue, endValue, lerpValue);
   }

   public static EasingType fromJson(JsonElement json) {
      return json instanceof JsonPrimitive primitive && primitive.isString() ? fromString(primitive.getAsString().toLowerCase(Locale.ROOT)) : LINEAR;
   }

   public static EasingType fromString(String name) {
      return BY_NAME.getOrDefault(name.toLowerCase(Locale.ROOT), LINEAR);
   }

   public static Float2FloatFunction easeIn(Float2FloatFunction function) {
      return function;
   }

   public static Float2FloatFunction easeOut(Float2FloatFunction function) {
      return time -> 1.0F - (Float)function.apply(1.0F - time);
   }

   public static Float2FloatFunction easeInOut(Float2FloatFunction function) {
      return time -> time < 0.5 ? (Float)function.apply(time * 2.0F) / 2.0F : 1.0F - (Float)function.apply((1.0F - time) * 2.0F) / 2.0F;
   }

   public static float linear(float n) {
      return n;
   }

   public static float quadratic(float n) {
      return n * n;
   }

   public static float cubic(float n) {
      return n * n * n;
   }

   public static float sine(float n) {
      return 1.0F - MathHelper.cos(n * (float) Math.PI / 2.0F);
   }

   public static float circle(float n) {
      return 1.0F - MochaMath.sqrt(1.0F - n * n);
   }

   public static float exp(float n) {
      return MochaMath.pow(2.0F, 10.0F * (n - 1.0F));
   }

   public static Float2FloatFunction elastic(Float n) {
      float n2 = n == null ? 1.0F : n;
      return t -> 1.0F - MochaMath.pow(MathHelper.cos(t * (float) Math.PI / 2.0F), 3.0F) * MathHelper.cos(t * n2 * (float) Math.PI);
   }

   public static Float2FloatFunction bounce(Float n) {
      float n2 = n == null ? 0.5F : n;
      Float2FloatFunction one = x -> 7.5625F * x * x;
      Float2FloatFunction two = x -> 30.25F * n2 * MochaMath.pow(x - 0.54545456F, 2.0F) + 1.0F - n2;
      Float2FloatFunction three = x -> 121.0F * n2 * n2 * MochaMath.pow(x - 0.8181818F, 2.0F) + 1.0F - n2 * n2;
      Float2FloatFunction four = x -> 484.0F * n2 * n2 * n2 * MochaMath.pow(x - 0.95454544F, 2.0F) + 1.0F - n2 * n2 * n2;
      return t -> Math.min(Math.min((Float)one.apply(t), (Float)two.apply(t)), Math.min((Float)three.apply(t), (Float)four.apply(t)));
   }

   public static Float2FloatFunction back(Float n) {
      float n2 = n == null ? 1.70158F : n * 1.70158F;
      return t -> t * t * ((n2 + 1.0F) * t - n2);
   }

   public static Float2FloatFunction pow(float n) {
      return t -> MochaMath.pow(t, n);
   }

   public static Float2FloatFunction step(Float n) {
      float n2 = n == null ? 2.0F : n;
      if (n2 < 2.0F) {
         throw new IllegalArgumentException("Steps must be >= 2, got: " + n2);
      }

      int steps = (int)n2;
      return t -> {
         float result = 0.0F;
         if (t < 0.0F) {
            return result;
         }

         float stepLength = 1.0F / steps;
         if (t > (result = (steps - 1) * stepLength)) {
            return result;
         }

         int leftBorderIndex = 0;
         int rightBorderIndex = steps - 1;

         while (rightBorderIndex - leftBorderIndex != 1) {
            int testIndex = leftBorderIndex + (rightBorderIndex - leftBorderIndex) / 2;
            if (t >= testIndex * stepLength) {
               leftBorderIndex = testIndex;
            } else {
               rightBorderIndex = testIndex;
            }
         }

         return leftBorderIndex * stepLength;
      };
   }

   public static EasingType fromId(byte id) {
      return BY_ID.getOrDefault(id, LINEAR);
   }

   // $VF: synthetic method
   private static EasingType[] $values() {
      return new EasingType[]{
         LINEAR,
         CONSTANT,
         STEP,
         EASE_IN_SINE,
         EASE_OUT_SINE,
         EASE_IN_OUT_SINE,
         EASE_IN_QUAD,
         EASE_OUT_QUAD,
         EASE_IN_OUT_QUAD,
         EASE_IN_CUBIC,
         EASE_OUT_CUBIC,
         EASE_IN_OUT_CUBIC,
         EASE_IN_QUART,
         EASE_OUT_QUART,
         EASE_IN_OUT_QUART,
         EASE_IN_QUINT,
         EASE_OUT_QUINT,
         EASE_IN_OUT_QUINT,
         EASE_IN_EXPO,
         EASE_OUT_EXPO,
         EASE_IN_OUT_EXPO,
         EASE_IN_CIRC,
         EASE_OUT_CIRC,
         EASE_IN_OUT_CIRC,
         EASE_IN_BACK,
         EASE_OUT_BACK,
         EASE_IN_OUT_BACK,
         EASE_IN_ELASTIC,
         EASE_OUT_ELASTIC,
         EASE_IN_OUT_ELASTIC,
         EASE_IN_BOUNCE,
         EASE_OUT_BOUNCE,
         EASE_IN_OUT_BOUNCE,
         CATMULLROM,
         BEZIER
      };
   }

   static {
      for (EasingType type : values()) {
         BY_NAME.putIfAbsent(type.name.toLowerCase(Locale.ROOT), type);
         BY_ID.putIfAbsent(type.id, type);
      }
   }
}
