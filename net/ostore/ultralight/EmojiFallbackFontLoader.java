package net.ostore.ultralight;

import java.awt.Font;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import me.ayydxn.luminescence.font.ULFontFile;
import me.ayydxn.luminescence.platform.ULFontLoader;
import me.ayydxn.luminescence.platform.impl.StandardULFontLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EmojiFallbackFontLoader implements ULFontLoader {
   private static final Logger LOG = LoggerFactory.getLogger("ultralight/fonts");
   private static final boolean IS_UNIX = !System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
   public static final String EMOJI_FAMILY = "AbysseEmoji";
   public static final String SYMBOLS_FAMILY = "AbysseSymbols";
   public static final String TEXT_FAMILY = "AbysseText";
   private static final String RES_EMOJI = "/fonts/NotoEmoji-Regular.ttf";
   private static final String RES_SYMBOLS = "/fonts/NotoSansSymbols2-Regular.ttf";
   private static final String RES_TEXT = "/fonts/NotoSans-Regular.ttf";
   private final StandardULFontLoader delegate;
   private final String emojiPath;
   private final String symbolsPath;
   private final String textPath;
   private final Font systemAwt;
   private final Font emojiAwt;
   private final Font symbolsAwt;
   private final Font textAwt;
   private final Set loggedCps = new HashSet();
   private boolean loggedLoadE = false;
   private boolean loggedLoadS = false;

   public EmojiFallbackFontLoader() {
      this.delegate = new StandardULFontLoader();
      this.emojiPath = extract("/fonts/NotoEmoji-Regular.ttf", "emoji");
      this.symbolsPath = extract("/fonts/NotoSansSymbols2-Regular.ttf", "symbols");
      this.textPath = extract("/fonts/NotoSans-Regular.ttf", "text");
      this.emojiAwt = awtFromPath(this.emojiPath);
      this.symbolsAwt = awtFromPath(this.symbolsPath);
      this.textAwt = awtFromPath(this.textPath);
      this.systemAwt = this.loadSystemFont();
      LOG.info(
         "[ul-fonts] secours prêt — emoji:{} symboles:{} texte:{} systeme(canDisplay):{}",
         new Object[]{this.emojiPath != null, this.symbolsPath != null, this.textPath != null, this.systemAwt != null}
      );
   }

   @Override
   public ULFontFile load(String family, int weight, boolean italic) {
      if (this.emojiPath != null && "AbysseEmoji".equalsIgnoreCase(family)) {
         if (!this.loggedLoadE) {
            LOG.debug("[ul-fonts] load() police emoji");
            this.loggedLoadE = true;
         }

         return ULFontFile.createFromFilePath(this.emojiPath);
      } else if (this.symbolsPath != null && "AbysseSymbols".equalsIgnoreCase(family)) {
         if (!this.loggedLoadS) {
            LOG.debug("[ul-fonts] load() police symboles");
            this.loggedLoadS = true;
         }

         return ULFontFile.createFromFilePath(this.symbolsPath);
      } else {
         return this.textPath != null && "AbysseText".equalsIgnoreCase(family)
            ? ULFontFile.createFromFilePath(this.textPath)
            : this.delegate.load(family, weight, italic);
      }
   }

   @Override
   public String getFallbackFontForCharacters(String chars, int weight, boolean italic) {
      if (chars != null) {
         int var4 = 0;
         int var5 = chars.length();

         while (var4 < var5) {
            int var6 = chars.codePointAt(var4);
            String var7 = this.pick(var6);
            if (var7 != null) {
               if (LOG.isDebugEnabled() && this.loggedCps.size() < 600 && this.loggedCps.add(var6)) {
                  LOG.debug("[ul-fonts] U+{} → {}", hex(var6), var7);
               }

               return var7;
            }

            var4 += Character.charCount(var6);
         }
      }

      if (IS_UNIX && this.textPath != null && this.textAwt != null && chars != null) {
         int var8 = 0;
         int var9 = chars.length();

         while (var8 < var9) {
            int var10 = chars.codePointAt(var8);
            if (this.textAwt.canDisplay(var10)) {
               return "AbysseText";
            }

            var8 += Character.charCount(var10);
         }
      }

      return this.delegate.getFallbackFontForCharacters(chars, weight, italic);
   }

   @Override
   public String getFallbackFont() {
      return this.textPath != null ? "AbysseText" : this.delegate.getFallbackFont();
   }

   private String pick(int cp) {
      if (cp < 161) {
         return null;
      }

      if (!Character.isSpaceChar(cp) && Character.getType(cp) != 16) {
         if (this.systemAwt != null) {
            if (this.systemAwt.canDisplay(cp)) {
               return null;
            }
         } else if (cp < 126976) {
            return null;
         }

         if (this.emojiPath != null && this.emojiAwt != null && this.emojiAwt.canDisplay(cp)) {
            return "AbysseEmoji";
         } else {
            return this.symbolsPath != null && this.symbolsAwt != null && this.symbolsAwt.canDisplay(cp) ? "AbysseSymbols" : null;
         }
      } else {
         return null;
      }
   }

   private static String hex(int cp) {
      return Integer.toHexString(cp).toUpperCase();
   }

   private static Font awtFromPath(String path) {
      if (path == null) {
         return null;
      }

      try {
         return Font.createFont(0, new File(path));
      } catch (Throwable var2) {
         LOG.warn("[ul-fonts] AWT police KO ({}): {}", path, var2.getMessage());
         return null;
      }
   }

   private Font loadSystemFont() {
      try {
         String var1 = this.delegate.getFallbackFont();
         ULFontFile var2 = this.delegate.load(var1, 400, false);
         if (var2 != null && var2.getFilePath() != null) {
            Font var3 = awtFromPath(var2.getFilePath());
            if (var3 != null) {
               return var3;
            }
         }
      } catch (Throwable var6) {
      }

      try {
         File var7 = new File(System.getenv("SystemRoot") + "/Fonts/arial.ttf");
         if (var7.isFile()) {
            return Font.createFont(0, var7);
         }
      } catch (Throwable var5) {
      }

      try {
         return new Font("Arial", 0, 12);
      } catch (Throwable var4) {
         return null;
      }
   }

   private static String extract(String res, String tag) {
      try (InputStream var2 = EmojiFallbackFontLoader.class.getResourceAsStream(res)) {
         if (var2 == null) {
            LOG.warn("[ul-fonts] Ressource introuvable : {}", res);
            return null;
         } else {
            Path var3 = Files.createTempFile("abysse-" + tag + "-", ".ttf");
            var3.toFile().deleteOnExit();
            Files.copy(var2, var3, StandardCopyOption.REPLACE_EXISTING);
            return var3.toAbsolutePath().toString();
         }
      } catch (Exception var7) {
         LOG.warn("[ul-fonts] Extraction police {} échouée : {}", tag, var7.getMessage());
         return null;
      }
   }
}
