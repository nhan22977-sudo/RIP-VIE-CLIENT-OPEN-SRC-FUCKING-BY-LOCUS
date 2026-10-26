package de.labystudio.spotifyapi.platform.windows.api.spotify;

import de.labystudio.spotifyapi.model.Track;
import de.labystudio.spotifyapi.platform.windows.api.WinProcess;
import de.labystudio.spotifyapi.platform.windows.api.jna.Psapi;
import de.labystudio.spotifyapi.platform.windows.api.jna.WindowsMediaControl;
import de.labystudio.spotifyapi.platform.windows.api.playback.PlaybackAccessor;
import de.labystudio.spotifyapi.platform.windows.api.playback.source.LegacyPlaybackAccessor;
import de.labystudio.spotifyapi.platform.windows.api.playback.source.MediaControlPlaybackAccessor;

public class SpotifyProcess extends WinProcess {
   private static final boolean DEBUG = System.getProperty("SPOTIFY_API_DEBUG") != null;
   private static final String PREFIX_SPOTIFY_TRACK = "spotify:track:";
   private static final long[] OFFSETS_TRACK_ID = new long[]{1621136L, 1395296L, 1374768L, 1073560L, 1362416L, 1057144L, 1350128L, 1044456L};
   private final long addressTrackId;
   private final PlaybackAccessor playbackAccessor;

   public SpotifyProcess(WindowsMediaControl mediaControl) {
      super("Spotify.exe");
      if (DEBUG) {
         System.out.println("Spotify process loaded! Searching for addresses...");
      }

      long timeScanStart = System.currentTimeMillis();
      this.addressTrackId = this.findTrackIdAddress();
      if (DEBUG) {
         System.out.println("Scanning took " + (System.currentTimeMillis() - timeScanStart) + "ms");
      }

      PlaybackAccessor accessor;
      try {
         if (mediaControl == null) {
            throw new IllegalArgumentException("MediaControl not available");
         }

         accessor = new MediaControlPlaybackAccessor(mediaControl);
      } catch (Throwable e) {
         e.printStackTrace();
         accessor = new LegacyPlaybackAccessor(this);
      }

      this.playbackAccessor = accessor;
   }

   private long findTrackIdAddress() {
      Psapi.ModuleInfo chromeElfModule = this.getModuleInfo("chrome_elf.dll");
      if (chromeElfModule == null) {
         throw new IllegalStateException("Could not find chrome_elf.dll module");
      }

      long chromeElfAddress = chromeElfModule.getBaseOfDll();
      long addressTrackId = -1L;
      long minTrackIdOffset = Long.MAX_VALUE;
      long maxTrackIdOffset = Long.MIN_VALUE;

      for (long trackIdOffset : OFFSETS_TRACK_ID) {
         minTrackIdOffset = Math.min(minTrackIdOffset, trackIdOffset);
         maxTrackIdOffset = Math.max(maxTrackIdOffset, trackIdOffset);
         long targetAddressTrackId = chromeElfAddress + trackIdOffset;
         if (Track.isTrackIdValid(this.readTrackId(targetAddressTrackId))) {
            addressTrackId = targetAddressTrackId;
            break;
         }
      }

      if (addressTrackId == -1L) {
         if (DEBUG) {
            System.out.println("Could not find track id with hardcoded offsets. Trying to find it dynamically...");
         }

         long threshold = (maxTrackIdOffset - minTrackIdOffset) * 3L;
         long scanAddressFrom = chromeElfAddress + minTrackIdOffset - threshold;
         long scanAddressTo = chromeElfAddress + maxTrackIdOffset + threshold;
         addressTrackId = this.findAddressOfText(
            scanAddressFrom, scanAddressTo, "spotify:track:", (address, index) -> Track.isTrackIdValid(this.readTrackId(address))
         );
      }

      if (addressTrackId == -1L) {
         throw new IllegalStateException("Could not find track id in memory");
      }

      if (DEBUG) {
         System.out
            .printf(
               "Found track id address: %s (+%s) [%s%s]%n",
               Long.toHexString(addressTrackId),
               Long.toHexString(addressTrackId - chromeElfAddress),
               "spotify:track:",
               this.readTrackId(addressTrackId)
            );
      }

      return addressTrackId;
   }

   private String readTrackId(long address) {
      return this.readString(address + 14L, 22);
   }

   public String readTrackId() {
      return this.readTrackId(this.addressTrackId);
   }

   public PlaybackAccessor getPlaybackAccessor() {
      return this.playbackAccessor;
   }
}
