package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.io.MimeTypes;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.logging.Logger;

public class UriStrings {
   private static final Logger logger = Logger.getLogger(UriStrings.class.getName());

   public static String createBufferUriString(Collection<? extends String> existingUriStrings) {
      int counter = 0;

      while (true) {
         String uri = "buffer" + counter + ".bin";
         if (!existingUriStrings.contains(uri)) {
            return uri;
         }

         counter++;
      }
   }

   public static String createImageUriString(ImageModel imageModel, Collection<? extends String> existingUriStrings) {
      String extensionWithoutDot = determineImageFileNameExtension(imageModel);
      int counter = 0;

      while (true) {
         String uri = "image" + counter + "." + extensionWithoutDot;
         if (!existingUriStrings.contains(uri)) {
            return uri;
         }

         counter++;
      }
   }

   private static String determineImageFileNameExtension(ImageModel imageModel) {
      String mimeTypeString = imageModel.getMimeType();
      if (mimeTypeString == null) {
         ByteBuffer imageData = imageModel.getImageData();
         mimeTypeString = MimeTypes.guessImageMimeTypeStringUnchecked(imageData);
      }

      if (mimeTypeString != null) {
         String extensionWithoutDot = MimeTypes.imageFileNameExtensionForMimeTypeString(mimeTypeString);
         if (extensionWithoutDot != null) {
            return extensionWithoutDot;
         }
      }

      logger.warning("Could not determine file extension for image URI");
      return "";
   }

   public static String createShaderUriString(ShaderModel shaderModel, Collection<? extends String> existingUriStrings) {
      String extensionWithoutDot = determineShaderFileNameExtension(shaderModel);
      int counter = 0;

      while (true) {
         String uri = "shader" + counter + "." + extensionWithoutDot;
         if (!existingUriStrings.contains(uri)) {
            return uri;
         }

         counter++;
      }
   }

   private static String determineShaderFileNameExtension(ShaderModel shaderModel) {
      return shaderModel.getShaderType() == ShaderModel.ShaderType.VERTEX_SHADER ? "vert" : "frag";
   }

   private UriStrings() {
   }
}
