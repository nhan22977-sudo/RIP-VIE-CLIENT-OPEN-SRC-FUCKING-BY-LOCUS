package me.drake.vieclient.modules.donut;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import me.drake.vieclient.chunkfinder.SeedSimulationEngine;
import net.minecraft.class_2248;

public final class TunnelTerrainOracle {
   private static final int SIZE = 16;
   private static final int Y_BUCKET = 4;
   private static final int LEVELS = 4;
   private static final int MAX_CACHED_MASKS = 4096;
   private static final int MAX_QUEUED = 256;
   private final SeedSimulationEngine engine;
   private final long seed;
   private final Object lock = new Object();
   private final Long2ObjectOpenHashMap masks = new Long2ObjectOpenHashMap();
   private final LongArrayFIFOQueue pending = new LongArrayFIFOQueue();
   private final LongOpenHashSet queued = new LongOpenHashSet();
   private final AtomicBoolean running = new AtomicBoolean(true);
   private final Thread worker;
   private volatile int hits;
   private volatile int misses;
   private volatile int computed;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private TunnelTerrainOracle(long seed) {
      this.seed = seedx;
      this.engine = new SeedSimulationEngine(seedx);
      Runnable var10003 = this::workerLoop;
      byte[] var10004 = new byte[17];
      var10004[0] = (byte)(76);
      var10004[1] = (byte)(-13);
      var10004[2] = (byte)(121);
      var10004[3] = (byte)(-13);
      var10004[4] = (byte)(122);
      var10004[5] = (byte)(-110);
      var10004[6] = (byte)(83);
      var10004[7] = (byte)(88);
      var10004[8] = (byte)(55);
      var10004[9] = (byte)(-50);
      var10004[10] = (byte)(-55);
      var10004[11] = (byte)(-103);
      var10004[12] = (byte)(36);
      var10004[13] = (byte)(94);
      var10004[14] = (byte)(118);
      var10004[15] = (byte)(34);
      var10004[16] = (byte)(15);
      this.worker = new Thread(var10003, $$v_s(var10004, 2179242729084801055L));
      this.worker.setDaemon(true);
      this.worker.setPriority(1);
      this.worker.start();
   }

   public static TunnelTerrainOracle from(String raw) {
      if (raw == null) {
         return null;
      }

      String var1 = raw.trim();
      if (var1.isEmpty()) {
         return null;
      }

      long var2;
      try {
         var2 = Long.parseLong(var1);
      } catch (NumberFormatException var5) {
         var2 = var1.hashCode();
      }

      return new TunnelTerrainOracle(var2);
   }

   public long seed() {
      return this.seed;
   }

   public int cacheHits() {
      return this.hits;
   }

   public int cacheMisses() {
      return this.misses;
   }

   public int chunksComputed() {
      return this.computed;
   }

   public void close() {
      this.running.set(false);
      this.worker.interrupt();
   }

   public TunnelHazardModel.Verdict predict(int x, int y, int z) {
      int var4 = x >> (4);
      int var5 = z >> (4);
      int var6 = Math.floorDiv(y, 4);
      long var7 = key(var4, var5, var6);
      TunnelTerrainOracle.GravelMask var9;
      synchronized (this.lock) {
         var9 = (TunnelTerrainOracle.GravelMask)this.masks.get(var7);
         if (var9 == null) {
            this.misses++;
            if (this.queued.size() < (256) && this.queued.add(var7)) {
               this.pending.enqueue(var7);
            }

            return TunnelHazardModel.Verdict.UNKNOWN;
         }
      }

      this.hits++;
      int var13 = Math.floorMod(x, 16);
      int var11 = Math.floorMod(z, 16);
      return var9.get(var13, var11) ? TunnelHazardModel.Verdict.FALLING : TunnelHazardModel.Verdict.UNKNOWN;
   }

   private void workerLoop() {
      while (this.running.get()) {
         long var1;
         synchronized (this.lock) {
            if (this.pending.isEmpty()) {
               try {
                  this.lock.wait(200L);
                  continue;
               } catch (InterruptedException var9) {
                  Thread.currentThread().interrupt();
                  return;
               }
            }

            var1 = this.pending.dequeueLong();
         }

         try {
            TunnelTerrainOracle.GravelMask var13 = this.computeMask(var1);
            synchronized (this.lock) {
               if (this.masks.size() >= (4096)) {
                  this.masks.clear();
               }

               this.masks.put(var1, var13);
               this.queued.remove(var1);
            }

            this.computed++;
         } catch (RuntimeException var11) {
            synchronized (this.lock) {
               this.queued.remove(var1);
            }
         }
      }
   }

   private TunnelTerrainOracle.GravelMask computeMask(long key) {
      int var3 = unpackX(key);
      int var4 = unpackZ(key);
      int var5 = unpackYBucket(key) * (4);
      long[] var6 = new long[4];

      for (int var7 = 0; var7 < (16); var7++) {
         for (int var8 = 0; var8 < (16); var8++) {
            int var9 = (var3 << (4)) + var7;
            int var10 = (var4 << (4)) + var8;
            boolean var11 = false;

            for (int var12 = 0; var12 < (4) && !var11; var12++) {
               class_2248 var13 = this.engine.expectedBaseStoneAt(var9, var5 + var12, var10);
               var11 = SeedSimulationEngine.blockToCode(var13) == (8);
            }

            if (var11) {
               int var14 = var7 * ('蛇' ^ '蛗') + var8;
               var6[var14 >> (6)] = var6[var14 >> (6)] | 1L << (var14 & (63));
            }
         }
      }

      return new TunnelTerrainOracle.GravelMask(var6[0], var6[1], var6[2], var6[3]);
   }

   public void flush() {
      synchronized (this.lock) {
         this.lock.notifyAll();
      }
   }

   private static long key(int cx, int cz, int yBucket) {
      return (long)(cx & (4194303)) << (42) | (long)(cz & (4194303)) << (20) | yBucket & 1048575L;
   }

   private static int unpackX(long key) {
      int var2 = (int)(key >>> (42) & 4194303L);
      return var2 << (10) >> (10);
   }

   private static int unpackZ(long key) {
      int var2 = (int)(key >>> (20) & 4194303L);
      return var2 << (10) >> (10);
   }

   private static int unpackYBucket(long key) {
      int var2 = (int)(key & 1048575L);
      return var2 << (12) >> (12);
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

   private record GravelMask(long b0, long b1, long b2, long b3) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      boolean get(int lx, int lz) {
         int var3 = lx * (16) + lz;

         long var4 = switch (var3 >> (6)) {
            case 0 -> this.b0;
            case 1 -> this.b1;
            case 2 -> this.b2;
            default -> this.b3;
         };
         return (var4 & 1L << (var3 & (63))) != 0L;
      }
   }
}
