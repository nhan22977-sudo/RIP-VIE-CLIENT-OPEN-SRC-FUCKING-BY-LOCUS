package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_3532;

public final class TunnelRoutePlanner {
   public static final int OBSTACLE_AVOID_MARGIN = 6;
   public static final int TURN_SCAN_DISTANCE = 10;
   public static final int SHORE_SCAN_DEPTH = 32;
   public static final int MAX_SKIRT_OFFSET = 32;
   public static final int MIN_SKIRT_CAP = 4;
   public static final int W_PROGRESS = 4;
   public static final int W_BREAK = 1;
   public static final double WALK_ONE_BLOCK = 4.63284688441047;
   public static final double SQRT_2 = 1.4142135623730951;
   public static final double TURN_45_COST = 2.0;
   public static final double VISIT_COST = 0.6;
   public static final int VISIT_CLAMP = 10;
   public static final double COMMIT_DISCOUNT = 0.5;
   public static final double STEP_COST_INF = 1.0E9;
   public static final double UNBREAKABLE_PENALTY = 2000.0;
   private static final int WANDER_SEGMENT_MIN = 8;
   private static final int WANDER_SEGMENT_MAX = 16;
   private static final int WANDER_CHANCE_PERCENT = 25;
   private TunnelRoutePlanner.Dir skirtSide;
   private int skirtOffset;
   private int skirtCap = 32;
   private boolean wanderSuppressed;
   private class_1657 player;
   private TunnelHazardModel budgetModel;
   private long budgetSpentAtStart;
   private long budgetCeiling;
   private boolean budgetBlown;
   private int shoreDepth = 32;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void setPlayer(class_1657 player) {
      this.player = playerx;
   }

   public void resetSkirt() {
      this.skirtSide = null;
      this.skirtOffset = 0;
      this.skirtCap = 32;
   }

   public TunnelRoutePlanner.Dir skirtSide() {
      return this.skirtSide;
   }

   public int skirtOffset() {
      return this.skirtOffset;
   }

   private void beginBudget(TunnelHazardModel hz, int queryBudget, int distance) {
      this.budgetModel = hz;
      this.budgetSpentAtStart = hz.memoMisses();
      this.budgetCeiling = Math.max(1, queryBudget);
      this.budgetBlown = false;
      this.shoreDepth = class_3532.method_15340(distance / (2), 8, 32);
   }

   private void endBudget() {
      this.budgetModel = null;
   }

   private boolean outOfBudget() {
      if (this.budgetBlown) {
         return true;
      } else if (this.budgetModel == null) {
         return false;
      } else if (this.budgetModel.memoMisses() - this.budgetSpentAtStart > this.budgetCeiling) {
         this.budgetBlown = true;
         return true;
      } else {
         return false;
      }
   }

   public void setWanderSuppressed(boolean suppressed) {
      this.wanderSuppressed = suppressed;
   }

   public TunnelRoutePlanner.Result plan(class_2338 start, TunnelRoutePlanner.Dir mainDir, int distance, TunnelHazardModel hz, int queryBudget) {
      return this.plan(start, mainDir, distance, hz, queryBudget, null);
   }

   public TunnelRoutePlanner.Result plan(
      class_2338 start, TunnelRoutePlanner.Dir mainDir, int distance, TunnelHazardModel hz, int queryBudget, TunnelRoutePlanner.StepCost stepCost
   ) {
      return this.plan(start, mainDir, distance, hz, queryBudget, stepCost, true);
   }

