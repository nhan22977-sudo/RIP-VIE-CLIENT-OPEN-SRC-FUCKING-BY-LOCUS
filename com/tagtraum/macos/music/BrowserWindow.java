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

@Plural("browser windows")
@Code("cBrW")
@Name("browser window")
@Inherits("window")
public interface BrowserWindow extends Reference, Window {
   TypeClass CLASS = new TypeClass("browser window", "«class cBrW»", Application.class, Window.CLASS);

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

   @Code("pPly")
   @Kind("property")
   @Name("view")
   @Type("playlist")
   void setView(Playlist var1);

   @Override
   Map<String, Object> getProperties();
}
