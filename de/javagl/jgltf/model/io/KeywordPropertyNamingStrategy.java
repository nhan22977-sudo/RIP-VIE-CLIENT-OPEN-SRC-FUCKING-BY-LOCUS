package de.javagl.jgltf.model.io;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import java.lang.reflect.Field;

class KeywordPropertyNamingStrategy extends PropertyNamingStrategy {
   private static final long serialVersionUID = 1L;

   public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
      return field.getName();
   }

   public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
      return this.handleKeywordNames(method.getDeclaringClass(), defaultName);
   }

   public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
      return this.handleKeywordNames(method.getDeclaringClass(), defaultName);
   }

   private String handleKeywordNames(Class<?> c, String defaultName) {
      if (!defaultName.endsWith("Property")) {
         return defaultName;
      }

      String baseName = defaultName.substring(0, defaultName.length() - "Property".length());
      Field[] fields = c.getDeclaredFields();

      for (Field field : fields) {
         if (field.getName().equalsIgnoreCase(defaultName)) {
            return baseName;
         }
      }

      return defaultName;
   }
}
