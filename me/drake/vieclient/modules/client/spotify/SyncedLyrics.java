package me.drake.vieclient.modules.client.spotify;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SyncedLyrics {
   public static final SyncedLyrics EMPTY = new SyncedLyrics(new SyncedLyrics.Line[0]);
   private static final Pattern TIMESTAMP = Pattern.compile(
      "(\\d{1,3}):(\\d{1,2})(?:[.:](\\d{1,3}))?"
   );
   private static final Pattern WORD_TIMESTAMP = Pattern.compile(
      "<(\\d{1,3}):(\\d{1,2})(?:[.:](\\d{1,3}))?>"
   );
   private static final Pattern METADATA = Pattern.compile(
      "[A-Za-z#][A-Za-z0-9_ ]*:.*"
   );
   private static final Pattern OFFSET = Pattern.compile(
      "offset\\s*:\\s*([+-]?\\d{1,7})\\s*"
   );
   private static final Pattern WHITESPACE = Pattern.compile("\\s+");
   private final SyncedLyrics.Line[] lines;
   private final long[] times;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SyncedLyrics(SyncedLyrics.Line[] lines) {
      this.lines = linesx;
      this.times = new long[linesx.length];

      for (int var2 = 0; var2 < linesx.length; var2++) {
         this.times[var2] = linesx[var2].timeMs();
      }
   }

   public static SyncedLyrics parse(String lrc) {
      if (lrc != null && !lrc.isBlank()) {
         ArrayList var1 = new ArrayList();
         ArrayList var2 = new ArrayList(4);
         int var3 = 0;

         for (String var7 : lrc.split("\\r?\\n|\\r")) {
            String var8 = var7.strip();
            if (!var8.isEmpty() && var8.charAt(0) == '\ufeff') {
               var8 = var8.substring(1).strip();
            }

            var2.clear();
            int var9 = 0;

            while (var9 < var8.length() && var8.charAt(var9) == '[') {
               int var10 = var8.indexOf(93, var9 + 1);
               if (var10 < 0) {
                  break;
               }

               String var11 = var8.substring(var9 + 1, var10).strip();
               Matcher var12 = TIMESTAMP.matcher(var11);
               if (var12.matches()) {
                  var2.add(toMillis(var12));
               } else {
                  if (!METADATA.matcher(var11).matches()) {
                     break;
                  }

                  Matcher var13 = OFFSET.matcher(var11.toLowerCase());
                  if (var13.matches()) {
                     var3 = Integer.parseInt(var13.group(1));
                  }
               }

               var9 = var10 + 1;

               while (var9 < var8.length() && var8.charAt(var9) == ' ') {
                  var9++;
               }
            }

            if (!var2.isEmpty()) {
               String var20 = var8.substring(var9);
               String var21 = cleanText(var20);
               long var22 = var2.stream().mapToLong(Long::longValue).min().orElse(0L);
               List var14 = wordTimes(var20, var21, var22);

               for (Long var16 : var2) {
                  var1.add(new SyncedLyrics.Line(var16, var21, shift(var14, var16 - var22)));
               }
            }
         }

         if (var1.isEmpty()) {
            return EMPTY;
         }

         SyncedLyrics.Line[] var17 = var1.toArray(new SyncedLyrics.Line[0]);
         if (var3 != 0) {
            for (int var18 = 0; var18 < var17.length; var18++) {
               SyncedLyrics.Line var19 = var17[var18];
               var17[var18] = new SyncedLyrics.Line(Math.max(0L, var19.timeMs() - var3), var19.text(), shift(var19.wordTimes(), -var3));
            }
         }

         Arrays.sort(var17, (a, b) -> Long.compare(a.timeMs(), b.timeMs()));
         return new SyncedLyrics(var17);
      } else {
         return EMPTY;
      }
   }

   public static SyncedLyrics fromPlain(List plainLines, long durationMs) {
      if (plainLines != null && !plainLines.isEmpty()) {
         long var3 = durationMs > 0L ? durationMs : plainLines.size() * 4000L;
         long var5 = Math.max(1000L, var3 / (plainLines.size() + 1));
         SyncedLyrics.Line[] var7 = new SyncedLyrics.Line[plainLines.size()];

         for (int var8 = 0; var8 < plainLines.size(); var8++) {
            var7[var8] = new SyncedLyrics.Line((var8 + 1) * var5, (String)plainLines.get(var8), List.of());
         }

         return new SyncedLyrics(var7);
      } else {
         return EMPTY;
      }
   }

   public int indexAt(long timeMs) {
      int var3 = 0;
      int var4 = this.times.length;

      while (var3 < var4) {
         int var5 = var3 + var4 >>> 1;
         if (this.times[var5] <= timeMs) {
            var3 = var5 + 1;
         } else {
            var4 = var5;
         }
      }

      return var3 - 1;
   }

   public SyncedLyrics.Line line(int index) {
      return this.lines[index];
   }

   public int size() {
      return this.lines.length;
   }

   public boolean isEmpty() {
      return this.lines.length == 0;
   }

   public int firstWithTimeOf(int last) {
      if (last >= 0 && last < this.lines.length) {
         long var2 = this.lines[last].timeMs();
         int var4 = last;

         while (var4 > 0 && this.lines[var4 - 1].timeMs() == var2) {
            var4--;
         }

         return var4;
      } else {
         return -1;
      }
   }

   public int lastWithTimeOf(int first) {
      if (first >= 0 && first < this.lines.length) {
         long var2 = this.lines[first].timeMs();
         int var4 = first;

         while (var4 + 1 < this.lines.length && this.lines[var4 + 1].timeMs() == var2) {
            var4++;
         }

         return var4;
      } else {
         return -1;
      }
   }

   public List texts() {
      ArrayList var1 = new ArrayList(this.lines.length);

      for (SyncedLyrics.Line var5 : this.lines) {
         if (!var5.isGap()) {
            var1.add(var5.text());
         }
      }

      return List.copyOf(var1);
   }

   public SyncedLyrics.Line[] allLines() {
      return this.lines;
   }

   private static long toMillis(Matcher time) {
      long var1 = Long.parseLong(time.group(1));
      long var3 = Long.parseLong(time.group(2));
      long var5 = 0L;
      String var7 = time.group(3);
      if (var7 != null) {
         var5 = Long.parseLong(var7);
         if (var7.length() == 1) {
            var5 *= 100L;
         } else if (var7.length() == (2)) {
            var5 *= 10L;
         }
      }

      return var1 * 60000L + var3 * 1000L + var5;
   }

   private static String cleanText(String text) {
      if (text.indexOf(60) >= 0) {
         text = WORD_TIMESTAMP.matcher(text).replaceAll("");
      }

      return WHITESPACE.matcher(text).replaceAll(" ").strip();
   }

   private static List wordTimes(String body, String text, long lineTime) {
      if (body.indexOf(60) >= 0 && !text.isEmpty()) {
         ArrayList var4 = new ArrayList();
         Matcher var5 = WORD_TIMESTAMP.matcher(body);
         long var6 = -1L;
         boolean var8 = false;
         int var9 = 0;

         while (var9 < body.length()) {
            if (body.charAt(var9) != (60) || !var5.region(var9, body.length()).lookingAt()) {
               char var12 = body.charAt(var9++);
               if (var12 <= (32)) {
                  var8 = false;
               } else if (!var8) {
                  var8 = true;
                  if (var6 < 0L) {
                     return List.of();
                  }

                  if (var4.isEmpty() ? var6 < lineTime - 1000L : var6 < (Long)var4.get(var4.size() - 1)) {
                     return List.of();
                  }

                  var4.add(var6);
                  var6 = -1L;
               }
            } else {
               var9 = var5.end();
               boolean var10 = var8 && var9 < body.length() && body.charAt(var9) > (32);
               if (!var10) {
                  var6 = toMillis(var5);
               }
            }
         }

         int var13 = 1;

         for (int var11 = 0; var11 < text.length(); var11++) {
            if (text.charAt(var11) == (32)) {
               var13++;
            }
         }

         return var4.size() == var13 ? var4 : List.of();
      } else {
         return List.of();
      }
   }

   private static List shift(List times, long by) {
      if (by != 0L && !timesx.isEmpty()) {
         ArrayList var3 = new ArrayList(timesx.size());

         for (long var5 : timesx) {
            var3.add(Math.max(0L, var5 + by));
         }

         return var3;
      } else {
         return timesx;
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

   public record Line(long timeMs, String text, List<Long> wordTimes) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public boolean isGap() {
         return this.text == null || this.text.isEmpty();
      }
   }
}
