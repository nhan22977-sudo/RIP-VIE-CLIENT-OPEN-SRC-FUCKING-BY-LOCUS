package me.drake.vieclient.modules.client.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

public final class SpotifyMetadataResolver implements MusicMetadataResolver {
   private static final String SEARCH_URL = "https://itunes.apple.com/search?term=";
   private final HttpTextClient http;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SpotifyMetadataResolver(HttpTextClient http) {
      this.http = httpx;
   }

   @Override
   public Optional resolve(String query) {
      if (query != null && !query.isBlank()) {
         String var2 = query.trim();
         if (isAdOrIdleQuery(var2)) {
            return Optional.empty();
         }

         Optional var3 = this.search(var2);
         if (var3.isPresent()) {
            return var3;
         }

         int var4 = -1;
         int var5 = 0;
         String[] var10000 = new String[3];
         byte[] var10003 = new byte[3];
         var10003[0] = (byte)(58);
         var10003[1] = (byte)(-73);
         var10003[2] = (byte)(60);
         var10000[0] = $$v_s(var10003, 5408283327871569983L);
         var10003 = new byte[5];
         var10003[0] = (byte)(58);
         var10003[1] = (byte)(120);
         var10003[2] = (byte)(-100);
         var10003[3] = (byte)(74);
         var10003[4] = (byte)(-111);
         var10000[1] = $$v_s(var10003, 5408283327871569983L);
         int var10002 = 2;
         var10003 = new byte[5];
         var10003[0] = (byte)(58);
         var10003[1] = (byte)(120);
         var10003[2] = (byte)(-100);
         var10003[3] = (byte)(77);
         var10003[4] = (byte)(-111);
         var10000[var10002] = $$v_s(var10003, 5408283327871569983L);

         for (String var9 : var10000) {
            int var10 = var2.indexOf(var9);
            if (var10 > 0 && var10 < var2.length() - var9.length()) {
               var4 = var10;
               var5 = var9.length();
               break;
            }
         }

         if (var4 > 0) {
            String var13 = var2.substring(0, var4).trim();
            String var14 = var2.substring(var4 + var5).trim();
            if (!var14.isBlank()) {
               var3 = this.search(var14);
               if (var3.isPresent()) {
                  return var3;
               }
            }

            if (!var13.isBlank()) {
               var3 = this.search(var13);
               if (var3.isPresent()) {
                  return var3;
               }
            }
         }

         return Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   private Optional search(String term) {
      try {
         String var2 = "https://itunes.apple.com/search?term=" + URLEncoder.encode(term, StandardCharsets.UTF_8) + "&entity=song&limit=1";
         String var3 = this.http.get(var2);
         if (var3 != null && !var3.isBlank()) {
            JsonObject var4 = JsonParser.parseString(var3).getAsJsonObject();
            if (!var4.has("results")) {
               return Optional.empty();
            }

            JsonArray var5 = var4.getAsJsonArray("results");
            if (var5.isEmpty()) {
               return Optional.empty();
            }

            JsonObject var6 = var5.get(0).getAsJsonObject();
            String var7 = var6.has("trackName")
               ? var6.get("trackName").getAsString().trim()
               : "";
            String var8 = var6.has("artistName")
               ? var6.get("artistName").getAsString().trim()
               : "";
            if (var7.isBlank()) {
               return Optional.empty();
            }

            int var9 = 0;
            if (var6.has("trackTimeMillis")) {
               try {
                  var9 = (int)Math.max(
                     0L,
                     var6.get("trackTimeMillis").getAsLong()
                  );
               } catch (RuntimeException var12) {
               }
            }

            String var10 = "";
            if (var6.has("artworkUrl100")
               && !var6.get("artworkUrl100").isJsonNull()) {
               String var11 = var6.get("artworkUrl100")
                  .getAsString()
                  .trim();
               var10 = var11.replace(
                  "100x100bb",
                  "600x600bb"
               );
            } else if (var6.has("artworkUrl60")
               && !var6.get("artworkUrl60").isJsonNull()) {
               var10 = var6.get("artworkUrl60")
                  .getAsString()
                  .trim()
                  .replace(
                     "60x60bb",
                     "600x600bb"
                  );
            }

            String var14 = "spotify:" + var7.toLowerCase(Locale.ROOT) + ":" + var8.toLowerCase(Locale.ROOT);
            return Optional.of(
               new MusicMetadata(var14, var7, var8.isBlank() ? "Spotify" : var8, var9, var10)
            );
         } else {
            return Optional.empty();
         }
      } catch (Throwable var13) {
         return Optional.empty();
      }
   }

   private static boolean isAdOrIdleQuery(String query) {
      String var1 = query.toLowerCase(Locale.ROOT);
      return var1.equals("advertisement")
         || var1.startsWith("advertisement")
         || var1.contains("advertisement")
         || var1.equals("quảng cáo")
         || var1.startsWith("quảng cáo")
         || var1.contains("quảng cáo")
         || var1.equals("spotify")
         || var1.equals("spotify free")
         || var1.equals("spotify premium")
         || var1.equals("unknown artist")
         || var1.equals("not playing");
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
