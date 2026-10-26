package me.drake.vieclient.util;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import me.drake.vieclient.VieClientMod;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class WindowIconHelper {
   private static final int[] ICON_SIZES;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private WindowIconHelper() {
   }

   public static void apply(long windowHandle) {
      if (windowHandle != 0L) {
         int var2 = GLFW.glfwGetPlatform();
         if (var2 == 393217 || var2 == 393220 || var2 == 393219) {
            ArrayList var3 = new ArrayList(ICON_SIZES.length);

            try {
               MemoryStack var4 = MemoryStack.stackPush();

               try {
                  Buffer var5 = GLFWImage.malloc(ICON_SIZES.length, var4);
                  int var6 = 0;

                  for (int var10 : ICON_SIZES) {
                     ByteBuffer var11 = loadIconData("/assets/vie_client/icons/icon_" + var10 + ".png", var4);
                     if (var11 != null) {
                        var3.add(var11);
                        var5.position(var6++);
                        var5.width(var10);
                        var5.height(var10);
                        var5.pixels(var11);
                     }
                  }

                  if (var6 > 0) {
                     var5.position(0);
                     var5.limit(var6);
                     GLFW.glfwSetWindowIcon(windowHandle, var5);
                     VieClientMod.LOGGER
                        .info(
                           "[VieClient] Successfully applied custom window icons ({} sizes).",
                           var6
                        );
                  }
               } catch (Throwable var20) {
                  if (var4 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var19) {
                        var20.addSuppressed(var19);
                     }
                  }

                  throw var20;
               }

               if (var4 != null) {
                  var4.close();
               }
            } catch (Throwable var21) {
               VieClientMod.LOGGER
                  .warn(
                     "[VieClient] Failed to apply custom window icon: {}",
                     var21.getMessage()
                  );
            } finally {
               for (ByteBuffer var14 : var3) {
                  STBImage.stbi_image_free(var14);
               }
            }
         }
      }
   }

   private static ByteBuffer loadIconData(String resourcePath, MemoryStack stack) {
      try (InputStream var2 = WindowIconHelper.class.getResourceAsStream(resourcePath)) {
         if (var2 == null) {
            return null;
         }

         byte[] var3 = var2.readAllBytes();
         ByteBuffer var4 = MemoryUtil.memAlloc(var3.length);

         try {
            var4.put(var3).flip();
            IntBuffer var5 = stack.mallocInt(1);
            IntBuffer var6 = stack.mallocInt(1);
            IntBuffer var7 = stack.mallocInt(1);
            return STBImage.stbi_load_from_memory(var4, var5, var6, var7, 4);
         } finally {
            MemoryUtil.memFree(var4);
         }
      } catch (Throwable var17) {
         return null;
      }
   }

   static {
      int[] var10000 = new int[6];
      var10000[0] = 16;
      var10000[1] = 32;
      var10000[2] = 48;
      var10000[3] = 64;
      var10000[4] = 128;
      var10000[5] = 256;
      ICON_SIZES = var10000;
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
