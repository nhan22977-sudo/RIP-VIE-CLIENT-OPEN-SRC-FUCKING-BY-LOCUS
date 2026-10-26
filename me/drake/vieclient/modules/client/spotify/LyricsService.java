package me.drake.vieclient.modules.client.spotify;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LyricsService implements AutoCloseable {
   private static final Logger LOGGER;
   private static final URI LRCLIB;
   private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(8L);
   private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(12L);
   private static final int MAX_BODY_BYTES = 4194304;
   private static final long REQUEST_GAP_MS = 300L;
   private static final long FOUND_TTL_MS = TimeUnit.DAYS.toMillis(30L);
   private static final long NOT_FOUND_TTL_MS = TimeUnit.HOURS.toMillis(12L);
   private static final long FAILURE_RETRY_MS = 60000L;
   private static final int MEMORY_ENTRIES = 64;
   private static final int DISK_MAX_FILES = 400;
   private static final long DISK_MAX_BYTES = 6291456L;
   private final Object lock = new Object();
   private final Map memory = Collections.synchronizedMap(new LinkedHashMap(64, 0.75F, true) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      @Override
      protected boolean removeEldestEntry(Entry eldest) {
         return this.size() > (64);
      }
   });
   private final Set pending = new HashSet();
   private final ThreadPoolExecutor worker;
   private final LyricsDiskCache disk;
   private final String userAgent;
   private volatile String wanted;
   private volatile LyricsService.KeyMemo lastKey;
   private volatile boolean closed;
   private volatile boolean online = true;
   private volatile int mode;
   private volatile long busyUntil;
   private volatile HttpClient http;
   private volatile ExecutorService httpExecutor;
   private long nextRequestAt;
   private int writesSincePrune = 25;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public LyricsService() {
      Path var1 = defaultCacheDir();
      this.disk = var1 == null ? null : new LyricsDiskCache(var1, 400, 6291456L);
      this.userAgent = defaultUserAgent();
      TimeUnit var10006 = TimeUnit.MILLISECONDS;
      LinkedBlockingQueue var10007 = new LinkedBlockingQueue();
      byte[] var10008 = new byte[16];
      var10008[0] = (byte)(118);
      var10008[1] = (byte)(-13);
      var10008[2] = (byte)(121);
      var10008[3] = (byte)(-99);
      var10008[4] = (byte)(-110);
      var10008[5] = (byte)(-81);
      var10008[6] = (byte)(-19);
      var10008[7] = (byte)(7);
      var10008[8] = (byte)(60);
      var10008[9] = (byte)(-113);
      var10008[10] = (byte)(-88);
      var10008[11] = (byte)(-113);
      var10008[12] = (byte)(-44);
      var10008[13] = (byte)(119);
      var10008[14] = (byte)(-61);
      var10008[15] = (byte)(98);
      this.worker = new ThreadPoolExecutor(1, 1, 30000L, var10006, var10007, daemonThreads($$v_s(var10008, 4722895328698945541L)));
      this.worker.allowCoreThreadTimeOut(true);
   }

   public Lyrics lookup(LyricsQuery query) {
      if (query != null && query.searchable()) {
         String var2 = this.keyOf(query);
         this.wanted = var2;
         long var3 = System.currentTimeMillis();
         synchronized (this.lock) {
            LyricsService.Memo var6 = (LyricsService.Memo)this.memory.get(var2);
            if (var6 != null && var3 < var6.freshUntil) {
               return var6.lyrics;
            }

            if (!this.closed && this.pending.add(var2)) {
               try {
                  this.worker.execute(() -> this.resolve(var2, query));
               } catch (RejectedExecutionException var9) {
                  this.pending.remove(var2);
               }
            }

            if (var6 != null) {
               return var6.lyrics;
            } else {
               return this.closed ? Lyrics.UNAVAILABLE : Lyrics.LOADING;
            }
         }
      } else {
         return Lyrics.NOT_FOUND;
      }
   }

   public void setOnline(boolean online) {
      if (this.online != onlinex) {
         this.online = onlinex;
         synchronized (this.lock) {
            this.mode++;
            this.memory.clear();
         }
      }
   }

   @Override
   public void close() {
      this.closed = true;
      this.worker.shutdownNow();
      synchronized (this.lock) {
         this.pending.clear();
      }

      HttpClient var4 = this.http;
      if (var4 != null) {
         var4.shutdownNow();
      }

      ExecutorService var2 = this.httpExecutor;
      if (var2 != null) {
         var2.shutdownNow();
      }
   }

   private void resolve(String key, LyricsQuery query) {
      LyricsDiskCache.Entry var3 = null;
      int var4 = this.mode;

      try {
         if (this.closed || !key.equals(this.wanted)) {
            return;
         }

         var3 = this.disk == null ? null : this.disk.read(key);
         if (var3 != null) {
            long var5 = var3.fetchedAt() + (var3.found() ? FOUND_TTL_MS : NOT_FOUND_TTL_MS);
            if (System.currentTimeMillis() < var5) {
               this.remember(key, Lyrics.of(var3.track()), var5);
               return;
            }
         }

         if (this.online) {
            LyricsService.Found var40 = this.fetch(query);
            long var41 = System.currentTimeMillis();
            Lyrics var8 = Lyrics.of(var40.track);
            if (!var40.complete()) {
               this.remember(key, var8, this.retryAt());
               return;
            }

            LrclibTrack var9 = var8.status() == Lyrics.Status.NOT_FOUND ? null : var40.track;
            this.remember(key, var8, var41 + (var9 == null ? NOT_FOUND_TTL_MS : FOUND_TTL_MS));
            if (this.disk != null && !this.closed) {
               this.disk.write(key, var9, var41);
               if (++this.writesSincePrune >= 25) {
                  this.writesSincePrune = 0;
                  this.disk.prune();
                  return;
               }
            }

            return;
         }

         Lyrics var39 = var3 != null ? Lyrics.of(var3.track()) : Lyrics.NOT_FOUND;
         synchronized (this.lock) {
            if (this.mode == var4) {
               this.memory.put(key, new LyricsService.Memo(var39, System.currentTimeMillis() + 60000L));
            }
         }
      } catch (InterruptedException var35) {
         Thread.currentThread().interrupt();
         return;
      } catch (IOException var36) {
         LOGGER.debug(
            "[VieClient] Lyrics lookup for \"{}\" failed: {}",
            query.title(),
            var36.toString()
         );
         this.remember(key, var3 != null ? Lyrics.of(var3.track()) : Lyrics.UNAVAILABLE, this.retryAt());
         return;
      } catch (RuntimeException var37) {
         LOGGER.debug("[VieClient] Lyrics lookup for \"" + query.title() + "\" failed", var37);
         this.remember(key, Lyrics.UNAVAILABLE, this.retryAt());
         return;
      } finally {
         synchronized (this.lock) {
            this.pending.remove(key);
         }
      }
   }

   private LyricsService.Found fetch(LyricsQuery query) throws IOException, InterruptedException {
      ArrayList var2 = new ArrayList();
      String var3 = query.title();
      String var4 = query.artist();
      boolean var5 = LyricsMatcher.isGenericArtist(var4);
      String[] var6 = LyricsMatcher.splitTitle(var3);
      if (var6 != null) {
         String var7 = var6[0].trim();
         String var8 = var6[1].trim();
         addCandidatePair(var2, LyricsMatcher.baseTitle(var8), LyricsMatcher.primaryArtist(var7));
         addCandidatePair(var2, var8, var7);
         addCandidatePair(var2, LyricsMatcher.baseTitle(var7), LyricsMatcher.primaryArtist(var8));
         addCandidatePair(var2, var7, var8);
      }

      if (!var5 && !var4.isBlank()) {
         addCandidatePair(var2, query.title(), query.artist());
         String var17 = LyricsMatcher.baseTitle(query.title());
         String var19 = LyricsMatcher.primaryArtist(query.artist());
         addCandidatePair(var2, var17, var19);
      }

      LrclibTrack var18 = null;

      for (String[] var9 : var2) {
         String var10 = var9[0];
         String var11 = var9[1];
         if (!var10.isBlank()) {
            if (query.hasDuration()) {
               try {
                  LrclibTrack var12 = this.get(var10, var11, query.album(), query.durationSeconds());
                  if (var12 != null && (var12.hasSynced() || var12.instrumental() && !var12.hasPlain())) {
                     return new LyricsService.Found(var12, true);
                  }

                  if (var12 != null && var12.usable() && var18 == null) {
                     var18 = var12;
                  }
               } catch (IOException var16) {
               }
            }

            try {
               LrclibTrack var25 = this.get(var10, var11, "", 0);
               if (var25 != null
                  && (var25.hasSynced() || var25.instrumental() && !var25.hasPlain())
                  && (!query.hasDuration() || Math.abs(var25.duration() - query.durationSeconds()) <= 35.0)) {
                  return new LyricsService.Found(var25, true);
               }

               if (var25 != null && var25.usable() && var18 == null) {
                  var18 = var25;
               }
            } catch (IOException var15) {
            }
         }
      }

      IOException var21 = null;

      for (String[] var23 : LyricsMatcher.searchTerms(query)) {
         List var24 = List.of();

         try {
            var24 = this.search(var23[0], var23[1]);
         } catch (IOException var14) {
            var21 = var14;
         }

         if (var24.isEmpty() && (!var23[0].isBlank() || !var23[1].isBlank())) {
            try {
               String var26 = (var23[0] + " " + var23[1]).trim();
               var24 = this.searchQ(var26);
            } catch (IOException var13) {
               var21 = var13;
            }
         }

         LrclibTrack var27 = LyricsMatcher.best(var24, query);
         if (var27 != null) {
            if (var27.hasSynced()) {
               return new LyricsService.Found(var27, true);
            }

            if (var18 == null) {
               var18 = var27;
            }
         }
      }

      if (var18 == null && var21 != null) {
         throw var21;
      } else {
         return new LyricsService.Found(var18, true);
      }
   }

   private static void addCandidatePair(List list, String title, String artist) {
      if (title != null && !title.isBlank()) {
         if (artist == null) {
            artist = "";
         }

         for (String[] var4 : list) {
            if (var4[0].equalsIgnoreCase(title) && var4[1].equalsIgnoreCase(artist)) {
               return;
            }
         }

         String[] var10001 = new String[2];
         var10001[0] = title;
         var10001[1] = artist;
         list.add(var10001);
      }
   }

   private LrclibTrack get(String title, String artist, String album, int durationSeconds) throws IOException, InterruptedException {
      LyricsService.Response var5 = this.send(
         withQuery(
            "api/get",
            "track_name",
            title,
            "artist_name",
            artist,
            "album_name",
            album,
            "duration",
            durationSeconds >= 1 && durationSeconds <= 3600 ? Integer.toString(durationSeconds) : ""
         )
      );
      if (var5.status == 404 || var5.status == 400) {
         return null;
      } else if (var5.status != 200) {
         throw new IOException("LRCLIB /api/get answered HTTP " + var5.status);
      } else {
         return LrclibTrack.fromJson(json(var5.body));
      }
   }

   private List search(String title, String artist) throws IOException, InterruptedException {
      byte[] var10001 = new byte[10];
      var10001[0] = (byte)(65);
      var10001[1] = (byte)(-22);
      var10001[2] = (byte)(117);
      var10001[3] = (byte)(-15);
      var10001[4] = (byte)(-115);
      var10001[5] = (byte)(-93);
      var10001[6] = (byte)(-23);
      var10001[7] = (byte)(27);
      var10001[8] = (byte)(43);
      var10001[9] = (byte)(-54);
      String var4 = $$v_s(var10001, 4722895328698945541L);
      String[] var10002 = new String[4];
      byte[] var10005 = new byte[10];
      var10005[0] = (byte)(84);
      var10005[1] = (byte)(-24);
      var10005[2] = (byte)(125);
      var10005[3] = (byte)(-67);
      var10005[4] = (byte)(-107);
      var10005[5] = (byte)(-103);
      var10005[6] = (byte)(-26);
      var10005[7] = (byte)(8);
      var10005[8] = (byte)(37);
      var10005[9] = (byte)(-57);
      var10002[0] = $$v_s(var10005, 4722895328698945541L);
      var10002[1] = title;
      int var10004 = 2;
      var10005 = new byte[11];
      var10005[0] = (byte)(65);
      var10005[1] = (byte)(-24);
      var10005[2] = (byte)(104);
      var10005[3] = (byte)(-73);
      var10005[4] = (byte)(-115);
      var10005[5] = (byte)(-78);
      var10005[6] = (byte)(-41);
      var10005[7] = (byte)(7);
      var10005[8] = (byte)(41);
      var10005[9] = (byte)(-49);
      var10005[10] = (byte)(-127);
      var10002[var10004] = $$v_s(var10005, 4722895328698945541L);
      var10002[3] = artist;
      LyricsService.Response var3 = this.send(withQuery(var4, var10002));
      if (var3.status == (404)) {
         return List.of();
      } else if (var3.status != (200)) {
         throw new IOException("LRCLIB /api/search answered HTTP " + var3.status);
      } else {
         return LrclibTrack.listFromJson(json(var3.body));
      }
   }

   private List searchQ(String q) throws IOException, InterruptedException {
      if (q != null && !q.isBlank()) {
         byte[] var10001 = new byte[10];
         var10001[0] = (byte)(65);
         var10001[1] = (byte)(-22);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-15);
         var10001[4] = (byte)(-115);
         var10001[5] = (byte)(-93);
         var10001[6] = (byte)(-23);
         var10001[7] = (byte)(27);
         var10001[8] = (byte)(43);
         var10001[9] = (byte)(-54);
         String var3 = $$v_s(var10001, 4722895328698945541L);
         String[] var10002 = new String[2];
         var10002[0] = "q";
         var10002[1] = q;
         LyricsService.Response var2 = this.send(withQuery(var3, var10002));
         if (var2.status == (404)) {
            return List.of();
         } else if (var2.status != (200)) {
            throw new IOException("LRCLIB /api/search answered HTTP " + var2.status);
         } else {
            return LrclibTrack.listFromJson(json(var2.body));
         }
      } else {
         return List.of();
      }
   }

   private LyricsService.Response send(String pathAndQuery) throws IOException, InterruptedException {
      URI var2 = LRCLIB.resolve(pathAndQuery);
      int var3 = 1;

      while (true) {
         this.pace();
         HttpRequest var4 = HttpRequest.newBuilder(var2)
            .timeout(REQUEST_TIMEOUT)
            .header("User-Agent", this.userAgent)
            .header(
               "Accept",
               "application/json"
            )
            .GET()
            .build();
         HttpResponse var5 = this.exchange(var4);
         this.nextRequestAt = System.currentTimeMillis() + 300L;
         int var6 = var5.statusCode();
         if (var6 != 429 && var6 != 503) {
            return new LyricsService.Response(var6, new String((byte[])var5.body(), StandardCharsets.UTF_8));
         }

         long var7 = retryAfterMillis(
            var5.headers().firstValue("Retry-After").orElse(null)
         );
         if (var7 * var3 > 3000L || var3 >= 3) {
            this.busyUntil = System.currentTimeMillis() + var7;
            throw new IOException("LRCLIB answered HTTP " + var6 + ", retry in " + var7 + " ms");
         }

         this.nextRequestAt = System.currentTimeMillis() + var7 * var3;
         var3++;
      }
   }

   private HttpResponse exchange(HttpRequest request) throws IOException, InterruptedException {
      CompletableFuture var2 = this.http().sendAsync(request, new LyricsService.CappedBody(4194304));

      try {
         return (HttpResponse)var2.get(REQUEST_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
      } catch (TimeoutException var6) {
         var2.cancel(true);
         throw new HttpTimeoutException("LRCLIB did not answer within " + REQUEST_TIMEOUT.toMillis() + " ms");
      } catch (InterruptedException var7) {
         var2.cancel(true);
         throw var7;
      } catch (ExecutionException var8) {
         Throwable var4 = var8.getCause();
         if (var4 instanceof IOException var5) {
            throw var5;
         } else {
            byte[] var10002 = new byte[21];
            var10002[0] = (byte)(108);
            var10002[1] = (byte)(-56);
            var10002[2] = (byte)(95);
            var10002[3] = (byte)(-110);
            var10002[4] = (byte)(-73);
            var10002[5] = (byte)(-124);
            var10002[6] = (byte)(-88);
            var10002[7] = (byte)(27);
            var10002[8] = (byte)(45);
            var10002[9] = (byte)(-45);
            var10002[10] = (byte)(-111);
            var10002[11] = (byte)(-109);
            var10002[12] = (byte)(-43);
            var10002[13] = (byte)(106);
            var10002[14] = (byte)(-128);
            var10002[15] = (byte)(119);
            var10002[16] = (byte)(17);
            var10002[17] = (byte)(35);
            var10002[18] = (byte)(-96);
            var10002[19] = (byte)(-53);
            var10002[20] = (byte)(42);
            throw new IOException($$v_s(var10002, 4722895328698945541L), var4);
         }
      }
   }

   private void pace() throws IOException, InterruptedException {
      long var1 = this.busyUntil;
      long var3 = System.currentTimeMillis();
      if (var1 - var3 > 8000L) {
         throw new IOException("LRCLIB asked to wait another " + (var1 - var3) + " ms");
      }

      long var5 = Math.max(this.nextRequestAt, var1) - var3;
      if (var5 > 0L) {
         Thread.sleep(var5);
      }
   }

   private HttpClient http() throws InterruptedException {
      HttpClient var1 = this.http;
      if (var1 != null) {
         return var1;
      } else if (this.closed) {
         throw new InterruptedException(
            "lyrics service closed"
         );
      } else {
         ExecutorService var2 = Executors.newCachedThreadPool(
            daemonThreads(
               "VieClient-Lyrics-Http"
            )
         );
         var1 = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).followRedirects(Redirect.NORMAL).executor(var2).build();
         this.httpExecutor = var2;
         this.http = var1;
         if (this.closed) {
            var1.shutdownNow();
            var2.shutdownNow();
            throw new InterruptedException(
               "lyrics service closed"
            );
         } else {
            return var1;
         }
      }
   }

   private void remember(String key, Lyrics lyrics, long freshUntil) {
      synchronized (this.lock) {
         this.memory.put(key, new LyricsService.Memo(lyrics, freshUntil));
      }
   }

   private long retryAt() {
      return Math.max(System.currentTimeMillis() + 60000L, this.busyUntil);
   }

   private String keyOf(LyricsQuery query) {
      LyricsService.KeyMemo var2 = this.lastKey;
      if (var2 != null && var2.query.equals(query)) {
         return var2.key;
      }

      String var3 = query.cacheKey();
      this.lastKey = new LyricsService.KeyMemo(query, var3);
      return var3;
   }

   private static String withQuery(String path, String... namesAndValues) {
      StringBuilder var2 = new StringBuilder(path);
      int var3 = 63;

      for (byte var4 = 0; var4 + 1 < namesAndValues.length; var4 += 2) {
         String var5 = namesAndValues[var4 + 1];
         if (var5 != null && !var5.isEmpty()) {
            var2.append((char)var3).append(namesAndValues[var4]).append((char)(61)).append(URLEncoder.encode(var5, StandardCharsets.UTF_8));
            var3 = 38;
         }
      }

      return var2.toString();
   }

   private static JsonElement json(String body) throws IOException {
      try {
         return LrclibTrack.parse(body);
      } catch (JsonParseException var2) {
         byte[] var10002 = new byte[26];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(95);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(-73);
         var10002[5] = (byte)(-124);
         var10002[6] = (byte)(-88);
         var10002[7] = (byte)(26);
         var10002[8] = (byte)(45);
         var10002[9] = (byte)(-52);
         var10002[10] = (byte)(-112);
         var10002[11] = (byte)(-42);
         var10002[12] = (byte)(-53);
         var10002[13] = (byte)(127);
         var10002[14] = (byte)(-52);
         var10002[15] = (byte)(119);
         var10002[16] = (byte)(31);
         var10002[17] = (byte)(56);
         var10002[18] = (byte)(-95);
         var10002[19] = (byte)(-53);
         var10002[20] = (byte)(42);
         var10002[21] = (byte)(22);
         var10002[22] = (byte)(-110);
         var10002[23] = (byte)(106);
         var10002[24] = (byte)(-41);
         var10002[25] = (byte)(92);
         throw new IOException($$v_s(var10002, 4722895328698945541L), var2);
      }
   }

   private static long retryAfterMillis(String header) {
      if (header != null && !header.isBlank()) {
         String var3 = header.strip();

         long var1;
         try {
            var1 = Long.parseLong(var3) * 1000L;
         } catch (NumberFormatException var7) {
            try {
               var1 = ZonedDateTime.parse(var3, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli() - System.currentTimeMillis();
            } catch (DateTimeParseException var6) {
               var1 = 30000L;
            }
         }

         return Math.max(1000L, Math.min(TimeUnit.HOURS.toMillis(1L), var1));
      } else {
         return 1500L;
      }
   }

   private static ThreadFactory daemonThreads(String name) {
      return runnable -> {
         Thread var2 = new Thread(runnable, name);
         var2.setDaemon(true);
         return var2;
      };
   }

   private static Path defaultCacheDir() {
      try {
         Path var10000 = FabricLoader.getInstance().getConfigDir();
         byte[] var10001 = new byte[9];
         var10001[0] = (byte)(86);
         var10001[1] = (byte)(-13);
         var10001[2] = (byte)(121);
         var10001[3] = (byte)(-67);
         var10001[4] = (byte)(-110);
         var10001[5] = (byte)(-81);
         var10001[6] = (byte)(-19);
         var10001[7] = (byte)(7);
         var10001[8] = (byte)(60);
         var10000 = var10000.resolve($$v_s(var10001, 4722895328698945541L));
         var10001 = new byte[12];
         var10001[0] = (byte)(76);
         var10001[1] = (byte)(-29);
         var10001[2] = (byte)(110);
         var10001[3] = (byte)(-73);
         var10001[4] = (byte)(-99);
         var10001[5] = (byte)(-75);
         var10001[6] = (byte)(-91);
         var10001[7] = (byte)(10);
         var10001[8] = (byte)(41);
         var10001[9] = (byte)(-63);
         var10001[10] = (byte)(-116);
         var10001[11] = (byte)(-109);
         Path var0 = var10000.resolve($$v_s(var10001, 4722895328698945541L));
         Files.createDirectories(var0);
         return var0;
      } catch (Throwable var1) {
         return null;
      }
   }

   private static String defaultUserAgent() {
      byte[] var10000 = new byte[3];
      var10000[0] = (byte)(68);
      var10000[1] = (byte)(-1);
      var10000[2] = (byte)(106);
      String var0 = $$v_s(var10000, 4722895328698945541L);

      try {
         FabricLoader var3 = FabricLoader.getInstance();
         byte[] var10001 = new byte[10];
         var10001[0] = (byte)(86);
         var10001[1] = (byte)(-13);
         var10001[2] = (byte)(121);
         var10001[3] = (byte)(-127);
         var10001[4] = (byte)(-99);
         var10001[5] = (byte)(-86);
         var10001[6] = (byte)(-31);
         var10001[7] = (byte)(12);
         var10001[8] = (byte)(38);
         var10001[9] = (byte)(-42);
         Optional var4 = var3.getModContainer($$v_s(var10001, 4722895328698945541L)).map(c -> c.getMetadata().getVersion().getFriendlyString());
         var10001 = new byte[3];
         var10001[0] = (byte)(68);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(106);
         var0 = var4.orElse($$v_s(var10001, 4722895328698945541L));
      } catch (Throwable var2) {
      }

      int var1 = var0.indexOf(43);
      if (var1 > 0) {
         var0 = var0.substring(0, var1);
      }

      return "VieClient/" + var0 + " (Vie Client Minecraft client)";
   }

   static {
      byte[] var10000 = new byte[23];
      var10000[0] = (byte)(118);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-99);
      var10000[4] = (byte)(-110);
      var10000[5] = (byte)(-81);
      var10000[6] = (byte)(-19);
      var10000[7] = (byte)(7);
      var10000[8] = (byte)(60);
      var10000[9] = (byte)(-113);
      var10000[10] = (byte)(-73);
      var10000[11] = (byte)(-122);
      var10000[12] = (byte)(-55);
      var10000[13] = (byte)(106);
      var10000[14] = (byte)(-55);
      var10000[15] = (byte)(119);
      var10000[16] = (byte)(9);
      var10000[17] = (byte)(6);
      var10000[18] = (byte)(-75);
      var10000[19] = (byte)(-36);
      var10000[20] = (byte)(39);
      var10000[21] = (byte)(85);
      var10000[22] = (byte)(-85);
      LOGGER = LoggerFactory.getLogger($$v_s(var10000, 4722895328698945541L));
      var10000 = new byte[19];
      var10000[0] = (byte)(72);
      var10000[1] = (byte)(-18);
      var10000[2] = (byte)(104);
      var10000[3] = (byte)(-82);
      var10000[4] = (byte)(-115);
      var10000[5] = (byte)(-4);
      var10000[6] = (byte)(-89);
      var10000[7] = (byte)(70);
      var10000[8] = (byte)(36);
      var10000[9] = (byte)(-48);
      var10000[10] = (byte)(-121);
      var10000[11] = (byte)(-102);
      var10000[12] = (byte)(-49);
      var10000[13] = (byte)(124);
      var10000[14] = (byte)(-114);
      var10000[15] = (byte)(127);
      var10000[16] = (byte)(21);
      var10000[17] = (byte)(62);
      var10000[18] = (byte)(-29);
      LRCLIB = URI.create($$v_s(var10000, 4722895328698945541L));
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

   private static final class CappedBody implements BodyHandler {
      private final int maxBytes;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      CappedBody(int maxBytes) {
         this.maxBytes = maxBytesx;
      }

      @Override
      public BodySubscriber apply(ResponseInfo info) {
         return info.statusCode() / (100) != (2)
            ? BodySubscribers.ofByteArray()
            : BodySubscribers.mapping(BodySubscribers.ofByteArray(), bytes -> bytes.length > this.maxBytes ? Arrays.copyOf(bytes, this.maxBytes) : bytes);
      }
   }

   private record Found(LrclibTrack track, boolean complete) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record KeyMemo(LyricsQuery query, String key) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record Memo(Lyrics lyrics, long freshUntil) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record Response(int status, String body) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
