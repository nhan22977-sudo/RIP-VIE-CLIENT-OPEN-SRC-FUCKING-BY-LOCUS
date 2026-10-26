package me.drake.vieclient.modules.donut.profiler.network;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import me.drake.vieclient.modules.donut.chunkprobe.ChunkKey;
import me.drake.vieclient.modules.donut.profiler.DonutProfilerService;

public final class NetworkCapture {
   public static final int MAX_PENDING_ARRIVALS = 512;
   private static final int MAX_COMPLETED_BATCH_SIZES = 32;
   private static final NetworkCapture INSTANCE = new NetworkCapture(DonutProfilerService.get()::publish);
   private final Consumer publisher;
   private final Supplier phaseSupplier;
   private final Supplier movementSupplier;
   private long arrivalEvictionCount;
   private final Map arrivals = new LinkedHashMap(512, 1.0F, false);
   private final Map completedBatchSizes = new LinkedHashMap() {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      @Override
      protected boolean removeEldestEntry(Entry eldest) {
         return this.size() > (32);
      }
   };
   private int batchId;
   private int batchPacketCount;
   private long batchStartedNanos;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public NetworkCapture(Consumer publisher) {
      this(publisherx, BatchAckController.get()::snapshot, NetworkMovementContext.get()::snapshot);
   }

   public NetworkCapture(Consumer publisher, Supplier phaseSupplier) {
      this(publisherx, phaseSupplierx, NetworkMovementContext.get()::snapshot);
   }

   public NetworkCapture(Consumer publisher, Supplier phaseSupplier, Supplier movementSupplier) {
      this.publisher = publisherx;
      this.phaseSupplier = phaseSupplierx;
      this.movementSupplier = movementSupplierx;
   }

   public static NetworkCapture get() {
      return INSTANCE;
   }

   public void onBatchStart(long nowNanos) {
      ChunkStreamSample var3;
      synchronized (this) {
         this.batchId++;
         this.batchPacketCount = 0;
         this.batchStartedNanos = nowNanos;
         var3 = this.sample(ChunkStreamSample.Kind.BATCH_START, 0, 0, 0, 0, 0, 0L, 0L, nowNanos);
      }

      this.publisher.accept(var3);
   }

   public void onChunkPacket(int chunkX, int chunkZ, int payloadBytes, long nowNanos) {
      ChunkStreamSample var6;
      synchronized (this) {
         BatchAckController.PhaseSnapshot var8 = (BatchAckController.PhaseSnapshot)this.phaseSupplier.get();
         NetworkMovementContext.Snapshot var9 = (NetworkMovementContext.Snapshot)this.movementSupplier.get();
         long var10 = this.batchStartedNanos == 0L ? 0L : Math.max(0L, nowNanos - this.batchStartedNanos);
         this.batchPacketCount++;
         long var12 = ChunkKey.pack(chunkX, chunkZ);
         this.evictOldestArrivalIfNew(var12);
         this.arrivals.put(var12, new NetworkCapture.PacketArrival(this.batchId, nowNanos, var10, payloadBytes, this.batchPacketCount, var8, var9));
         var6 = this.sample(ChunkStreamSample.Kind.CHUNK_PACKET, chunkX, chunkZ, this.batchPacketCount, 0, payloadBytes, var10, 0L, nowNanos, var8, var9);
      }

      this.publisher.accept(var6);
   }

   public void onChunkReady(int chunkX, int chunkZ, long nowNanos) {
      this.captureChunkReady(chunkX, chunkZ, nowNanos);
   }

   private void captureChunkReady(int chunkX, int chunkZ, long nowNanos) {
      ChunkStreamSample var5;
      synchronized (this) {
         NetworkCapture.PacketArrival var7 = (NetworkCapture.PacketArrival)this.arrivals.remove(ChunkKey.pack(chunkX, chunkZ));
         int var8 = var7 == null ? this.batchId : var7.batchId();
         long var9 = var7 == null ? 0L : Math.max(0L, nowNanos - var7.arrivedNanos());
         BatchAckController.PhaseSnapshot var11 = (BatchAckController.PhaseSnapshot)this.phaseSupplier.get();
         NetworkMovementContext.Snapshot var12 = (NetworkMovementContext.Snapshot)this.movementSupplier.get();
         BatchAckController.PhaseSnapshot var13 = var7 == null ? var11 : var7.phase();
         NetworkMovementContext.Snapshot var14;
         if (var7 != null) {
            var14 = var7.movement();
         } else {
            var14 = var12;
         }

         int var15 = var7 == null ? 0 : var7.batchOrdinal();
         int var16 = var7 == null ? 0 : this.completedBatchSizes.getOrDefault(var8, 0);
         var5 = new ChunkStreamSample(
            ChunkStreamSample.Kind.CHUNK_READY,
            chunkX,
            chunkZ,
            var8,
            var15,
            var16,
            var7 == null ? 0 : var7.payloadBytes(),
            var7 == null ? 0L : var7.batchOffsetNanos(),
            var9,
            nowNanos,
            var14.playerChunkX(),
            var14.playerChunkZ(),
            var14.pingMs(),
            var14.speed(),
            var14.directionX(),
            var14.directionZ(),
            var13.generation(),
            var13.collecting(),
            var13.multiplier()
         );
      }

      this.publisher.accept(var5);
   }

