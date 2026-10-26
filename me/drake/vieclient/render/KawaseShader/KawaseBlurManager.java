package me.drake.vieclient.render.KawaseShader;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.VieClientMod;
import net.minecraft.class_310;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class KawaseBlurManager {
   private static FramebufferHelper fbo;
   private static KawaseBlurShader blurShader;
   private static FullscreenQuad quad;
   private static int screenWidth = 0;
   private static int screenHeight = 0;
   private static boolean initialized = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static void init(int width, int height) {
      if (!initialized || screenWidth != width || screenHeight != height) {
         try {
            cleanup();
            screenWidth = width;
            screenHeight = height;
            fbo = new FramebufferHelper(width, height);
            blurShader = new KawaseBlurShader();
            quad = new FullscreenQuad();
            initialized = true;
            VieClientMod.LOGGER
               .info(
                  "[VieClient] Kawase Blur initialized: {}x{}",
                  width,
                  height
               );
         } catch (Exception var3) {
            VieClientMod.LOGGER
               .error(
                  "[VieClient] Blur init failed",
                  var3
               );
            initialized = false;
         }
      }
   }

   public static void startCapture() {
      if (initialized && fbo != null) {
         try {
            fbo.bind();
         } catch (Exception var1) {
            byte[] var10001 = new byte[31];
            var10001[0] = (byte)(102);
            var10001[1] = (byte)(-52);
            var10001[2] = (byte)(117);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(76);
            var10001[5] = (byte)(-118);
            var10001[6] = (byte)(-66);
            var10001[7] = (byte)(42);
            var10001[8] = (byte)(59);
            var10001[9] = (byte)(-42);
            var10001[10] = (byte)(-71);
            var10001[11] = (byte)(-42);
            var10001[12] = (byte)(36);
            var10001[13] = (byte)(74);
            var10001[14] = (byte)(-98);
            var10001[15] = (byte)(69);
            var10001[16] = (byte)(25);
            var10001[17] = (byte)(9);
            var10001[18] = (byte)(-83);
            var10001[19] = (byte)(-34);
            var10001[20] = (byte)(-53);
            var10001[21] = (byte)(99);
            var10001[22] = (byte)(-11);
            var10001[23] = (byte)(122);
            var10001[24] = (byte)(-91);
            var10001[25] = (byte)(116);
            var10001[26] = (byte)(-11);
            var10001[27] = (byte)(47);
            var10001[28] = (byte)(-21);
            var10001[29] = (byte)(11);
            var10001[30] = (byte)(-53);
            VieClientMod.LOGGER.error($$v_s(var10001, 7481667236553543704L), var1);
         }
      }
   }

   public static void endCapture() {
      if (initialized && fbo != null && blurShader != null && quad != null) {
         try {
            class_310 var0 = class_310.method_1551();
            int var1 = fbo.getTextureId();
            if (var1 == 0) {
               return;
            }

            GL30.glBindFramebuffer(36160, 0);
            GL11.glViewport(0, 0, var0.method_22683().method_4489(), var0.method_22683().method_4506());
            boolean var2 = GL11.glIsEnabled(2929);
            boolean var3 = GL11.glIsEnabled(2884);
            boolean var4 = GL11.glIsEnabled(3042);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            if (!var4) {
               GL11.glEnable(3042);
            }

            GL11.glBlendFunc(770, 771);
            blurShader.use();
            blurShader.setTexture(var1);
            blurShader.setTexelSize(var0.method_22683().method_4489(), var0.method_22683().method_4506());
            blurShader.setOffset(2.0F);
            quad.render();
            GL20.glUseProgram(0);
            if (var2) {
               GL11.glEnable(2929);
            }

            if (var3) {
               GL11.glEnable(2884);
            }

            if (!var4) {
               GL11.glDisable(3042);
            }
         } catch (Exception var5) {
            byte[] var10001 = new byte[29];
            var10001[0] = (byte)(102);
            var10001[1] = (byte)(-52);
            var10001[2] = (byte)(117);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(76);
            var10001[5] = (byte)(-118);
            var10001[6] = (byte)(-66);
            var10001[7] = (byte)(42);
            var10001[8] = (byte)(59);
            var10001[9] = (byte)(-42);
            var10001[10] = (byte)(-71);
            var10001[11] = (byte)(-42);
            var10001[12] = (byte)(50);
            var10001[13] = (byte)(80);
            var10001[14] = (byte)(-101);
            var10001[15] = (byte)(116);
            var10001[16] = (byte)(12);
            var10001[17] = (byte)(58);
            var10001[18] = (byte)(-72);
            var10001[19] = (byte)(-37);
            var10001[20] = (byte)(-51);
            var10001[21] = (byte)(115);
            var10001[22] = (byte)(-89);
            var10001[23] = (byte)(121);
            var10001[24] = (byte)(-28);
            var10001[25] = (byte)(123);
            var10001[26] = (byte)(-8);
            var10001[27] = (byte)(35);
            var10001[28] = (byte)(-29);
            VieClientMod.LOGGER.error($$v_s(var10001, 7481667236553543704L), var5);
         }
      }
   }

   public static void cleanup() {
      try {
         if (fbo != null) {
            fbo.cleanup();
            fbo = null;
         }

         if (blurShader != null) {
            blurShader.cleanup();
            blurShader = null;
         }

         if (quad != null) {
            quad.cleanup();
            quad = null;
         }

         initialized = false;
         VieClientMod.LOGGER
            .info(
               "[VieClient] Blur cleaned up"
            );
      } catch (Exception var1) {
         VieClientMod.LOGGER
            .error(
               "[VieClient] Blur cleanup failed",
               var1
            );
      }
   }

   public static boolean isInitialized() {
      return initialized;
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
