package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1297;
import net.minecraft.class_1802;
import net.minecraft.class_2663;

public class AutoDoubleHand extends Module {
   private final BoolSetting onPop = (BoolSetting)this.addSetting(
      new BoolSetting(
         "OnPop",
         "Switch totem when you pop",
         true
      )
   );
   private final BoolSetting onHealth = (BoolSetting)this.addSetting(
      new BoolSetting(
         "OnHealth",
         "Switch totem when health is low",
         true
      )
   );
   private final DoubleSetting healthThreshold = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Health",
         "Health threshold to trigger switch",
         10.0,
         0.0,
         20.0
      )
   );
   private final BoolSetting useSecondTotem = (BoolSetting)this.addSetting(
      new BoolSetting(
         "UseSecondTotem",
         "Use the second totem in hotbar",
         true
      )
   );
   private final BoolSetting randomDelay = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Random Delay",
         "Randomly vary delay",
         true
      )
   );
   private int previousSlot = -1;
   private float lastHealth = 0.0F;
   private boolean hasTriggered = false;
   private int switchDelayTicks = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoDoubleHand() {
      super(
         "AutoDoubleHand",
         "Auto switches to totems on pop or low health",
         Category.COMBAT
      );
      this.useSecondTotem.setVisibleInGui(false);
      this.randomDelay.setVisibleInGui(false);
      this.healthThreshold.visibleWhen(this.onHealth::get);
   }

   @Override
   public void onEnable() {
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.reset();
      super.onDisable();
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      this.reset();
   }

   @EventHandler
   public void onGameJoin(GameJoinEvent event) {
      this.reset();
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1687 != null && mc.field_1724 != null && mc.field_1755 == null) {
            if (this.switchDelayTicks > 0) {
               this.switchDelayTicks--;
               if (this.switchDelayTicks == 0) {
                  this.executeSwap();
               }
            }

            if ((Boolean)this.onHealth.get()) {
               float var2 = mc.field_1724.method_6032();
               if (var2 <= (Double)this.healthThreshold.get() && !this.hasTriggered) {
                  this.switchToTotem();
                  this.hasTriggered = true;
               } else if (var2 > (Double)this.healthThreshold.get()) {
                  this.hasTriggered = false;
               }

               this.lastHealth = var2;
            }
         }
      }
   }

   @EventHandler
   public void onPacketReceive(ReceivePacketEvent event) {
      if ((Boolean)this.onPop.get()) {
         if (mc.field_1687 != null && mc.field_1724 != null) {
            if (event.getPacket() instanceof class_2663 var2 && var2.method_11470() == (35)) {
               class_1297 var4 = var2.method_11469(mc.field_1687);
               if (var4 != null && var4.equals(mc.field_1724)) {
                  this.switchToTotem();
               }
            }
         }
      }
   }

   private void switchToTotem() {
      if ((Boolean)this.randomDelay.get() && this.switchDelayTicks == 0) {
         this.switchDelayTicks = ThreadLocalRandom.current().nextInt(1, 3);
      } else {
         this.executeSwap();
      }
   }

   private void executeSwap() {
      int var1 = this.useSecondTotem.get() ? this.findSecondTotemInHotbar() : this.findTotemInHotbar();
      if (var1 != (-1)) {
         if (!ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.EMERGENCY, 2)) {
            return;
         }

         if (this.previousSlot == (-1)) {
            this.previousSlot = mc.field_1724.method_31548().method_67532();
         }

         InventoryUtil.swap(var1);
      }
   }

   private int findSecondTotemInHotbar() {
      int var1 = 0;

      for (int var2 = 0; var2 < (9); var2++) {
         if (mc.field_1724.method_31548().method_5438(var2).method_31574(class_1802.field_8288)) {
            if (++var1 == (2)) {
               return var2;
            }
         }
      }

      return -1;
   }

   private int findTotemInHotbar() {
      for (int var1 = 0; var1 < (9); var1++) {
         if (mc.field_1724.method_31548().method_5438(var1).method_31574(class_1802.field_8288)) {
            return var1;
         }
      }

      return -1;
   }

   private void reset() {
      if (this.previousSlot != (-1)) {
         ActionArbiter.get().release(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.EMERGENCY);
      }

      this.previousSlot = -1;
      this.lastHealth = 0.0F;
      this.hasTriggered = false;
      this.switchDelayTicks = 0;
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
