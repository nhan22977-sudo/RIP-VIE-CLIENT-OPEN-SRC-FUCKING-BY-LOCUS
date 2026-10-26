package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.CancellableEvent;
import net.minecraft.class_1297;

public class AttackEvent extends CancellableEvent {
   private final class_1297 target;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AttackEvent() {
      this(null);
   }

   public AttackEvent(class_1297 target) {
      this.target = targetx;
   }

   public class_1297 getTarget() {
      return this.target;
   }
}
