package me.drake.vieclient.modules.donut.chunkprobe;

public final class ChunkKey {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private ChunkKey() {
   }

   public static long pack(int x, int z) {
      return x & 4294967295L | (z & 4294967295L) << (32);
   }

   public static int unpackX(long key) {
      return (int)key;
   }

   public static int unpackZ(long key) {
      return (int)(key >> (32));
   }
}
