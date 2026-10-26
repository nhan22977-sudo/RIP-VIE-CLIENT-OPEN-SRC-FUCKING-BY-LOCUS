package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.ModeSetting;
import net.minecraft.class_310;

public class TrueStretched extends Module {
   public final ModeSetting resolution = (ModeSetting)this.addSetting(
      new ModeSetting(
         "Resolution",
         "FPS stretched aspect ratio preset",
         TrueStretched.StretchedResolution.RES_4_3,
         TrueStretched.StretchedResolution.class
      )
   );
   public final DoubleSetting customRatio = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Custom Ratio",
         "Custom aspect ratio (width / height)",
         1.33,
         0.4,
         3.0,
         0.01
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public TrueStretched() {
      super(
         "True Stretched",
         "Stretches 3D world perspective like FPS games (4:3, 5:4, etc.)",
         Category.RENDER
      );
      this.customRatio.visibleWhen(() -> this.resolution.get() == TrueStretched.StretchedResolution.CUSTOM);
   }

   public float getTargetAspectRatio() {
      TrueStretched.StretchedResolution var1 = (TrueStretched.StretchedResolution)this.resolution.get();
      return var1 == TrueStretched.StretchedResolution.CUSTOM ? ((Double)this.customRatio.get()).floatValue() : var1.getRatio();
   }

   public static float getStretchFactor() {
      VieClientMod var0 = VieClientMod.getInstance();
      if (var0 != null && var0.getModuleManager() != null) {
         TrueStretched var1 = (TrueStretched)var0.getModuleManager().getModule(TrueStretched.class);
         if (var1 != null && var1.isEnabled()) {
            float var2 = var1.getTargetAspectRatio();
            if (var2 <= 0.0F) {
               return 1.0F;
            }

            class_310 var3 = class_310.method_1551();
            if (var3 != null && var3.method_22683() != null) {
               int var4 = var3.method_22683().method_4489();
               int var5 = var3.method_22683().method_4506();
               if (var4 > 0 && var5 > 0) {
                  float var6 = (float)var4 / var5;
                  float var7 = var6 / var2;
                  return Math.abs(var7 - 1.0F) < 1.0E-4F ? 1.0F : var7;
               } else {
                  return 1.0F;
               }
            } else {
               return 1.0F;
            }
         } else {
            return 1.0F;
         }
      } else {
         return 1.0F;
      }
   }

   public static float getEffectiveAspectRatio(int fbW, int fbH) {
      if (fbW > 0 && fbH > 0) {
         float var2 = (float)fbW / fbH;
         float var3 = getStretchFactor();
         return !(Math.abs(var3 - 1.0F) < 1.0E-4F) && !(var3 <= 1.0E-4F) ? var2 / var3 : var2;
      } else {
         return 1.7777778F;
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

   public enum StretchedResolution {
      RES_4_3("4:3", 1.3333334F),
      RES_5_4("5:4", 1.25F),
      RES_16_10("16:10", 1.6F),
      RES_1_1("1:1", 1.0F),
      CUSTOM("Custom", 0.0F);

      private final String label;
      private final float ratio;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      StretchedResolution(String label, float ratio) {
         this.label = labelx;
         this.ratio = ratiox;
      }

      public String getLabel() {
         return this.label;
      }

      public float getRatio() {
         return this.ratio;
      }

      @Override
      public String toString() {
         return this.label;
      }

      // $VF: synthetic method
      private static TrueStretched.StretchedResolution[] $values() {
         TrueStretched.StretchedResolution[] var10000 = new TrueStretched.StretchedResolution[5];
         var10000[0] = RES_4_3;
         var10000[1] = RES_5_4;
         var10000[2] = RES_16_10;
         var10000[3] = RES_1_1;
         var10000[4] = CUSTOM;
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
