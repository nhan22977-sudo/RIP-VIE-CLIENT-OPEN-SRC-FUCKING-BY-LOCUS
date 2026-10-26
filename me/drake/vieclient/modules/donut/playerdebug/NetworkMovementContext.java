package me.drake.vieclient.modules.donut.playerdebug;

public final class NetworkMovementContext {
   private static final NetworkMovementContext INSTANCE = new NetworkMovementContext();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static NetworkMovementContext get() {
      return INSTANCE;
   }

   public NetworkMovementContext.Snapshot snapshot() {
      return NetworkMovementContext.Snapshot.EMPTY;
   }

   public record Snapshot(int playerChunkX, int playerChunkZ, double pingMs, double speed, double directionX, double directionZ) {
      public static final NetworkMovementContext.Snapshot EMPTY = new NetworkMovementContext.Snapshot(0, 0, 0.0, 0.0, 0.0, 0.0);
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
