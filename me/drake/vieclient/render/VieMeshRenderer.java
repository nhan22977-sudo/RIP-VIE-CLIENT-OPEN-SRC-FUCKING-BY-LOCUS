package me.drake.vieclient.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.class_5595;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.class_11280;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_11280.class_11281;
import org.joml.Matrix4f;

public class VieMeshRenderer {
   private static final int UBO_SIZE = new Std140SizeCalculator().putMat4f().putMat4f().get();
   private static final VieMeshRenderer.MeshData MESH_DATA = new VieMeshRenderer.MeshData();
   private static class_11280 uboStorage;
   private static final FrameMatrixCache UNIFORM_CACHE = new FrameMatrixCache();
   public static Matrix4f projection = new Matrix4f();
   public static Matrix4f lastProjectionMatrix = new Matrix4f();
   public static Matrix4f lastPositionMatrix = new Matrix4f();
   public static boolean hasCapturedWorldMatrices = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static void flipFrame() {
      UNIFORM_CACHE.clear();
      if (uboStorage != null) {
         uboStorage.method_71100();
      }
   }

   public static void invalidate() {
      UNIFORM_CACHE.clear();
      if (uboStorage != null) {
         uboStorage.method_71100();
      }

      hasCapturedWorldMatrices = false;
   }

   public static void render(RenderPipeline pipeline, VieMeshBuilder mesh, class_4587 matrices) {
      if (mesh.isBuilding()) {
         mesh.end();
      }

      int var3 = mesh.getIndicesCount();
      if (var3 > 0) {
         class_310 var4 = class_310.method_1551();
         class_276 var5 = var4.method_1522();
         RenderSystem.getModelViewStack().pushMatrix();

         try {
            if (matrices != null) {
               RenderSystem.getModelViewStack().mul(matrices.method_23760().method_23761());
            }

            VieMeshBuilder.GpuBufferPair var6 = mesh.getBuffers();
            GpuBufferSlice var7 = writeMeshData(projection, RenderSystem.getModelViewStack());
            RenderPass var8;
            if (pipeline.wantsDepthTexture()) {
               var8 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
                  byte[] var10000 = new byte[7];
                  var10000[0] = (byte)(105);
                  var10000[1] = (byte)(-13);
                  var10000[2] = (byte)(121);
                  var10000[3] = (byte)(-2);
                  var10000[4] = (byte)(-76);
                  var10000[5] = (byte)(-87);
                  var10000[6] = (byte)(-1);
                  return $$v_s(var10000, -1536813479934832614L);
               }, var5.method_71639(), OptionalInt.empty(), var5.method_71640(), OptionalDouble.empty());
            } else {
               var8 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> {
                  byte[] var10000 = new byte[7];
                  var10000[0] = (byte)(105);
                  var10000[1] = (byte)(-13);
                  var10000[2] = (byte)(121);
                  var10000[3] = (byte)(-2);
                  var10000[4] = (byte)(-76);
                  var10000[5] = (byte)(-87);
                  var10000[6] = (byte)(-1);
                  return $$v_s(var10000, -1536813479934832614L);
               }, var5.method_71639(), OptionalInt.empty());
            }

            RenderPass var9 = var8;

            try {
               var8.setPipeline(pipeline);
               byte[] var10001 = new byte[8];
               var10001[0] = (byte)(114);
               var10001[1] = (byte)(-1);
               var10001[2] = (byte)(111);
               var10001[3] = (byte)(-74);
               var10001[4] = (byte)(-75);
               var10001[5] = (byte)(-101);
               var10001[6] = (byte)(-37);
               var10001[7] = (byte)(-93);
               var8.setUniform($$v_s(var10001, -1536813479934832614L), var7);
               var8.setVertexBuffer(0, var6.vertexBuffer);
               var8.setIndexBuffer(var6.indexBuffer, class_5595.field_27373);
               var8.drawIndexed(0, 0, var3, 1);
            } catch (Throwable var17) {
               if (var9 != null) {
                  try {
                     var9.close();
                  } catch (Throwable var16) {
                     var17.addSuppressed(var16);
                  }
               }

               throw var17;
            }

            if (var9 != null) {
               var9.close();
            }
         } finally {
            RenderSystem.getModelViewStack().popMatrix();
         }
      }
   }

   private static GpuBufferSlice writeMeshData(Matrix4f proj, Matrix4f modelView) {
      GpuBufferSlice var2 = (GpuBufferSlice)UNIFORM_CACHE.get(proj, modelView);
      if (var2 != null) {
         return var2;
      }

      MESH_DATA.proj.set(proj);
      MESH_DATA.modelView.set(modelView);
      return (GpuBufferSlice)UNIFORM_CACHE.put(proj, modelView, getUboStorage().method_71102(MESH_DATA));
   }

   private static class_11280 getUboStorage() {
      if (uboStorage == null) {
         byte[] var10002 = new byte[14];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-13);
         var10002[2] = (byte)(121);
         var10002[3] = (byte)(-2);
         var10002[4] = (byte)(-36);
         var10002[5] = (byte)(-38);
         var10002[6] = (byte)(-30);
         var10002[7] = (byte)(-89);
         var10002[8] = (byte)(36);
         var10002[9] = (byte)(-54);
         var10002[10] = (byte)(-60);
         var10002[11] = (byte)(-93);
         var10002[12] = (byte)(-21);
         var10002[13] = (byte)(109);
         uboStorage = new class_11280($$v_s(var10002, -1536813479934832614L), UBO_SIZE, 64);
      }

      return uboStorage;
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

   private static final class MeshData implements class_11281 {
      final Matrix4f proj = new Matrix4f();
      final Matrix4f modelView = new Matrix4f();
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public void method_71104(ByteBuffer buffer) {
         Std140Builder.intoBuffer(buffer).putMat4f(this.proj).putMat4f(this.modelView);
      }

      @Override
      public boolean equals(Object o) {
         return false;
      }
   }
}
