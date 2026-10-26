package me.drake.vieclient.render.nvg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.imageio.ImageIO;
import net.minecraft.class_12079.class_12080;
import net.minecraft.class_12079.class_12081;

public final class PlayerHeadCache {
   private static final int MAX_ENTRIES = 256;
   private static final int MAX_DOWNLOAD_BYTES = 8388608;
   private static final long RETRY_DELAY_MS = 30000L;
   private static final ConcurrentHashMap FAST_HEADS = new ConcurrentHashMap();
   private static final ConcurrentHashMap IMAGE_IDS = new ConcurrentHashMap();
   private static final ConcurrentHashMap DECODED = new ConcurrentHashMap();
   private static final ConcurrentHashMap FAILURES = new ConcurrentHashMap();
   private static final Set IN_FLIGHT = ConcurrentHashMap.newKeySet();
   private static final ConcurrentLinkedDeque DECODED_ORDER = new ConcurrentLinkedDeque();
   private static final ConcurrentLinkedDeque IMAGE_ORDER = new ConcurrentLinkedDeque();
   private static final ConcurrentLinkedQueue PENDING_DELETES = new ConcurrentLinkedQueue();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private PlayerHeadCache() {
   }

   public static int getOrRequest(UUID playerId, class_12081 asset) {
      if (playerId != null && asset instanceof class_12080 var2) {
         String var3 = var2.comp_4939();
         if (var3 != null && !var3.isBlank()) {
            PlayerHeadCache.FastHeadEntry var4 = (PlayerHeadCache.FastHeadEntry)FAST_HEADS.get(playerId);
            if (var4 != null && var4.url.equals(var3)) {
               return var4.imageId;
            }

            PlayerHeadCache.CacheKey var5 = new PlayerHeadCache.CacheKey(playerId, var3);
            Integer var6 = (Integer)IMAGE_IDS.get(var5);
            if (var6 != null) {
               FAST_HEADS.put(playerId, new PlayerHeadCache.FastHeadEntry(var3, var6));
               return var6;
            }

            BufferedImage var7 = (BufferedImage)DECODED.get(var5);
            if (var7 == null) {
               long var11 = System.currentTimeMillis();
               Long var10 = (Long)FAILURES.get(var5);
               if (var10 != null && isBackedOff(var11, var10)) {
                  return -1;
               }

               if (!IN_FLIGHT.add(var5)) {
                  return -1;
               }

               CompletableFuture.runAsync(() -> {
                  try {
                     BufferedImage var2x = cropFace(download(var3));
                     if (var2x != null) {
                        DECODED.put(var5, var2x);
                        DECODED_ORDER.remove(var5);
                        DECODED_ORDER.addLast(var5);
                        FAILURES.remove(var5);
                        trimDecoded();
                        return;
                     }

                     FAILURES.put(var5, System.currentTimeMillis());
                  } catch (Throwable var6x) {
                     FAILURES.put(var5, System.currentTimeMillis());
                     return;
                  } finally {
                     IN_FLIGHT.remove(var5);
                  }
               });
               return -1;
            } else if (!NVGRenderer.isInFrame()) {
               return -1;
            } else {
               flushPendingDeletes();
               int var8 = NVGImage.createPixelArtFromBufferedImage(var7);
               if (var8 <= 0) {
                  return -1;
               } else {
                  Integer var9 = IMAGE_IDS.putIfAbsent(var5, var8);
                  if (var9 != null) {
                     NVGImage.deleteImage(var8);
                     FAST_HEADS.put(playerId, new PlayerHeadCache.FastHeadEntry(var3, var9));
                     return var9;
                  } else {
                     IMAGE_ORDER.remove(var5);
                     IMAGE_ORDER.addLast(var5);
                     FAST_HEADS.put(playerId, new PlayerHeadCache.FastHeadEntry(var3, var8));
                     trimImagesInFrame();
                     return var8;
                  }
               }
            }
         } else {
            return -1;
         }
      } else {
         return -1;
      }
   }

   static boolean isBackedOff(long nowMs, long failedAtMs) {
      long var4 = nowMs - failedAtMs;
      return var4 >= 0L && var4 < 30000L;
   }

