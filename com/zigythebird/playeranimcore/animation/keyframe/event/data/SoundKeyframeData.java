package com.zigythebird.playeranimcore.animation.keyframe.event.data;

import java.util.Objects;

public class SoundKeyframeData extends KeyFrameData {
   private final String sound;

   public SoundKeyframeData(Float startTick, String sound) {
      super(startTick);
      this.sound = sound;
   }

   public String getSound() {
      return this.sound;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.getStartTick(), this.sound);
   }
}
