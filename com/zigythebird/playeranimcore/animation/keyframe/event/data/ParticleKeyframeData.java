package com.zigythebird.playeranimcore.animation.keyframe.event.data;

import java.util.Objects;

public class ParticleKeyframeData extends KeyFrameData {
   private final String effect;
   private final String locator;
   private final String script;

   public ParticleKeyframeData(float startTick, String effect, String locator, String script) {
      super(startTick);
      this.script = script;
      this.locator = locator;
      this.effect = effect;
   }

   public String getEffect() {
      return this.effect;
   }

   public String getLocator() {
      return this.locator;
   }

   public String script() {
      return this.script;
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.getStartTick(), this.effect, this.locator, this.script);
   }
}
