package me.drake.vieclient.modules.client.hud;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class RegionGrid {
   public static final double WORLD_BORDER = 225000.0;
   public static final double REGION_SIZE = 50000.0;
   public static final int GRID_N = 9;
   public static final int[][] GRID = new int[][]{
      {82, 100, 101, 102, 103, 104, 105, 106, 91},
      {83, 44, 75, 42, 41, 40, 39, 38, 92},
      {84, 45, 14, 13, 12, 11, 10, 37, 93},
      {85, 46, 74, 3, 2, 1, 25, 36, 94},
      {86, 47, 72, 71, 5, 4, 24, 35, 95},
      {87, 51, 17, 9, 8, 7, 23, 34, 96},
      {88, 54, 18, 61, 62, 21, 22, 33, 97},
      {89, 26, 27, 28, 29, 30, 59, 32, 98},
      {90, 107, 108, 109, 110, 111, 112, 113, 99}
   };
   private static final Map NAMES = new HashMap();
   private static final Color[][] CELL_COLOR;
   private static final Color WARZONE_RED;
   private static final Set RED_REGIONS;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private RegionGrid() {
   }

   public static int gridX(double x) {
      return (int)Math.floor((x + 225000.0) / 50000.0);
   }

   public static int gridZ(double z) {
      return (int)Math.floor((z + 225000.0) / 50000.0);
   }

   public static boolean inGrid(int gx, int gz) {
      return gx >= 0 && gx < (9) && gz >= 0 && gz < (9);
   }

   public static int regionAt(double x, double z) {
      int var4 = gridX(x);
      int var5 = gridZ(z);
      return inGrid(var4, var5) ? GRID[var5][var4] : -1;
   }

   public static String nameOf(int regionId) {
      return (String)NAMES.get(regionId);
   }

   public static Color colorAt(int gx, int gz) {
      int var2 = GRID[gz][gx];
      return RED_REGIONS.contains(var2) ? WARZONE_RED : CELL_COLOR[gz][gx];
   }

   public static boolean isRed(int regionId) {
      return RED_REGIONS.contains(regionId);
   }

   public static Color cellColor(String region) {
      if (region == null) {
         return new Color(55, 58, 72, 145);
      }

      return switch (region) {
         case "NA East" -> new Color(85, 195, 250, 220);
         case "NA West" -> new Color(45, 125, 235, 220);
         case "EU Central" -> new Color(155, 215, 75, 220);
         case "EU West" -> new Color(30, 185, 100, 220);
         case "Asia" -> new Color(245, 195, 45, 220);
         case "Oceania" -> new Color(245, 120, 25, 220);
         default -> new Color(55, 58, 72, 145);
      };
   }

   static {
      for (int var3 : new int[]{1, 2, 4, 5, 24, 25, 36, 35, 94, 95, 96, 93, 37, 10, 11, 12, 41, 40, 39, 38, 92, 91, 106, 105, 104, 103}) {
         NAMES.put(var3, "NA East");
      }

      for (int var21 : new int[]{100, 101, 102, 44, 75, 42, 45, 14, 13, 3}) {
         NAMES.put(var21, "NA West");
      }

      for (int var22 : new int[]{82, 83, 84, 85, 46, 74}) {
         NAMES.put(var22, "Oceania");
      }

      for (int var23 : new int[]{86, 47, 72, 71, 87, 88}) {
         NAMES.put(var23, "Asia");
      }

      for (int var24 : new int[]{89, 90, 9, 8, 7, 23, 24, 34, 61, 62, 21, 22, 33, 97, 27, 28, 29, 30, 59, 32, 98, 99}) {
         NAMES.put(var24, "EU Central");
      }

      for (int var25 : new int[]{51, 17, 54, 18, 26, 107, 108, 109, 110, 111, 112, 113}) {
         NAMES.put(var25, "EU West");
      }

      CELL_COLOR = new Color[9][9];

      for (int var9 = 0; var9 < 9; var9++) {
         for (int var15 = 0; var15 < 9; var15++) {
            CELL_COLOR[var9][var15] = cellColor((String)NAMES.get(GRID[var9][var15]));
         }
      }

      WARZONE_RED = new Color(235, 65, 65, 220);
      RED_REGIONS = Set.of(83, 86, 90, 99, 88, 89, 98, 111, 112, 113, 4, 5);
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
