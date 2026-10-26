package me.drake.vieclient.modules.donut;

final class DebugRegionVisibility {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private DebugRegionVisibility() {
   }

   static boolean intersectsRenderWindow(int originX, int originZ, int size, int playerChunkX, int playerChunkZ, int radius) {
      int var6 = originX + size - 1;
      int var7 = originZ + size - 1;
      int var8 = playerChunkX - radius;
      int var9 = playerChunkX + radius;
      int var10 = playerChunkZ - radius;
      int var11 = playerChunkZ + radius;
      return var6 >= var8 && originX <= var9 && var7 >= var10 && originZ <= var11;
   }
}
