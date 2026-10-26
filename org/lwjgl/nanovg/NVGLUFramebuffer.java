package org.lwjgl.nanovg;

import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.Struct;
import org.lwjgl.system.StructBuffer;
import org.lwjgl.system.Struct.Layout;
import org.lwjgl.system.Struct.Member;

@NativeType("struct NVGLUframebuffer")
public class NVGLUFramebuffer extends Struct<NVGLUFramebuffer> {
   public static final int SIZEOF;
   public static final int ALIGNOF;
   public static final int FBO;
   public static final int RBO;
   public static final int TEXTURE;
   public static final int IMAGE;

   protected NVGLUFramebuffer(long address, @Nullable ByteBuffer container) {
      super(address, container);
   }

   protected NVGLUFramebuffer create(long address, @Nullable ByteBuffer container) {
      return new NVGLUFramebuffer(address, container);
   }

   public NVGLUFramebuffer(ByteBuffer container) {
      super(MemoryUtil.memAddress(container), __checkContainer(container, SIZEOF));
   }

   public int sizeof() {
      return SIZEOF;
   }

   @NativeType("GLuint")
   public int fbo() {
      return nfbo(this.address());
   }

   @NativeType("GLuint")
   public int rbo() {
      return nrbo(this.address());
   }

   @NativeType("GLuint")
   public int texture() {
      return ntexture(this.address());
   }

   public int image() {
      return nimage(this.address());
   }

   public static NVGLUFramebuffer create(long address) {
      return new NVGLUFramebuffer(address, null);
   }

   @Nullable
   public static NVGLUFramebuffer createSafe(long address) {
      return address == 0L ? null : new NVGLUFramebuffer(address, null);
   }

   public static NVGLUFramebuffer.Buffer create(long address, int capacity) {
      return new NVGLUFramebuffer.Buffer(address, capacity);
   }

   @Nullable
   public static NVGLUFramebuffer.Buffer createSafe(long address, int capacity) {
      return address == 0L ? null : new NVGLUFramebuffer.Buffer(address, capacity);
   }

   public static int nfbo(long struct) {
      return UNSAFE.getInt(null, struct + FBO);
   }

   public static int nrbo(long struct) {
      return UNSAFE.getInt(null, struct + RBO);
   }

   public static int ntexture(long struct) {
      return UNSAFE.getInt(null, struct + TEXTURE);
   }

   public static int nimage(long struct) {
      return UNSAFE.getInt(null, struct + IMAGE);
   }

   static {
      Layout layout = __struct(new Member[]{__member(4), __member(4), __member(4), __member(4)});
      SIZEOF = layout.getSize();
      ALIGNOF = layout.getAlignment();
      FBO = layout.offsetof(0);
      RBO = layout.offsetof(1);
      TEXTURE = layout.offsetof(2);
      IMAGE = layout.offsetof(3);
   }

   public static class Buffer extends StructBuffer<NVGLUFramebuffer, NVGLUFramebuffer.Buffer> {
      private static final NVGLUFramebuffer ELEMENT_FACTORY = NVGLUFramebuffer.create(-1L);

      public Buffer(ByteBuffer container) {
         super(container, container.remaining() / NVGLUFramebuffer.SIZEOF);
      }

      public Buffer(long address, int cap) {
         super(address, null, -1, 0, cap, cap);
      }

      Buffer(long address, @Nullable ByteBuffer container, int mark, int pos, int lim, int cap) {
         super(address, container, mark, pos, lim, cap);
      }

      protected NVGLUFramebuffer.Buffer self() {
         return this;
      }

      protected NVGLUFramebuffer getElementFactory() {
         return ELEMENT_FACTORY;
      }

      @NativeType("GLuint")
      public int fbo() {
         return NVGLUFramebuffer.nfbo(this.address());
      }

      @NativeType("GLuint")
      public int rbo() {
         return NVGLUFramebuffer.nrbo(this.address());
      }

      @NativeType("GLuint")
      public int texture() {
         return NVGLUFramebuffer.ntexture(this.address());
      }

      public int image() {
         return NVGLUFramebuffer.nimage(this.address());
      }
   }
}
