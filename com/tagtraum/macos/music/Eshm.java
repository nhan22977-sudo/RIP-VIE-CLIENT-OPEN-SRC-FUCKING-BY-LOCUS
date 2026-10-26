package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("eShM")
@Name("eShM")
public enum Eshm implements JaplEnum, Codec<Eshm> {
   SONGS("songs", "kShS", null),
   ALBUMS("albums", "kShA", null),
   GROUPINGS("groupings", "kShG", null);

   public static final TypeClass CLASS = new TypeClass("eShM", "«class eShM»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Eshm(String name, String code, String description) {
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

   public Eshm _decode(String objectReference, String applicationReference) {
      if ("kShS".equals(objectReference) || "songs".equals(objectReference) || "«constant ****kShS»".equals(objectReference)) {
         return SONGS;
      } else if ("kShA".equals(objectReference) || "albums".equals(objectReference) || "«constant ****kShA»".equals(objectReference)) {
         return ALBUMS;
      } else if (!"kShG".equals(objectReference) && !"groupings".equals(objectReference) && !"«constant ****kShG»".equals(objectReference)) {
         throw new IllegalArgumentException("Enum " + this.name + " is unknown.");
      } else {
         return GROUPINGS;
      }
   }

   @Override
   public String _encode(Object japlEnum) {
      return ((JaplEnum)japlEnum).getName();
   }

   @Override
   public Class<Eshm> _getJavaType() {
      return Eshm.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
