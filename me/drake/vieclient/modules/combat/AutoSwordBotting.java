package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.AttackEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.modules.movement.FreeLook;
import me.drake.vieclient.util.InventoryUtil;
import me.drake.vieclient.util.RotationUtil;
import me.drake.vieclient.util.WorldUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_742;
import net.minecraft.class_7923;
import net.minecraft.class_9334;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class AutoSwordBotting extends Module {
   private final DoubleSetting range = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Range",
         "Maximum scan range to acquire target (blocks)",
         8.0,
         3.0,
         16.0,
         0.5
      )
   );
   private final DoubleSetting reach = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Reach",
         "Maximum attack reach distance (blocks)",
         3.05,
         2.6,
         3.4,
         0.05
      )
   );
   private final EnumSetting priority = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Priority",
         "Target selection priority",
         AutoSwordBotting.Priority.DISTANCE
      )
   );
   private final BoolSetting humanizeAim = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Humanize Aim",
         "Smooth camera rotation with mouse GCD easing",
         true
      )
   );
   private final DoubleSetting aimSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Aim Speed",
         "Horizontal camera tracking speed",
         12.0,
         2.0,
         40.0,
         0.5
      )
   );
   private final DoubleSetting verticalSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Vertical Speed",
         "Vertical pitch tracking speed",
         8.0,
         1.0,
         30.0,
         0.5
      )
   );
   private final BoolSetting curvedAim = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Curved Aim",
         "Human wrist curved path (anti-cheat safe)",
         true
      )
   );
   private final BoolSetting microTremor = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Micro Tremor",
         "Natural biological micro-tremors",
         true
      )
   );
   private final BoolSetting gcdFix = (BoolSetting)this.addSetting(
      new BoolSetting(
         "GCD Fix",
         "Quantize rotation deltas to match mouse sensitivity",
         true
      )
   );
   private final BoolSetting randomizeHitbox = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Randomize Hitbox",
         "Drift aim point smoothly across target hitbox",
         true
      )
   );
   private final DoubleSetting minCooldown = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Min Cooldown",
         "Weapon recharge percentage before striking",
         0.92,
         0.85,
         1.0,
         0.01
      )
   );
   private final BoolSetting autoCrit = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Auto Crit",
         "Master toggle for critical strike timing and execution",
         true
      )
   );
   private final EnumSetting critMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Crit Mode",
         "Crit strategy: SMART (tactical), AGGRESSIVE (always), FALL_ONLY (drops only)",
         AutoSwordBotting.CritMode.SMART
      )
   );
   private final DoubleSetting minCritHealth = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Crit Min Health",
         "Minimum player HP required to jump-crit (avoids airborne punish)",
         8.0,
         2.0,
         20.0,
         1.0
      )
   );
   private final BoolSetting smartCritSprint = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Crit Sprint Reset",
         "Momentarily reset sprint at moment of strike to satisfy vanilla crit requirement",
         true
      )
   );
   private final BoolSetting ceilingCrits = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Ceiling Crits",
         "Take advantage of low ceilings (<=2.5 blocks) for rapid instant crits",
         true
      )
   );
   private final BoolSetting shieldBreaker = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Shield Breaker",
         "Auto swap to Axe to stun enemy shield, then swap back",
         true
      )
   );
   private final BoolSetting autoSword = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Auto Sword",
         "Automatically hold best sword from hotbar in combat",
         true
      )
   );
   private final BoolSetting autoBlock = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Auto Block",
         "Raise offhand shield during weapon recharge against threats",
         false
      )
   );
   private final BoolSetting autoSpacing = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Auto Spacing",
         "Autonomous footwork to hold ideal combo spacing",
         true
      )
   );
   private final EnumSetting strafeMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Strafe",
         "Strafe around the opponent in combat",
         AutoSwordBotting.StrafeMode.CIRCLE
      )
   );
   private final EnumSetting resetMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Reset Mode",
         "Sprint reset: ADAPTIVE (smart W/S-Tap) | WTAP_ONLY | STAP_ONLY | OFF",
         AutoSwordBotting.ResetMode.ADAPTIVE
      )
   );
   private final DoubleSetting sTapDistance = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "S-Tap Distance",
         "Distance threshold to prefer S-Tap over W-Tap (blocks)",
         2.3,
         1.8,
         2.8,
         0.05
      )
   );
   private final BoolSetting approachCheck = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Approach Check",
         "Use S-Tap when enemy charges, W-Tap when enemy is knocked back",
         true
      )
   );
   private final BoolSetting cooldownSpacing = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Cooldown Spacing",
         "Step back while weapon is on cooldown, push forward when ready",
         true
      )
   );
   private final BoolSetting dynamicTapTicks = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Dynamic Tap Ticks",
         "Dynamically adjust tap duration (1-3 ticks) based on momentum",
         true
      )
   );
   private final BoolSetting jumpReset = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Jump Reset",
         "Jump upon taking damage to reduce horizontal knockback",
         true
      )
   );
   private final BoolSetting freecamSupport = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Freecam / FreeLook",
         "Maintain autonomous aiming & combat when FreeLook or Freecam is active",
         true
      )
   );
   private final BoolSetting smartTrade = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Smart Trade",
         "Intelligent hit exchange: time hits during enemy recovery & avoid bad trades",
         true
      )
   );
   private final DoubleSetting tradeHpAdvantage = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Trade Min HP Ratio",
         "Minimum player HP ratio vs enemy to commit to aggressive trades",
         0.75,
         0.3,
         1.5,
         0.05
      )
   );
   private final BoolSetting punishRecovery = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Punish Recovery",
         "Capitalize on enemy attack cooldown/swing recovery windows",
         true
      )
   );
   private final BoolSetting counterReach = (BoolSetting)this.addSetting(
      new BoolSetting(
         "First Strike Reach",
         "Time strike at maximum reach before opponent can land a hit",
         true
      )
   );
   private final BoolSetting comboDeny = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Combo Deny",
         "Detect and break out of opponent's sprint combos and hit-chains",
         true
      )
   );
   private final BoolSetting defensiveParry = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Defensive Parry",
         "Instant shield flash to block follow-up combo hits and stop enemy sprint",
         true
      )
   );
   private final BoolSetting strafeEscape = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Strafe Escape",
         "Break linear combo lock by strafing sharply perpendicular to enemy",
         true
      )
   );
   private final DoubleSetting comboEscapeDist = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Deny Distance",
         "Distance threshold to trigger emergency combo evasion (blocks)",
         2.4,
         1.8,
         3.2,
         0.1
      )
   );
   private final BoolSetting runAway = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Run Away",
         "Tactically disengage and retreat when health is critical",
         true
      )
   );
   private final DoubleSetting runAwayHealth = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Retreat HP",
         "Health threshold to trigger tactical retreat",
         8.0,
         2.0,
         16.0,
         1.0
      )
   );
   private final DoubleSetting reengageHealth = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Re-engage HP",
         "Health threshold to resume fighting after retreating",
         14.0,
         6.0,
         20.0,
         1.0
      )
   );
   private final DoubleSetting retreatDistance = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Retreat Distance",
         "Safe distance to maintain while fleeing (blocks)",
         14.0,
         6.0,
         24.0,
         1.0
      )
   );
   private final BoolSetting rearGlance = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Look Back Glance",
         "Humanized brief backward glance while fleeing to check pursuer distance",
         true
      )
   );
   private final BoolSetting evasiveZigZag = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Evasive Zig-Zag",
         "Zig-zag strafe while running to dodge sprint hits and projectiles",
         true
      )
   );
   private final BoolSetting retreatAutoEat = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Retreat Auto Eat",
         "Prioritize eating Gapple/Food while keeping distance during retreat",
         true
      )
   );
   private final BoolSetting surpriseTurnaround = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Surprise Turnaround",
         "Whip around 180° to punish recklessly pursuing enemies with a surprise strike",
         true
      )
   );
   private AutoSwordBotting.BotState botState = AutoSwordBotting.BotState.COMBAT;
   private class_1657 currentTarget = null;
   private class_1657 lastAimTarget = null;
   private int strafeTicks = 0;
   private boolean strafeLeft = true;
   private AutoSwordBotting.ActiveTap currentTap = AutoSwordBotting.ActiveTap.NONE;
   private int tapTicks = 0;
   private int previousWeaponSlot = -1;
   private int axeSwapCooldown = 0;
   private int comboCount = 0;
   private int jumpCooldown = 0;
   private int jumpPulseTicks = 0;
   private int punishDisabledShieldTicks = 0;
   private int critJumpTicks = 0;
   private long lastDamageTime = 0L;
   private int consecutiveHitsTaken = 0;
   private int comboDenyTicks = 0;
   private boolean comboDenyStrafeLeft = true;
   private int parryTicks = 0;
   private long lastEnemySwingTime = 0L;
   private boolean enemySwungInWindow = false;
   private int glanceTicks = 0;
   private int glanceCooldown = 0;
   private int zigZagTicks = 0;
   private boolean zigZagLeft = true;
   private int retreatEatSlot = -1;
   private int eatTicks = 0;
   private int retreatTicks = 0;
   private int turnaroundCooldownTicks = 0;
   private int surpriseTurnaroundTicks = 0;
   private float lastSmartEscapeYaw = 0.0F;
   private float velocityYaw = 0.0F;
   private float velocityPitch = 0.0F;
   private float residualYaw = 0.0F;
   private float residualPitch = 0.0F;
   private double currentOffsetH = 0.0;
   private double currentOffsetV = 0.0;
   private double targetOffsetH = 0.0;
   private double targetOffsetV = 0.0;
   private long nextRandomizeTime = 0L;
   private long targetAcquiredTime = 0L;
   private float initialFlickAngle = 0.0F;
   private float flickCurveDir = 1.0F;
   private final Random random = new Random();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoSwordBotting() {
      super(
         "Auto Sword Botting",
         "Autonomous humanized combat AI for sword PvP with spacing, crit timing and shield break",
         Category.COMBAT
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetCombat();
   }

   @Override
   public void onDisable() {
      this.releaseKeys();
      this.resetCombat();
      super.onDisable();
   }

   private void resetCombat() {
      this.currentTarget = null;
      this.lastAimTarget = null;
      this.strafeTicks = 0;
      this.strafeLeft = true;
      this.currentTap = AutoSwordBotting.ActiveTap.NONE;
      this.tapTicks = 0;
      this.axeSwapCooldown = 0;
      this.comboCount = 0;
      this.jumpCooldown = 0;
      this.jumpPulseTicks = 0;
      this.punishDisabledShieldTicks = 0;
      this.velocityYaw = 0.0F;
      this.velocityPitch = 0.0F;
      this.residualYaw = 0.0F;
      this.residualPitch = 0.0F;
      this.currentOffsetH = 0.0;
      this.currentOffsetV = 0.0;
      this.targetOffsetH = 0.0;
      this.targetOffsetV = 0.0;
      this.nextRandomizeTime = 0L;
      this.targetAcquiredTime = 0L;
      this.initialFlickAngle = 0.0F;
      this.flickCurveDir = 1.0F;
      this.botState = AutoSwordBotting.BotState.COMBAT;
      this.lastDamageTime = 0L;
      this.consecutiveHitsTaken = 0;
      this.comboDenyTicks = 0;
      this.parryTicks = 0;
      this.lastEnemySwingTime = 0L;
      this.enemySwungInWindow = false;
      this.glanceTicks = 0;
      this.glanceCooldown = 0;
      this.zigZagTicks = 0;
      this.critJumpTicks = 0;
      this.retreatTicks = 0;
      this.turnaroundCooldownTicks = 0;
      this.surpriseTurnaroundTicks = 0;
      this.stopRetreatEating();
      if (this.previousWeaponSlot != (-1) && mc.field_1724 != null) {
         InventoryUtil.swap(this.previousWeaponSlot);
      }

      this.previousWeaponSlot = -1;
      ActionArbiter.get().release(ActionArbiter.Resource.CAMERA, ActionArbiter.Owner.AIMASSIST);
   }

   private void releaseKeys() {
      if (mc.field_1690 != null) {
         mc.field_1690.field_1894.method_23481(false);
         mc.field_1690.field_1881.method_23481(false);
         mc.field_1690.field_1913.method_23481(false);
         mc.field_1690.field_1849.method_23481(false);
         mc.field_1690.field_1903.method_23481(false);
         mc.field_1690.field_1904.method_23481(false);
      }
   }

   @EventHandler
   public void onAttack(AttackEvent event) {
      if (event.getTarget() == this.currentTarget) {
         this.comboCount++;
         this.handleHitSprintReset(this.currentTarget);
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (!this.isInGame()) {
            this.resetCombat();
         } else {
            if (this.jumpCooldown > 0 && mc.field_1724 != null && mc.field_1724.method_24828()) {
               this.jumpCooldown--;
            }

            if (this.punishDisabledShieldTicks > 0) {
               this.punishDisabledShieldTicks--;
            }

            FreeLook var2 = (FreeLook)VieClientMod.getInstance().getModuleManager().getModule(FreeLook.class);
            Freecam var3 = (Freecam)VieClientMod.getInstance().getModuleManager().getModule(Freecam.class);
            boolean var4 = var2 != null && var2.isEnabled();
            boolean var5 = var3 != null && var3.isEnabled();
            if ((Boolean)this.freecamSupport.get() || !var4 && !var5) {
               ActionArbiter.get().tryAcquire(ActionArbiter.Resource.CAMERA, ActionArbiter.Owner.AIMASSIST);
               this.currentTarget = this.findBestTarget();
               if (this.currentTarget == null) {
                  if (this.previousWeaponSlot != -1) {
                     InventoryUtil.swap(this.previousWeaponSlot);
                     this.previousWeaponSlot = -1;
                  }

                  this.stopRetreatEating();
                  this.botState = AutoSwordBotting.BotState.COMBAT;
                  this.releaseKeys();
                  this.comboCount = 0;
               } else {
                  double var6 = mc.field_1724.method_5739(this.currentTarget);
                  if (!(this.currentTarget.field_6251 > 0.05F) && !this.currentTarget.field_6252) {
                     if (System.currentTimeMillis() - this.lastEnemySwingTime > 400L) {
                        this.enemySwungInWindow = false;
                     }
                  } else {
                     this.lastEnemySwingTime = System.currentTimeMillis();
                     this.enemySwungInWindow = true;
                  }

                  this.handleWeaponSelection();
                  float var8 = mc.field_1724.method_6032();
                  if (this.turnaroundCooldownTicks > 0) {
                     this.turnaroundCooldownTicks--;
                  }

                  if ((Boolean)this.runAway.get()) {
                     if (this.botState == AutoSwordBotting.BotState.COMBAT) {
                        if (var8 <= (float)((Double)this.runAwayHealth.get()).doubleValue()) {
                           this.botState = AutoSwordBotting.BotState.RETREAT;
                           this.glanceTicks = 0;
                           this.glanceCooldown = 25;
                           this.zigZagTicks = 0;
                           this.retreatTicks = 0;
                           this.surpriseTurnaroundTicks = 0;
                        }
                     } else {
                        boolean var9 = this.isEnemyChasingUs(this.currentTarget);
                        if (var8 >= (float)((Double)this.reengageHealth.get()).doubleValue()) {
                           this.botState = AutoSwordBotting.BotState.COMBAT;
                           this.glanceTicks = 0;
                           this.retreatTicks = 0;
                           this.surpriseTurnaroundTicks = 0;
                           this.stopRetreatEating();
                        } else if (!var9 && var6 > 7.5) {
                           this.botState = AutoSwordBotting.BotState.COMBAT;
                           this.glanceTicks = 0;
                           this.retreatTicks = 0;
                           this.surpriseTurnaroundTicks = 0;
                           this.stopRetreatEating();
                        } else if (var6 > (Double)this.retreatDistance.get() * 1.5) {
                           this.botState = AutoSwordBotting.BotState.COMBAT;
                           this.glanceTicks = 0;
                           this.retreatTicks = 0;
                           this.surpriseTurnaroundTicks = 0;
                           this.stopRetreatEating();
                        }
                     }
                  } else {
                     this.botState = AutoSwordBotting.BotState.COMBAT;
                     this.retreatTicks = 0;
                     this.surpriseTurnaroundTicks = 0;
                  }

                  if (this.botState == AutoSwordBotting.BotState.RETREAT) {
                     this.handleRetreat(var6);
                  } else {
                     if (mc.field_1724.field_6235 == 9) {
                        long var14 = System.currentTimeMillis();
                        if (var14 - this.lastDamageTime < 750L) {
                           this.consecutiveHitsTaken++;
                        } else {
                           this.consecutiveHitsTaken = 1;
                        }

                        this.lastDamageTime = var14;
                        if ((Boolean)this.comboDeny.get() && this.currentTarget != null) {
                           double var11 = mc.field_1724.method_5739(this.currentTarget);
                           boolean var13 = this.isEnemyCharging(this.currentTarget);
                           if (this.consecutiveHitsTaken >= 2 || var13 && var11 < (Double)this.comboEscapeDist.get()) {
                              this.comboDenyTicks = this.dynamicTapTicks.get() ? 7 : 5;
                              this.comboDenyStrafeLeft = ThreadLocalRandom.current().nextBoolean();
                              if ((Boolean)this.defensiveParry.get() && mc.field_1724.method_6079().method_31574(class_1802.field_8255)) {
                                 this.parryTicks = 4;
                              }
                           }
                        }
                     }

                     if ((Boolean)this.autoSpacing.get()) {
                        this.handleFootwork(var6);
                     }

                     this.handleTap();
                     if ((Boolean)this.jumpReset.get() && mc.field_1724.field_6235 == 9 && mc.field_1724.method_24828() && mc.field_1690 != null) {
                        mc.field_1690.field_1903.method_23481(true);
                     }

                     this.handleAutoBlock(var6);
                     this.handleAttack(var6);
                  }
               }
            } else {
               this.releaseKeys();
            }
         }
      }
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D) {
         if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1755 == null) {
            if (this.currentTarget != null && this.currentTarget.method_5805() && this.isInGame()) {
               FreeLook var2 = (FreeLook)VieClientMod.getInstance().getModuleManager().getModule(FreeLook.class);
               Freecam var3 = (Freecam)VieClientMod.getInstance().getModuleManager().getModule(Freecam.class);
               boolean var4 = var2 != null && var2.isEnabled();
               boolean var5 = var3 != null && var3.isEnabled();
               if ((Boolean)this.freecamSupport.get() || !var4 && !var5) {
                  if (ActionArbiter.get().tryAcquire(ActionArbiter.Resource.CAMERA, ActionArbiter.Owner.AIMASSIST)) {
                     if (this.botState == AutoSwordBotting.BotState.RETREAT) {
                        boolean var6 = this.isEnemyChasingUs(this.currentTarget);
                        double var7 = mc.field_1724.method_5739(this.currentTarget);
                        if (this.surpriseTurnaroundTicks > 0) {
                           this.aim(this.currentTarget, event.getFrameTime());
                        } else if (!var6 && var7 >= 5.0) {
                           this.aim(this.currentTarget, event.getFrameTime());
                        } else if (this.glanceTicks > 0) {
                           this.aim(this.currentTarget, event.getFrameTime());
                        } else {
                           this.aimEscape(this.currentTarget, event.getFrameTime());
                        }
                     } else {
                        this.aim(this.currentTarget, event.getFrameTime());
                     }
                  }
               }
            } else {
               this.velocityYaw = 0.0F;
               this.velocityPitch = 0.0F;
               this.residualYaw = 0.0F;
               this.residualPitch = 0.0F;
               ActionArbiter.get().release(ActionArbiter.Resource.CAMERA, ActionArbiter.Owner.AIMASSIST);
            }
         }
      }
   }

   private void aim(class_1657 entity, float frameTime) {
      if (mc.field_1724 != null) {
         double var3 = class_3532.method_15363(frameTime, 0.001F, 0.05F);
         long var5 = System.currentTimeMillis();
         if (entity != this.lastAimTarget) {
            this.velocityYaw = 0.0F;
            this.velocityPitch = 0.0F;
            this.residualYaw = 0.0F;
            this.residualPitch = 0.0F;
            this.targetAcquiredTime = var5;
            this.initialFlickAngle = 0.0F;
            this.flickCurveDir = this.random.nextBoolean() ? 1.0F : -1.0F;
            this.lastAimTarget = entity;
         }

         class_243 var7 = this.getTargetAimPos(entity, var3);
         double var8 = var7.field_1352 - mc.field_1724.method_23317();
         double var10 = var7.field_1351 - (mc.field_1724.method_23318() + mc.field_1724.method_18381(mc.field_1724.method_18376()));
         double var12 = var7.field_1350 - mc.field_1724.method_23321();
         double var14 = Math.sqrt(var8 * var8 + var12 * var12);
         float var16 = (float)class_3532.method_15338(Math.toDegrees(Math.atan2(var12, var8)) - 90.0);
         float var17 = (float)class_3532.method_15350(-Math.toDegrees(Math.atan2(var10, var14)), -90.0, 90.0);
         if (!(Boolean)this.humanizeAim.get()) {
            mc.field_1724.method_36456(var16);
            mc.field_1724.method_36457(var17);
            this.syncPlayerModelRotations(var16, var17);
         } else {
            float var18 = mc.field_1724.method_36454();
            float var19 = mc.field_1724.method_36455();
            float var20 = class_3532.method_15393(var16 - var18);
            float var21 = var17 - var19;
            float var22 = (float)Math.hypot(var20, var21);
            if (this.initialFlickAngle <= 0.0F || var22 > this.initialFlickAngle) {
               this.initialFlickAngle = var22;
            }

            boolean var23 = this.isCrosshairOnTarget(entity);
            if (Math.abs(var20) < 0.04F && Math.abs(var21) < 0.04F) {
               this.velocityYaw = 0.0F;
               this.velocityPitch = 0.0F;
               this.initialFlickAngle = 0.0F;
            } else {
               float var24 = this.computeAngleFactor(var20);
               float var25 = this.computeAngleFactor(var21);
               float var26 = 1.0F;
               if (this.targetAcquiredTime > 0L) {
                  long var27 = var5 - this.targetAcquiredTime;
                  if (var27 < 130L) {
                     float var29 = class_3532.method_15363((float)var27 / 130.0F, 0.15F, 1.0F);
                     var26 = var29 * var29 * (3.0F - 2.0F * var29);
                  }
               }

               float var47 = 0.0F;
               if ((Boolean)this.curvedAim.get() && this.initialFlickAngle > 4.0F) {
                  float var28 = 1.0F - class_3532.method_15363(var22 / this.initialFlickAngle, 0.0F, 1.0F);
                  float var49 = (float)Math.sin(var28 * Math.PI);
                  float var30 = Math.min(2.0F, this.initialFlickAngle * 0.1F);
                  var47 = var49 * var30 * this.flickCurveDir;
               }

               float var48 = (float)((Double)this.aimSpeed.get() * 22.0) * var24 * var26;
               if (var23) {
                  var48 *= 0.35F;
               }

               float var50 = this.calculateDesiredSpeed(var20 + var47, var48);
               float var51 = class_3532.method_15363((Math.abs(var20) - 18.0F) / 90.0F, 0.0F, 1.0F);
               float var31 = Math.abs(var50) > Math.abs(this.velocityYaw) ? 14.0F + 6.0F * var51 : 20.0F;
               this.velocityYaw = this.velocityYaw + (var50 - this.velocityYaw) * (1.0F - (float)Math.exp(-var31 * var3));
               float var32 = this.velocityYaw * (float)var3;
               if (Math.abs(var32) > Math.abs(var20) && Math.abs(var20) > 0.04F) {
                  var32 = var20;
                  this.velocityYaw = 0.0F;
               }

               if ((Boolean)this.microTremor.get()) {
                  var32 += this.computeMicroTremor(var20, this.velocityYaw, false);
               }

               float var33;
               if ((Boolean)this.gcdFix.get() && mc.field_1690 != null) {
                  float var34 = ((Double)mc.field_1690.method_42495().method_41753()).floatValue();
                  float var35 = RotationUtil.gcdFor(var34);
                  if (var35 > 1.0E-4F) {
                     this.residualYaw += var32;
                     int var36 = Math.round(this.residualYaw / var35);
                     var33 = var36 * var35;
                     if (Math.abs(var33) > Math.abs(var20) && Math.abs(var20) > var35) {
                        int var37 = (int)(Math.abs(var20) / var35);
                        var33 = Math.copySign(var37 * var35, var20);
                     }

                     this.residualYaw -= var33;
                     this.residualYaw = class_3532.method_15363(this.residualYaw, -var35, var35);
                  } else {
                     var33 = var32;
                  }
               } else {
                  var33 = var32;
               }

               float var52 = var18;
               if (Math.abs(var33) > 0.0F) {
                  var52 = var18 + var33;
                  mc.field_1724.method_36456(var52);
               }

               float var53 = var19;
               if ((Double)this.verticalSpeed.get() > 0.0) {
                  float var54 = 1.0F;
                  if (Math.abs(var20) > 6.0F) {
                     var54 = 1.0F / (1.0F + (Math.abs(var20) - 6.0F) / 16.0F);
                  }

                  float var55 = (float)((Double)this.verticalSpeed.get() * 18.0) * var25 * var26 * var54;
                  if (var23) {
                     var55 *= 0.35F;
                  }

                  float var38 = this.calculateDesiredSpeed(var21, var55);
                  float var39 = class_3532.method_15363((Math.abs(var21) - 18.0F) / 90.0F, 0.0F, 1.0F);
                  float var40 = Math.abs(var38) > Math.abs(this.velocityPitch) ? 13.0F + 5.0F * var39 : 18.0F;
                  this.velocityPitch = this.velocityPitch + (var38 - this.velocityPitch) * (1.0F - (float)Math.exp(-var40 * var3));
                  float var41 = this.velocityPitch * (float)var3;
                  if (Math.abs(var41) > Math.abs(var21) && Math.abs(var21) > 0.04F) {
                     var41 = var21;
                     this.velocityPitch = 0.0F;
                  }

                  if ((Boolean)this.microTremor.get()) {
                     var41 += this.computeMicroTremor(var21, this.velocityPitch, true);
                  }

                  float var42;
                  if ((Boolean)this.gcdFix.get() && mc.field_1690 != null) {
                     float var43 = ((Double)mc.field_1690.method_42495().method_41753()).floatValue();
                     float var44 = RotationUtil.gcdFor(var43);
                     if (var44 > 1.0E-4F) {
                        this.residualPitch += var41;
                        int var45 = Math.round(this.residualPitch / var44);
                        var42 = var45 * var44;
                        if (Math.abs(var42) > Math.abs(var21) && Math.abs(var21) > var44) {
                           int var46 = (int)(Math.abs(var21) / var44);
                           var42 = Math.copySign(var46 * var44, var21);
                        }

                        this.residualPitch -= var42;
                        this.residualPitch = class_3532.method_15363(this.residualPitch, -var44, var44);
                     } else {
                        var42 = var41;
                     }
                  } else {
                     var42 = var41;
                  }

                  if (Math.abs(var42) > 0.0F) {
                     var53 = class_3532.method_15363(var19 + var42, -90.0F, 90.0F);
                     mc.field_1724.method_36457(var53);
                  }
               }

               this.syncPlayerModelRotations(var52, var53);
            }
         }
      }
   }

   private class_243 getTargetAimPos(class_1657 entity, double dt) {
      class_243 var4 = new class_243(entity.method_23317(), entity.method_23318(), entity.method_23321());
      float var5 = entity.method_18381(entity.method_18376());
      boolean var6 = mc.field_1724 != null && !mc.field_1724.method_24828();
      boolean var7 = var6 && (mc.field_1724.field_6017 > 0.0 || mc.field_1724.method_18798().field_1351 < -0.04);
      double var8 = this.autoCrit.get() && var7 ? 0.85 : 0.75;
      double var10 = var4.field_1351 + var5 * var8;
      if ((Boolean)this.randomizeHitbox.get() && mc.field_1724 != null) {
         long var12 = System.currentTimeMillis();
         if (entity != this.lastAimTarget || var12 >= this.nextRandomizeTime) {
            this.targetOffsetH = this.random.nextDouble() * 0.36 - 0.18;
            this.targetOffsetV = this.random.nextDouble() * 0.3 - 0.15;
            this.nextRandomizeTime = var12 + 180L + this.random.nextInt(170);
            this.lastAimTarget = entity;
         }

         double var14 = 1.0 - Math.exp(-8.0 * dt);
         this.currentOffsetH = this.currentOffsetH + (this.targetOffsetH - this.currentOffsetH) * var14;
         this.currentOffsetV = this.currentOffsetV + (this.targetOffsetV - this.currentOffsetV) * var14;
         double var16 = entity.method_23317() - mc.field_1724.method_23317();
         double var18 = entity.method_23321() - mc.field_1724.method_23321();
         double var20 = Math.sqrt(var16 * var16 + var18 * var18);
         double var22 = 0.0;
         double var24 = 0.0;
         if (var20 > 1.0E-4) {
            double var26 = -var18 / var20;
            double var28 = var16 / var20;
            var22 = var26 * this.currentOffsetH;
            var24 = var28 * this.currentOffsetH;
         }

         double var32 = entity.method_23318() + 0.2;
         double var33 = entity.method_23318() + entity.method_17682() - 0.2;
         double var30 = class_3532.method_15350(var10 + this.currentOffsetV, var32, var33);
         return new class_243(var4.field_1352 + var22, var30, var4.field_1350 + var24);
      } else {
         return new class_243(var4.field_1352, var10, var4.field_1350);
      }
   }

   public boolean isDetachedCameraActive() {
      VieClientMod var1 = VieClientMod.getInstance();
      if (var1 != null && var1.getModuleManager() != null) {
         FreeLook var2 = (FreeLook)var1.getModuleManager().getModule(FreeLook.class);
         if (var2 != null && var2.isEnabled()) {
            return true;
         }

         Freecam var3 = (Freecam)var1.getModuleManager().getModule(Freecam.class);
         return var3 != null && var3.isEnabled();
      } else {
         return false;
      }
   }

   public void syncPlayerModelRotations(float yaw, float pitch) {
      if (mc.field_1724 != null) {
         mc.field_1724.field_6241 = yaw;
         if (Math.abs(class_3532.method_15393(yaw - mc.field_1724.field_6259)) > 75.0F) {
            mc.field_1724.field_6259 = yaw;
         }

         float var3 = class_3532.method_15393(mc.field_1724.field_6283 - yaw);
         if (var3 < -75.0F) {
            mc.field_1724.field_6283 = yaw - 75.0F;
         } else if (var3 > 75.0F) {
            mc.field_1724.field_6283 = yaw + 75.0F;
         }

         if (Math.abs(class_3532.method_15393(mc.field_1724.field_6283 - mc.field_1724.field_6220)) > 75.0F) {
            mc.field_1724.field_6220 = mc.field_1724.field_6283;
         }
      }
   }

   private boolean isCrosshairOnTarget(class_1657 entity) {
      if (mc == null || mc.field_1724 == null || entity == null) {
         return false;
      }

      if (!this.isDetachedCameraActive() && mc.field_1692 == entity) {
         return true;
      }

      class_243 var2 = mc.field_1724.method_33571();
      class_243 var3 = mc.field_1724.method_5828(1.0F);
      double var4 = (Double)this.range.get();
      class_243 var6 = var2.method_1019(var3.method_1021(var4));
      class_238 var7 = entity.method_5829();
      float var8 = entity.method_5871();
      if (var8 > 0.0F) {
         var7 = var7.method_1014(var8);
      }

      Optional var9 = var7.method_992(var2, var6);
      if (var9.isEmpty()) {
         return false;
      }

      if (mc.field_1687 != null) {
         class_3965 var10 = mc.field_1687.method_17742(new class_3959(var2, var6, class_3960.field_17558, class_242.field_1348, mc.field_1724));
         if (var10 != null && var10.method_17783() != class_240.field_1333) {
            return var10.method_17784().method_1025(var2) > ((class_243)var9.get()).method_1025(var2);
         }
      }

      return true;
   }

   private float computeAngleFactor(float diffAngle) {
      float var2 = Math.abs(diffAngle);
      if (var2 > 18.0F) {
         float var3 = class_3532.method_15363((var2 - 18.0F) / 90.0F, 0.0F, 1.0F);
         float var4 = var3 * var3 * (3.0F - 2.0F * var3);
         return 1.0F + 1.5F * var4;
      } else {
         return 1.0F;
      }
   }

   private float calculateDesiredSpeed(float diffAngle, float maxSpeedDegPerSec) {
      float var3 = Math.abs(diffAngle);
      if (var3 < 0.04F) {
         return 0.0F;
      }

      float var4 = 16.0F;
      float var5 = class_3532.method_15363(var3 / var4, 0.0F, 1.0F);
      float var6 = var5 * var5 * (3.0F - 2.0F * var5);
      float var7 = 0.06F;
      float var8 = var7 + (1.0F - var7) * var6;
      return Math.copySign(maxSpeedDegPerSec * var8, diffAngle);
   }

   private float computeMicroTremor(float diff, float velocity, boolean isPitch) {
      float var4 = class_3532.method_15363(Math.abs(velocity) / 25.0F, 0.0F, 1.0F);
      float var5 = class_3532.method_15363(Math.abs(diff) / 6.0F, 0.0F, 1.0F);
      float var6 = Math.min(var4, var5);
      if (var6 <= 0.001F) {
         return 0.0F;
      }

      double var7 = System.currentTimeMillis() % 1000000L / 1000.0;
      double var9;
      if (!isPitch) {
         var9 = 0.018 * Math.sin(var7 * 54.6637121724624 + 0.3)
            + 0.012 * Math.sin(var7 * 65.97344572538566 + 1.7)
            + 0.008 * Math.sin(var7 * 76.65486074759094 + 3.1);
      } else {
         var9 = 0.014 * Math.sin(var7 * 57.17698629533423 + 2.1)
            + 0.009 * Math.sin(var7 * 70.37167544041137 + 0.8)
            + 0.006 * Math.sin(var7 * 80.4247719318987 + 4.5);
      }

      return (float)(var9 * var6);
   }

   private class_1657 findBestTarget() {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         double var1 = (Double)this.range.get();
         double var3 = var1 * var1;
         AutoSwordBotting.Priority var5 = (AutoSwordBotting.Priority)this.priority.get();
         class_742 var6 = null;
         double var7 = Double.MAX_VALUE;

         for (class_742 var10 : mc.field_1687.method_18456()) {
            if (var10 != mc.field_1724 && var10.method_5805() && !var10.method_7325() && !var10.method_68878() && !this.isFriend(var10)) {
               double var11 = mc.field_1724.method_5858(var10);
               if (!(var11 > var3)) {
                  double var13 = switch (var5) {
                     case DISTANCE -> var11;
                     case HEALTH -> var10.method_6032();
                     case ANGLE -> this.getYawDeltaTo(var10);
                  };
                  if (var13 < var7) {
                     var7 = var13;
                     var6 = var10;
                  }
               }
            }
         }

         return var6;
      } else {
         return null;
      }
   }

   private double getYawDeltaTo(class_1657 target) {
      float var2 = RotationUtil.getYaw(target);
      return Math.abs(class_3532.method_15393(var2 - mc.field_1724.method_36454()));
   }

   private boolean isFriend(class_1657 player) {
      VieClientMod var2 = VieClientMod.getInstance();
      return var2 != null && var2.getFriendManager() != null && var2.getFriendManager().isFriend(player.method_5477().getString());
   }

   private void handleWeaponSelection() {
      if (mc.field_1724 != null) {
         if (this.axeSwapCooldown > 0) {
            this.axeSwapCooldown--;
         } else {
            boolean var1 = this.currentTarget.method_6039() && !WorldUtils.isShieldFacingAway(this.currentTarget);
            if ((Boolean)this.shieldBreaker.get() && var1) {
               int var2 = this.findAxeInHotbar();
               if (var2 != (-1)) {
                  if (this.previousWeaponSlot == (-1)) {
                     this.previousWeaponSlot = mc.field_1724.method_31548().method_67532();
                  }

                  InventoryUtil.swap(var2);
                  return;
               }
            }

            if (this.previousWeaponSlot != (-1) && !var1) {
               InventoryUtil.swap(this.previousWeaponSlot);
               this.previousWeaponSlot = -1;
            } else {
               if ((Boolean)this.autoSword.get() && this.previousWeaponSlot == (-1)) {
                  class_1799 var4 = mc.field_1724.method_6047();
                  if (!this.isSword(var4)) {
                     int var3 = this.findBestSwordInHotbar();
                     if (var3 != (-1)) {
                        InventoryUtil.swap(var3);
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isSword(class_1799 stack) {
      if (stack == null || stack.method_7960()) {
         return false;
      }

      if (stack.method_31573(class_3489.field_42611)) {
         return true;
      }

      String var2 = class_7923.field_41178.method_10221(stack.method_7909()).method_12832();
      byte[] var10001 = new byte[6];
      var10001[0] = (byte)(6);
      var10001[1] = (byte)(-23);
      var10001[2] = (byte)(107);
      var10001[3] = (byte)(-79);
      var10001[4] = (byte)(88);
      var10001[5] = (byte)(113);
      return var2.endsWith($$v_s(var10001, -2988477469244010372L));
   }

   private boolean isAxe(class_1799 stack) {
      if (stack == null || stack.method_7960()) {
         return false;
      }

      if (stack.method_31573(class_3489.field_42612)) {
         return true;
      }

      String var2 = class_7923.field_41178.method_10221(stack.method_7909()).method_12832();
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(6);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(100);
      var10001[3] = (byte)(-69);
      return var2.endsWith($$v_s(var10001, -2988477469244010372L));
   }

   private int findAxeInHotbar() {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var1 = 0; var1 < (9); var1++) {
         class_1799 var2 = mc.field_1724.method_31548().method_5438(var1);
         if (this.isAxe(var2)) {
            return var1;
         }
      }

      return -1;
   }

   private int findBestSwordInHotbar() {
      if (mc.field_1724 == null) {
         return -1;
      }

      int var1 = -1;

      for (int var2 = 0; var2 < (9); var2++) {
         class_1799 var3 = mc.field_1724.method_31548().method_5438(var2);
         if (this.isSword(var3)) {
            var1 = var2;
            break;
         }
      }

      return var1;
   }

   private void handleFootwork(double dist) {
      if (mc.field_1690 != null && mc.field_1724 != null && this.currentTarget != null) {
         if (this.comboDenyTicks > 0) {
            this.comboDenyTicks--;
            mc.field_1690.field_1894.method_23481(false);
            mc.field_1690.field_1881.method_23481(true);
            mc.field_1724.method_5728(false);
            if ((Boolean)this.strafeEscape.get()) {
               mc.field_1690.field_1913.method_23481(this.comboDenyStrafeLeft);
               mc.field_1690.field_1849.method_23481(!this.comboDenyStrafeLeft);
            }

            if ((Boolean)this.jumpReset.get() && mc.field_1724.method_24828()) {
               mc.field_1690.field_1903.method_23481(true);
            }

            if (this.parryTicks > 0) {
               this.parryTicks--;
               mc.field_1690.field_1904.method_23481(true);
            }
         } else {
            float var3 = (float)this.getYawDeltaTo(this.currentTarget);
            boolean var4 = false;
            if ((Boolean)this.smartTrade.get()) {
               float var5 = mc.field_1724.method_6032();
               float var6 = this.currentTarget.method_6032();
               double var7 = var5 / Math.max(1.0F, var6);
               if ((var7 < (Double)this.tradeHpAdvantage.get() || var5 <= 7.0F) && !this.enemySwungInWindow && this.currentTarget.field_6235 == 0) {
                  var4 = dist < 2.85;
               }
            }

            if (this.tapTicks <= 0) {
               float var9 = mc.field_1724.method_7261(0.5F);
               boolean var11 = (Boolean)this.cooldownSpacing.get() && var9 < 0.7F && dist <= 2.5;
               if (dist > 2.85 && !var4) {
                  mc.field_1690.field_1894.method_23481(var3 < 65.0F);
                  mc.field_1690.field_1881.method_23481(false);
                  mc.field_1724.method_5728(var3 < 35.0F && !mc.field_1724.field_5976);
               } else if (!(dist < 1.75) && !var11 && !var4) {
                  mc.field_1690.field_1894.method_23481(dist > 2.45 && var3 < 45.0F);
                  mc.field_1690.field_1881.method_23481(false);
               } else {
                  mc.field_1690.field_1894.method_23481(false);
                  mc.field_1690.field_1881.method_23481(true);
               }
            }

            AutoSwordBotting.StrafeMode var10 = (AutoSwordBotting.StrafeMode)this.strafeMode.get();
            if (var10 != AutoSwordBotting.StrafeMode.NONE && dist <= 3.8) {
               this.strafeTicks++;
               int var12 = var10 == AutoSwordBotting.StrafeMode.CIRCLE ? 22 : 12;
               if (this.strafeTicks >= var12 || mc.field_1724.field_5976) {
                  this.strafeTicks = 0;
                  this.strafeLeft = var10 == AutoSwordBotting.StrafeMode.CIRCLE ? !this.strafeLeft : ThreadLocalRandom.current().nextBoolean();
               }

               mc.field_1690.field_1913.method_23481(this.strafeLeft);
               mc.field_1690.field_1849.method_23481(!this.strafeLeft);
            } else {
               mc.field_1690.field_1913.method_23481(false);
               mc.field_1690.field_1849.method_23481(false);
            }

            if (this.shouldInitiateCritJump(dist)) {
               if (mc.field_1690 != null) {
                  mc.field_1690.field_1903.method_23481(true);
               }

               this.jumpPulseTicks = 1;
               this.critJumpTicks = this.hasLowCeiling() ? 8 : 14;
               this.jumpCooldown = this.hasLowCeiling() ? 6 : 10;
            } else if (this.jumpPulseTicks > 0) {
               this.jumpPulseTicks--;
               if (this.jumpPulseTicks == 0 && mc.field_1690 != null) {
                  mc.field_1690.field_1903.method_23481(false);
               }
            } else if (mc.field_1690 != null && (!(Boolean)this.jumpReset.get() || mc.field_1724.field_6235 != 9)) {
               mc.field_1690.field_1903.method_23481(false);
            }

            if (this.critJumpTicks > 0) {
               this.critJumpTicks--;
               if (mc.field_1724.method_24828() && this.critJumpTicks < (this.hasLowCeiling() ? 6 : 10)) {
                  this.critJumpTicks = 0;
               }
            }
         }
      }
   }

   private boolean shouldInitiateCritJump(double dist) {
      if (!(Boolean)this.autoCrit.get() || this.critMode.get() == AutoSwordBotting.CritMode.OFF || this.critMode.get() == AutoSwordBotting.CritMode.FALL_ONLY) {
         return false;
      } else if (mc.field_1724 == null || this.currentTarget == null) {
         return false;
      } else if (!mc.field_1724.method_24828()) {
         return false;
      } else if (mc.field_1724.method_5799() || mc.field_1724.method_5771() || mc.field_1724.method_6101()) {
         return false;
      } else if (this.jumpCooldown > 0) {
         return false;
      } else if (mc.field_1724.method_6032() < (Double)this.minCritHealth.get()) {
         return false;
      } else if (dist < 1.85 || dist > (Double)this.reach.get() - 0.2) {
         return false;
      } else if (this.getYawDeltaTo(this.currentTarget) > 22.0) {
         return false;
      } else if (this.getApproachSpeed(this.currentTarget) < -0.15) {
         return false;
      } else if (mc.field_1724.field_6235 >= 6) {
         return false;
      } else if (this.hasObstacleAhead(1.4)) {
         return false;
      } else if (!this.currentTarget.method_24828() && this.currentTarget.method_18798().field_1351 < -0.05 && dist < 2.5) {
         return false;
      } else {
         boolean var3 = (Boolean)this.ceilingCrits.get() && this.hasLowCeiling();
         float var4 = mc.field_1724.method_7261(0.5F);
         boolean var5 = this.isAxe(mc.field_1724.method_6047());
         float var6 = var3 ? (var5 ? 0.88F : 0.8F) : (var5 ? 0.75F : 0.6F);
         if (var4 < var6) {
            return false;
         } else if (this.critMode.get() == AutoSwordBotting.CritMode.AGGRESSIVE) {
            return true;
         } else if (this.punishDisabledShieldTicks > 0) {
            return true;
         } else if (this.currentTarget.field_6235 > 0 || this.comboCount > 0) {
            return true;
         } else if (this.currentTarget.method_6039()) {
            return true;
         } else if (!this.currentTarget.method_24828() && this.currentTarget.method_18798().field_1351 <= 0.05) {
            return true;
         } else {
            return this.getYawDeltaTo(this.currentTarget) > 60.0 ? true : dist >= 2.1 && dist <= 2.7 && !this.currentTarget.method_5624();
         }
      }
   }

   private boolean hasLowCeiling() {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         class_2338 var1 = mc.field_1724.method_24515();

         for (int var2 = 2; var2 <= (3); var2++) {
            class_2338 var3 = var1.method_10086(var2);
            class_2680 var4 = mc.field_1687.method_8320(var3);
            if (!var4.method_26215() && !var4.method_26220(mc.field_1687, var3).method_1110()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void handleHitSprintReset(class_1657 target) {
      if (this.resetMode.get() != AutoSwordBotting.ResetMode.OFF && mc.field_1724 != null) {
         if (mc.field_1724.method_5624() || mc.field_1690 != null && mc.field_1690.field_1894.method_1434()) {
            double var2 = mc.field_1724.method_5739(target);
            double var4 = this.getApproachSpeed(target);
            AutoSwordBotting.ResetMode var6 = (AutoSwordBotting.ResetMode)this.resetMode.get();
            boolean var7;
            if (var6 == AutoSwordBotting.ResetMode.STAP_ONLY) {
               var7 = true;
            } else if (var6 == AutoSwordBotting.ResetMode.WTAP_ONLY) {
               var7 = false;
            } else {
               boolean var8 = var2 <= (Double)this.sTapDistance.get();
               boolean var9 = (Boolean)this.approachCheck.get() && var4 < -0.1 && var2 > 2.15;
               boolean var10 = (Boolean)this.approachCheck.get() && var4 > 0.12;
               boolean var11 = target.method_6039();
               boolean var12 = mc.field_1724.method_6032() < 10.0 && var2 < 2.5;
               if (var9) {
                  var7 = false;
               } else if (!var8 && !var10 && !var11 && !var12) {
                  var7 = false;
               } else {
                  var7 = true;
               }
            }

            int var13 = 2;
            if ((Boolean)this.dynamicTapTicks.get()) {
               if (var7) {
                  if (var2 < 1.8 || var4 > 0.25) {
                     var13 = 3;
                  } else if (var2 > 2.3) {
                     var13 = 1;
                  } else {
                     var13 = 2;
                  }
               } else if (var2 > 2.7) {
                  var13 = 1;
               } else {
                  var13 = 2;
               }
            }

            this.currentTap = var7 ? AutoSwordBotting.ActiveTap.S_TAP : AutoSwordBotting.ActiveTap.W_TAP;
            this.tapTicks = var13;
         }
      }
   }

   private void handleTap() {
      if (mc.field_1690 != null) {
         if (this.tapTicks > 0) {
            this.tapTicks--;
            if (this.currentTap == AutoSwordBotting.ActiveTap.S_TAP) {
               mc.field_1690.field_1894.method_23481(false);
               mc.field_1690.field_1881.method_23481(true);
               if (mc.field_1724 != null) {
                  mc.field_1724.method_5728(false);
               }
            } else if (this.currentTap == AutoSwordBotting.ActiveTap.W_TAP) {
               mc.field_1690.field_1894.method_23481(false);
               mc.field_1690.field_1881.method_23481(false);
               if (mc.field_1724 != null) {
                  mc.field_1724.method_5728(false);
               }
            }

            if (this.tapTicks == 0) {
               if (this.currentTap == AutoSwordBotting.ActiveTap.S_TAP) {
                  mc.field_1690.field_1881.method_23481(false);
               }

               this.currentTap = AutoSwordBotting.ActiveTap.NONE;
            }
         }
      }
   }

   private void handleAutoBlock(double dist) {
      if ((Boolean)this.autoBlock.get() && mc.field_1690 != null && mc.field_1724 != null) {
         boolean var3 = mc.field_1724.method_6079().method_31574(class_1802.field_8255);
         if (var3) {
            float var4 = mc.field_1724.method_7261(0.5F);
            if (var4 < 0.85F && dist <= 3.2 && this.currentTarget != null && !this.currentTarget.method_6039()) {
               mc.field_1690.field_1904.method_23481(true);
            } else {
               mc.field_1690.field_1904.method_23481(false);
            }
         }
      }
   }

   private void handleAttack(double dist) {
      if (mc.field_1724 != null && mc.field_1761 != null && this.currentTarget != null) {
         if (!(dist > (Double)this.reach.get())) {
            float var3 = mc.field_1724.method_7261(0.5F);
            if (!(var3 < (float)((Double)this.minCooldown.get()).doubleValue())) {
               if (this.isCrosshairOnTarget(this.currentTarget) || !(this.getYawDeltaTo(this.currentTarget) > 30.0)) {
                  if ((Boolean)this.smartTrade.get()) {
                     float var4 = mc.field_1724.method_6032();
                     float var5 = this.currentTarget.method_6032();
                     double var6 = var4 / Math.max(1.0F, var5);
                     boolean var8 = (Boolean)this.punishRecovery.get() && this.enemySwungInWindow;
                     boolean var9 = (Boolean)this.counterReach.get() && dist >= (Double)this.reach.get() - 0.25 && dist <= (Double)this.reach.get();
                     boolean var10 = var6 < (Double)this.tradeHpAdvantage.get() || var4 <= 7.0F;
                     if (var10 && !var8 && !var9) {
                        boolean var11 = this.currentTarget.field_6235 > 0;
                        boolean var12 = this.getYawDeltaTo(this.currentTarget) > 55.0;
                        boolean var13 = !this.currentTarget.method_24828() && this.currentTarget.method_18798().field_1351 <= 0.05;
                        if (!var11 && !var12 && !var13 && dist < 2.3) {
                           return;
                        }
                     }
                  }

                  if (this.critJumpTicks <= 0 || !mc.field_1724.method_24828() || !(mc.field_1724.method_18798().field_1351 >= 0.0)) {
                     boolean var14 = !mc.field_1724.method_24828()
                        && !mc.field_1724.method_6101()
                        && !mc.field_1724.method_5799()
                        && !mc.field_1724.method_5771();
                     if ((Boolean)this.autoCrit.get() && this.critMode.get() != AutoSwordBotting.CritMode.OFF && var14) {
                        boolean var15 = (Boolean)this.ceilingCrits.get() && this.hasLowCeiling();
                        boolean var17 = mc.field_1724.field_6017 > 0.0 || mc.field_1724.method_18798().field_1351 <= (var15 ? 0.02 : -0.02);
                        if (!var17) {
                           return;
                        }
                     }

                     boolean var16 = mc.field_1724.method_5624();
                     boolean var18 = var14 && (mc.field_1724.field_6017 > 0.0 || mc.field_1724.method_18798().field_1351 < -0.01);
                     if (var18 && (Boolean)this.smartCritSprint.get()) {
                        mc.field_1724.method_5728(false);
                        if (mc.field_1690 != null) {
                           mc.field_1690.field_1867.method_23481(false);
                        }
                     }

                     if ((Boolean)this.autoBlock.get() && mc.field_1690 != null) {
                        mc.field_1690.field_1904.method_23481(false);
                     }

                     WorldUtils.hitEntity(this.currentTarget, true);
                     this.critJumpTicks = 0;
                     if (var16 && this.currentTap == AutoSwordBotting.ActiveTap.NONE && mc.field_1724 != null) {
                        mc.field_1724.method_5728(true);
                     }

                     if ((Boolean)this.shieldBreaker.get() && this.previousWeaponSlot != -1) {
                        this.axeSwapCooldown = 2;
                        InventoryUtil.swap(this.previousWeaponSlot);
                        this.previousWeaponSlot = -1;
                        this.punishDisabledShieldTicks = 40;
                     }
                  }
               }
            }
         }
      }
   }

   private void handleRetreat(double dist) {
      if (mc.field_1690 != null && mc.field_1724 != null && this.currentTarget != null) {
         this.retreatTicks++;
         if (this.shouldInitiateSurpriseTurnaround(dist)) {
            this.surpriseTurnaroundTicks = 12;
            this.retreatTicks = 0;
            this.turnaroundCooldownTicks = 30;
            this.stopRetreatEating();
         }

         if (this.surpriseTurnaroundTicks > 0) {
            this.handleSurpriseTurnaround(dist);
         } else {
            boolean var3 = this.isEnemyChasingUs(this.currentTarget);
            if (!var3 && dist >= 5.0) {
               this.handlePassiveRetreat(dist);
            } else {
               mc.field_1690.field_1894.method_23481(true);
               mc.field_1690.field_1881.method_23481(false);
               mc.field_1724.method_5728(!mc.field_1724.field_5976);
               if (this.shouldRetreatJump()) {
                  if (mc.field_1724.method_24828()) {
                     mc.field_1690.field_1903.method_23481(true);
                  }
               } else if (mc.field_1724.method_24828()) {
                  if (dist < 8.0 && mc.field_1724.method_5624() && !this.hasObstacleAhead(2.5)) {
                     mc.field_1690.field_1903.method_23481(true);
                  } else {
                     mc.field_1690.field_1903.method_23481(false);
                  }
               }

               if ((Boolean)this.evasiveZigZag.get()) {
                  this.zigZagTicks++;
                  if (this.zigZagTicks >= 12 || mc.field_1724.field_5976) {
                     this.zigZagTicks = 0;
                     this.zigZagLeft = !this.zigZagLeft;
                  }

                  boolean var4 = this.isSideBlocked(this.zigZagLeft);
                  if (!var4) {
                     mc.field_1690.field_1913.method_23481(this.zigZagLeft);
                     mc.field_1690.field_1849.method_23481(!this.zigZagLeft);
                  } else {
                     mc.field_1690.field_1913.method_23481(false);
                     mc.field_1690.field_1849.method_23481(false);
                  }
               } else {
                  mc.field_1690.field_1913.method_23481(false);
                  mc.field_1690.field_1849.method_23481(false);
               }

               if ((Boolean)this.rearGlance.get()) {
                  if (this.glanceTicks > 0) {
                     this.glanceTicks--;
                  } else {
                     this.glanceCooldown--;
                     if (this.glanceCooldown <= 0) {
                        this.glanceTicks = 5;
                        this.glanceCooldown = 40 + ThreadLocalRandom.current().nextInt(25);
                     }
                  }
               } else {
                  this.glanceTicks = 0;
               }

               if ((Boolean)this.retreatAutoEat.get()) {
                  this.handleRetreatEating(dist);
               }

               if (dist <= (Double)this.reach.get() && mc.field_1724.method_7261(0.5F) >= 0.9F) {
                  this.handleWeaponSelection();
                  if (this.isCrosshairOnTarget(this.currentTarget) || this.getYawDeltaTo(this.currentTarget) <= 45.0) {
                     WorldUtils.hitEntity(this.currentTarget, true);
                  }
               }
            }
         }
      }
   }

   private boolean shouldInitiateSurpriseTurnaround(double dist) {
      if (!(Boolean)this.surpriseTurnaround.get() || this.turnaroundCooldownTicks > 0) {
         return false;
      }

      if (mc.field_1724 != null && this.currentTarget != null) {
         if (mc.field_1724.method_6032() < 3.0F) {
            return false;
         }

         float var3 = mc.field_1724.method_7261(0.5F);
         boolean var4 = var3 >= 0.9F;
         boolean var5 = dist <= 4.2 && this.isEnemyCharging(this.currentTarget);
         boolean var6 = this.retreatTicks >= (10);
         return var4 && var5 && var6 ? true : this.isCornered() && dist <= 5.0 && var3 >= 0.85F;
      } else {
         return false;
      }
   }

   private void handleSurpriseTurnaround(double dist) {
      this.surpriseTurnaroundTicks--;
      if (mc.field_1690 != null) {
         mc.field_1690.field_1894.method_23481(true);
         mc.field_1690.field_1881.method_23481(false);
         mc.field_1690.field_1913.method_23481(false);
         mc.field_1690.field_1849.method_23481(false);
      }

      mc.field_1724.method_5728(true);
      this.handleWeaponSelection();
      if (this.shouldInitiateCritJump(dist)) {
         if (mc.field_1690 != null) {
            mc.field_1690.field_1903.method_23481(true);
         }

         this.jumpPulseTicks = 1;
         this.critJumpTicks = this.hasLowCeiling() ? 8 : 14;
         this.jumpCooldown = this.hasLowCeiling() ? 6 : 10;
      }

      if (dist <= (Double)this.reach.get()) {
         this.handleAttack(dist);
         if (this.currentTarget.field_6235 > 0) {
            if (mc.field_1724.method_6032() >= (float)((Double)this.reengageHealth.get()).doubleValue()) {
               this.botState = AutoSwordBotting.BotState.COMBAT;
               this.surpriseTurnaroundTicks = 0;
            } else {
               this.surpriseTurnaroundTicks = 0;
               this.retreatTicks = 0;
            }
         }
      }
   }

   private void handlePassiveRetreat(double dist) {
      if (mc.field_1690 != null) {
         if (dist < 5.2) {
            mc.field_1690.field_1894.method_23481(false);
            mc.field_1690.field_1881.method_23481(true);
            mc.field_1690.field_1913.method_23481(false);
            mc.field_1690.field_1849.method_23481(false);
         } else if (dist > 8.0) {
            mc.field_1690.field_1894.method_23481(false);
            mc.field_1690.field_1881.method_23481(false);
         } else {
            mc.field_1690.field_1894.method_23481(false);
            mc.field_1690.field_1881.method_23481(false);
         }

         mc.field_1690.field_1903.method_23481(false);
      }

      mc.field_1724.method_5728(false);
      if ((Boolean)this.retreatAutoEat.get()) {
         this.handleRetreatEating(dist);
      }

      if (mc.field_1724.method_6032() >= (float)((Double)this.reengageHealth.get()).doubleValue()) {
         this.botState = AutoSwordBotting.BotState.COMBAT;
         this.stopRetreatEating();
      }
   }

   private boolean isEnemyChasingUs(class_1657 target) {
      if (mc.field_1724 != null && target != null) {
         double var2 = this.getApproachSpeed(target);
         if (var2 > 0.06) {
            return true;
         }

         double var4 = this.getYawDeltaFrom(target, mc.field_1724);
         if (target.method_5624() && var4 < 65.0) {
            return true;
         }

         double var6 = mc.field_1724.method_5739(target);
         return var6 < 3.8;
      } else {
         return false;
      }
   }

   private double getYawDeltaFrom(class_1297 source, class_1297 target) {
      double var3 = target.method_23317() - source.method_23317();
      double var5 = target.method_23321() - source.method_23321();
      float var7 = (float)class_3532.method_15338(Math.toDegrees(Math.atan2(var5, var3)) - 90.0);
      return Math.abs(class_3532.method_15393(var7 - source.method_36454()));
   }

   private boolean isCornered() {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         double var1 = Math.toRadians(mc.field_1724.method_36454());
         double var3 = -Math.sin(var1) * 1.4;
         double var5 = Math.cos(var1) * 1.4;
         class_2338 var7 = class_2338.method_49637(mc.field_1724.method_23317() + var3, mc.field_1724.method_23318() + 1.0, mc.field_1724.method_23321() + var5);
         return this.isSolidBlock(var7) && mc.field_1724.field_5976;
      } else {
         return false;
      }
   }

   private boolean isSideBlocked(boolean left) {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         double var2 = Math.toRadians(mc.field_1724.method_36454() + (left ? -90.0 : 90.0));
         double var4 = -Math.sin(var2) * 1.0;
         double var6 = Math.cos(var2) * 1.0;
         class_2338 var8 = class_2338.method_49637(mc.field_1724.method_23317() + var4, mc.field_1724.method_23318() + 1.0, mc.field_1724.method_23321() + var6);
         return this.isSolidBlock(var8);
      } else {
         return false;
      }
   }

   private boolean isSolidBlock(class_2338 pos) {
      if (mc.field_1687 == null) {
         return false;
      }

      class_2680 var2 = mc.field_1687.method_8320(pos);
      return !var2.method_26215() && !var2.method_26220(mc.field_1687, pos).method_1110();
   }

   private boolean hasObstacleAhead(double forwardDist) {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         double var3 = Math.toRadians(mc.field_1724.method_36454());
         double var5 = -Math.sin(var3) * forwardDist;
         double var7 = Math.cos(var3) * forwardDist;
         double var9 = mc.field_1724.method_23317() + var5;
         double var11 = mc.field_1724.method_23321() + var7;
         double var13 = mc.field_1724.method_23318();
         class_2338 var15 = class_2338.method_49637(var9, var13 + 1.0, var11);
         class_2338 var16 = class_2338.method_49637(var9, var13 + 1.8, var11);
         return this.isSolidBlock(var15) || this.isSolidBlock(var16);
      } else {
         return false;
      }
   }

   private float findBestEscapeYaw(float toEnemyYaw, class_1657 entity) {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         float var3 = class_3532.method_15393(toEnemyYaw + 180.0F);
         float[] var4 = new float[]{0.0F, 25.0F, -25.0F, 50.0F, -50.0F, 75.0F, -75.0F, 100.0F, -100.0F, 125.0F, -125.0F, 150.0F, -150.0F};
         float var5 = var3;
         double var6 = -99999.0;
         double var8 = mc.field_1724.method_23317();
         double var10 = mc.field_1724.method_23318();
         double var12 = mc.field_1724.method_23321();

         for (float var17 : var4) {
            float var18 = class_3532.method_15393(var3 + var17);
            double var19 = Math.toRadians(var18);
            double var21 = -Math.sin(var19);
            double var23 = Math.cos(var19);
            double var25 = 0.0;
            boolean var27 = false;

            for (double var28 = 0.8; var28 <= 4.0; var28 += 0.8) {
               double var30 = var8 + var21 * var28;
               double var32 = var12 + var23 * var28;
               class_2338 var34 = class_2338.method_49637(var30, var10, var32);
               class_2338 var35 = class_2338.method_49637(var30, var10 + 1.0, var32);
               class_2338 var36 = class_2338.method_49637(var30, var10 + 1.8, var32);
               boolean var37 = this.isSolidBlock(var34);
               boolean var38 = this.isSolidBlock(var35);
               boolean var39 = this.isSolidBlock(var36);
               if (var38 || var39) {
                  var27 = true;
                  var25 = var28 - 0.8;
                  break;
               }

               if (var37) {
                  class_2338 var40 = class_2338.method_49637(var30, var10 + 2.0, var32);
                  if (this.isSolidBlock(var40)) {
                     var27 = true;
                     var25 = var28 - 0.8;
                     break;
                  }
               }

               class_2338 var45 = class_2338.method_49637(var30, var10 - 1.0, var32);
               class_2338 var41 = class_2338.method_49637(var30, var10 - 2.0, var32);
               class_2338 var42 = class_2338.method_49637(var30, var10 - 3.0, var32);
               if (!var37 && !this.isSolidBlock(var45) && !this.isSolidBlock(var41) && !this.isSolidBlock(var42)) {
                  var27 = true;
                  var25 = var28 - 0.8;
                  break;
               }

               var25 = var28;
            }

            double var43 = var25 * 15.0;
            var43 -= Math.abs(var17) * 0.12;
            if (var27 && var25 < 2.0) {
               var43 -= 100.0 * (2.0 - var25);
            }

            if (mc.field_1724.field_5976 && Math.abs(var17) >= 45.0) {
               var43 += 15.0;
            }

            if (var43 > var6) {
               var6 = var43;
               var5 = var18;
            }
         }

         this.lastSmartEscapeYaw = var5;
         return var5;
      } else {
         return class_3532.method_15393(toEnemyYaw + 180.0F);
      }
   }

   private boolean shouldRetreatJump() {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         double var1 = Math.toRadians(mc.field_1724.method_36454());
         double var3 = -Math.sin(var1) * 1.2;
         double var5 = Math.cos(var1) * 1.2;
         class_2338 var7 = class_2338.method_49637(mc.field_1724.method_23317() + var3, mc.field_1724.method_23318(), mc.field_1724.method_23321() + var5);
         class_2338 var8 = class_2338.method_49637(mc.field_1724.method_23317() + var3, mc.field_1724.method_23318() + 1.0, mc.field_1724.method_23321() + var5);
         class_2338 var9 = class_2338.method_49637(mc.field_1724.method_23317() + var3, mc.field_1724.method_23318() + 1.8, mc.field_1724.method_23321() + var5);
         return this.isSolidBlock(var7) && !this.isSolidBlock(var8) && !this.isSolidBlock(var9);
      } else {
         return false;
      }
   }

   private void handleRetreatEating(double dist) {
      if (mc.field_1724 != null && mc.field_1690 != null) {
         if (dist < 3.5) {
            this.stopRetreatEating();
         } else {
            class_1799 var3 = mc.field_1724.method_6079();
            if (this.isConsumableHeal(var3)) {
               mc.field_1690.field_1904.method_23481(true);
               this.eatTicks++;
            } else {
               int var4 = this.findGappleInHotbar();
               if (var4 != (-1)) {
                  if (this.retreatEatSlot == (-1)) {
                     this.retreatEatSlot = mc.field_1724.method_31548().method_67532();
                  }

                  InventoryUtil.swap(var4);
                  mc.field_1690.field_1904.method_23481(true);
                  this.eatTicks++;
               }
            }
         }
      }
   }

   private void stopRetreatEating() {
      if (mc.field_1690 != null && this.eatTicks > 0) {
         mc.field_1690.field_1904.method_23481(false);
      }

      this.eatTicks = 0;
      if (this.retreatEatSlot != (-1) && mc.field_1724 != null) {
         InventoryUtil.swap(this.retreatEatSlot);
         this.retreatEatSlot = -1;
      }
   }

   private boolean isConsumableHeal(class_1799 stack) {
      return stack != null && !stack.method_7960()
         ? stack.method_31574(class_1802.field_8463)
            || stack.method_31574(class_1802.field_8367)
            || stack.method_31574(class_1802.field_8574)
            || stack.method_7909().method_57347() != null && stack.method_7909().method_57347().method_57832(class_9334.field_50075)
         : false;
   }

   private int findGappleInHotbar() {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var1 = 0; var1 < (9); var1++) {
         class_1799 var2 = mc.field_1724.method_31548().method_5438(var1);
         if (var2.method_31574(class_1802.field_8367) || var2.method_31574(class_1802.field_8463)) {
            return var1;
         }
      }

      for (int var3 = 0; var3 < (9); var3++) {
         class_1799 var4 = mc.field_1724.method_31548().method_5438(var3);
         if (this.isConsumableHeal(var4)) {
            return var3;
         }
      }

      return -1;
   }

   private void aimEscape(class_1657 entity, float frameTime) {
      if (mc.field_1724 != null) {
         double var3 = class_3532.method_15363(frameTime, 0.001F, 0.05F);
         double var5 = entity.method_23317() - mc.field_1724.method_23317();
         double var7 = entity.method_23321() - mc.field_1724.method_23321();
         float var9 = (float)class_3532.method_15338(Math.toDegrees(Math.atan2(var7, var5)) - 90.0);
         float var10 = this.findBestEscapeYaw(var9, entity);
         float var11 = 0.0F;
         float var12 = mc.field_1724.method_36454();
         float var13 = mc.field_1724.method_36455();
         float var14 = class_3532.method_15393(var10 - var12);
         float var15 = var11 - var13;
         float var16 = (float)((Double)this.aimSpeed.get() * 26.0);
         float var17 = this.calculateDesiredSpeed(var14, var16);
         this.velocityYaw = this.velocityYaw + (var17 - this.velocityYaw) * (1.0F - (float)Math.exp(-22.0 * var3));
         float var18 = this.velocityYaw * (float)var3;
         float var19 = (float)((Double)this.verticalSpeed.get() * 16.0);
         float var20 = this.calculateDesiredSpeed(var15, var19);
         this.velocityPitch = this.velocityPitch + (var20 - this.velocityPitch) * (1.0F - (float)Math.exp(-18.0 * var3));
         float var21 = this.velocityPitch * (float)var3;
         float var22 = var12 + var18;
         float var23 = class_3532.method_15363(var13 + var21, -90.0F, 90.0F);
         mc.field_1724.method_36456(var22);
         mc.field_1724.method_36457(var23);
         this.syncPlayerModelRotations(var22, var23);
      }
   }

   private double getApproachSpeed(class_1657 target) {
      if (mc.field_1724 != null && target != null) {
         class_243 var2 = new class_243(target.method_23317() - mc.field_1724.method_23317(), 0.0, target.method_23321() - mc.field_1724.method_23321());
         double var3 = var2.method_1033();
         if (var3 > 0.001) {
            class_243 var5 = var2.method_1021(1.0 / var3);
            class_243 var6 = target.method_18798().method_1020(mc.field_1724.method_18798());
            return -var6.method_1026(var5);
         } else {
            return 0.0;
         }
      } else {
         return 0.0;
      }
   }

   private boolean isEnemyCharging(class_1657 target) {
      return this.getApproachSpeed(target) > 0.12 && target.method_5624();
   }

   public BoolSetting getFreecamSupportSetting() {
      return this.freecamSupport;
   }

   public BoolSetting getSmartTradeSetting() {
      return this.smartTrade;
   }

   public DoubleSetting getTradeHpAdvantageSetting() {
      return this.tradeHpAdvantage;
   }

   public BoolSetting getPunishRecoverySetting() {
      return this.punishRecovery;
   }

   public BoolSetting getCounterReachSetting() {
      return this.counterReach;
   }

   public BoolSetting getComboDenySetting() {
      return this.comboDeny;
   }

   public BoolSetting getDefensiveParrySetting() {
      return this.defensiveParry;
   }

   public BoolSetting getStrafeEscapeSetting() {
      return this.strafeEscape;
   }

   public DoubleSetting getComboEscapeDistSetting() {
      return this.comboEscapeDist;
   }

   public BoolSetting getRunAwaySetting() {
      return this.runAway;
   }

   public DoubleSetting getRunAwayHealthSetting() {
      return this.runAwayHealth;
   }

   public DoubleSetting getReengageHealthSetting() {
      return this.reengageHealth;
   }

   public DoubleSetting getRetreatDistanceSetting() {
      return this.retreatDistance;
   }

   public BoolSetting getRearGlanceSetting() {
      return this.rearGlance;
   }

   public BoolSetting getEvasiveZigZagSetting() {
      return this.evasiveZigZag;
   }

   public BoolSetting getRetreatAutoEatSetting() {
      return this.retreatAutoEat;
   }

   public BoolSetting getSurpriseTurnaroundSetting() {
      return this.surpriseTurnaround;
   }

   public AutoSwordBotting.BotState getBotState() {
      return this.botState;
   }

   @Override
   public String getInfo() {
      if (this.currentTarget != null && mc.field_1724 != null) {
         double var1 = mc.field_1724.method_5739(this.currentTarget);
         String var3 = this.currentTarget.method_5477().getString();
         String var4 = this.botState == AutoSwordBotting.BotState.RETREAT
            ? " [Retreating]"
            : (this.comboDenyTicks > 0 ? " [Combo Deny]" : "");
         String var5 = this.autoCrit.get() && this.critMode.get() != AutoSwordBotting.CritMode.OFF
            ? " [Crit]"
            : "";
         String var6 = this.currentTap != AutoSwordBotting.ActiveTap.NONE
            ? " ["
               + (
                  this.currentTap == AutoSwordBotting.ActiveTap.S_TAP
                     ? "S-Tap"
                     : "W-Tap"
               )
               + "]"
            : "";
         return String.format(
            "%s (%.1fm)%s%s%s%s",
            var3,
            var1,
            this.comboCount > 1 ? " x" + this.comboCount : "",
            var4,
            var5,
            var6
         );
      } else {
         return null;
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

   private enum ActiveTap {
      NONE,
      W_TAP,
      S_TAP;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoSwordBotting.ActiveTap[] $values() {
         AutoSwordBotting.ActiveTap[] var10000 = new AutoSwordBotting.ActiveTap[3];
         var10000[0] = NONE;
         var10000[1] = W_TAP;
         var10000[2] = S_TAP;
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
         NONE = new AutoSwordBotting.ActiveTap();
         var10002 = new byte[5];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-59);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(61);
         W_TAP = new AutoSwordBotting.ActiveTap();
         var10002 = new byte[5];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-59);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(61);
         S_TAP = new AutoSwordBotting.ActiveTap();
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

   public enum BotState {
      COMBAT,
      RETREAT;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoSwordBotting.BotState[] $values() {
         AutoSwordBotting.BotState[] var10000 = new AutoSwordBotting.BotState[2];
         var10000[0] = COMBAT;
         var10000[1] = RETREAT;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-100);
         var10002[4] = (byte)(123);
         var10002[5] = (byte)(10);
         COMBAT = new AutoSwordBotting.BotState();
         var10002 = new byte[7];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(127);
         var10002[5] = (byte)(31);
         var10002[6] = (byte)(29);
         RETREAT = new AutoSwordBotting.BotState();
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
      SMART,
      AGGRESSIVE,
      FALL_ONLY,
      OFF;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoSwordBotting.CritMode[] $values() {
         AutoSwordBotting.CritMode[] var10000 = new AutoSwordBotting.CritMode[4];
         var10000[0] = SMART;
         var10000[1] = AGGRESSIVE;
         var10000[2] = FALL_ONLY;
         var10000[3] = OFF;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-41);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(-61);
         SMART = new AutoSwordBotting.CritMode();
         var10002 = new byte[10];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-35);
         var10002[2] = (byte)(91);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(-46);
         var10002[5] = (byte)(61);
         var10002[6] = (byte)(-116);
         var10002[7] = (byte)(108);
         var10002[8] = 1;
         var10002[9] = (byte)(-25);
         AGGRESSIVE = new AutoSwordBotting.CritMode();
         var10002 = new byte[9];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(-56);
         var10002[5] = (byte)(33);
         var10002[6] = (byte)(-111);
         var10002[7] = (byte)(105);
         var10002[8] = (byte)(14);
         FALL_ONLY = new AutoSwordBotting.CritMode();
         var10002 = new byte[3];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-36);
         var10002[2] = (byte)(90);
         OFF = new AutoSwordBotting.CritMode();
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

   public enum Priority {
      DISTANCE,
      HEALTH,
      ANGLE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoSwordBotting.Priority[] $values() {
         AutoSwordBotting.Priority[] var10000 = new AutoSwordBotting.Priority[3];
         var10000[0] = DISTANCE;
         var10000[1] = HEALTH;
         var10000[2] = ANGLE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[8];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-35);
         var10002[5] = (byte)(-65);
         var10002[6] = (byte)(67);
         var10002[7] = (byte)(-85);
         DISTANCE = new AutoSwordBotting.Priority();
         var10002 = new byte[6];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(-56);
         var10002[5] = (byte)(-71);
         HEALTH = new AutoSwordBotting.Priority();
         var10002 = new byte[5];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-44);
         var10002[2] = (byte)(91);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(-39);
         ANGLE = new AutoSwordBotting.Priority();
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

   public enum ResetMode {
      ADAPTIVE,
      WTAP_ONLY,
      STAP_ONLY,
      OFF;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoSwordBotting.ResetMode[] $values() {
         AutoSwordBotting.ResetMode[] var10000 = new AutoSwordBotting.ResetMode[4];
         var10000[0] = ADAPTIVE;
         var10000[1] = WTAP_ONLY;
         var10000[2] = STAP_ONLY;
         var10000[3] = OFF;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[8];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-34);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-114);
         var10002[4] = (byte)(108);
         var10002[5] = (byte)(15);
         var10002[6] = (byte)(-5);
         var10002[7] = (byte)(-36);
         ADAPTIVE = new AutoSwordBotting.ResetMode();
         var10002 = new byte[9];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-50);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-114);
         var10002[4] = (byte)(103);
         var10002[5] = (byte)(9);
         var10002[6] = (byte)(-29);
         var10002[7] = (byte)(-43);
         var10002[8] = (byte)(14);
         WTAP_ONLY = new AutoSwordBotting.ResetMode();
         var10002 = new byte[9];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-50);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-114);
         var10002[4] = (byte)(103);
         var10002[5] = (byte)(9);
         var10002[6] = (byte)(-29);
         var10002[7] = (byte)(-43);
         var10002[8] = (byte)(14);
         STAP_ONLY = new AutoSwordBotting.ResetMode();
         var10002 = new byte[3];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-36);
         var10002[2] = (byte)(90);
         OFF = new AutoSwordBotting.ResetMode();
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

   public enum StrafeMode {
      CIRCLE,
      RANDOM,
      NONE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoSwordBotting.StrafeMode[] $values() {
         AutoSwordBotting.StrafeMode[] var10000 = new AutoSwordBotting.StrafeMode[3];
         var10000[0] = CIRCLE;
         var10000[1] = RANDOM;
         var10000[2] = NONE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-99);
         var10002[4] = (byte)(78);
         var10002[5] = (byte)(-8);
         CIRCLE = new AutoSwordBotting.StrafeMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-102);
         var10002[4] = (byte)(77);
         var10002[5] = (byte)(-16);
         RANDOM = new AutoSwordBotting.StrafeMode();
         var10002 = new byte[4];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         NONE = new AutoSwordBotting.StrafeMode();
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
