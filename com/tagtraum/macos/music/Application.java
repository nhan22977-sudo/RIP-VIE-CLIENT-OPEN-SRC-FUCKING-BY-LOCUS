package com.tagtraum.macos.music;

import com.tagtraum.japlscript.Code;
import com.tagtraum.japlscript.Id;
import com.tagtraum.japlscript.JaplScript;
import com.tagtraum.japlscript.Kind;
import com.tagtraum.japlscript.Name;
import com.tagtraum.japlscript.Parameter;
import com.tagtraum.japlscript.Reference;
import com.tagtraum.japlscript.Type;
import com.tagtraum.japlscript.language.JaplScriptFile;
import com.tagtraum.japlscript.language.TypeClass;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Code("capp")
@Name("application")
public interface Application extends Reference {
   TypeClass CLASS = new TypeClass("application", "«class capp»", Application.class, null);
   Set<Class<?>> APPLICATION_CLASSES = new HashSet<>(
      Arrays.asList(
         FolderPlaylist.class,
         BrowserWindow.class,
         Source.class,
         AudioCdPlaylist.class,
         RadioTunerPlaylist.class,
         AirplayDevice.class,
         Playlist.class,
         SubscriptionPlaylist.class,
         Visual.class,
         AudioCdTrack.class,
         Track.class,
         PlaylistWindow.class,
         EqPreset.class,
         UrlTrack.class,
         VideoWindow.class,
         EqWindow.class,
         Item.class,
         Artwork.class,
         SharedTrack.class,
         LibraryPlaylist.class,
         Encoder.class,
         Application.class,
         UserPlaylist.class,
         MiniplayerWindow.class,
         Window.class,
         FileTrack.class,
         PrintSettings.class,
         Eknd.class,
         Enum.class,
         Epls.class,
         Erpt.class,
         Eshm.class,
         Esrc.class,
         Esra.class,
         Espk.class,
         Emdk.class,
         Ertk.class,
         Eapd.class,
         Ecls.class,
         Eexf.class
      )
   );

   @Kind("command")
   @Name("print")
   void print(
      Reference var1,
      @Parameter("print dialog") boolean var2,
      @Parameter("with properties") PrintSettings var3,
      @Parameter("kind") Eknd var4,
      @Parameter("theme") String var5
   );

   @Kind("command")
   @Name("close")
   void close(Reference var1);

   @Kind("command")
   @Name("count")
   int count(Reference var1, @Parameter("each") TypeClass var2);

   @Kind("command")
   @Name("delete")
   void delete(Reference var1);

   @Kind("command")
   @Name("duplicate")
   Reference duplicate(Reference var1, @Parameter("to") Reference var2);

   @Kind("command")
   @Name("exists")
   boolean exists(Reference var1);

   @Kind("command")
   @Name("make")
   Reference make(@Parameter("new") TypeClass var1, @Parameter("at") Reference var2, @Parameter("with properties") Map<String, Reference> var3);

   @Kind("make")
   <T extends Reference> T make(Class<T> var1);

   @Kind("command")
   @Name("move")
   void move(Playlist var1, @Parameter("to") Reference var2);

   @Kind("command")
   @Name("open")
   void open(Reference var1);

   @Kind("command")
   @Name("run")
   void run();

   @Kind("command")
   @Name("quit")
   void quit();

   @Kind("command")
   @Name("save")
   void save(Reference var1);

   @Kind("command")
   @Name("add")
   Track[] add(JaplScriptFile[] var1, @Parameter("to") Reference var2);

   @Kind("command")
   @Name("back track")
   void backTrack();

   @Kind("command")
   @Name("convert")
   Track[] convert(Reference[] var1);

   @Kind("command")
   @Name("download")
   void download(Item var1);

   @Kind("command")
   @Name("export")
   String export(Item var1, @Parameter("as") Eexf var2, @Parameter("to") JaplScriptFile var3);

   @Kind("command")
   @Name("fast forward")
   void fastForward();

   @Kind("command")
   @Name("next track")
   void nextTrack();

   @Kind("command")
   @Name("pause")
   void pause();

   @Kind("command")
   @Name("play")
   void play(Reference var1, @Parameter("once") boolean var2);

   @Kind("command")
   @Name("playpause")
   void playpause();

   @Kind("command")
   @Name("previous track")
   void previousTrack();

   @Kind("command")
   @Name("refresh")
   void refresh(FileTrack var1);

