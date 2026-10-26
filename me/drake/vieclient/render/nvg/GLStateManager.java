package me.drake.vieclient.render.nvg;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

public final class GLStateManager {
   private static final int[] lastActiveTexture = new int[1];
   private static final int[] lastProgram = new int[1];
   private static final int[] lastTexture = new int[1];
   private static final int[] lastSampler = new int[1];
   private static final int[] lastArrayBuffer = new int[1];
   private static final int[] lastVertexArrayObject = new int[1];
   private static final int[] lastPixelUnpackBuffer = new int[1];
   private static final int[] lastPolygonMode = new int[2];
   private static final int[] lastViewport = new int[4];
   private static final int[] lastScissorBox = new int[4];
   private static final int[] lastUnpackAlignment = new int[1];
   private static final int[] lastUnpackRowLength = new int[1];
   private static final int[] lastUnpackSkipPixels = new int[1];
   private static final int[] lastUnpackSkipRows = new int[1];
   private static final boolean[] lastColorMask = new boolean[4];
   private static final int[] lastBlendSrcRgb = new int[1];
   private static final int[] lastBlendDstRgb = new int[1];
   private static final int[] lastBlendSrcAlpha = new int[1];
   private static final int[] lastBlendDstAlpha = new int[1];
   private static final int[] lastBlendEquationRgb = new int[1];
   private static final int[] lastBlendEquationAlpha = new int[1];
   private static boolean lastEnableBlend;
   private static boolean lastEnableCullFace;
   private static boolean lastEnableDepthTest;
   private static boolean lastEnableStencilTest;
   private static boolean lastEnableScissorTest;
   private static boolean lastEnablePrimitiveRestart;
   private static boolean lastDepthMask;
   private static final int[] lastFramebuffer = new int[1];
   private static final int[] lastReadFramebuffer = new int[1];
   private static int glVersion = -1;
   private static boolean hasSamplerObjects;
   private static boolean glVersion330OrAbove;
   private static boolean glVersion310OrAbove;
   private static boolean glVersion200OrAbove;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private GLStateManager() {
   }

   public static void setup() {
      if (glVersion == (-1)) {
         int[] var0 = new int[1];
         int[] var1 = new int[1];
         GL30.glGetIntegerv(33307, var0);
         GL30.glGetIntegerv(33308, var1);
         glVersion = var0[0] * (100) + var1[0] * (10);
         glVersion200OrAbove = glVersion >= (200);
         glVersion310OrAbove = glVersion >= (310);
         glVersion330OrAbove = glVersion >= (330);
         hasSamplerObjects = glVersion330OrAbove || GL.getCapabilities().GL_ARB_sampler_objects;
      }
   }

   public static void push() {
      if (glVersion == -1) {
         throw new IllegalStateException(
            "GLStateManager.setup() must be called before push/pop!"
         );
      }

      GL30.glGetIntegerv(34016, lastActiveTexture);
      GL30.glActiveTexture(33984);
      GL30.glGetIntegerv(35725, lastProgram);
      GL30.glGetIntegerv(32873, lastTexture);
      if (hasSamplerObjects) {
         GL30.glGetIntegerv(35097, lastSampler);
         GL33.glBindSampler(0, 0);
      }

      GL30.glGetIntegerv(34964, lastArrayBuffer);
      GL30.glGetIntegerv(34229, lastVertexArrayObject);
      GL30.glGetIntegerv(35055, lastPixelUnpackBuffer);
      if (lastPixelUnpackBuffer[0] != 0) {
         GL30.glBindBuffer(35052, 0);
      }

      if (glVersion200OrAbove) {
         GL30.glGetIntegerv(2880, lastPolygonMode);
      }

      class_310 var0 = class_310.method_1551();
      class_1041 var1 = var0 != null ? var0.method_22683() : null;
      if (var1 != null && var1.method_4489() > 0 && var1.method_4506() > 0) {
         lastViewport[0] = 0;
         lastViewport[1] = 0;
         lastViewport[2] = var1.method_4489();
         lastViewport[3] = var1.method_4506();
      } else {
         GL30.glGetIntegerv(2978, lastViewport);
      }

      GL30.glGetIntegerv(3088, lastScissorBox);
      GL30.glGetIntegerv(3317, lastUnpackAlignment);
      GL30.glGetIntegerv(3314, lastUnpackRowLength);
      GL30.glGetIntegerv(3316, lastUnpackSkipPixels);
      GL30.glGetIntegerv(3315, lastUnpackSkipRows);
      GL30.glPixelStorei(3317, 4);
      GL30.glPixelStorei(3314, 0);
      GL30.glPixelStorei(3316, 0);
      GL30.glPixelStorei(3315, 0);
      MemoryStack var2 = MemoryStack.stackPush();

      try {
         ByteBuffer var3 = var2.malloc(4);
         GL30.glGetBooleanv(3107, var3);
         lastColorMask[0] = var3.get(0) != 0;
         lastColorMask[1] = var3.get(1) != 0;
         lastColorMask[2] = var3.get(2) != 0;
         lastColorMask[3] = var3.get(3) != 0;
      } catch (Throwable var6) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (var2 != null) {
         var2.close();
      }

      GL30.glColorMask(true, true, true, true);
      GL30.glGetIntegerv(32969, lastBlendSrcRgb);
      GL30.glGetIntegerv(32968, lastBlendDstRgb);
      GL30.glGetIntegerv(32971, lastBlendSrcAlpha);
      GL30.glGetIntegerv(32970, lastBlendDstAlpha);
      GL30.glGetIntegerv(32777, lastBlendEquationRgb);
      GL30.glGetIntegerv(34877, lastBlendEquationAlpha);
      lastEnableBlend = GL30.glIsEnabled(3042);
      lastEnableCullFace = GL30.glIsEnabled(2884);
      lastEnableDepthTest = GL30.glIsEnabled(2929);
      lastEnableStencilTest = GL30.glIsEnabled(2960);
      lastEnableScissorTest = GL30.glIsEnabled(3089);
      if (glVersion310OrAbove) {
         lastEnablePrimitiveRestart = GL30.glIsEnabled(36765);
      }

      lastDepthMask = GL30.glGetBoolean(2930);
      GL30.glGetIntegerv(36006, lastFramebuffer);
      GL30.glGetIntegerv(36010, lastReadFramebuffer);
   }

