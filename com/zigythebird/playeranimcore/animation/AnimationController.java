package com.zigythebird.playeranimcore.animation;

import com.zigythebird.playeranimcore.PlayerAnimLib;
import com.zigythebird.playeranimcore.animation.keyframe.AnimationPoint;
import com.zigythebird.playeranimcore.animation.keyframe.BoneAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.Keyframe;
import com.zigythebird.playeranimcore.animation.keyframe.KeyframeLocation;
import com.zigythebird.playeranimcore.animation.keyframe.KeyframeStack;
import com.zigythebird.playeranimcore.animation.keyframe.event.CustomKeyFrameEvents;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.CustomInstructionKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.KeyFrameData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.SoundKeyframeData;
import com.zigythebird.playeranimcore.animation.layered.AnimationContainer;
import com.zigythebird.playeranimcore.animation.layered.AnimationSnapshot;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractFadeModifier;
import com.zigythebird.playeranimcore.animation.layered.modifier.AbstractModifier;
import com.zigythebird.playeranimcore.animation.layered.modifier.SpeedModifier;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.bones.AdvancedBoneSnapshot;
import com.zigythebird.playeranimcore.bones.AdvancedPlayerAnimBone;
import com.zigythebird.playeranimcore.bones.PivotBone;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.AnimationStage;
import com.zigythebird.playeranimcore.enums.PlayState;
import com.zigythebird.playeranimcore.enums.State;
import com.zigythebird.playeranimcore.enums.TransformType;
import com.zigythebird.playeranimcore.event.EventResult;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import com.zigythebird.playeranimcore.util.MatrixUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;
import team.unnamed.mocha.MochaEngine;
import team.unnamed.mocha.parser.ast.FloatExpression;

public abstract class AnimationController implements IAnimation {
   public static KeyframeLocation<Keyframe> EMPTY_KEYFRAME_LOCATION = new KeyframeLocation<>(new Keyframe(0.0F), 0.0F);
   public static KeyframeLocation<Keyframe> EMPTY_SCALE_KEYFRAME_LOCATION = new KeyframeLocation<>(
      new Keyframe(0.0F, Collections.singletonList(FloatExpression.ONE), Collections.singletonList(FloatExpression.ONE)), 0.0F
   );
   protected final AnimationController.AnimationStateHandler stateHandler;
   protected final Map<String, Vec3f> bonePositions;
   protected final Map<String, AdvancedPlayerAnimBone> bones = new Object2ObjectOpenHashMap();
   protected final Map<String, PlayerAnimBone> activeBones = new Object2ObjectOpenHashMap();
   protected final Map<String, PivotBone> pivotBones = new Object2ObjectOpenHashMap();
   protected Queue<QueuedAnimation> animationQueue = new LinkedList<>();
   protected final MochaEngine<AnimationController> molangRuntime;
   protected boolean needsAnimationReload = false;
   protected CustomKeyFrameEvents.CustomKeyFrameHandler<SoundKeyframeData> soundKeyframeHandler = null;
   protected CustomKeyFrameEvents.CustomKeyFrameHandler<ParticleKeyframeData> particleKeyframeHandler = null;
   protected CustomKeyFrameEvents.CustomKeyFrameHandler<CustomInstructionKeyframeData> customKeyframeHandler = null;
   protected RawAnimation triggeredAnimation = null;
   protected boolean handlingTriggeredAnimations = false;
   protected RawAnimation currentRawAnimation;
   protected QueuedAnimation currentAnimation;
   protected int tick;
   protected float startAnimFrom;
   protected State animationState = State.STOPPED;
   protected boolean isLoopStarted = false;
   protected Consumer<Function<String, AdvancedPlayerAnimBone>> postAnimationSetupConsumer = function -> {};
   protected Function<AnimationController, EasingType> overrideEasingTypeFunction = controller -> null;
   private final Set<KeyFrameData> executedKeyFrames = new ObjectOpenHashSet();
   protected AnimationData animationData;
   protected Function<AnimationController, FirstPersonMode> firstPersonMode = null;
   protected Function<AnimationController, FirstPersonConfiguration> firstPersonConfiguration = null;
   private boolean firstPersonFollowsCamera = false;
   private int firstPersonTransitionLength = 0;
   private final List<AbstractModifier> modifiers = new ArrayList<>();
   private final AnimationController.InternalAnimationAccessor internalAnimationAccessor = new AnimationController.InternalAnimationAccessor(this);

   public AnimationController(
      AnimationController.AnimationStateHandler animationHandler,
      Map<String, Vec3f> bonePositions,
      Function<AnimationController, MochaEngine<AnimationController>> molangRuntime
   ) {
      this.stateHandler = animationHandler;
      this.bonePositions = bonePositions;
      this.molangRuntime = molangRuntime.apply(this);
      this.registerBones();
   }

