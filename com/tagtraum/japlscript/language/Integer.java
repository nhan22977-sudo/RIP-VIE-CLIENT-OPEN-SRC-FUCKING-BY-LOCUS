package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;

public class Integer implements Codec<java.lang.Integer> {
   private static final Integer instance = new Integer();
   private static final TypeClass[] CLASSES = new TypeClass[]{
      new TypeClass("integer", new Chevron("class", "long")), new TypeClass("unsigned integer", new Chevron("class", "magn"))
   };

   private Integer() {
   }

   public static Integer getInstance() {
      return instance;
   }

   public java.lang.Integer _decode(String objectReference, String applicationReference) {
      return java.lang.Integer.valueOf(objectReference.trim());
   }

   @Override
   public String _encode(Object number) {
      return number == null ? "null" : number.toString();
   }

   @Override
   public Class<java.lang.Integer> _getJavaType() {
      return int.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
