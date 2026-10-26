package me.drake.vieclient.util.render;

import java.nio.charset.StandardCharsets;

public final class CordNotification {
   public static final long LIFETIME_MS = 5000L;
   public static final int ICON_NONE = 0;
   public static final int ICON_RAIN = 1;
   public static final int ICON_SUN = 2;
   private static String title = "";
   private static int icon = 0;
   private static boolean active;
   private static long startedAt;
   private static long lifetimeMs = 5000L;
   private static float expand = 0.0F;
   private static long lastMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private CordNotification() {
   }

   public static void push(String title, int icon) {
      push(titlex, iconx, 5000L);
   }

   public static void push(String title, int icon, long durationMs) {
      if (titlex != null && !titlex.trim().isEmpty()) {
         if (iconx == 1) {
            ToastManager.pushWeather(true);
            return;
         }

         if (iconx == (2)) {
            ToastManager.pushWeather(false);
            return;
         }

         byte[] var10000 = new byte[4];
         var10000[0] = (byte)(91);
         var10000[1] = (byte)(-1);
         var10000[2] = (byte)(112);
         var10000[3] = (byte)(-78);
         String var4 = $$v_s(var10000, 2589472875345072156L);
         byte[] var10004 = new byte[6];
         var10004[0] = (byte)(119);
         var10004[1] = (byte)(-11);
         var10004[2] = (byte)(104);
         var10004[3] = (byte)(-73);
         var10004[4] = (byte)(6);
         var10004[5] = (byte)(28);
         ToastManager.pushNotification("", titlex, "", var4, $$v_s(var10004, 2589472875345072156L), true);
      }
   }

   public static void tick() {
   }

   public static float expandFactor() {
      return 0.0F;
   }

   public static boolean active() {
      return false;
   }

   public static String title() {
      return "";
   }

   public static int icon() {
      return 0;
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
