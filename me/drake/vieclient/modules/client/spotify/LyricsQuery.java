package me.drake.vieclient.modules.client.spotify;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class LyricsQuery {
   private final String title;
   private final String artist;
   private final String album;
   private final long durationMs;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public LyricsQuery(String title, String artist, String album, long durationMs) {
      this.title = titlex == null ? "" : titlex;
      this.artist = artistx == null ? "" : artistx;
      this.album = albumx == null ? "" : albumx;
      this.durationMs = Math.max(0L, durationMsx);
   }

   public LyricsQuery withDurationMillis(long ms) {
      return new LyricsQuery(this.title, this.artist, this.album, ms);
   }

   public String title() {
      return this.title;
   }

   public String artist() {
      return this.artist;
   }

   public String album() {
      return this.album;
   }

   public long durationMs() {
      return this.durationMs;
   }

   public boolean hasDuration() {
      int var1 = this.durationSeconds();
      return var1 >= 1 && var1 <= (3600);
   }

   public int durationSeconds() {
      return (int)Math.round(this.durationMs / 1000.0);
   }

   public boolean searchable() {
      if (this.title.isBlank()) {
         return false;
      } else {
         return !this.artist.isBlank() ? true : hasSeparator(this.title);
      }
   }

   public static boolean hasSeparator(String s) {
      if (s == null) {
         return false;
      }

      byte[] var10001 = new byte[3];
      var10001[0] = (byte)(29);
      var10001[1] = (byte)(-73);
      var10001[2] = (byte)(60);
      if (!s.contains($$v_s(var10001, 5904672051392925720L))) {
         var10001 = new byte[5];
         var10001[0] = (byte)(29);
         var10001[1] = (byte)(120);
         var10001[2] = (byte)(-100);
         var10001[3] = (byte)(77);
         var10001[4] = (byte)(107);
         if (!s.contains($$v_s(var10001, 5904672051392925720L))) {
            var10001 = new byte[5];
            var10001[0] = (byte)(29);
            var10001[1] = (byte)(120);
            var10001[2] = (byte)(-100);
            var10001[3] = (byte)(74);
            var10001[4] = (byte)(107);
            if (!s.contains($$v_s(var10001, 5904672051392925720L))) {
               var10001 = new byte[5];
               var10001[0] = (byte)(29);
               var10001[1] = (byte)(120);
               var10001[2] = (byte)(-100);
               var10001[3] = (byte)(124);
               var10001[4] = (byte)(107);
               if (!s.contains($$v_s(var10001, 5904672051392925720L))) {
                  var10001 = new byte[3];
                  var10001[0] = (byte)(29);
                  var10001[1] = (byte)(-26);
                  var10001[2] = (byte)(60);
                  if (!s.contains($$v_s(var10001, 5904672051392925720L))) {
                     return false;
                  }
               }
            }
         }
      }

      return true;
   }

   public String cacheKey() {
      return cacheKey(this.title, this.artist, this.album, this.durationSeconds());
   }

   public static String cacheKey(String title, String artist, String album, int durSec) {
      String var4 = normalise(titlex) + "|" + normalise(artistx) + "|" + normalise(albumx);
      return Long.toHexString(fnv1a64(var4));
   }

   @Override
   public boolean equals(Object o) {
      return !(o instanceof LyricsQuery var2)
         ? false
         : this.title.equalsIgnoreCase(var2.title) && this.artist.equalsIgnoreCase(var2.artist) && this.album.equalsIgnoreCase(var2.album);
   }

   @Override
   public int hashCode() {
      Object[] var10000 = new Object[3];
      var10000[0] = this.title.toLowerCase();
      var10000[1] = this.artist.toLowerCase();
      var10000[2] = this.album.toLowerCase();
      return Objects.hash(var10000);
   }

   private static String normalise(String s) {
      if (s == null) {
         return "";
      }

      String var10000 = s.toLowerCase();
      byte[] var10001 = new byte[3];
      var10001[0] = (byte)('\ue1ff' ^ '\ue19e');
      var10001[1] = (byte)(-23);
      var10001[2] = (byte)(55);
      return var10000.replaceAll($$v_s(var10001, 5904672051392925720L), " ").trim();
   }

   private static long fnv1a64(String s) {
      long var1 = -3750763034362895579L;

      for (int var3 = 0; var3 < s.length(); var3++) {
         var1 ^= s.charAt(var3);
         var1 *= 1099511628211L;
      }

      return var1;
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
