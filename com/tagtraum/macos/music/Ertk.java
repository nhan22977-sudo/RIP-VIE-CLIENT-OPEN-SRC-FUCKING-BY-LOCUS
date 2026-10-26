package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("eRtK")
@Name("eRtK")
public enum Ertk implements JaplEnum, Codec<Ertk> {
   USER("user", "kRtU", "user-specified rating"),
   COMPUTED("computed", "kRtC", "computed rating");

   public static final TypeClass CLASS = new TypeClass("eRtK", "«class eRtK»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Ertk(String name, String code, String description) {
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

   public Ertk _decode(String objectReference, String applicationReference) {
      if ("kRtU".equals(objectReference) || "user".equals(objectReference) || "«constant ****kRtU»".equals(objectReference)) {
         return USER;
      } else if (!"kRtC".equals(objectReference) && !"computed".equals(objectReference) && !"«constant ****kRtC»".equals(objectReference)) {
         throw new IllegalArgumentException("Enum " + this.name + " is unknown.");
      } else {
         return COMPUTED;
      }
   }

   @Override
   public String _encode(Object japlEnum) {
      return ((JaplEnum)japlEnum).getName();
   }

   @Override
   public Class<Ertk> _getJavaType() {
      return Ertk.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