   public static int getLastFramebuffer() {
      return lastFramebuffer[0];
   }

   public static void pop() {
      if (glVersion == -1) {
         throw new IllegalStateException(
            "GLStateManager.setup() must be called before push/pop!"
         );
      }

      GL30.glUseProgram(lastProgram[0]);
      GL30.glActiveTexture(33984);
      GL30.glBindTexture(3553, lastTexture[0]);
      if (hasSamplerObjects) {
         GL33.glBindSampler(0, lastSampler[0]);
      }

      GL30.glActiveTexture(lastActiveTexture[0]);
      GL30.glPixelStorei(3317, lastUnpackAlignment[0]);
      GL30.glPixelStorei(3314, lastUnpackRowLength[0]);
      GL30.glPixelStorei(3316, lastUnpackSkipPixels[0]);
      GL30.glPixelStorei(3315, lastUnpackSkipRows[0]);
      GL30.glBindVertexArray(lastVertexArrayObject[0]);
      GL30.glBindBuffer(34962, lastArrayBuffer[0]);
      GL30.glBindBuffer(35052, lastPixelUnpackBuffer[0]);
      GL30.glColorMask(lastColorMask[0], lastColorMask[1], lastColorMask[2], lastColorMask[3]);
      GL30.glBlendEquationSeparate(lastBlendEquationRgb[0], lastBlendEquationAlpha[0]);
      GL30.glBlendFuncSeparate(lastBlendSrcRgb[0], lastBlendDstRgb[0], lastBlendSrcAlpha[0], lastBlendDstAlpha[0]);
      setGlState(3042, lastEnableBlend);
      setGlState(2884, lastEnableCullFace);
      setGlState(2929, lastEnableDepthTest);
      setGlState(2960, lastEnableStencilTest);
      setGlState(3089, lastEnableScissorTest);
      if (glVersion310OrAbove) {
         setGlState(36765, lastEnablePrimitiveRestart);
      }

      if (glVersion200OrAbove) {
         GL30.glPolygonMode(1032, lastPolygonMode[0]);
      }

      GL30.glViewport(lastViewport[0], lastViewport[1], lastViewport[2], lastViewport[3]);
      GL30.glScissor(lastScissorBox[0], lastScissorBox[1], lastScissorBox[2], lastScissorBox[3]);
      GL30.glDepthMask(lastDepthMask);
      GL30.glBindFramebuffer(36009, lastFramebuffer[0]);
      GL30.glBindFramebuffer(36008, lastReadFramebuffer[0]);
   }

   private static void setGlState(int capability, boolean enabled) {
      if (enabled) {
         GL30.glEnable(capability);
      } else {
         GL30.glDisable(capability);
      }
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }
}
