package me.drake.vieclient.modules.donut.chunkprobe;

import java.nio.charset.StandardCharsets;

public enum Confidence {
   NONE,
   POSSIBLE,
   HIGH;

   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   // $VF: synthetic method
   private static Confidence[] $values() {
      Confidence[] var10000 = new Confidence[3];
      var10000[0] = NONE;
      var10000[1] = POSSIBLE;
      var10000[2] = HIGH;
      return var10000;
   }

   // $VF: Failed to inline enum fields
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static {
      byte[] var10002 = new byte[4];
      var10002[0] = (byte)(118);
      var10002[1] = (byte)(-43);
      var10002[2] = (byte)(82);
      var10002[3] = (byte)(-101);
      NONE = new Confidence();
      var10002 = new byte[8];
      var10002[0] = (byte)(104);
      var10002[1] = (byte)(-43);
      var10002[2] = (byte)(79);
      var10002[3] = (byte)(-115);
      var10002[4] = (byte)(-6);
      var10002[5] = (byte)(123);
      var10002[6] = (byte)(-126);
      var10002[7] = (byte)(-118);
      POSSIBLE = new Confidence();
      var10002 = new byte[4];
      var10002[0] = (byte)(112);
      var10002[1] = (byte)(-45);
      var10002[2] = (byte)(91);
      var10002[3] = (byte)(-106);
      HIGH = new Confidence();
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
