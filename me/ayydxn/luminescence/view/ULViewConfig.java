package me.ayydxn.luminescence.view;

import org.jetbrains.annotations.ApiStatus.Internal;

public class ULViewConfig implements AutoCloseable {
   private long handle = ULViewConfig.NativeMethods.nulCreateViewConfig();

   @Override
   public void close() {
      if (this.handle != 0L) {
         ULViewConfig.NativeMethods.nulDestroyViewConfig(this.handle);
         this.handle = 0L;
      }
   }

   public void setDisplayID(int displayID) {
      ULViewConfig.NativeMethods.nulViewConfigSetDisplayID(this.handle, displayID);
   }

   public void isAccelerated(boolean isAccelerated) {
      ULViewConfig.NativeMethods.nulViewConfigIsAccelerated(this.handle, isAccelerated);
   }

   public void setTransparent(boolean isTransparent) {
      ULViewConfig.NativeMethods.nulViewConfigSetIsTransparent(this.handle, isTransparent);
   }

   public void setInitialDeviceScale(double initialDeviceScale) {
      ULViewConfig.NativeMethods.nulViewConfigSetInitialDeviceScale(this.handle, initialDeviceScale);
   }

   public void setInitialFocus(boolean isFocused) {
      ULViewConfig.NativeMethods.nulViewConfigSetInitialFocus(this.handle, isFocused);
   }

   public void setEnableImages(boolean enabled) {
      ULViewConfig.NativeMethods.nulViewConfigSetEnableImages(this.handle, enabled);
   }

   public void setEnableJavaScript(boolean enabled) {
      ULViewConfig.NativeMethods.nulViewConfigSetEnableJavaScript(this.handle, enabled);
   }

   public void setFontFamilyStandard(String fontName) {
      ULViewConfig.NativeMethods.nulViewConfigSetFontFamilyStandard(this.handle, fontName);
   }

   public void setFontFamilyFixed(String fontName) {
      ULViewConfig.NativeMethods.nulViewConfigSetFontFamilyFixed(this.handle, fontName);
   }

   public void setFontFamilySansSerif(String fontName) {
      ULViewConfig.NativeMethods.nulViewConfigSetFontFamilySansSerif(this.handle, fontName);
   }

   public void setUserAgent(String userAgent) {
      ULViewConfig.NativeMethods.nulViewConfigSetUserAgent(this.handle, userAgent);
   }

   @Internal
   public long getHandle() {
      return this.handle;
   }

   private static final class NativeMethods {
      private static native long nulCreateViewConfig();

      private static native void nulDestroyViewConfig(long var0);

      private static native void nulViewConfigSetDisplayID(long var0, int var2);

      private static native void nulViewConfigIsAccelerated(long var0, boolean var2);

      private static native void nulViewConfigSetIsTransparent(long var0, boolean var2);

      private static native void nulViewConfigSetInitialDeviceScale(long var0, double var2);

      private static native void nulViewConfigSetInitialFocus(long var0, boolean var2);

      private static native void nulViewConfigSetEnableImages(long var0, boolean var2);

      private static native void nulViewConfigSetEnableJavaScript(long var0, boolean var2);

      private static native void nulViewConfigSetFontFamilyStandard(long var0, String var2);

      private static native void nulViewConfigSetFontFamilyFixed(long var0, String var2);

      private static native void nulViewConfigSetFontFamilySansSerif(long var0, String var2);

      private static native void nulViewConfigSetUserAgent(long var0, String var2);
   }
}
