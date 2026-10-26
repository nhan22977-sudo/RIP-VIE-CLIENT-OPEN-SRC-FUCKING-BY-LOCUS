package me.ayydxn.luminescence.config;

import me.ayydxn.luminescence.util.NativeEnum;

public enum FontHinting implements NativeEnum {
   SMOOTH(0),
   NORMAL(1),
   MONOCHROME(2),
   NONE(3);

   private final int value;

   FontHinting(int value) {
      this.value = value;
   }

   @Override
   public int getValue() {
      return this.value;
   }

   // $VF: synthetic method
   private static FontHinting[] $values() {
      return new FontHinting[]{SMOOTH, NORMAL, MONOCHROME, NONE};
   }
}
