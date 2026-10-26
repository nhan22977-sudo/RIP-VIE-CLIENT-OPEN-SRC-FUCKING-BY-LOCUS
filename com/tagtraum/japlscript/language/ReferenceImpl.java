package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplScript;
import com.tagtraum.japlscript.ObjectInvocationHandler;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.execution.JaplScriptException;

public class ReferenceImpl implements Reference, Codec<Reference> {
   private static final ReferenceImpl instance = new ReferenceImpl();
   private static final TypeClass[] CLASSES = new TypeClass[]{
      new TypeClass("reference", new Chevron("class", "obj ")),
      new TypeClass("specifier", new Chevron("class", "obj ")),
      new TypeClass("anything", new Chevron("class", "****")),
      new TypeClass("any", new Chevron("class", "****"))
   };
   private final String objectReference;
   private final String applicationReference;
   private TypeClass typeClass;

   private ReferenceImpl() {
      this.objectReference = null;
      this.applicationReference = null;
   }

   public static ReferenceImpl getInstance() {
      return instance;
   }

   public ReferenceImpl(String objectReference, String applicationReference) {
      this.objectReference = objectReference;
      this.applicationReference = applicationReference;
   }

   @Override
   public String getObjectReference() {
      return this.objectReference;
   }

   @Override
   public String getApplicationReference() {
      return this.applicationReference;
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
   public String toString() {
      return "[" + this.applicationReference + "]: " + this.objectReference;
   }

   @Override
   public int hashCode() {
      return this.objectReference == null ? 0 : this.objectReference.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof ReferenceImpl)) {
         return false;
      } else {
         ReferenceImpl reference = (ReferenceImpl)o;
         if (this.objectReference != null ? this.objectReference.equals(reference.objectReference) : reference.objectReference == null) {
            return this.applicationReference != null
               ? this.applicationReference.equals(reference.applicationReference)
               : reference.applicationReference == null;
         } else {
            return false;
         }
      }
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
      return this.getClass() == ReferenceImpl.class ? Reference.class : this.getClass();
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }

   public ReferenceImpl _decode(String objectReference, String applicationReference) {
      if (objectReference != null && !"".equals(objectReference) && !"null".equals(objectReference) && !"missing value".equals(objectReference)) {
         try {
            return (ReferenceImpl)this.getClass().getConstructor(String.class, String.class).newInstance(objectReference, applicationReference);
         } catch (Exception e) {
            throw new JaplScriptException("Failed to create new ReferenceImpl for " + this.getClass(), e);
         }
      } else {
         return null;
      }
   }
}
