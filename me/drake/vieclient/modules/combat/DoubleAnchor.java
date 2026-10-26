package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.PreItemUseEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BindSetting;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.KeyUtils;

public final class DoubleAnchor extends Module {
   private final BindSetting triggerBind;
   private final IntSetting switchDelay;
   private final IntSetting totemSlot;
   private final IntSetting placeDelay;
   private final IntSetting glowstoneDelay;
   private final IntSetting explodeDelay;
   private final BoolSetting humanize;
   private final BoolSetting randomDelay;
   private final BoolSetting debug;
   private final BoolSetting followingCamera = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Following Camera",
         "Follow each placement and interaction face",
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
   private final AnchorCombatState combatState = new AnchorCombatState();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public DoubleAnchor() {
      super(
         "Double Anchor",
         "Automatically places and explodes 2 respawn anchors",
         Category.COMBAT,
         -1
      );
      this.triggerBind = (BindSetting)this.addSetting(
         new BindSetting(
            "Trigger Bind",
            "Key that starts double anchoring (-1 = Always active)",
            -1
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
      this.totemSlot = (IntSetting)this.addSetting(
         new IntSetting(
            "Totem Slot",
            "Totem slot",
            1,
            1,
            9,
            1
         )
      );
      this.placeDelay = (IntSetting)this.addSetting(
         new IntSetting(
            "Place Delay",
            "Delay after anchor placement",
            2,
            0,
            10,
            1
         )
      );
      this.glowstoneDelay = (IntSetting)this.addSetting(
         new IntSetting(
            "Glowstone Delay",
            "Delay after charging",
            1,
            0,
            10,
            1
         )
      );
      this.explodeDelay = (IntSetting)this.addSetting(
         new IntSetting(
            "Explode Delay",
            "Delay before explosion",
            1,
            0,
            10,
            1
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
            "Natural rotation curve with Mouse GCD quantization",
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
      this.humanize.setVisibleInGui(false);
      this.debug.setVisibleInGui(false);
      this.randomDelay.setVisibleInGui(false);
      this.rotationSpeed.setVisibleInGui(false);
   }

   public AnchorCombatState getCombatStateForTest() {
      return this.combatState;
   }

   @Override
   public void onEnable() {
      VieClientMod.getInstance().getEventBus().subscribe(this);
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      VieClientMod.getInstance().getEventBus().unsubscribe(this);
      this.resetState();
      super.onDisable();
   }

   public void resetState() {
      byte[] var10001 = new byte[30];
      var10001[0] = (byte)(101);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(105);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(103);
      var10001[5] = (byte)(46);
      var10001[6] = (byte)(72);
      var10001[7] = (byte)(39);
      var10001[8] = (byte)(42);
      var10001[9] = (byte)(-54);
      var10001[10] = (byte)(-117);
      var10001[11] = (byte)(-124);
      var10001[12] = (byte)(115);
      var10001[13] = (byte)(-31);
      var10001[14] = (byte)(68);
      var10001[15] = (byte)(66);
      var10001[16] = (byte)(20);
      var10001[17] = (byte)(62);
      var10001[18] = (byte)(-20);
      var10001[19] = (byte)(-63);
      var10001[20] = (byte)(-55);
      var10001[21] = (byte)(-101);
      var10001[22] = (byte)(61);
      var10001[23] = (byte)(112);
      var10001[24] = (byte)(-22);
      var10001[25] = (byte)(115);
      var10001[26] = (byte)(-10);
      var10001[27] = (byte)(42);
      var10001[28] = (byte)(-26);
      var10001[29] = (byte)(-89);
      this.combatState.reset($$v_s(var10001, 6992565964431937540L));
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      this.resetState();
   }

   @EventHandler
   public void onGameJoin(GameJoinEvent event) {
      this.resetState();
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D) {
         this.combatState.onRender(event.getFrameTime());
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1761 != null && mc.field_1755 == null && !mc.field_1724.method_6115()) {
            boolean var2 = this.checkActivationKey();
            if (!var2) {
               this.combatState
                  .reset(
                     "Trigger key not pressed"
                  );
            } else {
               int var3 = this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0;
               this.combatState.targetCycles = 2;
               this.combatState.rapidChain = true;
               this.combatState.humanize = (Boolean)this.humanize.get();
               this.combatState.switchDelay = (Integer)this.switchDelay.get() + var3;
               this.combatState.placeDelay = (Integer)this.placeDelay.get() + var3;
               this.combatState.chargeDelay = (Integer)this.glowstoneDelay.get() + var3;
               this.combatState.explodeDelay = (Integer)this.explodeDelay.get() + var3;
               this.combatState.totemSlot = (Integer)this.totemSlot.get();
               this.combatState.autoFillTotem = true;
               this.combatState.lookDownBlocks = 1.0;
               this.combatState.rotationSpeed = (Double)this.rotationSpeed.get();
               this.combatState.followingCamera = (Boolean)this.followingCamera.get();
               this.combatState.autoExplode = true;
               this.combatState.debug = (Boolean)this.debug.get();
               this.combatState.tick(var2, true);
            }
         } else {
            this.resetState();
         }
      }
   }

   private boolean checkActivationKey() {
      int var1 = (Integer)this.triggerBind.get();
      return var1 == (-1) || var1 == 0 || KeyUtils.isKeyPressed(var1);
   }

   public boolean isAnchoringActive() {
      return this.combatState.isBusy();
   }

   @EventHandler
   public void onItemUse(PreItemUseEvent event) {
      if (this.combatState.isBusy()) {
         event.cancel();
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
}
