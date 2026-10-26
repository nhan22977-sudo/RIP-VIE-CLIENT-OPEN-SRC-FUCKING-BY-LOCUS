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

@NativeType("struct NSVGpath")
public class NSVGPath extends Struct<NSVGPath> {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int PTS;
   public static final int NPTS;
   public static final int CLOSED;
   public static final int BOUNDS;
   public static final int NEXT;

   protected NSVGPath(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected NSVGPath create(long address, @Nullable ByteBuffer container) {
      return new NSVGPath(address, container);
   }

   public NSVGPath(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   @NativeType("float *")
   public FloatBuffer pts() {
      return npts(this.address());
   }

   public int npts() {
      return nnpts(this.address());
   }

   @NativeType("char")
   public byte closed() {
      return nclosed(this.address());
   }

   @NativeType("float[4]")
   public FloatBuffer bounds() {
      return nbounds(this.address());
   }

   public float bounds(int index) {
      return nbounds(this.address(), index);
   }

   @NativeType("NSVGpath *")
   public NSVGPath next() {
      return nnext(this.address());
   }

   public static NSVGPath create(long address) {
      return new NSVGPath(address, null);
   }

   @Nullable
   public static NSVGPath createSafe(long address) {
      return address == 0L ? null : new NSVGPath(address, null);
   }

   public static NSVGPath.Buffer create(long address, int capacity) {
      return new NSVGPath.Buffer(address, capacity);
   }

   @Nullable
   public static NSVGPath.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new NSVGPath.Buffer(address, capacity);
   }

   public static FloatBuffer npts(long struct) {
      return MemoryUtil.memFloatBuffer(MemoryUtil.memGetAddress(struct + PTS), nnpts(struct) << 1);
   }

   public static int nnpts(long struct) {
      return UNSAFE.getInt(null, struct + NPTS);
   }

   public static byte nclosed(long struct) {
      return UNSAFE.getByte(null, struct + CLOSED);
   }

   public static FloatBuffer nbounds(long struct) {
      return MemoryUtil.memFloatBuffer(struct + BOUNDS, 4);
   }

   public static float nbounds(long struct, int index) {
      return UNSAFE.getFloat(null, struct + BOUNDS + Checks.check(index, 4) * 4L);
   }

   public static NSVGPath nnext(long struct) {
      return create(MemoryUtil.memGetAddress(struct + NEXT));
   }

   static {
      Layout layout = __struct(new Member[]{__member(POINTER_SIZE), __member(4), __member(1), __array(4, 4), __member(POINTER_SIZE)});
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      PTS = layout.offsetof(0);
      NPTS = layout.offsetof(1);
      CLOSED = layout.offsetof(2);
      BOUNDS = layout.offsetof(3);
      NEXT = layout.offsetof(4);
   }

   public static class Buffer extends StructBuffer<NSVGPath, NSVGPath.Buffer> {
      private static final NSVGPath ELEMENT_FACTORY = NSVGPath.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / NSVGPath.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected NSVGPath.Buffer self() {
         return this;
      }

      protected NSVGPath getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("float *")
      public FloatBuffer pts() {
         return NSVGPath.npts(this.address());
      }

      public int npts() {
         return NSVGPath.nnpts(this.address());
      }

      @NativeType("char")
      public byte closed() {
         return NSVGPath.nclosed(this.address());
      }

      @NativeType("float[4]")
      public FloatBuffer bounds() {
         return NSVGPath.nbounds(this.address());
      }

      public float bounds(int index) {
         return NSVGPath.nbounds(this.address(), index);
      }

      @NativeType("NSVGpath *")
      public NSVGPath next() {
         return NSVGPath.nnext(this.address());
      }
   }
}
