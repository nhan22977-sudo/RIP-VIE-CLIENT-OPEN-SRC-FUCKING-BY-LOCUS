package me.drake.vieclient.render.nvg;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class IconManager {
   private static final String BASE = "assets/vie_client/vie/textures/icons/";
   private static final String ALT_BASE = "assets/vie_client/meilaa/textures/icons/";
   private static final String ROOT_BASE = "assets/vie_client/icons/";
   private static final Map cache = new HashMap();
   private static final Map colorCache = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private IconManager() {
   }

   public static int id(String name) {
      label19: {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(81);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(123);
         var10000[3] = (byte)(-79);
         var10000[4] = (byte)(30);
         var10000[5] = (byte)(108);
         var10000[6] = (byte)(124);
         if (!$$v_s(var10000, 366717683509743640L).equals(name)) {
            var10000 = new byte[6];
            var10000[0] = (byte)(80);
            var10000[1] = (byte)(-1);
            var10000[2] = (byte)(117);
            var10000[3] = (byte)(-78);
            var10000[4] = (byte)(18);
            var10000[5] = (byte)(104);
            if (!$$v_s(var10000, 366717683509743640L).equals(name)) {
               break label19;
            }
         }

         var10000 = new byte[7];
         var10000[0] = (byte)(81);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(123);
         var10000[3] = (byte)(-79);
         var10000[4] = (byte)(5);
         var10000[5] = (byte)(96);
         var10000[6] = (byte)(112);
         name = $$v_s(var10000, 366717683509743640L);
      }

      Integer var1 = (Integer)cache.get(name);
      if (var1 != null) {
         return var1;
      }

      int var2 = loadIconWhite(name);
      if (var2 != (-1)) {
         cache.put(name, var2);
      }

      return var2;
   }

   private static int loadIconWhite(String name) {
      int var1 = NVGImage.loadImageWhite("assets/vie_client/vie/textures/icons/" + name + ".png");
      if (var1 == (-1)) {
         var1 = NVGImage.loadImageWhite("assets/vie_client/meilaa/textures/icons/" + name + ".png");
      }

      if (var1 == (-1)) {
         var1 = NVGImage.loadImageWhite("assets/vie_client/icons/" + name + ".png");
      }

      return var1;
   }

   public static boolean has(String name) {
      return id(name) != (-1);
   }

   public static int idColored(String name) {
      label19: {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(81);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(123);
         var10000[3] = (byte)(-79);
         var10000[4] = (byte)(30);
         var10000[5] = (byte)(108);
         var10000[6] = (byte)(124);
         if (!$$v_s(var10000, 366717683509743640L).equals(name)) {
            var10000 = new byte[6];
            var10000[0] = (byte)(80);
            var10000[1] = (byte)(-1);
            var10000[2] = (byte)(117);
            var10000[3] = (byte)(-78);
            var10000[4] = (byte)(18);
            var10000[5] = (byte)(104);
            if (!$$v_s(var10000, 366717683509743640L).equals(name)) {
               break label19;
            }
         }

         var10000 = new byte[7];
         var10000[0] = (byte)(81);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(123);
         var10000[3] = (byte)(-79);
         var10000[4] = (byte)(5);
         var10000[5] = (byte)(96);
         var10000[6] = (byte)(112);
         name = $$v_s(var10000, 366717683509743640L);
      }

      Integer var1 = (Integer)colorCache.get(name);
      if (var1 != null) {
         return var1;
      }

      int var2 = loadIconColored(name);
      if (var2 != (-1)) {
         colorCache.put(name, var2);
      }

      return var2;
   }

   private static int loadIconColored(String name) {
      int var1 = NVGImage.loadImage("assets/vie_client/vie/textures/icons/" + name + ".png");
      if (var1 == (-1)) {
         var1 = NVGImage.loadImage("assets/vie_client/meilaa/textures/icons/" + name + ".png");
      }

      if (var1 == (-1)) {
         var1 = NVGImage.loadImage("assets/vie_client/icons/" + name + ".png");
      }

      return var1;
   }

   public static boolean hasColored(String name) {
      return idColored(name) != (-1);
   }

   public static void invalidate() {
      for (int var1 : cache.values()) {
         if (var1 != (-1)) {
            NVGImage.deleteImage(var1);
         }
      }

      cache.clear();

      for (int var3 : colorCache.values()) {
         if (var3 != (-1)) {
            NVGImage.deleteImage(var3);
         }
      }

      colorCache.clear();
   }

   public static void draw(String name, float x, float y, float size) {
      draw(name, x, y, size, 255);
   }

   public static void draw(String name, float x, float y, float size, int alpha) {
      int var5 = id(name);
      if (var5 != (-1)) {
         NanoVGRenderer.drawImage(var5, x, y, size, size, Math.max(0, Math.min(255, alpha)) * 0.003921569F);
      }
   }

   public static void draw(String name, float x, float y, float size, Color tint) {
      draw(name, x, y, size, tint == null ? 255 : tint.getAlpha());
   }

   public static void draw(String name, float x, float y, float w, float h, int alpha) {
      int var6 = id(name);
      if (var6 != (-1)) {
         NanoVGRenderer.drawImage(var6, x, y, w, h, Math.max(0, Math.min(255, alpha)) * 0.003921569F);
      }
   }

   public static void draw(String name, float x, float y, float w, float h, Color tint) {
      draw(name, x, y, w, h, tint == null ? 255 : tint.getAlpha());
   }

   public static void drawColored(String name, float x, float y, float size) {
      drawColored(name, x, y, size, 255);
   }

   public static void drawColored(String name, float x, float y, float size, int alpha) {
      int var5 = idColored(name);
      if (var5 != (-1)) {
         NanoVGRenderer.drawImage(var5, x, y, size, size, Math.max(0, Math.min(255, alpha)) * 0.003921569F);
      }
   }

   public static void drawColored(String name, float x, float y, float w, float h, int alpha) {
      int var6 = idColored(name);
      if (var6 != (-1)) {
         NanoVGRenderer.drawImage(var6, x, y, w, h, Math.max(0, Math.min(255, alpha)) * 0.003921569F);
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
