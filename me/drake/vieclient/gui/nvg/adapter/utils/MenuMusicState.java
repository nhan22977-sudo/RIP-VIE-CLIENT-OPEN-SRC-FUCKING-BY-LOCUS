package me.drake.vieclient.gui.nvg.adapter.utils;

final class MenuMusicState {
   private boolean muted;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   boolean isMuted() {
      return this.muted;
   }

   boolean toggle() {
      this.muted = !this.muted;
      return this.muted;
   }
}
