package me.drake.vieclient.render.KawaseShader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import me.drake.vieclient.VieClientMod;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

public class KawaseBlurShader {
   private int program;
   private int vertexShader;
   private int fragmentShader;
   private int textureLoc = -1;
   private int offsetLoc = -1;
   private int texelSizeLoc = -1;
   private static final String VERT_SRC = "#version 330 core\nlayout (location = 0) in vec2 a_Pos;\nlayout (location = 1) in vec2 a_Uv;\nout vec2 v_Uv;\nvoid main() {\n    gl_Position = vec4(a_Pos, 0.0, 1.0);\n    v_Uv = a_Uv;\n}\n";
   private static final String FRAG_SRC = "#version 330 core\n\nuniform sampler2D u_Tex;\nuniform vec2 u_Texel;    // 1/width, 1/height\nuniform float u_Offset;  // blur strength\n\nin vec2 v_Uv;\nout vec4 fragColor;\n\nvoid main() {\n    vec2 off = u_Offset * u_Texel;\n\n    // Kawase Blur - 4-tap\n    vec4 c = texture(u_Tex, v_Uv + vec2(off.x,  off.y))\n           + texture(u_Tex, v_Uv + vec2(-off.x, off.y))\n           + texture(u_Tex, v_Uv + vec2(off.x, -off.y))\n           + texture(u_Tex, v_Uv + vec2(-off.x, -off.y));\n\n    fragColor = c * 0.25;\n}\n";
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public KawaseBlurShader() {
      this.createShader();
   }

   private void createShader() {
      try {
         this.vertexShader = this.compileShader(
            loadAsset(
               "kawase_blur.vsh",
               "#version 330 core
layout (location = 0) in vec2 a_Pos;
layout (location = 1) in vec2 a_Uv;
out vec2 v_Uv;
void main() {
    gl_Position = vec4(a_Pos, 0.0, 1.0);
    v_Uv = a_Uv;
}
"
            ),
            35633,
            "kawase_blur.vsh"
         );
         this.fragmentShader = this.compileShader(
            loadAsset(
               "kawase_blur.fsh",
               "#version 330 core

uniform sampler2D u_Tex;
uniform vec2 u_Texel;    // 1/width, 1/height
uniform float u_Offset;  // blur strength

in vec2 v_Uv;
out vec4 fragColor;

void main() {
    vec2 off = u_Offset * u_Texel;

    // Kawase Blur - 4-tap
    vec4 c = texture(u_Tex, v_Uv + vec2(off.x,  off.y))
           + texture(u_Tex, v_Uv + vec2(-off.x, off.y))
           + texture(u_Tex, v_Uv + vec2(off.x, -off.y))
           + texture(u_Tex, v_Uv + vec2(-off.x, -off.y));

    fragColor = c * 0.25;
}
"
            ),
            35632,
            "kawase_blur.fsh"
         );
         if (this.vertexShader != 0 && this.fragmentShader != 0) {
            this.program = GL20.glCreateProgram();
            GL20.glAttachShader(this.program, this.vertexShader);
            GL20.glAttachShader(this.program, this.fragmentShader);
            GL20.glLinkProgram(this.program);
            if (GL20.glGetProgrami(this.program, 35714) == 0) {
               String var1 = GL20.glGetProgramInfoLog(this.program);
               throw new RuntimeException("Shader link failed: " + var1);
            }

            GL20.glUseProgram(this.program);
            this.textureLoc = GL20.glGetUniformLocation(this.program, "u_Tex");
            this.offsetLoc = GL20.glGetUniformLocation(this.program, "u_Offset");
            this.texelSizeLoc = GL20.glGetUniformLocation(this.program, "u_Texel");
            GL20.glUniform1i(this.textureLoc, 0);
            GL20.glUseProgram(0);
            GL20.glDetachShader(this.program, this.vertexShader);
            GL20.glDetachShader(this.program, this.fragmentShader);
            GL20.glDeleteShader(this.vertexShader);
            GL20.glDeleteShader(this.fragmentShader);
            this.vertexShader = 0;
            this.fragmentShader = 0;
            VieClientMod.LOGGER
               .info(
                  "[VieClient] KawaseBlurShader created (Program: {})",
                  this.program
               );
         } else {
            throw new RuntimeException(
               "Shader compilation failed"
            );
         }
      } catch (Exception var2) {
         VieClientMod.LOGGER
            .error(
               "[VieClient] KawaseBlurShader create failed",
               var2
            );
         this.cleanup();
         throw new RuntimeException(
            "Failed to create blur shader",
            var2
         );
      }
   }

   public void use() {
      if (this.program != 0) {
         GL20.glUseProgram(this.program);
      }
   }

