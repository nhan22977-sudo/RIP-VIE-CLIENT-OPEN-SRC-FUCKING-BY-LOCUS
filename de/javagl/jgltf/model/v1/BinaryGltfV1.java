package de.javagl.jgltf.model.v1;

import de.javagl.jgltf.impl.v1.GlTFProperty;
import de.javagl.jgltf.impl.v1.Image;
import de.javagl.jgltf.model.GltfException;
import de.javagl.jgltf.model.image.ImageReaders;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageReader;

public class BinaryGltfV1 {
   private static final String KHRONOS_BINARY_GLTF_EXTENSION_NAME = "KHR_binary_glTF";
   private static final String BINARY_GLTF_BUFFER_ID = "binary_glTF";

   public static boolean hasBinaryGltfExtension(GlTFProperty gltfProperty) {
      return GltfExtensionsV1.hasExtension(gltfProperty, "KHR_binary_glTF");
   }

   public static String getBinaryGltfBufferViewId(GlTFProperty gltfProperty) {
      return GltfExtensionsV1.getExtensionPropertyValueAsString(gltfProperty, "KHR_binary_glTF", "bufferView");
   }

   public static void setBinaryGltfBufferViewId(GlTFProperty gltfProperty, String bufferViewId) {
      GltfExtensionsV1.setExtensionPropertyValue(gltfProperty, "KHR_binary_glTF", "bufferView", bufferViewId);
   }

   public static void setBinaryGltfImageProperties(Image image, ByteBuffer imageData) {
      ImageReader imageReader = null;

      try {
         imageReader = ImageReaders.findImageReader(imageData);
         int width = imageReader.getWidth(0);
         int height = imageReader.getHeight(0);
         String mimeType = "image/" + imageReader.getFormatName();
         GltfExtensionsV1.setExtensionPropertyValue(image, "KHR_binary_glTF", "width", width);
         GltfExtensionsV1.setExtensionPropertyValue(image, "KHR_binary_glTF", "height", height);
         GltfExtensionsV1.setExtensionPropertyValue(image, "KHR_binary_glTF", "mimeType", mimeType);
      } catch (IOException e) {
         throw new GltfException("Could not derive image properties for binary glTF", e);
      } finally {
         if (imageReader != null) {
            imageReader.dispose();
         }
      }
   }

   public static String getBinaryGltfExtensionName() {
      return "KHR_binary_glTF";
   }

   public static String getBinaryGltfBufferId() {
      return "binary_glTF";
   }

   public static boolean isBinaryGltfBufferId(String id) {
      return "binary_glTF".equals(id);
   }

   private BinaryGltfV1() {
   }
}
