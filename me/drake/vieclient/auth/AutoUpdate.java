package me.drake.vieclient.auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Collections;
import java.util.Enumeration;
import me.drake.vieclient.CoreEntry;
import me.drake.vieclient.security.ChimeraEngine;
import org.slf4j.Logger;

public final class AutoUpdate {
   private static final String TAG = "[VieClient-Update]";
   private static final String API_BASE = "https://vieservices.top/api";
   private static final String CACHE_DIR_NAME = ".vieclient_cache";
   private static final Duration TIMEOUT = Duration.ofSeconds(15L);
   private static final String PROP_ENABLED = "vieclient.autoUpdate";
   private static final String ENV_ENABLED = "VIE_AUTO_UPDATE";
   private final HttpClient httpClient;
   private final ChimeraEngine chimera;
   private final Gson gson;
   private boolean enabled = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoUpdate() {
      this.httpClient = HttpClient.newBuilder().version(Version.HTTP_1_1).connectTimeout(TIMEOUT).followRedirects(Redirect.NORMAL).build();
      this.gson = new Gson();
      this.chimera = buildChimeraEngine();
      String var1 = System.getenv("VIE_AUTO_UPDATE");
      if (var1 != null) {
         this.enabled = !var1.equals("0") && !var1.equalsIgnoreCase("false");
      }

      String var2 = System.getProperty(
         "vieclient.autoUpdate"
      );
      if (var2 == null) {
         var2 = System.getProperty(
            "viecheats.autoUpdate"
         );
      }

      if (var2 != null) {
         this.enabled = !var2.equals("0") && !var2.equalsIgnoreCase("false");
      }
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean on) {
      this.enabled = on;
   }

