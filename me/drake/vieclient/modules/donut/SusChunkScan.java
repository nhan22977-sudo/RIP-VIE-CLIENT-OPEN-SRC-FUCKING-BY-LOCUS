package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.class_1944;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_638;
import net.minecraft.class_2338.class_2339;

final class SusChunkScan {
   static final int MIN_Y = -64;
   static final int MAX_Y = 64;
   static final byte AIR = 0;
   static final byte SOLID = 1;
   static final byte BUDDING_AMETHYST = 2;
   static final byte AMETHYST_CLUSTER = 3;
   static final byte AMETHYST_BUD = 4;
   static final byte AMETHYST_STRUCTURAL = 5;
   static final byte CALCITE = 6;
   static final byte FLUID = 7;
   private static final int WIDTH = 16;
   private static final int MAX_CANDIDATES = 9000;
   private static final int[][] NEIGHBORS;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SusChunkScan() {
   }

   static SusChunkScan.ChunkSnapshot snapshot(int chunkX, int chunkZ, int minY, int maxY, byte[] blocks, byte[] blockLight) {
      return new SusChunkScan.ChunkSnapshot(chunkX, chunkZ, minY, maxY, blocks, blockLight);
   }

   static SusChunkScan.ChunkSnapshot capture(class_2818 chunk, class_638 world) {
      int var2 = -64;
      int var3 = 64;
      byte[] var4 = new byte[expectedSize(var2, var3)];
      byte[] var5 = new byte[var4.length];
      class_2826[] var6 = chunk.method_12006();
      int var7 = world.method_32891();
      class_2339 var8 = new class_2339();
      int var9 = chunk.method_12004().method_8326();
      int var10 = chunk.method_12004().method_8328();

      for (int var11 = var2; var11 <= var3; var11++) {
         int var12 = (var11 >> (4)) - var7;
         class_2826 var13 = var12 >= 0 && var12 < var6.length ? var6[var12] : null;

         for (int var14 = 0; var14 < (16); var14++) {
            for (int var15 = 0; var15 < (16); var15++) {
               int var16 = ((var11 - var2) * (16) + var14) * (16) + var15;
               class_2680 var17 = var13 == null ? class_2246.field_10124.method_9564() : var13.method_12254(var15, var11 & (15), var14);
               var4[var16] = encode(var17);
               var8.method_10103(var9 + var15, var11, var10 + var14);
               var5[var16] = (byte)world.method_8314(class_1944.field_9282, var8);
            }
         }
      }

      return new SusChunkScan.ChunkSnapshot(chunk.method_12004().field_9181, chunk.method_12004().field_9180, var2, var3, var4, var5);
   }

   static SusChunkScan.Result analyze(SusChunkScan.ChunkSnapshot snapshot, int sensitivity) {
      int var2 = Math.max(1, sensitivity);
      int var3 = var2 <= (20) ? var2 : Math.max(1, 1 + Math.round((var2 - (50)) * 19.0F / 50.0F));
      SusChunkScan.StandardResult var4 = scanStandard(snapshot, var3);
      long var5 = packChunk(snapshot.chunkX(), snapshot.chunkZ());
      if (var4.grown()) {
         return new SusChunkScan.Result(var5, var4.positions().size(), var4.visibleBudding(), var4.positions(), SusChunkScan.Classification.GROWN);
      } else {
         SusChunkScan.Result var7 = scanLightOnly(snapshot, var3);
         if (var7.classification() == SusChunkScan.Classification.GROWN) {
            return var7;
         } else {
            return var4.hasCenter()
               ? new SusChunkScan.Result(
                  var5, var4.positions().size(), Math.max(1, var4.visibleBudding()), var4.positions(), SusChunkScan.Classification.UNGROWN
               )
               : new SusChunkScan.Result(var5, 0, 0, Set.of(), SusChunkScan.Classification.NONE);
         }
      }
   }

