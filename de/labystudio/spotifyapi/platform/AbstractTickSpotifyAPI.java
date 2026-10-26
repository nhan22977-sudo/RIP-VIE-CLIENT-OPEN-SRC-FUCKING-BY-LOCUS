package de.labystudio.spotifyapi.platform;

import de.labystudio.spotifyapi.SpotifyAPI;
import de.labystudio.spotifyapi.SpotifyListener;
import de.labystudio.spotifyapi.config.SpotifyConfiguration;
import de.labystudio.spotifyapi.open.OpenSpotifyAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTickSpotifyAPI implements SpotifyAPI {
   protected static final long TICK_INTERVAL = 1000L;
   protected final List<SpotifyListener> listeners = new ArrayList<>();
   private OpenSpotifyAPI openAPI;
   protected SpotifyConfiguration configuration;
   private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
      Thread thread = new Thread(runnable, "spotify-api-tick");
      thread.setDaemon(true);
      return thread;
   });
   private ScheduledFuture<?> task;
   private long timeLastException = -1L;

   @Override
   public SpotifyAPI initialize(SpotifyConfiguration configuration) {
      synchronized (this) {
         this.configuration = configuration;
         if (this.executor.isShutdown()) {
            throw new IllegalStateException("This SpotifyAPI has been shutdown and cannot be reused");
         }

         if (this.isInitialized()) {
            throw new IllegalStateException("This SpotifyAPI is already initialized");
         }

         this.onInitialized();
         this.task = this.executor.scheduleWithFixedDelay(this::onInternalTick, 0L, 1000L, TimeUnit.MILLISECONDS);
         this.onInternalTick();
         return this;
      }
   }

   protected void onInitialized() {
   }

   protected synchronized void onInternalTick() {
      try {
         long timeSinceLastException = System.currentTimeMillis() - this.timeLastException;
         if (timeSinceLastException < this.configuration.getExceptionReconnectDelay()) {
            return;
         }

         this.onTick();
      } catch (Exception e) {
         this.timeLastException = System.currentTimeMillis();
         this.listeners.forEach(listener -> listener.onDisconnect(e));
         if (!this.configuration.isAutoReconnect()) {
            this.stop();
         }
      }
   }

   protected abstract void onTick() throws Exception;

   @Override
   public void registerListener(SpotifyListener listener) {
      this.listeners.add(listener);
   }

   @Override
   public void unregisterListener(SpotifyListener listener) {
      this.listeners.remove(listener);
   }

   @Override
   public boolean isInitialized() {
      return this.task != null;
   }

   @Override
   public SpotifyConfiguration getConfiguration() {
      return this.configuration;
   }

   @Override
   public void stop() {
      synchronized (this) {
         if (this.task != null) {
            this.task.cancel(true);
            this.task = null;
         }
      }
   }

   @Override
   public void shutdown() {
      this.stop();
      this.executor.shutdownNow();
   }
}
