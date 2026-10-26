package me.drake.vieclient.modules.donut.profiler;

import java.nio.charset.StandardCharsets;

public enum FinderConfidence {
   POSSIBLE,
   CONFIRMED;

   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   // $VF: synthetic method
   private static FinderConfidence[] $values() {
      FinderConfidence[] var10000 = new FinderConfidence[2];
      var10000[0] = POSSIBLE;
      var10000[1] = CONFIRMED;
      return var10000;
   }

   // $VF: Failed to inline enum fields
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static {
      byte[] var10002 = new byte[8];
      var10002[0] = (byte)(105);
      var10002[1] = (byte)(-43);
      var10002[2] = (byte)(79);
      var10002[3] = (byte)(-115);
      var10002[4] = (byte)(-6);
      var10002[5] = (byte)(-31);
      var10002[6] = (byte)(-47);
      var10002[7] = (byte)(-9);
      POSSIBLE = new FinderConfidence();
      var10002 = new byte[9];
      var10002[0] = (byte)(122);
      var10002[1] = (byte)(-43);
      var10002[2] = (byte)(82);
      var10002[3] = (byte)(-104);
      var10002[4] = (byte)(-6);
      var10002[5] = (byte)(-15);
      var10002[6] = (byte)(-48);
      var10002[7] = (byte)(-9);
      var10002[8] = (byte)(21);
      CONFIRMED = new FinderConfidence();
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