   public abstract void registerBones();

   public void setFirstPersonFollowsCamera(boolean followsCamera) {
      this.firstPersonFollowsCamera = followsCamera;
   }

   @Override
   public boolean isFirstPersonFollowsCamera() {
      return this.firstPersonFollowsCamera;
   }

   public void setFirstPersonTransitionLength(int ticks) {
      this.firstPersonTransitionLength = Math.max(0, ticks);
   }

   @Override
   public int getFirstPersonTransitionLength() {
      return this.firstPersonTransitionLength;
   }

   public AnimationController setSoundKeyframeHandler(CustomKeyFrameEvents.CustomKeyFrameHandler<SoundKeyframeData> soundHandler) {
      this.soundKeyframeHandler = soundHandler;
      return this;
   }

   public AnimationController setParticleKeyframeHandler(CustomKeyFrameEvents.CustomKeyFrameHandler<ParticleKeyframeData> particleHandler) {
      this.particleKeyframeHandler = particleHandler;
      return this;
   }

   public AnimationController setCustomInstructionKeyframeHandler(
      CustomKeyFrameEvents.CustomKeyFrameHandler<CustomInstructionKeyframeData> customInstructionHandler
   ) {
      this.customKeyframeHandler = customInstructionHandler;
      return this;
   }

   public AnimationController setPostAnimationSetupConsumer(Consumer<Function<String, AdvancedPlayerAnimBone>> postAnimationSetupConsumer) {
      this.postAnimationSetupConsumer = postAnimationSetupConsumer;
      return this;
   }

   public AnimationController setOverrideEasingType(EasingType easingTypeFunction) {
      return this.setOverrideEasingTypeFunction(animatable -> easingTypeFunction);
   }

   public AnimationController setOverrideEasingTypeFunction(Function<AnimationController, EasingType> easingType) {
      this.overrideEasingTypeFunction = easingType;
      return this;
   }

   public AnimationController receiveTriggeredAnimations() {
      this.handlingTriggeredAnimations = true;
      return this;
   }

   @Nullable
   public QueuedAnimation getCurrentAnimation() {
      return this.currentAnimation;
   }

   @Nullable
   public Animation getCurrentAnimationInstance() {
      QueuedAnimation queuedAnimation = this.getCurrentAnimation();
      if (queuedAnimation != null) {
         Animation animation = queuedAnimation.animation();
         if (animation != null) {
            return animation;
         }
      }

      RawAnimation rawAnimation = this.getTriggeredAnimation();
      if (rawAnimation != null) {
         List<RawAnimation.Stage> stages = rawAnimation.getAnimationStages();
         if (!stages.isEmpty()) {
            return stages.getFirst().animation();
         }
      }

      return null;
   }

   @Nullable
   public RawAnimation getTriggeredAnimation() {
      return this.triggeredAnimation;
   }

   @NotNull
   public State getAnimationState() {
      return this.animationState;
   }

   public boolean isLoopStarted() {
      return this.isLoopStarted;
   }

   @Override
   public boolean isActive() {
      return this.animationState.isActive();
   }

   public AnimationData getAnimationData() {
      return this.animationData;
   }

   public void forceAnimationReset() {
      this.needsAnimationReload = true;
   }

   public void stop() {
      this.animationState = State.STOPPED;
      this.resetEventKeyFrames();
   }

   public void pause() {
      this.animationState = State.PAUSED;
   }

   public void unpause() {
      if (this.animationState == State.PAUSED) {
         this.animationState = State.RUNNING;
      }
   }

   public boolean hasAnimationFinished() {
      return this.currentRawAnimation != null && this.animationState == State.STOPPED;
   }

   public RawAnimation getCurrentRawAnimation() {
      return this.currentRawAnimation;
   }

   public boolean isPlayingTriggeredAnimation() {
      return this.triggeredAnimation != null && !this.hasAnimationFinished();
   }

   protected void setAnimation(RawAnimation rawAnimation, float startAnimFrom) {
      if (rawAnimation != null && !rawAnimation.getAnimationStages().isEmpty()) {
         if (this.needsAnimationReload || !rawAnimation.equals(this.currentRawAnimation)) {
            Queue<QueuedAnimation> animations = this.getQueuedAnimations(rawAnimation);
            if (animations != null) {
               this.animationQueue = animations;
               this.currentRawAnimation = rawAnimation;
               this.startAnimFrom = startAnimFrom;
               this.tick = 0;
               this.animationState = State.RUNNING;
               this.currentAnimation = this.animationQueue.poll();
               this.setupNewAnimation();
               this.needsAnimationReload = false;
               return;
            }

            this.stop();
         }
      } else {
         this.stop();
      }
   }

