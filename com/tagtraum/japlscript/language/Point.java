package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.execution.JaplScriptException;

public class Point implements Codec<java.awt.Point> {
   private static final Point instance = new Point();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("point", new Chevron("class", "QDpt"))};

   private Point() {
   }

   public static Point getInstance() {
      return instance;
   }

   public java.awt.Point _decode(String objectReference, String applicationReference) {
      String t = objectReference.trim();
      if (t.isEmpty()) {
         return null;
      } else if (t.startsWith("{") && t.endsWith("}")) {
         String sub = t.substring(1, t.length() - 1);
         int comma = sub.indexOf(44);
         int x = java.lang.Integer.parseInt(sub.substring(0, comma).trim());
         int y = java.lang.Integer.parseInt(sub.substring(comma + 1).trim());
         return new java.awt.Point(x, y);
      } else {
         throw new JaplScriptException("Failed to parse point: " + objectReference);
      }
   }

   @Override
   public String _encode(Object object) {
      if (object == null) {
         return "null";
      }

      java.awt.Point point = (java.awt.Point)object;
      return "{" + point.x + ", " + point.y + "}";
   }

   @Override
   public Class<java.awt.Point> _getJavaType() {
      return java.awt.Point.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
