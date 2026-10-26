package com.zigythebird.playeranim.util;

import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.class_4587;
import net.minecraft.class_5603;
import net.minecraft.class_630;
import net.minecraft.class_4587.class_4665;

public final class RenderUtil {
   public static void rotateMatrixAroundBone(class_4587 poseStack, PlayerAnimBone bone) {
      rotateZYX(poseStack.method_23760(), bone.getRotZ(), bone.getRotY(), bone.getRotX());
   }

   public static void translatePartToCape(class_630 part, PlayerAnimBone bone, class_5603 initialPose) {
      part.field_3657 = bone.getPosX() + initialPose.comp_2997();
      part.field_3656 = -(bone.getPosY() + initialPose.comp_2998());
      part.field_3655 = bone.getPosZ() + initialPose.comp_2999();
      part.field_3654 = bone.getRotX();
      part.field_3675 = bone.getRotY();
      part.field_3674 = bone.getRotZ();
      part.field_37938 = bone.getScaleX();
      part.field_37939 = bone.getScaleY();
      part.field_37940 = bone.getScaleZ();
   }

   public static void translatePartToBone(class_630 part, PlayerAnimBone bone, class_5603 initialPose) {
      part.field_3657 = bone.getPosX() + initialPose.comp_2997();
      part.field_3656 = -bone.getPosY() + initialPose.comp_2998();
      part.field_3655 = bone.getPosZ() + initialPose.comp_2999();
      part.field_3654 = bone.getRotX();
      part.field_3675 = bone.getRotY() + initialPose.comp_3001();
      part.field_3674 = bone.getRotZ();
      part.field_37938 = bone.getScaleX();
      part.field_37939 = bone.getScaleY();
      part.field_37940 = bone.getScaleZ();
   }

   public static void translateMatrixToBone(class_4587 poseStack, PlayerAnimBone bone) {
      poseStack.method_46416(bone.getPosX() / 16.0F, bone.getPosY() / 16.0F, bone.getPosZ() / 16.0F);
      rotateMatrixAroundBone(poseStack, bone);
      poseStack.method_22905(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
   }

   public static PlayerAnimBone copyVanillaPart(class_630 part, PlayerAnimBone bone) {
      class_5603 initialPose = part.method_41921();
      bone.setPosX(part.field_3657 - initialPose.comp_2997());
      bone.setPosY(-(part.field_3656 - initialPose.comp_2998()));
      bone.setPosZ(part.field_3655 - initialPose.comp_2999());
      bone.setRotX(part.field_3654);
      bone.setRotY(part.field_3675);
      bone.setRotZ(part.field_3674);
      bone.setScaleX(part.field_37938);
      bone.setScaleY(part.field_37939);
      bone.setScaleZ(part.field_37940);
      bone.setBend(0.0F);
      return bone;
   }

   public static void rotateZYX(class_4665 matrices, float angleZ, float angleY, float angleX) {
      matrices.method_23761().rotateZYX(angleZ, angleY, angleX);
      matrices.method_23762().rotateZYX(angleZ, angleY, angleX);
   }
}
