package me.ayydxn.luminescence.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class UltralightNativeLoader {
   private static final String UL_NATIVE_PATH_PROPERTY = "ultralight.native.path";
   private static final String UL_BIN_PATH_PROPERTY = "ultralight.bin.path";
   private static final List<String> ULTRALIGHT_LOAD_ORDER = List.of("UltralightCore", "WebCore", "Ultralight", "AppCore");
   private static volatile boolean isLoaded = false;

   private UltralightNativeLoader() {
   }

   public static synchronized void load() {
      String binPathProperty = System.getProperty("ultralight.bin.path");
      load(binPathProperty != null ? Path.of(binPathProperty) : null);
   }

   public static synchronized void load(@Nullable Path ultralightBinaryPath) {
      if (!isLoaded) {
         loadUltralightBinaries(ultralightBinaryPath);
         loadJNIBridge();
         isLoaded = true;
      }
   }

   private static void loadUltralightBinaries(@Nullable Path binaryPath) {
      for (String libraryName : ULTRALIGHT_LOAD_ORDER) {
         String filename = getPlatformLibraryFilename(libraryName);
         if (binaryPath != null) {
            Path libraryPath = binaryPath.resolve(filename).toAbsolutePath();
            if (!Files.exists(libraryPath)) {
               throw new RuntimeException("Ultralight binary not found at: " + libraryPath + "\nEnsure the directory contains the 1.4.0 SDK binaries.");
            }

            System.load(libraryPath.toString());
         } else {
            try {
               System.loadLibrary(libraryName);
            } catch (UnsatisfiedLinkError exception) {
               throw new RuntimeException(
                  "Failed to load '" + libraryName + "' from OS search path.\nEither place binaries on the PATH/LD_LIBRARY_PATH or use load(Path).", exception
               );
            }
         }
      }
   }

   private static void loadJNIBridge() {
      String override = System.getProperty("ultralight.native.path");
      if (override != null) {
         System.load(Path.of(override).toAbsolutePath().toString());
      } else {
         String platform = getCurrentPlatformString();
         String libraryFilename = getPlatformLibraryFilename("LuminescenceJNI");
         String resourcePath = "/natives/" + platform + "/" + libraryFilename;

         try {
            label58: {
               try (InputStream inputStream = UltralightNativeLoader.class.getResourceAsStream(resourcePath)) {
                  if (inputStream == null) {
                     break label58;
                  }

                  Path tempFile = Files.createTempFile("LuminescenceJNI", libraryFilename);
                  tempFile.toFile().deleteOnExit();
                  Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                  System.load(tempFile.toAbsolutePath().toString());
               }

               return;
            }
         } catch (IOException exception) {
            throw new RuntimeException("Failed to extract JNI bridge from " + resourcePath, exception);
         }

         try {
            System.loadLibrary("LuminescenceJNI");
         } catch (UnsatisfiedLinkError exception) {
            throw new RuntimeException("LuminescenceJNI not found on classpath or library path.", exception);
         }
      }
   }

   private static String getCurrentPlatformString() {
      String os = System.getProperty("os.name").toLowerCase();
      String arch = System.getProperty("os.arch").toLowerCase();
      boolean isArm = arch.contains("aarch64") || arch.contains("arm");
      if (os.contains("win")) {
         return "windows-x64";
      } else if (os.contains("mac")) {
         return isArm ? "macos-arm64" : "macos-x64";
      } else {
         return isArm ? "linux-arm64" : "linux-x64";
      }
   }

   private static String getPlatformLibraryFilename(String baseName) {
      String os = System.getProperty("os.name").toLowerCase();
      if (os.contains("win")) {
         return baseName + ".dll";
      } else {
         return os.contains("mac") ? "lib" + baseName + ".dylib" : "lib" + baseName + ".so";
      }
   }
}
