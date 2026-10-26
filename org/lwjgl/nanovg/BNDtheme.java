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

public class BNDtheme extends Struct<BNDtheme> implements NativeResource {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int BACKGROUNDCOLOR;
   public static final int REGULARTHEME;
   public static final int TOOLTHEME;
   public static final int RADIOTHEME;
   public static final int TEXTFIELDTHEME;
   public static final int OPTIONTHEME;
   public static final int CHOICETHEME;
   public static final int NUMBERFIELDTHEME;
   public static final int SLIDERTHEME;
   public static final int SCROLLBARTHEME;
   public static final int TOOLTIPTHEME;
   public static final int MENUTHEME;
   public static final int MENUITEMTHEME;
   public static final int NODETHEME;

   protected BNDtheme(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected BNDtheme create(long address, @Nullable ByteBuffer container) {
      return new BNDtheme(address, container);
   }

   public BNDtheme(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   @NativeType("NVGcolor")
   public NVGColor backgroundColor() {
      return nbackgroundColor(this.address());
   }

   public BNDwidgetTheme regularTheme() {
      return nregularTheme(this.address());
   }

   public BNDwidgetTheme toolTheme() {
      return ntoolTheme(this.address());
   }

   public BNDwidgetTheme radioTheme() {
      return nradioTheme(this.address());
   }

   public BNDwidgetTheme textFieldTheme() {
      return ntextFieldTheme(this.address());
   }

   public BNDwidgetTheme optionTheme() {
      return noptionTheme(this.address());
   }

   public BNDwidgetTheme choiceTheme() {
      return nchoiceTheme(this.address());
   }

   public BNDwidgetTheme numberFieldTheme() {
      return nnumberFieldTheme(this.address());
   }

   public BNDwidgetTheme sliderTheme() {
      return nsliderTheme(this.address());
   }

   public BNDwidgetTheme scrollBarTheme() {
      return nscrollBarTheme(this.address());
   }

   public BNDwidgetTheme tooltipTheme() {
      return ntooltipTheme(this.address());
   }

   public BNDwidgetTheme menuTheme() {
      return nmenuTheme(this.address());
   }

   public BNDwidgetTheme menuItemTheme() {
      return nmenuItemTheme(this.address());
   }

   public BNDnodeTheme nodeTheme() {
      return nnodeTheme(this.address());
   }

   public BNDtheme backgroundColor(@NativeType("NVGcolor") NVGColor value) {
      nbackgroundColor(this.address(), value);
      return this;
   }

   public BNDtheme backgroundColor(Consumer<NVGColor> consumer) {
      consumer.accept(this.backgroundColor());
      return this;
   }

   public BNDtheme regularTheme(BNDwidgetTheme value) {
      nregularTheme(this.address(), value);
      return this;
   }

   public BNDtheme regularTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.regularTheme());
      return this;
   }

   public BNDtheme toolTheme(BNDwidgetTheme value) {
      ntoolTheme(this.address(), value);
      return this;
   }

