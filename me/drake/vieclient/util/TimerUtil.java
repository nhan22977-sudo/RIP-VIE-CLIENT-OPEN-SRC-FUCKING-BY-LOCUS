package me.drake.vieclient.util;

public class TimerUtil {
   private int ticks = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void tick() {
      this.ticks++;
   }

   public boolean hasElapsed(int delay) {
      return this.ticks >= delay;
   }

   public void reset() {
      this.ticks = 0;
   }

   public int getTicks() {
      return this.ticks;
   }

   public boolean checkAndReset(int delay) {
      if (this.ticks >= delay) {
         this.ticks = 0;
         return true;
      } else {
         return false;
      }
   }
}
