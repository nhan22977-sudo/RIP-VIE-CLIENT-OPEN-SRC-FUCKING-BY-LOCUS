package me.drake.vieclient.util;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import net.minecraft.class_310;

public final class ServerSafety {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private ServerSafety() {
   }

   public static boolean isDonutServer() {
      String var0 = currentServerAddressLower();
      if (var0.isEmpty()) {
         return false;
      }

      byte[] var10001 = new byte[5];
      var10001[0] = (byte)(91);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(114);
      var10001[3] = (byte)(-85);
      var10001[4] = (byte)(-26);
      if (!var0.contains($$v_s(var10001, 226343450603409434L))) {
         var10001 = new byte[8];
         var10001[0] = (byte)(91);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(114);
         var10001[3] = (byte)(-85);
         var10001[4] = (byte)(-26);
         var10001[5] = (byte)(-113);
         var10001[6] = (byte)(74);
         var10001[7] = (byte)(91);
         if (!var0.contains($$v_s(var10001, 226343450603409434L))) {
            var10001 = new byte[7];
            var10001[0] = (byte)(91);
            var10001[1] = (byte)(-11);
            var10001[2] = (byte)(114);
            var10001[3] = (byte)(-85);
            var10001[4] = (byte)(-26);
            var10001[5] = (byte)(-113);
            var10001[6] = (byte)(74);
            if (!var0.contains($$v_s(var10001, 226343450603409434L))) {
               var10001 = new byte[6];
               var10001[0] = (byte)(91);
               var10001[1] = (byte)(-11);
               var10001[2] = (byte)(114);
               var10001[3] = (byte)(-83);
               var10001[4] = (byte)(-1);
               var10001[5] = (byte)(-116);
               if (!var0.contains($$v_s(var10001, 226343450603409434L))) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public static String currentServerAddressLower() {
      class_310 var0 = class_310.method_1551();
      return var0 != null && var0.method_1558() != null && var0.method_1558().field_3761 != null ? var0.method_1558().field_3761.toLowerCase(Locale.ROOT) : "";
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
