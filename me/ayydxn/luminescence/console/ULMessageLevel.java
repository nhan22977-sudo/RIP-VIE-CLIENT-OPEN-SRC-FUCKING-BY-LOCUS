package me.ayydxn.luminescence.console;

import me.ayydxn.luminescence.util.NativeEnum;
import me.ayydxn.luminescence.util.UsedByNative;

public enum ULMessageLevel implements NativeEnum {
   LOG(0),
   WARNING(1),
   ERROR(2),
   DEBUG(3),
   INFO(4);

   private final int value;

   ULMessageLevel(int value) {
      this.value = value;
   }

   @Override
   public int getValue() {
      return this.value;
   }

   @UsedByNative("ViewCallbackAdapter.cpp")
   public static ULMessageLevel fromNativeValue(int value) {
      return NativeEnum.resolve(ULMessageLevel.class, value, LOG);
   }

   // $VF: synthetic method
   private static ULMessageLevel[] $values() {
      return new ULMessageLevel[]{LOG, WARNING, ERROR, DEBUG, INFO};
   }
}
