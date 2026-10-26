package me.ayydxn.luminescence.javascript;

import me.ayydxn.luminescence.util.UsedByNative;

public class JSFunction extends JSObject {
   @UsedByNative("JSFunction.cpp")
   JSFunction(long contextHandle, long valueHandle) {
      super(contextHandle, valueHandle);
   }

   public static JSFunction create(JSContext context, String name, JSFunction.JSCallback callback) throws JSException {
      return JSFunction.NativeMethods.nJSFunctionCreate(context.getHandle(), name, callback);
   }

   public JSValue call(JSObject thisObject, JSValue... arguments) throws JSException {
      long[] handles = new long[arguments.length];

      for (int i = 0; i < arguments.length; i++) {
         handles[i] = arguments[i].valueHandle;
      }

      return JSFunction.NativeMethods.nJSFunctionCall(this.contextHandle, this.valueHandle, thisObject != null ? thisObject.valueHandle : 0L, handles);
   }

   public JSObject callAsConstructor(JSValue... args) throws JSException {
      long[] handles = new long[args.length];

      for (int i = 0; i < args.length; i++) {
         handles[i] = args[i].valueHandle;
      }

      return JSFunction.NativeMethods.nJSFunctionCallAsConstructor(this.contextHandle, this.valueHandle, handles);
   }

   @FunctionalInterface
   public interface JSCallback {
      JSValue invoke(JSContext var1, JSObject var2, JSValue[] var3) throws JSException;
   }

   private static final class NativeMethods {
      private static native JSFunction nJSFunctionCreate(long var0, String var2, JSFunction.JSCallback var3) throws JSException;

      private static native JSValue nJSFunctionCall(long var0, long var2, long var4, long[] var6) throws JSException;

      private static native JSObject nJSFunctionCallAsConstructor(long var0, long var2, long[] var4) throws JSException;
   }
}
