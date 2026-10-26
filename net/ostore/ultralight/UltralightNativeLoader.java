package net.ostore.ultralight;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class UltralightNativeLoader {
   private static final Logger LOG = LoggerFactory.getLogger("ultralight/loader");
   static final String SDK_DIR_NAME = "ultralight-1.4";
   private static final String SDK_VERSION = "1.4.0";
   private static final String PACK_REV = "2";
   private static final String RELEASE_BASE = System.getProperty(
      "ultralight.natives.url", "https://github.com/Ostores/ultralight-fabric/releases/download/natives-1.4.0/"
   );
   private static final String VERSION_FILE = ".ul-natives-version";
   private static volatile boolean loaded = false;
   private static Path loadedSdkDir = null;

   private UltralightNativeLoader() {
   }

   static Path getSdkDir() {
      return loadedSdkDir;
   }

   static boolean isLoaded() {
      return loaded;
   }

   static Path load() {
      if (loaded) {
         return loadedSdkDir;
      }

      String var0 = detectPlatform();
      if (var0 == null) {
         LOG.error("[ul] Plateforme non supportée : {}", System.getProperty("os.name"));
         return null;
      }

      Path var1 = FabricLoader.getInstance().getGameDir().resolve("ultralight-1.4");
      Path var2 = var1.resolve("bin");
      if (!ensureNatives(var0, var1, var2)) {
         return null;
      }

      try {
         Path var3 = var2.resolve(jniLibName(var0));
         if (Files.isRegularFile(var3)) {
            System.setProperty("ultralight.native.path", var3.toAbsolutePath().toString());
         }

         preloadBundledDeps(var0, var2);
         me.ayydxn.luminescence.internal.UltralightNativeLoader.load(var2.toAbsolutePath());
      } catch (Throwable var4) {
         LOG.error("[ul] Chargement des natifs Ultralight 1.4 / Luminescence échoué", var4);
         return null;
      }

      loadedSdkDir = var1;
      loaded = true;
      LOG.info("[ul] Natifs Ultralight 1.4 (WebKit 615) chargés [{}].", var0);
      return var1;
   }

   private static boolean ensureNatives(String platform, Path sdkDir, Path binDir) {
      Path var3 = sdkDir.resolve(".ul-natives-version");
      String var4 = "1.4.0-" + platform + "-r2";

      try {
         if (Files.isRegularFile(var3) && var4.equals(Files.readString(var3, StandardCharsets.UTF_8).trim()) && nativesComplete(platform, binDir)) {
            return true;
         }
      } catch (Exception var13) {
      }

      if (nativesComplete(platform, binDir)) {
         LOG.info("[ul] Natifs déjà présents dans {} — téléchargement ignoré.", binDir);
         writeMarker(var3, var4);
         return true;
      }

      String var5 = RELEASE_BASE + "ultralight-natives-" + platform + ".zip";
      LOG.info("[ul] Téléchargement des natifs Ultralight 1.4 [{}] depuis {} …", platform, var5);

      try {
         Files.createDirectories(sdkDir);
         Path var6 = Files.createTempFile("ul-natives-", ".zip");

         try {
            download(var5, var6);
            unzip(var6, sdkDir);
         } finally {
            Files.deleteIfExists(var6);
         }

         if (!nativesComplete(platform, binDir)) {
            LOG.error("[ul] Pack téléchargé invalide (SDK Ultralight ou JNI absent) pour {}.", platform);
            return false;
         } else {
            writeMarker(var3, var4);
            LOG.info("[ul] Natifs Ultralight 1.4 [{}] installés.", platform);
            return true;
         }
      } catch (Throwable var12) {
         LOG.error(
            "[ul] Téléchargement/extraction des natifs échoué ({}). Vérifie ta connexion ou place les natifs dans {} manuellement.", var12.getMessage(), binDir
         );
         return false;
      }
   }

   private static void download(String url, Path dest) throws Exception {
      HttpClient var2 = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build();
      HttpRequest var3 = HttpRequest.newBuilder(URI.create(url)).GET().build();
      HttpResponse var4 = var2.send(var3, BodyHandlers.ofFile(dest));
      if (var4.statusCode() != 200) {
         throw new IllegalStateException("HTTP " + var4.statusCode() + " pour " + url);
      }
   }

   private static void unzip(Path zip, Path destDir) throws Exception {
      Path var2 = destDir.toAbsolutePath().normalize();

      ZipEntry var5;
      try (
         InputStream var3 = Files.newInputStream(zip);
         ZipInputStream var4 = new ZipInputStream(var3);
      ) {
         for (; (var5 = var4.getNextEntry()) != null; var4.closeEntry()) {
            Path var6 = var2.resolve(var5.getName()).normalize();
            if (!var6.startsWith(var2)) {
               throw new IllegalStateException("Zip slip: " + var5.getName());
            }

            if (var5.isDirectory()) {
               Files.createDirectories(var6);
            } else {
               Files.createDirectories(var6.getParent());
               Files.copy(var4, var6, StandardCopyOption.REPLACE_EXISTING);
            }
         }
      }
   }

   private static void writeMarker(Path marker, String tag) {
      try {
         Files.writeString(marker, tag, StandardCharsets.UTF_8);
      } catch (Exception var3) {
      }
   }

   private static void preloadBundledDeps(String platform, Path binDir) {
      if (!platform.startsWith("windows")) {
         String var2 = platform.startsWith("macos") ? ".dylib" : ".so";
         List var3 = List.of("libWebCore", "libUltralight", "libUltralightCore", "libAppCore", "libLuminescenceJNI");
         ArrayList var4 = new ArrayList();

         try (Stream var5 = Files.list(binDir)) {
            var5.filter(x$0 -> Files.isRegularFile(x$0)).forEach(p -> {
               String var4x = p.getFileName().toString();
               int var5x = var4x.indexOf(var2);
               if (var5x >= 0) {
                  String var6x = var4x.substring(0, var5x);
                  if (!var3.contains(var6x)) {
                     var4.add(p);
                  }
               }
            });
         } catch (Exception var14) {
            return;
         }

         if (!var4.isEmpty()) {
            ArrayList var15 = new ArrayList(var4);
            boolean var6 = true;

            while (var6 && !var15.isEmpty()) {
               var6 = false;
               ArrayList var7 = new ArrayList();

               for (Path var9 : var15) {
                  try {
                     System.load(var9.toAbsolutePath().toString());
                     var6 = true;
                  } catch (Throwable var12) {
                     var7.add(var9);
                  }
               }

               var15 = var7;
            }

            int var16 = var4.size() - var15.size();
            if (var16 > 0) {
               LOG.info("[ul] {} lib(s) d'appoint préchargée(s) depuis bin/.", var16);
            }

            if (!var15.isEmpty()) {
               LOG.warn("[ul] Libs d'appoint non préchargées : {}", var15);
            }
         }
      }
   }

   private static String jniLibName(String platform) {
      if (platform.startsWith("windows")) {
         return "LuminescenceJNI.dll";
      } else {
         return platform.startsWith("macos") ? "libLuminescenceJNI.dylib" : "libLuminescenceJNI.so";
      }
   }

   private static String sdkCoreLibName(String platform) {
      if (platform.startsWith("windows")) {
         return "WebCore.dll";
      } else {
         return platform.startsWith("macos") ? "libWebCore.dylib" : "libWebCore.so";
      }
   }

   private static boolean nativesComplete(String platform, Path binDir) {
      return Files.isRegularFile(binDir.resolve(jniLibName(platform))) && Files.isRegularFile(binDir.resolve(sdkCoreLibName(platform)));
   }

   private static String detectPlatform() {
      String var0 = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
      String var1 = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
      boolean var2 = var1.contains("aarch64") || var1.contains("arm");
      if (var0.contains("win")) {
         return "windows-x64";
      } else if (var0.contains("mac")) {
         return var2 ? "macos-arm64" : "macos-x64";
      } else if (!var0.contains("nux") && !var0.contains("nix")) {
         return null;
      } else {
         return var2 ? "linux-arm64" : "linux-x64";
      }
   }
}
