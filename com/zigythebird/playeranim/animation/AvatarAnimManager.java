package com.zigythebird.playeranim.animation;

import com.zigythebird.playeranim.util.RenderUtil;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.layered.AnimationStack;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.class_11890;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_5603;
import net.minecraft.class_630;
import org.jetbrains.annotations.ApiStatus.Internal;

public class AvatarAnimManager extends AnimationStack {
   private final class_11890 avatar;
   private float lastUpdateTime;
   private boolean isFirstTick = true;
   private float tickDelta;
   private float firstPersonTransitionProgress = 0.0F;
   private boolean firstPersonTransitioningToPAL;

   public AvatarAnimManager(class_11890 avatar) {
      this.avatar = avatar;
   }

   public void tickAnimation(AnimationStack playerAnimManager, AnimationData state) {
      playerAnimManager.getLayers().removeIf(pairx -> pairx.right() == null || ((IAnimation)pairx.right()).canRemove());

      for (Pair<Integer, IAnimation> pair : playerAnimManager.getLayers()) {
         IAnimation animation = (IAnimation)pair.right();
         if (animation.isActive()) {
            animation.setupAnim(state.copy());
         }
      }

      this.finishFirstTick();
   }

   public float getLastUpdateTime() {
      return this.lastUpdateTime;
   }

   public void updatedAt(float updateTime) {
      this.lastUpdateTime = updateTime;
   }

   public boolean isFirstTick() {
      return this.isFirstTick;
   }

   protected void finishFirstTick() {
      this.isFirstTick = false;
   }

   public float getTickDelta() {
      return this.tickDelta;
   }

   public float getFirstPersonTransitionProgress() {
      return this.firstPersonTransitionProgress;
   }

   public boolean isFirstPersonTransitioningToPAL() {
      return this.firstPersonTransitioningToPAL;
   }

   @Internal
   public void setTickDelta(float tickDelta) {
      this.tickDelta = tickDelta;
   }

   public void updatePart(class_630 part, PlayerAnimBone bone) {
      class_5603 initialPose = part.method_41921();
      bone = this.get3DTransform(bone);
      RenderUtil.translatePartToBone(part, bone, initialPose);
   }

   public void handleAnimations(float partialTick, boolean fullTick, boolean isFirstPersonPass) {
      class_243 velocity = this.avatar.method_18798();
      AvatarAnimManager animatableManager = this.avatar.playerAnimLib$getAnimManager();
      int currentTick = this.avatar.field_6012;
      float currentFrameTime = currentTick + partialTick;
      AnimationData animationData = new AnimationData(
         (float)((Math.abs(velocity.field_1352) + Math.abs(velocity.field_1350)) / 2.0), partialTick, isFirstPersonPass
      );
      if (fullTick) {
         animatableManager.tick(animationData.copy());
      }

      if (animatableManager.isFirstTick() || currentFrameTime != animatableManager.getLastUpdateTime()) {
         if (!class_310.method_1551().method_1493()) {
            animatableManager.updatedAt(currentFrameTime);
         }

         this.tickAnimation(animatableManager, animationData);
      }
   }

   public class_11890 getAvatar() {
      return this.avatar;
   }

   @Override
   public void tick(AnimationData state) {
      super.tick(state);
      int firstPersonTransitionLength = this.getFirstPersonTransitionLength();
      float target = this.getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL ? 1.0F : 0.0F;
      if (firstPersonTransitionLength <= 0) {
         this.firstPersonTransitionProgress = target;
      } else {
         float step = 1.0F / firstPersonTransitionLength;
         if (this.firstPersonTransitionProgress < target) {
            this.firstPersonTransitionProgress += step;
            this.firstPersonTransitioningToPAL = true;
            if (this.firstPersonTransitionProgress > target) {
               this.firstPersonTransitionProgress = target;
            }
         } else if (this.firstPersonTransitionProgress > target) {
            this.firstPersonTransitionProgress -= step;
            this.firstPersonTransitioningToPAL = false;
            if (this.firstPersonTransitionProgress < target) {
               this.firstPersonTransitionProgress = target;
            }
         }
      }
   }
}
