package me.drake.vieclient.modules.movement;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.modules.combat.AutoSwordBotting;
import me.drake.vieclient.modules.exploit.Freecam;
import net.minecraft.class_3532;
import net.minecraft.class_5498;
import org.lwjgl.glfw.GLFW;

public final class FreeLook extends Module {
   private final EnumSetting mode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Mode",
         "FreeLook rotation mode.",
         FreeLook.Mode.CAMERA
      )
   );
   private final BoolSetting togglePerspective = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Toggle Perspective",
         "Switch to third person when enabled.",
         true
      )
   );
   private final BoolSetting scrollDistance = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Scroll Distance",
         "Scroll to adjust third-person camera distance.",
         true
      )
   );
   private final BoolSetting noClip = (BoolSetting)this.addSetting(
      new BoolSetting(
         "See Through Walls",
         "Let the camera pass through blocks instead of being pushed in.",
         true
      )
   );
   private final BoolSetting advanced = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Advanced Settings",
         "Show advanced animation and camera settings.",
         false
      )
   );
   private final BoolSetting zoomTransition = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Zoom Transition",
         "Smooth zoom-out animation when turning on Free Look.",
         true
      )
   );
   private final DoubleSetting zoomDistance = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Zoom Distance",
         "Target camera distance when zooming out on enable.",
         256.0,
         4.0,
         256.0,
         1.0
      )
   );
   private final DoubleSetting transitionDuration = (DoubleSetting)this.addSetting(
      (DoubleSetting)hidden(
         new DoubleSetting(
            "Transition Time",
            "Duration of the zoom-out animation in seconds.",
            0.35,
            0.1,
            1.5,
            0.05
         )
      )
   );
   private final EnumSetting easing = (EnumSetting)this.addSetting(
      (EnumSetting)hidden(
         new EnumSetting(
               "Easing",
               "Animation curve for the zoom-out effect.",
               FreeLook.Easing.QUART
            )
            .withDisplayName(
               FreeLook.Easing.QUART, "Quart (Smooth)"
            )
            .withDisplayName(FreeLook.Easing.EXPO, "Expo (Snappy)")
            .withDisplayName(
               FreeLook.Easing.CUBIC, "Cubic (Natural)"
            )
            .withDisplayName(
               FreeLook.Easing.BACK, "Back (Overshoot)"
            )
            .withDisplayName(FreeLook.Easing.SMOOTH, "Sine (Gentle)")
      )
   );
   private final BoolSetting dynamicFov = (BoolSetting)this.addSetting(
      (BoolSetting)hidden(
         new BoolSetting(
            "Dynamic FOV",
            "Subtle cinematic FOV widening during zoom-out.",
            false
         )
      )
   );
   private final DoubleSetting fovBump = (DoubleSetting)this.addSetting(
      (DoubleSetting)hidden(
         new DoubleSetting(
            "FOV Bump",
            "Maximum FOV expansion during zoom-out.",
            6.0,
            0.0,
            20.0,
            0.5
         )
      )
   );
   private final DoubleSetting minDistance = (DoubleSetting)this.addSetting(
      (DoubleSetting)hidden(
         new DoubleSetting(
            "Min Distance",
            "Minimum camera distance.",
            1.0,
            0.1,
            10.0,
            0.1
         )
      )
   );
   private final DoubleSetting maxDistance = (DoubleSetting)this.addSetting(
      (DoubleSetting)hidden(
         new DoubleSetting(
            "Max Distance",
            "Maximum camera distance.",
            256.0,
            4.0,
            256.0,
            1.0
         )
      )
   );
   private final DoubleSetting scrollStep = (DoubleSetting)this.addSetting(
      (DoubleSetting)hidden(
         new DoubleSetting(
            "Scroll Step",
            "Distance change per scroll tick.",
            1.0,
            0.1,
            10.0,
            0.1
         )
      )
   );
   private final BoolSetting smoothScroll = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Smooth Zoom",
         "Smooth interpolation when scrolling camera distance.",
         true
      )
   );
   private final DoubleSetting smoothScrollSpeed = (DoubleSetting)this.addSetting(
      (DoubleSetting)hidden(
         new DoubleSetting(
            "Zoom Smoothing",
            "Speed of smooth scroll zooming.",
            14.0,
            4.0,
            30.0,
            1.0
         )
      )
   );
   private final BoolSetting smoothLook = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Smooth Camera",
         "Cinematic mouse look smoothing.",
         false
      )
   );
   private final DoubleSetting smoothLookSpeed = (DoubleSetting)this.addSetting(
      (DoubleSetting)hidden(
         new DoubleSetting(
            "Look Smoothing",
            "Smooth camera rotation speed.",
            18.0,
            4.0,
            40.0,
            1.0
         )
      )
   );
   private final DoubleSetting sensitivity = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Sensitivity",
         "Mouse sensitivity multiplier.",
         1.0,
         1.0,
         10.0,
         0.1
      )
   );
   private final BoolSetting arrows = (BoolSetting)this.addSetting(
      (BoolSetting)hidden(
         new BoolSetting(
            "Arrows Control Opposite",
            "Arrow keys rotate opposite target.",
            true
         )
      )
   );
   private final DoubleSetting arrowSpeed = (DoubleSetting)this.addSetting(
      (DoubleSetting)hidden(
         new DoubleSetting(
            "Arrow Speed",
            "Arrow rotation speed.",
            10.0,
            4.0,
            10.0,
            0.1
         )
      )
   );
   public float cameraYaw;
   public float cameraPitch;
   private float targetYaw;
   private float targetPitch;
   private float cameraDistance = 256.0F;
   private float currentDistance = 256.0F;
   private float startDistance = 0.0F;
   private boolean isTransitioning = false;
   private long transitionStartNanos = 0L;
   private long lastFrameNanos = 0L;
   private class_5498 prePerspective = class_5498.field_26664;
   private boolean previousChunkCulling = true;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FreeLook() {
      super(
         "Free Look",
         "Allows more rotation options in third person.",
         Category.MOVEMENT
      );
      this.zoomTransition.visibleWhen(this.advanced::get);
      this.zoomDistance.visibleWhen(() -> (Boolean)this.advanced.get() && (Boolean)this.zoomTransition.get());
      this.smoothScroll.visibleWhen(() -> (Boolean)this.advanced.get() && (Boolean)this.scrollDistance.get());
      this.smoothLook.visibleWhen(this.advanced::get);
      this.sensitivity.visibleWhen(this.advanced::get);
      this.noClip.onChanged(enabled -> {
         if (this.isEnabled() && mc != null) {
            if (enabled) {
               mc.field_1730 = false;
            } else {
               mc.field_1730 = this.previousChunkCulling;
            }
         }
      });
   }

   private static Setting hidden(Setting setting) {
      setting.setVisibleInGui(false);
      return setting;
   }

   public static float ease(FreeLook.Easing easing, float t) {
      t = class_3532.method_15363(t, 0.0F, 1.0F);
      switch (easingx) {
         case QUART:
         default:
            float var7 = 1.0F - t;
            return 1.0F - var7 * var7 * var7 * var7;
         case EXPO:
            return t >= 1.0F ? 1.0F : 1.0F - (float)Math.pow(2.0, -10.0 * t);
         case CUBIC:
            float var6 = 1.0F - t;
            return 1.0F - var6 * var6 * var6;
         case BACK:
            float var2 = 1.25F;
            float var3 = var2 + 1.0F;
            float var4 = t - 1.0F;
            return 1.0F + var3 * var4 * var4 * var4 + var2 * var4 * var4;
         case SMOOTH:
            return 0.5F - 0.5F * (float)Math.cos(t * Math.PI);
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (mc != null && mc.field_1724 != null && mc.field_1690 != null) {
         this.cameraYaw = mc.field_1724.method_36454();
         this.cameraPitch = mc.field_1724.method_36455();
         this.targetYaw = this.cameraYaw;
         this.targetPitch = this.cameraPitch;
         this.cameraDistance = ((Double)this.zoomDistance.get()).floatValue();
         this.cameraDistance = class_3532.method_15363(
            this.cameraDistance, ((Double)this.minDistance.get()).floatValue(), ((Double)this.maxDistance.get()).floatValue()
         );
         this.prePerspective = mc.field_1690.method_31044();
         if (this.prePerspective != class_5498.field_26665 && (Boolean)this.togglePerspective.get()) {
            mc.field_1690.method_31043(class_5498.field_26665);
         }

         if ((Boolean)this.zoomTransition.get()) {
            this.isTransitioning = true;
            this.transitionStartNanos = System.nanoTime();
            this.lastFrameNanos = this.transitionStartNanos;
            this.startDistance = this.prePerspective == class_5498.field_26664 ? 0.0F : Math.min(4.0F, this.cameraDistance);
            this.currentDistance = this.startDistance;
         } else {
            this.isTransitioning = false;
            this.currentDistance = this.cameraDistance;
            this.lastFrameNanos = System.nanoTime();
         }

         this.previousChunkCulling = mc.field_1730;
         if ((Boolean)this.noClip.get()) {
            mc.field_1730 = false;
         }
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.isTransitioning = false;
      if (mc != null && mc.field_1724 != null && mc.field_1690 != null) {
         if (mc.field_1690.method_31044() != this.prePerspective && (Boolean)this.togglePerspective.get()) {
            mc.field_1690.method_31043(this.prePerspective);
         }

         if (this.previousChunkCulling != mc.field_1730) {
            VieClientMod var1 = VieClientMod.getInstance();
            Freecam var2 = var1 != null && var1.getModuleManager() != null ? (Freecam)var1.getModuleManager().getModule(Freecam.class) : null;
            if (var2 == null || !var2.isEnabled()) {
               mc.field_1730 = this.previousChunkCulling;
            }
         }
      }
   }

   public void onCameraFrame() {
      if (this.isEnabled()) {
         long var1 = System.nanoTime();
         double var3 = this.lastFrameNanos > 0L ? (var1 - this.lastFrameNanos) / 1.0E9 : 0.016;
         var3 = class_3532.method_15350(var3, 5.0E-4, 0.05);
         this.lastFrameNanos = var1;
         if (this.isTransitioning) {
            long var5 = var1 - this.transitionStartNanos;
            double var7 = Math.max(0.01, (Double)this.transitionDuration.get());
            double var9 = var5 / (var7 * 1.0E9);
            if (var9 >= 1.0) {
               this.isTransitioning = false;
               this.currentDistance = this.cameraDistance;
            } else {
               float var11 = ease((FreeLook.Easing)this.easing.get(), (float)var9);
               this.currentDistance = this.startDistance + (this.cameraDistance - this.startDistance) * var11;
            }
         } else if ((Boolean)this.smoothScroll.get()) {
            float var13 = 1.0F - (float)Math.exp(-(Double)this.smoothScrollSpeed.get() * var3);
            this.currentDistance = this.currentDistance + (this.cameraDistance - this.currentDistance) * var13;
         } else {
            this.currentDistance = this.cameraDistance;
         }

         if ((Boolean)this.smoothLook.get()) {
            float var14 = 1.0F - (float)Math.exp(-(Double)this.smoothLookSpeed.get() * var3);
            float var6 = class_3532.method_15393(this.targetYaw - this.cameraYaw);
            this.cameraYaw += var6 * var14;
            this.cameraPitch = this.cameraPitch + (this.targetPitch - this.cameraPitch) * var14;
            this.cameraYaw = class_3532.method_15393(this.cameraYaw);
            this.cameraPitch = class_3532.method_15363(this.cameraPitch, -90.0F, 90.0F);
         }
      }
   }

   public float getFovModifier() {
      if (this.isEnabled() && (Boolean)this.zoomTransition.get() && (Boolean)this.dynamicFov.get()) {
         float var1 = ((Double)this.fovBump.get()).floatValue();
         if (!this.isTransitioning) {
            return var1;
         }

         long var2 = System.nanoTime();
         long var4 = var2 - this.transitionStartNanos;
         double var6 = Math.max(0.01, (Double)this.transitionDuration.get());
         double var8 = var4 / (var6 * 1.0E9);
         if (var8 >= 1.0) {
            return var1;
         }

         float var10 = ease((FreeLook.Easing)this.easing.get(), (float)var8);
         return var10 * var1;
      } else {
         return 0.0F;
      }
   }

   public float getRenderYaw() {
      return this.cameraYaw;
   }

   public float getRenderPitch() {
      return this.cameraPitch;
   }

   public boolean playerMode() {
      return this.isEnabled()
         && mc != null
         && mc.field_1690 != null
         && mc.field_1690.method_31044() == class_5498.field_26665
         && this.mode.get() == FreeLook.Mode.PLAYER;
   }

   public boolean cameraMode() {
      return this.isEnabled() && this.mode.get() == FreeLook.Mode.CAMERA;
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.POST) {
         if (mc != null && mc.field_1724 != null && mc.method_22683() != null) {
            if ((Boolean)this.arrows.get()) {
               long var2 = mc.method_22683().method_4490();
               int var4 = (int)((Double)this.arrowSpeed.get() * 2.0);
               boolean var5 = this.isCombatBotActive();
               boolean var6 = !var5 && this.mode.get() != FreeLook.Mode.PLAYER;
               boolean var7 = var6 && ActionArbiter.get().tryAcquire(ActionArbiter.Resource.CAMERA, ActionArbiter.Owner.FREELOOK);

               for (int var8 = 0; var8 < var4; var8++) {
                  if (this.mode.get() != FreeLook.Mode.PLAYER && !var5) {
                     if (var7) {
                        float var9 = mc.field_1724.method_36454();
                        float var10 = mc.field_1724.method_36455();
                        if (isKeyPressed(var2, 263)) {
                           var9 -= 0.5F;
                        }

                        if (isKeyPressed(var2, 262)) {
                           var9 += 0.5F;
                        }

                        if (isKeyPressed(var2, 265)) {
                           var10 -= 0.5F;
                        }

                        if (isKeyPressed(var2, 264)) {
                           var10 += 0.5F;
                        }

                        mc.field_1724.method_36456(var9);
                        mc.field_1724.method_36457(var10);
                     }
                  } else {
                     if (isKeyPressed(var2, 263)) {
                        this.cameraYaw -= 0.5F;
                        this.targetYaw = this.cameraYaw;
                     }

                     if (isKeyPressed(var2, 262)) {
                        this.cameraYaw += 0.5F;
                        this.targetYaw = this.cameraYaw;
                     }

                     if (isKeyPressed(var2, 265)) {
                        this.cameraPitch -= 0.5F;
                        this.targetPitch = this.cameraPitch;
                     }

                     if (isKeyPressed(var2, 264)) {
                        this.cameraPitch += 0.5F;
                        this.targetPitch = this.cameraPitch;
                     }
                  }
               }

               if (var7) {
                  mc.field_1724.method_36457(class_3532.method_15363(mc.field_1724.method_36455(), -90.0F, 90.0F));
               }

               this.cameraPitch = class_3532.method_15363(this.cameraPitch, -90.0F, 90.0F);
               this.targetPitch = class_3532.method_15363(this.targetPitch, -90.0F, 90.0F);
            }
         }
      }
   }

   public void changeLookDirection(double deltaX, double deltaY) {
      double var5 = (Double)this.sensitivity.get() * 0.5;
      if ((Boolean)this.smoothLook.get()) {
         this.targetYaw += (float)(deltaX * var5);
         this.targetPitch += (float)(deltaY * var5);
         this.targetYaw = class_3532.method_15393(this.targetYaw);
         this.targetPitch = class_3532.method_15363(this.targetPitch, -90.0F, 90.0F);
      } else {
         this.cameraYaw += (float)(deltaX * var5);
         this.cameraPitch += (float)(deltaY * var5);
         this.cameraYaw = class_3532.method_15393(this.cameraYaw);
         this.cameraPitch = class_3532.method_15363(this.cameraPitch, -90.0F, 90.0F);
         this.targetYaw = this.cameraYaw;
         this.targetPitch = this.cameraPitch;
      }
   }

   public void applyMouseDelta(float deltaYaw, float deltaPitch) {
      this.changeLookDirection(deltaYaw, deltaPitch);
   }

   public boolean handleMouseScroll(double amount) {
      if (!this.isEnabled()) {
         return false;
      } else if (mc != null && mc.field_1755 != null) {
         return false;
      } else if (!(Boolean)this.scrollDistance.get()) {
         return false;
      } else {
         return mc != null && mc.field_1690 != null && mc.field_1690.method_31044() == class_5498.field_26664 ? false : this.applyScroll(amount);
      }
   }

   public boolean applyScroll(double amount) {
      double var3 = (Double)this.scrollStep.get();
      double var5 = var3;
      if (this.cameraDistance > 12.0F) {
         var5 = var3 * (1.0 + (this.cameraDistance - 12.0F) * 0.06F);
      }

      this.cameraDistance -= (float)(amount * var5);
      this.cameraDistance = class_3532.method_15363(
         this.cameraDistance, ((Double)this.minDistance.get()).floatValue(), ((Double)this.maxDistance.get()).floatValue()
      );
      if (!(Boolean)this.smoothScroll.get() && !this.isTransitioning) {
         this.currentDistance = this.cameraDistance;
      }

      return true;
   }

   public float getCameraDistance() {
      return !this.isEnabled() ? this.cameraDistance : this.currentDistance;
   }

   public void setCameraDistance(float distance) {
      this.cameraDistance = class_3532.method_15363(distance, ((Double)this.minDistance.get()).floatValue(), ((Double)this.maxDistance.get()).floatValue());
      this.currentDistance = this.cameraDistance;
   }

   public DoubleSetting getMinDistanceSetting() {
      return this.minDistance;
   }

   public DoubleSetting getMaxDistanceSetting() {
      return this.maxDistance;
   }

   public DoubleSetting getScrollStepSetting() {
      return this.scrollStep;
   }

   public BoolSetting getZoomTransitionSetting() {
      return this.zoomTransition;
   }

   public DoubleSetting getZoomDistanceSetting() {
      return this.zoomDistance;
   }

   public DoubleSetting getTransitionDurationSetting() {
      return this.transitionDuration;
   }

   public EnumSetting getEasingSetting() {
      return this.easing;
   }

   public BoolSetting getDynamicFovSetting() {
      return this.dynamicFov;
   }

   public DoubleSetting getFovBumpSetting() {
      return this.fovBump;
   }

   public BoolSetting getSmoothScrollSetting() {
      return this.smoothScroll;
   }

   public BoolSetting getSmoothLookSetting() {
      return this.smoothLook;
   }

   public BoolSetting getAdvancedSetting() {
      return this.advanced;
   }

   public EnumSetting getModeSetting() {
      return this.mode;
   }

   public BoolSetting getTogglePerspectiveSetting() {
      return this.togglePerspective;
   }

   public BoolSetting getScrollDistanceSetting() {
      return this.scrollDistance;
   }

   public BoolSetting getNoClipSetting() {
      return this.noClip;
   }

   public DoubleSetting getSensitivitySetting() {
      return this.sensitivity;
   }

   public BoolSetting getArrowsSetting() {
      return this.arrows;
   }

   public DoubleSetting getArrowSpeedSetting() {
      return this.arrowSpeed;
   }

   public boolean isTransitioning() {
      return this.isTransitioning;
   }

   public boolean isNoClip() {
      return this.isEnabled() && mc != null && mc.field_1690 != null && mc.field_1690.method_31044() != class_5498.field_26664 && (Boolean)this.noClip.get();
   }

   public boolean isPlayerMode() {
      return this.playerMode();
   }

   public boolean isCameraMode() {
      return this.cameraMode();
   }

   public boolean isRenderAllActive() {
      return this.isEnabled()
         && mc != null
         && mc.field_1690 != null
         && mc.field_1690.method_31044() != class_5498.field_26664
         && (this.cameraMode() || (Boolean)this.noClip.get());
   }

   private boolean isCombatBotActive() {
      VieClientMod var1 = VieClientMod.getInstance();
      if (var1 != null && var1.getModuleManager() != null) {
         AutoSwordBotting var2 = (AutoSwordBotting)var1.getModuleManager().getModule(AutoSwordBotting.class);
         return var2 != null && var2.isEnabled();
      } else {
         return false;
      }
   }

   private static boolean isKeyPressed(long window, int key) {
      return GLFW.glfwGetKey(window, key) == 1;
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

   public enum Easing {
      QUART,
      EXPO,
      CUBIC,
      BACK,
      SMOOTH;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static FreeLook.Easing[] $values() {
         FreeLook.Easing[] var10000 = new FreeLook.Easing[5];
         var10000[0] = QUART;
         var10000[1] = EXPO;
         var10000[2] = CUBIC;
         var10000[3] = BACK;
         var10000[4] = SMOOTH;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(-26);
         QUART = new FreeLook.Easing();
         var10002 = new byte[4];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-62);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-111);
         EXPO = new FreeLook.Easing();
         var10002 = new byte[5];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(94);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-15);
         CUBIC = new FreeLook.Easing();
         var10002 = new byte[4];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(95);
         var10002[3] = (byte)(-107);
         BACK = new FreeLook.Easing();
         var10002 = new byte[6];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-41);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-111);
         var10002[4] = (byte)(-26);
         var10002[5] = (byte)(-127);
         SMOOTH = new FreeLook.Easing();
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

   public enum Mode {
      PLAYER,
      CAMERA;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static FreeLook.Mode[] $values() {
         FreeLook.Mode[] var10000 = new FreeLook.Mode[2];
         var10000[0] = PLAYER;
         var10000[1] = CAMERA;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-121);
         var10002[4] = (byte)(111);
         var10002[5] = (byte)(2);
         PLAYER = new FreeLook.Mode();
         var10002 = new byte[6];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(120);
         var10002[5] = (byte)(17);
         CAMERA = new FreeLook.Mode();
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
