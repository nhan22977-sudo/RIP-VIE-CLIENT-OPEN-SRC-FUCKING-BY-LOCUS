package me.drake.vieclient.render;

public final class BackdropFrameGate {
   private boolean attempted;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void beginFrame() {
      this.attempted = false;
   }

   public synchronized boolean shouldCapture(int width, int height) {
      if (width > 0 && height > 0 && !this.attempted) {
         this.attempted = true;
         return true;
      } else {
         return false;
      }
   }
}
