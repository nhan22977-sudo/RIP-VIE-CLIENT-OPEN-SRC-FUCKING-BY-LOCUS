package me.drake.vieclient.util;

import java.nio.charset.StandardCharsets;
import net.minecraft.class_1923;
import net.minecraft.class_2596;
import net.minecraft.class_2797;
import net.minecraft.class_2799;
import net.minecraft.class_2828;
import net.minecraft.class_2846;
import net.minecraft.class_2885;
import net.minecraft.class_2886;
import net.minecraft.class_310;

public final class PlayerFootprintUtil {
   private static final PlayerFootprintUtil.FootprintSignal NONE = new PlayerFootprintUtil.FootprintSignal(false, -1L, 0);
   private static final ThreadLocal TLS = ThreadLocal.withInitial(PlayerFootprintUtil.FootprintSignal::new);
   private static final String[] TARGETING_CMD_PREFIXES = new String[]{
      "/home",
      "/spawn",
      "/tpa",
      "/tpahere",
      "/sethome",
      "/setworldspawn",
      "/warp",
      "/back",
      "/tpr",
      "/rtp",
      "/accept",
      "/deny",
      "/sit",
      "/pet",
      "/jobs",
      "/bal",
      "/money",
      "/w ",
      "/msg ",
      "/r "
   };
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private PlayerFootprintUtil() {
   }

   public static PlayerFootprintUtil.FootprintSignal inspect(class_2596 packet) {
      if (packet == null) {
         return NONE;
      } else {
         class_310 var1 = class_310.method_1551();
         if (var1.field_1724 == null) {
            return NONE;
         } else {
            long var2 = class_1923.method_8331(
               (int)var1.field_1724.method_23317() >> (4), (int)var1.field_1724.method_23321() >> (4)
            );
            if (packet instanceof class_2846 var8) {
               return setSignal(
                  class_1923.method_8331(var8.method_12362().method_10263() >> (4), var8.method_12362().method_10260() >> (4)),
                  8
               );
            } else if (packet instanceof class_2885 var7) {
               return setSignal(
                  class_1923.method_8331(
                     var7.method_12543().method_17777().method_10263() >> (4),
                     var7.method_12543().method_17777().method_10260() >> (4)
                  ),
                  6
               );
            } else if (packet instanceof class_2797 var6) {
               String var5 = var6.comp_945();
               if (var5.length() <= 0 || var5.charAt(0) != (47)) {
                  return setSignal(var2, 2);
               } else {
                  return isTargetingCommand(var5) ? setSignal(var2, 7) : setSignal(var2, 3);
               }
            } else if (packet instanceof class_2799) {
               return setSignal(var2, 2);
            } else if (packet instanceof class_2886) {
               return setSignal(var2, 4);
            } else if (packet instanceof class_2828 var4) {
               return !var4.method_36171()
                  ? NONE
                  : setSignal(
                     class_1923.method_8331((int)var4.method_12269(0.0) >> (4), (int)var4.method_12274(0.0) >> (4)), 1
                  );
            } else {
               return NONE;
            }
         }
      }
   }

   private static PlayerFootprintUtil.FootprintSignal setSignal(long chunkKey, int weight) {
      PlayerFootprintUtil.FootprintSignal var3 = (PlayerFootprintUtil.FootprintSignal)TLS.get();
      var3.hasSignal = true;
      var3.chunkKey = chunkKey;
      var3.weight = weight;
      return var3;
   }

   private static boolean isTargetingCommand(String cmd) {
      int var1 = cmd.length();

      for (String var5 : TARGETING_CMD_PREFIXES) {
         int var6 = var5.length();
         if (var1 >= var6 && cmd.regionMatches(true, 0, var5, 0, var6)) {
            return true;
         }
      }

      return false;
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

   public static final class FootprintSignal {
      public boolean hasSignal;
      public long chunkKey;
      public int weight;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public FootprintSignal() {
         this(false, -1L, 0);
      }

      public FootprintSignal(boolean hasSignal, long chunkKey, int weight) {
         this.hasSignal = hasSignalx;
         this.chunkKey = chunkKeyx;
         this.weight = weightx;
      }

      public boolean hasSignal() {
         return this.hasSignal;
      }

      public long chunkKey() {
         return this.chunkKey;
      }

      public int weight() {
         return this.weight;
      }
   }
}