   private static SusChunkScan.StandardResult scanStandard(SusChunkScan.ChunkSnapshot snapshot, int effectiveThreshold) {
      int[] var2 = findCenter(snapshot);
      if (var2 == null) {
         return new SusChunkScan.StandardResult(false, false, 0, Set.of());
      }

      int var3 = Math.max(-2, var2[0] - 8);
      int var4 = Math.min(17, var2[0] + 8);
      int var5 = Math.max(snapshot.minY(), var2[1] - 8);
      int var6 = Math.min(snapshot.maxY(), var2[1] + 8);
      int var7 = Math.max(-2, var2[2] - 8);
      int var8 = Math.min(17, var2[2] + 8);
      ArrayList var9 = new ArrayList();
      int var10 = 0;

      for (int var11 = var5; var11 <= var6; var11++) {
         for (int var12 = var3; var12 <= var4; var12++) {
            for (int var13 = var7; var13 <= var8; var13++) {
               byte var14 = snapshot.blockAt(var12, var11, var13);
               if (var14 == 3) {
                  if (hasSolidOrAmethystNeighbor(snapshot, var12, var11, var13)) {
                     var9.add(new SusChunkScan.Point(var12, var11, var13));
                  }
               } else if (var14 != 2) {
                  if (var9.size() < 9000 && var14 != 7 && var14 != 0) {
                     int var25 = snapshot.blockLightAt(var12, var11, var13);
                     if (var25 == 0 && hasBrightAirOrAmethystNeighbor(snapshot, var12, var11, var13)) {
                        var9.add(new SusChunkScan.Point(var12, var11, var13));
                     }
                  }
               } else {
                  var10++;

                  for (int[] var18 : NEIGHBORS) {
                     int var19 = var12 + var18[0];
                     int var20 = var11 + var18[1];
                     int var21 = var13 + var18[2];
                     byte var22 = snapshot.blockAt(var19, var20, var21);
                     if (var22 == 0 || var22 == 4 || var22 == 7) {
                        int var23 = snapshot.blockLightAt(var19, var20, var21);
                        if (var23 >= 3 && var23 <= 5 && var9.size() < 9000) {
                           var9.add(new SusChunkScan.Point(var19, var20, var21));
                        }
                     }
                  }
               }
            }
         }
      }

      Set var24 = largestComponent(var9, 36);
      return new SusChunkScan.StandardResult(true, var24.size() >= effectiveThreshold, var10, toBlockPositions(snapshot, var24));
   }

   private static int[] findCenter(SusChunkScan.ChunkSnapshot snapshot) {
      int var1 = 0;
      int var2 = 0;
      int var3 = 0;
      int var4 = 0;

      for (int var5 = snapshot.minY(); var5 <= snapshot.maxY(); var5++) {
         for (int var6 = 0; var6 < (16); var6++) {
            for (int var7 = 0; var7 < (16); var7++) {
               byte var8 = snapshot.blockAt(var6, var5, var7);
               if (var8 == (5)
                  || var8 == (2)
                  || var8 == (3)
                  || var8 == (4)
                  || var8 == (6)) {
                  var1 += var6;
                  var2 += var5;
                  var3 += var7;
                  var4++;
               }
            }
         }
      }

      int[] var10000;
      if (var4 < (5)) {
         var10000 = null;
      } else {
         var10000 = new int[3];
         var10000[0] = var1 / var4;
         var10000[1] = var2 / var4;
         var10000[2] = var3 / var4;
      }

      return var10000;
   }

   private static SusChunkScan.Result scanLightOnly(SusChunkScan.ChunkSnapshot snapshot, int threshold) {
      ArrayList var2 = new ArrayList();
      HashSet var3 = new HashSet();

      for (int var4 = snapshot.minY(); var4 <= snapshot.maxY(); var4++) {
         for (int var5 = 0; var5 < 16; var5++) {
            for (int var6 = 0; var6 < 16; var6++) {
               byte var7 = snapshot.blockAt(var5, var4, var6);
               if (var7 != 7) {
                  int var8 = snapshot.blockLightAt(var5, var4, var6);
                  if (var8 >= 6) {
                     var3.add(areaKey(var5, var6));
                  } else if (var8 >= 3 && var8 <= 5 && !hasFluidNeighbor(snapshot, var5, var4, var6)) {
                     boolean var9 = true;

                     for (int[] var13 : NEIGHBORS) {
                        int var14 = var5 + var13[0];
                        int var15 = var4 + var13[1];
                        int var16 = var6 + var13[2];
                        if (snapshot.blockAt(var14, var15, var16) == 7) {
                           var9 = false;
                           break;
                        }

                        int var17 = snapshot.blockLightAt(var14, var15, var16);
                        if (var17 >= 6) {
                           var3.add(areaKey(var14, var16));
                           var9 = false;
                           break;
                        }

                        if (var17 > var8) {
                           var9 = false;
                           break;
                        }
                     }

                     if (var9) {
                        var2.add(new SusChunkScan.Point(var5, var4, var6));
                     }
                  } else if (hasFluidNeighbor(snapshot, var5, var4, var6)) {
                  }
               }
            }
         }
      }

      var2.removeIf(point -> var3.contains(areaKey(point.x(), point.z())));
      if (var2.size() < threshold) {
         return none(snapshot);
      }

      Set var18 = Set.of();

      for (Set var20 : components(var2, 121)) {
         if (var20.size() >= threshold) {
            SusChunkScan.Sphere var21 = fitSphere(var20);
            if (var21 != null && !(var21.radius() < 3.0) && !(var21.radius() > 7.0) && acceptSphere(snapshot, var20, var21) && var20.size() > var18.size()) {
               var18 = var20;
            }
         }
      }

      return var18.size() < threshold
         ? none(snapshot)
         : new SusChunkScan.Result(
            packChunk(snapshot.chunkX(), snapshot.chunkZ()), var18.size(), 0, toBlockPositions(snapshot, var18), SusChunkScan.Classification.GROWN
         );
   }

