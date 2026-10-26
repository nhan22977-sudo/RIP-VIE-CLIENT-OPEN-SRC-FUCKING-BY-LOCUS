package de.labystudio.spotifyapi;

import de.labystudio.spotifyapi.config.SpotifyConfiguration;
import de.labystudio.spotifyapi.model.MediaKey;
import de.labystudio.spotifyapi.model.Track;
import java.util.concurrent.CompletableFuture;

public interface SpotifyAPI {
   default SpotifyAPI initialize() {
      return this.initialize(new SpotifyConfiguration.Builder().build());
   }

   SpotifyAPI initialize(SpotifyConfiguration var1);

   default CompletableFuture<SpotifyAPI> initializeAsync(SpotifyConfiguration configuration) {
      return CompletableFuture.supplyAsync(() -> this.initialize(configuration));
   }

   default CompletableFuture<SpotifyAPI> initializeAsync() {
      return CompletableFuture.supplyAsync(this::initialize);
   }

   Track getTrack();

   default boolean hasTrack() {
      return this.getTrack() != null;
   }

   int getPosition();

   boolean hasPosition();

   boolean isPlaying();

   void pressMediaKey(MediaKey var1);

   boolean isConnected();

   boolean isInitialized();

   void registerListener(SpotifyListener var1);

   void unregisterListener(SpotifyListener var1);

   SpotifyConfiguration getConfiguration();

   void stop();

   void shutdown();
}
