package de.javagl.jgltf.model.image;

import java.nio.ByteBuffer;

final class DefaultPixelData implements PixelData {
   private final int width;
   private final int height;
   private final ByteBuffer pixelsRGBA;

   DefaultPixelData(int width, int height, ByteBuffer pixelsRGBA) {
      this.width = width;
      this.height = height;
      this.pixelsRGBA = pixelsRGBA;
   }

   @Override
   public int getWidth() {
      return this.width;
   }

   @Override
   public int getHeight() {
      return this.height;
   }

   @Override
   public ByteBuffer getPixelsRGBA() {
      return this.pixelsRGBA.slice();
   }
}
