package me.drake.vieclient.util;

import java.util.HashMap;
import java.util.Map;

public final class ChunkScanMemo {
   private final Map scanned = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public boolean needsScan(long key, Object chunk) {
      return chunk != null && this.scanned.get(key) != chunk;
   }

   public void markScanned(long key, Object chunk) {
      if (chunk != null) {
         this.scanned.put(key, chunk);
      }
   }

   public void invalidate(long key) {
      this.scanned.remove(key);
   }

   public void clear() {
      this.scanned.clear();
   }

   public void retainWithin(int centerX, int centerZ, int radius) {
      this.scanned
         .keySet()
         .removeIf(
            key -> Math.abs((long)((int)key.longValue()) - centerX) > radius || Math.abs((long)((int)(key >>> (32))) - centerZ) > radius
         );
   }
}
