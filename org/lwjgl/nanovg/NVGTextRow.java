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

@NativeType("struct NVGtextRow")
public class NVGTextRow extends Struct<NVGTextRow> implements NativeResource {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int START;
   public static final int END;
   public static final int NEXT;
   public static final int WIDTH;
   public static final int MINX;
   public static final int MAXX;

   protected NVGTextRow(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected NVGTextRow create(long address, @Nullable ByteBuffer container) {
      return new NVGTextRow(address, container);
   }

   public NVGTextRow(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   @NativeType("char *")
   public long start() {
      return nstart(this.address());
   }

   @NativeType("char *")
   public long end() {
      return nend(this.address());
   }

   @NativeType("char *")
   public long next() {
      return nnext(this.address());
   }

   public float width() {
      return nwidth(this.address());
   }

   public float minx() {
      return nminx(this.address());
   }

   public float maxx() {
      return nmaxx(this.address());
   }

   public static NVGTextRow malloc() {
      return new NVGTextRow(MemoryUtil.nmemAllocChecked(SIZEOF), null);
   }

   public static NVGTextRow calloc() {
      return new NVGTextRow(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
   }

   public static NVGTextRow create() {
      ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
      return new NVGTextRow(MemoryUtil.memAddress(container), container);
   }

   public static NVGTextRow create(long address) {
      return new NVGTextRow(address, null);
   }

   @Nullable
   public static NVGTextRow createSafe(long address) {
      return address == 0L ? null : new NVGTextRow(address, null);
   }

   public static NVGTextRow.Buffer malloc(int capacity) {
      return new NVGTextRow.Buffer(MemoryUtil.nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
   }

   public static NVGTextRow.Buffer calloc(int capacity) {
      return new NVGTextRow.Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
   }

   public static NVGTextRow.Buffer create(int capacity) {
      ByteBuffer container = __create(capacity, SIZEOF);
      return new NVGTextRow.Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
   }

   public static NVGTextRow.Buffer create(long address, int capacity) {
      return new NVGTextRow.Buffer(address, capacity);
   }

   @Nullable
   public static NVGTextRow.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new NVGTextRow.Buffer(address, capacity);
   }

   @Deprecated
   public static NVGTextRow mallocStack() {
      return malloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGTextRow callocStack() {
      return calloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGTextRow mallocStack(MemoryStack stack) {
      return malloc(stack);
   }

   @Deprecated
   public static NVGTextRow callocStack(MemoryStack stack) {
      return calloc(stack);
   }

   @Deprecated
   public static NVGTextRow.Buffer mallocStack(int capacity) {
      return malloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGTextRow.Buffer callocStack(int capacity) {
      return calloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static NVGTextRow.Buffer mallocStack(int capacity, MemoryStack stack) {
      return malloc(capacity, stack);
   }

   @Deprecated
   public static NVGTextRow.Buffer callocStack(int capacity, MemoryStack stack) {
      return calloc(capacity, stack);
   }

   public static NVGTextRow malloc(MemoryStack stack) {
      return new NVGTextRow(stack.nmalloc(ALIGNOF, SIZEOF), null);
   }

   public static NVGTextRow calloc(MemoryStack stack) {
      return new NVGTextRow(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
   }

   public static NVGTextRow.Buffer malloc(int capacity, MemoryStack stack) {
      return new NVGTextRow.Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
   }

   public static NVGTextRow.Buffer calloc(int capacity, MemoryStack stack) {
      return new NVGTextRow.Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
   }

   public static long nstart(long struct) {
      return MemoryUtil.memGetAddress(struct + START);
   }

   public static long nend(long struct) {
      return MemoryUtil.memGetAddress(struct + END);
   }

   public static long nnext(long struct) {
      return MemoryUtil.memGetAddress(struct + NEXT);
   }

   public static float nwidth(long struct) {
      return UNSAFE.getFloat(null, struct + WIDTH);
   }

   public static float nminx(long struct) {
      return UNSAFE.getFloat(null, struct + MINX);
   }

   public static float nmaxx(long struct) {
      return UNSAFE.getFloat(null, struct + MAXX);
   }

   static {
      Layout layout = __struct(new Member[]{__member(POINTER_SIZE), __member(POINTER_SIZE), __member(POINTER_SIZE), __member(4), __member(4), __member(4)});
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      START = layout.offsetof(0);
      END = layout.offsetof(1);
      NEXT = layout.offsetof(2);
      WIDTH = layout.offsetof(3);
      MINX = layout.offsetof(4);
      MAXX = layout.offsetof(5);
   }

   public static class Buffer extends StructBuffer<NVGTextRow, NVGTextRow.Buffer> implements NativeResource {
      private static final NVGTextRow ELEMENT_FACTORY = NVGTextRow.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / NVGTextRow.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected NVGTextRow.Buffer self() {
         return this;
      }

      protected NVGTextRow getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("char *")
      public long start() {
         return NVGTextRow.nstart(this.address());
      }

      @NativeType("char *")
      public long end() {
         return NVGTextRow.nend(this.address());
      }

      @NativeType("char *")
      public long next() {
         return NVGTextRow.nnext(this.address());
      }

      public float width() {
         return NVGTextRow.nwidth(this.address());
      }

      public float minx() {
         return NVGTextRow.nminx(this.address());
      }

      public float maxx() {
         return NVGTextRow.nmaxx(this.address());
      }
   }
}
