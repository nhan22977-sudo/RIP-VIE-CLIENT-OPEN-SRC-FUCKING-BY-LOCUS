package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import net.minecraft.class_1268;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import org.joml.Quaternionf;

public class HandViewAnimation extends Module {
   private final EnumSetting axis = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Axis",
         "Fixed item rotation axis",
         HandViewAnimation.Axis.Y
      )
   );
   private final DoubleSetting duration = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Duration",
         "Seconds per full turn",
         0.5,
         0.1,
         3.0,
         0.05
      )
   );
   private final BoolSetting loop = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Loop",
         "Rotate continuously",
         false
      )
   );
   private final BoolSetting reverse = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Reverse",
         "Spin the other way round",
         false
      )
   );
   private final EnumSetting applyTo = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Apply To",
         "Hand to spin",
         HandViewAnimation.ApplyTo.MAIN_HAND
      )
   );
   private boolean previousAttackPressed;
   private boolean previousUsePressed;
   private boolean animRunning;
   private float animProgress;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public HandViewAnimation() {
      super(
         "ItemSpin",
         "Spins the held item on a fixed axis",
         Category.CUSTOMIZE
      );
   }

   @Override
   public String getInfo() {
      return ((HandViewAnimation.Axis)this.axis.get()).name();
   }

   @Override
   public void onTick() {
      if (mc != null && mc.field_1690 != null) {
         boolean var1 = mc.field_1690.field_1886.method_1434();
         boolean var2 = mc.field_1690.field_1904.method_1434();
         boolean var3 = var1 && !this.previousAttackPressed || var2 && !this.previousUsePressed;
         this.previousAttackPressed = var1;
         this.previousUsePressed = var2;
         if (var3) {
            this.triggerSpin();
         }

         if ((Boolean)this.loop.get()) {
            this.animRunning = true;
            this.animProgress = this.animProgress + (float)(1.0 / ((Double)this.duration.get() * 20.0));
            if (this.animProgress >= 1.0F) {
               this.animProgress--;
            }
         } else if (this.animRunning) {
            this.animProgress = this.animProgress + (float)(1.0 / ((Double)this.duration.get() * 20.0));
            if (this.animProgress >= 1.0F) {
               this.animProgress = 1.0F;
               this.animRunning = false;
            }
         }
      }
   }

   private void triggerSpin() {
      this.animRunning = true;
      this.animProgress = 0.0F;
   }

   public boolean shouldAnimate(class_1268 hand) {
      if (!this.isEnabled()) {
         return false;
      }

      return switch ((HandViewAnimation.ApplyTo)this.applyTo.get()) {
         case MAIN_HAND -> hand == class_1268.field_5808;
         case OFF_HAND -> hand == class_1268.field_5810;
         case BOTH -> true;
      };
   }

   public void applyTransform(class_4587 matrices) {
      float var2 = !this.animRunning && !this.loop.get() ? 0.0F : this.animProgress;
      float var3 = var2 * 360.0F * (this.reverse.get() ? 1.0F : -1.0F);
      matrices.method_22907(rotationFor((HandViewAnimation.Axis)this.axis.get(), var3));
   }

   private static Quaternionf rotationFor(HandViewAnimation.Axis selectedAxis, float degrees) {
      return switch (selectedAxis) {
         case X -> class_7833.field_40714.rotationDegrees(degrees);
         case Y -> class_7833.field_40716.rotationDegrees(degrees);
         case Z -> class_7833.field_40718.rotationDegrees(degrees);
      };
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

   public enum ApplyTo {
      MAIN_HAND,
      OFF_HAND,
      BOTH;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static HandViewAnimation.ApplyTo[] $values() {
         HandViewAnimation.ApplyTo[] var10000 = new HandViewAnimation.ApplyTo[3];
         var10000[0] = MAIN_HAND;
         var10000[1] = OFF_HAND;
         var10000[2] = BOTH;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[9];
         var10002[0] = (byte)(117);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(-75);
         var10002[5] = (byte)(43);
         var10002[6] = (byte)(-56);
         var10002[7] = (byte)(-28);
         var10002[8] = (byte)(20);
         MAIN_HAND = new HandViewAnimation.ApplyTo();
         var10002 = new byte[8];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-36);
         var10002[2] = (byte)(90);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(-94);
         var10002[5] = (byte)(34);
         var10002[6] = (byte)(-57);
         var10002[7] = (byte)(-18);
         OFF_HAND = new HandViewAnimation.ApplyTo();
         var10002 = new byte[4];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-106);
         BOTH = new HandViewAnimation.ApplyTo();
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

   public enum Axis {
      X,
      Y,
      Z;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static HandViewAnimation.Axis[] $values() {
         HandViewAnimation.Axis[] var10000 = new HandViewAnimation.Axis[3];
         var10000[0] = X;
         var10000[1] = Y;
         var10000[2] = Z;
         return var10000;
      }
   }
}
