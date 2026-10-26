package me.drake.vieclient.modules.client.music;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongSupplier;

public final class YouTubeBrowserProvider implements MusicProvider {
   private final MediaSessionBridge bridge;
   private final WindowScanner scanner;
   private final MusicMetadataService metadataService;
   private final BrowserMusicProvider fallback;
   private final LongSupplier clock;
   private final AtomicBoolean closed = new AtomicBoolean();
   private volatile String requestedIdentity = "";
   private volatile MusicMetadata resolvedMetadata;
   private volatile MusicMetadata cachedMetadata = null;
   private volatile String cachedMetadataIdentity = "";
   private volatile String resolvedMetadataIdentity = "";
   private volatile int lastKnownDurationMs = 0;
   private volatile String lastKnownDurationIdentity = "";
   private volatile String timelineIdentity = "";
   private volatile long timelineDetectedAtMs = 0L;
   private volatile int lastPausedPositionMs = 0;
   private volatile boolean wasPlayingBefore = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public YouTubeBrowserProvider(MediaSessionBridge bridge, WindowScanner scanner, MusicMetadataService metadataService) {
      this(bridgex, scannerx, metadataServicex, System::currentTimeMillis);
   }

   YouTubeBrowserProvider(MediaSessionBridge bridge, WindowScanner scanner, MusicMetadataService metadataService, LongSupplier clock) {
      this.bridge = bridgex;
      this.scanner = scannerx;
      this.metadataService = metadataServicex;
      this.fallback = new BrowserMusicProvider(scannerx, metadataServicex);
      this.clock = clockx;
   }

   @Override
   public String id() {
      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(92);
      var10000[1] = (byte)(-24);
      var10000[2] = (byte)(115);
      var10000[3] = (byte)(-87);
      var10000[4] = (byte)(39);
      var10000[5] = (byte)(-17);
      var10000[6] = (byte)(42);
      return $$v_s(var10000, -6603591050475614181L);
   }

