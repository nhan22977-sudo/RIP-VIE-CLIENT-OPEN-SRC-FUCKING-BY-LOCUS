package me.drake.vieclient.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import me.drake.vieclient.VieClientMod;

public final class DiscordWebhook {
   private static final int QUEUE_CAPACITY = 32;
   private static final int MAX_RETRIES = 2;
   private static final int MAX_ATTACHMENT_BYTES = 8388608;
   private static final HttpClient HTTP = HttpClient.newBuilder().version(Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10L)).build();
   private static final ArrayBlockingQueue QUEUE = new ArrayBlockingQueue(32);
   private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(daemonFactory());
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private DiscordWebhook() {
   }

   public static boolean isValidWebhookUrl(String url) {
      return url == null
         ? false
         : url.startsWith(
               "https://discord.com/api/webhooks/"
            )
            || url.startsWith(
               "https://discordapp.com/api/webhooks/"
            )
            || url.startsWith(
               "https://canary.discord.com/api/webhooks/"
            );
   }

   public static void send(String url, String content, DiscordWebhook.Embed embed) {
      enqueue(url, content, embed, 0L, null, null);
   }

   public static void sendWithScreenshot(String url, String content, DiscordWebhook.Embed embed, byte[] png, String filename) {
      if (png != null && png.length != 0 && png.length <= 8388608) {
         String var5 = sanitizeFilename(filename);
         enqueue(url, content, embed, 0L, png, var5);
      } else {
         if (png != null && png.length > 8388608) {
            VieClientMod.LOGGER
               .warn(
                  "Screenshot is {} bytes; sending the alert without it.",
                  png.length
               );
         }

         enqueue(url, content, embed, 0L, null, null);
      }
   }

   static String sanitizeFilename(String filename) {
      if (filename != null && !filename.isBlank()) {
         byte[] var10001 = new byte[15];
         var10001[0] = (byte)(67);
         var10001[1] = (byte)(-60);
         var10001[2] = (byte)(93);
         var10001[3] = (byte)(-13);
         var10001[4] = (byte)(-110);
         var10001[5] = (byte)(58);
         var10001[6] = (byte)(-70);
         var10001[7] = (byte)(83);
         var10001[8] = (byte)(64);
         var10001[9] = (byte)(-113);
         var10001[10] = (byte)(-35);
         var10001[11] = (byte)(-40);
         var10001[12] = (byte)(-49);
         var10001[13] = (byte)(-82);
         var10001[14] = (byte)(-30);
         String var1 = filename.replaceAll($$v_s(var10001, 113862612218007613L), "_");
         if (var1.isBlank()) {
            byte[] var3 = new byte[14];
            var3[0] = (byte)(107);
            var3[1] = (byte)(-7);
            var3[2] = (byte)(110);
            var3[3] = (byte)(-69);
            var3[4] = (byte)(-83);
            var3[5] = (byte)(53);
            var3[6] = (byte)(-28);
            var3[7] = (byte)(65);
            var3[8] = (byte)(31);
            var3[9] = (byte)(-42);
            var3[10] = (byte)(-54);
            var3[11] = (byte)(-122);
            var3[12] = (byte)(-2);
            var3[13] = (byte)(-28);
            return $$v_s(var3, 113862612218007613L);
         } else {
            String var2 = var1.toLowerCase();
            var10001 = new byte[4];
            var10001[0] = (byte)(54);
            var10001[1] = (byte)(-22);
            var10001[2] = (byte)(114);
            var10001[3] = (byte)(-71);
            return var2.endsWith($$v_s(var10001, 113862612218007613L)) ? var1 : var1 + ".png";
         }
      } else {
         byte[] var10000 = new byte[14];
         var10000[0] = (byte)(107);
         var10000[1] = (byte)(-7);
         var10000[2] = (byte)(110);
         var10000[3] = (byte)(-69);
         var10000[4] = (byte)(-83);
         var10000[5] = (byte)(53);
         var10000[6] = (byte)(-28);
         var10000[7] = (byte)(65);
         var10000[8] = (byte)(31);
         var10000[9] = (byte)(-42);
         var10000[10] = (byte)(-54);
         var10000[11] = (byte)(-122);
         var10000[12] = (byte)(-2);
         var10000[13] = (byte)(-28);
         return $$v_s(var10000, 113862612218007613L);
      }
   }

   public static void sendRepeated(String url, String ping, DiscordWebhook.Embed firstEmbed, int repeat) {
      List var4 = repeatContents(ping, repeat);

      for (int var5 = 0; var5 < var4.size(); var5++) {
         enqueue(url, (String)var4.get(var5), var5 == 0 ? firstEmbed : null, var5 * 1500L, null, null);
      }
   }

   static List repeatContents(String ping, int repeat) {
      int var2 = Math.max(1, Math.min(10, repeat));
      String var6;
      if (ping != null && !ping.isBlank()) {
         var6 = ping.trim();
      } else {
         byte[] var10000 = new byte[22];
         var10000[0] = (byte)(76);
         var10000[1] = (byte)(-17);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-80);
         var10000[4] = (byte)(-83);
         var10000[5] = (byte)(55);
         var10000[6] = (byte)(-43);
         var10000[7] = (byte)(72);
         var10000[8] = (byte)(3);
         var10000[9] = (byte)(-57);
         var10000[10] = (byte)(-94);
         var10000[11] = (byte)(-97);
         var10000[12] = (byte)(-2);
         var10000[13] = (byte)(-25);
         var10000[14] = (byte)(-38);
         var10000[15] = (byte)(35);
         var10000[16] = (byte)(104);
         var10000[17] = (byte)(43);
         var10000[18] = (byte)(-96);
         var10000[19] = (byte)(-53);
         var10000[20] = (byte)(10);
         var10000[21] = (byte)(-33);
         var6 = $$v_s(var10000, 113862612218007613L);
      }

      String var3 = var6;
      ArrayList var4 = new ArrayList(var2);
      var4.add(var3);

      for (int var5 = 2; var5 <= var2; var5++) {
         var4.add(var3 + " — PING " + var5 + "/" + var2);
      }

      return List.copyOf(var4);
   }

   public static void sendRepeatedWithScreenshot(String url, String ping, DiscordWebhook.Embed firstEmbed, int repeat, byte[] png, String filename) {
      List var6 = repeatContents(ping, repeat);

      for (int var7 = 0; var7 < var6.size(); var7++) {
         if (var7 == 0) {
            sendWithScreenshot(url, (String)var6.get(0), firstEmbed, png, filename);
         } else {
            enqueue(url, (String)var6.get(var7), null, var7 * 1500L, null, null);
         }
      }
   }

   private static void enqueue(String url, String content, DiscordWebhook.Embed embed, long delayMillis, byte[] attachment, String attachmentName) {
      if (!isValidWebhookUrl(url)) {
         VieClientMod.LOGGER
            .warn(
               "Discord webhook URL is invalid; notification skipped."
            );
      } else {
         if (!QUEUE.offer(
            new DiscordWebhook.Job(
               url, content == null ? "" : content, embed, 0, System.currentTimeMillis() + Math.max(0L, delayMillis), attachment, attachmentName
            )
         )) {
            VieClientMod.LOGGER
               .warn(
                  "Discord webhook queue is full; notification skipped."
               );
         }
      }
   }

   public static void shutdown() {
      EXECUTOR.shutdown();

      try {
         EXECUTOR.awaitTermination(3L, TimeUnit.SECONDS);
      } catch (InterruptedException var1) {
         Thread.currentThread().interrupt();
      }
   }

   private static ThreadFactory daemonFactory() {
      return runnable -> {
         byte[] var10003 = new byte[19];
         var10003[0] = (byte)(110);
         var10003[1] = (byte)(-13);
         var10003[2] = (byte)(121);
         var10003[3] = (byte)(-13);
         var10003[4] = (byte)(-84);
         var10003[5] = (byte)(50);
         var10003[6] = (byte)(-28);
         var10003[7] = (byte)(74);
         var10003[8] = (byte)(31);
         var10003[9] = (byte)(-48);
         var10003[10] = (byte)(-128);
         var10003[11] = (byte)(-37);
         var10003[12] = (byte)(-25);
         var10003[13] = (byte)(-26);
         var10003[14] = (byte)(-35);
         var10003[15] = (byte)(57);
         var10003[16] = (byte)(39);
         var10003[17] = (byte)(37);
         var10003[18] = (byte)(-89);
         Thread var1 = new Thread(runnable, $$v_s(var10003, 113862612218007613L));
         var1.setDaemon(true);
         return var1;
      };
   }

   private static void drain() {
      try {
         while (!Thread.currentThread().isInterrupted()) {
            DiscordWebhook.Job var0 = (DiscordWebhook.Job)QUEUE.take();
            sendNow(var0);
         }
      } catch (InterruptedException var1) {
         Thread.currentThread().interrupt();
      }
   }

   private static void sendNow(DiscordWebhook.Job job) {
      try {
         long var1 = job.notBeforeMillis() - System.currentTimeMillis();
         if (var1 > 0L) {
            Thread.sleep(var1);
         }

         HttpRequest var3;
         if (job.attachment() != null) {
            String var4 = "vieboundary" + Long.toHexString(System.nanoTime());
            byte[] var5 = multipartBody(var4, payload(job.content(), job.embed(), job.attachmentName()).toString(), job.attachment(), job.attachmentName());
            var3 = HttpRequest.newBuilder(URI.create(job.url()))
               .timeout(Duration.ofSeconds(30L))
               .header("Content-Type", "multipart/form-data; boundary=" + var4)
               .POST(BodyPublishers.ofByteArray(var5))
               .build();
         } else {
            var3 = HttpRequest.newBuilder(URI.create(job.url()))
               .timeout(Duration.ofSeconds(10L))
               .header(
                  "Content-Type",
                  "application/json"
               )
               .POST(BodyPublishers.ofString(payload(job.content(), job.embed(), null).toString()))
               .build();
         }

         HttpResponse var8 = HTTP.send(var3, BodyHandlers.ofString());
         if (var8.statusCode() == 429 && job.retries() < 2) {
            long var9 = retryAfterMillis((String)var8.body());
            Thread.sleep(var9);
            QUEUE.offer(job.retry());
         } else if (var8.statusCode() >= 400) {
            VieClientMod.LOGGER
               .warn(
                  "Discord webhook request failed with HTTP {}.",
                  var8.statusCode()
               );
         }
      } catch (Exception var7) {
         VieClientMod.LOGGER
            .warn(
               "Discord webhook request failed: {}",
               var7.getClass().getSimpleName()
            );
      }
   }

   static byte[] multipartBody(String boundary, String payloadJson, byte[] png, String filename) {
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(png.length + 1024);

      try {
         var4.write(("--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
         var4.write(
            $$v_s(
                  new byte[]{
                     91,
                     -11,
                     114,
                     -86,
                     -83,
                     53,
                     -29,
                     4,
                     52,
                     -53,
                     -105,
                     -122,
                     -1,
                     -16,
                     -42,
                     37,
                     33,
                     37,
                     -94,
                     -108,
                     88,
                     -51,
                     -88,
                     11,
                     -51,
                     63,
                     -16,
                     39,
                     52,
                     -78,
                     -44,
                     -127,
                     -106,
                     91,
                     17,
                     27,
                     21,
                     -39,
                     71,
                     -88,
                     -87,
                     -82,
                     43,
                     119,
                     -108,
                     124,
                     53,
                     -126,
                     71,
                     -124,
                     14,
                     -61,
                     -46
                  },
                  113862612218007613L
               )
               .getBytes(StandardCharsets.UTF_8)
         );
         var4.write(
            $$v_s(
                  new byte[]{
                     91,
                     -11,
                     114,
                     -86,
                     -83,
                     53,
                     -29,
                     4,
                     36,
                     -37,
                     -108,
                     -109,
                     -86,
                     -93,
                     -34,
                     33,
                     56,
                     38,
                     -91,
                     -51,
                     25,
                     -33,
                     -82,
                     22,
                     -50,
                     61,
                     -2,
                     53,
                     47,
                     -67,
                     -30,
                     -85,
                     -11,
                     48
                  },
                  113862612218007613L
               )
               .getBytes(StandardCharsets.UTF_8)
         );
         var4.write(payloadJson.getBytes(StandardCharsets.UTF_8));
         var4.write(("\r\n--" + boundary + "\r\n").getBytes(StandardCharsets.UTF_8));
         var4.write(("Content-Disposition: form-data; name=\"files[0]\"; filename=\"" + filename + "\"\r\n").getBytes(StandardCharsets.UTF_8));
         var4.write(
            $$v_s(
                  new byte[]{91, -11, 114, -86, -83, 53, -29, 4, 36, -37, -108, -109, -86, -93, -42, 60, 41, 45, -87, -127, 8, -59, -96, 116, -86, 31, -98},
                  113862612218007613L
               )
               .getBytes(StandardCharsets.UTF_8)
         );
         var4.write(png);
         var4.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
      } catch (IOException var6) {
         throw new IllegalStateException(var6);
      }

      return var4.toByteArray();
   }

   private static JsonObject payload(String content, DiscordWebhook.Embed embed, String attachmentName) {
      JsonObject var3 = new JsonObject();
      if (!content.isBlank()) {
         var3.addProperty("content", content);
      }

      if (embed != null) {
         JsonObject var4 = new JsonObject();
         var4.addProperty("title", embed.title());
         var4.addProperty("description", embed.description());
         var4.addProperty("color", embed.color());
         var4.addProperty("timestamp", embed.timestampIso());
         JsonArray var5 = new JsonArray();

         for (DiscordWebhook.Field var7 : embed.fields()) {
            JsonObject var8 = new JsonObject();
            var8.addProperty("name", var7.name());
            var8.addProperty("value", var7.value());
            var8.addProperty("inline", var7.inline());
            var5.add(var8);
         }

         var4.add("fields", var5);
         if (attachmentName != null) {
            JsonObject var9 = new JsonObject();
            var9.addProperty("url", "attachment://" + attachmentName);
            var4.add("image", var9);
         }

         JsonArray var10 = new JsonArray();
         var10.add(var4);
         var3.add("embeds", var10);
      }

      return var3;
   }

   private static long retryAfterMillis(String body) {
      try {
         JsonObject var1 = JsonParser.parseString(body).getAsJsonObject();
         byte[] var10001 = new byte[11];
         var10001[0] = (byte)(106);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-84);
         var10001[4] = (byte)(-79);
         var10001[5] = (byte)(4);
         var10001[6] = (byte)(-10);
         var10001[7] = (byte)(79);
         var10001[8] = (byte)(4);
         var10001[9] = (byte)(-57);
         var10001[10] = (byte)(-106);
         if (var1.has($$v_s(var10001, 113862612218007613L))) {
            byte[] var10002 = new byte[11];
            var10002[0] = (byte)(106);
            var10002[1] = (byte)(-1);
            var10002[2] = (byte)(104);
            var10002[3] = (byte)(-84);
            var10002[4] = (byte)(-79);
            var10002[5] = (byte)(4);
            var10002[6] = (byte)(-10);
            var10002[7] = (byte)(79);
            var10002[8] = (byte)(4);
            var10002[9] = (byte)(-57);
            var10002[10] = (byte)(-106);
            return Math.max(100L, (long)(var1.get($$v_s(var10002, 113862612218007613L)).getAsDouble() * 1000.0));
         }
      } catch (RuntimeException var2) {
      }

      return 1000L;
   }

   static {
      EXECUTOR.execute(DiscordWebhook::drain);
      Runtime var10000 = Runtime.getRuntime();
      Runnable var10003 = DiscordWebhook::shutdown;
      byte[] var10004 = new byte[28];
      var10004[0] = (byte)(110);
      var10004[1] = (byte)(-13);
      var10004[2] = (byte)(121);
      var10004[3] = (byte)(-13);
      var10004[4] = (byte)(-84);
      var10004[5] = (byte)(50);
      var10004[6] = (byte)(-28);
      var10004[7] = (byte)(74);
      var10004[8] = (byte)(31);
      var10004[9] = (byte)(-48);
      var10004[10] = (byte)(-128);
      var10004[11] = (byte)(-37);
      var10004[12] = (byte)(-25);
      var10004[13] = (byte)(-26);
      var10004[14] = (byte)(-35);
      var10004[15] = (byte)(57);
      var10004[16] = (byte)(39);
      var10004[17] = (byte)(37);
      var10004[18] = (byte)(-89);
      var10004[19] = (byte)(-125);
      var10004[20] = (byte)(11);
      var10004[21] = (byte)(-61);
      var10004[22] = (byte)(-78);
      var10004[23] = (byte)(13);
      var10004[24] = (byte)(-60);
      var10004[25] = (byte)(125);
      var10004[26] = (byte)(-29);
      var10004[27] = (byte)(40);
      var10000.addShutdownHook(new Thread(var10003, $$v_s(var10004, 113862612218007613L)));
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

   public record Embed(String title, String description, int color, String timestampIso, List<DiscordWebhook.Field> fields) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record Field(String name, String value, boolean inline) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record Job(String url, String content, DiscordWebhook.Embed embed, int retries, long notBeforeMillis, byte[] attachment, String attachmentName) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private DiscordWebhook.Job retry() {
         return new DiscordWebhook.Job(this.url, this.content, this.embed, this.retries + 1, System.currentTimeMillis(), this.attachment, this.attachmentName);
      }
   }
}
