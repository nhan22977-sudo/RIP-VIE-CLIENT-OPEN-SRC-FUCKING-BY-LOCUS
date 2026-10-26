package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("enum")
@Name("enum")
public enum Enum implements JaplEnum, Codec<Enum> {
   STANDARD("standard", "lwst", "Standard PostScript error handling"),
   DETAILED("detailed", "lwdt", "print a detailed report of PostScript errors");

   public static final TypeClass CLASS = new TypeClass("enum", "«class enum»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Enum(String name, String code, String description) {
      this.name = name;
      this.code = code;
      this.description = description;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String getCode() {
      return this.code;
   }

   @Override
   public String getDescription() {
      return this.description;
   }

   public Enum _decode(String objectReference, String applicationReference) {
      if ("lwst".equals(objectReference) || "standard".equals(objectReference) || "«constant ****lwst»".equals(objectReference)) {
         return STANDARD;
      } else if (!"lwdt".equals(objectReference) && !"detailed".equals(objectReference) && !"«constant ****lwdt»".equals(objectReference)) {
         throw new IllegalArgumentException("Enum " + this.name + " is unknown.");
      } else {
         return DETAILED;
      }
   }

   @Override
   public String _encode(Object japlEnum) {
      return ((JaplEnum)japlEnum).getName();
   }

   @Override
   public Class<Enum> _getJavaType() {
      return Enum.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