   @Override
   public Optional poll() {
      if (this.closed.get()) {
         return Optional.empty();
      }

      List var1 = this.browserHints();
      Optional var2 = this.bridge.latest().filter(WindowsMediaSessionSnapshot::isSupportedBrowser).filter(candidate -> !candidate.title().isBlank());
      Optional var3 = var2.filter(candidate -> var1.isEmpty() || var1.stream().anyMatch(hint -> YouTubeSessionMatcher.matches(hint, candidate)));
      MusicMetadata var4 = this.resolvedMetadata;
      if (var4 != null) {
         this.cachedMetadata = var4;
         this.cachedMetadataIdentity = this.resolvedMetadataIdentity;
         if (var4.durationMs() > 0) {
            this.lastKnownDurationMs = var4.durationMs();
            this.lastKnownDurationIdentity = this.resolvedMetadataIdentity;
         }
      }

      if (var3.isEmpty()) {
         this.requestedIdentity = "";
         this.resolvedMetadata = null;
         this.cachedMetadata = null;
         this.cachedMetadataIdentity = "";
         this.resolvedMetadataIdentity = "";
         this.lastKnownDurationMs = 0;
         this.lastKnownDurationIdentity = "";
         this.timelineIdentity = "";
         this.timelineDetectedAtMs = 0L;
         this.lastPausedPositionMs = 0;
         this.wasPlayingBefore = false;
         return this.fallback.poll();
      }

      WindowsMediaSessionSnapshot var5 = (WindowsMediaSessionSnapshot)var3.get();
      Optional var6 = var1.stream().filter(candidate -> YouTubeSessionMatcher.matches(candidate, var5)).findFirst();
      MusicSource var7 = var6.map(BrowserTrackHint::source).orElse(MusicSource.YOUTUBE_BROWSER);
      String var8 = var6.map(BrowserTrackHint::owner).filter(o -> !o.isBlank()).orElseGet(() -> {
         String var2x;
         if (var5.artist().isBlank()) {
            if (var7 == MusicSource.SPOTIFY_API) {
               byte[] var10000 = new byte[7];
               var10000[0] = (byte)(109);
               var10000[1] = (byte)(-22);
               var10000[2] = (byte)(115);
               var10000[3] = (byte)(-86);
               var10000[4] = (byte)(61);
               var10000[5] = (byte)(-20);
               var10000[6] = (byte)(33);
               var2x = $$v_s(var10000, -6603591050475614181L);
            } else {
               byte[] var3x = new byte[7];
               var3x[0] = (byte)(124);
               var3x[1] = (byte)(-24);
               var3x[2] = (byte)(115);
               var3x[3] = (byte)(-87);
               var3x[4] = (byte)(39);
               var3x[5] = (byte)(-17);
               var3x[6] = (byte)(42);
               var2x = $$v_s(var3x, -6603591050475614181L);
            }
         } else {
            var2x = var5.artist();
         }

         return var2x;
      });
      String var9 = var6.map(BrowserTrackHint::query).orElse(var5.title());
      String var10 = (
            var7 == MusicSource.SPOTIFY_API
               ? "spotify-browser:"
               : "youtube-browser:"
         )
         + var9.toLowerCase(Locale.ROOT);
      int var11 = this.resolveDuration(var5, var10);
      boolean var12 = var5.playing();
      int var13;
      if (var12) {
         var13 = this.currentPosition(var5, var10, var11);
         if (var11 > 0 && var13 >= var11) {
            var13 = var11;
            var12 = false;
         }

         this.lastPausedPositionMs = var13;
         this.wasPlayingBefore = var12;
      } else {
         if (var5.positionMs() > 0) {
            var13 = var5.positionMs();
         } else if (var10.equals(this.timelineIdentity) && this.lastPausedPositionMs > 0) {
            var13 = this.lastPausedPositionMs;
         } else if (var10.equals(this.timelineIdentity) && this.timelineDetectedAtMs > 0L) {
            long var14 = Math.max(0L, this.clock.getAsLong() - this.timelineDetectedAtMs);
            var13 = (int)Math.min(var11 > 0 ? var11 : 2147483647L, var14);
         } else {
            var13 = Math.min(var11 > 0 ? var11 : Integer.MAX_VALUE, var5.rawPositionMs());
         }

         if (var11 > 0 && var13 >= var11) {
            var13 = var11;
         }

         this.lastPausedPositionMs = var13;
         this.wasPlayingBefore = false;
      }

      MusicSnapshot var16 = new MusicSnapshot(var7, var5.title(), var8, var11, var13, var12, "", var10, var5.artwork());
      this.requestMetadata(var7, var10, var9);
      MusicMetadata var15 = this.resolvedMetadata;
      if (var15 != null) {
         this.cachedMetadata = var15;
         this.cachedMetadataIdentity = this.resolvedMetadataIdentity;
         if (var15.durationMs() > 0) {
            this.lastKnownDurationMs = var15.durationMs();
            this.lastKnownDurationIdentity = var10;
         }
      }

      return Optional.of(var15 == null ? var16 : enrich(var16, var15));
   }

   private int currentPosition(WindowsMediaSessionSnapshot session, String identity, int resolvedDurationMs) {
      long var4 = session.positionMs();
      if (var4 > 0L) {
         this.timelineIdentity = identity;
         this.timelineDetectedAtMs = this.clock.getAsLong() - var4;
         this.lastPausedPositionMs = (int)var4;
      } else {
         if (!identity.equals(this.timelineIdentity) || this.timelineDetectedAtMs == 0L) {
            this.timelineIdentity = identity;
            this.timelineDetectedAtMs = this.clock.getAsLong() - this.lastPausedPositionMs;
         } else if (!this.wasPlayingBefore && this.lastPausedPositionMs > 0) {
            this.timelineDetectedAtMs = this.clock.getAsLong() - this.lastPausedPositionMs;
         }

         long var6 = Math.max(0L, this.clock.getAsLong() - this.timelineDetectedAtMs);
         if (resolvedDurationMs > 0 && var6 >= resolvedDurationMs) {
            var6 = resolvedDurationMs;
         }

         var4 = var6;
      }

      if (resolvedDurationMs > 0) {
         var4 = Math.min(var4, resolvedDurationMs);
      }

      return (int)Math.max(0L, Math.min(2147483647L, var4));
   }

