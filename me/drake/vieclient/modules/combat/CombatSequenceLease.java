package me.drake.vieclient.modules.combat;

final class CombatSequenceLease {
   static final CombatSequenceLease SHARED = new CombatSequenceLease();
   private Object owner;
   private Object world;
   private int touchedTick;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   boolean acquire(Object requester, Object world, int tick) {
      if (this.owner != null && this.owner != requester && this.world == worldx && tick >= this.touchedTick && tick - this.touchedTick <= (2)
         )
       {
         return false;
      }

      this.owner = requester;
      this.world = worldx;
      this.touchedTick = tick;
      return true;
   }

   boolean owns(Object requester) {
      return this.owner == requester;
   }

   boolean release(Object requester) {
      if (this.owner != requester) {
         return false;
      }

      this.owner = null;
      this.world = null;
      return true;
   }
}
