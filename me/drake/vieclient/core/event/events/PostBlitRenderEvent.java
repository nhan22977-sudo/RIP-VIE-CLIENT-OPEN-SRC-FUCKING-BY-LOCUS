package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.Event;

public class PostBlitRenderEvent extends Event {
   public float tickDelta;
   private boolean backdropDrawn;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public PostBlitRenderEvent(float tickDelta) {
      this(tickDeltax, false);
   }

   public PostBlitRenderEvent(float tickDelta, boolean backdropDrawn) {
      this.tickDelta = tickDeltax;
      this.backdropDrawn = backdropDrawnx;
   }

   public void update(float tickDelta, boolean backdropDrawn) {
      this.tickDelta = tickDeltax;
      this.backdropDrawn = backdropDrawnx;
   }

   public boolean backdropDrawn() {
      return this.backdropDrawn;
   }
}
