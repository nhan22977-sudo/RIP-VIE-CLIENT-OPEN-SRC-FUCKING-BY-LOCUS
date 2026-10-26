package me.drake.vieclient.modules.donut;

import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import java.nio.charset.StandardCharsets;
import net.minecraft.class_1657;
import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3486;
import net.minecraft.class_3610;
import net.minecraft.class_638;
import net.minecraft.class_2338.class_2339;

public final class TunnelHazardModel {
   public static final int CORRIDOR_HALF_WIDTH = 1;
   public static final int CORRIDOR_TOP = 2;
   public static final int LETHAL_COLUMN_HEIGHT = 4;
   public static final int FALLING_COLUMN_HEIGHT = 2;
   public static final int VOID_CHECK_DEPTH = 4;
   public static final int VOID_Y_THRESHOLD = 59;
   public static final int WATER_DROWN_DEPTH = 5;
   public static final int MAX_STEP_DOWN = 3;
   private static final byte MEMO_EMPTY = -1;
   private final Long2ByteOpenHashMap columnMemo = new Long2ByteOpenHashMap();
   private final Long2IntOpenHashMap floorMemo = new Long2IntOpenHashMap();
   private static final int FLOOR_MEMO_EMPTY = Integer.MIN_VALUE;
   private final TunnelHazardModel.Verdict[] verdicts = TunnelHazardModel.Verdict.values();
   private final class_2339 scratch = new class_2339();
   private class_638 world;
   private TunnelTerrainOracle oracle;
   private long memoHits;
   private long memoMisses;
   public static final int BREAK_COST_INF = 100000;
   private static final int BREAK_ADDITIONAL = 2;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public TunnelHazardModel() {
      this.columnMemo.defaultReturnValue((byte)(-1));
      this.floorMemo.defaultReturnValue(-2147483648);
   }

   public void beginTick(class_638 world, TunnelTerrainOracle oracle) {
      this.world = worldx;
      this.oracle = oraclex;
      this.columnMemo.clear();
      this.floorMemo.clear();
      this.memoHits = 0L;
      this.memoMisses = 0L;
   }

   public long memoHits() {
      return this.memoHits;
   }

   public long memoMisses() {
      return this.memoMisses;
   }

   public TunnelHazardModel.Verdict classify(class_2338 foot) {
      return this.classify(foot.method_10263(), foot.method_10264(), foot.method_10260());
   }

   public TunnelHazardModel.Verdict classify(int x, int y, int z) {
      if (this.world == null) {
         return TunnelHazardModel.Verdict.UNKNOWN;
      } else {
         long var4 = class_2338.method_10064(x, y, z);
         byte var6 = this.columnMemo.get(var4);
         if (var6 != (-1)) {
            this.memoHits++;
            return this.verdicts[var6];
         } else {
            this.memoMisses++;
            TunnelHazardModel.Verdict var7 = this.computeColumn(x, y, z);
            this.columnMemo.put(var4, (byte)var7.ordinal());
            return var7;
         }
      }
   }

   private TunnelHazardModel.Verdict computeColumn(int x, int y, int z) {
      if (!this.isChunkLoaded(x, z)) {
         TunnelHazardModel.Verdict var6 = this.oracle == null ? TunnelHazardModel.Verdict.UNKNOWN : this.oracle.predict(x, y, z);
         return var6 == TunnelHazardModel.Verdict.FALLING ? TunnelHazardModel.Verdict.FALLING_PREDICTED : var6;
      }

      for (int var4 = 0; var4 <= (4); var4++) {
         if (this.isLethal(this.scratch.method_10103(x, y + var4, z))) {
            return TunnelHazardModel.Verdict.LETHAL;
         }
      }

      if (this.isVoidHazard(x, y, z)) {
         return TunnelHazardModel.Verdict.LETHAL;
      }

      if (this.isWalkBlocked(x, y, z)) {
         return TunnelHazardModel.Verdict.BLOCKED;
      }

      for (int var5 = 0; var5 <= (2); var5++) {
         if (this.isFalling(this.scratch.method_10103(x, y + var5, z))) {
            return TunnelHazardModel.Verdict.FALLING;
         }
      }

      if (this.isFlowingLiquid(x, y, z) || this.isFlowingLiquid(x, y + 1, z)) {
         return TunnelHazardModel.Verdict.FLOWING;
      } else {
         return !this.isWater(this.scratch.method_10103(x, y, z)) && !this.isWater(this.scratch.method_10103(x, y + 1, z))
            ? TunnelHazardModel.Verdict.SAFE
            : TunnelHazardModel.Verdict.WET;
      }
   }

