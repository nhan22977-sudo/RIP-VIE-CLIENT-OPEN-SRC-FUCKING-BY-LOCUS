package me.drake.vieclient.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import net.minecraft.class_2338;

public final class SpawnerEntityCache {
   private static final Map CACHE = new ConcurrentHashMap();
   private static final List LISTENERS = new CopyOnWriteArrayList();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SpawnerEntityCache() {
   }

   public static void addListener(BiConsumer listener) {
      if (listener != null) {
         LISTENERS.add(listener);
      }
   }

   public static void removeListener(BiConsumer listener) {
      if (listener != null) {
         LISTENERS.remove(listener);
      }
   }

   public static void put(class_2338 pos, String entityId) {
      if (pos != null && entityId != null) {
         String var2 = (String)CACHE.get(pos.method_10063());
         if (!entityId.isEmpty() || var2 == null || var2.isEmpty()) {
            CACHE.put(pos.method_10063(), entityId);

            for (BiConsumer var4 : LISTENERS) {
               try {
                  var4.accept(pos, entityId);
               } catch (Throwable var6) {
               }
            }
         }
      }
   }

   public static String get(class_2338 pos) {
      return (String)CACHE.get(pos.method_10063());
   }

   public static void remove(class_2338 pos) {
      CACHE.remove(pos.method_10063());
   }

   public static void clear() {
      CACHE.clear();
   }
}
