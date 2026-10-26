package me.drake.vieclient.modules.client.music;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.function.LongSupplier;

public final class BrowserMusicProvider implements MusicProvider {
   private final WindowScanner scanner;
   private final MusicMetadataService metadataService;
   private final LongSupplier clock;
   private volatile MusicSnapshot current;
   private volatile String requestedIdentity = "";
   private volatile MusicMetadata resolvedMetadata;
   private volatile long detectedAtMs;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BrowserMusicProvider(WindowScanner scanner, MusicMetadataService metadataService) {
      this(scannerx, metadataServicex, System::currentTimeMillis);
   }

   BrowserMusicProvider(WindowScanner scanner, MusicMetadataService metadataService, LongSupplier clock) {
      this.scanner = scannerx;
      this.metadataService = metadataServicex;
      this.clock = clockx;
   }

   @Override
   public String id() {
      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(88);
      var10000[1] = (byte)(-24);
      var10000[2] = (byte)(115);
      var10000[3] = (byte)(-87);
      var10000[4] = (byte)(-1);
      var10000[5] = (byte)(-94);
      var10000[6] = (byte)(91);
      return $$v_s(var10000, -4167490788504907745L);
   }

   @Override
   public Optional poll() {
      for (WindowInfo var2 : this.scanner.scan()) {
         Optional var3 = BrowserTitleParser.parse(var2.processName(), var2.title());
         if (!var3.isEmpty()) {
            BrowserTrackHint var4 = (BrowserTrackHint)var3.get();
            String var5 = var4.source() + ":" + var4.query().toLowerCase(Locale.ROOT);
            long var6 = this.clock.getAsLong();
            boolean var8 = this.current == null || !var5.equals(this.current.identity());
            if (var8) {
               this.detectedAtMs = var6;
               this.resolvedMetadata = null;
            }

            int var9 = (int)Math.min(2147483647L, Math.max(0L, var6 - this.detectedAtMs));
            MusicMetadata var10 = this.resolvedMetadata;
            boolean var11 = true;
            if (var10 != null && var10.durationMs() > 0 && var9 >= var10.durationMs()) {
               var9 = var10.durationMs();
               var11 = false;
            }

            MusicSnapshot var12 = new MusicSnapshot(var4.source(), var4.title(), var4.owner(), 0, var9, var11, "", var5);
            this.current = var10 == null ? var12 : enrich(var12, var10);
            if (!var5.equals(this.requestedIdentity)) {
               this.requestedIdentity = var5;
               this.metadataService.resolve(var4.source(), var4.query()).thenAccept(result -> {
                  if (var5.equals(this.requestedIdentity)) {
                     this.resolvedMetadata = (MusicMetadata)result.orElse(null);
                     MusicMetadata var4x = this.resolvedMetadata;
                     this.current = var4x == null ? var12 : enrich(var12, var4x);
                  }
               });
            }

            return Optional.ofNullable(this.current);
         }
      }

      this.current = null;
      this.requestedIdentity = "";
      this.resolvedMetadata = null;
      this.detectedAtMs = 0L;
      return Optional.empty();
   }

   private static MusicSnapshot enrich(MusicSnapshot provisional, MusicMetadata metadata) {
      int var2 = metadata.durationMs() > 0 ? metadata.durationMs() : provisional.durationMs();
      int var3 = provisional.positionMs();
      boolean var4 = provisional.playing();
      if (var2 > 0 && var3 >= var2) {
         var3 = var2;
         var4 = false;
      }

      return new MusicSnapshot(
         provisional.source(),
         metadata.title().isBlank() ? provisional.title() : metadata.title(),
         metadata.owner().isBlank() ? provisional.owner() : metadata.owner(),
         var2,
         var3,
         var4,
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
