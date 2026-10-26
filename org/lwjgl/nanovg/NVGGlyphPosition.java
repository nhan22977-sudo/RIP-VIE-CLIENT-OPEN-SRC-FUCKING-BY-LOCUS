package org.lwjgl.nanovg;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeResource;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.system.Struct.Layout;
import org.lwjgl.system.Struct.Member;

@NativeType("struct NVGglyphPosition")
public class NVGGlyphPosition extends Struct<NVGGlyphPosition> implements NativeResource {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int STR;
   public static final int X;
   public static final int MINX;
   public static final int MAXX;

   protected NVGGlyphPosition(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected NVGGlyphPosition create(long address, @Nullable ByteBuffer container) {
      return new NVGGlyphPosition(address, container);
   }

   public NVGGlyphPosition(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   @NativeType("char *")
   public long str() {
      return nstr(this.address());
   }

   public float x() {
      return nx(this.address());
   }

   public float minx() {
      return nminx(this.address());
   }

   public float maxx() {
      return nmaxx(this.address());
   }

   public static NVGGlyphPosition malloc() {
      return new NVGGlyphPosition(MemoryUtil.nmemAllocChecked(SIZEOF), null);
   }

   public static NVGGlyphPosition calloc() {
      return new NVGGlyphPosition(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
   }

   public static NVGGlyphPosition create() {
      ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
      return new NVGGlyphPosition(MemoryUtil.memAddress(container), container);
   }

   public static NVGGlyphPosition create(long address) {
      return new NVGGlyphPosition(address, null);
   }

   @Nullable
   public static NVGGlyphPosition createSafe(long address) {
      return address == 0L ? null : new NVGGlyphPosition(address, null);
   }

   public static NVGGlyphPosition.Buffer malloc(int capacity) {
      return new NVGGlyphPosition.Buffer(MemoryUtil.nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
   }

   public static NVGGlyphPosition.Buffer calloc(int capacity) {
      return new NVGGlyphPosition.Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
   }

   public static NVGGlyphPosition.Buffer create(int capacity) {
      ByteBuffer container = __create(capacity, SIZEOF);
      return new NVGGlyphPosition.Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
   }

   public static NVGGlyphPosition.Buffer create(long address, int capacity) {
      return new NVGGlyphPosition.Buffer(address, capacity);
   }

   @Nullable
   public static NVGGlyphPosition.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new NVGGlyphPosition.Buffer(address, capacity);
   }

   @Deprecated
   public static NVGGlyphPosition mallocStack() {
      return malloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGGlyphPosition callocStack() {
      return calloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGGlyphPosition mallocStack(MemoryStack stack) {
      return malloc(stack);
   }

   @Deprecated
   public static NVGGlyphPosition callocStack(MemoryStack stack) {
      return calloc(stack);
   }

   @Deprecated
   public static NVGGlyphPosition.Buffer mallocStack(int capacity) {
      return malloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGGlyphPosition.Buffer callocStack(int capacity) {
      return calloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGGlyphPosition.Buffer mallocStack(int capacity, MemoryStack stack) {
      return malloc(capacity, stack);
   }

   @Deprecated
   public static NVGGlyphPosition.Buffer callocStack(int capacity, MemoryStack stack) {
      return calloc(capacity, stack);
   }

   public static NVGGlyphPosition malloc(MemoryStack stack) {
      return new NVGGlyphPosition(stack.nmalloc(ALIGNOF, SIZEOF), null);
   }

   public static NVGGlyphPosition calloc(MemoryStack stack) {
      return new NVGGlyphPosition(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
   }

   public static NVGGlyphPosition.Buffer malloc(int capacity, MemoryStack stack) {
      return new NVGGlyphPosition.Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
   }

   public static NVGGlyphPosition.Buffer calloc(int capacity, MemoryStack stack) {
      return new NVGGlyphPosition.Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
   }

   public static long nstr(long struct) {
      return MemoryUtil.memGetAddress(struct + STR);
   }

   public static float nx(long struct) {
      return UNSAFE.getFloat(null, struct + X);
   }

   public static float nminx(long struct) {
      return UNSAFE.getFloat(null, struct + MINX);
   }

   public static float nmaxx(long struct) {
      return UNSAFE.getFloat(null, struct + MAXX);
   }

   static {
      Layout layout = __struct(new Member[]{__member(POINTER_SIZE), __member(4), __member(4), __member(4)});
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      STR = layout.offsetof(0);
      X = layout.offsetof(1);
      MINX = layout.offsetof(2);
      MAXX = layout.offsetof(3);
   }

   public static class Buffer extends StructBuffer<NVGGlyphPosition, NVGGlyphPosition.Buffer> implements NativeResource {
      private static final NVGGlyphPosition ELEMENT_FACTORY = NVGGlyphPosition.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / NVGGlyphPosition.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected NVGGlyphPosition.Buffer self() {
         return this;
      }

      protected NVGGlyphPosition getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("char *")
      public long str() {
         return NVGGlyphPosition.nstr(this.address());
      }

      public float x() {
         return NVGGlyphPosition.nx(this.address());
      }

      public float minx() {
         return NVGGlyphPosition.nminx(this.address());
      }

      public float maxx() {
         return NVGGlyphPosition.nmaxx(this.address());
      }
   }
}
