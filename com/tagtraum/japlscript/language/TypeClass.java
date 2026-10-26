package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplScript;
import com.tagtraum.japlscript.ObjectInvocationHandler;
import com.tagtraum.japlscript.Reference;
import java.util.Objects;

public class TypeClass implements Reference, Codec<Reference> {
   private static final TypeClass instance = new TypeClass();
   private static final TypeClass[] CLASSES = new TypeClass[]{
      new TypeClass("class", new Chevron("class", "pcls")),
      new TypeClass("type", new Chevron("class", "type")),
      new TypeClass("type class", new Chevron("class", "type"))
   };
   private final String objectReference;
   private final String applicationReference;
   private String code;
   private TypeClass superClass;
   private Class<?> applicationInterface;
   private TypeClass typeClass;

   public TypeClass() {
      this(null, (String)null);
      this.code = null;
      this.superClass = null;
      this.applicationInterface = null;
   }

   public TypeClass(String objectReference, String applicationReference) {
      this.objectReference = objectReference;
      this.applicationReference = applicationReference;
      if (objectReference != null && objectReference.startsWith("«")) {
         this.code = objectReference;
      }
   }

   public TypeClass(String name, Chevron code) {
      this(name, code == null ? null : code.toString(), null, null, null);
   }

   public TypeClass(String name, String code, Class<?> applicationInterface, TypeClass superClass) {
      this(name, code, null, applicationInterface, superClass);
   }

   public TypeClass(String name, String code, String applicationReference, TypeClass superClass) {
      this(name, code, applicationReference, null, superClass);
   }

   public TypeClass(String name, String code, String applicationReference, Class<?> applicationInterface, TypeClass superClass) {
      this(name, applicationReference);
      this.code = code;
      this.superClass = superClass;
      this.applicationInterface = applicationInterface;
   }

   @Override
   public String getObjectReference() {
      return this.objectReference;
   }

   @Override
   public String getApplicationReference() {
      return this.applicationReference;
   }

   public static TypeClass getInstance() {
      return instance;
   }

   public static TypeClass fromClass(Class<?> klass) throws NoSuchFieldException, IllegalAccessException {
      return (TypeClass)klass.getDeclaredField("CLASS").get(null);
   }

   public TypeClass getSuperClass() {
      return this.superClass;
   }

   public Class<?> getApplicationInterface() {
      return this.applicationInterface;
   }

   public String getName() {
      return this.getObjectReference();
   }

   public Chevron getCode() {
      return this.code == null ? null : Chevron.parse(this.code);
   }

   public TypeClass intern() {
      return JaplScript.internTypeClass(this);
   }

   public boolean isAssignableFrom(TypeClass cls) {
      if (cls == null) {
         return false;
      }

      TypeClass otherClass = cls;

      while (otherClass != null && !otherClass.equals(this)) {
         otherClass = otherClass.getSuperClass();
      }

      return otherClass != null && otherClass.equals(this);
   }

   public boolean isInstance(Object object) {
      if (object == null) {
         return false;
      }

      if (!(object instanceof Reference)) {
         return false;
      }

      Reference reference = (Reference)object;
      TypeClass thisClass = reference.getTypeClass();

      while (thisClass != null && !thisClass.equals(this)) {
         thisClass = thisClass.getSuperClass();
      }

      return thisClass != null && thisClass.equals(this);
   }

   @Override
   public int hashCode() {
      return 1;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof TypeClass)) {
         return false;
      }

      TypeClass that = (TypeClass)obj;
      return this == that
         ? true
         : Objects.equals(this.getObjectReference(), that.getObjectReference())
            || Objects.equals(this.getObjectReference(), that.code)
            || this.code != null && this.code.equals(that.code)
            || this.code != null && this.code.equals(that.getObjectReference());
   }

   @Override
   public String toString() {
      return this.getObjectReference() != null && this.getObjectReference().equals(this.code)
         ? this.getObjectReference()
         : this.getObjectReference() + "/" + this.code;
   }

   @Override
   public synchronized TypeClass getTypeClass() {
      if (this.typeClass == null) {
         this.typeClass = new ObjectInvocationHandler(this).getTypeClass();
      }

      return this.typeClass;
   }

   @Override
   public boolean isInstanceOf(TypeClass typeClass) {
      return typeClass != null && typeClass.isInstance(this);
   }

   @Override
   public <T> T cast(Class<T> klass) {
      return JaplScript.cast(klass, this);
   }

   @Override
   public String _encode(Object reference) {
      return ((Reference)reference).getObjectReference();
   }

   @Override
   public Class<? extends Reference> _getJavaType() {
      return TypeClass.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }

   public TypeClass _decode(String objectReference, String applicationReference) {
      return objectReference != null && !"null".equals(objectReference) && !"missing value".equals(objectReference)
         ? new TypeClass(objectReference, applicationReference)
         : null;
   }
}
