package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.control.ActionArbiter;
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
import me.drake.vieclient.util.BlockUtil;
import me.drake.vieclient.util.InventoryUtil;
import me.drake.vieclient.util.KeyUtils;
import me.drake.vieclient.util.MathUtil;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_3965;
import net.minecraft.class_9334;

public final class AutoHitCrystal extends Module {
   private final BindSetting triggerBind;
   private final BoolSetting checkPlace;
   private final IntSetting switchDelay;
   private final IntSetting switchChance;
   private final IntSetting placeDelay;
   private final IntSetting placeChance;
   private final BoolSetting workWithTotem;
   private final BoolSetting workWithCrystal;
   private final BoolSetting swordSwap;
   private final BoolSetting humanize;
   private final IntSetting reactionDelay;
   private final DoubleSetting rotationSpeed;
   private final BoolSetting randomDelay;
   private final BoolSetting debug;
   private final CrystalCombatState combatState = new CrystalCombatState();
   private int originalWeaponSlot = -1;
   private boolean active = false;
   private int nextAttemptTick;
   private long attacksAtStart;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoHitCrystal() {
      super(
         "Auto Hit Crystal",
         "Automatically hit-crystals for you",
         Category.COMBAT,
         -1
      );
      this.triggerBind = (BindSetting)this.addSetting(
         new BindSetting(
            "Trigger Bind",
            "Key that does hit crystalling (-1 = Always active)",
            -1
         )
      );
      this.checkPlace = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Check Place",
            "Checks if you can place the obsidian on that block",
            false
         )
      );
      this.switchDelay = (IntSetting)this.addSetting(
         new IntSetting(
            "Switch Delay",
            "Delay before switching items (ticks)",
            1,
            0,
            20,
            1
         )
      );
      this.switchChance = (IntSetting)this.addSetting(
         new IntSetting(
            "Switch Chance",
            "Chance to switch items (%)",
            100,
            0,
            100,
            1
         )
      );
      this.placeDelay = (IntSetting)this.addSetting(
         new IntSetting(
            "Place Delay",
            "Delay before placing obsidian (ticks)",
            1,
            0,
            20,
            1
         )
      );
      this.placeChance = (IntSetting)this.addSetting(
         new IntSetting(
            "Place Chance",
            "Chance to place obsidian (%)",
            100,
            0,
            100,
            1
         )
      );
      this.workWithTotem = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Work With Totem",
            "Allow hit-crystal while holding totem",
            false
         )
      );
      this.workWithCrystal = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Work With Crystal",
            "Allow hit-crystal while holding crystal",
            false
         )
      );
      this.swordSwap = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Sword Swap",
            "Swap to sword before/after crystalling",
            true
         )
      );
      this.humanize = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Humanize",
            "Natural rotation and timing curves",
            true
         )
      );
      this.reactionDelay = (IntSetting)this.addSetting(
         new IntSetting(
            "ReactionDelay",
            "Reaction delay when target changes (ticks)",
            1,
            0,
            5
         )
      );
      this.rotationSpeed = (DoubleSetting)this.addSetting(
         new DoubleSetting(
            "RotationSpeed",
            "Turn limit (deg/tick); Both modes stay smooth and capped at 360 deg/s",
            8.0,
            5.0,
            180.0
         )
      );
      this.randomDelay = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Random Delay",
            "Randomly vary delay",
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
      this.randomDelay.setVisibleInGui(false);
      this.debug.setVisibleInGui(false);
      this.checkPlace.setVisibleInGui(false);
      this.switchChance.setVisibleInGui(false);
      this.placeChance.setVisibleInGui(false);
      this.reactionDelay.visibleWhen(this.humanize::get);
      this.rotationSpeed.visibleWhen(this.humanize::get);
   }

   public CrystalCombatState getCombatStateForTest() {
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
      this.active = false;
      byte[] var10001 = new byte[32];
      var10001[0] = (byte)(101);
      var10001[1] = (byte)(-17);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-79);
      var10001[4] = (byte)(23);
      var10001[5] = (byte)(-89);
      var10001[6] = (byte)(-84);
      var10001[7] = (byte)(-82);
      var10001[8] = (byte)(62);
      var10001[9] = (byte)(-37);
      var10001[10] = (byte)(-105);
      var10001[11] = (byte)(-126);
      var10001[12] = (byte)(102);
      var10001[13] = (byte)(122);
      var10001[14] = (byte)(-48);
      var10001[15] = (byte)(-25);
      var10001[16] = (byte)(17);
      var10001[17] = (byte)(57);
      var10001[18] = (byte)(-87);
      var10001[19] = (byte)(-38);
      var10001[20] = (byte)(-49);
      var10001[21] = (byte)(81);
      var10001[22] = (byte)(-6);
      var10001[23] = (byte)(-99);
      var10001[24] = (byte)(-8);
      var10001[25] = (byte)(123);
      var10001[26] = (byte)(-25);
      var10001[27] = (byte)(39);
      var10001[28] = (byte)(-75);
      var10001[29] = (byte)(42);
      var10001[30] = (byte)(-59);
      var10001[31] = 1;
      this.combatState.reset($$v_s(var10001, -4189736447060488191L));
      this.originalWeaponSlot = -1;
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      this.reset();
      this.nextAttemptTick = 0;
   }

   @EventHandler
   public void onGameJoin(GameJoinEvent event) {
      this.reset();
      this.nextAttemptTick = 0;
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
         } else if (mc.field_1755 == null && !mc.field_1724.method_6115()) {
            int var2 = (Integer)this.triggerBind.get();
            boolean var3 = var2 == -1 || var2 == 0 || KeyUtils.isKeyPressed(var2);
            if (!var3) {
               if (this.active) {
                  this.reset();
               }
            } else {
               class_1799 var4 = mc.field_1724.method_6047();
               boolean var5 = var4.method_57826(class_9334.field_55878);
               boolean var6 = (Boolean)this.workWithTotem.get() && var4.method_31574(class_1802.field_8288);
               boolean var7 = (Boolean)this.workWithCrystal.get() && var4.method_31574(class_1802.field_8301);
               if (this.active || var5 || var6 || var7) {
                  if (!this.active) {
                     if (mc.field_1724.field_6012 < this.nextAttemptTick || !this.combatState.hasUsableTarget(true)) {
                        return;
                     }

                     if ((Boolean)this.checkPlace.get()
                        && mc.field_1765 instanceof class_3965 var8
                        && !mc.field_1687.method_8320(var8.method_17777()).method_27852(class_2246.field_10540)
                        && !mc.field_1687.method_8320(var8.method_17777()).method_27852(class_2246.field_9987)) {
                        class_2338 var12 = mc.field_1687.method_8320(var8.method_17777()).method_45474()
                           ? var8.method_17777()
                           : var8.method_17777().method_10093(var8.method_17780());
                        if (!BlockUtil.canPlaceBlockClient(var12)) {
                           return;
                        }
                     }

                     if (MathUtil.randomInt(1, 100) > (Integer)this.switchChance.get() || MathUtil.randomInt(1, 100) > (Integer)this.placeChance.get()) {
                        this.nextAttemptTick = mc.field_1724.field_6012 + 4;
                        return;
                     }

                     if (!this.combatState.tryReserveTarget(true)) {
                        return;
                     }

                     this.originalWeaponSlot = var5 ? mc.field_1724.method_31548().method_67532() : -1;
                     this.attacksAtStart = this.combatState.getAttacksSent();
                     this.active = true;
                  }

                  if (this.combatState.getState() == CrystalCombatState.State.ACQUIRE && !this.combatState.hasUsableTarget(true)) {
                     this.reset();
                  } else {
                     int var11 = this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0;
                     this.combatState.humanize = (Boolean)this.humanize.get();
                     this.combatState.reactionDelay = (Integer)this.reactionDelay.get() + var11;
                     this.combatState.rotationSpeed = (Double)this.rotationSpeed.get();
                     this.combatState.switchDelay = (Integer)this.switchDelay.get() + var11;
                     this.combatState.placeDelay = (Integer)this.placeDelay.get() + var11;
                     this.combatState.breakDelay = 1 + var11;
                     this.combatState.waitEntityTimeout = 6;
                     this.combatState.debug = (Boolean)this.debug.get();
                     this.combatState.tick(true, true, CrystalCombatState.Mode.OBSIDIAN_FIRST);
                     if (this.combatState.getState() == CrystalCombatState.State.COOLDOWN || this.combatState.getState() == CrystalCombatState.State.IDLE) {
                        boolean var13 = this.combatState.getAttacksSent() > this.attacksAtStart;
                        boolean var10 = mc.field_1724.method_31548().method_67532() == this.combatState.getExpectedSlot();
                        if (var13
                           && var10
                           && (Boolean)this.swordSwap.get()
                           && this.originalWeaponSlot >= 0
                           && mc.field_1724.method_31548().method_5438(this.originalWeaponSlot).method_57826(class_9334.field_55878)
                           && ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.CRYSTAL_COMBAT)) {
                           InventoryUtil.swap(this.originalWeaponSlot);
                        }

                        this.nextAttemptTick = mc.field_1724.field_6012 + 2;
                        this.reset();
                     }
                  }
               }
            }
         } else {
            this.reset();
         }
      }
   }

   @EventHandler
   public void onItemUse(PreItemUseEvent event) {
      if (this.active && this.combatState.isBusy()) {
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
