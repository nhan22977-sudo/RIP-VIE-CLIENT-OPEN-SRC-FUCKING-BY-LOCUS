package de.javagl.jgltf.model.animation;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Animation {
   private final float[] timesS;
   private final float[][] values;
   private final Interpolator interpolator;
   private final float[] outputValues;
   private final List<AnimationListener> listeners;

   public Animation(float[] timesS, float[][] values, InterpolatorType interpolatorType) {
      Objects.requireNonNull(timesS, "The times may not be null");
      Objects.requireNonNull(values, "The values may not be null");
      if (timesS.length == 0) {
         throw new IllegalArgumentException("The keys may not have a length of 0");
      }

      if (values.length != timesS.length) {
         throw new IllegalArgumentException("The values must have a length of " + timesS.length + ", but have a length of " + values.length);
      }

      this.timesS = (float[])timesS.clone();
      this.values = new float[values.length][];

      for (int i = 0; i < values.length; i++) {
         this.values[i] = (float[])values[i].clone();
      }

      this.outputValues = new float[values[0].length];
      this.interpolator = Interpolators.create(interpolatorType);
      this.listeners = new CopyOnWriteArrayList<>();
   }

   float getStartTimeS() {
      return this.timesS[0];
   }

   float getEndTimeS() {
      return this.timesS[this.timesS.length - 1];
   }

   float getDurationS() {
      return this.getEndTimeS() - this.getStartTimeS();
   }

   public void addAnimationListener(AnimationListener listener) {
      this.listeners.add(listener);
   }

   public void removeAnimationListener(AnimationListener listener) {
      this.listeners.remove(listener);
   }

   void update(float timeS) {
      int index0 = InterpolatorKeys.computeIndex(timeS, this.timesS);
      int index1 = Math.min(this.timesS.length - 1, index0 + 1);
      float alpha = InterpolatorKeys.computeAlpha(timeS, this.timesS, index0);
      float[] a = this.values[index0];
      float[] b = this.values[index1];
      this.interpolator.interpolate(a, b, alpha, this.outputValues);

      for (AnimationListener listener : this.listeners) {
         listener.animationUpdated(this, timeS, this.outputValues);
      }
   }
}
