package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.render.post.MotionBlurPass;

public class MotionBlur extends Module {
   private static MotionBlur INSTANCE;
   private final DoubleSetting strength = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Strength",
         "Motion blur persistence and trail strength",
         50.0,
         10.0,
         90.0,
         5.0
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MotionBlur() {
      super(
         "MotionBlur",
         "Smooth motion blur accumulation trail when panning camera",
         Category.RENDER
      );
      INSTANCE = this;
   }

   public static MotionBlur get() {
      return INSTANCE;
   }

   public static boolean isActive() {
      return INSTANCE != null && INSTANCE.isEnabled();
   }

   @Override
   public void onDisable() {
      MotionBlurPass.release();
   }

   public static void renderAfterWorld(boolean worldRendered) {
      if (isActive()) {
         MotionBlurPass.apply(worldRendered, ((Double)INSTANCE.strength.get()).floatValue() / 100.0F);
      }
   }

   @Override
   public String getInfo() {
      byte[] var10000 = new byte[6];
      var10000[0] = (byte)(27);
      var10000[1] = (byte)(-76);
      var10000[2] = (byte)(44);
      var10000[3] = (byte)(-72);
      var10000[4] = (byte)(-93);
      var10000[5] = (byte)(-80);
      return String.format($$v_s(var10000, -3411112507552313317L), this.strength.get());
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
