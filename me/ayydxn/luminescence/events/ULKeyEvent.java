package me.ayydxn.luminescence.events;

import org.jetbrains.annotations.ApiStatus.Internal;

public class ULKeyEvent implements AutoCloseable {
   private long handle;

   public ULKeyEvent(
      KeyEventType type,
      int modifiers,
      int virtualKeyCode,
      int nativeKeyCode,
      String text,
      String unmodifiedText,
      boolean isKeypad,
      boolean isAutoRepeat,
      boolean isSystemKey
   ) {
      this.handle = ULKeyEvent.NativeMethods.nulCreateKeyEvent(
         type.ordinal(), modifiers, virtualKeyCode, nativeKeyCode, text, unmodifiedText, isKeypad, isAutoRepeat, isSystemKey
      );
   }

   @Override
   public void close() {
      if (this.handle != 0L) {
         ULKeyEvent.NativeMethods.nulDestroyKeyEvent(this.handle);
         this.handle = 0L;
      }
   }

   @Internal
   public long getHandle() {
      return this.handle;
   }

   private static final class NativeMethods {
      private static native long nulCreateKeyEvent(int var0, int var1, int var2, int var3, String var4, String var5, boolean var6, boolean var7, boolean var8);

      private static native void nulDestroyKeyEvent(long var0);
   }
}
