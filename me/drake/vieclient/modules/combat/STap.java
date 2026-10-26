package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.AttackEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BindSetting;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.KeyUtils;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_3675;

public class STap extends Module {
   private final BindSetting triggerBind = (BindSetting)this.addSetting(
      new BindSetting(
         "Trigger Bind",
         "Hold key to enable tap (-1 = Always active)",
         -1
      )
   );
   private final BoolSetting smart = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Smart",
         "Intelligently adjusts S-tap timing, spacing, and cooldown to combo lock opponents",
         true
      )
   );
   private final BoolSetting smartSpacing = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Smart Spacing",
         "Skips S-tap if enemy is far (> 2.65m) to prevent dropping combos, taps when close",
         true
      )
   );
   private final BoolSetting cooldownCheck = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Cooldown Check",
         "Only tap when attack cooldown is ready for sprint knockback (>= 85%)",
         true
      )
   );
   private final BoolSetting targetHurtCheck = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Target Hurt Check",
         "Only tap when the target is actually damaged (avoids i-frame waste)",
         true
      )
   );
   private final BoolSetting dynamicHold = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Dynamic Hold",
         "Holds S longer when opponent is rushing in or too close",
         true
      )
   );
   private final BoolSetting humanize = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Humanize",
         "Adds natural human-like micro-variations to tap timing to bypass anti-cheats",
         true
      )
   );
   private final DoubleSetting minDistance = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Min Distance",
         "Minimum distance to trigger S-tap (blocks)",
         0.0,
         0.0,
         3.0,
         0.1
      )
   );
   private final DoubleSetting maxDistance = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Max Distance",
         "Maximum distance to trigger S-tap (blocks)",
         2.8,
         1.8,
         4.0,
         0.1
      )
   );
   private final IntSetting chance = (IntSetting)this.addSetting(
      new IntSetting(
         "Chance",
         "Chance to trigger S-Tap on hit (%)",
         100,
         1,
         100,
         1
      )
   );
   private final IntSetting delay = (IntSetting)this.addSetting(
      new IntSetting(
         "Delay",
         "Delay before pressing S (ticks)",
         0,
         0,
         5,
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
   private final IntSetting holdTicks = (IntSetting)this.addSetting(
      new IntSetting(
         "Hold Ticks",
         "Duration to hold S pressed (ticks)",
         1,
         1,
         5,
         1
      )
   );
   private final BoolSetting onlyGround = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Only Ground",
         "Only S-Tap when player is on the ground",
         false
      )
   );
   private final BoolSetting onlySprinting = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Only Sprinting",
         "Only S-Tap when player is currently sprinting",
         true
      )
   );
   private int delayCounter = -1;
   private int holdCounter = -1;
   private boolean tapping = false;
   private int comboStreak = 0;
   private long lastHitTime = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public STap() {
      super(
         "STap",
         "Automatically taps S on attack to reset sprint and maintain spacing",
         Category.COMBAT
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetState();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.restoreKey();
      this.resetState();
   }

   @EventHandler
   public void onAttack(AttackEvent event) {
      if (this.isTriggerActive()) {
         if (mc.field_1724 != null && mc.field_1687 != null) {
            if (event.getTarget() != null && event.getTarget() instanceof class_1309 var2) {
               if (!(Boolean)this.onlySprinting.get() || mc.field_1724.method_5624()) {
                  if (!(Boolean)this.onlyGround.get() || mc.field_1724.method_24828()) {
                     if (mc.field_1690 != null && mc.field_1690.field_1894.method_1434()) {
                        if ((Integer)this.chance.get() >= 100 || !(Math.random() * 100.0 > ((Integer)this.chance.get()).intValue())) {
                           if ((Boolean)this.smart.get()) {
                              if ((Boolean)this.cooldownCheck.get()) {
                                 float var12 = mc.field_1724.method_7261(0.5F);
                                 if (var12 < 0.85F) {
                                    return;
                                 }
                              }

                              if ((Boolean)this.targetHurtCheck.get() && var2.field_6235 > 2) {
                                 return;
                              }

                              double var13 = mc.field_1724.method_5739(var2);
                              if (var13 < (Double)this.minDistance.get() || var13 > (Double)this.maxDistance.get()) {
                                 return;
                              }

                              class_243 var5 = new class_243(
                                 var2.method_23317() - mc.field_1724.method_23317(),
                                 var2.method_23318() - mc.field_1724.method_23318(),
                                 var2.method_23321() - mc.field_1724.method_23321()
                              );
                              double var6 = var5.method_1033();
                              double var8 = 0.0;
                              if (var6 > 0.001) {
                                 class_243 var10 = var5.method_1021(1.0 / var6);
                                 var8 = -var2.method_18798().method_1026(var10);
                              }

                              if ((Boolean)this.smartSpacing.get() && var13 >= 2.65 && var8 <= 0.0) {
                                 this.updateComboStreak();
                                 return;
                              }

                              int var15 = (Integer)this.delay.get();
                              if ((Boolean)this.humanize.get() && var15 == 0 && Math.random() < 0.15) {
                                 var15 = 1;
                              }

                              if ((Boolean)this.randomDelay.get()) {
                                 var15 += ThreadLocalRandom.current().nextInt(0, 2);
                              }

                              int var11 = (Integer)this.holdTicks.get();
                              if ((Boolean)this.dynamicHold.get()) {
                                 if (var13 < 2.0 || var8 > 0.08) {
                                    var11 = Math.max(var11, 2);
                                 } else if (var13 >= 2.4) {
                                    var11 = 1;
                                 }
                              }

                              this.delayCounter = var15;
                              this.holdCounter = var11;
                              this.tapping = false;
                              this.updateComboStreak();
                           } else {
                              int var14 = (Integer)this.delay.get();
                              if (var14 > 0 && Math.random() < 0.3) {
                                 var14++;
                              }

                              if ((Boolean)this.randomDelay.get()) {
                                 var14 += ThreadLocalRandom.current().nextInt(0, 2);
                              }

                              this.delayCounter = var14;
                              this.holdCounter = (Integer)this.holdTicks.get();
                              this.tapping = false;
                              this.updateComboStreak();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1690 != null) {
            if (mc.field_1724.field_6235 > 0) {
               this.comboStreak = 0;
            }

            if (this.delayCounter > 0) {
               this.delayCounter--;
            } else if (this.delayCounter == 0 && !this.tapping) {
               this.tapping = true;
               this.delayCounter = -1;
               mc.field_1690.field_1881.method_23481(true);
            } else {
               if (this.tapping) {
                  if (this.holdCounter > 0) {
                     this.holdCounter--;
                     mc.field_1690.field_1881.method_23481(true);
                     return;
                  }

                  this.finishTap();
               }
            }
         } else {
            this.resetState();
         }
      }
   }

   private void finishTap() {
      this.restoreKey();
      this.resetState();
   }

   private void restoreKey() {
      if (mc.field_1690 != null && mc.field_1690.field_1881 != null) {
         mc.field_1690.field_1881.method_23481(this.isBackPhysicallyPressed());
      }
   }

   private boolean isBackPhysicallyPressed() {
      if (mc.method_22683() != null && mc.field_1690 != null && mc.field_1690.field_1881 != null) {
         int var1 = mc.field_1690.field_1881.method_1429().method_1444();

         try {
            var1 = class_3675.method_15981(mc.field_1690.field_1881.method_1428()).method_1444();
         } catch (Throwable var3) {
         }

         return var1 > 0 && KeyUtils.isKeyPressed(var1);
      } else {
         return false;
      }
   }

   private boolean isTriggerActive() {
      int var1 = (Integer)this.triggerBind.get();
      return var1 == (-1) || var1 == 0 || KeyUtils.isKeyPressed(var1);
   }

   private void updateComboStreak() {
      long var1 = System.currentTimeMillis();
      if (var1 - this.lastHitTime <= 1500L) {
         this.comboStreak++;
      } else {
         this.comboStreak = 1;
      }

      this.lastHitTime = var1;
   }

   public int getComboStreak() {
      if (System.currentTimeMillis() - this.lastHitTime > 1500L) {
         this.comboStreak = 0;
      }

      return this.comboStreak;
   }

   public boolean isTapping() {
      return this.tapping;
   }

   private void resetState() {
      this.delayCounter = -1;
      this.holdCounter = -1;
      this.tapping = false;
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
