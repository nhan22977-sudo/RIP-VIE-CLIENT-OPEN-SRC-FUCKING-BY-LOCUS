package me.ayydxn.luminescence.config;

import me.ayydxn.luminescence.util.NativeEnum;

public enum FaceWinding implements NativeEnum {
   CLOCKWISE(0),
   COUNTER_CLOCKWISE(1);

   private final int value;

   FaceWinding(int value) {
      this.value = value;
   }

   @Override
   public int getValue() {
      return this.value;
   }

   // $VF: synthetic method
   private static FaceWinding[] $values() {
      return new FaceWinding[]{CLOCKWISE, COUNTER_CLOCKWISE};
   }
}