   public TunnelRoutePlanner.Result plan(
      class_2338 start,
      TunnelRoutePlanner.Dir mainDir,
      int distance,
      TunnelHazardModel hz,
      int queryBudget,
      TunnelRoutePlanner.StepCost stepCost,
      boolean commitSkirt
   ) {
      ArrayList var8 = new ArrayList(Math.max(16, distance));
      ArrayList var9 = new ArrayList();
      long var10 = hz.memoMisses();
      boolean var12 = false;
      this.beginBudget(hz, queryBudget, distance);
      TunnelRoutePlanner.Skirt var13 = new TunnelRoutePlanner.Skirt();
      if (commitSkirt) {
         var13.side = this.skirtSide;
         var13.offset = this.skirtOffset;
         var13.cap = this.skirtCap;
      }

      try {
         int var14 = start.method_10263();
         int var15 = start.method_10264();
         int var16 = start.method_10260();
         TunnelRoutePlanner.Dir var17 = mainDir;
         if (var13.side != null && !this.blockedAhead(var14, var15, var16, mainDir, 6, hz, false)) {
            var13.reset();
         }

         for (int var18 = 0; var18 < distance; var18++) {
            if (this.outOfBudget()) {
               if (commitSkirt && var8.isEmpty()) {
                  this.storeSkirt(var13);
               }

               return new TunnelRoutePlanner.Result(var8, true, false, (int)(hz.memoMisses() - var10), var9);
            }

            boolean var19 = var18 < 2;
            TunnelRoutePlanner.Dir var20 = this.wanderDirAt(var14, var16, mainDir);
            TunnelRoutePlanner.Dir var21;
            if (var13.side != null && var13.offset > 0) {
               TunnelRoutePlanner.Dir var35 = this.rejoinStep(var13.side, mainDir);
               int var37 = var14 + var35.dx;
               int var39 = var16 + var35.dz;
               int var40 = hz.followFloor(var37, var39, var15);
               if (!this.blocks(var37, var40, var39, hz, var35, var19) && !this.blockedAhead(var37, var40, var39, mainDir, 6, hz, false)) {
                  var21 = var35;
                  var13.offset--;
                  if (var13.offset == 0) {
                     var13.reset();
                  }
               } else if (!this.blockedAhead(var14, var15, var16, mainDir, 6, hz, false)) {
                  var21 = mainDir;
               } else {
                  int var41 = var14 + var13.side.dx;
                  int var43 = var16 + var13.side.dz;
                  int var44 = hz.followFloor(var41, var43, var15);
                  if (!this.blocks(var41, var44, var43, hz, var13.side, var19) && var13.offset < var13.cap) {
                     var21 = var13.side;
                     var13.offset++;
                  } else {
                     var13.reset();
                     TunnelRoutePlanner.Dir var45 = this.chooseSafeTurn(var14, var15, var16, mainDir, 10, hz, stepCost);
                     if (var45 == null) {
                        var12 = true;
                        break;
                     }

                     var21 = var45;
                  }
               }
            } else {
               TunnelRoutePlanner.Dir var22 = this.chooseBestStep(var14, var15, var16, var20, mainDir, var17, var19, hz, stepCost);
               if (var22 != null) {
                  var21 = var22;
               } else {
                  int var23 = this.shoreClearance(var14, var15, var16, mainDir, mainDir.perpendicular(), hz);
                  int var24 = this.shoreClearance(var14, var15, var16, mainDir, mainDir.perpendicular().opposite(), hz);
                  TunnelRoutePlanner.Dir var25 = null;
                  int var26 = 33;
                  if (var23 <= 32 || var24 <= 32) {
                     boolean var27 = var23 <= var24;
                     var25 = var27 ? mainDir.perpendicular() : mainDir.perpendicular().opposite();
                     var26 = Math.min(var23, var24);
                  }

                  int var42 = var25 == null ? var14 : var14 + var25.dx;
                  int var28 = var25 == null ? var16 : var16 + var25.dz;
                  int var29 = var25 == null ? var15 : hz.followFloor(var42, var28, var15);
                  if (var25 != null && !this.blocks(var42, var29, var28, hz, var25, var19)) {
                     var13.side = var25;
                     var13.offset = 1;
                     var13.cap = class_3532.method_15340(var26 + 2, 4, 32);
                     var21 = var25;
                  } else {
                     TunnelRoutePlanner.Dir var30 = this.chooseSafeTurn(var14, var15, var16, mainDir, 10, hz, stepCost);
                     if (var30 == null) {
                        var12 = true;
                        break;
                     }

                     var21 = var30;
                  }
               }
            }

            var21 = this.enforceSelectedDirection(var14, var15, var16, var21, var20, mainDir, var17, var19, hz, stepCost);
            if (var21 == null) {
               var12 = true;
               break;
            }

            if (var18 == 0 && commitSkirt) {
               this.storeSkirt(var13);
            }

            int var36 = var14 + var21.dx;
            int var38 = var16 + var21.dz;
            if (hz.riseFloor(var36, var38, var15) > var15) {
               var9.add(var8.size());
            }

            var14 = var36;
            var16 = var38;
            var15 = hz.followFloor(var14, var16, var15);
            var17 = var21;
            var8.add(new class_2338(var14, var15, var16));
         }

         if (commitSkirt && var8.isEmpty()) {
            this.storeSkirt(var13);
         }

         return new TunnelRoutePlanner.Result(var8, this.budgetBlown, var12, (int)(hz.memoMisses() - var10), var9);
      } finally {
         this.endBudget();
      }
   }

