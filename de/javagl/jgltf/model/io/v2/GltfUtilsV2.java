package de.javagl.jgltf.model.io.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.model.GltfException;
import de.javagl.jgltf.model.io.JacksonUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

class GltfUtilsV2 {
   static GlTF copy(GlTF gltf) {
      ObjectMapper objectMapper = JacksonUtils.createObjectMapper();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try {
         objectMapper.writeValue(baos, gltf);
         return (GlTF)objectMapper.readValue(baos.toByteArray(), GlTF.class);
      } catch (IOException e) {
         throw new GltfException("Could not copy glTF", e);
      }
   }

   static BufferView copy(BufferView bufferView) {
      BufferView copy = new BufferView();
      copy.setExtensions(bufferView.getExtensions());
      copy.setExtras(bufferView.getExtras());
      copy.setName(bufferView.getName());
      copy.setBuffer(bufferView.getBuffer());
      copy.setByteOffset(bufferView.getByteOffset());
      copy.setByteLength(bufferView.getByteLength());
      copy.setTarget(bufferView.getTarget());
      copy.setByteStride(bufferView.getByteStride());
      return copy;
   }

   static Image copy(Image image) {
      Image copy = new Image();
      copy.setExtensions(image.getExtensions());
      copy.setExtras(image.getExtras());
      copy.setName(image.getName());
      copy.setUri(image.getUri());
      copy.setBufferView(image.getBufferView());
      copy.setMimeType(image.getMimeType());
      return copy;
   }

   private GltfUtilsV2() {
   }
}
