package com.tagtraum.japlscript;

import com.tagtraum.japlscript.execution.Aspect;
import com.tagtraum.japlscript.execution.JaplScriptException;
import com.tagtraum.japlscript.execution.ScriptExecutor;
import com.tagtraum.japlscript.execution.Session;
import com.tagtraum.japlscript.language.Record;
import com.tagtraum.japlscript.language.ReferenceImpl;
import com.tagtraum.japlscript.language.TypeClass;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ObjectInvocationHandler implements InvocationHandler {
   private static final Logger LOG = Logger.getLogger(ObjectInvocationHandler.class.getName());
   private static final Method TO_STRING_METHOD;
   private static final Method EQUALS_METHOD;
   private static final Method HASHCODE_METHOD;
   private static final Method OBJECT_REFERENCE_METHOD;
   private static final Method APPLICATION_REFERENCE_METHOD;
   private static final Method CAST_METHOD;
   private static final Method IS_INSTANCE_OF_METHOD;
   private static final Method TYPE_CLASS_METHOD;
   private final Reference reference;
   private boolean reduceScriptExecutions = true;

   public ObjectInvocationHandler(Reference reference) {
      this.reference = reference;
   }

   public boolean isReduceScriptExecutions() {
      return this.reduceScriptExecutions;
   }

   public void setReduceScriptExecutions(boolean reduceScriptExecutions) {
      this.reduceScriptExecutions = reduceScriptExecutions;
   }

   public TypeClass getTypeClass() {
      try {
         TypeClass typeClass = this.executeAppleScript(this.reference, "return class of " + this.reference.getObjectReference(), TypeClass.class);
         return typeClass.intern();
      } catch (IOException e) {
         throw new JaplScriptException(e);
      }
   }

   private TypeClass getTypeClass(Map<String, Reference> propertyMap, Class<?> interfaceClass) {
      Reference classRef = propertyMap.get(new Chevron("property", "pcls").toString());
      TypeClass typeClass;
      if (classRef != null) {
         typeClass = new TypeClass(null, classRef.getObjectReference(), this.reference.getApplicationReference(), null).intern();
      } else {
         classRef = propertyMap.get("class");
         if (classRef != null) {
            typeClass = new TypeClass(classRef.getObjectReference(), null, this.reference.getApplicationReference(), null).intern();
         } else {
            typeClass = this.getTypeClass();
            if (typeClass.getCode().getCode().equals("reco")) {
               try {
                  return (TypeClass)interfaceClass.getDeclaredField("CLASS").get(null);
               } catch (IllegalAccessException | NoSuchFieldException e) {
                  throw new JaplScriptException("Failed to lookup TypeClass for " + interfaceClass.getName(), e);
               }
            }
         }
      }

      return typeClass;
   }

   @Override
   public Object invoke(Object proxy, Method method, Object[] args) {
      try {
         Object returnValue = null;
         if (TO_STRING_METHOD.equals(method)) {
            return this.toString(this.reference);
         }

         if (OBJECT_REFERENCE_METHOD.equals(method)) {
            return this.reference.getObjectReference();
         }

         if (EQUALS_METHOD.equals(method)) {
            return this.equals(this.reference, args[0]);
         }

         if (HASHCODE_METHOD.equals(method)) {
            return this.reference.hashCode();
         }

         if (APPLICATION_REFERENCE_METHOD.equals(method)) {
            return this.reference.getApplicationReference();
         }

         if (CAST_METHOD.equals(method)) {
            return JaplScript.cast((Class)args[0], this.reference);
         }

         if (IS_INSTANCE_OF_METHOD.equals(method)) {
            if (args.length == 1 && args[0] != null) {
               TypeClass typeClass = ((TypeClass)args[0]).intern();
               return typeClass.isInstance(this.reference);
            } else {
               return false;
            }
         } else if (!TYPE_CLASS_METHOD.equals(method) || args != null && args.length != 0) {
            if (!"getProperties".equals(method.getName()) || args != null && args.length != 0) {
               Kind kind = method.getAnnotation(Kind.class);
               if ("element".equals(kind.value())) {
                  returnValue = this.invokeElement(method, method.getReturnType(), args);
               } else if ("property".equals(kind.value())) {
                  returnValue = this.invokeProperty(method, method.getReturnType(), args);
               } else if ("command".equals(kind.value())) {
                  returnValue = this.invokeCommand(method, method.getReturnType(), args);
               } else if ("make".equals(kind.value())) {
                  returnValue = this.invokeMake(method, args);
               }

               if (returnValue instanceof TypeClass && ((TypeClass)returnValue).getApplicationReference() != null) {
                  returnValue = JaplScript.internTypeClass((TypeClass)returnValue);
               }

               return returnValue;
            } else {
               return this.invokeProperties(method.getDeclaringClass());
            }
         } else {
            return this.getTypeClass();
         }
      } catch (RuntimeException rte) {
         throw rte;
      } catch (Exception e) {
         throw new JaplScriptException(e);
      }
   }

   private Map<String, Object> invokeProperties(Class<?> interfaceClass) throws IOException {
      Reference properties;
      if (!this.isRecord()) {
         properties = this.executeAppleScript(this.reference, "return properties" + this.getOfClause(), Record.class);
      } else {
         properties = this.reference;
      }

      return this.toJavaMap(properties, interfaceClass);
   }

   private Map<String, Object> toJavaMap(Reference record, Class<?> interfaceClass) {
      Map<String, Reference> stringReferenceMap = JaplScript.cast(Map.class, record);
      TypeClass typeClass = this.getTypeClass(stringReferenceMap, interfaceClass);
      Map<String, Object> javaMap = new HashMap<>();

      for (Entry<String, Reference> e : stringReferenceMap.entrySet()) {
         String propertyName = e.getKey();
         Reference propertyValue = e.getValue();
         Property property = JaplScript.getProperty(this.reference, typeClass, propertyName);
         if (property != null) {
            javaMap.put(property.getJavaName(), JaplScript.cast(property.getJavaClass(), true, propertyValue));
         } else {
            LOG.warning("Failed to translate AppleScript property named \"" + propertyName + "\" to Java.");
            javaMap.put(propertyName, propertyValue);
         }
      }

      return javaMap;
   }

   private String toString(Reference reference) {
      return "[" + reference.getApplicationReference() + "]: " + reference.getObjectReference();
   }

   private boolean equals(Reference ref1, Object ref2) {
      if (ref2 == null) {
         return false;
      } else {
         return !(ref2 instanceof Reference) ? false : this.toString(ref1).equals(this.toString((Reference)ref2));
      }
   }

   private <T> T invokeMake(Method method, Object... args) throws IOException {
      if (args.length != 1) {
         throw new JaplScriptException("Wrong number of arguments for " + method + ": " + args.length);
      }

      if (!(args[0] instanceof Class)) {
         throw new JaplScriptException("Argument is not a class object: " + args[0].getClass());
      }

      Class<T> klass = (Class<T>)args[0];
      Name applescriptClassname = klass.getAnnotation(Name.class);
      if (applescriptClassname == null) {
         throw new IOException("\"make\" failed, because we failed to find a Name annotation for class " + klass);
      }

      String applescript = "make " + applescriptClassname.value();
      return this.executeAppleScript(this.reference, applescript, klass);
   }

   private <T> T invokeCommand(Method method, Class<T> returnType, Object... args) throws IOException {
      Name name = method.getAnnotation(Name.class);
      Parameter[] parameters = getFirstParameterAnnotations(method);
      StringBuilder applescript = new StringBuilder(name.value() + " ");
      if (args != null) {
         for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg != null) {
               if (parameters[i] != null) {
                  applescript.append(parameters[i].value());
               }

               applescript.append(' ');
               applescript.append(this.encode(arg));
               applescript.append(" ");
            }
         }
      }

      return this.executeAppleScript(this.reference, applescript.toString(), returnType);
   }

   private static Parameter[] getFirstParameterAnnotations(Method method) {
      Annotation[][] parameterAnnotations = method.getParameterAnnotations();
      Parameter[] parameters = new Parameter[parameterAnnotations.length];
      int j = 0;

      for (Annotation[] anns : parameterAnnotations) {
         if (anns.length > 0) {
            parameters[j] = (Parameter)anns[0];
         }

         j++;
      }

      return parameters;
   }

   private <T> T invokeElement(Method method, Class<T> returnType, Object... args) throws IOException, NoSuchMethodException {
      T returnValue = null;
      Type type = method.getAnnotation(Type.class);
      if (method.getName().startsWith("get")) {
         if (method.getReturnType().isArray()) {
            String plural = this.getPlural(method.getReturnType().getComponentType());
            String applescript;
            if (args != null
               && args[0] != null
               && !((String)args[0]).trim().isEmpty()
               && method.getParameterTypes().length == 1
               && method.getParameterTypes()[0] == String.class) {
               applescript = "return " + plural + this.getOfClause() + " where " + args[0];
            } else {
               if (args != null && args[0] != null && !((String)args[0]).trim().isEmpty() && method.getParameterTypes().length != 0) {
                  throw new JaplScriptException("Unknown method signature. " + method);
               }

               applescript = "return " + plural + this.getOfClause();
            }

            returnValue = this.executeAppleScript(this.reference, applescript, returnType);
         } else if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0] == int.class) {
            int index = (Integer)args[0] + 1;
            String objectreference = type.value() + " " + index + this.getOfClause();
            if (this.reduceScriptExecutions) {
               if (index < 1) {
                  throw new ArrayIndexOutOfBoundsException("Index has to be greater than 0");
               }

               returnValue = JaplScript.cast(returnType, new ReferenceImpl(objectreference, this.reference.getApplicationReference()));
            } else {
               String applescript = "return " + objectreference;
               returnValue = this.executeAppleScript(this.reference, applescript, returnType);
            }
         } else {
            if (method.getParameterTypes().length != 1 || method.getParameterTypes()[0] != Id.class) {
               throw new JaplScriptException("Unknown method signature. " + method);
            }

            Id id = (Id)args[0];
            String objectreference = type.value() + " " + id + this.getOfClause();
            if (this.reduceScriptExecutions) {
               returnValue = JaplScript.cast(returnType, new ReferenceImpl(objectreference, this.reference.getApplicationReference()));
            } else {
               String applescript = "return " + objectreference;
               returnValue = this.executeAppleScript(this.reference, applescript, returnType);
            }
         }
      } else if (method.getName().startsWith("set")) {
         if (method.getParameterTypes().length != 2 || method.getParameterTypes()[0] != int.class) {
            throw new JaplScriptException("Unknown method signature. " + method);
         }

         String plural = this.getPlural(method.getReturnType());
         int index = (Integer)args[0] + 1;
         Reference ref = (Reference)args[1];
         String applescript = "set item " + index + " of " + plural + this.getOfClause() + " to (" + ref.getObjectReference() + ")";
         this.executeAppleScript(this.reference, applescript, returnType);
      } else if (method.getName().startsWith("count")) {
         Method getMethod = method.getDeclaringClass().getMethod("get" + method.getName().substring("count".length()));
         String plural = this.getPlural(getMethod.getReturnType().getComponentType());
         String applescript;
         if (args != null
            && args[0] != null
            && !((String)args[0]).trim().isEmpty()
            && method.getParameterTypes().length == 1
            && method.getParameterTypes()[0] == String.class) {
            applescript = "count " + plural + this.getOfClause() + " where " + args[0];
         } else {
            if (args != null && args[0] != null && !((String)args[0]).trim().isEmpty() && method.getParameterTypes().length != 0) {
               throw new JaplScriptException("Unknown method signature. " + method);
            }

            applescript = "count " + plural + this.getOfClause();
         }

         returnValue = this.executeAppleScript(this.reference, applescript, returnType);
      }

      return returnValue;
   }

   private String getPlural(Class<?> type) {
      Plural pluralAnnotation = type.getAnnotation(Plural.class);
      String plural;
      if (pluralAnnotation != null) {
         plural = pluralAnnotation.value();
      } else {
         LOG.warning("Type " + type.getName() + " does not have a defined AppleScript plural. Trying to simply add an 's'.");
         plural = type.getAnnotation(Name.class).value() + "s";
      }

      return plural;
   }

   private <T> T invokeProperty(Method method, Class<T> returnType, Object[] args) throws IOException {
      T returnValue = null;
      Name name = method.getAnnotation(Name.class);
      Code code = method.getAnnotation(Code.class);
      Chevron propertyChevron = new Chevron("property", code.value());
      if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
         String applescript = "return " + propertyChevron + this.getOfClause();
         returnValue = this.executeAppleScript(this.reference, applescript, returnType);
      } else if (method.getName().startsWith("set")) {
         String applescript = "set " + propertyChevron + this.getOfClause() + " to " + this.encode(args[0]);
         returnValue = this.executeAppleScript(this.reference, applescript, returnType);
      }

      return returnValue;
   }

   private boolean isRecord() {
      return this.reference != null
         && this.reference.getObjectReference() != null
         && this.reference.getObjectReference().startsWith("{")
         && this.reference.getObjectReference().endsWith("}");
   }

   private String getOfClause() {
      return this.reference.getObjectReference() == null ? "" : " of " + this.reference.getObjectReference();
   }

   private String encode(Object arg) {
      if (arg instanceof Object[]) {
         return this.encode((Object[])arg);
      }

      if (arg instanceof List) {
         return this.encode((List<?>)arg);
      }

      if (arg instanceof Map) {
         return this.encode((Map<String, ?>)arg);
      }

      for (Codec<?> type : JaplScript.getTypes()) {
         if (type._getJavaType().isAssignableFrom(arg.getClass())) {
            return type._encode(arg);
         }
      }

      return JaplEnum.class.isAssignableFrom(arg.getClass()) ? ObjectInvocationHandler.EncoderEnum.DUMMY._encode(arg) : arg.toString();
   }

   private String encode(Object[] array) {
      return Arrays.stream(array).map(this::encode).collect(Collectors.joining(", ", "{", "}"));
   }

   private String encode(List<?> list) {
      return list.stream().map(this::encode).collect(Collectors.joining(", ", "{", "}"));
   }

   private String encode(Map<String, ?> map) {
      return map.entrySet().stream().map(e -> e.getKey() + ": " + this.encode(e.getValue())).collect(Collectors.joining(", ", "{", "}"));
   }

   public <T> T executeAppleScript(Reference reference, String appleScript, Class<T> returnType) throws IOException {
      return this.executeAppleScript(reference.getApplicationReference(), appleScript, returnType);
   }

   private <T> T executeAppleScript(String application, String appleScript, Class<T> returnType) throws IOException {
      return this.executeAppleScript(this.tell(application, appleScript), returnType);
   }

   private <T> T executeAppleScript(CharSequence appleScript, Class<T> returnType) throws IOException {
      Session session = Session.get();
      if (session == null) {
         ScriptExecutor scriptExecutor = ScriptExecutor.newInstance();
         scriptExecutor.setScript(appleScript);
         String returnValue = scriptExecutor.execute();
         if (LOG.isLoggable(Level.FINE)) {
            LOG.fine(appleScript + " == > " + returnValue);
         }

         ReferenceImpl reference = new ReferenceImpl(returnValue, this.reference.getApplicationReference());
         return !returnType.equals(void.class) ? JaplScript.cast((Class<T>)JaplScript.guessMostSpecificSubclass(returnType, reference), true, reference) : null;
      } else if (!returnType.equals(void.class) && !session.isIgnoreReturnValues()) {
         try {
            session.add(appleScript);
            ScriptExecutor scriptExecutor = ScriptExecutor.newInstance();
            scriptExecutor.setScript(session.getScript());
            String returnValue = scriptExecutor.execute();
            return JaplScript.cast(
               (Class<T>)JaplScript.guessMostSpecificSubclass(returnType, this.reference),
               new ReferenceImpl(returnValue, this.reference.getApplicationReference())
            );
         } finally {
            session.reset();
         }
      } else {
         session.add(appleScript);
         return null;
      }
   }

   private CharSequence tell(String application, String appleScript) {
      StringBuilder sb = new StringBuilder();
      List<Aspect> globalAspects = JaplScript.getGlobalAspects();

      for (Aspect aspect : globalAspects) {
         String before = aspect.before(application, appleScript);
         if (before != null) {
            sb.append(before).append("\r\n");
         }
      }

      Session session = Session.get();
      if (session != null) {
         List<Aspect> aspects = session.getAspects();

         for (Aspect aspect : aspects) {
            String before = aspect.before(application, appleScript);
            if (before != null) {
               sb.append("  ").append(before).append("\r\n");
            }
         }

         sb.append("  ").append(appleScript).append("\r\n");
         Collections.reverse(aspects);

         for (Aspect aspect : aspects) {
            String after = aspect.after(application, appleScript);
            if (after != null) {
               sb.append("  ").append(after).append("\r\n");
            }
         }
      } else {
         sb.append(appleScript).append("\r\n");
      }

      Collections.reverse(globalAspects);

      for (Aspect aspect : globalAspects) {
         String after = aspect.after(application, appleScript);
         if (after != null) {
            sb.append(after).append("\r\n");
         }
      }

      return sb.toString();
   }

   static {
      try {
         TO_STRING_METHOD = Object.class.getMethod("toString");
         HASHCODE_METHOD = Object.class.getMethod("hashCode");
         EQUALS_METHOD = Object.class.getMethod("equals", Object.class);
         OBJECT_REFERENCE_METHOD = Reference.class.getMethod("getObjectReference");
         APPLICATION_REFERENCE_METHOD = Reference.class.getMethod("getApplicationReference");
         CAST_METHOD = Reference.class.getMethod("cast", Class.class);
         IS_INSTANCE_OF_METHOD = Reference.class.getMethod("isInstanceOf", TypeClass.class);
         TYPE_CLASS_METHOD = Reference.class.getMethod("getTypeClass");
      } catch (NoSuchMethodException e) {
         throw new Error(e);
      }
   }

   private enum EncoderEnum implements JaplEnum, Codec<ObjectInvocationHandler.EncoderEnum> {
      DUMMY;

      @Override
      public String getName() {
         return null;
      }

      @Override
      public String getCode() {
         return null;
      }

      @Override
      public String getDescription() {
         return null;
      }

      public ObjectInvocationHandler.EncoderEnum _decode(String objectReference, String applicationReference) {
         return null;
      }

      @Override
      public String _encode(Object japlEnum) {
         return ((JaplEnum)japlEnum).getName();
      }

      @Override
      public Class<? extends ObjectInvocationHandler.EncoderEnum> _getJavaType() {
         return ObjectInvocationHandler.EncoderEnum.class;
      }

      @Override
      public TypeClass[] _getAppleScriptTypes() {
         return new TypeClass[0];
      }
   }
}
