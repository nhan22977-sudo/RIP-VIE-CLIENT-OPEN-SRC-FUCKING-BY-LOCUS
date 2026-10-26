package de.javagl.jgltf.model.image;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.logging.Logger;

public class PixelDatas {
   private static final Logger logger = Logger.getLogger(PixelDatas.class.getName());

   public static PixelData create(ByteBuffer imageData) {
      BufferedImage textureImage = ImageUtils.readAsBufferedImage(imageData);
      if (textureImage == null) {
         logger.warning("Could not read image from image data");
         return null;
      } else {
         return create(textureImage);
      }
   }

   public static PixelData create(BufferedImage image) {
      ByteBuffer pixelDataARGB = ImageUtils.getImagePixelsARGB(image, false);
      ByteBuffer pixelDataRGBA = ImageUtils.swizzleARGBtoRGBA(pixelDataARGB);
      int width = image.getWidth();
      int height = image.getHeight();
      return new DefaultPixelData(width, height, pixelDataRGBA);
   }

   public static PixelData createErrorPixelData() {
      ByteBuffer pixelDataRGBA = ByteBuffer.allocateDirect(128);
      IntBuffer intPixelDataRGBA = pixelDataRGBA.order(ByteOrder.BIG_ENDIAN).asIntBuffer();
      intPixelDataRGBA.put(0, -16776961);
      intPixelDataRGBA.put(1, -1);
      intPixelDataRGBA.put(2, -16776961);
      intPixelDataRGBA.put(3, -1);
      int width = 2;
      int height = 2;
      return new DefaultPixelData(width, height, pixelDataRGBA);
   }

   private PixelDatas() {
   }
}
