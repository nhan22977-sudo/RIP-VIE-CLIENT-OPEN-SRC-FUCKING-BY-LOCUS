package com.zigythebird.playeranimcore.animation.layered;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import java.util.HashMap;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerAnimationFrame implements IAnimation {
   protected boolean isActive = false;
   protected PlayerAnimationFrame.PlayerBone head = new PlayerAnimationFrame.PlayerBone();
   protected PlayerAnimationFrame.PlayerBone body = new PlayerAnimationFrame.PlayerBone();
   protected PlayerAnimationFrame.PlayerBone rightArm = new PlayerAnimationFrame.PlayerBone();
   protected PlayerAnimationFrame.PlayerBone leftArm = new PlayerAnimationFrame.PlayerBone();
   protected PlayerAnimationFrame.PlayerBone rightLeg = new PlayerAnimationFrame.PlayerBone();
   protected PlayerAnimationFrame.PlayerBone leftLeg = new PlayerAnimationFrame.PlayerBone();
   protected PlayerAnimationFrame.PlayerBone rightItem = new PlayerAnimationFrame.PlayerBone();
   protected PlayerAnimationFrame.PlayerBone leftItem = new PlayerAnimationFrame.PlayerBone();
   HashMap<String, PlayerAnimationFrame.PlayerBone> parts = new HashMap<>();

   public PlayerAnimationFrame() {
      this.parts.put("head", this.head);
      this.parts.put("body", this.body);
      this.parts.put("right_arm", this.rightArm);
      this.parts.put("left_arm", this.leftArm);
      this.parts.put("right_leg", this.rightLeg);
      this.parts.put("left_leg", this.leftLeg);
      this.parts.put("right_item", this.rightItem);
      this.parts.put("left_item", this.leftItem);
   }

   @Override
   public void tick(AnimationData state) {
      IAnimation.super.tick(state);
   }

   @Override
   public boolean isActive() {
      return this.isActive;
   }

   public void resetPose() {
      for (Entry<String, PlayerAnimationFrame.PlayerBone> entry : this.parts.entrySet()) {
         entry.getValue().setToInitialPose();
      }
   }

   public void enableAll() {
      for (Entry<String, PlayerAnimationFrame.PlayerBone> entry : this.parts.entrySet()) {
         entry.getValue().enableAll();
      }
   }

   @Override
   public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
      PlayerAnimationFrame.PlayerBone part = this.parts.get(bone.getName());
      return part != null ? part.applyToBone(bone) : bone;
   }

   @Override
   public String toString() {
      return "PlayerAnimationFrame{isActive=" + this.isActive + ", parts=" + this.parts + "}";
   }

   public static class PlayerBone {
      public Float offsetPosX = null;
      public Float offsetPosY = null;
      public Float offsetPosZ = null;
      public Float rotX = null;
      public Float rotY = null;
      public Float rotZ = null;
      public Float scaleX = null;
      public Float scaleY = null;
      public Float scaleZ = null;
      public Float bend = null;

      public void setToInitialPose() {
         this.rotX = null;
         this.rotY = null;
         this.rotZ = null;
         this.offsetPosX = null;
         this.offsetPosY = null;
         this.offsetPosZ = null;
         this.scaleX = null;
         this.scaleY = null;
         this.scaleZ = null;
         this.bend = null;
      }

      public void enableAll() {
         this.rotX = 0.0F;
         this.rotY = 0.0F;
         this.rotZ = 0.0F;
         this.offsetPosX = 0.0F;
         this.offsetPosY = 0.0F;
         this.offsetPosZ = 0.0F;
         this.scaleX = 1.0F;
         this.scaleY = 1.0F;
         this.scaleZ = 1.0F;
         this.bend = 0.0F;
      }

      public PlayerAnimBone applyToBone(PlayerAnimBone bone) {
         if (this.offsetPosX != null) {
            bone.setPosX(this.offsetPosX);
         }

         if (this.offsetPosY != null) {
            bone.setPosY(this.offsetPosY);
         }

         if (this.offsetPosZ != null) {
            bone.setPosZ(this.offsetPosZ);
         }

         if (this.rotX != null) {
            bone.setRotX(this.rotX);
         }

         if (this.rotY != null) {
            bone.setRotY(this.rotY);
         }

         if (this.rotZ != null) {
            bone.setRotZ(this.rotZ);
         }

         if (this.scaleX != null) {
            bone.setScaleX(this.scaleX);
         }

         if (this.scaleY != null) {
            bone.setScaleY(this.scaleY);
         }

         if (this.scaleZ != null) {
            bone.setScaleZ(this.scaleZ);
         }

         if (this.bend != null) {
            bone.setBend(this.bend);
         }

         return bone;
      }
   }
}
