package me.drake.vieclient.modules.client.hud;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;

public final class NewRegionGrid {
   public static final int GRID = 36;
   public static final double WORLD_BORDER = 225000.0;
   public static final double MAP_SIZE = 450000.0;
   public static final double CELL_SIZE = 12500.0;
   public static final Map REGIONS = new LinkedHashMap();
   public static final String[][] CELLS = new String[36][36];
   public static final int[][] SHARD_IDS = new int[36][36];
   public static final List SHARDS = new ArrayList();
   public static final Color COLOR_EUROPE;
   public static final Color COLOR_EU_CENTRAL;
   public static final Color COLOR_EU_WEST;
   public static final Color COLOR_ASIA;
   public static final Color COLOR_OCEANIA;
   public static final Color COLOR_NA_WEST;
   public static final Color COLOR_NA_EAST;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private NewRegionGrid() {
   }

   public static Color colorOf(String region) {
      if (region == null) {
         return new Color(55, 58, 72, 145);
      }

      return switch (region) {
         case "Europe" -> COLOR_EUROPE;
         case "EU Central" -> COLOR_EU_CENTRAL;
         case "EU West" -> COLOR_EU_WEST;
         case "Asia" -> COLOR_ASIA;
         case "Oceania" -> COLOR_OCEANIA;
         case "NA West" -> COLOR_NA_WEST;
         case "NA East" -> COLOR_NA_EAST;
         default -> new Color(55, 58, 72, 145);
      };
   }

   public static int gridCol(double x) {
      return (int)Math.floor((x + 225000.0) / 12500.0);
   }

   public static int gridRow(double z) {
      return (int)Math.floor((z + 225000.0) / 12500.0);
   }

   public static boolean inGrid(int col, int row) {
      return col >= 0 && col < (36) && row >= 0 && row < (36);
   }

   public static String getRegion(int col, int row) {
      return !inGrid(col, row) ? null : CELLS[row][col];
   }

   public static NewRegionGrid.Shard getShard(int col, int row) {
      if (!inGrid(col, row)) {
         return null;
      }

      int var2 = SHARD_IDS[row][col];
      return var2 >= 0 && var2 < SHARDS.size() ? (NewRegionGrid.Shard)SHARDS.get(var2) : null;
   }

   public static NewRegionGrid.Shard shardAt(double x, double z) {
      int var4 = gridCol(x);
      int var5 = gridRow(z);
      return getShard(var4, var5);
   }

   public static String regionAt(double x, double z) {
      int var4 = gridCol(x);
      int var5 = gridRow(z);
      return getRegion(var4, var5);
   }

   public static boolean differs(int r1, int c1, int r2, int c2, boolean regionLevel) {
      boolean var5 = Objects.equals(CELLS[r1][c1], CELLS[r2][c2]);
      return regionLevel ? !var5 : var5 && SHARD_IDS[r1][c1] != SHARD_IDS[r2][c2];
   }

