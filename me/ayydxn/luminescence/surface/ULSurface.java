package me.ayydxn.luminescence.surface;

import java.nio.ByteBuffer;
import me.ayydxn.luminescence.geometry.ULIntRect;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ULSurface {
   protected final long handle;

   @Internal
   public ULSurface(long handle) {
      this.handle = handle;
   }

   public int getWidth() {
      return ULSurface.NativeMethods.nulSurfaceGetWidth(this.handle);
   }

   public int getHeight() {
      return ULSurface.NativeMethods.nulSurfaceGetHeight(this.handle);
   }

   public int getRowBytes() {
      return ULSurface.NativeMethods.nulSurfaceGetRowBytes(this.handle);
   }

   public long getSize() {
      return ULSurface.NativeMethods.nulSurfaceGetSize(this.handle);
   }

   public ByteBuffer lockPixels() {
      return ULSurface.NativeMethods.nulSurfaceLockPixels(this.handle);
   }

   public void unlockPixels() {
      ULSurface.NativeMethods.nulSurfaceUnlockPixels(this.handle);
   }

   public ULSurface.LockedPixels acquirePixelLock() {
      return new ULSurface.LockedPixels();
   }

   public void resize(int width, int height) {
      ULSurface.NativeMethods.nulSurfaceResize(this.handle, width, height);
   }

   public void setDirtyBounds(ULIntRect dirtyBounds) {
      ULSurface.NativeMethods.nulSurfaceSetDirtyBounds(this.handle, dirtyBounds);
   }

   public ULIntRect getDirtyBounds() {
      return ULSurface.NativeMethods.nulSurfaceGetDirtyBounds(this.handle);
   }

   public void clearDirtyBounds() {
      ULSurface.NativeMethods.nulSurfaceClearDirtyBounds(this.handle);
   }

   public long getHandle() {
      return this.handle;
   }

   public final class LockedPixels implements AutoCloseable {
      private final ByteBuffer pixels = ULSurface.this.lockPixels();

      public ByteBuffer pixels() {
         return this.pixels;
      }

      @Override
      public void close() {
         ULSurface.this.unlockPixels();
      }
   }

   private static final class NativeMethods {
      private static native int nulSurfaceGetWidth(long var0);

      private static native int nulSurfaceGetHeight(long var0);

      private static native int nulSurfaceGetRowBytes(long var0);

      private static native long nulSurfaceGetSize(long var0);

      private static native ByteBuffer nulSurfaceLockPixels(long var0);

      private static native void nulSurfaceUnlockPixels(long var0);

      private static native void nulSurfaceResize(long var0, int var2, int var3);

      private static native void nulSurfaceSetDirtyBounds(long var0, ULIntRect var2);

      private static native ULIntRect nulSurfaceGetDirtyBounds(long var0);

      private static native void nulSurfaceClearDirtyBounds(long var0);
   }
}
