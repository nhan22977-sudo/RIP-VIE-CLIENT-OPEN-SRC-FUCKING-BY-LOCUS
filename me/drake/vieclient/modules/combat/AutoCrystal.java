package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.PreItemUseEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BindSetting;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.BlockUtil;
import me.drake.vieclient.util.KeyUtils;
import me.drake.vieclient.util.WorldUtils;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public final class AutoCrystal extends Module {
   private final BindSetting triggerBind;
   private final IntSetting placeDelay;
   private final IntSetting breakDelay;
   private final BoolSetting stopOnKill;
   private final BoolSetting autoSwitch = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Auto Switch",
         "Select crystals immediately when aiming at a valid obsidian or bedrock base",
         true
      )
   );
   private final IntSetting switchDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "Switch Delay",
         "Ticks between automatic selection and placement",
         1,
         0,
         10
      )
   );
   private final BoolSetting humanize;
   private final IntSetting reactionDelay;
   private final BoolSetting randomDelay;
   private final BoolSetting debug;
   private final CrystalCombatState combatState = new CrystalCombatState(false);
   public boolean isActive = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoCrystal() {
      super(
         "Auto Crystal",
         "Automatically crystals fast for you",
         Category.COMBAT,
         -1
      );
      this.triggerBind = (BindSetting)this.addSetting(
         new BindSetting(
            "Trigger Bind",
            "Key that does the crystalling (-1 = Always active)",
            -1
         )
      );
      this.placeDelay = (IntSetting)this.addSetting(
         new IntSetting(
            "Place Delay",
            "Delay between placements",
            1,
            0,
            20,
            1
         )
      );
      this.breakDelay = (IntSetting)this.addSetting(
         new IntSetting(
            "Break Delay",
            "Delay between breaks",
            1,
            0,
            20,
            1
         )
      );
      this.stopOnKill = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Stop on Kill",
            "Stops crystalling when a player dies nearby",
            false
         )
      );
      this.humanize = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Humanize",
            "Apply reaction timing when acquiring a target",
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
      this.autoSwitch.setVisibleInGui(false);
      this.switchDelay.setVisibleInGui(false);
      this.stopOnKill.setVisibleInGui(false);
      this.debug.setVisibleInGui(false);
      this.randomDelay.setVisibleInGui(false);
      this.reactionDelay.visibleWhen(this.humanize::get);
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
      this.isActive = false;
      byte[] var10001 = new byte[29];
      var10001[0] = (byte)(96);
      var10001[1] = (byte)(-17);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-79);
      var10001[4] = (byte)(89);
      var10001[5] = (byte)(-39);
      var10001[6] = (byte)(23);
      var10001[7] = (byte)(104);
      var10001[8] = (byte)(61);
      var10001[9] = (byte)(-61);
      var10001[10] = (byte)(-120);
      var10001[11] = (byte)(-42);
      var10001[12] = (byte)(48);
      var10001[13] = (byte)(22);
      var10001[14] = (byte)(53);
      var10001[15] = (byte)(6);
      var10001[16] = (byte)(5);
      var10001[17] = (byte)(106);
      var10001[18] = (byte)(-93);
      var10001[19] = (byte)(-36);
      var10001[20] = (byte)(-118);
      var10001[21] = (byte)(63);
      var10001[22] = (byte)(87);
      var10001[23] = (byte)(56);
      var10001[24] = (byte)(-8);
      var10001[25] = (byte)(112);
      var10001[26] = (byte)(-8);
      var10001[27] = (byte)(35);
      var10001[28] = (byte)(-10);
      this.combatState.reset($$v_s(var10001, 3705747412585009156L));
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
   public void onTick(TickEvent tickEvent) {
      if (tickEvent.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1761 == null) {
            this.reset();
         } else if (mc.field_1755 != null) {
            this.reset();
         } else if (mc.field_1724.method_6115()) {
            this.reset();
         } else {
            boolean var2 = this.isKeyActive();
            if (!var2) {
               this.combatState
                  .reset(
                     "Trigger bind not active"
                  );
            } else {
               boolean var3 = mc.field_1724.method_6047().method_31574(class_1802.field_8301)
                  || mc.field_1724.method_6079().method_31574(class_1802.field_8301);
               if (!var3 && !(Boolean)this.autoSwitch.get()) {
                  this.combatState
                     .reset("Not holding crystal");
               } else if ((Boolean)this.stopOnKill.get() && WorldUtils.isDeadBodyNearby()) {
                  this.combatState
                     .reset(
                        "Stop on kill triggered"
                     );
               } else {
                  int var4 = this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0;
                  this.combatState.humanize = (Boolean)this.humanize.get();
                  this.combatState.reactionDelay = (Integer)this.reactionDelay.get() + var4;
                  this.combatState.placeDelay = (Integer)this.placeDelay.get() + var4;
                  this.combatState.breakDelay = (Integer)this.breakDelay.get() + var4;
                  this.combatState.autoSelectCrystal = (Boolean)this.autoSwitch.get();
                  this.combatState.switchDelay = (Integer)this.switchDelay.get() + var4;
                  this.combatState.waitEntityTimeout = 6;
                  this.combatState.debug = (Boolean)this.debug.get();
                  this.combatState.tick(var2, false, CrystalCombatState.Mode.CRYSTAL_FIRST);
               }
            }
         }
      }
   }

   private boolean isKeyActive() {
      int var1 = (Integer)this.triggerBind.get();
      return var1 != (-1) && var1 != 0 && !KeyUtils.isKeyPressed(var1) ? (this.isActive = false) : (this.isActive = true);
   }

   @EventHandler
   public void onPreItemUse(PreItemUseEvent preItemUseEvent) {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         if (this.isKeyActive() && mc.field_1755 == null && !mc.field_1724.method_6115() && (!(Boolean)this.stopOnKill.get() || !WorldUtils.isDeadBodyNearby())
            )
          {
            if ((mc.field_1724.method_6047().method_31574(class_1802.field_8301) || mc.field_1724.method_6079().method_31574(class_1802.field_8301))
               && mc.field_1765 instanceof class_3965 var2
               && mc.field_1765.method_17783() == class_240.field_1332) {
               class_2338 var4 = var2.method_17777();
               if (BlockUtil.isBlockAtPosition(var4, class_2246.field_10540) || BlockUtil.isBlockAtPosition(var4, class_2246.field_9987)) {
                  preItemUseEvent.cancel();
               }
            }
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
}
