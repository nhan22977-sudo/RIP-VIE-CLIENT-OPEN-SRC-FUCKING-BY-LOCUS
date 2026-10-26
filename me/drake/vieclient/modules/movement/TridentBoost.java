package me.drake.vieclient.modules.movement;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;

public final class TridentBoost extends Module {
   private static volatile TridentBoost INSTANCE;
   private final BoolSetting noWater = (BoolSetting)this.addSetting(
      new BoolSetting(
         "No Water",
         "Allow using riptide out of water or without rain",
         true
      )
   );
   private final DoubleSetting chargeScale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Charge Scale",
         "Scale minimum charge time before riptide can launch (0 = instant, 1 = vanilla)",
         0.0,
         0.0,
         1.0,
         0.1
      )
   );
   private final DoubleSetting boost = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Boost Multiplier",
         "Velocity multiplier when launching with riptide",
         2.0,
         0.1,
         10.0,
         0.1
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public TridentBoost() {
      super(
         "Trident Boost",
         "Use tridents out of water, charge them faster, and boost riptide velocity.",
         Category.MOVEMENT
      );
      INSTANCE = this;
   }

   public static TridentBoost getInstance() {
      return INSTANCE;
   }

   public BoolSetting getNoWater() {
      return this.noWater;
   }

   public DoubleSetting getChargeScale() {
      return this.chargeScale;
   }

   public DoubleSetting getBoost() {
      return this.boost;
   }

   public static boolean allowOutOfWater() {
      TridentBoost var0 = INSTANCE;
      return var0 != null && var0.isEnabled() && var0.noWater.isEnabled();
   }

   public static int minChargeTicks(int vanilla) {
      TridentBoost var1 = INSTANCE;
      return var1 != null && var1.isEnabled() ? Math.max(1, (int)Math.round(vanilla * (Double)var1.chargeScale.get())) : vanilla;
   }

   public static double riptideMultiplier() {
      TridentBoost var0 = INSTANCE;
      return var0 != null && var0.isEnabled() ? (Double)var0.boost.get() : 1.0;
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
