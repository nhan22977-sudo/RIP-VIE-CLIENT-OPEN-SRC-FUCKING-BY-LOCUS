package de.javagl.jgltf.model.v1;

import java.util.LinkedHashMap;
import java.util.Map;

final class IndexMappingSet {
   private final Map<Object, Map<String, Integer>> indexMappings = new LinkedHashMap<>();

   private Map<String, Integer> get(Object name) {
      return this.indexMappings.computeIfAbsent(name, n -> new LinkedHashMap<>());
   }

   void generate(Object name, Map<String, ?> map) {
      if (map != null) {
         this.get(name).putAll(IndexMappings.computeIndexMapping(map));
      }
   }

   Integer getIndex(String name, String key) {
      return key == null ? null : this.get(name).get(key);
   }
}
