package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;

public class Short implements Codec<java.lang.Short> {
   private static final Short instance = new Short();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("small integer", new Chevron("class", "shor"))};

   private Short() {
   }

   public static Short getInstance() {
      return instance;
   }

   public java.lang.Short _decode(String objectReference, String applicationReference) {
      return java.lang.Short.valueOf(objectReference.trim());
   }

   @Override
   public String _encode(Object number) {
      return number == null ? "null" : number.toString();
   }

   @Override
   public Class<java.lang.Short> _getJavaType() {
      return short.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
