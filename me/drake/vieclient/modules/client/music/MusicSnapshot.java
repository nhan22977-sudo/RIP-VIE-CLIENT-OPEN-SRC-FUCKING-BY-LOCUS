package me.drake.vieclient.modules.client.music;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;

public record MusicSnapshot(
   MusicSource source, String title, String owner, int durationMs, int positionMs, boolean playing, String artworkUrl, String identity, BufferedImage artwork
) {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MusicSnapshot(MusicSource source, String title, String owner, int durationMs, int positionMs, boolean playing, String artworkUrl, String identity) {
      this(sourcex, titlex, ownerx, durationMsx, positionMsx, playingx, artworkUrlx, identityx, null);
   }

   public MusicSnapshot(
      MusicSource source,
      String title,
      String owner,
      int durationMs,
      int positionMs,
      boolean playing,
      String artworkUrl,
      String identity,
      BufferedImage artwork
   ) {
      byte[] var10001 = new byte[11];
      var10001[0] = (byte)(119);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-2);
      var10001[4] = (byte)(-37);
      var10001[5] = (byte)(93);
      var10001[6] = (byte)(105);
      var10001[7] = (byte)(27);
      var10001[8] = (byte)(56);
      var10001[9] = (byte)(-52);
      var10001[10] = (byte)(-125);
      title = clean(title, $$v_s(var10001, 5335621182826991644L));
      var10001 = new byte[14];
      var10001[0] = (byte)(108);
      var10001[1] = (byte)(-12);
      var10001[2] = (byte)(119);
      var10001[3] = (byte)(-80);
      var10001[4] = (byte)(-28);
      var10001[5] = (byte)(70);
      var10001[6] = (byte)(102);
      var10001[7] = (byte)(66);
      var10001[8] = (byte)(16);
      var10001[9] = (byte)(-48);
      var10001[10] = (byte)(-112);
      var10001[11] = (byte)(-97);
      var10001[12] = (byte)(-96);
      var10001[13] = (byte)(-99);
      owner = clean(owner, $$v_s(var10001, 5335621182826991644L));
      artworkUrl = artworkUrl == null ? "" : artworkUrl.trim();
      identity = clean(identity, source + ":" + title + ":" + owner);
      durationMs = Math.max(0, durationMs);
      positionMs = Math.max(0, Math.min(positionMs, durationMs > 0 ? durationMs : positionMs));
      this.source = source;
      this.title = title;
      this.owner = owner;
      this.durationMs = durationMs;
      this.positionMs = positionMs;
      this.playing = playing;
      this.artworkUrl = artworkUrl;
      this.identity = identity;
      this.artwork = artwork;
   }

   public MusicSnapshot withMetadata(MusicMetadata metadata) {
      return metadata == null
         ? this
         : new MusicSnapshot(
            this.source,
            metadata.title().isBlank() ? this.title : metadata.title(),
            metadata.owner().isBlank() ? this.owner : metadata.owner(),
            metadata.durationMs() > 0 ? metadata.durationMs() : this.durationMs,
            this.positionMs,
            this.playing,
            metadata.artworkUrl().isBlank() ? this.artworkUrl : metadata.artworkUrl(),
            metadata.identity().isBlank() ? this.identity : metadata.identity(),
            this.artwork
         );
   }

   public MusicSnapshot withPosition(int newPositionMs) {
      return new MusicSnapshot(this.source, this.title, this.owner, this.durationMs, newPositionMs, this.playing, this.artworkUrl, this.identity, this.artwork);
   }

   public MusicSnapshot withPlaying(boolean newPlaying) {
      return new MusicSnapshot(this.source, this.title, this.owner, this.durationMs, this.positionMs, newPlaying, this.artworkUrl, this.identity, this.artwork);
   }

   private static String clean(String value, String fallback) {
      return value != null && !value.isBlank() ? value.trim() : fallback;
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
