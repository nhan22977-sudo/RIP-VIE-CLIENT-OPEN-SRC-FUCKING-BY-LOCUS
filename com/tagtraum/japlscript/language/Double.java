package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;

public class Double implements Codec<java.lang.Double> {
   private static final Double instance = new Double();
   private static final TypeClass[] CLASSES = new TypeClass[]{
      new TypeClass("number", new Chevron("class", "nmbr")), new TypeClass("real", new Chevron("class", "doub"))
   };

   private Double() {
   }

   public static Double getInstance() {
      return instance;
   }

   public java.lang.Double _decode(String objectReference, String applicationReference) {
      return java.lang.Double.valueOf(objectReference.trim());
   }

   @Override
   public String _encode(Object number) {
      return number == null ? "null" : number.toString();
   }

   @Override
   public Class<java.lang.Double> _getJavaType() {
      return double.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
