package de.labystudio.spotifyapi.platform.windows.api.playback.source;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import de.labystudio.spotifyapi.platform.windows.api.jna.WindowsMediaControl;
import de.labystudio.spotifyapi.platform.windows.api.playback.PlaybackAccessor;

public class MediaControlPlaybackAccessor implements PlaybackAccessor {
   private final WindowsMediaControl mediaControl;
   private long playbackPosition;
   private long trackDuration;
   private boolean isPlaying;
   private String title;
   private String artist;
   private byte[] coverArt;

   public MediaControlPlaybackAccessor(WindowsMediaControl mediaControl) {
      if (mediaControl == null) {
         throw new IllegalArgumentException("MediaControl cannot be null");
      }

      this.mediaControl = mediaControl;
   }

   @Override
   public void updatePlayback() {
      this.playbackPosition = this.mediaControl.getPlaybackPosition();
      if (this.playbackPosition == -1L) {
         throw new IllegalStateException("Playback information unavailable");
      }

      int isPlaying = this.mediaControl.isPlaying();
      if (isPlaying < 0) {
         throw new IllegalStateException("Failed to retrieve playback state");
      }

      this.isPlaying = isPlaying == 1;
   }

   @Override
   public void updateTrack() {
      this.trackDuration = this.mediaControl.getTrackDuration();
      if (this.trackDuration <= 0L) {
         throw new IllegalStateException("Track duration is invalid or unavailable");
      }

      Pointer titlePtr = this.mediaControl.getTrackTitle();
      if (titlePtr == null) {
         throw new IllegalStateException("Track title pointer is null");
      }

      this.title = titlePtr.getString(0L, "UTF-8");
      this.mediaControl.freeString(titlePtr);
      Pointer artistPtr = this.mediaControl.getArtistName();
      if (artistPtr == null) {
         throw new IllegalStateException("Artist name pointer is null");
      }

      this.artist = artistPtr.getString(0L, "UTF-8");
      this.mediaControl.freeString(artistPtr);
      PointerByReference bufferRef = new PointerByReference();
      NativeLongByReference lengthRef = new NativeLongByReference();
      if (this.mediaControl.getCoverArt(bufferRef, lengthRef)) {
         Pointer buffer = bufferRef.getValue();
         if (buffer == null) {
            this.coverArt = null;
         } else {
            int length = lengthRef.getValue().intValue();
            this.coverArt = buffer.getByteArray(0L, length);
            this.mediaControl.freeCoverArt(buffer);
         }
      }
   }

   @Override
   public boolean isValid() {
      return this.playbackPosition >= 0L && this.trackDuration > 0L && this.playbackPosition <= this.trackDuration;
   }

   @Override
   public int getLength() {
      return (int)this.trackDuration;
   }

   @Override
   public int getPosition() {
      return (int)this.playbackPosition;
   }

   @Override
   public boolean isPlaying() {
      return this.isPlaying;
   }

   @Override
   public String getTitle() {
      return this.title;
   }

   @Override
   public String getArtist() {
      return this.artist;
   }

   @Override
   public byte[] getCoverArt() {
      return this.coverArt != null ? this.coverArt : null;
   }
}
