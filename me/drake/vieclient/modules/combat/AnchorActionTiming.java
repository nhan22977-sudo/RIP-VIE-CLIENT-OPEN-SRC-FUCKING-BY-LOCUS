package me.drake.vieclient.modules.combat;

final class AnchorActionTiming {
   private boolean started;
   private int armedAt;
   private int slotReadyAt;
   private int lastInteraction = -2147483648;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   void reset() {
      this.started = false;
      this.armedAt = this.slotReadyAt = 0;
      this.lastInteraction = -2147483648;
   }

   void beginTick(int tick) {
      if (!this.started) {
         this.started = true;
         this.armedAt = tick + 1;
      }
   }

   boolean canChangeSlot(int tick) {
      return tick != this.lastInteraction;
   }

   void selected(int tick, int delay) {
      this.slotReadyAt = tick + Math.max(1, delay);
   }

   boolean slotReady(int tick) {
      return tick >= this.slotReadyAt;
   }

   boolean canInteract(int tick) {
      return this.started && tick >= this.armedAt && this.slotReady(tick) && tick != this.lastInteraction;
   }

   void interacted(int tick) {
      this.lastInteraction = tick;
   }
}
