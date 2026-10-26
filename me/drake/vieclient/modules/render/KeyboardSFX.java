package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_310;
import net.minecraft.class_3419;

public class KeyboardSFX extends Module {
   private static KeyboardSFX INSTANCE;
   public final EnumSetting pack = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Sound Pack",
         "Switch sound profile",
         KeyboardSFX.SoundPack.CHERRY_BLACK_PBT,
         KeyboardSFX.SoundPack.values()
      )
   );
   public final IntSetting volume = (IntSetting)this.addSetting(
      new IntSetting(
         "Volume",
         "Playback volume percentage",
         80,
         0,
         100
      )
   );
   public final BoolSetting mouseClicks = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Mouse Clicks",
         "Play switch sounds on left and right mouse clicks",
         true
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public KeyboardSFX() {
      super(
         "KeyboardSFX",
         "Mechanical keyboard & click sound feedback",
         Category.CUSTOMIZE
      );
      INSTANCE = this;
   }

   public static KeyboardSFX getInstance() {
      return INSTANCE;
   }

   public static boolean isActive() {
      return INSTANCE != null && INSTANCE.isEnabled();
   }

   public static void onKey() {
      if (isActive()) {
         INSTANCE.playSound();
      }
   }

   public static void onMouseClick() {
      if (isActive() && (Boolean)INSTANCE.mouseClicks.get()) {
         INSTANCE.playSound();
      }
   }

   private void playSound() {
      class_310 var1 = class_310.method_1551();
      if (var1 != null && var1.field_1690 != null) {
         float var2 = var1.field_1690.method_71978(class_3419.field_15250);
         float var3 = ((Integer)this.volume.get()).intValue() / 100.0F;
         float var4 = Math.max(0.0F, Math.min(1.0F, var3 * var2));
         if (!(var4 <= 0.0F)) {
            int var5 = ThreadLocalRandom.current().nextInt(1, 41);
            String var6 = var5 < (10) ? "0" + var5 : Integer.toString(var5);
            KeyboardSFX.SoundPack var7 = this.pack != null ? (KeyboardSFX.SoundPack)this.pack.get() : null;
            String var10000;
            if (var7 != null) {
               var10000 = var7.getFolder();
            } else {
               byte[] var10 = new byte[17];
               var10[0] = (byte)(81);
               var10[1] = (byte)(-14);
               var10[2] = (byte)(121);
               var10[3] = (byte)(-84);
               var10[4] = (byte)(47);
               var10[5] = (byte)(-50);
               var10[6] = (byte)(11);
               var10[7] = (byte)(32);
               var10[8] = (byte)(5);
               var10[9] = (byte)(-64);
               var10[10] = (byte)(-120);
               var10[11] = (byte)(-125);
               var10[12] = (byte)(96);
               var10[13] = (byte)(48);
               var10[14] = (byte)(47);
               var10[15] = (byte)(66);
               var10[16] = (byte)(17);
               var10000 = $$v_s(var10, 8098995934118268951L);
            }

            String var8 = var10000;
            String var9 = "assets/creamykeys/sounds/keyboards/" + var8 + "/key_" + var6 + ".ogg";
            KeyboardSoundPlayer.play(var9, var4);
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

   public enum SoundPack {
      CHERRY_BLACK_ABS(
         "cherrymx_black_abs",
         "Cherry MX Black (ABS)"
      ),
      CHERRY_BLACK_PBT(
         "cherrymx_black_pbt",
         "Cherry MX Black (PBT)"
      ),
      CHERRY_BLUE_ABS(
         "cherrymx_blue_abs",
         "Cherry MX Blue (ABS)"
      ),
      CHERRY_BLUE_ABS_2(
         "cherrymx_blue_abs_2",
         "Cherry MX Blue (ABS 2)"
      ),
      CHERRY_BROWN_ABS(
         "cherrymx_brown_abs",
         "Cherry MX Brown (ABS)"
      ),
      CHERRY_BROWN_PBT(
         "cherrymx_brown_pbt",
         "Cherry MX Brown (PBT)"
      ),
      CHERRY_RED_ABS(
         "cherrymx_red_abs",
         "Cherry MX Red (ABS)"
      );

      private final String folder;
      private final String label;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      SoundPack(String folder, String label) {
         this.folder = folderx;
         this.label = labelx;
      }

      public String getFolder() {
         return this.folder;
      }

      @Override
      public String toString() {
         return this.label;
      }

      // $VF: synthetic method
      private static KeyboardSFX.SoundPack[] $values() {
         KeyboardSFX.SoundPack[] var10000 = new KeyboardSFX.SoundPack[7];
         var10000[0] = CHERRY_BLACK_ABS;
         var10000[1] = CHERRY_BLACK_PBT;
         var10000[2] = CHERRY_BLUE_ABS;
         var10000[3] = CHERRY_BLUE_ABS_2;
         var10000[4] = CHERRY_BROWN_ABS;
         var10000[5] = CHERRY_BROWN_PBT;
         var10000[6] = CHERRY_RED_ABS;
         return var10000;
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
