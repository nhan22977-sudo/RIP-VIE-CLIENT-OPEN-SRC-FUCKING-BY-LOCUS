package de.javagl.jgltf.model.animation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class AnimationManager {
   private final AnimationManager.AnimationPolicy animationPolicy;
   private long startNs;
   private long currentNs;
   private final List<Animation> animations;
   private float maxEndTimeS;
   private final List<AnimationManagerListener> animationManagerListeners;

   public AnimationManager(AnimationManager.AnimationPolicy animationPolicy) {
      this.animationPolicy = animationPolicy;
      this.startNs = System.nanoTime();
      this.currentNs = this.startNs;
      this.animations = new CopyOnWriteArrayList<>();
      this.maxEndTimeS = 0.0F;
      this.animationManagerListeners = new CopyOnWriteArrayList<>();
   }

   public void reset() {
      this.startNs = System.nanoTime();
      this.currentNs = System.nanoTime();
      this.performStep(0L);
   }

   float getCurrentTimeS() {
      long timeNs = this.currentNs - this.startNs;
      return (float)timeNs * 1.0E-9F;
   }

   public void addAnimation(Animation animation) {
      Objects.requireNonNull(animation, "The animation may not be null");
      this.animations.add(animation);
      this.updateMaxEndTime();
   }

   public void addAnimations(Iterable<? extends Animation> animations) {
      for (Animation animation : animations) {
         this.addAnimation(animation);
      }
   }

   public void removeAnimation(Animation animation) {
      this.animations.remove(animation);
      this.updateMaxEndTime();
   }

   public void removeAnimations(Iterable<? extends Animation> animations) {
      for (Animation animation : animations) {
         this.removeAnimation(animation);
      }
   }

   public List<Animation> getAnimations() {
      return Collections.unmodifiableList(this.animations);
   }

   private void updateMaxEndTime() {
      this.maxEndTimeS = 0.0F;

      for (Animation animation : this.animations) {
         this.maxEndTimeS = Math.max(this.maxEndTimeS, animation.getEndTimeS());
      }
   }

   void performStep(long deltaNs) {
      this.currentNs += deltaNs;
      float currentTimeS = this.getCurrentTimeS();
      if (this.animationPolicy == AnimationManager.AnimationPolicy.ONCE && currentTimeS > this.maxEndTimeS) {
         this.animations.clear();
      } else {
         for (Animation animation : this.animations) {
            if (this.animationPolicy == AnimationManager.AnimationPolicy.LOOP) {
               float loopTimeS = currentTimeS % this.maxEndTimeS;
               animation.update(loopTimeS);
            } else if (this.animationPolicy == AnimationManager.AnimationPolicy.PING_PONG) {
               int interval = (int)(currentTimeS / this.maxEndTimeS);
               float loopTimeS = currentTimeS % this.maxEndTimeS;
               float pingPongTimeS = loopTimeS;
               if ((interval & 1) != 0) {
                  pingPongTimeS = this.maxEndTimeS - loopTimeS;
               }

               animation.update(pingPongTimeS);
            } else {
               animation.update(currentTimeS);
            }
         }

         this.fireAnimationsUpdated();
      }
   }

   public void addAnimationManagerListener(AnimationManagerListener listener) {
      this.animationManagerListeners.add(listener);
   }

   public void removeAnimationManagerListener(AnimationManagerListener listener) {
      this.animationManagerListeners.remove(listener);
   }

   private void fireAnimationsUpdated() {
      for (AnimationManagerListener listener : this.animationManagerListeners) {
         listener.animationsUpdated(this);
      }
   }

   public enum AnimationPolicy {
      ONCE,
      PING_PONG,
      LOOP;
   }
}
