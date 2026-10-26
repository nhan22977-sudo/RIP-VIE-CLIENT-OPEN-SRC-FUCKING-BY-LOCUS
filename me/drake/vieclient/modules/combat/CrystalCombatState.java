package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class CrystalCombatState {
   private final class_310 mc = class_310.method_1551();
   private final boolean followAim;
   public boolean humanize = true;
   public int reactionDelay = 1;
   public double rotationSpeed = 8.0;
   public int switchDelay = 1;
   public int placeDelay = 1;
   public int breakDelay = 1;
   public int waitEntityTimeout = 8;
   public double maxPlaceReach = 4.5;
   public double maxBreakReach = 3.0;
   public boolean debug = false;
   public boolean autoBreak = true;
   public boolean autoSelectCrystal = false;
   public int autoBreakDelay = 0;
   private CrystalCombatState.State state = CrystalCombatState.State.IDLE;
   private class_2338 targetBasePos = null;
   private class_2350 targetFace = class_2350.field_11036;
   private class_2338 placedCrystalPos = null;
   private class_1297 targetCrystalEntity = null;
   private boolean isObsidianPhase = false;
   private class_3965 placementHit;
   private final CombatAim aim = new CombatAim();
   private int placeClock;
   private int breakClock;
   private int spawnBreakClock;
   private int lastAttackedId = -1;
   private Object attackWorld;
   private int lastRotationTick = -1;
   private boolean actionSent;
   private int aimTicks;
   private int selectedReadyAt;
   private long attacksSent;
   private class_2338 manualOverrideBase;
   private int reactionClock = 0;
   private int switchClock = 0;
   private int waitEntityClock = 0;
   private int cooldownClock = 0;
   private long lastTickTime = 0L;
   private static final long LAG_SPIKE_MS = 250L;
   private int lastSelectedSlot = -1;
   private float lastPlayerYaw = 0.0F;
   private float lastPlayerPitch = 0.0F;
   private boolean playerIntervened = false;
   private long lastLogTime = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static int computeBreakDelay(int minCps, int maxCps, Random random) {
      if (maxCps < minCps) {
         maxCps = minCps;
      }

      if (minCps < 1) {
         minCps = 1;
      }

      int var3 = minCps + random.nextInt(Math.max(1, maxCps - minCps + 1));
      return Math.max(1, Math.round(20.0F / var3));
   }

   public CrystalCombatState() {
      this(true);
   }

   public CrystalCombatState(boolean followAim) {
      this.followAim = followAimx;
   }

   public CrystalCombatState.State getState() {
      return this.state;
   }

   public boolean isBusy() {
      return this.state != CrystalCombatState.State.IDLE && this.state != CrystalCombatState.State.ACQUIRE && this.state != CrystalCombatState.State.COOLDOWN;
   }

   public long getAttacksSent() {
      return this.attacksSent;
   }

   public int getExpectedSlot() {
      return this.lastSelectedSlot;
   }

   public void setStateForTest(CrystalCombatState.State s) {
      this.state = s;
   }

   public class_2338 getTargetBasePos() {
      return this.targetBasePos;
   }

   public class_2338 getPlacedCrystalPos() {
      return this.placedCrystalPos;
   }

   public class_1297 getTargetCrystalEntity() {
      return this.targetCrystalEntity;
   }

   public int getWaitEntityClock() {
      return this.waitEntityClock;
   }

   public int getCooldownClock() {
      return this.cooldownClock;
   }

   public void setWaitEntityClockForTest(int ticks) {
      this.waitEntityClock = ticks;
   }

   public void setCooldownClockForTest(int ticks) {
      this.cooldownClock = ticks;
   }

   public void setPlacedCrystalPosForTest(class_2338 pos) {
      this.placedCrystalPos = pos;
   }

   public void reset(String reason) {
      if (this.state != CrystalCombatState.State.IDLE) {
         this.logDebug("Reset: " + reason + " (from " + this.state + ")");
      }

      this.state = CrystalCombatState.State.IDLE;
      this.targetBasePos = null;
      this.targetFace = class_2350.field_11036;
      this.placedCrystalPos = null;
      this.targetCrystalEntity = null;
      this.isObsidianPhase = false;
      this.reactionClock = 0;
      this.switchClock = 0;
      this.waitEntityClock = 0;
      this.cooldownClock = 0;
      this.playerIntervened = false;
      this.placementHit = null;
      this.aim.reset();
      this.placeClock = this.breakClock = this.spawnBreakClock = 0;
      this.lastSelectedSlot = -1;
      this.lastTickTime = 0L;
      this.aimTicks = 0;
      this.selectedReadyAt = 0;
      this.lastRotationTick = -1;
      this.manualOverrideBase = null;
      this.releaseResources();
   }

   private void releaseResources() {
      if (CombatSequenceLease.SHARED.release(this) || this.mc == null || this.mc.field_1724 == null) {
         ActionArbiter var1 = ActionArbiter.get();
         var1.release(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.CRYSTAL_COMBAT);
         var1.release(ActionArbiter.Resource.CAMERA, ActionArbiter.Owner.CRYSTAL_COMBAT);
         var1.release(ActionArbiter.Resource.USE_KEY, ActionArbiter.Owner.CRYSTAL_COMBAT);
         var1.release(ActionArbiter.Resource.ATTACK_KEY, ActionArbiter.Owner.CRYSTAL_COMBAT);
      }
   }

   public void transitionTo(CrystalCombatState.State newState, String reason) {
      if (this.state != newState) {
         this.logDebug("Transition: " + this.state + " -> " + newState + " [" + reason + "]");
         this.state = newState;
         if (newState == CrystalCombatState.State.COOLDOWN) {
            this.releaseResources();
         }

         if (newState != CrystalCombatState.State.AIM) {
            this.aim.reset();
            this.aimTicks = 0;
            if (CombatSequenceLease.SHARED.owns(this)) {
               ActionArbiter.get().release(ActionArbiter.Resource.CAMERA, ActionArbiter.Owner.CRYSTAL_COMBAT);
            }
         }
      }
   }

   public void tick(boolean activeKeyHeld, boolean allowObsidianPlacement, CrystalCombatState.Mode preferredMode) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null && this.mc.field_1761 != null) {
         if (this.attackWorld != this.mc.field_1687) {
            this.lastAttackedId = -1;
            this.attackWorld = this.mc.field_1687;
         }

         if (this.mc.field_1755 == null && !this.mc.field_1724.method_6115()) {
            if (!this.followAim) {
               class_3965 var4 = this.lookHit(this.maxPlaceReach);
               this.updateCurrentAim(var4.method_17783() == class_240.field_1332 ? var4.method_17777() : null, this.findDirectCrystalTarget());
            }

            this.detectPlayerIntervention();
            long var8 = System.currentTimeMillis();
            if (this.lastTickTime > 0L && var8 - this.lastTickTime > 250L) {
               this.logDebug("Lag spike detected (" + (var8 - this.lastTickTime) + "ms). Suppressing bursts.");
               if (this.state == CrystalCombatState.State.WAIT_ENTITY || this.state == CrystalCombatState.State.AIM) {
                  this.transitionTo(
                     CrystalCombatState.State.COOLDOWN,
                     "Lag burst suppression"
                  );
                  this.cooldownClock = Math.max(this.cooldownClock, 2);
               }
            }

            this.lastTickTime = var8;
            if (!activeKeyHeld) {
               this.manualOverrideBase = null;
               if (this.state != CrystalCombatState.State.IDLE && this.state != CrystalCombatState.State.COOLDOWN) {
                  this.reset("Key released");
               } else if (this.state == CrystalCombatState.State.COOLDOWN && this.cooldownClock <= 0) {
                  this.reset(
                     "Key released during cooldown"
                  );
               }

               if (this.state == CrystalCombatState.State.COOLDOWN && this.cooldownClock > 0) {
                  this.cooldownClock--;
               }
            } else if (this.playerIntervened) {
               this.reset("Manual input changed");
               class_3965 var10 = this.lookHit(this.maxPlaceReach);
               this.manualOverrideBase = var10.method_17783() == class_240.field_1332 ? var10.method_17777() : null;
            } else {
               if (this.manualOverrideBase != null) {
                  class_3965 var6 = this.lookHit(this.maxPlaceReach);
                  if (var6.method_17783() == class_240.field_1332 && var6.method_17777().equals(this.manualOverrideBase)) {
                     return;
                  }

                  this.manualOverrideBase = null;
               }

               if ((CombatSequenceLease.SHARED.owns(this) || this.isBusy())
                  && !CombatSequenceLease.SHARED.acquire(this, this.mc.field_1687, this.mc.field_1724.field_6012)) {
                  this.reset(
                     "Another combat sequence owns control"
                  );
               } else {
                  if (this.autoSelectCrystal && (this.state == CrystalCombatState.State.IDLE || this.state == CrystalCombatState.State.ACQUIRE)) {
                     this.prepareCrystalSelection();
                  }

                  if (this.state == CrystalCombatState.State.ACQUIRE && CombatSequenceLease.SHARED.owns(this) && !this.hasUsableTarget(allowObsidianPlacement)) {
                     this.releaseResources();
                  }

                  if (this.placeClock > 0) {
                     this.placeClock--;
                  }

                  if (this.breakClock > 0) {
                     this.breakClock--;
                  }

                  if (this.spawnBreakClock > 0) {
                     this.spawnBreakClock--;
                  }

                  this.actionSent = false;

                  for (int var9 = 0; var9 < 8 && !this.actionSent; var9++) {
                     CrystalCombatState.State var7 = this.state;
                     switch (this.state) {
                        case IDLE:
                           this.transitionTo(CrystalCombatState.State.ACQUIRE, "Key held");
                           this.reactionClock = this.humanize ? Math.max(0, this.reactionDelay) : 0;
                           break;
                        case ACQUIRE:
                           this.handleAcquire(allowObsidianPlacement, preferredMode);
                           break;
                        case AIM:
                           this.handleAim();
                           break;
                        case SWITCH:
                           this.handleSwitch();
                           break;
                        case PLACE:
                           this.handlePlace();
                           break;
                        case WAIT_ENTITY:
                           this.handleWaitEntity();
                           break;
                        case BREAK:
                           this.handleBreak();
                           break;
                        case COOLDOWN:
                           this.handleCooldown();
                     }

                     if (this.state == var7 || this.state == CrystalCombatState.State.COOLDOWN || this.state == CrystalCombatState.State.IDLE) {
                        break;
                     }
                  }
               }
            }
         } else {
            this.reset("Screen open");
         }
      } else {
         this.reset("World or player null");
      }
   }

   private void handleAcquire(boolean allowObsidianPlacement, CrystalCombatState.Mode preferredMode) {
      class_746 var3 = this.mc.field_1724;
      if (var3 != null) {
         if (this.reactionClock > 0) {
            this.reactionClock--;
         } else {
            class_1297 var4 = this.findDirectCrystalTarget();
            if (var4 != null) {
               if (this.acquireSequence()) {
                  this.targetCrystalEntity = var4;
                  this.isObsidianPhase = false;
                  this.targetBasePos = null;
                  this.placedCrystalPos = null;
                  this.transitionTo(
                     CrystalCombatState.State.BREAK,
                     "Found direct crystal entity to break"
                  );
               }
            } else {
               class_3965 var5 = this.lookHit(this.maxPlaceReach);
               if (var5 instanceof class_3965 var6 && var5.method_17783() == class_240.field_1332) {
                  class_2338 var7 = var6.method_17777();
                  if (var3.method_33571().method_1025(class_243.method_24953(var7)) > this.maxPlaceReach * this.maxPlaceReach) {
                     return;
                  }

                  boolean var8 = this.mc.field_1687.method_8320(var7).method_27852(class_2246.field_10540)
                     || this.mc.field_1687.method_8320(var7).method_27852(class_2246.field_9987);
                  if (var8) {
                     class_1297 var9 = this.findCrystalOnBase(var7);
                     if (var9 != null) {
                        if (this.autoBreak && var9.method_5628() != this.lastAttackedId) {
                           if (!this.acquireSequence()) {
                              return;
                           }

                           this.targetBasePos = var7;
                           this.placedCrystalPos = var7.method_10084();
                           this.targetCrystalEntity = var9;
                           this.transitionTo(
                              CrystalCombatState.State.BREAK,
                              "Crystal on aimed base"
                           );
                           return;
                        }

                        return;
                     }

                     if (this.isValidCrystalPlacementSpot(var7)) {
                        if (!this.acquireSequence()) {
                           return;
                        }

                        this.placementHit = var6;
                        this.targetBasePos = var7;
                        this.targetFace = var6.method_17780();
                        this.placedCrystalPos = var7.method_10084();
                        this.isObsidianPhase = false;
                        this.transitionTo(
                           this.humanize ? CrystalCombatState.State.AIM : CrystalCombatState.State.SWITCH,
                           "Acquired crystal base"
                        );
                     }
                  } else if (allowObsidianPlacement || preferredMode == CrystalCombatState.Mode.OBSIDIAN_FIRST) {
                     class_2338 var10 = this.mc.field_1687.method_8320(var7).method_45474() ? var7 : var7.method_10093(var6.method_17780());
                     if (this.canPlaceObsidianAt(var10)) {
                        if (!this.acquireSequence()) {
                           return;
                        }

                        this.placementHit = var6;
                        this.targetBasePos = var7;
                        this.targetFace = var6.method_17780();
                        this.isObsidianPhase = true;
                        this.transitionTo(
                           this.humanize ? CrystalCombatState.State.AIM : CrystalCombatState.State.SWITCH,
                           "Acquired obsidian placement target"
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private void handleAim() {
      class_746 var1 = this.mc.field_1724;
      if (var1 != null && this.mc.field_1687 != null) {
         if (this.playerIntervened) {
            this.logDebug(
               "Player manual aim intervention detected; pausing assist."
            );
            this.transitionTo(
               CrystalCombatState.State.COOLDOWN,
               "Player intervened"
            );
            this.cooldownClock = 2;
            this.playerIntervened = false;
         } else if (this.targetCrystalEntity != null) {
            this.transitionTo(
               CrystalCombatState.State.BREAK,
               "Keep base aim while breaking"
            );
         } else if (this.currentPlacementHit() != null) {
            this.aim.reset();
            this.transitionTo(
               CrystalCombatState.State.SWITCH,
               "Placement face already under crosshair"
            );
         } else if (!this.followAim) {
            this.discardTarget();
         } else {
            if (this.targetBasePos == null
               || this.placementHit == null
               || ++this.aimTicks > 30
               || this.mc.field_1724.method_33571().method_1025(this.placementHit.method_17784()) > this.maxPlaceReach * this.maxPlaceReach) {
               this.reset(
                  "Placement target no longer reachable"
               );
            }
         }
      }
   }

   public void onRender(float frameSeconds) {
      if (this.followAim
         && this.state == CrystalCombatState.State.AIM
         && this.targetCrystalEntity == null
         && this.placementHit != null
         && this.mc.field_1724 != null
         && this.mc.field_1687 != null
         && this.mc.field_1755 == null
         && CombatSequenceLease.SHARED.owns(this)) {
         this.detectPlayerIntervention();
         if (this.playerIntervened) {
            byte[] var10001 = new byte[19];
            var10001[0] = (byte)(88);
            var10001[1] = (byte)(-5);
            var10001[2] = (byte)(114);
            var10001[3] = (byte)(-85);
            var10001[4] = (byte)(107);
            var10001[5] = (byte)(40);
            var10001[6] = (byte)(76);
            var10001[7] = (byte)(-126);
            var10001[8] = (byte)(28);
            var10001[9] = (byte)(-49);
            var10001[10] = (byte)(-127);
            var10001[11] = (byte)(-124);
            var10001[12] = (byte)(51);
            var10001[13] = (byte)(-68);
            var10001[14] = (byte)(45);
            var10001[15] = (byte)(-9);
            var10001[16] = (byte)(53);
            var10001[17] = (byte)(63);
            var10001[18] = (byte)(-72);
            this.reset($$v_s(var10001, -3931753854832226256L));
         } else if (ActionArbiter.get().tryAcquire(ActionArbiter.Resource.CAMERA, ActionArbiter.Owner.CRYSTAL_COMBAT)) {
            class_746 var2 = this.mc.field_1724;
            class_243 var3 = this.placementHit.method_17784();
            class_243 var4 = var2.method_33571();
            double var5 = var3.field_1352 - var4.field_1352;
            double var7 = var3.field_1351 - var4.field_1351;
            double var9 = var3.field_1350 - var4.field_1350;
            double var11 = Math.sqrt(var5 * var5 + var9 * var9);
            float var13 = (float)class_3532.method_15338(Math.toDegrees(Math.atan2(var9, var5)) - 90.0);
            float var14 = (float)class_3532.method_15350(-Math.toDegrees(Math.atan2(var7, var11)), -90.0, 90.0);
            CombatAim.Angles var15 = this.aim
               .step(
                  var2.method_36454(),
                  var2.method_36455(),
                  var13,
                  var14,
                  this.rotationSpeed,
                  ((Double)this.mc.field_1690.method_42495().method_41753()).floatValue(),
                  frameSeconds,
                  this.humanize
               );
            var2.method_36456(var15.yaw());
            var2.method_36457(var15.pitch());
            this.lastRotationTick = var2.field_6012;
            this.lastPlayerYaw = var15.yaw();
            this.lastPlayerPitch = var15.pitch();
         }
      }
   }

   private void handleSwitch() {
      class_746 var1 = this.mc.field_1724;
      if (var1 != null) {
         class_1792 var2 = this.isObsidianPhase ? class_1802.field_8281 : class_1802.field_8301;
         boolean var3 = var1.method_6047().method_31574(var2);
         boolean var4 = var1.method_6079().method_31574(var2);
         if (!var3 && !var4) {
            if (!ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.CRYSTAL_COMBAT)) {
               this.logDebug(
                  "Hotbar slot claimed by higher priority owner."
               );
            } else {
               int var5 = InventoryUtil.findInHotbar(var2);
               if (var5 < 0) {
                  this.logDebug("Missing item in hotbar: " + var2.method_63680().getString());
                  this.transitionTo(
                     CrystalCombatState.State.COOLDOWN,
                     "No required item in hotbar"
                  );
                  this.cooldownClock = 5;
               } else {
                  InventoryUtil.swap(var5);
                  this.lastSelectedSlot = var5;
                  if (this.switchDelay > 0) {
                     this.switchClock = this.switchDelay;
                     this.transitionTo(
                        CrystalCombatState.State.PLACE,
                        "Switched item; waiting switch delay"
                     );
                  } else {
                     this.transitionTo(
                        CrystalCombatState.State.PLACE,
                        "Switched item (instant)"
                     );
                  }
               }
            }
         } else {
            this.transitionTo(
               CrystalCombatState.State.PLACE,
               "Required item already in hand"
            );
         }
      }
   }

   private void handlePlace() {
      class_746 var1 = this.mc.field_1724;
      if (var1 != null && this.mc.field_1687 != null && this.mc.field_1761 != null) {
         if (var1.field_6012 >= this.selectedReadyAt && var1.field_6012 > this.lastRotationTick) {
            if (this.switchClock > 0) {
               this.switchClock--;
            } else if (this.placeClock <= 0) {
               if (this.targetBasePos == null) {
                  this.transitionTo(
                     CrystalCombatState.State.ACQUIRE,
                     "Target base pos lost"
                  );
               } else if (var1.method_33571().method_1025(class_243.method_24953(this.targetBasePos)) > this.maxPlaceReach * this.maxPlaceReach) {
                  this.transitionTo(
                     CrystalCombatState.State.COOLDOWN,
                     "Target base out of reach"
                  );
                  this.cooldownClock = 2;
               } else if (this.isObsidianPhase) {
                  int var6 = InventoryUtil.findInHotbar(class_1802.field_8281);
                  if (var6 < 0 && !var1.method_24518(class_1802.field_8281)) {
                     this.transitionTo(
                        CrystalCombatState.State.COOLDOWN,
                        "Out of obsidian"
                     );
                     this.cooldownClock = 5;
                  } else {
                     class_3965 var7 = this.currentPlacementHit();
                     if (var7 == null) {
                        this.transitionTo(
                           CrystalCombatState.State.AIM,
                           "Align placement face"
                        );
                     } else if (this.claimInteraction(false)) {
                        class_1268 var8 = var1.method_6047().method_31574(class_1802.field_8281) ? class_1268.field_5808 : class_1268.field_5810;
                        class_1269 var5 = this.mc.field_1761.method_2896(var1, var8, var7);
                        this.actionSent = true;
                        if (var5.method_23665()) {
                           var1.method_6104(var8);
                           this.isObsidianPhase = false;
                           this.targetBasePos = this.mc.field_1687.method_8320(var7.method_17777()).method_27852(class_2246.field_10540)
                              ? var7.method_17777()
                              : var7.method_17777().method_10093(var7.method_17780());
                           this.placedCrystalPos = this.targetBasePos.method_10084();
                           this.placementHit = new class_3965(
                              class_243.method_24953(this.targetBasePos).method_1031(0.0, 0.5, 0.0), class_2350.field_11036, this.targetBasePos, false
                           );
                           this.transitionTo(
                              CrystalCombatState.State.SWITCH,
                              "Obsidian placed; moving to crystal switch"
                           );
                           this.switchClock = Math.max(0, this.switchDelay);
                        } else {
                           this.logDebug(
                              "Obsidian placement rejected by client/server."
                           );
                           this.transitionTo(
                              CrystalCombatState.State.COOLDOWN,
                              "Obsidian placement rejected"
                           );
                           this.cooldownClock = 2;
                        }
                     }
                  }
               } else {
                  class_1268 var2 = var1.method_6047().method_31574(class_1802.field_8301)
                     ? class_1268.field_5808
                     : (var1.method_6079().method_31574(class_1802.field_8301) ? class_1268.field_5810 : null);
                  if (var2 == null) {
                     this.transitionTo(
                        CrystalCombatState.State.SWITCH,
                        "Need crystal in hand"
                     );
                  } else if (!this.isValidCrystalPlacementSpot(this.targetBasePos)) {
                     this.transitionTo(
                        CrystalCombatState.State.COOLDOWN,
                        "Spot obstructed or invalid"
                     );
                     this.cooldownClock = 1;
                  } else {
                     class_3965 var3 = this.currentPlacementHit();
                     if (var3 == null) {
                        this.transitionTo(
                           CrystalCombatState.State.AIM,
                           "Align crystal base"
                        );
                     } else if (this.claimInteraction(false)) {
                        class_1269 var4 = this.mc.field_1761.method_2896(var1, var2, var3);
                        this.actionSent = true;
                        if (var4.method_23665()) {
                           var1.method_6104(var2);
                           this.placedCrystalPos = this.targetBasePos.method_10084();
                           this.waitEntityClock = Math.max(2, this.waitEntityTimeout);
                           this.placeClock = Math.max(1, this.placeDelay);
                           this.spawnBreakClock = Math.max(0, this.autoBreakDelay);
                           this.transitionTo(
                              CrystalCombatState.State.WAIT_ENTITY,
                              "Crystal placed; waiting for entity spawn"
                           );
                        } else {
                           this.logDebug(
                              "Crystal placement rejected by client/server."
                           );
                           this.transitionTo(
                              CrystalCombatState.State.COOLDOWN,
                              "Crystal placement rejected"
                           );
                           this.cooldownClock = 2;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void handleWaitEntity() {
      if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
         this.reset(
            "World null during wait"
         );
      } else if (this.placedCrystalPos == null) {
         this.transitionTo(
            CrystalCombatState.State.COOLDOWN,
            "No placed pos to wait for"
         );
         this.cooldownClock = 1;
      } else {
         class_1297 var1 = this.findCrystalOnBase(this.placedCrystalPos.method_10074());
         if (var1 == null) {
            this.waitEntityClock--;
            if (this.waitEntityClock <= 0) {
               this.logDebug("Wait entity timeout expired at " + this.placedCrystalPos + "; cancelling action without breaking.");
               this.placedCrystalPos = null;
               this.targetCrystalEntity = null;
               this.transitionTo(
                  CrystalCombatState.State.COOLDOWN,
                  "Entity wait timed out"
               );
               this.cooldownClock = Math.max(1, this.placeDelay);
            }
         } else if (!this.autoBreak) {
            this.transitionTo(
               CrystalCombatState.State.COOLDOWN,
               "AutoBreak disabled"
            );
         } else {
            this.targetCrystalEntity = var1;
            this.transitionTo(CrystalCombatState.State.BREAK, "Confirmed crystal entity spawned: " + this.targetCrystalEntity.method_5628());
         }
      }
   }

   private void handleBreak() {
      class_746 var1 = this.mc.field_1724;
      if (var1 != null && this.mc.field_1687 != null && this.mc.field_1761 != null) {
         if (this.breakClock <= 0 && this.spawnBreakClock <= 0) {
            class_1297 var2 = this.targetCrystalEntity;
            if (var2 != null && !var2.method_31481() && var2.method_5805()) {
               class_243 var3 = CombatGeometry.closestPoint(var2.method_5829(), var1.method_33571());
               double var4 = var1.method_33571().method_1025(var3);
               if (var4 > this.maxBreakReach * this.maxBreakReach) {
                  this.transitionTo(
                     CrystalCombatState.State.COOLDOWN,
                     "Crystal out of reach (" + String.format("%.2f", Math.sqrt(var4)) + "m)"
                  );
                  this.cooldownClock = 1;
               } else if (!this.canBreakFromCurrentAim(var2, var3) || var2.method_5628() == this.lastAttackedId) {
                  this.transitionTo(
                     CrystalCombatState.State.COOLDOWN,
                     "Crystal no longer on aimed base or visible"
                  );
               } else if (this.claimInteraction(true)) {
                  this.mc.field_1761.method_2918(var1, var2);
                  var1.method_6104(class_1268.field_5808);
                  this.actionSent = true;
                  this.lastAttackedId = var2.method_5628();
                  this.attacksSent++;
                  this.breakClock = Math.max(1, this.breakDelay);
                  this.targetCrystalEntity = null;
                  this.placedCrystalPos = null;
                  this.targetBasePos = null;
                  this.transitionTo(
                     CrystalCombatState.State.COOLDOWN, "Broke crystal"
                  );
                  this.cooldownClock = 0;
               }
            } else {
               this.transitionTo(
                  CrystalCombatState.State.COOLDOWN,
                  "Crystal entity gone or dead"
               );
               this.cooldownClock = 1;
            }
         }
      }
   }

   private void handleCooldown() {
      if (this.cooldownClock > 0) {
         this.cooldownClock--;
      } else {
         byte[] var10002 = new byte[38];
         var10002[0] = (byte)(86);
         var10002[1] = (byte)(-11);
         var10002[2] = (byte)(115);
         var10002[3] = (byte)(-78);
         var10002[4] = (byte)(110);
         var10002[5] = (byte)(43);
         var10002[6] = (byte)(27);
         var10002[7] = (byte)(-113);
         var10002[8] = (byte)(93);
         var10002[9] = (byte)(-57);
         var10002[10] = (byte)(-120);
         var10002[11] = (byte)(-105);
         var10002[12] = (byte)(34);
         var10002[13] = (byte)(-17);
         var10002[14] = (byte)(33);
         var10002[15] = (byte)(-3);
         var10002[16] = (byte)(126);
         var10002[17] = (byte)(106);
         var10002[18] = (byte)(-66);
         var10002[19] = (byte)(-53);
         var10002[20] = (byte)(-37);
         var10002[21] = (byte)(-48);
         var10002[22] = (byte)(69);
         var10002[23] = (byte)(-111);
         var10002[24] = (byte)(-53);
         var10002[25] = (byte)(125);
         var10002[26] = (byte)(-26);
         var10002[27] = (byte)(102);
         var10002[28] = (byte)(-20);
         var10002[29] = (byte)(-87);
         var10002[30] = (byte)(108);
         var10002[31] = (byte)(29);
         var10002[32] = (byte)(-43);
         var10002[33] = (byte)(89);
         var10002[34] = (byte)(5);
         var10002[35] = (byte)(29);
         var10002[36] = (byte)(-122);
         var10002[37] = (byte)(-127);
         this.transitionTo(CrystalCombatState.State.ACQUIRE, $$v_s(var10002, -3931753854832226256L));
      }
   }

   private class_1297 findDirectCrystalTarget() {
      if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
         if (this.mc.field_1765 instanceof class_3966 var2) {
            class_1297 var3 = var2.method_17782();
            if (this.autoBreak
               && var3 instanceof class_1511
               && var3.method_5628() != this.lastAttackedId
               && var3.method_5805()
               && !var3.method_31481()
               && this.mc.field_1724.method_33571().method_1025(CombatGeometry.closestPoint(var3.method_5829(), this.mc.field_1724.method_33571()))
                  <= this.maxBreakReach * this.maxBreakReach) {
               return var3;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean isValidCrystalPlacementSpot(class_2338 basePos) {
      if (this.mc.field_1687 == null) {
         return false;
      }

      if (!this.mc.field_1687.method_8320(basePos).method_27852(class_2246.field_10540)
         && !this.mc.field_1687.method_8320(basePos).method_27852(class_2246.field_9987)) {
         return false;
      }

      class_2338 var2 = basePos.method_10084();
      if (!this.mc.field_1687.method_8320(var2).method_26215()) {
         return false;
      }

      class_238 var3 = new class_238(
         var2.method_10263(),
         var2.method_10264(),
         var2.method_10260(),
         var2.method_10263() + 1,
         var2.method_10264() + (2),
         var2.method_10260() + 1
      );
      List var4 = this.mc.field_1687.method_8333(null, var3, e -> e.method_5805() && !e.method_31481());
      return var4.isEmpty();
   }

   private boolean canPlaceObsidianAt(class_2338 pos) {
      return this.mc.field_1687 == null
         ? false
         : this.mc.field_1687.method_8320(pos).method_45474()
            && this.mc.field_1687.method_8333(null, new class_238(pos), e -> e.method_5805() && !e.method_31481()).isEmpty();
   }

   private boolean acquireSequence() {
      return CombatSequenceLease.SHARED.acquire(this, this.mc.field_1687, this.mc.field_1724.field_6012);
   }

   void updateCurrentAim(class_2338 aimedBase, class_1297 directCrystal) {
      if (!this.followAim && this.isBusy()) {
         if (directCrystal == null || directCrystal != this.targetCrystalEntity) {
            class_2338 var3 = this.targetBasePos != null ? this.targetBasePos : (this.placedCrystalPos != null ? this.placedCrystalPos.method_10074() : null);
            if (var3 == null || !var3.equals(aimedBase) && (directCrystal == null || !CombatGeometry.crystalOnBase(directCrystal.method_73189(), var3))) {
               if (this.targetCrystalEntity == null || aimedBase == null || !CombatGeometry.crystalOnBase(this.targetCrystalEntity.method_73189(), aimedBase)) {
                  this.discardTarget();
               }
            }
         }
      }
   }

   private void discardTarget() {
      this.targetBasePos = this.placedCrystalPos = null;
      this.targetCrystalEntity = null;
      this.placementHit = null;
      this.isObsidianPhase = false;
      this.waitEntityClock = 0;
      this.aim.reset();
      this.releaseResources();
      byte[] var10002 = new byte[34];
      var10002[0] = (byte)(84);
      var10002[1] = (byte)(-13);
      var10002[2] = (byte)(113);
      var10002[3] = (byte)(-2);
      var10002[4] = (byte)(103);
      var10002[5] = (byte)(43);
      var10002[6] = (byte)(26);
      var10002[7] = (byte)(-124);
      var10002[8] = (byte)(25);
      var10002[9] = (byte)(-103);
      var10002[10] = (byte)(-60);
      var10002[11] = (byte)(-110);
      var10002[12] = (byte)(59);
      var10002[13] = (byte)(-17);
      var10002[14] = (byte)(39);
      var10002[15] = (byte)(-8);
      var10002[16] = (byte)(55);
      var10002[17] = (byte)(46);
      var10002[18] = (byte)(-20);
      var10002[19] = (byte)(-34);
      var10002[20] = (byte)(-56);
      var10002[21] = (byte)(-47);
      var10002[22] = (byte)(74);
      var10002[23] = (byte)(-40);
      var10002[24] = (byte)(-62);
      var10002[25] = (byte)(103);
      var10002[26] = (byte)(-25);
      var10002[27] = (byte)(102);
      var10002[28] = (byte)(-10);
      var10002[29] = (byte)(-83);
      var10002[30] = (byte)(102);
      var10002[31] = (byte)(14);
      var10002[32] = (byte)(-112);
      var10002[33] = (byte)(78);
      this.transitionTo(CrystalCombatState.State.ACQUIRE, $$v_s(var10002, -3931753854832226256L));
      this.reactionClock = this.humanize ? Math.max(0, this.reactionDelay) : 0;
   }

   private void prepareCrystalSelection() {
      if (!this.mc.field_1724.method_24518(class_1802.field_8301)) {
         class_3965 var1 = this.lookHit(this.maxPlaceReach);
         if (var1.method_17783() == class_240.field_1332) {
            class_2338 var2 = var1.method_17777();
            if (this.mc.field_1687.method_8320(var2).method_27852(class_2246.field_10540)
               || this.mc.field_1687.method_8320(var2).method_27852(class_2246.field_9987)) {
               if (this.isValidCrystalPlacementSpot(var2) || this.findCrystalOnBase(var2) != null) {
                  int var3 = InventoryUtil.findInHotbar(class_1802.field_8301);
                  if (var3 >= 0 && this.acquireSequence()) {
                     if (!ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.CRYSTAL_COMBAT)) {
                        this.releaseResources();
                     } else {
                        InventoryUtil.swap(var3);
                        this.lastSelectedSlot = var3;
                        this.selectedReadyAt = this.mc.field_1724.field_6012 + Math.max(0, this.switchDelay);
                     }
                  }
               }
            }
         }
      }
   }

   public boolean hasUsableTarget(boolean allowObsidian) {
      if (this.mc.field_1724 == null || this.mc.field_1687 == null) {
         return false;
      }

      if (this.findDirectCrystalTarget() != null) {
         return true;
      }

      class_3965 var2 = this.lookHit(this.maxPlaceReach);
      if (var2.method_17783() != class_240.field_1332) {
         return false;
      }

      class_2338 var3 = var2.method_17777();
      return !this.mc.field_1687.method_8320(var3).method_27852(class_2246.field_10540)
            && !this.mc.field_1687.method_8320(var3).method_27852(class_2246.field_9987)
         ? allowObsidian && this.canPlaceObsidianAt(this.mc.field_1687.method_8320(var3).method_45474() ? var3 : var3.method_10093(var2.method_17780()))
         : this.isValidCrystalPlacementSpot(var3) || this.findCrystalOnBase(var3) != null;
   }

   public boolean tryReserveTarget(boolean allowObsidian) {
      return this.hasUsableTarget(allowObsidian) && this.acquireSequence();
   }

   private class_3965 currentPlacementHit() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null && this.targetBasePos != null) {
         class_3965 var1 = this.lookHit(this.maxPlaceReach);
         if (var1.method_17783() != class_240.field_1332 || !var1.method_17777().equals(this.targetBasePos)) {
            return null;
         } else {
            return this.isObsidianPhase && var1.method_17780() != this.targetFace ? null : var1;
         }
      } else {
         return null;
      }
   }

   private class_3965 lookHit(double reach) {
      class_243 var3 = this.mc.field_1724.method_33571();
      return this.mc
         .field_1687
         .method_17742(
            new class_3959(
               var3,
               var3.method_1019(this.mc.field_1724.method_5828(1.0F).method_1021(reach)),
               class_3960.field_17559,
               class_242.field_1348,
               this.mc.field_1724
            )
         );
   }

   private class_1297 findCrystalOnBase(class_2338 base) {
      List var2 = this.mc
         .field_1687
         .method_8333(
            null,
            new class_238(base.method_10084()).method_1014(1.0),
            e -> e instanceof class_1511 && e.method_5805() && !e.method_31481() && CombatGeometry.crystalOnBase(e.method_73189(), base)
         );
      return var2.isEmpty() ? null : (class_1297)var2.get(0);
   }

   private boolean canBreakFromCurrentAim(class_1297 entity, class_243 attackPoint) {
      class_243 var3 = this.mc.field_1724.method_33571();
      class_3965 var4 = this.mc.field_1687.method_17742(new class_3959(var3, attackPoint, class_3960.field_17558, class_242.field_1348, this.mc.field_1724));
      if (var4.method_17783() != class_240.field_1333 && var4.method_17784().method_1025(var3) + 1.0E-6 < attackPoint.method_1025(var3)) {
         return false;
      }

      class_3965 var5 = this.lookHit(this.maxPlaceReach);
      boolean var6 = var5.method_17783() == class_240.field_1332
         && (
            this.mc.field_1687.method_8320(var5.method_17777()).method_27852(class_2246.field_10540)
               || this.mc.field_1687.method_8320(var5.method_17777()).method_27852(class_2246.field_9987)
         );
      return CombatGeometry.aimMatchesCrystal(
         entity.method_73189(), entity.method_5829(), var3, this.mc.field_1724.method_5828(1.0F), this.maxBreakReach, var6 ? var5.method_17777() : null
      );
   }

   private boolean claimInteraction(boolean attack) {
      return ActionArbiter.get().tryAcquire(attack ? ActionArbiter.Resource.ATTACK_KEY : ActionArbiter.Resource.USE_KEY, ActionArbiter.Owner.CRYSTAL_COMBAT)
         && CombatActionGate.SHARED.tryClaim(this.mc.field_1687, this.mc.field_1724.field_6012);
   }

   private void detectPlayerIntervention() {
      if (this.mc.field_1724 != null) {
         int var1 = this.mc.field_1724.method_31548().method_67532();
         boolean var2 = this.lastSelectedSlot >= 0 && (this.isBusy() || CombatSequenceLease.SHARED.owns(this));
         if (var2 && var1 != this.lastSelectedSlot) {
            this.playerIntervened = true;
         }

         this.lastSelectedSlot = var1;
         float var3 = Math.abs(class_3532.method_15393(this.mc.field_1724.method_36454() - this.lastPlayerYaw));
         float var4 = Math.abs(this.mc.field_1724.method_36455() - this.lastPlayerPitch);
         if (this.followAim && var2 && (var3 > 15.0F || var4 > 10.0F)) {
            this.playerIntervened = true;
         }

         this.lastPlayerYaw = this.mc.field_1724.method_36454();
         this.lastPlayerPitch = this.mc.field_1724.method_36455();
      }
   }

   private void logDebug(String msg) {
      if (this.debug) {
         long var2 = System.currentTimeMillis();
         if (var2 - this.lastLogTime >= 50L) {
            this.lastLogTime = var2;
            System.out.println("[CrystalCombat] " + msg);
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

   public enum Mode {
      CRYSTAL_FIRST,
      OBSIDIAN_FIRST;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CrystalCombatState.Mode[] $values() {
         CrystalCombatState.Mode[] var10000 = new CrystalCombatState.Mode[2];
         var10000[0] = CRYSTAL_FIRST;
         var10000[1] = OBSIDIAN_FIRST;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[13];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(69);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(105);
         var10002[5] = (byte)(73);
         var10002[6] = (byte)(81);
         var10002[7] = (byte)(64);
         var10002[8] = (byte)(23);
         var10002[9] = (byte)(-21);
         var10002[10] = (byte)(-74);
         var10002[11] = (byte)(-91);
         var10002[12] = (byte)(49);
         CRYSTAL_FIRST = new CrystalCombatState.Mode();
         var10002 = new byte[14];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-40);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(121);
         var10002[5] = (byte)(65);
         var10002[6] = (byte)(92);
         var10002[7] = (byte)(81);
         var10002[8] = (byte)(14);
         var10002[9] = (byte)(-28);
         var10002[10] = (byte)(-83);
         var10002[11] = (byte)(-92);
         var10002[12] = (byte)(54);
         var10002[13] = (byte)(-124);
         OBSIDIAN_FIRST = new CrystalCombatState.Mode();
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

   public enum State {
      IDLE,
      ACQUIRE,
      AIM,
      SWITCH,
      PLACE,
      WAIT_ENTITY,
      BREAK,
      COOLDOWN;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CrystalCombatState.State[] $values() {
         CrystalCombatState.State[] var10000 = new CrystalCombatState.State[8];
         var10000[0] = IDLE;
         var10000[1] = ACQUIRE;
         var10000[2] = AIM;
         var10000[3] = SWITCH;
         var10000[4] = PLACE;
         var10000[5] = WAIT_ENTITY;
         var10000[6] = BREAK;
         var10000[7] = COOLDOWN;
         return var10000;
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
