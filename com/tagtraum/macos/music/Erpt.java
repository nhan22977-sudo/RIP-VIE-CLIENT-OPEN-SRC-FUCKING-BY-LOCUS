package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("eRpt")
@Name("eRpt")
public enum Erpt implements JaplEnum, Codec<Erpt> {
   OFF("off", "kRpO", null),
   ONE("one", "kRp1", null),
   ALL("all", "kAll", null);

   public static final TypeClass CLASS = new TypeClass("eRpt", "«class eRpt»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Erpt(String name, String code, String description) {
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

   public Erpt _decode(String objectReference, String applicationReference) {
      if ("kRpO".equals(objectReference) || "off".equals(objectReference) || "«constant ****kRpO»".equals(objectReference)) {
         return OFF;
      } else if ("kRp1".equals(objectReference) || "one".equals(objectReference) || "«constant ****kRp1»".equals(objectReference)) {
         return ONE;
      } else if (!"kAll".equals(objectReference) && !"all".equals(objectReference) && !"«constant ****kAll»".equals(objectReference)) {
         throw new IllegalArgumentException("Enum " + this.name + " is unknown.");
      } else {
         return ALL;
      }
   }

   @Override
   public String _encode(Object japlEnum) {
      return ((JaplEnum)japlEnum).getName();
   }

   @Override
   public Class<Erpt> _getJavaType() {
      return Erpt.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
