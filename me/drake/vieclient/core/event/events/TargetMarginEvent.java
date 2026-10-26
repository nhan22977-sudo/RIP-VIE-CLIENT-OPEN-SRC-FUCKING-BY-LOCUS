package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.CancellableEvent;
import net.minecraft.class_1297;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class TargetMarginEvent extends CancellableEvent {
   public class_1297 entity;
   public CallbackInfoReturnable cir;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public TargetMarginEvent(class_1297 entity, CallbackInfoReturnable cir) {
      this.entity = entityx;
      this.cir = cirx;
   }
}
