package me.drake.vieclient.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import javax.imageio.ImageIO;
import net.minecraft.class_1041;
import net.minecraft.class_155;
import net.minecraft.class_310;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WindowIcon {
   private static final Logger LOG;
   private static boolean applied = false;
   private static String lastTitle = null;
   private static int titleTickThrottle = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private WindowIcon() {
   }

   public static void apply(class_310 client) {
      if (!applied) {
         applied = true;
         if (client != null) {
            class_1041 var1 = client.method_22683();
            if (var1 != null) {
               long var2 = var1.method_4490();
               if (var2 != 0L) {
                  installIcon(client, var2);
                  installTitle(client, var2);
               }
            }
         }
      }
   }

   public static void tick(class_310 client) {
      if (applied) {
         if (client != null) {
            if (++titleTickThrottle >= (100)) {
               titleTickThrottle = 0;
               class_1041 var1 = client.method_22683();
               if (var1 != null) {
                  long var2 = var1.method_4490();
                  if (var2 != 0L) {
                     if (lastTitle == null) {
                        installTitle(client, var2);
                     } else {
                        try {
                           GLFW.glfwSetWindowTitle(var2, lastTitle);
                        } catch (Throwable var5) {
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void installTitle(class_310 client, long handle) {
      String var3 = class_155.method_16673().comp_4024();
      String var4 = "Minecraft " + var3 + " - Vie Client";
      GLFW.glfwSetWindowTitle(handle, var4);
      lastTitle = var4;
      byte[] var10001 = new byte[35];
      var10001[0] = (byte)(109);
      var10001[1] = (byte)(-52);
      var10001[2] = (byte)(117);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(-89);
      var10001[5] = (byte)(-34);
      var10001[6] = (byte)(-104);
      var10001[7] = (byte)(-124);
      var10001[8] = (byte)(3);
      var10001[9] = (byte)(-126);
      var10001[10] = (byte)(-77);
      var10001[11] = (byte)(-97);
      var10001[12] = (byte)(-40);
      var10001[13] = 1;
      var10001[14] = (byte)(-80);
      var10001[15] = (byte)(-27);
      var10001[16] = (byte)(70);
      var10001[17] = (byte)(62);
      var10001[18] = (byte)(-91);
      var10001[19] = (byte)(-38);
      var10001[20] = (byte)(50);
      var10001[21] = (byte)(40);
      var10001[22] = (byte)(-121);
      var10001[23] = (byte)(-55);
      var10001[24] = (byte)(-21);
      var10001[25] = (byte)(102);
      var10001[26] = (byte)(-76);
      var10001[27] = (byte)(50);
      var10001[28] = (byte)(9);
      var10001[29] = (byte)(21);
      var10001[30] = (byte)(-83);
      var10001[31] = (byte)(25);
      var10001[32] = (byte)(-85);
      var10001[33] = (byte)(24);
      var10001[34] = (byte)(82);
      LOG.info($$v_s(var10001, -4398781709471723501L), var4);
   }

   private static void installIcon(class_310 client, long handle) {
      String var3 = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
      if (!var3.contains("mac")
         && !var3.contains("darwin")) {
         int[] var4 = new int[]{16, 24, 32, 48, 64, 128, 256};

         try {
            MemoryStack var5 = MemoryStack.stackPush();

            label92: {
               label93: {
                  label99: {
                     try {
                        BufferedImage var6 = loadSourceIcon();
                        if (var6 == null) {
                           LOG.warn(
                              "[VieIcon] Failed to load VIE icon from assets — taskbar will keep Mojang's icon."
                           );
                           break label99;
                        }

                        BufferedImage var7 = trimTransparentPadding(var6);
                        if (var7 == null) {
                           LOG.warn(
                              "[VieIcon] VIE icon appears fully transparent — taskbar will keep Mojang's icon."
                           );
                           break label93;
                        }

                        LOG.info(
                           "[VieIcon] Source icon {}x{} → content {}x{} (trimmed transparent padding).",
                           new Object[]{var6.getWidth(), var6.getHeight(), var7.getWidth(), var7.getHeight()}
                        );
                        Buffer var8 = GLFWImage.malloc(var4.length);

                        for (int var9 = 0; var9 < var4.length; var9++) {
                           ByteBuffer var10 = renderIconAt(var7, var4[var9]);
                           if (var10 == null) {
                              LOG.warn(
                                 "[VieIcon] Failed to render {}x{} icon variant — aborting inject.",
                                 var4[var9],
                                 var4[var9]
                              );
                              break label92;
                           }

                           GLFWImage var11 = GLFWImage.malloc();
                           var11.set(var4[var9], var4[var9], var10);
                           var8.put(var9, var11);
                        }

                        GLFW.glfwSetWindowIcon(handle, var8);
                        LOG.info(
                           "[VieIcon] Taskbar/window icon set to VIE dragon (sizes: {}).",
                           Arrays.toString(var4)
                        );
                     } catch (Throwable var13) {
                        if (var5 != null) {
                           try {
                              var5.close();
                           } catch (Throwable var12) {
                              var13.addSuppressed(var12);
                           }
                        }

                        throw var13;
                     }

                     if (var5 != null) {
                        var5.close();
                     }

                     return;
                  }

                  if (var5 != null) {
                     var5.close();
                  }

                  return;
               }

               if (var5 != null) {
                  var5.close();
               }

               return;
            }

            if (var5 != null) {
               var5.close();
            }
         } catch (Throwable var14) {
            LOG.error(
               "[VieIcon] glfwSetWindowIcon failed — taskbar keeps the Mojang icon.",
               var14
            );
         }
      }
   }

   private static BufferedImage loadSourceIcon() {
      try (InputStream var0 = WindowIcon.class
            .getResourceAsStream(
               "/assets/vie_client/icon.png"
            )) {
         return var0 == null ? null : ImageIO.read(var0);
      } catch (Throwable var5) {
         LOG.error(
            "[VieIcon] Could not decode assets/vie_client/icon.png",
            var5
         );
         return null;
      }
   }

   private static BufferedImage trimTransparentPadding(BufferedImage src) {
      int var1 = src.getWidth();
      int var2 = src.getHeight();
      if (var1 > 0 && var2 > 0) {
         int[] var3 = src.getRGB(0, 0, var1, var2, null, 0, var1);
         int var4 = var1;
         int var5 = var2;
         int var6 = -1;
         int var7 = -1;

         for (int var8 = 0; var8 < var2; var8++) {
            for (int var9 = 0; var9 < var1; var9++) {
               int var10 = var3[var8 * var1 + var9] >>> (24) & (255);
               if (var10 > (4)) {
                  if (var9 < var4) {
                     var4 = var9;
                  }

                  if (var9 > var6) {
                     var6 = var9;
                  }

                  if (var8 < var5) {
                     var5 = var8;
                  }

                  if (var8 > var7) {
                     var7 = var8;
                  }
               }
            }
         }

         if (var6 < var4) {
            return null;
         }

         int var11 = var6 - var4 + 1;
         int var12 = var7 - var5 + 1;
         return src.getSubimage(var4, var5, var11, var12);
      } else {
         return null;
      }
   }

   private static ByteBuffer renderIconAt(BufferedImage content, int size) {
      int var2 = content.getWidth();
      int var3 = content.getHeight();
      float var4 = Math.min((float)size / var2, (float)size / var3) * 0.94F;
      int var5 = Math.max(1, Math.round(var2 * var4));
      int var6 = Math.max(1, Math.round(var3 * var4));
      int var7 = (size - var5) / (2);
      int var8 = (size - var6) / (2);
      BufferedImage var9 = new BufferedImage(size, size, 2);
      Graphics2D var10 = var9.createGraphics();

      try {
         var10.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         var10.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         var10.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         var10.drawImage(content, var7, var8, var5, var6, null);
      } finally {
         var10.dispose();
      }

      int[] var11 = new int[size * size];
      var9.getRGB(0, 0, size, size, var11, 0, size);
      ByteBuffer var12 = MemoryUtil.memAlloc(var11.length * (4));

      for (int var16 : var11) {
         var12.put((byte)(var16 >> (16) & (255)));
         var12.put((byte)(var16 >> (8) & (255)));
         var12.put((byte)(var16 & (255)));
         var12.put((byte)(var16 >> (24) & (255)));
      }

      var12.flip();
      return var12;
   }

   static {
      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(96);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-105);
      var10000[4] = (byte)(-115);
      var10000[5] = (byte)(-46);
      var10000[6] = (byte)(-103);
      LOG = LoggerFactory.getLogger($$v_s(var10000, -4398781709471723501L));
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
