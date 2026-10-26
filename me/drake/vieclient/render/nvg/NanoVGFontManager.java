package me.drake.vieclient.render.nvg;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.system.MemoryUtil;

public class NanoVGFontManager {
   private static int regularFont = -1;
   private static int boldFont = -1;
   private static int iconFont = -1;
   private static int tiengvietFont = -1;
   private static int vieExternalFont = -1;
   private static int minecraftFont = -1;
   private static int comfortaaFont = -1;
   private static int sfProRegularFont = -1;
   private static int sfProSemiboldFont = -1;
   private static int sfProBoldFont = -1;
   private static int titleFont = -1;
   private static int titleBoldFont = -1;
   private static boolean fontsLoaded = false;
   private static String resolvedMainFontPath = null;
   private static String resolvedTitleFontPath = null;
   private static final Map FONT_NAMES = new HashMap();
   private static final Set loadedContexts = new HashSet();
   private static final String[] FONT_CANDIDATES = new String[]{
      "assets/vie_client/vie/fonts/SF-Pro-Display-Medium.otf",
      "assets/vie_client/font/vie_external.ttf",
      "assets/vie_client/vie/fonts/Motivate.otf",
      "assets/vie_client/vie/fonts/CIND.otf",
      "assets/vie_client/vie/fonts/Cyberpunks.ttf",
      "assets/vie_client/vie/fonts/Poppins-Medium.ttf",
      "assets/vie_client/vie/fonts/monaco.ttf"
   };
   private static final String SPACE_GROTESK_PATH = "assets/vie_client/vie/fonts/SF-Pro-Display-Medium.otf";
   private static final String SPACE_GROTESK_BOLD_PATH = "assets/vie_client/vie/fonts/SF-Pro-Display-Semibold.otf";
   private static final String SF_PRO_REGULAR_PATH = "assets/vie_client/vie/fonts/SF-Pro-Display-Medium.otf";
   private static final String SF_PRO_SEMIBOLD_PATH = "assets/vie_client/vie/fonts/SF-Pro-Display-Semibold.otf";
   private static final String SF_PRO_BOLD_PATH = "assets/vie_client/vie/fonts/SF-Pro-Display-Bold.otf";
   private static final String TIENGVIET_FONT_PATH = "assets/vie_client/vie/fonts/tiengviet.otf";
   private static final String ICON_FONT_PATH = "assets/vie_client/vie/fonts/font awesome 7 freeregular400.otf";
   private static final String VIE_EXTERNAL_FONT_PATH = "assets/vie_client/font/vie_external.ttf";
   private static final String MINECRAFT_FONT_PATH = "assets/vie_client/vie/fonts/Minecraft.otf";
   private static final String COMFORTAA_DOWNLOAD_PATH = "C:\\Users\\titbi\\Downloads\\Comfortaa-Bold.ttf";
   private static final String COMFORTAA_RESOURCE_PATH = "assets/vie_client/vie/fonts/Comfortaa-Bold.ttf";
   private static final String TITLE_FONT_PATH = "assets/vie_client/font/vie_external.ttf";
   private static final String TITLE_BOLD_FONT_PATH = "assets/vie_client/font/vie_external.ttf";
   private static volatile boolean useMinecraftFont = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static void loadFonts() {
      if (!fontsLoaded) {
         long var0 = NVGRenderer.getContext();
         if (var0 == 0L) {
            System.err
               .println(
                  "[NanoVGFontManager] Primary VG context is 0, cannot load fonts!"
               );
         } else {
            String var2 = findMainFontPath();
            if (var2 == null) {
               System.err
                  .println(
                     "[NanoVGFontManager] No valid font file found! Text will be invisible."
                  );
            } else {
               resolvedMainFontPath = var2;
               System.out.println("[NanoVGFontManager] Main font: " + var2);
               resolvedTitleFontPath = findTitleFontPath();
               System.out
                  .println(
                     "[NanoVGFontManager] Title font: "
                        + (resolvedTitleFontPath != null ? resolvedTitleFontPath : "(none)")
                  );

               try {
                  regularFont = loadFont("inter", var2, var0);
                  boldFont = loadFont(
                     "inter-bold",
                     "assets/vie_client/vie/fonts/SF-Pro-Display-Semibold.otf",
                     var0
                  );

                  try {
                     tiengvietFont = loadFont(
                        "tiengviet",
                        "assets/vie_client/vie/fonts/tiengviet.otf",
                        var0
                     );
                  } catch (Exception var13) {
                     tiengvietFont = -1;
                  }

                  try {
                     iconFont = loadFont(
                        "fa-solid",
                        "assets/vie_client/vie/fonts/font awesome 7 freeregular400.otf",
                        var0
                     );
                  } catch (Exception var12) {
                     iconFont = regularFont;
                  }

                  try {
                     vieExternalFont = loadFont(
                        "vie-external",
                        "assets/vie_client/font/vie_external.ttf",
                        var0
                     );
                  } catch (Exception var11) {
                     vieExternalFont = -1;
                  }

                  try {
                     minecraftFont = loadFont(
                        "minecraft",
                        "assets/vie_client/vie/fonts/Minecraft.otf",
                        var0
                     );
                  } catch (Exception var10) {
                     minecraftFont = -1;
                  }

                  try {
                     comfortaaFont = loadFont(
                        "comfortaa",
                        "C:\\Users\\titbi\\Downloads\\Comfortaa-Bold.ttf",
                        var0
                     );
                  } catch (Exception var9) {
                     try {
                        comfortaaFont = loadFont(
                           "comfortaa",
                           "assets/vie_client/vie/fonts/Comfortaa-Bold.ttf",
                           var0
                        );
                     } catch (Exception var8) {
                        comfortaaFont = -1;
                     }
                  }

                  try {
                     sfProRegularFont = loadFont(
                        "sfpro-regular",
                        "assets/vie_client/vie/fonts/SF-Pro-Display-Medium.otf",
                        var0
                     );
                  } catch (Exception var7) {
                     sfProRegularFont = regularFont;
                  }

                  try {
                     sfProSemiboldFont = loadFont(
                        "sfpro-semibold",
                        "assets/vie_client/vie/fonts/SF-Pro-Display-Semibold.otf",
                        var0
                     );
                  } catch (Exception var15) {
                     sfProSemiboldFont = sfProRegularFont > 0 ? sfProRegularFont : regularFont;
                  }

                  try {
                     sfProBoldFont = loadFont(
                        "sfpro-bold",
                        "assets/vie_client/vie/fonts/SF-Pro-Display-Bold.otf",
                        var0
                     );
                  } catch (Exception var14) {
                     sfProBoldFont = sfProSemiboldFont > 0 ? sfProSemiboldFont : regularFont;
                  }

                  if (resolvedTitleFontPath != null) {
                     try {
                        titleFont = loadFont("title", resolvedTitleFontPath, var0);
                     } catch (Exception var6) {
                        titleFont = -1;
                     }

                     try {
                        titleBoldFont = loadFont(
                           "title-bold",
                           "assets/vie_client/font/vie_external.ttf",
                           var0
                        );
                     } catch (Exception var5) {
                        titleBoldFont = titleFont;
                     }
                  } else {
                     titleFont = regularFont;
                     titleBoldFont = boldFont;
                  }

                  if (regularFont < 0) {
                     regularFont = 0;
                  }

                  if (boldFont < 0) {
                     boldFont = regularFont;
                  }

                  FONT_NAMES.put(regularFont, "inter");
                  FONT_NAMES.put(boldFont, "inter-bold");
                  if (tiengvietFont >= 0) {
                     FONT_NAMES.put(tiengvietFont, "tiengviet");
                  }

                  if (iconFont >= 0) {
                     FONT_NAMES.put(iconFont, "fa-solid");
                  }

                  if (vieExternalFont >= 0) {
                     FONT_NAMES.put(vieExternalFont, "vie-external");
                  }

                  if (minecraftFont >= 0) {
                     FONT_NAMES.put(minecraftFont, "minecraft");
                  }

                  if (comfortaaFont >= 0) {
                     FONT_NAMES.put(comfortaaFont, "comfortaa");
                  }

                  if (sfProRegularFont > 0) {
                     FONT_NAMES.put(sfProRegularFont, "sfpro-regular");
                  }

                  if (sfProSemiboldFont > 0) {
                     FONT_NAMES.put(
                        sfProSemiboldFont, "sfpro-semibold"
                     );
                  }

                  if (sfProBoldFont > 0) {
                     FONT_NAMES.put(sfProBoldFont, "sfpro-bold");
                  }

                  if (titleFont > 0) {
                     FONT_NAMES.put(titleFont, "title");
                  }

                  if (titleBoldFont > 0) {
                     FONT_NAMES.put(titleBoldFont, "title-bold");
                  }

                  loadedContexts.add(var0);
                  fontsLoaded = true;
                  System.out
                     .println(
                        "[NanoVGFontManager] OK regular="
                           + regularFont
                           + " bold="
                           + boldFont
                           + " tiengviet="
                           + tiengvietFont
                           + " vie-external="
                           + vieExternalFont
                           + " sfpro="
                           + sfProRegularFont
                           + "/"
                           + sfProSemiboldFont
                           + "/"
                           + sfProBoldFont
                           + " title="
                           + titleFont
                     );
               } catch (Exception var16) {
                  System.err.println("[NanoVGFontManager] FAILED: " + var16.getMessage());
                  var16.printStackTrace();
                  if (regularFont < 0) {
                     regularFont = 0;
                  }

                  if (boldFont < 0) {
                     boldFont = 0;
                  }
               }
            }
         }
      }
   }

