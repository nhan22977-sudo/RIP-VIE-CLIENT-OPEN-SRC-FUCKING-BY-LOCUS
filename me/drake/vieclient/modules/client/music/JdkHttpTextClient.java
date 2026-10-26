package me.drake.vieclient.modules.client.music;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public final class JdkHttpTextClient implements HttpTextClient {
   private static final int MAX_BODY_BYTES = 4194304;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   @Override
   public String get(String url) {
      try {
         HttpRequest var2 = HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(8L))
            .header(
               "User-Agent",
               "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
            )
            .header(
               "Accept-Language",
               "en-US,en;q=0.9"
            )
            .GET()
            .build();
         HttpResponse var3 = JdkHttpTextClient.Holder.CLIENT.send(var2, BodyHandlers.ofInputStream());
         if (var3.statusCode() >= 200 && var3.statusCode() < 300) {
            byte[] var4;
            try (InputStream var5 = (InputStream)var3.body()) {
               var4 = var5.readNBytes(4194304);
            }

            return new String(var4, StandardCharsets.UTF_8);
         } else {
            ((InputStream)var3.body()).close();
            throw new IllegalStateException("HTTP " + var3.statusCode());
         }
      } catch (InterruptedException var10) {
         Thread.currentThread().interrupt();
         throw new IllegalStateException(
            "HTTP interrupted", var10
         );
      } catch (IllegalStateException var11) {
         throw var11;
      } catch (Exception var12) {
         throw new IllegalStateException(
            "HTTP request failed", var12
         );
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

   private static final class Holder {
      static final HttpClient CLIENT = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).connectTimeout(Duration.ofSeconds(5L)).build();
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
