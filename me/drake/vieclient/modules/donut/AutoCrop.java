package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_1268;
import net.minecraft.class_2242;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2302;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2421;
import net.minecraft.class_2680;
import net.minecraft.class_3830;
import net.minecraft.class_746;

public class AutoCrop extends Module {
   private final DoubleSetting radius = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Radius",
         "Harvest radius",
         5.0,
         1.0,
         10.0
      )
   );
   private final IntSetting delay = (IntSetting)this.addSetting(
      new IntSetting(
         "Delay",
         "Ticks between harvests",
         2,
         0,
         10
      )
   );
   private int delayTicks = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoCrop() {
      super(
         "AutoCrop",
         "Auto harvests and replants crops (DonutSMP)",
         Category.SMP_UTILITIES
      );
   }

   @Override
   public void onTick() {
      if (this.isInGame()) {
         if (this.delayTicks > 0) {
            this.delayTicks--;
         } else {
            class_746 var1 = mc.field_1724;
            class_2338 var2 = var1.method_24515();
            int var3 = (int)Math.ceil((Double)this.radius.get());
            double var4 = (Double)this.radius.get() * (Double)this.radius.get();

            for (class_2338 var7 : class_2338.method_25996(var2, var3, 2, var3)) {
               if (!(var7.method_40081(var1.method_23317(), var1.method_23318(), var1.method_23321()) > var4)) {
                  class_2680 var8 = mc.field_1687.method_8320(var7);
                  if (this.isMatureCrop(var8)) {
                     mc.field_1761.method_2910(var7, class_2350.field_11036);
                     var1.method_6104(class_1268.field_5808);
                     this.delayTicks = (Integer)this.delay.get();
                     return;
                  }
               }
            }
         }
      }
   }

   private boolean isMatureCrop(class_2680 state) {
      class_2248 var2 = state.method_26204();
      if (var2 instanceof class_2302 var3) {
         return var3.method_9825(state);
      } else if (var2 == class_2246.field_10341) {
         return (Integer)state.method_11654(class_2242.field_9962) == (3);
      } else if (var2 == class_2246.field_9974) {
         return (Integer)state.method_11654(class_2421.field_11306) == (3);
      } else {
         return var2 == class_2246.field_16999 ? (Integer)state.method_11654(class_3830.field_17000) == (3) : false;
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
