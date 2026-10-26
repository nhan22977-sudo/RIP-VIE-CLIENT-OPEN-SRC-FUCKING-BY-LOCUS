package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.util.BlockUtil;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1303;
import net.minecraft.class_1542;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4969;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class AnchorCombatState {
   private final class_310 mc = class_310.method_1551();
   public boolean humanize = true;
   public boolean followingCamera = true;
   public boolean placeProtection = false;
   public AnchorCombatState.ProtectionMode protectionMode = AnchorCombatState.ProtectionMode.SMART;
   public AnchorCombatState.BypassMode bypassMode = AnchorCombatState.BypassMode.NORMAL;
   public boolean checkOffhandTotem = true;
   public int switchDelay = 1;
   public int placeDelay = 2;
   public int chargeDelay = 1;
   public int explodeDelay = 1;
   public int totemSlot = 1;
   public int maxRotateTicks = 20;
   public int targetCycles = 1;
   public double lookDownBlocks = 1.0;
   public double rotationSpeed = 8.0;
   public double maxPlaceReach = 4.5;
   public double fov = 90.0;
   public boolean autoExplode = true;
   public boolean autoFillTotem = true;
   public boolean debug = false;
   public boolean requireTotem = false;
   public boolean rapidChain = false;
   public boolean pipelinePreparation = false;
   public boolean requireServerConfirmation = false;
   private AnchorCombatState.State state = AnchorCombatState.State.IDLE;
   private int cyclesRemaining = 1;
   private int cooldownClock;
   private int explodeReadyAt;
   private int fillAttempts;
   private int lastTick = -1;
   private int lastSelectedSlot = -1;
   private int rotateStarted = -1;
   private int lastRotationTick = -1;
   private float lastYaw;
   private float lastPitch;
   private long lastTickTime;
   private class_2338 anchorPos;
   private class_2338 protectionPos;
   private class_3965 initialHit;
   private class_3965 replacementHit;
   private class_243 cameraTarget;
   private boolean actionSent;
   private final CombatAim aim = new CombatAim();
   private final CombatConfirmation confirmation = new CombatConfirmation();
   private final AnchorActionTiming actionTiming = new AnchorActionTiming();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AnchorCombatState.State getState() {
      return this.state;
   }

   public void setStateForTest(AnchorCombatState.State value) {
      this.state = value;
   }

   public class_2338 getAnchorPos() {
      return this.anchorPos;
   }

   public void setAnchorPosForTest(class_2338 value) {
      this.anchorPos = value;
   }

   public int getCooldownClock() {
      return this.cooldownClock;
   }

   public void setCooldownClockForTest(int value) {
      this.cooldownClock = value;
   }

   public int getCyclesRemaining() {
      return this.cyclesRemaining;
   }

   public void setCyclesRemainingForTest(int value) {
      this.cyclesRemaining = value;
   }

   public boolean isSuppressedUseKey() {
      return false;
   }

   public boolean isBusy() {
      return this.state != AnchorCombatState.State.IDLE && this.state != AnchorCombatState.State.ACQUIRE && this.state != AnchorCombatState.State.COOLDOWN;
   }

   public void reset(String reason) {
      this.releaseResources();
      this.state = AnchorCombatState.State.IDLE;
      this.anchorPos = this.protectionPos = null;
      this.initialHit = null;
      this.replacementHit = null;
      this.cameraTarget = null;
      this.aim.reset();
      this.cyclesRemaining = Math.max(1, this.targetCycles);
      this.cooldownClock = this.explodeReadyAt = this.fillAttempts = 0;
      this.actionTiming.reset();
      this.lastSelectedSlot = this.rotateStarted = this.lastTick = -1;
      this.lastRotationTick = -1;
      this.lastTickTime = 0L;
   }

   private void releaseResources() {
      if (CombatSequenceLease.SHARED.release(this) || this.mc == null || this.mc.field_1724 == null) {
         ActionArbiter.Resource[] var10000 = new ActionArbiter.Resource[3];
         var10000[0] = ActionArbiter.Resource.CAMERA;
         var10000[1] = ActionArbiter.Resource.HOTBAR_SLOT;
         var10000[2] = ActionArbiter.Resource.USE_KEY;

         for (ActionArbiter.Resource var4 : var10000) {
            ActionArbiter.get().release(var4, ActionArbiter.Owner.ANCHOR_COMBAT);
         }
      }
   }

   public void transitionTo(AnchorCombatState.State next, String reason) {
      if (this.state != next) {
         if (this.debug) {
            System.out.println("[AnchorCombat] " + this.state + " -> " + next + ": " + reason);
         }

         this.state = next;
         if (!this.pipelinePreparation
            || next == AnchorCombatState.State.COOLDOWN
            || next == AnchorCombatState.State.IDLE
            || next == AnchorCombatState.State.WAIT_EXPLOSION) {
            this.cameraTarget = null;
            this.aim.reset();
         }

         this.rotateStarted = -1;
         if (next == AnchorCombatState.State.COOLDOWN) {
            this.lastSelectedSlot = -1;
            this.releaseResources();
         }
      }
   }

   public void tick(boolean keyHeld, boolean allowPlace) {
      if (this.mc == null
         || this.mc.field_1724 == null
         || this.mc.field_1687 == null
         || this.mc.field_1761 == null
         || this.mc.field_1755 != null
         || this.mc.field_1724.method_6115()
         || !keyHeld) {
         this.reset("Inactive");
      } else if (this.lastTick != this.mc.field_1724.field_6012) {
         this.lastTick = this.mc.field_1724.field_6012;
         this.actionTiming.beginTick(this.mc.field_1724.field_6012);
         if (!this.isBusy() || CombatSequenceLease.SHARED.acquire(this, this.mc.field_1687, this.mc.field_1724.field_6012) && !this.manualInput()) {
            long var3 = System.nanoTime();
            if (this.isBusy() && this.lastTickTime != 0L && var3 - this.lastTickTime > 250000000L) {
               this.abort("Lag spike");
            }

            this.lastTickTime = var3;
            this.rememberInput();
            this.actionSent = false;

            for (int var5 = 0; var5 < 12; var5++) {
               AnchorCombatState.State var6 = this.state;
               switch (this.state) {
                  case IDLE:
                     this.transitionTo(AnchorCombatState.State.ACQUIRE, "Ready");
                     break;
                  case ACQUIRE:
                     this.acquire(allowPlace);
                     break;
                  case PLACE_ANCHOR:
                     this.placeAnchor();
                     break;
                  case WAIT_ANCHOR:
                     this.waitFor(this.isAnchor(), AnchorCombatState.State.ROTATE_SAFE);
                     break;
                  case ROTATE_SAFE:
                     if (!this.isAnchor()) {
                        this.abort("Anchor missing");
                     } else if (this.pipelinePreparation) {
                        this.transitionTo(
                           this.charged() ? this.afterCharge() : AnchorCombatState.State.PLACE_GLOWSTONE,
                           "Prepare aim and item together"
                        );
                     } else {
                        class_3965 var9 = this.anchorHit();
                        if (var9 == null) {
                           this.abort("Anchor occluded");
                        } else if (this.align(var9, false)) {
                           this.transitionTo(
                              this.charged() ? this.afterCharge() : AnchorCombatState.State.PLACE_GLOWSTONE,
                              "Aim ready"
                           );
                        }
                     }
                     break;
                  case PLACE_GLOWSTONE:
                     this.charge();
                     break;
                  case WAIT_CHARGE:
                     this.waitFor(this.charged(), this.afterCharge());
                     break;
                  case PLACE_PROTECTION:
                     this.protect();
                     break;
                  case WAIT_PROTECTION:
                     if (this.protectionPresent()) {
                        this.transitionTo(
                           AnchorCombatState.State.SWITCH_TOTEM,
                           "Protection present"
                        );
                     } else {
                        CombatConfirmation.Result var8 = this.confirmation.poll(this.mc.field_1724.field_6012, this.protectionPresent());
                        if (var8 == CombatConfirmation.Result.READY) {
                           this.transitionTo(
                              AnchorCombatState.State.SWITCH_TOTEM,
                              "World change observed"
                           );
                        } else if (var8 == CombatConfirmation.Result.TIMED_OUT) {
                           if (this.protectionMode == AnchorCombatState.ProtectionMode.SMART && this.canBypassProtection()) {
                              this.transitionTo(
                                 AnchorCombatState.State.SWITCH_TOTEM,
                                 "Smart fallback: protection timeout, detonate safely"
                              );
                           } else {
                              this.abort(
                                 "World confirmation timed out"
                              );
                           }
                        }
                     }
                     break;
                  case SWITCH_TOTEM:
                     this.switchTotem();
                     break;
                  case EXPLODE:
                     this.explode();
                     break;
                  case WAIT_EXPLOSION:
                     CombatConfirmation.Result var7 = this.confirmation.poll(this.mc.field_1724.field_6012, !this.isAnchor());
                     if (var7 == CombatConfirmation.Result.READY) {
                        this.completeExplosion();
                     } else if (var7 == CombatConfirmation.Result.TIMED_OUT) {
                        this.abort(
                           "Explosion not observed"
                        );
                     }
                     break;
                  case COOLDOWN:
                     if (this.cooldownClock > 0) {
                        this.cooldownClock--;
                     } else {
                        this.transitionTo(
                           AnchorCombatState.State.ACQUIRE,
                           "Cooldown elapsed"
                        );
                     }
               }

               if (this.actionSent || this.state == var6 || this.state == AnchorCombatState.State.COOLDOWN || this.state == AnchorCombatState.State.IDLE) {
                  break;
               }
            }

            if (this.pipelinePreparation) {
               this.prepareNextAction();
            }
         } else {
            this.reset(
               "Sequence interrupted"
            );
         }
      }
   }

   private void prepareNextAction() {
      class_3965 var1 = null;
      int var2 = -1;
      boolean var3 = false;
      switch (this.state) {
         case WAIT_ANCHOR:
            if (!this.isAnchor()) {
               return;
            }

            var1 = this.anchorHit();
            if (!this.charged()) {
               var2 = InventoryUtil.findInHotbar(class_1802.field_8801);
            }
            break;
         case WAIT_CHARGE:
            if (!this.isAnchor()) {
               return;
            }

            if (this.placeProtection && this.protectionMode != AnchorCombatState.ProtectionMode.OFF) {
               if (this.protectionPos == null) {
                  this.protectionPos = this.findProtectionPosition();
               }

               if (this.protectionPos != null && !this.protectionPresent()) {
                  var1 = this.placementHit(this.protectionPos, null);
                  var3 = true;
               }
            } else {
               var1 = this.anchorHit();
               if (this.checkOffhandTotem && this.mc.field_1724.method_6079().method_31574(class_1802.field_8288)) {
                  int var5 = InventoryUtil.findInHotbar(class_1802.field_23141);
                  var2 = var5 >= 0 ? var5 : class_3532.method_15340(this.totemSlot - 1, 0, 8);
               } else {
                  var2 = this.isRapidFirstExplosion()
                     ? InventoryUtil.findInHotbar(class_1802.field_23141)
                     : class_3532.method_15340(this.totemSlot - 1, 0, 8);
               }
            }
            break;
         case WAIT_PROTECTION:
            if (!this.isAnchor()) {
               return;
            }

            var1 = this.anchorHit();
            if (this.checkOffhandTotem && this.mc.field_1724.method_6079().method_31574(class_1802.field_8288)) {
               int var4 = InventoryUtil.findInHotbar(class_1802.field_23141);
               var2 = var4 >= 0 ? var4 : class_3532.method_15340(this.totemSlot - 1, 0, 8);
            } else {
               var2 = class_3532.method_15340(this.totemSlot - 1, 0, 8);
            }
            break;
         default:
            return;
      }

      if (var2 >= 0) {
         this.selectSlot(var2);
      }

      if (var1 != null) {
         this.align(var1, var3);
      }
   }

   private void acquire(boolean allowPlace) {
      class_3965 var2 = this.currentLookHit();
      boolean var3 = var2.method_17783() == class_240.field_1332;
      boolean var4 = var3 && this.mc.field_1687.method_8320(var2.method_17777()).method_27852(class_2246.field_23152);
      class_2338 var5 = var4 ? var2.method_17777() : (var3 ? this.placementPosition(var2) : null);
      if (!var4) {
         class_2338 var6 = this.findNearbyExistingAnchor();
         if (var6 != null) {
            var4 = true;
            var5 = var6;
            var2 = this.anchorHitFor(var6);
         }
      }

      if (var5 != null) {
         if (var4 || allowPlace && this.canPlace(var5) && InventoryUtil.findInHotbar(class_1802.field_23141) >= 0) {
            if (this.isInFov(var5)) {
               if (CombatSequenceLease.SHARED.acquire(this, this.mc.field_1687, this.mc.field_1724.field_6012)) {
                  this.anchorPos = var5;
                  this.protectionPos = null;
                  this.initialHit = var2;
                  this.replacementHit = null;
                  this.cyclesRemaining = Math.max(1, this.targetCycles);
                  this.fillAttempts = 0;
                  this.rememberInput();
                  AnchorCombatState.State var10001 = var4 ? AnchorCombatState.State.ROTATE_SAFE : AnchorCombatState.State.PLACE_ANCHOR;
                  byte[] var10002 = new byte[15];
                  var10002[0] = (byte)(93);
                  var10002[1] = (byte)(-5);
                  var10002[2] = (byte)(110);
                  var10002[3] = (byte)(-71);
                  var10002[4] = (byte)(-128);
                  var10002[5] = (byte)(-53);
                  var10002[6] = (byte)(-80);
                  var10002[7] = (byte)(-115);
                  var10002[8] = (byte)(2);
                  var10002[9] = (byte)(-45);
                  var10002[10] = (byte)(-111);
                  var10002[11] = (byte)(-97);
                  var10002[12] = (byte)(-49);
                  var10002[13] = (byte)(2);
                  var10002[14] = (byte)(-36);
                  this.transitionTo(var10001, $$v_s(var10002, -4281971771685220308L));
               }
            }
         }
      }
   }

   private void placeAnchor() {
      if (!this.canPlace(this.anchorPos)) {
         this.abort(
            "Anchor position obstructed"
         );
      } else {
         class_3965 var1 = this.placementHit(this.anchorPos, this.initialHit);
         if (var1 == null) {
            this.abort(
               "Placement support missing or occluded"
            );
         } else {
            boolean var2 = this.select(class_1802.field_23141);
            if (this.state == AnchorCombatState.State.PLACE_ANCHOR) {
               boolean var3 = this.align(var1, true);
               if (var2 && var3) {
                  class_3965 var4 = this.followingCamera ? this.currentLookHit() : var1;
                  if (var4.method_17783() != class_240.field_1332) {
                     var4 = var1;
                  }

                  if (this.interact(var4)) {
                     this.confirmation.begin(this.mc.field_1724.field_6012, this.placeDelay, 12, this.requireServerConfirmation);
                     this.transitionTo(
                        AnchorCombatState.State.WAIT_ANCHOR,
                        "Await anchor"
                     );
                  }
               }
            }
         }
      }
   }

   private void charge() {
      if (!this.isAnchor()) {
         this.abort("Anchor missing");
      } else if (this.charged()) {
         this.transitionTo(
            this.afterCharge(), "Already charged"
         );
      } else {
         class_3965 var1 = this.anchorHit();
         if (var1 == null) {
            this.abort("Anchor occluded");
         } else {
            boolean var2 = this.select(class_1802.field_8801);
            if (this.state == AnchorCombatState.State.PLACE_GLOWSTONE) {
               boolean var3 = this.align(var1, false);
               if (var2 && var3) {
                  class_3965 var4 = this.followingCamera ? this.currentLookHit() : var1;
                  if (var4.method_17783() != class_240.field_1332) {
                     var4 = var1;
                  }

                  if (this.interact(var4)) {
                     this.confirmation.begin(this.mc.field_1724.field_6012, this.chargeDelay, 12, this.requireServerConfirmation);
                     this.transitionTo(
                        AnchorCombatState.State.WAIT_CHARGE,
                        "Await charge"
                     );
                  }
               }
            }
         }
      }
   }

   private AnchorCombatState.State afterCharge() {
      return this.placeProtection && this.protectionMode != AnchorCombatState.ProtectionMode.OFF
         ? AnchorCombatState.State.PLACE_PROTECTION
         : AnchorCombatState.State.SWITCH_TOTEM;
   }

   public void onServerBlockUpdate(class_2338 pos, class_2680 update) {
      boolean var3;
      switch (this.state) {
         case WAIT_ANCHOR:
            if (!pos.equals(this.anchorPos)) {
               return;
            }

            var3 = update.method_27852(class_2246.field_23152);
            break;
         case ROTATE_SAFE:
         case PLACE_GLOWSTONE:
         case PLACE_PROTECTION:
         case SWITCH_TOTEM:
         case EXPLODE:
         default:
            return;
         case WAIT_CHARGE:
            if (!pos.equals(this.anchorPos)) {
               return;
            }

            var3 = update.method_27852(class_2246.field_23152) && (Integer)update.method_11654(class_4969.field_23153) > 0;
            break;
         case WAIT_PROTECTION:
            if (!pos.equals(this.protectionPos)) {
               return;
            }

            var3 = update.method_27852(class_2246.field_10171);
            break;
         case WAIT_EXPLOSION:
            if (!pos.equals(this.anchorPos)) {
               return;
            }

            var3 = !update.method_27852(class_2246.field_23152);
      }

      this.confirmation.observeServerUpdate(var3);
   }

   private void waitFor(boolean observed, AnchorCombatState.State next) {
      CombatConfirmation.Result var3 = this.confirmation.poll(this.mc.field_1724.field_6012, observed);
      if (var3 == CombatConfirmation.Result.READY) {
         byte[] var10002 = new byte[21];
         var10002[0] = (byte)(94);
         var10002[1] = (byte)(-11);
         var10002[2] = (byte)(110);
         var10002[3] = (byte)(-78);
         var10002[4] = (byte)(-127);
         var10002[5] = (byte)(-97);
         var10002[6] = (byte)(-13);
         var10002[7] = (byte)(-124);
         var10002[8] = 0;
         var10002[9] = (byte)(-52);
         var10002[10] = (byte)(-125);
         var10002[11] = (byte)(-109);
         var10002[12] = (byte)(-99);
         var10002[13] = (byte)(8);
         var10002[14] = (byte)(-38);
         var10002[15] = (byte)(-25);
         var10002[16] = (byte)(60);
         var10002[17] = (byte)(56);
         var10002[18] = (byte)(-70);
         var10002[19] = (byte)(-53);
         var10002[20] = (byte)(49);
         this.transitionTo(next, $$v_s(var10002, -4281971771685220308L));
      } else if (var3 == CombatConfirmation.Result.TIMED_OUT) {
         byte[] var10001 = new byte[28];
         var10001[0] = (byte)(94);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(110);
         var10001[3] = (byte)(-78);
         var10001[4] = (byte)(-127);
         var10001[5] = (byte)(-97);
         var10001[6] = (byte)(-13);
         var10001[7] = (byte)(-125);
         var10001[8] = (byte)(15);
         var10001[9] = (byte)(-60);
         var10001[10] = (byte)(-115);
         var10001[11] = (byte)(-124);
         var10001[12] = (byte)(-48);
         var10001[13] = (byte)(6);
         var10001[14] = (byte)(-52);
         var10001[15] = (byte)(-3);
         var10001[16] = (byte)(54);
         var10001[17] = (byte)(36);
         var10001[18] = (byte)(-20);
         var10001[19] = (byte)(-38);
         var10001[20] = (byte)(60);
         var10001[21] = (byte)(34);
         var10001[22] = (byte)(-91);
         var10001[23] = (byte)(-40);
         var10001[24] = (byte)(-111);
         var10001[25] = (byte)(125);
         var10001[26] = (byte)(-31);
         var10001[27] = (byte)(50);
         this.abort($$v_s(var10001, -4281971771685220308L));
      }
   }

   private boolean canBypassProtection() {
      if (this.mc.field_1724 == null) {
         return false;
      }

      boolean var1 = this.mc.field_1724.method_6079().method_31574(class_1802.field_8288)
         || this.mc.field_1724.method_6047().method_31574(class_1802.field_8288)
         || InventoryUtil.findInHotbar(class_1802.field_8288) >= 0;
      return var1 || this.mc.field_1724.method_6032() >= 14.0F;
   }

   private void protect() {
      if (!this.charged()) {
         this.abort(
            "Charged anchor missing"
         );
      } else if (this.protectionMode != AnchorCombatState.ProtectionMode.OFF && this.placeProtection) {
         if (this.protectionPos == null) {
            this.protectionPos = this.findProtectionPosition();
         }

         if (this.protectionPos == null) {
            if (this.protectionMode == AnchorCombatState.ProtectionMode.SMART && this.canBypassProtection()) {
               this.transitionTo(
                  AnchorCombatState.State.SWITCH_TOTEM,
                  "Smart fallback: protection unviable, detonate safely"
               );
            } else {
               this.abort(
                  "No visible protection position"
               );
            }
         } else if (this.protectionPresent()) {
            this.transitionTo(
               AnchorCombatState.State.SWITCH_TOTEM,
               "Protection present"
            );
         } else {
            class_3965 var1 = this.placementHit(this.protectionPos, null);
            if (this.canPlace(this.protectionPos) && var1 != null) {
               boolean var2 = this.select(class_1802.field_8801);
               if (this.state == AnchorCombatState.State.PLACE_PROTECTION) {
                  boolean var3 = this.align(var1, true);
                  if (var2 && var3) {
                     class_3965 var4 = this.followingCamera ? this.currentLookHit() : var1;
                     if (var4.method_17783() != class_240.field_1332) {
                        var4 = var1;
                     }

                     if (this.interact(var4)) {
                        this.confirmation.begin(this.mc.field_1724.field_6012, this.placeDelay, 12, this.requireServerConfirmation);
                        this.transitionTo(
                           AnchorCombatState.State.WAIT_PROTECTION,
                           "Await protection"
                        );
                     }
                  }
               }
            } else if (this.protectionMode == AnchorCombatState.ProtectionMode.SMART && this.canBypassProtection()) {
               this.transitionTo(
                  AnchorCombatState.State.SWITCH_TOTEM,
                  "Smart fallback: protection obstructed, detonate safely"
               );
            } else {
               this.abort(
                  "Protection position obstructed"
               );
            }
         }
      } else {
         this.transitionTo(
            AnchorCombatState.State.SWITCH_TOTEM,
            "Protection skipped"
         );
      }
   }

   private void switchTotem() {
      if (this.isRapidFirstExplosion()) {
         if (this.select(class_1802.field_23141)) {
            this.explodeReadyAt = this.mc.field_1724.field_6012 + Math.max(0, this.explodeDelay);
            this.transitionTo(
               AnchorCombatState.State.EXPLODE,
               "Next anchor selected for rapid replacement"
            );
         }
      } else {
         boolean var1 = this.mc.field_1724.method_6079().method_31574(class_1802.field_8288);
         if (this.checkOffhandTotem && var1) {
            int var7 = BlockUtil.getAnchorCharges(this.anchorPos);
            boolean var9 = this.mc.field_1724.method_6047().method_31574(class_1802.field_8801);
            if (!AnchorDetonationSlot.glowstoneBlocksDetonation(var7, var9, false)) {
               this.explodeReadyAt = this.mc.field_1724.field_6012 + Math.max(0, this.explodeDelay);
               this.transitionTo(
                  AnchorCombatState.State.EXPLODE,
                  "Offhand totem protects player, detonating with held item"
               );
            } else {
               int var4 = InventoryUtil.findInHotbar(class_1802.field_23141);
               int var5 = class_3532.method_15340(this.totemSlot - 1, 0, 8);
               int var6 = var4 >= 0 ? var4 : var5;
               if (this.selectSlot(var6)) {
                  this.explodeReadyAt = this.mc.field_1724.field_6012 + Math.max(0, this.explodeDelay);
                  this.transitionTo(
                     AnchorCombatState.State.EXPLODE,
                     "Switched off glowstone for offhand-protected detonation"
                  );
               }
            }
         } else {
            int var2 = class_3532.method_15340(this.totemSlot - 1, 0, 8);
            if (this.selectSlot(var2)) {
               if (this.autoFillTotem && !var1 && this.fillAttempts < 2 && this.mc.field_1724.field_7512 == this.mc.field_1724.field_7498) {
                  int var3 = AnchorDetonationSlot.refillSource(var2, i -> this.mc.field_1724.method_31548().method_5438(i).method_31574(class_1802.field_8288));
                  if (var3 >= 0) {
                     if (ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.ANCHOR_COMBAT) && this.claimAction()) {
                        this.mc
                           .field_1761
                           .method_2906(
                              this.mc.field_1724.field_7498.field_7763,
                              AnchorDetonationSlot.inventoryScreenSlot(var3),
                              var2,
                              class_1713.field_7791,
                              this.mc.field_1724
                           );
                        this.actionSent = true;
                        this.fillAttempts++;
                        this.actionTiming.selected(this.mc.field_1724.field_6012, Math.max(2, this.switchDelay));
                        return;
                     }

                     return;
                  }
               }

               int var8 = AnchorDetonationSlot.choose(
                  var2,
                  this.requireTotem,
                  var1,
                  BlockUtil.getAnchorCharges(this.anchorPos),
                  i -> this.mc.field_1724.method_31548().method_5438(i).method_31574(class_1802.field_8288),
                  i -> this.mc.field_1724.method_31548().method_5438(i).method_31574(class_1802.field_8801)
               );
               if (var8 < 0) {
                  this.abort(
                     this.requireTotem
                        ? "Required totem unavailable"
                        : "No detonation slot available"
                  );
               } else if (this.selectSlot(var8)) {
                  if (!this.autoExplode) {
                     this.finish();
                  } else {
                     this.explodeReadyAt = this.mc.field_1724.field_6012 + Math.max(0, this.explodeDelay);
                     this.transitionTo(
                        AnchorCombatState.State.EXPLODE,
                        "Detonation slot selected"
                     );
                  }
               }
            }
         }
      }
   }

   private void explode() {
      if (this.mc.field_1724.field_6012 >= this.explodeReadyAt) {
         boolean var1 = this.placeProtection && this.protectionMode == AnchorCombatState.ProtectionMode.ALWAYS;
         if (this.charged() && (!var1 || this.protectionPresent())) {
            if (!this.isRapidFirstExplosion() && this.requireTotem && !this.mc.field_1724.method_24518(class_1802.field_8288)) {
               this.abort(
                  "Required totem lost before detonation"
               );
            } else if (AnchorDetonationSlot.glowstoneBlocksDetonation(
               BlockUtil.getAnchorCharges(this.anchorPos),
               this.mc.field_1724.method_6047().method_31574(class_1802.field_8801),
               this.mc.field_1724.method_6079().method_31574(class_1802.field_8801)
            )) {
               this.abort(
                  "Glowstone would recharge the anchor"
               );
            } else {
               class_3965 var2 = this.anchorHit();
               if (var2 == null) {
                  this.abort("Anchor occluded");
               } else {
                  if (this.isRapidFirstExplosion()) {
                     this.replacementHit = this.findReplacementHit();
                     if (this.replacementHit == null) {
                        this.abort(
                           "No support for second anchor"
                        );
                        return;
                     }

                     if (!this.alignReplacement()) {
                        return;
                     }
                  } else if (!this.align(var2, false)) {
                     return;
                  }

                  class_3965 var3 = this.followingCamera ? this.currentLookHit() : var2;
                  if (var3.method_17783() != class_240.field_1332) {
                     var3 = var2;
                  }

                  if (this.interact(var3)) {
                     this.confirmation.begin(this.mc.field_1724.field_6012, 1, 16, this.requireServerConfirmation);
                     this.transitionTo(
                        AnchorCombatState.State.WAIT_EXPLOSION,
                        "Await anchor removal"
                     );
                  }
               }
            }
         } else {
            this.abort(
               "Detonation prerequisites lost"
            );
         }
      }
   }

   void completeExplosion() {
      if (--this.cyclesRemaining > 0) {
         this.initialHit = this.replacementHit;
         this.replacementHit = null;
         this.protectionPos = null;
         byte[] var10002 = new byte[17];
         var10002[0] = (byte)(71);
         var10002[1] = (byte)(-1);
         var10002[2] = (byte)(100);
         var10002[3] = (byte)(-86);
         var10002[4] = (byte)(-59);
         var10002[5] = (byte)(-34);
         var10002[6] = (byte)(-2);
         var10002[7] = (byte)(-113);
         var10002[8] = (byte)(9);
         var10002[9] = (byte)(-51);
         var10002[10] = (byte)(-106);
         var10002[11] = (byte)(-42);
         var10002[12] = (byte)(-34);
         var10002[13] = (byte)(30);
         var10002[14] = (byte)(-37);
         var10002[15] = (byte)(-8);
         var10002[16] = (byte)(60);
         this.transitionTo(AnchorCombatState.State.PLACE_ANCHOR, $$v_s(var10002, -4281971771685220308L));
      } else {
         this.finish();
      }
   }

   boolean isRapidFirstExplosion() {
      return this.rapidChain && this.cyclesRemaining > 1;
   }

   private class_3965 findReplacementHit() {
      for (class_2350 var4 : class_2350.values()) {
         class_2338 var5 = this.anchorPos.method_10093(var4);
         if (this.mc.field_1687.method_8320(var5).method_26212(this.mc.field_1687, var5)) {
            class_2350 var6 = var4.method_10153();
            class_243 var7 = CombatGeometry.faceCenter(var5, var6);
            if (!(this.mc.field_1724.method_33571().method_1025(var7) > this.reach() * this.reach())) {
               class_3965 var8 = this.trace(var7.method_1019(class_243.method_24954(var6.method_62675()).method_1021(-0.001)));
               if (var8.method_17783() == class_240.field_1332 && var8.method_17777().equals(this.anchorPos)) {
                  return new class_3965(var7, var6, var5, false);
               }
            }
         }
      }

      return null;
   }

   private boolean alignReplacement() {
      if (!this.followingCamera) {
         return true;
      }

      class_3965 var1 = this.currentLookHit();
      if (var1.method_17783() == class_240.field_1332
         && var1.method_17777().equals(this.anchorPos)
         && CombatGeometry.rayHitsFace(
            this.replacementHit.method_17777(),
            this.replacementHit.method_17780(),
            this.mc.field_1724.method_33571(),
            this.mc.field_1724.method_5828(1.0F),
            this.reach()
         )) {
         this.cameraTarget = null;
         this.rotateStarted = -1;
         this.aim.reset();
         return this.mc.field_1724.field_6012 > this.lastRotationTick;
      }

      this.cameraTarget = this.replacementHit.method_17784();
      if (this.rotateStarted < 0) {
         this.rotateStarted = this.mc.field_1724.field_6012;
      }

      if (this.mc.field_1724.field_6012 - this.rotateStarted >= Math.max(1, this.maxRotateTicks)) {
         byte[] var10001 = new byte[25];
         var10001[0] = (byte)(91);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(108);
         var10001[3] = (byte)(-78);
         var10001[4] = (byte)(-124);
         var10001[5] = (byte)(-36);
         var10001[6] = (byte)(-11);
         var10001[7] = (byte)(-127);
         var10001[8] = (byte)(4);
         var10001[9] = (byte)(-52);
         var10001[10] = (byte)(-112);
         var10001[11] = (byte)(-42);
         var10001[12] = (byte)(-36);
         var10001[13] = (byte)(14);
         var10001[14] = (byte)(-43);
         var10001[15] = (byte)(-76);
         var10001[16] = (byte)(45);
         var10001[17] = (byte)(35);
         var10001[18] = (byte)(-95);
         var10001[19] = (byte)(-53);
         var10001[20] = (byte)(49);
         var10001[21] = (byte)(111);
         var10001[22] = (byte)(-81);
         var10001[23] = (byte)(-55);
         var10001[24] = (byte)(-59);
         this.abort($$v_s(var10001, -4281971771685220308L));
      }

      return false;
   }

   private void finish() {
      this.cyclesRemaining = Math.max(1, this.targetCycles);
      this.cooldownClock = this.bypassMode == AnchorCombatState.BypassMode.FAST ? 0 : 1;
      byte[] var10002 = new byte[17];
      var10002[0] = (byte)(90);
      var10002[1] = (byte)(-1);
      var10002[2] = (byte)(109);
      var10002[3] = (byte)(-85);
      var10002[4] = (byte)(-128);
      var10002[5] = (byte)(-47);
      var10002[6] = (byte)(-13);
      var10002[7] = (byte)(-119);
      var10002[8] = (byte)(65);
      var10002[9] = (byte)(-63);
      var10002[10] = (byte)(-117);
      var10002[11] = (byte)(-101);
      var10002[12] = (byte)(-51);
      var10002[13] = (byte)(11);
      var10002[14] = (byte)(-35);
      var10002[15] = (byte)(-32);
      var10002[16] = (byte)(60);
      this.transitionTo(AnchorCombatState.State.COOLDOWN, $$v_s(var10002, -4281971771685220308L));
   }

   private void abort(String reason) {
      this.cooldownClock = this.bypassMode == AnchorCombatState.BypassMode.FAST ? 1 : 3;
      this.transitionTo(AnchorCombatState.State.COOLDOWN, reason);
   }

   private boolean select(class_1792 item) {
      if (this.mc.field_1724.method_6047().method_31574(item)) {
         return this.selectSlot(this.mc.field_1724.method_31548().method_67532());
      } else {
         int var2 = InventoryUtil.findInHotbar(item);
         if (var2 < 0) {
            this.abort("Missing " + item);
            return false;
         } else {
            return this.selectSlot(var2);
         }
      }
   }

   private boolean selectSlot(int slot) {
      if (!this.actionTiming.canChangeSlot(this.mc.field_1724.field_6012)) {
         return false;
      }

      if (!ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.ANCHOR_COMBAT)) {
         return false;
      }

      if (this.mc.field_1724.method_31548().method_67532() != slot) {
         InventoryUtil.swap(slot);
         this.lastSelectedSlot = slot;
         this.actionTiming.selected(this.mc.field_1724.field_6012, this.switchDelay);
      }

      return this.actionTiming.slotReady(this.mc.field_1724.field_6012);
   }

   private boolean claimAction() {
      if (!this.actionTiming.canInteract(this.mc.field_1724.field_6012)) {
         return false;
      } else if (ActionArbiter.get().tryAcquire(ActionArbiter.Resource.USE_KEY, ActionArbiter.Owner.ANCHOR_COMBAT)
         && CombatActionGate.SHARED.tryClaim(this.mc.field_1687, this.mc.field_1724.field_6012)) {
         this.actionTiming.interacted(this.mc.field_1724.field_6012);
         return true;
      } else {
         return false;
      }
   }

   private boolean interact(class_3965 hit) {
      if (!this.claimAction()) {
         return false;
      } else {
         this.actionSent = true;
         class_1269 var2 = this.mc.field_1761.method_2896(this.mc.field_1724, class_1268.field_5808, hit);
         if (!var2.method_23665()) {
            byte[] var10001 = new byte[20];
            var10001[0] = (byte)(64);
            var10001[1] = (byte)(-12);
            var10001[2] = (byte)(104);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(-105);
            var10001[5] = (byte)(-34);
            var10001[6] = (byte)(-13);
            var10001[7] = (byte)(-104);
            var10001[8] = (byte)(8);
            var10001[9] = (byte)(-51);
            var10001[10] = (byte)(-118);
            var10001[11] = (byte)(-42);
            var10001[12] = (byte)(-49);
            var10001[13] = (byte)(2);
            var10001[14] = (byte)(-46);
            var10001[15] = (byte)(-15);
            var10001[16] = (byte)(58);
            var10001[17] = (byte)(62);
            var10001[18] = (byte)(-87);
            var10001[19] = (byte)(-54);
            this.abort($$v_s(var10001, -4281971771685220308L));
            return false;
         } else {
            this.mc.field_1724.method_6104(class_1268.field_5808);
            return true;
         }
      }
   }

   private boolean isAnchor() {
      return this.anchorPos != null && this.mc.field_1687.method_8320(this.anchorPos).method_27852(class_2246.field_23152);
   }

   private boolean charged() {
      return this.isAnchor() && BlockUtil.isRespawnAnchorCharged(this.anchorPos);
   }

   private boolean protectionPresent() {
      return this.protectionPos != null && this.mc.field_1687.method_8320(this.protectionPos).method_27852(class_2246.field_10171);
   }

   private class_2338 placementPosition(class_3965 hit) {
      return this.mc.field_1687.method_8320(hit.method_17777()).method_45474() ? hit.method_17777() : hit.method_17777().method_10093(hit.method_17780());
   }

   private boolean canPlace(class_2338 pos) {
      if (pos != null && this.mc.field_1687.method_8320(pos).method_45474()) {
         class_238 var2 = new class_238(pos);
         return !var2.method_994(this.mc.field_1724.method_5829())
            && this.mc
               .field_1687
               .method_8333(null, var2, e -> e.method_5805() && !e.method_31481() && !(e instanceof class_1542) && !(e instanceof class_1303))
               .isEmpty();
      } else {
         return false;
      }
   }

   private double reach() {
      return Math.min(this.maxPlaceReach, this.mc.field_1724.method_55754());
   }

   private class_3965 trace(class_243 point) {
      class_243 var2 = this.mc.field_1724.method_33571();
      class_243 var3 = point.method_1020(var2);
      if (var3.method_1027() > this.reach() * this.reach()) {
         point = var2.method_1019(var3.method_1029().method_1021(this.reach()));
      }

      return this.mc.field_1687.method_17742(new class_3959(var2, point, class_3960.field_17559, class_242.field_1348, this.mc.field_1724));
   }

   private class_3965 currentLookHit() {
      return this.trace(this.mc.field_1724.method_33571().method_1019(this.mc.field_1724.method_5828(1.0F).method_1021(this.reach())));
   }

   private class_3965 placementHit(class_2338 pos, class_3965 preferred) {
      if (preferred != null && this.placementPosition(preferred).equals(pos)) {
         class_243 var3 = class_243.method_24954(preferred.method_17780().method_62675()).method_1021(-0.001);
         class_3965 var4 = this.trace(preferred.method_17784().method_1019(var3));
         if (var4.method_17783() == class_240.field_1332 && this.placementPosition(var4).equals(pos)) {
            return var4;
         }
      }

      for (class_2350 var6 : class_2350.values()) {
         class_2338 var7 = pos.method_10093(var6);
         if (this.mc.field_1687.method_8320(var7).method_26212(this.mc.field_1687, var7)) {
            class_2350 var8 = var6.method_10153();
            class_3965 var9 = this.trace(CombatGeometry.faceCenter(var7, var8).method_1019(class_243.method_24954(var8.method_62675()).method_1021(-0.001)));
            if (var9.method_17783() == class_240.field_1332 && var9.method_17777().equals(var7) && var9.method_17780() == var8) {
               return var9;
            }
         }
      }

      return null;
   }

   private class_3965 anchorHit() {
      return this.anchorHitFor(this.anchorPos);
   }

   private class_3965 anchorHitFor(class_2338 pos) {
      if (pos == null) {
         return null;
      }

      class_3965 var2 = this.currentLookHit();
      if (var2.method_17783() == class_240.field_1332 && var2.method_17777().equals(pos)) {
         return var2;
      }

      class_3965 var3 = this.trace(CombatGeometry.anchorAimPoint(pos, this.lookDownBlocks));
      if (var3.method_17783() == class_240.field_1332 && var3.method_17777().equals(pos)) {
         return var3;
      }

      for (class_2350 var7 : class_2350.values()) {
         class_3965 var8 = this.trace(CombatGeometry.faceCenter(pos, var7).method_1019(class_243.method_24954(var7.method_62675()).method_1021(-0.001)));
         if (var8.method_17783() == class_240.field_1332 && var8.method_17777().equals(pos)) {
            return var8;
         }
      }

      return null;
   }

   private class_2338 findNearbyExistingAnchor() {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         class_2338 var1 = this.mc.field_1724.method_24515();
         int var2 = (int)Math.ceil(this.reach());
         class_2338 var3 = null;
         double var4 = Double.MAX_VALUE;

         for (int var6 = -var2; var6 <= var2; var6++) {
            for (int var7 = -2; var7 <= (2); var7++) {
               for (int var8 = -var2; var8 <= var2; var8++) {
                  class_2338 var9 = var1.method_10069(var6, var7, var8);
                  if (this.mc.field_1687.method_8320(var9).method_27852(class_2246.field_23152) && this.isInFov(var9)) {
                     double var10 = this.mc.field_1724.method_33571().method_1025(class_243.method_24953(var9));
                     if (var10 <= this.reach() * this.reach() && var10 < var4) {
                        for (class_2350 var15 : class_2350.values()) {
                           class_3965 var16 = this.trace(
                              CombatGeometry.faceCenter(var9, var15).method_1019(class_243.method_24954(var15.method_62675()).method_1021(-0.001))
                           );
                           if (var16.method_17783() == class_240.field_1332 && var16.method_17777().equals(var9)) {
                              var3 = var9;
                              var4 = var10;
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

         return var3;
      } else {
         return null;
      }
   }

   private class_2338 findProtectionPosition() {
      class_243 var1 = this.mc.field_1724.method_73189();
      int var2 = class_2338.method_49638(var1).method_10264();
      int var3 = this.anchorPos.method_10264();
      int var4 = Math.min(var2, var3) - 1;
      int var5 = Math.max(var2, var3) + 1;
      class_2338 var6 = class_2338.method_49637(
         (var1.field_1352 + this.anchorPos.method_10263() + 0.5) / 2.0, var1.field_1351, (var1.field_1350 + this.anchorPos.method_10260() + 0.5) / 2.0
      );
      class_2338[] var10000 = new class_2338[5];
      var10000[0] = var6;
      var10000[1] = var6.method_10095();
      var10000[2] = var6.method_10072();
      var10000[3] = var6.method_10078();
      var10000[4] = var6.method_10067();
      class_2338[] var7 = var10000;

      for (class_2338 var11 : var7) {
         for (int var12 = var4; var12 <= var5; var12++) {
            class_2338 var13 = new class_2338(var11.method_10263(), var12, var11.method_10260());
            if (!var13.equals(this.anchorPos)
               && CombatGeometry.betweenPlayerAndAnchor(var1, this.anchorPos, var13)
               && (this.mc.field_1687.method_8320(var13).method_27852(class_2246.field_10171) || this.canPlace(var13) && this.placementHit(var13, null) != null)
               )
             {
               return var13;
            }
         }
      }

      return null;
   }

   private boolean align(class_3965 target, boolean matchFace) {
      if (!this.followingCamera) {
         return true;
      }

      class_3965 var3 = this.currentLookHit();
      if (var3.method_17783() != class_240.field_1332
         || !var3.method_17777().equals(target.method_17777())
         || matchFace && var3.method_17780() != target.method_17780()) {
         this.cameraTarget = target.method_17784();
         if (this.rotateStarted < 0) {
            this.rotateStarted = this.mc.field_1724.field_6012;
         }

         if (this.mc.field_1724.field_6012 - this.rotateStarted >= Math.max(1, this.maxRotateTicks)) {
            byte[] var10001 = new byte[26];
            var10001[0] = (byte)(74);
            var10001[1] = (byte)(-5);
            var10001[2] = (byte)(113);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(-105);
            var10001[5] = (byte)(-34);
            var10001[6] = (byte)(-80);
            var10001[7] = (byte)(-115);
            var10001[8] = (byte)(13);
            var10001[9] = (byte)(-53);
            var10001[10] = (byte)(-125);
            var10001[11] = (byte)(-104);
            var10001[12] = (byte)(-48);
            var10001[13] = (byte)(2);
            var10001[14] = (byte)(-42);
            var10001[15] = (byte)(-32);
            var10001[16] = (byte)(121);
            var10001[17] = (byte)(62);
            var10001[18] = (byte)(-91);
            var10001[19] = (byte)(-61);
            var10001[20] = (byte)(48);
            var10001[21] = (byte)(43);
            var10001[22] = (byte)(-32);
            var10001[23] = (byte)(-45);
            var10001[24] = (byte)(-60);
            var10001[25] = (byte)(102);
            this.abort($$v_s(var10001, -4281971771685220308L));
         }

         return false;
      } else {
         this.cameraTarget = null;
         this.rotateStarted = -1;
         this.aim.reset();
         return this.bypassMode == AnchorCombatState.BypassMode.FAST
            ? this.mc.field_1724.field_6012 >= this.lastRotationTick
            : this.mc.field_1724.field_6012 > this.lastRotationTick;
      }
   }

   public void onRender(float frameSeconds) {
      if (this.cameraTarget != null
         && this.mc != null
         && this.mc.field_1724 != null
         && this.mc.field_1687 != null
         && this.mc.field_1755 == null
         && CombatSequenceLease.SHARED.owns(this)) {
         if (this.manualInput()) {
            byte[] var10001 = new byte[19];
            var10001[0] = (byte)(68);
            var10001[1] = (byte)(-5);
            var10001[2] = (byte)(114);
            var10001[3] = (byte)(-85);
            var10001[4] = (byte)(-124);
            var10001[5] = (byte)(-45);
            var10001[6] = (byte)(-80);
            var10001[7] = (byte)(-113);
            var10001[8] = 0;
            var10001[9] = (byte)(-49);
            var10001[10] = (byte)(-127);
            var10001[11] = (byte)(-124);
            var10001[12] = (byte)(-36);
            var10001[13] = (byte)(71);
            var10001[14] = (byte)(-47);
            var10001[15] = (byte)(-6);
            var10001[16] = (byte)(41);
            var10001[17] = (byte)(63);
            var10001[18] = (byte)(-72);
            this.reset($$v_s(var10001, -4281971771685220308L));
         } else if (ActionArbiter.get().tryAcquire(ActionArbiter.Resource.CAMERA, ActionArbiter.Owner.ANCHOR_COMBAT)) {
            class_243 var2 = this.cameraTarget.method_1020(this.mc.field_1724.method_33571());
            float var3 = (float)(Math.toDegrees(Math.atan2(var2.field_1350, var2.field_1352)) - 90.0);
            float var4 = (float)(-Math.toDegrees(Math.atan2(var2.field_1351, Math.hypot(var2.field_1352, var2.field_1350))));
            CombatAim.Angles var5 = this.aim
               .step(
                  this.mc.field_1724.method_36454(),
                  this.mc.field_1724.method_36455(),
                  var3,
                  var4,
                  this.rotationSpeed,
                  ((Double)this.mc.field_1690.method_42495().method_41753()).floatValue(),
                  frameSeconds,
                  this.humanize
               );
            this.mc.field_1724.method_36456(var5.yaw());
            this.mc.field_1724.method_36457(var5.pitch());
            this.lastRotationTick = this.mc.field_1724.field_6012;
            this.rememberInput();
         }
      }
   }

   private boolean manualInput() {
      return this.lastSelectedSlot >= 0
         && (
            this.lastSelectedSlot != this.mc.field_1724.method_31548().method_67532()
               || Math.abs(class_3532.method_15393(this.mc.field_1724.method_36454() - this.lastYaw)) > 15.0F
               || Math.abs(this.mc.field_1724.method_36455() - this.lastPitch) > 10.0F
         );
   }

   private void rememberInput() {
      this.lastSelectedSlot = this.mc.field_1724.method_31548().method_67532();
      this.lastYaw = this.mc.field_1724.method_36454();
      this.lastPitch = this.mc.field_1724.method_36455();
   }

   public boolean isInFov(class_243 target) {
      if (this.fov >= 360.0) {
         return true;
      } else {
         return this.mc.field_1724 == null
            ? false
            : CombatGeometry.isInFov(this.mc.field_1724.method_33571(), this.mc.field_1724.method_5720(), target, this.fov);
      }
   }

   public boolean isInFov(class_2338 pos) {
      if (this.fov >= 360.0) {
         return true;
      }

      if (this.mc.field_1724 == null) {
         return false;
      }

      class_243 var2 = this.mc.field_1724.method_33571();
      class_243 var3 = this.mc.field_1724.method_5720();
      if (CombatGeometry.isInFov(var2, var3, class_243.method_24953(pos), this.fov)) {
         return true;
      }

      if (CombatGeometry.isInFov(var2, var3, CombatGeometry.anchorAimPoint(pos, this.lookDownBlocks), this.fov)) {
         return true;
      }

      for (class_2350 var7 : class_2350.values()) {
         if (CombatGeometry.isInFov(var2, var3, CombatGeometry.faceCenter(pos, var7), this.fov)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasTargetInFov() {
      if (this.anchorPos != null || this.state != AnchorCombatState.State.IDLE) {
         return true;
      }

      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         class_3965 var1 = this.currentLookHit();
         if (var1.method_17783() == class_240.field_1332) {
            class_2338 var2 = var1.method_17777();
            if (this.mc.field_1687.method_8320(var2).method_27852(class_2246.field_23152) && this.isInFov(var2)) {
               return true;
            }

            class_2338 var3 = this.placementPosition(var1);
            if (var3 != null && this.canPlace(var3) && this.isInFov(var3) && InventoryUtil.findInHotbar(class_1802.field_23141) >= 0) {
               return true;
            }
         }

         return this.findNearbyExistingAnchor() != null;
      } else {
         return false;
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

   public enum BypassMode {
      NORMAL,
      GRIM_AC,
      FAST;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AnchorCombatState.BypassMode[] $values() {
         AnchorCombatState.BypassMode[] var10000 = new AnchorCombatState.BypassMode[3];
         var10000[0] = NORMAL;
         var10000[1] = GRIM_AC;
         var10000[2] = FAST;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(75);
         var10002[5] = (byte)(46);
         NORMAL = new AnchorCombatState.BypassMode();
         var10002 = new byte[7];
         var10002[0] = (byte)(127);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(85);
         var10002[5] = (byte)(35);
         var10002[6] = (byte)(-127);
         GRIM_AC = new AnchorCombatState.BypassMode();
         var10002 = new byte[4];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         FAST = new AnchorCombatState.BypassMode();
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

   public enum ProtectionMode {
      SMART,
      ALWAYS,
      OFF;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AnchorCombatState.ProtectionMode[] $values() {
         AnchorCombatState.ProtectionMode[] var10000 = new AnchorCombatState.ProtectionMode[3];
         var10000[0] = SMART;
         var10000[1] = ALWAYS;
         var10000[2] = OFF;
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
         var10002[4] = (byte)(-47);
         SMART = new AnchorCombatState.ProtectionMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(75);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(-36);
         var10002[5] = (byte)(-59);
         ALWAYS = new AnchorCombatState.ProtectionMode();
         var10002 = new byte[3];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-36);
         var10002[2] = (byte)(90);
         OFF = new AnchorCombatState.ProtectionMode();
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
      PLACE_ANCHOR,
      WAIT_ANCHOR,
      ROTATE_SAFE,
      PLACE_GLOWSTONE,
      WAIT_CHARGE,
      PLACE_PROTECTION,
      WAIT_PROTECTION,
      SWITCH_TOTEM,
      EXPLODE,
      WAIT_EXPLOSION,
      COOLDOWN;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AnchorCombatState.State[] $values() {
         AnchorCombatState.State[] var10000 = new AnchorCombatState.State[13];
         var10000[0] = IDLE;
         var10000[1] = ACQUIRE;
         var10000[2] = PLACE_ANCHOR;
         var10000[3] = WAIT_ANCHOR;
         var10000[4] = ROTATE_SAFE;
         var10000[5] = PLACE_GLOWSTONE;
         var10000[6] = WAIT_CHARGE;
         var10000[7] = PLACE_PROTECTION;
         var10000[8] = WAIT_PROTECTION;
         var10000[9] = SWITCH_TOTEM;
         var10000[10] = EXPLODE;
         var10000[11] = WAIT_EXPLOSION;
         var10000[12] = COOLDOWN;
         return var10000;
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4]
                  ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                  ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }
}
