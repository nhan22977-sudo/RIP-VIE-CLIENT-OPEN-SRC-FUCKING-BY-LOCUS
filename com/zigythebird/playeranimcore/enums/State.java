package com.zigythebird.playeranimcore.enums;

public enum State {
   RUNNING(true),
   PAUSED(true),
   STOPPED(false);

   private final boolean isActive;

   public boolean isActive() {
      return this.isActive;
   }

   State(boolean isActive) {
      this.isActive = isActive;
   }

   // $VF: synthetic method
   private static State[] $values() {
      return new State[]{RUNNING, PAUSED, STOPPED};
   }
}
