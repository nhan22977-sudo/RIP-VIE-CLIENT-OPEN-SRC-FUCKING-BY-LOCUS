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

@Plural("radio tuner playlists")
@Code("cRTP")
@Name("radio tuner playlist")
@Inherits("playlist")
public interface RadioTunerPlaylist extends Reference, Playlist {
   TypeClass CLASS = new TypeClass("radio tuner playlist", "«class cRTP»", Application.class, Playlist.CLASS);

   @Kind("element")
   @Type("URL track")
   default UrlTrack[] getUrlTracks() {
      return this.getUrlTracks(null);
   }

   @Kind("element")
   @Type("URL track")
   UrlTrack[] getUrlTracks(String var1);

   @Kind("element")
   @Type("URL track")
   UrlTrack getUrlTrack(int var1);

   @Kind("element")
   @Type("URL track")
   UrlTrack getUrlTrack(Id var1);

   @Kind("element")
   @Type("URL track")
   default int countUrlTracks() {
      return this.countUrlTracks(null);
   }

   @Kind("element")
   @Type("URL track")
   int countUrlTracks(String var1);

   @Override
   Map<String, Object> getProperties();
}
