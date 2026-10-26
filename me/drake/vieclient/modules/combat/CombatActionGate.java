package me.drake.vieclient.modules.combat;

final class CombatActionGate {
   static final CombatActionGate SHARED = new CombatActionGate();
   private Object world;
   private int tick = -2147483648;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   boolean tryClaim(Object world, int tick) {
      if (this.world == worldx && this.tick == tickx) {
         return false;
      }

      this.world = worldx;
      this.tick = tickx;
      return true;
   }
}
