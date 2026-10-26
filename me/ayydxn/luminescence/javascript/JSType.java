package me.ayydxn.luminescence.javascript;

import me.ayydxn.luminescence.util.NativeEnum;

public enum JSType implements NativeEnum {
   UNDEFINED(0),
   NULL(1),
   BOOLEAN(2),
   NUMBER(3),
   STRING(4),
   OBJECT(5);

   private final int value;

   JSType(int value) {
      this.value = value;
   }

   @Override
   public int getValue() {
      return this.value;
   }

   public static JSType fromNativeValue(int nativeValue) {
      return NativeEnum.resolve(JSType.class, nativeValue, UNDEFINED);
   }

   // $VF: synthetic method
   private static JSType[] $values() {
      return new JSType[]{UNDEFINED, NULL, BOOLEAN, NUMBER, STRING, OBJECT};
   }
}
