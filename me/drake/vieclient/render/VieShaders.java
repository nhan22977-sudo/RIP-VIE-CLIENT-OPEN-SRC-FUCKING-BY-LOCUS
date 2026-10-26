package me.drake.vieclient.render;

import com.mojang.blaze3d.shaders.ShaderType;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.class_2960;
import org.apache.commons.io.IOUtils;

public final class VieShaders {
   private static final Map CACHE = new ConcurrentHashMap();
   private static final String EDGE_SPREAD_FSH = "#version 330\n\nuniform sampler2D InSampler;\n\nlayout(std140) uniform EdgeSpreadConfig {\n    vec2 Step;\n};\n\nin vec2 texCoord;\n\nout vec4 fragColor;\n\nconst float WEIGHTS[7] = float[](0.1553, 0.1443, 0.1155, 0.0798, 0.0475, 0.0244, 0.0109);\n\nvoid main() {\n    vec4 sum = texture(InSampler, texCoord) * WEIGHTS[0];\n    for (int i = 1; i < 7; i++) {\n        vec2 offset = Step * float(i);\n        sum += (texture(InSampler, texCoord + offset) + texture(InSampler, texCoord - offset)) * WEIGHTS[i];\n    }\n    fragColor = sum;\n}\n";
   private static final String EDGE_GLOW_FSH = "#version 330\n\nuniform sampler2D CaptureSampler;\nuniform sampler2D HaloSampler;\n\nlayout(std140) uniform EdgeGlowConfig {\n    vec4 Tint;      // rgb: tint color, a: aura strength\n    vec4 Sheen;     // rgb: sheen color, a: sheen strength\n    vec4 Shape;     // x: stroke/rim, y: reach, z: phase, w: fill\n};\n\nin vec2 texCoord;\n\nout vec4 fragColor;\n\nconst vec2 RAYS[8] = vec2[](\n    vec2(1.0, 0.0), vec2(0.7071, 0.7071), vec2(0.0, 1.0), vec2(-0.7071, 0.7071),\n    vec2(-1.0, 0.0), vec2(-0.7071, -0.7071), vec2(0.0, -1.0), vec2(0.7071, -0.7071)\n);\n\nvoid main() {\n    vec4 capture = texture(CaptureSampler, texCoord);\n    vec4 halo = texture(HaloSampler, texCoord);\n    if (capture.a <= 0.0 && halo.a <= 0.0) {\n        discard;\n    }\n\n    // Sheen moving diagonal glint wave\n    float phase = fract(dot(texCoord, vec2(0.48, -0.52)) - Shape.z);\n    float glint = (1.0 - smoothstep(0.0, 0.065, abs(phase - 0.5))) * Sheen.a;\n    vec3 tint = mix(Tint.rgb, Sheen.rgb, glint);\n\n    // Soft expanded halo aura\n    float auraAlpha = Tint.a * (1.0 - exp(-halo.a * 3.5));\n    vec4 aura = vec4(tint * auraAlpha, auraAlpha * 0.65);\n\n    vec3 body = capture.rgb;\n    if (capture.a > 0.0) {\n        if (Shape.w > 0.0) {\n            body = mix(body, tint * capture.a, clamp(Shape.w, 0.0, 1.0));\n        }\n        if (Shape.x > 0.0) {\n            vec2 reach = vec2(Shape.y) / vec2(textureSize(CaptureSampler, 0));\n            float open = 0.0;\n            for (int i = 0; i < 8; i++) {\n                open += 1.0 - texture(CaptureSampler, texCoord + RAYS[i] * reach).a;\n            }\n            float rim = smoothstep(0.0, 0.45, open * 0.125) * Shape.x;\n            body = mix(body, tint * capture.a, rim * 0.75) + tint * capture.a * rim * 0.3;\n        }\n    }\n\n    // Blend: body inside + aura glow outside\n    fragColor = vec4(body, capture.a) + aura * (1.0 - capture.a);\n}\n";
   private static final String COLOR_CORRECTION_FSH = "#version 330\n\nuniform sampler2D InSampler;\n\nlayout(std140) uniform ColorGradingConfig {\n    float Vibrance;\n    float Contrast;\n    float Exposure;\n    float Warmth;\n    float Vignette;\n};\n\nin vec2 texCoord;\n\nout vec4 fragColor;\n\nvoid main() {\n    vec3 color = texture(InSampler, texCoord).rgb * Exposure;\n    color *= vec3(1.0 + 0.08 * Warmth, 1.0 + 0.02 * Warmth, 1.0 - 0.08 * Warmth);\n\n    float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));\n    float saturation = max(color.r, max(color.g, color.b)) - min(color.r, min(color.g, color.b));\n    color = mix(vec3(luma), color, 1.0 + Vibrance * (1.0 - saturation));\n\n    color = (color - 0.5) * Contrast + 0.5;\n\n    vec2 fromCentre = (texCoord - 0.5) * vec2(1.25, 1.0);\n    color *= 1.0 - Vignette * smoothstep(0.35, 0.95, length(fromCentre) * 1.35);\n\n    fragColor = vec4(clamp(color, 0.0, 1.0), 1.0);\n}\n";
   private static final String MOTION_BLUR_FSH = "#version 330\n\nuniform sampler2D InSampler;\n\nlayout(std140) uniform MotionBlurConfig {\n    float Retention;\n    float Seed;\n};\n\nin vec2 texCoord;\n\nout vec4 fragColor;\n\nvoid main() {\n    float noise = fract(52.9829189 * fract(dot(gl_FragCoord.xy + Seed, vec2(0.06711056, 0.00583715))));\n    vec3 history = texture(InSampler, texCoord).rgb + (noise - 0.5) / (255.0 * Retention);\n    fragColor = vec4(history, Retention);\n}\n";
   private static final String POS_COLOR_VERT = "#version 330 core\n\nlayout (location = 0) in vec4 pos;\nlayout (location = 1) in vec4 color;\n\nlayout (std140) uniform MeshData {\n    mat4 u_Proj;\n    mat4 u_ModelView;\n};\n\nout vec4 v_Color;\n\nvoid main() {\n    gl_Position = u_Proj * u_ModelView * pos;\n    v_Color = color;\n}\n";
   private static final String POS_COLOR_FRAG = "#version 330 core\n\nin vec4 v_Color;\nout vec4 color;\n\nvoid main() {\n    color = v_Color;\n}\n";
   private static final String HUD_COLOR_VERT = "#version 330 core\n\nlayout (location = 0) in vec4 pos;\nlayout (location = 1) in vec4 color;\n\nout vec4 v_Color;\n\nvoid main() {\n    gl_Position = vec4(pos.xyz, 1.0);\n    v_Color = color;\n}\n";
   private static final String HUD_COLOR_FRAG = "#version 330 core\n\nin vec4 v_Color;\nout vec4 color;\n\nvoid main() {\n    color = v_Color;\n}\n";
   private static final String HUD_TEX_VERT = "#version 330 core\n\nlayout (location = 0) in vec4 pos;\nlayout (location = 1) in vec2 uv;\nlayout (location = 2) in vec4 color;\n\nout vec2 v_Uv;\nout vec4 v_Color;\n\nvoid main() {\n    gl_Position = vec4(pos.xyz, 1.0);\n    v_Uv = uv;\n    v_Color = color;\n}\n";
   private static final String HUD_TEX_FRAG = "#version 330 core\n\nuniform sampler2D Sampler0;\n\nin vec2 v_Uv;\nin vec4 v_Color;\nout vec4 color;\n\nvoid main() {\n    color = texture(Sampler0, v_Uv) * v_Color;\n}\n";
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private VieShaders() {
   }

