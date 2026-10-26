package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;

public class Boolean implements Codec<java.lang.Boolean> {
   private static final Boolean instance = new Boolean();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("boolean", new Chevron("class", "bool"))};

   private Boolean() {
   }

   public static Boolean getInstance() {
      return instance;
   }

   public java.lang.Boolean _decode(String objectReference, String applicationReference) {
      return java.lang.Boolean.valueOf(objectReference.trim());
   }

   @Override
   public String _encode(Object aBoolean) {
      return aBoolean == null ? "null" : aBoolean.toString();
   }

   @Override
   public Class<java.lang.Boolean> _getJavaType() {
      return boolean.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
