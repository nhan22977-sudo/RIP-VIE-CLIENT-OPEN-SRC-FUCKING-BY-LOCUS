package me.drake.vieclient.modules.world;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_239;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public class AutoPlace extends Module {
   private final IntSetting delay = (IntSetting)this.addSetting(
      new IntSetting(
         "Delay",
         "Ticks between placements",
         2,
         0,
         20,
         1
      )
   );
   private final BoolSetting onlyBlocks = (BoolSetting)this.addSetting(
      new BoolSetting(
         "OnlyBlocks",
         "Only place when holding blocks",
         true
      )
   );
   private int delayTicks = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoPlace() {
      super(
         "AutoPlace",
         "Auto-places blocks on timer",
         Category.WORLD
      );
      this.onlyBlocks.setVisibleInGui(false);
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (this.isInGame()) {
            if (this.delayTicks > 0) {
               this.delayTicks--;
            } else if (!(Boolean)this.onlyBlocks.get() || mc.field_1724.method_6047().method_7909() instanceof class_1747) {
               class_239 var2 = mc.field_1765;
               if (var2 != null && var2.method_17783() == class_240.field_1332) {
                  class_3965 var3 = (class_3965)var2;
                  mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, var3);
                  mc.field_1724.method_6104(class_1268.field_5808);
                  this.delayTicks = (Integer)this.delay.get();
               }
            }
         }
      }
   }

   @Override
   public String getInfo() {
      return this.delay.get() + "t";
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
