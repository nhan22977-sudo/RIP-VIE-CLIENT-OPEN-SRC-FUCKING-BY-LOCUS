package me.ayydxn.luminescence.renderer;

import me.ayydxn.luminescence.config.ULConfig;
import me.ayydxn.luminescence.events.ULGamepadEvent;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ULRenderer {
   private long handle;

   public ULRenderer(ULConfig config) {
      this.handle = ULRenderer.NativeMethods.nulCreateRenderer(config.getHandle());
   }

   public void destroy() {
      if (this.handle != 0L) {
         ULRenderer.NativeMethods.nulDestroyRenderer(this.handle);
         this.handle = 0L;
      }
   }

   public void update() {
      ULRenderer.NativeMethods.nulUpdate(this.handle);
   }

   public void refreshDisplay(int displayID) {
      ULRenderer.NativeMethods.nulRefreshDisplay(this.handle, displayID);
   }

   public void render() {
      ULRenderer.NativeMethods.nulRender(this.handle);
   }

   public void purgeMemory() {
      ULRenderer.NativeMethods.nulPurgeMemory(this.handle);
   }

   public void logMemoryUsage() {
      ULRenderer.NativeMethods.nulLogMemoryUsage(this.handle);
   }

   public boolean startRemoteInspectorServer(String address, int port) {
      return ULRenderer.NativeMethods.nulStartRemoteInspectorServer(this.handle, address, port);
   }

   public void setGamepadDetails(int index, String id, int axisCount, int buttonCount) {
      ULRenderer.NativeMethods.nulSetGamepadDetails(this.handle, index, id, axisCount, buttonCount);
   }

   public void fireGamepadEvent(ULGamepadEvent gamepadEvent) {
      this.validateGamepadEventType(gamepadEvent, ULGamepadEvent.Type.REGULAR);
      ULRenderer.NativeMethods.nulFireGamepadEvent(this.handle, gamepadEvent.getHandle());
   }

   public void fireGamepadAxisEvent(ULGamepadEvent gamepadEvent) {
      this.validateGamepadEventType(gamepadEvent, ULGamepadEvent.Type.AXIS);
      ULRenderer.NativeMethods.nulFireGamepadAxisEvent(this.handle, gamepadEvent.getHandle());
   }

   public void fireGamepadButtonEvent(ULGamepadEvent gamepadEvent) {
      this.validateGamepadEventType(gamepadEvent, ULGamepadEvent.Type.BUTTON);
      ULRenderer.NativeMethods.nulFireGamepadButtonEvent(this.handle, gamepadEvent.getHandle());
   }

   private void validateGamepadEventType(ULGamepadEvent gamepadEvent, ULGamepadEvent.Type expectedEventType) {
      if (gamepadEvent.getType() != expectedEventType) {
         String message = String.format(
            "Mismatched Gamepad Event! This method requires a %s event, but the provided event is of type %s. Ensure you are using the correct factory method (e.g., ULGamepadEvent.createAxis()).",
            expectedEventType,
            gamepadEvent.getType()
         );
         throw new IllegalArgumentException(message);
      }
   }

   @Internal
   public long getHandle() {
      return this.handle;
   }

   private static final class NativeMethods {
      private static native long nulCreateRenderer(long var0);

      private static native void nulDestroyRenderer(long var0);

      private static native void nulUpdate(long var0);

      private static native void nulRefreshDisplay(long var0, int var2);

      private static native void nulRender(long var0);

      private static native void nulPurgeMemory(long var0);

      private static native void nulLogMemoryUsage(long var0);

      private static native boolean nulStartRemoteInspectorServer(long var0, String var2, int var3);

      private static native void nulSetGamepadDetails(long var0, int var2, String var3, int var4, int var5);

      private static native void nulFireGamepadEvent(long var0, long var2);

      private static native void nulFireGamepadAxisEvent(long var0, long var2);

      private static native void nulFireGamepadButtonEvent(long var0, long var2);
   }
}
