package org.lwjgl.nanovg;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.system.Struct.Layout;
import org.lwjgl.system.Struct.Member;

@NativeType("struct NSVGpaint")
public class NSVGPaint extends Struct<NSVGPaint> {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int TYPE;
   public static final int COLOR;
   public static final int GRADIENT;

   protected NSVGPaint(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected NSVGPaint create(long address, @Nullable ByteBuffer container) {
      return new NSVGPaint(address, container);
   }

   public NSVGPaint(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   @NativeType("signed char")
   public byte type() {
      return ntype(this.address());
   }

   @NativeType("unsigned int")
   public int color() {
      return ncolor(this.address());
   }

   @NativeType("NSVGgradient *")
   public NSVGGradient gradient() {
      return ngradient(this.address());
   }

   public static NSVGPaint create(long address) {
      return new NSVGPaint(address, null);
   }

   @Nullable
   public static NSVGPaint createSafe(long address) {
      return address == 0L ? null : new NSVGPaint(address, null);
   }

   public static NSVGPaint.Buffer create(long address, int capacity) {
      return new NSVGPaint.Buffer(address, capacity);
   }

   @Nullable
   public static NSVGPaint.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new NSVGPaint.Buffer(address, capacity);
   }

   public static byte ntype(long struct) {
      return UNSAFE.getByte(null, struct + TYPE);
   }

   public static int ncolor(long struct) {
      return UNSAFE.getInt(null, struct + COLOR);
   }

   public static NSVGGradient ngradient(long struct) {
      return NSVGGradient.create(MemoryUtil.memGetAddress(struct + GRADIENT));
   }

   static {
      Layout layout = __struct(new Member[]{__member(1), __union(new Member[]{__member(4), __member(POINTER_SIZE)})});
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      TYPE = layout.offsetof(0);
      COLOR = layout.offsetof(2);
      GRADIENT = layout.offsetof(3);
   }

   public static class Buffer extends StructBuffer<NSVGPaint, NSVGPaint.Buffer> {
      private static final NSVGPaint ELEMENT_FACTORY = NSVGPaint.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / NSVGPaint.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected NSVGPaint.Buffer self() {
         return this;
      }

      protected NSVGPaint getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("signed char")
      public byte type() {
         return NSVGPaint.ntype(this.address());
      }

      @NativeType("unsigned int")
      public int color() {
         return NSVGPaint.ncolor(this.address());
      }

      @NativeType("NSVGgradient *")
      public NSVGGradient gradient() {
         return NSVGPaint.ngradient(this.address());
      }
   }
}
