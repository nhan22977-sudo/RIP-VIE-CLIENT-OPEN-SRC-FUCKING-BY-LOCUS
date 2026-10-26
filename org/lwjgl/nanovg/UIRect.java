package org.lwjgl.nanovg;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
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

@NativeType("struct UIrect")
public class UIRect extends Struct<UIRect> implements NativeResource {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int V;
   public static final int X;
   public static final int Y;
   public static final int W;
   public static final int H;

   protected UIRect(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected UIRect create(long address, @Nullable ByteBuffer container) {
      return new UIRect(address, container);
   }

   public UIRect(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   @NativeType("int[4]")
   public IntBuffer v() {
      return nv(this.address());
   }

   public int v(int index) {
      return nv(this.address(), index);
   }

   public int x() {
      return nx(this.address());
   }

   public int y() {
      return ny(this.address());
   }

   public int w() {
      return nw(this.address());
   }

   public int h() {
      return nh(this.address());
   }

   public UIRect v(@NativeType("int[4]") IntBuffer value) {
      nv(this.address(), value);
      return this;
   }

   public UIRect v(int index, int value) {
      nv(this.address(), index, value);
      return this;
   }

   public UIRect x(int value) {
      nx(this.address(), value);
      return this;
   }

   public UIRect y(int value) {
      ny(this.address(), value);
      return this;
   }

   public UIRect w(int value) {
      nw(this.address(), value);
      return this;
   }

   public UIRect h(int value) {
      nh(this.address(), value);
      return this;
   }

   public UIRect set(UIRect src) {
      MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
      return this;
   }

   public static UIRect malloc() {
      return new UIRect(MemoryUtil.nmemAllocChecked(SIZEOF), null);
   }

   public static UIRect calloc() {
      return new UIRect(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
   }

   public static UIRect create() {
      ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
      return new UIRect(MemoryUtil.memAddress(container), container);
   }

   public static UIRect create(long address) {
      return new UIRect(address, null);
   }

   @Nullable
   public static UIRect createSafe(long address) {
      return address == 0L ? null : new UIRect(address, null);
   }

   public static UIRect.Buffer malloc(int capacity) {
      return new UIRect.Buffer(MemoryUtil.nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
   }

   public static UIRect.Buffer calloc(int capacity) {
      return new UIRect.Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
   }

   public static UIRect.Buffer create(int capacity) {
      ByteBuffer container = __create(capacity, SIZEOF);
      return new UIRect.Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
   }

   public static UIRect.Buffer create(long address, int capacity) {
      return new UIRect.Buffer(address, capacity);
   }

   @Nullable
   public static UIRect.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new UIRect.Buffer(address, capacity);
   }

   @Deprecated
   public static UIRect mallocStack() {
      return malloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static UIRect callocStack() {
      return calloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static UIRect mallocStack(MemoryStack stack) {
      return malloc(stack);
   }

   @Deprecated
   public static UIRect callocStack(MemoryStack stack) {
      return calloc(stack);
   }

   @Deprecated
   public static UIRect.Buffer mallocStack(int capacity) {
      return malloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static UIRect.Buffer callocStack(int capacity) {
      return calloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static UIRect.Buffer mallocStack(int capacity, MemoryStack stack) {
      return malloc(capacity, stack);
   }

   @Deprecated
   public static UIRect.Buffer callocStack(int capacity, MemoryStack stack) {
      return calloc(capacity, stack);
   }

   public static UIRect malloc(MemoryStack stack) {
      return new UIRect(stack.nmalloc(ALIGNOF, SIZEOF), null);
   }

   public static UIRect calloc(MemoryStack stack) {
      return new UIRect(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
   }

   public static UIRect.Buffer malloc(int capacity, MemoryStack stack) {
      return new UIRect.Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
   }

   public static UIRect.Buffer calloc(int capacity, MemoryStack stack) {
      return new UIRect.Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
   }

   public static IntBuffer nv(long struct) {
      return MemoryUtil.memIntBuffer(struct + V, 4);
   }

   public static int nv(long struct, int index) {
      return UNSAFE.getInt(null, struct + V + Checks.check(index, 4) * 4L);
   }

   public static int nx(long struct) {
      return UNSAFE.getInt(null, struct + X);
   }

   public static int ny(long struct) {
      return UNSAFE.getInt(null, struct + Y);
   }

   public static int nw(long struct) {
      return UNSAFE.getInt(null, struct + W);
   }

   public static int nh(long struct) {
      return UNSAFE.getInt(null, struct + H);
   }

   public static void nv(long struct, IntBuffer value) {
      if (Checks.CHECKS) {
         Checks.checkGT(value, 4);
      }

      MemoryUtil.memCopy(MemoryUtil.memAddress(value), struct + V, value.remaining() * 4);
   }

   public static void nv(long struct, int index, int value) {
      UNSAFE.putInt(null, struct + V + Checks.check(index, 4) * 4L, value);
   }

   public static void nx(long struct, int value) {
      UNSAFE.putInt(null, struct + X, value);
   }

   public static void ny(long struct, int value) {
      UNSAFE.putInt(null, struct + Y, value);
   }

   public static void nw(long struct, int value) {
      UNSAFE.putInt(null, struct + W, value);
   }

   public static void nh(long struct, int value) {
      UNSAFE.putInt(null, struct + H, value);
   }

   static {
      Layout layout = __struct(new Member[]{__union(new Member[]{__array(4, 4), __struct(new Member[]{__member(4), __member(4), __member(4), __member(4)})})});
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      V = layout.offsetof(1);
      X = layout.offsetof(3);
      Y = layout.offsetof(4);
      W = layout.offsetof(5);
      H = layout.offsetof(6);
   }

   public static class Buffer extends StructBuffer<UIRect, UIRect.Buffer> implements NativeResource {
      private static final UIRect ELEMENT_FACTORY = UIRect.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / UIRect.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected UIRect.Buffer self() {
         return this;
      }

      protected UIRect getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("int[4]")
      public IntBuffer v() {
         return UIRect.nv(this.address());
      }

      public int v(int index) {
         return UIRect.nv(this.address(), index);
      }

      public int x() {
         return UIRect.nx(this.address());
      }

      public int y() {
         return UIRect.ny(this.address());
      }

      public int w() {
         return UIRect.nw(this.address());
      }

      public int h() {
         return UIRect.nh(this.address());
      }

      public UIRect.Buffer v(@NativeType("int[4]") IntBuffer value) {
         UIRect.nv(this.address(), value);
         return this;
      }

      public UIRect.Buffer v(int index, int value) {
         UIRect.nv(this.address(), index, value);
         return this;
      }

      public UIRect.Buffer x(int value) {
         UIRect.nx(this.address(), value);
         return this;
      }

      public UIRect.Buffer y(int value) {
         UIRect.ny(this.address(), value);
         return this;
      }

      public UIRect.Buffer w(int value) {
         UIRect.nw(this.address(), value);
         return this;
      }

      public UIRect.Buffer h(int value) {
         UIRect.nh(this.address(), value);
         return this;
      }
   }
}
