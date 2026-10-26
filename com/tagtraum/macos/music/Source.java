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

@Plural("sources")
@Code("cSrc")
@Name("source")
@Inherits("item")
public interface Source extends Reference, Item {
   TypeClass CLASS = new TypeClass("source", "«class cSrc»", Application.class, Item.CLASS);

   @Kind("element")
   @Type("audio CD playlist")
   default AudioCdPlaylist[] getAudioCdPlaylists() {
      return this.getAudioCdPlaylists(null);
   }

   @Kind("element")
   @Type("audio CD playlist")
   AudioCdPlaylist[] getAudioCdPlaylists(String var1);

   @Kind("element")
   @Type("audio CD playlist")
   AudioCdPlaylist getAudioCdPlaylist(int var1);

   @Kind("element")
   @Type("audio CD playlist")
   AudioCdPlaylist getAudioCdPlaylist(Id var1);

   @Kind("element")
   @Type("audio CD playlist")
   default int countAudioCdPlaylists() {
      return this.countAudioCdPlaylists(null);
   }

   @Kind("element")
   @Type("audio CD playlist")
   int countAudioCdPlaylists(String var1);

   @Kind("element")
   @Type("library playlist")
   default LibraryPlaylist[] getLibraryPlaylists() {
      return this.getLibraryPlaylists(null);
   }

   @Kind("element")
   @Type("library playlist")
   LibraryPlaylist[] getLibraryPlaylists(String var1);

   @Kind("element")
   @Type("library playlist")
   LibraryPlaylist getLibraryPlaylist(int var1);

   @Kind("element")
   @Type("library playlist")
   LibraryPlaylist getLibraryPlaylist(Id var1);

   @Kind("element")
   @Type("library playlist")
   default int countLibraryPlaylists() {
      return this.countLibraryPlaylists(null);
   }

   @Kind("element")
   @Type("library playlist")
   int countLibraryPlaylists(String var1);

   @Kind("element")
   @Type("playlist")
   default Playlist[] getPlaylists() {
      return this.getPlaylists(null);
   }

   @Kind("element")
   @Type("playlist")
   Playlist[] getPlaylists(String var1);

   @Kind("element")
   @Type("playlist")
   Playlist getPlaylist(int var1);

   @Kind("element")
   @Type("playlist")
   Playlist getPlaylist(Id var1);

   @Kind("element")
   @Type("playlist")
   default int countPlaylists() {
      return this.countPlaylists(null);
   }

   @Kind("element")
   @Type("playlist")
   int countPlaylists(String var1);

   @Kind("element")
   @Type("radio tuner playlist")
   default RadioTunerPlaylist[] getRadioTunerPlaylists() {
      return this.getRadioTunerPlaylists(null);
   }

   @Kind("element")
   @Type("radio tuner playlist")
   RadioTunerPlaylist[] getRadioTunerPlaylists(String var1);

   @Kind("element")
   @Type("radio tuner playlist")
   RadioTunerPlaylist getRadioTunerPlaylist(int var1);

   @Kind("element")
   @Type("radio tuner playlist")
   RadioTunerPlaylist getRadioTunerPlaylist(Id var1);

   @Kind("element")
   @Type("radio tuner playlist")
   default int countRadioTunerPlaylists() {
      return this.countRadioTunerPlaylists(null);
   }

   @Kind("element")
   @Type("radio tuner playlist")
   int countRadioTunerPlaylists(String var1);

   @Kind("element")
   @Type("subscription playlist")
   default SubscriptionPlaylist[] getSubscriptionPlaylists() {
      return this.getSubscriptionPlaylists(null);
   }

   @Kind("element")
   @Type("subscription playlist")
   SubscriptionPlaylist[] getSubscriptionPlaylists(String var1);

   @Kind("element")
   @Type("subscription playlist")
   SubscriptionPlaylist getSubscriptionPlaylist(int var1);

   @Kind("element")
   @Type("subscription playlist")
   SubscriptionPlaylist getSubscriptionPlaylist(Id var1);

   @Kind("element")
   @Type("subscription playlist")
   default int countSubscriptionPlaylists() {
      return this.countSubscriptionPlaylists(null);
   }

   @Kind("element")
   @Type("subscription playlist")
   int countSubscriptionPlaylists(String var1);

   @Kind("element")
   @Type("user playlist")
   default UserPlaylist[] getUserPlaylists() {
      return this.getUserPlaylists(null);
   }

   @Kind("element")
   @Type("user playlist")
   UserPlaylist[] getUserPlaylists(String var1);

   @Kind("element")
   @Type("user playlist")
   UserPlaylist getUserPlaylist(int var1);

   @Kind("element")
   @Type("user playlist")
   UserPlaylist getUserPlaylist(Id var1);

   @Kind("element")
   @Type("user playlist")
   default int countUserPlaylists() {
      return this.countUserPlaylists(null);
   }

   @Kind("element")
   @Type("user playlist")
   int countUserPlaylists(String var1);

   @Code("capa")
   @Kind("property")
   @Name("capacity")
   @Type("double integer")
   long getCapacity();

   @Code("frsp")
   @Kind("property")
   @Name("free space")
   @Type("double integer")
   long getFreeSpace();

   @Code("pKnd")
   @Kind("property")
   @Name("kind")
   @Type("eSrc")
   Esrc getKind();

   @Override
   Map<String, Object> getProperties();
}
