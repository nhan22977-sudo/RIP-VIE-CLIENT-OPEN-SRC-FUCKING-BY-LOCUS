package com.zigythebird.playeranim.animation.layered.modifier;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranimcore.animation.layered.modifier.MirrorModifier;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import net.minecraft.class_1306;
import net.minecraft.class_310;
import org.jetbrains.annotations.NotNull;

public class MirrorIfLeftHandModifier extends MirrorModifier {
   @Override
   public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
      return this.getController() instanceof PlayerAnimationController controller
            && controller.getAvatar() == class_310.method_1551().field_1724
            && class_310.method_1551().field_1690.method_42552().method_41753() == class_1306.field_6182
         ? bone
         : super.get3DTransform(bone);
   }
}
