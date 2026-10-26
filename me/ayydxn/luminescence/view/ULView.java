package me.ayydxn.luminescence.view;

import me.ayydxn.luminescence.events.ULKeyEvent;
import me.ayydxn.luminescence.events.ULMouseEvent;
import me.ayydxn.luminescence.events.ULScrollEvent;
import me.ayydxn.luminescence.gpu.ULRenderTarget;
import me.ayydxn.luminescence.javascript.JSContext;
import me.ayydxn.luminescence.renderer.ULRenderer;
import me.ayydxn.luminescence.renderer.ULSession;
import me.ayydxn.luminescence.surface.ULSurface;
import me.ayydxn.luminescence.util.UsedByNative;
import org.jetbrains.annotations.Nullable;

public class ULView {
   private long handle;

   public ULView(ULRenderer renderer, int width, int height, ULViewConfig viewConfig, @Nullable ULSession session) {
      this.handle = ULView.NativeMethods.nulCreateView(renderer.getHandle(), width, height, viewConfig.getHandle(), session != null ? session.getHandle() : 0L);
   }

   @UsedByNative("ViewCallbackAdapter.cpp")
   private ULView(long handle) {
      this.handle = handle;
   }

   public void destroy() {
      if (this.handle != 0L) {
         ULView.NativeMethods.nulDestroyView(this.handle);
         this.handle = 0L;
      }
   }

   public String getURL() {
      return ULView.NativeMethods.nulViewGetURL(this.handle);
   }

   public String getTitle() {
      return ULView.NativeMethods.nulViewGetTitle(this.handle);
   }

   public int getWidth() {
      return ULView.NativeMethods.nulViewGetWidth(this.handle);
   }

   public int getHeight() {
      return ULView.NativeMethods.nulViewGetHeight(this.handle);
   }

   public int getDisplayID() {
      return ULView.NativeMethods.nulViewGetDisplayId(this.handle);
   }

   public void setDisplayID(int displayID) {
      ULView.NativeMethods.nulViewSetDisplayId(this.handle, displayID);
   }

   public double getDeviceScale() {
      return ULView.NativeMethods.nulViewGetDeviceScale(this.handle);
   }

   public void setDeviceScale(double deviceScale) {
      ULView.NativeMethods.nulViewSetDeviceScale(this.handle, deviceScale);
   }

   public boolean isAccelerated() {
      return ULView.NativeMethods.nulViewIsAccelerated(this.handle);
   }

   public boolean isTransparent() {
      return ULView.NativeMethods.nulViewIsTransparent(this.handle);
   }

   public boolean isLoading() {
      return ULView.NativeMethods.nulViewIsLoading(this.handle);
   }

   @Nullable
   public ULRenderTarget getRenderTarget() {
      return !this.isAccelerated() ? null : ULView.NativeMethods.nulViewGetRenderTarget(this.handle);
   }

   public ULSurface getSurface() {
      long surfaceHandle = ULView.NativeMethods.nulViewGetSurface(this.handle);
      if (surfaceHandle == 0L) {
         throw new NullPointerException("The view's surface is null!");
      } else {
         return new ULSurface(surfaceHandle);
      }
   }

   public void loadHTML(String html) {
      ULView.NativeMethods.nulViewLoadHTML(this.handle, html);
   }

   public void loadURL(String url) {
      ULView.NativeMethods.nulViewLoadURL(this.handle, url);
   }

   public void resize(int width, int height) {
      ULView.NativeMethods.nulViewResize(this.handle, width, height);
   }

   public long lockJSContext() {
      return ULView.NativeMethods.nulViewLockJSContext(this.handle);
   }

   public void unlockJSContext() {
      ULView.NativeMethods.nulViewUnlockJSContext(this.handle);
   }

   public JSContext acquireJSContextLock() {
      return new JSContext(this.handle, ULView.NativeMethods.nulViewLockJSContext(this.handle));
   }

   public String evaluateScript(String jsString, String[] exception) {
      return ULView.NativeMethods.nulViewEvaluateScript(this.handle, jsString, exception);
   }

   public boolean canGoBack() {
      return ULView.NativeMethods.nulViewCanGoBack(this.handle);
   }

   public boolean canGoForward() {
      return ULView.NativeMethods.nulViewCanGoForward(this.handle);
   }

   public void goBack() {
      ULView.NativeMethods.nulViewGoBack(this.handle);
   }

