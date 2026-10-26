package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;

public final class AutoJumpReset extends Module {
   private final IntSetting chance = (IntSetting)this.addSetting(
      new IntSetting(
         "Chance",
         "Chance to jump on hit (%)",
         100,
         0,
         100,
         1
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

   public AutoJumpReset() {
      super(
         "Auto Jump Reset",
         "Automatically jumps for you when you get hit so you take less knockback (not good for crystal pvp)",
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
   public void onTick(TickEvent event) {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         if (mc.field_1755 == null) {
            if (!mc.field_1724.method_6115()) {
               if (mc.field_1724.method_24828()) {
                  int var2 = mc.field_1724.field_6254 > 0 ? mc.field_1724.field_6254 - 1 : 9;
                  if ((Boolean)this.randomDelay.get() && ThreadLocalRandom.current().nextBoolean()) {
                     var2 = Math.max(0, var2 - 1);
                  }

                  if (mc.field_1724.field_6235 == var2) {
                     if ((Integer)this.chance.get() < (100) && Math.random() * 100.0 > ((Integer)this.chance.get()).intValue()) {
                        return;
                     }

                     mc.field_1724.method_6043();
                  }
               }
            }
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
}
