package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Inherits;
import com.tagtraum.japlscript.Kind;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.Type;
import com.tagtraum.japlscript.language.Picture;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Map;

@Plural("artworks")
@Code("cArt")
@Name("artwork")
@Inherits("item")
public interface Artwork extends Reference, Item {
   TypeClass CLASS = new TypeClass("artwork", "«class cArt»", Application.class, Item.CLASS);

   @Code("pPCT")
   @Kind("property")
   @Name("data")
   @Type("picture")
   Picture getData();

   @Code("pPCT")
   @Kind("property")
   @Name("data")
   @Type("picture")
   void setData(Picture var1);

   @Code("pDes")
   @Kind("property")
   @Name("description")
   @Type("text")
   String getDescription();

   @Code("pDes")
   @Kind("property")
   @Name("description")
   @Type("text")
   void setDescription(String var1);

   @Code("pDlA")
   @Kind("property")
   @Name("downloaded")
   @Type("boolean")
   boolean isDownloaded();

   @Code("pFmt")
   @Kind("property")
   @Name("format")
   @Type("type")
   TypeClass getFormat();

   @Code("pKnd")
   @Kind("property")
   @Name("kind")
   @Type("integer")
   int getKind();

   @Code("pKnd")
   @Kind("property")
   @Name("kind")
   @Type("integer")
   void setKind(int var1);

   @Code("pRaw")
   @Kind("property")
   @Name("raw data")
   @Type("any")
   Reference getRawData();

   @Code("pRaw")
   @Kind("property")
   @Name("raw data")
   @Type("any")
   void setRawData(Reference var1);

   @Override
   Map<String, Object> getProperties();
}
