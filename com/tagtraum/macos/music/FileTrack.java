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

@Plural("file tracks")
@Code("cFlT")
@Name("file track")
@Inherits("track")
public interface FileTrack extends Reference, Track {
   TypeClass CLASS = new TypeClass("file track", "«class cFlT»", Application.class, Track.CLASS);

   @Code("pLoc")
   @Kind("property")
   @Name("location")
   @Type("file")
   JaplScriptFile getLocation();

   @Code("pLoc")
   @Kind("property")
   @Name("location")
   @Type("file")
   void setLocation(JaplScriptFile var1);

   @Override
   Map<String, Object> getProperties();
}
