package com.zigythebird.playeranimcore.animation.keyframe.event.data;

import java.util.Objects;

public abstract class KeyFrameData {
   private final float startTick;

   public KeyFrameData(float startTick) {
      this.startTick = startTick;
   }

   public float getStartTick() {
      return this.startTick;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else {
         return obj != null && this.getClass() == obj.getClass() ? this.hashCode() == obj.hashCode() : false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(this.startTick);
   }
}
