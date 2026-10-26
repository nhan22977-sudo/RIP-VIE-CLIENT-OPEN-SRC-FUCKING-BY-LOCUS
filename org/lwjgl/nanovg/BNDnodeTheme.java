package org.lwjgl.nanovg;

import java.nio.ByteBuffer;
import java.util.function.Consumer;
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

public class BNDnodeTheme extends Struct<BNDnodeTheme> implements NativeResource {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int NODESELECTEDCOLOR;
   public static final int WIRESCOLOR;
   public static final int TEXTSELECTEDCOLOR;
   public static final int ACTIVENODECOLOR;
   public static final int WIRESELECTCOLOR;
   public static final int NODEBACKDROPCOLOR;
   public static final int NOODLECURVING;

   protected BNDnodeTheme(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected BNDnodeTheme create(long address, @Nullable ByteBuffer container) {
      return new BNDnodeTheme(address, container);
   }

   public BNDnodeTheme(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   @NativeType("NVGcolor")
   public NVGColor nodeSelectedColor() {
      return nnodeSelectedColor(this.address());
   }

   @NativeType("NVGcolor")
   public NVGColor wiresColor() {
      return nwiresColor(this.address());
   }

   @NativeType("NVGcolor")
   public NVGColor textSelectedColor() {
      return ntextSelectedColor(this.address());
   }

   @NativeType("NVGcolor")
   public NVGColor activeNodeColor() {
      return nactiveNodeColor(this.address());
   }

   @NativeType("NVGcolor")
   public NVGColor wireSelectColor() {
      return nwireSelectColor(this.address());
   }

   @NativeType("NVGcolor")
   public NVGColor nodeBackdropColor() {
      return nnodeBackdropColor(this.address());
   }

   public int noodleCurving() {
      return nnoodleCurving(this.address());
   }

   public BNDnodeTheme nodeSelectedColor(@NativeType("NVGcolor") NVGColor value) {
      nnodeSelectedColor(this.address(), value);
      return this;
   }

   public BNDnodeTheme nodeSelectedColor(Consumer<NVGColor> consumer) {
      consumer.accept(this.nodeSelectedColor());
      return this;
   }

   public BNDnodeTheme wiresColor(@NativeType("NVGcolor") NVGColor value) {
      nwiresColor(this.address(), value);
      return this;
   }

   public BNDnodeTheme wiresColor(Consumer<NVGColor> consumer) {
      consumer.accept(this.wiresColor());
      return this;
   }

   public BNDnodeTheme textSelectedColor(@NativeType("NVGcolor") NVGColor value) {
      ntextSelectedColor(this.address(), value);
      return this;
   }

   public BNDnodeTheme textSelectedColor(Consumer<NVGColor> consumer) {
      consumer.accept(this.textSelectedColor());
      return this;
   }

   public BNDnodeTheme activeNodeColor(@NativeType("NVGcolor") NVGColor value) {
      nactiveNodeColor(this.address(), value);
      return this;
   }

   public BNDnodeTheme activeNodeColor(Consumer<NVGColor> consumer) {
      consumer.accept(this.activeNodeColor());
      return this;
   }

   public BNDnodeTheme wireSelectColor(@NativeType("NVGcolor") NVGColor value) {
      nwireSelectColor(this.address(), value);
      return this;
   }

   public BNDnodeTheme wireSelectColor(Consumer<NVGColor> consumer) {
      consumer.accept(this.wireSelectColor());
      return this;
   }

   public BNDnodeTheme nodeBackdropColor(@NativeType("NVGcolor") NVGColor value) {
      nnodeBackdropColor(this.address(), value);
      return this;
   }

   public BNDnodeTheme nodeBackdropColor(Consumer<NVGColor> consumer) {
      consumer.accept(this.nodeBackdropColor());
      return this;
   }

   public BNDnodeTheme noodleCurving(int value) {
      nnoodleCurving(this.address(), value);
      return this;
   }

   public BNDnodeTheme set(
      NVGColor nodeSelectedColor,
      NVGColor wiresColor,
      NVGColor textSelectedColor,
      NVGColor activeNodeColor,
      NVGColor wireSelectColor,
      NVGColor nodeBackdropColor,
      int noodleCurving
   ) {
      this.nodeSelectedColor(nodeSelectedColor);
      this.wiresColor(wiresColor);
      this.textSelectedColor(textSelectedColor);
      this.activeNodeColor(activeNodeColor);
      this.wireSelectColor(wireSelectColor);
      this.nodeBackdropColor(nodeBackdropColor);
      this.noodleCurving(noodleCurving);
      return this;
   }

   public BNDnodeTheme set(BNDnodeTheme src) {
      MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
      return this;
   }

   public static BNDnodeTheme malloc() {
      return new BNDnodeTheme(MemoryUtil.nmemAllocChecked(SIZEOF), null);
   }

   public static BNDnodeTheme calloc() {
      return new BNDnodeTheme(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
   }

   public static BNDnodeTheme create() {
      ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
      return new BNDnodeTheme(MemoryUtil.memAddress(container), container);
   }

   public static BNDnodeTheme create(long address) {
      return new BNDnodeTheme(address, null);
   }

   @Nullable
   public static BNDnodeTheme createSafe(long address) {
      return address == 0L ? null : new BNDnodeTheme(address, null);
   }

   public static BNDnodeTheme.Buffer malloc(int capacity) {
      return new BNDnodeTheme.Buffer(MemoryUtil.nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
   }

   public static BNDnodeTheme.Buffer calloc(int capacity) {
      return new BNDnodeTheme.Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
   }

   public static BNDnodeTheme.Buffer create(int capacity) {
      ByteBuffer container = __create(capacity, SIZEOF);
      return new BNDnodeTheme.Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
   }

   public static BNDnodeTheme.Buffer create(long address, int capacity) {
      return new BNDnodeTheme.Buffer(address, capacity);
   }

   @Nullable
   public static BNDnodeTheme.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new BNDnodeTheme.Buffer(address, capacity);
   }

   @Deprecated
   public static BNDnodeTheme mallocStack() {
      return malloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static BNDnodeTheme callocStack() {
      return calloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static BNDnodeTheme mallocStack(MemoryStack stack) {
      return malloc(stack);
   }

   @Deprecated
   public static BNDnodeTheme callocStack(MemoryStack stack) {
      return calloc(stack);
   }

   @Deprecated
   public static BNDnodeTheme.Buffer mallocStack(int capacity) {
      return malloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static BNDnodeTheme.Buffer callocStack(int capacity) {
      return calloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static BNDnodeTheme.Buffer mallocStack(int capacity, MemoryStack stack) {
      return malloc(capacity, stack);
   }

   @Deprecated
   public static BNDnodeTheme.Buffer callocStack(int capacity, MemoryStack stack) {
      return calloc(capacity, stack);
   }

   public static BNDnodeTheme malloc(MemoryStack stack) {
      return new BNDnodeTheme(stack.nmalloc(ALIGNOF, SIZEOF), null);
   }

   public static BNDnodeTheme calloc(MemoryStack stack) {
      return new BNDnodeTheme(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
   }

   public static BNDnodeTheme.Buffer malloc(int capacity, MemoryStack stack) {
      return new BNDnodeTheme.Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
   }

   public static BNDnodeTheme.Buffer calloc(int capacity, MemoryStack stack) {
      return new BNDnodeTheme.Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
   }

   public static NVGColor nnodeSelectedColor(long struct) {
      return NVGColor.create(struct + NODESELECTEDCOLOR);
   }

   public static NVGColor nwiresColor(long struct) {
      return NVGColor.create(struct + WIRESCOLOR);
   }

   public static NVGColor ntextSelectedColor(long struct) {
      return NVGColor.create(struct + TEXTSELECTEDCOLOR);
   }

   public static NVGColor nactiveNodeColor(long struct) {
      return NVGColor.create(struct + ACTIVENODECOLOR);
   }

   public static NVGColor nwireSelectColor(long struct) {
      return NVGColor.create(struct + WIRESELECTCOLOR);
   }

   public static NVGColor nnodeBackdropColor(long struct) {
      return NVGColor.create(struct + NODEBACKDROPCOLOR);
   }

   public static int nnoodleCurving(long struct) {
      return UNSAFE.getInt(null, struct + NOODLECURVING);
   }

   public static void nnodeSelectedColor(long struct, NVGColor value) {
      MemoryUtil.memCopy(value.address(), struct + NODESELECTEDCOLOR, NVGColor.SIZEOF);
   }

   public static void nwiresColor(long struct, NVGColor value) {
      MemoryUtil.memCopy(value.address(), struct + WIRESCOLOR, NVGColor.SIZEOF);
   }

   public static void ntextSelectedColor(long struct, NVGColor value) {
      MemoryUtil.memCopy(value.address(), struct + TEXTSELECTEDCOLOR, NVGColor.SIZEOF);
   }

   public static void nactiveNodeColor(long struct, NVGColor value) {
      MemoryUtil.memCopy(value.address(), struct + ACTIVENODECOLOR, NVGColor.SIZEOF);
   }

   public static void nwireSelectColor(long struct, NVGColor value) {
      MemoryUtil.memCopy(value.address(), struct + WIRESELECTCOLOR, NVGColor.SIZEOF);
   }

   public static void nnodeBackdropColor(long struct, NVGColor value) {
      MemoryUtil.memCopy(value.address(), struct + NODEBACKDROPCOLOR, NVGColor.SIZEOF);
   }

   public static void nnoodleCurving(long struct, int value) {
      UNSAFE.putInt(null, struct + NOODLECURVING, value);
   }

   static {
      Layout layout = __struct(
         new Member[]{
            __member(NVGColor.SIZEOF, NVGColor.ALIGNOF),
            __member(NVGColor.SIZEOF, NVGColor.ALIGNOF),
            __member(NVGColor.SIZEOF, NVGColor.ALIGNOF),
            __member(NVGColor.SIZEOF, NVGColor.ALIGNOF),
            __member(NVGColor.SIZEOF, NVGColor.ALIGNOF),
            __member(NVGColor.SIZEOF, NVGColor.ALIGNOF),
            __member(4)
         }
      );
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      NODESELECTEDCOLOR = layout.offsetof(0);
      WIRESCOLOR = layout.offsetof(1);
      TEXTSELECTEDCOLOR = layout.offsetof(2);
      ACTIVENODECOLOR = layout.offsetof(3);
      WIRESELECTCOLOR = layout.offsetof(4);
      NODEBACKDROPCOLOR = layout.offsetof(5);
      NOODLECURVING = layout.offsetof(6);
   }

   public static class Buffer extends StructBuffer<BNDnodeTheme, BNDnodeTheme.Buffer> implements NativeResource {
      private static final BNDnodeTheme ELEMENT_FACTORY = BNDnodeTheme.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / BNDnodeTheme.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected BNDnodeTheme.Buffer self() {
         return this;
      }

      protected BNDnodeTheme getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("NVGcolor")
      public NVGColor nodeSelectedColor() {
         return BNDnodeTheme.nnodeSelectedColor(this.address());
      }

      @NativeType("NVGcolor")
      public NVGColor wiresColor() {
         return BNDnodeTheme.nwiresColor(this.address());
      }

      @NativeType("NVGcolor")
      public NVGColor textSelectedColor() {
         return BNDnodeTheme.ntextSelectedColor(this.address());
      }

      @NativeType("NVGcolor")
      public NVGColor activeNodeColor() {
         return BNDnodeTheme.nactiveNodeColor(this.address());
      }

      @NativeType("NVGcolor")
      public NVGColor wireSelectColor() {
         return BNDnodeTheme.nwireSelectColor(this.address());
      }

      @NativeType("NVGcolor")
      public NVGColor nodeBackdropColor() {
         return BNDnodeTheme.nnodeBackdropColor(this.address());
      }

      public int noodleCurving() {
         return BNDnodeTheme.nnoodleCurving(this.address());
      }

      public BNDnodeTheme.Buffer nodeSelectedColor(@NativeType("NVGcolor") NVGColor value) {
         BNDnodeTheme.nnodeSelectedColor(this.address(), value);
         return this;
      }

      public BNDnodeTheme.Buffer nodeSelectedColor(Consumer<NVGColor> consumer) {
         consumer.accept(this.nodeSelectedColor());
         return this;
      }

      public BNDnodeTheme.Buffer wiresColor(@NativeType("NVGcolor") NVGColor value) {
         BNDnodeTheme.nwiresColor(this.address(), value);
         return this;
      }

      public BNDnodeTheme.Buffer wiresColor(Consumer<NVGColor> consumer) {
         consumer.accept(this.wiresColor());
         return this;
      }

      public BNDnodeTheme.Buffer textSelectedColor(@NativeType("NVGcolor") NVGColor value) {
         BNDnodeTheme.ntextSelectedColor(this.address(), value);
         return this;
      }

      public BNDnodeTheme.Buffer textSelectedColor(Consumer<NVGColor> consumer) {
         consumer.accept(this.textSelectedColor());
         return this;
      }

      public BNDnodeTheme.Buffer activeNodeColor(@NativeType("NVGcolor") NVGColor value) {
         BNDnodeTheme.nactiveNodeColor(this.address(), value);
         return this;
      }

      public BNDnodeTheme.Buffer activeNodeColor(Consumer<NVGColor> consumer) {
         consumer.accept(this.activeNodeColor());
         return this;
      }

      public BNDnodeTheme.Buffer wireSelectColor(@NativeType("NVGcolor") NVGColor value) {
         BNDnodeTheme.nwireSelectColor(this.address(), value);
         return this;
      }

      public BNDnodeTheme.Buffer wireSelectColor(Consumer<NVGColor> consumer) {
         consumer.accept(this.wireSelectColor());
         return this;
      }

      public BNDnodeTheme.Buffer nodeBackdropColor(@NativeType("NVGcolor") NVGColor value) {
         BNDnodeTheme.nnodeBackdropColor(this.address(), value);
         return this;
      }

      public BNDnodeTheme.Buffer nodeBackdropColor(Consumer<NVGColor> consumer) {
         consumer.accept(this.nodeBackdropColor());
         return this;
      }

      public BNDnodeTheme.Buffer noodleCurving(int value) {
         BNDnodeTheme.nnoodleCurving(this.address(), value);
         return this;
      }
   }
}
