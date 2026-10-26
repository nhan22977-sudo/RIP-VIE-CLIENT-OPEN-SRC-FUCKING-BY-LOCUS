package me.drake.vieclient.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import net.minecraft.class_243;
import net.minecraft.class_310;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

public class VieMeshBuilder {
   private final VertexFormat format;
   private final int vertexSize;
   private ByteBuffer vertices;
   private long verticesStart;
   private long verticesPtr;
   private ByteBuffer indices;
   private long indicesPtr;
   private int vertexI;
   private int indicesCount;
   private boolean building;
   private double cameraX;
   private double cameraY;
   private double cameraZ;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public VieMeshBuilder(RenderPipeline pipeline) {
      this.format = pipeline.getVertexFormat();
      this.vertexSize = this.format.getVertexSize();
   }

   public void begin() {
      if (this.building) {
         throw new IllegalStateException(
            "VieMeshBuilder.begin() called while building"
         );
      }

      if (this.vertices == null) {
         this.allocate(1024, 2048);
      }

      this.verticesPtr = this.verticesStart;
      this.vertexI = 0;
      this.indicesCount = 0;
      this.building = true;
      class_310 var1 = class_310.method_1551();
      if (var1.field_1773 != null && var1.field_1773.method_19418() != null) {
         class_243 var2 = var1.field_1773.method_19418().method_71156();
         this.cameraX = var2.field_1352;
         this.cameraY = var2.field_1351;
         this.cameraZ = var2.field_1350;
      } else {
         this.cameraX = 0.0;
         this.cameraY = 0.0;
         this.cameraZ = 0.0;
      }
   }

   public void beginNoCamera() {
      if (this.building) {
         throw new IllegalStateException(
            "VieMeshBuilder.beginNoCamera() called while building"
         );
      }

      if (this.vertices == null) {
         this.allocate(1024, 2048);
      }

      this.verticesPtr = this.verticesStart;
      this.vertexI = 0;
      this.indicesCount = 0;
      this.building = true;
      this.cameraX = 0.0;
      this.cameraY = 0.0;
      this.cameraZ = 0.0;
   }

   public VieMeshBuilder vec3(double x, double y, double z) {
      this.ensureVertexCapacity(1);
      long var7 = this.verticesPtr;
      MemoryUtil.memPutFloat(var7, (float)(x - this.cameraX));
      MemoryUtil.memPutFloat(var7 + 4L, (float)(y - this.cameraY));
      MemoryUtil.memPutFloat(var7 + 8L, (float)(z - this.cameraZ));
      this.verticesPtr += 12L;
      return this;
   }

   public VieMeshBuilder color(int r, int g, int b, int a) {
      long var5 = this.verticesPtr;
      MemoryUtil.memPutByte(var5, (byte)r);
      MemoryUtil.memPutByte(var5 + 1L, (byte)g);
      MemoryUtil.memPutByte(var5 + 2L, (byte)b);
      MemoryUtil.memPutByte(var5 + 3L, (byte)a);
      this.verticesPtr += 4L;
      return this;
   }

   public VieMeshBuilder color(int argb) {
      return this.color(
         argb >> (16) & (255),
         argb >> (8) & (255),
         argb & (255),
         argb >> (24) & (255)
      );
   }

   public VieMeshBuilder uv(float u, float v) {
      long var3 = this.verticesPtr;
      MemoryUtil.memPutFloat(var3, u);
      MemoryUtil.memPutFloat(var3 + 4L, v);
      this.verticesPtr += 8L;
      return this;
   }

   public int next() {
      return this.vertexI++;
   }

   public void line(int i1, int i2) {
      this.ensureIndexCapacity(2);
      long var3 = this.indicesPtr + this.indicesCount * 4L;
      MemoryUtil.memPutInt(var3, i1);
      MemoryUtil.memPutInt(var3 + 4L, i2);
      this.indicesCount += 2;
   }

   public void quad(int i1, int i2, int i3, int i4) {
      this.ensureIndexCapacity(6);
      long var5 = this.indicesPtr + this.indicesCount * 4L;
      MemoryUtil.memPutInt(var5, i1);
      MemoryUtil.memPutInt(var5 + 4L, i2);
      MemoryUtil.memPutInt(var5 + 8L, i3);
      MemoryUtil.memPutInt(var5 + 12L, i3);
      MemoryUtil.memPutInt(var5 + 16L, i4);
      MemoryUtil.memPutInt(var5 + 20L, i1);
      this.indicesCount += 6;
   }

   public void ensureCapacity(int vertexCount, int indexCount) {
      this.ensureVertexCapacity(vertexCount);
      this.ensureIndexCapacity(indexCount);
   }

