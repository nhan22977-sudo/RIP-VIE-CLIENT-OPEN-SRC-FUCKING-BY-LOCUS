package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import net.minecraft.class_1306;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_7833;

public class SwingAnimation extends Module {
   private final BoolSetting noEquipDip = (BoolSetting)this.addSetting(
      new BoolSetting(
         "No Equip Dip",
         "Prevents held item from dropping down and up during attack cooldown or re-equip",
         true
      )
   );
   private final DoubleSetting swingScale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Item Scale",
         "Size multiplier for held item",
         1.0,
         0.5,
         1.5,
         0.05
      )
   );
   private final DoubleSetting xOffset = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Offset X",
         "Horizontal shift for held hand",
         0.0,
         -0.6,
         0.6,
         0.02
      )
   );
   private final DoubleSetting yOffset = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Offset Y",
         "Vertical shift for held hand",
         0.0,
         -0.6,
         0.6,
         0.02
      )
   );
   private final DoubleSetting zOffset = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Offset Z",
         "Depth shift for held hand",
         0.0,
         -0.6,
         0.6,
         0.02
      )
   );
   private final BoolSetting mainHandOnly = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Main Hand Only",
         "Only apply custom swing animation to main hand",
         false
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SwingAnimation() {
      super(
         "Swing Animation",
         "Iconic Exhibition first-person swing animation",
         Category.RENDER
      );
   }

   @Override
   public String getInfo() {
      byte[] var10000 = new byte[10];
      var10000[0] = (byte)(113);
      var10000[1] = (byte)(-30);
      var10000[2] = (byte)(116);
      var10000[3] = (byte)(-73);
      var10000[4] = (byte)(-53);
      var10000[5] = (byte)(-2);
      var10000[6] = (byte)(24);
      var10000[7] = (byte)(-36);
      var10000[8] = (byte)(51);
      var10000[9] = (byte)(-52);
      return $$v_s(var10000, -7102377753022574575L);
   }

   public boolean isNoEquipDip() {
      return this.isEnabled() && (Boolean)this.noEquipDip.get();
   }

   public boolean shouldAnimate(class_1306 arm) {
      return !this.isEnabled() ? false : !(Boolean)this.mainHandOnly.get() || arm == class_1306.field_6183;
   }

   public boolean applySwing(class_4587 matrices, class_1306 arm, float swingProgress) {
      if (!this.shouldAnimate(arm)) {
         return false;
      }

      int var4 = arm == class_1306.field_6183 ? 1 : -1;
      float var5 = class_3532.method_15363(swingProgress, 0.0F, 1.0F);
      float var6 = (float)((Double)this.swingScale.get()).doubleValue();
      if (Math.abs(var6 - 1.0F) > 0.001F) {
         matrices.method_22905(var6, var6, var6);
      }

      float var7 = (float)((Double)this.xOffset.get()).doubleValue() * var4;
      float var8 = (float)((Double)this.yOffset.get()).doubleValue();
      float var9 = (float)((Double)this.zOffset.get()).doubleValue();
      if (var7 != 0.0F || var8 != 0.0F || var9 != 0.0F) {
         matrices.method_46416(var7, var8, var9);
      }

      this.applyExhibition(matrices, var4, var5);
      return true;
   }

   public void applyExhibition(class_4587 matrices, int side, float swingProgress) {
      float var4 = class_3532.method_15374(swingProgress * swingProgress * (float) Math.PI);
      float var5 = class_3532.method_15374(class_3532.method_15355(swingProgress) * (float) Math.PI);
      matrices.method_46416(side * 0.12F * var5, -0.16F * var5, -0.22F * var5);
      matrices.method_22907(class_7833.field_40716.rotationDegrees(side * (45.0F + var4 * -20.0F)));
      matrices.method_22907(class_7833.field_40718.rotationDegrees(side * var5 * -32.0F));
      matrices.method_22907(class_7833.field_40714.rotationDegrees(var5 * -65.0F));
      matrices.method_22907(class_7833.field_40716.rotationDegrees(side * -35.0F));
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
