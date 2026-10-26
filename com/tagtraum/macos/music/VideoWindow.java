package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Inherits;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Map;

@Plural("video windows")
@Code("cNPW")
@Name("video window")
@Inherits("window")
public interface VideoWindow extends Reference, Window {
   TypeClass CLASS = new TypeClass("video window", "«class cNPW»", Application.class, Window.CLASS);

   @Override
   Map<String, Object> getProperties();
}
