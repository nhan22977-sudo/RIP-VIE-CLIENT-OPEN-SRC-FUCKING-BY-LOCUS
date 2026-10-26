package me.drake.vieclient.modules.donut.chunkdebug;

import java.nio.charset.StandardCharsets;

public enum SectionStatus {
   NORMAL,
   EMPTY_MASK,
   ZEROED_PRESENT,
   UNIFORM_MAX,
   UNKNOWN;

   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   // $VF: synthetic method
   private static SectionStatus[] $values() {
      SectionStatus[] var10000 = new SectionStatus[5];
      var10000[0] = NORMAL;
      var10000[1] = EMPTY_MASK;
      var10000[2] = ZEROED_PRESENT;
      var10000[3] = UNIFORM_MAX;
      var10000[4] = UNKNOWN;
      return var10000;
   }

   // $VF: Failed to inline enum fields
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static {
      byte[] var10002 = new byte[6];
      var10002[0] = (byte)(112);
      var10002[1] = (byte)(-43);
      var10002[2] = (byte)(78);
      var10002[3] = (byte)(-109);
      var10002[4] = (byte)(106);
      var10002[5] = (byte)(62);
      NORMAL = new SectionStatus();
      var10002 = new byte[10];
      var10002[0] = (byte)(123);
      var10002[1] = (byte)(-41);
      var10002[2] = (byte)(76);
      var10002[3] = (byte)(-118);
      var10002[4] = (byte)(114);
      var10002[5] = (byte)(45);
      var10002[6] = (byte)(91);
      var10002[7] = (byte)(24);
      var10002[8] = (byte)(5);
      var10002[9] = (byte)(-23);
      EMPTY_MASK = new SectionStatus();
      var10002 = new byte[14];
      var10002[0] = (byte)(100);
      var10002[1] = (byte)(-33);
      var10002[2] = (byte)(78);
      var10002[3] = (byte)(-111);
      var10002[4] = (byte)(110);
      var10002[5] = (byte)(54);
      var10002[6] = (byte)(73);
      var10002[7] = (byte)(9);
      var10002[8] = (byte)(4);
      var10002[9] = (byte)(-25);
      var10002[10] = (byte)(-73);
      var10002[11] = (byte)(-77);
      var10002[12] = (byte)(61);
      var10002[13] = (byte)(-2);
      ZEROED_PRESENT = new SectionStatus();
      var10002 = new byte[11];
      var10002[0] = (byte)(107);
      var10002[1] = (byte)(-44);
      var10002[2] = (byte)(85);
      var10002[3] = (byte)(-104);
      var10002[4] = (byte)(100);
      var10002[5] = (byte)(32);
      var10002[6] = (byte)(91);
      var10002[7] = (byte)(6);
      var10002[8] = (byte)(27);
      var10002[9] = (byte)(-29);
      var10002[10] = (byte)(-68);
      UNIFORM_MAX = new SectionStatus();
      var10002 = new byte[7];
      var10002[0] = (byte)(107);
      var10002[1] = (byte)(-44);
      var10002[2] = (byte)(87);
      var10002[3] = (byte)(-112);
      var10002[4] = (byte)(100);
      var10002[5] = (byte)(37);
      var10002[6] = (byte)(88);
      UNKNOWN = new SectionStatus();
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
