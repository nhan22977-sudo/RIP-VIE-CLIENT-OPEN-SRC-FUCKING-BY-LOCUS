package de.labystudio.spotifyapi.platform.windows;

import de.labystudio.spotifyapi.SpotifyListener;
import de.labystudio.spotifyapi.model.MediaKey;
import de.labystudio.spotifyapi.model.Track;
import de.labystudio.spotifyapi.platform.AbstractTickSpotifyAPI;
import de.labystudio.spotifyapi.platform.windows.api.jna.WindowsMediaControl;
import de.labystudio.spotifyapi.platform.windows.api.playback.PlaybackAccessor;
import de.labystudio.spotifyapi.platform.windows.api.spotify.SpotifyProcess;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import javax.imageio.ImageIO;

public class WinSpotifyAPI extends AbstractTickSpotifyAPI {
   private static WindowsMediaControl mediaControl;
   private SpotifyProcess process;
   private Track currentTrack;
   private int currentPosition = -1;
   private boolean hasTrackPosition = false;
   private boolean isPlaying;
   private long lastTimePositionUpdated;
   private long prevLastReportedPosition = -1L;

   @Override
   protected void onInitialized() {
      try {
         this.initializeMediaControl(this.configuration.getNativesDirectory());
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }

   private void initializeMediaControl(Path nativesDirectory) throws IOException {
      if (mediaControl == null) {
         boolean is64Bit = System.getProperty("os.arch").contains("64");
         String path = "/natives/windows-x" + (is64Bit ? 64 : 86) + "/windowsmediacontrol.dll";

         try (InputStream nativesStream = SpotifyProcess.class.getResourceAsStream(path)) {
            if (nativesStream == null) {
               throw new IOException("Could not find native library: " + path);
            }

            Path nativeLibraryPath = nativesDirectory.resolve("windowsmediacontrol.dll");

            try {
               if (!Files.exists(nativesDirectory)) {
                  Files.createDirectories(nativesDirectory);
               }

               Files.copy(nativesStream, nativeLibraryPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
               throw new IOException("Failed to copy native library to " + nativeLibraryPath, e);
            }

            mediaControl = WindowsMediaControl.loadLibrary(nativeLibraryPath);
         }
      }
   }

   @Override
   protected void onTick() {
      if (!this.isConnected()) {
         this.process = new SpotifyProcess(mediaControl);
         this.listeners.forEach(SpotifyListener::onConnect);
      }

      String trackId = this.process.readTrackId();
      if (!Track.isTrackIdValid(trackId)) {
         throw new IllegalStateException("Invalid track ID: " + trackId);
      }

      PlaybackAccessor accessor = this.process.getPlaybackAccessor();
      accessor.updatePlayback();
      String currentTrackId = this.currentTrack == null ? null : this.currentTrack.getId();
      if (!Objects.equals(trackId, currentTrackId)) {
         accessor.updateTrack();
         String trackTitle = accessor.getTitle();
         String trackArtist = accessor.getArtist();
         String currentTrackTitle = this.currentTrack == null ? null : this.currentTrack.getName();
         String currentTrackArtist = this.currentTrack == null ? null : this.currentTrack.getArtist();
         if (!Objects.equals(trackTitle, currentTrackTitle) || !Objects.equals(trackArtist, currentTrackArtist)) {
            BufferedImage coverArt = this.toBufferedImage(accessor.getCoverArt());
            Track track = new Track(trackId, trackTitle, trackArtist, accessor.getLength(), coverArt);
            this.currentTrack = track;
            this.listeners.forEach(listener -> listener.onTrackChanged(track));
         }
      }

      boolean isPlaying = accessor.isPlaying();
      if (isPlaying != this.isPlaying) {
         this.isPlaying = isPlaying;
         this.listeners.forEach(listener -> listener.onPlayBackChanged(isPlaying));
      }

      if (accessor.hasTrackPosition()) {
         this.hasTrackPosition = true;
         int lastReportedPosition = accessor.getPosition();
         if (this.prevLastReportedPosition != lastReportedPosition) {
            this.prevLastReportedPosition = lastReportedPosition;
            int expectedPosition = this.getPosition();
            boolean seeked = Math.abs(lastReportedPosition - expectedPosition) > 1000L;
            this.currentPosition = lastReportedPosition;
            this.lastTimePositionUpdated = System.currentTimeMillis();
            if (seeked) {
               this.listeners.forEach(listener -> listener.onPositionChanged(this.currentPosition));
            }
         }
      } else {
         this.currentPosition = -1;
         this.hasTrackPosition = false;
         this.lastTimePositionUpdated = System.currentTimeMillis();
      }

      this.listeners.forEach(SpotifyListener::onSync);
   }

   @Override
   public Track getTrack() {
      return this.currentTrack;
   }

   @Override
   public int getPosition() {
      if (!this.hasPosition()) {
         throw new IllegalStateException("Position is not known yet. Pause the song for a second and try again.");
      } else if (this.isPlaying) {
         long timePassed = System.currentTimeMillis() - this.lastTimePositionUpdated;
         long interpolatedPosition = this.currentPosition + timePassed;
         return this.hasTrack() ? (int)Math.min(interpolatedPosition, this.currentTrack.getLength()) : (int)interpolatedPosition;
      } else {
         return this.currentPosition;
      }
   }

   @Override
   public boolean hasPosition() {
      return !this.isConnected() ? false : this.hasTrackPosition;
   }

   @Override
   public void pressMediaKey(MediaKey mediaKey) {
      if (!this.isConnected()) {
         throw new IllegalStateException("Spotify is not connected");
      }

      switch (mediaKey) {
         case NEXT:
            this.process.pressKey(176);
            break;
         case PREV:
            this.process.pressKey(177);
            break;
         case PLAY_PAUSE:
            this.process.pressKey(179);
      }

      this.onInternalTick();
   }

   @Override
   public boolean isPlaying() {
      return this.isPlaying;
   }

   @Override
   public boolean isConnected() {
      return this.process != null && this.process.isOpen();
   }

   @Override
   public void stop() {
      super.stop();
      if (this.process != null) {
         this.process.close();
         this.process = null;
      }

      this.currentTrack = null;
      this.currentPosition = -1;
      this.hasTrackPosition = false;
      this.isPlaying = false;
      this.lastTimePositionUpdated = 0L;
      this.prevLastReportedPosition = -1L;
   }

   private BufferedImage toBufferedImage(byte[] data) {
      if (data != null && data.length != 0) {
         try {
            return ImageIO.read(new ByteArrayInputStream(data));
         } catch (Throwable e) {
            e.printStackTrace();
            return null;
         }
      } else {
         return null;
      }
   }
}
