package me.drake.vieclient.core.event.events;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.Event;

public class TickEvent extends Event {
   public static final TickEvent PRE = new TickEvent(TickEvent.Phase.PRE);
   public static final TickEvent POST = new TickEvent(TickEvent.Phase.POST);
   private final TickEvent.Phase phase;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public TickEvent(TickEvent.Phase phase) {
      this.phase = phasex;
   }

   public TickEvent.Phase getPhase() {
      return this.phase;
   }

   public enum Phase {
      PRE,
      POST;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TickEvent.Phase[] $values() {
         TickEvent.Phase[] var10000 = new TickEvent.Phase[2];
         var10000[0] = PRE;
         var10000[1] = POST;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(89);
         PRE = new TickEvent.Phase();
         var10002 = new byte[4];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         POST = new TickEvent.Phase();
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
