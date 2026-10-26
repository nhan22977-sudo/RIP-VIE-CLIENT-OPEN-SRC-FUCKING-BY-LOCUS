package me.ayydxn.luminescence.javascript;

import me.ayydxn.luminescence.util.UsedByNative;

public class JSValue {
   @UsedByNative("JSValue.cpp")
   final long contextHandle;
   @UsedByNative("JSValue.cpp")
   final long valueHandle;

   JSValue(long contextHandle, long valueHandle) {
      this.contextHandle = contextHandle;
      this.valueHandle = valueHandle;
   }

   public JSType getType() {
      return JSValue.NativeMethods.nJSValueGetType(this.contextHandle, this.valueHandle);
   }

   public boolean isUndefined() {
      return this.getType() == JSType.UNDEFINED;
   }

   public boolean isNull() {
      return this.getType() == JSType.NULL;
   }

   public boolean isBoolean() {
      return this.getType() == JSType.BOOLEAN;
   }

   public boolean isNumber() {
      return this.getType() == JSType.NUMBER;
   }

   public boolean isString() {
      return this.getType() == JSType.STRING;
   }

   public boolean isObject() {
      return JSValue.NativeMethods.nJSValueIsObject(this.contextHandle, this.valueHandle);
   }

   public boolean isArray() {
      return JSValue.NativeMethods.nJSValueIsArray(this.contextHandle, this.valueHandle);
   }

   public boolean isFunction() {
      return JSValue.NativeMethods.nJSValueIsFunction(this.contextHandle, this.valueHandle);
   }

   public boolean toBoolean() {
      return JSValue.NativeMethods.nJSValueToBoolean(this.contextHandle, this.valueHandle);
   }

   public double toNumber() throws JSException {
      return JSValue.NativeMethods.nJSValueToNumber(this.contextHandle, this.valueHandle);
   }

   public String toJSONString(int indent) throws JSException {
      return JSValue.NativeMethods.nJSValueToJSONString(this.contextHandle, this.valueHandle, indent);
   }

   @Override
   public String toString() {
      return JSValue.NativeMethods.nJSValueToString(this.contextHandle, this.valueHandle);
   }

   private static final class NativeMethods {
      private static native JSType nJSValueGetType(long var0, long var2);

      private static native boolean nJSValueIsObject(long var0, long var2);

      private static native boolean nJSValueIsArray(long var0, long var2);

      private static native boolean nJSValueIsFunction(long var0, long var2);

      private static native boolean nJSValueToBoolean(long var0, long var2);

      private static native double nJSValueToNumber(long var0, long var2) throws JSException;

      private static native String nJSValueToJSONString(long var0, long var2, int var4) throws JSException;

      private static native String nJSValueToString(long var0, long var2);
   }
}
