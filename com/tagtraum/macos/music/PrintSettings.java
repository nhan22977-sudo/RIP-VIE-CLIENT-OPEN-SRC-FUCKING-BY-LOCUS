package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Kind;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.Type;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Date;
import java.util.Map;

@Code("pset")
@Name("print settings")
public interface PrintSettings extends Reference {
   TypeClass CLASS = new TypeClass("print settings", "«class pset»", Application.class, null);

   @Code("lwcp")
   @Kind("property")
   @Name("copies")
   @Type("integer")
   int getCopies();

   @Code("lwcl")
   @Kind("property")
   @Name("collating")
   @Type("boolean")
   boolean isCollating();

   @Code("lwfp")
   @Kind("property")
   @Name("starting page")
   @Type("integer")
   int getStartingPage();

   @Code("lwlp")
   @Kind("property")
   @Name("ending page")
   @Type("integer")
   int getEndingPage();

   @Code("lwla")
   @Kind("property")
   @Name("pages across")
   @Type("integer")
   int getPagesAcross();

   @Code("lwld")
   @Kind("property")
   @Name("pages down")
   @Type("integer")
   int getPagesDown();

   @Code("lweh")
   @Kind("property")
   @Name("error handling")
   @Type("enum")
   Enum getErrorHandling();

   @Code("lwqt")
   @Kind("property")
   @Name("requested print time")
   @Type("date")
   Date getRequestedPrintTime();

   @Code("lwpf")
   @Kind("property")
   @Name("printer features")
   @Type("text")
   String[] getPrinterFeatures();

   @Code("faxn")
   @Kind("property")
   @Name("fax number")
   @Type("text")
   String getFaxNumber();

   @Code("trpr")
   @Kind("property")
   @Name("target printer")
   @Type("text")
   String getTargetPrinter();

   Map<String, Object> getProperties();
}
