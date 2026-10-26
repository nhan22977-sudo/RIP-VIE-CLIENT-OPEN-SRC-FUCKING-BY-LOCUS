package me.drake.vieclient.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.ShaderType;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.render.post.ColorCorrectionPass;
import me.drake.vieclient.render.post.EdgeGlowPass;
import me.drake.vieclient.render.post.MotionBlurPass;
import net.minecraft.class_10789;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import org.apache.commons.io.IOUtils;

public abstract class ViePipelines {
   private static final List PIPELINES = new ArrayList();
   public static volatile boolean isPrecompiled = false;
   private static final String FALLBACK_POS_COLOR_VERT = "#version 330 core\n\nlayout (location = 0) in vec4 pos;\nlayout (location = 1) in vec4 color;\n\nlayout (std140) uniform MeshData {\n    mat4 u_Proj;\n    mat4 u_ModelView;\n};\n\nout vec4 v_Color;\n\nvoid main() {\n    gl_Position = u_Proj * u_ModelView * pos;\n    v_Color = color;\n}\n";
   private static final String FALLBACK_POS_COLOR_FRAG = "#version 330 core\n\nin vec4 v_Color;\nout vec4 color;\n\nvoid main() {\n    color = v_Color;\n}\n";
   private static final String FALLBACK_HUD_COLOR_VERT = "#version 330 core\n\nlayout (location = 0) in vec4 pos;\nlayout (location = 1) in vec4 color;\n\nout vec4 v_Color;\n\nvoid main() {\n    gl_Position = vec4(pos.xyz, 1.0);\n    v_Color = color;\n}\n";
   private static final String FALLBACK_HUD_COLOR_FRAG = "#version 330 core\n\nin vec4 v_Color;\nout vec4 color;\n\nvoid main() {\n    color = v_Color;\n}\n";
   private static final String FALLBACK_HUD_TEX_VERT = "#version 330 core\n\nlayout (location = 0) in vec4 pos;\nlayout (location = 1) in vec2 uv;\nlayout (location = 2) in vec4 color;\n\nout vec2 v_Uv;\nout vec4 v_Color;\n\nvoid main() {\n    gl_Position = pos;\n    v_Uv = uv;\n    v_Color = color;\n}\n";
   private static final String FALLBACK_HUD_TEX_FRAG = "#version 330 core\n\nuniform sampler2D Sampler0;\n\nin vec2 v_Uv;\nin vec4 v_Color;\nout vec4 color;\n\nvoid main() {\n    vec4 texColor = texture(Sampler0, v_Uv);\n    float outAlpha = texColor.a * v_Color.a;\n    if (outAlpha <= 0.001) discard;\n    color = vec4(texColor.rgb * v_Color.rgb, outAlpha);\n}\n";
   private static final Snippet MESH_UNIFORMS = RenderPipeline.builder(new Snippet[0])
      .withUniform("MeshData", class_10789.field_60031)
      .buildSnippet();
   public static final RenderPipeline WORLD_TRIANGLES = add(
      RenderPipeline.builder(new Snippet[]{MESH_UNIFORMS})
         .withLocation(
            class_2960.method_60655(
               "vie_client",
               "pipeline/world_triangles"
            )
         )
         .withVertexFormat(class_290.field_1576, class_5596.field_27379)
         .withVertexShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.vert"
            )
         )
         .withFragmentShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.frag"
            )
         )
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .build()
   );
   public static final RenderPipeline WORLD_TRIANGLES_ADD = add(
      RenderPipeline.builder(new Snippet[]{MESH_UNIFORMS})
         .withLocation(
            class_2960.method_60655(
               "vie_client",
               "pipeline/world_triangles_add"
            )
         )
         .withVertexFormat(class_290.field_1576, class_5596.field_27379)
         .withVertexShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.vert"
            )
         )
         .withFragmentShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.frag"
            )
         )
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.ADDITIVE)
         .withCull(false)
         .build()
   );
   public static final RenderPipeline WORLD_LINES = add(
      RenderPipeline.builder(new Snippet[]{MESH_UNIFORMS})
         .withLocation(
            class_2960.method_60655(
               "vie_client",
               "pipeline/world_lines"
            )
         )
         .withVertexFormat(class_290.field_1576, class_5596.field_29344)
         .withVertexShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.vert"
            )
         )
         .withFragmentShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.frag"
            )
         )
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .build()
   );
   public static final RenderPipeline WORLD_TRIANGLES_DEPTH = add(
      RenderPipeline.builder(new Snippet[]{MESH_UNIFORMS})
         .withLocation(
            class_2960.method_60655(
               "vie_client",
               "pipeline/world_triangles_depth"
            )
         )
         .withVertexFormat(class_290.field_1576, class_5596.field_27379)
         .withVertexShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.vert"
            )
         )
         .withFragmentShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.frag"
            )
         )
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .build()
   );
   public static final RenderPipeline WORLD_LINES_DEPTH = add(
      RenderPipeline.builder(new Snippet[]{MESH_UNIFORMS})
         .withLocation(
            class_2960.method_60655(
               "vie_client",
               "pipeline/world_lines_depth"
            )
         )
         .withVertexFormat(class_290.field_1576, class_5596.field_29344)
         .withVertexShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.vert"
            )
         )
         .withFragmentShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.frag"
            )
         )
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .build()
   );
   public static final RenderPipeline WORLD_TRIANGLES_ADD_DEPTH = add(
      RenderPipeline.builder(new Snippet[]{MESH_UNIFORMS})
         .withLocation(
            class_2960.method_60655(
               "vie_client",
               "pipeline/world_triangles_add_depth"
            )
         )
         .withVertexFormat(class_290.field_1576, class_5596.field_27379)
         .withVertexShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.vert"
            )
         )
         .withFragmentShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/pos_color.frag"
            )
         )
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.ADDITIVE)
         .withCull(false)
         .build()
   );
   public static final RenderPipeline HUD_COLOR = add(
      RenderPipeline.builder(new Snippet[0])
         .withLocation(
            class_2960.method_60655(
               "vie_client",
               "pipeline/hud_color"
            )
         )
         .withVertexFormat(class_290.field_1576, class_5596.field_27379)
         .withVertexShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/hud_color.vert"
            )
         )
         .withFragmentShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/hud_color.frag"
            )
         )
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .build()
   );
   public static final RenderPipeline HUD_TEX = add(
      RenderPipeline.builder(new Snippet[0])
         .withLocation(
            class_2960.method_60655(
               "vie_client",
               "pipeline/hud_tex"
            )
         )
         .withVertexFormat(class_290.field_1575, class_5596.field_27379)
         .withVertexShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/hud_tex.vert"
            )
         )
         .withFragmentShader(
            class_2960.method_60655(
               "vie_client",
               "shaders/hud_tex.frag"
            )
         )
         .withSampler("Sampler0")
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .build()
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private static RenderPipeline add(RenderPipeline pipeline) {
      PIPELINES.add(pipeline);
      return pipeline;
   }

   public static void precompile() {
      class_310 var0 = class_310.method_1551();
      precompile(var0 != null ? var0.method_1478() : null);
   }

   public static void precompile(class_3300 resources) {
      if (!isPrecompiled) {
         try {
            GpuDevice var1 = RenderSystem.getDevice();
            if (var1 == null) {
               return;
            }

            if (resources == null) {
               class_310 var2 = class_310.method_1551();
               if (var2 != null) {
                  resources = var2.method_1478();
               }
            }

            HashMap var8 = new HashMap();
            ArrayList var3 = new ArrayList(PIPELINES);
            var3.add(EdgeGlowPass.PIPELINE_SPREAD);
            var3.add(EdgeGlowPass.PIPELINE_GLOW);
            var3.add(ColorCorrectionPass.PIPELINE);
            var3.add(MotionBlurPass.PIPELINE);

            for (RenderPipeline var5 : var3) {
               VieClientMod.LOGGER
                  .info(
                     "[VieClient] Precompiling pipeline: {}",
                     var5.getLocation()
                  );
               class_3300 var6 = resources;
               var1.precompilePipeline(var5, (identifier, shaderType) -> var8.computeIfAbsent(identifier, id -> loadShaderSource(var6, id, shaderType)));
            }

            isPrecompiled = true;
            VieClientMod.LOGGER
               .info(
                  "[VieClient] Successfully precompiled {} render pipelines",
                  var3.size()
               );
         } catch (Exception var7) {
            isPrecompiled = false;
            VieClientMod.LOGGER
               .error(
                  "[VieClient] Failed to precompile pipelines",
                  var7
               );
         }
      }
   }

   private static String loadShaderSource(class_3300 resources, class_2960 identifier, ShaderType shaderType) {
      if (resources != null) {
         try {
            Optional var3 = resources.method_14486(identifier);
            if (var3.isPresent()) {
               try (InputStream var12 = ((class_3298)var3.get()).method_14482()) {
                  return IOUtils.toString(var12, StandardCharsets.UTF_8);
               } catch (IOException var9) {
                  VieClientMod.LOGGER.warn("[VieClient] Failed to read shader: " + identifier, var9);
               }
            }
         } catch (Throwable var10) {
         }
      }

      String var11 = VieShaders.getShaderSource(identifier, shaderType);
      if (var11 != null && !var11.isEmpty()) {
         return var11;
      }

      String var4 = fallbackShaderSource(identifier);
      if (var4 != null) {
         return var4;
      }

      VieClientMod.LOGGER
         .error(
            "[VieClient] Shader not found and no fallback: {}",
            identifier
         );
      return "";
   }

   private static String fallbackShaderSource(class_2960 identifier) {
      String var1 = identifier.method_12832();

      return switch (var1) {
         case "shaders/pos_color.vert" -> "#version 330 core

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
         case "shaders/pos_color.frag" -> "#version 330 core

in vec4 v_Color;
out vec4 color;

void main() {
    color = v_Color;
}
";
         case "shaders/hud_color.vert" -> "#version 330 core

layout (location = 0) in vec4 pos;
layout (location = 1) in vec4 color;

out vec4 v_Color;

void main() {
    gl_Position = vec4(pos.xyz, 1.0);
    v_Color = color;
}
";
         case "shaders/hud_color.frag" -> "#version 330 core

in vec4 v_Color;
out vec4 color;

void main() {
    color = v_Color;
}
";
         case "shaders/hud_tex.vert" -> "#version 330 core

layout (location = 0) in vec4 pos;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec4 color;

out vec2 v_Uv;
out vec4 v_Color;

void main() {
    gl_Position = pos;
    v_Uv = uv;
    v_Color = color;
}
";
         case "shaders/hud_tex.frag" -> "#version 330 core

uniform sampler2D Sampler0;

in vec2 v_Uv;
in vec4 v_Color;
out vec4 color;

void main() {
    vec4 texColor = texture(Sampler0, v_Uv);
    float outAlpha = texColor.a * v_Color.a;
    if (outAlpha <= 0.001) discard;
    color = vec4(texColor.rgb * v_Color.rgb, outAlpha);
}
";
         default -> null;
      };
   }

   private ViePipelines() {
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
