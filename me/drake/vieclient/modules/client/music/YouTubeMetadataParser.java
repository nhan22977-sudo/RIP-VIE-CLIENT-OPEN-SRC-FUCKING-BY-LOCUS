package me.drake.vieclient.modules.client.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class YouTubeMetadataParser {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private YouTubeMetadataParser() {
   }

   public static Optional parse(String fallbackVideoId, String html) {
      if (html != null && !html.isBlank()) {
         String var2 = fallbackVideoId;
         String var3 = "";
         String var4 = "";
         int var5 = 0;
         String var6 = "";
         String var7 = extractObjectAfter(
            html,
            "ytInitialPlayerResponse"
         );
         if (var7 != null) {
            try {
               JsonObject var8 = JsonParser.parseString(var7).getAsJsonObject();
               JsonObject var9 = var8.getAsJsonObject("videoDetails");
               if (var9 != null) {
                  var2 = string(var9, "videoId", fallbackVideoId);
                  var3 = string(var9, "title", "");
                  var4 = string(
                     var9,
                     "author",
                     "YouTube"
                  );
                  var5 = secondsToMillis(
                     string(var9, "lengthSeconds", "0")
                  );
                  if (var5 <= 0) {
                     var5 = parseMillis(
                        string(
                           var9, "approxDurationMs", "0"
                        )
                     );
                  }

                  var6 = thumbnail(var9, var2);
               }

               if (var5 <= 0 && var8.has("microformat")) {
                  JsonObject var10 = var8.getAsJsonObject("microformat");
                  if (var10 != null
                     && var10.has(
                        "playerMicroformatRenderer"
                     )) {
                     JsonObject var11 = var10.getAsJsonObject(
                        "playerMicroformatRenderer"
                     );
                     var5 = secondsToMillis(
                        string(var11, "lengthSeconds", "0")
                     );
                     if (var3.isBlank()) {
                        var3 = string(var11, "title", "");
                     }

                     if (var4.isBlank() || var4.equalsIgnoreCase("YouTube")) {
                        var4 = string(
                           var11,
                           "ownerChannelName",
                           var4
                        );
                     }
                  }
               }

               if (var5 <= 0 && var8.has("streamingData")) {
                  var5 = extractDurationFromStreamingData(
                     var8.getAsJsonObject("streamingData")
                  );
               }
            } catch (RuntimeException var12) {
            }
         }

         if (var5 <= 0) {
            var5 = extractDurationFromHtml(html);
         }

         if (var3.isBlank() || var4.isBlank() || var6.isBlank()) {
            Optional var13 = parseFromMetaTags(var2, html);
            if (var13.isPresent()) {
               MusicMetadata var14 = (MusicMetadata)var13.get();
               if (var3.isBlank()) {
                  var3 = var14.title();
               }

               if (var4.isBlank() || var4.equalsIgnoreCase("YouTube")) {
                  var4 = var14.owner();
               }

               if (var6.isBlank()) {
                  var6 = var14.artworkUrl();
               }

               if (var5 <= 0) {
                  var5 = var14.durationMs();
               }
            }
         }

         if (var6.isBlank() && var2 != null && !var2.isBlank()) {
            var6 = "https://i.ytimg.com/vi/" + var2 + "/hqdefault.jpg";
         }

         if (var4.isBlank()) {
            var4 = "YouTube";
         }

         return var3.isBlank() && var5 <= 0 ? Optional.empty() : Optional.of(new MusicMetadata(var2, var3, var4, var5, var6));
      } else {
         return Optional.empty();
      }
   }

   public static Optional parseSearch(String searchHtml) {
      if (searchHtml != null && !searchHtml.isBlank()) {
         String var1 = "\"videoId\":\"";
         int var2 = 0;

         while (true) {
            int var3 = searchHtml.indexOf(var1, var2);
            if (var3 < 0) {
               return Optional.empty();
            }

            int var4 = var3 + var1.length();
            int var5 = searchHtml.indexOf(34, var4);
            if (var5 <= var4) {
               return Optional.empty();
            }

            String var6 = searchHtml.substring(var4, var5);
            var2 = var5 + 1;
            if (var6.matches("[A-Za-z0-9_-]{11}")) {
               int var7 = Math.min(searchHtml.length(), var5 + 2500);
               String var8 = searchHtml.substring(var4, var7);
               String var9 = extractSnippetField(
                  var8,
                  "\"title\":{\"runs\":[{\"text\":\"",
                  "\""
               );
               if (var9.isBlank()) {
                  var9 = extractSnippetField(
                     var8,
                     "\"title\":{\"accessibility\":{\"accessibilityData\":{\"label\":\"",
                     "\""
                  );
               }

               String var10 = extractSnippetField(
                  var8,
                  "\"longBylineText\":{\"runs\":[{\"text\":\"",
                  "\""
               );
               if (var10.isBlank()) {
                  var10 = extractSnippetField(
                     var8,
                     "\"ownerText\":{\"runs\":[{\"text\":\"",
                     "\""
                  );
               }

               String var11 = extractSnippetField(
                  var8,
                  "\"lengthText\":{\"accessibility\":",
                  "\"simpleText\":\"",
                  "\""
               );
               if (var11.isBlank()) {
                  var11 = extractSnippetField(
                     var8, "\"simpleText\":\"", "\""
                  );
               }

               int var12 = parseTimeStringToMillis(var11);
               String var13 = "https://i.ytimg.com/vi/" + var6 + "/hqdefault.jpg";
               if (!var9.isBlank() || var12 > 0) {
                  return Optional.of(
                     new MusicMetadata(
                        var6, var9, var10.isBlank() ? "YouTube" : var10, var12, var13
                     )
                  );
               }
            }
         }
      } else {
         return Optional.empty();
      }
   }

   public static int parseTimeStringToMillis(String timeStr) {
      if (timeStr != null && !timeStr.isBlank()) {
         String var1 = timeStr.trim();
         String[] var2 = var1.split(":");
         int var3 = 0;

         for (String var7 : var2) {
            try {
               var3 = var3 * (60) + Integer.parseInt(var7.trim());
            } catch (NumberFormatException var9) {
               return 0;
            }
         }

         return (int)Math.min(2147483647L, var3 * 1000L);
      } else {
         return 0;
      }
   }

   private static String extractSnippetField(String snippet, String prefix, String suffix) {
      int var3 = snippet.indexOf(prefix);
      if (var3 < 0) {
         return "";
      }

      int var4 = var3 + prefix.length();
      int var5 = snippet.indexOf(suffix, var4);
      return var5 <= var4 ? "" : snippet.substring(var4, var5).trim();
   }

   private static String extractSnippetField(String snippet, String context, String prefix, String suffix) {
      int var4 = snippet.indexOf(context);
      if (var4 < 0) {
         return "";
      } else {
         int var5 = snippet.indexOf(prefix, var4);
         if (var5 >= 0 && var5 <= var4 + (200)) {
            int var6 = var5 + prefix.length();
            int var7 = snippet.indexOf(suffix, var6);
            return var7 <= var6 ? "" : snippet.substring(var6, var7).trim();
         } else {
            return "";
         }
      }
   }

   private static int extractDurationFromStreamingData(JsonObject streamingData) {
      if (streamingData == null) {
         return 0;
      }

      for (String var4 : new String[]{
         "adaptiveFormats",
         "formats"
      }) {
         if (streamingData.has(var4)) {
            JsonArray var5 = streamingData.getAsJsonArray(var4);
            if (var5 != null) {
               for (int var6 = 0; var6 < var5.size(); var6++) {
                  JsonObject var7 = var5.get(var6).getAsJsonObject();
                  if (var7.has("approxDurationMs")) {
                     int var8 = parseMillis(
                        string(
                           var7, "approxDurationMs", "0"
                        )
                     );
                     if (var8 > 0) {
                        return var8;
                     }
                  }
               }
            }
         }
      }

      return 0;
   }

   private static int extractDurationFromHtml(String html) {
      Matcher var1 = Pattern.compile(
            "\"approxDurationMs\"\\s*:\\s*\"(\\d+)\""
         )
         .matcher(html);
      if (var1.find()) {
         int var2 = parseMillis(var1.group(1));
         if (var2 > 0) {
            return var2;
         }
      }

      Matcher var8 = Pattern.compile(
            "\"lengthSeconds\"\\s*:\\s*\"(\\d+)\""
         )
         .matcher(html);
      if (var8.find()) {
         int var3 = secondsToMillis(var8.group(1));
         if (var3 > 0) {
            return var3;
         }
      }

      Matcher var9 = Pattern.compile(
            "itemprop=[\"']duration[\"'][^>]*content=[\"']([^\"']+)[\"']"
         )
         .matcher(html);
      if (var9.find()) {
         int var4 = parseIsoDuration(var9.group(1));
         if (var4 > 0) {
            return var4;
         }
      }

      Matcher var10 = Pattern.compile(
            "content=[\"']([^\"']+)[\"'][^>]*itemprop=[\"']duration[\"']"
         )
         .matcher(html);
      if (var10.find()) {
         int var5 = parseIsoDuration(var10.group(1));
         if (var5 > 0) {
            return var5;
         }
      }

      Matcher var11 = Pattern.compile(
            "\"duration\"\\s*:\\s*\"(PT[^\"]+)\""
         )
         .matcher(html);
      if (var11.find()) {
         int var6 = parseIsoDuration(var11.group(1));
         if (var6 > 0) {
            return var6;
         }
      }

      Matcher var12 = Pattern.compile(
            "property=[\"'](?:og:video:duration|video:duration)[\"'][^>]*content=[\"'](\\d+)[\"']"
         )
         .matcher(html);
      if (var12.find()) {
         int var7 = secondsToMillis(var12.group(1));
         if (var7 > 0) {
            return var7;
         }
      }

      return 0;
   }

   private static Optional parseFromMetaTags(String fallbackVideoId, String html) {
      try {
         String var2 = matchMeta(
            html,
            "name=\"title\"",
            "property=\"og:title\""
         );
         String var3 = matchMeta(
            html,
            "itemprop=\"author\"",
            "name=\"author\"",
            "property=\"og:video:tag\""
         );
         if (var3.isBlank()) {
            var3 = "YouTube";
         }

         String var4 = matchMeta(
            html, "itemprop=\"duration\""
         );
         int var5 = parseIsoDuration(var4);
         String var6 = matchMeta(
            html,
            "property=\"og:image\"",
            "rel=\"image_src\""
         );
         if (var6.isBlank() && fallbackVideoId != null && !fallbackVideoId.isBlank()) {
            var6 = "https://i.ytimg.com/vi/" + fallbackVideoId + "/hqdefault.jpg";
         }

         return var2.isBlank() && var5 <= 0 && var6.isBlank() ? Optional.empty() : Optional.of(new MusicMetadata(fallbackVideoId, var2, var3, var5, var6));
      } catch (RuntimeException var7) {
         return Optional.empty();
      }
   }

   private static String matchMeta(String html, String... keyPatterns) {
      for (String var5 : keyPatterns) {
         int var6 = html.indexOf(var5);
         if (var6 >= 0) {
            byte[] var10001 = new byte[9];
            var10001[0] = (byte)(98);
            var10001[1] = (byte)(-11);
            var10001[2] = (byte)(114);
            var10001[3] = (byte)(-86);
            var10001[4] = (byte)(-74);
            var10001[5] = (byte)(-91);
            var10001[6] = (byte)(122);
            var10001[7] = (byte)(-14);
            var10001[8] = (byte)(75);
            int var7 = html.indexOf($$v_s(var10001, -1797757129991139292L), var6);
            if (var7 >= 0 && var7 < var6 + (120)) {
               var10001 = new byte[9];
               var10001[0] = (byte)('뇄' ^ '놦');
               var10001[1] = (byte)(-11);
               var10001[2] = (byte)(114);
               var10001[3] = (byte)(-86);
               var10001[4] = (byte)(-74);
               var10001[5] = (byte)(-91);
               var10001[6] = (byte)(122);
               var10001[7] = (byte)(-14);
               var10001[8] = (byte)(75);
               int var8 = var7 + $$v_s(var10001, -1797757129991139292L).length();
               int var9 = html.indexOf(34, var8);
               if (var9 > var8) {
                  return html.substring(var8, var9).trim();
               }
            }

            var10001 = new byte[6];
            var10001[0] = (byte)(105);
            var10001[1] = (byte)(-24);
            var10001[2] = (byte)(121);
            var10001[3] = (byte)(-72);
            var10001[4] = (byte)(-18);
            var10001[5] = (byte)(-23);
            int var11 = html.indexOf($$v_s(var10001, -1797757129991139292L), var6);
            if (var11 >= 0 && var11 < var6 + (120)) {
               var10001 = new byte[6];
               var10001[0] = (byte)(105);
               var10001[1] = (byte)(-24);
               var10001[2] = (byte)(121);
               var10001[3] = (byte)(-72);
               var10001[4] = (byte)(-18);
               var10001[5] = (byte)(-23);
               int var12 = var11 + $$v_s(var10001, -1797757129991139292L).length();
               int var10 = html.indexOf(34, var12);
               if (var10 > var12) {
                  return html.substring(var12, var10).trim();
               }
            }
         }
      }

      return "";
   }

   private static int parseIsoDuration(String iso) {
      if (iso != null && !iso.isBlank()) {
         try {
            return (int)Math.min(2147483647L, Duration.parse(iso.trim()).toMillis());
         } catch (Exception var2) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   static Optional firstVideoId(String searchHtml) {
      if (searchHtml == null) {
         return Optional.empty();
      }

      Optional var1 = parseSearch(searchHtml);
      if (var1.isPresent() && !((MusicMetadata)var1.get()).identity().isBlank()) {
         return Optional.of(((MusicMetadata)var1.get()).identity());
      }

      byte[] var10000 = new byte[11];
      var10000[0] = (byte)(35);
      var10000[1] = (byte)(-20);
      var10000[2] = (byte)(117);
      var10000[3] = (byte)(-70);
      var10000[4] = (byte)(-74);
      var10000[5] = (byte)(-92);
      var10000[6] = (byte)(71);
      var10000[7] = (byte)(-85);
      var10000[8] = (byte)(75);
      var10000[9] = (byte)(-104);
      var10000[10] = (byte)(-58);
      String var2 = $$v_s(var10000, -1797757129991139292L);
      int var3 = 0;

      while (true) {
         int var4 = searchHtml.indexOf(var2, var3);
         if (var4 < 0) {
            return Optional.empty();
         }

         int var5 = var4 + var2.length();
         int var6 = searchHtml.indexOf(34, var5);
         if (var6 <= var5) {
            return Optional.empty();
         }

         String var7 = searchHtml.substring(var5, var6);
         byte[] var10001 = new byte[17];
         var10001[0] = (byte)(90);
         var10001[1] = (byte)(-37);
         var10001[2] = (byte)(49);
         var10001[3] = (byte)(-124);
         var10001[4] = (byte)(-78);
         var10001[5] = (byte)(-26);
         var10001[6] = (byte)(116);
         var10001[7] = (byte)(-1);
         var10001[8] = (byte)(68);
         var10001[9] = (byte)(-101);
         var10001[10] = (byte)(-69);
         var10001[11] = (byte)(-37);
         var10001[12] = (byte)(-42);
         var10001[13] = (byte)(104);
         var10001[14] = (byte)(23);
         var10001[15] = (byte)(-122);
         var10001[16] = (byte)('슍' ^ '슡');
         if (var7.matches($$v_s(var10001, -1797757129991139292L))) {
            return Optional.of(var7);
         }

         var3 = var6 + 1;
      }
   }

   private static String thumbnail(JsonObject details, String videoId) {
      byte[] var10001 = new byte[9];
      var10001[0] = (byte)(117);
      var10001[1] = (byte)(-14);
      var10001[2] = (byte)(105);
      var10001[3] = (byte)(-77);
      var10001[4] = (byte)(-79);
      var10001[5] = (byte)(-91);
      var10001[6] = (byte)(111);
      var10001[7] = (byte)(-90);
      var10001[8] = (byte)(5);
      JsonObject var2 = details.getAsJsonObject($$v_s(var10001, -1797757129991139292L));
      if (var2 != null) {
         var10001 = new byte[10];
         var10001[0] = (byte)(117);
         var10001[1] = (byte)(-14);
         var10001[2] = (byte)(105);
         var10001[3] = (byte)(-77);
         var10001[4] = (byte)(-79);
         var10001[5] = (byte)(-91);
         var10001[6] = (byte)(111);
         var10001[7] = (byte)(-90);
         var10001[8] = (byte)(5);
         var10001[9] = (byte)(-47);
         JsonArray var3 = var2.getAsJsonArray($$v_s(var10001, -1797757129991139292L));
         if (var3 != null && !var3.isEmpty()) {
            JsonObject var4 = var3.get(var3.size() - 1).getAsJsonObject();
            var10001 = new byte[3];
            var10001[0] = (byte)(116);
            var10001[1] = (byte)(-24);
            var10001[2] = (byte)(112);
            String var5 = string(var4, $$v_s(var10001, -1797757129991139292L), "");
            if (!var5.isBlank()) {
               return var5;
            }
         }
      }

      return videoId != null && !videoId.isBlank() ? "https://i.ytimg.com/vi/" + videoId + "/hqdefault.jpg" : "";
   }

   private static int secondsToMillis(String seconds) {
      try {
         long var1 = Long.parseLong(seconds);
         return (int)Math.min(2147483647L, Math.max(0L, var1 * 1000L));
      } catch (NumberFormatException var3) {
         return 0;
      }
   }

   private static int parseMillis(String millis) {
      try {
         long var1 = Long.parseLong(millis);
         return (int)Math.min(2147483647L, Math.max(0L, var1));
      } catch (NumberFormatException var3) {
         return 0;
      }
   }

   private static String string(JsonObject object, String key, String fallback) {
      return object != null && object.has(key) && !object.get(key).isJsonNull() ? object.get(key).getAsString() : fallback;
   }

   private static String extractObjectAfter(String text, String marker) {
      int var2 = text.indexOf(marker);
      if (var2 < 0) {
         return null;
      }

      int var3 = text.indexOf(123, var2 + marker.length());
      if (var3 < 0) {
         return null;
      }

      int var4 = 0;
      boolean var5 = false;
      boolean var6 = false;

      for (int var7 = var3; var7 < text.length(); var7++) {
         char var8 = text.charAt(var7);
         if (var5) {
            if (var6) {
               var6 = false;
            } else if (var8 == (92)) {
               var6 = true;
            } else if (var8 == (34)) {
               var5 = false;
            }
         } else if (var8 == (34)) {
            var5 = true;
         } else if (var8 == (123)) {
            var4++;
         } else if (var8 == (125)) {
            if (--var4 == 0) {
               return text.substring(var3, var7 + 1);
            }
         }
      }

      return null;
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
