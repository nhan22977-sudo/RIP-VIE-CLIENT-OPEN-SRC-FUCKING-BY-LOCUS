package com.zigythebird.playeranimcore.bones;

public class AdvancedBoneSnapshot extends BoneSnapshot {
   public boolean scaleXEnabled;
   public boolean scaleYEnabled;
   public boolean scaleZEnabled;
   public boolean positionXEnabled;
   public boolean positionYEnabled;
   public boolean positionZEnabled;
   public boolean rotXEnabled;
   public boolean rotYEnabled;
   public boolean rotZEnabled;
   public boolean bendEnabled;

   public AdvancedBoneSnapshot(PlayerAnimBone bone) {
      super(bone);
      if (bone instanceof IBoneEnabled boneEnabled) {
         this.scaleXEnabled = boneEnabled.isScaleXEnabled();
         this.scaleYEnabled = boneEnabled.isScaleYEnabled();
         this.scaleZEnabled = boneEnabled.isScaleZEnabled();
         this.positionXEnabled = boneEnabled.isPositionXEnabled();
         this.positionYEnabled = boneEnabled.isPositionYEnabled();
         this.positionZEnabled = boneEnabled.isPositionZEnabled();
         this.rotXEnabled = boneEnabled.isRotXEnabled();
         this.rotYEnabled = boneEnabled.isRotYEnabled();
         this.rotZEnabled = boneEnabled.isRotZEnabled();
         this.bendEnabled = boneEnabled.isBendEnabled();
      }
   }
}