   private static boolean acceptSphere(SusChunkScan.ChunkSnapshot snapshot, Set points, SusChunkScan.Sphere sphere) {
      double var3 = 0.0;
      double var5 = 0.0;
      double var7 = 0.0;
      double var9 = 0.0;
      int var11 = Integer.MAX_VALUE;
      int var12 = Integer.MIN_VALUE;
      int var13 = Integer.MAX_VALUE;
      int var14 = Integer.MIN_VALUE;
      int var15 = Integer.MAX_VALUE;
      int var16 = Integer.MIN_VALUE;
      HashSet var17 = new HashSet();

      for (SusChunkScan.Point var19 : points) {
         double var20 = var19.x() - sphere.x();
         double var22 = var19.y() - sphere.y();
         double var24 = var19.z() - sphere.z();
         double var26 = Math.sqrt(var20 * var20 + var22 * var22 + var24 * var24);
         if (var26 < 1.0E-4) {
            var26 = 1.0;
         }

         var3 += Math.abs(var26 - sphere.radius());
         var5 += var20 / var26;
         var7 += var22 / var26;
         var9 += var24 / var26;
         var17.add((var20 >= 0.0 ? 1 : 0) | (var22 >= 0.0 ? 2 : 0) | (var24 >= 0.0 ? 4 : 0));
         var11 = Math.min(var11, var19.x());
         var12 = Math.max(var12, var19.x());
         var13 = Math.min(var13, var19.y());
         var14 = Math.max(var14, var19.y());
         var15 = Math.min(var15, var19.z());
         var16 = Math.max(var16, var19.z());
      }

      if (!(var3 / points.size() > 1.35) && var17.size() >= 3 && !(Math.sqrt(var5 * var5 + var7 * var7 + var9 * var9) / points.size() > 0.72)) {
         int var28 = (int)Math.round(sphere.x());
         int var29 = (int)Math.round(sphere.y());
         int var30 = (int)Math.round(sphere.z());
         return snapshot.blockLightAt(var28, var29, var30) <= 3
            && (var12 - var11 + 1 >= 3 || var16 - var15 + 1 >= 3)
            && var12 - var11 + 1 + var16 - var15 + 1 >= 6
            && var14 - var13 + 1 >= 2;
      } else {
         return false;
      }
   }

