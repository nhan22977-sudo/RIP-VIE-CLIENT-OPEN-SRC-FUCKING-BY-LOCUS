package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Inherits;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Map;

@Plural("folder playlists")
@Code("cFoP")
@Name("folder playlist")
@Inherits("user playlist")
public interface FolderPlaylist extends Reference, UserPlaylist {
   TypeClass CLASS = new TypeClass("folder playlist", "«class cFoP»", Application.class, UserPlaylist.CLASS);

   @Override
   Map<String, Object> getProperties();
}
