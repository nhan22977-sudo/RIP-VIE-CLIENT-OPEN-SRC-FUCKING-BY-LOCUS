package me.drake.vieclient.modules.donut.chunkdebug;

import java.util.ArrayDeque;
import java.util.Deque;

final class PacketIntake {
   static final int MAX_PENDING = 16384;
   private final Deque queue = new ArrayDeque();
   private long droppedCount;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   synchronized boolean offer(PacketIntake.PendingLight pending) {
      if (pending == null) {
         return false;
      } else if (this.queue.size() >= (16384)) {
         this.droppedCount++;
         return false;
      } else {
         this.queue.addLast(pending);
         return true;
      }
   }

   synchronized PacketIntake.PendingLight poll() {
      return (PacketIntake.PendingLight)this.queue.pollFirst();
   }

   synchronized void clear() {
      this.queue.clear();
   }

   synchronized long droppedCount() {
      return this.droppedCount;
   }

   record PendingLight(int chunkX, int chunkZ, LightPayload payload, long arrivalNanos) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
