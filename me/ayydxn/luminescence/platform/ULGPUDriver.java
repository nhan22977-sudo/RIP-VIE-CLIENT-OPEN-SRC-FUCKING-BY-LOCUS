package me.ayydxn.luminescence.platform;

import me.ayydxn.luminescence.bitmap.ULBitmap;
import me.ayydxn.luminescence.gpu.ULCommand;
import me.ayydxn.luminescence.gpu.ULIndexBuffer;
import me.ayydxn.luminescence.gpu.ULRenderBuffer;
import me.ayydxn.luminescence.gpu.ULVertexBuffer;
import me.ayydxn.luminescence.util.UsedByNative;

public interface ULGPUDriver {
   void beginSynchronize();

   void endSynchronize();

   @UsedByNative("GPUDriverCallbackAdapter.cpp")
   int nextTextureId();

   void createTexture(int var1, ULBitmap var2);

   void updateTexture(int var1, ULBitmap var2);

   void destroyTexture(int var1);

   @UsedByNative("GPUDriverCallbackAdapter.cpp")
   int nextRenderBufferId();

   void createRenderBuffer(int var1, ULRenderBuffer var2);

   void destroyRenderBuffer(int var1);

   @UsedByNative("GPUDriverCallbackAdapter.cpp")
   int nextGeometryId();

   void createGeometry(int var1, ULVertexBuffer var2, ULIndexBuffer var3);

   void updateGeometry(int var1, ULVertexBuffer var2, ULIndexBuffer var3);

   void destroyGeometry(int var1);

   void updateCommandList(ULCommand[] var1);
}
