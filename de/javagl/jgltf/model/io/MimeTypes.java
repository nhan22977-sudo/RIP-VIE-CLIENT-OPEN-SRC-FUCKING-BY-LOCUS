package de.javagl.jgltf.model.io;

import de.javagl.jgltf.model.image.ImageReaders;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import javax.imageio.ImageReader;

public class MimeTypes {
   private static final Logger logger = Logger.getLogger(MimeTypes.class.getName());

   private static String guessImageMimeTypeString(String uriString) {
      try {
         URI uri = new URI(uriString);
         if ("data".equalsIgnoreCase(uri.getScheme())) {
            String raw = uri.getRawSchemeSpecificPart().toLowerCase();
            String type = getStringBetween(raw, "image/", ";base64");
            return "image/" + type.toLowerCase();
         }
      } catch (URISyntaxException e) {
         return null;
      }

      int lastDotIndex = uriString.lastIndexOf(46);
      if (lastDotIndex == -1) {
         return null;
      }

      String end = uriString.substring(lastDotIndex + 1).toLowerCase();
      return !end.equalsIgnoreCase("jpg") && !end.equalsIgnoreCase("jpeg") ? "image/" + end.toLowerCase() : "image/jpeg";
   }

   private static String getStringBetween(String input, String before, String after) {
      int beforeIndex = input.indexOf(before);
      if (beforeIndex < 0) {
         return null;
      }

      int afterIndex = input.indexOf(after);
      return afterIndex < beforeIndex ? null : input.substring(beforeIndex + before.length(), afterIndex);
   }

   private static String guessImageMimeTypeString(ByteBuffer imageData) throws IOException {
      ImageReader imageReader = null;

      try {
         imageReader = ImageReaders.findImageReader(imageData);
         return "image/" + imageReader.getFormatName().toLowerCase();
      } finally {
         if (imageReader != null) {
            imageReader.dispose();
         }
      }
   }

   public static String guessImageMimeTypeStringUnchecked(ByteBuffer imageData) {
      try {
         return guessImageMimeTypeString(imageData);
      } catch (IOException e) {
         return null;
      }
   }

   public static String guessImageMimeTypeString(String uriString, ByteBuffer imageData) {
      if (uriString != null) {
         String imageMimeTypeString = guessImageMimeTypeString(uriString);
         if (imageMimeTypeString != null) {
            return imageMimeTypeString;
         }
      }

      return imageData != null ? guessImageMimeTypeStringUnchecked(imageData) : null;
   }

   public static String imageFileNameExtensionForMimeTypeString(String mimeTypeString) {
      if ("image/jpeg".equals(mimeTypeString)) {
         return "jpg";
      }

      if ("image/png".equals(mimeTypeString)) {
         return "png";
      }

      if ("image/gif".equals(mimeTypeString)) {
         return "gif";
      }

      logger.warning("Invalid MIME type string: " + mimeTypeString);
      return null;
   }

   private MimeTypes() {
   }
}