   public void goForward() {
      ULView.NativeMethods.nulViewGoForward(this.handle);
   }

   public void goToHistoryOffset(int offset) {
      ULView.NativeMethods.nulViewGoToHistoryOffset(this.handle, offset);
   }

   public void reload() {
      ULView.NativeMethods.nulViewReload(this.handle);
   }

   public void stop() {
      ULView.NativeMethods.nulViewStop(this.handle);
   }

   public void focus() {
      ULView.NativeMethods.nulViewFocus(this.handle);
   }

   public void unfocus() {
      ULView.NativeMethods.nulViewUnfocus(this.handle);
   }

   public boolean hasFocus() {
      return ULView.NativeMethods.nulViewHasFocus(this.handle);
   }

   public boolean hasInputFocus() {
      return ULView.NativeMethods.nulViewHasInputFocus(this.handle);
   }

   public void fireKeyEvent(ULKeyEvent keyEvent) {
      ULView.NativeMethods.nulViewFireKeyEvent(this.handle, keyEvent.getHandle());
   }

   public void fireMouseEvent(ULMouseEvent mouseEvent) {
      ULView.NativeMethods.nulViewFireMouseEvent(this.handle, mouseEvent.getHandle());
   }

   public void fireScrollEvent(ULScrollEvent scrollEvent) {
      ULView.NativeMethods.nulViewFireScrollEvent(this.handle, scrollEvent.getHandle());
   }

   public void setListener(@Nullable ULViewListener listener) {
      ULView.NativeMethods.nulViewSetViewListener(this.handle, listener);
   }

   public void setNeedsPaint(boolean needsPaint) {
      ULView.NativeMethods.nulViewSetNeedsPaint(this.handle, needsPaint);
   }

   public boolean needsPaint() {
      return ULView.NativeMethods.nulViewGetNeedsPaint(this.handle);
   }

   public void createLocalInspectorView() {
      ULView.NativeMethods.nulViewCreateLocalInspectorView(this.handle);
   }

   private static final class NativeMethods {
      private static native long nulCreateView(long var0, int var2, int var3, long var4, long var6);

      private static native void nulDestroyView(long var0);

      private static native String nulViewGetURL(long var0);

      private static native String nulViewGetTitle(long var0);

      private static native int nulViewGetWidth(long var0);

      private static native int nulViewGetHeight(long var0);

      private static native int nulViewGetDisplayId(long var0);

      private static native void nulViewSetDisplayId(long var0, int var2);

      private static native double nulViewGetDeviceScale(long var0);

      private static native void nulViewSetDeviceScale(long var0, double var2);

      private static native boolean nulViewIsAccelerated(long var0);

      private static native boolean nulViewIsTransparent(long var0);

      private static native boolean nulViewIsLoading(long var0);

      private static native ULRenderTarget nulViewGetRenderTarget(long var0);

      private static native long nulViewGetSurface(long var0);

      private static native void nulViewLoadHTML(long var0, String var2);

      private static native void nulViewLoadURL(long var0, String var2);

      private static native void nulViewResize(long var0, int var2, int var3);

      private static native long nulViewLockJSContext(long var0);

      private static native void nulViewUnlockJSContext(long var0);

      private static native String nulViewEvaluateScript(long var0, String var2, String[] var3);

      private static native boolean nulViewCanGoBack(long var0);

      private static native boolean nulViewCanGoForward(long var0);

      private static native void nulViewGoBack(long var0);

      private static native void nulViewGoForward(long var0);

      private static native void nulViewGoToHistoryOffset(long var0, int var2);

      private static native void nulViewReload(long var0);

      private static native void nulViewStop(long var0);

      private static native void nulViewFocus(long var0);

      private static native void nulViewUnfocus(long var0);

      private static native boolean nulViewHasFocus(long var0);

      private static native boolean nulViewHasInputFocus(long var0);

      private static native void nulViewFireKeyEvent(long var0, long var2);

      private static native void nulViewFireMouseEvent(long var0, long var2);

      private static native void nulViewFireScrollEvent(long var0, long var2);

      private static native void nulViewSetViewListener(long var0, ULViewListener var2);

      private static native void nulViewSetNeedsPaint(long var0, boolean var2);

      private static native boolean nulViewGetNeedsPaint(long var0);

      private static native void nulViewCreateLocalInspectorView(long var0);
   }
}