   public void checkAndApply() {
      if (!this.enabled) {
         CoreEntry.LOGGER
            .info(
               "{} Auto-update disabled by config",
               "[VieClient-Update]"
            );
      } else if (this.chimera == null) {
         CoreEntry.LOGGER
            .warn(
               "{} VIE_CHIMERA_KEY is not configured — skip update",
               "[VieClient-Update]"
            );
      } else {
         try {
            String var1 = findLicenseKey();
            if (var1 == null || var1.isEmpty()) {
               CoreEntry.LOGGER
                  .warn(
                     "{} No license key — skip update",
                     "[VieClient-Update]"
                  );
               return;
            }

            String var2 = detectPlatform();
            String var3 = computeHwid();
            String var4 = "1.0.1";
            CoreEntry.LOGGER
               .info(
                  "{} Checking for updates (platform={} ver={})...",
                  new Object[]{
                     "[VieClient-Update]", var2, var4
                  }
               );
            JsonObject var5 = new JsonObject();
            var5.addProperty("hwid", var3);
            var5.addProperty("key", var1);
            var5.addProperty("platform", var2);
            var5.addProperty("version", var4);
            ChimeraEngine.GenomePacket var6 = this.chimera.encode(this.gson.toJson(var5));
            JsonObject var7 = new JsonObject();
            var7.addProperty("sequence", var6.sequence());
            JsonObject var8 = new JsonObject();
            var8.addProperty("locus", var6.locus());
            var8.addProperty(
               "protocol",
               "CHIMERA-PROTECT"
            );
            var7.add("metadata", var8);
            JsonObject var9 = new JsonObject();
            var9.add("genome_packet", var7);
            HttpRequest var10 = HttpRequest.newBuilder()
               .uri(
                  URI.create(
                     "https://vieservices.top/api/v1/update/check"
                  )
               )
               .header(
                  "Content-Type",
                  "application/json"
               )
               .header(
                  "X-Chimera-Protocol",
                  "CHIMERA-PROTECT"
               )
               .timeout(TIMEOUT)
               .POST(BodyPublishers.ofString(this.gson.toJson(var9), StandardCharsets.UTF_8))
               .build();
            HttpResponse var11 = this.httpClient.send(var10, BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (var11.statusCode() != 200) {
               CoreEntry.LOGGER
                  .warn(
                     "{} API returned {}",
                     "[VieClient-Update]",
                     var11.statusCode()
                  );
               return;
            }

            String var12 = (String)var11.body();
            JsonObject var13 = JsonParser.parseString(var12).getAsJsonObject();
            String var14;
            if (var13.has("genome_packet")) {
               JsonObject var15 = var13.getAsJsonObject("genome_packet");
               String var16 = var15.get("sequence").getAsString();
               String var17 = var15.getAsJsonObject("metadata")
                  .get("locus")
                  .getAsString();
               var14 = this.chimera.decode(new ChimeraEngine.GenomePacket(var16, var17));
            } else {
               var14 = var12;
            }

            JsonObject var20 = JsonParser.parseString(var14).getAsJsonObject();
            boolean var21 = var20.get("updateAvailable")
               .getAsBoolean();
            if (!var21) {
               CoreEntry.LOGGER
                  .info(
                     "{} Already latest ({})",
                     "[VieClient-Update]",
                     var4
                  );
               return;
            }

            String var22 = var20.get("downloadUrl").getAsString();
            String var18 = var20.get("version").getAsString();
            CoreEntry.LOGGER
               .info(
                  "{} Update available: {} -> {}",
                  new Object[]{
                     "[VieClient-Update]", var4, var18
                  }
               );
            this.downloadFile(var22, var2);
            saveVersion(var18);
            CoreEntry.LOGGER
               .info(
                  "{} Update applied — restart to use new version",
                  "[VieClient-Update]"
               );
         } catch (Exception var19) {
            CoreEntry.LOGGER
               .warn(
                  "{} Failed: {}",
                  "[VieClient-Update]",
                  var19.getMessage()
               );
         }
      }
   }

   private void downloadFile(String url, String platform) throws Exception {
      HttpRequest var3 = HttpRequest.newBuilder().uri(URI.create(url)).timeout(TIMEOUT).GET().build();
      Path var4 = getCacheDir().resolve(fileNameFor(platform));
      this.httpClient.send(var3, BodyHandlers.ofFile(var4));
      Logger var10000 = CoreEntry.LOGGER;
      byte[] var10001 = new byte[17];
      var10001[0] = (byte)(31);
      var10001[1] = (byte)(-25);
      var10001[2] = (byte)(60);
      var10001[3] = (byte)(-102);
      var10001[4] = (byte)(126);
      var10001[5] = (byte)(-23);
      var10001[6] = (byte)(-114);
      var10001[7] = (byte)(11);
      var10001[8] = (byte)(99);
      var10001[9] = (byte)(-61);
      var10001[10] = (byte)(-128);
      var10001[11] = (byte)(-109);
      var10001[12] = (byte)(45);
      var10001[13] = (byte)(124);
      var10001[14] = (byte)(-24);
      var10001[15] = (byte)(100);
      var10001[16] = (byte)(73);
      String var5 = $$v_s(var10001, 5756515840257413185L);
      byte[] var10002 = new byte[18];
      var10002[0] = (byte)(63);
      var10002[1] = (byte)(-52);
      var10002[2] = (byte)(117);
      var10002[3] = (byte)(-69);
      var10002[4] = (byte)(82);
      var10002[5] = (byte)(-14);
      var10002[6] = (byte)(-119);
      var10002[7] = (byte)(2);
      var10002[8] = (byte)(98);
      var10002[9] = (byte)(-42);
      var10002[10] = (byte)(-55);
      var10002[11] = (byte)(-93);
      var10002[12] = (byte)(57);
      var10002[13] = (byte)(34);
      var10002[14] = (byte)(-87);
      var10002[15] = (byte)(107);
      var10002[16] = (byte)(81);
      var10002[17] = (byte)(23);
      var10000.info(var5, $$v_s(var10002, 5756515840257413185L), var4);
   }

   private static String fileNameFor(String platform) {
      switch (platform) {
         case "ios":
            return "data-ios.bin";
         case "android":
            return "data-arm64.bin";
         case "windows":
            return "data.bin";
         case "macos":
            return "data.bin";
         case "linux":
            return "data.bin";
         default:
            return "data.bin";
      }
   }

   private static Path getCacheDir() {
      byte[] var10000 = new byte[9];
      var10000[0] = (byte)(17);
      var10000[1] = (byte)(-23);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-84);
      var10000[4] = (byte)(63);
      var10000[5] = (byte)(-10);
      var10000[6] = (byte)(-113);
      var10000[7] = (byte)(10);
      var10000[8] = (byte)(105);
      String var3 = System.getProperty($$v_s(var10000, 5756515840257413185L), ".");
      String[] var10001 = new String[1];
      byte[] var10004 = new byte[16];
      var10004[0] = (byte)(74);
      var10004[1] = (byte)(-20);
      var10004[2] = (byte)(117);
      var10004[3] = (byte)(-69);
      var10004[4] = (byte)(114);
      var10004[5] = (byte)(-14);
      var10004[6] = (byte)(-119);
      var10004[7] = (byte)(2);
      var10004[8] = (byte)(98);
      var10004[9] = (byte)(-42);
      var10004[10] = (byte)(-69);
      var10004[11] = (byte)(-107);
      var10004[12] = (byte)(40);
      var10004[13] = (byte)(37);
      var10004[14] = (byte)(-96);
      var10004[15] = (byte)(122);
      var10001[0] = $$v_s(var10004, 5756515840257413185L);
      Path var0 = Paths.get(var3, var10001);

      try {
         Files.createDirectories(var0);
      } catch (IOException var2) {
      }

      return var0;
   }

