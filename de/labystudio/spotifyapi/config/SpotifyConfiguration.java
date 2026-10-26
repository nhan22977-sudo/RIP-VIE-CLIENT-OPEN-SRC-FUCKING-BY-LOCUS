package de.labystudio.spotifyapi.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SpotifyConfiguration {
   private final long exceptionReconnectDelay;
   private final boolean autoReconnect;
   private final Path nativesDirectory;

   private SpotifyConfiguration(long exceptionReconnectDelay, boolean autoReconnect, Path nativesDirectory) {
      this.exceptionReconnectDelay = exceptionReconnectDelay;
      this.autoReconnect = autoReconnect;
      this.nativesDirectory = nativesDirectory;
   }

   public long getExceptionReconnectDelay() {
      return this.exceptionReconnectDelay;
   }

   public boolean isAutoReconnect() {
      return this.autoReconnect;
   }

   public Path getNativesDirectory() {
      return this.nativesDirectory;
   }

   public static class Builder {
      private long exceptionReconnectDelay = 10000L;
      private boolean autoReconnect = true;
      private Path nativesDirectory = Paths.get(System.getProperty("java.io.tmpdir"), "spotify-api-natives");

      public SpotifyConfiguration.Builder exceptionReconnectDelay(long exceptionReconnectDelay) {
         this.exceptionReconnectDelay = exceptionReconnectDelay;
         return this;
      }

      public SpotifyConfiguration.Builder autoReconnect(boolean autoReconnect) {
         this.autoReconnect = autoReconnect;
         return this;
      }

      public SpotifyConfiguration.Builder nativesDirectory(Path nativesDirectory) {
         this.nativesDirectory = nativesDirectory;
         return this;
      }

      public SpotifyConfiguration build() {
         return new SpotifyConfiguration(this.exceptionReconnectDelay, this.autoReconnect, this.nativesDirectory);
      }
   }
}
