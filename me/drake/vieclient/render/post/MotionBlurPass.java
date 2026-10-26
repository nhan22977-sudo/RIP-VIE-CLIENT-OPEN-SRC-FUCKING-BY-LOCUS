package me.drake.vieclient.render.post;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
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

public final class MotionBlurPass {
   public static final RenderPipeline PIPELINE = RenderPipeline.builder(new Snippet[0])
      .withLocation(
         class_2960.method_60655(
            "vie_client",
            "pipeline/motion_blur"
         )
      )
      .withVertexShader("core/screenquad")
      .withFragmentShader(
         class_2960.method_60655(
            "vie_client",
            "core/motion_blur"
         )
      )
      .withSampler("InSampler")
      .withUniform(
         "MotionBlurConfig", class_10789.field_60031
      )
      .withBlend(BlendFunction.TRANSLUCENT)
      .withColorWrite(true, false)
      .withDepthWrite(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withVertexFormat(class_290.field_1592, class_5596.field_27379)
      .build();
   private static final Supplier PASS_LABEL = () -> {
      byte[] var10000 = new byte[15];
      var10000[0] = (byte)(99);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(39);
      var10000[5] = (byte)(-15);
      var10000[6] = (byte)(-65);
      var10000[7] = (byte)(-67);
      var10000[8] = (byte)(50);
      var10000[9] = (byte)(-52);
      var10000[10] = (byte)(-60);
      var10000[11] = (byte)(-76);
      var10000[12] = (byte)(94);
      var10000[13] = (byte)(51);
      var10000[14] = (byte)(-111);
      return $$v_s(var10000, -231864104578199536L);
   };
   private static final Supplier CONFIG_LABEL = () -> {
      byte[] var10000 = new byte[22];
      var10000[0] = (byte)(99);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(39);
      var10000[5] = (byte)(-15);
      var10000[6] = (byte)(-65);
      var10000[7] = (byte)(-67);
      var10000[8] = (byte)(50);
      var10000[9] = (byte)(-52);
      var10000[10] = (byte)(-60);
      var10000[11] = (byte)(-76);
      var10000[12] = (byte)(94);
      var10000[13] = (byte)(51);
      var10000[14] = (byte)(-111);
      var10000[15] = (byte)(-116);
      var10000[16] = (byte)(38);
      var10000[17] = (byte)(37);
      var10000[18] = (byte)(-94);
      var10000[19] = (byte)(-56);
      var10000[20] = (byte)(-77);
      var10000[21] = (byte)(9);
      return $$v_s(var10000, -231864104578199536L);
   };
   private static class_6367 historyBuffer;
   private static GpuBuffer configBuffer;
   private static GpuBufferSlice configSlice;
   private static ByteBuffer configData;
   private static boolean primed = false;
   private static long lastFrameNanos = 0L;
   private static int worldKey = 0;
   private static int noiseSeed = 0;
   private static boolean failed = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private MotionBlurPass() {
   }

   public static void apply(boolean worldRendered, float retention60) {
      class_310 var2 = class_310.method_1551();
      if (var2 != null && var2.field_1687 != null && worldRendered) {
         class_276 var3 = var2.method_1522();
         if (var3 != null) {
            GpuTexture var4 = var3.method_30277();
            if (var4 != null && var3.field_1482 > 0 && var3.field_1481 > 0) {
               long var5 = System.nanoTime();
               long var7 = var5 - lastFrameNanos;
               lastFrameNanos = var5;
               int var9 = System.identityHashCode(var2.field_1687);
               if (var9 != worldKey) {
                  worldKey = var9;
                  primed = false;
               }

               if (historyBuffer == null) {
                  allocate(var3.field_1482, var3.field_1481);
                  primed = false;
               } else if (historyBuffer.field_1482 != var3.field_1482 || historyBuffer.field_1481 != var3.field_1481) {
                  historyBuffer.method_1234(var3.field_1482, var3.field_1481);
                  primed = false;
               }

               GpuDevice var10 = RenderSystem.getDevice();
               CommandEncoder var11 = var10.createCommandEncoder();
               if (primed && var7 > 0L && var7 < 1000000000L) {
                  float var12 = (float)Math.pow(retention60, var7 * 6.0E-8);
                  if (var12 >= 0.005F && shaderReady(var10)) {
                     blendHistory(var11, var3, var12);
                  }
               }

               var11.copyTextureToTexture(var4, historyBuffer.method_30277(), 0, 0, 0, 0, 0, var3.field_1482, var3.field_1481);
               primed = true;
            }
         }
      } else {
         primed = false;
      }
   }

   private static void blendHistory(CommandEncoder encoder, class_276 main, float retention) {
      noiseSeed = noiseSeed + 1 & 63;
      configData.putFloat(0, retention);
      configData.putFloat(4, noiseSeed * 7.0F);
      encoder.writeToBuffer(configSlice, configData);

      try {
         RenderPass var3 = encoder.createRenderPass(PASS_LABEL, main.method_71639(), OptionalInt.empty());

         try {
            var3.setPipeline(PIPELINE);
            var3.setUniform("MotionBlurConfig", configSlice);
            var3.bindTexture(
               "InSampler",
               historyBuffer.method_71639(),
               RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST)
            );
            var3.draw(0, 3);
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            var3.close();
         }
      } catch (Throwable var8) {
         VieClientMod.LOGGER
            .warn(
               "[VieClient] Motion Blur render pass failed: {}",
               var8.getMessage()
            );
      }
   }

   private static boolean shaderReady(GpuDevice device) {
      if (device.precompilePipeline(PIPELINE).isValid()) {
         failed = false;
         return true;
      }

      if (!failed) {
         failed = true;
         VieClientMod.LOGGER
            .warn(
               "[VieClient] Motion Blur shader pipeline compilation failed."
            );
      }

      return false;
   }

   private static void allocate(int width, int height) {
      byte[] var10002 = new byte[23];
      var10002[0] = (byte)(99);
      var10002[1] = (byte)(-13);
      var10002[2] = (byte)(121);
      var10002[3] = (byte)(-2);
      var10002[4] = (byte)(7);
      var10002[5] = (byte)(-15);
      var10002[6] = (byte)(-65);
      var10002[7] = (byte)(-67);
      var10002[8] = (byte)(50);
      var10002[9] = (byte)(-52);
      var10002[10] = (byte)(-60);
      var10002[11] = (byte)(-108);
      var10002[12] = (byte)(94);
      var10002[13] = (byte)(51);
      var10002[14] = (byte)(-111);
      var10002[15] = (byte)(-116);
      var10002[16] = (byte)(13);
      var10002[17] = (byte)(35);
      var10002[18] = (byte)(-65);
      var10002[19] = (byte)(-38);
      var10002[20] = (byte)(-75);
      var10002[21] = (byte)(28);
      var10002[22] = (byte)(-30);
      historyBuffer = new class_6367($$v_s(var10002, -231864104578199536L), width, height, false);
      configBuffer = RenderSystem.getDevice().createBuffer(CONFIG_LABEL, 136, 16L);
      configSlice = configBuffer.slice();
      configData = BufferUtils.createByteBuffer(16);
   }

   public static void release() {
      if (historyBuffer != null) {
         historyBuffer.method_1238();
         historyBuffer = null;
      }

      if (configBuffer != null) {
         configBuffer.close();
         configBuffer = null;
      }

      configSlice = null;
      configData = null;
      primed = false;
      lastFrameNanos = 0L;
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
