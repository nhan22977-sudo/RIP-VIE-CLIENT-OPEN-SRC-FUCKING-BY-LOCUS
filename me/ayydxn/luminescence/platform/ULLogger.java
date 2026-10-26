package me.ayydxn.luminescence.platform;

import me.ayydxn.luminescence.util.NativeEnum;
import me.ayydxn.luminescence.util.UsedByNative;

public interface ULLogger {
   void logMessage(ULLogger.Level var1, String var2);

   enum Level implements NativeEnum {
      ERROR(0),
      WARNING(1),
      INFO(2);

      private final int value;

      Level(int value) {
         this.value = value;
      }

      @Override
      public int getValue() {
         return this.value;
      }

      @UsedByNative("LoggerCallbackAdapter.cpp")
      public static ULLogger.Level fromNativeValue(int value) {
         return NativeEnum.resolve(ULLogger.Level.class, value, INFO);
      }

      // $VF: synthetic method
      private static ULLogger.Level[] $values() {
         return new ULLogger.Level[]{ERROR, WARNING, INFO};
      }
   }
}
