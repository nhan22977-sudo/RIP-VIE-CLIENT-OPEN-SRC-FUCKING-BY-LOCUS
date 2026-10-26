package me.ayydxn.luminescence.surface;

import me.ayydxn.luminescence.bitmap.ULBitmap;

public class ULBitmapSurface extends ULSurface {
   private ULBitmapSurface(long handle) {
      super(handle);
   }

   public static ULBitmapSurface fromSurface(ULSurface surface) {
      return new ULBitmapSurface(surface.handle);
   }

   public ULBitmap getBitmap() {
      return new ULBitmap(ULBitmapSurface.NativeMethods.nulBitmapSurfaceGetBitmap(this.handle));
   }

   private static final class NativeMethods {
      private static native long nulBitmapSurfaceGetBitmap(long var0);
   }
}