   static BufferedImage cropFace(BufferedImage skin) {
      if (skin != null && skin.getWidth() >= (48) && skin.getHeight() >= (32)) {
         BufferedImage var1 = new BufferedImage(8, 8, 2);

         for (int var2 = 0; var2 < (8); var2++) {
            for (int var3 = 0; var3 < (8); var3++) {
               var1.setRGB(var3, var2, skin.getRGB((8) + var3, (8) + var2));
            }
         }

         for (int var11 = 0; var11 < (8); var11++) {
            for (int var12 = 0; var12 < (8); var12++) {
               int var4 = skin.getRGB((40) + var12, (8) + var11);
               int var5 = var4 >>> (24);
               if (var5 != 0) {
                  if (var5 == (255)) {
                     var1.setRGB(var12, var11, var4);
                  } else {
                     int var6 = var1.getRGB(var12, var11);
                     int var7 = (255) - var5;
                     int var8 = (
                           (var4 >>> (16) & (255)) * var5
                              + (var6 >>> (16) & (255)) * var7
                              + (127)
                        )
                        / (255);
                     int var9 = (
                           (var4 >>> (8) & (255)) * var5
                              + (var6 >>> (8) & (255)) * var7
                              + (127)
                        )
                        / (255);
                     int var10 = ((var4 & (255)) * var5 + (var6 & (255)) * var7 + (127)) / (255);
                     var1.setRGB(var12, var11, -16777216 | var8 << (16) | var9 << (8) | var10);
                  }
               }
            }
         }

         return var1;
      } else {
         return null;
      }
   }

   public static void invalidateImages() {
      IMAGE_IDS.values().forEach(NVGImage::deleteImage);
      IMAGE_IDS.clear();
      IMAGE_ORDER.clear();
      FAST_HEADS.clear();

      Integer var0;
      while ((var0 = (Integer)PENDING_DELETES.poll()) != null) {
         NVGImage.deleteImage(var0);
      }
   }

   private static BufferedImage download(String url) throws Exception {
      URI var1 = URI.create(url);
      String var2 = var1.getScheme();
      byte[] var10000 = new byte[5];
      var10000[0] = (byte)(81);
      var10000[1] = (byte)(-18);
      var10000[2] = (byte)(104);
      var10000[3] = (byte)(-82);
      var10000[4] = (byte)(85);
      if (!$$v_s(var10000, 7133743175833997340L).equalsIgnoreCase(var2)) {
         var10000 = new byte[4];
         var10000[0] = (byte)(81);
         var10000[1] = (byte)(-18);
         var10000[2] = (byte)(104);
         var10000[3] = (byte)(-82);
         if (!$$v_s(var10000, 7133743175833997340L).equalsIgnoreCase(var2)) {
            return null;
         }
      }

      URLConnection var3 = var1.toURL().openConnection();
      var3.setConnectTimeout(5000);
      var3.setReadTimeout(5000);
      int var4 = var3.getContentLength();
      if (var4 > (8388608)) {
         return null;
      }

      try (InputStream var5 = var3.getInputStream()) {
         byte[] var6 = var5.readNBytes(8388609);
         return var6.length > (8388608) ? null : ImageIO.read(new ByteArrayInputStream(var6));
      }
   }

   private static void trimDecoded() {
      while (DECODED.size() > (256)) {
         PlayerHeadCache.CacheKey var0 = (PlayerHeadCache.CacheKey)DECODED_ORDER.pollFirst();
         if (var0 == null) {
            return;
         }

         DECODED.remove(var0);
         FAILURES.remove(var0);
         Integer var1 = (Integer)IMAGE_IDS.remove(var0);
         IMAGE_ORDER.remove(var0);
         FAST_HEADS.remove(var0.playerId());
         if (var1 != null) {
            PENDING_DELETES.add(var1);
         }
      }
   }

   private static void trimImagesInFrame() {
      while (IMAGE_IDS.size() > (256)) {
         PlayerHeadCache.CacheKey var0 = (PlayerHeadCache.CacheKey)IMAGE_ORDER.pollFirst();
         if (var0 == null) {
            return;
         }

         Integer var1 = (Integer)IMAGE_IDS.remove(var0);
         FAST_HEADS.remove(var0.playerId());
         if (var1 != null) {
            NVGImage.deleteImage(var1);
         }
      }
   }

   private static void flushPendingDeletes() {
      Integer var0;
      while ((var0 = (Integer)PENDING_DELETES.poll()) != null) {
         NVGImage.deleteImage(var0);
      }
   }

   public static void invalidate() {
      for (Integer var1 : IMAGE_IDS.values()) {
         if (var1 != null && var1 != (-1)) {
            PENDING_DELETES.add(var1);
         }
      }

      IMAGE_IDS.clear();
      FAST_HEADS.clear();
      IMAGE_ORDER.clear();
      flushPendingDeletes();
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

   private record CacheKey(UUID playerId, String url) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record FastHeadEntry(String url, int imageId) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
