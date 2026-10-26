package me.drake.vieclient.render.post;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat.class_5596;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.OptionalInt;
import java.util.function.Supplier;
import me.drake.vieclient.VieClientMod;
import net.minecraft.class_10789;
import net.minecraft.class_276;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_6367;
import org.lwjgl.BufferUtils;

public final class ColorCorrectionPass {
   public static final RenderPipeline PIPELINE = RenderPipeline.builder(new Snippet[0])
      .withLocation(
         class_2960.method_60655(
            "vie_client",
            "pipeline/color_correction"
         )
      )
      .withVertexShader("core/screenquad")
      .withFragmentShader(
         class_2960.method_60655(
            "vie_client",
            "core/color_correction"
         )
      )
      .withSampler("InSampler")
      .withUniform(
         "ColorGradingConfig",
         class_10789.field_60031
      )
      .withoutBlend()
      .withColorWrite(true, false)
      .withDepthWrite(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withVertexFormat(class_290.field_1592, class_5596.field_27379)
      .build();
   private static final Supplier PASS_LABEL = () -> {
      byte[] var10000 = new byte[20];
      var10000[0] = (byte)(99);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(119);
      var10000[5] = (byte)(8);
      var10000[6] = (byte)(118);
      var10000[7] = (byte)(-121);
      var10000[8] = (byte)(47);
      var10000[9] = (byte)(-126);
      var10000[10] = (byte)(-89);
      var10000[11] = (byte)(-103);
      var10000[12] = (byte)(30);
      var10000[13] = (byte)(-51);
      var10000[14] = (byte)(87);
      var10000[15] = (byte)(-13);
      var10000[16] = (byte)(17);
      var10000[17] = (byte)(35);
      var10000[18] = (byte)(-93);
      var10000[19] = (byte)(-64);
      return $$v_s(var10000, -4604445127518990320L);
   };
   private static final Supplier CONFIG_LABEL = () -> {
      byte[] var10000 = new byte[27];
      var10000[0] = (byte)(99);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(119);
      var10000[5] = (byte)(8);
      var10000[6] = (byte)(118);
      var10000[7] = (byte)(-121);
      var10000[8] = (byte)(47);
      var10000[9] = (byte)(-126);
      var10000[10] = (byte)(-89);
      var10000[11] = (byte)(-103);
      var10000[12] = (byte)(30);
      var10000[13] = (byte)(-51);
      var10000[14] = (byte)(87);
      var10000[15] = (byte)(-13);
      var10000[16] = (byte)(17);
      var10000[17] = (byte)(35);
      var10000[18] = (byte)(-93);
      var10000[19] = (byte)(-64);
      var10000[20] = (byte)(-92);
      var10000[21] = (byte)(-44);
      var10000[22] = (byte)(37);
      var10000[23] = (byte)(-42);
      var10000[24] = (byte)(-21);
      var10000[25] = (byte)(123);
      var10000[26] = (byte)(-13);
      return $$v_s(var10000, -4604445127518990320L);
   };
   private static class_6367 copyBuffer;
   private static GpuBuffer configBuffer;
   private static GpuBufferSlice configSlice;
   private static ByteBuffer configData;
   private static boolean failed = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private ColorCorrectionPass() {
   }

   public static void apply(boolean worldRendered, float vibrance, float contrast, float exposure, float warmth, float vignette) {
      class_310 var6 = class_310.method_1551();
      if (var6 != null && var6.field_1687 != null && worldRendered) {
         class_276 var7 = var6.method_1522();
         if (var7 != null) {
            GpuTexture var8 = var7.method_30277();
            if (var8 != null && var7.field_1482 > 0 && var7.field_1481 > 0) {
               if (copyBuffer == null) {
                  allocate(var7.field_1482, var7.field_1481);
               } else if (copyBuffer.field_1482 != var7.field_1482 || copyBuffer.field_1481 != var7.field_1481) {
                  copyBuffer.method_1234(var7.field_1482, var7.field_1481);
               }

               GpuDevice var9 = RenderSystem.getDevice();
               if (!var9.precompilePipeline(PIPELINE).isValid()) {
                  if (!failed) {
                     failed = true;
                     VieClientMod.LOGGER
                        .warn(
                           "[VieClient] Color Correction shader pipeline compilation failed."
                        );
                  }
               } else {
                  CommandEncoder var10 = var9.createCommandEncoder();
                  var10.copyTextureToTexture(var8, copyBuffer.method_30277(), 0, 0, 0, 0, 0, var7.field_1482, var7.field_1481);
                  configData.putFloat(0, vibrance);
                  configData.putFloat(4, contrast);
                  configData.putFloat(8, exposure);
                  configData.putFloat(12, warmth);
                  configData.putFloat(16, vignette);
                  var10.writeToBuffer(configSlice, configData);

                  try {
                     RenderPass var11 = var10.createRenderPass(PASS_LABEL, var7.method_71639(), OptionalInt.empty());

                     try {
                        var11.setPipeline(PIPELINE);
                        var11.setUniform(
                           "ColorGradingConfig",
                           configSlice
                        );
                        var11.bindTexture(
                           "InSampler",
                           copyBuffer.method_71639(),
                           RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST)
                        );
                        var11.draw(0, 3);
                     } catch (Throwable var15) {
                        if (var11 != null) {
                           try {
                              var11.close();
                           } catch (Throwable var14) {
                              var15.addSuppressed(var14);
                           }
                        }

                        throw var15;
                     }

                     if (var11 != null) {
                        var11.close();
                     }
                  } catch (Throwable var16) {
                     VieClientMod.LOGGER
                        .warn(
                           "[VieClient] Color Correction render pass failed: {}",
                           var16.getMessage()
                        );
                  }
               }
            }
         }
      }
   }

   private static void allocate(int width, int height) {
      byte[] var10002 = new byte[25];
      var10002[0] = (byte)(99);
      var10002[1] = (byte)(-13);
      var10002[2] = (byte)(121);
      var10002[3] = (byte)(-2);
      var10002[4] = (byte)(87);
      var10002[5] = (byte)(8);
      var10002[6] = (byte)(118);
      var10002[7] = (byte)(-121);
      var10002[8] = (byte)(47);
      var10002[9] = (byte)(-126);
      var10002[10] = (byte)(-121);
      var10002[11] = (byte)(-103);
      var10002[12] = (byte)(30);
      var10002[13] = (byte)(-51);
      var10002[14] = (byte)(87);
      var10002[15] = (byte)(-13);
      var10002[16] = (byte)(17);
      var10002[17] = (byte)(35);
      var10002[18] = (byte)(-93);
      var10002[19] = (byte)(-64);
      var10002[20] = (byte)(-92);
      var10002[21] = (byte)(-12);
      var10002[22] = (byte)(37);
      var10002[23] = (byte)(-56);
      var10002[24] = (byte)(-12);
      copyBuffer = new class_6367($$v_s(var10002, -4604445127518990320L), width, height, false);
      configBuffer = RenderSystem.getDevice().createBuffer(CONFIG_LABEL, 136, 32L);
      configSlice = configBuffer.slice();
      configData = BufferUtils.createByteBuffer(32);
   }

   public static void release() {
      if (copyBuffer != null) {
         copyBuffer.method_1238();
         copyBuffer = null;
      }

      if (configBuffer != null) {
         configBuffer.close();
         configBuffer = null;
      }

      configSlice = null;
      configData = null;
      failed = false;
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
