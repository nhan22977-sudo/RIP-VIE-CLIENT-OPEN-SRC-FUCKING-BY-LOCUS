package me.ayydxn.luminescence.events;

import org.jetbrains.annotations.ApiStatus.Internal;

public class ULGamepadEvent implements AutoCloseable {
   private final ULGamepadEvent.Type type;
   private long handle;

   private ULGamepadEvent(long handle, ULGamepadEvent.Type type) {
      this.handle = handle;
      this.type = type;
   }

   public static ULGamepadEvent create(int index, GamepadEventType eventType) {
      long handle = ULGamepadEvent.NativeMethods.nulCreateGamepadEvent(index, eventType.ordinal());
      return new ULGamepadEvent(handle, ULGamepadEvent.Type.REGULAR);
   }

   public static ULGamepadEvent createAxis(int index, int axisIndex, double value) {
      long handle = ULGamepadEvent.NativeMethods.nulCreateGamepadAxisEvent(index, axisIndex, value);
      return new ULGamepadEvent(handle, ULGamepadEvent.Type.AXIS);
   }

   public static ULGamepadEvent createButton(int index, int buttonIndex, double value) {
      long handle = ULGamepadEvent.NativeMethods.nulCreateGamepadButtonEvent(index, buttonIndex, value);
      return new ULGamepadEvent(handle, ULGamepadEvent.Type.BUTTON);
   }

   @Override
   public void close() {
      if (this.handle != 0L) {
         switch (this.type) {
            case REGULAR:
               ULGamepadEvent.NativeMethods.nulDestroyGamepadEvent(this.handle);
               break;
            case AXIS:
               ULGamepadEvent.NativeMethods.nulDestroyGamepadAxisEvent(this.handle);
               break;
            case BUTTON:
               ULGamepadEvent.NativeMethods.nulDestroyGamepadButtonEvent(this.handle);
         }

         this.handle = 0L;
      }
   }

   @Internal
   public ULGamepadEvent.Type getType() {
      return this.type;
   }

   @Internal
   public long getHandle() {
      return this.handle;
   }

   private static final class NativeMethods {
      private static native long nulCreateGamepadEvent(int var0, int var1);

      private static native void nulDestroyGamepadEvent(long var0);

      private static native long nulCreateGamepadAxisEvent(int var0, int var1, double var2);

      private static native void nulDestroyGamepadAxisEvent(long var0);

      private static native long nulCreateGamepadButtonEvent(int var0, int var1, double var2);

      private static native void nulDestroyGamepadButtonEvent(long var0);
   }

   @Internal
   public enum Type {
      REGULAR,
      AXIS,
      BUTTON;

      // $VF: synthetic method
      private static ULGamepadEvent.Type[] $values() {
         return new ULGamepadEvent.Type[]{REGULAR, AXIS, BUTTON};
      }
   }
}
