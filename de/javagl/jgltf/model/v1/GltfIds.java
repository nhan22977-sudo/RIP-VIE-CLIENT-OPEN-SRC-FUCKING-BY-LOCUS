package de.javagl.jgltf.model.v1;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class GltfIds {
   public static String generateId(String prefix, Map<? extends String, ?> map) {
      Set<? extends String> set = Collections.emptySet();
      if (map != null) {
         set = map.keySet();
      }

      return generateId(prefix, set);
   }

   public static String generateId(String prefix, Set<? extends String> set) {
      Set<? extends String> localSet = Collections.emptySet();
      if (set != null) {
         localSet = set;
      }

      int counter = localSet.size();

      while (true) {
         String id = prefix + counter;
         if (!localSet.contains(id)) {
            return id;
         }

         counter++;
      }
   }

   private GltfIds() {
   }
}