   private void storeSkirt(TunnelRoutePlanner.Skirt sk) {
      this.skirtSide = sk.side;
      this.skirtOffset = sk.offset;
      this.skirtCap = sk.cap;
   }

   public TunnelRoutePlanner.Dir chooseSafeTurn(int x, int y, int z, TunnelRoutePlanner.Dir forward, int distance, TunnelHazardModel hz) {
      return this.chooseSafeTurn(x, y, z, forward, distance, hz, null);
   }

   public TunnelRoutePlanner.Dir chooseSafeTurn(
      int x, int y, int z, TunnelRoutePlanner.Dir forward, int distance, TunnelHazardModel hz, TunnelRoutePlanner.StepCost stepCost
   ) {
      TunnelRoutePlanner.Dir var8 = forward.clockwise();
      TunnelRoutePlanner.Dir var9 = forward.counterClockwise();
      int var10 = this.shoreClearance(x, y, z, forward, forward.perpendicular(), hz);
      int var11 = this.shoreClearance(x, y, z, forward, forward.perpendicular().opposite(), hz);
      boolean var12 = var10 <= var11;
      int var13 = 2147483647;
      TunnelRoutePlanner.Dir var14 = null;

      for (int var15 = 0; var15 < (3); var15++) {
         TunnelRoutePlanner.Dir var16 = var12 ? var8 : var9;
         TunnelRoutePlanner.Dir var17 = var12 ? var9 : var8;
         TunnelRoutePlanner.Dir[] var10000 = new TunnelRoutePlanner.Dir[2];
         var10000[0] = var16;
         var10000[1] = var17;

         for (TunnelRoutePlanner.Dir var21 : var10000) {
            if (!this.blockedAhead(x, y, z, var21, distance, hz, false)) {
               int var22 = stepCost == null ? 0 : this.pathCost(x, y, z, var21, distance, hz, stepCost);
               if (var21 == forward) {
                  var22 -= 4;
               }

               if (var22 < var13) {
                  var13 = var22;
                  var14 = var21;
               }
            }
         }

         var8 = var8.clockwise();
         var9 = var9.counterClockwise();
      }

      return var14;
   }

   private int pathCost(int x, int y, int z, TunnelRoutePlanner.Dir direction, int distance, TunnelHazardModel hz, TunnelRoutePlanner.StepCost stepCost) {
      int var8 = 0;
      int var9 = x;
      int var10 = z;
      int var11 = y;

      for (int var12 = 1; var12 <= distance; var12++) {
         var9 += direction.dx;
         var10 += direction.dz;
         int var13 = var11;
         if (rejectsDiagonalAscent(direction, var13, hz.riseFloor(var9, var10, var13))) {
            return 2147483647;
         }

         var11 = hz.followFloor(var9, var10, var13);
         var8 += stepCost.cost(var9, var10, direction);
         var8 += 1 * hz.breakCostTicks(var9, var11, var10, this.player);
         if (direction.isDiagonal()) {
            int var14 = var9;
            int var15 = var10 - direction.dz;
            int var16 = var9 - direction.dx;
            int var17 = var10;
            var8 += 1 * hz.breakCostTicks(var14, hz.followFloor(var14, var15, var13), var15, this.player);
            var8 += 1 * hz.breakCostTicks(var16, hz.followFloor(var16, var17, var13), var17, this.player);
         }
      }

      return var8;
   }

