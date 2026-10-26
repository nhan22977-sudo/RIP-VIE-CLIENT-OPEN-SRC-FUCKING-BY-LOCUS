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

@Plural("URL tracks")
@Code("cURT")
@Name("URL track")
@Inherits("track")
public interface UrlTrack extends Reference, Track {
   TypeClass CLASS = new TypeClass("URL track", "«class cURT»", Application.class, Track.CLASS);

   @Code("pURL")
   @Kind("property")
   @Name("address")
   @Type("text")
   String getAddress();

   @Code("pURL")
   @Kind("property")
   @Name("address")
   @Type("text")
   void setAddress(String var1);

   @Override
   Map<String, Object> getProperties();
}
