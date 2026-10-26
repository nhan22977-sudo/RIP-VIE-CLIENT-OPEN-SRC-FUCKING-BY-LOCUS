package me.drake.vieclient.render.KawaseShader;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.VieClientMod;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class FramebufferHelper {
   private int fboId;
   private int textureId;
   private int depthRboId;
   private final int width;
   private final int height;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FramebufferHelper(int width, int height) {
      this.width = widthx;
      this.height = heightx;
      this.createFramebuffer();
   }

   private void createFramebuffer() {
      try {
         this.fboId = GL30.glGenFramebuffers();
         if (this.fboId == 0) {
            throw new RuntimeException(
               "Failed to create framebuffer"
            );
         }

         GL30.glBindFramebuffer(36160, this.fboId);
         this.textureId = GL11.glGenTextures();
         GL11.glBindTexture(3553, this.textureId);
         GL11.glTexParameteri(3553, 10242, 10496);
         GL11.glTexParameteri(3553, 10243, 10496);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5121, (ByteBuffer)null);
         GL30.glFramebufferTexture2D(36160, 36064, 3553, this.textureId, 0);
         int var1 = GL30.glCheckFramebufferStatus(36160);
         if (var1 != 36053) {
            throw new RuntimeException("FBO not complete! Status: " + var1);
         }

         GL30.glBindFramebuffer(36160, 0);
         GL11.glBindTexture(3553, 0);
         VieClientMod.LOGGER
            .info(
               "[VieClient] FramebufferHelper created: {}x{} (FBO: {})",
               new Object[]{this.width, this.height, this.fboId}
            );
      } catch (Exception var2) {
         VieClientMod.LOGGER
            .error(
               "[VieClient] FramebufferHelper create failed",
               var2
            );
         this.cleanup();
         throw new RuntimeException(
            "Failed to create framebuffer",
            var2
         );
      }
   }

   public void bind() {
      if (this.fboId != 0) {
         GL30.glBindFramebuffer(36160, this.fboId);
         GL11.glViewport(0, 0, this.width, this.height);
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClear(16640);
      }
   }

   public void unbind(int screenWidth, int screenHeight) {
      if (this.fboId != 0) {
         GL30.glBindFramebuffer(36160, 0);
         GL11.glViewport(0, 0, screenWidth, screenHeight);
      }
   }

   public int getTextureId() {
      return this.textureId;
   }

   public int getFboId() {
      return this.fboId;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void cleanup() {
      if (this.fboId != 0) {
         GL30.glDeleteFramebuffers(this.fboId);
         this.fboId = 0;
      }

      if (this.textureId != 0) {
         GL11.glDeleteTextures(this.textureId);
         this.textureId = 0;
      }

      if (this.depthRboId != 0) {
         GL30.glDeleteRenderbuffers(this.depthRboId);
         this.depthRboId = 0;
      }

      byte[] var10001 = new byte[40];
      var10001[0] = (byte)(101);
      var10001[1] = (byte)(-52);
      var10001[2] = (byte)(117);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(87);
      var10001[5] = (byte)(-37);
      var10001[6] = (byte)(104);
      var10001[7] = (byte)(-59);
      var10001[8] = (byte)(56);
      var10001[9] = (byte)(-42);
      var10001[10] = (byte)(-71);
      var10001[11] = (byte)(-42);
      var10001[12] = (byte)(10);
      var10001[13] = (byte)(29);
      var10001[14] = (byte)(72);
      var10001[15] = (byte)(-75);
      var10001[16] = (byte)(11);
      var10001[17] = (byte)(40);
      var10001[18] = (byte)(-71);
      var10001[19] = (byte)(-56);
      var10001[20] = (byte)(-62);
      var10001[21] = (byte)(34);
      var10001[22] = (byte)(35);
      var10001[23] = (byte)(-72);
      var10001[24] = (byte)(-29);
      var10001[25] = (byte)(126);
      var10001[26] = (byte)(-28);
      var10001[27] = (byte)(35);
      var10001[28] = (byte)(-18);
      var10001[29] = (byte)(31);
      var10001[30] = (byte)(26);
      var10001[31] = (byte)(68);
      var10001[32] = (byte)(-69);
      var10001[33] = (byte)(91);
      var10001[34] = (byte)(18);
      var10001[35] = (byte)(27);
      var10001[36] = (byte)(-112);
      var10001[37] = (byte)(55);
      var10001[38] = (byte)(-44);
      var10001[39] = (byte)(48);
      VieClientMod.LOGGER.info($$v_s(var10001, -8646232141598568421L));
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
