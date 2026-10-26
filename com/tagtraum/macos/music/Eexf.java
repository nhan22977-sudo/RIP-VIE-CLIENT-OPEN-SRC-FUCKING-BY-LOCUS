package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("eExF")
@Name("eExF")
public enum Eexf implements JaplEnum, Codec<Eexf> {
   PLAIN_TEXT("plain text", "kTXT", null),
   UNICODE_TEXT("Unicode text", "kUCT", null),
   XML("XML", "kXML", null),
   M3U("M3U", "kM3U", null),
   M3U8("M3U8", "kM38", null);

   public static final TypeClass CLASS = new TypeClass("eExF", "«class eExF»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Eexf(String name, String code, String description) {
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

   public Eexf _decode(String objectReference, String applicationReference) {
      if ("kTXT".equals(objectReference) || "plain text".equals(objectReference) || "«constant ****kTXT»".equals(objectReference)) {
         return PLAIN_TEXT;
      } else if ("kUCT".equals(objectReference) || "Unicode text".equals(objectReference) || "«constant ****kUCT»".equals(objectReference)) {
         return UNICODE_TEXT;
      } else if ("kXML".equals(objectReference) || "XML".equals(objectReference) || "«constant ****kXML»".equals(objectReference)) {
         return XML;
      } else if ("kM3U".equals(objectReference) || "M3U".equals(objectReference) || "«constant ****kM3U»".equals(objectReference)) {
         return M3U;
      } else if (!"kM38".equals(objectReference) && !"M3U8".equals(objectReference) && !"«constant ****kM38»".equals(objectReference)) {
         throw new IllegalArgumentException("Enum " + this.name + " is unknown.");
      } else {
         return M3U8;
      }
   }

   @Override
   public String _encode(Object japlEnum) {
      return ((JaplEnum)japlEnum).getName();
   }

   @Override
   public Class<Eexf> _getJavaType() {
      return Eexf.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
