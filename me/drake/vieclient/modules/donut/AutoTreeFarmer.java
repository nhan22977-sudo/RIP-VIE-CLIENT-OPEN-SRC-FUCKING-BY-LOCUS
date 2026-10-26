package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2397;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3481;
import net.minecraft.class_3965;

public class AutoTreeFarmer extends Module {
   private final IntSetting radiusXZ = (IntSetting)this.addSetting(
      new IntSetting(
         "RadiusXZ",
         "Horizontal scan radius",
         8,
         1,
         16,
         1
      )
   );
   private final IntSetting breakDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "BreakDelay",
         "Ticks between breaks",
         2,
         0,
         10,
         1
      )
   );
   private final BoolSetting replant = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Replant",
         "Auto-replant saplings",
         true
      )
   );
   private final BoolSetting autoSwitch = (BoolSetting)this.addSetting(
      new BoolSetting(
         "AutoSwitch",
         "Auto switch to axe",
         true
      )
   );
   private final Queue breakQueue = new ArrayDeque();
   private final Set replantPositions = new HashSet();
   private int delayTicks = 0;
   private int scanCooldown = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoTreeFarmer() {
      super(
         "AutoTreeFarmer",
         "Auto-chops trees + replants",
         Category.SMP_UTILITIES
      );
      this.autoSwitch.setVisibleInGui(false);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.breakQueue.clear();
      this.replantPositions.clear();
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (this.isInGame()) {
            if (this.breakQueue.isEmpty()) {
               if ((Boolean)this.replant.get() && !this.replantPositions.isEmpty()) {
                  class_2338 var6 = (class_2338)this.replantPositions.iterator().next();
                  this.replantPositions.remove(var6);
                  if (mc.field_1687.method_8320(var6).method_26215()) {
                     int var7 = InventoryUtil.findInHotbar(
                        stack -> stack.method_7909() == class_1802.field_17535
                           || stack.method_7909() == class_1802.field_17536
                           || stack.method_7909() == class_1802.field_17537
                           || stack.method_7909() == class_1802.field_17538
                           || stack.method_7909() == class_1802.field_17539
                           || stack.method_7909() == class_1802.field_17540
                     );
                     if (var7 >= 0) {
                        mc.field_1724.method_31548().method_61496(var7);
                        class_3965 var9 = new class_3965(class_243.method_24953(var6.method_10074()), class_2350.field_11036, var6.method_10074(), false);
                        mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, var9);
                     }
                  }
               } else if (this.scanCooldown > 0) {
                  this.scanCooldown--;
               } else {
                  this.scanCooldown = 40;
                  this.scanForTrees();
               }
            } else if (this.delayTicks > 0) {
               this.delayTicks--;
            } else {
               class_2338 var2 = (class_2338)this.breakQueue.poll();
               if (var2 != null) {
                  class_2680 var3 = mc.field_1687.method_8320(var2);
                  if (this.isLog(var3) || this.isLeaves(var3)) {
                     if ((Boolean)this.autoSwitch.get()) {
                        int var4 = InventoryUtil.findInHotbar(
                           stack -> stack.method_7909() == class_1802.field_8556
                              || stack.method_7909() == class_1802.field_22025
                              || stack.method_7909() == class_1802.field_8475
                              || stack.method_7909() == class_1802.field_8062
                              || stack.method_7909() == class_1802.field_8406
                        );
                        if (var4 >= 0) {
                           mc.field_1724.method_31548().method_61496(var4);
                        }
                     }

                     mc.field_1761.method_2902(var2, class_2350.field_11036);
                     this.delayTicks = (Integer)this.breakDelay.get();
                     if (this.isLog(var3)) {
                        class_2338 var8 = var2.method_10074();
                        class_2680 var5 = mc.field_1687.method_8320(var8);
                        if (var5.method_26204() == class_2246.field_10566 || var5.method_26204() == class_2246.field_10219) {
                           this.replantPositions.add(var2);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void scanForTrees() {
      class_2338 var1 = mc.field_1724.method_24515();
      int var2 = (Integer)this.radiusXZ.get();

      for (int var3 = -var2; var3 <= var2; var3++) {
         for (int var4 = -var2; var4 <= var2; var4++) {
            for (int var5 = 0; var5 <= var2 * (2); var5++) {
               class_2338 var6 = var1.method_10069(var3, var5, var4);
               class_2680 var7 = mc.field_1687.method_8320(var6);
               if (this.isLog(var7)) {
                  this.floodFillTree(var6);
                  return;
               }
            }
         }
      }
   }

   private void floodFillTree(class_2338 start) {
      HashSet var2 = new HashSet();
      ArrayDeque var3 = new ArrayDeque();
      var3.add(start);
      var2.add(start);
      int var4 = 256;

      while (!var3.isEmpty() && var2.size() < var4) {
         class_2338 var5 = (class_2338)var3.poll();
         class_2680 var6 = mc.field_1687.method_8320(var5);
         if (this.isLog(var6) || this.isLeaves(var6)) {
            this.breakQueue.add(var5);

            for (class_2350 var10 : class_2350.values()) {
               class_2338 var11 = var5.method_10093(var10);
               if (!var2.contains(var11)) {
                  class_2680 var12 = mc.field_1687.method_8320(var11);
                  if (this.isLog(var12) || this.isLeaves(var12)) {
                     var2.add(var11);
                     var3.add(var11);
                  }
               }
            }
         }
      }
   }

   private boolean isLog(class_2680 state) {
      return state.method_26164(class_3481.field_15475);
   }

   private boolean isLeaves(class_2680 state) {
      return state.method_26204() instanceof class_2397;
   }

   @Override
   public String getInfo() {
      return !this.breakQueue.isEmpty() ? this.breakQueue.size() + " blocks" : null;
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
