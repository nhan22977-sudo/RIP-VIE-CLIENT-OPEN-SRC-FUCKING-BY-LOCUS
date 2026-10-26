package org.lwjgl.nanovg;

import javax.annotation.Nullable;
import org.lwjgl.system.Callback;

public abstract class UIHandler extends Callback implements UIHandlerI {
   public static UIHandler create(long functionPointer) {
      UIHandlerI instance = (UIHandlerI)Callback.get(functionPointer);
      return instance instanceof UIHandler ? (UIHandler)instance : new UIHandler.Container(functionPointer, instance);
   }

   @Nullable
   public static UIHandler createSafe(long functionPointer) {
      return functionPointer == 0L ? null : create(functionPointer);
   }

   public static UIHandler create(UIHandlerI instance) {
      return instance instanceof UIHandler ? (UIHandler)instance : new UIHandler.Container(instance.address(), instance);
   }

   protected UIHandler() {
      super(CIF);
   }

   UIHandler(long functionPointer) {
      super(functionPointer);
   }

   private static final class Container extends UIHandler {
      private final UIHandlerI delegate;

      Container(long functionPointer, UIHandlerI delegate) {
         super(functionPointer);
         this.delegate = delegate;
      }

      @Override
      public void invoke(int item, int event) {
         this.delegate.invoke(item, event);
      }
   }
}
