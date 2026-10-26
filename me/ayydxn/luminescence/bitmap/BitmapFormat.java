package me.ayydxn.luminescence.bitmap;

public enum BitmapFormat {
   A8_UNORM(1),
   BGRA8_UNORM_SRGB(4);

   private final int bytesPerPixel;

   BitmapFormat(int bytesPerPixel) {
      this.bytesPerPixel = bytesPerPixel;
   }

   public static BitmapFormat fromNativeValue(int nativeValue) {
      return switch (nativeValue) {
         case 0 -> A8_UNORM;
         case 1 -> BGRA8_UNORM_SRGB;
         default -> throw new IllegalArgumentException("Unexpected bitmap format native value: " + nativeValue);
      };
   }

   public int getBytesPerPixel() {
      return this.bytesPerPixel;
   }

   // $VF: synthetic method
   private static BitmapFormat[] $values() {
      return new BitmapFormat[]{A8_UNORM, BGRA8_UNORM_SRGB};
   }
}
