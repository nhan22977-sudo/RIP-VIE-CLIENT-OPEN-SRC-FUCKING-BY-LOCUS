package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.render.post.ColorCorrectionPass;

public class ColorCorrection extends Module {
   private static ColorCorrection INSTANCE;
   private boolean updatingPreset = false;
   private final EnumSetting preset = (EnumSetting)this.addSetting(
      new EnumSetting(
            "Preset",
            "Color grading aesthetic preset",
            ColorCorrection.Preset.VIE_CYBER
         )
         .withDisplayName(ColorCorrection.Preset.CUSTOM, "Custom")
         .withDisplayName(ColorCorrection.Preset.VIE_CYBER, "Vie Cyber")
         .withDisplayName(ColorCorrection.Preset.CINEMATIC, "Cinematic")
         .withDisplayName(ColorCorrection.Preset.VIBRANT, "Vibrant")
         .withDisplayName(ColorCorrection.Preset.COLD_WINTER, "Cold Winter")
         .withDisplayName(ColorCorrection.Preset.WARM_SUNSET, "Warm Sunset")
         .withDisplayName(ColorCorrection.Preset.RETRO, "Retro Pastel")
         .withDisplayName(ColorCorrection.Preset.NOIR, "Noir Drama")
   );
   private final DoubleSetting brightness = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Brightness",
         "Overall screen brightness and exposure",
         4.0,
         -20.0,
         30.0,
         1.0
      )
   );
   private final DoubleSetting contrast = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Contrast",
         "Screen contrast level between light and dark areas",
         14.0,
         -20.0,
         40.0,
         1.0
      )
   );
   private final DoubleSetting vibrance = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Vibrance",
         "Vibrance and intelligent color saturation level",
         45.0,
         -100.0,
         100.0,
         1.0
      )
   );
   private final DoubleSetting warmth = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Warmth",
         "Color temperature warmth (warm amber or cool azure)",
         -20.0,
         -100.0,
         100.0,
         1.0
      )
   );
   private final DoubleSetting vignette = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Vignette",
         "Vignette darkness around screen corners",
         35.0,
         0.0,
         100.0,
         1.0
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ColorCorrection() {
      super(
         "ColorCorrection",
         "Fine-tune color grading, contrast, and screen vignetting",
         Category.RENDER
      );
      INSTANCE = this;
      this.preset.onChanged(selected -> {
         if (selected != ColorCorrection.Preset.CUSTOM) {
            this.applyPreset(selected);
         }

         this.requestConfigSave();
      });
      Consumer var1 = val -> this.checkCustom();
      this.brightness.onChanged(var1);
      this.contrast.onChanged(var1);
      this.vibrance.onChanged(var1);
      this.warmth.onChanged(var1);
      this.vignette.onChanged(var1);
   }

   private void applyPreset(ColorCorrection.Preset selected) {
      this.updatingPreset = true;

      try {
         this.brightness.set(selected.brightness);
         this.contrast.set(selected.contrast);
         this.vibrance.set(selected.vibrance);
         this.warmth.set(selected.warmth);
         this.vignette.set(selected.vignette);
      } finally {
         this.updatingPreset = false;
      }
   }

   private void checkCustom() {
      if (!this.updatingPreset) {
         ColorCorrection.Preset var1 = (ColorCorrection.Preset)this.preset.get();
         if (var1 != ColorCorrection.Preset.CUSTOM
            && (
               Math.abs((Double)this.brightness.get() - var1.brightness) > 0.001
                  || Math.abs((Double)this.contrast.get() - var1.contrast) > 0.001
                  || Math.abs((Double)this.vibrance.get() - var1.vibrance) > 0.001
                  || Math.abs((Double)this.warmth.get() - var1.warmth) > 0.001
                  || Math.abs((Double)this.vignette.get() - var1.vignette) > 0.001
            )) {
            this.preset.set(ColorCorrection.Preset.CUSTOM);
         }
      }
   }

   public static ColorCorrection get() {
      return INSTANCE;
   }

   public static boolean isActive() {
      return INSTANCE != null && INSTANCE.isEnabled();
   }

   @Override
   public void onDisable() {
      ColorCorrectionPass.release();
   }

   public static void renderAfterWorld(boolean worldRendered) {
      if (isActive()) {
         ColorCorrection var1 = INSTANCE;
         ColorCorrectionPass.apply(
            worldRendered,
            ((Double)var1.vibrance.get()).floatValue() / 100.0F,
            1.0F + ((Double)var1.contrast.get()).floatValue() / 100.0F,
            1.0F + ((Double)var1.brightness.get()).floatValue() / 100.0F,
            ((Double)var1.warmth.get()).floatValue() / 100.0F,
            ((Double)var1.vignette.get()).floatValue() / 100.0F
         );
      }
   }

   public EnumSetting getPresetSetting() {
      return this.preset;
   }

   public DoubleSetting getBrightnessSetting() {
      return this.brightness;
   }

   public DoubleSetting getContrastSetting() {
      return this.contrast;
   }

   public DoubleSetting getVibranceSetting() {
      return this.vibrance;
   }

   public DoubleSetting getWarmthSetting() {
      return this.warmth;
   }

   public DoubleSetting getVignetteSetting() {
      return this.vignette;
   }

   @Override
   public String getInfo() {
      String var1;
      if (this.preset.get() == ColorCorrection.Preset.CUSTOM) {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(7);
         var10000[1] = (byte)(-65);
         var10000[2] = (byte)(50);
         var10000[3] = (byte)(-18);
         var10000[4] = (byte)(56);
         var10000[5] = (byte)(-13);
         var10000[6] = (byte)(101);
         var1 = String.format($$v_s(var10000, -7258948384911536119L), this.vibrance.get());
      } else {
         var1 = ((ColorCorrection.Preset)this.preset.get()).getDisplayName();
      }

      return var1;
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

   public enum Preset {
      CUSTOM("Custom", 35.0, 8.0, 3.0, 15.0, 30.0),
      VIE_CYBER("Vie Cyber", 45.0, 14.0, 4.0, -20.0, 35.0),
      CINEMATIC("Cinematic", 25.0, 10.0, 2.0, 15.0, 40.0),
      VIBRANT("Vibrant", 65.0, 8.0, 5.0, 5.0, 15.0),
      COLD_WINTER("Cold Winter", 20.0, 12.0, 6.0, -40.0, 25.0),
      WARM_SUNSET("Warm Sunset", 35.0, 8.0, 4.0, 45.0, 30.0),
      RETRO("Retro Pastel", 10.0, -5.0, 8.0, 20.0, 25.0),
      NOIR("Noir Drama", -80.0, 24.0, -2.0, 0.0, 50.0);

      private final String displayName;
      public final double vibrance;
      public final double contrast;
      public final double brightness;
      public final double warmth;
      public final double vignette;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Preset(String displayName, double vibrance, double contrast, double brightness, double warmth, double vignette) {
         this.displayName = displayNamex;
         this.vibrance = vibrancex;
         this.contrast = contrastx;
         this.brightness = brightnessx;
         this.warmth = warmthx;
         this.vignette = vignettex;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static ColorCorrection.Preset[] $values() {
         ColorCorrection.Preset[] var10000 = new ColorCorrection.Preset[8];
         var10000[0] = CUSTOM;
         var10000[1] = VIE_CYBER;
         var10000[2] = CINEMATIC;
         var10000[3] = VIBRANT;
         var10000[4] = COLD_WINTER;
         var10000[5] = WARM_SUNSET;
         var10000[6] = RETRO;
         var10000[7] = NOIR;
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
