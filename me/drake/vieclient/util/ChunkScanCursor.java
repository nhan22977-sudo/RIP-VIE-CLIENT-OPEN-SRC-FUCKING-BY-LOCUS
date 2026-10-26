package me.drake.vieclient.util;

public final class ChunkScanCursor {
   private int index;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public long next(int centerX, int centerZ, int radius) {
      int var4 = radius * (2) + 1;
      int var5 = var4 * var4;
      if (this.index >= var5) {
         this.index = 0;
      }

      int var6 = centerX + this.index % var4 - radius;
      int var7 = centerZ + this.index / var4 - radius;
      this.index = (this.index + 1) % var5;
      return var6 & 4294967295L | (var7 & 4294967295L) << (32);
   }

   public void reset() {
      this.index = 0;
   }
}