   protected void setAnimation(RawAnimation rawAnimation) {
      this.setAnimation(rawAnimation, 0.0F);
   }

   protected Queue<QueuedAnimation> getQueuedAnimations(RawAnimation rawAnimation) {
      LinkedList<QueuedAnimation> animations = new LinkedList<>();

      for (RawAnimation.Stage stage : rawAnimation.getAnimationStages()) {
         Animation animation;
         if (stage.stage() == AnimationStage.WAIT) {
            animation = Animation.generateWaitAnimation(stage.additionalTicks());
         } else {
            animation = stage.animation();
         }

         if (animation != null) {
            animations.add(new QueuedAnimation(animation, stage.loopType()));
         }
      }

      return animations;
   }

   public void triggerAnimation(RawAnimation newAnimation, float startAnimFrom) {
      if (newAnimation != null) {
         this.stop();
         this.triggeredAnimation = newAnimation;
         this.needsAnimationReload = true;
         this.animationState = State.RUNNING;
         this.tick = 0;
         this.startAnimFrom = startAnimFrom;
      }
   }

   public void triggerAnimation(RawAnimation newAnimation) {
      this.triggerAnimation(newAnimation, 0.0F);
   }

   public void triggerAnimation(Animation newAnimation, float startAnimFrom) {
      this.triggerAnimation(RawAnimation.begin().then(newAnimation, Animation.LoopType.DEFAULT), startAnimFrom);
   }

   public void triggerAnimation(Animation newAnimation) {
      this.triggerAnimation(RawAnimation.begin().then(newAnimation, Animation.LoopType.DEFAULT), 0.0F);
   }

