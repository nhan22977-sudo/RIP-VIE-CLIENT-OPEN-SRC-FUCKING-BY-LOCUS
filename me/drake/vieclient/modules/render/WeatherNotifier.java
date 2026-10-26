package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.util.render.ToastManager;

public class WeatherNotifier extends Module {
   private boolean prevRaining;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public WeatherNotifier() {
      byte[] var10001 = new byte[15];
      var10001[0] = (byte)(110);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-86);
      var10001[4] = (byte)(89);
      var10001[5] = (byte)(4);
      var10001[6] = (byte)(-71);
      var10001[7] = (byte)(43);
      var10001[8] = (byte)(62);
      var10001[9] = (byte)(-42);
      var10001[10] = (byte)(-115);
      var10001[11] = (byte)(-112);
      var10001[12] = 0;
      var10001[13] = (byte)(-36);
      var10001[14] = (byte)(-111);
      String var1 = $$v_s(var10001, 5604940328348143644L);
      byte[] var10002 = new byte[35];
      var10002[0] = (byte)(119);
      var10002[1] = (byte)(-11);
      var10002[2] = (byte)(104);
      var10002[3] = (byte)(-73);
      var10002[4] = (byte)(87);
      var10002[5] = (byte)(8);
      var10002[6] = (byte)(-82);
      var10002[7] = (byte)(22);
      var10002[8] = (byte)(113);
      var10002[9] = (byte)(-43);
      var10002[10] = (byte)(-116);
      var10002[11] = (byte)(-109);
      var10002[12] = (byte)(7);
      var10002[13] = (byte)(-103);
      var10002[14] = (byte)(-108);
      var10002[15] = (byte)(114);
      var10002[16] = (byte)(27);
      var10002[17] = (byte)(38);
      var10002[18] = (byte)(-88);
      var10002[19] = (byte)(-114);
      var10002[20] = (byte)(-10);
      var10002[21] = (byte)(-12);
      var10002[22] = (byte)(-6);
      var10002[23] = (byte)(65);
      var10002[24] = (byte)(-23);
      var10002[25] = (byte)(119);
      var10002[26] = (byte)(-26);
      var10002[27] = (byte)(102);
      var10002[28] = (byte)(-38);
      var10002[29] = (byte)(-127);
      var10002[30] = (byte)(-46);
      var10002[31] = (byte)(-125);
      var10002[32] = (byte)(-66);
      var10002[33] = (byte)(95);
      var10002[34] = (byte)(15);
      super(var1, $$v_s(var10002, 5604940328348143644L), Category.RENDER);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.prevRaining = this.isInGame() && mc.field_1687 != null && mc.field_1687.method_8419();
   }

   @EventHandler
   public void onTick(TickEvent e) {
      if (e.getPhase() == TickEvent.Phase.PRE && this.isInGame()) {
         boolean var2 = mc.field_1687.method_8419();
         if (var2 != this.prevRaining) {
            this.prevRaining = var2;
            ToastManager.pushWeather(var2);
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
