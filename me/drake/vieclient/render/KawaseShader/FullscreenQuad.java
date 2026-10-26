package me.drake.vieclient.render.KawaseShader;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.VieClientMod;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class FullscreenQuad {
   private int vao;
   private int vbo;
   private int ebo;
   private int vertexCount = 6;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FullscreenQuad() {
      this.createQuad();
   }

   private void createQuad() {
      try {
         float[] var1 = new float[]{-1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, -1.0F, 1.0F, 0.0F, -1.0F, -1.0F, 0.0F, 0.0F};
         int[] var2 = new int[]{0, 1, 2, 2, 3, 0};
         this.vao = GL30.glGenVertexArrays();
         GL30.glBindVertexArray(this.vao);
         this.vbo = GL15.glGenBuffers();
         GL15.glBindBuffer(34962, this.vbo);
         GL15.glBufferData(34962, var1, 35044);
         this.ebo = GL15.glGenBuffers();
         GL15.glBindBuffer(34963, this.ebo);
         GL15.glBufferData(34963, var2, 35044);
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 2, 5126, false, 16, 0L);
         GL20.glEnableVertexAttribArray(1);
         GL20.glVertexAttribPointer(1, 2, 5126, false, 16, 8L);
         GL30.glBindVertexArray(0);
         GL15.glBindBuffer(34962, 0);
         GL15.glBindBuffer(34963, 0);
         VieClientMod.LOGGER
            .info(
               "[VieClient] FullscreenQuad created (VAO: {})",
               this.vao
            );
      } catch (Exception var3) {
         VieClientMod.LOGGER
            .error(
               "[VieClient] FullscreenQuad create failed",
               var3
            );
         throw new RuntimeException(
            "Failed to create fullscreen quad",
            var3
         );
      }
   }

   public void render() {
      if (this.vao == 0) {
         byte[] var10001 = new byte[42];
         var10001[0] = (byte)(101);
         var10001[1] = (byte)(-52);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(74);
         var10001[5] = (byte)(-119);
         var10001[6] = (byte)(15);
         var10001[7] = (byte)(-40);
         var10001[8] = (byte)(56);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-71);
         var10001[11] = (byte)(-42);
         var10001[12] = (byte)(23);
         var10001[13] = (byte)(72);
         var10001[14] = (byte)(34);
         var10001[15] = (byte)(-87);
         var10001[16] = (byte)(29);
         var10001[17] = (byte)(41);
         var10001[18] = (byte)(-66);
         var10001[19] = (byte)(-53);
         var10001[20] = (byte)(-36);
         var10001[21] = (byte)(123);
         var10001[22] = (byte)(103);
         var10001[23] = (byte)(-104);
         var10001[24] = (byte)(-25);
         var10001[25] = (byte)(118);
         var10001[26] = (byte)(-76);
         var10001[27] = (byte)(40);
         var10001[28] = (byte)(-18);
         var10001[29] = (byte)(25);
         var10001[30] = (byte)(62);
         var10001[31] = (byte)(92);
         var10001[32] = (byte)(-80);
         var10001[33] = (byte)(83);
         var10001[34] = (byte)(8);
         var10001[35] = (byte)(23);
         var10001[36] = (byte)(-120);
         var10001[37] = (byte)(41);
         var10001[38] = (byte)(-81);
         var10001[39] = (byte)(39);
         var10001[40] = (byte)(-109);
         var10001[41] = (byte)(-90);
         VieClientMod.LOGGER.warn($$v_s(var10001, -7681667961061126117L));
      } else {
         GL30.glBindVertexArray(this.vao);
         GL11.glDrawElements(4, this.vertexCount, 5125, 0L);
         GL30.glBindVertexArray(0);
      }
   }

   public void cleanup() {
      if (this.vao != 0) {
         GL30.glDeleteVertexArrays(this.vao);
         this.vao = 0;
      }

      if (this.vbo != 0) {
         GL15.glDeleteBuffers(this.vbo);
         this.vbo = 0;
      }

      if (this.ebo != 0) {
         GL15.glDeleteBuffers(this.ebo);
         this.ebo = 0;
      }

      byte[] var10001 = new byte[37];
      var10001[0] = (byte)(101);
      var10001[1] = (byte)(-52);
      var10001[2] = (byte)(117);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(74);
      var10001[5] = (byte)(-119);
      var10001[6] = (byte)(15);
      var10001[7] = (byte)(-40);
      var10001[8] = (byte)(56);
      var10001[9] = (byte)(-42);
      var10001[10] = (byte)(-71);
      var10001[11] = (byte)(-42);
      var10001[12] = (byte)(23);
      var10001[13] = (byte)(72);
      var10001[14] = (byte)(34);
      var10001[15] = (byte)(-87);
      var10001[16] = (byte)(29);
      var10001[17] = (byte)(41);
      var10001[18] = (byte)(-66);
      var10001[19] = (byte)(-53);
      var10001[20] = (byte)(-36);
      var10001[21] = (byte)(123);
      var10001[22] = (byte)(103);
      var10001[23] = (byte)(-104);
      var10001[24] = (byte)(-25);
      var10001[25] = (byte)(118);
      var10001[26] = (byte)(-76);
      var10001[27] = (byte)(37);
      var10001[28] = (byte)(-19);
      var10001[29] = (byte)(8);
      var10001[30] = (byte)(127);
      var10001[31] = (byte)(91);
      var10001[32] = (byte)(-69);
      var10001[33] = (byte)(94);
      var10001[34] = (byte)(92);
      var10001[35] = (byte)(11);
      var10001[36] = (byte)(-103);
      VieClientMod.LOGGER.info($$v_s(var10001, -7681667961061126117L));
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
