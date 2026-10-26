package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Kind;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.Type;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Map;

@Plural("items")
@Code("cobj")
@Name("item")
public interface Item extends Reference {
   TypeClass CLASS = new TypeClass("item", "«class cobj»", Application.class, null);

   @Code("pcls")
   @Kind("property")
   @Name("class")
   @Type("type")
   TypeClass getKlass();

   @Code("ctnr")
   @Kind("property")
   @Name("container")
   @Type("specifier")
   Reference getContainer();

   @Code("ID  ")
   @Kind("property")
   @Name("id")
   @Type("integer")
   int getId();

   @Code("pidx")
   @Kind("property")
   @Name("index")
   @Type("integer")
   int getIndex();

   @Code("pnam")
   @Kind("property")
   @Name("name")
   @Type("text")
   String getName();

   @Code("pnam")
   @Kind("property")
   @Name("name")
   @Type("text")
   void setName(String var1);

   @Code("pPIS")
   @Kind("property")
   @Name("persistent ID")
   @Type("text")
   String getPersistentId();

   Map<String, Object> getProperties();
}