   public TunnelHazardModel.Verdict classifyWithLateralShell(int x, int y, int z) {
      TunnelHazardModel.Verdict var4 = this.classify(x, y, z);
      if (var4 != TunnelHazardModel.Verdict.SAFE && !var4.isWet()) {
         return var4;
      }

      for (int var5 = 0; var5 <= (2); var5++) {
         if (this.isLethal(this.scratch.method_10103(x + 1, y + var5, z))
            || this.isLethal(this.scratch.method_10103(x - 1, y + var5, z))
            || this.isLethal(this.scratch.method_10103(x, y + var5, z + 1))
            || this.isLethal(this.scratch.method_10103(x, y + var5, z - 1))) {
            return TunnelHazardModel.Verdict.LETHAL;
         }
      }

      return var4;
   }

   public TunnelHazardModel.Verdict classifySwath(int x, int y, int z, TunnelRoutePlanner.Dir travel) {
      if (travel == null) {
         return this.classify(x, y, z);
      }

      TunnelRoutePlanner.Dir var5 = TunnelMotion.leftOf(travel);
      TunnelHazardModel.Verdict var6 = this.classify(x, y, z);

      for (int var7 = -1; var7 <= 1; var7++) {
         int var8 = x + var5.dx * var7;
         int var9 = z + var5.dz * var7;
         TunnelHazardModel.Verdict var10 = this.classify(var8, y, var9);
         if (var10 == TunnelHazardModel.Verdict.LETHAL || var10 == TunnelHazardModel.Verdict.BLOCKED || var10 == TunnelHazardModel.Verdict.FLOWING) {
            return var10;
         }

         if (var10 == TunnelHazardModel.Verdict.FALLING && var7 == 0) {
            var6 = TunnelHazardModel.Verdict.FALLING;
         } else if (var10 == TunnelHazardModel.Verdict.FALLING_PREDICTED && var6 == TunnelHazardModel.Verdict.SAFE) {
            var6 = TunnelHazardModel.Verdict.FALLING_PREDICTED;
         } else if (var10.isWet() && var6 == TunnelHazardModel.Verdict.SAFE) {
            var6 = var10;
         } else if (var10 == TunnelHazardModel.Verdict.UNKNOWN && var6 == TunnelHazardModel.Verdict.SAFE) {
            var6 = TunnelHazardModel.Verdict.UNKNOWN;
         }
      }

      return var6;
   }

   public TunnelHazardModel.Verdict classifyCenter(int x, int y, int z) {
      return this.classify(x, y, z);
   }

   public boolean canMine3x3(class_2338 center, TunnelRoutePlanner.Dir travel) {
      if (this.world != null && center != null && travel != null) {
         TunnelRoutePlanner.Dir var3 = TunnelMotion.leftOf(travel);

         for (int var4 = -1; var4 <= 1; var4++) {
            for (int var5 = 0; var5 <= (2); var5++) {
               int var6 = center.method_10263() + var3.dx * var4;
               int var7 = center.method_10264() + var5;
               int var8 = center.method_10260() + var3.dz * var4;
               class_2338 var9 = new class_2338(var6, var7, var8);
               if (!this.world.method_8320(var9).method_26215() && !this.canMine(var9)) {
                  return false;
               }

               if (this.isLethal(this.scratch.method_10103(var6 + travel.dx, var7, var8 + travel.dz))) {
                  return false;
               }
            }
         }

         int var10 = 0;

         for (int var11 = 1;
            var11 <= (3)
               && this.isFalling(this.scratch.method_10103(center.method_10263(), center.method_10264() + var11, center.method_10260()));
            var11++
         ) {
            var10++;
         }

         return var10 < (2);
      } else {
         return false;
      }
   }

   public static boolean edgeFallingBlocksRoute(boolean centerColumn) {
      return centerColumn;
   }

   public boolean blocksSwath(int x, int y, int z, TunnelRoutePlanner.Dir travel, boolean shell) {
      TunnelHazardModel.Verdict var6 = this.classifySwath(x, y, z, travel);
      return var6.blocksRoute() ? true : shell && this.classifyWithLateralShell(x, y, z).blocksRoute();
   }

   public int breakCostTicks(int x, int y, int z, class_1657 player) {
      if (this.world != null && player != null) {
         int var5 = 0;

         for (int var6 = 0; var6 <= 1; var6++) {
            this.scratch.method_10103(x, y + var6, z);
            class_2680 var7 = this.world.method_8320(this.scratch);
            if (!var7.method_26215()) {
               if (!var7.method_26227().method_15769()) {
                  return 100000;
               }

               if (!this.canMine(this.scratch.method_10062())) {
                  return 100000;
               }

               float var8 = var7.method_26165(player, this.world, this.scratch);
               if (var8 <= 0.0F) {
                  return 100000;
               }

               int var9 = (int)Math.min(100000.0, Math.ceil(1.0F / var8) + 2.0);
               var5 = Math.min(100000, var5 + var9);
            }
         }

         return var5;
      } else {
         return 0;
      }
   }