   public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable RawAnimation newAnimation, boolean fadeFromNothing) {
      if (fadeFromNothing || this.isActive()) {
         if (this.isActive()) {
            Map<String, AdvancedBoneSnapshot> snapshots = new HashMap<>();

            for (PlayerAnimBone bone : this.activeBones.values()) {
               snapshots.put(bone.getName(), new AdvancedBoneSnapshot(bone));
            }

            fadeModifier.setTransitionAnimation(new AnimationSnapshot(snapshots));
         }

         this.addModifierLast(fadeModifier);
      }

      this.triggerAnimation(newAnimation);
   }

   public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable RawAnimation newAnimation) {
      this.replaceAnimationWithFade(fadeModifier, newAnimation, true);
   }

   public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable Animation newAnimation, boolean fadeFromNothing) {
      this.replaceAnimationWithFade(fadeModifier, RawAnimation.begin().then(newAnimation, Animation.LoopType.DEFAULT), fadeFromNothing);
   }

   public void replaceAnimationWithFade(@NotNull AbstractFadeModifier fadeModifier, @Nullable Animation newAnimation) {
      this.replaceAnimationWithFade(fadeModifier, newAnimation, true);
   }

   public boolean stopTriggeredAnimation() {
      if (this.triggeredAnimation == null) {
         return false;
      }

      if (this.currentRawAnimation == this.triggeredAnimation) {
         this.currentAnimation = null;
         this.currentRawAnimation = null;
      }

      this.triggeredAnimation = null;
      this.needsAnimationReload = true;
      return true;
   }

   protected PlayState handleAnimation(AnimationData state) {
      if (this.triggeredAnimation != null) {
         if (this.currentRawAnimation != this.triggeredAnimation) {
            this.currentAnimation = null;
         }

         this.setAnimation(this.triggeredAnimation, this.startAnimFrom);
         if (!this.hasAnimationFinished() && !this.handlingTriggeredAnimations) {
            return PlayState.CONTINUE;
         }

         this.triggeredAnimation = null;
         this.needsAnimationReload = true;
      }

      return this.stateHandler.handle(this, state, (animation, startTick) -> {
         this.setAnimation(animation, startTick - state.getPartialTick());
         return PlayState.CONTINUE;
      });
   }

   public void process(AnimationData state) {
      float adjustedTick = state.getPartialTick() + this.startAnimFrom + this.tick;
      PlayState playState = this.handleAnimation(state);
      if (playState != PlayState.STOP && (this.currentAnimation != null || !this.animationQueue.isEmpty())) {
         if (this.getAnimationState() == State.RUNNING) {
            this.processCurrentAnimation(adjustedTick, state);
         }
      } else {
         this.animationState = State.STOPPED;
      }
   }

   public float getAnimationSpeed() {
      float speed = 1.0F;

      for (AbstractModifier modifier : this.modifiers) {
         if (modifier instanceof SpeedModifier speedModifier) {
            speed *= speedModifier.speed;
         }
      }

      return speed;
   }

   private void processCurrentAnimation(float adjustedTick, AnimationData animationData) {
      QueuedAnimation queued = this.currentAnimation;
      if (queued == null) {
         this.animationState = State.STOPPED;
      } else {
         Animation animation = queued.animation();
         if (adjustedTick >= animation.length()) {
            if (queued.loopType().shouldPlayAgain(this, animation)) {
               if (this.animationState != State.PAUSED) {
                  this.tick = 0;
                  this.startAnimFrom = queued.loopType().restartFromTick(this, animation);
                  adjustedTick = this.startAnimFrom;
                  this.startAnimFrom = this.startAnimFrom - animationData.getPartialTick();
                  this.resetEventKeyFrames();
                  this.isLoopStarted = true;
               }
            } else {
               QueuedAnimation nextAnimation = this.animationQueue.peek();
               this.resetEventKeyFrames();
               if (nextAnimation == null) {
                  this.animationState = State.STOPPED;
                  this.currentAnimation = null;

                  for (AdvancedPlayerAnimBone bone : this.bones.values()) {
                     bone.setToInitialPose();
                  }

                  for (PlayerAnimBone bone : this.pivotBones.values()) {
                     bone.setToInitialPose();
                  }

                  return;
               }

               this.animationState = State.RUNNING;
               this.tick = 0;
               this.startAnimFrom = -animationData.getPartialTick();
               adjustedTick = 0.0F;
               this.currentAnimation = this.animationQueue.poll();
               queued = this.currentAnimation;
               this.setupNewAnimation();
            }
         }

         if (queued != null) {
            for (PlayerAnimBone bone : this.bones.values()) {
               bone.setToInitialPose();
            }

            for (PlayerAnimBone bone : this.pivotBones.values()) {
               bone.setToInitialPose();
            }

            for (Entry<String, BoneAnimation> entry : animation.boneAnimations().entrySet()) {
               PlayerAnimBone bone = this.bones.getOrDefault(entry.getKey(), null);
               boolean isAdvancedBone = false;
               AdvancedPlayerAnimBone advancedBone = null;
               if (bone == null) {
                  bone = this.pivotBones.getOrDefault(entry.getKey(), null);
               } else {
                  advancedBone = (AdvancedPlayerAnimBone)bone;
                  isAdvancedBone = true;
               }

               if (bone != null) {
                  BoneAnimation boneAnimation = entry.getValue();
                  KeyframeStack rotationKeyFrames = boneAnimation.rotationKeyFrames();
                  KeyframeStack positionKeyFrames = boneAnimation.positionKeyFrames();
                  KeyframeStack scaleKeyFrames = boneAnimation.scaleKeyFrames();
                  List<Keyframe> bendKeyFrames = boneAnimation.bendKeyFrames();
                  AnimationPoint rotXPoint = this.getAnimationPointAtTick(
                     queued,
                     rotationKeyFrames.xKeyframes(),
                     adjustedTick,
                     TransformType.ROTATION,
                     isAdvancedBone ? advancedBone::setRotXTransitionLength : null
                  );
                  AnimationPoint rotYPoint = this.getAnimationPointAtTick(
                     queued,
                     rotationKeyFrames.yKeyframes(),
                     adjustedTick,
                     TransformType.ROTATION,
                     isAdvancedBone ? advancedBone::setRotYTransitionLength : null
                  );
                  AnimationPoint rotZPoint = this.getAnimationPointAtTick(
                     queued,
                     rotationKeyFrames.zKeyframes(),
                     adjustedTick,
                     TransformType.ROTATION,
                     isAdvancedBone ? advancedBone::setRotZTransitionLength : null
                  );
                  AnimationPoint posXPoint = this.getAnimationPointAtTick(
                     queued,
                     positionKeyFrames.xKeyframes(),
                     adjustedTick,
                     TransformType.POSITION,
                     isAdvancedBone ? advancedBone::setPositionXTransitionLength : null
                  );
                  AnimationPoint posYPoint = this.getAnimationPointAtTick(
                     queued,
                     positionKeyFrames.yKeyframes(),
                     adjustedTick,
                     TransformType.POSITION,
                     isAdvancedBone ? advancedBone::setPositionYTransitionLength : null
                  );
                  AnimationPoint posZPoint = this.getAnimationPointAtTick(
                     queued,
                     positionKeyFrames.zKeyframes(),
                     adjustedTick,
                     TransformType.POSITION,
                     isAdvancedBone ? advancedBone::setPositionZTransitionLength : null
                  );
                  AnimationPoint scaleXPoint = this.getAnimationPointAtTick(
                     queued, scaleKeyFrames.xKeyframes(), adjustedTick, TransformType.SCALE, isAdvancedBone ? advancedBone::setScaleXTransitionLength : null
                  );
                  AnimationPoint scaleYPoint = this.getAnimationPointAtTick(
                     queued, scaleKeyFrames.yKeyframes(), adjustedTick, TransformType.SCALE, isAdvancedBone ? advancedBone::setScaleYTransitionLength : null
                  );
                  AnimationPoint scaleZPoint = this.getAnimationPointAtTick(
                     queued, scaleKeyFrames.zKeyframes(), adjustedTick, TransformType.SCALE, isAdvancedBone ? advancedBone::setScaleZTransitionLength : null
                  );
                  AnimationPoint bendPoint = this.getAnimationPointAtTick(
                     queued, bendKeyFrames, adjustedTick, TransformType.BEND, isAdvancedBone ? advancedBone::setBendTransitionLength : null
                  );
                  EasingType easingType = this.overrideEasingTypeFunction.apply(this);
                  bone.setRotX(EasingType.lerpWithOverride(this.molangRuntime, rotXPoint, easingType));
                  bone.setRotY(EasingType.lerpWithOverride(this.molangRuntime, rotYPoint, easingType));
                  bone.setRotZ(EasingType.lerpWithOverride(this.molangRuntime, rotZPoint, easingType));
                  bone.setPosX(EasingType.lerpWithOverride(this.molangRuntime, posXPoint, easingType));
                  bone.setPosY(EasingType.lerpWithOverride(this.molangRuntime, posYPoint, easingType));
                  bone.setPosZ(EasingType.lerpWithOverride(this.molangRuntime, posZPoint, easingType));
                  bone.setScaleX(EasingType.lerpWithOverride(this.molangRuntime, scaleXPoint, easingType));
                  bone.setScaleY(EasingType.lerpWithOverride(this.molangRuntime, scaleYPoint, easingType));
                  bone.setScaleZ(EasingType.lerpWithOverride(this.molangRuntime, scaleZPoint, easingType));
                  bone.setBend(EasingType.lerpWithOverride(this.molangRuntime, bendPoint, easingType));
               }
            }

            this.applyCustomPivotPoints();
            this.handleCustomKeyframe(
               animation.keyFrames().sounds(), this.soundKeyframeHandler, CustomKeyFrameEvents.SOUND_KEYFRAME_EVENT.invoker(), adjustedTick, animationData
            );
            this.handleCustomKeyframe(
               animation.keyFrames().particles(),
               this.particleKeyframeHandler,
               CustomKeyFrameEvents.PARTICLE_KEYFRAME_EVENT.invoker(),
               adjustedTick,
               animationData
            );
            this.handleCustomKeyframe(
               animation.keyFrames().customInstructions(),
               this.customKeyframeHandler,
               CustomKeyFrameEvents.CUSTOM_INSTRUCTION_KEYFRAME_EVENT.invoker(),
               adjustedTick,
               animationData
            );
         }
      }
   }

   protected void applyCustomPivotPoints() {
      if (this.currentAnimation != null) {
         Map<String, String> parentsMap = this.currentAnimation.animation().parents();
         if (!parentsMap.isEmpty()) {
            Set<String> processedBones = new HashSet<>();

            for (PlayerAnimBone bone : this.bones.values()) {
               this.processBoneHierarchy(bone, parentsMap, processedBones);
            }

            for (PlayerAnimBone bone : this.pivotBones.values()) {
               this.processBoneHierarchy(bone, parentsMap, processedBones);
            }
         }
      }
   }

   private void processBoneHierarchy(PlayerAnimBone bone, Map<String, String> parentsMap, Set<String> processedBones) {
      String boneName = bone.getName();
      if (!processedBones.contains(boneName)) {
         String parentName = parentsMap.get(boneName);
         if (parentName == null) {
            processedBones.add(boneName);
         } else {
            PlayerAnimBone parent = this.pivotBones.get(parentName);
            if (parent == null) {
               parent = this.bones.get(parentName);
            }

            if (parent == null) {
               PlayerAnimLib.LOGGER.error("Parent {} not found for {}", parentName, boneName);
            } else {
               this.processBoneHierarchy(parent, parentsMap, processedBones);
               this.activeBones.put(boneName, bone);
               MatrixUtil.applyParentsToChild(bone, Collections.singletonList(parent), this::getBonePosition);
               processedBones.add(boneName);
            }
         }
      }
   }

   protected <T extends KeyFrameData> void handleCustomKeyframe(
      T[] keyframes,
      @Nullable CustomKeyFrameEvents.CustomKeyFrameHandler<T> main,
      CustomKeyFrameEvents.CustomKeyFrameHandler<T> event,
      float animationTick,
      AnimationData animationData
   ) {
      for (T keyframeData : keyframes) {
         if (animationTick >= keyframeData.getStartTick() && this.executedKeyFrames.add(keyframeData)) {
            EventResult result = main == null ? EventResult.PASS : main.handle(animationTick, this, keyframeData, animationData);
            if (result == EventResult.PASS) {
               result = event.handle(animationTick, this, keyframeData, animationData);
            }

            if (result == EventResult.FAIL) {
               return;
            }
         }
      }
   }

   public float getAnimationTime() {
      return this.getAnimationTicks() / 20.0F;
   }

   public float getAnimationTicks() {
      return this.animationData == null ? 0.0F : this.tick + this.startAnimFrom + this.animationData.getPartialTick();
   }

   protected void setupNewAnimation() {
      this.isLoopStarted = false;
      if (this.currentAnimation != null) {
         this.activeBones.clear();
         this.resetEventKeyFrames();

         for (AdvancedPlayerAnimBone bone : this.bones.values()) {
            bone.setEnabled(this.currentAnimation.animation().getBone(bone.getName()) != null);
         }

         for (Entry<String, BoneAnimation> entry : this.currentAnimation.animation().boneAnimations().entrySet()) {
            if (this.bones.containsKey(entry.getKey())) {
               AdvancedPlayerAnimBone bone = this.bones.get(entry.getKey());
               this.activeBones.put(entry.getKey(), bone);
               if (this.currentAnimation.isDisableAxisIfNotModified()) {
                  BoneAnimation boneAnimation = entry.getValue();
                  bone.positionXEnabled = !boneAnimation.positionKeyFrames().xKeyframes().isEmpty();
                  bone.positionYEnabled = !boneAnimation.positionKeyFrames().yKeyframes().isEmpty();
                  bone.positionZEnabled = !boneAnimation.positionKeyFrames().zKeyframes().isEmpty();
                  bone.rotXEnabled = !boneAnimation.rotationKeyFrames().xKeyframes().isEmpty();
                  bone.rotYEnabled = !boneAnimation.rotationKeyFrames().yKeyframes().isEmpty();
                  bone.rotZEnabled = !boneAnimation.rotationKeyFrames().zKeyframes().isEmpty();
                  bone.scaleXEnabled = !boneAnimation.scaleKeyFrames().xKeyframes().isEmpty();
                  bone.scaleYEnabled = !boneAnimation.scaleKeyFrames().yKeyframes().isEmpty();
                  bone.scaleZEnabled = !boneAnimation.scaleKeyFrames().zKeyframes().isEmpty();
                  bone.bendEnabled = !boneAnimation.bendKeyFrames().isEmpty();
               } else {
                  bone.setEnabled(true);
               }
            } else if (this.pivotBones.containsKey(entry.getKey())) {
               this.activeBones.put(entry.getKey(), this.pivotBones.get(entry.getKey()));
            }
         }

         for (String entry : this.currentAnimation.animation().parents().keySet()) {
            if (this.bones.containsKey(entry)) {
               this.bones.get(entry).setEnabled(true);
            }
         }

         this.pivotBones.clear();

         for (Entry<String, Vec3f> entry : this.currentAnimation.animation().bones().entrySet()) {
            this.pivotBones.put(entry.getKey(), new PivotBone(entry.getKey(), entry.getValue()));
         }

         this.postAnimationSetupConsumer.accept(this.bones::get);
      }
   }

   private AnimationPoint getAnimationPointAtTick(
      QueuedAnimation queued, List<Keyframe> frames, float tick, TransformType type, Consumer<Float> transitionLengthSetter
   ) {
      Animation animation = queued.animation();
      ExtraAnimationData extraData = animation.data();
      float endTick = extraData.<Float>get("endTick").orElse(animation.length() - 1.0F);
      KeyframeLocation<Keyframe> location = this.getCurrentKeyFrameLocation(
         frames,
         tick,
         type,
         extraData.isAnimationPlayerAnimatorFormat() && queued.loopType().shouldPlayAgain(null, animation),
         animation.length(),
         queued.loopType().restartFromTick(null, animation)
      );
      Keyframe currentFrame = location.keyframe();
      float startValue = this.molangRuntime.eval(currentFrame.startValue());
      float endValue = this.molangRuntime.eval(currentFrame.endValue());
      if (type == TransformType.ROTATION || type == TransformType.BEND) {
         if (!MolangLoader.isConstant(currentFrame.startValue())) {
            startValue = (float)Math.toRadians(startValue);
         }

         if (!MolangLoader.isConstant(currentFrame.endValue())) {
            endValue = (float)Math.toRadians(endValue);
         }
      }

      if (transitionLengthSetter != null) {
         if (queued.hasBeginTick() && !frames.isEmpty() && currentFrame == frames.getFirst() && extraData.<Float>get("beginTick").get() > tick) {
            startValue = endValue;
            transitionLengthSetter.accept(currentFrame.length());
         } else if (queued.hasEndTick() && !frames.isEmpty() && currentFrame == frames.getLast() && endTick <= tick) {
            transitionLengthSetter.accept(animation.length() - endTick);
         } else {
            transitionLengthSetter.accept(null);
         }
      }

      return new AnimationPoint(currentFrame.easingType(), currentFrame.easingArgs(), location.startTick(), currentFrame.length(), startValue, endValue);
   }

   private KeyframeLocation<Keyframe> getCurrentKeyFrameLocation(
      List<Keyframe> frames, float ageInTicks, TransformType type, boolean isPlayerAnimatorLoop, float animTime, float returnToTick
   ) {
      if (frames.isEmpty()) {
         return type == TransformType.SCALE ? EMPTY_SCALE_KEYFRAME_LOCATION : EMPTY_KEYFRAME_LOCATION;
      }

      Keyframe firstFrame = returnToTick == 0.0F ? frames.getFirst() : Keyframe.getKeyframeAtTime(frames, returnToTick);
      float totalFrameTime = 0.0F;

      for (Keyframe frame : frames) {
         totalFrameTime += frame.length();
         if (totalFrameTime > ageInTicks) {
            if (isPlayerAnimatorLoop && this.isLoopStarted() && frame == firstFrame) {
               float stopTickMinusLastKeyframe = animTime - Keyframe.getLastKeyframeTime(frames);
               return new KeyframeLocation<>(
                  new Keyframe(
                     frame.length() + stopTickMinusLastKeyframe, frames.getLast().endValue(), frame.endValue(), frame.easingType(), frame.easingArgs()
                  ),
                  ageInTicks + stopTickMinusLastKeyframe
               );
            }

            return new KeyframeLocation<>(frame, ageInTicks - (totalFrameTime - frame.length()));
         }
      }

      return isPlayerAnimatorLoop
         ? new KeyframeLocation<>(
            new Keyframe(
               firstFrame.length() + animTime - totalFrameTime,
               frames.getLast().endValue(),
               firstFrame.endValue(),
               firstFrame.easingType(),
               firstFrame.easingArgs()
            ),
            ageInTicks - totalFrameTime
         )
         : new KeyframeLocation<>(frames.getLast(), ageInTicks);
   }

   protected void resetEventKeyFrames() {
      if (!this.executedKeyFrames.isEmpty()) {
         CustomKeyFrameEvents.RESET_KEYFRAMES_EVENT.invoker().handle(this, this.executedKeyFrames);
      }

      this.executedKeyFrames.clear();
   }

   public PlayerAnimBone get3DTransformRaw(@NotNull PlayerAnimBone bone) {
      if (this.activeBones.containsKey(bone.getName())) {
         PlayerAnimBone bone1 = this.activeBones.get(bone.getName());
         QueuedAnimation queued = this.currentAnimation;
         if (queued != null && bone1 instanceof AdvancedPlayerAnimBone advancedBone) {
            ExtraAnimationData extraData = queued.animation().data();
            if (queued.hasBeginTick() && extraData.<Float>get("beginTick").get() > this.getAnimationTicks()) {
               bone.beginOrEndTickLerp(advancedBone, this.getAnimationTicks(), null);
            } else if (queued.hasEndTick() && extraData.<Float>get("endTick").get() <= this.getAnimationTicks()) {
               bone.beginOrEndTickLerp(advancedBone, this.getAnimationTicks() - extraData.<Float>get("endTick").get(), this.currentAnimation.animation());
            } else {
               bone.copyOtherBoneIfNotDisabled(bone1);
            }
         } else {
            bone.copyOtherBoneIfNotDisabled(bone1);
         }
      }

      return bone;
   }

   @Override
   public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
      return !this.modifiers.isEmpty() ? this.modifiers.getFirst().get3DTransform(bone) : this.get3DTransformRaw(bone);
   }

   @NotNull
   @Override
   public FirstPersonMode getFirstPersonMode() {
      return this.firstPersonMode != null ? this.firstPersonMode.apply(this) : FirstPersonMode.NONE;
   }

   @NotNull
   @Override
   public FirstPersonConfiguration getFirstPersonConfiguration() {
      return this.firstPersonConfiguration != null ? this.firstPersonConfiguration.apply(this) : IAnimation.DEFAULT_FIRST_PERSON_CONFIG;
   }

   public AnimationController setFirstPersonMode(FirstPersonMode mode) {
      this.firstPersonMode = controller -> mode;
      return this;
   }

   public AnimationController setFirstPersonModeHandler(Function<AnimationController, FirstPersonMode> modeHandler) {
      this.firstPersonMode = modeHandler;
      return this;
   }

   public AnimationController setFirstPersonConfiguration(FirstPersonConfiguration config) {
      this.firstPersonConfiguration = controller -> config;
      return this;
   }

   public AnimationController setFirstPersonConfigurationHandler(Function<AnimationController, FirstPersonConfiguration> configHandler) {
      this.firstPersonConfiguration = configHandler;
      return this;
   }

   @Override
   public void tick(AnimationData state) {
      this.animationData = state;

      for (int i = 0; i < this.modifiers.size(); i++) {
         if (this.modifiers.get(i).canRemove()) {
            this.removeModifier(i--);
         }
      }

      if (!this.modifiers.isEmpty()) {
         this.modifiers.getFirst().tick(state);
      } else {
         this.handleAnimation(state);
         if (this.animationState == State.RUNNING) {
            this.tick++;
         }
      }
   }

   @Override
   public void setupAnim(AnimationData state) {
      this.animationData = state;
      if (!this.modifiers.isEmpty()) {
         this.modifiers.getFirst().setupAnim(state);
      } else {
         this.process(state);
      }
   }

   public Vec3f getBonePosition(String name) {
      if (this.bonePositions.containsKey(name)) {
         return this.bonePositions.get(name);
      } else {
         return this.pivotBones.containsKey(name) ? this.pivotBones.get(name).getPivot() : Vec3f.ZERO;
      }
   }

   @Internal
   public List<AbstractModifier> getModifiers() {
      return this.modifiers;
   }

   public AnimationController addModifier(@NotNull AbstractModifier modifier, int idx) {
      modifier.setHost(this);
      this.modifiers.add(idx, modifier);
      this.linkModifiers();
      return this;
   }

   public AnimationController addModifierBefore(@NotNull AbstractModifier modifier) {
      this.addModifier(modifier, 0);
      return this;
   }

   public AnimationController addModifierLast(@NotNull AbstractModifier modifier) {
      this.addModifier(modifier, this.modifiers.size());
      return this;
   }

   public AnimationController removeModifier(int idx) {
      this.modifiers.remove(idx);
      this.linkModifiers();
      return this;
   }

   public AnimationController removeAllModifiers() {
      this.modifiers.clear();
      return this;
   }

   public int getModifierCount() {
      return this.modifiers.size();
   }

   @Nullable
   public AbstractModifier getModifier(int idx) {
      try {
         return this.modifiers.get(idx);
      } catch (IndexOutOfBoundsException e) {
         return null;
      }
   }

   public boolean removeModifierIf(Predicate<? super AbstractModifier> predicate) {
      boolean success = this.modifiers.removeIf(predicate);
      this.linkModifiers();
      return success;
   }

   protected void linkModifiers() {
      Iterator<AbstractModifier> modifierIterator = this.modifiers.iterator();
      if (modifierIterator.hasNext()) {
         AbstractModifier tmp = modifierIterator.next();

         while (modifierIterator.hasNext()) {
            AbstractModifier tmp2 = modifierIterator.next();
            tmp.setAnim(tmp2);
            tmp = tmp2;
         }

         tmp.setAnim(this.internalAnimationAccessor);
      }
   }

   public AdvancedPlayerAnimBone registerPlayerAnimBone(String name) {
      return this.registerPlayerAnimBone(new AdvancedPlayerAnimBone(name));
   }

   public AdvancedPlayerAnimBone registerPlayerAnimBone(AdvancedPlayerAnimBone bone) {
      this.bones.put(bone.getName(), bone);
      return bone;
   }

   @Nullable
   public AdvancedPlayerAnimBone getBone(String name) {
      return this.bones.get(name);
   }

   @Override
   public String toString() {
      return "AnimationController{currentAnimation="
         + this.getCurrentAnimationInstance()
         + ", tick="
         + this.getAnimationTicks()
         + ", modifiers="
         + this.modifiers
         + "}";
   }

   @FunctionalInterface
   public interface AnimationSetter {
      default PlayState setAnimation(RawAnimation animation) {
         return this.setAnimation(animation, 0);
      }

      PlayState setAnimation(RawAnimation var1, int var2);
   }

   @FunctionalInterface
   public interface AnimationStateHandler {
      PlayState handle(AnimationController var1, AnimationData var2, AnimationController.AnimationSetter var3);
   }

   private static class InternalAnimationAccessor extends AnimationContainer<AnimationController> {
      private InternalAnimationAccessor(AnimationController controller) {
         super(controller);
      }

      @Override
      public void tick(AnimationData state) {
         this.anim.handleAnimation(state);
         if (this.anim.animationState == State.RUNNING) {
            this.anim.tick++;
         }
      }

      @Override
      public void setupAnim(AnimationData state) {
         this.anim.process(state);
      }

      @Override
      public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
         return this.anim.get3DTransformRaw(bone);
      }
   }
}
