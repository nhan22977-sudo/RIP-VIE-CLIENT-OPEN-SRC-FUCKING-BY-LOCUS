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

@Plural("library playlists")
@Code("cLiP")
@Name("library playlist")
@Inherits("playlist")
public interface LibraryPlaylist extends Reference, Playlist {
   TypeClass CLASS = new TypeClass("library playlist", "«class cLiP»", Application.class, Playlist.CLASS);

   @Kind("element")
   @Type("file track")
   default FileTrack[] getFileTracks() {
      return this.getFileTracks(null);
   }

   @Kind("element")
   @Type("file track")
   FileTrack[] getFileTracks(String var1);

   @Kind("element")
   @Type("file track")
   FileTrack getFileTrack(int var1);

   @Kind("element")
   @Type("file track")
   FileTrack getFileTrack(Id var1);

   @Kind("element")
   @Type("file track")
   default int countFileTracks() {
      return this.countFileTracks(null);
   }

   @Kind("element")
   @Type("file track")
   int countFileTracks(String var1);

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

   @Kind("element")
   @Type("shared track")
   default SharedTrack[] getSharedTracks() {
      return this.getSharedTracks(null);
   }

   @Kind("element")
   @Type("shared track")
   SharedTrack[] getSharedTracks(String var1);

   @Kind("element")
   @Type("shared track")
   SharedTrack getSharedTrack(int var1);

   @Kind("element")
   @Type("shared track")
   SharedTrack getSharedTrack(Id var1);

   @Kind("element")
   @Type("shared track")
   default int countSharedTracks() {
      return this.countSharedTracks(null);
   }

   @Kind("element")
   @Type("shared track")
   int countSharedTracks(String var1);

   @Override
   Map<String, Object> getProperties();
}
