package com.tagtraum.japlscript;

import com.tagtraum.japlscript.language.TypeClass;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Property {
   private final String code;
   private final String name;
   private final String javaName;
   private final Class<?> javaClass;
   private final TypeClass typeClass;

   public Property(String code, String name, String javaName, Class<?> javaClass, TypeClass typeClass) {
      Objects.requireNonNull(code);
      Objects.requireNonNull(name);
      Objects.requireNonNull(javaName);
      Objects.requireNonNull(javaClass);
      Objects.requireNonNull(typeClass);
      this.code = code;
      this.name = name;
      this.javaName = javaName;
      this.javaClass = javaClass;
      this.typeClass = typeClass;
   }

   public Property(Method method, Class<?> application) {
      this(
         method.getAnnotation(Code.class).value(),
         method.getAnnotation(Name.class).value(),
         toJavaPropertyName(method),
         method.getReturnType(),
         new TypeClass(method.getAnnotation(Type.class).value(), null, application, null).intern()
      );
   }

   private static String toJavaPropertyName(Method method) {
      String methodName = method.getName();
      return methodName.startsWith("is")
         ? methodName.substring(2, 3).toLowerCase(Locale.ROOT) + methodName.substring(3)
         : methodName.substring(3, 4).toLowerCase(Locale.ROOT) + methodName.substring(4);
   }

   public static Set<Property> fromAnnotations(Class<?> klass, Class<?> applicationInterface) {
      return Arrays.stream(klass.getMethods())
         .filter(m -> !m.getReturnType().equals(void.class))
         .filter(m -> m.getAnnotation(Kind.class) != null && m.getAnnotation(Kind.class).value().equals("property"))
         .filter(m -> !m.getDeclaringClass().equals(Reference.class))
         .map(method -> new Property(method, applicationInterface))
         .collect(Collectors.toSet());
   }

   public String getCode() {
      return this.code;
   }

   public String getName() {
      return this.name;
   }

   public String getJavaName() {
      return this.javaName;
   }

   public Class<?> getJavaClass() {
      return this.javaClass;
   }

   public TypeClass getTypeClass() {
      return this.typeClass;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (o != null && this.getClass() == o.getClass()) {
         Property property = (Property)o;
         if (!this.code.equals(property.code)) {
            return false;
         } else if (!this.name.equals(property.name)) {
            return false;
         } else if (!this.javaName.equals(property.javaName)) {
            return false;
         } else {
            return !this.javaClass.equals(property.javaClass) ? false : this.typeClass.equals(property.typeClass);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.code.hashCode();
      result = 31 * result + this.name.hashCode();
      result = 31 * result + this.javaName.hashCode();
      result = 31 * result + this.javaClass.hashCode();
      return 31 * result + this.typeClass.hashCode();
   }

   public Chevron toChevron() {
      return new Chevron("property", this.code);
   }

   @Override
   public String toString() {
      return this.toChevron()
         + "[name="
         + this.name
         + ",javaName="
         + this.javaName
         + ",javaClass="
         + this.javaClass.getName()
         + ",typeClass="
         + this.typeClass
         + "]";
   }
}
