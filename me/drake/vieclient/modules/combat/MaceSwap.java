package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.EnchantmentUtil;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1743;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_3966;
import net.minecraft.class_9334;

public class MaceSwap extends Module {
   private final BoolSetting enableWindBurst = (BoolSetting)this.addSetting(
      new BoolSetting(
         "WindBurst",
         "Prioritise Wind Burst enchanted weapons",
         true
      )
   );
   private final BoolSetting enableBreach = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Breach",
         "Prioritise Breach enchanted weapons",
         true
      )
   );
   private final BoolSetting onlySword = (BoolSetting)this.addSetting(
      new BoolSetting(
         "OnlySword",
         "Only trigger when holding a sword",
         false
      )
   );
   private final BoolSetting onlyAxe = (BoolSetting)this.addSetting(
      new BoolSetting(
         "OnlyAxe",
         "Only trigger when holding an axe",
         false
      )
   );
   private final BoolSetting switchBack = (BoolSetting)this.addSetting(
      new BoolSetting(
         "SwitchBack",
         "Return to original slot after trigger release",
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
   private final IntSetting switchDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "SwitchDelay",
         "Ticks before swapping back after release",
         0,
         0,
         20
      )
   );
   private final BoolSetting debug = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Debug",
         "Log swap events to console",
         false
      )
   );
   private int previousSlot = -1;
   private int restoreClock = -1;
   private boolean attacking = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MaceSwap() {
      super(
         "MaceSwap",
         "Swaps to a mace when attack is held - for instant mace smashes",
         Category.COMBAT
      );
      this.enableWindBurst.setVisibleInGui(false);
      this.enableBreach.setVisibleInGui(false);
      this.randomDelay.setVisibleInGui(false);
      this.debug.setVisibleInGui(false);
      this.switchDelay.visibleWhen(this.switchBack::get);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.reset();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.previousSlot != (-1) && mc.field_1724 != null) {
         InventoryUtil.swap(this.previousSlot);
      }

      this.reset();
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      this.reset();
   }

   @EventHandler
   public void onGameJoin(GameJoinEvent event) {
      this.reset();
   }

   public boolean isSwapActive() {
      return this.attacking && this.previousSlot != (-1);
   }

   @EventHandler
   public void onTickPre(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 == null || mc.field_1687 == null) {
            this.reset();
         } else if (mc.field_1755 != null) {
            this.reset();
         } else {
            boolean var2 = mc.field_1690.field_1886.method_1434();
            boolean var3 = mc.field_1765 instanceof class_3966;
            if (this.restoreClock >= 0) {
               if (var2) {
                  this.restoreClock = (Integer)this.switchDelay.get()
                     + (this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0);
               } else {
                  if (this.restoreClock <= 0) {
                     if ((Boolean)this.switchBack.get() && this.previousSlot != (-1)) {
                        if ((Boolean)this.debug.get()) {
                           System.out.println("[MaceSwap] Swapping back to slot " + this.previousSlot);
                        }

                        InventoryUtil.swap(this.previousSlot);
                     }

                     this.reset();
                     return;
                  }

                  this.restoreClock--;
               }
            }

            if (var2 && var3) {
               if (!this.attacking || this.previousSlot == (-1)) {
                  if (!this.isValidWeapon()) {
                     return;
                  }

                  if (!ActionArbiter.get()
                     .tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOTOOL, (Integer)this.switchDelay.get() + (2))) {
                     return;
                  }

                  this.previousSlot = mc.field_1724.method_31548().method_67532();
                  boolean var4 = false;
                  if ((Boolean)this.enableWindBurst.get() || (Boolean)this.enableBreach.get()) {
                     var4 = InventoryUtil.swapStack(stack -> {
                        if (!stack.method_7960() && stack.method_31574(class_1802.field_49814)) {
                           boolean var2x = (Boolean)this.enableWindBurst.get() && EnchantmentUtil.hasEnchantment(stack, class_1893.field_50159);
                           boolean var3x = (Boolean)this.enableBreach.get() && EnchantmentUtil.hasEnchantment(stack, class_1893.field_50158);
                           return var2x || var3x;
                        } else {
                           return false;
                        }
                     });
                  }

                  if (!var4) {
                     var4 = InventoryUtil.swap(class_1802.field_49814);
                  }

                  if ((Boolean)this.debug.get()) {
                     System.out.println("[MaceSwap] Swap result: " + var4);
                  }

                  if (!var4) {
                     this.previousSlot = -1;
                     ActionArbiter.get().release(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOTOOL);
                     return;
                  }

                  this.attacking = true;
                  this.restoreClock = (Integer)this.switchDelay.get();
               }
            } else {
               if (this.attacking && this.restoreClock < 0 && this.previousSlot != (-1)) {
                  this.restoreClock = (Integer)this.switchDelay.get();
               }

               this.attacking = false;
            }
         }
      }
   }

   private boolean isValidWeapon() {
      if (mc.field_1724 == null) {
         return false;
      } else {
         class_1792 var1 = mc.field_1724.method_6047().method_7909();
         boolean var2 = var1.method_57347().method_57832(class_9334.field_55878);
         boolean var3 = var1 instanceof class_1743;
         if ((Boolean)this.onlySword.get() && (Boolean)this.onlyAxe.get()) {
            return var2 || var3;
         } else if ((Boolean)this.onlySword.get()) {
            return var2;
         } else {
            return this.onlyAxe.get() ? var3 : var2 || var3;
         }
      }
   }

   private void reset() {
      if (this.previousSlot != (-1)) {
         ActionArbiter.get().release(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOTOOL);
      }

      this.previousSlot = -1;
      this.restoreClock = -1;
      this.attacking = false;
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
