package de.labystudio.spotifyapi.platform.windows.api.playback.source;

import de.labystudio.spotifyapi.platform.windows.api.playback.PlaybackAccessor;
import de.labystudio.spotifyapi.platform.windows.api.spotify.SpotifyProcess;
import de.labystudio.spotifyapi.platform.windows.api.spotify.SpotifyWindowTitle;

/** @deprecated */
public class LegacyPlaybackAccessor implements PlaybackAccessor {
   private final SpotifyProcess spotifyProcess;
   private boolean playing;
   private SpotifyWindowTitle windowTitle = SpotifyWindowTitle.UNKNOWN;

   public LegacyPlaybackAccessor(SpotifyProcess spotifyProcess) {
      this.spotifyProcess = spotifyProcess;
   }

   @Override
   public void updatePlayback() {
      SpotifyWindowTitle windowTitle = SpotifyWindowTitle.of(this.spotifyProcess.getWindowTitle());
      if (windowTitle == null) {
         this.playing = false;
      } else {
         this.windowTitle = windowTitle;
         this.playing = true;
      }
   }

   @Override
   public void updateTrack() {
   }

   @Override
   public boolean isValid() {
      return true;
   }

   @Override
   public int getLength() {
      return -1;
   }

   @Override
   public int getPosition() {
      return -1;
   }

   @Override
   public boolean isPlaying() {
      return this.playing;
   }

   @Override
   public String getTitle() {
      return this.windowTitle.getTrackName();
   }

   @Override
   public String getArtist() {
      return this.windowTitle.getTrackArtist();
   }

   @Override
   public byte[] getCoverArt() {
      return null;
   }
}
