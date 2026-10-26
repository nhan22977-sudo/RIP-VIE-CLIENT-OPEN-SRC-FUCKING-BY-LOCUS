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

@Plural("playlist windows")
@Code("cPlW")
@Name("playlist window")
@Inherits("window")
public interface PlaylistWindow extends Reference, Window {
   TypeClass CLASS = new TypeClass("playlist window", "«class cPlW»", Application.class, Window.CLASS);

   @Code("sele")
   @Kind("property")
   @Name("selection")
   @Type("specifier")
   Reference getSelection();

   @Code("pPly")
   @Kind("property")
   @Name("view")
   @Type("playlist")
   Playlist getView();

   @Override
   Map<String, Object> getProperties();
}
