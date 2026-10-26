package me.drake.vieclient.modules.client.music;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.LongSupplier;

public final class SoundCloudAppProvider implements MusicProvider {
   private final MediaSessionBridge bridge;
   private final MusicMetadataService metadataService;
   private final LongSupplier clock;
   private final AtomicBoolean closed = new AtomicBoolean();
   private volatile String requestedIdentity = "";
   private volatile MusicMetadata resolvedMetadata;
   private volatile MusicMetadata cachedMetadata = null;
   private volatile String cachedMetadataIdentity = "";
   private volatile String resolvedMetadataIdentity = "";
   private volatile String timelineIdentity = "";
   private volatile long estimatedPositionMs = 0L;
   private volatile int lastReportedPositionMs = 0;
   private volatile int lastKnownDurationMs = 0;
   private volatile String lastKnownDurationIdentity = "";
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SoundCloudAppProvider(MediaSessionBridge bridge) {
      this(bridgex, null);
   }

   public SoundCloudAppProvider(MediaSessionBridge bridge, MusicMetadataService metadataService) {
      this(bridgex, metadataServicex, System::currentTimeMillis);
   }

   SoundCloudAppProvider(MediaSessionBridge bridge, MusicMetadataService metadataService, LongSupplier clock) {
      this.bridge = bridgex;
      this.metadataService = metadataServicex;
      this.clock = clockx;
   }

   @Override
   public String id() {
      byte[] var10000 = new byte[14];
      var10000[0] = (byte)(74);
      var10000[1] = (byte)(-11);
      var10000[2] = (byte)(105);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(-48);
      var10000[5] = (byte)(-3);
      var10000[6] = (byte)(61);
      var10000[7] = (byte)(93);
      var10000[8] = (byte)(36);
      var10000[9] = (byte)(-58);
      var10000[10] = (byte)(-55);
      var10000[11] = (byte)(-105);
      var10000[12] = (byte)(-100);
      var10000[13] = (byte)(54);
      return $$v_s(var10000, 1896648818904256540L);
   }

