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

public final class EdgeGlowPass {
   public static final RenderPipeline PIPELINE_GLOW = RenderPipeline.builder(new Snippet[0])
      .withLocation(
         class_2960.method_60655(
            "vie_client",
            "pipeline/edge_glow"
         )
      )
      .withVertexShader("core/screenquad")
      .withFragmentShader(
         class_2960.method_60655(
            "vie_client",
            "core/edge_glow"
         )
      )
      .withSampler("CaptureSampler")
      .withSampler("HaloSampler")
      .withUniform("EdgeGlowConfig", class_10789.field_60031)
      .withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA)
      .withColorWrite(true, false)
      .withDepthWrite(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withVertexFormat(class_290.field_1592, class_5596.field_27379)
      .build();
   public static final RenderPipeline PIPELINE_SPREAD = RenderPipeline.builder(new Snippet[0])
      .withLocation(
         class_2960.method_60655(
            "vie_client",
            "pipeline/edge_spread"
         )
      )
      .withVertexShader("core/screenquad")
      .withFragmentShader(
         class_2960.method_60655(
            "vie_client",
            "core/edge_spread"
         )
      )
      .withSampler("InSampler")
      .withUniform("EdgeSpreadConfig", class_10789.field_60031)
      .withoutBlend()
      .withDepthWrite(false)
      .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
      .withVertexFormat(class_290.field_1592, class_5596.field_27379)
      .build();
   private static final Supplier PASS_GLOW_LABEL = () -> {
      byte[] var10000 = new byte[21];
      var10000[0] = (byte)(115);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(-36);
      var10000[5] = (byte)(-12);
      var10000[6] = (byte)(91);
      var10000[7] = 1;
      var10000[8] = (byte)(109);
      var10000[9] = (byte)(-27);
      var10000[10] = (byte)(-120);
      var10000[11] = (byte)(-103);
      var10000[12] = (byte)(-74);
      var10000[13] = (byte)(104);
      var10000[14] = (byte)(87);
      var10000[15] = (byte)(115);
      var10000[16] = (byte)(24);
      var10000[17] = (byte)(58);
      var10000[18] = (byte)(-93);
      var10000[19] = (byte)(-35);
      var10000[20] = (byte)(76);
      return $$v_s(var10000, 5494195971010318336L);
   };
   private static final Supplier PASS_SPREAD_LABEL = () -> {
      byte[] var10000 = new byte[20];
      var10000[0] = (byte)(115);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)('\ude57' ^ '\ude2e');
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(-36);
      var10000[5] = (byte)(-12);
      var10000[6] = (byte)(91);
      var10000[7] = 1;
      var10000[8] = (byte)(109);
      var10000[9] = (byte)(-15);
      var10000[10] = (byte)(-108);
      var10000[11] = (byte)(-124);
      var10000[12] = (byte)(-92);
      var10000[13] = (byte)(41);
      var10000[14] = (byte)(112);
      var10000[15] = (byte)(60);
      var10000[16] = (byte)(55);
      var10000[17] = (byte)(38);
      var10000[18] = (byte)(-71);
      var10000[19] = (byte)(-36);
      return $$v_s(var10000, 5494195971010318336L);
   };
   private static final Supplier CONFIG_GLOW_LABEL = () -> {
      byte[] var10000 = new byte[20];
      var10000[0] = (byte)(115);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(-36);
      var10000[5] = (byte)(-12);
      var10000[6] = (byte)(91);
      var10000[7] = 1;
      var10000[8] = (byte)(109);
      var10000[9] = (byte)(-27);
      var10000[10] = (byte)(-120);
      var10000[11] = (byte)(-103);
      var10000[12] = (byte)(-74);
      var10000[13] = (byte)(104);
      var10000[14] = (byte)(87);
      var10000[15] = (byte)(115);
      var10000[16] = (byte)(27);
      var10000[17] = (byte)(44);
      var10000[18] = (byte)(-91);
      var10000[19] = (byte)(-55);
      return $$v_s(var10000, 5494195971010318336L);
   };
   private static final Supplier CONFIG_SPREAD_LABEL = () -> {
      byte[] var10000 = new byte[22];
      var10000[0] = (byte)(115);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(-36);
      var10000[5] = (byte)(-12);
      var10000[6] = (byte)(91);
      var10000[7] = 1;
      var10000[8] = (byte)(109);
      var10000[9] = (byte)(-15);
      var10000[10] = (byte)(-108);
      var10000[11] = (byte)(-124);
      var10000[12] = (byte)(-92);
      var10000[13] = (byte)(41);
      var10000[14] = (byte)(112);
      var10000[15] = (byte)(60);
      var10000[16] = (byte)(54);
      var10000[17] = (byte)(37);
      var10000[18] = (byte)(-94);
      var10000[19] = (byte)(-56);
      var10000[20] = (byte)(64);
      var10000[21] = (byte)(7);
      return $$v_s(var10000, 5494195971010318336L);
   };
   private static class_6367 captureBuffer;
   private static class_6367 blurAcrossBuffer;
   private static class_6367 blurDownBuffer;
   private static GpuBuffer glowConfigBuffer;
   private static GpuBufferSlice glowConfigSlice;
   private static ByteBuffer glowConfigData;
   private static GpuBuffer spreadConfigBuffer;
   private static GpuBufferSlice spreadConfigSlice;
   private static ByteBuffer spreadConfigData;
   private static boolean capturing = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private EdgeGlowPass() {
   }

   public static boolean beginCapture() {
      return beginCapture(false);
   }

   public static boolean beginCapture(boolean copyDepth) {
      if (!capturing && RenderSystem.outputColorTextureOverride == null) {
         class_310 var1 = class_310.method_1551();
         if (var1 != null && var1.field_1687 != null) {
            class_276 var2 = var1.method_1522();
            if (var2 != null && var2.method_30277() != null && var2.field_1482 > 0 && var2.field_1481 > 0) {
               int var3 = var2.field_1482;
               int var4 = var2.field_1481;
               GpuDevice var5 = RenderSystem.getDevice();
               if (var5 == null) {
                  return false;
               }

               if (var5.precompilePipeline(PIPELINE_GLOW).isValid() && var5.precompilePipeline(PIPELINE_SPREAD).isValid()) {
                  ensureBuffers(var3, var4);
                  CommandEncoder var6 = var5.createCommandEncoder();
                  if (copyDepth && var2.method_30278() != null && captureBuffer.method_30278() != null) {
                     captureBuffer.method_29329(var2);
                     var6.clearColorTexture(captureBuffer.method_30277(), 0);
                  } else {
                     var6.clearColorAndDepthTextures(captureBuffer.method_30277(), 0, captureBuffer.method_30278(), 1.0);
                  }

                  RenderSystem.outputColorTextureOverride = captureBuffer.method_71639();
                  RenderSystem.outputDepthTextureOverride = captureBuffer.method_71640();
                  capturing = true;
                  return true;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean isCapturing() {
      return capturing;
   }

   public static boolean endCapture() {
      if (!capturing) {
         return false;
      }

      capturing = false;
      RenderSystem.outputColorTextureOverride = null;
      RenderSystem.outputDepthTextureOverride = null;
      return true;
   }

   public static void compose(int tintArgb, float aura, float reachPx, float rim, float rimPx, int sheenArgb, float sheen, float phase) {
      compose(tintArgb, aura, reachPx, rim, rimPx, sheenArgb, sheen, phase, 0.0F);
   }

   public static void compose(int tintArgb, float aura, float reachPx, float rim, float rimPx, int sheenArgb, float sheen, float phase, float fill) {
      class_310 var9 = class_310.method_1551();
      if (var9 != null && captureBuffer != null) {
         class_276 var10 = var9.method_1522();
         if (var10 != null && var10.method_30277() != null) {
            int var11 = captureBuffer.field_1482;
            int var12 = captureBuffer.field_1481;

            try {
               CommandEncoder var13 = RenderSystem.getDevice().createCommandEncoder();
               float var14 = Math.max(0.75F, reachPx / 6.0F);
               spreadConfigData.putFloat(0, var14 / var11);
               spreadConfigData.putFloat(4, 0.0F);
               var13.writeToBuffer(spreadConfigSlice, spreadConfigData);
               RenderPass var15 = var13.createRenderPass(PASS_SPREAD_LABEL, blurAcrossBuffer.method_71639(), OptionalInt.empty());

               try {
                  var15.setPipeline(PIPELINE_SPREAD);
                  var15.setUniform(
                     "EdgeSpreadConfig", spreadConfigSlice
                  );
                  var15.bindTexture(
                     "InSampler",
                     captureBuffer.method_71639(),
                     RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR)
                  );
                  var15.draw(0, 3);
               } catch (Throwable var23) {
                  if (var15 != null) {
                     try {
                        var15.close();
                     } catch (Throwable var20) {
                        var23.addSuppressed(var20);
                     }
                  }

                  throw var23;
               }

               if (var15 != null) {
                  var15.close();
               }

               spreadConfigData.putFloat(0, 0.0F);
               spreadConfigData.putFloat(4, var14 / var12);
               var13.writeToBuffer(spreadConfigSlice, spreadConfigData);
               var15 = var13.createRenderPass(PASS_SPREAD_LABEL, blurDownBuffer.method_71639(), OptionalInt.empty());

               try {
                  var15.setPipeline(PIPELINE_SPREAD);
                  var15.setUniform(
                     "EdgeSpreadConfig", spreadConfigSlice
                  );
                  var15.bindTexture(
                     "InSampler",
                     blurAcrossBuffer.method_71639(),
                     RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR)
                  );
                  var15.draw(0, 3);
               } catch (Throwable var22) {
                  if (var15 != null) {
                     try {
                        var15.close();
                     } catch (Throwable var19) {
                        var22.addSuppressed(var19);
                     }
                  }

                  throw var22;
               }

               if (var15 != null) {
                  var15.close();
               }

               putColor(glowConfigData, 0, tintArgb, aura);
               putColor(glowConfigData, 16, sheenArgb, sheen);
               glowConfigData.putFloat(32, rim);
               glowConfigData.putFloat(36, rimPx);
               glowConfigData.putFloat(40, phase);
               glowConfigData.putFloat(44, fill);
               var13.writeToBuffer(glowConfigSlice, glowConfigData);
               var15 = var13.createRenderPass(PASS_GLOW_LABEL, var10.method_71639(), OptionalInt.empty());

               try {
                  var15.setPipeline(PIPELINE_GLOW);
                  var15.setUniform("EdgeGlowConfig", glowConfigSlice);
                  var15.bindTexture(
                     "CaptureSampler",
                     captureBuffer.method_71639(),
                     RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR)
                  );
                  var15.bindTexture(
                     "HaloSampler",
                     blurDownBuffer.method_71639(),
                     RenderSystem.getSamplerCache().method_75294(FilterMode.LINEAR)
                  );
                  var15.draw(0, 3);
               } catch (Throwable var21) {
                  if (var15 != null) {
                     try {
                        var15.close();
                     } catch (Throwable var18) {
                        var21.addSuppressed(var18);
                     }
                  }

                  throw var21;
               }

               if (var15 != null) {
                  var15.close();
               }
            } catch (Throwable var24) {
               VieClientMod.LOGGER
                  .error(
                     "[VieClient] Failed to composite Edge Glow pass",
                     var24
                  );
            }
         }
      }
   }

   private static void putColor(ByteBuffer buf, int offset, int argb, float a) {
      buf.putFloat(offset, (argb >> (16) & (255)) / 255.0F);
      buf.putFloat(offset + (4), (argb >> (8) & (255)) / 255.0F);
      buf.putFloat(offset + (8), (argb & (255)) / 255.0F);
      buf.putFloat(offset + (12), a);
   }

   private static void ensureBuffers(int w, int h) {
      if (captureBuffer == null) {
         captureBuffer = new class_6367(
            "Vie Edge Glow Capture",
            w,
            h,
            true
         );
         blurAcrossBuffer = new class_6367(
            "Vie Edge Spread Across",
            Math.max(1, w / 2),
            Math.max(1, h / 2),
            false
         );
         blurDownBuffer = new class_6367(
            "Vie Edge Spread Down",
            Math.max(1, w / 2),
            Math.max(1, h / 2),
            false
         );
         GpuDevice var2 = RenderSystem.getDevice();
         glowConfigBuffer = var2.createBuffer(CONFIG_GLOW_LABEL, 136, 48L);
         glowConfigSlice = glowConfigBuffer.slice();
         glowConfigData = BufferUtils.createByteBuffer(48);
         spreadConfigBuffer = var2.createBuffer(CONFIG_SPREAD_LABEL, 136, 16L);
         spreadConfigSlice = spreadConfigBuffer.slice();
         spreadConfigData = BufferUtils.createByteBuffer(16);
      } else if (captureBuffer.field_1482 != w || captureBuffer.field_1481 != h) {
         captureBuffer.method_1234(w, h);
         blurAcrossBuffer.method_1234(Math.max(1, w / 2), Math.max(1, h / 2));
         blurDownBuffer.method_1234(Math.max(1, w / 2), Math.max(1, h / 2));
      }
   }

   public static void release() {
      if (capturing) {
         endCapture();
      }

      if (captureBuffer != null) {
         captureBuffer.method_1238();
         captureBuffer = null;
      }

      if (blurAcrossBuffer != null) {
         blurAcrossBuffer.method_1238();
         blurAcrossBuffer = null;
      }

      if (blurDownBuffer != null) {
         blurDownBuffer.method_1238();
         blurDownBuffer = null;
      }

      if (glowConfigBuffer != null) {
         glowConfigBuffer.close();
         glowConfigBuffer = null;
      }

      if (spreadConfigBuffer != null) {
         spreadConfigBuffer.close();
         spreadConfigBuffer = null;
      }

      glowConfigSlice = null;
      glowConfigData = null;
      spreadConfigSlice = null;
      spreadConfigData = null;
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