   @Kind("command")
   @Name("resume")
   void resume();

   @Kind("command")
   @Name("reveal")
   void reveal(Item var1);

   @Kind("command")
   @Name("rewind")
   void rewind();

   @Kind("command")
   @Name("search")
   Track[] search(Playlist var1, @Parameter("for") String var2, @Parameter("only") Esra var3);

   @Kind("command")
   @Name("select")
   void select(Reference var1);

   @Kind("command")
   @Name("stop")
   void stop();

   @Kind("command")
   @Name("open location")
   void openLocation(String var1);

   @Kind("element")
   @Type("AirPlay device")
   default AirplayDevice[] getAirplayDevices() {
      return this.getAirplayDevices(null);
   }

   @Kind("element")
   @Type("AirPlay device")
   AirplayDevice[] getAirplayDevices(String var1);

   @Kind("element")
   @Type("AirPlay device")
   AirplayDevice getAirplayDevice(int var1);

   @Kind("element")
   @Type("AirPlay device")
   AirplayDevice getAirplayDevice(Id var1);

   @Kind("element")
   @Type("AirPlay device")
   default int countAirplayDevices() {
      return this.countAirplayDevices(null);
   }

   @Kind("element")
   @Type("AirPlay device")
   int countAirplayDevices(String var1);

   @Kind("element")
   @Type("browser window")
   default BrowserWindow[] getBrowserWindows() {
      return this.getBrowserWindows(null);
   }

   @Kind("element")
   @Type("browser window")
   BrowserWindow[] getBrowserWindows(String var1);

   @Kind("element")
   @Type("browser window")
   BrowserWindow getBrowserWindow(int var1);

   @Kind("element")
   @Type("browser window")
   BrowserWindow getBrowserWindow(Id var1);

   @Kind("element")
   @Type("browser window")
   default int countBrowserWindows() {
      return this.countBrowserWindows(null);
   }

   @Kind("element")
   @Type("browser window")
   int countBrowserWindows(String var1);

   @Kind("element")
   @Type("encoder")
   default Encoder[] getEncoders() {
      return this.getEncoders(null);
   }

   @Kind("element")
   @Type("encoder")
   Encoder[] getEncoders(String var1);

   @Kind("element")
   @Type("encoder")
   Encoder getEncoder(int var1);

   @Kind("element")
   @Type("encoder")
   Encoder getEncoder(Id var1);

   @Kind("element")
   @Type("encoder")
   default int countEncoders() {
      return this.countEncoders(null);
   }

   @Kind("element")
   @Type("encoder")
   int countEncoders(String var1);

   @Kind("element")
   @Type("EQ preset")
   default EqPreset[] getEqPresets() {
      return this.getEqPresets(null);
   }

   @Kind("element")
   @Type("EQ preset")
   EqPreset[] getEqPresets(String var1);

   @Kind("element")
   @Type("EQ preset")
   EqPreset getEqPreset(int var1);

   @Kind("element")
   @Type("EQ preset")
   EqPreset getEqPreset(Id var1);

   @Kind("element")
   @Type("EQ preset")
   default int countEqPresets() {
      return this.countEqPresets(null);
   }

   @Kind("element")
   @Type("EQ preset")
   int countEqPresets(String var1);

   @Kind("element")
   @Type("EQ window")
   default EqWindow[] getEqWindows() {
      return this.getEqWindows(null);
   }

   @Kind("element")
   @Type("EQ window")
   EqWindow[] getEqWindows(String var1);

   @Kind("element")
   @Type("EQ window")
   EqWindow getEqWindow(int var1);

   @Kind("element")
   @Type("EQ window")
   EqWindow getEqWindow(Id var1);

   @Kind("element")
   @Type("EQ window")
   default int countEqWindows() {
      return this.countEqWindows(null);
   }

   @Kind("element")
   @Type("EQ window")
   int countEqWindows(String var1);

   @Kind("element")
   @Type("miniplayer window")
   default MiniplayerWindow[] getMiniplayerWindows() {
      return this.getMiniplayerWindows(null);
   }

   @Kind("element")
   @Type("miniplayer window")
   MiniplayerWindow[] getMiniplayerWindows(String var1);

   @Kind("element")
   @Type("miniplayer window")
   MiniplayerWindow getMiniplayerWindow(int var1);

