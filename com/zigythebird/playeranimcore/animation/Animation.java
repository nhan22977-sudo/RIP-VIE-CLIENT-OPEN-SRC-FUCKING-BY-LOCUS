package com.zigythebird.playeranimcore.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.zigythebird.playeranimcore.animation.keyframe.BoneAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.CustomInstructionKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.SoundKeyframeData;
import com.zigythebird.playeranimcore.enums.AnimationStage;
import com.zigythebird.playeranimcore.loading.UniversalAnimLoader;
import com.zigythebird.playeranimcore.math.Vec3f;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Animation(
   ExtraAnimationData data,
   float length,
   Animation.LoopType loopType,
   Map<String, BoneAnimation> boneAnimations,
   Animation.Keyframes keyFrames,
   Map<String, Vec3f> bones,
   Map<String, String> parents
) implements Supplier<UUID> {
   static Animation generateWaitAnimation(float length) {
      return new Animation(
         new ExtraAnimationData("name", AnimationStage.WAIT.name()),
         length,
         Animation.LoopType.PLAY_ONCE,
         Collections.emptyMap(),
         UniversalAnimLoader.NO_KEYFRAMES,
         new HashMap<>(),
         new HashMap<>()
      );
   }

   public boolean isPlayingAt(float tick) {
      return this.loopType.shouldPlayAgain(null, this) || tick < this.length() && tick > 0.0F;
   }

   @Nullable
   public BoneAnimation getBone(String id) {
      return this.boneAnimations.get(id);
   }

   public Optional<BoneAnimation> getBoneOptional(String id) {
      return Optional.ofNullable(this.getBone(id));
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof Animation animation)
         ? false
         : Float.compare(this.length, animation.length) == 0
            && Objects.equals(this.keyFrames, animation.keyFrames)
            && Objects.equals(this.bones, animation.bones)
            && Objects.equals(this.parents, animation.parents)
            && Objects.equals(this.boneAnimations, animation.boneAnimations);
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.length, this.boneAnimations, this.keyFrames, this.bones, this.parents);
   }

   private UUID generateUuid() {
      return generateUuid(
         Float.floatToIntBits(this.length), this.boneAnimations.hashCode(), this.keyFrames.hashCode(), this.bones.hashCode(), this.parents.hashCode()
      );
   }

   private static UUID generateUuid(int... hashes) {
      long mostSigBits = 17L;
      long leastSigBits = 31L;

      for (int hash : hashes) {
         mostSigBits = 31L * mostSigBits + hash;
         leastSigBits = 37L * leastSigBits + hash;
      }

      return new UUID(mostSigBits, leastSigBits);
   }

   public UUID uuid() {
      if (!this.data().has("uuid")) {
         this.data().put("uuid", this.generateUuid());
      } else if (this.data().getRaw("uuid") instanceof String str) {
         this.data().put("uuid", UUID.fromString(str));
      }

      return this.data().<UUID>get("uuid").orElseThrow();
   }

   @NotNull
   @Override
   public String toString() {
      return "Animation{data=" + this.data + ", length=" + this.length + "}";
   }

   public UUID get() {
      return this.uuid();
   }

   @NotNull
   public String getNameOrId() {
      return Objects.requireNonNullElseGet(this.data().name(), () -> this.uuid().toString());
   }

   public record Keyframes(SoundKeyframeData[] sounds, ParticleKeyframeData[] particles, CustomInstructionKeyframeData[] customInstructions) {
      @Override
      public int hashCode() {
         return Objects.hash(Arrays.hashCode(this.sounds), Arrays.hashCode(this.particles), Arrays.hashCode(this.customInstructions));
      }
   }

   @FunctionalInterface
   public interface LoopType {
      Map<String, Animation.LoopType> LOOP_TYPES = new ConcurrentHashMap<>(4);
      Animation.LoopType DEFAULT = new Animation.LoopType() {
         @Override
         public boolean shouldPlayAgain(@Nullable AnimationController controller, Animation currentAnimation) {
            return currentAnimation.loopType().shouldPlayAgain(controller, currentAnimation);
         }

         @Override
         public float restartFromTick(@Nullable AnimationController controller, Animation currentAnimation) {
            return currentAnimation.loopType().restartFromTick(controller, currentAnimation);
         }
      };
      Animation.LoopType PLAY_ONCE = register("play_once", register("false", (controller, currentAnimation) -> false));
      Animation.LoopType HOLD_ON_LAST_FRAME = register("hold_on_last_frame", (controller, currentAnimation) -> {
         if (controller != null) {
            controller.pause();
         }

         return true;
      });
      Animation.LoopType LOOP = register("loop", register("true", (controller, currentAnimation) -> true));

      boolean shouldPlayAgain(@Nullable AnimationController var1, Animation var2);

      default float restartFromTick(@Nullable AnimationController controller, Animation currentAnimation) {
         return 0.0F;
      }

      static Animation.LoopType returnToTickLoop(float tick) {
         return new Animation.LoopType() {
            @Override
            public boolean shouldPlayAgain(@Nullable AnimationController controller, Animation currentAnimation) {
               return true;
            }

            @Override
            public float restartFromTick(@Nullable AnimationController controller, Animation currentAnimation) {
               return tick;
            }
         };
      }

      static Animation.LoopType fromJson(JsonElement json) {
         if (json != null && json.isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
               return primitive.getAsBoolean() ? LOOP : PLAY_ONCE;
            } else {
               return primitive.isString() ? fromString(primitive.getAsString()) : PLAY_ONCE;
            }
         } else {
            return PLAY_ONCE;
         }
      }

      static Animation.LoopType fromString(String name) {
         return LOOP_TYPES.getOrDefault(name, PLAY_ONCE);
      }

      static Animation.LoopType register(String name, Animation.LoopType loopType) {
         LOOP_TYPES.put(name, loopType);
         return loopType;
      }
   }
}
