package me.ayydxn.luminescence.javascript;

import org.jetbrains.annotations.ApiStatus.Internal;

public class JSContext implements AutoCloseable {
   private final long viewHandle;
   final long handle;

   @Internal
   public JSContext(long viewHandle, long handle) {
      this.viewHandle = viewHandle;
      this.handle = handle;
   }

   @Override
   public void close() {
      JSContext.NativeMethods.nJSContextUnlock(this.viewHandle);
   }

   public JSValue make(double value) {
      return JSContext.NativeMethods.nJSContextMakeNumber(this.handle, value);
   }

   public JSValue make(String value) {
      return JSContext.NativeMethods.nJSContextMakeString(this.handle, value);
   }

   public JSValue make(boolean value) {
      return JSContext.NativeMethods.nJSContextMakeBoolean(this.handle, value);
   }

   public JSValue makeUndefined() {
      return JSContext.NativeMethods.nJSContextMakeUndefined(this.handle);
   }

   public JSValue makeNull() {
      return JSContext.NativeMethods.nJSContextMakeNull(this.handle);
   }

   public JSObject globalObject() {
      return JSContext.NativeMethods.nJSContextGetGlobalObject(this.handle);
   }

   public JSValue evaluate(String script) throws JSException {
      return JSContext.NativeMethods.nJSContextEvaluate(this.handle, script, null, 0);
   }

   public JSValue evaluate(String script, String sourceURL, int startingLineNumber) throws JSException {
      return JSContext.NativeMethods.nJSContextEvaluate(this.handle, script, sourceURL, startingLineNumber);
   }

   @Internal
   public long getHandle() {
      return this.handle;
   }

   private static final class NativeMethods {
      private static native JSValue nJSContextMakeNumber(long var0, double var2);

      private static native JSValue nJSContextMakeString(long var0, String var2);

      private static native JSValue nJSContextMakeBoolean(long var0, boolean var2);

      private static native JSValue nJSContextMakeUndefined(long var0);

      private static native JSValue nJSContextMakeNull(long var0);

      private static native JSObject nJSContextGetGlobalObject(long var0);

      private static native JSValue nJSContextEvaluate(long var0, String var2, String var3, int var4) throws JSException;

      private static native void nJSContextUnlock(long var0);
   }
}
