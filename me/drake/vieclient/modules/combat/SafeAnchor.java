package me.drake.vieclient.modules.combat;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.PlayerMovementTickEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.PreItemUseEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.ServerBlockUpdateEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BindSetting;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.ModeSetting;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.KeyUtils;

public class SafeAnchor extends Module {
   private final ModeSetting bypassMode = (ModeSetting)this.addSetting(
      new ModeSetting(
         "Bypass Mode",
         "Anticheat preset: GRIM_AC (strict reach/GCD), NORMAL (balanced), FAST (maximum burst)",
         AnchorCombatState.BypassMode.NORMAL,
         AnchorCombatState.BypassMode.class
      )
   );
   private final ModeSetting protectionModeSetting = (ModeSetting)this.addSetting(
      new ModeSetting(
         "Protection Mode",
         "SMART: safe fallback if blocked | ALWAYS: strictly require block | OFF: raw burst",
         AnchorCombatState.ProtectionMode.SMART,
         AnchorCombatState.ProtectionMode.class
      )
   );
   private final DoubleSetting fov = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "FOV",
         "Target acquisition FOV in degrees",
         90.0,
         10.0,
         360.0,
         5.0
      )
   );
   private final BoolSetting drawFov = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Draw FOV",
         "Render FOV circle on screen (when FOV < 180°)",
         true
      )
   );
   private final ColorSetting fovColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "FOV Color",
         "Color of the FOV circle",
         -2141869833
      )
   );
   private final ColorSetting targetColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Target Color",
         "Color of the FOV circle when an anchor is in FOV",
         -1427159996
      )
   );
   private final BoolSetting checkOffhandTotem = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Offhand Totem",
         "Skip totem switch when holding Totem of Undying in offhand for instant detonation",
         true
      )
   );
   private final BoolSetting fastSequence = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Fast Sequence",
         "Prepare aim while waiting for server updates; keep one tick between slot changes and use",
         true
      )
   );
   private final BindSetting triggerBind = (BindSetting)this.addSetting(
      new BindSetting(
         "Trigger Bind",
         "Key to activate (-1 = Always active)",
         -1
      )
   );
   private final IntSetting switchDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "Switch Delay",
         "Delay (ticks) before switching items",
         2,
         0,
         20,
         1
      )
   );
   private final DoubleSetting range = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Range",
         "Maximum distance to place anchor",
         4.5,
         3.0,
         6.0
      )
   );
   private final IntSetting glowstoneDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "Glowstone Delay",
         "Delay before placing/charging glowstone",
         0,
         0,
         20,
         1
      )
   );
   private final BoolSetting autoExplode = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Auto Explode",
         "Automatically explode anchor after charging",
         true
      )
   );
   private final IntSetting explodeDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "Explode Delay",
         "Delay before exploding",
         0,
         0,
         20,
         1
      )
   );
   private final IntSetting totemSlot = (IntSetting)this.addSetting(
      new IntSetting(
         "Totem Slot",
         "Hotbar slot for totem (1-9)",
         9,
         1,
         9,
         1
      )
   );
   private final BoolSetting autoFillTotem = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Auto Fill Totem",
         "Auto move totem from inventory to totem slot if empty",
         true
      )
   );
   private final BoolSetting followingCamera = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Following Camera",
         "Follow the actual placement and interaction faces",
         true
      )
   );
   private final BoolSetting humanize = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Humanize",
         "Smooth camera acceleration and sensitivity-aware movement",
         true
      )
   );
   private final DoubleSetting rotationSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "RotationSpeed",
         "Turn limit (deg/tick); Both modes stay smooth and capped at 360 deg/s",
         8.0,
         1.0,
         90.0
      )
   );
   private final BoolSetting randomDelay = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Random Delay",
         "Randomly vary delay",
         true
      )
   );
   private final AnchorCombatState combatState = new AnchorCombatState();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SafeAnchor() {
      super(
         "Safe Anchor",
         "Place anchor, place protection, charge and explode",
         Category.COMBAT,
         -1
      );
      this.fovColor.visibleWhen(this.drawFov::get);
      this.targetColor.visibleWhen(this.drawFov::get);
      this.switchDelay.visibleWhen(() -> !(Boolean)this.fastSequence.get() && this.bypassMode.get() != AnchorCombatState.BypassMode.FAST);
      this.glowstoneDelay.visibleWhen(() -> !(Boolean)this.fastSequence.get() && this.bypassMode.get() != AnchorCombatState.BypassMode.FAST);
      this.explodeDelay.visibleWhen(() -> !(Boolean)this.fastSequence.get() && this.bypassMode.get() != AnchorCombatState.BypassMode.FAST);
      this.rotationSpeed.visibleWhen(() -> !(Boolean)this.fastSequence.get() && this.bypassMode.get() != AnchorCombatState.BypassMode.FAST);
      this.randomDelay.setVisibleInGui(false);
      this.humanize.setVisibleInGui(false);
      this.autoExplode.setVisibleInGui(false);
      this.autoFillTotem.setVisibleInGui(false);
   }

   public AnchorCombatState getCombatStateForTest() {
      return this.combatState;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.reset();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.reset();
   }

   private void reset() {
      byte[] var10001 = new byte[18];
      var10001[0] = (byte)(67);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(122);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(-114);
      var10001[5] = (byte)(-50);
      var10001[6] = (byte)(-23);
      var10001[7] = (byte)(-67);
      var10001[8] = (byte)(23);
      var10001[9] = (byte)(-48);
      var10001[10] = (byte)(-60);
      var10001[11] = (byte)(-123);
      var10001[12] = (byte)(-29);
      var10001[13] = (byte)(23);
      var10001[14] = (byte)(-46);
      var10001[15] = (byte)(-35);
      var10001[16] = (byte)(37);
      var10001[17] = (byte)(46);
      this.combatState.reset($$v_s(var10001, -177471663784079307L));
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
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D) {
         this.combatState.onRender(event.getFrameTime());
      }
   }

   @EventHandler
   public void onItemUse(PreItemUseEvent event) {
      if (this.combatState.isBusy()) {
         event.cancel();
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         int var2 = (Integer)this.triggerBind.get();
         if (mc.field_1724 == null
            || mc.field_1687 == null
            || mc.field_1755 != null
            || mc.field_1724.method_5765()
            || mc.field_1724.method_6115()
            || var2 != (-1) && var2 != 0 && !KeyUtils.isKeyPressed(var2)) {
            this.reset();
         }
      }
   }

   @EventHandler
   public void onServerBlockUpdate(ServerBlockUpdateEvent event) {
      this.combatState.onServerBlockUpdate(event.getPos(), event.getState());
   }

   @EventHandler
   public void onMovementTick(PlayerMovementTickEvent event) {
      if (mc.field_1724 != null && mc.field_1724.method_5765()) {
         this.reset();
      } else {
         this.configureCombatState();
         int var2 = (Integer)this.triggerBind.get();
         this.combatState.tick(var2 == (-1) || var2 == 0 || KeyUtils.isKeyPressed(var2), true);
      }
   }

   void configureCombatState() {
      AnchorCombatState.BypassMode var1 = (AnchorCombatState.BypassMode)this.bypassMode.get();
      boolean var2 = (Boolean)this.fastSequence.get() || var1 == AnchorCombatState.BypassMode.FAST;
      this.combatState.bypassMode = var1;
      this.combatState.requireServerConfirmation = var1 != AnchorCombatState.BypassMode.FAST;
      this.combatState.pipelinePreparation = var2;
      this.combatState.followingCamera = var1 == AnchorCombatState.BypassMode.GRIM_AC || (Boolean)this.followingCamera.get();
      this.combatState.humanize = var1 == AnchorCombatState.BypassMode.GRIM_AC || (Boolean)this.humanize.get();
      this.combatState.rotationSpeed = var1 == AnchorCombatState.BypassMode.FAST ? 30.0 : (var2 ? 18.0 : (Double)this.rotationSpeed.get());
      int var3 = this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0;
      this.combatState.switchDelay = var2 ? 1 + var3 : Math.max(1, (Integer)this.switchDelay.get() + var3);
      this.combatState.placeDelay = var2 ? var3 : 2;
      double var4 = var1 == AnchorCombatState.BypassMode.GRIM_AC ? Math.min((Double)this.range.get(), 3.15) : (Double)this.range.get();
      this.combatState.maxPlaceReach = var4;
      this.combatState.chargeDelay = var2 ? var3 : (Integer)this.glowstoneDelay.get() + var3;
      this.combatState.explodeDelay = var2 ? var3 : (Integer)this.explodeDelay.get() + var3;
      this.combatState.autoExplode = (Boolean)this.autoExplode.get();
      this.combatState.totemSlot = (Integer)this.totemSlot.get();
      this.combatState.autoFillTotem = (Boolean)this.autoFillTotem.get();
      this.combatState.checkOffhandTotem = (Boolean)this.checkOffhandTotem.get();
      this.combatState.protectionMode = (AnchorCombatState.ProtectionMode)this.protectionModeSetting.get();
      this.combatState.placeProtection = this.protectionModeSetting.get() != AnchorCombatState.ProtectionMode.OFF;
      this.combatState.fov = (Double)this.fov.get();
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      this.renderFovCircle();
   }

   private void renderFovCircle() {
      if (mc.field_1724 != null && mc.field_1687 != null && (Boolean)this.drawFov.get()) {
         double var1 = (Double)this.fov.get();
         if (!(var1 >= 180.0)) {
            int var3 = mc.method_22683().method_4486();
            int var4 = mc.method_22683().method_4502();
            if (var3 > 0 && var4 > 0) {
               float var5 = var3 / 2.0F;
               float var6 = var4 / 2.0F;
               double var7 = mc.field_1690 != null && mc.field_1690.method_41808() != null
                  ? ((Integer)mc.field_1690.method_41808().method_41753()).intValue()
                  : 70.0;
               if (var7 <= 0.0) {
                  var7 = 70.0;
               }

               double var9 = Math.toRadians(var1 / 2.0);
               double var11 = Math.toRadians(var7 / 2.0);
               float var13 = (float)(Math.tan(var9) / Math.tan(var11) * (var4 / 2.0));
               if (!(var13 <= 0.0F)) {
                  boolean var14 = this.combatState.hasTargetInFov();
                  int var15 = var14 ? (Integer)this.targetColor.get() : (Integer)this.fovColor.get();
                  Color var16 = new Color(var15, true);
                  Color var17 = new Color(var16.getRed(), var16.getGreen(), var16.getBlue(), var16.getAlpha() / (3));

                  try (NanoVGFrame var18 = NanoVGRenderer.frame()) {
                     if (!var18.isActive()) {
                        return;
                     }

                     NanoVGRenderer.drawCircleOutline(var5, var6, var13, 3.0F, var17);
                     NanoVGRenderer.drawCircleOutline(var5, var6, var13, 1.5F, var16);
                  }
               }
            }
         }
      }
   }

   public DoubleSetting getFovSetting() {
      return this.fov;
   }

   public BoolSetting getDrawFovSetting() {
      return this.drawFov;
   }

   public ColorSetting getFovColorSetting() {
      return this.fovColor;
   }

   public ColorSetting getTargetColorSetting() {
      return this.targetColor;
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
