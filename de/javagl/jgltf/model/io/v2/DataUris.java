package de.javagl.jgltf.model.io.v2;

import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.GltfException;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.io.MimeTypes;
import java.nio.ByteBuffer;
import java.util.Base64;

public class DataUris {
   public static String createBufferDataUri(BufferModel bufferModel) {
      ByteBuffer bufferData = bufferModel.getBufferData();
      byte[] data = new byte[bufferData.capacity()];
      bufferData.slice().get(data);
      String encodedData = Base64.getEncoder().encodeToString(data);
      return "data:application/gltf-buffer;base64," + encodedData;
   }

   public static String createImageDataUri(String currentUri, ImageModel imageModel) {
      ByteBuffer imageData = imageModel.getImageData();
      String imageMimeTypeString = MimeTypes.guessImageMimeTypeString(currentUri, imageData);
      if (imageMimeTypeString == null) {
         throw new GltfException("Could not detect MIME type of image");
      }

      byte[] data = new byte[imageData.capacity()];
      imageData.slice().get(data);
      String encodedData = Base64.getEncoder().encodeToString(data);
      return "data:" + imageMimeTypeString + ";base64," + encodedData;
   }
}
