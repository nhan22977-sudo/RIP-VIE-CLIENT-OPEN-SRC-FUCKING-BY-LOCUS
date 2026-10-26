package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("eSpK")
@Name("eSpK")
public enum Espk implements JaplEnum, Codec<Espk> {
   NONE("none", "kNon", null),
   FOLDER("folder", "kSpF", null),
   GENIUS("Genius", "kSpG", null),
   LIBRARY("Library", "kSpL", null),
   MUSIC("Music", "kSpZ", null),
   PURCHASED_MUSIC("Purchased Music", "kSpM", null);

   public static final TypeClass CLASS = new TypeClass("eSpK", "«class eSpK»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Espk(String name, String code, String description) {
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

   public Espk _decode(String objectReference, String applicationReference) {
      if ("kNon".equals(objectReference) || "none".equals(objectReference) || "«constant ****kNon»".equals(objectReference)) {
         return NONE;
      } else if ("kSpF".equals(objectReference) || "folder".equals(objectReference) || "«constant ****kSpF»".equals(objectReference)) {
         return FOLDER;
      } else if ("kSpG".equals(objectReference) || "Genius".equals(objectReference) || "«constant ****kSpG»".equals(objectReference)) {
         return GENIUS;
      } else if ("kSpL".equals(objectReference) || "Library".equals(objectReference) || "«constant ****kSpL»".equals(objectReference)) {
         return LIBRARY;
      } else if ("kSpZ".equals(objectReference) || "Music".equals(objectReference) || "«constant ****kSpZ»".equals(objectReference)) {
         return MUSIC;
      } else if (!"kSpM".equals(objectReference) && !"Purchased Music".equals(objectReference) && !"«constant ****kSpM»".equals(objectReference)) {
         throw new IllegalArgumentException("Enum " + this.name + " is unknown.");
      } else {
         return PURCHASED_MUSIC;
      }
   }

   @Override
   public String _encode(Object japlEnum) {
      return ((JaplEnum)japlEnum).getName();
   }

   @Override
   public Class<Espk> _getJavaType() {
      return Espk.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
