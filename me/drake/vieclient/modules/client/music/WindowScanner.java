package me.drake.vieclient.modules.client.music;

import java.util.List;
import java.util.Locale;

@FunctionalInterface
public interface WindowScanner {
   List scan();

   static WindowScanner system() {
      String var0 = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
      return var0.contains("win") ? new WindowsWindowScanner() : List::of;
   }
}
