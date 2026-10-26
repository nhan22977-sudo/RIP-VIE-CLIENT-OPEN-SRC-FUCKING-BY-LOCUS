package me.ayydxn.luminescence.platform.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import me.ayydxn.luminescence.font.ULFontFile;
import me.ayydxn.luminescence.platform.ULFontLoader;
import org.jetbrains.annotations.Nullable;

public class StandardULFontLoader implements ULFontLoader {
   private final List<Path> fontSearchPaths = new ArrayList<>();
   private final String fallback;

   public StandardULFontLoader() {
      this("Arial");
   }

   public StandardULFontLoader(String defaultFontFamily) {
      this.fallback = defaultFontFamily;
      this.setupSystemFontPaths();
   }

   @Override
   public String getFallbackFont() {
      return this.fallback;
   }

   @Override
   public String getFallbackFontForCharacters(String characters, int weight, boolean isItalic) {
      return this.fallback;
   }

   @Nullable
   @Override
   public ULFontFile load(String family, int weight, boolean isItalic) {
      String[] variations = new String[]{family + ".ttf", family + ".otf", family + (weight >= 700 ? "bd" : "") + (isItalic ? "i" : "") + ".ttf"};

      for (Path searchRoot : this.fontSearchPaths) {
         for (String fileName : variations) {
            Path fontPath = searchRoot.resolve(fileName);
            if (Files.exists(fontPath)) {
               return ULFontFile.createFromFilePath(fontPath.toAbsolutePath().toString());
            }
         }
      }

      return null;
   }

   public void addFontSearchPath(Path searchPath) {
      this.fontSearchPaths.add(searchPath);
   }

   private void setupSystemFontPaths() {
      String os = System.getProperty("os.name").toLowerCase();
      if (os.contains("win")) {
         this.fontSearchPaths.add(Path.of(System.getenv("SystemRoot"), "Fonts"));
      } else if (os.contains("mac")) {
         this.fontSearchPaths.add(Path.of("/Library/Fonts"));
         this.fontSearchPaths.add(Path.of(System.getProperty("user.home"), "Library/Fonts"));
      } else {
         this.fontSearchPaths.add(Path.of("/usr/share/fonts"));
         this.fontSearchPaths.add(Path.of("/usr/local/share/fonts"));
         this.fontSearchPaths.add(Path.of(System.getProperty("user.home"), ".local/share/fonts"));
      }
   }
}