   private static SusChunkScan.Sphere fitSphere(Set points) {
      double var1 = 0.0;
      double var3 = 0.0;
      double var5 = 0.0;

      for (SusChunkScan.Point var8 : points) {
         var1 += var8.x();
         var3 += var8.y();
         var5 += var8.z();
      }

      var1 /= points.size();
      var3 /= points.size();
      var5 /= points.size();
      double[][] var25 = new double[4][4];
      double[] var26 = new double[4];

      for (SusChunkScan.Point var10 : points) {
         double var11 = var10.x() - var1;
         double var13 = var10.y() - var3;
         double var15 = var10.z() - var5;
         double var17 = var11 * var11 + var13 * var13 + var15 * var15;
         double[] var10000 = new double[4];
         var10000[0] = var11;
         var10000[1] = var13;
         var10000[2] = var15;
         var10000[3] = 1.0;
         double[] var19 = var10000;

         for (int var20 = 0; var20 < (4); var20++) {
            for (int var21 = 0; var21 < (4); var21++) {
               var25[var20][var21] = var25[var20][var21] + var19[var20] * var19[var21];
            }

            var26[var20] += var19[var20] * var17;
         }
      }

      double[] var27 = solve4x4(var25, var26);
      if (var27 == null) {
         return null;
      }

      double var28 = var27[0] / 2.0;
      double var12 = var27[1] / 2.0;
      double var14 = var27[2] / 2.0;
      double var16 = var27[3] + var28 * var28 + var12 * var12 + var14 * var14;
      return var16 <= 0.0 ? null : new SusChunkScan.Sphere(var1 + var28, var3 + var12, var5 + var14, Math.sqrt(var16));
   }

   static double[] solve4x4(double[][] matrix, double[] vector) {
      if (matrix.length == 4 && vector.length == 4) {
         double[][] var2 = new double[4][5];

         for (int var3 = 0; var3 < 4; var3++) {
            if (matrix[var3].length != 4) {
               throw new IllegalArgumentException(
                  "Expected 4x4 system"
               );
            }

            System.arraycopy(matrix[var3], 0, var2[var3], 0, 4);
            var2[var3][4] = vector[var3];
         }

         for (int var10 = 0; var10 < 4; var10++) {
            int var4 = var10;

            for (int var5 = var10 + 1; var5 < 4; var5++) {
               if (Math.abs(var2[var5][var10]) > Math.abs(var2[var4][var10])) {
                  var4 = var5;
               }
            }

            double[] var13 = var2[var10];
            var2[var10] = var2[var4];
            var2[var4] = var13;
            if (Math.abs(var2[var10][var10]) < 1.0E-9) {
               return null;
            }

            for (int var6 = var10 + 1; var6 < 4; var6++) {
               double var7 = var2[var6][var10] / var2[var10][var10];

               for (int var9 = var10; var9 <= 4; var9++) {
                  var2[var6][var9] = var2[var6][var9] - var7 * var2[var10][var9];
               }
            }
         }

         double[] var11 = new double[4];

         for (int var12 = 3; var12 >= 0; var12--) {
            double var14 = 0.0;

            for (int var15 = var12 + 1; var15 < 4; var15++) {
               var14 += var2[var12][var15] * var11[var15];
            }

            var11[var12] = (var2[var12][4] - var14) / var2[var12][var12];
         }

         return var11;
      } else {
         throw new IllegalArgumentException(
            "Expected 4x4 system"
         );
      }
   }

   private static Set largestComponent(List candidates, int maxDistanceSquared) {
      Set var2 = Set.of();

      for (Set var4 : components(candidates, maxDistanceSquared)) {
         if (var4.size() > var2.size()) {
            var2 = var4;
         }
      }

      return var2;
   }

   private static List components(List candidates, int maxDistanceSquared) {
      boolean[] var2 = new boolean[candidates.size()];
      ArrayList var3 = new ArrayList();

      for (int var4 = 0; var4 < candidates.size(); var4++) {
         if (!var2[var4]) {
            LinkedHashSet var5 = new LinkedHashSet();
            ArrayDeque var6 = new ArrayDeque();
            var6.add(var4);
            var2[var4] = true;

            while (!var6.isEmpty()) {
               int var7 = (Integer)var6.remove();
               SusChunkScan.Point var8 = (SusChunkScan.Point)candidates.get(var7);
               var5.add(var8);

               for (int var9 = 0; var9 < candidates.size(); var9++) {
                  if (!var2[var9] && squaredDistance(var8, (SusChunkScan.Point)candidates.get(var9)) <= maxDistanceSquared) {
                     var2[var9] = true;
                     var6.add(var9);
                  }
               }
            }

            var3.add(var5);
         }
      }

      return var3;
   }

   private static boolean hasSolidOrAmethystNeighbor(SusChunkScan.ChunkSnapshot snapshot, int x, int y, int z) {
      for (int[] var7 : NEIGHBORS) {
         byte var8 = snapshot.blockAt(x + var7[0], y + var7[1], z + var7[2]);
         if ((var8 == 1 || var8 == (6) || var8 == (5) || var8 == (2) || var8 == (4))
            && var8 != (3)) {
            return true;
         }
      }

      return false;
   }

