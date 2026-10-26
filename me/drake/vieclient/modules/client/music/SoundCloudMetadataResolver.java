package me.drake.vieclient.modules.client.music;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SoundCloudMetadataResolver implements MusicMetadataResolver {
   private static final String HOME = "https://soundcloud.com/";
   private static final String SEARCH = "https://soundcloud.com/search";
   private static final String API_SEARCH = "https://api-v2.soundcloud.com/search/tracks";
   private static final Pattern OG_TITLE = Pattern.compile(
      "(?is)<meta\\s+property=\"og:title\"\\s+content=\"([^\"]+)\""
   );
   private static final Pattern OG_IMAGE = Pattern.compile(
      "(?is)<meta\\s+property=\"og:image\"\\s+content=\"([^\"]+)\""
   );
   private static final Pattern OG_DESC = Pattern.compile(
      "(?is)<meta\\s+property=\"og:description\"\\s+content=\"([^\"]+)\""
   );
   private static final Pattern TRACK_LINK = Pattern.compile(
      "(?i)href=\"(/[A-Za-z0-9][A-Za-z0-9_-]{1,30}/[A-Za-z0-9][A-Za-z0-9_-]{1,80})(?:\\?[^\"]*)?\""
   );
   private static final Pattern DURATION_MS_PATTERN = Pattern.compile(
      "\"full_duration\":(\\d+)|(?:\"duration\"|full_duration):(\\d{4,9})"
   );
   private static final Pattern DURATION_ISO_PATTERN = Pattern.compile(
      "(?i)itemprop=\"duration\"\\s+content=\"(PT[^\"]+)\"|\"duration\":\"(PT[^\"]+)\""
   );
   private static final Pattern BY_ARTIST = Pattern.compile(
      "(?i)\\bby\\b\\s+([^,.;\\n]+)"
   );
   private static final Set NON_TRACK_SLUGS = Set.of(
      "likes",
      "sets",
      "tracks",
      "reposts",
      "albums",
      "playlists",
      "following",
      "followers",
      "comments",
      "popular-tracks",
      "stats",
      "recent",
      "spotlight",
      "toptracks"
   );
   private final HttpTextClient http;
   private volatile String clientId = "";
   private static final String DEFAULT_CLIENT_ID = "tNiXK56A9yLDayzQ5DsBvqwiyhRLZZNv";
   private static final Pattern NUMERIC_ENTITY = Pattern.compile(
      "&#(x[0-9a-fA-F]+|[0-9]+);"
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SoundCloudMetadataResolver(HttpTextClient http) {
      this.http = httpx;
   }

   @Override
   public Optional resolve(String query) {
      if (query != null && !query.isBlank()) {
         String var2 = query.trim();
         Optional var3 = this.resolveViaApi(var2);
         if (var3.isPresent()) {
            return var3;
         }

         try {
            Optional var4 = this.scrapeSearch(var2);
            if (var4.isPresent()) {
               return var4;
            }
         } catch (RuntimeException var5) {
         }

         return Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   private Optional resolveViaApi(String query) {
      String var2 = this.getClientId();
      if (var2.isBlank()) {
         return Optional.empty();
      }

      try {
         String var3 = "https://api-v2.soundcloud.com/search/tracks?q="
            + URLEncoder.encode(query, StandardCharsets.UTF_8)
            + "&client_id="
            + var2
            + "&limit=10&offset=0";
         return SoundCloudMetadataParser.parseSearch(this.http.get(var3));
      } catch (RuntimeException var8) {
         synchronized (this) {
            this.clientId = "";
         }

         String var4 = this.getClientId();
         if (!var4.isBlank()) {
            try {
               String var5 = "https://api-v2.soundcloud.com/search/tracks?q="
                  + URLEncoder.encode(query, StandardCharsets.UTF_8)
                  + "&client_id="
                  + var4
                  + "&limit=10&offset=0";
               return SoundCloudMetadataParser.parseSearch(this.http.get(var5));
            } catch (RuntimeException var7) {
            }
         }

         return Optional.empty();
      }
   }

   private Optional scrapeSearch(String query) {
      String var2 = "https://soundcloud.com/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
      String var3 = this.http.get(var2);

      for (String var6 : extractTrackPaths(var3)) {
         String var7 = "https://soundcloud.com/" + var6;

         try {
            Optional var8 = this.scrapeTrack(var7, query);
            if (var8.isPresent()) {
               return var8;
            }
         } catch (RuntimeException var9) {
         }
      }

      return Optional.empty();
   }

   private Optional scrapeTrack(String trackUrl, String originalQuery) {
      String var3 = this.http.get(trackUrl);
      String var4 = matchGroup(OG_TITLE, var3);
      if (var4.isBlank()) {
         return Optional.empty();
      }

      String var5 = matchGroup(OG_IMAGE, var3);
      String var6 = matchGroup(OG_DESC, var3);
      String var7 = decodeHtmlEntities(var4).trim();
      String var8 = inferOwner(var7, var6, originalQuery);
      int var9 = extractDuration(var3);
      return Optional.of(new MusicMetadata("soundcloud:" + trackUrl, var7, var8, var9, var5));
   }

   private static int extractDuration(String html) {
      if (html != null && !html.isBlank()) {
         Matcher var1 = DURATION_MS_PATTERN.matcher(html);
         if (var1.find()) {
            String var2 = var1.group(1) != null ? var1.group(1) : var1.group(2);

            try {
               long var3 = Long.parseLong(var2);
               if (var3 > 0L) {
                  return (int)Math.min(2147483647L, var3);
               }
            } catch (NumberFormatException var5) {
            }
         }

         Matcher var7 = DURATION_ISO_PATTERN.matcher(html);
         if (var7.find()) {
            String var8 = var7.group(1) != null ? var7.group(1) : var7.group(2);

            try {
               return (int)Math.min(2147483647L, Duration.parse(var8).toMillis());
            } catch (Exception var6) {
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   private static String inferOwner(String title, String description, String originalQuery) {
      if (description != null && !description.isBlank()) {
         Matcher var3 = BY_ARTIST.matcher(description);
         if (var3.find()) {
            return decodeHtmlEntities(var3.group(1)).trim();
         }
      }

      if (originalQuery != null && originalQuery.contains(title)) {
         int var5 = originalQuery.indexOf(title) + title.length();
         String var4 = originalQuery.substring(var5).trim();
         if (!var4.isEmpty()) {
            return var4;
         }
      }

      byte[] var10000 = new byte[14];
      var10000[0] = (byte)(69);
      var10000[1] = (byte)(-12);
      var10000[2] = (byte)(119);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(28);
      var10000[5] = (byte)(-69);
      var10000[6] = (byte)(124);
      var10000[7] = (byte)(-33);
      var10000[8] = (byte)(57);
      var10000[9] = (byte)(-48);
      var10000[10] = (byte)(-112);
      var10000[11] = (byte)(-97);
      var10000[12] = (byte)(88);
      var10000[13] = (byte)(96);
      return $$v_s(var10000, -2949555620909166539L);
   }

   private static List extractTrackPaths(String html) {
      if (html != null && !html.isBlank()) {
         Matcher var1 = TRACK_LINK.matcher(html);
         LinkedHashSet var2 = new LinkedHashSet();

         while (var1.find()) {
            String var3 = var1.group(1);
            String var4 = var3.toLowerCase(Locale.ROOT);
            if (!var4.startsWith("/search/")
               && !var4.startsWith("/upload")
               && !var4.startsWith("/discover")
               && !var4.startsWith("/charts")
               && !var4.startsWith("/terms")
               && !var4.startsWith("/privacy")
               && !var4.startsWith("/imprint")
               && !var4.startsWith("/messages")
               && !var4.startsWith("/notifications")
               && !var4.startsWith("/settings")
               && !var4.startsWith("/you/")
               && !var4.startsWith("/me/")) {
               int var5 = var3.indexOf(47, 1);
               if (var5 >= 0) {
                  String var6 = var3.substring(1, var5);
                  String var7 = var3.substring(var5 + 1);
                  if (!var6.isBlank() && !var7.isBlank() && !NON_TRACK_SLUGS.contains(var7.toLowerCase(Locale.ROOT))) {
                     var2.add(var3);
                     if (var2.size() >= 5) {
                        break;
                     }
                  }
               }
            }
         }

         return List.copyOf(var2);
      } else {
         return List.of();
      }
   }

   private static String matchGroup(Pattern pattern, String input) {
      if (input == null) {
         return "";
      }

      Matcher var2 = pattern.matcher(input);
      return var2.find() ? var2.group(1) : "";
   }

   private static String decodeHtmlEntities(String value) {
      if (value == null) {
         return "";
      }

      byte[] var10001 = new byte[6];
      var10001[0] = (byte)(54);
      var10001[1] = (byte)(-21);
      var10001[2] = (byte)(105);
      var10001[3] = (byte)(-79);
      var10001[4] = (byte)(7);
      var10001[5] = (byte)(-9);
      String var10000 = value.replace($$v_s(var10001, -2949555620909166539L), "\"");
      var10001 = new byte[6];
      var10001[0] = (byte)(54);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(108);
      var10001[3] = (byte)(-79);
      var10001[4] = 0;
      var10001[5] = (byte)(-9);
      var10000 = var10000.replace($$v_s(var10001, -2949555620909166539L), "'");
      var10001 = new byte[4];
      var10001[0] = (byte)(54);
      var10001[1] = (byte)(-10);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-27);
      var10000 = var10000.replace($$v_s(var10001, -2949555620909166539L), "<");
      var10001 = new byte[4];
      var10001[0] = (byte)(54);
      var10001[1] = (byte)(-3);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-27);
      var10000 = var10000.replace($$v_s(var10001, -2949555620909166539L), ">");
      var10001 = new byte[6];
      var10001[0] = (byte)(54);
      var10001[1] = (byte)(-12);
      var10001[2] = (byte)(126);
      var10001[3] = (byte)(-83);
      var10001[4] = (byte)(3);
      var10001[5] = (byte)(-9);
      String var1 = var10000.replace($$v_s(var10001, -2949555620909166539L), " ");
      Matcher var2 = NUMERIC_ENTITY.matcher(var1);
      StringBuilder var3 = new StringBuilder(var1.length());

      while (var2.find()) {
         String var4 = var2.group(1);
         String var5 = var2.group();

         try {
            int var6 = var4.charAt(0) != (120) && var4.charAt(0) != (88)
               ? Integer.parseInt(var4)
               : Integer.parseInt(var4.substring(1), 16);
            if (Character.isValidCodePoint(var6)) {
               var5 = new String(Character.toChars(var6));
            }
         } catch (RuntimeException var7) {
         }

         var2.appendReplacement(var3, Matcher.quoteReplacement(var5));
      }

      var2.appendTail(var3);
      var10000 = var3.toString();
      var10001 = new byte[5];
      var10001[0] = (byte)(54);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(113);
      var10001[3] = (byte)(-82);
      var10001[4] = (byte)(72);
      return var10000.replace($$v_s(var10001, -2949555620909166539L), "&");
   }

   private String getClientId() {
      String var1 = this.clientId;
      if (!var1.isBlank()) {
         return var1;
      }

      synchronized (this) {
         if (!this.clientId.isBlank()) {
            return this.clientId;
         }

         try {
            String var3 = this.http
               .get(
                  "https://soundcloud.com/"
               );
            Optional var4 = SoundCloudMetadataParser.extractClientId(var3);
            if (var4.isPresent()) {
               this.clientId = (String)var4.get();
               return this.clientId;
            }

            List var5 = SoundCloudMetadataParser.extractScriptUrls(var3);

            for (int var6 = var5.size() - 1; var6 >= 0; var6--) {
               try {
                  Optional var7 = SoundCloudMetadataParser.extractClientId(this.http.get((String)var5.get(var6)));
                  if (var7.isPresent()) {
                     this.clientId = (String)var7.get();
                     return this.clientId;
                  }
               } catch (RuntimeException var9) {
               }
            }
         } catch (RuntimeException var10) {
         }

         this.clientId = "tNiXK56A9yLDayzQ5DsBvqwiyhRLZZNv";
         return this.clientId;
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
}
