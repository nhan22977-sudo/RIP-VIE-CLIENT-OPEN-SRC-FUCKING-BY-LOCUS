package de.javagl.jgltf.model.v1;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

class IndexMappings {
   static Map<String, Integer> computeIndexMapping(Map<String, ?> map) {
      if (map == null) {
         return Collections.emptyMap();
      }

      Map<String, Integer> indexMapping = new LinkedHashMap<>();
      int indexCounter = 0;

      for (String key : map.keySet()) {
         indexMapping.put(key, indexCounter);
         indexCounter++;
      }

      return indexMapping;
   }

   private IndexMappings() {
   }
}
