package me.drake.vieclient.chunkfinder;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2919;
import net.minecraft.class_3532;
import net.minecraft.class_6677;

public final class SeedSimulationEngine {
   private static final int CACHE_MIN_Y = -64;
   private static final int CACHE_MAX_Y = 192;
   private static final int CACHE_HEIGHT = 257;
   private static final int CHUNK_SIZE = 16;
   private static final int CELLS_PER_CHUNK = 65792;
   private static final int UNDERGROUND_ORES_STEP_ORDINAL = 6;
   private static final int SOURCE_CHUNK_RADIUS = 2;
   private static final int IDX_ORE_DIRT = 0;
   private static final int IDX_ORE_GRAVEL = 1;
   private static final int IDX_ORE_GRANITE_UPPER = 2;
   private static final int IDX_ORE_GRANITE_LOWER = 3;
   private static final int IDX_ORE_DIORITE_UPPER = 4;
   private static final int IDX_ORE_DIORITE_LOWER = 5;
   private static final int IDX_ORE_ANDESITE_UPPER = 6;
   private static final int IDX_ORE_ANDESITE_LOWER = 7;
   private static final int IDX_ORE_TUFF = 8;
   public static final byte CODE_OTHER = 0;
   public static final byte CODE_STONE = 1;
   public static final byte CODE_GRANITE = 2;
   public static final byte CODE_DIORITE = 3;
   public static final byte CODE_ANDESITE = 4;
   public static final byte CODE_TUFF = 5;
   public static final byte CODE_DEEPSLATE = 6;
   public static final byte CODE_DIRT = 7;
   public static final byte CODE_GRAVEL = 8;
   private static final SeedSimulationEngine.OreDef[] ORE_FEATURES;
   private final long seed;
   private final Map chunkCache = Collections.synchronizedMap(new LinkedHashMap(256, 0.75F, true) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      @Override
      protected boolean removeEldestEntry(Entry eldest) {
         return this.size() > (512);
      }
   });
   private final Map sourceContributionCache = Collections.synchronizedMap(new LinkedHashMap(512, 0.75F, true) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      @Override
      protected boolean removeEldestEntry(Entry eldest) {
         return this.size() > (2048);
      }
   });
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SeedSimulationEngine(long seed) {
      this.seed = seedx;
   }

   public long getSeed() {
      return this.seed;
   }

   public class_2248 expectedBaseStoneAt(int x, int y, int z) {
      if (y >= (-64) && y <= (192)) {
         int var4 = Math.floorDiv(x, 16);
         int var5 = Math.floorDiv(z, 16);
         long var6 = packChunk(var4, var5);
         SeedSimulationEngine.ChunkState var8 = (SeedSimulationEngine.ChunkState)this.chunkCache.get(var6);
         if (var8 == null) {
            var8 = this.buildChunkState(var4, var5);
            this.chunkCache.put(var6, var8);
         }

         int var9 = Math.floorMod(x, 16);
         int var10 = Math.floorMod(z, 16);
         byte var11 = var8.get(var9, y, var10);
         return codeToBlock(var11);
      } else {
         return defaultStoneAtY(y);
      }
   }

   public class_2248 expectedLayerBlockAt(int x, int y, int z) {
      return this.expectedBaseStoneAt(x, y, z);
   }

   public class_2248 expectedSecondaryStoneAt(int x, int y, int z) {
      class_2248 var4 = this.expectedBaseStoneAt(x, y, z);
      return var4 != class_2246.field_10474 && var4 != class_2246.field_10508 && var4 != class_2246.field_10115 && var4 != class_2246.field_27165 ? null : var4;
   }

   public class_2680 expectedStateAt(int x, int y, int z) {
      return this.expectedBaseStoneAt(x, y, z).method_9564();
   }

   public class_2680 sampleWithVanillaNoiseTemplate(int x, int y, int z) {
      return this.expectedStateAt(x, y, z);
   }

   public static byte blockToCode(class_2248 block) {
      if (block == null) {
         return 0;
      } else if (block == class_2246.field_10340) {
         return 1;
      } else if (block == class_2246.field_10474) {
         return (byte)(2);
      } else if (block == class_2246.field_10508) {
         return (byte)(3);
      } else if (block == class_2246.field_10115) {
         return (byte)(4);
      } else if (block == class_2246.field_27165) {
         return (byte)(5);
      } else if (block == class_2246.field_28888) {
         return (byte)(6);
      } else if (block == class_2246.field_10566) {
         return (byte)(7);
      } else {
         return (byte)(block == class_2246.field_10255 ? 8 : 0);
      }
   }

   private SeedSimulationEngine.ChunkState buildChunkState(int targetChunkX, int targetChunkZ) {
      SeedSimulationEngine.ChunkState var3 = new SeedSimulationEngine.ChunkState(targetChunkX, targetChunkZ);
      var3.fillBase(defaultStoneRow());

      for (int var4 = targetChunkX - (2); var4 <= targetChunkX + (2); var4++) {
         for (int var5 = targetChunkZ - (2); var5 <= targetChunkZ + (2); var5++) {
            long var6 = packChunk(var4, var5);
            SeedSimulationEngine.SourceContribution var8 = (SeedSimulationEngine.SourceContribution)this.sourceContributionCache.get(var6);
            if (var8 == null) {
               var8 = this.buildSourceContribution(var4, var5);
               this.sourceContributionCache.put(var6, var8);
            }

            var8.applyToTarget(var3);
         }
      }

      return var3;
   }

   private SeedSimulationEngine.SourceContribution buildSourceContribution(int sourceChunkX, int sourceChunkZ) {
      class_2919 var3 = new class_2919(new class_6677(0L));
      long var4 = var3.method_12661(this.seed, sourceChunkX << (4), sourceChunkZ << (4));
      HashMap var6 = new HashMap();
      int var7 = sourceChunkX << (4);
      int var8 = sourceChunkZ << (4);

      for (SeedSimulationEngine.OreDef var12 : ORE_FEATURES) {
         var3.method_12664(var4, var12.decoratorIndex, 6);
         int var13;
         if (var12.rarityBased) {
            var13 = var3.method_43057() < 1.0F / Math.max(1, var12.countOrRarity) ? 1 : 0;
         } else {
            var13 = Math.max(0, var12.countOrRarity);
         }

         for (int var14 = 0; var14 < var13; var14++) {
            int var15 = var7 + var3.method_43048(16);
            int var16 = var8 + var3.method_43048(16);
            int var17 = var3.method_43048(var12.maxY - var12.minY + 1) + var12.minY;
            this.placeOreVein(var3, var6, var12, var15, var17, var16);
         }
      }

      return new SeedSimulationEngine.SourceContribution(var6);
   }

   private void placeOreVein(class_2919 random, Map out, SeedSimulationEngine.OreDef def, int originX, int originY, int originZ) {
      int var7 = def.size;
      if (var7 > 0) {
         float var8 = random.method_43057() * (float) Math.PI;
         float var9 = var7 / 8.0F;
         double var10 = originX + Math.sin(var8) * var9;
         double var12 = originX - Math.sin(var8) * var9;
         double var14 = originZ + Math.cos(var8) * var9;
         double var16 = originZ - Math.cos(var8) * var9;
         double var18 = originY + random.method_43048(3) - 2;
         double var20 = originY + random.method_43048(3) - 2;
         double[] var22 = new double[var7];
         double[] var23 = new double[var7];
         double[] var24 = new double[var7];
         double[] var25 = new double[var7];

         for (int var26 = 0; var26 < var7; var26++) {
            float var27 = (float)var26 / var7;
            var22[var26] = class_3532.method_16436(var27, var10, var12);
            var23[var26] = class_3532.method_16436(var27, var18, var20);
            var24[var26] = class_3532.method_16436(var27, var14, var16);
            var25[var26] = ((class_3532.method_15374((float) Math.PI * var27) + 1.0) * random.method_43058() * var7 / 16.0 + 1.0) / 2.0;
         }

         for (int var53 = 0; var53 < var7 - 1; var53++) {
            if (!(var25[var53] <= 0.0)) {
               for (int var55 = var53 + 1; var55 < var7; var55++) {
                  if (!(var25[var55] <= 0.0)) {
                     double var28 = var22[var53] - var22[var55];
                     double var30 = var23[var53] - var23[var55];
                     double var32 = var24[var53] - var24[var55];
                     double var34 = var25[var53] - var25[var55];
                     if (!(var34 * var34 <= var28 * var28 + var30 * var30 + var32 * var32)) {
                        if (var34 > 0.0) {
                           var25[var55] = -1.0;
                        } else {
                           var25[var53] = -1.0;
                        }
                     }
                  }
               }
            }
         }

         for (int var54 = 0; var54 < var7; var54++) {
            double var56 = var25[var54];
            if (!(var56 <= 0.0)) {
               int var29 = class_3532.method_15357(var22[var54] - var56);
               int var57 = Math.max(class_3532.method_15357(var23[var54] - var56), -64);
               int var31 = class_3532.method_15357(var24[var54] - var56);
               int var58 = class_3532.method_15357(var22[var54] + var56);
               int var33 = Math.min(class_3532.method_15357(var23[var54] + var56), 192);
               int var59 = class_3532.method_15357(var24[var54] + var56);

               for (int var35 = var29; var35 <= var58; var35++) {
                  double var36 = (var35 + 0.5 - var22[var54]) / var56;
                  double var38 = var36 * var36;
                  if (!(var38 >= 1.0)) {
                     for (int var40 = var57; var40 <= var33; var40++) {
                        double var41 = (var40 + 0.5 - var23[var54]) / var56;
                        double var43 = var41 * var41;
                        if (!(var38 + var43 >= 1.0)) {
                           for (int var45 = var31; var45 <= var59; var45++) {
                              double var46 = (var45 + 0.5 - var24[var54]) / var56;
                              double var48 = var38 + var43 + var46 * var46;
                              if (!(var48 >= 1.0)) {
                                 long var50 = class_2338.method_10064(var35, var40, var45);
                                 byte var52 = out.getOrDefault(var50, blockToCode(defaultStoneAtY(var40)));
                                 if (isBaseStoneOverworld(var52)
                                    && (!(def.discardChanceOnAirExposure > 0.0) || !(random.method_43057() < def.discardChanceOnAirExposure))) {
                                    out.put(var50, def.resultCode);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean isBaseStoneOverworld(byte code) {
      return code == 1
         || code == (2)
         || code == (3)
         || code == (4)
         || code == (5)
         || code == (6);
   }

   private static class_2248 defaultStoneAtY(int y) {
      if (y < 0) {
         return class_2246.field_28888;
      } else if (y > (8)) {
         return class_2246.field_10340;
      } else {
         return y <= (4) ? class_2246.field_28888 : class_2246.field_10340;
      }
   }

   private static byte[] defaultStoneRow() {
      byte[] var0 = new byte[257];

      for (int var1 = -64; var1 <= (192); var1++) {
         var0[var1 - (-64)] = blockToCode(defaultStoneAtY(var1));
      }

      return var0;
   }

   private static long packChunk(int chunkX, int chunkZ) {
      return (long)chunkX << (32) ^ chunkZ & 4294967295L;
   }

   private static class_2248 codeToBlock(byte code) {
      return switch (code) {
         case 2 -> class_2246.field_10474;
         case 3 -> class_2246.field_10508;
         case 4 -> class_2246.field_10115;
         case 5 -> class_2246.field_27165;
         case 6 -> class_2246.field_28888;
         case 7 -> class_2246.field_10566;
         case 8 -> class_2246.field_10255;
         default -> class_2246.field_10340;
      };
   }

   static {
      SeedSimulationEngine.OreDef[] var10000 = new SeedSimulationEngine.OreDef[9];
      var10000[0] = new SeedSimulationEngine.OreDef((byte)(7), 33, false, 7, 0, 160, 0, 0.0);
      var10000[1] = new SeedSimulationEngine.OreDef(
         (byte)(8), 33, false, 14, -64, 192, 1, 0.0
      );
      var10000[2] = new SeedSimulationEngine.OreDef(
         (byte)(2), 64, true, 6, 64, 128, 2, 0.0
      );
      var10000[3] = new SeedSimulationEngine.OreDef(
         (byte)(2), 64, false, 2, 0, 60, 3, 0.0
      );
      var10000[4] = new SeedSimulationEngine.OreDef(
         (byte)(3), 64, true, 6, 64, 128, 4, 0.0
      );
      var10000[5] = new SeedSimulationEngine.OreDef(
         (byte)(3), 64, false, 2, 0, 60, 5, 0.0
      );
      var10000[6] = new SeedSimulationEngine.OreDef(
         (byte)(4), 64, true, 6, 64, 128, 6, 0.0
      );
      var10000[7] = new SeedSimulationEngine.OreDef(
         (byte)(4), 64, false, 2, 0, 60, 7, 0.0
      );
      var10000[8] = new SeedSimulationEngine.OreDef(
         (byte)(5), 64, false, 2, -64, 0, 8, 0.0
      );
      ORE_FEATURES = var10000;
   }

   private static final class ChunkState {
      private final int targetChunkX;
      private final int targetChunkZ;
      private final byte[] cells = new byte[65792];
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private ChunkState(int targetChunkX, int targetChunkZ) {
         this.targetChunkX = targetChunkXx;
         this.targetChunkZ = targetChunkZx;
      }

      private void fillBase(byte[] baseRow) {
         int var2 = 0;

         for (int var3 = 0; var3 < (257); var3++) {
            byte var4 = baseRow[var3];

            for (int var5 = 0; var5 < (16); var5++) {
               for (int var6 = 0; var6 < (16); var6++) {
                  this.cells[var2++] = var4;
               }
            }
         }
      }

      private byte get(int localX, int y, int localZ) {
         if (y >= (-64) && y <= (192)) {
            int var4 = this.index(localX, y, localZ);
            return var4 < 0 ? 0 : this.cells[var4];
         } else {
            return 0;
         }
      }

      private byte getWorld(int worldX, int y, int worldZ) {
         int var4 = worldX - (this.targetChunkX << (4));
         int var5 = worldZ - (this.targetChunkZ << (4));
         return var4 >= 0 && var4 <= (15) && var5 >= 0 && var5 <= (15) ? this.get(var4, y, var5) : 0;
      }

      private void setWorld(int worldX, int y, int worldZ, byte value) {
         int var5 = worldX - (this.targetChunkX << (4));
         int var6 = worldZ - (this.targetChunkZ << (4));
         if (var5 >= 0 && var5 <= (15) && var6 >= 0 && var6 <= (15)) {
            int var7 = this.index(var5, y, var6);
            if (var7 >= 0) {
               this.cells[var7] = value;
            }
         }
      }

      private int index(int localX, int y, int localZ) {
         if (localX < 0 || localX > (15) || localZ < 0 || localZ > (15)) {
            return -1;
         } else if (y >= (-64) && y <= (192)) {
            int var4 = y - (-64);
            return var4 * (16) * (16) + localX * (16) + localZ;
         } else {
            return -1;
         }
      }
   }

   private record OreDef(
      byte resultCode, int size, boolean rarityBased, int countOrRarity, int minY, int maxY, int decoratorIndex, double discardChanceOnAirExposure
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private static final class SourceContribution {
      private final long[] positions;
      private final byte[] values;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private SourceContribution(Map cells) {
         this.positions = new long[cells.size()];
         this.values = new byte[cells.size()];
         int var2 = 0;

         for (Entry var4 : cells.entrySet()) {
            this.positions[var2] = (Long)var4.getKey();
            this.values[var2] = (Byte)var4.getValue();
            var2++;
         }
      }

      private void applyToTarget(SeedSimulationEngine.ChunkState target) {
         int var2 = target.targetChunkX << (4);
         int var3 = var2 + (15);
         int var4 = target.targetChunkZ << (4);
         int var5 = var4 + (15);

         for (int var6 = 0; var6 < this.positions.length; var6++) {
            long var7 = this.positions[var6];
            int var9 = class_2338.method_10061(var7);
            int var10 = class_2338.method_10071(var7);
            int var11 = class_2338.method_10083(var7);
            if (var9 >= var2 && var9 <= var3 && var11 >= var4 && var11 <= var5) {
               target.setWorld(var9, var10, var11, this.values[var6]);
            }
         }
      }
   }
}