   public static String getShaderSource(class_2960 id, ShaderType type) {
      if (id == null) {
         return null;
      }

      String var2 = (String)CACHE.get(id);
      if (var2 != null) {
         return var2;
      }

      String var3 = id.method_12832();
      String[] var4;
      if (!var3.endsWith(".fsh")
         && !var3.endsWith(".vsh")
         && !var3.endsWith(".frag")
         && !var3.endsWith(".vert")) {
         String var5 = type == ShaderType.VERTEX
            ? ".vsh"
            : ".fsh";
         String var6 = type == ShaderType.VERTEX
            ? ".vert"
            : ".frag";
         var4 = new String[]{
            "assets/" + id.method_12836() + "/shaders/" + var3 + var5,
            "assets/" + id.method_12836() + "/shaders/" + var3 + var6,
            "assets/" + id.method_12836() + "/" + var3 + var5,
            "assets/" + id.method_12836() + "/" + var3 + var6,
            var3 + var5,
            var3 + var6
         };
      } else {
         var4 = new String[]{"assets/" + id.method_12836() + "/" + var3, "assets/" + id.method_12836() + "/shaders/" + var3, var3};
      }

      ClassLoader var16 = VieShaders.class.getClassLoader();

      for (String var9 : var4) {
         try (InputStream var10 = var16.getResourceAsStream(var9)) {
            if (var10 != null) {
               String var11 = IOUtils.toString(var10, StandardCharsets.UTF_8);
               if (var11 != null && !var11.isEmpty()) {
                  CACHE.put(id, var11);
                  return var11;
               }
            }
         } catch (Throwable var15) {
         }
      }

      String var18 = getFallback(var3, type);
      if (var18 != null) {
         CACHE.put(id, var18);
         return var18;
      } else {
         return null;
      }
   }

