package me.ayydxn.luminescence.config;

import java.util.function.BiConsumer;
import me.ayydxn.luminescence.util.ULString;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ULConfig implements AutoCloseable {
   private long handle = ULConfig.NativeMethods.nulCreateConfig();

   @Override
   public void close() {
      if (this.handle != 0L) {
         ULConfig.NativeMethods.nulDestroyConfig(this.handle);
         this.handle = 0L;
      }
   }

   public void setCachePath(String cachePath) {
      this.withNativeString(cachePath, ULConfig.NativeMethods::nulConfigSetCachePath);
   }

   public void setResourcePathPrefix(String resourcePathPrefix) {
      this.withNativeString(resourcePathPrefix, ULConfig.NativeMethods::nulConfigSetResourcePathPrefix);
   }

   public void setFaceWinding(FaceWinding faceWinding) {
      ULConfig.NativeMethods.nulConfigSetFaceWinding(this.handle, faceWinding.getValue());
   }

   public void setFontHinting(FontHinting fontHinting) {
      ULConfig.NativeMethods.nulConfigSetFontHinting(this.handle, fontHinting.getValue());
   }

   public void setFontGamma(double fontGamma) {
      ULConfig.NativeMethods.nulConfigSetFontGamma(this.handle, fontGamma);
   }

   public void setUserStylesheet(String stylesheetString) {
      this.withNativeString(stylesheetString, ULConfig.NativeMethods::nulConfigSetUserStylesheet);
   }

   public void setForceRepaint(boolean isEnabled) {
      ULConfig.NativeMethods.nulConfigSetForceRepaint(this.handle, isEnabled);
   }

   public void setAnimationTimerDelay(double delay) {
      ULConfig.NativeMethods.nulConfigSetAnimationTimerDelay(this.handle, delay);
   }

   public void setScrollTimerDelay(double delay) {
      ULConfig.NativeMethods.nulConfigSetScrollTimerDelay(this.handle, delay);
   }

   public void setRecycleDelay(double delay) {
      ULConfig.NativeMethods.nulConfigSetRecycleDelay(this.handle, delay);
   }

   public void setMemoryCacheSize(int size) {
      ULConfig.NativeMethods.nulConfigSetMemoryCacheSize(this.handle, size);
   }

   public void setPageCacheSize(int size) {
      ULConfig.NativeMethods.nulConfigSetPageCacheSize(this.handle, size);
   }

   public void setOverrideRAMSize(int size) {
      ULConfig.NativeMethods.nulConfigSetOverrideRAMSize(this.handle, size);
   }

   public void setMinLargeHeapSize(int size) {
      ULConfig.NativeMethods.nulConfigSetMinLargeHeapSize(this.handle, size);
   }

   public void setMinSmallHeapSize(int size) {
      ULConfig.NativeMethods.nulConfigSetMinSmallHeapSize(this.handle, size);
   }

   public void setNumRendererThreads(int size) {
      ULConfig.NativeMethods.nulConfigSetNumRendererThreads(this.handle, size);
   }

   public void setMaxUpdateTime(double delay) {
      ULConfig.NativeMethods.nulConfigSetMaxUpdateTime(this.handle, delay);
   }

   public void setBitmapAlignment(int size) {
      ULConfig.NativeMethods.nulConfigSetBitmapAlignment(this.handle, size);
   }

   public void setEffectQuality(EffectQuality effectQuality) {
      ULConfig.NativeMethods.nulConfigSetEffectQuality(this.handle, effectQuality.ordinal());
   }

   @Internal
   public long getHandle() {
      return this.handle;
   }

   private void withNativeString(String value, BiConsumer<Long, Long> nativeMethod) {
      try (ULString ulString = ULString.create(value)) {
         nativeMethod.accept(this.handle, ulString.getHandle());
      }
   }

   private static final class NativeMethods {
      private static native long nulCreateConfig();

      private static native void nulDestroyConfig(long var0);

      private static native void nulConfigSetCachePath(long var0, long var2);

      private static native void nulConfigSetResourcePathPrefix(long var0, long var2);

      private static native void nulConfigSetFaceWinding(long var0, int var2);

      private static native void nulConfigSetFontHinting(long var0, int var2);

      private static native void nulConfigSetFontGamma(long var0, double var2);

      private static native void nulConfigSetUserStylesheet(long var0, long var2);

      private static native void nulConfigSetForceRepaint(long var0, boolean var2);

      private static native void nulConfigSetAnimationTimerDelay(long var0, double var2);

      private static native void nulConfigSetScrollTimerDelay(long var0, double var2);

      private static native void nulConfigSetRecycleDelay(long var0, double var2);

      private static native void nulConfigSetMemoryCacheSize(long var0, int var2);

      private static native void nulConfigSetPageCacheSize(long var0, int var2);

      private static native void nulConfigSetOverrideRAMSize(long var0, int var2);

      private static native void nulConfigSetMinLargeHeapSize(long var0, int var2);

      private static native void nulConfigSetMinSmallHeapSize(long var0, int var2);

      private static native void nulConfigSetNumRendererThreads(long var0, int var2);

      private static native void nulConfigSetMaxUpdateTime(long var0, double var2);

      private static native void nulConfigSetBitmapAlignment(long var0, int var2);

      private static native void nulConfigSetEffectQuality(long var0, int var2);
   }
}
