package me.drake.vieclient.modules.donut.profiler.network;

public final class BatchAckPhaseTracker {
   private long activeGeneration = -1L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public long observe(ChunkStreamSample sample) {
      if (sample != null && orderedStreamPhase(sample.kind())) {
         long var2 = -1L;
         if (this.activeGeneration >= 0L && (!sample.batchAckCollecting() || sample.batchAckGeneration() != this.activeGeneration)) {
            var2 = this.activeGeneration;
            this.activeGeneration = -1L;
         }

         if (sample.kind() == ChunkStreamSample.Kind.CHUNK_PACKET && sample.batchAckCollecting()) {
            this.activeGeneration = sample.batchAckGeneration();
         }

         return var2;
      } else {
         return -1L;
      }
   }

   public void reset() {
      this.activeGeneration = -1L;
   }

   private static boolean orderedStreamPhase(ChunkStreamSample.Kind kind) {
      return kind == ChunkStreamSample.Kind.BATCH_START || kind == ChunkStreamSample.Kind.CHUNK_PACKET || kind == ChunkStreamSample.Kind.BATCH_END;
   }
}
