package me.drake.vieclient.modules.client.music;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class MusicMetadataService implements AutoCloseable {
   private static final long FAILURE_BACKOFF_MS = 30000L;
   private final Map resolvers;
   private final Executor executor;
   private final boolean ownsExecutor;
   private final Map cache;
   private final ConcurrentHashMap inFlight = new ConcurrentHashMap();
   private final Map negativeUntil;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MusicMetadataService(Map resolvers, Executor executor, int maxEntries) {
      this(resolversx, executorx, maxEntries, false);
   }

   private MusicMetadataService(Map resolvers, Executor executor, int maxEntries, boolean ownsExecutor) {
      this.resolvers = Map.copyOf(resolversx);
      this.executor = executorx;
      this.ownsExecutor = ownsExecutorx;
      final int var5 = Math.max(8, maxEntries);
      this.cache = new LinkedHashMap(var5, 0.75F, true) {
         // $VF: synthetic field
         private static final boolean $$vie_p = true;

         @Override
         protected boolean removeEldestEntry(Entry eldest) {
            return this.size() > var5;
         }
      };
      this.negativeUntil = new LinkedHashMap(var5, 0.75F, true) {
         // $VF: synthetic field
         private static final boolean $$vie_p = true;

         @Override
         protected boolean removeEldestEntry(Entry eldest) {
            return this.size() > var5;
         }
      };
   }

   public static MusicMetadataService createDefault(Map resolvers) {
      ExecutorService var1 = Executors.newSingleThreadExecutor(runnable -> {
         byte[] var10003 = new byte[18];
         var10003[0] = (byte)(76);
         var10003[1] = (byte)(-13);
         var10003[2] = (byte)(121);
         var10003[3] = (byte)(-13);
         var10003[4] = (byte)(20);
         var10003[5] = (byte)(-96);
         var10003[6] = (byte)(-98);
         var10003[7] = (byte)(80);
         var10003[8] = (byte)(49);
         var10003[9] = (byte)(-113);
         var10003[10] = (byte)(-119);
         var10003[11] = (byte)(-109);
         var10003[12] = (byte)(85);
         var10003[13] = (byte)(108);
         var10003[14] = (byte)(-95);
         var10003[15] = (byte)(32);
         var10003[16] = (byte)(30);
         var10003[17] = (byte)(43);
         Thread var1x = new Thread(runnable, $$v_s(var10003, 1291983063612641311L));
         var1x.setDaemon(true);
         return var1x;
      });
      return new MusicMetadataService(resolversx, var1, 96, true);
   }

   public CompletableFuture resolve(MusicSource source, String query) {
      if (source != null && query != null && !query.isBlank()) {
         MusicMetadataService.Key var3 = new MusicMetadataService.Key(source, query.trim().toLowerCase(Locale.ROOT));
         synchronized (this.cache) {
            MusicMetadata var5 = (MusicMetadata)this.cache.get(var3);
            if (var5 != null) {
               return CompletableFuture.completedFuture(Optional.of(var5));
            }
         }

         synchronized (this.negativeUntil) {
            Long var13 = (Long)this.negativeUntil.get(var3);
            if (var13 != null) {
               if (var13 > System.currentTimeMillis()) {
                  return CompletableFuture.completedFuture(Optional.empty());
               }

               this.negativeUntil.remove(var3);
            }
         }

         MusicMetadataResolver var12 = (MusicMetadataResolver)this.resolvers.get(source);
         if (var12 == null) {
            return CompletableFuture.completedFuture(Optional.empty());
         }

         CompletableFuture var14 = new CompletableFuture();
         CompletableFuture var6 = this.inFlight.putIfAbsent(var3, var14);
         if (var6 != null) {
            return var6;
         }

         try {
            this.executor.execute(() -> {
               try {
                  Optional var5x = var12.resolve(query.trim());
                  var5x.ifPresent(metadata -> {
                     synchronized (this.cache) {
                        this.cache.put(var3, metadata);
                     }

                     synchronized (this.negativeUntil) {
                        this.negativeUntil.remove(var3);
                     }
                  });
                  if (var5x.isEmpty()) {
                     this.markFailed(var3);
                  }

                  var14.complete(var5x);
               } catch (Throwable var9) {
                  this.markFailed(var3);
                  var14.complete(Optional.empty());
               } finally {
                  this.inFlight.remove(var3, var14);
               }
            });
         } catch (Throwable var8) {
            this.inFlight.remove(var3, var14);
            var14.complete(Optional.empty());
         }

         return var14;
      } else {
         return CompletableFuture.completedFuture(Optional.empty());
      }
   }

   private void markFailed(MusicMetadataService.Key key) {
      synchronized (this.negativeUntil) {
         this.negativeUntil.put(key, System.currentTimeMillis() + 30000L);
      }
   }

   @Override
   public void close() {
      for (CompletableFuture var2 : this.inFlight.values()) {
         var2.complete(Optional.empty());
      }

      this.inFlight.clear();
      synchronized (this.negativeUntil) {
         this.negativeUntil.clear();
      }

      synchronized (this.cache) {
         this.cache.clear();
      }

      if (this.ownsExecutor && this.executor instanceof ExecutorService var9) {
         var9.shutdownNow();
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

   private record Key(MusicSource source, String query) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
