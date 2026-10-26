package me.drake.vieclient.modules.client.music;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public final class JdkArtworkLoader implements ArtworkLoader {
   private static final int MAX_ARTWORK_BYTES = 12582912;
   private static final int MAX_ARTWORK_DIMENSION = 4096;
   private static final long MAX_ARTWORK_PIXELS = 16777216L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   @Override
   public BufferedImage load(String url) {
      if (url != null && !url.isBlank()) {
         try {
            URI var2 = URI.create(url);
            String var3 = var2.getScheme() == null ? "" : var2.getScheme().toLowerCase(Locale.ROOT);
            if (!var3.equals("https")
               && !var3.equals("http")) {
               return null;
            }

            HttpRequest var4 = HttpRequest.newBuilder(var2)
               .timeout(Duration.ofSeconds(8L))
               .header(
                  "User-Agent",
                  "Mozilla/5.0 VieClient-MusicHUD/1.0"
               )
               .GET()
               .build();
            HttpResponse var5 = JdkArtworkLoader.Holder.CLIENT.send(var4, BodyHandlers.ofInputStream());
            if (var5.statusCode() >= 200 && var5.statusCode() < 300) {
               String var6 = var5.headers()
                  .firstValue("Content-Type")
                  .orElse("");
               if (!var6.toLowerCase(Locale.ROOT).startsWith("image/")) {
                  ((InputStream)var5.body()).close();
                  return null;
               }

               byte[] var7;
               try (InputStream var8 = (InputStream)var5.body()) {
                  var7 = var8.readNBytes(12582913);
               }

               return var7.length != 0 && var7.length <= 12582912 ? decodeBounded(var7) : null;
            } else {
               ((InputStream)var5.body()).close();
               return null;
            }
         } catch (InterruptedException var13) {
            Thread.currentThread().interrupt();
            return null;
         } catch (Exception var14) {
            return null;
         }
      } else {
         return null;
      }
   }

   private static BufferedImage decodeBounded(byte[] encoded) {
      try (ImageInputStream var1 = ImageIO.createImageInputStream(new ByteArrayInputStream(encoded))) {
         if (var1 == null) {
            return null;
         }

         Iterator var2 = ImageIO.getImageReaders(var1);
         if (!var2.hasNext()) {
            return null;
         }

         ImageReader var3 = (ImageReader)var2.next();

         try {
            var3.setInput(var1, true, true);
            int var4 = var3.getWidth(0);
            int var5 = var3.getHeight(0);
            if (!isSafeSize(var4, var5)) {
               return null;
            }

            BufferedImage var6 = var3.read(0);
            return var6 != null && isSafeSize(var6.getWidth(), var6.getHeight()) ? var6 : null;
         } finally {
            var3.dispose();
         }
      } catch (Exception var16) {
         return null;
      }
   }

   private static boolean isSafeSize(int width, int height) {
      return width > 0 && height > 0 && width <= (4096) && height <= (4096) && (long)width * height <= 16777216L;
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

   private static final class Holder {
      static final HttpClient CLIENT = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).connectTimeout(Duration.ofSeconds(5L)).build();
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
