package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;

public class Float implements Codec<java.lang.Float> {
   private static final Float instance = new Float();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("small real", new Chevron("class", "sing"))};

   private Float() {
   }

   public static Float getInstance() {
      return instance;
   }

   public java.lang.Float _decode(String objectReference, String applicationReference) {
      return java.lang.Float.valueOf(objectReference.trim());
   }

   @Override
   public String _encode(Object number) {
      return number == null ? "null" : number.toString();
   }

   @Override
   public Class<java.lang.Float> _getJavaType() {
      return float.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
