package me.drake.vieclient.modules.combat;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.PreItemUseEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BindSetting;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.KeyUtils;
import net.minecraft.class_1802;
import net.minecraft.class_9334;
import org.lwjgl.glfw.GLFW;

public final class AnchorMacro extends Module {
   private final BindSetting triggerBind;
   private final DoubleSetting fov;
   private final BoolSetting drawFov;
   private final ColorSetting fovColor;
   private final ColorSetting targetColor;
   private final IntSetting switchDelay;
   private final IntSetting glowstoneDelay;
   private final IntSetting explodeDelay;
   private final IntSetting totemSlot;
   private final BoolSetting autoFillTotem;
   private final BoolSetting randomDelay;
   private final BoolSetting humanize;
   private final BoolSetting debug;
   private final AnchorCombatState combatState = new AnchorCombatState();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AnchorMacro() {
      super(
         "Anchor Macro",
         "Automatically blows up respawn anchors for you",
         Category.COMBAT,
         -1
      );
      this.triggerBind = (BindSetting)this.addSetting(
         new BindSetting(
            "Trigger Bind",
            "Key to trigger anchor macro (-1 = Always active)",
            -1
         )
      );
      this.fov = (DoubleSetting)this.addSetting(
         new DoubleSetting(
            "FOV",
            "Target acquisition FOV in degrees",
            90.0,
            10.0,
            360.0,
            5.0
         )
      );
      this.drawFov = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Draw FOV",
            "Render FOV circle on screen (when FOV < 180°)",
            true
         )
      );
      this.fovColor = (ColorSetting)this.addSetting(
         new ColorSetting(
            "FOV Color",
            "Color of the FOV circle",
            -2130728448
         )
      );
      this.targetColor = (ColorSetting)this.addSetting(
         new ColorSetting(
            "Target Color",
            "Color of the FOV circle when an anchor is in FOV",
            -1427159996
         )
      );
      this.switchDelay = (IntSetting)this.addSetting(
         new IntSetting(
            "Switch Delay",
            "Delay between switches",
            1,
            0,
            20,
            1
         )
      );
      this.glowstoneDelay = (IntSetting)this.addSetting(
         new IntSetting(
            "Glowstone Delay",
            "Delay before glowstone",
            1,
            0,
            20,
            1
         )
      );
      this.explodeDelay = (IntSetting)this.addSetting(
         new IntSetting(
            "Explode Delay",
            "Delay before explode",
            1,
            0,
            20,
            1
         )
      );
      this.totemSlot = (IntSetting)this.addSetting(
         new IntSetting(
            "Totem Slot",
            "Totem slot",
            9,
            1,
            9,
            1
         )
      );
      this.autoFillTotem = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Auto Fill Totem",
            "Move an available totem into the configured slot before exploding",
            true
         )
      );
      this.randomDelay = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Random Delay",
            "Randomly vary delay",
            true
         )
      );
      this.humanize = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Humanize",
            "Natural safe rotation with Mouse GCD quantization",
            true
         )
      );
      this.debug = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Debug",
            "Log state transitions to console",
            false
         )
      );
      this.debug.setVisibleInGui(false);
      this.randomDelay.setVisibleInGui(false);
      this.humanize.setVisibleInGui(false);
      this.fovColor.visibleWhen(this.drawFov::get);
      this.targetColor.visibleWhen(this.drawFov::get);
      this.totemSlot.visibleWhen(this.autoFillTotem::get);
   }

   public AnchorCombatState getCombatStateForTest() {
      return this.combatState;
   }

   @Override
   public void onEnable() {
      VieClientMod.getInstance().getEventBus().subscribe(this);
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      VieClientMod.getInstance().getEventBus().unsubscribe(this);
      this.reset();
      super.onDisable();
   }

   public void reset() {
      byte[] var10001 = new byte[29];
      var10001[0] = (byte)(100);
      var10001[1] = (byte)(-12);
      var10001[2] = (byte)(127);
      var10001[3] = (byte)(-74);
      var10001[4] = (byte)(-123);
      var10001[5] = (byte)(-63);
      var10001[6] = (byte)(-53);
      var10001[7] = (byte)(13);
      var10001[8] = (byte)(46);
      var10001[9] = (byte)(-48);
      var10001[10] = (byte)(-117);
      var10001[11] = (byte)(-42);
      var10001[12] = (byte)(-64);
      var10001[13] = (byte)(14);
      var10001[14] = (byte)(-35);
      var10001[15] = (byte)(113);
      var10001[16] = 1;
      var10001[17] = (byte)(106);
      var10001[18] = (byte)(-93);
      var10001[19] = (byte)(-36);
      var10001[20] = (byte)(122);
      var10001[21] = (byte)(39);
      var10001[22] = (byte)(-65);
      var10001[23] = (byte)(79);
      var10001[24] = (byte)(-4);
      var10001[25] = (byte)(112);
      var10001[26] = (byte)(-8);
      var10001[27] = (byte)(35);
      var10001[28] = (byte)(6);
      this.combatState.reset($$v_s(var10001, 4937472770980433920L));
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
   public void onTick(TickEvent tickEvent) {
      if (tickEvent.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
            this.reset();
         } else if (mc.field_1755 != null) {
            this.reset();
         } else if (!this.combatState.isBusy() && this.isShieldOrFoodActive()) {
            this.reset();
         } else {
            int var2 = (Integer)this.triggerBind.get();
            boolean var3 = var2 == -1 || var2 == 0 || KeyUtils.isKeyPressed(var2);
            if (!var3) {
               this.combatState
                  .reset(
                     "Trigger key not pressed"
                  );
            } else {
               int var4 = this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0;
               this.combatState.humanize = (Boolean)this.humanize.get();
               this.combatState.switchDelay = (Integer)this.switchDelay.get() + var4;
               this.combatState.chargeDelay = (Integer)this.glowstoneDelay.get() + var4;
               this.combatState.explodeDelay = (Integer)this.explodeDelay.get() + var4;
               this.combatState.totemSlot = (Integer)this.totemSlot.get();
               this.combatState.autoFillTotem = (Boolean)this.autoFillTotem.get();
               this.combatState.requireTotem = false;
               this.combatState.lookDownBlocks = 1.0;
               this.combatState.rotationSpeed = 8.0;
               this.combatState.autoExplode = true;
               this.combatState.debug = (Boolean)this.debug.get();
               this.combatState.fov = (Double)this.fov.get();
               this.combatState.tick(var3, false);
            }
         }
      }
   }

   private boolean isShieldOrFoodActive() {
      if (mc.field_1724 == null) {
         return false;
      }

      boolean var1 = mc.field_1724.method_6047().method_57826(class_9334.field_50075) || mc.field_1724.method_6079().method_57826(class_9334.field_50075);
      boolean var2 = mc.field_1724.method_6047().method_31574(class_1802.field_8255) || mc.field_1724.method_6079().method_31574(class_1802.field_8255);
      boolean var3 = mc.method_22683() != null && GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 1) == 1;
      return (var1 || var2) && var3;
   }

   @EventHandler
   public void onItemUse(PreItemUseEvent event) {
      if (this.combatState.isBusy()) {
         event.cancel();
      }
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
