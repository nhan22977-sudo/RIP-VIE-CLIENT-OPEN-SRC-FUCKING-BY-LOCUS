package net.ostore.ultralight;

import com.sun.management.HotSpotDiagnosticMXBean;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import me.ayydxn.luminescence.config.ULConfig;
import me.ayydxn.luminescence.platform.ULPlatform;
import me.ayydxn.luminescence.platform.impl.StandardULFileSystem;
import me.ayydxn.luminescence.platform.impl.StandardULFontLoader;
import me.ayydxn.luminescence.renderer.ULRenderer;
import me.ayydxn.luminescence.view.ULView;
import me.ayydxn.luminescence.view.ULViewConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UltralightEngine {
   private static final Logger LOG = LoggerFactory.getLogger("ultralight/engine");
   private static volatile boolean ready = false;
   private static boolean initAttempted = false;
   private static ULRenderer renderer;
   static final Set activeViews = Collections.newSetFromMap(new ConcurrentHashMap());
   static final AtomicInteger VIEW_COUNTER = new AtomicInteger(0);

   private UltralightEngine() {
   }

   public static boolean isReady() {
      return ready;
   }

   public static void init() {
      HudRenderCallback.EVENT.register((HudRenderCallback)(ctx, tick) -> onFrame());
   }

   private static void doInit() {
      if (isDisabledByUser()) {
         LOG.info("[ul] Rendu HTML désactivé par configuration (-Dultralight.disable).");
      } else if (!"true".equalsIgnoreCase(System.getProperty("ultralight.skipCpuCheck")) && !cpuSupportsAvx2()) {
         LOG.warn(
            "[ul] CPU sans support AVX2 détecté — WebKit 615 requiert AVX2. Rendu HTML désactivé pour éviter un crash natif (EXCEPTION_ILLEGAL_INSTRUCTION). Force via -Dultralight.skipCpuCheck=true (à vos risques)."
         );
      } else {
         Path var0 = UltralightNativeLoader.load();
         if (var0 == null) {
            LOG.warn("[ul] Natifs non disponibles — rendu HTML désactivé.");
         } else {
            try {
               boolean var1 = !"false".equalsIgnoreCase(System.getProperty("ultralight.emojifallback", "true"));
               if (var1) {
                  ULPlatform.setFontLoader(new EmojiFallbackFontLoader());
               } else {
                  ULPlatform.setFontLoader(new StandardULFontLoader());
                  LOG.info("[ul] Police de secours emoji désactivée (-Dultralight.emojifallback=false) → loader standard.");
               }

               ULPlatform.setFileSystem(new StandardULFileSystem());

               try (ULConfig var2 = new ULConfig()) {
                  String var3 = var0.resolve("resources").toFile().getAbsolutePath().replace('\\', '/') + "/";
                  var2.setResourcePathPrefix(var3);
                  renderer = new ULRenderer(var2);
               }

               ready = true;
               LOG.info("[ul] Moteur Ultralight 1.4 initialisé.");
            } catch (Throwable var7) {
               LOG.error("[ul] Initialisation échouée", var7);
            }
         }
      }
   }

   private static boolean isDisabledByUser() {
      if ("true".equalsIgnoreCase(System.getProperty("ultralight.disable"))) {
         return true;
      }

      String var0 = System.getenv("ULTRALIGHT_DISABLE");
      return var0 != null && "true".equalsIgnoreCase(var0.trim());
   }

   private static boolean cpuSupportsAvx2() {
      try {
         HotSpotDiagnosticMXBean var0 = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
         if (var0 == null) {
            return true;
         }

         String var1 = var0.getVMOption("UseAVX").getValue();
         return Integer.parseInt(var1.trim()) >= 2;
      } catch (Throwable var2) {
         LOG.debug("[ul] Détection AVX2 impossible ({}), on tente l'init.", var2.toString());
         return true;
      }
   }

   public static void renderFrame() {
      onFrame();
   }

   static void onFrame() {
      if (!initAttempted) {
         initAttempted = true;
         doInit();
      }

      if (ready && renderer != null) {
         UltralightCssProbe.tick();
         if (!activeViews.isEmpty()) {
            renderer.update();

            for (UltralightBrowserView var1 : activeViews) {
               var1.prepareFrame();
            }

            renderer.render();

            for (UltralightBrowserView var3 : activeViews) {
               var3.onRendererTick();
            }
         }
      }
   }

   static ULView createView(int width, int height, boolean transparent, double deviceScale) {
      try (ULViewConfig var5 = new ULViewConfig()) {
         var5.isAccelerated(false);
         var5.setTransparent(transparent);
         var5.setInitialDeviceScale(deviceScale);
         var5.setEnableJavaScript(true);
         var5.setEnableImages(true);
         return new ULView(renderer, width, height, var5, null);
      }
   }

   static void registerView(UltralightBrowserView view) {
      activeViews.add(view);
   }

   static void unregisterView(UltralightBrowserView view) {
      activeViews.remove(view);
   }
}