   public static void loadFontsForContext(long vg) {
      if (vg != 0L && !loadedContexts.contains(vg)) {
         if (!fontsLoaded) {
            loadFonts();
         }

         String var2 = resolvedMainFontPath;
         if (var2 == null) {
            var2 = findMainFontPath();
         }

         if (var2 == null) {
            System.err
               .println(
                  "[NanoVGFontManager] loadFontsForContext: no font found"
               );
         } else {
            String var3 = resolvedTitleFontPath;
            if (var3 == null) {
               var3 = findTitleFontPath();
            }

            try {
               loadFont("inter", var2, vg);
               loadFont(
                  "inter-bold",
                  "assets/vie_client/vie/fonts/SF-Pro-Display-Semibold.otf",
                  vg
               );

               try {
                  loadFont(
                     "tiengviet",
                     "assets/vie_client/vie/fonts/tiengviet.otf",
                     vg
                  );
               } catch (Exception var15) {
               }

               try {
                  loadFont(
                     "fa-solid",
                     "assets/vie_client/vie/fonts/font awesome 7 freeregular400.otf",
                     vg
                  );
               } catch (Exception var14) {
               }

               try {
                  loadFont(
                     "minecraft",
                     "assets/vie_client/vie/fonts/Minecraft.otf",
                     vg
                  );
               } catch (Exception var13) {
               }

               try {
                  loadFont(
                     "comfortaa",
                     "C:\\Users\\titbi\\Downloads\\Comfortaa-Bold.ttf",
                     vg
                  );
               } catch (Exception var12) {
                  try {
                     loadFont(
                        "comfortaa",
                        "assets/vie_client/vie/fonts/Comfortaa-Bold.ttf",
                        vg
                     );
                  } catch (Exception var11) {
                  }
               }

               try {
                  loadFont(
                     "sfpro-regular",
                     "assets/vie_client/vie/fonts/SF-Pro-Display-Medium.otf",
                     vg
                  );
               } catch (Exception var10) {
               }

               try {
                  loadFont(
                     "sfpro-semibold",
                     "assets/vie_client/vie/fonts/SF-Pro-Display-Semibold.otf",
                     vg
                  );
               } catch (Exception var9) {
               }

               try {
                  loadFont(
                     "sfpro-bold",
                     "assets/vie_client/vie/fonts/SF-Pro-Display-Bold.otf",
                     vg
                  );
               } catch (Exception var8) {
               }

               if (var3 != null) {
                  try {
                     loadFont("title", var3, vg);
                  } catch (Exception var7) {
                  }

                  try {
                     loadFont(
                        "title-bold",
                        "assets/vie_client/font/vie_external.ttf",
                        vg
                     );
                  } catch (Exception var6) {
                  }
               }

               loadedContexts.add(vg);
               System.out.println("[NanoVGFontManager] Fonts loaded into context " + vg);
            } catch (Exception var16) {
               System.err.println("[NanoVGFontManager] loadFontsForContext failed: " + var16.getMessage());
            }
         }
      }
   }

