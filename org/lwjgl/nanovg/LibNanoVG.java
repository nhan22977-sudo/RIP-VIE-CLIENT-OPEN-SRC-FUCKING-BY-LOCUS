package org.lwjgl.nanovg;

import org.lwjgl.system.Configuration;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Platform;
import org.lwjgl.system.MemoryUtil.MemoryAllocator;

final class LibNanoVG {
   private LibNanoVG() {
   }

   static void initialize() {
   }

   private static native void setupMalloc(long var0, long var2, long var4, long var6, long var8, long var10);

   static {
      String libName = Platform.mapLibraryNameBundled("lwjgl_nanovg");
      Library.loadSystem(System::load, System::loadLibrary, LibNanoVG.class, "org.lwjgl.nanovg", libName);
      MemoryAllocator allocator = MemoryUtil.getAllocator((Boolean)Configuration.DEBUG_MEMORY_ALLOCATOR_INTERNAL.get(true));
      setupMalloc(
         allocator.getMalloc(), allocator.getCalloc(), allocator.getRealloc(), allocator.getFree(), allocator.getAlignedAlloc(), allocator.getAlignedFree()
      );
   }
}
