package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;

public class Long implements Codec<java.lang.Long> {
   private static final Long instance = new Long();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("double integer", new Chevron("class", "comp"))};

   private Long() {
   }

   public static Long getInstance() {
      return instance;
   }

   public java.lang.Long _decode(String objectReference, String applicationReference) {
      return java.lang.Long.valueOf(objectReference.trim());
   }

   @Override
   public String _encode(Object number) {
      return number == null ? "null" : number.toString();
   }

   @Override
   public Class<java.lang.Long> _getJavaType() {
      return long.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
