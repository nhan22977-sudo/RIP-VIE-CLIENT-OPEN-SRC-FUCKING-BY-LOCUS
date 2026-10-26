package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Id;
import com.tagtraum.japlscript.Inherits;
import com.tagtraum.japlscript.Kind;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Plural;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.Type;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Map;

@Plural("playlists")
@Code("cPly")
@Name("playlist")
@Inherits("item")
public interface Playlist extends Reference, Item {
   TypeClass CLASS = new TypeClass("playlist", "«class cPly»", Application.class, Item.CLASS);

   @Kind("element")
   @Type("track")
   default Track[] getTracks() {
      return this.getTracks(null);
   }

   @Kind("element")
   @Type("track")
   Track[] getTracks(String var1);

   @Kind("element")
   @Type("track")
   Track getTrack(int var1);

   @Kind("element")
   @Type("track")
   Track getTrack(Id var1);

   @Kind("element")
   @Type("track")
   default int countTracks() {
      return this.countTracks(null);
   }

   @Kind("element")
   @Type("track")
   int countTracks(String var1);

   @Kind("element")
   @Type("artwork")
   default Artwork[] getArtworks() {
      return this.getArtworks(null);
   }

   @Kind("element")
   @Type("artwork")
   Artwork[] getArtworks(String var1);

   @Kind("element")
   @Type("artwork")
   Artwork getArtwork(int var1);

   @Kind("element")
   @Type("artwork")
   Artwork getArtwork(Id var1);

   @Kind("element")
   @Type("artwork")
   default int countArtworks() {
      return this.countArtworks(null);
   }

   @Kind("element")
   @Type("artwork")
   int countArtworks(String var1);

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

   @Code("pHat")
   @Kind("property")
   @Name("disliked")
   @Type("boolean")
   boolean isDisliked();

   @Code("pHat")
   @Kind("property")
   @Name("disliked")
   @Type("boolean")
   void setDisliked(boolean var1);

   @Code("pDur")
   @Kind("property")
   @Name("duration")
   @Type("integer")
   int getDuration();

   @Code("pnam")
   @Kind("property")
   @Name("name")
   @Type("text")
   @Override
   String getName();

   @Code("pnam")
   @Kind("property")
   @Name("name")
   @Type("text")
   @Override
   void setName(String var1);

   @Code("pLov")
   @Kind("property")
   @Name("favorited")
   @Type("boolean")
   boolean isFavorited();

   @Code("pLov")
   @Kind("property")
   @Name("favorited")
   @Type("boolean")
   void setFavorited(boolean var1);

   @Code("pPlP")
   @Kind("property")
   @Name("parent")
   @Type("playlist")
   Playlist getParent();

   @Code("pSiz")
   @Kind("property")
   @Name("size")
   @Type("integer")
   int getSize();

   @Code("pSpK")
   @Kind("property")
   @Name("special kind")
   @Type("eSpK")
   Espk getSpecialKind();

   @Code("pTim")
   @Kind("property")
   @Name("time")
   @Type("text")
   String getTime();

   @Code("pvis")
   @Kind("property")
   @Name("visible")
   @Type("boolean")
   boolean isVisible();

   @Override
   Map<String, Object> getProperties();
}
