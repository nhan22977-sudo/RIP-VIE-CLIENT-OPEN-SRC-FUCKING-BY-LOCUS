package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.module.ModuleManager;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_3489;
import net.minecraft.class_3966;

public class SpearSwap extends Module {
   private final IntSetting switchDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "SwitchDelay",
         "Ticks before swapping back after the trigger is released",
         0,
         0,
         20
      )
   );
   private final BoolSetting onlyRelease = (BoolSetting)this.addSetting(
      new BoolSetting(
         "OnlyRelease",
         "Only swap back when the attack button is released (otherwise swap back on next press)",
         false
      )
   );
   private final BoolSetting randomDelay = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Random Delay",
         "Randomly vary delay",
         false
      )
   );
   private final BoolSetting debug = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Debug",
         "Log swap events to console",
         false
      )
   );
   private int prevSlot = -1;
   private int restoreClock = -1;
   private boolean attacking = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SpearSwap() {
      super(
         "SpearSwap",
         "Swaps to a spear while you hold left-click - for instant lunge combos",
         Category.COMBAT
      );
      this.randomDelay.setVisibleInGui(false);
      this.debug.setVisibleInGui(false);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.reset();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.prevSlot != (-1) && mc.field_1724 != null) {
         InventoryUtil.swap(this.prevSlot);
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

   @EventHandler
   public void onTickPre(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
            this.reset();
         } else if (mc.field_1755 != null) {
            this.reset();
         } else {
            boolean var2 = mc.field_1690.field_1886.method_1434();
            Module var3 = VieClientMod.getInstance()
               .getModuleManager()
               .getModuleByName("MaceSwap");
            boolean var4 = var3 != null && var3.isEnabled() && mc.field_1765 instanceof class_3966 && this.maceIsCurrentlySwapped();
            if (var4) {
               this.attacking = false;
               this.restoreClock = -1;
               this.prevSlot = -1;
            } else {
               if (this.restoreClock >= 0) {
                  if (var2 && (Boolean)this.onlyRelease.get()) {
                     int var5 = this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0;
                     this.restoreClock = (Integer)this.switchDelay.get() + var5;
                  } else {
                     if (this.restoreClock <= 0) {
                        if ((Boolean)this.debug.get()) {
                           System.out.println("[SpearSwap] Swapping back to slot " + this.prevSlot);
                        }

                        InventoryUtil.swap(this.prevSlot);
                        this.reset();
                        return;
                     }

                     this.restoreClock--;
                  }
               }

               if (var2) {
                  int var8 = mc.field_1724.method_31548().method_67532();
                  int var6 = this.findSpearSlot();
                  if (var6 != -1 && var6 != var8) {
                     if (!this.attacking || this.prevSlot == -1) {
                        if (!ActionArbiter.get()
                           .tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOTOOL, (Integer)this.switchDelay.get() + 2)) {
                           return;
                        }

                        if ((Boolean)this.debug.get()) {
                           System.out.println("[SpearSwap] Attack pressed — swapping from slot " + var8 + " to spear slot " + var6);
                        }

                        this.prevSlot = var8;
                        InventoryUtil.swap(var6);
                     }

                     this.attacking = true;
                     int var7 = this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0;
                     this.restoreClock = (Integer)this.switchDelay.get() + var7;
                  }
               } else {
                  if (this.attacking && this.restoreClock < 0 && this.prevSlot != -1) {
                     int var9 = this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0;
                     this.restoreClock = (Integer)this.switchDelay.get() + var9;
                  }

                  this.attacking = false;
               }
            }
         }
      }
   }

   private int findSpearSlot() {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var1 = 0; var1 < (9); var1++) {
         if (mc.field_1724.method_31548().method_5438(var1).method_31573(class_3489.field_63257)) {
            return var1;
         }
      }

      return -1;
   }

   private boolean maceIsCurrentlySwapped() {
      ModuleManager var10000 = VieClientMod.getInstance().getModuleManager();
      byte[] var10001 = new byte[8];
      var10001[0] = (byte)(122);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(127);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(-78);
      var10001[5] = (byte)(51);
      var10001[6] = (byte)(-54);
      var10001[7] = 0;
      return var10000.getModuleByName($$v_s(var10001, 6388525775421952018L)) instanceof MaceSwap var2 && var2.isSwapActive();
   }

   private void reset() {
      if (this.prevSlot != (-1)) {
         ActionArbiter.get().release(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOTOOL);
      }

      this.prevSlot = -1;
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
