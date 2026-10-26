package de.javagl.jgltf.model.image;

import de.javagl.jgltf.model.io.Buffers;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class ImageUtils {
   private static final Logger logger = Logger.getLogger(ImageUtils.class.getName());

   static BufferedImage readAsBufferedImage(ByteBuffer byteBuffer) {
      if (byteBuffer == null) {
         return null;
      }

      try (InputStream inputStream = Buffers.createByteBufferInputStream(byteBuffer.slice())) {
         return ImageIO.read(inputStream);
      } catch (IOException e) {
         logger.severe(e.toString());
         return null;
      }
   }

   static ByteBuffer getImagePixelsARGB(BufferedImage inputImage, boolean flipVertically) {
      BufferedImage image = inputImage;
      if (flipVertically) {
         image = flipVertically(image);
      }

      if (image.getType() != 2) {
         image = convertToARGB(image);
      }

      IntBuffer imageBuffer = getBuffer(image);
      ByteBuffer outputByteBuffer = ByteBuffer.allocateDirect(imageBuffer.remaining() * 4).order(ByteOrder.BIG_ENDIAN);
      IntBuffer output = outputByteBuffer.asIntBuffer();
      output.put(imageBuffer.slice());
      return outputByteBuffer;
   }

   static ByteBuffer swizzleARGBtoRGBA(ByteBuffer pixels) {
      return swizzle(pixels, 16, 8, 0, 24);
   }

   static ByteBuffer swizzleRGBAtoARGB(ByteBuffer pixels) {
      return swizzle(pixels, 0, 24, 16, 8);
   }

   private static ByteBuffer swizzle(ByteBuffer pixels, int s0, int s1, int s2, int s3) {
      IntBuffer iBuffer = pixels.asIntBuffer();
      ByteBuffer oByteBuffer = ByteBuffer.allocateDirect(iBuffer.capacity() * 4).order(pixels.order());
      IntBuffer oBuffer = oByteBuffer.asIntBuffer();

      for (int i = 0; i < iBuffer.capacity(); i++) {
         int input = iBuffer.get(i);
         int output = swizzle(input, s0, s1, s2, s3);
         oBuffer.put(i, output);
      }

      return oByteBuffer;
   }

   private static int swizzle(int input, int s0, int s1, int s2, int s3) {
      int b0 = input >> s0 & 0xFF;
      int b1 = input >> s1 & 0xFF;
      int b2 = input >> s2 & 0xFF;
      int b3 = input >> s3 & 0xFF;
      return b0 << 24 | b1 << 16 | b2 << 8 | b3;
   }

   private static BufferedImage convertToARGB(BufferedImage image) {
      BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), 2);
      Graphics2D g = newImage.createGraphics();
      g.drawImage(image, 0, 0, null);
      g.dispose();
      return newImage;
   }

   private static BufferedImage flipVertically(BufferedImage image) {
      int w = image.getWidth();
      int h = image.getHeight();
      BufferedImage newImage = new BufferedImage(w, h, 2);
      Graphics2D g = newImage.createGraphics();
      g.drawImage(image, 0, h, w, -h, null);
      g.dispose();
      return newImage;
   }

   private static IntBuffer getBuffer(BufferedImage image) {
      DataBuffer dataBuffer = image.getRaster().getDataBuffer();
      if (!(dataBuffer instanceof DataBufferInt)) {
         throw new IllegalArgumentException("Invalid buffer type in image, only TYPE_INT_* is allowed");
      }

      DataBufferInt dataBufferInt = (DataBufferInt)dataBuffer;
      int[] data = dataBufferInt.getData();
      return IntBuffer.wrap(data);
   }

   public static BufferedImage createBufferedImage(PixelData pixelData) {
      int w = pixelData.getWidth();
      int h = pixelData.getHeight();
      BufferedImage image = new BufferedImage(w, h, 2);
      IntBuffer imageBuffer = getBuffer(image);
      ByteBuffer pixels = pixelData.getPixelsRGBA();
      ByteBuffer argbBytes = swizzleRGBAtoARGB(pixels);
      imageBuffer.put(argbBytes.asIntBuffer());
      return image;
   }

   public static BufferedImage createBufferedImageRGB(BufferedImage inputImage) {
      int w = inputImage.getWidth();
      int h = inputImage.getHeight();
      BufferedImage image = new BufferedImage(w, h, 1);
      Graphics2D g = image.createGraphics();
      g.drawImage(inputImage, 0, 0, null);
      g.dispose();
      return image;
   }

   public static ByteBuffer createImageDataBuffer(PixelData pixelData, String mimeType) {
      String formatName = null;
      BufferedImage image;
      if ("image/gif".equals(mimeType)) {
         formatName = "gif";
         image = createBufferedImage(pixelData);
      } else if ("image/jpeg".equals(mimeType)) {
         formatName = "jpg";
         BufferedImage argbImage = createBufferedImage(pixelData);
         image = createBufferedImageRGB(argbImage);
      } else {
         if (!"image/png".equals(mimeType)) {
            throw new IllegalArgumentException("The MIME type string must be \"image/gif\", \"image/jpeg\" or \"image/png\", but is " + mimeType);
         }

         formatName = "png";
         image = createBufferedImage(pixelData);
      }

      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
         ImageIO.write(image, formatName, baos);
         return Buffers.create(baos.toByteArray());
      } catch (IOException e) {
         logger.severe(e.toString());
         return null;
      }
   }

   public static void main(String[] args) {
      int input = 287454020;
      ByteBuffer b0 = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
      b0.asIntBuffer().put(input);
      ByteBuffer b1 = swizzleARGBtoRGBA(b0);
      int rgba = b1.asIntBuffer().get();
      ByteBuffer b2 = swizzleRGBAtoARGB(b1);
      int argb = b2.asIntBuffer().get();
      System.out.println("Input: " + Integer.toHexString(input));
      System.out.println("RGBA : " + Integer.toHexString(rgba));
      System.out.println("ARGB : " + Integer.toHexString(argb));
   }

   private ImageUtils() {
   }
}
