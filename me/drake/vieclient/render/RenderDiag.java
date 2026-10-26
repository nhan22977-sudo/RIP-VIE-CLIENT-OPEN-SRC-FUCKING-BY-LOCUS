package me.drake.vieclient.render;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.drake.vieclient.VieClientMod;

public final class RenderDiag {
   private static final boolean ENABLED = true;
   private static final long ENSURE_LOG_INTERVAL_MS = 1500L;
   private static final long DRAW_LOG_INTERVAL_MS = 1000L;
   private static final Map lastEnsureLogAt = new ConcurrentHashMap();
   private static final Map drawWindowStart = new ConcurrentHashMap();
   private static final Map drawCount = new ConcurrentHashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private RenderDiag() {
   }

   public static void ensure(String tag, boolean fallback, boolean precompiled, boolean disabled, boolean reloadInProgress) {
      long var5 = System.currentTimeMillis();
      long var7 = lastEnsureLogAt.getOrDefault(tag, 0L);
      if (var5 - var7 >= 1500L) {
         lastEnsureLogAt.put(tag, var5);
         VieClientMod.LOGGER
            .info(
               "[VieClient][RenderDiag] ensure {} fallback={} precompiled={} disabled={} reloading={}",
               new Object[]{tag, fallback, precompiled, disabled, reloadInProgress}
            );
      }
   }

   public static void draw(String tag) {
      long var1 = System.currentTimeMillis();
      long var3 = drawWindowStart.getOrDefault(tag, var1);
      int var5 = drawCount.getOrDefault(tag, 0) + 1;
      drawCount.put(tag, var5);
      drawWindowStart.putIfAbsent(tag, var1);
      if (var1 - var3 >= 1000L) {
         byte[] var10001 = new byte[36];
         var10001[0] = (byte)(98);
         var10001[1] = (byte)(-52);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(105);
         var10001[5] = (byte)(-25);
         var10001[6] = (byte)(-77);
         var10001[7] = (byte)(32);
         var10001[8] = (byte)(63);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-71);
         var10001[11] = (byte)(-83);
         var10001[12] = (byte)(32);
         var10001[13] = (byte)(54);
         var10001[14] = (byte)(-100);
         var10001[15] = (byte)(89);
         var10001[16] = (byte)(12);
         var10001[17] = (byte)(56);
         var10001[18] = (byte)(-120);
         var10001[19] = (byte)(-57);
         var10001[20] = (byte)(-5);
         var10001[21] = (byte)(28);
         var10001[22] = (byte)(-41);
         var10001[23] = (byte)(53);
         var10001[24] = (byte)(-27);
         var10001[25] = (byte)(96);
         var10001[26] = (byte)(-11);
         var10001[27] = (byte)(49);
         var10001[28] = (byte)(-115);
         var10001[29] = (byte)(112);
         var10001[30] = (byte)(-126);
         var10001[31] = (byte)(-74);
         var10001[32] = (byte)(-92);
         var10001[33] = (byte)(7);
         var10001[34] = (byte)(7);
         var10001[35] = (byte)(3);
         VieClientMod.LOGGER.info($$v_s(var10001, 7915451911178014748L), tag, var5);
         drawWindowStart.put(tag, var1);
         drawCount.put(tag, 0);
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
