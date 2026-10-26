package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.execution.JaplScriptException;

public class Rectangle implements Codec<java.awt.Rectangle> {
   private static final Rectangle instance = new Rectangle();
   private static final TypeClass[] CLASSES = new TypeClass[]{
      new TypeClass("bounding rectangle", new Chevron("class", "qdrt")), new TypeClass("rectangle", new Chevron("class", "qdrt"))
   };

   private Rectangle() {
   }

   public static Rectangle getInstance() {
      return instance;
   }

   public java.awt.Rectangle _decode(String objectReference, String applicationReference) {
      String t = objectReference.trim();
      if (t.isEmpty()) {
         return null;
      } else if (t.startsWith("{") && t.endsWith("}")) {
         String sub = t.substring(1, t.length() - 1);
         String[] parts = sub.split(",");
         int x0 = java.lang.Integer.parseInt(parts[0].trim());
         int y0 = java.lang.Integer.parseInt(parts[1].trim());
         int x1 = java.lang.Integer.parseInt(parts[2].trim());
         int y1 = java.lang.Integer.parseInt(parts[3].trim());
         return new java.awt.Rectangle(x0, y0, x1 - x0, y1 - y0);
      } else {
         throw new JaplScriptException("Failed to parse rectangle: " + objectReference);
      }
   }

   @Override
   public String _encode(Object object) {
      if (object == null) {
         return "null";
      }

      java.awt.Rectangle rectangle = (java.awt.Rectangle)object;
      return "{" + rectangle.x + ", " + rectangle.y + ", " + (rectangle.x + rectangle.width) + ", " + (rectangle.y + rectangle.height) + "}";
   }

   @Override
   public Class<java.awt.Rectangle> _getJavaType() {
      return java.awt.Rectangle.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