   public void onChunkReady(int chunkX, int chunkZ, long nowNanos, int playerChunkX, int playerChunkZ, double pingMs, double speed) {
      this.captureChunkReady(chunkX, chunkZ, nowNanos);
   }

   public void onChunkUnload(int chunkX, int chunkZ, long nowNanos) {
      ChunkStreamSample var5;
      synchronized (this) {
         this.arrivals.remove(ChunkKey.pack(chunkX, chunkZ));
         var5 = this.sample(ChunkStreamSample.Kind.CHUNK_UNLOAD, chunkX, chunkZ, 0, 0, 0, 0L, 0L, nowNanos);
      }

      this.publisher.accept(var5);
   }

   public void onBatchEnd(int batchSize, long nowNanos) {
      ChunkStreamSample var4;
      synchronized (this) {
         int var6 = Math.max(0, batchSize);
         this.completedBatchSizes.put(this.batchId, var6);
         var4 = this.sample(ChunkStreamSample.Kind.BATCH_END, 0, 0, 0, var6, 0, 0L, 0L, nowNanos);
      }

      this.publisher.accept(var4);
   }

   public synchronized void clear() {
      this.arrivals.clear();
      this.completedBatchSizes.clear();
      this.batchId = 0;
      this.batchPacketCount = 0;
      this.batchStartedNanos = 0L;
   }

   public synchronized long arrivalEvictionCount() {
      return this.arrivalEvictionCount;
   }

   private void evictOldestArrivalIfNew(long chunkKey) {
      if (this.arrivals.size() >= (512) && !this.arrivals.containsKey(chunkKey)) {
         Iterator var3 = this.arrivals.keySet().iterator();
         var3.next();
         var3.remove();
         if (this.arrivalEvictionCount != Long.MAX_VALUE) {
            this.arrivalEvictionCount++;
         }
      }
   }

   private ChunkStreamSample sample(
      ChunkStreamSample.Kind kind,
      int chunkX,
      int chunkZ,
      int batchOrdinal,
      int batchSize,
      int payloadBytes,
      long batchOffsetNanos,
      long applyLatencyNanos,
      long capturedNanos
   ) {
      return this.sample(
         kind,
         chunkX,
         chunkZ,
         batchOrdinal,
         batchSize,
         payloadBytes,
         batchOffsetNanos,
         applyLatencyNanos,
         capturedNanos,
         (BatchAckController.PhaseSnapshot)this.phaseSupplier.get(),
         (NetworkMovementContext.Snapshot)this.movementSupplier.get()
      );
   }

   private ChunkStreamSample sample(
      ChunkStreamSample.Kind kind,
      int chunkX,
      int chunkZ,
      int batchOrdinal,
      int batchSize,
      int payloadBytes,
      long batchOffsetNanos,
      long applyLatencyNanos,
      long capturedNanos,
      BatchAckController.PhaseSnapshot phase,
      NetworkMovementContext.Snapshot movement
   ) {
      return new ChunkStreamSample(
         kind,
         chunkX,
         chunkZ,
         this.batchId,
         batchOrdinal,
         batchSize,
         payloadBytes,
         batchOffsetNanos,
         applyLatencyNanos,
         capturedNanos,
         movement.playerChunkX(),
         movement.playerChunkZ(),
         movement.pingMs(),
         movement.speed(),
         movement.directionX(),
         movement.directionZ(),
         phase.generation(),
         phase.collecting(),
         phase.multiplier()
      );
   }

   static {
      DonutProfilerService.get().registerSessionReset(() -> {
         INSTANCE.clear();
         RawFrameBridge.get().clear();
         RegionLatencyTracker.get().clear();
         BatchAckController.get().forceResetForSession();
      });
   }

   private record PacketArrival(
      int batchId,
      long arrivedNanos,
      long batchOffsetNanos,
      int payloadBytes,
      int batchOrdinal,
      BatchAckController.PhaseSnapshot phase,
      NetworkMovementContext.Snapshot movement
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
