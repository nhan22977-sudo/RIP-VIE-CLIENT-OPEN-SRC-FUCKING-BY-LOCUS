package me.drake.vieclient.modules.client.music;

import de.labystudio.spotifyapi.SpotifyAPI;
import de.labystudio.spotifyapi.SpotifyAPIFactory;
import de.labystudio.spotifyapi.model.Track;
import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

public final class SpotifyMusicProvider implements MusicProvider {
   private static final String[] SEPARATORS;
   private static final int DEFAULT_AD_DURATION_MS = 30000;
   private final SpotifyAPI api;
   private final WindowScanner scanner;
   private final MusicMetadataService metadataService;
   private final LongSupplier clock;
   private volatile String lastTrackIdentity = "";
   private volatile long trackDetectedAtMs = 0L;
   private volatile String requestedIdentity = "";
   private volatile MusicMetadata cachedMetadata = null;
   private volatile String cachedMetadataIdentity = "";
   private volatile String lastResolvedTrack = "";
   private volatile String lastResolvedArtist = "";
   private volatile int lastResolvedDuration = 0;
   private volatile String lastResolvedArtworkUrl = "";
   private volatile int lastResolvedPosition = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SpotifyMusicProvider() {
      this(SpotifyAPIFactory::create, WindowScanner.system(), null);
   }

   public SpotifyMusicProvider(WindowScanner scanner, MusicMetadataService metadataService) {
      this(SpotifyAPIFactory::create, scannerx, metadataServicex);
   }

   SpotifyMusicProvider(SpotifyAPI api) {
      this(apix, WindowScanner.system(), null);
   }

   SpotifyMusicProvider(Supplier factory) {
      this(factory, WindowScanner.system(), null);
   }

   SpotifyMusicProvider(SpotifyAPI api, WindowScanner scanner, MusicMetadataService metadataService) {
      this(apix, scannerx, metadataServicex, System::currentTimeMillis);
   }

   SpotifyMusicProvider(Supplier factory, WindowScanner scanner, MusicMetadataService metadataService) {
      this(createSafely(factory), scannerx, metadataServicex, System::currentTimeMillis);
   }

   SpotifyMusicProvider(SpotifyAPI api, WindowScanner scanner, MusicMetadataService metadataService, LongSupplier clock) {
      this.api = apix;
      this.scanner = scannerx;
      this.metadataService = metadataServicex;
      this.clock = clockx != null ? clockx : System::currentTimeMillis;
      this.initialize();
   }

   private void initialize() {
      if (this.api != null) {
         try {
            this.api.initializeAsync();
         } catch (Throwable var2) {
         }
      }
   }

   private static SpotifyAPI createSafely(Supplier factory) {
      if (factory == null) {
         return null;
      }

      try {
         return (SpotifyAPI)factory.get();
      } catch (Throwable var2) {
         return null;
      }
   }

   @Override
   public String id() {
      byte[] var10000 = new byte[11];
      var10000[0] = (byte)(86);
      var10000[1] = (byte)(-22);
      var10000[2] = (byte)(115);
      var10000[3] = (byte)(-86);
      var10000[4] = (byte)(27);
      var10000[5] = (byte)(-126);
      var10000[6] = (byte)(-2);
      var10000[7] = (byte)(118);
      var10000[8] = (byte)(44);
      var10000[9] = (byte)(-46);
      var10000[10] = (byte)(-115);
      return $$v_s(var10000, 8323842656089395200L);
   }

   @Override
   public Optional poll() {
      long var1 = this.clock.getAsLong();
      Optional var3 = this.pollNative(var1);
      if (var3.isPresent()) {
         return var3;
      }

      if (this.scanner != null) {
         Optional var4 = this.pollWindow(var1);
         if (var4.isPresent()) {
            return var4;
         }
      }

      this.lastTrackIdentity = "";
      this.trackDetectedAtMs = 0L;
      this.requestedIdentity = "";
      this.cachedMetadata = null;
      this.cachedMetadataIdentity = "";
      this.lastResolvedTrack = "";
      this.lastResolvedArtist = "";
      this.lastResolvedDuration = 0;
      this.lastResolvedArtworkUrl = "";
      this.lastResolvedPosition = 0;
      return Optional.empty();
   }

