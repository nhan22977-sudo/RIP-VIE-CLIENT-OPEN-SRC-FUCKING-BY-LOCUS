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

@Plural("encoders")
@Code("cEnc")
@Name("encoder")
@Inherits("item")
public interface Encoder extends Reference, Item {
   TypeClass CLASS = new TypeClass("encoder", "«class cEnc»", Application.class, Item.CLASS);

   @Code("pFmt")
   @Kind("property")
   @Name("format")
   @Type("text")
   String getFormat();

   @Override
   Map<String, Object> getProperties();
}
