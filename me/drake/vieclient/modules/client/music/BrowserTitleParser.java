package me.drake.vieclient.modules.client.music;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BrowserTitleParser {
   private static final String[] BROWSER_SUFFIXES = new String[]{
      " - Google Chrome",
      " - Microsoft Edge",
      " - Mozilla Firefox",
      " - Brave",
      " - Thorium",
      " - Chromium",
      " - Opera",
      " - Vivaldi",
      " – Google Chrome",
      " – Microsoft Edge",
      " — Mozilla Firefox"
   };
   private static final Pattern YOUTUBE = Pattern.compile(
      "^(.+?)\\s+-\\s+YouTube(?: Music)?(?:\\s+-\\s+.*)?$",
      2
   );
   private static final Pattern SPOTIFY = Pattern.compile(
      "^(.+?)\\s+-\\s+Spotify(?:\\s+-\\s+.*)?$",
      2
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private BrowserTitleParser() {
   }

   public static Optional parse(String processName, String windowTitle) {
      if (windowTitle != null && !windowTitle.isBlank()) {
         String var2 = windowTitle.trim();
         boolean var3 = isBrowser(processName) || hasBrowserBranding(var2);
         if (!var3) {
            return Optional.empty();
         }

         String var4 = stripNotificationCounter(stripBrowserBranding(var2));
         Matcher var5 = SPOTIFY.matcher(var4);
         if (!var5.matches()) {
            Matcher var14 = YOUTUBE.matcher(var4);
            if (var14.matches()) {
               String var16 = var14.group(1).trim();
               return !var16.isBlank() && !var16.equalsIgnoreCase("YouTube")
                  ? Optional.of(
                     new BrowserTrackHint(MusicSource.YOUTUBE_BROWSER, var16, "YouTube", var16)
                  )
                  : Optional.empty();
            } else {
               return Optional.empty();
            }
         } else {
            String var6 = var5.group(1).trim();
            if (isSpotifyIdle(var6)) {
               return Optional.empty();
            }

            if (SpotifyMusicProvider.isAdvertisement(var6, null)) {
               return Optional.of(
                  new BrowserTrackHint(
                     MusicSource.SPOTIFY_API,
                     "Advertisement",
                     "Spotify",
                     "Advertisement"
                  )
               );
            }

            if (var6.contains(" • ")) {
               int var15 = var6.indexOf(" • ");
               String var17 = var6.substring(0, var15).trim();
               String var19 = var6.substring(var15 + 3).trim();
               return Optional.of(new BrowserTrackHint(MusicSource.SPOTIFY_API, var17, var19, var19 + " - " + var17));
            }

            int var7 = -1;
            int var8 = 0;

            for (String var12 : new String[]{
               " - ",
               " — ",
               " – "
            }) {
               int var13 = var6.indexOf(var12);
               if (var13 > 0 && var13 < var6.length() - var12.length()) {
                  var7 = var13;
                  var8 = var12.length();
                  break;
               }
            }

            if (var7 > 0) {
               String var18 = var6.substring(0, var7).trim();
               String var20 = var6.substring(var7 + var8).trim();
               return Optional.of(new BrowserTrackHint(MusicSource.SPOTIFY_API, var18, var20, var6));
            } else {
               return Optional.of(
                  new BrowserTrackHint(MusicSource.SPOTIFY_API, var6, "Spotify", var6)
               );
            }
         }
      } else {
         return Optional.empty();
      }
   }

   private static boolean isSpotifyIdle(String inner) {
      String var1 = inner.toLowerCase(Locale.ROOT);
      byte[] var10001 = new byte[7];
      var10001[0] = (byte)(109);
      var10001[1] = (byte)(-22);
      var10001[2] = (byte)(115);
      var10001[3] = (byte)(-86);
      var10001[4] = (byte)(-122);
      var10001[5] = (byte)(7);
      var10001[6] = (byte)(115);
      if (!var1.equals($$v_s(var10001, 5190890422858403899L))) {
         var10001 = new byte[10];
         var10001[0] = (byte)(105);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(126);
         var10001[3] = (byte)(-2);
         var10001[4] = (byte)(-97);
         var10001[5] = (byte)(13);
         var10001[6] = (byte)(107);
         var10001[7] = (byte)(25);
         var10001[8] = (byte)(19);
         var10001[9] = (byte)(-48);
         if (!var1.contains($$v_s(var10001, 5190890422858403899L))) {
            var10001 = new byte[12];
            var10001[0] = (byte)(109);
            var10001[1] = (byte)(-22);
            var10001[2] = (byte)(115);
            var10001[3] = (byte)(-86);
            var10001[4] = (byte)(-122);
            var10001[5] = (byte)(7);
            var10001[6] = (byte)(115);
            var10001[7] = (byte)(64);
            var10001[8] = (byte)(16);
            var10001[9] = (byte)(-48);
            var10001[10] = (byte)(-127);
            var10001[11] = (byte)(-109);
            if (!var1.equals($$v_s(var10001, 5190890422858403899L))) {
               var10001 = new byte[15];
               var10001[0] = (byte)(109);
               var10001[1] = (byte)(-22);
               var10001[2] = (byte)(115);
               var10001[3] = (byte)(-86);
               var10001[4] = (byte)(-122);
               var10001[5] = (byte)(7);
               var10001[6] = (byte)(115);
               var10001[7] = (byte)(64);
               var10001[8] = (byte)(6);
               var10001[9] = (byte)(-48);
               var10001[10] = (byte)(-127);
               var10001[11] = (byte)(-101);
               var10001[12] = (byte)(-34);
               var10001[13] = (byte)(-52);
               var10001[14] = (byte)(79);
               if (!var1.equals($$v_s(var10001, 5190890422858403899L))) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   private static boolean hasBrowserBranding(String title) {
      String var1 = title.toLowerCase(Locale.ROOT);

      for (String var5 : BROWSER_SUFFIXES) {
         if (var1.endsWith(var5.toLowerCase(Locale.ROOT))) {
            return true;
         }
      }

      return false;
   }

   private static String stripBrowserBranding(String title) {
      String var1 = title.toLowerCase(Locale.ROOT);

      for (String var5 : BROWSER_SUFFIXES) {
         if (var1.endsWith(var5.toLowerCase(Locale.ROOT))) {
            return title.substring(0, title.length() - var5.length()).trim();
         }
      }

      return title;
   }

   private static String stripNotificationCounter(String title) {
      byte[] var10001 = new byte[11];
      var10001[0] = (byte)(64);
      var10001[1] = (byte)(-58);
      var10001[2] = (byte)(52);
      var10001[3] = (byte)(-126);
      var10001[4] = (byte)(-117);
      var10001[5] = (byte)(74);
      var10001[6] = (byte)(86);
      var10001[7] = (byte)(73);
      var10001[8] = (byte)(42);
      var10001[9] = (byte)(-47);
      var10001[10] = (byte)(-50);
      return title.replaceFirst($$v_s(var10001, 5190890422858403899L), "").trim();
   }

   private static boolean isBrowser(String processName) {
      if (processName == null) {
         return false;
      }

      String var1 = processName.toLowerCase(Locale.ROOT);
      return var1.contains("chrome")
         || var1.contains("msedge")
         || var1.contains("firefox")
         || var1.contains("brave")
         || var1.contains("thorium")
         || var1.contains("chromium")
         || var1.contains("opera")
         || var1.contains("vivaldi");
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
}
