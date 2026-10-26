package com.zigythebird.playeranim.animation;

import com.zigythebird.playeranim.PlayerAnimLibMod;
import com.zigythebird.playeranim.util.RenderUtil;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.HumanoidAnimationController;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import java.util.function.Function;
import net.minecraft.class_11890;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.MochaEngine;

public class PlayerAnimationController extends HumanoidAnimationController {
   protected final class_11890 avatar;

   public PlayerAnimationController(class_11890 avatar, AnimationController.AnimationStateHandler animationHandler) {
      this(avatar, animationHandler, MolangLoader::createNewEngine);
   }

   public PlayerAnimationController(
      class_11890 avatar,
      AnimationController.AnimationStateHandler animationHandler,
      Function<AnimationController, MochaEngine<AnimationController>> molangRuntime
   ) {
      super(animationHandler, molangRuntime);
      this.avatar = avatar;
   }

   public class_11890 getAvatar() {
      return this.avatar;
   }

   public boolean triggerAnimation(class_2960 newAnimation, float startAnimFrom) {
      if (PlayerAnimResources.hasAnimation(newAnimation)) {
         this.triggerAnimation(PlayerAnimResources.getAnimation(newAnimation), startAnimFrom);
         return true;
      } else {
         PlayerAnimLibMod.LOGGER.error("Could not find animation with the name:" + newAnimation);
         return false;
      }
   }

   public boolean triggerAnimation(class_2960 newAnimation) {
      return this.triggerAnimation(newAnimation, 0.0F);
   }

   public boolean replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable class_2960 newAnimation, boolean fadeFromNothing) {
      if (PlayerAnimResources.hasAnimation(newAnimation)) {
         this.replaceAnimationWithFade(fadeModifier, PlayerAnimResources.getAnimation(newAnimation), fadeFromNothing);
         return true;
      } else {
         return false;
      }
   }

   public boolean replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable class_2960 newAnimation) {
      return this.replaceAnimationWithFade(fadeModifier, newAnimation, true);
   }

   @Nullable
   public class_4587 getBoneWorldPositionPoseStack(String name, float tickDelta, class_243 cameraPos) {
      if (!this.activeBones.containsKey(name)) {
         return null;
      }

      class_4587 poseStack = new class_4587();
      Vec3f pivot = this.getBonePosition(name);
      class_243 position = this.avatar.method_30950(tickDelta).method_1020(cameraPos).method_1031(pivot.x(), pivot.y(), pivot.z());
      poseStack.method_22904(position.method_10216(), position.method_10214(), position.method_10215());
      poseStack.method_22907(
         class_7833.field_40716.rotationDegrees(180.0F - class_3532.method_16439(tickDelta, this.avatar.field_6220, this.avatar.field_6283))
      );
      RenderUtil.translateMatrixToBone(poseStack, this.activeBones.get(name));
      return poseStack;
   }
}
