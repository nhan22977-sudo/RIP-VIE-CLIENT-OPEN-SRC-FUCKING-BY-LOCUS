package me.drake.vieclient.modules.donut;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.class_2338;

public final class TunnelVisitMap {
   private static final int CELL_SHIFT = 1;
   private static final int MAX_VISITS = 15;
   private static final int PRUNE_RADIUS = 384;
   private static final int PRUNE_INTERVAL_TICKS = 200;
   private final Long2ByteOpenHashMap visits = new Long2ByteOpenHashMap();
   private long startPackedCell;
   private double centroidX;
   private double centroidZ;
   private long samples;
   private int pruneTimer;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public TunnelVisitMap() {
      this.visits.defaultReturnValue((byte)0);
   }

   public void reset(class_2338 start) {
      this.visits.clear();
      this.startPackedCell = pack(start.method_10263(), start.method_10260());
      this.centroidX = cellX(start.method_10263());
      this.centroidZ = cellZ(start.method_10260());
      this.samples = 0L;
      this.pruneTimer = 0;
   }

   public void mark(int x, int z) {
      long var3 = pack(x, z);
      int var5 = Math.min(15, this.visits.get(var3) + 1);
      this.visits.put(var3, (byte)var5);
      double var6 = cellX(x);
      double var8 = cellZ(z);
      this.samples++;
      this.centroidX = this.centroidX + (var6 - this.centroidX) / this.samples;
      this.centroidZ = this.centroidZ + (var8 - this.centroidZ) / this.samples;
   }

   public int visits(int x, int z) {
      return this.visits.get(pack(x, z));
   }

   public int pathVisitCost(int x, int z, int dx, int dz, int distance) {
      int var6 = 0;

      for (int var7 = 1; var7 <= distance; var7++) {
         var6 += this.visits(x + dx * var7, z + dz * var7);
      }

      return var6;
   }

   public int noveltyAlong(int x, int z, int dx, int dz, int distance) {
      int var6 = 0;

      for (int var7 = 1; var7 <= distance; var7++) {
         if (this.visits(x + dx * var7, z + dz * var7) == 0) {
            var6++;
         }
      }

      return var6;
   }

   public double outwardBias(int x, int z, int dx, int dz) {
      double var5 = Math.sqrt((double)dx * dx + (double)dz * dz);
      if (var5 == 0.0) {
         return 0.0;
      }

      double var7 = cellX(x) - this.centroidX;
      double var9 = cellZ(z) - this.centroidZ;
      double var11 = Math.sqrt(var7 * var7 + var9 * var9);
      return var11 == 0.0 ? 0.0 : (dx * var7 + dz * var9) / (var5 * var11);
   }

   public void tickPrune(int px, int pz) {
      if (++this.pruneTimer >= (200)) {
         this.pruneTimer = 0;
         int var3 = px >> 1;
         int var4 = pz >> 1;
         int var5 = 192;
         long var6 = (long)var5 * var5;
         ObjectIterator var8 = this.visits.long2ByteEntrySet().iterator();

         while (var8.hasNext()) {
            long var9 = ((Entry)var8.next()).getLongKey();
            int var11 = (int)(var9 >> (32));
            int var12 = (int)var9;
            long var13 = (long)var11 - var3;
            long var15 = (long)var12 - var4;
            if (var13 * var13 + var15 * var15 > var6) {
               var8.remove();
            }
         }
      }
   }

   public int size() {
      return this.visits.size();
   }

   private static long pack(int x, int z) {
      return (long)(x >> 1) << (32) ^ z >> 1 & 4294967295L;
   }

   private static int cellX(int x) {
      return x >> 1;
   }

   private static int cellZ(int z) {
      return z >> 1;
   }
}
