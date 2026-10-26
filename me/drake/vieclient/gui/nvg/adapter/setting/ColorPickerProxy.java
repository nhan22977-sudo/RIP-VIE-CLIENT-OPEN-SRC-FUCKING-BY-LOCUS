package me.drake.vieclient.gui.nvg.adapter.setting;

import java.awt.Color;
import java.nio.charset.StandardCharsets;

public class ColorPickerProxy extends ColorSetting {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ColorPickerProxy(Color initial) {
      byte[] var10001 = new byte[14];
      var10001[0] = (byte)(101);
      var10001[1] = (byte)(-59);
      var10001[2] = (byte)(108);
      var10001[3] = (byte)(-73);
      var10001[4] = (byte)(13);
      var10001[5] = (byte)(-2);
      var10001[6] = (byte)(-108);
      var10001[7] = (byte)(-83);
      var10001[8] = (byte)(13);
      var10001[9] = (byte)(-46);
      var10001[10] = (byte)(-106);
      var10001[11] = (byte)(-103);
      var10001[12] = (byte)(78);
      var10001[13] = (byte)(52);
      super($$v_s(var10001, -580318013938216929L), initial);
   }

   @Override
   public Color getValue() {
      return super.getValue();
   }

   @Override
   public void setValue(Color v) {
      super.setValue(v);
   }

   @Override
   public void setValue(int red, int green, int blue, int alpha) {
      super.setValue(red, green, blue, alpha);
   }

   public int argb() {
      Color var1 = this.getValue();
      return (var1.getAlpha() & (255)) << (24)
         | (var1.getRed() & (255)) << (16)
         | (var1.getGreen() & (255)) << (8)
         | var1.getBlue() & (255);
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