   @Override
   public Optional poll() {
      if (this.closed.get()) {
         return Optional.empty();
      }

      Optional var1 = this.bridge.latest().filter(WindowsMediaSessionSnapshot::isSoundCloudApp).filter(snapshot -> !snapshot.title().isBlank());
      if (var1.isEmpty()) {
         this.requestedIdentity = "";
         this.resolvedMetadata = null;
         this.cachedMetadata = null;
         this.cachedMetadataIdentity = "";
         this.resolvedMetadataIdentity = "";
         this.lastKnownDurationMs = 0;
         this.lastKnownDurationIdentity = "";
         this.resetTimeline();
         return Optional.empty();
      }

      WindowsMediaSessionSnapshot var2 = (WindowsMediaSessionSnapshot)var1.get();
      String var3 = identity(var2);
      int var4 = this.resolveDuration(var2, var3);
      boolean var5 = var2.playing();
      int var6 = var5 ? this.position(var2, var3) : Math.min(var4 > 0 ? var4 : 2147483647, var2.rawPositionMs());
      MusicSnapshot var7 = new MusicSnapshot(MusicSource.SOUNDCLOUD_APP, var2.title(), var2.artist(), var4, var6, var5, "", var3, var2.artwork());
      this.requestMetadata(var7);
      MusicMetadata var8 = this.resolvedMetadata;
      if (var8 != null) {
         this.cachedMetadata = var8;
         this.cachedMetadataIdentity = this.resolvedMetadataIdentity;
         if (var4 <= 0 && var8.durationMs() > 0 && var3.equals(this.resolvedMetadataIdentity)) {
            var4 = var8.durationMs();
            this.lastKnownDurationMs = var4;
            this.lastKnownDurationIdentity = var3;
         }
      }

      return Optional.of(var8 == null ? var7 : enrich(var7, var8));
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
         this.resetTimeline();
         this.bridge.close();
      }
   }

   private void requestMetadata(MusicSnapshot provisional) {
      if (this.metadataService != null) {
         String var2 = provisional.identity();
         if (!var2.equals(this.requestedIdentity)) {
            this.requestedIdentity = var2;
            this.resolvedMetadata = null;
            String var10000 = provisional.owner();
            byte[] var10001 = new byte[14];
            var10001[0] = (byte)(108);
            var10001[1] = (byte)(-12);
            var10001[2] = (byte)(119);
            var10001[3] = (byte)(-80);
            var10001[4] = (byte)(-37);
            var10001[5] = (byte)(-23);
            var10001[6] = (byte)(63);
            var10001[7] = (byte)(18);
            var10001[8] = (byte)(16);
            var10001[9] = (byte)(-48);
            var10001[10] = (byte)(-112);
            var10001[11] = (byte)(-97);
            var10001[12] = (byte)(-97);
            var10001[13] = (byte)(50);
            String var3 = var10000.equals($$v_s(var10001, 1896648818904256540L)) ? provisional.title() : provisional.title() + " " + provisional.owner();
            this.metadataService.resolve(MusicSource.SOUNDCLOUD_APP, var3).thenAccept(result -> {
               if (!this.closed.get() && var2.equals(this.requestedIdentity)) {
                  this.resolvedMetadata = (MusicMetadata)result.orElse(null);
                  this.resolvedMetadataIdentity = var2;
               }
            });
         }
      }
   }

   private static MusicSnapshot enrich(MusicSnapshot provisional, MusicMetadata metadata) {
      int var2 = provisional.durationMs() > (1000)
         ? provisional.durationMs()
         : (metadata.durationMs() > 0 ? metadata.durationMs() : provisional.durationMs());
      return new MusicSnapshot(
         provisional.source(),
         provisional.title(),
         provisional.owner(),
         var2,
         provisional.positionMs(),
         provisional.playing(),
         metadata.artworkUrl(),
         provisional.identity(),
         provisional.artwork()
      );
   }

   private int resolveDuration(WindowsMediaSessionSnapshot session, String identity) {
      if (session.durationMs() > (1000)) {
         this.lastKnownDurationMs = session.durationMs();
         this.lastKnownDurationIdentity = identity;
         return session.durationMs();
      } else if (this.cachedMetadata != null && identity.equals(this.cachedMetadataIdentity) && this.cachedMetadata.durationMs() > 0) {
         this.lastKnownDurationMs = this.cachedMetadata.durationMs();
         this.lastKnownDurationIdentity = identity;
         return this.cachedMetadata.durationMs();
      } else {
         return identity.equals(this.lastKnownDurationIdentity) && this.lastKnownDurationMs > 0 ? this.lastKnownDurationMs : 0;
      }
   }

   private static String identity(WindowsMediaSessionSnapshot snapshot) {
      return "soundcloud-app:"
         + snapshot.sourceApp().toLowerCase(Locale.ROOT)
         + ":"
         + snapshot.title().toLowerCase(Locale.ROOT)
         + ":"
         + snapshot.artist().toLowerCase(Locale.ROOT);
   }

   private int position(WindowsMediaSessionSnapshot session, String identity) {
      long var3 = session.positionMs();
      long var5 = this.lastKnownDurationMs > 0 && identity.equals(this.lastKnownDurationIdentity) ? this.lastKnownDurationMs : 2147483647L;
      long var7;
      if (!identity.equals(this.timelineIdentity)) {
         var7 = var3;
      } else if (var3 + 500L >= this.lastReportedPositionMs && var3 <= var5) {
         var7 = var3;
      } else {
         var7 = this.lastReportedPositionMs;
      }

      int var9 = (int)Math.min(2147483647L, Math.max(0L, var7));
      this.timelineIdentity = identity;
      this.lastReportedPositionMs = var9;
      this.estimatedPositionMs = var9;
      return (int)Math.max(0L, Math.min(2147483647L, this.estimatedPositionMs));
   }

   private void resetTimeline() {
      this.timelineIdentity = "";
      this.estimatedPositionMs = 0L;
      this.lastReportedPositionMs = 0;
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
