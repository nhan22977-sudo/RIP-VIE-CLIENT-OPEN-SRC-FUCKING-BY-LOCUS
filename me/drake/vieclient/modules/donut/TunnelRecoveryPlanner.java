package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import net.minecraft.class_2338;

public final class TunnelRecoveryPlanner {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public TunnelRecoveryPlanner.Action choose(TunnelRecoveryPlanner.Snapshot snapshot) {
      if (snapshot == null || snapshot.origin() == null) {
         return TunnelRecoveryPlanner.Action.replan();
      } else if (isForbiddenTravelY(snapshot.origin().method_10264())) {
         class_2338 var2 = snapshot.landing();
         return var2 != null && var2.method_10264() < snapshot.origin().method_10264()
            ? new TunnelRecoveryPlanner.Action(TunnelRecoveryPlanner.ActionType.MOVE_TO, var2)
            : TunnelRecoveryPlanner.Action.replan();
      } else if (snapshot.landing() != null && snapshot.frontBlocked() && isAdjacentLanding(snapshot.origin(), snapshot.landing())) {
         return new TunnelRecoveryPlanner.Action(TunnelRecoveryPlanner.ActionType.JUMP, snapshot.landing());
      } else if (snapshot.edgeObstacle() != null && snapshot.edgeMineable()) {
         return new TunnelRecoveryPlanner.Action(TunnelRecoveryPlanner.ActionType.MINE, snapshot.edgeObstacle());
      } else {
         return snapshot.landing() != null && !snapshot.frontBlocked() && snapshot.landing().method_10264() != snapshot.origin().method_10264()
            ? new TunnelRecoveryPlanner.Action(TunnelRecoveryPlanner.ActionType.MOVE_TO, snapshot.landing())
            : TunnelRecoveryPlanner.Action.replan();
      }
   }

   private static boolean isAdjacentLanding(class_2338 origin, class_2338 landing) {
      int var2 = Math.abs(landing.method_10263() - origin.method_10263());
      int var3 = Math.abs(landing.method_10260() - origin.method_10260());
      int var4 = landing.method_10264() - origin.method_10264();
      return var2 <= 1 && var3 <= 1 && (var2 != 0 || var3 != 0) && var4 >= 0 && var4 <= 1;
   }

   public static boolean isForbiddenTravelY(int y) {
      return y == (-58);
   }

   public record Action(TunnelRecoveryPlanner.ActionType type, class_2338 target) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public static TunnelRecoveryPlanner.Action replan() {
         return new TunnelRecoveryPlanner.Action(TunnelRecoveryPlanner.ActionType.REPLAN, null);
      }
   }

   public enum ActionType {
      MINE,
      MOVE_TO,
      JUMP,
      REPLAN;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TunnelRecoveryPlanner.ActionType[] $values() {
         TunnelRecoveryPlanner.ActionType[] var10000 = new TunnelRecoveryPlanner.ActionType[4];
         var10000[0] = MINE;
         var10000[1] = MOVE_TO;
         var10000[2] = JUMP;
         var10000[3] = REPLAN;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(114);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         MINE = new TunnelRecoveryPlanner.ActionType();
         var10002 = new byte[7];
         var10002[0] = (byte)(114);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(74);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(101);
         var10002[5] = 1;
         var10002[6] = (byte)(-53);
         MOVE_TO = new TunnelRecoveryPlanner.ActionType();
         var10002 = new byte[4];
         var10002[0] = (byte)(117);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-114);
         JUMP = new TunnelRecoveryPlanner.ActionType();
         var10002 = new byte[6];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(123);
         var10002[5] = (byte)(27);
         REPLAN = new TunnelRecoveryPlanner.ActionType();
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

   public static final class Snapshot {
      private final class_2338 origin;
      private class_2338 edgeObstacle;
      private boolean edgeMineable;
      private boolean frontBlocked;
      private class_2338 landing;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private Snapshot(class_2338 origin) {
         this.origin = originx.method_10062();
      }

      public static TunnelRecoveryPlanner.Snapshot.Builder builder(class_2338 origin) {
         return new TunnelRecoveryPlanner.Snapshot.Builder(originx);
      }

      public class_2338 origin() {
         return this.origin;
      }

      public class_2338 edgeObstacle() {
         return this.edgeObstacle;
      }

      public boolean edgeMineable() {
         return this.edgeMineable;
      }

      public boolean frontBlocked() {
         return this.frontBlocked;
      }

      public class_2338 landing() {
         return this.landing;
      }

      public static final class Builder {
         private final TunnelRecoveryPlanner.Snapshot snapshot;
         // $VF: synthetic field
         private static final boolean $$vie_p = true;

         private Builder(class_2338 origin) {
            this.snapshot = new TunnelRecoveryPlanner.Snapshot(origin);
         }

         public TunnelRecoveryPlanner.Snapshot.Builder edgeObstacle(class_2338 pos, boolean mineable) {
            this.snapshot.edgeObstacle = pos.method_10062();
            this.snapshot.edgeMineable = mineable;
            return this;
         }

         public TunnelRecoveryPlanner.Snapshot.Builder frontBlocked() {
            this.snapshot.frontBlocked = true;
            return this;
         }

         public TunnelRecoveryPlanner.Snapshot.Builder landing(class_2338 pos) {
            this.snapshot.landing = pos.method_10062();
            return this;
         }

         public TunnelRecoveryPlanner.Snapshot build() {
            return this.snapshot;
         }
      }
   }
}
