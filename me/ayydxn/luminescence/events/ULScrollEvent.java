package me.ayydxn.luminescence.events;

import org.jetbrains.annotations.ApiStatus.Internal;

public class ULScrollEvent implements AutoCloseable {
   private long handle;

   public ULScrollEvent(ScrollEventType type, int deltaX, int deltaY) {
      this.handle = ULScrollEvent.NativeMethods.nulCreateScrollEvent(type.ordinal(), deltaX, deltaY);
   }

   @Override
   public void close() {
      if (this.handle != 0L) {
         ULScrollEvent.NativeMethods.nulDestroyScrollEvent(this.handle);
         this.handle = 0L;
      }
   }

   @Internal
   public long getHandle() {
      return this.handle;
   }

   private static final class NativeMethods {
      private static native long nulCreateScrollEvent(int var0, int var1, int var2);

      private static native void nulDestroyScrollEvent(long var0);
   }
}
