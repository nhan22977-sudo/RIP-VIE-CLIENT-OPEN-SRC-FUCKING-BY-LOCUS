package me.ayydxn.luminescence.events;

import org.jetbrains.annotations.ApiStatus.Internal;

public class ULMouseEvent implements AutoCloseable {
   private long handle;

   public ULMouseEvent(MouseEventType type, int x, int y, MouseButton button) {
      this.handle = ULMouseEvent.NativeMethods.nulCreateMouseEvent(type.ordinal(), x, y, button.ordinal());
   }

   @Override
   public void close() {
      if (this.handle != 0L) {
         ULMouseEvent.NativeMethods.nulDestroyMouseEvent(this.handle);
         this.handle = 0L;
      }
   }

   @Internal
   public long getHandle() {
      return this.handle;
   }

   private static final class NativeMethods {
      private static native long nulCreateMouseEvent(int var0, int var1, int var2, int var3);

      private static native void nulDestroyMouseEvent(long var0);
   }
}