   private Optional pollNative(long now) {
      if (this.api == null) {
         return Optional.empty();
      }

      try {
         if (!this.api.isConnected()) {
            return Optional.empty();
         }

         Track var3 = null;

         try {
            if (this.api.hasTrack()) {
               var3 = this.api.getTrack();
            }
         } catch (Throwable var13) {
            var3 = null;
         }

         if (var3 == null) {
            return Optional.empty();
         }

         String var4 = var3.getName() != null ? var3.getName() : "";
         String var5 = var3.getArtist() != null ? var3.getArtist() : "";
         if (isAdvertisement(var4, var5)) {
            return Optional.of(this.handleAdvertisement(now, var3.getLength(), this.api.hasPosition() ? this.api.getPosition() : -1));
         }

         String var6 = var3.getId();
         String var7 = var6 != null && !var6.isBlank() ? "spotify:" + var6 : "spotify:" + var4.toLowerCase(Locale.ROOT) + ":" + var5.toLowerCase(Locale.ROOT);
         if (!var7.equals(this.lastTrackIdentity)) {
            this.lastTrackIdentity = var7;
            this.trackDetectedAtMs = now;
            this.cachedMetadata = null;
            this.cachedMetadataIdentity = "";
            this.requestedIdentity = "";
         }

         int var8;
         if (this.api.hasPosition()) {
            var8 = this.api.getPosition();
            this.trackDetectedAtMs = now - var8;
         } else if (this.api.isPlaying()) {
            long var9 = Math.max(0L, now - this.trackDetectedAtMs);
            int var11 = var3.getLength();
            if (var11 > 0 && var9 > var11) {
               var9 %= var11;
            }

            var8 = (int)Math.min(2147483647L, var9);
         } else {
            var8 = 0;
         }

         int var15 = var3.getLength();
         BufferedImage var10 = var3.getCoverArt();
         String var16 = "";
         if (this.metadataService != null && (var15 <= 0 || var10 == null)) {
            String var12 = !var5.isBlank() && !var5.equalsIgnoreCase("Spotify")
               ? var5 + " - " + var4
               : var4;
            this.requestMetadata(var7, var12);
            if (this.cachedMetadata != null && var7.equals(this.cachedMetadataIdentity)) {
               if (var15 <= 0 && this.cachedMetadata.durationMs() > 0) {
                  var15 = this.cachedMetadata.durationMs();
               }

               if (var10 == null && !this.cachedMetadata.artworkUrl().isBlank()) {
                  var16 = this.cachedMetadata.artworkUrl();
               }
            }
         }

         return Optional.of(
            new MusicSnapshot(
               MusicSource.SPOTIFY_API,
               var4.isBlank() ? "Unknown Track" : var4,
               var5.isBlank() ? "Spotify" : var5,
               var15,
               var8,
               this.api.isPlaying(),
               var16,
               var7,
               var10
            )
         );
      } catch (Throwable var14) {
         return Optional.empty();
      }
   }

   private Optional pollWindow(long now) {
      try {
         List var3 = this.scanner.scan();
         if (var3 == null || var3.isEmpty()) {
            return Optional.empty();
         }

         boolean var4 = false;
         WindowInfo var5 = null;

         for (WindowInfo var7 : var3) {
            String var8 = normalizeProcess(var7.processName());
            if (var8.contains("spotify")) {
               var4 = true;
               String var9 = var7.title();
               if (var9 != null && !var9.isBlank()) {
                  String var10 = var9.trim();
                  if (!isIdleTitle(var10)) {
                     if (isAdvertisement(var10, null)) {
                        return Optional.of(this.handleAdvertisement(now, 30000, -1));
                     }

                     int var11 = -1;
                     int var12 = 0;

                     for (String var16 : SEPARATORS) {
                        int var17 = var10.indexOf(var16);
                        if (var17 > 0 && var17 < var10.length() - var16.length()) {
                           var11 = var17;
                           var12 = var16.length();
                           break;
                        }
                     }

                     String var25;
                     String var26;
                     if (var11 > 0) {
                        var25 = var10.substring(0, var11).trim();
                        var26 = var10.substring(var11 + var12).trim();
                     } else {
                        var25 = "Spotify";
                        var26 = var10;
                     }

                     if (isAdvertisement(var26, var25)) {
                        return Optional.of(this.handleAdvertisement(now, 30000, -1));
                     }

                     String var27 = var25.equalsIgnoreCase("Spotify")
                        ? var26
                        : var25 + " - " + var26;
                     String var28 = "spotify-win:" + var26.toLowerCase(Locale.ROOT) + ":" + var25.toLowerCase(Locale.ROOT);
                     if (!var28.equals(this.lastTrackIdentity)) {
                        this.lastTrackIdentity = var28;
                        this.trackDetectedAtMs = now;
                        this.cachedMetadata = null;
                        this.cachedMetadataIdentity = "";
                        this.requestedIdentity = "";
                     }

                     if (this.metadataService != null) {
                        this.requestMetadata(var28, var27);
                     }

                     int var29 = 0;
                     String var18 = "";
                     String var19 = var26;
                     String var20 = var25;
                     if (this.cachedMetadata != null && var28.equals(this.cachedMetadataIdentity)) {
                        var29 = this.cachedMetadata.durationMs();
                        var18 = this.cachedMetadata.artworkUrl();
                        if (!this.cachedMetadata.title().isBlank()) {
                           var19 = this.cachedMetadata.title();
                        }

                        if (!this.cachedMetadata.owner().isBlank()) {
                           var20 = this.cachedMetadata.owner();
                        }
                     }

                     long var21 = Math.max(0L, now - this.trackDetectedAtMs);
                     if (var29 > 0 && var21 > var29) {
                        var21 %= var29;
                     }

                     int var23 = (int)Math.min(2147483647L, var21);
                     this.lastResolvedTrack = var19;
                     this.lastResolvedArtist = var20;
                     this.lastResolvedDuration = var29;
                     this.lastResolvedArtworkUrl = var18;
                     this.lastResolvedPosition = var23;
                     return Optional.of(new MusicSnapshot(MusicSource.SPOTIFY_API, var19, var20, var29, var23, true, var18, var28, null));
                  }

                  var5 = var7;
               }
            }
         }

         if (var4 && var5 != null && !this.lastResolvedTrack.isBlank() && !this.lastTrackIdentity.isBlank()) {
            return Optional.of(
               new MusicSnapshot(
                  MusicSource.SPOTIFY_API,
                  this.lastResolvedTrack,
                  this.lastResolvedArtist,
                  this.lastResolvedDuration,
                  this.lastResolvedPosition,
                  false,
                  this.lastResolvedArtworkUrl,
                  this.lastTrackIdentity,
                  null
               )
            );
         }
      } catch (Throwable var24) {
      }

      return Optional.empty();
   }