   private static String findMainFontPath() {
      for (String var3 : FONT_CANDIDATES) {
         InputStream var4 = openResource(var3);
         if (var4 != null) {
            byte[] var5;
            try {
               var5 = var4.readAllBytes();
               var4.close();
            } catch (Exception var7) {
               continue;
            }

            if (var5.length >= (10000)) {
               return var3;
            }
         }
      }

      return null;
   }

   private static String findTitleFontPath() {
      String[] var0 = new String[]{
         "assets/vie_client/font/vie_external.ttf",
         "assets/vie_client/font/vie_external.ttf"
      };

      for (String var4 : var0) {
         InputStream var5 = openResource(var4);
         if (var5 != null) {
            byte[] var6;
            try {
               var6 = var5.readAllBytes();
               var5.close();
            } catch (Exception var8) {
               continue;
            }

            if (var6.length >= 10000) {
               return var4;
            }
         }
      }

      return null;
   }

   public static String getFontName(int fontId) {
      String var1 = (String)FONT_NAMES.get(fontId);
      String var10000;
      if (var1 != null) {
         var10000 = var1;
      } else {
         byte[] var2 = new byte[5];
         var2[0] = (byte)(24);
         var2[1] = (byte)(-12);
         var2[2] = (byte)(104);
         var2[3] = (byte)(-69);
         var2[4] = (byte)(-52);
         var10000 = $$v_s(var2, -3250569756764024748L);
      }

      return var10000;
   }

