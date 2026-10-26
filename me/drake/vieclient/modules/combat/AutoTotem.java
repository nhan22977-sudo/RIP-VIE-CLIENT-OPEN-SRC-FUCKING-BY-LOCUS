package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1802;

public final class AutoTotem extends Module {
   private final IntSetting delay = (IntSetting)this.addSetting(
      new IntSetting(
         "Delay",
         "Delay between totem swaps",
         1,
         0,
         5,
         1
      )
   );
   private final BoolSetting randomDelay = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Random Delay",
         "Randomly vary delay",
         true
      )
   );
   private int delayCounter;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoTotem() {
      super(
         "Auto Totem",
         "Automatically holds totem in your off hand",
         Category.COMBAT
      );
      this.randomDelay.setVisibleInGui(false);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.delayCounter = 0;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.delayCounter = 0;
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      this.delayCounter = 0;
   }

   @EventHandler
   public void onGameJoin(GameJoinEvent event) {
      this.delayCounter = 0;
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (mc.field_1724 != null && mc.field_1761 != null && mc.field_1755 == null) {
         int var2 = (Integer)this.delay.get() + (this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0);
         if (mc.field_1724.method_6079().method_31574(class_1802.field_8288)) {
            this.delayCounter = var2;
         } else if (this.delayCounter > 0) {
            this.delayCounter--;
         } else {
            int var3 = this.findItemSlot(class_1802.field_8288);
            if (var3 != (-1)) {
               mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, convertSlotIndex(var3), 40, class_1713.field_7791, mc.field_1724);
               this.delayCounter = var2;
            }
         }
      }
   }

   public int findItemSlot(class_1792 item) {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var2 = 0; var2 < (36); var2++) {
         if (mc.field_1724.method_31548().method_5438(var2).method_31574(item)) {
            return var2;
         }
      }

      return -1;
   }

   private static int convertSlotIndex(int slotIndex) {
      return slotIndex < (9) ? (36) + slotIndex : slotIndex;
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
