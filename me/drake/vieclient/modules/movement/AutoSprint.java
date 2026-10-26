package me.drake.vieclient.modules.movement;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;

public final class AutoSprint extends Module {
   private final BoolSetting omniDirectional = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Omni Directional",
         "Sprint in all directions",
         false
      )
   );
   private final BoolSetting keepSprint = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Keep Sprint",
         "Keep sprinting after attacking",
         true
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoSprint() {
      super(
         "Auto Sprint",
         "Automatically sprints when moving forward",
         Category.MOVEMENT
      );
      this.keepSprint.setVisibleInGui(false);
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1690 != null) {
            boolean var2 = this.omniDirectional.isEnabled()
               ? mc.field_1690.field_1894.method_1434()
                  || mc.field_1690.field_1881.method_1434()
                  || mc.field_1690.field_1913.method_1434()
                  || mc.field_1690.field_1849.method_1434()
               : mc.field_1690.field_1894.method_1434();
            if (var2
               && !mc.field_1724.method_5624()
               && !mc.field_1724.method_5715()
               && !mc.field_1724.method_6115()
               && !mc.field_1724.field_5976
               && mc.field_1724.method_7344().method_7586() > (6)
               && ActionArbiter.get().tryAcquire(ActionArbiter.Resource.SPRINT, ActionArbiter.Owner.AUTOSPRINT)) {
               mc.field_1724.method_5728(true);
            }

            if (this.keepSprint.isEnabled() && mc.field_1724.method_5624()) {
               if (mc.field_1724.field_34927) {
                  return;
               }

               if ((mc.field_1690.field_1894.method_1434() || this.omniDirectional.isEnabled())
                  && ActionArbiter.get().tryAcquire(ActionArbiter.Resource.SPRINT, ActionArbiter.Owner.AUTOSPRINT)) {
                  mc.field_1724.method_5728(true);
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
