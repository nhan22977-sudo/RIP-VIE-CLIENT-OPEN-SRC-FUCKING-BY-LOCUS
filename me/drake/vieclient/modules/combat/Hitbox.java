package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.TargetMarginEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import net.minecraft.class_1657;

public final class Hitbox extends Module {
   private final DoubleSetting expand = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Expand",
         "Hitbox expansion amount",
         0.5,
         0.0,
         2.0,
         0.05
      )
   );
   private final BoolSetting enableRender = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Enable Render",
         "Enable render visualization",
         true
      )
   );
   private final BoolSetting randomDelay = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Random Delay",
         "Randomly vary delay",
         false
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Hitbox() {
      super(
         "HitBox",
         "Expands a player's hitbox.",
         Category.COMBAT
      );
      this.enableRender.setVisibleInGui(false);
      this.randomDelay.setVisibleInGui(false);
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @EventHandler
   public void onTargetMargin(TargetMarginEvent event) {
      if (event.entity instanceof class_1657) {
         event.cir.setReturnValue((float)((Double)this.expand.get()).doubleValue());
      }
   }

   public double getHitboxExpansion() {
      return !this.enableRender.get() ? 0.0 : (Double)this.expand.get();
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
