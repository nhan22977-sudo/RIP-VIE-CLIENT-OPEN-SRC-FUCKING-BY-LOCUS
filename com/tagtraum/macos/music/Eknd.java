package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("eKnd")
@Name("eKnd")
public enum Eknd implements JaplEnum, Codec<Eknd> {
   TRACK_LISTING("track listing", "kTrk", "a basic listing of tracks within a playlist"),
   ALBUM_LISTING("album listing", "kAlb", "a listing of a playlist grouped by album"),
   CD_INSERT("cd insert", "kCDi", "a printout of the playlist for jewel case inserts");

   public static final TypeClass CLASS = new TypeClass("eKnd", "«class eKnd»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Eknd(String name, String code, String description) {
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

   public Eknd _decode(String objectReference, String applicationReference) {
      if ("kTrk".equals(objectReference) || "track listing".equals(objectReference) || "«constant ****kTrk»".equals(objectReference)) {
         return TRACK_LISTING;
      } else if ("kAlb".equals(objectReference) || "album listing".equals(objectReference) || "«constant ****kAlb»".equals(objectReference)) {
         return ALBUM_LISTING;
      } else if (!"kCDi".equals(objectReference) && !"cd insert".equals(objectReference) && !"«constant ****kCDi»".equals(objectReference)) {
         throw new IllegalArgumentException("Enum " + this.name + " is unknown.");
      } else {
         return CD_INSERT;
      }
   }

   @Override
   public String _encode(Object japlEnum) {
      return ((JaplEnum)japlEnum).getName();
   }

   @Override
   public Class<Eknd> _getJavaType() {
      return Eknd.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
