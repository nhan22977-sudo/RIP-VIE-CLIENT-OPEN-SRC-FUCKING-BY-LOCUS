package me.ayydxn.luminescence.javascript;

import org.jetbrains.annotations.Nullable;

public class JSException extends Exception {
   @Nullable
   private final JSValue value;

   public JSException(JSValue value) {
      super(value.toString());
      this.value = value;
   }

   public JSException(String message) {
      super(message);
      this.value = null;
   }

   @Nullable
   public JSValue getJSValue() {
      return this.value;
   }
}
