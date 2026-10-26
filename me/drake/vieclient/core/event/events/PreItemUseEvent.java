package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.CancellableEvent;

public class PreItemUseEvent extends CancellableEvent {
   public int cooldown;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public PreItemUseEvent(int cooldown) {
      this.cooldown = cooldownx;
   }
}
