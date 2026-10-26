package com.zigythebird.playeranimcore.bones;

import com.zigythebird.playeranimcore.enums.TransformType;
import com.zigythebird.playeranimcore.math.Vec3f;
import it.unimi.dsi.fastutil.Pair;

public class BoneSnapshot {
   public final PlayerAnimBone bone;
   private float scaleX;
   private float scaleY;
   private float scaleZ;
   private float offsetPosX;
   private float offsetPosY;
   private float offsetPosZ;
   private float rotX;
   private float rotY;
   private float rotZ;
   private float bendAxis;
   private float bend;

   public BoneSnapshot() {
      this.bone = null;
      this.setToInitialPose();
   }

   public BoneSnapshot(PlayerAnimBone bone) {
      this.rotX = bone.getRotX();
      this.rotY = bone.getRotY();
      this.rotZ = bone.getRotZ();
      this.offsetPosX = bone.getPosX();
      this.offsetPosY = bone.getPosY();
      this.offsetPosZ = bone.getPosZ();
      this.scaleX = bone.getScaleX();
      this.scaleY = bone.getScaleY();
      this.scaleZ = bone.getScaleZ();
      this.bend = bone.getBend();
      this.bone = bone;
   }

   public BoneSnapshot(BoneSnapshot bone) {
      this.bone = bone.getBone();
      this.rotX = bone.getRotX();
      this.rotY = bone.getRotY();
      this.rotZ = bone.getRotZ();
      this.offsetPosX = bone.getOffsetX();
      this.offsetPosY = bone.getOffsetY();
      this.offsetPosZ = bone.getOffsetZ();
      this.scaleX = bone.getScaleX();
      this.scaleY = bone.getScaleY();
      this.scaleZ = bone.getScaleZ();
      this.bendAxis = bone.getBendAxis();
      this.bend = bone.getBend();
   }

   public BoneSnapshot(PlayerAnimBone bone, boolean isInitial) {
      this.bone = bone;
      if (!isInitial) {
         this.rotX = bone.getRotX();
         this.rotY = bone.getRotY();
         this.rotZ = bone.getRotZ();
         this.offsetPosX = bone.getPosX();
         this.offsetPosY = bone.getPosY();
         this.offsetPosZ = bone.getPosZ();
         this.scaleX = bone.getScaleX();
         this.scaleY = bone.getScaleY();
         this.scaleZ = bone.getScaleZ();
         this.bend = bone.getBend();
      } else {
         this.setToInitialPose();
      }
   }

   public PlayerAnimBone getBone() {
      return this.bone;
   }

   public float getScaleX() {
      return this.scaleX;
   }

   public float getScaleY() {
      return this.scaleY;
   }

   public float getScaleZ() {
      return this.scaleZ;
   }

   public float getOffsetX() {
      return this.offsetPosX;
   }

   public float getOffsetY() {
      return this.offsetPosY;
   }

   public float getOffsetZ() {
      return this.offsetPosZ;
   }

   public float getRotX() {
      return this.rotX;
   }

   public float getRotY() {
      return this.rotY;
   }

   public float getRotZ() {
      return this.rotZ;
   }

   public float getBendAxis() {
      return this.bendAxis;
   }

   public float getBend() {
      return this.bend;
   }

   public void updateScale(float scaleX, float scaleY, float scaleZ) {
      this.scaleX = scaleX;
      this.scaleY = scaleY;
      this.scaleZ = scaleZ;
   }

   public void updateOffset(float offsetX, float offsetY, float offsetZ) {
      this.offsetPosX = offsetX;
      this.offsetPosY = offsetY;
      this.offsetPosZ = offsetZ;
   }

   public void updateRotation(float rotX, float rotY, float rotZ) {
      this.rotX = rotX;
      this.rotY = rotY;
      this.rotZ = rotZ;
   }

   public void updateBend(float bendAxis, float bend) {
      this.bendAxis = bendAxis;
      this.bend = bend;
   }

   public void updateBend(Pair<Float, Float> bend) {
      this.updateBend((Float)bend.left(), (Float)bend.right());
   }

   public Vec3f getTransformFromType(TransformType type) {
      switch (type) {
         case POSITION:
            return new Vec3f(this.offsetPosX, this.offsetPosY, this.offsetPosZ);
         case ROTATION:
            return new Vec3f(this.rotX, this.rotY, this.rotZ);
         case SCALE:
            return new Vec3f(this.scaleX, this.scaleY, this.scaleZ);
         case BEND:
            return new Vec3f(this.bendAxis, this.bend, 0.0F);
         default:
            return null;
      }
   }

   public void setToInitialPose() {
      this.rotX = 0.0F;
      this.rotY = 0.0F;
      this.rotZ = 0.0F;
      this.offsetPosX = 0.0F;
      this.offsetPosY = 0.0F;
      this.offsetPosZ = 0.0F;
      this.scaleX = 1.0F;
      this.scaleY = 1.0F;
      this.scaleZ = 1.0F;
      this.bendAxis = 0.0F;
      this.bend = 0.0F;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj != null && this.getClass() == obj.getClass() ? this.hashCode() == obj.hashCode() : false;
      }
   }

   @Override
   public int hashCode() {
      return this.bone.getName().hashCode();
   }
}
