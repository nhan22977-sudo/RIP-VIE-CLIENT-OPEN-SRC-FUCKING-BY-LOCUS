package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("eClS")
@Name("eClS")
public enum Ecls implements JaplEnum, Codec<Ecls> {
   UNKNOWN("unknown", "kUnk", null),
   PURCHASED("purchased", "kPur", null),
   MATCHED("matched", "kMat", null),
   UPLOADED("uploaded", "kUpl", null),
   INELIGIBLE("ineligible", "kRej", null),
   REMOVED("removed", "kRem", null),
   ERROR("error", "kErr", null),
   DUPLICATE("duplicate", "kDup", null),
   SUBSCRIPTION("subscription", "kSub", null),
   PRERELEASE("prerelease", "kPrR", null),
   NO_LONGER_AVAILABLE("no longer available", "kRev", null),
   NOT_UPLOADED("not uploaded", "kUpP", null);

   public static final TypeClass CLASS = new TypeClass("eClS", "«class eClS»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Ecls(String name, String code, String description) {
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

   public Ecls _decode(String objectReference, String applicationReference) {
      if ("kUnk".equals(objectReference) || "unknown".equals(objectReference) || "«constant ****kUnk»".equals(objectReference)) {
         return UNKNOWN;
      } else if ("kPur".equals(objectReference) || "purchased".equals(objectReference) || "«constant ****kPur»".equals(objectReference)) {
         return PURCHASED;
      } else if ("kMat".equals(objectReference) || "matched".equals(objectReference) || "«constant ****kMat»".equals(objectReference)) {
         return MATCHED;
      } else if ("kUpl".equals(objectReference) || "uploaded".equals(objectReference) || "«constant ****kUpl»".equals(objectReference)) {
         return UPLOADED;
      } else if ("kRej".equals(objectReference) || "ineligible".equals(objectReference) || "«constant ****kRej»".equals(objectReference)) {
         return INELIGIBLE;
      } else if ("kRem".equals(objectReference) || "removed".equals(objectReference) || "«constant ****kRem»".equals(objectReference)) {
         return REMOVED;
      } else if ("kErr".equals(objectReference) || "error".equals(objectReference) || "«constant ****kErr»".equals(objectReference)) {
         return ERROR;
      } else if ("kDup".equals(objectReference) || "duplicate".equals(objectReference) || "«constant ****kDup»".equals(objectReference)) {
         return DUPLICATE;
      } else if ("kSub".equals(objectReference) || "subscription".equals(objectReference) || "«constant ****kSub»".equals(objectReference)) {
         return SUBSCRIPTION;
      } else if ("kPrR".equals(objectReference) || "prerelease".equals(objectReference) || "«constant ****kPrR»".equals(objectReference)) {
         return PRERELEASE;
      } else if ("kRev".equals(objectReference) || "no longer available".equals(objectReference) || "«constant ****kRev»".equals(objectReference)) {
         return NO_LONGER_AVAILABLE;
      } else if (!"kUpP".equals(objectReference) && !"not uploaded".equals(objectReference) && !"«constant ****kUpP»".equals(objectReference)) {
         throw new IllegalArgumentException("Enum " + this.name + " is unknown.");
      } else {
         return NOT_UPLOADED;
      }
   }

   @Override
   public String _encode(Object japlEnum) {
      return ((JaplEnum)japlEnum).getName();
   }

   @Override
   public Class<Ecls> _getJavaType() {
      return Ecls.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
