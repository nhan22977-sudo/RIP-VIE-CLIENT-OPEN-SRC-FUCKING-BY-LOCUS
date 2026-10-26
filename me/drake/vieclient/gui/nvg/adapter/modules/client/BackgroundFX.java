package me.drake.vieclient.gui.nvg.adapter.modules.client;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.gui.nvg.adapter.module.Category;
import me.drake.vieclient.gui.nvg.adapter.module.Module;
import me.drake.vieclient.gui.nvg.adapter.setting.BooleanSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.ColorSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.NumberSetting;

public class BackgroundFX extends Module {
   public static final BackgroundFX instance = new BackgroundFX();
   public final BooleanSetting worldEffect;
   public final NumberSetting worldDensity;
   public final NumberSetting worldRadius;
   public final ColorSetting worldAccent;
   public boolean enabled;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private BackgroundFX() {
      super(new BackgroundFX.Raw());
      byte[] var10003 = new byte[12];
      var10003[0] = (byte)(105);
      var10003[1] = (byte)(-11);
      var10003[2] = (byte)(110);
      var10003[3] = (byte)(-78);
      var10003[4] = (byte)(12);
      var10003[5] = (byte)(28);
      var10003[6] = (byte)(-64);
      var10003[7] = (byte)(117);
      var10003[8] = (byte)(48);
      var10003[9] = (byte)(-57);
      var10003[10] = (byte)(-121);
      var10003[11] = (byte)(-126);
      this.worldEffect = new BooleanSetting($$v_s(var10003, 4289365083642122267L), false);
      var10003 = new byte[7];
      var10003[0] = (byte)(122);
      var10003[1] = (byte)(-1);
      var10003[2] = (byte)(114);
      var10003[3] = (byte)(-83);
      var10003[4] = 1;
      var10003[5] = (byte)(72);
      var10003[6] = (byte)(-4);
      this.worldDensity = new NumberSetting($$v_s(var10003, 4289365083642122267L), 0.5, 80.0, 12.0, 1.0);
      var10003 = new byte[6];
      var10003[0] = (byte)(108);
      var10003[1] = (byte)(-5);
      var10003[2] = (byte)(120);
      var10003[3] = (byte)(-73);
      var10003[4] = (byte)(29);
      var10003[5] = (byte)(79);
      this.worldRadius = new NumberSetting($$v_s(var10003, 4289365083642122267L), 2.0, 32.0, 6.0, 1.0);
      var10003 = new byte[6];
      var10003[0] = (byte)(127);
      var10003[1] = (byte)(-7);
      var10003[2] = (byte)(127);
      var10003[3] = (byte)(-69);
      var10003[4] = (byte)(6);
      var10003[5] = (byte)(72);
      this.worldAccent = new ColorSetting(
         $$v_s(var10003, 4289365083642122267L), new Color(120, 170, 255, 255)
      );
      this.enabled = false;
   }

   @Override
   public CharSequence getName() {
      byte[] var10000 = new byte[12];
      var10000[0] = (byte)(124);
      var10000[1] = (byte)(-5);
      var10000[2] = (byte)(127);
      var10000[3] = (byte)(-75);
      var10000[4] = (byte)(15);
      var10000[5] = (byte)(78);
      var10000[6] = (byte)(-22);
      var10000[7] = (byte)(102);
      var10000[8] = (byte)(56);
      var10000[9] = (byte)(-58);
      var10000[10] = (byte)(-94);
      var10000[11] = (byte)(-82);
      return $$v_s(var10000, 4289365083642122267L);
   }

   @Override
   public boolean isEnabled() {
      return this.enabled;
   }

   @Override
   public void setEnabled(boolean b) {
      this.enabled = b;
   }

   @Override
   public void toggle() {
      this.enabled = !this.enabled;
   }

   @Override
   public Category getCategory() {
      return Category.PROTECT;
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

   private static final class Raw extends me.drake.vieclient.core.module.Module {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Raw() {
         byte[] var10001 = new byte[12];
         var10001[0] = (byte)(123);
         var10001[1] = (byte)(-5);
         var10001[2] = (byte)(127);
         var10001[3] = (byte)(-75);
         var10001[4] = (byte)(115);
         var10001[5] = (byte)(-35);
         var10001[6] = (byte)(-11);
         var10001[7] = (byte)(-96);
         var10001[8] = (byte)(63);
         var10001[9] = (byte)(-58);
         var10001[10] = (byte)(-94);
         var10001[11] = (byte)(-82);
         String var1 = $$v_s(var10001, -172982121584668644L);
         byte[] var10002 = new byte[24];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-2);
         var10002[4] = (byte)(118);
         var10002[5] = (byte)(-50);
         var10002[6] = (byte)(-7);
         var10002[7] = (byte)(-66);
         var10002[8] = (byte)(54);
         var10002[9] = (byte)(-48);
         var10002[10] = (byte)(-117);
         var10002[11] = (byte)(-125);
         var10002[12] = (byte)(34);
         var10002[13] = (byte)(19);
         var10002[14] = (byte)(-110);
         var10002[15] = (byte)(-35);
         var10002[16] = (byte)(8);
         var10002[17] = (byte)(56);
         var10002[18] = (byte)(-72);
         var10002[19] = (byte)(-57);
         var10002[20] = (byte)(-57);
         var10002[21] = (byte)(51);
         var10002[22] = (byte)(-81);
         var10002[23] = (byte)(-10);
         super(var1, $$v_s(var10002, -172982121584668644L), me.drake.vieclient.core.module.Category.PROTECT);
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
}