   private TunnelRoutePlanner.Dir chooseBestStep(
      int x,
      int y,
      int z,
      TunnelRoutePlanner.Dir desired,
      TunnelRoutePlanner.Dir mainDir,
      TunnelRoutePlanner.Dir committed,
      boolean shell,
      TunnelHazardModel hz,
      TunnelRoutePlanner.StepCost stepCost
   ) {
      TunnelRoutePlanner.Dir[] var10000 = new TunnelRoutePlanner.Dir[4];
      var10000[0] = desired;
      var10000[1] = mainDir;
      var10000[2] = mainDir.clockwise();
      var10000[3] = mainDir.counterClockwise();
      TunnelRoutePlanner.Dir[] var10 = var10000;
      double var11 = Double.MAX_VALUE;
      TunnelRoutePlanner.Dir var13 = null;

      for (TunnelRoutePlanner.Dir var17 : var10) {
         if (!this.blockedAhead(x, y, z, var17, 6, hz, shell && (var17 == desired || var17 == mainDir))) {
            double var18 = this.stepCostTicks(x, y, z, var17, mainDir, committed, hz, stepCost);
            if (!(var18 >= 1.0E9) && var18 < var11) {
               var11 = var18;
               var13 = var17;
            }
         }
      }

      return var13;
   }

   private TunnelRoutePlanner.Dir enforceSelectedDirection(
      int x,
      int y,
      int z,
      TunnelRoutePlanner.Dir selected,
      TunnelRoutePlanner.Dir desired,
      TunnelRoutePlanner.Dir mainDir,
      TunnelRoutePlanner.Dir committed,
      boolean shell,
      TunnelHazardModel hz,
      TunnelRoutePlanner.StepCost stepCost
   ) {
      int var11 = hz.riseFloor(x + selected.dx, z + selected.dz, y);
      TunnelRoutePlanner.Dir var12 = rejectsDiagonalAscent(selected, y, var11)
         ? this.chooseBestStep(x, y, z, desired, mainDir, committed, shell, hz, stepCost)
         : selected;
      return selectedDirectionWithoutDiagonalAscent(selected, y, var11, var12);
   }

   public double stepCostTicks(
      int x,
      int y,
      int z,
      TunnelRoutePlanner.Dir candidate,
      TunnelRoutePlanner.Dir mainDir,
      TunnelRoutePlanner.Dir committed,
      TunnelHazardModel hz,
      TunnelRoutePlanner.StepCost stepCost
   ) {
      int var9 = candidate.dx * mainDir.dx + candidate.dz * mainDir.dz;
      if (var9 <= 0) {
         return 1.0E9;
      }

      int var10 = x + candidate.dx;
      int var11 = z + candidate.dz;
      int var12 = hz.riseFloor(var10, var11, y);
      if (rejectsDiagonalAscent(candidate, y, var12)) {
         return 1.0E9;
      }

      int var13 = hz.followFloor(var10, var11, y);
      double var14 = this.breakTermTicks(x, y, z, var10, var13, var11, candidate, hz);
      double var16 = 4.63284688441047 * (candidate.isDiagonal() ? 1.4142135623730951 : 1.0) + var14;
      var16 += 2.0 * turnSteps(committed, candidate);
      if (stepCost != null) {
         var16 += 0.6 * Math.min(10, Math.max(0, stepCost.cost(var10, var11, candidate)));
      }

      if (candidate == committed) {
         var16 -= 0.5;
      }

      return var16 / var9;
   }

   static boolean rejectsDiagonalAscent(TunnelRoutePlanner.Dir candidate, int currentY, int riseY) {
      return candidate.isDiagonal() && riseY > currentY;
   }

