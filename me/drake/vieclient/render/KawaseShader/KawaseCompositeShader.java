package me.drake.vieclient.render.KawaseShader;

import java.io.IOException;
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

public final class KawaseCompositeShader {
   public final int program;
   private final int textureLoc;
   private static final String VERT = "#version 330 core\nlayout (location = 0) in vec2 a_Pos;\nlayout (location = 1) in vec2 a_Uv;\nout vec2 v_Uv;\nvoid main() {\n    gl_Position = vec4(a_Pos, 0.0, 1.0);\n    v_Uv = a_Uv;\n}\n";
   private static final String FRAG = "#version 330 core\nuniform sampler2D u_Tex;\nin vec2 v_Uv;\nout vec4 fragColor;\nvoid main() {\n    fragColor = vec4(texture(u_Tex, v_Uv).rgb, 1.0);\n}\n";
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public KawaseCompositeShader() {
      int var1 = compile(
         load(
            "kawase_composite.vsh",
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
         "kawase_composite.vsh"
      );
      int var2 = compile(
         load(
            "kawase_composite.fsh",
            "#version 330 core
uniform sampler2D u_Tex;
in vec2 v_Uv;
out vec4 fragColor;
void main() {
    fragColor = vec4(texture(u_Tex, v_Uv).rgb, 1.0);
}
"
         ),
         35632,
         "kawase_composite.fsh"
      );
      int var3 = 0;
      if (var1 != 0 && var2 != 0) {
         var3 = GL20.glCreateProgram();
         GL20.glAttachShader(var3, var1);
         GL20.glAttachShader(var3, var2);
         GL20.glLinkProgram(var3);
         GL20.glDetachShader(var3, var1);
         GL20.glDetachShader(var3, var2);
         if (GL20.glGetProgrami(var3, 35714) == 0) {
            VieClientMod.LOGGER
               .error(
                  "[VieClient] KawaseCompositeShader link error: {}",
                  GL20.glGetProgramInfoLog(var3)
               );
            GL20.glDeleteProgram(var3);
            var3 = 0;
         }
      }

      if (var1 != 0) {
         GL20.glDeleteShader(var1);
      }

      if (var2 != 0) {
         GL20.glDeleteShader(var2);
      }

      this.program = var3;
      this.textureLoc = var3 == 0 ? -1 : GL20.glGetUniformLocation(var3, "u_Tex");
      if (var3 != 0) {
         GL20.glUseProgram(var3);
         GL20.glUniform1i(this.textureLoc, 0);
         GL20.glUseProgram(0);
      }
   }

   public void use() {
      GL20.glUseProgram(this.program);
   }

   public void setTexture(int textureId) {
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, textureId);
      GL20.glUniform1i(this.textureLoc, 0);
   }

   public void cleanup() {
      if (this.program != 0) {
         GL20.glDeleteProgram(this.program);
      }
   }

   private static String load(String name, String fallback) {
      try {
         class_310 var2 = class_310.method_1551();
         class_3300 var3 = var2.method_1478();
         Optional var4 = var3.method_14486(
            class_2960.method_60655("vie_client", "shaders/kawase/" + name)
         );
         if (var4.isPresent()) {
            try (InputStream var5 = ((class_3298)var4.get()).method_14482()) {
               return IOUtils.toString(var5, StandardCharsets.UTF_8);
            }
         }
      } catch (IOException var10) {
         VieClientMod.LOGGER
            .warn(
               "[VieClient] KawaseCompositeShader {} unreadable, using fallback",
               name
            );
      }

      return fallback;
   }

   private static int compile(String src, int type, String tag) {
      int var3 = GL20.glCreateShader(type);
      GL20.glShaderSource(var3, src);
      GL20.glCompileShader(var3);
      if (GL20.glGetShaderi(var3, 35713) == 0) {
         byte[] var10001 = new byte[54];
         var10001[0] = (byte)(107);
         var10001[1] = (byte)(-52);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(86);
         var10001[5] = (byte)(116);
         var10001[6] = (byte)(-62);
         var10001[7] = (byte)(-64);
         var10001[8] = (byte)(54);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-71);
         var10001[11] = (byte)(-42);
         var10001[12] = (byte)(6);
         var10001[13] = (byte)(-95);
         var10001[14] = (byte)(-12);
         var10001[15] = (byte)(-68);
         var10001[16] = (byte)(19);
         var10001[17] = (byte)(47);
         var10001[18] = (byte)(-113);
         var10001[19] = (byte)(-63);
         var10001[20] = (byte)(-56);
         var10001[21] = (byte)(-104);
         var10001[22] = (byte)(-108);
         var10001[23] = (byte)(-122);
         var10001[24] = (byte)(-31);
         var10001[25] = (byte)(102);
         var10001[26] = (byte)(-15);
         var10001[27] = (byte)(21);
         var10001[28] = (byte)(-11);
         var10001[29] = (byte)(-15);
         var10001[30] = (byte)(-73);
         var10001[31] = (byte)(72);
         var10001[32] = (byte)(-94);
         var10001[33] = (byte)(26);
         var10001[34] = (byte)(7);
         var10001[35] = (byte)(3);
         var10001[36] = (byte)(-43);
         var10001[37] = (byte)(-37);
         var10001[38] = (byte)(100);
         var10001[39] = (byte)(40);
         var10001[40] = (byte)(-120);
         var10001[41] = (byte)(-85);
         var10001[42] = (byte)(40);
         var10001[43] = (byte)(115);
         var10001[44] = (byte)(13);
         var10001[45] = (byte)(5);
         var10001[46] = (byte)(17);
         var10001[47] = (byte)(15);
         var10001[48] = (byte)(111);
         var10001[49] = (byte)(-104);
         var10001[50] = (byte)(22);
         var10001[51] = (byte)(-18);
         var10001[52] = (byte)(126);
         var10001[53] = (byte)(117);
         VieClientMod.LOGGER.error($$v_s(var10001, -8239117074988543979L), tag, GL20.glGetShaderInfoLog(var3));
         GL20.glDeleteShader(var3);
         return 0;
      } else {
         return var3;
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
