package me.drake.vieclient.modules.donut.profiler.network;

import java.util.ArrayDeque;
import java.util.Optional;

public final class RawFrameBridge {
   private static final RawFrameBridge INSTANCE = new RawFrameBridge(64);
   private final int capacity;
   private final ArrayDeque pending = new ArrayDeque();
   private boolean discardNextDecoded;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public RawFrameBridge(int capacity) {
      this.capacity = Math.max(1, capacityx);
   }

   public static RawFrameBridge get() {
      return INSTANCE;
   }

   public synchronized void onInflated(int compressedBytes, int decodedBytes, long capturedNanos) {
      while (this.pending.size() >= this.capacity) {
         this.pending.removeFirst();
      }

      this.pending.addLast(new RawFrameBridge.PendingFrame(Math.max(0, compressedBytes), Math.max(0, decodedBytes), capturedNanos));
   }

   public synchronized Optional onDecodedChunk(String packetClass, int chunkX, int chunkZ, String profileBucket, long decodedNanos, int decoderBytes) {
      if (this.discardNextDecoded) {
         this.discardNextDecoded = false;
         this.pending.clear();
         return Optional.empty();
      }

      RawFrameBridge.PendingFrame var8 = (RawFrameBridge.PendingFrame)this.pending.pollFirst();
      int var9 = Math.max(0, decoderBytes);
      if (var8 == null) {
         var8 = new RawFrameBridge.PendingFrame(var9, var9, decodedNanos);
      }

      return Optional.of(
         new RawFrameSample(
            chunkX,
            chunkZ,
            profileBucket == null ? "" : profileBucket,
            var8.compressedBytes(),
            var8.decodedBytes(),
            packetClass == null ? "" : packetClass,
            var8.capturedNanos(),
            decodedNanos
         )
      );
   }

   public synchronized void onDecodedNonChunk() {
      if (this.discardNextDecoded) {
         this.discardNextDecoded = false;
         this.pending.clear();
      } else {
         this.pending.pollFirst();
      }
   }

   public synchronized void markAmbiguousBundle() {
      this.pending.clear();
      this.discardNextDecoded = true;
   }

   public synchronized void clear() {
      this.pending.clear();
      this.discardNextDecoded = false;
   }

   private record PendingFrame(int compressedBytes, int decodedBytes, long capturedNanos) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
