package me.drake.vieclient.modules.client.spotify;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.regex.Pattern;

final class LyricsDiskCache {
   private static final int FORMAT = 1;
   private static final String SUFFIX = ".json";
   private static final Pattern KEY;
   private static final long MAX_FILE_BYTES = 524288L;
   private final Path dir;
   private final int maxFiles;
   private final long maxBytes;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   LyricsDiskCache(Path dir, int maxFiles, long maxBytes) {
      this.dir = dirx;
      this.maxFiles = maxFilesx;
      this.maxBytes = maxBytesx;
   }

   LyricsDiskCache.Entry read(String key) {
      Path var2 = this.file(key);
      if (var2 == null) {
         return null;
      }

      try {
         if (Files.size(var2) > 524288L) {
            return null;
         }

         String var3 = Files.readString(var2, StandardCharsets.UTF_8);
         JsonElement var4 = JsonParser.parseString(var3);
         if (!var4.isJsonObject()) {
            return null;
         }

         JsonObject var5 = var4.getAsJsonObject();
         byte[] var10001 = new byte[6];
         var10001[0] = (byte)(86);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(110);
         var10001[3] = (byte)(-77);
         var10001[4] = (byte)(-49);
         var10001[5] = (byte)(-90);
         if (numI(var5, $$v_s(var10001, 5205892988436467733L)) != 1) {
            return null;
         }

         var10001 = new byte[9];
         var10001[0] = (byte)(86);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-67);
         var10001[4] = (byte)(-58);
         var10001[5] = (byte)(-73);
         var10001[6] = (byte)(88);
         var10001[7] = (byte)(33);
         var10001[8] = (byte)(44);
         long var6 = numL(var5, $$v_s(var10001, 5205892988436467733L));
         if (var6 <= 0L) {
            return null;
         }

         var10001 = new byte[5];
         var10001[0] = (byte)(68);
         var10001[1] = (byte)(-24);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-67);
         var10001[4] = (byte)(-59);
         LrclibTrack var10000;
         if (var5.has($$v_s(var10001, 5205892988436467733L))) {
            var10001 = new byte[5];
            var10001[0] = (byte)(68);
            var10001[1] = (byte)(-24);
            var10001[2] = (byte)(125);
            var10001[3] = (byte)(-67);
            var10001[4] = (byte)(-59);
            var10000 = LrclibTrack.fromJson(var5.get($$v_s(var10001, 5205892988436467733L)));
         } else {
            var10000 = null;
         }

