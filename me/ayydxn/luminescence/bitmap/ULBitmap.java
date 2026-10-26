package me.ayydxn.luminescence.bitmap;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ULBitmap {
   private long handle;

   @Internal
   public ULBitmap(long handle) {
      this.handle = handle;
   }

   public static ULBitmap createEmpty() {
      return new ULBitmap(ULBitmap.NativeMethods.nulCreateEmptyBitmap());
   }

   public static ULBitmap create(int width, int height, BitmapFormat format) {
      return new ULBitmap(ULBitmap.NativeMethods.nulCreateBitmap(width, height, format.ordinal()));
   }

   public static ULBitmap createFromPixels(int width, int height, BitmapFormat format, ByteBuffer pixels, boolean shouldCopy) {
      if (!pixels.isDirect()) {
         throw new IllegalArgumentException("Bitmap pixels must be a direct ByteBuffer!");
      } else {
         int bytesPerPixel = format.getBytesPerPixel();
         int rowBytes = width * bytesPerPixel;
         long capacity = pixels.capacity();
         long requiredSize = (long)rowBytes * height;
         if (capacity < requiredSize) {
            throw new IllegalArgumentException(
               String.format(
                  "ByteBuffer capacity (%d bytes) is too small for %dx%d %s bitmap (requires %d bytes)!", capacity, width, height, format.name(), requiredSize
               )
            );
         } else {
            long bitmapHandle = ULBitmap.NativeMethods.nulCreateBitmapFromPixels(width, height, format.ordinal(), rowBytes, pixels, capacity, shouldCopy);
            if (bitmapHandle == 0L) {
               throw new IllegalStateException(
                  "Failed to create native ULBitmap! This usually occurs if the ByteBuffer address could not be retrieved by JNI or the native engine encountered an allocation error."
               );
            } else {
               return new ULBitmap(bitmapHandle);
            }
         }
      }
   }

   public static ULBitmap createFromCopy(ULBitmap otherBitmap) {
      return new ULBitmap(ULBitmap.NativeMethods.nulCreateBitmapFromCopy(otherBitmap.handle));
   }

   public void destroy() {
      if (this.handle != 0L) {
         ULBitmap.NativeMethods.nulDestroyBitmap(this.handle);
         this.handle = 0L;
      }
   }

   public int getWidth() {
      return ULBitmap.NativeMethods.nulBitmapGetWidth(this.handle);
   }

   public int getHeight() {
      return ULBitmap.NativeMethods.nulBitmapGetHeight(this.handle);
   }

   public BitmapFormat getFormat() {
      return BitmapFormat.fromNativeValue(ULBitmap.NativeMethods.nulBitmapGetFormat(this.handle));
   }

   public int getBytesPerPixel() {
      return ULBitmap.NativeMethods.nulBitmapGetBpp(this.handle);
   }

   public int getRowBytes() {
      return ULBitmap.NativeMethods.nulBitmapGetRowBytes(this.handle);
   }

   public int getSize() {
      return ULBitmap.NativeMethods.nulBitmapGetSize(this.handle);
   }

   public boolean ownsPixels() {
      return ULBitmap.NativeMethods.nulBitmapOwnsPixels(this.handle);
   }

   public ByteBuffer lockPixels() {
      return ULBitmap.NativeMethods.nulBitmapLockPixels(this.handle);
   }

   public void unlockPixels() {
      ULBitmap.NativeMethods.nulBitmapUnlockPixels(this.handle);
   }

   public ULBitmap.LockedPixels acquirePixelLock() {
      return new ULBitmap.LockedPixels();
   }

   public ByteBuffer rawPixels() {
      return ULBitmap.NativeMethods.nulBitmapRawPixels(this.handle);
   }

   public boolean isEmpty() {
      return ULBitmap.NativeMethods.nulBitmapIsEmpty(this.handle);
   }

   public void erase() {
      ULBitmap.NativeMethods.nulBitmapErase(this.handle);
   }

   public boolean writePNG(Path path) {
      return ULBitmap.NativeMethods.nulBitmapWritePNG(this.handle, path.toString());
   }

   public void swapRedBlueChannels() {
      ULBitmap.NativeMethods.nulBitmapSwapRedBlueChannels(this.handle);
   }

   public final class LockedPixels implements AutoCloseable {
      private final ByteBuffer pixels = ULBitmap.this.lockPixels();

      private LockedPixels() {
      }

      public ByteBuffer pixels() {
         return this.pixels;
      }

      @Override
      public void close() {
         ULBitmap.this.unlockPixels();
      }
   }

   private static final class NativeMethods {
      private static native long nulCreateEmptyBitmap();

      private static native long nulCreateBitmap(int var0, int var1, int var2);

      private static native long nulCreateBitmapFromPixels(int var0, int var1, int var2, int var3, ByteBuffer var4, long var5, boolean var7);

      private static native long nulCreateBitmapFromCopy(long var0);

      private static native void nulDestroyBitmap(long var0);

      private static native int nulBitmapGetWidth(long var0);

      private static native int nulBitmapGetHeight(long var0);

      private static native int nulBitmapGetFormat(long var0);

      private static native int nulBitmapGetBpp(long var0);

      private static native int nulBitmapGetRowBytes(long var0);

      private static native int nulBitmapGetSize(long var0);

      private static native boolean nulBitmapOwnsPixels(long var0);

      private static native ByteBuffer nulBitmapLockPixels(long var0);

      private static native void nulBitmapUnlockPixels(long var0);

      private static native ByteBuffer nulBitmapRawPixels(long var0);

      private static native boolean nulBitmapIsEmpty(long var0);

      private static native void nulBitmapErase(long var0);

      private static native boolean nulBitmapWritePNG(long var0, String var2);

      private static native void nulBitmapSwapRedBlueChannels(long var0);
   }
}
