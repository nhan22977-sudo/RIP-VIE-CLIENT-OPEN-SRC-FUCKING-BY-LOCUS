package me.drake.vieclient.utils.entity;

import java.nio.charset.StandardCharsets;

public enum Target {
   Feet,
   Body,
   Head;

   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   // $VF: synthetic method
   private static Target[] $values() {
      Target[] var10000 = new Target[3];
      var10000[0] = Feet;
      var10000[1] = Body;
      var10000[2] = Head;
      return var10000;
   }

   // $VF: Failed to inline enum fields
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static {
      byte[] var10002 = new byte[4];
      var10002[0] = (byte)(126);
      var10002[1] = (byte)(-1);
      var10002[2] = (byte)(121);
      var10002[3] = (byte)(-86);
      Feet = new Target();
      var10002 = new byte[4];
      var10002[0] = (byte)(122);
      var10002[1] = (byte)(-11);
      var10002[2] = (byte)(120);
      var10002[3] = (byte)(-89);
      Body = new Target();
      var10002 = new byte[4];
      var10002[0] = (byte)(112);
      var10002[1] = (byte)(-1);
      var10002[2] = (byte)(125);
      var10002[3] = (byte)(-70);
      Head = new Target();
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
