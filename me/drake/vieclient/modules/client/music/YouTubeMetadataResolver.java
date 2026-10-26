package me.drake.vieclient.modules.client.music;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class YouTubeMetadataResolver implements MusicMetadataResolver {
   private static final Pattern VIDEO_ID;
   private final HttpTextClient http;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public YouTubeMetadataResolver(HttpTextClient http) {
      this.http = httpx;
   }

   @Override
   public Optional resolve(String rawQuery) {
      if (rawQuery != null && !rawQuery.isBlank()) {
         String var2 = cleanQuery(rawQuery);
         Optional var3 = extractVideoId(var2);
         if (var3.isPresent()) {
            String var12 = (String)var3.get();
            String var13 = this.http.get("https://www.youtube.com/watch?v=" + var12);
            return YouTubeMetadataParser.parse(var12, var13);
         }

         String var4 = URLEncoder.encode(var2.isBlank() ? rawQuery.trim() : var2, StandardCharsets.UTF_8);
         String var5 = this.http.get("https://www.youtube.com/results?search_query=" + var4);
         Optional var6 = YouTubeMetadataParser.parseSearch(var5);
         String var7 = var6.map(MusicMetadata::identity).filter(id -> !id.isBlank()).orElseGet(() -> YouTubeMetadataParser.firstVideoId(var5).orElse(""));
         if (var7.isBlank()) {
            return Optional.empty();
         }

         String var8 = this.http.get("https://www.youtube.com/watch?v=" + var7);
         Optional var9 = YouTubeMetadataParser.parse(var7, var8);
         if (var9.isPresent()) {
            MusicMetadata var10 = (MusicMetadata)var9.get();
            if (var10.durationMs() > 0) {
               return Optional.of(var10);
            }

            if (var6.isPresent() && ((MusicMetadata)var6.get()).durationMs() > 0) {
               MusicMetadata var11;
               String var14;
               MusicMetadata var10000;
               String var10002;
               String var10003;
               label42: {
                  var11 = (MusicMetadata)var6.get();
                  var10000 = new MusicMetadata;
                  var10002 = var10.identity();
                  var10003 = var10.title().isBlank() ? var11.title() : var10.title();
                  if (!var10.owner().isBlank()) {
                     var14 = var10.owner();
                     byte[] var10005 = new byte[7];
                     var10005[0] = (byte)(103);
                     var10005[1] = (byte)(-11);
                     var10005[2] = (byte)(105);
                     var10005[3] = (byte)(-118);
                     var10005[4] = (byte)(77);
                     var10005[5] = (byte)(55);
                     var10005[6] = (byte)(-39);
                     if (!var14.equalsIgnoreCase($$v_s(var10005, -5062174068725002213L))) {
                        var14 = var10.owner();
                        break label42;
                     }
                  }

                  var14 = var11.owner();
               }

               var10000./* $VF: Unable to resugar constructor */<init>(
                  var10002, var10003, var14, var11.durationMs(), var10.artworkUrl().isBlank() ? var11.artworkUrl() : var10.artworkUrl()
               );
               return Optional.of(var10000);
            } else {
               return Optional.of(var10);
            }
         } else {
            return var6;
         }
      } else {
         return Optional.empty();
      }
   }

   private static String cleanQuery(String query) {
      if (query == null) {
         return "";
      }

      String var1 = query.trim();
      var1 = var1.replaceFirst("^\\(\\d+\\+?\\)\\s*", "");
      var1 = var1.replaceFirst(
         "(?i)\\s*[-–—]\\s*YouTube(?:\\s*Music)?$",
         ""
      );
      var1 = var1.replaceFirst(
         "(?i)\\s*[-–—]\\s*(?:Google Chrome|Microsoft Edge|Mozilla Firefox|Brave|Thorium|Chromium|Opera|Vivaldi)$",
         ""
      );
      return var1.trim();
   }

   private static Optional extractVideoId(String value) {
      if (value == null) {
         return Optional.empty();
      }

      Matcher var1 = VIDEO_ID.matcher(value.trim());
      return !var1.find() ? Optional.empty() : Optional.ofNullable(var1.group(1));
   }

   static {
      byte[] var10000 = new byte[40];
      var10000[0] = (byte)(22);
      var10000[1] = (byte)(-91);
      var10000[2] = (byte)(38);
      var10000[3] = (byte)(-89);
      var10000[4] = (byte)(87);
      var10000[5] = (byte)(32);
      var10000[6] = (byte)(-56);
      var10000[7] = (byte)(-28);
      var10000[8] = (byte)(10);
      var10000[9] = (byte)(-116);
      var10000[10] = (byte)(-122);
      var10000[11] = (byte)(-109);
      var10000[12] = (byte)(79);
      var10000[13] = (byte)(-15);
      var10000[14] = (byte)(-49);
      var10000[15] = (byte)(-42);
      var10000[16] = (byte)(72);
      var10000[17] = (byte)(23);
      var10000[18] = (byte)(-70);
      var10000[19] = (byte)(-109);
      var10000[20] = (byte)(-95);
      var10000[21] = (byte)(-115);
      var10000[22] = (byte)(-73);
      var10000[23] = (byte)(-128);
      var10000[24] = (byte)(-85);
      var10000[25] = (byte)(72);
      var10000[26] = (byte)(-11);
      var10000[27] = (byte)(107);
      var10000[28] = (byte)(-54);
      var10000[29] = (byte)(-19);
      var10000[30] = (byte)(-23);
      var10000[31] = (byte)(32);
      var10000[32] = (byte)(-127);
      var10000[33] = (byte)(23);
      var10000[34] = (byte)(33);
      var10000[35] = (byte)(5);
      var10000[36] = (byte)(-23);
      var10000[37] = (byte)(-60);
      var10000[38] = (byte)(97);
      var10000[39] = (byte)(88);
      VIDEO_ID = Pattern.compile($$v_s(var10000, -5062174068725002213L));
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
