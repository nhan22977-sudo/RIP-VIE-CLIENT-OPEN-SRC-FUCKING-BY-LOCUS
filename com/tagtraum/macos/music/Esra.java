package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("eSrA")
@Name("eSrA")
public enum Esra implements JaplEnum, Codec<Esra> {
   ALBUMS("albums", "kSrL", "albums only"),
   ALL("all", "kAll", "all text fields"),
   ARTISTS("artists", "kSrR", "artists only"),
   COMPOSERS("composers", "kSrC", "composers only"),
   DISPLAYED("displayed", "kSrV", "visible text fields"),
   NAMES("names", "kSrS", "track names only");

   public static final TypeClass CLASS = new TypeClass("eSrA", "«class eSrA»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Esra(String name, String code, String description) {
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

   public Esra _decode(String objectReference, String applicationReference) {
      if ("kSrL".equals(objectReference) || "albums".equals(objectReference) || "«constant ****kSrL»".equals(objectReference)) {
         return ALBUMS;
      } else if ("kAll".equals(objectReference) || "all".equals(objectReference) || "«constant ****kAll»".equals(objectReference)) {
         return ALL;
      } else if ("kSrR".equals(objectReference) || "artists".equals(objectReference) || "«constant ****kSrR»".equals(objectReference)) {
         return ARTISTS;
      } else if ("kSrC".equals(objectReference) || "composers".equals(objectReference) || "«constant ****kSrC»".equals(objectReference)) {
         return COMPOSERS;
      } else if ("kSrV".equals(objectReference) || "displayed".equals(objectReference) || "«constant ****kSrV»".equals(objectReference)) {
         return DISPLAYED;
      } else if (!"kSrS".equals(objectReference) && !"names".equals(objectReference) && !"«constant ****kSrS»".equals(objectReference)) {
         throw new IllegalArgumentException("Enum " + this.name + " is unknown.");
      } else {
         return NAMES;
      }
   }

   @Override
   public String _encode(Object japlEnum) {
      return ((JaplEnum)japlEnum).getName();
   }

   @Override
   public Class<Esra> _getJavaType() {
      return Esra.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
