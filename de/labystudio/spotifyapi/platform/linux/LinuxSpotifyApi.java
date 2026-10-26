package de.labystudio.spotifyapi.platform.linux;

import de.labystudio.spotifyapi.SpotifyListener;
import de.labystudio.spotifyapi.model.MediaKey;
import de.labystudio.spotifyapi.model.Track;
import de.labystudio.spotifyapi.platform.AbstractTickSpotifyAPI;
import de.labystudio.spotifyapi.platform.linux.api.MPRISCommunicator;
import de.labystudio.spotifyapi.platform.linux.api.model.Metadata;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Objects;
import javax.imageio.ImageIO;

public class LinuxSpotifyApi extends AbstractTickSpotifyAPI {
   private boolean connected = false;
   private Track currentTrack;
   private int currentPosition = -1;
   private boolean isPlaying;
   private long lastTimePositionUpdated;
   private final MPRISCommunicator mediaPlayer = new MPRISCommunicator();

   @Override
   protected void onTick() throws Exception {
      Metadata metadata = this.mediaPlayer.readMetadata();
      String trackId = metadata.getTrackId();
      if (!this.connected) {
         this.connected = true;
         this.listeners.forEach(SpotifyListener::onConnect);
      }

      String currentTrackId = this.currentTrack == null ? null : this.currentTrack.getId();
      if (!Objects.equals(trackId, currentTrackId)) {
         String trackName = metadata.getTrackName();
         String trackArtist = metadata.getArtistsJoined();
         int trackLength = metadata.getTrackLength();
         BufferedImage coverArt = this.toBufferedImage(metadata.getArtUrl());
         boolean isFirstTrack = !this.hasTrack();
         Track track = new Track(trackId, trackName, trackArtist, trackLength, coverArt);
         this.currentTrack = track;
         this.listeners.forEach(listener -> listener.onTrackChanged(track));
         if (!isFirstTrack) {
            this.updatePosition(0);
         }
      }

      boolean isPlaying = this.mediaPlayer.readIsPlaying();
      if (isPlaying != this.isPlaying) {
         this.isPlaying = isPlaying;
         this.listeners.forEach(listener -> listener.onPlayBackChanged(isPlaying));
      }

      int position = this.mediaPlayer.readPosition();
      if (!this.hasPosition() || Math.abs(position - this.getPosition()) >= 1000) {
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
               this.mediaPlayer.playPause();
               break;
            case NEXT:
               this.mediaPlayer.next();
               break;
            case PREV:
               this.mediaPlayer.previous();
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

   private BufferedImage toBufferedImage(String artUrl) {
      if (artUrl != null && !artUrl.isEmpty()) {
         try {
            return ImageIO.read(new URL(artUrl));
         } catch (Throwable e) {
            e.printStackTrace();
            return null;
         }
      } else {
         return null;
      }
   }
}
