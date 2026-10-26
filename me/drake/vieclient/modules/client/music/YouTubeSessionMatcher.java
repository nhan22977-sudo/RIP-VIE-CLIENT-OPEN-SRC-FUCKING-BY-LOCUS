package me.drake.vieclient.modules.client.music;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

final class YouTubeSessionMatcher {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private YouTubeSessionMatcher() {
   }

   static boolean matches(BrowserTrackHint hint, WindowsMediaSessionSnapshot session) {
      if (hint != null && session != null && session.isSupportedBrowser()) {
         String var2 = normalize(hint.title());
         String var3 = normalize(session.title());
         if (var2.isBlank() || var3.isBlank()) {
            return false;
         } else if (var2.equals(var3)) {
            return true;
         } else {
            Set var4 = tokens(var2);
            Set var5 = tokens(var3);
            if (!var4.isEmpty() && !var5.isEmpty()) {
               long var6 = var4.stream().filter(var5::contains).count();
               int var8 = Math.min(var4.size(), var5.size());
               return var6 >= Math.min(2, var8) && (double)var6 / var8 >= 0.75;
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private static String normalize(String value) {
      if (value == null) {
         return "";
      }

      String var1 = Normalizer.normalize(value, Form.NFKD).toLowerCase(Locale.ROOT);
      var1 = var1.replaceFirst(
         "\\s*[-–—]\\s*(?:youtube(?: music)?|spotify)$",
         ""
      );
      return var1.replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
   }

   private static Set tokens(String value) {
      byte[] var10003 = new byte[3];
      var10003[0] = (byte)(100);
      var10003[1] = (byte)(-23);
      var10003[2] = (byte)(55);
      return new HashSet<>(Arrays.asList(value.split($$v_s(var10003, -4717684332574814179L))));
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