   public boolean canMine(class_2338 target) {
      if (this.world == null || target == null) {
         return false;
      }

      if (this.isLethal(target)) {
         return false;
      }

      if (this.isLiquid(target)) {
         return false;
      }

      if (this.hasLavaNeighbour(target)) {
         return false;
      }

      if (this.hasLavaNeighbour(this.scratch.method_10103(target.method_10263(), target.method_10264() + 1, target.method_10260()))) {
         return false;
      }

      if (this.hasFloodingNeighbour(target)) {
         return false;
      }

      if (this.hasFloodingNeighbour(this.scratch.method_10103(target.method_10263(), target.method_10264() + 1, target.method_10260()))) {
         return false;
      }

      int var2 = 0;

      for (int var3 = 1;
         var3 <= (3) && this.isFalling(this.scratch.method_10103(target.method_10263(), target.method_10264() + var3, target.method_10260()));
         var3++
      ) {
         var2++;
      }

      return var2 < (2);
   }

   private boolean hasLavaNeighbour(class_2338 pos) {
      int var2 = pos.method_10263();
      int var3 = pos.method_10264();
      int var4 = pos.method_10260();
      return this.isLethal(this.scratch.method_10103(var2 + 1, var3, var4))
         || this.isLethal(this.scratch.method_10103(var2 - 1, var3, var4))
         || this.isLethal(this.scratch.method_10103(var2, var3, var4 + 1))
         || this.isLethal(this.scratch.method_10103(var2, var3, var4 - 1))
         || this.isLethal(this.scratch.method_10103(var2, var3 + 1, var4))
         || this.isLethal(this.scratch.method_10103(var2, var3 - 1, var4));
   }

   private boolean hasFloodingNeighbour(class_2338 pos) {
      int var2 = pos.method_10263();
      int var3 = pos.method_10264();
      int var4 = pos.method_10260();
      return this.isLiquid(this.scratch.method_10103(var2, var3 + 1, var4))
         ? true
         : this.floodsSideways(var2 + 1, var3, var4)
            || this.floodsSideways(var2 - 1, var3, var4)
            || this.floodsSideways(var2, var3, var4 + 1)
            || this.floodsSideways(var2, var3, var4 - 1);
   }

   private boolean floodsSideways(int x, int y, int z) {
      class_3610 var4 = this.world.method_8320(this.scratch.method_10103(x, y, z)).method_26227();
      if (var4.method_15769()) {
         return false;
      } else {
         return var4.method_15771() ? true : this.world.method_8320(this.scratch.method_10103(x, y - 1, z)).method_26227().method_15769();
      }
   }

   public int followFloor(int x, int z, int refY) {
      if (this.world == null) {
         return refY;
      }

      long var4 = (x & 67108863L) << (38) | (z & 67108863L) << (12) | refY + (2048) & 4095L;
      int var6 = this.floorMemo.get(var4);
      if (var6 != (-2147483648)) {
         return var6;
      }

      int var7 = this.resolveFloor(x, z, refY);
      this.floorMemo.put(var4, var7);
      return var7;
   }

   public int riseFloor(int x, int z, int refY) {
      if (this.world == null) {
         return refY;
      }

      class_2338 var4 = new class_2338(x, refY, z);
      class_2338 var5 = var4.method_10084();
      return this.isSolidSupport(var4) && this.isClearForPlayer(var5) && this.isClearForPlayer(var5.method_10084()) ? var5.method_10264() : refY;
   }

   private boolean isSolidSupport(class_2338 pos) {
      class_2680 var2 = this.world.method_8320(pos);
      return isFullBlockSupport(var2, this.world, pos);
   }

   static boolean isFullBlockSupport(class_2680 state, class_1922 world, class_2338 pos) {
      return !state.method_26215() && state.method_26227().method_15769() && isFullBlockSupport(state.method_26220(worldx, pos));
   }

   static boolean isFullBlockSupport(class_265 shape) {
      if (!shape.method_1110() && shape.method_1090().size() == 1) {
         class_238 var1 = shape.method_1107();
         return var1.field_1323 == 0.0
            && var1.field_1322 == 0.0
            && var1.field_1321 == 0.0
            && var1.field_1320 == 1.0
            && var1.field_1325 == 1.0
            && var1.field_1324 == 1.0;
      } else {
         return false;
      }
   }

   private boolean isClearForPlayer(class_2338 pos) {
      class_2680 var2 = this.world.method_8320(pos);
      return var2.method_26227().method_15769() && var2.method_26220(this.world, pos).method_1110();
   }

