package me.drake.vieclient.integration;

import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.api.events.client.ClientEmoteAPI;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.main.mixinFunctions.IPlayerEntity;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import me.drake.vieclient.VieClientMod;
import net.minecraft.class_310;

public final class EmotecraftBridge {
   private static final String RESOURCE_DIR = "/assets/vie_client/emotes/";
   private static final String RESOURCE_INDEX = "/assets/vie_client/emotes/index.txt";
   private static final String USER_DIR = "config/vie/emotes";
   private static final String EXTENSION = ".emotecraft";
   private static final int MAX_NAME_LENGTH = 24;
   private static final List EMOTES = new ArrayList();
   private static boolean loaded;
   private static boolean emotecraftMissing;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private EmotecraftBridge() {
   }

   public static synchronized List emotes() {
      ensureLoaded();
      return Collections.unmodifiableList(EMOTES);
   }

   public static synchronized int count() {
      ensureLoaded();
      return EMOTES.size();
   }

   public static synchronized String nameAt(int index) {
      ensureLoaded();
      return index >= 0 && index < EMOTES.size() ? ((EmotecraftBridge.Emote)EMOTES.get(index)).name : null;
   }

   public static synchronized int lengthTicksAt(int index) {
      ensureLoaded();
      return index >= 0 && index < EMOTES.size() ? ((EmotecraftBridge.Emote)EMOTES.get(index)).lengthTicks : 0;
   }

   public static synchronized boolean play(int index) {
      ensureLoaded();
      if (index >= 0 && index < EMOTES.size()) {
         try {
            return ((EmotecraftBridge.Emote)EMOTES.get(index)).holder.playEmote();
         } catch (Throwable var2) {
            byte[] var10001 = new byte[32];
            var10001[0] = (byte)(119);
            var10001[1] = (byte)(-52);
            var10001[2] = (byte)(117);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(17);
            var10001[5] = (byte)(-99);
            var10001[6] = (byte)(62);
            var10001[7] = (byte)(-66);
            var10001[8] = (byte)(42);
            var10001[9] = (byte)(-42);
            var10001[10] = (byte)(-71);
            var10001[11] = (byte)(-42);
            var10001[12] = (byte)(73);
            var10001[13] = (byte)(70);
            var10001[14] = (byte)(10);
            var10001[15] = (byte)(-49);
            var10001[16] = (byte)(24);
            var10001[17] = (byte)(106);
            var10001[18] = (byte)(-94);
            var10001[19] = (byte)(-63);
            var10001[20] = (byte)(-106);
            var10001[21] = (byte)(33);
            var10001[22] = (byte)(119);
            var10001[23] = (byte)(-25);
            var10001[24] = (byte)(-11);
            var10001[25] = (byte)(107);
            var10001[26] = (byte)(-76);
            var10001[27] = (byte)(35);
            var10001[28] = (byte)(-73);
            var10001[29] = (byte)(22);
            var10001[30] = (byte)(91);
            var10001[31] = (byte)(54);
            VieClientMod.LOGGER.debug($$v_s(var10001, -913052136836444151L), var2);
            return false;
         }
      } else {
         return false;
      }
   }

   public static synchronized boolean loop(int index, int startTick) {
      ensureLoaded();
      if (index >= 0 && index < EMOTES.size()) {
         try {
            class_310 var2 = class_310.method_1551();
            if (var2 != null && var2.field_1724 != null) {
               Animation var3 = ((EmotecraftBridge.Emote)EMOTES.get(index)).holder.getEmote();
               if (var3 == null) {
                  return false;
               }

               ((IPlayerEntity)var2.field_1724).emotecraft$playEmote(var3, Animation.LoopType.LOOP, startTick, false);
               return true;
            } else {
               return false;
            }
         } catch (Throwable var4) {
            byte[] var10001 = new byte[32];
            var10001[0] = (byte)(119);
            var10001[1] = (byte)(-52);
            var10001[2] = (byte)(117);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(17);
            var10001[5] = (byte)(-99);
            var10001[6] = (byte)(62);
            var10001[7] = (byte)(-66);
            var10001[8] = (byte)(42);
            var10001[9] = (byte)(-42);
            var10001[10] = (byte)(-71);
            var10001[11] = (byte)(-42);
            var10001[12] = (byte)(73);
            var10001[13] = (byte)(70);
            var10001[14] = (byte)(10);
            var10001[15] = (byte)(-49);
            var10001[16] = (byte)(24);
            var10001[17] = (byte)(106);
            var10001[18] = (byte)(-94);
            var10001[19] = (byte)(-63);
            var10001[20] = (byte)(-106);
            var10001[21] = (byte)(33);
            var10001[22] = (byte)(107);
            var10001[23] = (byte)(-28);
            var10001[24] = (byte)(-5);
            var10001[25] = (byte)(98);
            var10001[26] = (byte)(-76);
            var10001[27] = (byte)(35);
            var10001[28] = (byte)(-73);
            var10001[29] = (byte)(22);
            var10001[30] = (byte)(91);
            var10001[31] = (byte)(54);
            VieClientMod.LOGGER.debug($$v_s(var10001, -913052136836444151L), var4);
            return false;
         }
      } else {
         return false;
      }
   }

