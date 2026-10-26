package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.CancellableEvent;
import net.minecraft.class_2672;

public class ChunkDataEvent extends CancellableEvent {
   private final class_2672 packet;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ChunkDataEvent(class_2672 packet) {
      this.packet = packetx;
   }

   public class_2672 getPacket() {
      return this.packet;
   }
}
