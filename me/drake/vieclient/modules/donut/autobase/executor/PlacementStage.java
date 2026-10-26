package me.drake.vieclient.modules.donut.autobase.executor;

import java.nio.charset.StandardCharsets;

public enum PlacementStage {
   IDLE,
   SELECT_TARGET,
   NAVIGATE,
   ACQUIRE_INPUT,
   SELECT_ITEM,
   REQUEST_SNEAK,
   WAIT_INPUT_PROPAGATION,
   AIM,
   VALIDATE,
   PLACE_ONCE,
   WAIT_WORLD_CONFIRMATION,
   COMPLETED,
   FAILED_BLOCKED;

   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   // $VF: synthetic method
   private static PlacementStage[] $values() {
      PlacementStage[] var10000 = new PlacementStage[13];
      var10000[0] = IDLE;
      var10000[1] = SELECT_TARGET;
      var10000[2] = NAVIGATE;
      var10000[3] = ACQUIRE_INPUT;
      var10000[4] = SELECT_ITEM;
      var10000[5] = REQUEST_SNEAK;
      var10000[6] = WAIT_INPUT_PROPAGATION;
      var10000[7] = AIM;
      var10000[8] = VALIDATE;
      var10000[9] = PLACE_ONCE;
      var10000[10] = WAIT_WORLD_CONFIRMATION;
      var10000[11] = COMPLETED;
      var10000[12] = FAILED_BLOCKED;
      return var10000;
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
