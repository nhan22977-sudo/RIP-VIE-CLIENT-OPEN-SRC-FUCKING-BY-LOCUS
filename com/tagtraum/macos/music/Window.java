package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Inherits;
import com.tagtraum.japlscript.Kind;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.Type;
import com.tagtraum.japlscript.language.TypeClass;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Map;

@Plural("windows")
@Code("cwin")
@Name("window")
@Inherits("item")
public interface Window extends Reference, Item {
   TypeClass CLASS = new TypeClass("window", "«class cwin»", Application.class, Item.CLASS);

   @Code("pbnd")
   @Kind("property")
   @Name("bounds")
   @Type("rectangle")
   Rectangle getBounds();

   @Code("pbnd")
   @Kind("property")
   @Name("bounds")
   @Type("rectangle")
   void setBounds(Rectangle var1);

   @Code("hclb")
   @Kind("property")
   @Name("closeable")
   @Type("boolean")
   boolean isCloseable();

   @Code("pWSh")
   @Kind("property")
   @Name("collapseable")
   @Type("boolean")
   boolean isCollapseable();

   @Code("wshd")
   @Kind("property")
   @Name("collapsed")
   @Type("boolean")
   boolean isCollapsed();

   @Code("wshd")
   @Kind("property")
   @Name("collapsed")
   @Type("boolean")
   void setCollapsed(boolean var1);

   @Code("pFSc")
   @Kind("property")
   @Name("full screen")
   @Type("boolean")
   boolean isFullScreen();

   @Code("pFSc")
   @Kind("property")
   @Name("full screen")
   @Type("boolean")
   void setFullScreen(boolean var1);

   @Code("ppos")
   @Kind("property")
   @Name("position")
   @Type("point")
   Point getPosition();

   @Code("ppos")
   @Kind("property")
   @Name("position")
   @Type("point")
   void setPosition(Point var1);

   @Code("prsz")
   @Kind("property")
   @Name("resizable")
   @Type("boolean")
   boolean isResizable();

   @Code("pvis")
   @Kind("property")
   @Name("visible")
   @Type("boolean")
   boolean isVisible();

   @Code("pvis")
   @Kind("property")
   @Name("visible")
   @Type("boolean")
   void setVisible(boolean var1);

   @Code("iszm")
   @Kind("property")
   @Name("zoomable")
   @Type("boolean")
   boolean isZoomable();

   @Code("pzum")
   @Kind("property")
   @Name("zoomed")
   @Type("boolean")
   boolean isZoomed();

   @Code("pzum")
   @Kind("property")
   @Name("zoomed")
   @Type("boolean")
   void setZoomed(boolean var1);

   @Override
   Map<String, Object> getProperties();
}
