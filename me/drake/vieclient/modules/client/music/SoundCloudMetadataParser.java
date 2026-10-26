package me.drake.vieclient.modules.client.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SoundCloudMetadataParser {
   private static final Pattern HYDRATION_CLIENT_ID = Pattern.compile(
      "\"apiClient\"[\\s\\S]*?\"id\"\\s*:\\s*\"([A-Za-z0-9_-]{32})\""
   );
   private static final Pattern QUOTED_CLIENT_ID = Pattern.compile(
      "(?i)client_id\\s*[:=]\\s*[\\\"']([A-Za-z0-9_-]{32})[\\\"']"
   );
   private static final Pattern URL_CLIENT_ID = Pattern.compile(
      "(?i)client_id(?:=|%3D)([A-Za-z0-9_-]{32})"
   );
   private static final Pattern SCRIPT_SOURCE = Pattern.compile(
      "(?i)<script[^>]+src=[\\\"']([^\\\"']+)[\\\"']"
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SoundCloudMetadataParser() {
   }

   public static Optional extractClientId(String text) {
      if (text != null && !text.isBlank()) {
         Matcher var1 = HYDRATION_CLIENT_ID.matcher(text);
         if (var1.find()) {
            return Optional.of(var1.group(1));
         }

         Matcher var2 = QUOTED_CLIENT_ID.matcher(text);
         if (var2.find()) {
            return Optional.of(var2.group(1));
         }

         Matcher var3 = URL_CLIENT_ID.matcher(text);
         return var3.find() ? Optional.of(var3.group(1)) : Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   public static List extractScriptUrls(String html) {
      if (html != null && !html.isBlank()) {
         ArrayList var1 = new ArrayList();
         Matcher var2 = SCRIPT_SOURCE.matcher(html);

         while (var2.find()) {
            String var3 = var2.group(1).trim();
            byte[] var10001 = new byte[3];
            var10001[0] = (byte)(3);
            var10001[1] = (byte)(-16);
            var10001[2] = (byte)(111);
            if (var3.contains($$v_s(var10001, -4582158688248934392L))) {
               var10001 = new byte[2];
               var10001[0] = (byte)(2);
               var10001[1] = (byte)(-75);
               if (var3.startsWith($$v_s(var10001, -4582158688248934392L))) {
                  var3 = "https:" + var3;
               } else if (var3.startsWith("/")) {
                  var3 = "https://soundcloud.com" + var3;
               }

               var10001 = new byte[8];
               var10001[0] = (byte)(69);
               var10001[1] = (byte)(-18);
               var10001[2] = (byte)(104);
               var10001[3] = (byte)(-82);
               var10001[4] = (byte)(57);
               var10001[5] = (byte)(2);
               var10001[6] = (byte)(68);
               var10001[7] = (byte)(-57);
               if (var3.startsWith($$v_s(var10001, -4582158688248934392L))) {
                  var1.add(var3);
               }
            }
         }

         return List.copyOf(var1);
      } else {
         return List.of();
      }
   }

   public static Optional parseSearch(String json) {
      try {
         JsonObject var1 = JsonParser.parseString(json).getAsJsonObject();
         JsonArray var2 = var1.getAsJsonArray("collection");
         if (var2 == null) {
            return Optional.empty();
         }

         for (JsonElement var4 : var2) {
            if (var4.isJsonObject()) {
               JsonObject var5 = var4.getAsJsonObject();
               if (!var5.has("kind")
                  || "track"
                     .equalsIgnoreCase(string(var5, "kind"))) {
                  String var6 = string(var5, "title");
                  if (!var6.isBlank()) {
                     int var7 = integer(var5, "full_duration");
                     if (var7 <= 0) {
                        var7 = integer(var5, "duration");
                     }

                     if (var7 > 0) {
                        JsonObject var8 = var5.has("user")
                              && var5.get("user").isJsonObject()
                           ? var5.getAsJsonObject("user")
                           : null;
                        String var9 = var8 == null ? "" : string(var8, "username");
                        String var10 = highResolutionArtwork(
                           string(var5, "artwork_url")
                        );
                        String var11 = string(var5, "id");
                        return Optional.of(new MusicMetadata(var11.isBlank() ? "soundcloud:" + var6 : "soundcloud:" + var11, var6, var9, var7, var10));
                     }
                  }
               }
            }
         }
      } catch (RuntimeException var12) {
      }

      return Optional.empty();
   }

   private static String highResolutionArtwork(String url) {
      byte[] var10001 = new byte[7];
      var10001[0] = 0;
      var10001[1] = (byte)(-10);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-84);
      var10001[4] = (byte)(45);
      var10001[5] = (byte)(93);
      var10001[6] = (byte)(69);
      String var1 = $$v_s(var10001, -4582158688248934392L);
      byte[] var10002 = new byte[10];
      var10002[0] = 0;
      var10002[1] = (byte)(-18);
      var10002[2] = (byte)(41);
      var10002[3] = (byte)(-18);
      var10002[4] = (byte)(122);
      var10002[5] = (byte)(64);
      var10002[6] = (byte)(94);
      var10002[7] = (byte)(-40);
      var10002[8] = (byte)(117);
      var10002[9] = (byte)(-116);
      return url.replace(var1, $$v_s(var10002, -4582158688248934392L));
   }

   private static String string(JsonObject object, String key) {
      return object != null && object.has(key) && !object.get(key).isJsonNull() ? object.get(key).getAsString().trim() : "";
   }

   private static int integer(JsonObject object, String key) {
      if (object.has(key) && !object.get(key).isJsonNull()) {
         long var2 = object.get(key).getAsLong();
         return (int)Math.max(0L, Math.min(2147483647L, var2));
      } else {
         return 0;
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
