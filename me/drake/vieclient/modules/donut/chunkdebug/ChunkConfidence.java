package me.drake.vieclient.modules.donut.chunkdebug;

import java.nio.charset.StandardCharsets;

public enum ChunkConfidence {
   LOW,
   MEDIUM,
   HIGH;

   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static ChunkConfidence fromEvidence(boolean strong, boolean veryStrong, int confirmations) {
      if (!strong || !veryStrong && confirmations < (2)) {
         return strong ? MEDIUM : LOW;
      } else {
         return HIGH;
      }
   }

   public boolean isAlerting() {
      return this != LOW;
   }

   // $VF: synthetic method
   private static ChunkConfidence[] $values() {
      ChunkConfidence[] var10000 = new ChunkConfidence[3];
      var10000[0] = LOW;
      var10000[1] = MEDIUM;
      var10000[2] = HIGH;
      return var10000;
   }

   // $VF: Failed to inline enum fields
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static {
      byte[] var10002 = new byte[3];
      var10002[0] = (byte)(116);
      var10002[1] = (byte)(-43);
      var10002[2] = (byte)(75);
      LOW = new ChunkConfidence();
      var10002 = new byte[6];
      var10002[0] = (byte)(117);
      var10002[1] = (byte)(-33);
      var10002[2] = (byte)(88);
      var10002[3] = (byte)(-105);
      var10002[4] = (byte)(-98);
      var10002[5] = (byte)(115);
      MEDIUM = new ChunkConfidence();
      var10002 = new byte[4];
      var10002[0] = (byte)(112);
      var10002[1] = (byte)(-45);
      var10002[2] = (byte)(91);
      var10002[3] = (byte)(-106);
      HIGH = new ChunkConfidence();
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
