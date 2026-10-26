package me.ayydxn.luminescence.platform;

public class ULPlatform {
   private ULPlatform() {
   }

   public static void setLogger(ULLogger logger) {
      if (logger == null) {
         throw new IllegalArgumentException("Cannot set the platform logger to a null ULLogger instance!");
      }

      ULPlatform.NativeMethods.nulPlatformSetLogger(logger);
   }

   public static void setFileSystem(ULFileSystem fileSystem) {
      if (fileSystem == null) {
         throw new IllegalArgumentException("Cannot set the platform filesystem to a null ULFileSystem instance!");
      }

      ULPlatform.NativeMethods.nulPlatformSetFileSystem(fileSystem);
   }

   public static void setClipboard(ULClipboard clipboard) {
      if (clipboard == null) {
         throw new IllegalArgumentException("Cannot set the platform clipboard to a null ULClipboard instance!");
      }

      ULPlatform.NativeMethods.nulPlatformSetClipboard(clipboard);
   }

   public static void setFontLoader(ULFontLoader fontLoader) {
      if (fontLoader == null) {
         throw new IllegalArgumentException("Cannot set the platform font loader to a null ULFontLoader instance!");
      }

      ULPlatform.NativeMethods.nulPlatformSetFontLoader(fontLoader);
   }

   public static void setGPUDriver(ULGPUDriver gpuDriver) {
      if (gpuDriver == null) {
         throw new IllegalArgumentException("Cannot set the platform GPU driver to a null ULGPUDriver instance!");
      }

      ULPlatform.NativeMethods.nulPlatformSetGPUDriver(gpuDriver);
   }

   static {
      Runtime.getRuntime().addShutdownHook(new Thread(ULPlatform.NativeMethods::nulPlatformShutdown, "Luminescence-Shutdown"));
   }

   private static final class NativeMethods {
      private static native void nulPlatformSetLogger(ULLogger var0);

      private static native void nulPlatformSetFileSystem(ULFileSystem var0);

      private static native void nulPlatformSetClipboard(ULClipboard var0);

      private static native void nulPlatformSetFontLoader(ULFontLoader var0);

      private static native void nulPlatformSetGPUDriver(ULGPUDriver var0);

      private static native void nulPlatformShutdown();
   }
}
