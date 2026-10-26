package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Inherits;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Map;

@Plural("visuals")
@Code("cVis")
@Name("visual")
@Inherits("item")
public interface Visual extends Reference, Item {
   TypeClass CLASS = new TypeClass("visual", "«class cVis»", Application.class, Item.CLASS);

   @Override
   Map<String, Object> getProperties();
}
