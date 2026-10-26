package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Inherits;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Map;

@Plural("shared tracks")
@Code("cShT")
@Name("shared track")
@Inherits("track")
public interface SharedTrack extends Reference, Track {
   TypeClass CLASS = new TypeClass("shared track", "«class cShT»", Application.class, Track.CLASS);

   @Override
   Map<String, Object> getProperties();
}
