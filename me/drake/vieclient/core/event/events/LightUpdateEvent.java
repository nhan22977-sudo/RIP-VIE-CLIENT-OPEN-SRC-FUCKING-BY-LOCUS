package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.Event;
import net.minecraft.class_2676;

public final class LightUpdateEvent extends Event {
   private final class_2676 packet;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public LightUpdateEvent(class_2676 packet) {
      this.packet = packetx;
   }

   public class_2676 getPacket() {
      return this.packet;
   }
}
