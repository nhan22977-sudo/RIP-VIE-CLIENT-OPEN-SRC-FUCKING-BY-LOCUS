package me.drake.vieclient.render.nvg;

public final class NanoVGFrame implements AutoCloseable {
   private final NanoVGFrameLifecycle owner;
   private final long generation;
   private final boolean active;
   private final boolean isolated;
   private boolean closed;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   NanoVGFrame(NanoVGFrameLifecycle owner, long generation, boolean active, boolean isolated) {
      this.owner = ownerx;
      this.generation = generationx;
      this.active = activex;
      this.isolated = isolatedx;
   }

   public boolean isActive() {
      return this.active;
   }

   @Override
   public void close() {
      if (!this.closed) {
         this.closed = true;
         if (this.active) {
            this.owner.release(this.generation, this.isolated);
         }
      }
   }
}
