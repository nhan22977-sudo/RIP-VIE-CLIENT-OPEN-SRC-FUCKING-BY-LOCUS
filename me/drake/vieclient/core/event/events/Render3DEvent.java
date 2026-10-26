package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.Event;
import net.minecraft.class_4587;

public class Render3DEvent extends Event {
   private final float tickDelta;
   private final class_4587 matrices;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Render3DEvent() {
      this.tickDelta = 1.0F;
      this.matrices = new class_4587();
   }

   public Render3DEvent(float tickDelta) {
      this.tickDelta = tickDeltax;
      this.matrices = new class_4587();
   }

   public Render3DEvent(float tickDelta, class_4587 matrices) {
      this.tickDelta = tickDeltax;
      this.matrices = matricesx;
   }

   public float getTickDelta() {
      return this.tickDelta;
   }

   public class_4587 getMatrices() {
      return this.matrices;
   }
}
