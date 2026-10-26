package me.drake.vieclient.modules.player;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_746;

public class AutoEat extends Module {
   private final IntSetting minHunger = (IntSetting)this.addSetting(
      new IntSetting(
         "MinHunger",
         "Eat when hunger below this",
         15,
         1,
         20
      )
   );
   private final DoubleSetting minHP = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "MinHP",
         "Eat when HP below this regardless of hunger",
         8.0,
         1.0,
         20.0
      )
   );
   private final BoolSetting eatWhileMoving = (BoolSetting)this.addSetting(
      new BoolSetting(
         "EatWhileMoving",
         "Allow eating while moving",
         true
      )
   );
   private boolean eating = false;
   private int prevSlot = -1;
   private int eatTicks = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoEat() {
      super(
         "AutoEat",
         "Automatically eats food when hungry",
         Category.PLAYER
      );
      this.eatWhileMoving.setVisibleInGui(false);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.stopEating();
   }

   @Override
   public void onTick() {
      if (this.isInGame() && mc.field_1690 != null) {
         class_746 var1 = mc.field_1724;
         if (mc.field_1755 != null) {
            if (this.eating) {
               this.stopEating();
            }
         } else {
            boolean var2 = mc.field_1690.field_1894.method_1434()
               || mc.field_1690.field_1881.method_1434()
               || mc.field_1690.field_1913.method_1434()
               || mc.field_1690.field_1849.method_1434()
               || var1.method_5624();
            if (!(Boolean)this.eatWhileMoving.get() && var2) {
               if (this.eating) {
                  this.stopEating();
               }
            } else {
               float var3 = var1.method_6032();
               int var4 = var1.method_7344().method_7586();
               boolean var5 = (var4 < (Integer)this.minHunger.get() || var3 < ((Double)this.minHP.get()).floatValue()) && var4 < (20);
               if (!var5) {
                  if (this.eating) {
                     this.stopEating();
                  }
               } else {
                  if (!this.eating) {
                     int var6 = InventoryUtil.findBestFoodInHotbar(var1);
                     if (var6 == (-1)) {
                        return;
                     }

                     if (!ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOEAT)) {
                        return;
                     }

                     this.prevSlot = var1.method_31548().method_67532();
                     var1.method_31548().method_61496(var6);
                     this.eating = true;
                     this.eatTicks = 0;
                  }

                  boolean var9 = ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOEAT);
                  boolean var7 = ActionArbiter.get().tryAcquire(ActionArbiter.Resource.USE_KEY, ActionArbiter.Owner.AUTOEAT);
                  if (var9 && var7) {
                     mc.field_1690.field_1904.method_23481(true);
                     this.eatTicks++;
                     boolean var8 = var1.method_7344().method_7586() >= (Integer)this.minHunger.get()
                        && var1.method_6032() >= ((Double)this.minHP.get()).floatValue();
                     if (!var1.method_6115() && this.eatTicks > (5) || var8 || this.eatTicks > (40)) {
                        this.stopEating();
                     }
                  } else {
                     this.stopEating();
                  }
               }
            }
         }
      }
   }

   private void stopEating() {
      if (mc != null && mc.field_1690 != null) {
         mc.field_1690.field_1904.method_23481(false);
      }

      if (this.eating && this.prevSlot != (-1) && mc != null && mc.field_1724 != null) {
         mc.field_1724.method_31548().method_61496(this.prevSlot);
      }

      if (this.eating) {
         ActionArbiter.get().release(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOEAT);
         ActionArbiter.get().release(ActionArbiter.Resource.USE_KEY, ActionArbiter.Owner.AUTOEAT);
      }

      this.eating = false;
      this.prevSlot = -1;
      this.eatTicks = 0;
   }

   @Override
   public String getInfo() {
      String var1;
      if (this.eating) {
         byte[] var10000 = new byte[6];
         var10000[0] = (byte)(119);
         var10000[1] = (byte)(-5);
         var10000[2] = (byte)(104);
         var10000[3] = (byte)(-73);
         var10000[4] = (byte)(-123);
         var10000[5] = (byte)(-36);
         var1 = $$v_s(var10000, 2983746157243781143L);
      } else {
         var1 = null;
      }

      return var1;
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
