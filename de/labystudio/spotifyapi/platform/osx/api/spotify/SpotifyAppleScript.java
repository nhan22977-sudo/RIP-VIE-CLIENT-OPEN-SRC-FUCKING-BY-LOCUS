package de.labystudio.spotifyapi.platform.osx.api.spotify;

import de.labystudio.spotifyapi.platform.osx.api.Action;
import de.labystudio.spotifyapi.platform.osx.api.AppleScript;

public class SpotifyAppleScript extends AppleScript {
   public static final Action CURRENT_TRACK = new Action("current", "track");
   public static final Action ID = new Action("id");
   public static final Action NAME = new Action("name");
   public static final Action ARTIST = new Action("artist");
   public static final Action LENGTH = new Action("duration");
   public static final Action PLAYER_POSITION = new Action("player", "position");
   public static final Action PLAYER_STATE = new Action("player", "state");
   public static final Action PLAY_PAUSE = new Action("playpause");
   public static final Action NEXT_TRACK = new Action("next", "track");
   public static final Action PREVIOUS_TRACK = new Action("previous", "track");

   public SpotifyAppleScript() {
      super("Spotify");
   }

   public String getTrackId() throws Exception {
      return this.getOf(ID, CURRENT_TRACK).substring(14);
   }

   public String getTrackName() throws Exception {
      return this.getOf(NAME, CURRENT_TRACK);
   }

   public String getTrackArtist() throws Exception {
      return this.getOf(ARTIST, CURRENT_TRACK);
   }

   public int getTrackLength() throws Exception {
      return Integer.parseInt(this.getOf(LENGTH, CURRENT_TRACK));
   }

   public int getPlayerPosition() throws Exception {
      return (int)(Double.parseDouble(this.get(PLAYER_POSITION)) * 1000.0);
   }

   public boolean getPlayerState() throws Exception {
      return this.get(PLAYER_STATE).equals("playing");
   }

   public void playPause() throws Exception {
      this.execute(PLAY_PAUSE);
   }

   public void nextTrack() throws Exception {
      this.execute(NEXT_TRACK);
   }

   public void previousTrack() throws Exception {
      this.execute(PREVIOUS_TRACK);
   }
}
