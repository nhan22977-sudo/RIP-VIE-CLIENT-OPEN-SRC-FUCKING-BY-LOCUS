package com.zigythebird.playeranimcore.animation.layered;

import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonConfiguration;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class AnimationStack implements IAnimation {
   protected final List<Pair<Integer, IAnimation>> layers = new ArrayList<>();

   public List<Pair<Integer, IAnimation>> getLayers() {
      return this.layers;
   }

   @Override
   public boolean isActive() {
      for (Pair<Integer, IAnimation> layer : this.layers) {
         if (((IAnimation)layer.right()).isActive()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void tick(AnimationData state) {
      for (Pair<Integer, IAnimation> layer : this.layers) {
         ((IAnimation)layer.right()).tick(state);
      }
   }

   @Override
   public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
      for (Pair<Integer, IAnimation> layer : this.layers) {
         if (((IAnimation)layer.right()).isActive()) {
            bone = ((IAnimation)layer.right()).get3DTransform(bone);
         }
      }

      return bone;
   }

   @Override
   public void setupAnim(AnimationData state) {
      for (Pair<Integer, IAnimation> layer : this.layers) {
         if (((IAnimation)layer.right()).isActive()) {
            ((IAnimation)layer.right()).setupAnim(state);
         }
      }
   }

   public void addAnimLayer(int priority, IAnimation layer) {
      int search = 0;

      while (this.layers.size() > search && this.layers.get(search).left() < priority) {
         search++;
      }

      this.layers.add(search, Pair.of(priority, layer));
   }

   public boolean removeLayer(IAnimation layer) {
      return this.layers.removeIf(integerIAnimationPair -> integerIAnimationPair.right() == layer);
   }

   public boolean removeLayer(int layerLevel) {
      return this.layers.removeIf(integerIAnimationPair -> (Integer)integerIAnimationPair.left() == layerLevel);
   }

   @NotNull
   @Override
   public FirstPersonMode getFirstPersonMode() {
      int i = this.layers.size();

      while (i > 0) {
         Pair<Integer, IAnimation> layer = this.layers.get(--i);
         if (((IAnimation)layer.right()).isActive()) {
            FirstPersonMode mode = ((IAnimation)layer.right()).getFirstPersonMode();
            if (mode != FirstPersonMode.NONE) {
               return mode;
            }
         }
      }

      return FirstPersonMode.NONE;
   }

   @NotNull
   @Override
   public FirstPersonConfiguration getFirstPersonConfiguration() {
      int i = this.layers.size();

      while (i > 0) {
         Pair<Integer, IAnimation> layer = this.layers.get(--i);
         if (((IAnimation)layer.right()).isActive()) {
            FirstPersonMode mode = ((IAnimation)layer.right()).getFirstPersonMode();
            if (mode != FirstPersonMode.NONE) {
               return ((IAnimation)layer.right()).getFirstPersonConfiguration();
            }
         }
      }

      return IAnimation.super.getFirstPersonConfiguration();
   }

   @Override
   public int getFirstPersonTransitionLength() {
      for (int i = this.layers.size() - 1; i >= 0; i--) {
         IAnimation layer = (IAnimation)this.layers.get(i).right();
         if (layer.isActive() && layer.getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL) {
            int transitionLength = layer.getFirstPersonTransitionLength();
            if (transitionLength > 0) {
               return transitionLength;
            }
         }
      }

      return 0;
   }

   @Override
   public boolean isFirstPersonFollowsCamera() {
      for (int i = this.layers.size() - 1; i >= 0; i--) {
         IAnimation layer = (IAnimation)this.layers.get(i).right();
         if (layer.isActive()) {
            return layer.isFirstPersonFollowsCamera();
         }
      }

      return IAnimation.super.isFirstPersonFollowsCamera();
   }

   public int getPriority() {
      int priority = 0;

      for (int i = this.layers.size() - 1; i >= 0; i--) {
         Pair<Integer, IAnimation> layer = this.layers.get(i);
         if (((IAnimation)layer.right()).isActive()) {
            priority = (Integer)layer.left();
            break;
         }
      }

      return priority;
   }

   @Override
   public String toString() {
      return "AnimationStack{layers=" + this.layers + "}";
   }
}
