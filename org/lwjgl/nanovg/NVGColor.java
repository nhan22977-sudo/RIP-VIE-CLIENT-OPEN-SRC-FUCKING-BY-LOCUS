package org.lwjgl.nanovg;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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

@NativeType("struct NVGcolor")
public class NVGColor extends Struct<NVGColor> implements NativeResource {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int RGBA;
   public static final int R;
   public static final int G;
   public static final int B;
   public static final int A;

   protected NVGColor(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected NVGColor create(long address, @Nullable ByteBuffer container) {
      return new NVGColor(address, container);
   }

   public NVGColor(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   @NativeType("float[4]")
   public FloatBuffer rgba() {
      return nrgba(this.address());
   }

   public float rgba(int index) {
      return nrgba(this.address(), index);
   }

   public float r() {
      return nr(this.address());
   }

   public float g() {
      return ng(this.address());
   }

   public float b() {
      return nb(this.address());
   }

   public float a() {
      return na(this.address());
   }

   public NVGColor rgba(@NativeType("float[4]") FloatBuffer value) {
      nrgba(this.address(), value);
      return this;
   }

   public NVGColor rgba(int index, float value) {
      nrgba(this.address(), index, value);
      return this;
   }

   public NVGColor r(float value) {
      nr(this.address(), value);
      return this;
   }

   public NVGColor g(float value) {
      ng(this.address(), value);
      return this;
   }

   public NVGColor b(float value) {
      nb(this.address(), value);
      return this;
   }

   public NVGColor a(float value) {
      na(this.address(), value);
      return this;
   }

   public NVGColor set(NVGColor src) {
      MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
      return this;
   }

   public static NVGColor malloc() {
      return new NVGColor(MemoryUtil.nmemAllocChecked(SIZEOF), null);
   }

   public static NVGColor calloc() {
      return new NVGColor(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
   }

   public static NVGColor create() {
      ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
      return new NVGColor(MemoryUtil.memAddress(container), container);
   }

   public static NVGColor create(long address) {
      return new NVGColor(address, null);
   }

   @Nullable
   public static NVGColor createSafe(long address) {
      return address == 0L ? null : new NVGColor(address, null);
   }

   public static NVGColor.Buffer malloc(int capacity) {
      return new NVGColor.Buffer(MemoryUtil.nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
   }

   public static NVGColor.Buffer calloc(int capacity) {
      return new NVGColor.Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
   }

   public static NVGColor.Buffer create(int capacity) {
      ByteBuffer container = __create(capacity, SIZEOF);
      return new NVGColor.Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
   }

   public static NVGColor.Buffer create(long address, int capacity) {
      return new NVGColor.Buffer(address, capacity);
   }

   @Nullable
   public static NVGColor.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new NVGColor.Buffer(address, capacity);
   }

   @Deprecated
   public static NVGColor mallocStack() {
      return malloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGColor callocStack() {
      return calloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGColor mallocStack(MemoryStack stack) {
      return malloc(stack);
   }

   @Deprecated
   public static NVGColor callocStack(MemoryStack stack) {
      return calloc(stack);
   }

   @Deprecated
   public static NVGColor.Buffer mallocStack(int capacity) {
      return malloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGColor.Buffer callocStack(int capacity) {
      return calloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGColor.Buffer mallocStack(int capacity, MemoryStack stack) {
      return malloc(capacity, stack);
   }

   @Deprecated
   public static NVGColor.Buffer callocStack(int capacity, MemoryStack stack) {
      return calloc(capacity, stack);
   }

   public static NVGColor malloc(MemoryStack stack) {
      return new NVGColor(stack.nmalloc(ALIGNOF, SIZEOF), null);
   }

   public static NVGColor calloc(MemoryStack stack) {
      return new NVGColor(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
   }

   public static NVGColor.Buffer malloc(int capacity, MemoryStack stack) {
      return new NVGColor.Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
   }

   public static NVGColor.Buffer calloc(int capacity, MemoryStack stack) {
      return new NVGColor.Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
   }

   public static FloatBuffer nrgba(long struct) {
      return MemoryUtil.memFloatBuffer(struct + RGBA, 4);
   }

   public static float nrgba(long struct, int index) {
      return UNSAFE.getFloat(null, struct + RGBA + Checks.check(index, 4) * 4L);
   }

   public static float nr(long struct) {
      return UNSAFE.getFloat(null, struct + R);
   }

   public static float ng(long struct) {
      return UNSAFE.getFloat(null, struct + G);
   }

   public static float nb(long struct) {
      return UNSAFE.getFloat(null, struct + B);
   }

   public static float na(long struct) {
      return UNSAFE.getFloat(null, struct + A);
   }

   public static void nrgba(long struct, FloatBuffer value) {
      if (Checks.CHECKS) {
         Checks.checkGT(value, 4);
      }

      MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + RGBA, value.remaining() * 4);
   }

   public static void nrgba(long struct, int index, float value) {
      UNSAFE.putFloat(null, struct + RGBA + Checks.check(index, 4) * 4L, value);
   }

   public static void nr(long struct, float value) {
      UNSAFE.putFloat(null, struct + R, value);
   }

   public static void ng(long struct, float value) {
      UNSAFE.putFloat(null, struct + G, value);
   }

   public static void nb(long struct, float value) {
      UNSAFE.putFloat(null, struct + B, value);
   }

   public static void na(long struct, float value) {
      UNSAFE.putFloat(null, struct + A, value);
   }

   static {
      Layout layout = __struct(new Member[]{__union(new Member[]{__array(4, 4), __struct(new Member[]{__member(4), __member(4), __member(4), __member(4)})})});
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      RGBA = layout.offsetof(1);
      R = layout.offsetof(3);
      G = layout.offsetof(4);
      B = layout.offsetof(5);
      A = layout.offsetof(6);
   }

   public static class Buffer extends StructBuffer<NVGColor, NVGColor.Buffer> implements NativeResource {
      private static final NVGColor ELEMENT_FACTORY = NVGColor.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / NVGColor.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected NVGColor.Buffer self() {
         return this;
      }

      protected NVGColor getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("float[4]")
      public FloatBuffer rgba() {
         return NVGColor.nrgba(this.address());
      }

      public float rgba(int index) {
         return NVGColor.nrgba(this.address(), index);
      }

      public float r() {
         return NVGColor.nr(this.address());
      }

      public float g() {
         return NVGColor.ng(this.address());
      }

      public float b() {
         return NVGColor.nb(this.address());
      }

      public float a() {
         return NVGColor.na(this.address());
      }

      public NVGColor.Buffer rgba(@NativeType("float[4]") FloatBuffer value) {
         NVGColor.nrgba(this.address(), value);
         return this;
      }

      public NVGColor.Buffer rgba(int index, float value) {
         NVGColor.nrgba(this.address(), index, value);
         return this;
      }

      public NVGColor.Buffer r(float value) {
         NVGColor.nr(this.address(), value);
         return this;
      }

      public NVGColor.Buffer g(float value) {
         NVGColor.ng(this.address(), value);
         return this;
      }

      public NVGColor.Buffer b(float value) {
         NVGColor.nb(this.address(), value);
         return this;
      }

      public NVGColor.Buffer a(float value) {
         NVGColor.na(this.address(), value);
         return this;
      }
   }
}
