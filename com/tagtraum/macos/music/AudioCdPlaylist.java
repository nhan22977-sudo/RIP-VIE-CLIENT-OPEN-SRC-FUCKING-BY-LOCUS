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

@Plural("audio CD playlists")
@Code("cCDP")
@Name("audio CD playlist")
@Inherits("playlist")
public interface AudioCdPlaylist extends Reference, Playlist {
   TypeClass CLASS = new TypeClass("audio CD playlist", "«class cCDP»", Application.class, Playlist.CLASS);

   @Kind("element")
   @Type("audio CD track")
   default AudioCdTrack[] getAudioCdTracks() {
      return this.getAudioCdTracks(null);
   }

   @Kind("element")
   @Type("audio CD track")
   AudioCdTrack[] getAudioCdTracks(String var1);

   @Kind("element")
   @Type("audio CD track")
   AudioCdTrack getAudioCdTrack(int var1);

   @Kind("element")
   @Type("audio CD track")
   AudioCdTrack getAudioCdTrack(Id var1);

   @Kind("element")
   @Type("audio CD track")
   default int countAudioCdTracks() {
      return this.countAudioCdTracks(null);
   }

   @Kind("element")
   @Type("audio CD track")
   int countAudioCdTracks(String var1);

   @Code("pArt")
   @Kind("property")
   @Name("artist")
   @Type("text")
   String getArtist();

   @Code("pArt")
   @Kind("property")
   @Name("artist")
   @Type("text")
   void setArtist(String var1);

   @Code("pAnt")
   @Kind("property")
   @Name("compilation")
   @Type("boolean")
   boolean isCompilation();

   @Code("pAnt")
   @Kind("property")
   @Name("compilation")
   @Type("boolean")
   void setCompilation(boolean var1);

   @Code("pCmp")
   @Kind("property")
   @Name("composer")
   @Type("text")
   String getComposer();

   @Code("pCmp")
   @Kind("property")
   @Name("composer")
   @Type("text")
   void setComposer(String var1);

   @Code("pDsC")
   @Kind("property")
   @Name("disc count")
   @Type("integer")
   int getDiscCount();

   @Code("pDsC")
   @Kind("property")
   @Name("disc count")
   @Type("integer")
   void setDiscCount(int var1);

   @Code("pDsN")
   @Kind("property")
   @Name("disc number")
   @Type("integer")
   int getDiscNumber();

   @Code("pDsN")
   @Kind("property")
   @Name("disc number")
   @Type("integer")
   void setDiscNumber(int var1);

   @Code("pGen")
   @Kind("property")
   @Name("genre")
   @Type("text")
   String getGenre();

   @Code("pGen")
   @Kind("property")
   @Name("genre")
   @Type("text")
   void setGenre(String var1);

   @Code("pYr ")
   @Kind("property")
   @Name("year")
   @Type("integer")
   int getYear();

   @Code("pYr ")
   @Kind("property")
   @Name("year")
   @Type("integer")
   void setYear(int var1);

   @Override
   Map<String, Object> getProperties();
}