   public static void stop() {
      try {
         ClientEmoteAPI.stopEmote();
      } catch (Throwable var1) {
         byte[] var10001 = new byte[32];
         var10001[0] = (byte)(119);
         var10001[1] = (byte)(-52);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(17);
         var10001[5] = (byte)(-99);
         var10001[6] = (byte)(62);
         var10001[7] = (byte)(-66);
         var10001[8] = (byte)(42);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-71);
         var10001[11] = (byte)(-42);
         var10001[12] = (byte)(73);
         var10001[13] = (byte)(70);
         var10001[14] = (byte)(10);
         var10001[15] = (byte)(-49);
         var10001[16] = (byte)(24);
         var10001[17] = (byte)(106);
         var10001[18] = (byte)(-94);
         var10001[19] = (byte)(-63);
         var10001[20] = (byte)(-106);
         var10001[21] = (byte)(33);
         var10001[22] = (byte)(116);
         var10001[23] = (byte)(-1);
         var10001[24] = (byte)(-5);
         var10001[25] = (byte)(98);
         var10001[26] = (byte)(-76);
         var10001[27] = (byte)(35);
         var10001[28] = (byte)(-73);
         var10001[29] = (byte)(22);
         var10001[30] = (byte)(91);
         var10001[31] = (byte)(54);
         VieClientMod.LOGGER.debug($$v_s(var10001, -913052136836444151L), var1);
      }
   }

   public static synchronized void reload() {
      EMOTES.clear();
      loaded = false;
      emotecraftMissing = false;
   }

   private static void ensureLoaded() {
      if (!loaded && !emotecraftMissing) {
         loaded = true;

         try {
            loadBundled();
            loadUserFolder();
            VieClientMod.LOGGER
               .info(
                  "[VieClient] Loaded {} emote(s).",
                  EMOTES.size()
               );
         } catch (NoClassDefFoundError var1) {
            emotecraftMissing = true;
            VieClientMod.LOGGER
               .warn(
                  "[VieClient] Emotecraft is not installed; the emote wheel is disabled."
               );
         } catch (Throwable var2) {
            VieClientMod.LOGGER
               .error(
                  "[VieClient] Failed to load emotes",
                  var2
               );
         }
      }
   }

   private static void loadBundled() {
      ArrayList var0 = new ArrayList();

      try {
         label116: {
            try (InputStream var1 = EmotecraftBridge.class
                  .getResourceAsStream(
                     "/assets/vie_client/emotes/index.txt"
                  )) {
               if (var1 != null) {
                  String[] var2 = new String(var1.readAllBytes(), StandardCharsets.UTF_8).split("\\R");
                  int var3 = var2.length;
                  int var4 = 0;

                  while (true) {
                     if (var4 >= var3) {
                        break label116;
                     }

                     String var5 = var2[var4];
                     String var6 = var5.trim();
                     if (!var6.isEmpty() && !var6.startsWith("#")) {
                        var0.add(var6);
                     }

                     var4++;
                  }
               }

               VieClientMod.LOGGER
                  .warn(
                     "[VieClient] Bundled emote index missing at {}",
                     "/assets/vie_client/emotes/index.txt"
                  );
            }

            return;
         }
      } catch (Throwable var12) {
         VieClientMod.LOGGER
            .error(
               "[VieClient] Could not read the bundled emote index",
               var12
            );
         return;
      }

      for (String var14 : var0) {
         try (InputStream var15 = EmotecraftBridge.class.getResourceAsStream("/assets/vie_client/emotes/" + var14)) {
            if (var15 != null) {
               register(var15.readAllBytes(), var14);
            } else {
               VieClientMod.LOGGER
                  .warn(
                     "[VieClient] Bundled emote {} is listed but missing",
                     var14
                  );
            }
         } catch (Throwable var10) {
            VieClientMod.LOGGER
               .error(
                  "[VieClient] Could not load bundled emote {}",
                  var14,
                  var10
               );
         }
      }
   }

   private static void loadUserFolder() {
      class_310 var0 = class_310.method_1551();
      if (var0 != null && var0.field_1697 != null) {
         Path var1 = var0.field_1697
            .toPath()
            .resolve("config/vie/emotes");
         if (Files.isDirectory(var1)) {
            ArrayList var2 = new ArrayList();

            try (Stream var3 = Files.list(var1)) {
               var3.filter(x$0 -> Files.isRegularFile(x$0)).filter(path -> {
                  String var10000 = path.getFileName().toString().toLowerCase(Locale.ROOT);
                  byte[] var10001 = new byte[11];
                  var10001[0] = (byte)(2);
                  var10001[1] = (byte)(-1);
                  var10001[2] = (byte)(113);
                  var10001[3] = (byte)(-79);
                  var10001[4] = (byte)(38);
                  var10001[5] = (byte)(-108);
                  var10001[6] = (byte)(52);
                  var10001[7] = (byte)(-87);
                  var10001[8] = (byte)(37);
                  var10001[9] = (byte)(-60);
                  var10001[10] = (byte)(-112);
                  return var10000.endsWith($$v_s(var10001, -913052136836444151L));
               }).sorted().forEach(var2::add);
            } catch (Throwable var9) {
               VieClientMod.LOGGER
                  .error(
                     "[VieClient] Could not scan {}",
                     var1,
                     var9
                  );
               return;
            }

            for (Path var4 : var2) {
               try {
                  register(Files.readAllBytes(var4), var4.getFileName().toString());
               } catch (Throwable var7) {
                  VieClientMod.LOGGER
                     .error(
                        "[VieClient] Could not load emote {}",
                        var4,
                        var7
                     );
               }
            }
         }
      }
   }

   private static void register(byte[] bytes, String fileName) throws Exception {
      Map var2 = UniversalEmoteSerializer.readData(new ByteArrayInputStream(bytes), fileName);
      if (var2 != null && !var2.isEmpty()) {
         for (Animation var4 : var2.values()) {
            if (var4 != null) {
               String var5 = displayName(var4, fileName);
               if (!hasName(var5)) {
                  EmoteHolder var6 = EmoteHolder.addEmoteToList(var4, null);
                  if (var6 != null) {
                     int var7 = Math.max(1, (int)var4.length());
                     EMOTES.add(new EmotecraftBridge.Emote(var5, var6, var7));
                  }
               }
            }
         }
      } else {
         byte[] var10001 = new byte[36];
         var10001[0] = (byte)(119);
         var10001[1] = (byte)(-52);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(17);
         var10001[5] = (byte)(-99);
         var10001[6] = (byte)(62);
         var10001[7] = (byte)(-66);
         var10001[8] = (byte)(42);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-71);
         var10001[11] = (byte)(-42);
         var10001[12] = (byte)(113);
         var10001[13] = (byte)(84);
         var10001[14] = (byte)(95);
         var10001[15] = (byte)(-64);
         var10001[16] = (byte)(19);
         var10001[17] = (byte)(36);
         var10001[18] = (byte)(-72);
         var10001[19] = (byte)(-49);
         var10001[20] = (byte)(-117);
         var10001[21] = (byte)(111);
         var10001[22] = (byte)(116);
         var10001[23] = (byte)(-85);
         var10001[24] = (byte)(-6);
         var10001[25] = (byte)(125);
         var10001[26] = (byte)(-76);
         var10001[27] = (byte)(39);
         var10001[28] = (byte)(-76);
         var10001[29] = (byte)(16);
         var10001[30] = (byte)(66);
         var10001[31] = (byte)(50);
         var10001[32] = (byte)(-72);
         var10001[33] = (byte)(83);
         var10001[34] = (byte)(19);
         var10001[35] = (byte)(16);
         VieClientMod.LOGGER.warn($$v_s(var10001, -913052136836444151L), fileName);
      }
   }

   private static boolean hasName(String name) {
      for (EmotecraftBridge.Emote var2 : EMOTES) {
         if (var2.name.equalsIgnoreCase(name)) {
            return true;
         }
      }

      return false;
   }

   private static String displayName(Animation animation, String fileName) {
      String var2 = null;

      try {
         var2 = animation.getNameOrId();
      } catch (Throwable var4) {
      }

      String var3 = sanitize(extractText(var2));
      return normalizeLabel(var3.isEmpty() ? prettifyFileName(fileName) : var3);
   }

   private static String extractText(String raw) {
      if (raw == null) {
         return "";
      }

      String var1 = raw.trim();
      if (!var1.startsWith("{") && !var1.startsWith("[")) {
         return var1;
      }

      byte[] var10001 = new byte[8];
      var10001[0] = (byte)(74);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(112);
      var10001[3] = (byte)(-78);
      var10001[4] = (byte)(48);
      var10001[5] = (byte)(-112);
      var10001[6] = (byte)(52);
      var10001[7] = (byte)(-80);
      String var2 = firstJsonField(var1, $$v_s(var10001, -913052136836444151L));
      if (!var2.isEmpty()) {
         return var2;
      }

      var10001 = new byte[4];
      var10001[0] = (byte)(88);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(100);
      var10001[3] = (byte)(-86);
      String var3 = firstJsonField(var1, $$v_s(var10001, -913052136836444151L));
      if (!var3.isEmpty()) {
         return var3;
      }

      var10001 = new byte[9];
      var10001[0] = (byte)(88);
      var10001[1] = (byte)(-24);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-80);
      var10001[4] = (byte)(33);
      var10001[5] = (byte)(-99);
      var10001[6] = (byte)(54);
      var10001[7] = (byte)(-81);
      var10001[8] = (byte)(33);
      String var4 = firstJsonField(var1, $$v_s(var10001, -913052136836444151L));
      if (var4.isEmpty()) {
         return "";
      }

      int var5 = var4.lastIndexOf(46);
      String var6 = var5 >= 0 && var5 + 1 < var4.length() ? var4.substring(var5 + 1) : var4;
      return var6.replace((char)(95), (char)(32)).replace((char)(45), (char)(32));
   }

   private static String firstJsonField(String json, String field) {
      String var2 = "\"" + field + "\"";
      int var3 = json.indexOf(var2);
      if (var3 < 0) {
         return "";
      }

      int var4 = json.indexOf(58, var3 + var2.length());
      if (var4 < 0) {
         return "";
      }

      int var5 = json.indexOf(34, var4 + 1);
      if (var5 < 0) {
         return "";
      }

      StringBuilder var6 = new StringBuilder();

      for (int var7 = var5 + 1; var7 < json.length(); var7++) {
         char var8 = json.charAt(var7);
         if (var8 == (92) && var7 + 1 < json.length()) {
            var6.append(json.charAt(++var7));
         } else {
            if (var8 == (34)) {
               break;
            }

            var6.append(var8);
         }
      }

      return var6.toString().trim();
   }

   private static String sanitize(String text) {
      if (text != null && !text.isEmpty()) {
         StringBuilder var1 = new StringBuilder(text.length());
         boolean var2 = false;

         for (int var3 = 0; var3 < text.length(); var3++) {
            int var4 = text.charAt(var3);
            if (var4 == (167)) {
               var3++;
            } else {
               if (var4 < (32) || var4 > (126)) {
                  var4 = 32;
               }

               if (var4 == (95)) {
                  var4 = 32;
               }

               if (var4 == (32)) {
                  if (var2 || var1.length() == 0) {
                     continue;
                  }

                  var2 = true;
               } else {
                  var2 = false;
               }

               var1.append((char)var4);
               if (var1.length() >= (24)) {
                  break;
               }
            }
         }

         return var1.toString().trim();
      } else {
         return "";
      }
   }

   private static String prettifyFileName(String fileName) {
      String var1 = fileName == null ? "" : fileName;
      int var2 = var1.lastIndexOf(46);
      if (var2 > 0) {
         var1 = var1.substring(0, var2);
      }

      var1 = sanitize(var1.replace((char)(45), (char)(32)));
      String var4;
      if (var1.isEmpty()) {
         byte[] var10000 = new byte[5];
         var10000[0] = (byte)(105);
         var10000[1] = (byte)(-9);
         var10000[2] = (byte)(115);
         var10000[3] = (byte)(-86);
         var10000[4] = (byte)(55);
         var4 = $$v_s(var10000, -913052136836444151L);
      } else {
         var4 = var1;
      }

      return var4;
   }

   private static String normalizeLabel(String text) {
      if (text != null && !text.isEmpty()) {
         boolean var1 = false;

         for (int var2 = 0; var2 < text.length(); var2++) {
            if (Character.isUpperCase(text.charAt(var2))) {
               var1 = true;
               break;
            }
         }

         StringBuilder var7 = new StringBuilder(text.length());
         boolean var3 = true;

         for (int var4 = 0; var4 < text.length(); var4++) {
            char var5 = text.charAt(var4);
            boolean var6 = var3 && (!var1 || var7.length() == 0);
            var7.append(var6 ? Character.toUpperCase(var5) : var5);
            var3 = var5 == (32);
         }

         return var7.toString();
      } else {
         byte[] var10000 = new byte[5];
         var10000[0] = (byte)(105);
         var10000[1] = (byte)(-9);
         var10000[2] = (byte)(115);
         var10000[3] = (byte)(-86);
         var10000[4] = (byte)(55);
         return $$v_s(var10000, -913052136836444151L);
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

   public static final class Emote {
      private final String name;
      private final EmoteHolder holder;
      private final int lengthTicks;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private Emote(String name, EmoteHolder holder, int lengthTicks) {
         this.name = namex;
         this.holder = holderx;
         this.lengthTicks = lengthTicksx;
      }

      public String getName() {
         return this.name;
      }

      public int getLengthTicks() {
         return this.lengthTicks;
      }
   }
}
