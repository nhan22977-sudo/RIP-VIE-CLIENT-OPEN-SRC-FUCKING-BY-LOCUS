package me.drake.vieclient.modules.donut.chunkdebug;

import java.nio.charset.StandardCharsets;

public enum LightTrack {
   SKY,
   BLOCK;

   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   // $VF: synthetic method
   private static LightTrack[] $values() {
      LightTrack[] var10000 = new LightTrack[2];
      var10000[0] = SKY;
      var10000[1] = BLOCK;
      return var10000;
   }

   // $VF: Failed to inline enum fields
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static {
      byte[] var10002 = new byte[3];
      var10002[0] = (byte)(106);
      var10002[1] = (byte)(-47);
      var10002[2] = (byte)(69);
      SKY = new LightTrack();
      var10002 = new byte[5];
      var10002[0] = (byte)(123);
      var10002[1] = (byte)(-42);
      var10002[2] = (byte)(83);
      var10002[3] = (byte)(-99);
      var10002[4] = (byte)(48);
      BLOCK = new LightTrack();
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
