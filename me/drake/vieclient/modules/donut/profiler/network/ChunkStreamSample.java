package me.drake.vieclient.modules.donut.profiler.network;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.modules.donut.chunkprobe.ChunkKey;
import me.drake.vieclient.modules.donut.profiler.ProfilerSample;

public record ChunkStreamSample(
   ChunkStreamSample.Kind kind,
   int chunkX,
   int chunkZ,
   int batchId,
   int batchOrdinal,
   int batchSize,
   int payloadBytes,
   long batchOffsetNanos,
   long applyLatencyNanos,
   long capturedNanos,
   int playerChunkX,
   int playerChunkZ,
   double pingMs,
   double speed,
   double directionX,
   double directionZ,
   long batchAckGeneration,
   boolean batchAckCollecting,
   float batchAckMultiplier
) implements ProfilerSample {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ChunkStreamSample(
      ChunkStreamSample.Kind kind,
      int chunkX,
      int chunkZ,
      int batchId,
      int batchSize,
      int payloadBytes,
      long batchOffsetNanos,
      long applyLatencyNanos,
      long capturedNanos
   ) {
      this(
         kindx,
         chunkXx,
         chunkZx,
         batchIdx,
         legacyBatchOrdinal(kindx, batchSizex),
         legacyBatchSize(kindx, batchSizex),
         payloadBytesx,
         batchOffsetNanosx,
         applyLatencyNanosx,
         capturedNanosx,
         0,
         0,
         50.0,
         0.0,
         0.0,
         0.0,
         0L,
         false,
         1.0F
      );
   }

   public ChunkStreamSample(
      ChunkStreamSample.Kind kind,
      int chunkX,
      int chunkZ,
      int batchId,
      int batchSize,
      int payloadBytes,
      long batchOffsetNanos,
      long applyLatencyNanos,
      long capturedNanos,
      int playerChunkX,
      int playerChunkZ,
      double pingMs,
      double speed,
      long batchAckGeneration,
      boolean batchAckCollecting,
      float batchAckMultiplier
   ) {
      this(
         kindx,
         chunkXx,
         chunkZx,
         batchIdx,
         legacyBatchOrdinal(kindx, batchSizex),
         legacyBatchSize(kindx, batchSizex),
         payloadBytesx,
         batchOffsetNanosx,
         applyLatencyNanosx,
         capturedNanosx,
         playerChunkXx,
         playerChunkZx,
         pingMsx,
         speedx,
         0.0,
         0.0,
         batchAckGenerationx,
         batchAckCollectingx,
         batchAckMultiplierx
      );
   }

   public ChunkStreamSample(
      ChunkStreamSample.Kind kind,
      int chunkX,
      int chunkZ,
      int batchId,
      int batchSize,
      int payloadBytes,
      long batchOffsetNanos,
      long applyLatencyNanos,
      long capturedNanos,
      int playerChunkX,
      int playerChunkZ,
      double pingMs,
      double speed,
      double directionX,
      double directionZ,
      long batchAckGeneration,
      boolean batchAckCollecting,
      float batchAckMultiplier
   ) {
      this(
         kindx,
         chunkXx,
         chunkZx,
         batchIdx,
         legacyBatchOrdinal(kindx, batchSizex),
         legacyBatchSize(kindx, batchSizex),
         payloadBytesx,
         batchOffsetNanosx,
         applyLatencyNanosx,
         capturedNanosx,
         playerChunkXx,
         playerChunkZx,
         pingMsx,
         speedx,
         directionXx,
         directionZx,
         batchAckGenerationx,
         batchAckCollectingx,
         batchAckMultiplierx
      );
   }

   private static int legacyBatchOrdinal(ChunkStreamSample.Kind kind, int value) {
      return kindx != ChunkStreamSample.Kind.CHUNK_PACKET && kindx != ChunkStreamSample.Kind.CHUNK_READY ? 0 : value;
   }

   private static int legacyBatchSize(ChunkStreamSample.Kind kind, int value) {
      return kindx != ChunkStreamSample.Kind.CHUNK_PACKET && kindx != ChunkStreamSample.Kind.CHUNK_READY ? value : 0;
   }

   public long chunkKey() {
      return ChunkKey.pack(this.chunkX, this.chunkZ);
   }

   public enum Kind {
      BATCH_START,
      CHUNK_PACKET,
      CHUNK_READY,
      CHUNK_UNLOAD,
      BATCH_END;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static ChunkStreamSample.Kind[] $values() {
         ChunkStreamSample.Kind[] var10000 = new ChunkStreamSample.Kind[5];
         var10000[0] = BATCH_START;
         var10000[1] = CHUNK_PACKET;
         var10000[2] = CHUNK_READY;
         var10000[3] = CHUNK_UNLOAD;
         var10000[4] = BATCH_END;
         return var10000;
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4]
                  ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                  ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }
}
