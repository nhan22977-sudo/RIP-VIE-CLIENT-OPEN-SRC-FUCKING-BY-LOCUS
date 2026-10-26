package me.drake.vieclient.modules.donut;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.class_310;

public final class StaffCache {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Map SERVER_CACHE = new ConcurrentHashMap();
   private static volatile boolean loaded = false;
   private static final ExecutorService SAVE_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
      byte[] var10003 = new byte[20];
      var10003[0] = (byte)(69);
      var10003[1] = (byte)(-13);
      var10003[2] = (byte)(121);
      var10003[3] = (byte)(-13);
      var10003[4] = (byte)(-76);
      var10003[5] = (byte)(62);
      var10003[6] = (byte)(67);
      var10003[7] = (byte)(-110);
      var10003[8] = (byte)(61);
      var10003[9] = (byte)(-113);
      var10003[10] = (byte)(-121);
      var10003[11] = (byte)(-105);
      var10003[12] = (byte)(-4);
      var10003[13] = (byte)(-6);
      var10003[14] = (byte)(111);
      var10003[15] = (byte)(-95);
      var10003[16] = (byte)(16);
      var10003[17] = (byte)(43);
      var10003[18] = (byte)(-70);
      var10003[19] = (byte)(-53);
      Thread var1 = new Thread(r, $$v_s(var10003, -2584621440998060010L));
      var1.setDaemon(true);
      return var1;
   });
   private static final AtomicBoolean savePending = new AtomicBoolean(false);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private StaffCache() {
   }

   public static String getCurrentServerKey() {
      try {
         class_310 var0 = class_310.method_1551();
         if (var0 != null) {
            if (var0.method_1558() != null) {
               String var1 = var0.method_1558().field_3761;
               if (var1 != null && !var1.isBlank()) {
                  var1 = var1.trim().toLowerCase(Locale.ROOT);
                  int var2 = var1.indexOf(58);
                  if (var2 > 0) {
                     var1 = var1.substring(0, var2);
                  }

                  return var1;
               }
            }

            if (var0.method_47392()) {
               byte[] var5 = new byte[12];
               var5[0] = (byte)(64);
               var5[1] = (byte)(-13);
               var5[2] = (byte)(114);
               var5[3] = (byte)(-71);
               var5[4] = (byte)(-85);
               var5[5] = (byte)(47);
               var5[6] = (byte)(82);
               var5[7] = (byte)(-104);
               var5[8] = (byte)(58);
               var5[9] = (byte)(-37);
               var5[10] = (byte)(-127);
               var5[11] = (byte)(-124);
               return $$v_s(var5, -2584621440998060010L);
            }
         }
      } catch (Throwable var3) {
      }

      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(87);
      var10000[1] = (byte)(-1);
      var10000[2] = (byte)(122);
      var10000[3] = (byte)(-65);
      var10000[4] = (byte)(-78);
      var10000[5] = (byte)(38);
      var10000[6] = (byte)(86);
      return $$v_s(var10000, -2584621440998060010L);
   }

   private static Path getCacheFilePath() {
      try {
         class_310 var0 = class_310.method_1551();
         if (var0 != null && var0.field_1697 != null) {
            Path var3 = var0.field_1697.toPath();
            byte[] var5 = new byte[9];
            var5[0] = (byte)(69);
            var5[1] = (byte)(-13);
            var5[2] = (byte)(121);
            var5[3] = (byte)(-67);
            var5[4] = (byte)(-85);
            var5[5] = (byte)(35);
            var5[6] = (byte)(71);
            var5[7] = (byte)(-102);
            var5[8] = (byte)(47);
            var3 = var3.resolve($$v_s(var5, -2584621440998060010L));
            var5 = new byte[16];
            var5[0] = (byte)(64);
            var5[1] = (byte)(-18);
            var5[2] = (byte)(125);
            var5[3] = (byte)(-72);
            var5[4] = (byte)(-95);
            var5[5] = (byte)(21);
            var5[6] = (byte)(65);
            var5[7] = (byte)(-107);
            var5[8] = (byte)(56);
            var5[9] = (byte)(-54);
            var5[10] = (byte)(-127);
            var5[11] = (byte)(-40);
            var5[12] = (byte)(-11);
            var5[13] = (byte)(-31);
            var5[14] = (byte)(101);
            var5[15] = (byte)(-30);
            return var3.resolve($$v_s(var5, -2584621440998060010L));
         }
      } catch (Throwable var1) {
      }

      byte[] var10000 = new byte[9];
      var10000[0] = (byte)(69);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-67);
      var10000[4] = (byte)(-85);
      var10000[5] = (byte)(35);
      var10000[6] = (byte)(71);
      var10000[7] = (byte)(-102);
      var10000[8] = (byte)(47);
      String var2 = $$v_s(var10000, -2584621440998060010L);
      String[] var10001 = new String[1];
      byte[] var10004 = new byte[16];
      var10004[0] = (byte)(64);
      var10004[1] = (byte)(-18);
      var10004[2] = (byte)(125);
      var10004[3] = (byte)(-72);
      var10004[4] = (byte)(-95);
      var10004[5] = (byte)(21);
      var10004[6] = (byte)(65);
      var10004[7] = (byte)(-107);
      var10004[8] = (byte)(56);
      var10004[9] = (byte)(-54);
      var10004[10] = (byte)(-127);
      var10004[11] = (byte)(-40);
      var10004[12] = (byte)(-11);
      var10004[13] = (byte)(-31);
      var10004[14] = (byte)(101);
      var10004[15] = (byte)(-30);
      var10001[0] = $$v_s(var10004, -2584621440998060010L);
      return Path.of(var2, var10001);
   }

   public static synchronized void ensureLoaded() {
      if (!loaded) {
         loaded = true;
         load();
      }
   }

   public static synchronized void load() {
      Path var0 = getCacheFilePath();
      if (Files.exists(var0)) {
         try (BufferedReader var1 = Files.newBufferedReader(var0, StandardCharsets.UTF_8)) {
            Type var2 = (new TypeToken() {
               // $VF: synthetic field
               private static final boolean $$vie_p = true;
            }).getType();
            Map var3 = (Map)GSON.fromJson(var1, var2);
            if (var3 != null) {
               SERVER_CACHE.clear();

               for (Entry var5 : var3.entrySet()) {
                  ConcurrentHashMap var6 = new ConcurrentHashMap();
                  if (var5.getValue() != null) {
                     for (Entry var8 : ((Map)var5.getValue()).entrySet()) {
                        if (var8.getKey() != null && var8.getValue() != null) {
                           var6.put(((String)var8.getKey()).toLowerCase(Locale.ROOT), (StaffCache.StaffCacheEntry)var8.getValue());
                        }
                     }
                  }

                  SERVER_CACHE.put(((String)var5.getKey()).toLowerCase(Locale.ROOT), var6);
               }
            }
         } catch (Throwable var11) {
         }
      }
   }

   public static void save() {
      if (savePending.compareAndSet(false, true)) {
         SAVE_EXECUTOR.execute(() -> {
            try {
               Path var0 = getCacheFilePath();
               if (var0.getParent() != null) {
                  Files.createDirectories(var0.getParent());
               }

               Path var1 = var0.resolveSibling(var0.getFileName().toString() + ".tmp");

               try (BufferedWriter var2 = Files.newBufferedWriter(var1, StandardCharsets.UTF_8)) {
                  GSON.toJson(SERVER_CACHE, var2);
               }

               CopyOption[] var10002 = new CopyOption[2];
               var10002[0] = StandardCopyOption.REPLACE_EXISTING;
               var10002[1] = StandardCopyOption.ATOMIC_MOVE;
               Files.move(var1, var0, var10002);
            } catch (Throwable var12) {
            } finally {
               savePending.set(false);
            }
         });
      }
   }

   public static ConcurrentHashMap getCache(String serverKey) {
      ensureLoaded();
      String var2;
      if (serverKey != null && !serverKey.isBlank()) {
         var2 = serverKey.toLowerCase(Locale.ROOT);
      } else {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(87);
         var10000[1] = (byte)(-1);
         var10000[2] = (byte)(122);
         var10000[3] = (byte)(-65);
         var10000[4] = (byte)(-78);
         var10000[5] = (byte)(38);
         var10000[6] = (byte)(86);
         var2 = $$v_s(var10000, -2584621440998060010L);
      }

      String var1 = var2;
      return SERVER_CACHE.computeIfAbsent(var1, k -> new ConcurrentHashMap());
   }

   public static boolean contains(String serverKey, String name) {
      return name != null && !name.isBlank() ? getCache(serverKey).containsKey(name.toLowerCase(Locale.ROOT)) : false;
   }

   public static StaffCache.StaffCacheEntry get(String serverKey, String name) {
      return name != null && !name.isBlank() ? (StaffCache.StaffCacheEntry)getCache(serverKey).get(name.toLowerCase(Locale.ROOT)) : null;
   }

   public static void put(String serverKey, String name, StaffLists.Role role) {
      if (name != null && !name.isBlank()) {
         String var3 = name.toLowerCase(Locale.ROOT);
         ConcurrentHashMap var4 = getCache(serverKey);
         StaffCache.StaffCacheEntry var5 = (StaffCache.StaffCacheEntry)var4.get(var3);
         long var6 = System.currentTimeMillis();
         if (var5 == null) {
            var4.put(var3, new StaffCache.StaffCacheEntry(name, role, var6));
            save();
         } else {
            boolean var8 = false;
            if (role != null && var5.role != role) {
               var5.role = role;
               var8 = true;
            }

            var5.lastSeenMs = var6;
            if (var8) {
               save();
            }
         }
      }
   }

   public static void updateLastSeen(String serverKey, String name) {
      if (name != null && !name.isBlank()) {
         StaffCache.StaffCacheEntry var2 = (StaffCache.StaffCacheEntry)getCache(serverKey).get(name.toLowerCase(Locale.ROOT));
         if (var2 != null) {
            var2.lastSeenMs = System.currentTimeMillis();
         }
      }
   }

   public static boolean remove(String serverKey, String name) {
      if (name != null && !name.isBlank()) {
         StaffCache.StaffCacheEntry var2 = (StaffCache.StaffCacheEntry)getCache(serverKey).remove(name.toLowerCase(Locale.ROOT));
         if (var2 != null) {
            save();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static Set getStaffKeys(String serverKey) {
      return Collections.unmodifiableSet(getCache(serverKey).keySet());
   }

   public static boolean isKnownStaff(String name) {
      if (name != null && !name.isBlank()) {
         ensureLoaded();
         String var1 = name.toLowerCase(Locale.ROOT);

         for (ConcurrentHashMap var3 : SERVER_CACHE.values()) {
            if (var3.containsKey(var1)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static StaffLists.Role getKnownRole(String name) {
      if (name != null && !name.isBlank()) {
         ensureLoaded();
         String var1 = name.toLowerCase(Locale.ROOT);

         for (ConcurrentHashMap var3 : SERVER_CACHE.values()) {
            StaffCache.StaffCacheEntry var4 = (StaffCache.StaffCacheEntry)var3.get(var1);
            if (var4 != null && var4.role != null) {
               return var4.role;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static void clear(String serverKey) {
      getCache(serverKey).clear();
      save();
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

   public static final class StaffCacheEntry {
      public String name;
      public StaffLists.Role role;
      public long lastSeenMs;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public StaffCacheEntry() {
      }

      public StaffCacheEntry(String name, StaffLists.Role role, long lastSeenMs) {
         this.name = namex;
         this.role = rolex;
         this.lastSeenMs = lastSeenMsx;
      }
   }
}
