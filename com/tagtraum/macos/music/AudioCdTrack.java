package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Inherits;
import com.tagtraum.japlscript.Kind;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.Type;
import com.tagtraum.japlscript.language.JaplScriptFile;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Map;

@Plural("audio CD tracks")
@Code("cCDT")
@Name("audio CD track")
@Inherits("track")
public interface AudioCdTrack extends Reference, Track {
   TypeClass CLASS = new TypeClass("audio CD track", "«class cCDT»", Application.class, Track.CLASS);

   @Code("pLoc")
   @Kind("property")
   @Name("location")
   @Type("file")
   JaplScriptFile getLocation();

   @Override
   Map<String, Object> getProperties();
}