   @Kind("element")
   @Type("miniplayer window")
   MiniplayerWindow getMiniplayerWindow(Id var1);

   @Kind("element")
   @Type("miniplayer window")
   default int countMiniplayerWindows() {
      return this.countMiniplayerWindows(null);
   }

   @Kind("element")
   @Type("miniplayer window")
   int countMiniplayerWindows(String var1);

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
   @Type("playlist window")
   default PlaylistWindow[] getPlaylistWindows() {
      return this.getPlaylistWindows(null);
   }

   @Kind("element")
   @Type("playlist window")
   PlaylistWindow[] getPlaylistWindows(String var1);

   @Kind("element")
   @Type("playlist window")
   PlaylistWindow getPlaylistWindow(int var1);

   @Kind("element")
   @Type("playlist window")
   PlaylistWindow getPlaylistWindow(Id var1);

   @Kind("element")
   @Type("playlist window")
   default int countPlaylistWindows() {
      return this.countPlaylistWindows(null);
   }

   @Kind("element")
   @Type("playlist window")
   int countPlaylistWindows(String var1);

   @Kind("element")
   @Type("source")
   default Source[] getSources() {
      return this.getSources(null);
   }

   @Kind("element")
   @Type("source")
   Source[] getSources(String var1);

   @Kind("element")
   @Type("source")
   Source getSource(int var1);

   @Kind("element")
   @Type("source")
   Source getSource(Id var1);

   @Kind("element")
   @Type("source")
   default int countSources() {
      return this.countSources(null);
   }

   @Kind("element")
   @Type("source")
   int countSources(String var1);

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
   @Type("video window")
   default VideoWindow[] getVideoWindows() {
      return this.getVideoWindows(null);
   }

   @Kind("element")
   @Type("video window")
   VideoWindow[] getVideoWindows(String var1);

   @Kind("element")
   @Type("video window")
   VideoWindow getVideoWindow(int var1);

   @Kind("element")
   @Type("video window")
   VideoWindow getVideoWindow(Id var1);

   @Kind("element")
   @Type("video window")
   default int countVideoWindows() {
      return this.countVideoWindows(null);
   }

   @Kind("element")
   @Type("video window")
   int countVideoWindows(String var1);

   @Kind("element")
   @Type("visual")
   default Visual[] getVisuals() {
      return this.getVisuals(null);
   }

   @Kind("element")
   @Type("visual")
   Visual[] getVisuals(String var1);

   @Kind("element")
   @Type("visual")
   Visual getVisual(int var1);

   @Kind("element")
   @Type("visual")
   Visual getVisual(Id var1);

   @Kind("element")
   @Type("visual")
   default int countVisuals() {
      return this.countVisuals(null);
   }

   @Kind("element")
   @Type("visual")
   int countVisuals(String var1);

   @Kind("element")
   @Type("window")
   default Window[] getWindows() {
      return this.getWindows(null);
   }

   @Kind("element")
   @Type("window")
   Window[] getWindows(String var1);

   @Kind("element")
   @Type("window")
   Window getWindow(int var1);

   @Kind("element")
   @Type("window")
   Window getWindow(Id var1);

   @Kind("element")
   @Type("window")
   default int countWindows() {
      return this.countWindows(null);
   }

   @Kind("element")
   @Type("window")
   int countWindows(String var1);

   @Code("pAPE")
   @Kind("property")
   @Name("AirPlay enabled")
   @Type("boolean")
   boolean isAirplayEnabled();

   @Code("pCnv")
   @Kind("property")
   @Name("converting")
   @Type("boolean")
   boolean isConverting();

   @Code("pAPD")
   @Kind("property")
   @Name("current AirPlay devices")
   @Type("AirPlay device")
   AirplayDevice[] getCurrentAirplayDevices();

   @Code("pAPD")
   @Kind("property")
   @Name("current AirPlay devices")
   @Type("AirPlay device")
   void setCurrentAirplayDevices(AirplayDevice[] var1);

   @Code("pEnc")
   @Kind("property")
   @Name("current encoder")
   @Type("encoder")
   Encoder getCurrentEncoder();

   @Code("pEnc")
   @Kind("property")
   @Name("current encoder")
   @Type("encoder")
   void setCurrentEncoder(Encoder var1);

   @Code("pEQP")
   @Kind("property")
   @Name("current EQ preset")
   @Type("EQ preset")
   EqPreset getCurrentEqPreset();

