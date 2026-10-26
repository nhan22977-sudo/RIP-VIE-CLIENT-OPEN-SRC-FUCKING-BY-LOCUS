package de.javagl.jgltf.model.io.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.javagl.jgltf.impl.v1.BufferView;
import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.impl.v1.Image;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.model.GltfException;
import de.javagl.jgltf.model.io.JacksonUtils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

class GltfUtilsV1 {
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
      return copy;
   }

   static Image copy(Image image) {
      Image copy = new Image();
      copy.setExtensions(image.getExtensions());
      copy.setExtras(image.getExtras());
      copy.setName(image.getName());
      if (image.getUri() != null) {
         copy.setUri(image.getUri());
      }

      return copy;
   }

   static Shader copy(Shader shader) {
      Shader copy = new Shader();
      copy.setExtensions(shader.getExtensions());
      copy.setExtras(shader.getExtras());
      copy.setName(shader.getName());
      copy.setType(shader.getType());
      copy.setUri(shader.getUri());
      return copy;
   }

   static <K, V> Map<K, V> createMap(Map<? extends K, ?> map, Collection<? extends V> collection) {
      if (map == null) {
         return Collections.emptyMap();
      }

      if (map.size() != collection.size()) {
         throw new IllegalArgumentException("The inputs must have the same size, but the sizes are " + map.size() + " and " + collection.size());
      }

      Iterator<? extends K> iterator0 = map.keySet().iterator();
      Iterator<? extends V> iterator1 = collection.iterator();
      Map<K, V> result = new LinkedHashMap<>();

      while (iterator0.hasNext()) {
         K k = (K)iterator0.next();
         V v = (V)iterator1.next();
         result.put(k, v);
      }

      return result;
   }

   private GltfUtilsV1() {
   }
}