   private static String getFallback(String path, ShaderType type) {
      String var2 = path;
      if (var2.startsWith("shaders/")) {
         var2 = var2.substring("shaders/".length());
      }
      return switch (var2) {
         case "core/edge_spread", "core/edge_spread.fsh" -> "#version 330

uniform sampler2D InSampler;

layout(std140) uniform EdgeSpreadConfig {
    vec2 Step;
};

in vec2 texCoord;

out vec4 fragColor;

const float WEIGHTS[7] = float[](0.1553, 0.1443, 0.1155, 0.0798, 0.0475, 0.0244, 0.0109);

void main() {
    vec4 sum = texture(InSampler, texCoord) * WEIGHTS[0];
    for (int i = 1; i < 7; i++) {
        vec2 offset = Step * float(i);
        sum += (texture(InSampler, texCoord + offset) + texture(InSampler, texCoord - offset)) * WEIGHTS[i];
    }
    fragColor = sum;
}
";
         case "core/edge_glow", "core/edge_glow.fsh" -> "#version 330

uniform sampler2D CaptureSampler;
uniform sampler2D HaloSampler;

layout(std140) uniform EdgeGlowConfig {
    vec4 Tint;      // rgb: tint color, a: aura strength
    vec4 Sheen;     // rgb: sheen color, a: sheen strength
    vec4 Shape;     // x: stroke/rim, y: reach, z: phase, w: fill
};

in vec2 texCoord;

out vec4 fragColor;

const vec2 RAYS[8] = vec2[](
    vec2(1.0, 0.0), vec2(0.7071, 0.7071), vec2(0.0, 1.0), vec2(-0.7071, 0.7071),
    vec2(-1.0, 0.0), vec2(-0.7071, -0.7071), vec2(0.0, -1.0), vec2(0.7071, -0.7071)
);

void main() {
    vec4 capture = texture(CaptureSampler, texCoord);
    vec4 halo = texture(HaloSampler, texCoord);
    if (capture.a <= 0.0 && halo.a <= 0.0) {
        discard;
    }

    // Sheen moving diagonal glint wave
    float phase = fract(dot(texCoord, vec2(0.48, -0.52)) - Shape.z);
    float glint = (1.0 - smoothstep(0.0, 0.065, abs(phase - 0.5))) * Sheen.a;
    vec3 tint = mix(Tint.rgb, Sheen.rgb, glint);

    // Soft expanded halo aura
    float auraAlpha = Tint.a * (1.0 - exp(-halo.a * 3.5));
    vec4 aura = vec4(tint * auraAlpha, auraAlpha * 0.65);

    vec3 body = capture.rgb;
    if (capture.a > 0.0) {
        if (Shape.w > 0.0) {
            body = mix(body, tint * capture.a, clamp(Shape.w, 0.0, 1.0));
        }
        if (Shape.x > 0.0) {
            vec2 reach = vec2(Shape.y) / vec2(textureSize(CaptureSampler, 0));
            float open = 0.0;
            for (int i = 0; i < 8; i++) {
                open += 1.0 - texture(CaptureSampler, texCoord + RAYS[i] * reach).a;
            }
            float rim = smoothstep(0.0, 0.45, open * 0.125) * Shape.x;
            body = mix(body, tint * capture.a, rim * 0.75) + tint * capture.a * rim * 0.3;
        }
    }

    // Blend: body inside + aura glow outside
    fragColor = vec4(body, capture.a) + aura * (1.0 - capture.a);
}
";
         case "core/color_correction", "core/color_correction.fsh" -> "#version 330

uniform sampler2D InSampler;

layout(std140) uniform ColorGradingConfig {
    float Vibrance;
    float Contrast;
    float Exposure;
    float Warmth;
    float Vignette;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec3 color = texture(InSampler, texCoord).rgb * Exposure;
    color *= vec3(1.0 + 0.08 * Warmth, 1.0 + 0.02 * Warmth, 1.0 - 0.08 * Warmth);

    float luma = dot(color, vec3(0.2126, 0.7152, 0.0722));
    float saturation = max(color.r, max(color.g, color.b)) - min(color.r, min(color.g, color.b));
    color = mix(vec3(luma), color, 1.0 + Vibrance * (1.0 - saturation));

    color = (color - 0.5) * Contrast + 0.5;

    vec2 fromCentre = (texCoord - 0.5) * vec2(1.25, 1.0);
    color *= 1.0 - Vignette * smoothstep(0.35, 0.95, length(fromCentre) * 1.35);

    fragColor = vec4(clamp(color, 0.0, 1.0), 1.0);
}
";
         case "core/motion_blur", "core/motion_blur.fsh" -> "#version 330

uniform sampler2D InSampler;

layout(std140) uniform MotionBlurConfig {
    float Retention;
    float Seed;
};

in vec2 texCoord;

out vec4 fragColor;

void main() {
    float noise = fract(52.9829189 * fract(dot(gl_FragCoord.xy + Seed, vec2(0.06711056, 0.00583715))));
    vec3 history = texture(InSampler, texCoord).rgb + (noise - 0.5) / (255.0 * Retention);
    fragColor = vec4(history, Retention);
}
";
         case "pos_color.vert" -> "#version 330 core

layout (location = 0) in vec4 pos;
layout (location = 1) in vec4 color;

layout (std140) uniform MeshData {
    mat4 u_Proj;
    mat4 u_ModelView;
};

out vec4 v_Color;

void main() {
    gl_Position = u_Proj * u_ModelView * pos;
    v_Color = color;
}
";
         case "pos_color.frag" -> "#version 330 core

in vec4 v_Color;
out vec4 color;

void main() {
    color = v_Color;
}
";
         case "hud_color.vert" -> "#version 330 core

layout (location = 0) in vec4 pos;
layout (location = 1) in vec4 color;

out vec4 v_Color;

void main() {
    gl_Position = vec4(pos.xyz, 1.0);
    v_Color = color;
}
";
         case "hud_color.frag" -> "#version 330 core

in vec4 v_Color;
out vec4 color;

void main() {
    color = v_Color;
}
";
         case "hud_tex.vert" -> "#version 330 core

layout (location = 0) in vec4 pos;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec4 color;

out vec2 v_Uv;
out vec4 v_Color;

void main() {
    gl_Position = vec4(pos.xyz, 1.0);
    v_Uv = uv;
    v_Color = color;
}
";
         case "hud_tex.frag" -> "#version 330 core

uniform sampler2D Sampler0;

in vec2 v_Uv;
in vec4 v_Color;
out vec4 color;

void main() {
    color = texture(Sampler0, v_Uv) * v_Color;
}
";
         default -> null;
      };
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
