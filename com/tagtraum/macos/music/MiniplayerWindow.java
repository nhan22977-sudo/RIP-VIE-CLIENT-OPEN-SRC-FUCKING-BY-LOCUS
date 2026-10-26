package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Inherits;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Map;

@Plural("miniplayer windows")
@Code("cMPW")
@Name("miniplayer window")
@Inherits("window")
public interface MiniplayerWindow extends Reference, Window {
   TypeClass CLASS = new TypeClass("miniplayer window", "«class cMPW»", Application.class, Window.CLASS);

   @Override
   Map<String, Object> getProperties();
}
