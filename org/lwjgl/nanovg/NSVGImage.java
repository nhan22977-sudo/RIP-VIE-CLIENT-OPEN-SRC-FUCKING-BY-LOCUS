package org.lwjgl.nanovg;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.system.Struct.Layout;
import org.lwjgl.system.Struct.Member;

@NativeType("struct NSVGimage")
public class NSVGImage extends Struct<NSVGImage> {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int WIDTH;
   public static final int HEIGHT;
   public static final int SHAPES;

   protected NSVGImage(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected NSVGImage create(long address, @Nullable ByteBuffer container) {
      return new NSVGImage(address, container);
   }

   public NSVGImage(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   public float width() {
      return nwidth(this.address());
   }

   public float height() {
      return nheight(this.address());
   }

   @NativeType("NSVGshape *")
   public NSVGShape shapes() {
      return nshapes(this.address());
   }

   public static NSVGImage create(long address) {
      return new NSVGImage(address, null);
   }

   @Nullable
   public static NSVGImage createSafe(long address) {
      return address == 0L ? null : new NSVGImage(address, null);
   }

   public static NSVGImage.Buffer create(long address, int capacity) {
      return new NSVGImage.Buffer(address, capacity);
   }

   @Nullable
   public static NSVGImage.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new NSVGImage.Buffer(address, capacity);
   }

   public static float nwidth(long struct) {
      return UNSAFE.getFloat(null, struct + WIDTH);
   }

   public static float nheight(long struct) {
      return UNSAFE.getFloat(null, struct + HEIGHT);
   }

   public static NSVGShape nshapes(long struct) {
      return NSVGShape.create(MemoryUtil.memGetAddress(struct + SHAPES));
   }

   static {
      Layout layout = __struct(new Member[]{__member(4), __member(4), __member(POINTER_SIZE)});
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      WIDTH = layout.offsetof(0);
      HEIGHT = layout.offsetof(1);
      SHAPES = layout.offsetof(2);
   }

   public static class Buffer extends StructBuffer<NSVGImage, NSVGImage.Buffer> {
      private static final NSVGImage ELEMENT_FACTORY = NSVGImage.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / NSVGImage.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected NSVGImage.Buffer self() {
         return this;
      }

      protected NSVGImage getElementFactory() {
         return ELEMENT_FACTORY;
      }

      public float width() {
         return NSVGImage.nwidth(this.address());
      }

      public float height() {
         return NSVGImage.nheight(this.address());
      }

      @NativeType("NSVGshape *")
      public NSVGShape shapes() {
         return NSVGImage.nshapes(this.address());
      }
   }
}
