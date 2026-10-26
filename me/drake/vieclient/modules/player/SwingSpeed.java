package me.drake.vieclient.modules.player;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.IntSetting;

public class SwingSpeed extends Module {
   private final IntSetting speed = (IntSetting)this.addSetting(
      new IntSetting(
         "Speed",
         "Swing speed modifier (0 = vanilla, negative = slower, positive = faster)",
         0,
         -200,
         20
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SwingSpeed() {
      super(
         "SwingSpeed",
         "Adjust hand swing animation speed",
         Category.PLAYER
      );
   }

   public int getSwingDuration() {
      int var1 = (Integer)this.speed.get();
      return var1 > 0 ? Math.max(2, (6) - var1 / (5)) : (6) - var1;
   }

   public IntSetting getSpeedSetting() {
      return this.speed;
   }

   @Override
   public String getInfo() {
      return String.valueOf(this.speed.get());
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
