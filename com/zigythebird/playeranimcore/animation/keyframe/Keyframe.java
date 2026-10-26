package com.zigythebird.playeranimcore.animation.keyframe;

import com.zigythebird.playeranimcore.easing.EasingType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import team.unnamed.mocha.parser.ast.Expression;

public record Keyframe(float length, List<Expression> startValue, List<Expression> endValue, EasingType easingType, List<List<Expression>> easingArgs) {
   public Keyframe(float length, List<Expression> startValue, List<Expression> endValue) {
      this(length, startValue, endValue, EasingType.LINEAR);
   }

   public Keyframe(float length, List<Expression> startValue, List<Expression> endValue, EasingType easingType) {
      this(length, startValue, endValue, easingType, new ObjectArrayList(0));
   }

   public Keyframe(float length) {
      this(length, Collections.emptyList(), Collections.emptyList());
   }

   public static float getLastKeyframeTime(List<Keyframe> list) {
      return (float)list.stream().mapToDouble(Keyframe::length).sum();
   }

   public static Keyframe getKeyframeAtTime(List<Keyframe> list, float tick) {
      float totalFrameTime = 0.0F;

      for (Keyframe keyframe : list) {
         totalFrameTime += keyframe.length;
         if (totalFrameTime >= tick) {
            return keyframe;
         }
      }

      return list.getLast();
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.length, this.startValue, this.endValue, this.easingType.id, this.easingArgs);
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof Keyframe keyframe)
         ? false
         : Float.compare(this.length, keyframe.length) == 0
            && this.easingType.id == keyframe.easingType.id
            && Objects.equals(this.endValue, keyframe.endValue)
            && Objects.equals(this.startValue, keyframe.startValue)
            && Objects.equals(this.easingArgs, keyframe.easingArgs);
   }
}
