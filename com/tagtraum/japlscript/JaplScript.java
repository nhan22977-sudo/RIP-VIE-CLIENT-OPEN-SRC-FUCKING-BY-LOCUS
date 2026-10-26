package com.tagtraum.japlscript;

import com.tagtraum.japlscript.execution.Aspect;
import com.tagtraum.japlscript.execution.JaplScriptException;
import com.tagtraum.japlscript.execution.Session;
import com.tagtraum.japlscript.language.Alias;
import com.tagtraum.japlscript.language.Boolean;
import com.tagtraum.japlscript.language.Data;
import com.tagtraum.japlscript.language.Date;
import com.tagtraum.japlscript.language.Double;
import com.tagtraum.japlscript.language.Float;
import com.tagtraum.japlscript.language.Integer;
import com.tagtraum.japlscript.language.JaplScriptFile;
import com.tagtraum.japlscript.language.Long;
import com.tagtraum.japlscript.language.Picture;
import com.tagtraum.japlscript.language.Point;
import com.tagtraum.japlscript.language.RGBColor;
import com.tagtraum.japlscript.language.Record;
import com.tagtraum.japlscript.language.Rectangle;
import com.tagtraum.japlscript.language.ReferenceImpl;
import com.tagtraum.japlscript.language.Short;
import com.tagtraum.japlscript.language.Tdta;
import com.tagtraum.japlscript.language.Text;
import com.tagtraum.japlscript.language.TypeClass;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public final class JaplScript {
   private static final Logger LOG = Logger.getLogger(JaplScript.class.getName());
   private static final int LAST_ASCII_CHAR = 127;
   private static final List<Codec<?>> types = new ArrayList<>();
   private static final List<Aspect> globalAspects = new ArrayList<>();
   private static final Map<String, Class<?>> applicationInterfaces = new HashMap<>();
   private static final Map<Class<?>, Map<TypeClass, Map<String, Property>>> applicationProperties = new HashMap<>();
   private static final Map<Class<?>, Map<TypeClass, Class<?>>> applicationClasses = new HashMap<>();
   private static final Map<String, Class<?>> APPLESCRIPT_TO_JAVA = new HashMap<>();
   private static final String SCRIPTING_ADDITION = "scripting addition";
   private static final String APPLICATION = "application";

   private static void addDefaultTypes() {
      addType(Text.getInstance());
      addType(Integer.getInstance());
      addType(Short.getInstance());
      addType(Long.getInstance());
      addType(Float.getInstance());
      addType(Double.getInstance());
      addType(Boolean.getInstance());
      addType(Date.getInstance());
      addType(Alias.getInstance());
      addType(Data.getInstance());
      addType(Picture.getInstance());
      addType(Tdta.getInstance());
      addType(JaplScriptFile.getInstance());
      addType(Point.getInstance());
      addType(Rectangle.getInstance());
      addType(RGBColor.getInstance());
      addType(TypeClass.getInstance());
      addType(Record.getInstance());
      addType(ReferenceImpl.getInstance());
   }

   private static void addDefaultGlobalAspects() {
      addGlobalAspect(new JaplScript.DateHelper());
      addGlobalAspect(new JaplScript.Tell());
   }

   private JaplScript() {
   }

   public static void addGlobalAspect(Aspect aspect) {
      globalAspects.add(aspect);
   }

   public static boolean removeGlobalAspect(Aspect aspect) {
      return globalAspects.remove(aspect);
   }

   public static List<Aspect> getGlobalAspects() {
      return new ArrayList<>(globalAspects);
   }

   public static void addType(Codec<?> type) {
      types.add(type);

      for (TypeClass tc : type._getAppleScriptTypes()) {
         APPLESCRIPT_TO_JAVA.put(tc.getName().toLowerCase(), type._getJavaType());
      }
   }

   public static boolean removeType(Codec<?> type) {
      return types.remove(type);
   }

   public static List<Codec<?>> getTypes() {
      return new ArrayList<>(types);
   }

   public static Session startSession() {
      return Session.startSession();
   }

   public static <T> T getApplication(Class<T> interfaceClass, String applicationName) {
      Reference reference = new ReferenceImpl(null, "application \"" + applicationName + "\"");
      registerApplicationInterface(interfaceClass, reference);
      registerApplicationProperties(interfaceClass);
      return cast(interfaceClass, reference);
   }

   public static <T> T getScriptingAddition(Class<T> interfaceClass, String scriptingAdditionName) {
      Reference reference = new ReferenceImpl(null, "scripting addition \"" + scriptingAdditionName + "\"");
      registerApplicationInterface(interfaceClass, reference);
      registerApplicationProperties(interfaceClass);
      return cast(interfaceClass, reference);
   }

   private static <T> void registerApplicationInterface(Class<T> interfaceClass, Reference reference) {
      if (!applicationInterfaces.containsKey(reference.getApplicationReference())) {
         applicationInterfaces.put(reference.getApplicationReference(), interfaceClass);
      }
   }

   public static TypeClass internTypeClass(TypeClass typeClass) {
      Class<?> applicationInterface;
      if (typeClass.getApplicationInterface() != null) {
         applicationInterface = typeClass.getApplicationInterface();
      } else {
         if (typeClass.getApplicationReference() == null) {
            LOG.warning(
               "TypeClass intern failure: Attempting to intern a TypeClass that has neither an application interface nor an application reference: "
                  + typeClass
            );
            return typeClass;
         }

         applicationInterface = getApplicationInterface(typeClass);
         if (applicationInterface == null) {
            LOG.warning("TypeClass intern failure: Failed to find application interface for application reference " + typeClass.getApplicationReference());
            return typeClass;
         }
      }

      Map<TypeClass, Map<String, Property>> typeClassMap = applicationProperties.get(applicationInterface);

      for (TypeClass appTypeClass : typeClassMap.keySet()) {
         if (Objects.equals(appTypeClass.getCode(), typeClass.getCode()) || Objects.equals(appTypeClass.getName(), typeClass.getName())) {
            return appTypeClass;
         }
      }

      if (getStandardJavaType(typeClass.getObjectReference()) == null) {
         LOG.warning("TypeClass intern failure: TypeClass " + typeClass + " is not declared in " + applicationInterface.getName());
      }

      return typeClass;
   }

   private static Class<?> getApplicationInterface(Reference reference) {
      return applicationInterfaces.get(reference.getApplicationReference());
   }

   public static Property getProperty(Reference reference, TypeClass typeClass, String name) {
      if (reference.getApplicationReference() == null) {
         throw new JaplScriptException("Property lookup failure. Cannot lookup property for null application reference: " + reference);
      }

      Class<?> applicationInterface = getApplicationInterface(reference);
      if (applicationInterface == null) {
         LOG.warning("An application interface for application reference " + reference.getApplicationReference() + " has not been registered.");
         return null;
      }

      Map<TypeClass, Map<String, Property>> typeClassMap = applicationProperties.get(applicationInterface);

      for (TypeClass appTypeClass = typeClass; appTypeClass != null; appTypeClass = appTypeClass.getSuperClass()) {
         if (!typeClassMap.containsKey(typeClass)) {
            LOG.warning("TypeClass " + typeClass + " of property " + name + " is not declared in " + applicationInterface.getSimpleName());
            return null;
         }

         Property property = typeClassMap.get(typeClass).get(name);
         if (property != null) {
            return property;
         }
      }

      return null;
   }

   private static void registerApplicationProperties(Class<?> applicationInterface) {
      if (!applicationProperties.containsKey(applicationInterface)) {
         Map<TypeClass, Map<String, Property>> appMap = new HashMap<>();
         Map<TypeClass, Class<?>> appClassMap = new HashMap<>();
         applicationProperties.put(applicationInterface, appMap);
         JaplScript.applicationClasses.put(applicationInterface, appClassMap);

         try {
            Field applicationClassesField = applicationInterface.getField("APPLICATION_CLASSES");
            Set<Class<?>> applicationClasses = (Set<Class<?>>)applicationClassesField.get(null);

            for (Class<?> klass : applicationClasses) {
               TypeClass typeClass = TypeClass.fromClass(klass);
               if (typeClass == null) {
                  throw new JaplScriptException("Generated class " + klass + " does not declare CLASS.");
               }

               appMap.put(typeClass, new HashMap<>());
               appClassMap.put(typeClass, klass);
            }

            for (Class<?> klass : applicationClasses) {
               Map<String, Property> classProperties = appMap.get(TypeClass.fromClass(klass));

               for (Property property : Property.fromAnnotations(klass, applicationInterface)) {
                  classProperties.put(property.getName(), property);
                  classProperties.put(property.toChevron().toString(), property);
               }
            }
         } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new JaplScriptException("Failure while registering application-wide properties", e);
         }
      }
   }

   public static <T> Class<? extends T> guessMostSpecificSubclass(Class<T> interfaceClass, Reference reference) {
      if (reference == null) {
         return interfaceClass;
      }

      if (interfaceClass.isArray()) {
         return interfaceClass;
      }

      Class<?> applicationInterface = getApplicationInterface(reference);
      if (applicationInterface == null) {
         LOG.fine(
            "An application interface for application reference "
               + reference.getApplicationReference()
               + " has not been registered, so we cannot look up more specific types."
         );
         return interfaceClass;
      }

      String objectReference = reference.getObjectReference();
      if (objectReference == null) {
         return interfaceClass;
      }

      String trimmedObjectReference = objectReference.trim();
      TypeClass typeClass = null;
      if (trimmedObjectReference.startsWith("«class ")) {
         String code = trimmedObjectReference.substring(0, trimmedObjectReference.indexOf("»") + 1);
         TypeClass tc = new TypeClass(code, code, reference.getApplicationReference(), applicationInterface, null);
         typeClass = tc.intern();
      } else {
         for (TypeClass tc : applicationProperties.get(applicationInterface).keySet()) {
            if (trimmedObjectReference.startsWith(tc.getName())) {
               typeClass = tc;
               break;
            }
         }
      }

      if (typeClass != null) {
         Class<?> interfaceClassSubClass = applicationClasses.get(applicationInterface).get(typeClass);
         if (!interfaceClass.equals(interfaceClassSubClass) && interfaceClassSubClass != null && interfaceClass.isAssignableFrom(interfaceClassSubClass)) {
            LOG.fine("Mapped requested class " + interfaceClass + " to more specific class " + interfaceClassSubClass + " for " + reference);
            return (Class<? extends T>)interfaceClassSubClass;
         }
      }

      return interfaceClass;
   }

   public static <T> T cast(Class<T> interfaceClass, Reference reference) {
      return cast(interfaceClass, false, reference);
   }

   public static <T> T cast(Class<T> interfaceClass, boolean useMostSpecificSubClass, Reference reference) {
      if (reference == null) {
         return null;
      }

      try {
         String objectReference = reference.getObjectReference();
         Class<? extends T> icc = interfaceClass;
         if (useMostSpecificSubClass) {
            icc = guessMostSpecificSubclass(interfaceClass, reference);
         }

         for (Codec<?> type : types) {
            if (icc == type._getJavaType()) {
               return (T)type._decode(reference);
            }
         }

         if (icc.isArray()) {
            return (T)(objectReference == null ? null : parseList(icc.getComponentType(), useMostSpecificSubClass, reference));
         } else if (icc.equals(Map.class)) {
            return (T)(objectReference == null ? Collections.emptyMap() : parseRecord(reference));
         } else if (objectReference != null && objectReference.trim().length() == 0) {
            return null;
         } else if (JaplEnum.class.isAssignableFrom(icc) && Codec.class.isAssignableFrom(icc)) {
            T firstConstant = (T)icc.getEnumConstants()[0];
            return (T)((Codec)firstConstant)._decode(reference);
         } else if (!icc.isInterface()) {
            throw new JaplScriptException("Cannot create proxy for non-interface class " + icc);
         } else {
            return (T)Proxy.newProxyInstance(JaplScript.class.getClassLoader(), new Class[]{icc}, new ObjectInvocationHandler(reference));
         }
      } catch (JaplScriptException e) {
         throw e;
      } catch (RuntimeException e) {
         throw new JaplScriptException("Failed to cast " + reference + " to " + interfaceClass, e);
      }
   }

   private static Object parseList(Class<?> interfaceClass, boolean useMostSpecificSubClass, Reference reference) {
      String objectReference = reference.getObjectReference();
      String applicationReference = reference.getApplicationReference();
      List<Object> result = new ArrayList<>();
      int depth = 0;
      boolean quotes = false;
      boolean curlies = objectReference.startsWith("{");
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < objectReference.length(); i++) {
         char c = objectReference.charAt(i);
         char previousChar = i == 0 ? '\u0000' : objectReference.charAt(i - 1);
         switch (c) {
            case '"':
               if (previousChar != '\\') {
                  quotes = !quotes;
               }
               break;
            case '{':
               depth++;
               break;
            case '}':
               depth--;
         }

         boolean lastChar = i == objectReference.length() - 1;
         if (quotes) {
            sb.append(c);
         } else if ((depth != 1 || c != ',') && (depth != 0 || c != '}') && (curlies || c != ',' && !lastChar)) {
            if (depth == 1 && c != '{') {
               sb.append(c);
            } else if (!curlies) {
               sb.append(c);
            } else if (depth > 1) {
               sb.append(c);
            }
         } else {
            if (!curlies && lastChar) {
               sb.append(c);
            }

            if (sb.length() > 0) {
               result.add(cast(interfaceClass, useMostSpecificSubClass, new ReferenceImpl(sb.toString(), applicationReference)));
               sb.setLength(0);
            }
         }
      }

      Object resultArray;
      if (interfaceClass.isPrimitive()) {
         resultArray = listToPrimitiveArray(interfaceClass, result);
      } else {
         resultArray = result.toArray((Object[])Array.newInstance(interfaceClass, result.size()));
      }

      return resultArray;
   }

   private static Object listToPrimitiveArray(Class<?> interfaceClass, List<Object> listOfObjects) {
      Object resultArray = null;
      if (interfaceClass == int.class) {
         resultArray = listOfObjects.stream().mapToInt(ix -> (java.lang.Integer)ix).toArray();
      } else if (interfaceClass == long.class) {
         resultArray = listOfObjects.stream().mapToLong(ix -> (java.lang.Long)ix).toArray();
      } else if (interfaceClass == double.class) {
         resultArray = listOfObjects.stream().mapToDouble(ix -> (java.lang.Double)ix).toArray();
      } else if (interfaceClass == float.class) {
         float[] typedArray = new float[listOfObjects.size()];

         for (int i = 0; i < typedArray.length; i++) {
            typedArray[i] = (java.lang.Float)listOfObjects.get(i);
         }

         resultArray = typedArray;
      } else if (interfaceClass == short.class) {
         short[] typedArray = new short[listOfObjects.size()];

         for (int i = 0; i < typedArray.length; i++) {
            typedArray[i] = (java.lang.Short)listOfObjects.get(i);
         }

         resultArray = typedArray;
      } else if (interfaceClass == char.class) {
         char[] typedArray = new char[listOfObjects.size()];

         for (int i = 0; i < typedArray.length; i++) {
            typedArray[i] = (Character)listOfObjects.get(i);
         }

         resultArray = typedArray;
      } else if (interfaceClass == boolean.class) {
         boolean[] typedArray = new boolean[listOfObjects.size()];

         for (int i = 0; i < typedArray.length; i++) {
            typedArray[i] = (java.lang.Boolean)listOfObjects.get(i);
         }

         resultArray = typedArray;
      }

      return resultArray;
   }

   private static Map<String, Reference> parseRecord(Reference reference) {
      String objectReference = reference.getObjectReference();
      String applicationReference = reference.getApplicationReference();
      Map<String, Reference> result = new HashMap<>();
      int depth = 0;
      boolean quotes = false;
      boolean curlies = objectReference.startsWith("{");
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < objectReference.length(); i++) {
         char c = objectReference.charAt(i);
         char previousChar = i == 0 ? '\u0000' : objectReference.charAt(i - 1);
         boolean isLastChar = i == objectReference.length() - 1;
         switch (c) {
            case '"':
               if (previousChar != '\\') {
                  quotes = !quotes;
               }
               break;
            case '{':
               depth++;
               break;
            case '}':
               depth--;
         }

         if (quotes) {
            sb.append(c);
         } else if ((depth != 1 || c != ',') && (depth != 0 || c != '}') && (curlies || c != ',' && !isLastChar)) {
            if (depth == 1 && c != '{') {
               sb.append(c);
            } else if (!curlies) {
               sb.append(c);
            } else if (depth > 1) {
               sb.append(c);
            }
         } else {
            if (!curlies && isLastChar) {
               sb.append(c);
            }

            if (sb.length() > 0) {
               int afterKey = sb.indexOf(":");
               String key = sb.substring(0, afterKey).trim();
               String value = sb.substring(afterKey + 1).trim();
               result.put(key, ReferenceImpl.getInstance()._decode(value, applicationReference));
               sb.setLength(0);
            }
         }
      }

      return result;
   }

   public static String quote(String s) {
      StringBuilder sb = new StringBuilder();
      sb.append("(\"");
      int i = 0;

      for (int max = s.length(); i < max; i++) {
         char c = s.charAt(i);
         if (c == '"') {
            sb.append('\\');
         }

         sb.append(c);
      }

      sb.append("\")");
      return sb.toString();
   }

   public static String getStandardJavaType(String applescriptType) {
      if (applescriptType == null) {
         return null;
      }

      if (applescriptType.equalsIgnoreCase("record")) {
         return Map.class.getName() + "<" + String.class.getName() + ", " + Reference.class.getName() + ">";
      }

      String lowercaseAppleScriptType = applescriptType.toLowerCase();
      Class<?> javaType = APPLESCRIPT_TO_JAVA.get(lowercaseAppleScriptType);
      return javaType != null ? javaType.getName() : null;
   }

   static {
      APPLESCRIPT_TO_JAVA.put("file specification", File.class);
      APPLESCRIPT_TO_JAVA.put("list", List.class);
      APPLESCRIPT_TO_JAVA.put("location reference", LocationReference.class);
      addDefaultGlobalAspects();
      addDefaultTypes();
   }

   private static class DateHelper implements Aspect {
      private DateHelper() {
      }

      public String before(String application, String body) {
         return null;
      }

      public String after(String application, String body) {
         return body.contains("my createDate")
            ? "on createDate(y, m, d, h, min, s)\n\tset dateVar to (current date)\n\tset year of dateVar to y\n\tset month of dateVar to m\n\tset day of dateVar to d\n\tset hours of dateVar to h\n\tset minutes of dateVar to min\n\tset seconds of dateVar to s\n\treturn dateVar\nend createDate"
            : null;
      }
   }

   private static class Tell implements Aspect {
      private Tell() {
      }

      public String before(String application, String body) {
         return application != null && !application.startsWith("scripting addition") ? "tell " + application : "";
      }

      public String after(String application, String body) {
         return application != null && !application.startsWith("scripting addition") ? "end tell" : "";
      }
   }
}
