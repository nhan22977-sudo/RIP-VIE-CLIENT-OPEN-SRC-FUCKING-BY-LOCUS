package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;

final class CombatConfirmation {
   private int readyAt;
   private int expiresAt;
   private boolean requireServerUpdate;
   private boolean serverObserved;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   void begin(int tick, int minimumDelay, int timeout) {
      this.begin(tick, minimumDelay, timeout, false);
   }

   void begin(int tick, int minimumDelay, int timeout, boolean requireServerUpdate) {
      this.readyAt = tick + Math.max(1, minimumDelay);
      this.expiresAt = this.readyAt + Math.max(1, timeout);
      this.requireServerUpdate = requireServerUpdatex;
      this.serverObserved = false;
   }

   void observeServerUpdate(boolean matches) {
      this.serverObserved = matches;
   }

   CombatConfirmation.Result poll(int tick, boolean observed) {
      if (tick < this.readyAt) {
         return CombatConfirmation.Result.WAITING;
      } else if (!observed || this.requireServerUpdate && !this.serverObserved) {
         return tick >= this.expiresAt ? CombatConfirmation.Result.TIMED_OUT : CombatConfirmation.Result.WAITING;
      } else {
         return CombatConfirmation.Result.READY;
      }
   }

   enum Result {
      WAITING,
      READY,
      TIMED_OUT;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CombatConfirmation.Result[] $values() {
         CombatConfirmation.Result[] var10000 = new CombatConfirmation.Result[3];
         var10000[0] = WAITING;
         var10000[1] = READY;
         var10000[2] = TIMED_OUT;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-36);
         var10002[5] = (byte)(77);
         var10002[6] = (byte)(3);
         WAITING = new CombatConfirmation.Result();
         var10002 = new byte[5];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-102);
         var10002[4] = (byte)(-52);
         READY = new CombatConfirmation.Result();
         var10002 = new byte[9];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(-47);
         var10002[5] = (byte)(92);
         var10002[6] = (byte)(11);
         var10002[7] = (byte)(11);
         var10002[8] = (byte)(4);
         TIMED_OUT = new CombatConfirmation.Result();
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
