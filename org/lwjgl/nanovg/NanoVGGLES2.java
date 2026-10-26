package org.lwjgl.nanovg;

import javax.annotation.Nullable;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class NanoVGGLES2 {
   public static final int NVG_ANTIALIAS = 1;
   public static final int NVG_STENCIL_STROKES = 2;
   public static final int NVG_DEBUG = 4;
   public static final int NVG_IMAGE_NODELETE = 65536;

   protected NanoVGGLES2() {
      throw new UnsupportedOperationException();
   }

   public static native int nnvglCreateImageFromHandle(long var0, int var2, int var3, int var4, int var5);

   public static int nvglCreateImageFromHandle(@NativeType("NVGcontext *") long ctx, @NativeType("GLuint") int textureId, int w, int h, int flags) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      return nnvglCreateImageFromHandle(ctx, textureId, w, h, flags);
   }

   public static native int nnvglImageHandle(long var0, int var2);

   @NativeType("GLuint")
   public static int nvglImageHandle(@NativeType("NVGcontext *") long ctx, int image) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      return nnvglImageHandle(ctx, image);
   }

   public static native long nnvgCreate(int var0);

   @NativeType("NVGcontext *")
   public static long nvgCreate(int flags) {
      return nnvgCreate(flags);
   }

   public static native void nnvgDelete(long var0);

   public static void nvgDelete(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgDelete(ctx);
   }

   public static native long nnvgluCreateFramebuffer(long var0, int var2, int var3, int var4);

   @Nullable
   @NativeType("NVGLUframebuffer *")
   public static NVGLUFramebuffer nvgluCreateFramebuffer(@NativeType("NVGcontext *") long ctx, int w, int h, int imageFlags) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      long __result = nnvgluCreateFramebuffer(ctx, w, h, imageFlags);
      return NVGLUFramebuffer.createSafe(__result);
   }

   public static native void nnvgluBindFramebuffer(long var0, long var2);

   public static void nvgluBindFramebuffer(@NativeType("NVGcontext *") long ctx, @Nullable @NativeType("NVGLUframebuffer *") NVGLUFramebuffer fb) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgluBindFramebuffer(ctx, MemoryUtil.memAddressSafe(fb));
   }

   public static native void nnvgluDeleteFramebuffer(long var0, long var2);

   public static void nvgluDeleteFramebuffer(@NativeType("NVGcontext *") long ctx, @NativeType("NVGLUframebuffer *") NVGLUFramebuffer fb) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgluDeleteFramebuffer(ctx, fb.address());
   }

   static {
      LibNanoVG.initialize();
   }
}
