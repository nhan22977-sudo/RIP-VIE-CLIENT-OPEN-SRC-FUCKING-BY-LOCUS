package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.CancellableEvent;
import net.minecraft.class_2596;

public class PacketReceiveEvent extends CancellableEvent {
   private final class_2596 packet;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public PacketReceiveEvent(class_2596 packet) {
      this.packet = packetx;
   }

   public class_2596 getPacket() {
      return this.packet;
   }
}