   static TunnelRoutePlanner.Dir selectedDirectionWithoutDiagonalAscent(
      TunnelRoutePlanner.Dir selected, int currentY, int riseY, TunnelRoutePlanner.Dir fallback
   ) {
      return rejectsDiagonalAscent(selected, currentY, riseY) ? fallback : selected;
   }

   private double breakTermTicks(int x, int y, int z, int nx, int ny, int nz, TunnelRoutePlanner.Dir candidate, TunnelHazardModel hz) {
      double var9 = this.columnTicks(nx, ny, nz, hz);
      if (candidate.isDiagonal()) {
         int var11 = nx;
         int var12 = z;
         int var13 = x;
         int var14 = nz;
         var9 += this.columnTicks(var11, hz.followFloor(var11, var12, y), var12, hz);
         var9 += this.columnTicks(var13, hz.followFloor(var13, var14, y), var14, hz);
      }

      return var9;
   }

   private double columnTicks(int x, int y, int z, TunnelHazardModel hz) {
      int var5 = hz.breakCostTicks(x, y, z, this.player);
      return var5 >= (100000) ? 2000.0 : 1.0 * var5;
   }

   private static int turnSteps(TunnelRoutePlanner.Dir from, TunnelRoutePlanner.Dir to) {
      if (from != null && to != null) {
         int var2 = Math.abs(from.ordinal() - to.ordinal());
         return Math.min(var2, (8) - var2);
      } else {
         return 0;
      }
   }

   public int shoreClearance(int x, int y, int z, TunnelRoutePlanner.Dir mainDir, TunnelRoutePlanner.Dir side, TunnelHazardModel hz) {
      int var7 = y;

      for (int var8 = 1; var8 <= this.shoreDepth; var8++) {
         if (this.outOfBudget()) {
            return 33;
         }

         int var9 = x + side.dx * var8;
         int var10 = z + side.dz * var8;
         var7 = hz.followFloor(var9, var10, var7);
         if (this.blocks(var9, var7, var10, hz, mainDir, false)) {
            return 33;
         }

         if (!this.blockedAhead(var9, var7, var10, mainDir, 6, hz, false)) {
            return var8;
         }
      }

      return 33;
   }

   public TunnelRoutePlanner.Dir rejoinStep(TunnelRoutePlanner.Dir side, TunnelRoutePlanner.Dir mainDir) {
      int var3 = Integer.signum(mainDir.dx - side.dx);
      int var4 = Integer.signum(mainDir.dz - side.dz);

      for (TunnelRoutePlanner.Dir var8 : TunnelRoutePlanner.Dir.VALUES) {
         if (var8.dx == var3 && var8.dz == var4) {
            return var8;
         }
      }

      return mainDir;
   }