   public BNDtheme toolTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.toolTheme());
      return this;
   }

   public BNDtheme radioTheme(BNDwidgetTheme value) {
      nradioTheme(this.address(), value);
      return this;
   }

   public BNDtheme radioTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.radioTheme());
      return this;
   }

   public BNDtheme textFieldTheme(BNDwidgetTheme value) {
      ntextFieldTheme(this.address(), value);
      return this;
   }

   public BNDtheme textFieldTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.textFieldTheme());
      return this;
   }

   public BNDtheme optionTheme(BNDwidgetTheme value) {
      noptionTheme(this.address(), value);
      return this;
   }

   public BNDtheme optionTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.optionTheme());
      return this;
   }

   public BNDtheme choiceTheme(BNDwidgetTheme value) {
      nchoiceTheme(this.address(), value);
      return this;
   }

   public BNDtheme choiceTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.choiceTheme());
      return this;
   }

   public BNDtheme numberFieldTheme(BNDwidgetTheme value) {
      nnumberFieldTheme(this.address(), value);
      return this;
   }

   public BNDtheme numberFieldTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.numberFieldTheme());
      return this;
   }

   public BNDtheme sliderTheme(BNDwidgetTheme value) {
      nsliderTheme(this.address(), value);
      return this;
   }

   public BNDtheme sliderTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.sliderTheme());
      return this;
   }

   public BNDtheme scrollBarTheme(BNDwidgetTheme value) {
      nscrollBarTheme(this.address(), value);
      return this;
   }

   public BNDtheme scrollBarTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.scrollBarTheme());
      return this;
   }

   public BNDtheme tooltipTheme(BNDwidgetTheme value) {
      ntooltipTheme(this.address(), value);
      return this;
   }

   public BNDtheme tooltipTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.tooltipTheme());
      return this;
   }

   public BNDtheme menuTheme(BNDwidgetTheme value) {
      nmenuTheme(this.address(), value);
      return this;
   }

   public BNDtheme menuTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.menuTheme());
      return this;
   }

   public BNDtheme menuItemTheme(BNDwidgetTheme value) {
      nmenuItemTheme(this.address(), value);
      return this;
   }

   public BNDtheme menuItemTheme(Consumer<BNDwidgetTheme> consumer) {
      consumer.accept(this.menuItemTheme());
      return this;
   }

   public BNDtheme nodeTheme(BNDnodeTheme value) {
      nnodeTheme(this.address(), value);
      return this;
   }

   public BNDtheme nodeTheme(Consumer<BNDnodeTheme> consumer) {
      consumer.accept(this.nodeTheme());
      return this;
   }

   public BNDtheme set(
      NVGColor backgroundColor,
      BNDwidgetTheme regularTheme,
      BNDwidgetTheme toolTheme,
      BNDwidgetTheme radioTheme,
      BNDwidgetTheme textFieldTheme,
      BNDwidgetTheme optionTheme,
      BNDwidgetTheme choiceTheme,
      BNDwidgetTheme numberFieldTheme,
      BNDwidgetTheme sliderTheme,
      BNDwidgetTheme scrollBarTheme,
      BNDwidgetTheme tooltipTheme,
      BNDwidgetTheme menuTheme,
      BNDwidgetTheme menuItemTheme,
      BNDnodeTheme nodeTheme
   ) {
      this.backgroundColor(backgroundColor);
      this.regularTheme(regularTheme);
      this.toolTheme(toolTheme);
      this.radioTheme(radioTheme);
      this.textFieldTheme(textFieldTheme);
      this.optionTheme(optionTheme);
      this.choiceTheme(choiceTheme);
      this.numberFieldTheme(numberFieldTheme);
      this.sliderTheme(sliderTheme);
      this.scrollBarTheme(scrollBarTheme);
      this.tooltipTheme(tooltipTheme);
      this.menuTheme(menuTheme);
      this.menuItemTheme(menuItemTheme);
      this.nodeTheme(nodeTheme);
      return this;
   }

   public BNDtheme set(BNDtheme src) {
      MemoryUtil.memCopy(src.address(), this.address(), SIZEOF);
      return this;
   }

   public static BNDtheme malloc() {
      return new BNDtheme(MemoryUtil.nmemAllocChecked(SIZEOF), null);
   }

   public static BNDtheme calloc() {
      return new BNDtheme(MemoryUtil.nmemCallocChecked(1L, SIZEOF), null);
   }

   public static BNDtheme create() {
      ByteBuffer container = BufferUtils.createByteBuffer(SIZEOF);
      return new BNDtheme(MemoryUtil.memAddress(container), container);
   }

   public static BNDtheme create(long address) {
      return new BNDtheme(address, null);
   }

   @Nullable
   public static BNDtheme createSafe(long address) {
      return address == 0L ? null : new BNDtheme(address, null);
   }

   public static BNDtheme.Buffer malloc(int capacity) {
      return new BNDtheme.Buffer(MemoryUtil.nmemAllocChecked(__checkMalloc(capacity, SIZEOF)), capacity);
   }

   public static BNDtheme.Buffer calloc(int capacity) {
      return new BNDtheme.Buffer(MemoryUtil.nmemCallocChecked(capacity, SIZEOF), capacity);
   }

   public static BNDtheme.Buffer create(int capacity) {
      ByteBuffer container = __create(capacity, SIZEOF);
      return new BNDtheme.Buffer(MemoryUtil.memAddress(container), container, -1, 0, capacity, capacity);
   }

   public static BNDtheme.Buffer create(long address, int capacity) {
      return new BNDtheme.Buffer(address, capacity);
   }

   @Nullable
   public static BNDtheme.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new BNDtheme.Buffer(address, capacity);
   }

   @Deprecated
   public static BNDtheme mallocStack() {
      return malloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static BNDtheme callocStack() {
      return calloc(MemoryStack.stackGet());
   }

   @Deprecated
   public static BNDtheme mallocStack(MemoryStack stack) {
      return malloc(stack);
   }

   @Deprecated
   public static BNDtheme callocStack(MemoryStack stack) {
      return calloc(stack);
   }

   @Deprecated
   public static BNDtheme.Buffer mallocStack(int capacity) {
      return malloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static BNDtheme.Buffer callocStack(int capacity) {
      return calloc(capacity, MemoryStack.stackGet());
   }

   @Deprecated
   public static BNDtheme.Buffer mallocStack(int capacity, MemoryStack stack) {
      return malloc(capacity, stack);
   }

   @Deprecated
   public static BNDtheme.Buffer callocStack(int capacity, MemoryStack stack) {
      return calloc(capacity, stack);
   }

   public static BNDtheme malloc(MemoryStack stack) {
      return new BNDtheme(stack.nmalloc(ALIGNOF, SIZEOF), null);
   }

   public static BNDtheme calloc(MemoryStack stack) {
      return new BNDtheme(stack.ncalloc(ALIGNOF, 1, SIZEOF), null);
   }

   public static BNDtheme.Buffer malloc(int capacity, MemoryStack stack) {
      return new BNDtheme.Buffer(stack.nmalloc(ALIGNOF, capacity * SIZEOF), capacity);
   }

   public static BNDtheme.Buffer calloc(int capacity, MemoryStack stack) {
      return new BNDtheme.Buffer(stack.ncalloc(ALIGNOF, capacity, SIZEOF), capacity);
   }

   public static NVGColor nbackgroundColor(long struct) {
      return NVGColor.create(struct + BACKGROUNDCOLOR);
   }

   public static BNDwidgetTheme nregularTheme(long struct) {
      return BNDwidgetTheme.create(struct + REGULARTHEME);
   }

   public static BNDwidgetTheme ntoolTheme(long struct) {
      return BNDwidgetTheme.create(struct + TOOLTHEME);
   }

   public static BNDwidgetTheme nradioTheme(long struct) {
      return BNDwidgetTheme.create(struct + RADIOTHEME);
   }

   public static BNDwidgetTheme ntextFieldTheme(long struct) {
      return BNDwidgetTheme.create(struct + TEXTFIELDTHEME);
   }

   public static BNDwidgetTheme noptionTheme(long struct) {
      return BNDwidgetTheme.create(struct + OPTIONTHEME);
   }

   public static BNDwidgetTheme nchoiceTheme(long struct) {
      return BNDwidgetTheme.create(struct + CHOICETHEME);
   }

   public static BNDwidgetTheme nnumberFieldTheme(long struct) {
      return BNDwidgetTheme.create(struct + NUMBERFIELDTHEME);
   }

   public static BNDwidgetTheme nsliderTheme(long struct) {
      return BNDwidgetTheme.create(struct + SLIDERTHEME);
   }

   public static BNDwidgetTheme nscrollBarTheme(long struct) {
      return BNDwidgetTheme.create(struct + SCROLLBARTHEME);
   }

   public static BNDwidgetTheme ntooltipTheme(long struct) {
      return BNDwidgetTheme.create(struct + TOOLTIPTHEME);
   }

   public static BNDwidgetTheme nmenuTheme(long struct) {
      return BNDwidgetTheme.create(struct + MENUTHEME);
   }

   public static BNDwidgetTheme nmenuItemTheme(long struct) {
      return BNDwidgetTheme.create(struct + MENUITEMTHEME);
   }

   public static BNDnodeTheme nnodeTheme(long struct) {
      return BNDnodeTheme.create(struct + NODETHEME);
   }

   public static void nbackgroundColor(long struct, NVGColor value) {
      MemoryUtil.memCopy(value.address(), struct + BACKGROUNDCOLOR, NVGColor.SIZEOF);
   }

   public static void nregularTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + REGULARTHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void ntoolTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + TOOLTHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void nradioTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + RADIOTHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void ntextFieldTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + TEXTFIELDTHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void noptionTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + OPTIONTHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void nchoiceTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + CHOICETHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void nnumberFieldTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + NUMBERFIELDTHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void nsliderTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + SLIDERTHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void nscrollBarTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + SCROLLBARTHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void ntooltipTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + TOOLTIPTHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void nmenuTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + MENUTHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void nmenuItemTheme(long struct, BNDwidgetTheme value) {
      MemoryUtil.memCopy(value.address(), struct + MENUITEMTHEME, BNDwidgetTheme.SIZEOF);
   }

   public static void nnodeTheme(long struct, BNDnodeTheme value) {
      MemoryUtil.memCopy(value.address(), struct + NODETHEME, BNDnodeTheme.SIZEOF);
   }

   static {
      Layout layout = __struct(
         new Member[]{
            __member(NVGColor.SIZEOF, NVGColor.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDwidgetTheme.SIZEOF, BNDwidgetTheme.ALIGNOF),
            __member(BNDnodeTheme.SIZEOF, BNDnodeTheme.ALIGNOF)
         }
      );
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      BACKGROUNDCOLOR = layout.offsetof(0);
      REGULARTHEME = layout.offsetof(1);
      TOOLTHEME = layout.offsetof(2);
      RADIOTHEME = layout.offsetof(3);
      TEXTFIELDTHEME = layout.offsetof(4);
      OPTIONTHEME = layout.offsetof(5);
      CHOICETHEME = layout.offsetof(6);
      NUMBERFIELDTHEME = layout.offsetof(7);
      SLIDERTHEME = layout.offsetof(8);
      SCROLLBARTHEME = layout.offsetof(9);
      TOOLTIPTHEME = layout.offsetof(10);
      MENUTHEME = layout.offsetof(11);
      MENUITEMTHEME = layout.offsetof(12);
      NODETHEME = layout.offsetof(13);
   }

   public static class Buffer extends StructBuffer<BNDtheme, BNDtheme.Buffer> implements NativeResource {
      private static final BNDtheme ELEMENT_FACTORY = BNDtheme.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / BNDtheme.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected BNDtheme.Buffer self() {
         return this;
      }

      protected BNDtheme getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("NVGcolor")
      public NVGColor backgroundColor() {
         return BNDtheme.nbackgroundColor(this.address());
      }

      public BNDwidgetTheme regularTheme() {
         return BNDtheme.nregularTheme(this.address());
      }

      public BNDwidgetTheme toolTheme() {
         return BNDtheme.ntoolTheme(this.address());
      }

      public BNDwidgetTheme radioTheme() {
         return BNDtheme.nradioTheme(this.address());
      }

      public BNDwidgetTheme textFieldTheme() {
         return BNDtheme.ntextFieldTheme(this.address());
      }

      public BNDwidgetTheme optionTheme() {
         return BNDtheme.noptionTheme(this.address());
      }

      public BNDwidgetTheme choiceTheme() {
         return BNDtheme.nchoiceTheme(this.address());
      }

      public BNDwidgetTheme numberFieldTheme() {
         return BNDtheme.nnumberFieldTheme(this.address());
      }

      public BNDwidgetTheme sliderTheme() {
         return BNDtheme.nsliderTheme(this.address());
      }

      public BNDwidgetTheme scrollBarTheme() {
         return BNDtheme.nscrollBarTheme(this.address());
      }

      public BNDwidgetTheme tooltipTheme() {
         return BNDtheme.ntooltipTheme(this.address());
      }

      public BNDwidgetTheme menuTheme() {
         return BNDtheme.nmenuTheme(this.address());
      }

      public BNDwidgetTheme menuItemTheme() {
         return BNDtheme.nmenuItemTheme(this.address());
      }

      public BNDnodeTheme nodeTheme() {
         return BNDtheme.nnodeTheme(this.address());
      }

      public BNDtheme.Buffer backgroundColor(@NativeType("NVGcolor") NVGColor value) {
         BNDtheme.nbackgroundColor(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer backgroundColor(Consumer<NVGColor> consumer) {
         consumer.accept(this.backgroundColor());
         return this;
      }

      public BNDtheme.Buffer regularTheme(BNDwidgetTheme value) {
         BNDtheme.nregularTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer regularTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.regularTheme());
         return this;
      }

      public BNDtheme.Buffer toolTheme(BNDwidgetTheme value) {
         BNDtheme.ntoolTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer toolTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.toolTheme());
         return this;
      }

      public BNDtheme.Buffer radioTheme(BNDwidgetTheme value) {
         BNDtheme.nradioTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer radioTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.radioTheme());
         return this;
      }

      public BNDtheme.Buffer textFieldTheme(BNDwidgetTheme value) {
         BNDtheme.ntextFieldTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer textFieldTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.textFieldTheme());
         return this;
      }

      public BNDtheme.Buffer optionTheme(BNDwidgetTheme value) {
         BNDtheme.noptionTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer optionTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.optionTheme());
         return this;
      }

      public BNDtheme.Buffer choiceTheme(BNDwidgetTheme value) {
         BNDtheme.nchoiceTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer choiceTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.choiceTheme());
         return this;
      }

      public BNDtheme.Buffer numberFieldTheme(BNDwidgetTheme value) {
         BNDtheme.nnumberFieldTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer numberFieldTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.numberFieldTheme());
         return this;
      }

      public BNDtheme.Buffer sliderTheme(BNDwidgetTheme value) {
         BNDtheme.nsliderTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer sliderTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.sliderTheme());
         return this;
      }

      public BNDtheme.Buffer scrollBarTheme(BNDwidgetTheme value) {
         BNDtheme.nscrollBarTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer scrollBarTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.scrollBarTheme());
         return this;
      }

      public BNDtheme.Buffer tooltipTheme(BNDwidgetTheme value) {
         BNDtheme.ntooltipTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer tooltipTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.tooltipTheme());
         return this;
      }

      public BNDtheme.Buffer menuTheme(BNDwidgetTheme value) {
         BNDtheme.nmenuTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer menuTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.menuTheme());
         return this;
      }

      public BNDtheme.Buffer menuItemTheme(BNDwidgetTheme value) {
         BNDtheme.nmenuItemTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer menuItemTheme(Consumer<BNDwidgetTheme> consumer) {
         consumer.accept(this.menuItemTheme());
         return this;
      }

      public BNDtheme.Buffer nodeTheme(BNDnodeTheme value) {
         BNDtheme.nnodeTheme(this.address(), value);
         return this;
      }

      public BNDtheme.Buffer nodeTheme(Consumer<BNDnodeTheme> consumer) {
         consumer.accept(this.nodeTheme());
         return this;
      }
   }
}