   private MusicSnapshot handleAdvertisement(long now, int durationMs, int rawPositionMs) {
      int var5 = durationMs > 0 ? durationMs : 30000;
      byte[] var10000 = new byte[10];
      var10000[0] = (byte)(86);
      var10000[1] = (byte)(-22);
      var10000[2] = (byte)(115);
      var10000[3] = (byte)(-86);
      var10000[4] = (byte)(27);
      var10000[5] = (byte)(-126);
      var10000[6] = (byte)(-2);
      var10000[7] = (byte)(97);
      var10000[8] = (byte)(44);
      var10000[9] = (byte)(-58);
      String var6 = $$v_s(var10000, 8323842656089395200L);
      if (!var6.equals(this.lastTrackIdentity)) {
         this.lastTrackIdentity = var6;
         this.trackDetectedAtMs = now;
         this.cachedMetadata = null;
         this.cachedMetadataIdentity = "";
         this.requestedIdentity = "";
      }

      int var7;
      if (rawPositionMs >= 0) {
         var7 = rawPositionMs;
         this.trackDetectedAtMs = now - var7;
      } else {
         long var8 = Math.max(0L, now - this.trackDetectedAtMs);
         if (var8 > var5) {
            var8 %= var5;
         }

         var7 = (int)Math.min(2147483647L, var8);
      }

      MusicSource var10002 = MusicSource.SPOTIFY_API;
      byte[] var10003 = new byte[13];
      var10003[0] = (byte)(100);
      var10003[1] = (byte)(-2);
      var10003[2] = (byte)(106);
      var10003[3] = (byte)(-69);
      var10003[4] = 0;
      var10003[5] = (byte)(-112);
      var10003[6] = (byte)(-18);
      var10003[7] = (byte)(40);
      var10003[8] = (byte)(40);
      var10003[9] = (byte)(-49);
      var10003[10] = (byte)(-127);
      var10003[11] = (byte)(-104);
      var10003[12] = (byte)(94);
      String var10 = $$v_s(var10003, 8323842656089395200L);
      byte[] var10004 = new byte[7];
      var10004[0] = (byte)(118);
      var10004[1] = (byte)(-22);
      var10004[2] = (byte)(115);
      var10004[3] = (byte)(-86);
      var10004[4] = (byte)(27);
      var10004[5] = (byte)(-126);
      var10004[6] = (byte)(-2);
      return new MusicSnapshot(var10002, var10, $$v_s(var10004, 8323842656089395200L), var5, var7, true, "", var6, null);
   }

   private void requestMetadata(String identity, String query) {
      if (this.metadataService != null && !identity.equals(this.requestedIdentity)) {
         this.requestedIdentity = identity;
         this.metadataService.resolve(MusicSource.SPOTIFY_API, query).thenAccept(result -> {
            if (identity.equals(this.requestedIdentity)) {
               this.cachedMetadata = (MusicMetadata)result.orElse(null);
               this.cachedMetadataIdentity = identity;
            }
         });
      }
   }

