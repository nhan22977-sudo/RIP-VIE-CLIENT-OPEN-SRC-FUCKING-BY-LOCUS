package me.ayydxn.luminescence.javascript;

import me.ayydxn.luminescence.util.UsedByNative;

public class JSObject extends JSValue {
   @UsedByNative("JSObject.cpp")
   JSObject(long contextHandle, long valueHandle) {
      super(contextHandle, valueHandle);
   }

   public JSValue getProperty(String name) throws JSException {
      return JSObject.NativeMethods.nJSObjectGetProperty(this.contextHandle, this.valueHandle, name);
   }

   public void setProperty(String name, JSValue value) throws JSException {
      JSObject.NativeMethods.nJSObjectSetProperty(this.contextHandle, this.valueHandle, name, value.valueHandle);
   }

   public boolean hasProperty(String name) {
      return JSObject.NativeMethods.nJSObjectHasProperty(this.contextHandle, this.valueHandle, name);
   }

   public boolean deleteProperty(String name) throws JSException {
      return JSObject.NativeMethods.nJSObjectDeleteProperty(this.contextHandle, this.valueHandle, name);
   }

   public JSValue getPropertyAtIndex(int index) throws JSException {
      return JSObject.NativeMethods.nJSObjectGetPropertyAtIndex(this.contextHandle, this.valueHandle, index);
   }

   public void setPropertyAtIndex(int index, JSValue value) throws JSException {
      JSObject.NativeMethods.nJSObjectSetPropertyAtIndex(this.contextHandle, this.valueHandle, index, value.valueHandle);
   }

   public String[] getPropertyNames() {
      return JSObject.NativeMethods.nJSObjectGetPropertyNames(this.contextHandle, this.valueHandle);
   }

   private static final class NativeMethods {
      private static native JSValue nJSObjectGetProperty(long var0, long var2, String var4) throws JSException;

      private static native void nJSObjectSetProperty(long var0, long var2, String var4, long var5) throws JSException;

      private static native boolean nJSObjectHasProperty(long var0, long var2, String var4);

      private static native boolean nJSObjectDeleteProperty(long var0, long var2, String var4) throws JSException;

      private static native JSValue nJSObjectGetPropertyAtIndex(long var0, long var2, int var4) throws JSException;

      private static native void nJSObjectSetPropertyAtIndex(long var0, long var2, int var4, long var5) throws JSException;

      private static native String[] nJSObjectGetPropertyNames(long var0, long var2);
   }
}
