package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import net.minecraft.class_2338;

final class SpawnerBreakerTargetTracker {
   private class_2338 target;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   void select(class_2338 target) {
      this.target = targetx == null ? null : targetx.method_10062();
   }

   class_2338 target() {
      return this.target;
   }

   SpawnerBreakerTargetTracker.Result update(boolean inputActive, boolean inRange, boolean stillSpawner) {
      if (this.target == null) {
         return SpawnerBreakerTargetTracker.Result.IDLE;
      } else if (!inputActive || !inRange) {
         this.clear();
         return SpawnerBreakerTargetTracker.Result.IDLE;
      } else if (!stillSpawner) {
         this.clear();
         return SpawnerBreakerTargetTracker.Result.COMPLETED;
      } else {
         return SpawnerBreakerTargetTracker.Result.ACTIVE;
      }
   }

   void clear() {
      this.target = null;
   }

   enum Result {
      IDLE,
      ACTIVE,
      COMPLETED;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static SpawnerBreakerTargetTracker.Result[] $values() {
         SpawnerBreakerTargetTracker.Result[] var10000 = new SpawnerBreakerTargetTracker.Result[3];
         var10000[0] = IDLE;
         var10000[1] = ACTIVE;
         var10000[2] = COMPLETED;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(113);
         var10002[1] = (byte)(-34);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-101);
         IDLE = new SpawnerBreakerTargetTracker.Result();
         var10002 = new byte[6];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-39);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-115);
         var10002[5] = (byte)(87);
         ACTIVE = new SpawnerBreakerTargetTracker.Result();
         var10002 = new byte[9];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-114);
         var10002[4] = (byte)(-105);
         var10002[5] = (byte)(87);
         var10002[6] = (byte)(65);
         var10002[7] = (byte)(-128);
         var10002[8] = (byte)(20);
         COMPLETED = new SpawnerBreakerTargetTracker.Result();
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
