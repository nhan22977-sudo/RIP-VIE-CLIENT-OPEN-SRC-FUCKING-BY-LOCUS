package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.util.RotationUtil;
import net.minecraft.class_1294;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3532;
import net.minecraft.class_746;

public final class TunnelMotion {
   public static final double CENTER_THRESHOLD = 0.32;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private TunnelMotion() {
   }

   public static TunnelMotion.Side centerCorrection(double x, double z, TunnelRoutePlanner.Dir dir, double threshold) {
      double var7 = x - Math.floor(x);
      double var9 = z - Math.floor(z);
      TunnelRoutePlanner.Dir var11 = leftOf(dir);
      double var12 = Math.sqrt((double)var11.dx * var11.dx + (double)var11.dz * var11.dz);
      double var14 = var11.dx / var12;
      double var16 = var11.dz / var12;
      double var18 = (var7 - 0.5) * var14 + (var9 - 0.5) * var16;
      if (var18 < -threshold) {
         return TunnelMotion.Side.LEFT;
      } else {
         return var18 > threshold ? TunnelMotion.Side.RIGHT : TunnelMotion.Side.NONE;
      }
   }

   public static TunnelRoutePlanner.Dir leftOf(TunnelRoutePlanner.Dir dir) {
      return TunnelRoutePlanner.Dir.VALUES[(dir.ordinal() + (6)) % (8)];
   }

   public static TunnelRoutePlanner.Dir rightOf(TunnelRoutePlanner.Dir dir) {
      return TunnelRoutePlanner.Dir.VALUES[(dir.ordinal() + (2)) % (8)];
   }

   public static boolean shouldSprint(class_746 player, boolean currentlySprinting) {
      if (player == null) {
         return false;
      } else {
         int var2 = player.method_7344().method_7586();
         if (currentlySprinting && var2 >= (7)) {
            return true;
         } else if (!currentlySprinting && var2 < (5)) {
            return false;
         } else if (player.method_5799() || player.method_5869()) {
            return false;
         } else {
            return player.method_5715() ? false : !player.method_6059(class_1294.field_5919);
         }
      }
   }

   public static boolean shouldSprint(class_746 player) {
      return shouldSprint(player, player != null && player.method_5624());
   }

   public static float stepYaw(float current, float desired, double frameTime, double speed, float sensitivity) {
      float var7 = class_3532.method_15393(desired - current);
      if (Math.abs(var7) < 0.05F) {
         return desired;
      }

      double var8 = Math.min(Math.max(frameTime, 0.0), 0.05);
      if (var8 <= 0.0) {
         return current;
      }

      double var10 = 7.0;
      float var12 = (float)(var7 * (1.0 - Math.exp(-var10 * var8)));
      float var13 = (float)(speed * var8);
      float var14 = class_3532.method_15363(var12, -var13, var13);
      float var15 = RotationUtil.applyGCD(var14, sensitivity);
      if (var15 == 0.0F) {
         float var16 = RotationUtil.gcdFor(sensitivity);
         if (var16 > 0.0F && Math.abs(var7) >= var16 * 0.5F) {
            var15 = Math.copySign(var16, var7);
         } else {
            var15 = Math.copySign(Math.min(Math.abs(var7), 0.05F), var7);
         }
      }

      if (Math.abs(var15) > Math.abs(var7)) {
         var15 = var7;
      }

      return current + var15;
   }

   public static float stepPitch(float current, float desired, double frameTime, double speed, float sensitivity) {
      float var7 = desired - current;
      if (Math.abs(var7) < 0.05F) {
         return desired;
      }

      double var8 = Math.min(Math.max(frameTime, 0.0), 0.05);
      if (var8 <= 0.0) {
         return current;
      }

      float var10 = (float)(var7 * (1.0 - Math.exp(-7.0 * var8)));
      float var11 = class_3532.method_15363(var10, (float)(-(speed * var8)), (float)(speed * var8));
      if (!(sensitivity > 0.0F)) {
         if (Math.abs(var11) < 0.05F) {
            var11 = Math.copySign(Math.min(Math.abs(var7), 0.05F), var7);
         }

         if (Math.abs(var11) > Math.abs(var7)) {
            var11 = var7;
         }

         return class_3532.method_15363(current + var11, -90.0F, 90.0F);
      } else {
         float var12 = RotationUtil.applyGCD(var11, sensitivity);
         if (var12 == 0.0F) {
            float var13 = RotationUtil.gcdFor(sensitivity);
            if (var13 > 0.0F && Math.abs(var7) >= var13 * 0.5F) {
               var12 = Math.copySign(var13, var7);
            } else {
               var12 = Math.copySign(Math.min(Math.abs(var7), 0.05F), var7);
            }
         }

         if (Math.abs(var12) > Math.abs(var7)) {
            var12 = var7;
         }

         return class_3532.method_15363(current + var12, -90.0F, 90.0F);
      }
   }

   public static float stepPitch(float current, float desired, double frameTime, double speed) {
      return stepPitch(current, desired, frameTime, speed, 0.0F);
   }

   public static final class BreakWatchdog {
      private class_2338 target;
      private int ticksOnTarget;
      private int allowance;
      private static final float SLACK = 3.0F;
      private static final int FLOOR = 20;
      private static final int CEILING = 200;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public void reset() {
         this.target = null;
         this.ticksOnTarget = 0;
         this.allowance = 0;
      }

      public boolean tick(class_2338 pos, class_1937 world, class_746 player) {
         if (pos == null || world == null || player == null) {
            this.reset();
            return false;
         } else if (!pos.equals(this.target)) {
            this.target = pos.method_10062();
            this.ticksOnTarget = 0;
            this.allowance = estimateAllowance(pos, world, player);
            return this.allowance <= 0;
         } else {
            return ++this.ticksOnTarget > this.allowance;
         }
      }

      public int ticksOnTarget() {
         return this.ticksOnTarget;
      }

      public int allowance() {
         return this.allowance;
      }

      private static int estimateAllowance(class_2338 pos, class_1937 world, class_746 player) {
         class_2680 var3 = world.method_8320(pos);
         if (var3.method_26215()) {
            return 20;
         }

         float var4;
         try {
            var4 = var3.method_26165(player, world, pos);
         } catch (RuntimeException var9) {
            return 20;
         }

         if (var4 <= 0.0F) {
            return 0;
         }

         long var5 = (long)Math.ceil(1.0 / var4);
         long var7 = Math.min(200L, var5 * 3L + 20L);
         return (int)Math.max(20L, var7);
      }
   }

   public enum Side {
      NONE,
      LEFT,
      RIGHT;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TunnelMotion.Side[] $values() {
         TunnelMotion.Side[] var10000 = new TunnelMotion.Side[3];
         var10000[0] = NONE;
         var10000[1] = LEFT;
         var10000[2] = RIGHT;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         NONE = new TunnelMotion.Side();
         var10002 = new byte[4];
         var10002[0] = (byte)(116);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(90);
         var10002[3] = (byte)(-118);
         LEFT = new TunnelMotion.Side();
         var10002 = new byte[5];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(91);
         var10002[3] = (byte)(-106);
         var10002[4] = (byte)(-52);
         RIGHT = new TunnelMotion.Side();
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
}
