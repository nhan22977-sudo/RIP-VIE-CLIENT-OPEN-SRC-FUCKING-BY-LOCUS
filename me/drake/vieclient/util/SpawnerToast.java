package me.drake.vieclient.util;

import java.nio.charset.StandardCharsets;
import net.minecraft.class_10799;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_368;
import net.minecraft.class_374;
import net.minecraft.class_368.class_369;

public class SpawnerToast implements class_368 {
   private static final class_2960 TEXTURE;
   private static final long DURATION_MS = 4200L;
   private final class_2338 pos;
   private final long createdAt = System.currentTimeMillis();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SpawnerToast(class_2338 pos) {
      this.pos = posx;
   }

   public class_369 method_61988() {
      return System.currentTimeMillis() - this.createdAt < 4200L ? class_369.field_2210 : class_369.field_2209;
   }

   public void method_61989(class_374 manager, long time) {
   }

   public class_3414 method_66530() {
      return (class_3414)class_3417.field_14793.comp_349();
   }

   public void method_1986(class_332 context, class_327 textRenderer, long time) {
      context.method_52706(class_10799.field_56883, TEXTURE, 0, 0, this.method_29049(), this.method_29050());
      byte[] var10002 = new byte[14];
      var10002[0] = (byte)(106);
      var10002[1] = (byte)(-22);
      var10002[2] = (byte)(125);
      var10002[3] = (byte)(-87);
      var10002[4] = (byte)(42);
      var10002[5] = (byte)(-44);
      var10002[6] = (byte)(-15);
      var10002[7] = (byte)(68);
      var10002[8] = (byte)(113);
      var10002[9] = (byte)(-60);
      var10002[10] = (byte)(-117);
      var10002[11] = (byte)(-125);
      var10002[12] = (byte)(114);
      var10002[13] = (byte)(13);
      context.method_51439(
         textRenderer, class_2561.method_43470($$v_s(var10002, 2269937345861242908L)), 30, 7, -256, false
      );
      context.method_51439(
         textRenderer,
         class_2561.method_43470("X: " + this.pos.method_10263() + " Y: " + this.pos.method_10264() + " Z: " + this.pos.method_10260()),
         30,
         18,
         -1,
         false
      );
      context.method_51445(new class_1799(class_1802.field_8849), 8, 8);
   }

   public Object method_1987() {
      return field_2208;
   }

   static {
      byte[] var10000 = new byte[17];
      var10000[0] = (byte)(77);
      var10000[1] = (byte)(-11);
      var10000[2] = (byte)(125);
      var10000[3] = (byte)(-83);
      var10000[4] = (byte)(48);
      var10000[5] = (byte)(-98);
      var10000[6] = (byte)(-30);
      var10000[7] = (byte)(83);
      var10000[8] = (byte)(39);
      var10000[9] = (byte)(-61);
      var10000[10] = (byte)(-118);
      var10000[11] = (byte)(-107);
      var10000[12] = (byte)(121);
      var10000[13] = (byte)(4);
      var10000[14] = (byte)(-50);
      var10000[15] = (byte)(33);
      var10000[16] = (byte)(29);
      TEXTURE = class_2960.method_60656($$v_s(var10000, 2269937345861242908L));
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
