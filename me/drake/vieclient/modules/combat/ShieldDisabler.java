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
import me.drake.vieclient.util.InventoryUtil;
import me.drake.vieclient.util.WorldUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1802;
import net.minecraft.class_239;
import net.minecraft.class_3966;

public final class ShieldDisabler extends Module {
   private final IntSetting hitDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "Hit Delay",
         "Delay between hits",
         0,
         0,
         20,
         1
      )
   );
   private final IntSetting switchDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "Switch Delay",
         "Delay before switching",
         0,
         0,
         20,
         1
      )
   );
   private final BoolSetting switchBack = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Switch Back",
         "Switch back to previous slot",
         true
      )
   );
   private final BoolSetting stun = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Stun",
         "Hit twice to stun",
         false
      )
   );
   private final BoolSetting clickSimulate = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Click Simulation",
         "Simulate mouse click",
         false
      )
   );
   private final BoolSetting requireHoldAxe = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Hold Axe",
         "Only work when holding axe",
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
   private int previousSlot;
   private int hitClock;
   private int switchClock;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ShieldDisabler() {
      super(
         "Shield Disabler",
         "Automatically disables your opponents shield",
         Category.COMBAT
      );
   }

   @Override
   public void onEnable() {
      this.hitClock = (Integer)this.hitDelay.get();
      this.switchClock = (Integer)this.switchDelay.get();
      this.previousSlot = -1;
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (this.previousSlot != (-1) && mc.field_1724 != null && (Boolean)this.switchBack.get()) {
         InventoryUtil.swap(this.previousSlot);
      }

      this.previousSlot = -1;
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      this.previousSlot = -1;
   }

   @EventHandler
   public void onGameJoin(GameJoinEvent event) {
      this.previousSlot = -1;
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1761 != null) {
         if (mc.field_1755 == null) {
            if (!(Boolean)this.requireHoldAxe.get() || mc.field_1724.method_6047().method_7909() instanceof class_1743) {
               class_239 var2 = mc.field_1765;
               if (mc.field_1765 instanceof class_3966) {
                  class_3966 var3 = (class_3966)var2;
                  class_1297 var4 = var3.method_17782();
                  if (mc.field_1724.method_6115()) {
                     return;
                  }

                  if (var4 instanceof class_1657 var5) {
                     if (WorldUtils.isShieldFacingAway(var5)) {
                        int var7 = this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0;
                        this.hitClock = (Integer)this.hitDelay.get() + var7;
                        this.switchClock = (Integer)this.switchDelay.get() + var7;
                        this.previousSlot = -1;
                        return;
                     }

                     if (var5.method_24518(class_1802.field_8255) && var5.method_6115()) {
                        if (this.switchClock > 0) {
                           if (this.previousSlot == (-1)) {
                              this.previousSlot = mc.field_1724.method_31548().method_67532();
                           }

                           this.switchClock--;
                           return;
                        }

                        if (this.selectAxe()) {
                           if (this.hitClock > 0) {
                              this.hitClock--;
                           } else {
                              WorldUtils.hitEntity(var5, true);
                              if ((Boolean)this.stun.get()) {
                                 WorldUtils.hitEntity(var5, true);
                              }

                              int var6 = this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0;
                              this.hitClock = (Integer)this.hitDelay.get() + var6;
                              this.switchClock = (Integer)this.switchDelay.get() + var6;
                           }
                        }
                     } else if (this.previousSlot != (-1)) {
                        if ((Boolean)this.switchBack.get()) {
                           InventoryUtil.swap(this.previousSlot);
                        }

                        this.previousSlot = -1;
                     }
                  }
               }
            }
         }
      }
   }

   private boolean selectAxe() {
      return mc.field_1724 != null && mc.field_1724.method_6047().method_7909() instanceof class_1743
         ? true
         : InventoryUtil.swapStack(stack -> stack.method_7909() instanceof class_1743);
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
