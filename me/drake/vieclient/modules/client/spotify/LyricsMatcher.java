package me.drake.vieclient.modules.client.spotify;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

final class LyricsMatcher {
   static final double DURATION_TOLERANCE_SECONDS = 35.0;
   private static final int TITLE_EXACT = 40;
   private static final int TITLE_BASE = 30;
   private static final int TITLE_CONTAINS = 12;
   private static final int ARTIST_EXACT = 30;
   private static final int ARTIST_SHARED = 20;
   private static final int ARTIST_CONTAINS = 10;
   private static final Pattern MARKS = Pattern.compile("\\p{M}+");
   private static final Pattern NON_WORD = Pattern.compile(
      "[^\\p{L}\\p{N}]+"
   );
   private static final Pattern APOSTROPHES = Pattern.compile("['’`´]");
   private static final Pattern BRACKETED = Pattern.compile(
      "\\s*[(\\[][^)\\]]*\\b(feat|ft|featuring|with|remaster(ed)?|explicit|clean|mono|stereo|bonus|deluxe|anniversary|edition|single version|album version|official|music\\s+video|video|audio|lyrics?|mv|m/v|hd|4k|visualizer|live|prod)[^)\\]]*[)\\]]",
      2
   );
   private static final Pattern DASHED = Pattern.compile(
      "\\s+[-–—|/]\\s+[^-–—|/]*\\b(remaster(ed)?|explicit|clean|mono|stereo|bonus track|deluxe|anniversary|edition|single version|album version|official|music\\s+video|video|audio|lyrics?|mv|m/v|hd|4k|visualizer|live).*$",
      2
   );
   private static final Pattern FEATURING = Pattern.compile(
      "\\s+(feat\\.?|ft\\.|featuring)\\s+.*$",
      2
   );
   private static final Pattern ARTIST_SPLIT = Pattern.compile(
      "\\s*(?:,|;|/|&|\\+|\\bx\\b|\\band\\b|\\bwith\\b|\\bfeat\\b\\.?|\\bft\\b\\.?|\\bfeaturing\\b)\\s*",
      2
   );
   private static final Pattern CREDIT_SPLIT = Pattern.compile(
      "\\s*[,;]\\s*|\\s+(?:&|x|feat\\.?|ft\\.?|featuring|with)\\s+",
      2
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private LyricsMatcher() {
   }

   static LrclibTrack best(List candidates, LyricsQuery query) {
      if (candidates != null && !candidates.isEmpty() && query != null) {
         LrclibTrack var2 = null;
         int var3 = -1;
         double var4 = Double.MAX_VALUE;

         for (LrclibTrack var7 : candidates) {
            int var8 = score(var7, query);
            if (var8 >= 0) {
               double var9 = drift(var7, query);
               if (var8 > var3 || var8 == var3 && var9 < var4) {
                  var2 = var7;
                  var3 = var8;
                  var4 = var9;
               }
            }
         }

         return var2;
      } else {
         return null;
      }
   }

   static int score(LrclibTrack candidate, LyricsQuery query) {
      if (candidate != null && candidate.usable()) {
         int var2 = durationScore(candidate, query);
         if (var2 < 0) {
            return -1;
         }

         int var3 = titleScore(query.title(), candidate.trackName());
         if (var3 <= 0) {
            return -1;
         }

         int var4 = artistScore(query.artist(), candidate.artistName());
         if (var4 <= 0) {
            return -1;
         }

         int var5 = candidate.hasSynced() ? 25 : (candidate.hasPlain() ? 5 : 8);
         return var3 + var4 + var2 + var5;
      } else {
         return -1;
      }
   }

   private static int durationScore(LrclibTrack candidate, LyricsQuery query) {
      if (query.hasDuration() && !(candidate.duration() <= 0.0)) {
         double var2 = Math.abs(candidate.duration() - query.durationSeconds());
         if (var2 > 35.0) {
            return -1;
         } else {
            return var2 <= 3.0 ? 20 : (int)Math.round(20.0 * (1.0 - var2 / 35.0));
         }
      } else {
         return 0;
      }
   }

   private static double drift(LrclibTrack candidate, LyricsQuery query) {
      return query.hasDuration() && !(candidate.duration() <= 0.0) ? Math.abs(candidate.duration() - query.durationSeconds()) : 3.0;
   }

   static int titleScore(String wanted, String offered) {
      String var2 = norm(wanted);
      String var3 = norm(offered);
      if (var2.isEmpty() || var3.isEmpty()) {
         return 0;
      } else if (var2.equals(var3)) {
         return 40;
      } else {
         String var4 = norm(baseTitle(wanted));
         String var5 = norm(baseTitle(offered));
         if (!var4.isEmpty() && var4.equals(var5)) {
            return 30;
         } else {
            return !containsWords(var4, var5) && !containsWords(var5, var4) ? 0 : 12;
         }
      }
   }

   static int artistScore(String wanted, String offered) {
      String var2 = norm(wanted);
      String var3 = norm(offered);
      if (!var2.isEmpty() && !var3.isEmpty()) {
         if (var2.equals(var3)) {
            return 30;
         }

         Set var4 = artists(wanted);
         Set var5 = artists(offered);

         for (String var7 : var4) {
            if (var5.contains(var7)) {
               return 20;
            }
         }

         String var8 = var4.isEmpty() ? var2 : (String)var4.iterator().next();
         return !containsWords(var3, var8) && !containsWords(var2, var3) ? 0 : 10;
      } else {
         return 0;
      }
   }

   static String baseTitle(String title) {
      if (title == null) {
         return "";
      }

      String var1 = BRACKETED.matcher(title).replaceAll("");
      var1 = DASHED.matcher(var1).replaceFirst("");
      var1 = FEATURING.matcher(var1).replaceFirst("");
      var1 = var1.strip();
      return var1.isEmpty() ? title.strip() : var1;
   }

   static String primaryArtist(String artist) {
      if (artist == null) {
         return "";
      }

      for (String var4 : CREDIT_SPLIT.split(artist.strip())) {
         if (!var4.isBlank()) {
            return var4.strip();
         }
      }

      return artist.strip();
   }

   static String norm(String text) {
      if (text != null && !text.isEmpty()) {
         String var10000 = text.toLowerCase(Locale.ROOT).replace((char)(305), (char)(105));
         byte[] var10002 = new byte[2];
         var10002[0] = (byte)(94);
         var10002[1] = (byte)(-23);
         var10000 = var10000.replace("ß", $$v_s(var10002, -8263864779627311096L));
         var10002 = new byte[2];
         var10002[0] = (byte)(76);
         var10002[1] = (byte)(-1);
         var10000 = var10000.replace("æ", $$v_s(var10002, -8263864779627311096L));
         var10002 = new byte[2];
         var10002[0] = (byte)(66);
         var10002[1] = (byte)(-1);
         var10000 = var10000.replace("œ", $$v_s(var10002, -8263864779627311096L))
            .replace((char)(248), (char)(111))
            .replace((char)(322), (char)(108))
            .replace((char)(273), (char)(100));
         var10002 = new byte[5];
         var10002[0] = (byte)(13);
         var10002[1] = (byte)(-5);
         var10002[2] = (byte)(114);
         var10002[3] = (byte)(-70);
         var10002[4] = (byte)(93);
         String var1 = var10000.replace("&", $$v_s(var10002, -8263864779627311096L));
         var1 = MARKS.matcher(Normalizer.normalize(var1, Form.NFD)).replaceAll("");
         var1 = APOSTROPHES.matcher(var1).replaceAll("");
         return NON_WORD.matcher(var1).replaceAll(" ").strip();
      } else {
         return "";
      }
   }

   private static Set artists(String credit) {
      LinkedHashSet var1 = new LinkedHashSet();

      for (String var5 : ARTIST_SPLIT.split(credit)) {
         String var6 = norm(var5);
         if (!var6.isEmpty()) {
            var1.add(var6);
         }
      }

      return var1;
   }

   private static boolean containsWords(String haystack, String needle) {
      return needle.length() >= (3) && haystack.length() >= needle.length() ? (" " + haystack + " ").contains(" " + needle + " ") : false;
   }

   static List searchTerms(LyricsQuery query) {
      ArrayList var1 = new ArrayList(6);
      String var2 = query.title();
      String var3 = query.artist();
      boolean var4 = isGenericArtist(var3);
      String[] var5 = splitTitle(var2);
      if (var5 != null) {
         String var6 = var5[0].trim();
         String var7 = var5[1].trim();
         addTerm(var1, baseTitle(var7), primaryArtist(var6));
         addTerm(var1, var7, var6);
         addTerm(var1, baseTitle(var6), primaryArtist(var7));
         addTerm(var1, var6, var7);
      }

      if (!var4 && !var3.isBlank()) {
         addTerm(var1, baseTitle(var2), primaryArtist(var3));
         addTerm(var1, var2, var3);
      } else if (var3.isBlank() || var4) {
         addTerm(var1, baseTitle(var2), "");
         addTerm(var1, var2, "");
      }

      return var1;
   }

   static boolean isGenericArtist(String artist) {
      if (artist != null && !artist.isBlank()) {
         String var1 = artist.trim().toLowerCase(Locale.ROOT);
         byte[] var10001 = new byte[7];
         var10001[0] = (byte)(84);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(105);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(8);
         var10001[5] = (byte)(102);
         var10001[6] = (byte)(54);
         if (!var1.equals($$v_s(var10001, -8263864779627311096L))) {
            var10001 = new byte[7];
            var10001[0] = (byte)(79);
            var10001[1] = (byte)(-24);
            var10001[2] = (byte)(115);
            var10001[3] = (byte)(-87);
            var10001[4] = (byte)(14);
            var10001[5] = (byte)(97);
            var10001[6] = (byte)(33);
            if (!var1.equals($$v_s(var10001, -8263864779627311096L))) {
               var10001 = new byte[7];
               var10001[0] = (byte)(94);
               var10001[1] = (byte)(-22);
               var10001[2] = (byte)(115);
               var10001[3] = (byte)(-86);
               var10001[4] = (byte)(20);
               var10001[5] = (byte)(98);
               var10001[6] = (byte)(42);
               if (!var1.equals($$v_s(var10001, -8263864779627311096L))) {
                  var10001 = new byte[7];
                  var10001[0] = (byte)(88);
                  var10001[1] = (byte)(-12);
                  var10001[2] = (byte)(119);
                  var10001[3] = (byte)(-80);
                  var10001[4] = (byte)(18);
                  var10001[5] = (byte)(115);
                  var10001[6] = (byte)(61);
                  if (!var1.equals($$v_s(var10001, -8263864779627311096L))) {
                     var10001 = new byte[15];
                     var10001[0] = (byte)(91);
                     var10001[1] = (byte)(-5);
                     var10001[2] = (byte)(110);
                     var10001[3] = (byte)(-73);
                     var10001[4] = (byte)(18);
                     var10001[5] = (byte)(113);
                     var10001[6] = (byte)(32);
                     var10001[7] = (byte)(-123);
                     var10001[8] = (byte)(36);
                     var10001[9] = (byte)(-48);
                     var10001[10] = (byte)(-112);
                     var10001[11] = (byte)(-97);
                     var10001[12] = (byte)(86);
                     var10001[13] = (byte)(-88);
                     var10001[14] = (byte)(8);
                     if (!var1.equals($$v_s(var10001, -8263864779627311096L))) {
                        return false;
                     }
                  }
               }
            }
         }

         return true;
      } else {
         return true;
      }
   }

   static String[] splitTitle(String title) {
      if (title == null) {
         return null;
      }

      String[] var10000 = new String[5];
      byte[] var10003 = new byte[3];
      var10003[0] = (byte)(13);
      var10003[1] = (byte)(-73);
      var10003[2] = (byte)(60);
      var10000[0] = $$v_s(var10003, -8263864779627311096L);
      var10003 = new byte[5];
      var10003[0] = (byte)(13);
      var10003[1] = (byte)(120);
      var10003[2] = (byte)(-100);
      var10003[3] = (byte)(77);
      var10003[4] = (byte)(93);
      var10000[1] = $$v_s(var10003, -8263864779627311096L);
      int var10002 = 2;
      var10003 = new byte[5];
      var10003[0] = (byte)(13);
      var10003[1] = (byte)(120);
      var10003[2] = (byte)(-100);
      var10003[3] = (byte)(74);
      var10003[4] = (byte)(93);
      var10000[var10002] = $$v_s(var10003, -8263864779627311096L);
      var10002 = 3;
      var10003 = new byte[5];
      var10003[0] = (byte)(13);
      var10003[1] = (byte)(120);
      var10003[2] = (byte)(-100);
      var10003[3] = (byte)(124);
      var10003[4] = (byte)(93);
      var10000[var10002] = $$v_s(var10003, -8263864779627311096L);
      var10002 = 4;
      var10003 = new byte[3];
      var10003[0] = (byte)(13);
      var10003[1] = (byte)(-26);
      var10003[2] = (byte)(60);
      var10000[var10002] = $$v_s(var10003, -8263864779627311096L);

      for (String var4 : var10000) {
         int var5 = title.indexOf(var4);
         if (var5 > 0 && var5 < title.length() - var4.length()) {
            var10000 = new String[2];
            var10000[0] = title.substring(0, var5);
            var10000[1] = title.substring(var5 + var4.length());
            return var10000;
         }
      }

      return null;
   }

   private static void addTerm(List list, String title, String artist) {
      if (title != null && !title.isBlank()) {
         if (artist == null) {
            artist = "";
         }

         for (String[] var4 : list) {
            if (var4[0].equalsIgnoreCase(title) && var4[1].equalsIgnoreCase(artist)) {
               return;
            }
         }

         String[] var10001 = new String[2];
         var10001[0] = title;
         var10001[1] = artist;
         list.add(var10001);
      }
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