   private static boolean hasBrightAirOrAmethystNeighbor(SusChunkScan.ChunkSnapshot snapshot, int x, int y, int z) {
      for (int[] var7 : NEIGHBORS) {
         int var8 = x + var7[0];
         int var9 = y + var7[1];
         int var10 = z + var7[2];
         byte var11 = snapshot.blockAt(var8, var9, var10);
         int var12 = snapshot.blockLightAt(var8, var9, var10);
         if (var12 >= (3) && var12 <= (5) && (var11 == 0 || var11 == (3) || var11 == (4))) {
            return true;
         }
      }

      return false;
   }

   private static boolean hasFluidNeighbor(SusChunkScan.ChunkSnapshot snapshot, int x, int y, int z) {
      for (int[] var7 : NEIGHBORS) {
         if (snapshot.blockAt(x + var7[0], y + var7[1], z + var7[2]) == (7)) {
            return true;
         }
      }

      return false;
   }

   private static Set toBlockPositions(SusChunkScan.ChunkSnapshot snapshot, Set points) {
      LinkedHashSet var2 = new LinkedHashSet();

      for (SusChunkScan.Point var4 : points) {
         var2.add(new class_2338(snapshot.chunkX() * (16) + var4.x(), var4.y(), snapshot.chunkZ() * (16) + var4.z()));
      }

      return var2;
   }

   private static SusChunkScan.Result none(SusChunkScan.ChunkSnapshot snapshot) {
      return new SusChunkScan.Result(packChunk(snapshot.chunkX(), snapshot.chunkZ()), 0, 0, Set.of(), SusChunkScan.Classification.NONE);
   }

   private static long packChunk(int x, int z) {
      return x & 4294967295L | (long)z << (32);
   }

   private static int expectedSize(int minY, int maxY) {
      return (256) * (maxY - minY + 1);
   }

   private static int squaredDistance(SusChunkScan.Point first, SusChunkScan.Point second) {
      int var2 = first.x() - second.x();
      int var3 = first.y() - second.y();
      int var4 = first.z() - second.z();
      return var2 * var2 + var3 * var3 + var4 * var4;
   }

   private static long areaKey(int x, int z) {
      return (long)(x >> (3)) << (32) | z >> (3) & 4294967295L;
   }

   private static byte encode(class_2680 state) {
      if (state.method_26215()) {
         return 0;
      } else if (!state.method_26227().method_15769()) {
         return (byte)(7);
      } else if (state.method_27852(class_2246.field_27160)) {
         return (byte)(2);
      } else if (state.method_27852(class_2246.field_27161)) {
         return (byte)(3);
      } else if (state.method_27852(class_2246.field_27164) || state.method_27852(class_2246.field_27163) || state.method_27852(class_2246.field_27162)) {
         return (byte)(4);
      } else if (state.method_27852(class_2246.field_27159)) {
         return (byte)(5);
      } else {
         return (byte)(state.method_27852(class_2246.field_27114) ? 6 : 1);
      }
   }

