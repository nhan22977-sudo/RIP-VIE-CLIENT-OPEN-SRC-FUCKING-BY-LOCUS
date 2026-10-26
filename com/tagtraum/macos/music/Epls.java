package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("ePlS")
@Name("ePlS")
public enum Epls implements JaplEnum, Codec<Epls> {
   STOPPED("stopped", "kPSS", null),
   PLAYING("playing", "kPSP", null),
   PAUSED("paused", "kPSp", null),
   FAST_FORWARDING("fast forwarding", "kPSF", null),
   REWINDING("rewinding", "kPSR", null);

   public static final TypeClass CLASS = new TypeClass("ePlS", "«class ePlS»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Epls(String name, String code, String description) {
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

   public Epls _decode(String objectReference, String applicationReference) {
      if ("kPSS".equals(objectReference) || "stopped".equals(objectReference) || "«constant ****kPSS»".equals(objectReference)) {
         return STOPPED;
      } else if ("kPSP".equals(objectReference) || "playing".equals(objectReference) || "«constant ****kPSP»".equals(objectReference)) {
         return PLAYING;
      } else if ("kPSp".equals(objectReference) || "paused".equals(objectReference) || "«constant ****kPSp»".equals(objectReference)) {
         return PAUSED;
      } else if ("kPSF".equals(objectReference) || "fast forwarding".equals(objectReference) || "«constant ****kPSF»".equals(objectReference)) {
         return FAST_FORWARDING;
      } else if (!"kPSR".equals(objectReference) && !"rewinding".equals(objectReference) && !"«constant ****kPSR»".equals(objectReference)) {
         throw new IllegalArgumentException("Enum " + this.name + " is unknown.");
      } else {
         return REWINDING;
      }
   }

   @Override
   public String _encode(Object japlEnum) {
      return ((JaplEnum)japlEnum).getName();
   }

   @Override
   public Class<Epls> _getJavaType() {
      return Epls.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
