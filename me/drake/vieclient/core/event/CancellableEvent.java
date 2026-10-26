package me.drake.vieclient.core.event;

public abstract class CancellableEvent extends Event {
   private boolean cancelled = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void cancel() {
      this.cancelled = true;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelledx;
   }
}
