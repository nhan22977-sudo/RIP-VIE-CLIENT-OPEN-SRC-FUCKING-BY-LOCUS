package me.drake.vieclient.modules.client.music;

public record MusicMetadata(String identity, String title, String owner, int durationMs, String artworkUrl) {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MusicMetadata(String identity, String title, String owner, int durationMs, String artworkUrl) {
      identity = identity == null ? "" : identity.trim();
      title = title == null ? "" : title.trim();
      owner = owner == null ? "" : owner.trim();
      durationMs = Math.max(0, durationMs);
      artworkUrl = artworkUrl == null ? "" : artworkUrl.trim();
      this.identity = identity;
      this.title = title;
      this.owner = owner;
      this.durationMs = durationMs;
      this.artworkUrl = artworkUrl;
   }
}
