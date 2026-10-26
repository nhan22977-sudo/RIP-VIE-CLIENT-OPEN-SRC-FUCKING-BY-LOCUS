package me.drake.vieclient.modules.combat;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Random;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BindSetting;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.ModeSetting;
import me.drake.vieclient.modules.movement.FreeLook;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.KeyUtils;
import me.drake.vieclient.util.RotationUtil;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_9334;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.lwjgl.glfw.GLFW;

public class AimAssist extends Module {
   private final DoubleSetting range = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Range",
         "Maximum target range (blocks)",
         6.5,
         1.0,
         10.0
      )
   );
   private final IntSetting fov = (IntSetting)this.addSetting(
      new IntSetting(
         "FOV",
         "Field-of-view gate (degrees)",
         90,
         10,
         360,
         5
      )
   );
   private final ModeSetting priority = (ModeSetting)this.addSetting(
      new ModeSetting(
         "Priority",
         "Target selection priority (Crosshair = closest to crosshair, Distance = closest in blocks)",
         AimAssist.Priority.CROSSHAIR,
         AimAssist.Priority.class
      )
   );
   private final BoolSetting drawFov = (BoolSetting)this.addSetting(
      new BoolSetting(
         "DrawFOV",
         "Render FOV circle on screen (when FOV < 180°)",
         true
      )
   );
   private final ColorSetting fovColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "FOVColor",
         "Color of the FOV circle",
         -2136451593
      )
   );
   private final ColorSetting targetColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "TargetColor",
         "Color when target is in FOV",
         -1427159996
      )
   );
   private final BindSetting triggerBind = (BindSetting)this.addSetting(
      new BindSetting(
         "Trigger Bind",
         "Hold key to aim (-1 = Always active)",
         -1
      )
   );
   private final DoubleSetting speed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Speed",
         "Horizontal aiming speed",
         6.0,
         1.0,
         20.0
      )
   );
   private final DoubleSetting verticalSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "VerticalSpeed",
         "Vertical aiming speed",
         4.0,
         0.0,
         15.0
      )
   );
   private final BoolSetting aimPitch = (BoolSetting)this.addSetting(
      new BoolSetting(
         "AimPitch",
         "Assist vertical pitch aiming",
         true
      )
   );
   private final BoolSetting targetPlayers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Players",
         "Target players",
         true
      )
   );
   private final BoolSetting targetMobs = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Mobs",
         "Target mobs (enables testing in singleplayer)",
         true
      )
   );
   private final BoolSetting clickOnly = (BoolSetting)this.addSetting(
      new BoolSetting(
         "ClickOnly",
         "Only aim when holding left click / attacking",
         false
      )
   );
   private final BoolSetting weaponOnly = (BoolSetting)this.addSetting(
      new BoolSetting(
         "WeaponOnly",
         "Only aim when holding a weapon (sword/axe/mace)",
         false
      )
   );
   private final BoolSetting stickyTarget = (BoolSetting)this.addSetting(
      new BoolSetting(
         "StickyTarget",
         "Keep aiming at current target while in range & FOV",
         true
      )
   );
   private final BoolSetting humanize = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Humanize",
         "Natural human acceleration, deceleration and micro-tremor",
         true
      )
   );
   private final BoolSetting gcdFix = (BoolSetting)this.addSetting(
      new BoolSetting(
         "GCDFix",
         "Quantize rotation deltas to match mouse sensitivity (anti-cheat safe)",
         true
      )
   );
   private final ModeSetting targetPoint = (ModeSetting)this.addSetting(
      new ModeSetting(
         "TargetPoint",
         "Body part to aim at",
         AimAssist.TargetPoint.CHEST,
         AimAssist.TargetPoint.class
      )
   );
   private final BoolSetting ignoreWalls = (BoolSetting)this.addSetting(
      new BoolSetting(
         "IgnoreWalls",
         "Aim through walls",
         true
      )
   );
   private final BoolSetting instantLook = (BoolSetting)this.addSetting(
      new BoolSetting(
         "InstantLook",
         "Instantly snap to target (blatant)",
         false
      )
   );
   private final BoolSetting randomizeHitbox = (BoolSetting)this.addSetting(
      new BoolSetting(
         "RandomizeHitbox",
         "Randomly offset aim point (left/right/diagonals) within target hitbox",
         true
      )
   );
   private final BoolSetting stopOnTarget = (BoolSetting)this.addSetting(
      new BoolSetting(
         "StopOnTarget",
         "Stop aiming when crosshair is already on target hitbox (extra safe/legit)",
         true
      )
   );
   private final BoolSetting fovScaling = (BoolSetting)this.addSetting(
      new BoolSetting(
         "FOVScaling",
         "Smoothly scale speed across FOV gate (safe for wide FOV)",
         true
      )
   );
   private final BoolSetting accelerate = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Accelerate",
         "Accelerate turn speed when target is far from crosshair or behind (humanized swipe)",
         true
      )
   );
   private final DoubleSetting maxAcceleration = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "MaxAcceleration",
         "Maximum speed multiplier for wide-angle turns",
         2.5,
         1.0,
         5.0
      )
   );
   private final BoolSetting curvedAim = (BoolSetting)this.addSetting(
      new BoolSetting(
         "CurvedAim",
         "Human wrist curved path (anti-cheat safe, breaks straight-line ratios)",
         true
      )
   );
   private final BoolSetting overshoot = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Overshoot",
         "Natural human overshoot on fast flicks",
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
   private class_1297 target;
   private class_1297 lastTarget = null;
   private double currentOffsetH = 0.0;
   private double currentOffsetV = 0.0;
   private double targetOffsetH = 0.0;
   private double targetOffsetV = 0.0;
   private long nextRandomizeTime = 0L;
   private long aimDelayUntil = 0L;
   private final Random random = new Random();
   private float velocityYaw = 0.0F;
   private float velocityPitch = 0.0F;
   private float residualYaw = 0.0F;
   private float residualPitch = 0.0F;
   private long targetAcquiredTime = 0L;
   private float initialFlickAngle = 0.0F;
   private float flickCurveDir = 1.0F;
   private int overshootPhase = 0;
   private float overshootAmount = 0.0F;
   private long overshootStartTime = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AimAssist() {
      super(
         "AimAssist",
         "Smoothly rotate toward nearby players or mobs with humanized motion",
         Category.COMBAT
      );
      this.gcdFix.setVisibleInGui(false);
      this.curvedAim.setVisibleInGui(false);
      this.fovScaling.setVisibleInGui(false);
      this.accelerate.setVisibleInGui(false);
      this.maxAcceleration.setVisibleInGui(false);
      this.overshoot.setVisibleInGui(false);
      this.randomizeHitbox.setVisibleInGui(false);
      this.stopOnTarget.setVisibleInGui(false);
      this.humanize.setVisibleInGui(false);
      this.aimPitch.setVisibleInGui(false);
      this.stickyTarget.setVisibleInGui(false);
      this.randomDelay.setVisibleInGui(false);
      this.fovColor.visibleWhen(this.drawFov::get);
      this.targetColor.visibleWhen(this.drawFov::get);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetMotionState();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.resetMotionState();
   }

   private void resetMotionState() {
      this.target = null;
      this.lastTarget = null;
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
      this.overshootPhase = 0;
      this.overshootAmount = 0.0F;
      this.overshootStartTime = 0L;
      this.aimDelayUntil = 0L;
   }

   @EventHandler
   public void onTick(TickEvent tickEvent) {
      if (tickEvent.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1687 != null && mc.field_1724 != null) {
            if (this.target != null) {
               double var2 = (Double)this.range.get() + (this.stickyTarget.get() ? 0.5 : 0.0);
               double var4 = ((Integer)this.fov.get()).intValue() + (this.stickyTarget.get() ? 15.0 : 0.0);
               if (!this.isValidTarget(this.target, var2, var4)) {
                  this.target = null;
                  this.velocityYaw = 0.0F;
                  this.velocityPitch = 0.0F;
                  this.residualYaw = 0.0F;
                  this.residualPitch = 0.0F;
               }
            }
         } else {
            this.resetMotionState();
         }
      }
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D) {
         if (mc.field_1724 != null && mc.field_1687 != null) {
            this.updateAim(event.getFrameTime());
         }
      }
   }

   private void updateAim(float frameTime) {
      if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1755 == null) {
         if (!this.isTriggerActive()) {
            if (this.target != null) {
               this.target = null;
               this.velocityYaw = 0.0F;
               this.velocityPitch = 0.0F;
               this.residualYaw = 0.0F;
               this.residualPitch = 0.0F;
            }
         } else if ((Boolean)this.clickOnly.get() && !this.isLeftClickHeld()) {
            this.velocityYaw = 0.0F;
            this.velocityPitch = 0.0F;
         } else if ((Boolean)this.weaponOnly.get() && !this.isHoldingWeapon()) {
            this.velocityYaw = 0.0F;
            this.velocityPitch = 0.0F;
         } else {
            if ((Boolean)this.stickyTarget.get() && this.target != null) {
               if (!this.isValidTarget(this.target, (Double)this.range.get() + 0.5, ((Integer)this.fov.get()).intValue() + 15.0)) {
                  this.target = null;
                  this.velocityYaw = 0.0F;
                  this.velocityPitch = 0.0F;
               }
            } else if (this.target != null && !this.isValidTarget(this.target, (Double)this.range.get(), ((Integer)this.fov.get()).intValue())) {
               this.target = null;
               this.velocityYaw = 0.0F;
               this.velocityPitch = 0.0F;
            }

            if (this.target == null) {
               this.target = this.findTarget();
               if (this.target != null && this.target != this.lastTarget) {
                  this.velocityYaw = 0.0F;
                  this.velocityPitch = 0.0F;
                  this.residualYaw = 0.0F;
                  this.residualPitch = 0.0F;
                  this.targetAcquiredTime = System.currentTimeMillis();
                  this.initialFlickAngle = 0.0F;
                  this.flickCurveDir = this.random.nextBoolean() ? 1.0F : -1.0F;
                  this.overshootPhase = 0;
                  if ((Boolean)this.randomDelay.get()) {
                     this.aimDelayUntil = System.currentTimeMillis() + 50L + this.random.nextInt(100);
                  }
               }
            }

            if (this.target != null && this.target.method_5805()) {
               if (System.currentTimeMillis() >= this.aimDelayUntil) {
                  AutoSwordBotting var2 = (AutoSwordBotting)VieClientMod.getInstance().getModuleManager().getModule(AutoSwordBotting.class);
                  if (var2 == null || !var2.isEnabled()) {
                     FreeLook var3 = (FreeLook)VieClientMod.getInstance().getModuleManager().getModule(FreeLook.class);
                     if (var3 == null || !var3.isEnabled() || !var3.cameraMode()) {
                        if (ActionArbiter.get().tryAcquire(ActionArbiter.Resource.CAMERA, ActionArbiter.Owner.AIMASSIST)) {
                           this.aim(this.target, frameTime);
                        }
                     }
                  }
               }
            } else {
               this.velocityYaw = 0.0F;
               this.velocityPitch = 0.0F;
            }
         }
      }
   }

   private class_1297 findTarget() {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         Object var1 = null;
         double var2 = Double.MAX_VALUE;
         AimAssist.Priority var4 = (AimAssist.Priority)this.priority.get();
         double var5 = (Double)this.range.get();
         int var7 = (Integer)this.fov.get();
         if ((Boolean)this.targetPlayers.get()) {
            for (class_1657 var9 : mc.field_1687.method_18456()) {
               if (this.isValidTarget(var9, var5, var7)) {
                  double var10 = this.computeTargetScore(var9, var4);
                  if (var10 < var2) {
                     var2 = var10;
                     var1 = var9;
                  }
               }
            }
         }

         if ((Boolean)this.targetMobs.get()) {
            double var14 = var5 * var5;

            for (class_1297 var11 : mc.field_1687.method_18112()) {
               if (var11 != mc.field_1724
                  && var11 instanceof class_1309
                  && !(var11 instanceof class_1657)
                  && !(mc.field_1724.method_5858(var11) > var14)
                  && this.isValidTarget(var11, var5, var7)) {
                  double var12 = this.computeTargetScore(var11, var4);
                  if (var12 < var2) {
                     var2 = var12;
                     var1 = var11;
                  }
               }
            }
         }

         return (class_1297)var1;
      } else {
         return null;
      }
   }

   double computeTargetScore(class_1297 entity, AimAssist.Priority prio) {
      if (mc.field_1724 != null && entity != null) {
         double var3 = this.getAngleDeltaToEntity(entity);
         double var5 = Math.sqrt(entity.method_5858(mc.field_1724));
         return prio == AimAssist.Priority.DISTANCE ? var5 + var3 * 0.001 : var3 + var5 * 0.001;
      } else {
         return Double.MAX_VALUE;
      }
   }

   double getAngleDeltaToEntity(class_1297 entity) {
      if (mc.field_1724 != null && entity != null) {
         double var2 = Math.abs(this.getYawDeltaToEntity(entity));
         if ((Boolean)this.aimPitch.get()) {
            class_243 var4 = this.getTargetAimPos(entity, 0.0);
            double var5 = var4.field_1352 - mc.field_1724.method_23317();
            double var7 = var4.field_1351 - (mc.field_1724.method_23318() + mc.field_1724.method_18381(mc.field_1724.method_18376()));
            double var9 = var4.field_1350 - mc.field_1724.method_23321();
            double var11 = Math.sqrt(var5 * var5 + var9 * var9);
            float var13 = (float)class_3532.method_15350(-Math.toDegrees(Math.atan2(var7, var11)), -90.0, 90.0);
            float var14 = var13 - mc.field_1724.method_36455();
            return Math.hypot(var2, var14);
         } else {
            return var2;
         }
      } else {
         return 0.0;
      }
   }

   float computeAngleFactor(float diffAngle) {
      float var2 = Math.abs(diffAngle);
      if ((Boolean)this.accelerate.get()) {
         if (var2 > 18.0F) {
            float var6 = class_3532.method_15363((var2 - 18.0F) / 90.0F, 0.0F, 1.0F);
            float var7 = var6 * var6 * (3.0F - 2.0F * var6);
            return 1.0F + (float)((Double)this.maxAcceleration.get() - 1.0) * var7;
         } else {
            return 1.0F;
         }
      } else {
         if ((Boolean)this.fovScaling.get() && (Integer)this.fov.get() < (360)) {
            float var3 = ((Integer)this.fov.get()).intValue() / 2.0F;
            float var4 = var3 * 0.35F;
            if (var2 > var4) {
               float var5 = class_3532.method_15363((var2 - var4) / Math.max(1.0F, var3 - var4), 0.0F, 1.0F);
               return 0.12F + 0.88F * (float)(0.5 * (1.0 + Math.cos(Math.PI * var5)));
            }
         }

         return 1.0F;
      }
   }

   private float calculateDesiredSpeed(float diffAngle, float maxSpeedDegPerSec) {
      float var3 = Math.abs(diffAngle);
      if (var3 < 0.04F) {
         return 0.0F;
      } else if (!(Boolean)this.humanize.get()) {
         float var9 = class_3532.method_15363(var3 / 10.0F, 0.15F, 1.0F);
         return Math.copySign(maxSpeedDegPerSec * var9, diffAngle);
      } else {
         float var4 = 16.0F;
         float var5 = class_3532.method_15363(var3 / var4, 0.0F, 1.0F);
         float var6 = var5 * var5 * (3.0F - 2.0F * var5);
         float var7 = 0.06F;
         float var8 = var7 + (1.0F - var7) * var6;
         return Math.copySign(maxSpeedDegPerSec * var8, diffAngle);
      }
   }

   private float computeMicroTremor(float diff, float velocity, boolean isPitch) {
      if (!(Boolean)this.humanize.get()) {
         return 0.0F;
      }

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

   private void aim(class_1297 entity, float frameTime) {
      double var3 = class_3532.method_15363(frameTime, 0.001F, 0.05F);
      long var5 = System.currentTimeMillis();
      class_243 var7 = this.getTargetAimPos(entity, var3);
      double var8 = var7.field_1352 - mc.field_1724.method_23317();
      double var10 = var7.field_1351 - (mc.field_1724.method_23318() + mc.field_1724.method_18381(mc.field_1724.method_18376()));
      double var12 = var7.field_1350 - mc.field_1724.method_23321();
      double var14 = Math.sqrt(var8 * var8 + var12 * var12);
      float var16 = (float)class_3532.method_15338(Math.toDegrees(Math.atan2(var12, var8)) - 90.0);
      float var17 = (float)class_3532.method_15350(-Math.toDegrees(Math.atan2(var10, var14)), -90.0, 90.0);
      if ((Boolean)this.instantLook.get()) {
         mc.field_1724.method_36456(var16);
         if ((Boolean)this.aimPitch.get()) {
            mc.field_1724.method_36457(var17);
         }
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
         if (var23 && (Boolean)this.stopOnTarget.get()) {
            float var24 = 1.0F - (float)Math.exp(-22.0 * var3);
            this.velocityYaw = this.velocityYaw + (0.0F - this.velocityYaw) * var24;
            this.velocityPitch = this.velocityPitch + (0.0F - this.velocityPitch) * var24;
            if (Math.abs(this.velocityYaw) < 0.2F && Math.abs(this.velocityPitch) < 0.2F) {
               this.velocityYaw = 0.0F;
               this.velocityPitch = 0.0F;
               this.residualYaw = 0.0F;
               this.residualPitch = 0.0F;
               this.initialFlickAngle = 0.0F;
               this.overshootPhase = 0;
               return;
            }
         }

         if (Math.abs(var20) < 0.04F && Math.abs(var21) < 0.04F) {
            this.velocityYaw = 0.0F;
            this.velocityPitch = 0.0F;
            this.initialFlickAngle = 0.0F;
            this.overshootPhase = 0;
         } else {
            float var46 = this.computeAngleFactor(var20);
            float var25 = this.computeAngleFactor(var21);
            float var26 = 1.0F;
            if ((Boolean)this.humanize.get() && this.targetAcquiredTime > 0L) {
               long var27 = var5 - this.targetAcquiredTime;
               if (var27 < 130L) {
                  float var29 = class_3532.method_15363((float)var27 / 130.0F, 0.15F, 1.0F);
                  var26 = var29 * var29 * (3.0F - 2.0F * var29);
               }
            }

            float var47 = 0.0F;
            if ((Boolean)this.curvedAim.get() && (Boolean)this.humanize.get() && this.initialFlickAngle > 4.0F) {
               float var28 = 1.0F - class_3532.method_15363(var22 / this.initialFlickAngle, 0.0F, 1.0F);
               float var49 = (float)Math.sin(var28 * Math.PI);
               float var30 = Math.min(2.0F, this.initialFlickAngle * 0.1F);
               var47 = var49 * var30 * this.flickCurveDir;
            }

            float var48 = 0.0F;
            if ((Boolean)this.overshoot.get() && (Boolean)this.humanize.get() && this.initialFlickAngle > 12.0F) {
               if (this.overshootPhase == 0 && Math.abs(var20) < 1.6F && Math.abs(this.velocityYaw) > 35.0F) {
                  this.overshootPhase = 1;
                  this.overshootAmount = Math.copySign(Math.min(0.5F, Math.abs(this.velocityYaw) * 0.005F), this.velocityYaw);
                  this.overshootStartTime = var5;
               }

               if (this.overshootPhase == 1) {
                  long var50 = var5 - this.overshootStartTime;
                  if (var50 < 100L) {
                     float var31 = (float)var50 / 100.0F;
                     var48 = this.overshootAmount * (1.0F - var31) * (float)Math.cos(var31 * Math.PI * 0.5);
                  } else {
                     this.overshootPhase = 2;
                  }
               }
            }

            float var51 = (float)((Double)this.speed.get() * 35.0) * var46 * var26;
            if (var23 && !(Boolean)this.stopOnTarget.get()) {
               var51 *= 0.35F;
            }

            float var52 = this.calculateDesiredSpeed(var20 + var47 + var48, var51);
            float var53 = class_3532.method_15363((Math.abs(var20) - 18.0F) / 90.0F, 0.0F, 1.0F);
            float var32 = Math.abs(var52) > Math.abs(this.velocityYaw)
               ? (this.humanize.get() ? 14.0F + (this.accelerate.get() ? 6.0F * var53 : 0.0F) : 32.0F)
               : (this.humanize.get() ? 20.0F : 38.0F);
            this.velocityYaw = this.velocityYaw + (var52 - this.velocityYaw) * (1.0F - (float)Math.exp(-var32 * var3));
            float var33 = this.velocityYaw * (float)var3;
            if (Math.abs(var33) > Math.abs(var20) && Math.abs(var20) > 0.04F && this.overshootPhase != 1) {
               var33 = var20;
               this.velocityYaw = 0.0F;
            }

            var33 += this.computeMicroTremor(var20, this.velocityYaw, false);
            float var34;
            if ((Boolean)this.gcdFix.get() && mc.field_1690 != null) {
               float var35 = ((Double)mc.field_1690.method_42495().method_41753()).floatValue();
               float var36 = RotationUtil.gcdFor(var35);
               if (var36 > 1.0E-4F) {
                  this.residualYaw += var33;
                  int var37 = Math.round(this.residualYaw / var36);
                  var34 = var37 * var36;
                  if (Math.abs(var34) > Math.abs(var20) && Math.abs(var20) > var36 && this.overshootPhase != 1) {
                     int var38 = (int)(Math.abs(var20) / var36);
                     var34 = Math.copySign(var38 * var36, var20);
                  }

                  this.residualYaw -= var34;
                  this.residualYaw = class_3532.method_15363(this.residualYaw, -var36, var36);
               } else {
                  var34 = var33;
               }
            } else {
               var34 = var33;
            }

            if (Math.abs(var34) > 0.0F) {
               mc.field_1724.method_36456(var18 + var34);
            }

            if ((Boolean)this.aimPitch.get() && (Double)this.verticalSpeed.get() > 0.0) {
               float var55 = 1.0F;
               if ((Boolean)this.humanize.get() && Math.abs(var20) > 6.0F) {
                  var55 = 1.0F / (1.0F + (Math.abs(var20) - 6.0F) / 16.0F);
               }

               float var56 = (float)((Double)this.verticalSpeed.get() * 25.0) * var25 * var26 * var55;
               if (var23 && !(Boolean)this.stopOnTarget.get()) {
                  var56 *= 0.35F;
               }

               float var57 = this.calculateDesiredSpeed(var21, var56);
               float var58 = class_3532.method_15363((Math.abs(var21) - 18.0F) / 90.0F, 0.0F, 1.0F);
               float var39 = Math.abs(var57) > Math.abs(this.velocityPitch)
                  ? (this.humanize.get() ? 13.0F + (this.accelerate.get() ? 5.0F * var58 : 0.0F) : 30.0F)
                  : (this.humanize.get() ? 18.0F : 35.0F);
               this.velocityPitch = this.velocityPitch + (var57 - this.velocityPitch) * (1.0F - (float)Math.exp(-var39 * var3));
               float var40 = this.velocityPitch * (float)var3;
               if (Math.abs(var40) > Math.abs(var21) && Math.abs(var21) > 0.04F) {
                  var40 = var21;
                  this.velocityPitch = 0.0F;
               }

               var40 += this.computeMicroTremor(var21, this.velocityPitch, true);
               float var41;
               if ((Boolean)this.gcdFix.get() && mc.field_1690 != null) {
                  float var42 = ((Double)mc.field_1690.method_42495().method_41753()).floatValue();
                  float var43 = RotationUtil.gcdFor(var42);
                  if (var43 > 1.0E-4F) {
                     this.residualPitch += var40;
                     int var44 = Math.round(this.residualPitch / var43);
                     var41 = var44 * var43;
                     if (Math.abs(var41) > Math.abs(var21) && Math.abs(var21) > var43) {
                        int var45 = (int)(Math.abs(var21) / var43);
                        var41 = Math.copySign(var45 * var43, var21);
                     }

                     this.residualPitch -= var41;
                     this.residualPitch = class_3532.method_15363(this.residualPitch, -var43, var43);
                  } else {
                     var41 = var40;
                  }
               } else {
                  var41 = var40;
               }

               if (Math.abs(var41) > 0.0F) {
                  mc.field_1724.method_36457(class_3532.method_15363(var19 + var41, -90.0F, 90.0F));
               }
            }
         }
      }
   }

   private class_243 getTargetAimPos(class_1297 entity, double dt) {
      class_243 var4 = new class_243(entity.method_23317(), entity.method_23318(), entity.method_23321());
      float var5 = entity.method_18381(entity.method_18376());
      AimAssist.TargetPoint var6 = (AimAssist.TargetPoint)this.targetPoint.get();

      double var7 = switch (var6) {
         case CHEST -> var4.field_1351 + var5 * 0.75;
         case HEAD -> var4.field_1351 + var5;
         case BODY -> var4.field_1351 + var5 * 0.5;
         case NEAREST -> {
            double var9 = mc.field_1724.method_23318() + mc.field_1724.method_18381(mc.field_1724.method_18376());
            yield class_3532.method_15350(var9, var4.field_1351 + 0.2, var4.field_1351 + var5);
         }
      };
      if ((Boolean)this.randomizeHitbox.get() && mc.field_1724 != null) {
         long var29 = System.currentTimeMillis();
         if (entity != this.lastTarget || var29 >= this.nextRandomizeTime) {
            this.targetOffsetH = this.random.nextDouble() * 0.4 - 0.2;
            this.targetOffsetV = this.random.nextDouble() * 0.4 - 0.2;
            this.nextRandomizeTime = var29 + 180L + this.random.nextInt(170);
            this.lastTarget = entity;
         }

         double var11 = 1.0 - Math.exp(-8.0 * dt);
         this.currentOffsetH = this.currentOffsetH + (this.targetOffsetH - this.currentOffsetH) * var11;
         this.currentOffsetV = this.currentOffsetV + (this.targetOffsetV - this.currentOffsetV) * var11;
         double var13 = entity.method_23317() - mc.field_1724.method_23317();
         double var15 = entity.method_23321() - mc.field_1724.method_23321();
         double var17 = Math.sqrt(var13 * var13 + var15 * var15);
         double var19 = 0.0;
         double var21 = 0.0;
         if (var17 > 1.0E-4) {
            double var23 = -var15 / var17;
            double var25 = var13 / var17;
            var19 = var23 * this.currentOffsetH;
            var21 = var25 * this.currentOffsetH;
         }

         double var30 = entity.method_23318() + 0.15;
         double var31 = entity.method_23318() + entity.method_17682() - 0.15;
         double var27 = class_3532.method_15350(var7 + this.currentOffsetV, var30, var31);
         return new class_243(var4.field_1352 + var19, var27, var4.field_1350 + var21);
      } else {
         return new class_243(var4.field_1352, var7, var4.field_1350);
      }
   }

   boolean isCrosshairOnTarget(class_1297 entity) {
      if (mc == null || mc.field_1724 == null || entity == null) {
         return false;
      }

      if (mc.field_1692 == entity) {
         return true;
      }

      class_243 var2 = mc.field_1724.method_33571();
      class_243 var3 = mc.field_1724.method_5828(1.0F);
      double var4 = (Double)this.range.get() + (this.stickyTarget.get() ? 0.5 : 0.0);
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

      if (!(Boolean)this.ignoreWalls.get() && mc.field_1687 != null) {
         class_3965 var10 = mc.field_1687.method_17742(new class_3959(var2, var6, class_3960.field_17558, class_242.field_1348, mc.field_1724));
         if (var10 != null && var10.method_17783() != class_240.field_1333) {
            return var10.method_17784().method_1025(var2) > ((class_243)var9.get()).method_1025(var2);
         }
      }

      return true;
   }

   private boolean isValidTarget(class_1297 entity, double maxRange, double maxFov) {
      if (entity != null && entity != mc.field_1724 && entity.method_5805()) {
         if (entity instanceof class_1657 var6) {
            if (!(Boolean)this.targetPlayers.get()) {
               return false;
            }

            if (var6.method_7325()) {
               return false;
            }

            if (!this.shouldAttack(var6)) {
               return false;
            }
         } else {
            if (!(entity instanceof class_1309)) {
               return false;
            }

            if (!(Boolean)this.targetMobs.get()) {
               return false;
            }
         }

         double var8 = entity.method_5858(mc.field_1724);
         if (var8 > maxRange * maxRange) {
            return false;
         } else {
            return !this.isInFov(entity, maxFov) ? false : (Boolean)this.ignoreWalls.get() || mc.field_1724.method_6057(entity);
         }
      } else {
         return false;
      }
   }

   private double getYawDeltaToEntity(class_1297 entity) {
      if (mc.field_1724 != null && entity != null) {
         double var2 = entity.method_23317() - mc.field_1724.method_23317();
         double var4 = entity.method_23321() - mc.field_1724.method_23321();
         double var6 = Math.toDegrees(Math.atan2(var4, var2)) - 90.0;
         return class_3532.method_15338(var6 - mc.field_1724.method_36454());
      } else {
         return 0.0;
      }
   }

   private boolean isInFov(class_1297 entity, double fovAngle) {
      return fovAngle >= 360.0 ? true : Math.abs(this.getYawDeltaToEntity(entity)) <= fovAngle / 2.0;
   }

   private boolean isTriggerActive() {
      int var1 = (Integer)this.triggerBind.get();
      return var1 == (-1) || var1 == 0 || KeyUtils.isKeyPressed(var1);
   }

   private boolean isLeftClickHeld() {
      return mc.method_22683() == null
         ? false
         : GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 0) == 1 || mc.field_1690 != null && mc.field_1690.field_1886.method_1434();
   }

   private boolean isHoldingWeapon() {
      if (mc.field_1724 == null) {
         return false;
      }

      class_1799 var1 = mc.field_1724.method_6047();
      if (var1 == null || var1.method_7960()) {
         return false;
      }

      if (var1.method_57826(class_9334.field_55878)) {
         return true;
      }

      class_1792 var2 = var1.method_7909();
      if (!(var2 instanceof class_1743) && !var2.equals(class_1802.field_49814)) {
         String var3 = var2.method_7876().toLowerCase();
         byte[] var10001 = new byte[5];
         var10001[0] = (byte)(117);
         var10001[1] = (byte)(-19);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-84);
         var10001[4] = (byte)(-121);
         if (!var3.contains($$v_s(var10001, 7741029539781005347L))) {
            var10001 = new byte[3];
            var10001[0] = (byte)(103);
            var10001[1] = (byte)(-30);
            var10001[2] = (byte)(121);
            if (!var3.contains($$v_s(var10001, 7741029539781005347L))) {
               var10001 = new byte[4];
               var10001[0] = (byte)(107);
               var10001[1] = (byte)(-5);
               var10001[2] = (byte)(127);
               var10001[3] = (byte)(-69);
               if (!var3.contains($$v_s(var10001, 7741029539781005347L))) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }

   private boolean shouldAttack(class_1657 player) {
      return true;
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (mc.field_1724 != null && mc.field_1687 != null && (Boolean)this.drawFov.get()) {
         double var2 = ((Integer)this.fov.get()).intValue();
         if (!(var2 >= 180.0)) {
            int var4 = mc.method_22683().method_4486();
            int var5 = mc.method_22683().method_4502();
            if (var4 > 0 && var5 > 0) {
               float var6 = var4 / 2.0F;
               float var7 = var5 / 2.0F;
               double var8 = mc.field_1690 != null && mc.field_1690.method_41808() != null
                  ? ((Integer)mc.field_1690.method_41808().method_41753()).intValue()
                  : 70.0;
               if (var8 <= 0.0) {
                  var8 = 70.0;
               }

               double var10 = Math.toRadians(var2 / 2.0);
               double var12 = Math.toRadians(var8 / 2.0);
               float var14 = (float)(Math.tan(var10) / Math.tan(var12) * (var5 / 2.0));
               if (!(var14 <= 0.0F)) {
                  boolean var15 = this.target != null && this.target.method_5805() && this.isValidTarget(this.target, (Double)this.range.get() + 0.5, var2);
                  int var16 = var15 ? (Integer)this.targetColor.get() : (Integer)this.fovColor.get();
                  Color var17 = new Color(var16, true);

                  try (NanoVGFrame var18 = NanoVGRenderer.frame()) {
                     if (!var18.isActive()) {
                        return;
                     }

                     NanoVGRenderer.drawCircleOutline(var6, var7, var14, 1.5F, var17);
                  }
               }
            }
         }
      }
   }

   @Override
   public String getInfo() {
      return this.target != null ? this.target.method_5477().getString() : null;
   }

   public AimAssist.Priority getPriority() {
      return (AimAssist.Priority)this.priority.get();
   }

   public ModeSetting getPrioritySetting() {
      return this.priority;
   }

   float getVelocityYaw() {
      return this.velocityYaw;
   }

   float getVelocityPitch() {
      return this.velocityPitch;
   }

   float getResidualYaw() {
      return this.residualYaw;
   }

   float getResidualPitch() {
      return this.residualPitch;
   }

   void setVelocityYawForTest(float v) {
      this.velocityYaw = v;
   }

   void setVelocityPitchForTest(float v) {
      this.velocityPitch = v;
   }

   void setResidualYawForTest(float r) {
      this.residualYaw = r;
   }

   void setResidualPitchForTest(float r) {
      this.residualPitch = r;
   }

   void resetMotionStateForTest() {
      this.resetMotionState();
   }

   public BoolSetting getAccelerateSetting() {
      return this.accelerate;
   }

   public DoubleSetting getMaxAccelerationSetting() {
      return this.maxAcceleration;
   }

   public BoolSetting getFovScalingSetting() {
      return this.fovScaling;
   }

   float calculateDesiredSpeedForTest(float diffAngle, float maxSpeed) {
      return this.calculateDesiredSpeed(diffAngle, maxSpeed);
   }

   float computeAngleFactorForTest(float diffAngle) {
      return this.computeAngleFactor(diffAngle);
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

   public enum Priority {
      CROSSHAIR,
      DISTANCE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AimAssist.Priority[] $values() {
         AimAssist.Priority[] var10000 = new AimAssist.Priority[2];
         var10000[0] = CROSSHAIR;
         var10000[1] = DISTANCE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[9];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(-108);
         var10002[5] = (byte)(89);
         var10002[6] = (byte)(-47);
         var10002[7] = (byte)(86);
         var10002[8] = (byte)(3);
         CROSSHAIR = new AimAssist.Priority();
         var10002 = new byte[8];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-122);
         var10002[5] = (byte)(95);
         var10002[6] = (byte)(-45);
         var10002[7] = (byte)(90);
         DISTANCE = new AimAssist.Priority();
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

   public enum TargetPoint {
      CHEST,
      HEAD,
      BODY,
      NEAREST;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AimAssist.TargetPoint[] $values() {
         AimAssist.TargetPoint[] var10000 = new AimAssist.TargetPoint[4];
         var10000[0] = CHEST;
         var10000[1] = HEAD;
         var10000[2] = BODY;
         var10000[3] = NEAREST;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-46);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(-16);
         CHEST = new AimAssist.TargetPoint();
         var10002 = new byte[4];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-102);
         HEAD = new AimAssist.TargetPoint();
         var10002 = new byte[4];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(88);
         var10002[3] = (byte)(-121);
         BODY = new AimAssist.TargetPoint();
         var10002 = new byte[7];
         var10002[0] = (byte)(113);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(-31);
         var10002[5] = (byte)(-67);
         var10002[6] = (byte)(-37);
         NEAREST = new AimAssist.TargetPoint();
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
