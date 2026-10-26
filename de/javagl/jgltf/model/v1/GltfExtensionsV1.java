package de.javagl.jgltf.model.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.impl.v1.GlTFProperty;
import de.javagl.jgltf.model.io.JacksonUtils;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GltfExtensionsV1 {
   public static void addExtensionUsed(GlTF gltf, String extensionName) {
      List<String> oldExtensionsUsed = gltf.getExtensionsUsed();
      if (oldExtensionsUsed == null || !oldExtensionsUsed.contains(extensionName)) {
         gltf.addExtensionsUsed(extensionName);
      }
   }

   public static void removeExtensionUsed(GlTF gltf, String extensionName) {
      List<String> oldExtensionsUsed = gltf.getExtensionsUsed();
      if (oldExtensionsUsed != null && oldExtensionsUsed.contains(extensionName)) {
         gltf.removeExtensionsUsed(extensionName);
      }
   }

   private static Map<String, Object> getExtensionMap(GlTFProperty gltfProperty, String extensionName) {
      Map<String, Object> extensions = gltfProperty.getExtensions();
      if (extensions == null) {
         return null;
      } else {
         Object value = extensions.get(extensionName);
         if (value == null) {
            return null;
         } else {
            return value instanceof Map ? (Map)value : null;
         }
      }
   }

   static boolean hasExtension(GlTFProperty gltfProperty, String extensionName) {
      return getExtensionMap(gltfProperty, extensionName) != null;
   }

   static String getExtensionPropertyValueAsString(GlTFProperty gltfProperty, String extensionName, String propertyName) {
      Map<String, Object> extensionMap = getExtensionMap(gltfProperty, extensionName);
      if (extensionMap == null) {
         return null;
      }

      Object value = extensionMap.get(propertyName);
      return value == null ? null : String.valueOf(value);
   }

   static void setExtensionPropertyValue(GlTFProperty gltfProperty, String extensionName, String propertyName, Object propertyValue) {
      Map<String, Object> extensionMap = getExtensionMap(gltfProperty, extensionName);
      if (extensionMap == null) {
         extensionMap = new LinkedHashMap<>();
         gltfProperty.addExtensions(extensionName, extensionMap);
      }

      extensionMap.put(propertyName, propertyValue);
   }

   static <T> T fetchExtensionObject(GlTFProperty gltfProperty, String extensionName, Class<T> type) {
      Map<String, Object> extensions = gltfProperty.getExtensions();
      if (extensions == null) {
         return null;
      }

      Object extensionObject = extensions.get(extensionName);
      if (extensionObject == null) {
         return null;
      }

      ObjectMapper objectMapper = JacksonUtils.createObjectMapper();
      return (T)objectMapper.convertValue(extensionObject, type);
   }

   private GltfExtensionsV1() {
   }
}
