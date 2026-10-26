package me.drake.vieclient.render.nvg;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashSet;
import java.util.Locale;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.system.Configuration;

public final class NanoVGNatives {
   private static boolean done = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private NanoVGNatives() {
   }

   public static synchronized void ensureLoaded() {
      if (!done) {
         done = true;

         try {
            byte[] var4 = new byte[21];
            var4[0] = (byte)(13);
            var4[1] = (byte)(-23);
            var4[2] = (byte)(101);
            var4[3] = (byte)(-83);
            var4[4] = (byte)(-88);
            var4[5] = (byte)(118);
            var4[6] = (byte)(113);
            var4[7] = (byte)(-25);
            var4[8] = (byte)(38);
            var4[9] = (byte)(-53);
            var4[10] = (byte)(-122);
            var4[11] = (byte)(-64);
            var4[12] = (byte)(-80);
            var4[13] = (byte)(-28);
            var4[14] = (byte)(88);
            var4[15] = (byte)(-39);
            var4[16] = (byte)(16);
            var4[17] = (byte)(39);
            var4[18] = (byte)(-30);
            var4[19] = (byte)(-35);
            var4[20] = (byte)(3);
            System.load($$v_s(var4, -2296891450011168761L));
         } catch (Throwable var3) {
            try {
               byte[] var10000 = new byte[19];
               var10000[0] = (byte)(13);
               var10000[1] = (byte)(-23);
               var10000[2] = (byte)(101);
               var10000[3] = (byte)(-83);
               var10000[4] = (byte)(-88);
               var10000[5] = (byte)(118);
               var10000[6] = (byte)(113);
               var10000[7] = (byte)(-25);
               var10000[8] = (byte)(38);
               var10000[9] = (byte)(-53);
               var10000[10] = (byte)(-122);
               var10000[11] = (byte)(-39);
               var10000[12] = (byte)(-24);
               var10000[13] = (byte)(-94);
               var10000[14] = (byte)(86);
               var10000[15] = (byte)(-35);
               var10000[16] = (byte)(92);
               var10000[17] = (byte)(57);
               var10000[18] = (byte)(-93);
               System.load($$v_s(var10000, -2296891450011168761L));
            } catch (Throwable var2) {
            }
         }

         loadDesktopNanoVGLibrary();
      }
   }

   private static void loadDesktopNanoVGLibrary() {
      String var0 = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
      String var1 = System.getProperty("os.arch", "").toLowerCase(Locale.ROOT);
      boolean var2 = var1.contains("aarch64")
         || var1.contains("arm64");
      String var3;
      String var4;
      if (var0.contains("win")) {
         var3 = "lwjgl_nanovg.dll";
         var4 = var2
            ? "windows/arm64/org/lwjgl/nanovg/lwjgl_nanovg.dll"
            : "windows/x64/org/lwjgl/nanovg/lwjgl_nanovg.dll";
      } else if (!var0.contains("mac")
         && !var0.contains("darwin")) {
         var3 = "liblwjgl_nanovg.so";
         var4 = var2
            ? "linux/arm64/org/lwjgl/nanovg/liblwjgl_nanovg.so"
            : "linux/x64/org/lwjgl/nanovg/liblwjgl_nanovg.so";
      } else {
         var3 = "liblwjgl_nanovg.dylib";
         var4 = var2
            ? "macos/arm64/org/lwjgl/nanovg/liblwjgl_nanovg.dylib"
            : "macos/x64/org/lwjgl/nanovg/liblwjgl_nanovg.dylib";
      }

      Path var5 = null;

      try {
         Path var6 = FabricLoader.getInstance().getGameDir();
         var5 = var6.resolve(".vieclient")
            .resolve("natives");
         if (!Files.exists(var5)) {
            Files.createDirectories(var5);
         }
      } catch (Throwable var18) {
         try {
            var5 = Path.of(System.getProperty("java.io.tmpdir"))
               .resolve("vie-natives");
            Files.createDirectories(var5);
         } catch (Throwable var17) {
         }
      }

      if (var5 != null) {
         Path var24 = var5.resolve(var3);
         if (!Files.exists(var24) || tryGetSize(var24) <= 0L) {
            try (InputStream var7 = NanoVGNatives.class.getClassLoader().getResourceAsStream(var4)) {
               if (var7 != null) {
                  Files.copy(var7, var24, StandardCopyOption.REPLACE_EXISTING);
                  System.out.println("[NanoVG] Extracted " + var3 + " from bundle resources (" + Files.size(var24) + " bytes)");
               }
            } catch (Throwable var23) {
               System.err.println("[NanoVG] Warning extracting native from resource: " + var23.getMessage());
            }
         }

         if (Files.exists(var24) && tryGetSize(var24) > 0L) {
            String var25 = var5.toAbsolutePath().toString();
            String var8 = System.getProperty(
               "java.library.path", ""
            );

            for (String var12 : var8.split(File.pathSeparator)) {
               if (var12 != null && !var12.isBlank()) {
                  try {
                     Path var13 = Path.of(var12.trim());
                     if (Files.isDirectory(var13) && Files.isWritable(var13)) {
                        Path var14 = var13.resolve(var3);
                        if (!Files.exists(var14) || Files.size(var14) <= 0L) {
                           Files.copy(var24, var14, StandardCopyOption.REPLACE_EXISTING);
                           System.out.println("[NanoVG] Deployed native to java.library.path: " + var14);
                        }
                     }
                  } catch (Throwable var20) {
                  }
               }
            }

            try {
               String var26 = (String)Configuration.LIBRARY_PATH.get();
               LinkedHashSet var27 = new LinkedHashSet();
               var27.add(var25);
               if (var26 != null && !var26.isBlank()) {
                  for (String var35 : var26.split(File.pathSeparator)) {
                     if (!var35.isBlank()) {
                        var27.add(var35.trim());
                     }
                  }
               }

               if (!var8.isBlank()) {
                  for (String var36 : var8.split(File.pathSeparator)) {
                     if (!var36.isBlank()) {
                        var27.add(var36.trim());
                     }
                  }
               }

               String var30 = String.join(File.pathSeparator, var27);
               Configuration.LIBRARY_PATH.set(var30);
               System.setProperty(
                  "org.lwjgl.librarypath",
                  var30
               );
            } catch (Throwable var19) {
            }

            try {
               System.load(var24.toAbsolutePath().toString());
               System.out.println("[NanoVG] Native library loaded successfully: " + var24.toAbsolutePath());
               return;
            } catch (Throwable var21) {
               System.err.println("[NanoVG] Warning loading native via System.load: " + var21.getMessage());
            }
         }
      }

      try {
         System.loadLibrary("lwjgl_nanovg");
      } catch (Throwable var15) {
      }
   }

   private static long tryGetSize(Path path) {
      try {
         return Files.size(path);
      } catch (Throwable var2) {
         return -1L;
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
