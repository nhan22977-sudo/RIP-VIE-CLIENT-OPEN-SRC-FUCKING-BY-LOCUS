package com.zigythebird.playeranimcore.animation.layered.modifier;

import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.layered.AnimationContainer;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractModifier extends AnimationContainer<IAnimation> {
   protected IAnimation host;

   public void setHost(IAnimation host) {
      this.host = host;
   }

   @Nullable
   protected AnimationController getController() {
      return this.host instanceof AnimationController controller ? controller : null;
   }

   @Override
   public boolean canRemove() {
      return false;
   }
}
