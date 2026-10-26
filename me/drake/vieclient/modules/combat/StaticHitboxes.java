package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.TargetPoseEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import net.minecraft.class_1657;
import net.minecraft.class_4050;

public final class StaticHitboxes extends Module {
   private final BoolSetting randomDelay = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Random Delay",
         "Randomly vary delay",
         false
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public StaticHitboxes() {
      super(
         "Static HitBoxes",
         "Expands a Player's Hitbox",
         Category.COMBAT
      );
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
   public void onTargetPose(TargetPoseEvent event) {
      if (this.isEnabled() && event.entity instanceof class_1657 && !((class_1657)event.entity).method_7340()) {
         event.cir.setReturnValue(class_4050.field_18076);
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
}
