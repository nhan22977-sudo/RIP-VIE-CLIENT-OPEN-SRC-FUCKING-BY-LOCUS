package me.drake.vieclient.modules.client.music;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import javax.imageio.ImageIO;

public final class GsmtcJsonParser {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private GsmtcJsonParser() {
   }

   public static Optional parse(String line) {
      return parse(line, System.currentTimeMillis());
   }

   static Optional parse(String line, long nowMs) {
      if (line != null && !line.isBlank()) {
         try {
            JsonObject var3 = JsonParser.parseString(line).getAsJsonObject();
            String var4 = string(var3, "sourceApp");
            String var5 = string(var3, "title");
            if (!var4.isBlank() && !var5.isBlank()) {
               int var6 = integer(var3, "durationMs");
               int var7 = integer(var3, "positionMs");
               long var8 = optionalLong(
                  var3, "timelineUpdatedAtMs"
               );
               boolean var10 = "Playing"
                  .equalsIgnoreCase(string(var3, "status"));
               int var11 = currentPosition(var7, var6, var10, var8, nowMs);
               return Optional.of(
                  new WindowsMediaSessionSnapshot(
                     var4,
                     var5,
                     string(var3, "artist"),
                     var6,
                     var11,
                     var10,
                     decodeArtwork(string(var3, "artworkBase64")),
                     nowMs,
                     var7,
                     var8
                  )
               );
            } else {
               return Optional.empty();
            }
         } catch (RuntimeException var12) {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   private static String string(JsonObject json, String key) {
      return json.has(key) && !json.get(key).isJsonNull() ? json.get(key).getAsString().trim() : "";
   }

   private static int integer(JsonObject json, String key) {
      if (json.has(key) && !json.get(key).isJsonNull()) {
         try {
            long var2 = json.get(key).getAsLong();
            return (int)Math.max(0L, Math.min(2147483647L, var2));
         } catch (RuntimeException var4) {
            return 0;
         }
      } else {
         return 0;
      }
   }

   private static long optionalLong(JsonObject json, String key) {
      if (json.has(key) && !json.get(key).isJsonNull()) {
         try {
            return json.get(key).getAsLong();
         } catch (RuntimeException var3) {
            return 0L;
         }
      } else {
         return 0L;
      }
   }

   private static int currentPosition(int rawPositionMs, int durationMs, boolean playing, long timelineUpdatedAtMs, long nowMs) {
      long var7 = rawPositionMs;
      if (playing && timelineUpdatedAtMs > 0L && timelineUpdatedAtMs <= nowMs) {
         long var9 = nowMs - timelineUpdatedAtMs;
         long var11 = durationMs > 0 ? durationMs : 2147483647L;
         long var13 = var11 - var7;
         if (var13 > 0L) {
            var7 += Math.min(var9, var13);
         }
      }

      if (durationMs > 0) {
         var7 = Math.min(var7, durationMs);
      }

      return (int)Math.max(0L, var7);
   }

   private static BufferedImage decodeArtwork(String value) {
      if (value.isBlank()) {
         return null;
      }

      try {
         byte[] var1 = Base64.getDecoder().decode(value);
         return ImageIO.read(new ByteArrayInputStream(var1));
      } catch (IllegalArgumentException | IOException var2) {
         return null;
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
