package com.zigythebird.playeranimcore.animation.layered.modifier;

import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class MirrorModifier extends AbstractModifier {
   public static final Map<String, String> mirrorMap;
   public boolean enabled = true;

   @Override
   public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
      if (!this.enabled) {
         return super.get3DTransform(bone);
      }

      String modelName = bone.getName();
      if (mirrorMap.containsKey(modelName)) {
         modelName = mirrorMap.get(modelName);
      }

      this.transformBone(bone);
      PlayerAnimBone newBone = new PlayerAnimBone(modelName);
      newBone.copyOtherBone(bone);
      newBone = super.get3DTransform(newBone);
      this.transformBone(newBone);
      bone.copyOtherBone(newBone);
      return bone;
   }

   @NotNull
   @Override
   public FirstPersonConfiguration getFirstPersonConfiguration() {
      FirstPersonConfiguration configuration = super.getFirstPersonConfiguration();
      return !this.enabled
         ? configuration
         : new FirstPersonConfiguration()
            .setShowLeftArm(configuration.isShowRightArm())
            .setShowRightArm(configuration.isShowLeftArm())
            .setShowLeftItem(configuration.isShowRightItem())
            .setShowRightItem(configuration.isShowLeftItem());
   }

   protected void transformBone(PlayerAnimBone bone) {
      bone.positionX *= -1.0F;
      bone.rotY *= -1.0F;
      bone.rotZ *= -1.0F;
      bone.bend *= -1.0F;
   }

   @Override
   public String toString() {
      return "MirrorModifier{anim=" + this.anim + ", enabled=" + this.enabled + "}";
   }

   static {
      HashMap<String, String> partMap = new HashMap<>();
      partMap.put("left_arm", "right_arm");
      partMap.put("left_leg", "right_leg");
      partMap.put("left_item", "right_item");
      partMap.put("right_arm", "left_arm");
      partMap.put("right_leg", "left_leg");
      partMap.put("right_item", "left_item");
      mirrorMap = Collections.unmodifiableMap(partMap);
   }
}