   private static InputStream openResource(String path) {
      try {
         File var1 = new File(path);
         if (var1.exists() && var1.isFile() && var1.canRead()) {
            return new FileInputStream(var1);
         }
      } catch (Exception var10) {
      }

      try {
         byte[] var10001 = new byte[7];
         var10001[0] = (byte)(16);
         var10001[1] = (byte)(-23);
         var10001[2] = (byte)(111);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(-54);
         var10001[5] = (byte)(5);
         var10001[6] = (byte)(-49);
         String var10000;
         if (path.startsWith($$v_s(var10001, -3250569756764024748L))) {
            var10001 = new byte[7];
            var10001[0] = (byte)(16);
            var10001[1] = (byte)(-23);
            var10001[2] = (byte)(111);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(-54);
            var10001[5] = (byte)(5);
            var10001[6] = (byte)(-49);
            var10000 = path.substring($$v_s(var10001, -3250569756764024748L).length());
         } else {
            var10000 = path;
         }

         String var11 = var10000;
         int var2 = var11.indexOf(47);
         if (var2 > 0) {
            String var3 = var11.substring(0, var2);
            String var4 = var11.substring(var2 + 1);
            InputStream var5 = class_310.method_1551().method_1478().open(class_2960.method_60655(var3, var4));
            if (var5 != null) {
               return var5;
            }
         }
      } catch (Exception var9) {
      }

      try {
         InputStream var12 = NanoVGFontManager.class.getResourceAsStream("/" + path);
         if (var12 != null) {
            return var12;
         }
      } catch (Exception var8) {
      }

      try {
         InputStream var13 = NanoVGFontManager.class.getClassLoader().getResourceAsStream(path);
         if (var13 != null) {
            return var13;
         }
      } catch (Exception var7) {
      }

      try {
         InputStream var14 = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
         if (var14 != null) {
            return var14;
         }
      } catch (Exception var6) {
      }

      return null;
   }

