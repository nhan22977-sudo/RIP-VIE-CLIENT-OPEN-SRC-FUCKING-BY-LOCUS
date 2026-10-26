package me.drake.vieclient.core.i18n;

import java.nio.charset.StandardCharsets;

public enum Language {
   VIE,
   ENG;

   private final String code;
   private final String displayName;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   Language(String code, String displayName) {
      this.code = codex;
      this.displayName = displayNamex;
   }

   public String getCode() {
      return this.code;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public Language next() {
      return this == VIE ? ENG : VIE;
   }

   @Override
   public String toString() {
      return this.displayName;
   }

   // $VF: synthetic method
   private static Language[] $values() {
      Language[] var10000 = new Language[2];
      var10000[0] = VIE;
      var10000[1] = ENG;
      return var10000;
   }

   // $VF: Failed to inline enum fields
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static {
      byte[] var10002 = new byte[3];
      var10002[0] = (byte)(107);
      var10002[1] = (byte)(-45);
      var10002[2] = (byte)(89);
      String var0 = $$v_s(var10002, -3736022484232450024L);
      byte[] var10004 = new byte[3];
      var10004[0] = (byte)(107);
      var10004[1] = (byte)(-45);
      var10004[2] = (byte)(89);
      String var3 = $$v_s(var10004, -3736022484232450024L);
      byte[] var10005 = new byte[14];
      var10005[0] = (byte)(105);
      var10005[1] = (byte)(-13);
      var10005[2] = (byte)(-3);
      var10005[3] = (byte)(100);
      var10005[4] = (byte)(94);
      var10005[5] = (byte)(75);
      var10005[6] = (byte)(66);
      var10005[7] = (byte)(-60);
      var10005[8] = (byte)(3);
      var10005[9] = (byte)(-53);
      var10005[10] = (byte)(5);
      var10005[11] = (byte)(77);
      var10005[12] = (byte)(62);
      var10005[13] = (byte)(-119);
      VIE = new Language(var3, $$v_s(var10005, -3736022484232450024L));
      var10002 = new byte[3];
      var10002[0] = (byte)(120);
      var10002[1] = (byte)(-44);
      var10002[2] = (byte)(91);
      String var2 = $$v_s(var10002, -3736022484232450024L);
      var10004 = new byte[3];
      var10004[0] = (byte)(120);
      var10004[1] = (byte)(-44);
      var10004[2] = (byte)(91);
      String var5 = $$v_s(var10004, -3736022484232450024L);
      var10005 = new byte[7];
      var10005[0] = (byte)(120);
      var10005[1] = (byte)(-12);
      var10005[2] = (byte)(123);
      var10005[3] = (byte)(-78);
      var10005[4] = (byte)(-120);
      var10005[5] = (byte)(86);
      var10005[6] = (byte)(77);
      ENG = new Language(var5, $$v_s(var10005, -3736022484232450024L));
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
