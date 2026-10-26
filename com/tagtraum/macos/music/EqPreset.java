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

@Plural("EQ presets")
@Code("cEQP")
@Name("EQ preset")
@Inherits("item")
public interface EqPreset extends Reference, Item {
   TypeClass CLASS = new TypeClass("EQ preset", "«class cEQP»", Application.class, Item.CLASS);

   @Code("pEQ1")
   @Kind("property")
   @Name("band 1")
   @Type("real")
   double getBand1();

   @Code("pEQ1")
   @Kind("property")
   @Name("band 1")
   @Type("real")
   void setBand1(double var1);

   @Code("pEQ2")
   @Kind("property")
   @Name("band 2")
   @Type("real")
   double getBand2();

   @Code("pEQ2")
   @Kind("property")
   @Name("band 2")
   @Type("real")
   void setBand2(double var1);

   @Code("pEQ3")
   @Kind("property")
   @Name("band 3")
   @Type("real")
   double getBand3();

   @Code("pEQ3")
   @Kind("property")
   @Name("band 3")
   @Type("real")
   void setBand3(double var1);

   @Code("pEQ4")
   @Kind("property")
   @Name("band 4")
   @Type("real")
   double getBand4();

   @Code("pEQ4")
   @Kind("property")
   @Name("band 4")
   @Type("real")
   void setBand4(double var1);

   @Code("pEQ5")
   @Kind("property")
   @Name("band 5")
   @Type("real")
   double getBand5();

   @Code("pEQ5")
   @Kind("property")
   @Name("band 5")
   @Type("real")
   void setBand5(double var1);

   @Code("pEQ6")
   @Kind("property")
   @Name("band 6")
   @Type("real")
   double getBand6();

   @Code("pEQ6")
   @Kind("property")
   @Name("band 6")
   @Type("real")
   void setBand6(double var1);

   @Code("pEQ7")
   @Kind("property")
   @Name("band 7")
   @Type("real")
   double getBand7();

   @Code("pEQ7")
   @Kind("property")
   @Name("band 7")
   @Type("real")
   void setBand7(double var1);

   @Code("pEQ8")
   @Kind("property")
   @Name("band 8")
   @Type("real")
   double getBand8();

   @Code("pEQ8")
   @Kind("property")
   @Name("band 8")
   @Type("real")
   void setBand8(double var1);

   @Code("pEQ9")
   @Kind("property")
   @Name("band 9")
   @Type("real")
   double getBand9();

   @Code("pEQ9")
   @Kind("property")
   @Name("band 9")
   @Type("real")
   void setBand9(double var1);

   @Code("pEQ0")
   @Kind("property")
   @Name("band 10")
   @Type("real")
   double getBand10();

   @Code("pEQ0")
   @Kind("property")
   @Name("band 10")
   @Type("real")
   void setBand10(double var1);

   @Code("pMod")
   @Kind("property")
   @Name("modifiable")
   @Type("boolean")
   boolean isModifiable();

   @Code("pEQA")
   @Kind("property")
   @Name("preamp")
   @Type("real")
   double getPreamp();

   @Code("pEQA")
   @Kind("property")
   @Name("preamp")
   @Type("real")
   void setPreamp(double var1);

   @Code("pUTC")
   @Kind("property")
   @Name("update tracks")
   @Type("boolean")
   boolean isUpdateTracks();

   @Code("pUTC")
   @Kind("property")
   @Name("update tracks")
   @Type("boolean")
   void setUpdateTracks(boolean var1);

   @Override
   Map<String, Object> getProperties();
}