   static {
      REGIONS.put(
         "NA West",
         new int[][]{
            {4, 0, 2, 4},
            {6, 0, 2, 4},
            {8, 0, 2, 4},
            {10, 0, 2, 4},
            {12, 0, 2, 4},
            {14, 0, 2, 4},
            {4, 4, 2, 4},
            {6, 4, 2, 4},
            {8, 4, 2, 4},
            {10, 4, 2, 4},
            {12, 4, 2, 4},
            {14, 4, 2, 4},
            {4, 8, 2, 4},
            {6, 8, 2, 4},
            {8, 8, 2, 4},
            {10, 8, 2, 4},
            {12, 8, 2, 4},
            {14, 8, 2, 4},
            {12, 12, 2, 4},
            {14, 12, 2, 4}
         }
      );
      REGIONS.put(
         "NA East",
         new int[][]{
            {16, 0, 2, 4},
            {18, 0, 2, 4},
            {20, 0, 2, 4},
            {22, 0, 2, 4},
            {24, 0, 4, 4},
            {28, 0, 2, 4},
            {30, 0, 2, 4},
            {32, 0, 2, 4},
            {34, 0, 2, 4},
            {16, 4, 2, 2},
            {18, 4, 2, 2},
            {20, 4, 2, 2},
            {22, 4, 2, 2},
            {24, 4, 2, 2},
            {26, 4, 2, 4},
            {28, 4, 2, 2},
            {30, 4, 2, 2},
            {32, 4, 2, 4},
            {34, 4, 2, 4},
            {16, 6, 2, 2},
            {18, 6, 2, 2},
            {20, 6, 2, 2},
            {22, 6, 2, 2},
            {24, 6, 2, 2},
            {28, 6, 2, 2},
            {30, 6, 2, 2},
            {16, 8, 2, 4},
            {18, 8, 2, 4},
            {20, 8, 4, 4},
            {24, 8, 2, 4},
            {26, 8, 2, 4},
            {28, 8, 2, 4},
            {30, 8, 2, 4},
            {32, 8, 2, 4},
            {34, 8, 2, 4},
            {16, 12, 2, 4},
            {18, 12, 2, 4},
            {20, 12, 4, 4},
            {24, 12, 2, 4},
            {26, 12, 2, 4},
            {28, 12, 2, 4},
            {30, 12, 2, 4},
            {32, 12, 2, 4},
            {34, 12, 2, 4},
            {16, 16, 4, 4},
            {20, 16, 2, 4},
            {22, 16, 2, 2},
            {24, 16, 4, 4},
            {28, 16, 2, 4},
            {30, 16, 2, 4},
            {32, 16, 2, 4},
            {34, 16, 2, 4},
            {22, 18, 2, 2},
            {32, 20, 2, 4},
            {34, 20, 2, 4},
            {32, 24, 2, 4},
            {34, 24, 2, 4}
         }
      );
      REGIONS.put(
         "Oceania",
         new int[][]{
            {0, 0, 2, 2},
            {2, 0, 2, 4},
            {0, 2, 2, 2},
            {0, 4, 2, 2},
            {2, 4, 2, 4},
            {0, 6, 2, 2},
            {0, 8, 2, 2},
            {2, 8, 2, 4},
            {0, 10, 2, 2},
            {0, 12, 2, 2},
            {2, 12, 2, 4},
            {4, 12, 2, 4},
            {6, 12, 2, 4},
            {8, 12, 2, 4},
            {10, 12, 2, 4},
            {0, 14, 2, 2}
         }
      );
      REGIONS.put(
         "Asia",
         new int[][]{
            {0, 16, 2, 4},
            {2, 16, 2, 4},
            {4, 16, 2, 4},
            {6, 16, 2, 4},
            {8, 16, 2, 2},
            {10, 16, 2, 2},
            {12, 16, 2, 2},
            {14, 16, 2, 2},
            {8, 18, 2, 2},
            {10, 18, 2, 2},
            {12, 18, 2, 2},
            {14, 18, 2, 2},
            {0, 20, 2, 4},
            {2, 20, 2, 4},
            {0, 24, 2, 4},
            {2, 24, 2, 4}
         }
      );
      REGIONS.put(
         "EU West",
         new int[][]{
            {4, 20, 2, 4},
            {6, 20, 2, 4},
            {8, 20, 2, 4},
            {10, 20, 2, 4},
            {4, 24, 2, 4},
            {6, 24, 2, 4},
            {8, 24, 2, 4},
            {10, 24, 2, 4},
            {4, 28, 2, 4},
            {6, 28, 2, 4},
            {4, 32, 2, 4},
            {6, 32, 2, 2},
            {8, 32, 2, 2},
            {10, 32, 2, 2},
            {12, 32, 2, 2},
            {14, 32, 2, 4},
            {16, 32, 2, 4},
            {18, 32, 2, 2},
            {20, 32, 2, 4},
            {22, 32, 2, 4},
            {24, 32, 2, 4},
            {26, 32, 2, 4},
            {6, 34, 2, 2},
            {8, 34, 2, 2},
            {10, 34, 2, 2},
            {12, 34, 2, 2},
            {18, 34, 2, 2}
         }
      );
      REGIONS.put(
         "EU Central",
         new int[][]{
            {12, 20, 2, 4},
            {14, 20, 2, 4},
            {16, 20, 2, 4},
            {18, 20, 2, 4},
            {20, 20, 2, 4},
            {22, 20, 2, 2},
            {26, 20, 2, 4},
            {28, 20, 2, 4},
            {30, 20, 2, 2},
            {22, 22, 2, 2},
            {24, 22, 1, 2},
            {25, 22, 1, 2},
            {12, 24, 2, 4},
            {14, 24, 2, 4},
            {16, 24, 2, 4},
            {18, 24, 2, 4},
            {20, 24, 2, 4},
            {22, 24, 2, 4},
            {24, 24, 2, 4},
            {28, 24, 2, 2},
            {30, 24, 2, 4},
            {26, 26, 2, 2},
            {0, 28, 4, 1},
            {8, 28, 2, 2},
            {10, 28, 2, 4},
            {12, 28, 2, 2},
            {14, 28, 2, 2},
            {16, 28, 2, 2},
            {18, 28, 2, 2},
            {20, 28, 2, 2},
            {22, 28, 2, 2},
            {24, 28, 2, 2},
            {26, 28, 2, 4},
            {28, 28, 2, 2},
            {30, 28, 2, 2},
            {32, 28, 2, 2},
            {0, 29, 4, 1},
            {0, 30, 2, 2},
            {2, 30, 2, 2},
            {16, 30, 2, 2},
            {18, 30, 2, 2},
            {22, 30, 2, 2},
            {24, 30, 2, 2},
            {32, 30, 2, 2},
            {34, 30, 2, 2},
            {0, 32, 2, 2},
            {28, 32, 2, 4},
            {32, 32, 2, 4},
            {0, 34, 4, 2},
            {30, 34, 2, 2},
            {34, 34, 2, 2},
            {26, 24, 2, 2}
         }
      );
      REGIONS.put(
         "Europe",
         new int[][]{
            {24, 20, 2, 2},
            {30, 22, 2, 2},
            {28, 26, 2, 2},
            {34, 28, 2, 2},
            {8, 30, 2, 2},
            {12, 30, 2, 2},
            {14, 30, 2, 2},
            {20, 30, 2, 2},
            {28, 30, 2, 2},
            {30, 30, 2, 2},
            {2, 32, 2, 2},
            {30, 32, 2, 2},
            {34, 32, 2, 2}
         }
      );

      for (int[] var3 : SHARD_IDS) {
         Arrays.fill(var3, -1);
      }

      for (Entry var17 : REGIONS.entrySet()) {
         String var18 = (String)var17.getKey();
         int[][] var19 = (int[][])((int[][])var17.getValue()).clone();
         Arrays.sort(var19, Comparator.<int[]>comparingInt(r -> r[1]).thenComparingInt(r -> r[0]));
         int var4 = 1;

         for (int[] var8 : var19) {
            int var9 = var8[0];
            int var10 = var8[1];
            int var11 = var8[2];
            int var12 = var8[3];
            if (var9 < 0 || var10 < 0 || var9 + var11 > 36 || var10 + var12 > 36) {
               throw new IllegalStateException("Shard out of bounds in " + var18 + ": " + var9 + "," + var10 + " " + var11 + "x" + var12);
            }

            int var13 = SHARDS.size();
            SHARDS.add(new NewRegionGrid.Shard(var18, var4++, var9, var10, var11, var12));

            for (int var14 = var10; var14 < var10 + var12; var14++) {
               for (int var15 = var9; var15 < var9 + var11; var15++) {
                  if (CELLS[var14][var15] != null) {
                     throw new IllegalStateException("Shards overlap at " + var15 + "," + var14 + " (" + CELLS[var14][var15] + " / " + var18 + ")");
                  }

                  CELLS[var14][var15] = var18;
                  SHARD_IDS[var14][var15] = var13;
               }
            }
         }
      }

      COLOR_EUROPE = new Color(218, 112, 214, 220);
      COLOR_EU_CENTRAL = new Color(146, 208, 80, 220);
      COLOR_EU_WEST = new Color(56, 118, 29, 220);
      COLOR_ASIA = new Color(255, 192, 0, 220);
      COLOR_OCEANIA = new Color(237, 125, 49, 220);
      COLOR_NA_WEST = new Color(68, 114, 196, 220);
      COLOR_NA_EAST = new Color(91, 155, 213, 220);
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

   public record Shard(String region, int number, int col, int row, int width, int height) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
