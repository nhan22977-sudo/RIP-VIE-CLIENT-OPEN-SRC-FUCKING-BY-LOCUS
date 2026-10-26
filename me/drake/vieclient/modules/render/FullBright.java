package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.mixin.MixinSimpleOption;
import net.minecraft.class_1293;
import net.minecraft.class_1294;

public class FullBright extends Module {
   private static final double MAX_GAMMA_BOOST = 256.0;
   private static final int NV_DURATION_TICKS = 880;
   private static final int NV_REFRESH_THRESHOLD_TICKS = 480;
   private final EnumSetting mode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Mode",
         "Brightness mode",
         FullBright.Mode.GAMMA,
         FullBright.Mode.values()
      )
   );
   private double originalGamma = 1.0;
   private boolean hadNightVisionBefore = false;
   private double lastAppliedGamma = 1.0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FullBright() {
      super(
         "FullBright",
         "Makes the world fully bright",
         Category.RENDER
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isInGame() && mc.field_1690 != null) {
         this.originalGamma = (Double)mc.field_1690.method_42473().method_41753();
         this.hadNightVisionBefore = mc.field_1724 != null && mc.field_1724.method_6059(class_1294.field_5925);
         this.lastAppliedGamma = this.originalGamma;
         this.setGammaRaw(256.0);
         this.ensureNightVisionBoost();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (mc != null && mc.field_1690 != null) {
         double var1 = (Double)mc.field_1690.method_42473().method_41753();
         double var3 = this.originalGamma;
         if (!(Math.abs(var1 - var3) < 1.0E-4)) {
            ((MixinSimpleOption)mc.field_1690.method_42473()).vieSetRawValue(var3);
            if (mc.field_1724 != null && !this.hadNightVisionBefore) {
               mc.field_1724.method_6016(class_1294.field_5925);
            }

            this.hadNightVisionBefore = false;
            this.lastAppliedGamma = var3;
         }
      }
   }

   @Override
   public void onTick() {
      if (this.isInGame()) {
         switch ((FullBright.Mode)this.mode.get()) {
            case GAMMA:
               this.setGammaRaw(256.0);
               this.ensureNightVisionBoost();
               break;
            case NIGHT_VISION:
               ((MixinSimpleOption)mc.field_1690.method_42473()).vieSetRawValue(this.originalGamma);
               this.ensureNightVisionBoost();
         }
      }
   }

   @Override
   public void onGameOptionsLoad() {
      if (mc != null && mc.field_1690 != null && this.isEnabled()) {
         switch ((FullBright.Mode)this.mode.get()) {
            case GAMMA:
               this.setGammaRaw(256.0);
               this.ensureNightVisionBoost();
               break;
            case NIGHT_VISION:
               this.ensureNightVisionBoost();
         }
      }
   }

   private void setGammaRaw(double value) {
      if (mc != null && mc.field_1690 != null) {
         if (this.lastAppliedGamma != value) {
            ((MixinSimpleOption)mc.field_1690.method_42473()).vieSetRawValue(value);
            this.lastAppliedGamma = value;
         }
      }
   }

   @Override
   public String getInfo() {
      return ((FullBright.Mode)this.mode.get()).name();
   }

   public boolean isGammaMode() {
      return this.isEnabled() && this.mode.get() == FullBright.Mode.GAMMA;
   }

   private void ensureNightVisionBoost() {
      if (mc.field_1724 != null) {
         class_1293 var1 = mc.field_1724.method_6112(class_1294.field_5925);
         if (var1 == null || var1.method_5584() < (480)) {
            mc.field_1724.method_6092(new class_1293(class_1294.field_5925, 880, 0, false, false, false));
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

   public enum Mode {
      GAMMA,
      NIGHT_VISION;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static FullBright.Mode[] $values() {
         FullBright.Mode[] var10000 = new FullBright.Mode[2];
         var10000[0] = GAMMA;
         var10000[1] = NIGHT_VISION;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(28);
         GAMMA = new FullBright.Mode();
         var10002 = new byte[12];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(91);
         var10002[3] = (byte)(-106);
         var10002[4] = (byte)(9);
         var10002[5] = (byte)(93);
         var10002[6] = (byte)(-6);
         var10002[7] = (byte)(63);
         var10002[8] = (byte)(2);
         var10002[9] = (byte)(-21);
         var10002[10] = (byte)(-85);
         var10002[11] = (byte)(-72);
         NIGHT_VISION = new FullBright.Mode();
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
