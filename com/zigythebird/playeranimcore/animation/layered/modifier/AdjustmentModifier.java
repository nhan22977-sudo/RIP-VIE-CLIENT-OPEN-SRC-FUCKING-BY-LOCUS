package com.zigythebird.playeranimcore.animation.layered.modifier;

import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import com.zigythebird.playeranimcore.math.Vec3f;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public class AdjustmentModifier extends AbstractModifier {
   public boolean fadeIn = true;
   public boolean fadeOut = true;
   public boolean enabled = true;
   private AnimationData data;
   protected BiFunction<String, AnimationData, Optional<AdjustmentModifier.PartModifier>> source;
   protected int instructedFadeout = 0;
   private int remainingFadeout = 0;

   public AdjustmentModifier(Function<String, Optional<AdjustmentModifier.PartModifier>> source) {
      this((name, data) -> source.apply(name));
   }

   public AdjustmentModifier(BiFunction<String, AnimationData, Optional<AdjustmentModifier.PartModifier>> source) {
      this.source = source;
   }

   @Override
   public void tick(AnimationData state) {
      super.tick(state);
      this.data = state;
      if (this.remainingFadeout > 0) {
         this.remainingFadeout--;
         if (this.remainingFadeout <= 0) {
            this.instructedFadeout = 0;
         }
      }
   }

   @Override
   public void setupAnim(AnimationData state) {
      super.setupAnim(state);
      this.data = state;
   }

   public void fadeOut(int fadeOut) {
      this.instructedFadeout = fadeOut;
      this.remainingFadeout = fadeOut + 1;
   }

   protected float getFadeOut(float delta) {
      float fadeOut = 1.0F;
      if (this.remainingFadeout > 0 && this.instructedFadeout > 0) {
         float current = Math.max(this.remainingFadeout - delta, 0.0F);
         fadeOut = current / this.instructedFadeout;
         return Math.min(fadeOut, 1.0F);
      }

      if (this.fadeOut && this.getController() instanceof AnimationController controller && controller.getCurrentAnimation() != null) {
         float stopTick = controller.getCurrentAnimation().animation().length();
         float endTick = controller.getCurrentAnimation().animation().data().<Float>get("endTick").orElse(stopTick);
         float position = -1.0F * (controller.getAnimationTicks() - stopTick);
         float length = stopTick - endTick;
         if (length > 0.0F) {
            fadeOut = position / length;
            fadeOut = Math.min(fadeOut, 1.0F);
         }
      }

      return fadeOut;
   }

   protected float getFadeIn() {
      float fadeIn = 1.0F;
      if (this.fadeIn && this.getController() instanceof AnimationController controller && controller.getCurrentAnimation() != null) {
         float beginTick = controller.getCurrentAnimation().animation().data().<Float>get("beginTick").orElse(0.0F);
         fadeIn = beginTick > 0.0F ? controller.getAnimationTicks() / beginTick : 1.0F;
         fadeIn = Math.min(fadeIn, 1.0F);
      }

      return fadeIn;
   }

   @Override
   public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
      if (!this.enabled) {
         return super.get3DTransform(bone);
      } else {
         Optional<AdjustmentModifier.PartModifier> partModifier = this.source.apply(bone.getName(), this.data);
         float fade = this.getFadeIn() * this.getFadeOut(this.data.getPartialTick());
         if (partModifier.isPresent()) {
            super.get3DTransform(bone);
            this.transformBone(bone, partModifier.get(), fade);
            return bone;
         } else {
            return super.get3DTransform(bone);
         }
      }
   }

   protected void transformBone(PlayerAnimBone bone, AdjustmentModifier.PartModifier partModifier, float fade) {
      Vec3f pos = partModifier.offset().mul(fade);
      Vec3f rot = partModifier.rotation().mul(fade);
      Vec3f scale = partModifier.scale().mul(fade);
      bone.updatePosition(pos.x() + bone.getPosX(), pos.y() + bone.getPosY(), pos.z() + bone.getPosZ());
      bone.updateRotation(rot.x() + bone.getRotX(), rot.y() + bone.getRotY(), rot.z() + bone.getRotZ());
      bone.updateScale(scale.x() + bone.getScaleX(), scale.y() + bone.getScaleY(), scale.z() + bone.getScaleZ());
   }

   @Override
   public String toString() {
      return "AdjustmentModifier{anim=" + this.anim + ", enabled=" + this.enabled + "}";
   }

   public static final class PartModifier {
      private final Vec3f rotation;
      private final Vec3f scale;
      private final Vec3f offset;

      public PartModifier(Vec3f rotation, Vec3f offset) {
         this(rotation, Vec3f.ZERO, offset);
      }

      public PartModifier(Vec3f rotation, Vec3f scale, Vec3f offset) {
         this.rotation = rotation;
         this.scale = scale;
         this.offset = offset;
      }

      public Vec3f rotation() {
         return this.rotation;
      }

      public Vec3f scale() {
         return this.scale;
      }

      public Vec3f offset() {
         return this.offset;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (obj != null && obj.getClass() == this.getClass()) {
            AdjustmentModifier.PartModifier that = (AdjustmentModifier.PartModifier)obj;
            return Objects.equals(this.rotation, that.rotation) && Objects.equals(this.scale, that.scale) && Objects.equals(this.offset, that.offset);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.rotation, this.scale, this.offset);
      }

      @Override
      public String toString() {
         return "PartModifier[rotation=" + this.rotation + ", scale=" + this.scale + ", offset=" + this.offset + "]";
      }
   }
}
