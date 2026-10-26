package net.ostore.ultralight;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class UltralightCssProbe {
   private static final Logger LOG = LoggerFactory.getLogger("ultralight/cssprobe");
   private static final boolean ENABLED = Boolean.getBoolean("ultralight.cssprobe") || "true".equalsIgnoreCase(System.getenv("ULTRALIGHT_CSSPROBE"));
   private static final String RESOURCE = "/assets/ultralight/css-probe.html";
   private static boolean started = false;
   private static UltralightBrowserView view;
   private static int ticksLeft = 0;

   private UltralightCssProbe() {
   }

   static void tick() {
      if (ENABLED) {
         if (!started) {
            started = true;
            start();
         } else {
            if (view != null && --ticksLeft <= 0) {
               view.close();
               view = null;
               LOG.info("[ul-cssprobe] Sonde fermée.");
            }
         }
      }
   }

   private static void start() {
      String var0 = readResource();
      if (var0 == null) {
         LOG.error("[ul-cssprobe] Ressource introuvable : {}", "/assets/ultralight/css-probe.html");
      } else {
         try {
            view = new UltralightBrowserView(512, 512, 1.0);
            view.setQueryHandler(msg -> LOG.info("[ul-cssprobe] pont JS→Java reçu : {}", msg));
            view.loadHTML(var0);
            ticksLeft = 600;
            LOG.info("[ul-cssprobe] Sonde CSS lancée — chercher 'CSS-PROBE-RESULT' dans les logs.");
         } catch (Throwable var2) {
            LOG.error("[ul-cssprobe] Lancement échoué", var2);
         }
      }
   }

   private static String readResource() {
      try (InputStream var0 = UltralightCssProbe.class.getResourceAsStream("/assets/ultralight/css-probe.html")) {
         return var0 == null ? null : new String(var0.readAllBytes(), StandardCharsets.UTF_8);
      } catch (Exception var5) {
         LOG.error("[ul-cssprobe] Lecture ressource échouée", var5);
         return null;
      }
   }
}
