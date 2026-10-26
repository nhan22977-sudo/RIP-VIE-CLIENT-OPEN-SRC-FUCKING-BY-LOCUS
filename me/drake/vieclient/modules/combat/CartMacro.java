package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.mixin.MinecraftClientAccessor;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public class CartMacro extends Module {
   private final BoolSetting autoMode = (BoolSetting)this.addSetting(
      new BoolSetting(
         "AutoMode",
         "AUTO: bow aim triggers placement | OFF: sneak to aim",
         false
      )
   );
   private final BoolSetting autoSwitch = (BoolSetting)this.addSetting(
      new BoolSetting(
         "AutoSwitch",
         "Restore original hotbar slot after placing",
         true
      )
   );
   private final BoolSetting placePower = (BoolSetting)this.addSetting(
      new BoolSetting(
         "PlacePower",
         "Place redstone block above rail to power it",
         true
      )
   );
   private final BoolSetting anyRail = (BoolSetting)this.addSetting(
      new BoolSetting(
         "AnyRail",
         "Fall back to any rail type if no powered rail",
         true
      )
   );
   private final BoolSetting randomDelay = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Random Delay",
         "Randomly vary delay",
         true
      )
   );
   private boolean isActive = false;
   private int tickCounter = 0;
   private int extraDelay = 0;
   private class_2338 targetPos = null;
   private int originalSlot = -1;
   private boolean wasSneaking = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public CartMacro() {
      super(
         "CartMacro",
         "Places rail+cart automatically",
         Category.COMBAT
      );
      this.randomDelay.setVisibleInGui(false);
      this.autoSwitch.setVisibleInGui(false);
   }

   @Override
   public void onEnable() {
      this.reset();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      if (this.isActive) {
         this.stopPlacing();
      }

      this.reset();
      super.onDisable();
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
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1755 == null) {
            if ((Boolean)this.autoMode.get()) {
               this.tickAuto();
            } else {
               this.tickSelf();
            }

            if (this.isActive && this.tickCounter > 0) {
               this.runSequence();
            }
         }
      }
   }

   private void tickSelf() {
      boolean var1 = mc.field_1724.method_5715();
      if (!this.isActive && var1 && !this.wasSneaking) {
         class_2338 var2 = this.getTargetPos();
         if (var2 != null) {
            if (!ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOTOOL, 6)
               || !ActionArbiter.get().tryAcquire(ActionArbiter.Resource.USE_KEY, ActionArbiter.Owner.AUTOTOOL, 6)) {
               return;
            }

            this.targetPos = var2;
            this.originalSlot = mc.field_1724.method_31548().method_67532();
            this.isActive = true;
            this.tickCounter = 0;
         }
      }

      if (this.isActive && this.wasSneaking && !var1 && this.tickCounter == 0) {
         this.tickCounter = 1;
      }

      this.wasSneaking = var1;
   }

   private void tickAuto() {
      boolean var1 = this.isDrawingBow();
      if (!this.isActive && var1) {
         class_2338 var2 = this.getTargetPos();
         if (var2 != null) {
            if (!ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOTOOL, 6)
               || !ActionArbiter.get().tryAcquire(ActionArbiter.Resource.USE_KEY, ActionArbiter.Owner.AUTOTOOL, 6)) {
               return;
            }

            this.targetPos = var2;
            this.originalSlot = mc.field_1724.method_31548().method_67532();
            this.isActive = true;
            this.tickCounter = 0;
         }
      }

      if (this.isActive && !var1 && this.tickCounter == 0) {
         this.tickCounter = 1;
      }
   }

   private void runSequence() {
      if (this.extraDelay > 0) {
         this.extraDelay--;
      } else {
         if (this.tickCounter == 1) {
            this.placeRail();
            this.tickCounter = this.placePower.get() ? 2 : 3;
            if ((Boolean)this.randomDelay.get()) {
               this.extraDelay = ThreadLocalRandom.current().nextInt(1, 3);
            }
         } else if (this.tickCounter == (2)) {
            this.placeRedstone();
            this.tickCounter = 3;
            if ((Boolean)this.randomDelay.get()) {
               this.extraDelay = ThreadLocalRandom.current().nextInt(1, 3);
            }
         } else if (this.tickCounter == (3)) {
            this.placeTntCart();
            this.stopPlacing();
         }
      }
   }

   private void stopPlacing() {
      if ((Boolean)this.autoSwitch.get() && this.originalSlot != (-1)) {
         InventoryUtil.swap(this.originalSlot);
      }

      this.reset();
   }

   private void placeRail() {
      if (this.targetPos != null) {
         int var1 = this.findPreferredRail();
         if (var1 != (-1)) {
            InventoryUtil.swap(var1);
            this.doUse();
         }
      }
   }

   private void placeRedstone() {
      if (this.targetPos != null) {
         class_2338 var1 = this.targetPos.method_10084();
         if (mc.field_1687.method_8320(var1).method_26215()) {
            int var2 = this.findInHotbar(class_1802.field_8793);
            if (var2 != (-1)) {
               InventoryUtil.swap(var2);
               this.doUse();
            }
         }
      }
   }

   private void placeTntCart() {
      if (this.targetPos != null) {
         int var1 = this.findInHotbar(class_1802.field_8069);
         if (var1 != (-1)) {
            InventoryUtil.swap(var1);
            this.doUse();
         }
      }
   }

   private void doUse() {
      ((MinecraftClientAccessor)mc).invokeDoItemUse();
   }

   private class_2338 getTargetPos() {
      class_239 var1 = mc.field_1765;
      if (var1 == null) {
         return null;
      } else if (var1.method_17783() == class_240.field_1332) {
         class_3965 var2 = (class_3965)var1;
         return var2.method_17777().method_10093(var2.method_17780());
      } else {
         return mc.field_1724 != null ? mc.field_1724.method_24515().method_10084() : null;
      }
   }

   private boolean isDrawingBow() {
      if (mc.field_1724 != null && mc.field_1724.method_6115()) {
         class_1792 var1 = mc.field_1724.method_6030().method_7909();
         return var1 instanceof class_1753 || var1 instanceof class_1764;
      } else {
         return false;
      }
   }

   private int findPreferredRail() {
      if (mc.field_1724 == null) {
         return -1;
      }

      int var1 = this.findInHotbar(class_1802.field_8848);
      if (var1 != (-1)) {
         return var1;
      }

      if ((Boolean)this.anyRail.get()) {
         for (int var2 = 0; var2 < (9); var2++) {
            class_1799 var3 = mc.field_1724.method_31548().method_5438(var2);
            if (!var3.method_7960() && this.isRailItem(var3.method_7909())) {
               return var2;
            }
         }
      }

      return -1;
   }

   private int findInHotbar(class_1792 item) {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var2 = 0; var2 < (9); var2++) {
         class_1799 var3 = mc.field_1724.method_31548().method_5438(var2);
         if (!var3.method_7960() && var3.method_31574(item)) {
            return var2;
         }
      }

      return -1;
   }

   private boolean isRailItem(class_1792 item) {
      return item == class_1802.field_8129 || item == class_1802.field_8848 || item == class_1802.field_8211 || item == class_1802.field_8655;
   }

   private void reset() {
      if (this.isActive) {
         ActionArbiter.get().release(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOTOOL);
         ActionArbiter.get().release(ActionArbiter.Resource.USE_KEY, ActionArbiter.Owner.AUTOTOOL);
      }

      this.isActive = false;
      this.tickCounter = 0;
      this.extraDelay = 0;
      this.targetPos = null;
      this.originalSlot = -1;
      this.wasSneaking = false;
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
