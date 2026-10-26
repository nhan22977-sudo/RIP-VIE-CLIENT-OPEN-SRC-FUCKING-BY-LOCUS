package me.ayydxn.luminescence.javascript;

import me.ayydxn.luminescence.util.UsedByNative;

public class JSArray extends JSObject {
   @UsedByNative("JSArray.cpp")
   JSArray(long contextHandle, long valueHandle) {
      super(contextHandle, valueHandle);
   }

   public int length() {
      return JSArray.NativeMethods.nJSArrayLength(this.contextHandle, this.valueHandle);
   }

   public JSValue get(int index) throws JSException {
      return this.getPropertyAtIndex(index);
   }

   public void set(int index, JSValue value) throws JSException {
      this.setPropertyAtIndex(index, value);
   }

   private static final class NativeMethods {
      private static native int nJSArrayLength(long var0, long var2);
   }
}
