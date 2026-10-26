package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.EnumSetting;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class BoneDropper extends Module {
   private final EnumSetting mode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Mode",
         "Drop mode",
         BoneDropper.DropMode.INVENTORY
      )
   );
   private static final int DROP_INTERVAL = 20;
   private int delayTicks = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BoneDropper() {
      super(
         "BoneDropper",
         "Auto-drops bones for wolf taming",
         Category.SMP_UTILITIES
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.delayTicks = 20;
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (this.isInGame()) {
            if (this.delayTicks > 0) {
               this.delayTicks--;
            } else {
               switch ((BoneDropper.DropMode)this.mode.get()) {
                  case INVENTORY:
                     this.handleInventoryMode();
                     break;
                  case SPAWNER:
                     this.handleSpawnerMode();
               }
            }
         }
      }
   }

   private void handleInventoryMode() {
      for (int var1 = 0; var1 < (36); var1++) {
         class_1799 var2 = mc.field_1724.method_31548().method_5438(var1);
         if (var2.method_7909() == class_1802.field_8606 && !var2.method_7960()) {
            int var3 = var1 < (9) ? var1 + (36) : var1;
            mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, var3, 0, class_1713.field_7795, mc.field_1724);
            this.delayTicks = 20;
            return;
         }
      }
   }

   private void handleSpawnerMode() {
      if (mc.field_1724.field_7512 instanceof class_1707 var1) {
         int var4 = var1.method_17388() * (9);

         for (int var3 = 0; var3 < var4; var3++) {
            if (var1.method_7611(var3).method_7677().method_31574(class_1802.field_8606)) {
               mc.field_1761.method_2906(mc.field_1724.field_7512.field_7763, var3, 1, class_1713.field_7795, mc.field_1724);
               this.delayTicks = 20;
               return;
            }
         }

         mc.field_1724.method_7346();
         this.delayTicks = 100;
      } else {
         if (mc.field_1765 != null) {
            mc.field_1690.field_1904.method_23481(true);
            this.delayTicks = 20;
            mc.field_1690.field_1904.method_23481(false);
         }
      }
   }

   @Override
   public String getInfo() {
      return ((BoneDropper.DropMode)this.mode.get()).name();
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

   public enum DropMode {
      INVENTORY,
      SPAWNER;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static BoneDropper.DropMode[] $values() {
         BoneDropper.DropMode[] var10000 = new BoneDropper.DropMode[2];
         var10000[0] = INVENTORY;
         var10000[1] = SPAWNER;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[9];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-44);
         var10002[2] = (byte)(74);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(-57);
         var10002[5] = (byte)(-94);
         var10002[6] = (byte)(-85);
         var10002[7] = (byte)(117);
         var10002[8] = (byte)(8);
         INVENTORY = new BoneDropper.DropMode();
         var10002 = new byte[7];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-54);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-119);
         var10002[4] = (byte)(-57);
         var10002[5] = (byte)(-77);
         var10002[6] = (byte)(-74);
         SPAWNER = new BoneDropper.DropMode();
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