   @Code("pEQP")
   @Kind("property")
   @Name("current EQ preset")
   @Type("EQ preset")
   void setCurrentEqPreset(EqPreset var1);

   @Code("pPla")
   @Kind("property")
   @Name("current playlist")
   @Type("playlist")
   Playlist getCurrentPlaylist();

   @Code("pStT")
   @Kind("property")
   @Name("current stream title")
   @Type("text")
   String getCurrentStreamTitle();

   @Code("pStU")
   @Kind("property")
   @Name("current stream URL")
   @Type("text")
   String getCurrentStreamUrl();

   @Code("pTrk")
   @Kind("property")
   @Name("current track")
   @Type("track")
   Track getCurrentTrack();

   @Code("pVis")
   @Kind("property")
   @Name("current visual")
   @Type("visual")
   Visual getCurrentVisual();

   @Code("pVis")
   @Kind("property")
   @Name("current visual")
   @Type("visual")
   void setCurrentVisual(Visual var1);

   @Code("pEQ ")
   @Kind("property")
   @Name("EQ enabled")
   @Type("boolean")
   boolean isEqEnabled();

   @Code("pEQ ")
   @Kind("property")
   @Name("EQ enabled")
   @Type("boolean")
   void setEqEnabled(boolean var1);

   @Code("pFix")
   @Kind("property")
   @Name("fixed indexing")
   @Type("boolean")
   boolean isFixedIndexing();

   @Code("pFix")
   @Kind("property")
   @Name("fixed indexing")
   @Type("boolean")
   void setFixedIndexing(boolean var1);

   @Code("pisf")
   @Kind("property")
   @Name("frontmost")
   @Type("boolean")
   boolean isFrontmost();

   @Code("pisf")
   @Kind("property")
   @Name("frontmost")
   @Type("boolean")
   void setFrontmost(boolean var1);

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

   @Code("pnam")
   @Kind("property")
   @Name("name")
   @Type("text")
   String getName();

   @Code("pMut")
   @Kind("property")
   @Name("mute")
   @Type("boolean")
   boolean isMute();

   @Code("pMut")
   @Kind("property")
   @Name("mute")
   @Type("boolean")
   void setMute(boolean var1);

   @Code("pPos")
   @Kind("property")
   @Name("player position")
   @Type("real")
   double getPlayerPosition();

   @Code("pPos")
   @Kind("property")
   @Name("player position")
   @Type("real")
   void setPlayerPosition(double var1);

   @Code("pPlS")
   @Kind("property")
   @Name("player state")
   @Type("ePlS")
   Epls getPlayerState();

   @Code("sele")
   @Kind("property")
   @Name("selection")
   @Type("specifier")
   Reference getSelection();

   @Code("pShE")
   @Kind("property")
   @Name("shuffle enabled")
   @Type("boolean")
   boolean isShuffleEnabled();

   @Code("pShE")
   @Kind("property")
   @Name("shuffle enabled")
   @Type("boolean")
   void setShuffleEnabled(boolean var1);

   @Code("pShM")
   @Kind("property")
   @Name("shuffle mode")
   @Type("eShM")
   Eshm getShuffleMode();

   @Code("pShM")
   @Kind("property")
   @Name("shuffle mode")
   @Type("eShM")
   void setShuffleMode(Eshm var1);

   @Code("pRpt")
   @Kind("property")
   @Name("song repeat")
   @Type("eRpt")
   Erpt getSongRepeat();

   @Code("pRpt")
   @Kind("property")
   @Name("song repeat")
   @Type("eRpt")
   void setSongRepeat(Erpt var1);

   @Code("pVol")
   @Kind("property")
   @Name("sound volume")
   @Type("integer")
   int getSoundVolume();

   @Code("pVol")
   @Kind("property")
   @Name("sound volume")
   @Type("integer")
   void setSoundVolume(int var1);

   @Code("vers")
   @Kind("property")
   @Name("version")
   @Type("text")
   String getVersion();

   @Code("pVsE")
   @Kind("property")
   @Name("visuals enabled")
   @Type("boolean")
   boolean isVisualsEnabled();

   @Code("pVsE")
   @Kind("property")
   @Name("visuals enabled")
   @Type("boolean")
   void setVisualsEnabled(boolean var1);

   Map<String, Object> getProperties();

   static Application getInstance() {
      return JaplScript.getApplication(Application.class, "Music");
   }
}