   public void setTexture(int textureId) {
      if (this.textureLoc != (-1)) {
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, textureId);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL20.glUniform1i(this.textureLoc, 0);
      }
   }

   public void setOffset(float offset) {
      if (this.offsetLoc != (-1)) {
         GL20.glUniform1f(this.offsetLoc, offset);
      }
   }

   public void setTexelSize(float width, float height) {
      if (this.texelSizeLoc != (-1)) {
         float var3 = 1.0F / width;
         float var4 = 1.0F / height;
         GL20.glUniform2f(this.texelSizeLoc, var3, var4);
      }
   }

   private static String loadAsset(String name, String fallback) {
      try {
         class_310 var2 = class_310.method_1551();
         if (var2 == null) {
            return fallback;
         }

         class_3300 var3 = var2.method_1478();
         Optional var4 = var3.method_14486(
            class_2960.method_60655("vie_client", "shaders/kawase/" + name)
         );
         if (var4.isPresent()) {
            try (InputStream var5 = ((class_3298)var4.get()).method_14482()) {
               VieClientMod.LOGGER
                  .info(
                     "[VieClient] Loaded KawaseBlurShader from {}",
                     name
                  );
               return IOUtils.toString(var5, StandardCharsets.UTF_8);
            }
         }
      } catch (Exception var10) {
         VieClientMod.LOGGER
            .warn(
               "[VieClient] KawaseBlurShader {} unreadable, using fallback",
               name
            );
      }

      return fallback;
   }

   private int compileShader(String source, int type, String tag) {
      int var4 = GL20.glCreateShader(type);
      GL20.glShaderSource(var4, source);
      GL20.glCompileShader(var4);
      if (GL20.glGetShaderi(var4, 35713) == 0) {
         String var5 = GL20.glGetShaderInfoLog(var4);
         byte[] var10001 = new byte[41];
         var10001[0] = (byte)(114);
         var10001[1] = (byte)(-52);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(-82);
         var10001[5] = (byte)(121);
         var10001[6] = (byte)(72);
         var10001[7] = (byte)(-22);
         var10001[8] = (byte)(47);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-71);
         var10001[11] = (byte)(-42);
         var10001[12] = (byte)(-26);
         var10001[13] = (byte)(-91);
         var10001[14] = (byte)(104);
         var10001[15] = (byte)(-109);
         var10001[16] = (byte)(28);
         var10001[17] = (byte)(56);
         var10001[18] = (byte)(-20);
         var10001[19] = (byte)(-51);
         var10001[20] = (byte)(50);
         var10001[21] = (byte)(-120);
         var10001[22] = 1;
         var10001[23] = (byte)(-74);
         var10001[24] = (byte)(-3);
         var10001[25] = (byte)(119);
         var10001[26] = (byte)(-76);
         var10001[27] = (byte)(35);
         var10001[28] = (byte)(23);
         var10001[29] = (byte)(-17);
         var10001[30] = (byte)(54);
         var10001[31] = (byte)(117);
         var10001[32] = (byte)(-23);
         var10001[33] = (byte)(18);
         var10001[34] = (byte)(7);
         var10001[35] = (byte)(3);
         var10001[36] = (byte)(36);
         var10001[37] = (byte)(-113);
         var10001[38] = (byte)(-95);
         var10001[39] = (byte)(20);
         var10001[40] = (byte)(-100);
         VieClientMod.LOGGER.error($$v_s(var10001, -6403332157280628724L), tag, var5);
         GL20.glDeleteShader(var4);
         return 0;
      } else {
         return var4;
      }
   }

   public void cleanup() {
      if (this.program != 0) {
         GL20.glDeleteProgram(this.program);
         this.program = 0;
      }

      if (this.vertexShader != 0) {
         GL20.glDeleteShader(this.vertexShader);
         this.vertexShader = 0;
      }

      if (this.fragmentShader != 0) {
         GL20.glDeleteShader(this.fragmentShader);
         this.fragmentShader = 0;
      }

      byte[] var10001 = new byte[39];
      var10001[0] = (byte)(114);
      var10001[1] = (byte)(-52);
      var10001[2] = (byte)(117);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(-82);
      var10001[5] = (byte)(121);
      var10001[6] = (byte)(72);
      var10001[7] = (byte)(-22);
      var10001[8] = (byte)(47);
      var10001[9] = (byte)(-42);
      var10001[10] = (byte)(-71);
      var10001[11] = (byte)(-42);
      var10001[12] = (byte)(-2);
      var10001[13] = (byte)(-84);
      var10001[14] = (byte)(126);
      var10001[15] = (byte)(-106);
      var10001[16] = (byte)(10);
      var10001[17] = (byte)(47);
      var10001[18] = (byte)(-114);
      var10001[19] = (byte)(-62);
      var10001[20] = (byte)(40);
      var10001[21] = (byte)(-105);
      var10001[22] = (byte)(34);
      var10001[23] = (byte)(-73);
      var10001[24] = (byte)(-16);
      var10001[25] = (byte)(118);
      var10001[26] = (byte)(-15);
      var10001[27] = (byte)(52);
      var10001[28] = (byte)(69);
      var10001[29] = (byte)(-2);
      var10001[30] = (byte)(53);
      var10001[31] = (byte)(98);
      var10001[32] = (byte)(-88);
      var10001[33] = (byte)(84);
      var10001[34] = (byte)(25);
      var10001[35] = (byte)(26);
      var10001[36] = (byte)(45);
      var10001[37] = (byte)(-64);
      var10001[38] = (byte)(-15);
      VieClientMod.LOGGER.info($$v_s(var10001, -6403332157280628724L));
   }

   public int getProgram() {
      return this.program;
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
