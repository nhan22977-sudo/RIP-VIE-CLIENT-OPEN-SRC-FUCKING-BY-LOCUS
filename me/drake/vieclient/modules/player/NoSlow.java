package me.drake.vieclient.modules.player;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;

public class NoSlow extends Module {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public NoSlow() {
      byte[] var10001 = new byte[6];
      var10001[0] = (byte)(119);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(79);
      var10001[3] = (byte)(-78);
      var10001[4] = (byte)(87);
      var10001[5] = (byte)(93);
      String var1 = $$v_s(var10001, 7038269153810305052L);
      byte[] var10002 = new byte[29];
      var10002[0] = (byte)(119);
      var10002[1] = (byte)(-11);
      var10002[2] = (byte)(60);
      var10002[3] = (byte)(-83);
      var10002[4] = (byte)(84);
      var10002[5] = (byte)(69);
      var10002[6] = (byte)(-40);
      var10002[7] = (byte)(45);
      var10002[8] = (byte)(62);
      var10002[9] = (byte)(-43);
      var10002[10] = (byte)(-118);
      var10002[11] = (byte)(-42);
      var10002[12] = (byte)(23);
      var10002[13] = (byte)(-102);
      var10002[14] = (byte)(-18);
      var10002[15] = (byte)(93);
      var10002[16] = (byte)(12);
      var10002[17] = (byte)(106);
      var10002[18] = (byte)(-71);
      var10002[19] = (byte)(-35);
      var10002[20] = (byte)(-31);
      var10002[21] = (byte)(-76);
      var10002[22] = (byte)(-104);
      var10002[23] = (byte)(57);
      var10002[24] = (byte)(-24);
      var10002[25] = (byte)(102);
      var10002[26] = (byte)(-15);
      var10002[27] = (byte)(43);
      var10002[28] = (byte)(-61);
      super(var1, $$v_s(var10002, 7038269153810305052L), Category.PLAYER);
   }

   @Override
   public void onTick() {
      if (this.isInGame()) {
         if (mc.field_1724.method_6115()
            && mc.field_1724.method_5624()
            && ActionArbiter.get().tryAcquire(ActionArbiter.Resource.SPRINT, ActionArbiter.Owner.NOSLOW)) {
            mc.field_1724.method_5728(true);
         }
      }
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