   public static boolean isAdvertisement(String title, String artist) {
      if (title != null) {
         String var2 = title.toLowerCase(Locale.ROOT).trim();
         if (var2.equals("advertisement")
            || var2.startsWith("advertisement")
            || var2.contains("advertisement")
            || var2.equals("quảng cáo")
            || var2.startsWith("quảng cáo")
            || var2.contains("quảng cáo")
            || var2.contains("spotify ad")) {
            return true;
         }
      }

      if (artist != null) {
         String var3 = artist.toLowerCase(Locale.ROOT).trim();
         if (var3.equals("advertisement")
            || var3.startsWith("advertisement")
            || var3.contains("advertisement")
            || var3.equals("quảng cáo")
            || var3.startsWith("quảng cáo")
            || var3.contains("quảng cáo")) {
            return true;
         }
      }

      return false;
   }

   public static boolean isIdleTitle(String title) {
      if (title != null && !title.isBlank()) {
         String var1 = title.toLowerCase(Locale.ROOT).trim();
         byte[] var10001 = new byte[7];
         var10001[0] = (byte)(86);
         var10001[1] = (byte)(-22);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(27);
         var10001[5] = (byte)(-126);
         var10001[6] = (byte)(-2);
         if (!var1.equals($$v_s(var10001, 8323842656089395200L))) {
            var10001 = new byte[12];
            var10001[0] = (byte)(86);
            var10001[1] = (byte)(-22);
            var10001[2] = (byte)(115);
            var10001[3] = (byte)(-86);
            var10001[4] = (byte)(27);
            var10001[5] = (byte)(-126);
            var10001[6] = (byte)(-2);
            var10001[7] = (byte)(123);
            var10001[8] = (byte)(43);
            var10001[9] = (byte)(-48);
            var10001[10] = (byte)(-127);
            var10001[11] = (byte)(-109);
            if (!var1.equals($$v_s(var10001, 8323842656089395200L))) {
               var10001 = new byte[15];
               var10001[0] = (byte)(86);
               var10001[1] = (byte)(-22);
               var10001[2] = (byte)(115);
               var10001[3] = (byte)(-86);
               var10001[4] = (byte)(27);
               var10001[5] = (byte)(-126);
               var10001[6] = (byte)(-2);
               var10001[7] = (byte)(123);
               var10001[8] = (byte)(61);
               var10001[9] = (byte)(-48);
               var10001[10] = (byte)(-127);
               var10001[11] = (byte)(-101);
               var10001[12] = (byte)(67);
               var10001[13] = (byte)(73);
               var10001[14] = (byte)(-62);
               if (!var1.equals($$v_s(var10001, 8323842656089395200L))) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }

   private static String normalizeProcess(String processName) {
      if (processName == null) {
         return "";
      }

      String var1 = processName.trim().toLowerCase(Locale.ROOT);
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(11);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(100);
      var10001[3] = (byte)(-69);
      if (var1.endsWith($$v_s(var10001, 8323842656089395200L))) {
         var1 = var1.substring(0, var1.length() - (4));
      }

      return var1;
   }

   @Override
   public void close() {
      this.lastTrackIdentity = "";
      this.trackDetectedAtMs = 0L;
      this.requestedIdentity = "";
      this.cachedMetadata = null;
      this.cachedMetadataIdentity = "";
      this.lastResolvedTrack = "";
      this.lastResolvedArtist = "";
      this.lastResolvedDuration = 0;
      this.lastResolvedArtworkUrl = "";
      this.lastResolvedPosition = 0;
      if (this.api != null) {
         try {
            this.api.shutdown();
         } catch (Throwable var2) {
         }
      }
   }

   static {
      String[] var10000 = new String[3];
      byte[] var10003 = new byte[3];
      var10003[0] = (byte)(5);
      var10003[1] = (byte)(-73);
      var10003[2] = (byte)(60);
      var10000[0] = $$v_s(var10003, 8323842656089395200L);
      var10003 = new byte[5];
      var10003[0] = (byte)(5);
      var10003[1] = (byte)(120);
      var10003[2] = (byte)(-100);
      var10003[3] = (byte)(74);
      var10003[4] = (byte)(82);
      var10000[1] = $$v_s(var10003, 8323842656089395200L);
      int var10002 = 2;
      var10003 = new byte[5];
      var10003[0] = (byte)(5);
      var10003[1] = (byte)(120);
      var10003[2] = (byte)(-100);
      var10003[3] = (byte)(77);
      var10003[4] = (byte)(82);
      var10000[var10002] = $$v_s(var10003, 8323842656089395200L);
      SEPARATORS = var10000;
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
