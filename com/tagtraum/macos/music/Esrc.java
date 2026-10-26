package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("eSrc")
@Name("eSrc")
public enum Esrc implements JaplEnum, Codec<Esrc> {
   LIBRARY("library", "kLib", null),
   AUDIO_CD("audio CD", "kACD", null),
   MP3_CD("MP3 CD", "kMCD", null),
   RADIO_TUNER("radio tuner", "kTun", null),
   SHARED_LIBRARY("shared library", "kShd", null),
   ITUNES_STORE("iTunes Store", "kITS", null),
   UNKNOWN("unknown", "kUnk", null);

   public static final TypeClass CLASS = new TypeClass("eSrc", "«class eSrc»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Esrc(String name, String code, String description) {
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

   public Esrc _decode(String objectReference, String applicationReference) {
      if ("kLib".equals(objectReference) || "library".equals(objectReference) || "«constant ****kLib»".equals(objectReference)) {
         return LIBRARY;
      } else if ("kACD".equals(objectReference) || "audio CD".equals(objectReference) || "«constant ****kACD»".equals(objectReference)) {
         return AUDIO_CD;
      } else if ("kMCD".equals(objectReference) || "MP3 CD".equals(objectReference) || "«constant ****kMCD»".equals(objectReference)) {
         return MP3_CD;
      } else if ("kTun".equals(objectReference) || "radio tuner".equals(objectReference) || "«constant ****kTun»".equals(objectReference)) {
         return RADIO_TUNER;
      } else if ("kShd".equals(objectReference) || "shared library".equals(objectReference) || "«constant ****kShd»".equals(objectReference)) {
         return SHARED_LIBRARY;
      } else if ("kITS".equals(objectReference) || "iTunes Store".equals(objectReference) || "«constant ****kITS»".equals(objectReference)) {
         return ITUNES_STORE;
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
   public Class<Esrc> _getJavaType() {
      return Esrc.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