   static {
      int[][] var10000 = new int[6][];
      int[] var10003 = new int[3];
      var10003[0] = 1;
      var10003[1] = 0;
      var10003[2] = 0;
      var10000[0] = var10003;
      var10003 = new int[3];
      var10003[0] = -1;
      var10003[1] = 0;
      var10003[2] = 0;
      var10000[1] = var10003;
      int var10002 = 2;
      var10003 = new int[3];
      var10003[0] = 0;
      var10003[1] = 1;
      var10003[2] = 0;
      var10000[var10002] = var10003;
      var10002 = 3;
      var10003 = new int[3];
      var10003[0] = 0;
      var10003[1] = -1;
      var10003[2] = 0;
      var10000[var10002] = var10003;
      var10002 = 4;
      var10003 = new int[3];
      var10003[0] = 0;
      var10003[1] = 0;
      var10003[2] = 1;
      var10000[var10002] = var10003;
      var10002 = 5;
      var10003 = new int[3];
      var10003[0] = 0;
      var10003[1] = 0;
      var10003[2] = -1;
      var10000[var10002] = var10003;
      NEIGHBORS = var10000;
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

   static final class ChunkSnapshot {
      private final int chunkX;
      private final int chunkZ;
      private final int minY;
      private final int maxY;
      private final byte[] blocks;
      private final byte[] blockLight;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      ChunkSnapshot(int chunkX, int chunkZ, int minY, int maxY, byte[] blocks, byte[] blockLight) {
         if (minYx <= maxYx && blocksx.length == SusChunkScan.expectedSize(minYx, maxYx) && blockLightx.length == SusChunkScan.expectedSize(minYx, maxYx)) {
            this.chunkX = chunkXx;
            this.chunkZ = chunkZx;
            this.minY = minYx;
            this.maxY = maxYx;
            this.blocks = (byte[])blocksx.clone();
            this.blockLight = (byte[])blockLightx.clone();
         } else {
            byte[] var10002 = new byte[29];
            var10002[0] = (byte)(115);
            var10002[1] = (byte)(-12);
            var10002[2] = (byte)(106);
            var10002[3] = (byte)(-65);
            var10002[4] = (byte)(47);
            var10002[5] = (byte)(79);
            var10002[6] = (byte)(-85);
            var10002[7] = (byte)(82);
            var10002[8] = (byte)(49);
            var10002[9] = (byte)(-54);
            var10002[10] = (byte)(-111);
            var10002[11] = (byte)(-104);
            var10002[12] = (byte)(112);
            var10002[13] = (byte)(-34);
            var10002[14] = (byte)(-108);
            var10002[15] = (byte)(100);
            var10002[16] = (byte)(11);
            var10002[17] = (byte)(58);
            var10002[18] = (byte)(-65);
            var10002[19] = (byte)(-58);
            var10002[20] = (byte)(-100);
            var10002[21] = (byte)(-94);
            var10002[22] = (byte)(-65);
            var10002[23] = (byte)(67);
            var10002[24] = (byte)(-16);
            var10002[25] = (byte)(96);
            var10002[26] = (byte)(-11);
            var10002[27] = (byte)(63);
            var10002[28] = (byte)(-72);
            throw new IllegalArgumentException($$v_s(var10002, 6542878112537104415L));
         }
      }

      int chunkX() {
         return this.chunkX;
      }

      int chunkZ() {
         return this.chunkZ;
      }

      int minY() {
         return this.minY;
      }

      int maxY() {
         return this.maxY;
      }

      byte blockAt(int x, int y, int z) {
         return this.inBounds(x, y, z) ? this.blocks[this.index(x, y, z)] : 0;
      }

      int blockLightAt(int x, int y, int z) {
         return this.inBounds(x, y, z) ? Byte.toUnsignedInt(this.blockLight[this.index(x, y, z)]) : 0;
      }

      boolean inBounds(int x, int y, int z) {
         return x >= 0 && x < (16) && z >= 0 && z < (16) && y >= this.minY && y <= this.maxY;
      }

      private int index(int x, int y, int z) {
         return ((y - this.minY) * (16) + z) * (16) + x;
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

   enum Classification {
      GROWN,
      UNGROWN,
      NONE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static SusChunkScan.Classification[] $values() {
         SusChunkScan.Classification[] var10000 = new SusChunkScan.Classification[3];
         var10000[0] = GROWN;
         var10000[1] = UNGROWN;
         var10000[2] = NONE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(127);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-119);
         var10002[4] = (byte)(26);
         GROWN = new SusChunkScan.Classification();
         var10002 = new byte[7];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-44);
         var10002[2] = (byte)(91);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(27);
         var10002[5] = (byte)(-75);
         var10002[6] = (byte)(59);
         UNGROWN = new SusChunkScan.Classification();
         var10002 = new byte[4];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         NONE = new SusChunkScan.Classification();
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

   private record Point(int x, int y, int z) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   record Result(long chunkKey, int hits, int visibleBudding, Set<class_2338> clusterPositions, SusChunkScan.Classification classification) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Result(long chunkKey, int hits, int visibleBudding, Set clusterPositions, SusChunkScan.Classification classification) {
         clusterPositions = Set.copyOf(clusterPositions);
         this.chunkKey = chunkKey;
         this.hits = hits;
         this.visibleBudding = visibleBudding;
         this.clusterPositions = clusterPositions;
         this.classification = classification;
      }
   }

   private record Sphere(double x, double y, double z, double radius) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record StandardResult(boolean hasCenter, boolean grown, int visibleBudding, Set<class_2338> positions) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
