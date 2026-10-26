package me.drake.vieclient.modules.client.music;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.LongSupplier;

public final class ProcessMusicProvider implements MusicProvider {
   private static final Set KNOWN_PLAYERS = Set.of(
      "musicbee",
      "foobar2000",
      "aimp",
      "tidal",
      "deezer",
      "amazon music",
      "amazonmusic",
      "itunes",
      "applemusic",
      "vlc",
      "winamp",
      "mediamonkey"
   );
   private static final List APP_NAMES = Set.of(
         "spotify",
         "spotify free",
         "spotify premium",
         "musicbee",
         "tidal",
         "deezer",
         "apple music",
         "itunes",
         "vlc media player",
         "vlc",
         "aimp",
         "foobar2000",
         "winamp",
         "mediamonkey",
         "amazon music"
      )
      .stream()
      .sorted(Comparator.comparingInt(String::length).reversed())
      .toList();
   private static final String[] SEPARATORS = new String[]{
      " - ",
      " — ",
      " – "
   };
   private final WindowScanner scanner;
   private final LongSupplier clock;
   private volatile String currentIdentity = "";
   private volatile long detectedAtMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ProcessMusicProvider(WindowScanner scanner) {
      this(scannerx, System::currentTimeMillis);
   }

   ProcessMusicProvider(WindowScanner scanner, LongSupplier clock) {
      this.scanner = scannerx;
      this.clock = clockx;
   }

   @Override
   public String id() {
      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(104);
      var10000[1] = (byte)(-24);
      var10000[2] = (byte)(115);
      var10000[3] = (byte)(-67);
      var10000[4] = (byte)(-126);
      var10000[5] = (byte)(108);
      var10000[6] = (byte)(34);
      return $$v_s(var10000, 3121770102441365565L);
   }

   @Override
   public Optional poll() {
      long var1 = this.clock.getAsLong();

      for (WindowInfo var4 : this.scanner.scan()) {
         String var5 = normalizeProcess(var4.processName());
         if (KNOWN_PLAYERS.contains(var5)) {
            Optional var6 = parseTitle(var4.title(), var5);
            if (!var6.isEmpty()) {
               ProcessMusicProvider.TitleParts var7 = (ProcessMusicProvider.TitleParts)var6.get();
               String var8 = "process:" + var5 + ":" + var7.title().toLowerCase(Locale.ROOT);
               if (!var8.equals(this.currentIdentity)) {
                  this.currentIdentity = var8;
                  this.detectedAtMs = var1;
               }

               int var9 = (int)Math.min(2147483647L, Math.max(0L, var1 - this.detectedAtMs));
               return Optional.of(new MusicSnapshot(MusicSource.PROCESS, var7.title(), var7.owner(), 0, var9, true, "", var8));
            }
         }
      }

      this.currentIdentity = "";
      this.detectedAtMs = 0L;
      return Optional.empty();
   }

   @Override
   public void close() {
      this.currentIdentity = "";
      this.detectedAtMs = 0L;
   }

   private static String normalizeProcess(String processName) {
      if (processName == null) {
         return "";
      }

      String var1 = processName.trim().toLowerCase(Locale.ROOT);
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(54);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(100);
      var10001[3] = (byte)(-69);
      if (var1.endsWith($$v_s(var10001, 3121770102441365565L))) {
         var1 = var1.substring(0, var1.length() - (4));
      }

      return var1;
   }

   private static Optional parseTitle(String title, String process) {
      if (title != null && !title.isBlank()) {
         String var2 = stripAppSuffix(title.trim(), process);
         if (var2.isBlank()) {
            return Optional.empty();
         }

         if (isAppName(var2.toLowerCase(Locale.ROOT), process)) {
            return Optional.empty();
         }

         int var3 = -1;
         int var4 = 0;

         for (String var8 : SEPARATORS) {
            int var9 = var2.indexOf(var8);
            if (var9 > 0 && var9 < var2.length() - var8.length()) {
               var3 = var9;
               var4 = var8.length();
               break;
            }
         }

         if (var3 < 0) {
            return Optional.of(
               new ProcessMusicProvider.TitleParts(
                  var2, "Unknown Artist"
               )
            );
         } else {
            String var10 = var2.substring(0, var3).trim();
            String var11 = var2.substring(var3 + var4).trim();
            if (var11.isBlank()) {
               return Optional.of(
                  new ProcessMusicProvider.TitleParts(
                     var2, "Unknown Artist"
                  )
               );
            } else {
               return var10.isBlank()
                  ? Optional.of(
                     new ProcessMusicProvider.TitleParts(
                        var11, "Unknown Artist"
                     )
                  )
                  : Optional.of(new ProcessMusicProvider.TitleParts(var11, var10));
            }
         }
      } else {
         return Optional.empty();
      }
   }

   private static String stripAppSuffix(String value, String process) {
      String var2 = value.toLowerCase(Locale.ROOT);

      for (String var6 : SEPARATORS) {
         for (String var8 : APP_NAMES) {
            String var9 = var6 + var8;
            if (var2.endsWith(var9)) {
               return value.substring(0, value.length() - var9.length()).trim();
            }
         }

         if (!process.isBlank()) {
            String var10 = var6 + process;
            if (var2.endsWith(var10)) {
               return value.substring(0, value.length() - var10.length()).trim();
            }
         }
      }

      return value;
   }

   private static boolean isAppName(String lowerTitle, String process) {
      if (lowerTitle.equals(process)) {
         return true;
      }

      for (String var3 : APP_NAMES) {
         if (lowerTitle.equals(var3)) {
            return true;
         }
      }

      return false;
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }

   private record TitleParts(String title, String owner) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