   private void ensureVertexCapacity(int count) {
      if (this.vertices == null) {
         this.allocate(1024, 2048);
      } else {
         if ((this.vertexI + count) * this.vertexSize > this.vertices.capacity()) {
            int var2 = this.getVertexOffset();
            int var3 = Math.max(this.vertices.capacity() * (2), this.vertices.capacity() + count * this.vertexSize);
            ByteBuffer var4 = BufferUtils.createByteBuffer(var3);
            MemoryUtil.memCopy(MemoryUtil.memAddress0(this.vertices), MemoryUtil.memAddress0(var4), var2);
            this.vertices = var4;
            this.verticesStart = MemoryUtil.memAddress0(this.vertices);
            this.verticesPtr = this.verticesStart + var2;
         }
      }
   }

   private void ensureIndexCapacity(int count) {
      if (this.indices == null) {
         this.allocate(1024, 2048);
      } else {
         if ((this.indicesCount + count) * (4) > this.indices.capacity()) {
            int var2 = Math.max(this.indices.capacity() * (2), this.indices.capacity() + count * (4));
            ByteBuffer var3 = BufferUtils.createByteBuffer(var2);
            MemoryUtil.memCopy(MemoryUtil.memAddress0(this.indices), MemoryUtil.memAddress0(var3), this.indicesCount * 4L);
            this.indices = var3;
            this.indicesPtr = MemoryUtil.memAddress0(this.indices);
         }
      }
   }

   private void allocate(int vertexCount, int indexCount) {
      this.vertices = BufferUtils.createByteBuffer(this.vertexSize * vertexCount);
      this.verticesPtr = this.verticesStart = MemoryUtil.memAddress0(this.vertices);
      this.indices = BufferUtils.createByteBuffer(indexCount * (4));
      this.indicesPtr = MemoryUtil.memAddress0(this.indices);
   }

   public void end() {
      if (!this.building) {
         byte[] var10002 = new byte[46];
         var10002[0] = (byte)(110);
         var10002[1] = (byte)(-13);
         var10002[2] = (byte)(121);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(42);
         var10002[5] = (byte)(8);
         var10002[6] = (byte)(-17);
         var10002[7] = (byte)(-54);
         var10002[8] = (byte)(37);
         var10002[9] = (byte)(-53);
         var10002[10] = (byte)(-120);
         var10002[11] = (byte)(-110);
         var10002[12] = (byte)(114);
         var10002[13] = (byte)(-47);
         var10002[14] = (byte)(-127);
         var10002[15] = (byte)(-107);
         var10002[16] = (byte)(6);
         var10002[17] = (byte)(46);
         var10002[18] = (byte)(-28);
         var10002[19] = (byte)(-121);
         var10002[20] = (byte)(-33);
         var10002[21] = (byte)(-24);
         var10002[22] = (byte)(-74);
         var10002[23] = (byte)(-76);
         var10002[24] = (byte)(-20);
         var10002[25] = (byte)(119);
         var10002[26] = (byte)(-16);
         var10002[27] = (byte)(102);
         var10002[28] = (byte)(-80);
         var10002[29] = (byte)(-101);
         var10002[30] = (byte)(-106);
         var10002[31] = (byte)(108);
         var10002[32] = (byte)(-67);
         var10002[33] = (byte)(26);
         var10002[34] = (byte)(18);
         var10002[35] = (byte)(17);
         var10002[36] = (byte)(-37);
         var10002[37] = (byte)(-5);
         var10002[38] = (byte)(69);
         var10002[39] = (byte)(29);
         var10002[40] = (byte)(-103);
         var10002[41] = (byte)(-82);
         var10002[42] = (byte)(32);
         var10002[43] = (byte)(127);
         var10002[44] = (byte)(25);
         var10002[45] = (byte)(100);
         throw new IllegalStateException($$v_s(var10002, -6880191853485633507L));
      }

      this.building = false;
   }

   public boolean isBuilding() {
      return this.building;
   }

   public boolean hasData() {
      return this.indicesCount > 0;
   }

   public GpuBuffer getVertexBuffer() {
      this.vertices.position(0).limit(this.getVertexOffset());
      return this.format.uploadImmediateVertexBuffer(this.vertices);
   }

   public GpuBuffer getIndexBuffer() {
      this.indices.position(0).limit(this.indicesCount * (4));
      return this.format.uploadImmediateIndexBuffer(this.indices);
   }

   public int getIndicesCount() {
      return this.indicesCount;
   }

   private int getVertexOffset() {
      return (int)(this.verticesPtr - this.verticesStart);
   }

   public VieMeshBuilder.GpuBufferPair getBuffers() {
      return new VieMeshBuilder.GpuBufferPair(this.getVertexBuffer(), this.getIndexBuffer());
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

   public static final class GpuBufferPair {
      public final GpuBuffer vertexBuffer;
      public final GpuBuffer indexBuffer;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public GpuBufferPair(GpuBuffer v, GpuBuffer i) {
         this.vertexBuffer = v;
         this.indexBuffer = i;
      }
   }
}
