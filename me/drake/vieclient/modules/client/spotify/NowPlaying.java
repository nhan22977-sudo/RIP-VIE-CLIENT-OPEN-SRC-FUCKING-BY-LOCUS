package me.drake.vieclient.modules.client.spotify;

import java.nio.charset.StandardCharsets;

public record NowPlaying(
   NowPlaying.Status status,
   String title,
   String artist,
   String album,
   NowPlaying.Source source,
   long durationMs,
   long positionMs,
   long sampledAtMs,
   double rate,
   Object art
) {
   public static final long LYRICS_MIN_LENGTH_MS = 30000L;
   public static final long LYRICS_MAX_LENGTH_MS = 3600000L;
   public static final NowPlaying UNSUPPORTED = idle(NowPlaying.Status.UNSUPPORTED);
   public static final NowPlaying WAITING = idle(NowPlaying.Status.WAITING);
   public static final NowPlaying NOT_RUNNING = idle(NowPlaying.Status.NOT_RUNNING);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public NowPlaying(
      NowPlaying.Status status,
      String title,
      String artist,
      String album,
      NowPlaying.Source source,
      long durationMs,
      long positionMs,
      long sampledAtMs,
      double rate,
      Object art
   ) {
      if (status == null) {
         status = NowPlaying.Status.NOT_RUNNING;
      }

      if (source == null) {
         source = NowPlaying.Source.NONE;
      }

      title = title == null ? "" : title;
      artist = artist == null ? "" : artist;
      album = album == null ? "" : album;
      durationMs = Math.max(0L, durationMs);
      positionMs = durationMs > 0L ? Math.max(0L, Math.min(positionMs, durationMs)) : Math.max(0L, positionMs);
      if (!(rate > 0.0) || rate > 16.0) {
         rate = 1.0;
      }

      this.status = status;
      this.title = title;
      this.artist = artist;
      this.album = album;
      this.source = source;
      this.durationMs = durationMs;
      this.positionMs = positionMs;
      this.sampledAtMs = sampledAtMs;
      this.rate = rate;
      this.art = art;
   }

   public NowPlaying(NowPlaying.Status status, String title, String artist) {
      this(statusx, titlex, artistx, "", NowPlaying.Source.WINDOW, 0L, 0L, 0L, 1.0, null);
   }

   private static NowPlaying idle(NowPlaying.Status s) {
      return new NowPlaying(s, "", "", "", NowPlaying.Source.NONE, 0L, 0L, 0L, 1.0, null);
   }

   public boolean playing() {
      return this.status == NowPlaying.Status.PLAYING;
   }

   public boolean hasTrack() {
      return !this.title.isEmpty();
   }

   public boolean active() {
      return this.status == NowPlaying.Status.PLAYING || this.status == NowPlaying.Status.PAUSED;
   }

   public boolean hasTimeline() {
      return this.source == NowPlaying.Source.SESSION && this.durationMs > 0L;
   }

   public boolean lyricsEligible() {
      if (!this.hasTimeline() || !this.hasTrack() || this.artist.isBlank()) {
         return false;
      }

      if (this.durationMs >= 30000L && this.durationMs <= 3600000L) {
         byte[] var10001 = new byte[7];
         var10001[0] = (byte)(106);
         var10001[1] = (byte)(-22);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(-78);
         var10001[5] = (byte)(-10);
         var10001[6] = (byte)(-36);
         if (!this.artist.equalsIgnoreCase($$v_s(var10001, -7158948373595959268L))) {
            var10001 = new byte[13];
            var10001[0] = (byte)(120);
            var10001[1] = (byte)(-2);
            var10001[2] = (byte)(106);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(-87);
            var10001[5] = (byte)(-28);
            var10001[6] = (byte)(-52);
            var10001[7] = (byte)(-57);
            var10001[8] = (byte)(52);
            var10001[9] = (byte)(-49);
            var10001[10] = (byte)(-127);
            var10001[11] = (byte)(-104);
            var10001[12] = (byte)(-9);
            if (!this.title.equalsIgnoreCase($$v_s(var10001, -7158948373595959268L))) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean sameTrack(NowPlaying other) {
      return other != null && this.title.equals(other.title) && this.artist.equals(other.artist) && this.album.equals(other.album);
   }

   public long positionAt(long nowMs) {
      if (!this.hasTimeline()) {
         return 0L;
      }

      if (!this.playing()) {
         return this.positionMs;
      }

      long var3 = this.positionMs + (long)(Math.max(0L, nowMs - this.sampledAtMs) * this.rate);
      return Math.min(var3, this.durationMs);
   }

   public NowPlaying toggled() {
      long var1 = System.currentTimeMillis();

      return switch (this.status) {
         case PLAYING -> new NowPlaying(
            NowPlaying.Status.PAUSED, this.title, this.artist, this.album, this.source, this.durationMs, this.positionAt(var1), var1, this.rate, this.art
         );
         case PAUSED -> this.hasTrack()
            ? new NowPlaying(
               NowPlaying.Status.PLAYING, this.title, this.artist, this.album, this.source, this.durationMs, this.positionMs, var1, this.rate, this.art
            )
            : this;
         default -> this;
      };
   }

   public static NowPlaying fromLabyStudio(String title, String artist, long durationMs, long positionMs, boolean playing) {
      NowPlaying.Status var7 = playing ? NowPlaying.Status.PLAYING : NowPlaying.Status.PAUSED;
      return new NowPlaying(
         var7,
         titlex == null ? "" : titlex,
         artistx == null ? "" : artistx,
         "",
         NowPlaying.Source.SESSION,
         durationMsx,
         positionMsx,
         System.currentTimeMillis(),
         1.0,
         null
      );
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

   public enum Source {
      NONE,
      WINDOW,
      SESSION;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static NowPlaying.Source[] $values() {
         NowPlaying.Source[] var10000 = new NowPlaying.Source[3];
         var10000[0] = NONE;
         var10000[1] = WINDOW;
         var10000[2] = SESSION;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         NONE = new NowPlaying.Source();
         var10002 = new byte[6];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-102);
         var10002[4] = (byte)(-53);
         var10002[5] = (byte)(56);
         WINDOW = new NowPlaying.Source();
         var10002 = new byte[7];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(-51);
         var10002[5] = (byte)(32);
         var10002[6] = (byte)(97);
         SESSION = new NowPlaying.Source();
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

   public enum Status {
      UNSUPPORTED,
      WAITING,
      NOT_RUNNING,
      PLAYING,
      PAUSED;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static NowPlaying.Status[] $values() {
         NowPlaying.Status[] var10000 = new NowPlaying.Status[5];
         var10000[0] = UNSUPPORTED;
         var10000[1] = WAITING;
         var10000[2] = NOT_RUNNING;
         var10000[3] = PLAYING;
         var10000[4] = PAUSED;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[11];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-44);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-117);
         var10002[4] = (byte)(13);
         var10002[5] = (byte)(-122);
         var10002[6] = (byte)(35);
         var10002[7] = (byte)(-100);
         var10002[8] = (byte)(2);
         var10002[9] = (byte)(-25);
         var10002[10] = (byte)(-96);
         UNSUPPORTED = new NowPlaying.Status();
         var10002 = new byte[7];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(20);
         var10002[5] = (byte)(-104);
         var10002[6] = (byte)(43);
         WAITING = new NowPlaying.Status();
         var10002 = new byte[11];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(15);
         var10002[5] = (byte)(-125);
         var10002[6] = (byte)(34);
         var10002[7] = (byte)(-128);
         var10002[8] = (byte)(31);
         var10002[9] = (byte)(-20);
         var10002[10] = (byte)(-93);
         NOT_RUNNING = new NowPlaying.Status();
         var10002 = new byte[7];
         var10002[0] = (byte)(110);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-121);
         var10002[4] = (byte)(20);
         var10002[5] = (byte)(-104);
         var10002[6] = (byte)(43);
         PLAYING = new NowPlaying.Status();
         var10002 = new byte[6];
         var10002[0] = (byte)(110);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(73);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(24);
         var10002[5] = (byte)(-110);
         PAUSED = new NowPlaying.Status();
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
}
