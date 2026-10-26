package me.drake.vieclient.modules.donut;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public final class AutoMineBridge {
   private static Boolean available = null;
   private static Method mGetInstance;
   private static Method mIsSelecting;
   private static Method mHandleMouseButton;
   private static Method mHandleMouseScroll;
   private static Method mHandleKey;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private AutoMineBridge() {
   }

   private static void ensureInit() {
      if (available == null) {
         try {
            Class var0 = Class.forName(
               "me.drake.vieclient.modules.donut.AutoMine"
            );
            mGetInstance = var0.getMethod("getInstance");
            mIsSelecting = var0.getMethod("isSelecting");
            mHandleMouseButton = var0.getMethod(
               "handleMouseButton", int.class, int.class
            );
            mHandleMouseScroll = var0.getMethod(
               "handleMouseScroll", double.class
            );
            mHandleKey = var0.getMethod("handleKey", int.class);
            available = true;
         } catch (Throwable var1) {
            available = false;
         }
      }
   }

   public static boolean isSelecting() {
      ensureInit();
      if (!available) {
         return false;
      }

      try {
         Object var0 = mGetInstance.invoke(null);
         return var0 == null ? false : (Boolean)mIsSelecting.invoke(var0);
      } catch (Throwable var1) {
         return false;
      }
   }

   public static boolean handleMouseButton(int button, int action) {
      ensureInit();
      if (!available) {
         return false;
      }

      try {
         Object var2 = mGetInstance.invoke(null);
         if (var2 == null) {
            return false;
         }

         Method var10000 = mHandleMouseButton;
         Object[] var10002 = new Object[2];
         var10002[0] = button;
         var10002[1] = action;
         return (Boolean)var10000.invoke(var2, var10002);
      } catch (Throwable var3) {
         return false;
      }
   }

   public static boolean handleMouseScroll(double vertical) {
      ensureInit();
      if (!available) {
         return false;
      }

      try {
         Object var2 = mGetInstance.invoke(null);
         return var2 == null ? false : (Boolean)mHandleMouseScroll.invoke(var2, vertical);
      } catch (Throwable var3) {
         return false;
      }
   }

   public static boolean handleKey(int key) {
      ensureInit();
      if (!available) {
         return false;
      }

      try {
         Object var1 = mGetInstance.invoke(null);
         return var1 == null ? false : (Boolean)mHandleKey.invoke(var1, key);
      } catch (Throwable var2) {
         return false;
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
