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

@NativeType("struct NSVGgradient")
public class NSVGGradient extends Struct<NSVGGradient> {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int XFORM;
   public static final int SPREAD;
   public static final int FX;
   public static final int FY;
   public static final int NSTOPS;
   public static final int PSTOPS;

   protected NSVGGradient(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected NSVGGradient create(long address, @Nullable ByteBuffer container) {
      return new NSVGGradient(address, container);
   }

   public NSVGGradient(ByteBuffer container) {
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

   @NativeType("char")
   public byte spread() {
      return nspread(this.address());
   }

   public float fx() {
      return nfx(this.address());
   }

   public float fy() {
      return nfy(this.address());
   }

   public int nstops() {
      return nnstops(this.address());
   }

   @NativeType("NSVGgradientStop[1]")
   public NSVGGradientStop.Buffer pstops() {
      return npstops(this.address());
   }

   @NativeType("NSVGgradientStop")
   public NSVGGradientStop pstops(int index) {
      return npstops(this.address(), index);
   }

   public static NSVGGradient create(long address) {
      return new NSVGGradient(address, null);
   }

   @Nullable
   public static NSVGGradient createSafe(long address) {
      return address == 0L ? null : new NSVGGradient(address, null);
   }

   public static NSVGGradient.Buffer create(long address, int capacity) {
      return new NSVGGradient.Buffer(address, capacity);
   }

   @Nullable
   public static NSVGGradient.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new NSVGGradient.Buffer(address, capacity);
   }

   public static FloatBuffer nxform(long struct) {
      return MemoryUtil.memFloatBuffer(struct + XFORM, 6);
   }

   public static float nxform(long struct, int index) {
      return UNSAFE.getFloat(null, struct + XFORM + Checks.check(index, 6) * 4L);
   }

   public static byte nspread(long struct) {
      return UNSAFE.getByte(null, struct + SPREAD);
   }

   public static float nfx(long struct) {
      return UNSAFE.getFloat(null, struct + FX);
   }

   public static float nfy(long struct) {
      return UNSAFE.getFloat(null, struct + FY);
   }

   public static int nnstops(long struct) {
      return UNSAFE.getInt(null, struct + NSTOPS);
   }

   public static NSVGGradientStop.Buffer npstops(long struct) {
      return NSVGGradientStop.create(struct + PSTOPS, nnstops(struct));
   }

   public static NSVGGradientStop npstops(long struct, int index) {
      return NSVGGradientStop.create(struct + PSTOPS + Checks.check(index, nnstops(struct)) * NSVGGradientStop.SIZEOF);
   }

   static {
      Layout layout = __struct(
         new Member[]{__array(4, 6), __member(1), __member(4), __member(4), __member(4), __array(NSVGGradientStop.SIZEOF, NSVGGradientStop.ALIGNOF, 1)}
      );
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      XFORM = layout.offsetof(0);
      SPREAD = layout.offsetof(1);
      FX = layout.offsetof(2);
      FY = layout.offsetof(3);
      NSTOPS = layout.offsetof(4);
      PSTOPS = layout.offsetof(5);
   }

   public static class Buffer extends StructBuffer<NSVGGradient, NSVGGradient.Buffer> {
      private static final NSVGGradient ELEMENT_FACTORY = NSVGGradient.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / NSVGGradient.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected NSVGGradient.Buffer self() {
         return this;
      }

      protected NSVGGradient getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("float[6]")
      public FloatBuffer xform() {
         return NSVGGradient.nxform(this.address());
      }

      public float xform(int index) {
         return NSVGGradient.nxform(this.address(), index);
      }

      @NativeType("char")
      public byte spread() {
         return NSVGGradient.nspread(this.address());
      }

      public float fx() {
         return NSVGGradient.nfx(this.address());
      }

      public float fy() {
         return NSVGGradient.nfy(this.address());
      }

      public int nstops() {
         return NSVGGradient.nnstops(this.address());
      }

      @NativeType("NSVGgradientStop[1]")
      public NSVGGradientStop.Buffer pstops() {
         return NSVGGradient.npstops(this.address());
      }

      @NativeType("NSVGgradientStop")
      public NSVGGradientStop pstops(int index) {
         return NSVGGradient.npstops(this.address(), index);
      }
   }
}
