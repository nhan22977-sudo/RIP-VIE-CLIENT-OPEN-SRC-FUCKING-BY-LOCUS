package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.ModelElement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class AbstractModelElement implements ModelElement {
   private Map<String, Object> extensions;
   private Object extras;

   public void setExtensions(Map<String, Object> extensions) {
      this.extensions = extensions;
   }

   public void addExtension(String name, Object extension) {
      Objects.requireNonNull(name, "The name may not be null");
      if (this.extensions == null) {
         this.extensions = new LinkedHashMap<>();
      }

      this.extensions.put(name, extension);
   }

   public void removeExtension(String name) {
      if (this.extensions != null) {
         this.extensions.remove(name);
         if (this.extensions.isEmpty()) {
            this.extensions = null;
         }
      }
   }

   public void setExtras(Object extras) {
      this.extras = extras;
   }

   @Override
   public Map<String, Object> getExtensions() {
      return this.extensions;
   }

   @Override
   public Object getExtras() {
      return this.extras;
   }
}
