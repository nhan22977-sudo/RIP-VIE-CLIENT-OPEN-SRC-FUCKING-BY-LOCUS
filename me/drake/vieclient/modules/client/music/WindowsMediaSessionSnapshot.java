package me.drake.vieclient.modules.client.music;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public record WindowsMediaSessionSnapshot(
   String sourceApp,
   String title,
   String artist,
   int durationMs,
   int positionMs,
   boolean playing,
   BufferedImage artwork,
   long capturedAtMs,
   int rawPositionMs,
   long timelineUpdatedAtMs
) {
   private static final String[] SOUNDCLOUD_APP_PREFIXES = new String[]{
      "soundcloudltd.soundcloud-musicaudio_",
      "soundcloudltd.soundcloud_"
   };
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public WindowsMediaSessionSnapshot(
      String sourceApp,
      String title,
      String artist,
      int durationMs,
      int positionMs,
      boolean playing,
      BufferedImage artwork,
      long capturedAtMs,
      int rawPositionMs,
      long timelineUpdatedAtMs
   ) {
      sourceApp = clean(sourceApp);
      title = clean(title);
      artist = clean(artist);
      durationMs = Math.max(0, durationMs);
      positionMs = Math.max(0, Math.min(positionMs, durationMs > 0 ? durationMs : positionMs));
      capturedAtMs = Math.max(0L, capturedAtMs);
      rawPositionMs = Math.max(0, Math.min(rawPositionMs, durationMs > 0 ? durationMs : rawPositionMs));
      timelineUpdatedAtMs = Math.max(0L, timelineUpdatedAtMs);
      this.sourceApp = sourceApp;
      this.title = title;
      this.artist = artist;
      this.durationMs = durationMs;
      this.positionMs = positionMs;
      this.playing = playing;
      this.artwork = artwork;
      this.capturedAtMs = capturedAtMs;
      this.rawPositionMs = rawPositionMs;
      this.timelineUpdatedAtMs = timelineUpdatedAtMs;
   }

   public WindowsMediaSessionSnapshot(
      String sourceApp, String title, String artist, int durationMs, int positionMs, boolean playing, BufferedImage artwork, long capturedAtMs
   ) {
      this(sourceAppx, titlex, artistx, durationMsx, positionMsx, playingx, artworkx, capturedAtMsx, positionMsx, 0L);
   }

   public WindowsMediaSessionSnapshot(String sourceApp, String title, String artist, int durationMs, int positionMs, boolean playing, BufferedImage artwork) {
      this(sourceAppx, titlex, artistx, durationMsx, positionMsx, playingx, artworkx, 0L, positionMsx, 0L);
   }

   public boolean isSoundCloudApp() {
      String var1 = this.sourceApp.toLowerCase(Locale.ROOT);

      for (String var5 : SOUNDCLOUD_APP_PREFIXES) {
         if (var1.startsWith(var5)) {
            return true;
         }
      }

      return false;
   }

   public boolean isSupportedBrowser() {
      String var1 = this.sourceApp.toLowerCase(Locale.ROOT);
      return var1.contains("chrome")
         || var1.contains("thorium")
         || var1.contains("msedge")
         || var1.contains("microsoftedge")
         || var1.contains("firefox")
         || var1.contains("brave")
         || var1.contains("opera");
   }

   private static String clean(String value) {
      return value == null ? "" : value.trim();
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }
}
