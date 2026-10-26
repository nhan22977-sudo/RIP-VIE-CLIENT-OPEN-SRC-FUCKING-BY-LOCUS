package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.execution.JaplScriptException;
import java.awt.Color;

public class RGBColor implements Codec<Color> {
   private static final RGBColor instance = new RGBColor();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("RGB color", new Chevron("class", "cRGB"))};

   private RGBColor() {
   }

   public static RGBColor getInstance() {
      return instance;
   }

   public Color _decode(String objectReference, String applicationReference) {
      String t = objectReference.trim();
      if (t.isEmpty()) {
         return null;
      } else if (t.startsWith("{") && t.endsWith("}")) {
         String sub = t.substring(1, t.length() - 1);
         String[] parts = sub.split(",");
         int r = java.lang.Integer.parseInt(parts[0].trim());
         int g = java.lang.Integer.parseInt(parts[1].trim());
         int b = java.lang.Integer.parseInt(parts[2].trim());
         return new Color(r / 65535.0F, g / 65535.0F, b / 65535.0F);
      } else {
         throw new JaplScriptException("Failed to parse RGBColor: " + objectReference);
      }
   }

   @Override
   public String _encode(Object object) {
      if (object == null) {
         return "null";
      }

      Color color = (Color)object;
      float[] rgbColorComponents = color.getRGBColorComponents(null);
      return "{"
         + (int)(rgbColorComponents[0] * 65535.0F - 0.5)
         + ", "
         + (int)(rgbColorComponents[1] * 65535.0F - 0.5)
         + ", "
         + (int)(rgbColorComponents[2] * 65535.0F - 0.5)
         + "}";
   }

   @Override
   public Class<Color> _getJavaType() {
      return Color.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
