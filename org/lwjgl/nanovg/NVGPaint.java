package org.lwjgl.nanovg;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.Checks;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.system.Struct.Layout;
import org.lwjgl.system.Struct.Member;

@NativeType("struct NVGpaint")
public class NVGPaint extends Struct<NVGPaint> implements NativeResource {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int XFORM;
   public static final int EXTENT;
   public static final int RADIUS;
   public static final int FEATHER;
   public static final int INNERCOLOR;
   public static final int OUTERCOLOR;
   public static final int IMAGE;

   protected NVGPaint(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected NVGPaint create(long address, @Nullable ByteBuffer container) {
      return new NVGPaint(address, container);
   }

   public NVGPaint(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   @NativeType("float[6]")
   public FloatBuffer xform() {
      return nxform(this.address());
   }

   public float xform(int index) {
      return nxform(this.address(), index);
   }

   @NativeType("float[2]")
   public FloatBuffer extent() {
      return nextent(this.address());
   }

   public float extent(int index) {
      return nextent(this.address(), index);
   }

   public float radius() {
      return nradius(this.address());
   }

   public float feather() {
      return nfeather(this.address());
   }

   @NativeType("NVGcolor")
   public NVGColor innerColor() {
      return ninnerColor(this.address());
   }

   @NativeType("NVGcolor")
   public NVGColor outerColor() {
      return nouterColor(this.address());
   }

   public int image() {
      return nimage(this.address());
   }

   public NVGPaint xform(@NativeType("float[6]") FloatBuffer value) {
      nxform(this.address(), value);
      return this;
   }

   public NVGPaint xform(int index, float value) {
      nxform(this.address(), index, value);
      return this;
   }

   public NVGPaint extent(@NativeType("float[2]") FloatBuffer value) {
      nextent(this.address(), value);
      return this;
   }

   public NVGPaint extent(int index, float value) {
      nextent(this.address(), index, value);
      return this;
   }

   public NVGPaint radius(float value) {
      nradius(this.address(), value);
      return this;
   }

   public NVGPaint feather(float value) {
      nfeather(this.address(), value);
      return this;
   }

   public NVGPaint innerColor(@NativeType("NVGcolor") NVGColor value) {
      ninnerColor(this.address(), value);
      return this;
   }

   public NVGPaint innerColor(Consumer<NVGColor> consumer) {
      consumer.accept(this.innerColor());
      return this;
   }

   public NVGPaint outerColor(@NativeType("NVGcolor") NVGColor value) {
      nouterColor(this.address(), value);
      return this;
   }

   public NVGPaint outerColor(Consumer<NVGColor> consumer) {
      consumer.accept(this.outerColor());
      return this;
   }

   public NVGPaint image(int value) {
      nimage(this.address(), value);
      return this;
   }

   public NVGPaint set(FloatBuffer xform, FloatBuffer extent, float radius, float feather, NVGColor innerColor, NVGColor outerColor, int image) {
      this.xform(xform);
      this.extent(extent);
      this.radius(radius);
      this.feather(feather);
      this.innerColor(innerColor);
      this.outerColor(outerColor);
      this.image(image);
      return this;
   }

   public NVGPaint set(NVGPaint src) {
      MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
      return this;
   }

   public static NVGPaint malloc() {
      return new NVGPaint(MemoryUtil.nmemAllocChecked(SIZEOF), null);
   }

   public static NVGPaint calloc() {
      return new NVGPaint(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
   }

   public static NVGPaint create() {
      ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
      return new NVGPaint(MemoryUtil.memAddress(container), container);
   }

   public static NVGPaint create(long address) {
      return new NVGPaint(address, null);
   }

   @Nullable
   public static NVGPaint createSafe(long address) {
      return address == 0L ? null : new NVGPaint(address, null);
   }

   public static NVGPaint.Buffer malloc(int capacity) {
      return new NVGPaint.Buffer(MemoryUtil.nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
   }

   public static NVGPaint.Buffer calloc(int capacity) {
      return new NVGPaint.Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
   }

   public static NVGPaint.Buffer create(int capacity) {
      ByteBuffer container = __create(capacity, SIZEOF);
      return new NVGPaint.Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
   }

   public static NVGPaint.Buffer create(long address, int capacity) {
      return new NVGPaint.Buffer(address, capacity);
   }

   @Nullable
   public static NVGPaint.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new NVGPaint.Buffer(address, capacity);
   }

   @Deprecated
   public static NVGPaint mallocStack() {
      return malloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGPaint callocStack() {
      return calloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGPaint mallocStack(MemoryStack stack) {
      return malloc(stack);
   }

   @Deprecated
   public static NVGPaint callocStack(MemoryStack stack) {
      return calloc(stack);
   }

   @Deprecated
   public static NVGPaint.Buffer mallocStack(int capacity) {
      return malloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGPaint.Buffer callocStack(int capacity) {
      return calloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGPaint.Buffer mallocStack(int capacity, MemoryStack stack) {
      return malloc(capacity, stack);
   }

   @Deprecated
   public static NVGPaint.Buffer callocStack(int capacity, MemoryStack stack) {
      return calloc(capacity, stack);
   }

   public static NVGPaint malloc(MemoryStack stack) {
      return new NVGPaint(stack.nmalloc(ALIGNOF, SIZEOF), null);
   }

   public static NVGPaint calloc(MemoryStack stack) {
      return new NVGPaint(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
   }

   public static NVGPaint.Buffer malloc(int capacity, MemoryStack stack) {
      return new NVGPaint.Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
   }

   public static NVGPaint.Buffer calloc(int capacity, MemoryStack stack) {
      return new NVGPaint.Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
   }

   public static FloatBuffer nxform(long struct) {
      return MemoryUtil.memFloatBuffer(struct + XFORM, 6);
   }

   public static float nxform(long struct, int index) {
      return UNSAFE.getFloat(null, struct + XFORM + Checks.check(index, 6) * 4L);
   }

   public static FloatBuffer nextent(long struct) {
      return MemoryUtil.memFloatBuffer(struct + EXTENT, 2);
   }

   public static float nextent(long struct, int index) {
      return UNSAFE.getFloat(null, struct + EXTENT + Checks.check(index, 2) * 4L);
   }

   public static float nradius(long struct) {
      return UNSAFE.getFloat(null, struct + RADIUS);
   }

   public static float nfeather(long struct) {
      return UNSAFE.getFloat(null, struct + FEATHER);
   }

   public static NVGColor ninnerColor(long struct) {
      return NVGColor.create(struct + INNERCOLOR);
   }

   public static NVGColor nouterColor(long struct) {
      return NVGColor.create(struct + OUTERCOLOR);
   }

   public static int nimage(long struct) {
      return UNSAFE.getInt(null, struct + IMAGE);
   }

   public static void nxform(long struct, FloatBuffer value) {
      if (Checks.CHECKS) {
         Checks.checkGT(value, 6);
      }

      MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + XFORM, value.remaining() * 4);
   }

   public static void nxform(long struct, int index, float value) {
      UNSAFE.putFloat(null, struct + XFORM + Checks.check(index, 6) * 4L, value);
   }

   public static void nextent(long struct, FloatBuffer value) {
      if (Checks.CHECKS) {
         Checks.checkGT(value, 2);
      }

      MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + EXTENT, value.remaining() * 4);
   }

   public static void nextent(long struct, int index, float value) {
      UNSAFE.putFloat(null, struct + EXTENT + Checks.check(index, 2) * 4L, value);
   }

   public static void nradius(long struct, float value) {
      UNSAFE.putFloat(null, struct + RADIUS, value);
   }

   public static void nfeather(long struct, float value) {
      UNSAFE.putFloat(null, struct + FEATHER, value);
   }

   public static void ninnerColor(long struct, NVGColor value) {
      MemoryUtil.memCopy(value.address(), struct + INNERCOLOR, NVGColor.SIZEOF);
   }

   public static void nouterColor(long struct, NVGColor value) {
      MemoryUtil.memCopy(value.address(), struct + OUTERCOLOR, NVGColor.SIZEOF);
   }

   public static void nimage(long struct, int value) {
      UNSAFE.putInt(null, struct + IMAGE, value);
   }

   static {
      Layout layout = __struct(
         new Member[]{
            __array(4, 6),
            __array(4, 2),
            __member(4),
            __member(4),
            __member(NVGColor.SIZEOF, NVGColor.ALIGNOF),
            __member(NVGColor.SIZEOF, NVGColor.ALIGNOF),
            __member(4)
         }
      );
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      XFORM = layout.offsetof(0);
      EXTENT = layout.offsetof(1);
      RADIUS = layout.offsetof(2);
      FEATHER = layout.offsetof(3);
      INNERCOLOR = layout.offsetof(4);
      OUTERCOLOR = layout.offsetof(5);
      IMAGE = layout.offsetof(6);
   }

   public static class Buffer extends StructBuffer<NVGPaint, NVGPaint.Buffer> implements NativeResource {
      private static final NVGPaint ELEMENT_FACTORY = NVGPaint.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / NVGPaint.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected NVGPaint.Buffer self() {
         return this;
      }

      protected NVGPaint getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("float[6]")
      public FloatBuffer xform() {
         return NVGPaint.nxform(this.address());
      }

      public float xform(int index) {
         return NVGPaint.nxform(this.address(), index);
      }

      @NativeType("float[2]")
      public FloatBuffer extent() {
         return NVGPaint.nextent(this.address());
      }

      public float extent(int index) {
         return NVGPaint.nextent(this.address(), index);
      }

      public float radius() {
         return NVGPaint.nradius(this.address());
      }

      public float feather() {
         return NVGPaint.nfeather(this.address());
      }

      @NativeType("NVGcolor")
      public NVGColor innerColor() {
         return NVGPaint.ninnerColor(this.address());
      }

      @NativeType("NVGcolor")
      public NVGColor outerColor() {
         return NVGPaint.nouterColor(this.address());
      }

      public int image() {
         return NVGPaint.nimage(this.address());
      }

      public NVGPaint.Buffer xform(@NativeType("float[6]") FloatBuffer value) {
         NVGPaint.nxform(this.address(), value);
         return this;
      }

      public NVGPaint.Buffer xform(int index, float value) {
         NVGPaint.nxform(this.address(), index, value);
         return this;
      }

      public NVGPaint.Buffer extent(@NativeType("float[2]") FloatBuffer value) {
         NVGPaint.nextent(this.address(), value);
         return this;
      }

      public NVGPaint.Buffer extent(int index, float value) {
         NVGPaint.nextent(this.address(), index, value);
         return this;
      }

      public NVGPaint.Buffer radius(float value) {
         NVGPaint.nradius(this.address(), value);
         return this;
      }

      public NVGPaint.Buffer feather(float value) {
         NVGPaint.nfeather(this.address(), value);
         return this;
      }

      public NVGPaint.Buffer innerColor(@NativeType("NVGcolor") NVGColor value) {
         NVGPaint.ninnerColor(this.address(), value);
         return this;
      }

      public NVGPaint.Buffer innerColor(Consumer<NVGColor> consumer) {
         consumer.accept(this.innerColor());
         return this;
      }

      public NVGPaint.Buffer outerColor(@NativeType("NVGcolor") NVGColor value) {
         NVGPaint.nouterColor(this.address(), value);
         return this;
      }

      public NVGPaint.Buffer outerColor(Consumer<NVGColor> consumer) {
         consumer.accept(this.outerColor());
         return this;
      }

      public NVGPaint.Buffer image(int value) {
         NVGPaint.nimage(this.address(), value);
         return this;
      }
   }
}
