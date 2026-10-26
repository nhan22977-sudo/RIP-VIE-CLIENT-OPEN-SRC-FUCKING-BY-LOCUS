package me.drake.vieclient.modules.client.spotify;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public record LrclibTrack(
   long id, String trackName, String artistName, String albumName, double duration, boolean instrumental, String plainLyrics, String syncedLyrics
) {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public LrclibTrack(
      long id, String trackName, String artistName, String albumName, double duration, boolean instrumental, String plainLyrics, String syncedLyrics
   ) {
      trackName = nn(trackName);
      artistName = nn(artistName);
      albumName = nn(albumName);
      plainLyrics = nn(plainLyrics);
      syncedLyrics = nn(syncedLyrics);
      if (!Double.isFinite(duration) || duration < 0.0) {
         duration = 0.0;
      }

      this.id = id;
      this.trackName = trackName;
      this.artistName = artistName;
      this.albumName = albumName;
      this.duration = duration;
      this.instrumental = instrumental;
      this.plainLyrics = plainLyrics;
      this.syncedLyrics = syncedLyrics;
   }

   public boolean hasSynced() {
      return !this.syncedLyrics.isBlank();
   }

   public boolean hasPlain() {
      return !this.plainLyrics.isBlank();
   }

   public boolean usable() {
      return this.instrumental || this.hasSynced() || this.hasPlain();
   }

   public static LrclibTrack fromJson(JsonElement element) {
      if (element != null && element.isJsonObject()) {
         JsonObject var1 = element.getAsJsonObject();
         return new LrclibTrack(
            num(var1, "id", 0L),
            text(var1, "trackName"),
            text(var1, "artistName"),
            text(var1, "albumName"),
            num(var1, "duration", 0.0),
            bool(var1, "instrumental"),
            text(var1, "plainLyrics"),
            text(var1, "syncedLyrics")
         );
      } else {
         return null;
      }
   }

   public static List listFromJson(JsonElement element) {
      if (element != null && element.isJsonArray()) {
         JsonArray var1 = element.getAsJsonArray();
         ArrayList var2 = new ArrayList(var1.size());

         for (JsonElement var4 : var1) {
            LrclibTrack var5 = fromJson(var4);
            if (var5 != null) {
               var2.add(var5);
            }
         }

         return List.copyOf(var2);
      } else {
         return List.of();
      }
   }

   public static JsonElement parse(String json) {
      return JsonParser.parseString(json);
   }

   public JsonObject toJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("id", this.id);
      var1.addProperty("trackName", this.trackName);
      var1.addProperty("artistName", this.artistName);
      var1.addProperty("albumName", this.albumName);
      var1.addProperty("duration", this.duration);
      var1.addProperty("instrumental", this.instrumental);
      var1.addProperty("plainLyrics", this.plainLyrics);
      var1.addProperty("syncedLyrics", this.syncedLyrics);
      return var1;
   }

   private static String nn(String v) {
      return v == null ? "" : v;
   }

   private static String text(JsonObject o, String n) {
      return o.get(n) instanceof JsonPrimitive var3 && var3.isString() ? var3.getAsString() : "";
   }

   private static long num(JsonObject o, String n, long fb) {
      if (o.get(n) instanceof JsonPrimitive var5 && var5.isNumber()) {
         try {
            return var5.getAsLong();
         } catch (NumberFormatException var7) {
            return fb;
         }
      } else {
         return fb;
      }
   }

   private static double num(JsonObject o, String n, double fb) {
      return o.get(n) instanceof JsonPrimitive var5 && var5.isNumber() ? var5.getAsDouble() : fb;
   }

   private static boolean bool(JsonObject o, String n) {
      return o.get(n) instanceof JsonPrimitive var3 && var3.isBoolean() && var3.getAsBoolean();
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
