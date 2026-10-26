package me.ayydxn.luminescence.platform.impl;

import com.ibm.icu.text.CharsetDetector;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import me.ayydxn.luminescence.platform.ULFileSystem;

public class StandardULFileSystem implements ULFileSystem {
   private static final Map<String, String> MIME_TYPES = Map.of(
      "html",
      "text/html",
      "css",
      "text/css",
      "js",
      "text/javascript",
      "png",
      "image/png",
      "jpg",
      "image/jpeg",
      "svg",
      "image/svg+xml",
      "json",
      "application/json",
      "ttf",
      "font/ttf",
      "otf",
      "font/otf"
   );

   @Override
   public boolean fileExists(String path) {
      return Files.exists(Path.of(path));
   }

   @Override
   public String getFileMimeType(String path) {
      int lastDotIndex = path.lastIndexOf(46);
      if (lastDotIndex == -1) {
         return "application/octet-stream";
      }

      String fileExtension = path.substring(lastDotIndex + 1).toLowerCase();
      return MIME_TYPES.getOrDefault(fileExtension, "application/octet-stream");
   }

   @Override
   public String getFileCharset(String path) {
      try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path))) {
         CharsetDetector charsetDetector = new CharsetDetector();
         charsetDetector.setText(bufferedInputStream);
         return charsetDetector.detect().getName();
      } catch (IOException exception) {
         exception.printStackTrace();
         return "application/unknown";
      }
   }

   @Override
   public byte[] openFile(String path) {
      try {
         return Files.readAllBytes(Path.of(path));
      } catch (IOException exception) {
         return null;
      }
   }
}