   public boolean blockedAhead(int x, int y, int z, TunnelRoutePlanner.Dir dir, int distance, TunnelHazardModel hz, boolean shell) {
      int var8 = y;

      for (int var9 = 1; var9 <= distance; var9++) {
         if (this.outOfBudget()) {
            return false;
         }

         int var10 = x + dir.dx * var9;
         int var11 = z + dir.dz * var9;
         var8 = hz.followFloor(var10, var11, var8);
         if (this.blocks(var10, var8, var11, hz, dir, shell && var9 <= (2))) {
            return true;
         }

         if (dir.isDiagonal()) {
            int var12 = x + dir.dx * var9;
            int var13 = z + dir.dz * (var9 - 1);
            int var14 = x + dir.dx * (var9 - 1);
            int var15 = z + dir.dz * var9;
            if (this.blocks(var12, hz.followFloor(var12, var13, var8), var13, hz, dir, false)) {
               return true;
            }

            if (this.blocks(var14, hz.followFloor(var14, var15, var8), var15, hz, dir, false)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean blocks(int x, int y, int z, TunnelHazardModel hz, TunnelRoutePlanner.Dir travel, boolean shell) {
      TunnelHazardModel.Verdict var7 = hz.classifySwath(x, y, z, travel);
      return var7.blocksRoute() ? true : shell && hz.classifyWithLateralShell(x, y, z).blocksRoute();
   }

   public TunnelRoutePlanner.Dir wanderDirAt(int x, int z, TunnelRoutePlanner.Dir mainDir) {
      if (this.wanderSuppressed) {
         return mainDir;
      } else {
         int var4 = Math.max(1, 12);
         long var5 = (long)x * mainDir.dx + (long)z * mainDir.dz;
         long var7 = Math.floorDiv(var5, var4);
         if ((var7 & 1L) == 0L) {
            return mainDir;
         } else {
            long var9 = var7 * -7046029254386353131L;
            var9 ^= var9 >>> (29);
            if ((int)Math.floorMod(var9, 100L) >= (25)) {
               return mainDir;
            } else {
               return (var9 >>> (33) & 1L) == 0L ? mainDir.clockwise() : mainDir.counterClockwise();
            }
         }
      }
   }

   public enum Dir {
      SOUTH(0, 1, 0.0F),
      SOUTHWEST(-1, 1, 45.0F),
      WEST(-1, 0, 90.0F),
      NORTHWEST(-1, -1, 135.0F),
      NORTH(0, -1, 180.0F),
      NORTHEAST(1, -1, -135.0F),
      EAST(1, 0, -90.0F),
      SOUTHEAST(1, 1, -45.0F);

      public static final TunnelRoutePlanner.Dir[] VALUES = values();
      public final int dx;
      public final int dz;
      public final float yaw;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Dir(int dx, int dz, float yaw) {
         this.dx = dxx;
         this.dz = dzx;
         this.yaw = yawx;
      }

      public boolean isDiagonal() {
         return this.dx != 0 && this.dz != 0;
      }

      public TunnelRoutePlanner.Dir clockwise() {
         return VALUES[(this.ordinal() + 1) % (8)];
      }

      public TunnelRoutePlanner.Dir counterClockwise() {
         return VALUES[(this.ordinal() + (7)) % (8)];
      }

      public TunnelRoutePlanner.Dir opposite() {
         return VALUES[(this.ordinal() + (4)) % (8)];
      }

      public TunnelRoutePlanner.Dir perpendicular() {
         return VALUES[(this.ordinal() + (2)) % (8)];
      }

      public static TunnelRoutePlanner.Dir fromYaw(float yaw) {
         int var1 = Math.round(class_3532.method_15393(yawx) / 45.0F);
         return VALUES[(var1 % (8) + (8)) % (8)];
      }

      // $VF: synthetic method
      private static TunnelRoutePlanner.Dir[] $values() {
         TunnelRoutePlanner.Dir[] var10000 = new TunnelRoutePlanner.Dir[8];
         var10000[0] = SOUTH;
         var10000[1] = SOUTHWEST;
         var10000[2] = WEST;
         var10000[3] = NORTHWEST;
         var10000[4] = NORTH;
         var10000[5] = NORTHEAST;
         var10000[6] = EAST;
         var10000[7] = SOUTHEAST;
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

   public record Result(List<class_2338> path, boolean budgetExhausted, boolean deadEnd, int queries, List<Integer> riseAt) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public Result(List path, boolean budgetExhausted, boolean deadEnd, int queries, List riseAt) {
         path = List.copyOf(path);
         riseAt = List.copyOf(riseAt);
         this.path = path;
         this.budgetExhausted = budgetExhausted;
         this.deadEnd = deadEnd;
         this.queries = queries;
         this.riseAt = riseAt;
      }

      public boolean isEmpty() {
         return this.path.isEmpty();
      }
   }

   private static final class Skirt {
      TunnelRoutePlanner.Dir side;
      int offset;
      int cap = 32;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      void reset() {
         this.side = null;
         this.offset = 0;
         this.cap = 32;
      }
   }

   public interface StepCost {
      int cost(int var1, int var2, TunnelRoutePlanner.Dir var3);
   }
}
