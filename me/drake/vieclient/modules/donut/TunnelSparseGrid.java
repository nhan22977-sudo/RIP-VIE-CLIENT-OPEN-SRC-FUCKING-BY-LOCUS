package me.drake.vieclient.modules.donut;

final class TunnelSparseGrid {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private TunnelSparseGrid() {
   }

   static int progressAlong(int anchorX, int anchorZ, int x, int z, int dx, int dz) {
      int var6 = dx * dx + dz * dz;
      if (var6 == 0) {
         return 0;
      }

      int var7 = (x - anchorX) * dx + (z - anchorZ) * dz;
      return Math.floorDiv(var7, var6);
   }

   static boolean reached(int anchorX, int anchorZ, int x, int z, int dx, int dz, int distance) {
      return distance <= 0 ? true : progressAlong(anchorX, anchorZ, x, z, dx, dz) >= distance;
   }
}
