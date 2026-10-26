package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;

public enum RenderMode {
   SHEET,
   PILLAR,
   BOTH;

   private final String label;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   RenderMode(String label) {
      this.label = labelx;
   }

   public String label() {
      return this.label;
   }

   // $VF: synthetic method
   private static RenderMode[] $values() {
      RenderMode[] var10000 = new RenderMode[3];
      var10000[0] = SHEET;
      var10000[1] = PILLAR;
      var10000[2] = BOTH;
      return var10000;
   }

   // $VF: Failed to inline enum fields
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static {
      byte[] var10002 = new byte[5];
      var10002[0] = (byte)(110);
      var10002[1] = (byte)(-46);
      var10002[2] = (byte)(89);
      var10002[3] = (byte)(-101);
      var10002[4] = (byte)(-56);
      String var0 = $$v_s(var10002, -7272503997482414056L);
      byte[] var10004 = new byte[5];
      var10004[0] = (byte)(110);
      var10004[1] = (byte)(-14);
      var10004[2] = (byte)(121);
      var10004[3] = (byte)(-69);
      var10004[4] = (byte)(-24);
      SHEET = new RenderMode($$v_s(var10004, -7272503997482414056L));
      var10002 = new byte[6];
      var10002[0] = (byte)(109);
      var10002[1] = (byte)(-45);
      var10002[2] = (byte)(80);
      var10002[3] = (byte)(-110);
      var10002[4] = (byte)(-35);
      var10002[5] = (byte)(108);
      String var2 = $$v_s(var10002, -7272503997482414056L);
      var10004 = new byte[6];
      var10004[0] = (byte)(109);
      var10004[1] = (byte)(-13);
      var10004[2] = (byte)(112);
      var10004[3] = (byte)(-78);
      var10004[4] = (byte)(-3);
      var10004[5] = (byte)(76);
      PILLAR = new RenderMode($$v_s(var10004, -7272503997482414056L));
      var10002 = new byte[4];
      var10002[0] = (byte)(127);
      var10002[1] = (byte)(-43);
      var10002[2] = (byte)(72);
      var10002[3] = (byte)(-106);
      String var4 = $$v_s(var10002, -7272503997482414056L);
      int var10003 = 2;
      var10004 = new byte[4];
      var10004[0] = (byte)(127);
      var10004[1] = (byte)(-11);
      var10004[2] = (byte)(104);
      var10004[3] = (byte)(-74);
      BOTH = new RenderMode($$v_s(var10004, -7272503997482414056L));
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
