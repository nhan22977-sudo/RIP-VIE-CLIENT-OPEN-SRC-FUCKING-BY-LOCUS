package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;

final class TunnelHeadingArbiter {
   private TunnelBaseFinder.Heading heading;
   private TunnelHeadingArbiter.HeadingOwner owner = TunnelHeadingArbiter.HeadingOwner.ROUTE;
   private int lockTicks;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   boolean request(TunnelBaseFinder.Heading next, TunnelHeadingArbiter.HeadingOwner requester, int requestedLockTicks) {
      if (next == null || requester == null) {
         return false;
      }

      if (this.lockTicks > 0 && requester.priority < this.owner.priority) {
         return false;
      }

      this.heading = next;
      this.owner = requester;
      this.lockTicks = Math.max(0, requestedLockTicks);
      return true;
   }

   void tick() {
      if (this.lockTicks > 0) {
         this.lockTicks--;
      }
   }

   TunnelBaseFinder.Heading heading() {
      return this.heading;
   }

   TunnelHeadingArbiter.HeadingOwner owner() {
      return this.owner;
   }

   int remainingLockTicks() {
      return this.lockTicks;
   }

   void reset() {
      this.heading = null;
      this.owner = TunnelHeadingArbiter.HeadingOwner.ROUTE;
      this.lockTicks = 0;
   }

   enum HeadingOwner {
      HAZARD,
      RECOVERY,
      ESCAPE,
      DOGLEG,
      DETOUR,
      ROUTE;

      private final int priority;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      HeadingOwner(int priority) {
         this.priority = priorityx;
      }

      // $VF: synthetic method
      private static TunnelHeadingArbiter.HeadingOwner[] $values() {
         TunnelHeadingArbiter.HeadingOwner[] var10000 = new TunnelHeadingArbiter.HeadingOwner[6];
         var10000[0] = HAZARD;
         var10000[1] = RECOVERY;
         var10000[2] = ESCAPE;
         var10000[3] = DOGLEG;
         var10000[4] = DETOUR;
         var10000[5] = ROUTE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(117);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(70);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(113);
         var10002[5] = (byte)(39);
         HAZARD = new TunnelHeadingArbiter.HeadingOwner(5);
         var10002 = new byte[8];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(95);
         var10002[3] = (byte)(-111);
         var10002[4] = (byte)(117);
         var10002[5] = (byte)(38);
         var10002[6] = (byte)(25);
         var10002[7] = (byte)(-94);
         RECOVERY = new TunnelHeadingArbiter.HeadingOwner(4);
         var10002 = new byte[6];
         var10002[0] = (byte)(120);
         var10002[1] = (byte)(-55);
         var10002[2] = (byte)(95);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(115);
         var10002[5] = (byte)(38);
         ESCAPE = new TunnelHeadingArbiter.HeadingOwner(3);
         var10002 = new byte[6];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(91);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(102);
         var10002[5] = (byte)(36);
         DOGLEG = new TunnelHeadingArbiter.HeadingOwner(2);
         var10002 = new byte[6];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-111);
         var10002[4] = (byte)(118);
         var10002[5] = (byte)(49);
         DETOUR = new TunnelHeadingArbiter.HeadingOwner(1);
         var10002 = new byte[5];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(73);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(102);
         ROUTE = new TunnelHeadingArbiter.HeadingOwner(0);
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