   private int resolveDuration(WindowsMediaSessionSnapshot session, String identity) {
      if (session.durationMs() > 0) {
         this.lastKnownDurationMs = session.durationMs();
         this.lastKnownDurationIdentity = identity;
         return session.durationMs();
      }

      if (identity.equals(this.lastKnownDurationIdentity) && this.lastKnownDurationMs > 0) {
         return this.lastKnownDurationMs;
      }

      if (this.cachedMetadata == null
         || this.cachedMetadata.durationMs() <= 0
         || !identity.equals(this.cachedMetadataIdentity) && !isMatchingTrack(identity, this.cachedMetadataIdentity, this.cachedMetadata)) {
         return 0;
      }

      this.lastKnownDurationMs = this.cachedMetadata.durationMs();
      this.lastKnownDurationIdentity = identity;
      return this.cachedMetadata.durationMs();
   }

   private static boolean isMatchingTrack(String currentIdentity, String cachedIdentity, MusicMetadata cached) {
      if (currentIdentity != null && cached != null) {
         String var3 = currentIdentity.toLowerCase(Locale.ROOT);
         String var4 = cached.title().toLowerCase(Locale.ROOT);
         if (!var4.isBlank() && var3.contains(var4)) {
            return true;
         }

         if (cachedIdentity != null && !cachedIdentity.isBlank()) {
            String var5 = cachedIdentity.toLowerCase(Locale.ROOT);
            if (var3.contains(var5) || var5.contains(var3)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public void close() {
      if (this.closed.compareAndSet(false, true)) {
         this.requestedIdentity = "";
         this.resolvedMetadata = null;
         this.cachedMetadata = null;
         this.cachedMetadataIdentity = "";
         this.resolvedMetadataIdentity = "";
         this.lastKnownDurationMs = 0;
         this.lastKnownDurationIdentity = "";
         this.timelineIdentity = "";
         this.timelineDetectedAtMs = 0L;
         this.lastPausedPositionMs = 0;
         this.wasPlayingBefore = false;
         this.bridge.close();
         this.fallback.close();
      }
   }

   private List browserHints() {
      ArrayList var1 = new ArrayList();

      for (WindowInfo var3 : this.scanner.scan()) {
         BrowserTitleParser.parse(var3.processName(), var3.title()).ifPresent(var1::add);
      }

      return var1;
   }

   private void requestMetadata(MusicSource source, String identity, String query) {
      if (!identity.equals(this.requestedIdentity)) {
         this.requestedIdentity = identity;
         this.resolvedMetadata = null;
         this.metadataService.resolve(source, query).thenAccept(result -> {
            if (!this.closed.get() && identity.equals(this.requestedIdentity)) {
               this.resolvedMetadata = (MusicMetadata)result.orElse(null);
               this.resolvedMetadataIdentity = identity;
            }
         });
      }
   }

   private static MusicSnapshot enrich(MusicSnapshot provisional, MusicMetadata metadata) {
      int var2 = provisional.durationMs() > 0 ? provisional.durationMs() : (metadata.durationMs() > 0 ? metadata.durationMs() : provisional.durationMs());
      return new MusicSnapshot(
         provisional.source(),
         metadata.title().isBlank() ? provisional.title() : metadata.title(),
         metadata.owner().isBlank() ? provisional.owner() : metadata.owner(),
         var2,
         provisional.positionMs(),
         provisional.playing(),
         metadata.artworkUrl().isBlank() ? provisional.artworkUrl() : metadata.artworkUrl(),
         provisional.identity(),
         provisional.artwork()
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
}
