package me.drake.vieclient.modules.donut.chunkprobe;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2540;
import net.minecraft.class_2672;

public final class ChunkProbeService implements AutoCloseable {
   private static final int QUEUE_CAPACITY = 64;
   private static final ChunkProbeService INSTANCE = new ChunkProbeService();
   private final RawChunkSectionDecoder decoder = new RawChunkSectionDecoder();
   private final Map futures = new WeakHashMap();
   private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
      1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(64), runnable -> {
         byte[] var10003 = new byte[21];
         var10003[0] = (byte)(76);
         var10003[1] = (byte)(-13);
         var10003[2] = (byte)(121);
         var10003[3] = (byte)(-67);
         var10003[4] = (byte)(47);
         var10003[5] = (byte)(26);
         var10003[6] = (byte)(-26);
         var10003[7] = (byte)(117);
         var10003[8] = (byte)(38);
         var10003[9] = (byte)(-113);
         var10003[10] = (byte)(-121);
         var10003[11] = (byte)(-98);
         var10003[12] = (byte)(110);
         var10003[13] = (byte)(-59);
         var10003[14] = (byte)(-64);
         var10003[15] = (byte)(78);
         var10003[16] = (byte)(26);
         var10003[17] = (byte)(56);
         var10003[18] = (byte)(-93);
         var10003[19] = (byte)(-52);
         var10003[20] = (byte)(-106);
         Thread var1 = new Thread(runnable, $$v_s(var10003, 3711157383455821855L));
         var1.setDaemon(true);
         return var1;
      }, (rejected, rejectedExecutor) -> {
         if (rejectedExecutor.isShutdown()) {
            completeEmpty(rejected);
         } else {
            completeEmpty(rejectedExecutor.getQueue().poll());
            if (!rejectedExecutor.getQueue().offer(rejected)) {
               completeEmpty(rejected);
            }
         }
      }
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static ChunkProbeService get() {
      return INSTANCE;
   }

   public CompletableFuture request(class_2672 packet, int bottomSectionY, int sectionCount) {
      synchronized (this.futures) {
         CompletableFuture var5 = (CompletableFuture)this.futures.get(packet);
         if (var5 != null) {
            return var5;
         }
      }

      class_2540 var8 = packet.method_38598().method_38586();
      byte[] var9 = new byte[var8.readableBytes()];
      var8.method_52952(var8.readerIndex(), var9);
      int var6 = class_2248.method_9507(class_2246.field_28888.method_9564());
      return this.requestCaptured(packet, packet.method_11523(), packet.method_11524(), bottomSectionY, sectionCount, var9, System.nanoTime(), var6);
   }

   CompletableFuture requestCaptured(
      Object identity, int chunkX, int chunkZ, int bottomSectionY, int sectionCount, byte[] payload, long receivedNanos, int deepslateRawId
   ) {
      synchronized (this.futures) {
         CompletableFuture var11 = (CompletableFuture)this.futures.get(identity);
         if (var11 != null) {
            return var11;
         }

         byte[] var12 = (byte[])payload.clone();
         CompletableFuture var13 = new CompletableFuture();
         this.futures.put(identity, var13);
         this.executor.execute(new ChunkProbeService.ProbeTask(var13, chunkX, chunkZ, bottomSectionY, sectionCount, var12, receivedNanos, deepslateRawId));
         return var13;
      }
   }

   public void clear() {
      synchronized (this.futures) {
         this.futures.clear();
      }
   }

   @Override
   public void close() {
      List var1 = this.executor.shutdownNow();
      var1.forEach(ChunkProbeService::completeEmpty);
      this.clear();
   }

   private static void completeEmpty(Runnable runnable) {
      if (runnable instanceof ChunkProbeService.ProbeTask var1) {
         var1.completeEmpty();
      }
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }

   private final class ProbeTask implements Runnable {
      private final CompletableFuture future;
      private final int chunkX;
      private final int chunkZ;
      private final int bottomSectionY;
      private final int sectionCount;
      private final byte[] payload;
      private final long receivedNanos;
      private final int deepslateRawId;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private ProbeTask(
         CompletableFuture future, int chunkX, int chunkZ, int bottomSectionY, int sectionCount, byte[] payload, long receivedNanos, int deepslateRawId
      ) {
         this.future = futurex;
         this.chunkX = chunkXx;
         this.chunkZ = chunkZx;
         this.bottomSectionY = bottomSectionYx;
         this.sectionCount = sectionCountx;
         this.payload = payloadx;
         this.receivedNanos = receivedNanosx;
         this.deepslateRawId = deepslateRawIdx;
      }

      @Override
      public void run() {
         this.future
            .complete(
               ChunkProbeService.this.decoder
                  .decode(
                     this.chunkX, this.chunkZ, this.bottomSectionY, this.sectionCount, this.payload, this.receivedNanos, rawId -> rawId == this.deepslateRawId
                  )
            );
      }

      private void completeEmpty() {
         this.future.complete(Optional.empty());
      }
   }
}
