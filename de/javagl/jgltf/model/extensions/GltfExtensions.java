package de.javagl.jgltf.model.extensions;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.javagl.jgltf.impl.v1.GlTFProperty;
import de.javagl.jgltf.model.io.JacksonUtils;
import java.util.Map;

public class GltfExtensions {
   public static <T> T obtain(Object gltfProperty, String extensionName, Class<T> extensionType) {
      if (gltfProperty instanceof GlTFProperty) {
         GlTFProperty gltfPropertyV1 = (GlTFProperty)gltfProperty;
         Map<String, Object> extensions = gltfPropertyV1.getExtensions();
         return obtainInternal(extensions, extensionName, extensionType);
      } else if (gltfProperty instanceof de.javagl.jgltf.impl.v2.GlTFProperty) {
         de.javagl.jgltf.impl.v2.GlTFProperty gltfPropertyV2 = (de.javagl.jgltf.impl.v2.GlTFProperty)gltfProperty;
         Map<String, Object> extensions = gltfPropertyV2.getExtensions();
         return obtainInternal(extensions, extensionName, extensionType);
      } else {
         throw new IllegalArgumentException("Not a valid glTF property: " + gltfProperty);
      }
   }

   private static <T> T obtainInternal(Map<String, Object> extensions, String extensionName, Class<T> extensionType) {
      if (extensions == null) {
         return null;
      }

      Object object = extensions.get(extensionName);
      return object == null ? null : convertValueOptional(object, extensionType);
   }

   private static <T> T convertValue(Object object, Class<T> type) throws IllegalArgumentException {
      ObjectMapper objectMapper = JacksonUtils.createObjectMapper();
      return (T)objectMapper.convertValue(object, type);
   }

   private static <T> T convertValueOptional(Object object, Class<T> type) {
      try {
         return convertValue(object, type);
      } catch (IllegalArgumentException e) {
         return null;
      }
   }

   private GltfExtensions() {
   }
}
