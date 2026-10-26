package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("eMdK")
@Name("eMdK")
public enum Emdk implements JaplEnum, Codec<Emdk> {
   SONG("song", "kMdS", "music track"),
   MUSIC_VIDEO("music video", "kVdV", "music video track"),
   UNKNOWN("unknown", "kUnk", null);

   public static final TypeClass CLASS = new TypeClass("eMdK", "«class eMdK»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Emdk(String name, String code, String description) {
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

   public Emdk _decode(String objectReference, String applicationReference) {
      if ("kMdS".equals(objectReference) || "song".equals(objectReference) || "«constant ****kMdS»".equals(objectReference)) {
         return SONG;
      } else if ("kVdV".equals(objectReference) || "music video".equals(objectReference) || "«constant ****kVdV»".equals(objectReference)) {
         return MUSIC_VIDEO;
      } else if (!"kUnk".equals(objectReference) && !"unknown".equals(objectReference) && !"«constant ****kUnk»".equals(objectReference)) {
         throw new IllegalArgumentException("Enum " + this.name + " is unknown.");
      } else {
         return UNKNOWN;
      }
   }

   @Override
   public String _encode(Object japlEnum) {
      return ((JaplEnum)japlEnum).getName();
   }

   @Override
   public Class<Emdk> _getJavaType() {
      return Emdk.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