   private static void saveVersion(String ver) {
      try {
         Path var10000 = getCacheDir();
         byte[] var10001 = new byte[13];
         var10001[0] = (byte)(6);
         var10001[1] = (byte)(-17);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-78);
         var10001[4] = (byte)(117);
         var10001[5] = (byte)(-63);
         var10001[6] = (byte)(-106);
         var10001[7] = (byte)(2);
         var10001[8] = (byte)(126);
         var10001[9] = (byte)(-47);
         var10001[10] = (byte)(-115);
         var10001[11] = (byte)(-103);
         var10001[12] = (byte)(39);
         Files.writeString(var10000.resolve($$v_s(var10001, 5756515840257413185L)), ver);
      } catch (IOException var2) {
      }
   }

   private static String computeHwid() {
      try {
         StringBuilder var0 = new StringBuilder();
         var0.append(System.getProperty("user.name", ""));
         var0.append(System.getProperty("os.name", ""));
         var0.append(System.getProperty("os.arch", ""));

         try {
            Enumeration var1 = NetworkInterface.getNetworkInterfaces();
            if (var1 != null) {
               for (NetworkInterface var3 : Collections.list(var1)) {
                  byte[] var4 = var3.getHardwareAddress();
                  if (var4 != null && var4.length > 0) {
                     for (byte var8 : var4) {
                        var0.append(String.format("%02X", var8));
                     }
                     break;
                  }
               }
            }
         } catch (Exception var9) {
         }

         MessageDigest var11 = MessageDigest.getInstance("SHA-256");
         byte[] var12 = var11.digest(var0.toString().getBytes("UTF-8"));
         StringBuilder var13 = new StringBuilder();

         for (byte var17 : var12) {
            var13.append(String.format("%02x", var17));
         }

         return var13.toString();
      } catch (Exception var10) {
         return "unknown_" + System.nanoTime();
      }
   }

   private static String findLicenseKey() {
      try {
         String var0 = getClassSourcePath();
         if (var0 != null) {
            String var1 = new File(var0).getName();
            if (var1.matches(
               "VIE-[A-Z]+-[A-F0-9]{6}\\.jar"
            )) {
               return var1.replaceAll(
                  ".*-(.{6})\\.jar",
                  "$1"
               );
            }
         }
      } catch (Exception var5) {
      }

      try {
         File var6 = new File("license.txt");
         if (var6.exists()) {
            return Files.readString(var6.toPath()).trim();
         }
      } catch (Exception var4) {
      }

      try {
         File var7 = new File(
            "vieclient/auth.json"
         );
         if (!var7.exists()) {
            var7 = new File("viecheats/auth.json");
         }

         if (var7.exists()) {
            String var8 = Files.readString(var7.toPath());
            JsonObject var2 = JsonParser.parseString(var8).getAsJsonObject();
            if (var2.has("key")) {
               return var2.get("key").getAsString();
            }
         }
      } catch (Exception var3) {
      }

      return null;
   }

   private static String getClassSourcePath() {
      try {
         return AutoUpdate.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
      } catch (Exception var1) {
         return null;
      }
   }

   private static String detectPlatform() {
      String var0 = System.getProperty("os.name", "").toLowerCase();
      if (!var0.contains("iphone")
         && !var0.contains("ios")) {
         if (System.getProperty("java.vm.vendor", "")
            .toLowerCase()
            .contains("android")) {
            return "android";
         } else if (var0.contains("win")) {
            return "windows";
         } else if (var0.contains("mac")
            || var0.contains("darwin")) {
            return "macos";
         } else {
            return !var0.contains("nix")
                  && !var0.contains("nux")
                  && !var0.contains("aix")
               ? "unknown"
               : "linux";
         }
      } else {
         return "ios";
      }
   }

   private static ChimeraEngine buildChimeraEngine() {
      return ChimeraEngine.configuredMasterKey().map(ChimeraEngine::new).orElse(null);
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
