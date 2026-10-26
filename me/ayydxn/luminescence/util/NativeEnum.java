package me.ayydxn.luminescence.util;

import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface NativeEnum {
   int getValue();

   static <E extends Enum<E> & NativeEnum> E resolve(Class<E> enumClass, int nativeValue, E defaultValue) {
      for (E enumConstant : (Enum[])enumClass.getEnumConstants()) {
         if (enumConstant.getValue() == nativeValue) {
            return enumConstant;
         }
      }

      return defaultValue;
   }
}
