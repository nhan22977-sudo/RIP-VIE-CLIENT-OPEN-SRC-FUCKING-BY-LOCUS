package net.ostore.ultralight;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import me.ayydxn.luminescence.console.ULMessageLevel;
import me.ayydxn.luminescence.console.ULMessageSource;
import me.ayydxn.luminescence.events.KeyEventType;
import me.ayydxn.luminescence.events.MouseButton;
import me.ayydxn.luminescence.events.MouseEventType;
import me.ayydxn.luminescence.events.ScrollEventType;
import me.ayydxn.luminescence.events.ULKeyEvent;
import me.ayydxn.luminescence.events.ULMouseEvent;
import me.ayydxn.luminescence.events.ULScrollEvent;
import me.ayydxn.luminescence.geometry.ULIntRect;
import me.ayydxn.luminescence.javascript.JSContext;
import me.ayydxn.luminescence.javascript.JSValue;
import me.ayydxn.luminescence.surface.ULBitmapSurface;
import me.ayydxn.luminescence.surface.ULSurface;
import me.ayydxn.luminescence.view.ULCursor;
import me.ayydxn.luminescence.view.ULView;
import me.ayydxn.luminescence.view.ULViewListener;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_1011.class_1012;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UltralightBrowserView {
   private static final Logger LOG = LoggerFactory.getLogger("ultralight/view");
   private static volatile String bridgeName = "ulQuery";
   private static final int[] UNPREMULT_RECIP = new int[256];
   private final ULView view;
   private final int viewId;
   private class_1043 mcTexture;
   private class_2960 texIdentifier;
   private ByteBuffer pixelBuffer;
   private IntBuffer pixelInts;
   private boolean textureReady = false;
   private boolean needsFullUpload = true;
   private int forcePaint = 0;
   private static final int REPAINT_AFTER_INPUT = 12;
   private volatile Consumer queryHandler;
   private volatile IntConsumer cursorHandler;
   private final AtomicBoolean pageReady = new AtomicBoolean(false);
   private Consumer onPageReadyCallback;
   private int[] blitRow;
   private static final String ULQ = "__ulq";

   public static void setBridgeName(String name) {
      if (name != null && !name.isEmpty()) {
         bridgeName = name;
      }
   }

   public UltralightBrowserView(int width, int height, double deviceScale) {
      this.viewId = UltralightEngine.VIEW_COUNTER.getAndIncrement();
      this.view = UltralightEngine.createView(width, height, true, deviceScale);
      this.view.setListener(new UltralightBrowserView.BridgeListener());
      UltralightEngine.registerView(this);
   }

   public void loadHTML(String html) {
      this.pageReady.set(false);
      this.view.loadHTML(sanitizeForLoad(html));
   }

   public void loadURL(String url) {
      this.pageReady.set(false);
      this.view.loadURL(url);
   }

   private static String sanitizeForLoad(String html) {
      if (html == null) {
         return "";
      }

      StringBuilder var1 = new StringBuilder(html.length() + 16);
      int var2 = 0;

      while (var2 < html.length()) {
         int var3 = html.codePointAt(var2);
         if (var3 > 65535) {
            var1.append("&#").append(var3).append(';');
            var2 += Character.charCount(var3);
         } else {
            var1.append(html.charAt(var2));
            var2++;
         }
      }

      return var1.toString();
   }

   public void resize(int physicalWidth, int physicalHeight) {
      this.view.resize(physicalWidth, physicalHeight);
      this.destroyMcTexture();
      this.needsFullUpload = true;
      this.textureReady = false;
   }

   public void setDeviceScale(double deviceScale) {
      try {
         this.view.setDeviceScale(deviceScale);
      } catch (Throwable var4) {
      }
   }

   public void executeJavaScript(String script) {
      try {
         this.view.evaluateScript(script, new String[1]);
      } catch (Throwable var3) {
         LOG.debug("[ul-view:{}] JS error: {}", this.viewId, var3.getMessage());
      }
   }

   public void setQueryHandler(Consumer handler) {
      this.queryHandler = handler;
   }

   public void updateQueryHandler(Consumer handler) {
      this.queryHandler = handler;
   }

   public void setOnPageReadyCallback(Consumer callback) {
      this.onPageReadyCallback = callback;
   }

   public void setCursorHandler(IntConsumer handler) {
      this.cursorHandler = handler;
   }

   public class_2960 getTextureIdentifier() {
      return this.textureReady ? this.texIdentifier : null;
   }

   public boolean isTextureReady() {
      return this.textureReady;
   }

   public boolean isPageReady() {
      return this.pageReady.get();
   }

   public void setPageReady(boolean v) {
      this.pageReady.set(v);
   }

   public ULView getView() {
      return this.view;
   }

   public void close() {
      UltralightEngine.unregisterView(this);
      this.destroyMcTexture();
      this.textureReady = false;
      this.pageReady.set(false);

      try {
         this.view.destroy();
      } catch (Throwable var2) {
      }
   }

   public void mouseMoved(int x, int y) {
      this.forcePaint = 12;

      try (ULMouseEvent var3 = new ULMouseEvent(MouseEventType.MOUSE_MOVED, x, y, MouseButton.NONE)) {
         this.view.fireMouseEvent(var3);
      } catch (Throwable var8) {
         LOG.debug("[ul-view:{}] mouse: {}", this.viewId, var8.getMessage());
      }
   }

   public void mousePressed(int x, int y, int glfwButton) {
      this.forcePaint = 12;

      try (ULMouseEvent var4 = new ULMouseEvent(MouseEventType.MOUSE_DOWN, x, y, mapButton(glfwButton))) {
         this.view.fireMouseEvent(var4);
      } catch (Throwable var9) {
         LOG.debug("[ul-view:{}] mouse: {}", this.viewId, var9.getMessage());
      }
   }

   public void mouseReleased(int x, int y, int glfwButton) {
      this.forcePaint = 12;

      try (ULMouseEvent var4 = new ULMouseEvent(MouseEventType.MOUSE_UP, x, y, mapButton(glfwButton))) {
         this.view.fireMouseEvent(var4);
      } catch (Throwable var9) {
         LOG.debug("[ul-view:{}] mouse: {}", this.viewId, var9.getMessage());
      }
   }

   public void scroll(int deltaXpixels, int deltaYpixels) {
      this.forcePaint = 12;

      try (ULScrollEvent var3 = new ULScrollEvent(ScrollEventType.SCROLL_BY_PIXEL, deltaXpixels, deltaYpixels)) {
         this.view.fireScrollEvent(var3);
      } catch (Throwable var8) {
         LOG.debug("[ul-view:{}] scroll: {}", this.viewId, var8.getMessage());
      }
   }

   public void charTyped(String text) {
      if (text != null && !text.isEmpty()) {
         this.forcePaint = 12;

         try (ULKeyEvent var2 = new ULKeyEvent(KeyEventType.CHAR, 0, 0, 0, text, text, false, false, false)) {
            this.view.fireKeyEvent(var2);
         } catch (Throwable var7) {
            LOG.debug("[ul-view:{}] char: {}", this.viewId, var7.getMessage());
         }
      }
   }

   public void keyPressed(int glfwKey, int glfwModifiers) {
      this.forcePaint = 12;
      this.fireKey(KeyEventType.RAW_KEY_DOWN, glfwKey, glfwModifiers);
   }

   public void keyReleased(int glfwKey, int glfwModifiers) {
      this.forcePaint = 12;
      this.fireKey(KeyEventType.KEY_UP, glfwKey, glfwModifiers);
   }

   public void requestRepaint() {
      this.forcePaint = 12;
   }

   public void focus() {
      try {
         this.view.focus();
      } catch (Throwable var2) {
      }
   }

   public void unfocus() {
      try {
         this.view.unfocus();
      } catch (Throwable var2) {
      }
   }

   public boolean hasInputFocus() {
      try {
         return this.view.hasInputFocus();
      } catch (Throwable var2) {
         return false;
      }
   }

   private void fireKey(KeyEventType type, int glfwKey, int glfwModifiers) {
      try (ULKeyEvent var4 = new ULKeyEvent(type, mapModifiers(glfwModifiers), glfwKeyToWindowsVK(glfwKey), 0, "", "", false, false, false)) {
         this.view.fireKeyEvent(var4);
      } catch (Throwable var9) {
         LOG.debug("[ul-view:{}] key: {}", this.viewId, var9.getMessage());
      }
   }

   private static MouseButton mapButton(int glfwButton) {
      return switch (glfwButton) {
         case 1 -> MouseButton.RIGHT;
         case 2 -> MouseButton.MIDDLE;
         default -> MouseButton.LEFT;
      };
   }

   private static int mapModifiers(int glfwModifiers) {
      byte var1 = 0;
      if ((glfwModifiers & 4) != 0) {
         var1 |= 1;
      }

      if ((glfwModifiers & 2) != 0) {
         var1 |= 2;
      }

      if ((glfwModifiers & 8) != 0) {
         var1 |= 4;
      }

      if ((glfwModifiers & 1) != 0) {
         var1 |= 8;
      }

      return var1;
   }

   private static int glfwKeyToWindowsVK(int glfwKey) {
      return switch (glfwKey) {
         case 32 -> 32;
         case 256 -> 27;
         case 257, 335 -> 13;
         case 258 -> 9;
         case 259 -> 8;
         case 260 -> 45;
         case 261 -> 46;
         case 262 -> 39;
         case 263 -> 37;
         case 264 -> 40;
         case 265 -> 38;
         case 266 -> 33;
         case 267 -> 34;
         case 268 -> 36;
         case 269 -> 35;
         default -> glfwKey;
      };
   }

   void prepareFrame() {
      if (this.forcePaint > 0) {
         try {
            this.view.setNeedsPaint(true);
         } catch (Throwable var2) {
         }
      }
   }

   void onRendererTick() {
      this.paintSurface();
      this.drainBridge();
   }

   private void paintSurface() {
      ULSurface var1 = this.view.getSurface();
      if (var1 != null && var1.getHandle() != 0L) {
         ULIntRect var2 = var1.getDirtyBounds();
         int var3 = var1.getWidth();
         int var4 = var1.getHeight();
         if (var3 > 0 && var4 > 0) {
            boolean var5 = this.needsFullUpload || this.forcePaint > 0;
            if (this.forcePaint > 0) {
               this.forcePaint--;
            }

            int var6 = 0;
            int var7 = 0;
            int var8 = var3;
            int var9 = var4;
            if (!var5) {
               if (var2 == null || var2.right <= var2.left || var2.bottom <= var2.top) {
                  return;
               }

               var6 = Math.max(0, var2.left);
               var7 = Math.max(0, var2.top);
               var8 = Math.min(var3, var2.right) - var6;
               var9 = Math.min(var4, var2.bottom) - var7;
               if (var8 <= 0 || var9 <= 0) {
                  return;
               }
            }

            this.ensureMcTexture(var3, var4);
            if (this.pixelInts != null) {
               ULBitmapSurface var10 = ULBitmapSurface.fromSurface(var1);

               try (ULSurface.LockedPixels var11 = var10.acquirePixelLock()) {
                  ByteBuffer var12 = var11.pixels();
                  IntBuffer var13 = var12.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
                  int var14 = var1.getRowBytes() / 4;
                  this.blit(var13, var14, var3, var6, var7, var8, var9);
                  this.needsFullUpload = false;
                  this.mcTexture.method_4524();
                  this.textureReady = true;
               } catch (Throwable var22) {
                  LOG.debug("[ul-view:{}] paint: {}", this.viewId, var22.getMessage());
               } finally {
                  var1.clearDirtyBounds();
               }
            }
         }
      }
   }

   private void blit(IntBuffer src, int srcStride, int dstStride, int x0, int y0, int rw, int rh) {
      int[] var8 = this.blitRow;
      if (var8 == null || var8.length < rw) {
         var8 = new int[rw];
         this.blitRow = var8;
      }

      for (int var9 = y0; var9 < y0 + rh; var9++) {
         src.position(var9 * srcStride + x0);
         src.get(var8, 0, rw);

         for (int var10 = 0; var10 < rw; var10++) {
            int var11 = var8[var10];
            int var12 = var11 >>> 24;
            if (var12 == 255) {
               var8[var10] = var11 & -16711936 | (var11 & 0xFF) << 16 | var11 >>> 16 & 0xFF;
            } else if (var12 == 0) {
               var8[var10] = 0;
            } else {
               int var13 = UNPREMULT_RECIP[var12];
               int var14 = Math.min(255, (var11 >>> 16 & 0xFF) * var13 + 32768 >> 16);
               int var15 = Math.min(255, (var11 >>> 8 & 0xFF) * var13 + 32768 >> 16);
               int var16 = Math.min(255, (var11 & 0xFF) * var13 + 32768 >> 16);
               var8[var10] = var12 << 24 | var16 << 16 | var15 << 8 | var14;
            }
         }

         this.pixelInts.position(var9 * dstStride + x0);
         this.pixelInts.put(var8, 0, rw);
      }
   }

   private void ensureMcTexture(int w, int h) {
      if (this.mcTexture == null) {
         long var3 = (long)w * h * 4L;
         this.pixelBuffer = MemoryUtil.memAlloc((int)var3);
         this.pixelInts = this.pixelBuffer.order(ByteOrder.nativeOrder()).asIntBuffer();
         class_1011 var5 = new class_1011(class_1012.field_4997, w, h, false, MemoryUtil.memAddress(this.pixelBuffer));
         this.mcTexture = new class_1043(() -> "ul_view_" + this.viewId, var5);
         this.texIdentifier = class_2960.method_60655("ultralight", "ul_view_" + this.viewId);
         class_310 var6 = class_310.method_1551();
         if (var6 != null) {
            var6.method_1531().method_4616(this.texIdentifier, this.mcTexture);
         }

         this.needsFullUpload = true;
      }
   }

   private void destroyMcTexture() {
      if (this.texIdentifier != null) {
         class_2960 var1 = this.texIdentifier;
         this.texIdentifier = null;
         this.mcTexture = null;
         class_310 var2 = class_310.method_1551();
         if (var2 != null) {
            var2.method_1531().method_4615(var1);
         }
      } else if (this.mcTexture != null) {
         this.mcTexture.close();
         this.mcTexture = null;
      }

      this.pixelInts = null;
      this.pixelBuffer = null;
   }

   private void installBridge() {
      try (JSContext var1 = this.view.acquireJSContextLock()) {
         String var2 = bridgeName;
         var1.evaluate(
            "window['"
               + var2
               + "']=function(d){(window.__ulq=window.__ulq||[]).push(String(d));};window.__ulq=window.__ulq||[];if(!window.__ulErrHook){window.__ulErrHook=1;window.addEventListener('error',function(e){window.__ulErr=(window.__ulErr?window.__ulErr+' | ':'')+((e&&e.message)||e)+' @'+((e&&e.filename)||'')+':'+((e&&e.lineno)||0);});}"
         );
         LOG.debug("[ul-view:{}] pont JS installé (file) : window.{}", this.viewId, var2);
      } catch (Throwable var6) {
         LOG.warn("[ul-view:{}] Bridge install failed: {}", this.viewId, var6.getMessage());
      }
   }

   public String evalString(String js) {
      try (JSContext var2 = this.view.acquireJSContextLock()) {
         JSValue var3 = var2.evaluate(js);
         return var3 == null ? null : var3.toString();
      } catch (Throwable var7) {
         return "EVAL_ERR:" + var7.getMessage();
      }
   }

   private void drainBridge() {
      Consumer var1 = this.queryHandler;
      if (var1 != null) {
         String var2;
         try (JSContext var3 = this.view.acquireJSContextLock()) {
            String var4 = bridgeName;
            JSValue var5 = var3.evaluate(
               "(function(){if(typeof window['"
                  + var4
                  + "']!=='function')window['"
                  + var4
                  + "']=function(d){(window.__ulq=window.__ulq||[]).push(String(d));};var q=window.__ulq;if(!q||!q.length)return '';window.__ulq=[];return q.join('\\u0001');})()"
            );
            var2 = var5 == null ? null : var5.toString();
         } catch (Throwable var11) {
            return;
         }

         if (var2 != null && !var2.isEmpty()) {
            for (String var6 : var2.split("\u0001")) {
               if (!var6.isEmpty()) {
                  try {
                     var1.accept(var6);
                  } catch (Throwable var9) {
                     LOG.warn("[ul-view:{}] Bridge handler error: {}", this.viewId, var9.getMessage());
                  }
               }
            }
         }
      }
   }

   private static int glfwCursorShape(ULCursor c) {
      return switch (c) {
         case HAND -> 221188;
         case IBEAM -> 221186;
         case CROSS -> 221187;
         case EAST_WEST_RESIZE, EAST_RESIZE, WEST_RESIZE, COLUMN_RESIZE -> 221189;
         case NORTH_SOUTH_RESIZE, NORTH_RESIZE, SOUTH_RESIZE, ROW_RESIZE -> 221190;
         default -> 221185;
      };
   }

   static {
      UNPREMULT_RECIP[0] = 0;

      for (int var0 = 1; var0 < 256; var0++) {
         UNPREMULT_RECIP[var0] = (int)(16711680L / var0);
      }
   }

   private final class BridgeListener implements ULViewListener {
      @Override
      public void onWindowObjectReady(long frameID, boolean isMainFrame, String url) {
         if (isMainFrame) {
            UltralightBrowserView.this.installBridge();
         }
      }

      @Override
      public void onDOMReady(long frameID, boolean isMainFrame, String url) {
         if (isMainFrame) {
            UltralightBrowserView.this.pageReady.set(true);
            Consumer var5 = UltralightBrowserView.this.onPageReadyCallback;
            if (var5 != null) {
               class_310.method_1551().execute(() -> var5.accept(null));
            }
         }
      }

      @Override
      public void onLoadingFail(long frameID, boolean isMainFrame, String url, String description, String errorDomain, int errorCode) {
         UltralightBrowserView.LOG.warn("[ul-view:{}] Load failed ({}): {} — {}", new Object[]{UltralightBrowserView.this.viewId, errorCode, url, description});
      }

      @Override
      public void onCursorChange(ULCursor cursor) {
         IntConsumer var2 = UltralightBrowserView.this.cursorHandler;
         if (var2 != null) {
            var2.accept(UltralightBrowserView.glfwCursorShape(cursor));
         }
      }

      @Override
      public void onConsoleMessageAdded(ULMessageSource source, ULMessageLevel level, String message, int line, int column, String sourceID) {
         String var7 = "[ul-console:" + UltralightBrowserView.this.viewId + "/" + source + "] " + message;
         switch (level) {
            case ERROR:
               UltralightBrowserView.LOG.error(var7);
               break;
            case WARNING:
               UltralightBrowserView.LOG.warn(var7);
               break;
            default:
               UltralightBrowserView.LOG.info(var7);
         }
      }
   }
}
