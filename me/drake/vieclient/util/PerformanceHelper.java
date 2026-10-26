package me.drake.vieclient.util;

import java.nio.charset.StandardCharsets;
import net.minecraft.class_310;

public final class PerformanceHelper {
   private static final long SAMPLE_INTERVAL_MS = 250L;
   private static long lastSampleAtMs = 0L;
   private static int cachedFps = 120;
   private static PerformanceHelper.Tier cachedTier = PerformanceHelper.Tier.ULTRA;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private PerformanceHelper() {
   }

   public static PerformanceHelper.Tier tier() {
      sampleIfNeeded();
      return cachedTier;
   }

   public static int fps() {
      sampleIfNeeded();
      return cachedFps;
   }

   public static int scaleBudget(int base) {
      if (base <= 1) {
         return 1;
      }

      return switch (tier()) {
         case ULTRA -> base;
         case HIGH -> Math.max(1, (int)Math.floor(base * 0.85));
         case MEDIUM -> Math.max(1, (int)Math.floor(base * 0.7));
         case LOW -> Math.max(1, (int)Math.floor(base * 0.55));
         case CRITICAL -> Math.max(1, (int)Math.floor(base * 0.4));
      };
   }

   public static int scaleRenderCap(int base) {
      if (base <= 1) {
         return 1;
      }

      return switch (tier()) {
         case ULTRA -> base;
         case HIGH -> Math.max(1, (int)Math.floor(base * 0.9));
         case MEDIUM -> Math.max(1, (int)Math.floor(base * 0.75));
         case LOW -> Math.max(1, (int)Math.floor(base * 0.6));
         case CRITICAL -> Math.max(1, (int)Math.floor(base * 0.45));
      };
   }

   public static int scaleInterval(int baseTicks) {
      int var1 = Math.max(1, baseTicks);

      return switch (tier()) {
         case ULTRA -> var1;
         case HIGH -> var1 + 1;
         case MEDIUM -> var1 + (2);
         case LOW -> var1 + (4);
         case CRITICAL -> var1 + (6);
      };
   }

   public static double scaleRange(double baseRange) {
      double var2 = Math.max(1.0, baseRange);

      return switch (tier()) {
         case ULTRA -> var2;
         case HIGH -> var2 * 0.95;
         case MEDIUM -> var2 * 0.88;
         case LOW -> var2 * 0.78;
         case CRITICAL -> var2 * 0.66;
      };
   }

   public static boolean shouldRenderWorld3DThisFrame() {
      return true;
   }

   private static void sampleIfNeeded() {
      long var0 = System.currentTimeMillis();
      if (var0 - lastSampleAtMs >= 250L) {
         lastSampleAtMs = var0;
         class_310 var2 = class_310.method_1551();
         if (var2 != null) {
            int var3 = var2.method_47599();
            if (var3 <= 0) {
               var3 = cachedFps;
            }

            cachedFps = Math.max(1, var3);
            if (cachedFps >= (200)) {
               cachedTier = PerformanceHelper.Tier.ULTRA;
            } else if (cachedFps >= (150)) {
               cachedTier = PerformanceHelper.Tier.HIGH;
            } else if (cachedFps >= (110)) {
               cachedTier = PerformanceHelper.Tier.MEDIUM;
            } else if (cachedFps >= (75)) {
               cachedTier = PerformanceHelper.Tier.LOW;
            } else {
               cachedTier = PerformanceHelper.Tier.CRITICAL;
            }
         }
      }
   }

   public enum Tier {
      ULTRA,
      HIGH,
      MEDIUM,
      LOW,
      CRITICAL;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static PerformanceHelper.Tier[] $values() {
         PerformanceHelper.Tier[] var10000 = new PerformanceHelper.Tier[5];
         var10000[0] = ULTRA;
         var10000[1] = HIGH;
         var10000[2] = MEDIUM;
         var10000[3] = LOW;
         var10000[4] = CRITICAL;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(78);
         ULTRA = new PerformanceHelper.Tier();
         var10002 = new byte[4];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(91);
         var10002[3] = (byte)(-106);
         HIGH = new PerformanceHelper.Tier();
         var10002 = new byte[6];
         var10002[0] = (byte)(115);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(88);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(90);
         var10002[5] = (byte)(5);
         MEDIUM = new PerformanceHelper.Tier();
         var10002 = new byte[3];
         var10002[0] = (byte)(114);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(75);
         LOW = new PerformanceHelper.Tier();
         var10002 = new byte[8];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(70);
         var10002[5] = (byte)(11);
         var10002[6] = (byte)(93);
         var10002[7] = (byte)(-89);
         CRITICAL = new PerformanceHelper.Tier();
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
}
