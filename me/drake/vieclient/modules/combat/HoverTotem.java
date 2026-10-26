package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.mixin.HandledScreenAccessor;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1735;
import net.minecraft.class_1802;
import net.minecraft.class_490;

public class HoverTotem extends Module {
   private final IntSetting tickDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "TickDelay",
         "Ticks to wait between operations",
         0,
         0,
         20
      )
   );
   private final BoolSetting hotbarTotem = (BoolSetting)this.addSetting(
      new BoolSetting(
         "HotbarTotem",
         "Also place totem in hotbar slot",
         true
      )
   );
   private final IntSetting hotbarSlot = (IntSetting)this.addSetting(
      new IntSetting(
         "HotbarSlot",
         "Preferred hotbar slot for totem",
         1,
         1,
         9
      )
   );
   private final BoolSetting autoSwitch = (BoolSetting)this.addSetting(
      new BoolSetting(
         "AutoSwitch",
         "Auto switch to totem slot when inventory opens",
         false
      )
   );
   private final BoolSetting randomDelay = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Random Delay",
         "Randomly vary delay",
         true
      )
   );
   private int remainingDelay;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public HoverTotem() {
      super(
         "HoverTotem",
         "Equips totem when hovering in inventory",
         Category.COMBAT
      );
      this.randomDelay.setVisibleInGui(false);
      this.hotbarSlot.visibleWhen(this.hotbarTotem::get);
   }

   @Override
   public void onEnable() {
      this.resetDelay();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.resetDelay();
      super.onDisable();
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      this.resetDelay();
   }

   @EventHandler
   public void onGameJoin(GameJoinEvent event) {
      this.resetDelay();
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 != null && mc.field_1761 != null) {
            if (mc.field_1755 instanceof class_490 var2) {
               try {
                  class_1735 var8 = ((HandledScreenAccessor)var2).getFocusedSlot();
                  if (var8 == null) {
                     return;
                  }

                  if (var8.field_7874 < (9) || var8.field_7874 > (44)) {
                     return;
                  }

                  if ((Boolean)this.autoSwitch.get()) {
                     InventoryUtil.swap((Integer)this.hotbarSlot.get() - 1);
                  }

                  if (var8.method_7677().method_7909() != class_1802.field_8288) {
                     return;
                  }

                  if (this.remainingDelay > 0) {
                     this.remainingDelay--;
                     return;
                  }

                  int var4 = var8.field_7874;
                  int var5 = ((class_1723)var2.method_17577()).field_7763;
                  if (!mc.field_1724.method_6079().method_31574(class_1802.field_8288)) {
                     this.equipOffhandTotem(var5, var4);
                  } else if ((Boolean)this.hotbarTotem.get()) {
                     int var6 = (Integer)this.hotbarSlot.get() - 1;
                     if (!mc.field_1724.method_31548().method_5438(var6).method_31574(class_1802.field_8288)) {
                        this.equipHotbarTotem(var5, var4, var6);
                     }
                  }
               } catch (Exception var7) {
               }
            } else {
               this.resetDelay();
            }
         }
      }
   }

   private void equipOffhandTotem(int syncId, int slotIndex) {
      mc.field_1761.method_2906(syncId, slotIndex, 40, class_1713.field_7791, mc.field_1724);
      this.resetDelay();
   }

   private void equipHotbarTotem(int syncId, int slotIndex, int hotbarSlotIndex) {
      mc.field_1761.method_2906(syncId, slotIndex, hotbarSlotIndex, class_1713.field_7791, mc.field_1724);
      this.resetDelay();
   }

   private void resetDelay() {
      this.remainingDelay = (Integer)this.tickDelay.get() + (this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 3) : 0);
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
