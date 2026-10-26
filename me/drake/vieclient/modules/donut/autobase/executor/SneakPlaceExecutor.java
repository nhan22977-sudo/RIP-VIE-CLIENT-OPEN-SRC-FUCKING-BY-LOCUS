package me.drake.vieclient.modules.donut.autobase.executor;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.event.events.ServerBlockUpdateEvent;
import me.drake.vieclient.modules.donut.autobase.camera.AutoBaseCamera;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1268;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_3965;
import net.minecraft.class_3675.class_306;
import net.minecraft.class_3675.class_307;
import org.lwjgl.glfw.GLFW;

public final class SneakPlaceExecutor {
   private PlacementStage stage = PlacementStage.IDLE;
   private PlacementTarget currentTarget = null;
   private boolean sneakRequestedByModule = false;
   private int sneakWaitTicks = 0;
   private int confirmationTicks = 0;
   private final int maxConfirmationWaitTicks = 20;
   private long actionGeneration = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public PlacementStage getStage() {
      return this.stage;
   }

   public PlacementTarget getCurrentTarget() {
      return this.currentTarget;
   }

   public long getActionGeneration() {
      return this.actionGeneration;
   }

   public void setTarget(PlacementTarget target) {
      this.currentTarget = target;
      this.stage = target != null ? PlacementStage.SELECT_TARGET : PlacementStage.IDLE;
      this.confirmationTicks = 0;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void tick(class_310 mc, ActionArbiter arbiter, AutoBaseCamera camera, double reachDistance) {
      if (mc != null && mc.field_1724 != null && mc.field_1687 != null) {
         switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$autobase$executor$PlacementStage[this.stage.ordinal()]) {
            case 1:
               if (this.currentTarget != null) {
                  this.stage = PlacementStage.SELECT_TARGET;
               }
               break;
            case 2:
               if (this.currentTarget == null) {
                  this.stage = PlacementStage.IDLE;
                  return;
               }

               this.stage = PlacementStage.ACQUIRE_INPUT;
               break;
            case 3:
               boolean var16 = arbiter.tryAcquireAll(
                  ActionArbiter.Owner.AUTO_BASE_BUILD,
                  5,
                  ActionArbiter.Resource.MOVEMENT,
                  ActionArbiter.Resource.CAMERA,
                  ActionArbiter.Resource.SNEAK,
                  ActionArbiter.Resource.USE_KEY,
                  ActionArbiter.Resource.HOTBAR_SLOT
               );
               if (var16) {
                  this.stage = PlacementStage.SELECT_ITEM;
               }
               break;
            case 4:
               if (this.currentTarget == null) {
                  this.stage = PlacementStage.IDLE;
                  return;
               }

               class_1792 var15 = this.currentTarget.getRequiredItem();
               if (var15 == null) {
                  this.stage = PlacementStage.REQUEST_SNEAK;
                  return;
               }

               int var17 = prepareItemInHotbar(mc, var15);
               if (var17 == -1) {
                  this.currentTarget.setFailureReason("Missing material in inventory: " + var15);
                  this.stage = PlacementStage.FAILED_BLOCKED;
                  return;
               }

               this.stage = PlacementStage.REQUEST_SNEAK;
               break;
            case 5:
               mc.field_1690.field_1832.method_23481(true);
               this.sneakRequestedByModule = true;
               this.sneakWaitTicks = 2;
               this.stage = PlacementStage.WAIT_INPUT_PROPAGATION;
               break;
            case 6:
               mc.field_1690.field_1832.method_23481(true);
               this.sneakWaitTicks--;
               if (this.sneakWaitTicks <= 0) {
                  this.stage = PlacementStage.AIM;
               }
               break;
            case 7:
               mc.field_1690.field_1832.method_23481(true);
               if (this.currentTarget == null) {
                  this.stage = PlacementStage.IDLE;
                  return;
               }

               class_243 var14 = mc.field_1724.method_33571();
               camera.setTargetPoint(var14, this.currentTarget.getHitVector());
               camera.update(mc.field_1724);
               if (camera.isAligned(mc.field_1724)) {
                  this.stage = PlacementStage.VALIDATE;
               }
               break;
            case 8:
               mc.field_1690.field_1832.method_23481(true);
               if (this.currentTarget == null) {
                  this.stage = PlacementStage.IDLE;
                  return;
               }

               class_243 var13 = mc.field_1724.method_33571();
               double var7 = var13.method_1022(this.currentTarget.getHitVector());
               if (var7 > reachDistance) {
                  this.currentTarget
                     .setFailureReason(
                        "Target out of reach (" + String.format("%.2f", var7) + " > " + reachDistance + ")"
                     );
                  this.stage = PlacementStage.FAILED_BLOCKED;
                  return;
               }

               class_2680 var9 = mc.field_1687.method_8320(this.currentTarget.getTargetPos());
               if (BlockStateMatcher.matches(var9, this.currentTarget.getExpectedState())) {
                  this.stage = PlacementStage.COMPLETED;
                  return;
               }

               if (this.currentTarget.getSupportPos() != null) {
                  class_2680 var10 = mc.field_1687.method_8320(this.currentTarget.getSupportPos());
                  if (var10.method_26215()) {
                     this.currentTarget.setFailureReason("Support block missing at " + this.currentTarget.getSupportPos());
                     this.stage = PlacementStage.FAILED_BLOCKED;
                     return;
                  }
               }

               this.stage = PlacementStage.PLACE_ONCE;
               break;
            case 9:
               mc.field_1690.field_1832.method_23481(true);
               if (this.currentTarget == null) {
                  this.stage = PlacementStage.IDLE;
                  return;
               }

               try {
                  class_3965 var12 = new class_3965(
                     this.currentTarget.getHitVector(),
                     this.currentTarget.getPlacementFace(),
                     this.currentTarget.getSupportPos() != null ? this.currentTarget.getSupportPos() : this.currentTarget.getTargetPos(),
                     false
                  );
                  mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, var12);
                  mc.field_1724.method_6104(class_1268.field_5808);
                  this.actionGeneration++;
                  this.confirmationTicks = 0;
                  this.stage = PlacementStage.WAIT_WORLD_CONFIRMATION;
               } catch (Exception var11) {
                  VieClientMod.LOGGER
                     .error(
                        "[AutoBase] Placement error: ",
                        var11
                     );
                  this.currentTarget.setFailureReason("Exception during interactBlock: " + var11.getMessage());
                  this.stage = PlacementStage.FAILED_BLOCKED;
               }
               break;
            case 10:
               mc.field_1690.field_1832.method_23481(true);
               if (this.currentTarget == null) {
                  this.stage = PlacementStage.IDLE;
                  return;
               }

               class_2680 var6 = mc.field_1687.method_8320(this.currentTarget.getTargetPos());
               if (BlockStateMatcher.matches(var6, this.currentTarget.getExpectedState())) {
                  this.stage = PlacementStage.COMPLETED;
                  return;
               }

               this.confirmationTicks++;
               if (this.confirmationTicks > 20) {
                  this.currentTarget.incrementRetry();
                  if (this.currentTarget.getRetryCount() > 2) {
                     this.currentTarget
                        .setFailureReason(
                           "Server confirmation timeout / placement rejected"
                        );
                     this.stage = PlacementStage.FAILED_BLOCKED;
                  } else {
                     this.stage = PlacementStage.AIM;
                  }
               }
            case 11:
            case 12:
         }
      } else {
         this.stage = PlacementStage.IDLE;
      }
   }

   public void onServerBlockUpdate(ServerBlockUpdateEvent event) {
      if (this.stage == PlacementStage.WAIT_WORLD_CONFIRMATION
         && this.currentTarget != null
         && event.getPos().equals(this.currentTarget.getTargetPos())
         && BlockStateMatcher.matches(event.getState(), this.currentTarget.getExpectedState())) {
         this.stage = PlacementStage.COMPLETED;
      }
   }

   public void release(class_310 mc, ActionArbiter arbiter) {
      if (this.sneakRequestedByModule && mc != null && mc.field_1690 != null) {
         this.sneakRequestedByModule = false;
         if (!isPhysicalKeyPressed(mc, mc.field_1690.field_1832)) {
            mc.field_1690.field_1832.method_23481(false);
         }
      }

      if (arbiter != null) {
         ActionArbiter.Owner var10001 = ActionArbiter.Owner.AUTO_BASE_BUILD;
         ActionArbiter.Resource[] var10002 = new ActionArbiter.Resource[5];
         var10002[0] = ActionArbiter.Resource.MOVEMENT;
         var10002[1] = ActionArbiter.Resource.CAMERA;
         var10002[2] = ActionArbiter.Resource.SNEAK;
         var10002[3] = ActionArbiter.Resource.USE_KEY;
         var10002[4] = ActionArbiter.Resource.HOTBAR_SLOT;
         arbiter.releaseAll(var10001, var10002);
      }
   }

   public void keepSneakingForConsecutiveTarget() {
      this.sneakRequestedByModule = true;
   }

   private static boolean isPhysicalKeyPressed(class_310 mc, class_304 binding) {
      if (mc != null && mc.method_22683() != null && binding != null) {
         try {
            class_306 var2 = binding.method_1429();
            if (var2.method_1442() == class_307.field_1668) {
               return class_3675.method_15987(mc.method_22683(), var2.method_1444());
            }

            if (var2.method_1442() == class_307.field_1672) {
               return GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), var2.method_1444()) == 1;
            }
         } catch (Throwable var3) {
         }

         return false;
      } else {
         return false;
      }
   }

   private static int prepareItemInHotbar(class_310 mc, class_1792 requiredItem) {
      if (mc.field_1724 == null) {
         return -1;
      }

      class_1661 var2 = mc.field_1724.method_31548();

      for (int var3 = 0; var3 < (9); var3++) {
         class_1799 var4 = var2.method_5438(var3);
         if (var4.method_7909() == requiredItem && !var4.method_7960()) {
            InventoryUtil.swap(var3);
            return var3;
         }
      }

      int var6 = -1;

      for (int var7 = 9; var7 < (36); var7++) {
         class_1799 var5 = var2.method_5438(var7);
         if (var5.method_7909() == requiredItem && !var5.method_7960()) {
            var6 = var7;
            break;
         }
      }

      if (var6 == (-1)) {
         return -1;
      }

      int var8 = 8;

      for (int var9 = 0; var9 < (9); var9++) {
         if (var2.method_5438(var9).method_7960()) {
            var8 = var9;
            break;
         }
      }

      if (mc.field_1761 != null) {
         mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, var6, var8, class_1713.field_7791, mc.field_1724);
      }

      InventoryUtil.swap(var8);
      return var8;
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
