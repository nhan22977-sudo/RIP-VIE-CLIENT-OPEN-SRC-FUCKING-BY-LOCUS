package me.drake.vieclient.utils.misc;

import me.drake.vieclient.util.KeyUtils;

public class Keybind {
   private final int key;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private Keybind(int key) {
      this.key = keyx;
   }

   public static Keybind none() {
      return new Keybind(-1);
   }

   public static Keybind of(int key) {
      return new Keybind(keyx);
   }

   public boolean isPressed() {
      return this.key != (-1) && this.key != 0 ? KeyUtils.isKeyPressed(this.key) : false;
   }

   public int getKey() {
      return this.key;
   }
}
