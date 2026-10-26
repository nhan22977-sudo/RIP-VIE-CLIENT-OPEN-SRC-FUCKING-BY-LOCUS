package org.lwjgl.nanovg;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.system.Struct.Layout;
import org.lwjgl.system.Struct.Member;

@NativeType("struct NSVGshape")
public class NSVGShape extends Struct<NSVGShape> {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int ID;
   public static final int FILL;
   public static final int STROKE;
   public static final int OPACITY;
   public static final int STROKEWIDTH;
   public static final int STROKEDASHOFFSET;
   public static final int STROKEDASHARRAY;
   public static final int STROKEDASHCOUNT;
   public static final int STROKELINEJOIN;
   public static final int STROKELINECAP;
   public static final int MITERLIMIT;
   public static final int FILLRULE;
   public static final int FLAGS;
   public static final int BOUNDS;
   public static final int FILLGRADIENT;
   public static final int STROKEGRADIENT;
   public static final int XFORM;
   public static final int PATHS;
   public static final int NEXT;

   protected NSVGShape(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected NSVGShape create(long address, @Nullable ByteBuffer container) {
      return new NSVGShape(address, container);
   }

   public NSVGShape(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   @NativeType("char[64]")
   public ByteBuffer id() {
      return nid(this.address());
   }

   @NativeType("char[64]")
   public String idString() {
      return nidString(this.address());
   }

   @NativeType("NSVGpaint")
   public NSVGPaint fill() {
      return nfill(this.address());
   }

   @NativeType("NSVGpaint")
   public NSVGPaint stroke() {
      return nstroke(this.address());
   }

   public float opacity() {
      return nopacity(this.address());
   }

   public float strokeWidth() {
      return nstrokeWidth(this.address());
   }

   public float strokeDashOffset() {
      return nstrokeDashOffset(this.address());
   }

   @NativeType("float[8]")
   public FloatBuffer strokeDashArray() {
      return nstrokeDashArray(this.address());
   }

   public float strokeDashArray(int index) {
      return nstrokeDashArray(this.address(), index);
   }

   @NativeType("char")
   public byte strokeDashCount() {
      return nstrokeDashCount(this.address());
   }

   @NativeType("char")
   public byte strokeLineJoin() {
      return nstrokeLineJoin(this.address());
   }

   @NativeType("char")
   public byte strokeLineCap() {
      return nstrokeLineCap(this.address());
   }

   public float miterLimit() {
      return nmiterLimit(this.address());
   }

   @NativeType("char")
   public byte fillRule() {
      return nfillRule(this.address());
   }

   @NativeType("unsigned char")
   public byte flags() {
      return nflags(this.address());
   }

   @NativeType("float[4]")
   public FloatBuffer bounds() {
      return nbounds(this.address());
   }

   public float bounds(int index) {
      return nbounds(this.address(), index);
   }

   @NativeType("char[64]")
   public ByteBuffer fillGradient() {
      return nfillGradient(this.address());
   }

   @NativeType("char[64]")
   public String fillGradientString() {
      return nfillGradientString(this.address());
   }

   @NativeType("char[64]")
   public ByteBuffer strokeGradient() {
      return nstrokeGradient(this.address());
   }

   @NativeType("char[64]")
   public String strokeGradientString() {
      return nstrokeGradientString(this.address());
   }

   @NativeType("float[6]")
   public FloatBuffer xform() {
      return nxform(this.address());
   }

   public float xform(int index) {
      return nxform(this.address(), index);
   }

   @NativeType("NSVGpath *")
   public NSVGPath paths() {
      return npaths(this.address());
   }

   @NativeType("NSVGshape *")
   public NSVGShape next() {
      return nnext(this.address());
   }

   public static NSVGShape create(long address) {
      return new NSVGShape(address, null);
   }

   @Nullable
   public static NSVGShape createSafe(long address) {
      return address == 0L ? null : new NSVGShape(address, null);
   }

   public static NSVGShape.Buffer create(long address, int capacity) {
      return new NSVGShape.Buffer(address, capacity);
   }

   @Nullable
   public static NSVGShape.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new NSVGShape.Buffer(address, capacity);
   }

   public static ByteBuffer nid(long struct) {
      return MemoryUtil.memByteBuffer(struct + ID, 64);
   }

   public static String nidString(long struct) {
      return MemoryUtil.memASCII(struct + ID);
   }

   public static NSVGPaint nfill(long struct) {
      return NSVGPaint.create(struct + FILL);
   }

   public static NSVGPaint nstroke(long struct) {
      return NSVGPaint.create(struct + STROKE);
   }

   public static float nopacity(long struct) {
      return UNSAFE.getFloat(null, struct + OPACITY);
   }

   public static float nstrokeWidth(long struct) {
      return UNSAFE.getFloat(null, struct + STROKEWIDTH);
   }

   public static float nstrokeDashOffset(long struct) {
      return UNSAFE.getFloat(null, struct + STROKEDASHOFFSET);
   }

   public static FloatBuffer nstrokeDashArray(long struct) {
      return MemoryUtil.memFloatBuffer(struct + STROKEDASHARRAY, 8);
   }

   public static float nstrokeDashArray(long struct, int index) {
      return UNSAFE.getFloat(null, struct + STROKEDASHARRAY + Checks.check(index, 8) * 4L);
   }

   public static byte nstrokeDashCount(long struct) {
      return UNSAFE.getByte(null, struct + STROKEDASHCOUNT);
   }

   public static byte nstrokeLineJoin(long struct) {
      return UNSAFE.getByte(null, struct + STROKELINEJOIN);
   }

   public static byte nstrokeLineCap(long struct) {
      return UNSAFE.getByte(null, struct + STROKELINECAP);
   }

   public static float nmiterLimit(long struct) {
      return UNSAFE.getFloat(null, struct + MITERLIMIT);
   }

   public static byte nfillRule(long struct) {
      return UNSAFE.getByte(null, struct + FILLRULE);
   }

   public static byte nflags(long struct) {
      return UNSAFE.getByte(null, struct + FLAGS);
   }

   public static FloatBuffer nbounds(long struct) {
      return MemoryUtil.memFloatBuffer(struct + BOUNDS, 4);
   }

   public static float nbounds(long struct, int index) {
      return UNSAFE.getFloat(null, struct + BOUNDS + Checks.check(index, 4) * 4L);
   }

   public static ByteBuffer nfillGradient(long struct) {
      return MemoryUtil.memByteBuffer(struct + FILLGRADIENT, 64);
   }

   public static String nfillGradientString(long struct) {
      return MemoryUtil.memASCII(struct + FILLGRADIENT);
   }

   public static ByteBuffer nstrokeGradient(long struct) {
      return MemoryUtil.memByteBuffer(struct + STROKEGRADIENT, 64);
   }

   public static String nstrokeGradientString(long struct) {
      return MemoryUtil.memASCII(struct + STROKEGRADIENT);
   }

   public static FloatBuffer nxform(long struct) {
      return MemoryUtil.memFloatBuffer(struct + XFORM, 6);
   }

   public static float nxform(long struct, int index) {
      return UNSAFE.getFloat(null, struct + XFORM + Checks.check(index, 6) * 4L);
   }

   public static NSVGPath npaths(long struct) {
      return NSVGPath.create(MemoryUtil.memGetAddress(struct + PATHS));
   }

   public static NSVGShape nnext(long struct) {
      return create(MemoryUtil.memGetAddress(struct + NEXT));
   }

   static {
      Layout layout = __struct(
         new Member[]{
            __array(1, 64),
            __member(NSVGPaint.SIZEOF, NSVGPaint.ALIGNOF),
            __member(NSVGPaint.SIZEOF, NSVGPaint.ALIGNOF),
            __member(4),
            __member(4),
            __member(4),
            __array(4, 8),
            __member(1),
            __member(1),
            __member(1),
            __member(4),
            __member(1),
            __member(1),
            __array(4, 4),
            __array(1, 64),
            __array(1, 64),
            __array(4, 6),
            __member(POINTER_SIZE),
            __member(POINTER_SIZE)
         }
      );
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      ID = layout.offsetof(0);
      FILL = layout.offsetof(1);
      STROKE = layout.offsetof(2);
      OPACITY = layout.offsetof(3);
      STROKEWIDTH = layout.offsetof(4);
      STROKEDASHOFFSET = layout.offsetof(5);
      STROKEDASHARRAY = layout.offsetof(6);
      STROKEDASHCOUNT = layout.offsetof(7);
      STROKELINEJOIN = layout.offsetof(8);
      STROKELINECAP = layout.offsetof(9);
      MITERLIMIT = layout.offsetof(10);
      FILLRULE = layout.offsetof(11);
      FLAGS = layout.offsetof(12);
      BOUNDS = layout.offsetof(13);
      FILLGRADIENT = layout.offsetof(14);
      STROKEGRADIENT = layout.offsetof(15);
      XFORM = layout.offsetof(16);
      PATHS = layout.offsetof(17);
      NEXT = layout.offsetof(18);
   }

   public static class Buffer extends StructBuffer<NSVGShape, NSVGShape.Buffer> {
      private static final NSVGShape ELEMENT_FACTORY = NSVGShape.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / NSVGShape.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected NSVGShape.Buffer self() {
         return this;
      }

      protected NSVGShape getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("char[64]")
      public ByteBuffer id() {
         return NSVGShape.nid(this.address());
      }

      @NativeType("char[64]")
      public String idString() {
         return NSVGShape.nidString(this.address());
      }

      @NativeType("NSVGpaint")
      public NSVGPaint fill() {
         return NSVGShape.nfill(this.address());
      }

      @NativeType("NSVGpaint")
      public NSVGPaint stroke() {
         return NSVGShape.nstroke(this.address());
      }

      public float opacity() {
         return NSVGShape.nopacity(this.address());
      }

      public float strokeWidth() {
         return NSVGShape.nstrokeWidth(this.address());
      }

      public float strokeDashOffset() {
         return NSVGShape.nstrokeDashOffset(this.address());
      }

      @NativeType("float[8]")
      public FloatBuffer strokeDashArray() {
         return NSVGShape.nstrokeDashArray(this.address());
      }

      public float strokeDashArray(int index) {
         return NSVGShape.nstrokeDashArray(this.address(), index);
      }

      @NativeType("char")
      public byte strokeDashCount() {
         return NSVGShape.nstrokeDashCount(this.address());
      }

      @NativeType("char")
      public byte strokeLineJoin() {
         return NSVGShape.nstrokeLineJoin(this.address());
      }

      @NativeType("char")
      public byte strokeLineCap() {
         return NSVGShape.nstrokeLineCap(this.address());
      }

      public float miterLimit() {
         return NSVGShape.nmiterLimit(this.address());
      }

      @NativeType("char")
      public byte fillRule() {
         return NSVGShape.nfillRule(this.address());
      }

      @NativeType("unsigned char")
      public byte flags() {
         return NSVGShape.nflags(this.address());
      }

      @NativeType("float[4]")
      public FloatBuffer bounds() {
         return NSVGShape.nbounds(this.address());
      }

      public float bounds(int index) {
         return NSVGShape.nbounds(this.address(), index);
      }

      @NativeType("char[64]")
      public ByteBuffer fillGradient() {
         return NSVGShape.nfillGradient(this.address());
      }

      @NativeType("char[64]")
      public String fillGradientString() {
         return NSVGShape.nfillGradientString(this.address());
      }

      @NativeType("char[64]")
      public ByteBuffer strokeGradient() {
         return NSVGShape.nstrokeGradient(this.address());
      }

      @NativeType("char[64]")
      public String strokeGradientString() {
         return NSVGShape.nstrokeGradientString(this.address());
      }

      @NativeType("float[6]")
      public FloatBuffer xform() {
         return NSVGShape.nxform(this.address());
      }

      public float xform(int index) {
         return NSVGShape.nxform(this.address(), index);
      }

      @NativeType("NSVGpath *")
      public NSVGPath paths() {
         return NSVGShape.npaths(this.address());
      }

      @NativeType("NSVGshape *")
      public NSVGShape next() {
         return NSVGShape.nnext(this.address());
      }
   }
}
