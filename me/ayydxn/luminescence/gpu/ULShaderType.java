package me.ayydxn.luminescence.gpu;

import me.ayydxn.luminescence.util.NativeEnum;
import me.ayydxn.luminescence.util.UsedByNative;

public enum ULShaderType implements NativeEnum {
   FILL(0),
   FILL_PATH(1);

   private final int value;

   ULShaderType(int value) {
      this.value = value;
   }

   @Override
   public int getValue() {
      return this.value;
   }

   @UsedByNative("GPUDriverCallbackAdapter.cpp")
   public static ULShaderType fromNativeValue(int value) {
      return NativeEnum.resolve(ULShaderType.class, value, FILL);
   }

   // $VF: synthetic method
   private static ULShaderType[] $values() {
      return new ULShaderType[]{FILL, FILL_PATH};
   }
}