         LrclibTrack var8 = var10000;
         touch(var2);
         return new LyricsDiskCache.Entry(var8, var6);
      } catch (Exception var9) {
         return null;
      }
   }

   void write(String key, LrclibTrack track, long fetchedAt) {
      Path var5 = this.file(key);
      if (var5 != null) {
         JsonObject var6 = new JsonObject();
         byte[] var10001 = new byte[6];
         var10001[0] = (byte)(86);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(110);
         var10001[3] = (byte)(-77);
         var10001[4] = (byte)(-49);
         var10001[5] = (byte)(-90);
         var6.addProperty($$v_s(var10001, 5205892988436467733L), 1);
         var10001 = new byte[9];
         var10001[0] = (byte)(86);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-67);
         var10001[4] = (byte)(-58);
         var10001[5] = (byte)(-73);
         var10001[6] = (byte)(88);
         var10001[7] = (byte)(33);
         var10001[8] = (byte)(44);
         var6.addProperty($$v_s(var10001, 5205892988436467733L), fetchedAt);
         if (track != null) {
            var10001 = new byte[5];
            var10001[0] = (byte)(68);
            var10001[1] = (byte)(-24);
            var10001[2] = (byte)(125);
            var10001[3] = (byte)(-67);
            var10001[4] = (byte)(-59);
            var6.add($$v_s(var10001, 5205892988436467733L), track.toJson());
         }

         Path var7 = var5.resolveSibling(var5.getFileName().toString() + ".tmp");

         try {
            Files.createDirectories(this.dir);
            String var13 = var6.toString();
            Charset var10002 = StandardCharsets.UTF_8;
            OpenOption[] var10003 = new OpenOption[2];
            var10003[0] = StandardOpenOption.CREATE;
            var10003[1] = StandardOpenOption.TRUNCATE_EXISTING;
            Files.writeString(var7, var13, var10002, var10003);

            try {
               CopyOption[] var14 = new CopyOption[2];
               var14[0] = StandardCopyOption.REPLACE_EXISTING;
               var14[1] = StandardCopyOption.ATOMIC_MOVE;
               Files.move(var7, var5, var14);
            } catch (IOException var9) {
               Files.move(var7, var5, StandardCopyOption.REPLACE_EXISTING);
            }
         } catch (IOException var10) {
            delete(var7);
         }
      }
   }

   void prune() {
      if (Files.isDirectory(this.dir)) {
         ArrayList var1 = new ArrayList();
         long var2 = 0L;

         try (DirectoryStream var4 = Files.newDirectoryStream(this.dir)) {
            for (Path var6 : var4) {
               String var7 = var6.getFileName().toString();
               if (var7.endsWith(".json.tmp")) {
                  delete(var6);
               } else if (var7.endsWith(".json")
                  && KEY.matcher(var7.substring(0, var7.length() - ".json".length())).matches()) {
                  BasicFileAttributes var8 = Files.readAttributes(var6, BasicFileAttributes.class);
                  if (var8.isRegularFile()) {
                     var1.add(new LyricsDiskCache.Stored(var6, var8.size(), var8.lastModifiedTime().toMillis()));
                     var2 += var8.size();
                  }
               }
            }
         } catch (IOException var11) {
            return;
         }

         if (var1.size() > this.maxFiles || var2 > this.maxBytes) {
            var1.sort((a, b) -> Long.compare(a.usedAt, b.usedAt));
            int var12 = var1.size();

            for (LyricsDiskCache.Stored var14 : var1) {
               if (var12 <= this.maxFiles && var2 <= this.maxBytes) {
                  break;
               }

               if (delete(var14.file)) {
                  var12--;
                  var2 -= var14.size;
               }
            }
         }
      }
   }

   private Path file(String key) {
      return key != null && KEY.matcher(key).matches() ? this.dir.resolve(key + ".json") : null;
   }

   private static boolean delete(Path file) {
      try {
         Files.deleteIfExists(file);
         return true;
      } catch (IOException var2) {
         return false;
      }
   }

   private static void touch(Path file) {
      try {
         Files.setLastModifiedTime(file, FileTime.fromMillis(System.currentTimeMillis()));
      } catch (IOException var2) {
      }
   }

   private static int numI(JsonObject o, String n) {
      return o.get(n) instanceof JsonPrimitive var3 && var3.isNumber() ? var3.getAsInt() : -1;
   }

   private static long numL(JsonObject o, String n) {
      return o.get(n) instanceof JsonPrimitive var3 && var3.isNumber() ? var3.getAsLong() : -1L;
   }

   static {
      byte[] var10000 = new byte[15];
      var10000[0] = (byte)(107);
      var10000[1] = (byte)(-86);
      var10000[2] = (byte)(49);
      var10000[3] = (byte)(-25);
      var10000[4] = (byte)(-49);
      var10000[5] = (byte)(-1);
      var10000[6] = (byte)(90);
      var10000[7] = (byte)(61);
      var10000[8] = (byte)(35);
      var10000[9] = (byte)(-109);
      var10000[10] = (byte)(-46);
      var10000[11] = (byte)(-38);
      var10000[12] = (byte)(-64);
      var10000[13] = (byte)(62);
      var10000[14] = (byte)(105);
      KEY = Pattern.compile($$v_s(var10000, 5205892988436467733L));
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

   record Entry(LrclibTrack track, long fetchedAt) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public boolean found() {
         return this.track != null;
      }
   }

   private record Stored(Path file, long size, long usedAt) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
