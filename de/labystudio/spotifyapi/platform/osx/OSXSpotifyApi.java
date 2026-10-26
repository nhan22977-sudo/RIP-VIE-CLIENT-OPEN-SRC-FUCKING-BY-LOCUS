package de.labystudio.spotifyapi.platform.osx;

import de.labystudio.spotifyapi.SpotifyListener;
import de.labystudio.spotifyapi.model.MediaKey;
import de.labystudio.spotifyapi.model.Track;
import de.labystudio.spotifyapi.platform.AbstractTickSpotifyAPI;
import de.labystudio.spotifyapi.platform.osx.api.spotify.SpotifyAppleScript;
import java.util.Objects;

public class OSXSpotifyApi extends AbstractTickSpotifyAPI {
   private final SpotifyAppleScript appleScript = new SpotifyAppleScript();
   private boolean connected = false;
   private Track currentTrack;
   private int currentPosition = -1;
   private boolean isPlaying;
   private long lastTimePositionUpdated;

   @Override
   protected void onTick() throws Exception {
      String trackId = this.appleScript.getTrackId();
      if (!this.connected && !trackId.isEmpty()) {
         this.connected = true;
         this.listeners.forEach(SpotifyListener::onConnect);
      }

      if (!Objects.equals(trackId, this.currentTrack == null ? null : this.currentTrack.getId())) {
         String trackName = this.appleScript.getTrackName();
         String trackArtist = this.appleScript.getTrackArtist();
         int trackLength = this.appleScript.getTrackLength();
         boolean isFirstTrack = !this.hasTrack();
         Track track = new Track(trackId, trackName, trackArtist, trackLength, null);
         this.currentTrack = track;
         this.listeners.forEach(listener -> listener.onTrackChanged(track));
         if (!isFirstTrack) {
            this.updatePosition(0);
         }
      }

      boolean isPlaying = this.appleScript.getPlayerState();
      if (isPlaying != this.isPlaying) {
         this.isPlaying = isPlaying;
         this.listeners.forEach(listener -> listener.onPlayBackChanged(isPlaying));
      }

      int position = this.appleScript.getPlayerPosition();
      if (!this.hasPosition() || Math.abs(position - this.getPosition()) > 1000) {
         this.updatePosition(position);
      }

      this.listeners.forEach(SpotifyListener::onSync);
   }

   @Override
   public void stop() {
      super.stop();
      this.connected = false;
      this.currentTrack = null;
      this.currentPosition = -1;
      this.isPlaying = false;
      this.lastTimePositionUpdated = 0L;
   }

   private void updatePosition(int position) {
      if (position != this.currentPosition) {
         this.currentPosition = position;
         this.lastTimePositionUpdated = System.currentTimeMillis();
         this.listeners.forEach(listener -> listener.onPositionChanged(position));
      }
   }

   @Override
   public void pressMediaKey(MediaKey mediaKey) {
      try {
         switch (mediaKey) {
            case PLAY_PAUSE:
               this.appleScript.playPause();
               break;
            case NEXT:
               this.appleScript.nextTrack();
               break;
            case PREV:
               this.appleScript.previousTrack();
         }
      } catch (Exception e) {
         this.listeners.forEach(listener -> listener.onDisconnect(e));
         this.connected = false;
      }
   }

   @Override
   public int getPosition() {
      if (!this.hasPosition()) {
         throw new IllegalStateException("Position is not known yet");
      } else if (this.isPlaying) {
         long timePassed = System.currentTimeMillis() - this.lastTimePositionUpdated;
         return this.currentPosition + (int)timePassed;
      } else {
         return this.currentPosition;
      }
   }

   @Override
   public Track getTrack() {
      return this.currentTrack;
   }

   @Override
   public boolean isPlaying() {
      return this.isPlaying;
   }

   @Override
   public boolean isConnected() {
      return this.connected;
   }

   @Override
   public boolean hasPosition() {
      return this.currentPosition != -1;
   }
}
