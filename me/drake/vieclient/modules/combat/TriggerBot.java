package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.CoreEntry;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.AttackEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.module.ModuleManager;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.MinMaxSetting;
import me.drake.vieclient.core.setting.ModeSetting;
import net.minecraft.class_1268;
import net.minecraft.class_1296;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1530;
import net.minecraft.class_1657;
import net.minecraft.class_1675;
import net.minecraft.class_1743;
import net.minecraft.class_1792;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3966;
import net.minecraft.class_9334;
import org.lwjgl.glfw.GLFW;

public final class TriggerBot extends Module {
   private final MinMaxSetting swordThreshold = (MinMaxSetting)this.addSetting(
      new MinMaxSetting("Sword Threshold", 0.1, 1.0, 0.01, 0.9, 0.95)
   );
   private final MinMaxSetting axeThreshold = (MinMaxSetting)this.addSetting(
      new MinMaxSetting("Axe Threshold", 0.1, 1.0, 0.01, 0.9, 0.95)
   );
   private final MinMaxSetting axePostDelay = (MinMaxSetting)this.addSetting(
      new MinMaxSetting("Axe Post Delay", 1.0, 500.0, 0.5, 120.0, 120.0)
   );
   private final MinMaxSetting reactionTime = (MinMaxSetting)this.addSetting(
      new MinMaxSetting("Reaction Time", 1.0, 350.0, 0.5, 20.0, 95.0)
   );
   private final MinMaxSetting missChance = (MinMaxSetting)this.addSetting(
      new MinMaxSetting("Miss Chance", 0.0, 100.0, 1.0, 0.0, 0.0)
   );
   private final ModeSetting cooldownMode = (ModeSetting)this.addSetting(
      new ModeSetting(
         "Cooldown Mode",
         "SMART: faster when close | STRICT: full delay | NONE: instant",
         TriggerBot.CooldownMode.SMART,
         TriggerBot.CooldownMode.class
      )
   );
   private final ModeSetting critMode = (ModeSetting)this.addSetting(
      new ModeSetting(
         "Criticals",
         "SMART: crits when airborne, sprint hits on ground | STRICT: waits for crit when airborne | NONE: attacks freely",
         TriggerBot.CritMode.SMART,
         TriggerBot.CritMode.class
      )
   );
   private final BoolSetting tapSync = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Tap Sync",
         "Wait 1 tick for WTap/STap sprint reset to finish so every hit deals sprint knockback",
         true
      )
   );
   private final BoolSetting hurtTimeSync = (BoolSetting)this.addSetting(
      new BoolSetting(
         "HurtTime Sync",
         "Wait for target i-frame (hurtTime <= 1) so no damage or knockback is wasted",
         true
      )
   );
   private final BoolSetting comboMode = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Combo Persistence",
         "Eliminate reaction delay on consecutive hits in a combo to keep target locked",
         true
      )
   );
   private final BoolSetting smartRange = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Smart Raycast",
         "Subpixel crosshair raycast within reach so fast turns never drop target",
         true
      )
   );
   private final DoubleSetting reach = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Reach",
         "Maximum attack reach distance in blocks",
         3.0,
         2.5,
         4.5,
         0.1
      )
   );
   private final BoolSetting ignorePassive = (BoolSetting)this.addSetting(
      new BoolSetting(
         "No Passive",
         "Ignore passive mobs",
         true
      )
   );
   private final BoolSetting ignoreInvisible = (BoolSetting)this.addSetting(
      new BoolSetting(
         "No Invisible",
         "Ignore invisible entities",
         true
      )
   );
   private final BoolSetting ignoreCrystals = (BoolSetting)this.addSetting(
      new BoolSetting(
         "No Crystals",
         "Ignore end crystals",
         true
      )
   );
   private final BoolSetting ignoreShields = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Ignore Shields",
         "Skip attacking while target is blocking with a shield",
         false
      )
   );
   private final BoolSetting onlySwordOrAxe = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Only Sword/Axe",
         "Only trigger when holding a sword or axe",
         true
      )
   );
   private final BoolSetting onlyMouseHold = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Only Mouse Hold",
         "Only trigger while left mouse button is held",
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
   private class_1297 target = null;
   private boolean waitingForReaction = false;
   private boolean waitingForDelay = false;
   private long reactionStart = 0L;
   private long currentReaction = 0L;
   private long axeDelayStart = 0L;
   private float randomizedThreshold = 0.9F;
   private long randomizedPostDelay = 120L;
   private int comboStreak = 0;
   private long lastHitTime = 0L;
   private class_1297 lastHitTarget = null;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public TriggerBot() {
      super(
         "Trigger Bot",
         "Automatically attacks when your crosshair is on a valid target",
         Category.COMBAT,
         -1
      );
   }

   @Override
   public void onEnable() {
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.resetState();
      super.onDisable();
   }

   @EventHandler
   private void onTick(TickEvent event) {
      if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1761 != null && mc.field_1755 == null) {
         if (!mc.field_1724.method_6115()) {
            if ((Boolean)this.onlySwordOrAxe.get() && !this.isHoldingSwordOrAxe()) {
               this.resetState();
            } else if ((Boolean)this.onlyMouseHold.get() && GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 0) != 1) {
               this.resetState();
            } else {
               class_1297 var2 = this.getCrosshairTarget((Double)this.reach.get());
               if (var2 != null && this.hasTarget(var2)) {
                  if ((Boolean)this.ignoreShields.get() && var2 instanceof class_1657 var3 && var3.method_6039()) {
                     this.resetState();
                  } else {
                     this.target = var2;
                     if (!(Boolean)this.tapSync.get() || !this.isTapModuleTapping()) {
                        if (!((Boolean)this.hurtTimeSync.get() && var2 instanceof class_1309 var9) || var9.field_6235 <= 1) {
                           boolean var10 = (Boolean)this.comboMode.get()
                              && this.lastHitTarget == var2
                              && System.currentTimeMillis() - this.lastHitTime <= 1200L;
                           if (!this.waitingForReaction) {
                              this.waitingForReaction = true;
                              this.reactionStart = System.currentTimeMillis();
                              if (var10) {
                                 this.currentReaction = 0L;
                              } else {
                                 long var4 = this.reactionTime.getRandomLongInRange();
                                 TriggerBot.CooldownMode var6 = (TriggerBot.CooldownMode)this.cooldownMode.get();
                                 if (var6 == TriggerBot.CooldownMode.SMART) {
                                    double var7 = mc.field_1724.method_5739(var2);
                                    if (var7 < 1.6) {
                                       var4 = (long)(var4 * 0.5);
                                    } else if (var7 < 2.2) {
                                       var4 = (long)(var4 * 0.75);
                                    }
                                 } else if (var6 == TriggerBot.CooldownMode.NONE) {
                                    var4 = 0L;
                                 }

                                 if ((Boolean)this.randomDelay.get()) {
                                    var4 += ThreadLocalRandom.current().nextLong(20L, 80L);
                                 }

                                 this.currentReaction = var4;
                              }
                           }

                           if (System.currentTimeMillis() - this.reactionStart >= this.currentReaction) {
                              TriggerBot.CritMode var11 = (TriggerBot.CritMode)this.critMode.get();
                              if (var11 == TriggerBot.CritMode.STRICT) {
                                 boolean var5 = !mc.field_1724.method_24828()
                                    && !mc.field_1724.method_5799()
                                    && !mc.field_1724.method_5771()
                                    && !mc.field_1724.method_6101();
                                 if (var5) {
                                    if (this.canCrit()
                                       && mc.field_1724.method_7261(0.5F) >= (float)this.swordThreshold.getCurrentMin()
                                       && this.hasTarget(this.target)) {
                                       this.doAttack();
                                       this.waitingForReaction = false;
                                    }
                                 } else if (this.hasElapsedDelay() && this.hasTarget(this.target)) {
                                    this.doAttack();
                                    this.waitingForReaction = false;
                                 }
                              } else if (var11 == TriggerBot.CritMode.SMART) {
                                 boolean var12 = !mc.field_1724.method_24828()
                                    && !mc.field_1724.method_5799()
                                    && !mc.field_1724.method_5771()
                                    && !mc.field_1724.method_6101()
                                    && mc.field_1724.field_6017 > 0.05F;
                                 if (var12) {
                                    if (this.canCrit() && this.hasElapsedDelay() && this.hasTarget(this.target)) {
                                       this.doAttack();
                                       this.waitingForReaction = false;
                                    }
                                 } else if (this.hasElapsedDelay() && this.hasTarget(this.target)) {
                                    this.doAttack();
                                    this.waitingForReaction = false;
                                 }
                              } else if (this.hasElapsedDelay() && this.hasTarget(this.target)) {
                                 this.doAttack();
                                 this.waitingForReaction = false;
                              }
                           }
                        }
                     }
                  }
               } else {
                  this.resetState();
               }
            }
         }
      }
   }

   private void doAttack() {
      if (this.target != null) {
         VieClientMod.getInstance().getEventBus().post(new AttackEvent(this.target));
         if (this.missChance.getCurrentMin() > 0.0 && Math.random() * 100.0 < this.missChance.getRandomDoubleInRange()) {
            mc.field_1724.method_6104(class_1268.field_5808);
         } else {
            mc.field_1761.method_2918(mc.field_1724, this.target);
            mc.field_1724.method_6104(class_1268.field_5808);
         }

         this.lastHitTime = System.currentTimeMillis();
         this.lastHitTarget = this.target;
         this.comboStreak++;
         this.waitingForDelay = false;
      }
   }

   private boolean hasElapsedDelay() {
      float var1 = mc.field_1724.method_7261(0.5F);
      if (this.isHoldingAxe()) {
         if (!this.waitingForDelay) {
            this.randomizedThreshold = (float)this.axeThreshold.getRandomDoubleInRange();
            this.randomizedPostDelay = this.axePostDelay.getRandomLongInRange()
               + (this.randomDelay.get() ? ThreadLocalRandom.current().nextLong(20L, 80L) : 0L);
            this.axeDelayStart = System.currentTimeMillis();
            this.waitingForDelay = true;
         }

         if (var1 >= this.randomizedThreshold) {
            if (System.currentTimeMillis() - this.axeDelayStart >= this.randomizedPostDelay) {
               this.waitingForDelay = false;
               return true;
            }
         } else {
            this.axeDelayStart = System.currentTimeMillis();
         }

         return false;
      } else {
         float var2 = (float)this.swordThreshold.getRandomDoubleInRange();
         TriggerBot.CooldownMode var3 = (TriggerBot.CooldownMode)this.cooldownMode.get();
         if (var3 == TriggerBot.CooldownMode.SMART && this.target != null) {
            double var4 = mc.field_1724.method_5739(this.target);
            if (var4 < 1.6) {
               var2 = Math.max(0.85F, var2 - 0.05F);
            } else if (var4 > 2.7) {
               var2 = Math.max(var2, 0.95F);
            }
         } else if (var3 == TriggerBot.CooldownMode.STRICT) {
            var2 = Math.max(var2, 0.98F);
         } else if (var3 == TriggerBot.CooldownMode.NONE) {
            return true;
         }

         return var1 >= var2;
      }
   }

   private boolean canCrit() {
      return !mc.field_1724.method_24828()
         && !mc.field_1724.method_5624()
         && !mc.field_1724.method_5771()
         && !mc.field_1724.method_6101()
         && mc.field_1724.field_6017 > 0.065F
         && mc.field_1724.method_5854() == null;
   }

   private class_1297 getCrosshairTarget(double maxReach) {
      if (mc.field_1692 != null && this.hasTarget(mc.field_1692) && mc.field_1724 != null && mc.field_1724.method_5739(mc.field_1692) <= maxReach) {
         return mc.field_1692;
      } else {
         class_1297 var3 = mc.method_1560();
         if ((Boolean)this.smartRange.get() && var3 != null && mc.field_1687 != null) {
            class_243 var4 = var3.method_33571();
            class_243 var5 = var3.method_5828(1.0F);
            class_243 var6 = var4.method_1019(var5.method_1021(maxReach));
            class_238 var7 = var3.method_5829().method_18804(var5.method_1021(maxReach)).method_1009(1.0, 1.0, 1.0);
            class_3966 var8 = class_1675.method_18075(var3, var4, var6, var7, this::hasTarget, maxReach * maxReach);
            return var8 != null ? var8.method_17782() : null;
         } else {
            return null;
         }
      }
   }

   private boolean isTapModuleTapping() {
      if (!(Boolean)this.tapSync.get()) {
         return false;
      }

      try {
         ModuleManager var1 = VieClientMod.getInstance().getModuleManager();
         if (var1 != null) {
            WTap var2 = (WTap)var1.getModule(WTap.class);
            if (var2 != null && var2.isEnabled() && var2.isTapping()) {
               return true;
            }

            STap var3 = (STap)var1.getModule(STap.class);
            if (var3 != null && var3.isEnabled() && var3.isTapping()) {
               return true;
            }
         }
      } catch (Throwable var4) {
      }

      return false;
   }

   private boolean hasTarget(class_1297 entity) {
      if (entity == null || entity == mc.field_1724) {
         return false;
      }

      if (!entity.method_5805()) {
         return false;
      }

      if (entity instanceof class_1530) {
         return false;
      }

      if (entity instanceof class_1657 var2) {
         try {
            CoreEntry.FriendManager var3 = VieClientMod.getInstance().getFriendManager();
            if (var3 != null && var3.isFriend(var2.method_5477().getString())) {
               return false;
            }
         } catch (Throwable var4) {
         }
      }

      if ((Boolean)this.ignoreCrystals.get() && entity instanceof class_1511) {
         return false;
      } else {
         return this.ignorePassive.get() && entity instanceof class_1296 ? false : !(Boolean)this.ignoreInvisible.get() || !entity.method_5767();
      }
   }

   private boolean isHoldingSwordOrAxe() {
      class_1792 var1 = mc.field_1724.method_6047().method_7909();
      return var1.method_57347().method_57832(class_9334.field_55878) || var1 instanceof class_1743;
   }

   private boolean isHoldingAxe() {
      return mc.field_1724.method_6047().method_7909() instanceof class_1743;
   }

   private void resetState() {
      this.waitingForReaction = false;
      this.waitingForDelay = false;
      this.target = null;
      this.reactionStart = 0L;
      this.currentReaction = 0L;
      this.axeDelayStart = 0L;
      if (System.currentTimeMillis() - this.lastHitTime > 1200L) {
         this.comboStreak = 0;
         this.lastHitTarget = null;
      }
   }

   public int getComboStreak() {
      if (System.currentTimeMillis() - this.lastHitTime > 1200L) {
         this.comboStreak = 0;
      }

      return this.comboStreak;
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

   public enum CooldownMode {
      SMART,
      STRICT,
      NONE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TriggerBot.CooldownMode[] $values() {
         TriggerBot.CooldownMode[] var10000 = new TriggerBot.CooldownMode[3];
         var10000[0] = SMART;
         var10000[1] = STRICT;
         var10000[2] = NONE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-41);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(106);
         SMART = new TriggerBot.CooldownMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-50);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(125);
         var10002[5] = (byte)(78);
         STRICT = new TriggerBot.CooldownMode();
         var10002 = new byte[4];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         NONE = new TriggerBot.CooldownMode();
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

   public enum CritMode {
      NONE,
      STRICT,
      SMART;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TriggerBot.CritMode[] $values() {
         TriggerBot.CritMode[] var10000 = new TriggerBot.CritMode[3];
         var10000[0] = NONE;
         var10000[1] = STRICT;
         var10000[2] = SMART;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         NONE = new TriggerBot.CritMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-50);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-20);
         var10002[5] = (byte)(-84);
         STRICT = new TriggerBot.CritMode();
         var10002 = new byte[5];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-41);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(-5);
         SMART = new TriggerBot.CritMode();
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
}
