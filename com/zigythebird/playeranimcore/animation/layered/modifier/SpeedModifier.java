package com.zigythebird.playeranimcore.animation.layered.modifier;

import com.zigythebird.playeranimcore.animation.AnimationData;

public class SpeedModifier extends AbstractModifier {
   public float speed;
   private float delta = 0.0F;
   private float shiftedDelta = 0.0F;

   public SpeedModifier(float speed) {
      if (!Float.isFinite(speed)) {
         throw new IllegalArgumentException("Speed must be a finite number");
      }

      this.speed = speed;
   }

   @Override
   public void tick(AnimationData state) {
      float delta = 1.0F - this.delta;
      this.delta = 0.0F;
      this.step(delta, state);
   }

   @Override
   public void setupAnim(AnimationData state) {
      float delta = state.getPartialTick() - this.delta;
      this.delta = state.getPartialTick();
      this.step(delta, state);
      super.setupAnim(state);
   }

   protected void step(float delta, AnimationData state) {
      delta *= this.speed;
      delta += this.shiftedDelta;

      while (delta > 1.0F) {
         delta--;
         super.tick(state);
      }

      state.setPartialTick(delta);
      this.shiftedDelta = delta;
   }

   @Override
   public String toString() {
      return "SpeedModifier{speed=" + this.speed + ", anim=" + this.anim + "}";
   }
}
