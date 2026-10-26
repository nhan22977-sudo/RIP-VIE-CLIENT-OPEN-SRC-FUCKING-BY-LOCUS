package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplEnum;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.language.TypeClass;

@Code("eAPD")
@Name("eAPD")
public enum Eapd implements JaplEnum, Codec<Eapd> {
   COMPUTER("computer", "kAPC", null),
   AIRPORT_EXPRESS("AirPort Express", "kAPX", null),
   APPLE_TV("Apple TV", "kAPT", null),
   AIRPLAY_DEVICE("AirPlay device", "kAPO", null),
   BLUETOOTH_DEVICE("Bluetooth device", "kAPB", null),
   HOMEPOD("HomePod", "kAPH", null),
   UNKNOWN("unknown", "kAPU", null);

   public static final TypeClass CLASS = new TypeClass("eAPD", "«class eAPD»", Application.class, null);
   private final String name;
   private final String code;
   private final String description;

   Eapd(String name, String code, String description) {
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

   public Eapd _decode(String objectReference, String applicationReference) {
      if ("kAPC".equals(objectReference) || "computer".equals(objectReference) || "«constant ****kAPC»".equals(objectReference)) {
         return COMPUTER;
      } else if ("kAPX".equals(objectReference) || "AirPort Express".equals(objectReference) || "«constant ****kAPX»".equals(objectReference)) {
         return AIRPORT_EXPRESS;
      } else if ("kAPT".equals(objectReference) || "Apple TV".equals(objectReference) || "«constant ****kAPT»".equals(objectReference)) {
         return APPLE_TV;
      } else if ("kAPO".equals(objectReference) || "AirPlay device".equals(objectReference) || "«constant ****kAPO»".equals(objectReference)) {
         return AIRPLAY_DEVICE;
      } else if ("kAPB".equals(objectReference) || "Bluetooth device".equals(objectReference) || "«constant ****kAPB»".equals(objectReference)) {
         return BLUETOOTH_DEVICE;
      } else if ("kAPH".equals(objectReference) || "HomePod".equals(objectReference) || "«constant ****kAPH»".equals(objectReference)) {
         return HOMEPOD;
      } else if (!"kAPU".equals(objectReference) && !"unknown".equals(objectReference) && !"«constant ****kAPU»".equals(objectReference)) {
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
   public Class<Eapd> _getJavaType() {
      return Eapd.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return new TypeClass[]{CLASS};
   }
}
