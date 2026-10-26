package me.ayydxn.luminescence.util;

import me.ayydxn.luminescence.bitmap.ULBitmap;
import me.ayydxn.luminescence.geometry.ULRect;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ULImageSource {
   private long handle;

   private ULImageSource(long handle) {
      this.handle = handle;
   }

   public static ULImageSource createFromTexture(int width, int height, int textureID, ULRect textureUV, ULBitmap bitmap) {
      long handle = ULImageSource.NativeMethods.nulCreateImageSourceFromTexture(width, height, textureID, textureUV, bitmap);
      return new ULImageSource(handle);
   }

   public static ULImageSource createFromBitmap(ULBitmap bitmap) {
      return new ULImageSource(ULImageSource.NativeMethods.nulCreateImageSourceFromBitmap(bitmap));
   }

   public void destroy() {
      if (this.handle != 0L) {
         ULImageSource.NativeMethods.nulDestroyImageSource(this.handle);
         this.handle = 0L;
      }
   }

   public void invalidate() {
      ULImageSource.NativeMethods.nulImageSourceInvalidate(this.handle);
   }

   public void addImageSourceToProvider(String id) {
      ULImageSource.NativeMethods.nulImageSourceProviderAddImageSource(id, this.handle);
   }

   public void removeImageSourceFromProvider(String id) {
      ULImageSource.NativeMethods.nulImageSourceProviderRemoveImageSource(id);
   }

   @Internal
   public long getHandle() {
      return this.handle;
   }

   private static final class NativeMethods {
      private static native long nulCreateImageSourceFromTexture(int var0, int var1, int var2, ULRect var3, ULBitmap var4);

      private static native long nulCreateImageSourceFromBitmap(ULBitmap var0);

      private static native void nulDestroyImageSource(long var0);

      private static native void nulImageSourceInvalidate(long var0);

      private static native void nulImageSourceProviderAddImageSource(String var0, long var1);

      private static native void nulImageSourceProviderRemoveImageSource(String var0);
   }
}
