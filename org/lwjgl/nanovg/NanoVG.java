package org.lwjgl.nanovg;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;

public class NanoVG {
   public static final float NVG_PI = (float) Math.PI;
   public static final int NVG_CCW = 1;
   public static final int NVG_CW = 2;
   public static final int NVG_SOLID = 1;
   public static final int NVG_HOLE = 2;
   public static final int NVG_BUTT = 0;
   public static final int NVG_ROUND = 1;
   public static final int NVG_SQUARE = 2;
   public static final int NVG_BEVEL = 3;
   public static final int NVG_MITER = 4;
   public static final int NVG_ALIGN_LEFT = 1;
   public static final int NVG_ALIGN_CENTER = 2;
   public static final int NVG_ALIGN_RIGHT = 4;
   public static final int NVG_ALIGN_TOP = 8;
   public static final int NVG_ALIGN_MIDDLE = 16;
   public static final int NVG_ALIGN_BOTTOM = 32;
   public static final int NVG_ALIGN_BASELINE = 64;
   public static final int NVG_ZERO = 1;
   public static final int NVG_ONE = 2;
   public static final int NVG_SRC_COLOR = 4;
   public static final int NVG_ONE_MINUS_SRC_COLOR = 8;
   public static final int NVG_DST_COLOR = 16;
   public static final int NVG_ONE_MINUS_DST_COLOR = 32;
   public static final int NVG_SRC_ALPHA = 64;
   public static final int NVG_ONE_MINUS_SRC_ALPHA = 128;
   public static final int NVG_DST_ALPHA = 256;
   public static final int NVG_ONE_MINUS_DST_ALPHA = 512;
   public static final int NVG_SRC_ALPHA_SATURATE = 1024;
   public static final int NVG_SOURCE_OVER = 0;
   public static final int NVG_SOURCE_IN = 1;
   public static final int NVG_SOURCE_OUT = 2;
   public static final int NVG_ATOP = 3;
   public static final int NVG_DESTINATION_OVER = 4;
   public static final int NVG_DESTINATION_IN = 5;
   public static final int NVG_DESTINATION_OUT = 6;
   public static final int NVG_DESTINATION_ATOP = 7;
   public static final int NVG_LIGHTER = 8;
   public static final int NVG_COPY = 9;
   public static final int NVG_XOR = 10;
   public static final int NVG_IMAGE_GENERATE_MIPMAPS = 1;
   public static final int NVG_IMAGE_REPEATX = 2;
   public static final int NVG_IMAGE_REPEATY = 4;
   public static final int NVG_IMAGE_FLIPY = 8;
   public static final int NVG_IMAGE_PREMULTIPLIED = 16;
   public static final int NVG_IMAGE_NEAREST = 32;
   static final long nvgCreateInternal = nvgCreateInternal();
   static final long nvgInternalParams = nvgInternalParams();
   static final long nvgDeleteInternal = nvgDeleteInternal();

   protected NanoVG() {
      throw new UnsupportedOperationException();
   }

   public static native void nnvgBeginFrame(long var0, float var2, float var3, float var4);

