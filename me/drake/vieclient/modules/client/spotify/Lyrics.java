package me.drake.vieclient.modules.client.spotify;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public record Lyrics(Lyrics.Status status, SyncedLyrics synced, List<String> plainLines, long sourceId) {
   public static final Lyrics LOADING = new Lyrics(Lyrics.Status.LOADING, SyncedLyrics.EMPTY, List.of(), 0L);
   public static final Lyrics NOT_FOUND = new Lyrics(Lyrics.Status.NOT_FOUND, SyncedLyrics.EMPTY, List.of(), 0L);
   public static final Lyrics UNAVAILABLE = new Lyrics(Lyrics.Status.UNAVAILABLE, SyncedLyrics.EMPTY, List.of(), 0L);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Lyrics(Lyrics.Status status, SyncedLyrics synced, List plainLines, long sourceId) {
      if (status == null) {
         status = Lyrics.Status.NOT_FOUND;
      }

      if (synced == null) {
         synced = SyncedLyrics.EMPTY;
      }

      plainLines = plainLines == null ? List.of() : List.copyOf(plainLines);
      this.status = status;
      this.synced = synced;
      this.plainLines = plainLines;
      this.sourceId = sourceId;
   }

   public boolean isSynced() {
      return this.status == Lyrics.Status.SYNCED && !this.synced.isEmpty();
   }

   public boolean isPlain() {
      return this.status == Lyrics.Status.PLAIN;
   }

   public boolean isLoading() {
      return this.status == Lyrics.Status.LOADING;
   }

   public boolean isMissing() {
      return this.status == Lyrics.Status.NOT_FOUND || this.status == Lyrics.Status.UNAVAILABLE;
   }

   public boolean isInstrumental() {
      return this.status == Lyrics.Status.INSTRUMENTAL;
   }

   public static Lyrics of(LrclibTrack track) {
      if (track == null) {
         return NOT_FOUND;
      }

      SyncedLyrics var1 = SyncedLyrics.parse(track.syncedLyrics());
      List var2 = plainLines(track.plainLyrics());
      if (var2.isEmpty() && var1.isEmpty()) {
         var2 = plainLines(track.syncedLyrics());
      }

      if (!var1.isEmpty() && !var1.texts().isEmpty()) {
         return new Lyrics(Lyrics.Status.SYNCED, var1, var2.isEmpty() ? var1.texts() : var2, track.id());
      } else if (!var2.isEmpty()) {
         SyncedLyrics var3 = SyncedLyrics.fromPlain(var2, (long)(track.duration() * 1000.0));
         return new Lyrics(Lyrics.Status.SYNCED, var3, var2, track.id());
      } else {
         return !track.instrumental() && var1.size() <= 0 ? NOT_FOUND : new Lyrics(Lyrics.Status.INSTRUMENTAL, SyncedLyrics.EMPTY, List.of(), track.id());
      }
   }

   static List plainLines(String text) {
      if (text != null && !text.isBlank()) {
         ArrayList var1 = new ArrayList();
         boolean var2 = false;
         byte[] var10001 = new byte[8];
         var10001[0] = (byte)(102);
         var10001[1] = (byte)(-24);
         var10001[2] = (byte)(35);
         var10001[3] = (byte)(-126);
         var10001[4] = (byte)(49);
         var10001[5] = (byte)(-118);
         var10001[6] = (byte)(-57);
         var10001[7] = (byte)(-78);

         for (String var6 : text.split($$v_s(var10001, -1686553090893557729L))) {
            String var7 = var6.strip();
            if (var7.isEmpty()) {
               var2 = !var1.isEmpty();
            } else {
               if (var2) {
                  var1.add("");
               }

               var2 = false;
               var1.add(var7);
            }
         }

         return List.copyOf(var1);
      } else {
         return List.of();
      }
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

   public enum Status {
      SYNCED,
      PLAIN,
      INSTRUMENTAL,
      LOADING,
      NOT_FOUND,
      UNAVAILABLE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static Lyrics.Status[] $values() {
         Lyrics.Status[] var10000 = new Lyrics.Status[6];
         var10000[0] = SYNCED;
         var10000[1] = PLAIN;
         var10000[2] = INSTRUMENTAL;
         var10000[3] = LOADING;
         var10000[4] = NOT_FOUND;
         var10000[5] = UNAVAILABLE;
         return var10000;
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