   private static int loadFont(String name, String path, long vg) throws Exception {
      InputStream var4 = openResource(path);
      if (var4 == null) {
         throw new RuntimeException("Font not found: " + path);
      }

      try {
         return createFont(name, var4, vg);
      } finally {
         var4.close();
      }
   }

   private static int createFont(String name, InputStream is, long vg) throws Exception {
      byte[] var4 = is.readAllBytes();
      if (var4.length < (10000)) {
         throw new RuntimeException("Font file too small/stub (" + var4.length + "b): " + name);
      }

      ByteBuffer var5 = MemoryUtil.memAlloc(var4.length);

      try {
         var5.put(var4).flip();
         int var6 = NanoVG.nvgCreateFontMem(vg, name, var5, false);
         if (var6 < 0) {
            throw new RuntimeException("nvgCreateFontMem failed for: " + name);
         } else {
            return var6;
         }
      } catch (Exception var7) {
         MemoryUtil.memFree(var5);
         throw var7;
      }
   }

   public static boolean isUseMinecraftFont() {
      return useMinecraftFont;
   }

   public static void setUseMinecraftFont(boolean useMinecraft) {
      useMinecraftFont = useMinecraft;
      NVGTextRenderer.invalidateMetrics();
   }

   public static int getFontId(boolean bold) {
      if (useMinecraftFont && minecraftFont > 0) {
         return minecraftFont;
      }

      int var1 = bold ? getBoldFont() : getRegularFont();
      if (var1 <= 0) {
         var1 = regularFont > 0 ? regularFont : (boldFont > 0 ? boldFont : 0);
      }

      return var1;
   }

   public static int getRegularFont() {
      return useMinecraftFont && minecraftFont > 0 ? minecraftFont : regularFont;
   }

   public static int getBoldFont() {
      return useMinecraftFont && minecraftFont > 0 ? minecraftFont : boldFont;
   }

   public static int getMinecraftFont() {
      return minecraftFont > 0 ? minecraftFont : regularFont;
   }

   public static int getComfortaaFont() {
      return comfortaaFont > 0 ? comfortaaFont : regularFont;
   }

   public static int getIconFont() {
      return iconFont;
   }

   public static int getTiengvietFont() {
      return tiengvietFont;
   }

   public static int getVieExternalFont() {
      return vieExternalFont > 0 ? vieExternalFont : regularFont;
   }

   public static int getSfProFont() {
      if (useMinecraftFont && minecraftFont > 0) {
         return minecraftFont;
      } else {
         return sfProRegularFont > 0 ? sfProRegularFont : regularFont;
      }
   }

   public static int getSfProSemiboldFont() {
      if (useMinecraftFont && minecraftFont > 0) {
         return minecraftFont;
      } else {
         return sfProSemiboldFont > 0 ? sfProSemiboldFont : getSfProFont();
      }
   }

   public static int getSfProBoldFont() {
      if (useMinecraftFont && minecraftFont > 0) {
         return minecraftFont;
      } else {
         return sfProBoldFont > 0 ? sfProBoldFont : getSfProSemiboldFont();
      }
   }

   public static int getTitleFont() {
      return titleFont > 0 ? titleFont : regularFont;
   }

   public static int getTitleBoldFont() {
      return titleBoldFont > 0 ? titleBoldFont : getTitleFont();
   }

   public static int getUiFont() {
      return getRegularFont();
   }

   public static int getUiBoldFont() {
      return getBoldFont();
   }

   public static int getBaseRegularFont() {
      return regularFont > 0 ? regularFont : 0;
   }

   public static int getBaseBoldFont() {
      return boldFont > 0 ? boldFont : (regularFont > 0 ? regularFont : 0);
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
