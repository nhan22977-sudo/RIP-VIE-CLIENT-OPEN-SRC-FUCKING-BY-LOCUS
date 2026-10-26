package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Inherits;
import com.tagtraum.japlscript.Kind;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.Type;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Map;

@Plural("AirPlay devices")
@Code("cAPD")
@Name("AirPlay device")
@Inherits("item")
public interface AirplayDevice extends Reference, Item {
   TypeClass CLASS = new TypeClass("AirPlay device", "«class cAPD»", Application.class, Item.CLASS);

   @Code("pAct")
   @Kind("property")
   @Name("active")
   @Type("boolean")
   boolean isActive();

   @Code("pAva")
   @Kind("property")
   @Name("available")
   @Type("boolean")
   boolean isAvailable();

   @Code("pKnd")
   @Kind("property")
   @Name("kind")
   @Type("eAPD")
   Eapd getKind();

   @Code("pMAC")
   @Kind("property")
   @Name("network address")
   @Type("text")
   String getNetworkAddress();

   @Code("pPro")
   @Kind("property")
   @Name("protected")
   @Type("boolean")
   boolean isProtected();

   @Code("selc")
   @Kind("property")
   @Name("selected")
   @Type("boolean")
   boolean isSelected();

   @Code("selc")
   @Kind("property")
   @Name("selected")
   @Type("boolean")
   void setSelected(boolean var1);

   @Code("pAud")
   @Kind("property")
   @Name("supports audio")
   @Type("boolean")
   boolean isSupportsAudio();

   @Code("pVid")
   @Kind("property")
   @Name("supports video")
   @Type("boolean")
   boolean isSupportsVideo();

   @Code("pVol")
   @Kind("property")
   @Name("sound volume")
   @Type("integer")
   int getSoundVolume();

   @Code("pVol")
   @Kind("property")
   @Name("sound volume")
   @Type("integer")
   void setSoundVolume(int var1);

   @Override
   Map<String, Object> getProperties();
}