   private int resolveFloor(int x, int z, int refY) {
      if (!this.isAir(this.scratch.method_10103(x, refY - 1, z))) {
         return refY;
      }

      for (int var4 = 1; var4 <= (3); var4++) {
         if (!this.isAir(this.scratch.method_10103(x, refY - 1 - var4, z))) {
            return refY - var4;
         }
      }

      return refY;
   }

   private boolean isChunkLoaded(int x, int z) {
      return this.world.method_2935().method_12126(x >> (4), z >> (4), false) != null;
   }

   private boolean isLethal(class_2338 pos) {
      class_2680 var2 = this.world.method_8320(pos);
      return var2.method_27852(class_2246.field_10164)
         || var2.method_27852(class_2246.field_10036)
         || var2.method_27852(class_2246.field_22089)
         || var2.method_26227().method_15767(class_3486.field_15518);
   }

   private boolean isFalling(class_2338 pos) {
      class_2680 var2 = this.world.method_8320(pos);
      return var2.method_27852(class_2246.field_10255)
         || var2.method_27852(class_2246.field_10102)
         || var2.method_27852(class_2246.field_10534)
         || var2.method_27852(class_2246.field_42728)
         || var2.method_27852(class_2246.field_43227);
   }

   private boolean isWater(class_2338 pos) {
      return this.world.method_8320(pos).method_26227().method_15767(class_3486.field_15517);
   }

   private boolean possiblyFlowing(class_2338 pos) {
      class_3610 var2 = this.world.method_8320(pos).method_26227();
      return !var2.method_15769() && !var2.method_15771();
   }

   public boolean isFlowingLiquid(int x, int y, int z) {
      if (this.world == null) {
         return false;
      } else {
         class_3610 var4 = this.world.method_8320(this.scratch.method_10103(x, y, z)).method_26227();
         if (var4.method_15769()) {
            return false;
         } else {
            return !var4.method_15771()
               ? true
               : this.possiblyFlowing(this.scratch.method_10103(x + 1, y, z))
                  || this.possiblyFlowing(this.scratch.method_10103(x - 1, y, z))
                  || this.possiblyFlowing(this.scratch.method_10103(x, y, z + 1))
                  || this.possiblyFlowing(this.scratch.method_10103(x, y, z - 1));
         }
      }
   }

   private boolean isLiquid(class_2338 pos) {
      return !this.world.method_8320(pos).method_26227().method_15769();
   }

   private boolean isAir(class_2338 pos) {
      return this.world.method_8320(pos).method_26215();
   }

   private boolean isWalkBlocked(int x, int y, int z) {
      if (!this.isAir(this.scratch.method_10103(x, y, z))) {
         return false;
      } else {
         return !this.isAir(this.scratch.method_10103(x, y + 1, z)) ? true : this.isAir(this.scratch.method_10103(x, y - 1, z));
      }
   }

   private boolean isVoidHazard(int x, int y, int z) {
      for (int var4 = 1; var4 <= (4); var4++) {
         class_2680 var5 = this.world.method_8320(this.scratch.method_10103(x, y - var4, z));
         if (var5.method_27852(class_2246.field_10164) || var5.method_26227().method_15767(class_3486.field_15518)) {
            return true;
         }

         if (var5.method_26227().method_15767(class_3486.field_15517)) {
            boolean var6 = this.world.method_8320(this.scratch.method_10103(x, y - var4 + 1, z)).method_26215();
            if (var6 && var4 >= (5)) {
               return true;
            }
         } else if (!var5.method_26215()) {
            return false;
         }
      }

      return y < (59);
   }

   public enum Verdict {
      SAFE,
      UNKNOWN,
      BLOCKED,
      LETHAL,
      FALLING,
      FALLING_PREDICTED,
      WET,
      FLOWING;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public boolean blocksRoute() {
         return this == BLOCKED || this == LETHAL || this == FALLING || this == FLOWING;
      }

      public boolean blocksWalking() {
         return this == LETHAL || this == BLOCKED || this == FLOWING;
      }

      public boolean isWet() {
         return this == WET || this == FLOWING;
      }

      // $VF: synthetic method
      private static TunnelHazardModel.Verdict[] $values() {
         TunnelHazardModel.Verdict[] var10000 = new TunnelHazardModel.Verdict[8];
         var10000[0] = SAFE;
         var10000[1] = UNKNOWN;
         var10000[2] = BLOCKED;
         var10000[3] = LETHAL;
         var10000[4] = FALLING;
         var10000[5] = FALLING_PREDICTED;
         var10000[6] = WET;
         var10000[7] = FLOWING;
         return var10000;
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
   }
}
