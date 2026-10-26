package com.zigythebird.playeranimcore.animation;

public class AnimationData {
   private float velocity;
   private float partialTick;
   private final boolean isFirstPersonPass;

   public AnimationData(float velocity, float partialTick, boolean isFirstPersonPass) {
      this.velocity = velocity;
      this.partialTick = partialTick;
      this.isFirstPersonPass = isFirstPersonPass;
   }

   public float getPartialTick() {
      return this.partialTick;
   }

   public float getVelocity() {
      return this.velocity;
   }

   public boolean isFirstPersonPass() {
      return this.isFirstPersonPass;
   }

   public boolean isMoving() {
      return this.velocity > 0.015F;
   }

   public boolean isMovingLenient() {
      return this.velocity > 1.0E-6F;
   }

   public void setVelocity(float velocity) {
      this.velocity = velocity;
   }

   public void setPartialTick(float partialTick) {
      this.partialTick = partialTick;
   }

   public AnimationData copy() {
      return new AnimationData(this.velocity, this.partialTick, this.isFirstPersonPass);
   }
}