   public static void nvgBeginFrame(@NativeType("NVGcontext *") long ctx, float windowWidth, float windowHeight, float devicePixelRatio) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgBeginFrame(ctx, windowWidth, windowHeight, devicePixelRatio);
   }

   public static native void nnvgCancelFrame(long var0);

   public static void nvgCancelFrame(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgCancelFrame(ctx);
   }

   public static native void nnvgEndFrame(long var0);

   public static void nvgEndFrame(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgEndFrame(ctx);
   }

   public static native void nnvgGlobalCompositeOperation(long var0, int var2);

   public static void nvgGlobalCompositeOperation(@NativeType("NVGcontext *") long ctx, int op) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgGlobalCompositeOperation(ctx, op);
   }

   public static native void nnvgGlobalCompositeBlendFunc(long var0, int var2, int var3);

   public static void nvgGlobalCompositeBlendFunc(@NativeType("NVGcontext *") long ctx, int sfactor, int dfactor) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgGlobalCompositeBlendFunc(ctx, sfactor, dfactor);
   }

   public static native void nnvgGlobalCompositeBlendFuncSeparate(long var0, int var2, int var3, int var4, int var5);

   public static void nvgGlobalCompositeBlendFuncSeparate(@NativeType("NVGcontext *") long ctx, int srcRGB, int dstRGB, int srcAlpha, int dstAlpha) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgGlobalCompositeBlendFuncSeparate(ctx, srcRGB, dstRGB, srcAlpha, dstAlpha);
   }

   public static native void nnvgRGB(byte var0, byte var1, byte var2, long var3);

   @NativeType("NVGcolor")
   public static NVGColor nvgRGB(
      @NativeType("unsigned char") byte r, @NativeType("unsigned char") byte g, @NativeType("unsigned char") byte b, @NativeType("NVGcolor") NVGColor __result
   ) {
      nnvgRGB(r, g, b, __result.address());
      return __result;
   }

   public static native void nnvgRGBf(float var0, float var1, float var2, long var3);

   @NativeType("NVGcolor")
   public static NVGColor nvgRGBf(float r, float g, float b, @NativeType("NVGcolor") NVGColor __result) {
      nnvgRGBf(r, g, b, __result.address());
      return __result;
   }

   public static native void nnvgRGBA(byte var0, byte var1, byte var2, byte var3, long var4);

   @NativeType("NVGcolor")
   public static NVGColor nvgRGBA(
      @NativeType("unsigned char") byte r,
      @NativeType("unsigned char") byte g,
      @NativeType("unsigned char") byte b,
      @NativeType("unsigned char") byte a,
      @NativeType("NVGcolor") NVGColor __result
   ) {
      nnvgRGBA(r, g, b, a, __result.address());
      return __result;
   }

   public static native void nnvgRGBAf(float var0, float var1, float var2, float var3, long var4);

   @NativeType("NVGcolor")
   public static NVGColor nvgRGBAf(float r, float g, float b, float a, @NativeType("NVGcolor") NVGColor __result) {
      nnvgRGBAf(r, g, b, a, __result.address());
      return __result;
   }

   public static native void nnvgLerpRGBA(long var0, long var2, float var4, long var5);

   @NativeType("NVGcolor")
   public static NVGColor nvgLerpRGBA(
      @NativeType("NVGcolor") NVGColor c0, @NativeType("NVGcolor") NVGColor c1, float u, @NativeType("NVGcolor") NVGColor __result
   ) {
      nnvgLerpRGBA(c0.address(), c1.address(), u, __result.address());
      return __result;
   }

   public static native void nnvgTransRGBA(long var0, byte var2, long var3);

   @NativeType("NVGcolor")
   public static NVGColor nvgTransRGBA(@NativeType("NVGcolor") NVGColor c0, @NativeType("unsigned char") byte a, @NativeType("NVGcolor") NVGColor __result) {
      nnvgTransRGBA(c0.address(), a, __result.address());
      return __result;
   }

   public static native void nnvgTransRGBAf(long var0, float var2, long var3);

   @NativeType("NVGcolor")
   public static NVGColor nvgTransRGBAf(@NativeType("NVGcolor") NVGColor c0, float a, @NativeType("NVGcolor") NVGColor __result) {
      nnvgTransRGBAf(c0.address(), a, __result.address());
      return __result;
   }

   public static native void nnvgHSL(float var0, float var1, float var2, long var3);

   @NativeType("NVGcolor")
   public static NVGColor nvgHSL(float h, float s, float l, @NativeType("NVGcolor") NVGColor __result) {
      nnvgHSL(h, s, l, __result.address());
      return __result;
   }

   public static native void nnvgHSLA(float var0, float var1, float var2, byte var3, long var4);

   @NativeType("NVGcolor")
   public static NVGColor nvgHSLA(float h, float s, float l, @NativeType("unsigned char") byte a, @NativeType("NVGcolor") NVGColor __result) {
      nnvgHSLA(h, s, l, a, __result.address());
      return __result;
   }

   public static native void nnvgSave(long var0);

   public static void nvgSave(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgSave(ctx);
   }

   public static native void nnvgRestore(long var0);

   public static void nvgRestore(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgRestore(ctx);
   }

   public static native void nnvgReset(long var0);

   public static void nvgReset(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgReset(ctx);
   }

   public static native void nnvgShapeAntiAlias(long var0, int var2);

   public static void nvgShapeAntiAlias(@NativeType("NVGcontext *") long ctx, @NativeType("int") boolean enabled) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgShapeAntiAlias(ctx, enabled ? 1 : 0);
   }

   public static native void nnvgStrokeColor(long var0, long var2);

   public static void nvgStrokeColor(@NativeType("NVGcontext *") long ctx, @NativeType("NVGcolor") NVGColor color) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgStrokeColor(ctx, color.address());
   }

   public static native void nnvgStrokePaint(long var0, long var2);

   public static void nvgStrokePaint(@NativeType("NVGcontext *") long ctx, @NativeType("NVGpaint") NVGPaint paint) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgStrokePaint(ctx, paint.address());
   }

   public static native void nnvgFillColor(long var0, long var2);

   public static void nvgFillColor(@NativeType("NVGcontext *") long ctx, @NativeType("NVGcolor") NVGColor color) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgFillColor(ctx, color.address());
   }

   public static native void nnvgFillPaint(long var0, long var2);

   public static void nvgFillPaint(@NativeType("NVGcontext *") long ctx, @NativeType("NVGpaint") NVGPaint paint) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgFillPaint(ctx, paint.address());
   }

   public static native void nnvgMiterLimit(long var0, float var2);

   public static void nvgMiterLimit(@NativeType("NVGcontext *") long ctx, float limit) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgMiterLimit(ctx, limit);
   }

   public static native void nnvgStrokeWidth(long var0, float var2);

   public static void nvgStrokeWidth(@NativeType("NVGcontext *") long ctx, float size) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgStrokeWidth(ctx, size);
   }

   public static native void nnvgLineCap(long var0, int var2);

   public static void nvgLineCap(@NativeType("NVGcontext *") long ctx, int cap) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgLineCap(ctx, cap);
   }

   public static native void nnvgLineJoin(long var0, int var2);

   public static void nvgLineJoin(@NativeType("NVGcontext *") long ctx, int join) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgLineJoin(ctx, join);
   }

   public static native void nnvgGlobalAlpha(long var0, float var2);

   public static void nvgGlobalAlpha(@NativeType("NVGcontext *") long ctx, float alpha) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgGlobalAlpha(ctx, alpha);
   }

   public static native void nnvgResetTransform(long var0);

   public static void nvgResetTransform(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgResetTransform(ctx);
   }

   public static native void nnvgTransform(long var0, float var2, float var3, float var4, float var5, float var6, float var7);

   public static void nvgTransform(@NativeType("NVGcontext *") long ctx, float a, float b, float c, float d, float e, float f) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgTransform(ctx, a, b, c, d, e, f);
   }

   public static native void nnvgTranslate(long var0, float var2, float var3);

   public static void nvgTranslate(@NativeType("NVGcontext *") long ctx, float x, float y) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgTranslate(ctx, x, y);
   }

   public static native void nnvgRotate(long var0, float var2);

   public static void nvgRotate(@NativeType("NVGcontext *") long ctx, float angle) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgRotate(ctx, angle);
   }

   public static native void nnvgSkewX(long var0, float var2);

   public static void nvgSkewX(@NativeType("NVGcontext *") long ctx, float angle) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgSkewX(ctx, angle);
   }

   public static native void nnvgSkewY(long var0, float var2);

   public static void nvgSkewY(@NativeType("NVGcontext *") long ctx, float angle) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgSkewY(ctx, angle);
   }

   public static native void nnvgScale(long var0, float var2, float var3);

   public static void nvgScale(@NativeType("NVGcontext *") long ctx, float x, float y) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgScale(ctx, x, y);
   }

   public static native void nnvgCurrentTransform(long var0, long var2);

   public static void nvgCurrentTransform(@NativeType("NVGcontext *") long ctx, @NativeType("float *") FloatBuffer xform) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.check(xform, 6);
      }

      nnvgCurrentTransform(ctx, MemoryUtil.memAddress(xform));
   }

   public static native void nnvgTransformIdentity(long var0);

   public static void nvgTransformIdentity(@NativeType("float *") FloatBuffer dst) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformIdentity(MemoryUtil.memAddress(dst));
   }

   public static native void nnvgTransformTranslate(long var0, float var2, float var3);

   public static void nvgTransformTranslate(@NativeType("float *") FloatBuffer dst, float tx, float ty) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformTranslate(MemoryUtil.memAddress(dst), tx, ty);
   }

   public static native void nnvgTransformScale(long var0, float var2, float var3);

   public static void nvgTransformScale(@NativeType("float *") FloatBuffer dst, float sx, float sy) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformScale(MemoryUtil.memAddress(dst), sx, sy);
   }

   public static native void nnvgTransformRotate(long var0, float var2);

   public static void nvgTransformRotate(@NativeType("float *") FloatBuffer dst, float a) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformRotate(MemoryUtil.memAddress(dst), a);
   }

   public static native void nnvgTransformSkewX(long var0, float var2);

   public static void nvgTransformSkewX(@NativeType("float *") FloatBuffer dst, float a) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformSkewX(MemoryUtil.memAddress(dst), a);
   }

   public static native void nnvgTransformSkewY(long var0, float var2);

   public static void nvgTransformSkewY(@NativeType("float *") FloatBuffer dst, float a) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformSkewY(MemoryUtil.memAddress(dst), a);
   }

   public static native void nnvgTransformMultiply(long var0, long var2);

   public static void nvgTransformMultiply(@NativeType("float *") FloatBuffer dst, @NativeType("float const *") FloatBuffer src) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
         Checks.check(src, 6);
      }

      nnvgTransformMultiply(MemoryUtil.memAddress(dst), MemoryUtil.memAddress(src));
   }

   public static native void nnvgTransformPremultiply(long var0, long var2);

   public static void nvgTransformPremultiply(@NativeType("float *") FloatBuffer dst, @NativeType("float const *") FloatBuffer src) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
         Checks.check(src, 6);
      }

      nnvgTransformPremultiply(MemoryUtil.memAddress(dst), MemoryUtil.memAddress(src));
   }

   public static native int nnvgTransformInverse(long var0, long var2);

   @NativeType("int")
   public static boolean nvgTransformInverse(@NativeType("float *") FloatBuffer dst, @NativeType("float const *") FloatBuffer src) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
         Checks.check(src, 6);
      }

      return nnvgTransformInverse(MemoryUtil.memAddress(dst), MemoryUtil.memAddress(src)) != 0;
   }

   public static native void nnvgTransformPoint(long var0, long var2, long var4, float var6, float var7);

   public static void nvgTransformPoint(
      @NativeType("float *") FloatBuffer dstx, @NativeType("float *") FloatBuffer dsty, @NativeType("float const *") FloatBuffer xform, float srcx, float srcy
   ) {
      if (Checks.CHECKS) {
         Checks.check(dstx, 1);
         Checks.check(dsty, 1);
         Checks.check(xform, 6);
      }

      nnvgTransformPoint(MemoryUtil.memAddress(dstx), MemoryUtil.memAddress(dsty), MemoryUtil.memAddress(xform), srcx, srcy);
   }

   public static native float nvgDegToRad(float var0);

   public static native float nvgRadToDeg(float var0);

   public static native int nnvgCreateImage(long var0, long var2, int var4);

   public static int nvgCreateImage(@NativeType("NVGcontext *") long ctx, @NativeType("char const *") ByteBuffer filename, int imageFlags) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkNT1(filename);
      }

      return nnvgCreateImage(ctx, MemoryUtil.memAddress(filename), imageFlags);
   }

   public static int nvgCreateImage(@NativeType("NVGcontext *") long ctx, @NativeType("char const *") CharSequence filename, int imageFlags) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         stack.nASCII(filename, true);
         long filenameEncoded = stack.getPointerAddress();
         return nnvgCreateImage(ctx, filenameEncoded, imageFlags);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native int nnvgCreateImageMem(long var0, int var2, long var3, int var5);

   public static int nvgCreateImageMem(@NativeType("NVGcontext *") long ctx, int imageFlags, @NativeType("unsigned char *") ByteBuffer data) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      return nnvgCreateImageMem(ctx, imageFlags, MemoryUtil.memAddress(data), data.remaining());
   }

   public static native int nnvgCreateImageRGBA(long var0, int var2, int var3, int var4, long var5);

   public static int nvgCreateImageRGBA(
      @NativeType("NVGcontext *") long ctx, int w, int h, int imageFlags, @NativeType("unsigned char const *") ByteBuffer data
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.check(data, w * h * 4);
      }

      return nnvgCreateImageRGBA(ctx, w, h, imageFlags, MemoryUtil.memAddress(data));
   }

   public static native void nnvgUpdateImage(long var0, int var2, long var3);

   public static void nvgUpdateImage(@NativeType("NVGcontext *") long ctx, int image, @NativeType("unsigned char const *") ByteBuffer data) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgUpdateImage(ctx, image, MemoryUtil.memAddress(data));
   }

   public static native void nnvgImageSize(long var0, int var2, long var3, long var5);

   public static void nvgImageSize(@NativeType("NVGcontext *") long ctx, int image, @NativeType("int *") IntBuffer w, @NativeType("int *") IntBuffer h) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.check(w, 1);
         Checks.check(h, 1);
      }

      nnvgImageSize(ctx, image, MemoryUtil.memAddress(w), MemoryUtil.memAddress(h));
   }

   public static native void nnvgDeleteImage(long var0, int var2);

   public static void nvgDeleteImage(@NativeType("NVGcontext *") long ctx, int image) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgDeleteImage(ctx, image);
   }

   public static native void nnvgLinearGradient(long var0, float var2, float var3, float var4, float var5, long var6, long var8, long var10);

   @NativeType("NVGpaint")
   public static NVGPaint nvgLinearGradient(
      @NativeType("NVGcontext *") long ctx,
      float sx,
      float sy,
      float ex,
      float ey,
      @NativeType("NVGcolor") NVGColor icol,
      @NativeType("NVGcolor") NVGColor ocol,
      @NativeType("NVGpaint") NVGPaint __result
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgLinearGradient(ctx, sx, sy, ex, ey, icol.address(), ocol.address(), __result.address());
      return __result;
   }

   public static native void nnvgBoxGradient(
      long var0, float var2, float var3, float var4, float var5, float var6, float var7, long var8, long var10, long var12
   );

   @NativeType("NVGpaint")
   public static NVGPaint nvgBoxGradient(
      @NativeType("NVGcontext *") long ctx,
      float x,
      float y,
      float w,
      float h,
      float r,
      float f,
      @NativeType("NVGcolor") NVGColor icol,
      @NativeType("NVGcolor") NVGColor ocol,
      @NativeType("NVGpaint") NVGPaint __result
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgBoxGradient(ctx, x, y, w, h, r, f, icol.address(), ocol.address(), __result.address());
      return __result;
   }

   public static native void nnvgRadialGradient(long var0, float var2, float var3, float var4, float var5, long var6, long var8, long var10);

   @NativeType("NVGpaint")
   public static NVGPaint nvgRadialGradient(
      @NativeType("NVGcontext *") long ctx,
      float cx,
      float cy,
      float inr,
      float outr,
      @NativeType("NVGcolor") NVGColor icol,
      @NativeType("NVGcolor") NVGColor ocol,
      @NativeType("NVGpaint") NVGPaint __result
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgRadialGradient(ctx, cx, cy, inr, outr, icol.address(), ocol.address(), __result.address());
      return __result;
   }

   public static native void nnvgImagePattern(long var0, float var2, float var3, float var4, float var5, float var6, int var7, float var8, long var9);

   @NativeType("NVGpaint")
   public static NVGPaint nvgImagePattern(
      @NativeType("NVGcontext *") long ctx,
      float ox,
      float oy,
      float ex,
      float ey,
      float angle,
      int image,
      float alpha,
      @NativeType("NVGpaint") NVGPaint __result
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgImagePattern(ctx, ox, oy, ex, ey, angle, image, alpha, __result.address());
      return __result;
   }

   public static native void nnvgScissor(long var0, float var2, float var3, float var4, float var5);

   public static void nvgScissor(@NativeType("NVGcontext *") long ctx, float x, float y, float w, float h) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgScissor(ctx, x, y, w, h);
   }

   public static native void nnvgIntersectScissor(long var0, float var2, float var3, float var4, float var5);

   public static void nvgIntersectScissor(@NativeType("NVGcontext *") long ctx, float x, float y, float w, float h) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgIntersectScissor(ctx, x, y, w, h);
   }

   public static native void nnvgResetScissor(long var0);

   public static void nvgResetScissor(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgResetScissor(ctx);
   }

   public static native void nnvgBeginPath(long var0);

   public static void nvgBeginPath(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgBeginPath(ctx);
   }

   public static native void nnvgMoveTo(long var0, float var2, float var3);

   public static void nvgMoveTo(@NativeType("NVGcontext *") long ctx, float x, float y) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgMoveTo(ctx, x, y);
   }

   public static native void nnvgLineTo(long var0, float var2, float var3);

   public static void nvgLineTo(@NativeType("NVGcontext *") long ctx, float x, float y) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgLineTo(ctx, x, y);
   }

   public static native void nnvgBezierTo(long var0, float var2, float var3, float var4, float var5, float var6, float var7);

   public static void nvgBezierTo(@NativeType("NVGcontext *") long ctx, float c1x, float c1y, float c2x, float c2y, float x, float y) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgBezierTo(ctx, c1x, c1y, c2x, c2y, x, y);
   }

   public static native void nnvgQuadTo(long var0, float var2, float var3, float var4, float var5);

   public static void nvgQuadTo(@NativeType("NVGcontext *") long ctx, float cx, float cy, float x, float y) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgQuadTo(ctx, cx, cy, x, y);
   }

   public static native void nnvgArcTo(long var0, float var2, float var3, float var4, float var5, float var6);

   public static void nvgArcTo(@NativeType("NVGcontext *") long ctx, float x1, float y1, float x2, float y2, float radius) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgArcTo(ctx, x1, y1, x2, y2, radius);
   }

   public static native void nnvgClosePath(long var0);

   public static void nvgClosePath(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgClosePath(ctx);
   }

   public static native void nnvgPathWinding(long var0, int var2);

   public static void nvgPathWinding(@NativeType("NVGcontext *") long ctx, int dir) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgPathWinding(ctx, dir);
   }

   public static native void nnvgArc(long var0, float var2, float var3, float var4, float var5, float var6, int var7);

   public static void nvgArc(@NativeType("NVGcontext *") long ctx, float cx, float cy, float r, float a0, float a1, int dir) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgArc(ctx, cx, cy, r, a0, a1, dir);
   }

   public static native void nnvgRect(long var0, float var2, float var3, float var4, float var5);

   public static void nvgRect(@NativeType("NVGcontext *") long ctx, float x, float y, float w, float h) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgRect(ctx, x, y, w, h);
   }

   public static native void nnvgRoundedRect(long var0, float var2, float var3, float var4, float var5, float var6);

   public static void nvgRoundedRect(@NativeType("NVGcontext *") long ctx, float x, float y, float w, float h, float r) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgRoundedRect(ctx, x, y, w, h, r);
   }

   public static native void nnvgRoundedRectVarying(long var0, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9);

   public static void nvgRoundedRectVarying(
      @NativeType("NVGcontext *") long ctx, float x, float y, float w, float h, float radTopLeft, float radTopRight, float radBottomRight, float radBottomLeft
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgRoundedRectVarying(ctx, x, y, w, h, radTopLeft, radTopRight, radBottomRight, radBottomLeft);
   }

   public static native void nnvgEllipse(long var0, float var2, float var3, float var4, float var5);

   public static void nvgEllipse(@NativeType("NVGcontext *") long ctx, float cx, float cy, float rx, float ry) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgEllipse(ctx, cx, cy, rx, ry);
   }

   public static native void nnvgCircle(long var0, float var2, float var3, float var4);

   public static void nvgCircle(@NativeType("NVGcontext *") long ctx, float cx, float cy, float r) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgCircle(ctx, cx, cy, r);
   }

   public static native void nnvgFill(long var0);

   public static void nvgFill(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgFill(ctx);
   }

   public static native void nnvgStroke(long var0);

   public static void nvgStroke(@NativeType("NVGcontext *") long ctx) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgStroke(ctx);
   }

   public static native int nnvgCreateFont(long var0, long var2, long var4);

   public static int nvgCreateFont(
      @NativeType("NVGcontext *") long ctx, @NativeType("char const *") ByteBuffer name, @NativeType("char const *") ByteBuffer filename
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkNT1(name);
         Checks.checkNT1(filename);
      }

      return nnvgCreateFont(ctx, MemoryUtil.memAddress(name), MemoryUtil.memAddress(filename));
   }

   public static int nvgCreateFont(
      @NativeType("NVGcontext *") long ctx, @NativeType("char const *") CharSequence name, @NativeType("char const *") CharSequence filename
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         stack.nASCII(name, true);
         long nameEncoded = stack.getPointerAddress();
         stack.nASCII(filename, true);
         long filenameEncoded = stack.getPointerAddress();
         return nnvgCreateFont(ctx, nameEncoded, filenameEncoded);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native int nnvgCreateFontAtIndex(long var0, long var2, long var4, int var6);

   public static int nvgCreateFontAtIndex(
      @NativeType("NVGcontext *") long ctx, @NativeType("char const *") ByteBuffer name, @NativeType("char const *") ByteBuffer filename, int fontIndex
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkNT1(name);
         Checks.checkNT1(filename);
      }

      return nnvgCreateFontAtIndex(ctx, MemoryUtil.memAddress(name), MemoryUtil.memAddress(filename), fontIndex);
   }

   public static int nvgCreateFontAtIndex(
      @NativeType("NVGcontext *") long ctx, @NativeType("char const *") CharSequence name, @NativeType("char const *") CharSequence filename, int fontIndex
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         stack.nASCII(name, true);
         long nameEncoded = stack.getPointerAddress();
         stack.nASCII(filename, true);
         long filenameEncoded = stack.getPointerAddress();
         return nnvgCreateFontAtIndex(ctx, nameEncoded, filenameEncoded, fontIndex);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native int nnvgCreateFontMem(long var0, long var2, long var4, int var6, int var7);

   public static int nvgCreateFontMem(
      @NativeType("NVGcontext *") long ctx,
      @NativeType("char const *") ByteBuffer name,
      @NativeType("unsigned char *") ByteBuffer data,
      @NativeType("int") boolean freeData
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkNT1(name);
      }

      return nnvgCreateFontMem(ctx, MemoryUtil.memAddress(name), MemoryUtil.memAddress(data), data.remaining(), freeData ? 1 : 0);
   }

   public static int nvgCreateFontMem(
      @NativeType("NVGcontext *") long ctx,
      @NativeType("char const *") CharSequence name,
      @NativeType("unsigned char *") ByteBuffer data,
      @NativeType("int") boolean freeData
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         stack.nASCII(name, true);
         long nameEncoded = stack.getPointerAddress();
         return nnvgCreateFontMem(ctx, nameEncoded, MemoryUtil.memAddress(data), data.remaining(), freeData ? 1 : 0);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native int nnvgCreateFontMemAtIndex(long var0, long var2, long var4, int var6, int var7, int var8);

   public static int nvgCreateFontMemAtIndex(
      @NativeType("NVGcontext *") long ctx,
      @NativeType("char const *") ByteBuffer name,
      @NativeType("unsigned char *") ByteBuffer data,
      @NativeType("int") boolean freeData,
      int fontIndex
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkNT1(name);
      }

      return nnvgCreateFontMemAtIndex(ctx, MemoryUtil.memAddress(name), MemoryUtil.memAddress(data), data.remaining(), freeData ? 1 : 0, fontIndex);
   }

   public static int nvgCreateFontMemAtIndex(
      @NativeType("NVGcontext *") long ctx,
      @NativeType("char const *") CharSequence name,
      @NativeType("unsigned char *") ByteBuffer data,
      @NativeType("int") boolean freeData,
      int fontIndex
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         stack.nASCII(name, true);
         long nameEncoded = stack.getPointerAddress();
         return nnvgCreateFontMemAtIndex(ctx, nameEncoded, MemoryUtil.memAddress(data), data.remaining(), freeData ? 1 : 0, fontIndex);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native int nnvgFindFont(long var0, long var2);

   public static int nvgFindFont(@NativeType("NVGcontext *") long ctx, @NativeType("char const *") ByteBuffer name) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkNT1(name);
      }

      return nnvgFindFont(ctx, MemoryUtil.memAddress(name));
   }

   public static int nvgFindFont(@NativeType("NVGcontext *") long ctx, @NativeType("char const *") CharSequence name) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         stack.nASCII(name, true);
         long nameEncoded = stack.getPointerAddress();
         return nnvgFindFont(ctx, nameEncoded);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native int nnvgAddFallbackFontId(long var0, int var2, int var3);

   public static int nvgAddFallbackFontId(@NativeType("NVGcontext *") long ctx, int baseFont, int fallbackFont) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      return nnvgAddFallbackFontId(ctx, baseFont, fallbackFont);
   }

   public static native int nnvgAddFallbackFont(long var0, long var2, long var4);

   public static int nvgAddFallbackFont(
      @NativeType("NVGcontext *") long ctx, @NativeType("char const *") ByteBuffer baseFont, @NativeType("char const *") ByteBuffer fallbackFont
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkNT1(baseFont);
         Checks.checkNT1(fallbackFont);
      }

      return nnvgAddFallbackFont(ctx, MemoryUtil.memAddress(baseFont), MemoryUtil.memAddress(fallbackFont));
   }

   public static int nvgAddFallbackFont(
      @NativeType("NVGcontext *") long ctx, @NativeType("char const *") CharSequence baseFont, @NativeType("char const *") CharSequence fallbackFont
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         stack.nASCII(baseFont, true);
         long baseFontEncoded = stack.getPointerAddress();
         stack.nASCII(fallbackFont, true);
         long fallbackFontEncoded = stack.getPointerAddress();
         return nnvgAddFallbackFont(ctx, baseFontEncoded, fallbackFontEncoded);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native void nnvgResetFallbackFontsId(long var0, int var2);

   public static void nvgResetFallbackFontsId(@NativeType("NVGcontext *") long ctx, int baseFont) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgResetFallbackFontsId(ctx, baseFont);
   }

   public static native void nnvgResetFallbackFonts(long var0, long var2);

   public static void nvgResetFallbackFonts(@NativeType("NVGcontext *") long ctx, @NativeType("char const *") ByteBuffer baseFont) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkNT1(baseFont);
      }

      nnvgResetFallbackFonts(ctx, MemoryUtil.memAddress(baseFont));
   }

   public static void nvgResetFallbackFonts(@NativeType("NVGcontext *") long ctx, @NativeType("char const *") CharSequence baseFont) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         stack.nASCII(baseFont, true);
         long baseFontEncoded = stack.getPointerAddress();
         nnvgResetFallbackFonts(ctx, baseFontEncoded);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native void nnvgFontSize(long var0, float var2);

   public static void nvgFontSize(@NativeType("NVGcontext *") long ctx, float size) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgFontSize(ctx, size);
   }

   public static native void nnvgFontBlur(long var0, float var2);

   public static void nvgFontBlur(@NativeType("NVGcontext *") long ctx, float blur) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgFontBlur(ctx, blur);
   }

   public static native void nnvgTextLetterSpacing(long var0, float var2);

   public static void nvgTextLetterSpacing(@NativeType("NVGcontext *") long ctx, float spacing) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgTextLetterSpacing(ctx, spacing);
   }

   public static native void nnvgTextLineHeight(long var0, float var2);

   public static void nvgTextLineHeight(@NativeType("NVGcontext *") long ctx, float lineHeight) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgTextLineHeight(ctx, lineHeight);
   }

   public static native void nnvgTextAlign(long var0, int var2);

   public static void nvgTextAlign(@NativeType("NVGcontext *") long ctx, int align) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgTextAlign(ctx, align);
   }

   public static native void nnvgFontFaceId(long var0, int var2);

   public static void nvgFontFaceId(@NativeType("NVGcontext *") long ctx, int font) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgFontFaceId(ctx, font);
   }

   public static native void nnvgFontFace(long var0, long var2);

   public static void nvgFontFace(@NativeType("NVGcontext *") long ctx, @NativeType("char const *") ByteBuffer font) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkNT1(font);
      }

      nnvgFontFace(ctx, MemoryUtil.memAddress(font));
   }

   public static void nvgFontFace(@NativeType("NVGcontext *") long ctx, @NativeType("char const *") CharSequence font) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         stack.nASCII(font, true);
         long fontEncoded = stack.getPointerAddress();
         nnvgFontFace(ctx, fontEncoded);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native float nnvgText(long var0, float var2, float var3, long var4, long var6);

   public static float nvgText(@NativeType("NVGcontext *") long ctx, float x, float y, @NativeType("char const *") ByteBuffer string) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      return nnvgText(ctx, x, y, MemoryUtil.memAddress(string), MemoryUtil.memAddress(string) + string.remaining());
   }

   public static float nvgText(@NativeType("NVGcontext *") long ctx, float x, float y, @NativeType("char const *") CharSequence string) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         int stringEncodedLength = stack.nUTF8(string, false);
         long stringEncoded = stack.getPointerAddress();
         return nnvgText(ctx, x, y, stringEncoded, stringEncoded + stringEncodedLength);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native void nnvgTextBox(long var0, float var2, float var3, float var4, long var5, long var7);

   public static void nvgTextBox(@NativeType("NVGcontext *") long ctx, float x, float y, float breakRowWidth, @NativeType("char const *") ByteBuffer string) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      nnvgTextBox(ctx, x, y, breakRowWidth, MemoryUtil.memAddress(string), MemoryUtil.memAddress(string) + string.remaining());
   }

   public static void nvgTextBox(@NativeType("NVGcontext *") long ctx, float x, float y, float breakRowWidth, @NativeType("char const *") CharSequence string) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         int stringEncodedLength = stack.nUTF8(string, false);
         long stringEncoded = stack.getPointerAddress();
         nnvgTextBox(ctx, x, y, breakRowWidth, stringEncoded, stringEncoded + stringEncodedLength);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native float nnvgTextBounds(long var0, float var2, float var3, long var4, long var6, long var8);

   public static float nvgTextBounds(
      @NativeType("NVGcontext *") long ctx,
      float x,
      float y,
      @NativeType("char const *") ByteBuffer string,
      @Nullable @NativeType("float *") FloatBuffer bounds
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkSafe(bounds, 4);
      }

      return nnvgTextBounds(ctx, x, y, MemoryUtil.memAddress(string), MemoryUtil.memAddress(string) + string.remaining(), MemoryUtil.memAddressSafe(bounds));
   }

   public static float nvgTextBounds(
      @NativeType("NVGcontext *") long ctx,
      float x,
      float y,
      @NativeType("char const *") CharSequence string,
      @Nullable @NativeType("float *") FloatBuffer bounds
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkSafe(bounds, 4);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         int stringEncodedLength = stack.nUTF8(string, false);
         long stringEncoded = stack.getPointerAddress();
         return nnvgTextBounds(ctx, x, y, stringEncoded, stringEncoded + stringEncodedLength, MemoryUtil.memAddressSafe(bounds));
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native void nnvgTextBoxBounds(long var0, float var2, float var3, float var4, long var5, long var7, long var9);

   public static void nvgTextBoxBounds(
      @NativeType("NVGcontext *") long ctx,
      float x,
      float y,
      float breakRowWidth,
      @NativeType("char const *") ByteBuffer string,
      @Nullable @NativeType("float *") FloatBuffer bounds
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkSafe(bounds, 4);
      }

      nnvgTextBoxBounds(
         ctx, x, y, breakRowWidth, MemoryUtil.memAddress(string), MemoryUtil.memAddress(string) + string.remaining(), MemoryUtil.memAddressSafe(bounds)
      );
   }

   public static void nvgTextBoxBounds(
      @NativeType("NVGcontext *") long ctx,
      float x,
      float y,
      float breakRowWidth,
      @NativeType("char const *") CharSequence string,
      @Nullable @NativeType("float *") FloatBuffer bounds
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkSafe(bounds, 4);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         int stringEncodedLength = stack.nUTF8(string, false);
         long stringEncoded = stack.getPointerAddress();
         nnvgTextBoxBounds(ctx, x, y, breakRowWidth, stringEncoded, stringEncoded + stringEncodedLength, MemoryUtil.memAddressSafe(bounds));
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native int nnvgTextGlyphPositions(long var0, float var2, float var3, long var4, long var6, long var8, int var10);

   public static int nvgTextGlyphPositions(
      @NativeType("NVGcontext *") long ctx,
      float x,
      float y,
      @NativeType("char const *") ByteBuffer string,
      @NativeType("NVGglyphPosition *") NVGGlyphPosition.Buffer positions
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      return nnvgTextGlyphPositions(
         ctx, x, y, MemoryUtil.memAddress(string), MemoryUtil.memAddress(string) + string.remaining(), positions.address(), positions.remaining()
      );
   }

   public static int nvgTextGlyphPositions(
      @NativeType("NVGcontext *") long ctx,
      float x,
      float y,
      @NativeType("char const *") CharSequence string,
      @NativeType("NVGglyphPosition *") NVGGlyphPosition.Buffer positions
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         int stringEncodedLength = stack.nUTF8(string, false);
         long stringEncoded = stack.getPointerAddress();
         return nnvgTextGlyphPositions(ctx, x, y, stringEncoded, stringEncoded + stringEncodedLength, positions.address(), positions.remaining());
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native void nnvgTextMetrics(long var0, long var2, long var4, long var6);

   public static void nvgTextMetrics(
      @NativeType("NVGcontext *") long ctx,
      @Nullable @NativeType("float *") FloatBuffer ascender,
      @Nullable @NativeType("float *") FloatBuffer descender,
      @Nullable @NativeType("float *") FloatBuffer lineh
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkSafe(ascender, 1);
         Checks.checkSafe(descender, 1);
         Checks.checkSafe(lineh, 1);
      }

      nnvgTextMetrics(ctx, MemoryUtil.memAddressSafe(ascender), MemoryUtil.memAddressSafe(descender), MemoryUtil.memAddressSafe(lineh));
   }

   public static native int nnvgTextBreakLines(long var0, long var2, long var4, float var6, long var7, int var9);

   public static int nvgTextBreakLines(
      @NativeType("NVGcontext *") long ctx,
      @NativeType("char const *") ByteBuffer string,
      float breakRowWidth,
      @NativeType("NVGtextRow *") NVGTextRow.Buffer rows
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      return nnvgTextBreakLines(
         ctx, MemoryUtil.memAddress(string), MemoryUtil.memAddress(string) + string.remaining(), breakRowWidth, rows.address(), rows.remaining()
      );
   }

   public static int nvgTextBreakLines(
      @NativeType("NVGcontext *") long ctx,
      @NativeType("char const *") CharSequence string,
      float breakRowWidth,
      @NativeType("NVGtextRow *") NVGTextRow.Buffer rows
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         int stringEncodedLength = stack.nUTF8(string, false);
         long stringEncoded = stack.getPointerAddress();
         return nnvgTextBreakLines(ctx, stringEncoded, stringEncoded + stringEncodedLength, breakRowWidth, rows.address(), rows.remaining());
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   @NativeType("void *")
   private static native long nvgCreateInternal();

   @NativeType("void *")
   private static native long nvgInternalParams();

   @NativeType("void *")
   private static native long nvgDeleteInternal();

   public static native void nnvgCurrentTransform(long var0, float[] var2);

   public static void nvgCurrentTransform(@NativeType("NVGcontext *") long ctx, @NativeType("float *") float[] xform) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.check(xform, 6);
      }

      nnvgCurrentTransform(ctx, xform);
   }

   public static native void nnvgTransformIdentity(float[] var0);

   public static void nvgTransformIdentity(@NativeType("float *") float[] dst) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformIdentity(dst);
   }

   public static native void nnvgTransformTranslate(float[] var0, float var1, float var2);

   public static void nvgTransformTranslate(@NativeType("float *") float[] dst, float tx, float ty) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformTranslate(dst, tx, ty);
   }

   public static native void nnvgTransformScale(float[] var0, float var1, float var2);

   public static void nvgTransformScale(@NativeType("float *") float[] dst, float sx, float sy) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformScale(dst, sx, sy);
   }

   public static native void nnvgTransformRotate(float[] var0, float var1);

   public static void nvgTransformRotate(@NativeType("float *") float[] dst, float a) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformRotate(dst, a);
   }

   public static native void nnvgTransformSkewX(float[] var0, float var1);

   public static void nvgTransformSkewX(@NativeType("float *") float[] dst, float a) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformSkewX(dst, a);
   }

   public static native void nnvgTransformSkewY(float[] var0, float var1);

   public static void nvgTransformSkewY(@NativeType("float *") float[] dst, float a) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
      }

      nnvgTransformSkewY(dst, a);
   }

   public static native void nnvgTransformMultiply(float[] var0, float[] var1);

   public static void nvgTransformMultiply(@NativeType("float *") float[] dst, @NativeType("float const *") float[] src) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
         Checks.check(src, 6);
      }

      nnvgTransformMultiply(dst, src);
   }

   public static native void nnvgTransformPremultiply(float[] var0, float[] var1);

   public static void nvgTransformPremultiply(@NativeType("float *") float[] dst, @NativeType("float const *") float[] src) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
         Checks.check(src, 6);
      }

      nnvgTransformPremultiply(dst, src);
   }

   public static native int nnvgTransformInverse(float[] var0, float[] var1);

   @NativeType("int")
   public static boolean nvgTransformInverse(@NativeType("float *") float[] dst, @NativeType("float const *") float[] src) {
      if (Checks.CHECKS) {
         Checks.check(dst, 6);
         Checks.check(src, 6);
      }

      return nnvgTransformInverse(dst, src) != 0;
   }

   public static native void nnvgTransformPoint(float[] var0, float[] var1, float[] var2, float var3, float var4);

   public static void nvgTransformPoint(
      @NativeType("float *") float[] dstx, @NativeType("float *") float[] dsty, @NativeType("float const *") float[] xform, float srcx, float srcy
   ) {
      if (Checks.CHECKS) {
         Checks.check(dstx, 1);
         Checks.check(dsty, 1);
         Checks.check(xform, 6);
      }

      nnvgTransformPoint(dstx, dsty, xform, srcx, srcy);
   }

   public static native void nnvgImageSize(long var0, int var2, int[] var3, int[] var4);

   public static void nvgImageSize(@NativeType("NVGcontext *") long ctx, int image, @NativeType("int *") int[] w, @NativeType("int *") int[] h) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.check(w, 1);
         Checks.check(h, 1);
      }

      nnvgImageSize(ctx, image, w, h);
   }

   public static native float nnvgTextBounds(long var0, float var2, float var3, long var4, long var6, float[] var8);

   public static float nvgTextBounds(
      @NativeType("NVGcontext *") long ctx, float x, float y, @NativeType("char const *") ByteBuffer string, @Nullable @NativeType("float *") float[] bounds
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkSafe(bounds, 4);
      }

      return nnvgTextBounds(ctx, x, y, MemoryUtil.memAddress(string), MemoryUtil.memAddress(string) + string.remaining(), bounds);
   }

   public static float nvgTextBounds(
      @NativeType("NVGcontext *") long ctx, float x, float y, @NativeType("char const *") CharSequence string, @Nullable @NativeType("float *") float[] bounds
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkSafe(bounds, 4);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         int stringEncodedLength = stack.nUTF8(string, false);
         long stringEncoded = stack.getPointerAddress();
         return nnvgTextBounds(ctx, x, y, stringEncoded, stringEncoded + stringEncodedLength, bounds);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native void nnvgTextBoxBounds(long var0, float var2, float var3, float var4, long var5, long var7, float[] var9);

   public static void nvgTextBoxBounds(
      @NativeType("NVGcontext *") long ctx,
      float x,
      float y,
      float breakRowWidth,
      @NativeType("char const *") ByteBuffer string,
      @Nullable @NativeType("float *") float[] bounds
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkSafe(bounds, 4);
      }

      nnvgTextBoxBounds(ctx, x, y, breakRowWidth, MemoryUtil.memAddress(string), MemoryUtil.memAddress(string) + string.remaining(), bounds);
   }

   public static void nvgTextBoxBounds(
      @NativeType("NVGcontext *") long ctx,
      float x,
      float y,
      float breakRowWidth,
      @NativeType("char const *") CharSequence string,
      @Nullable @NativeType("float *") float[] bounds
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkSafe(bounds, 4);
      }

      MemoryStack stack = MemoryStack.stackGet();
      int stackPointer = stack.getPointer();

      try {
         int stringEncodedLength = stack.nUTF8(string, false);
         long stringEncoded = stack.getPointerAddress();
         nnvgTextBoxBounds(ctx, x, y, breakRowWidth, stringEncoded, stringEncoded + stringEncodedLength, bounds);
      } finally {
         stack.setPointer(stackPointer);
      }
   }

   public static native void nnvgTextMetrics(long var0, float[] var2, float[] var3, float[] var4);

   public static void nvgTextMetrics(
      @NativeType("NVGcontext *") long ctx,
      @Nullable @NativeType("float *") float[] ascender,
      @Nullable @NativeType("float *") float[] descender,
      @Nullable @NativeType("float *") float[] lineh
   ) {
      if (Checks.CHECKS) {
         Checks.check(ctx);
         Checks.checkSafe(ascender, 1);
         Checks.checkSafe(descender, 1);
         Checks.checkSafe(lineh, 1);
      }

      nnvgTextMetrics(ctx, ascender, descender, lineh);
   }

   static {
      LibNanoVG.initialize();
   }
}
