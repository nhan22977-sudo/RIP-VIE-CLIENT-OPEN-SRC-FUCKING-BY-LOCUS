package me.drake.vieclient.util;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.render.FrostedBackground;
import net.minecraft.class_1011;
import net.minecraft.class_10868;
import net.minecraft.class_310;
import net.minecraft.class_1011.class_1012;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public final class DebugFrameCapture {
   private static final boolean ENABLED;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private DebugFrameCapture() {
   }

   public static void tryCapture(class_310 mc) {
      if (ENABLED) {
         try {
            File var1 = new File(
               mc.field_1697,
               "viedebug_capture.txt"
            );
            if (!var1.exists()) {
               return;
            }

            if (!var1.delete()) {
               return;
            }

            int var2 = mc.method_22683().method_4489();
            int var3 = mc.method_22683().method_4506();
            if (var2 <= 0 || var3 <= 0 || (long)var2 * var3 > 16000000L) {
               VieClientMod.LOGGER
                  .warn(
                     "[VieClient] debug capture size {}x{} out of range",
                     var2,
                     var3
                  );
               return;
            }

            VieClientMod.LOGGER
               .info(
                  "[VieClient] debug capture: {}x{} screen={} frostActive={}",
                  new Object[]{
                     var2,
                     var3,
                     mc.field_1755 == null ? "null" : mc.field_1755.getClass().getName(),
                     FrostedBackground.isFrostActive()
                  }
               );
            GL30.glBindFramebuffer(36008, 0);
            writePng(
               readPixels(var2, var3),
               var2,
               var3,
               new File(mc.field_1697, "viedebug_fb0.png")
            );

            try {
               class_10868 var4 = (class_10868)mc.method_1522().method_30277();
               int var5 = GL30.glGenFramebuffers();
               GL30.glBindFramebuffer(36160, var5);
               GL30.glFramebufferTexture2D(36160, 36064, 3553, var4.method_68427(), 0);
               int var6 = GL11.glGetTexLevelParameteri(3553, 0, 4096);
               int var7 = GL11.glGetTexLevelParameteri(3553, 0, 4097);
               int var8 = GL30.glCheckFramebufferStatus(36160);
               if (var8 == 36053 && var6 > 0 && var7 > 0 && (long)var6 * var7 <= 16000000L) {
                  writePng(
                     readPixels(var6, var7),
                     var6,
                     var7,
                     new File(
                        mc.field_1697,
                        "viedebug_mainfb.png"
                     )
                  );
               } else {
                  VieClientMod.LOGGER
                     .warn(
                        "[VieClient] main FB readback incomplete: status={} {}x{}",
                        new Object[]{var8, var6, var7}
                     );
               }

               GL30.glBindFramebuffer(36160, 0);
               GL30.glDeleteFramebuffers(var5);
            } catch (Throwable var9) {
               VieClientMod.LOGGER
                  .warn(
                     "[VieClient] main FB capture failed",
                     var9
                  );
            }

            VieClientMod.LOGGER
               .info(
                  "[VieClient] debug capture done -> viedebug_fb0.png / viedebug_mainfb.png"
               );
         } catch (Throwable var10) {
            VieClientMod.LOGGER
               .error(
                  "[VieClient] debug capture failed",
                  var10
               );
         }
      }
   }

   private static ByteBuffer readPixels(int w, int h) {
      ByteBuffer var2 = BufferUtils.createByteBuffer(w * h * (4));
      GL11.glReadPixels(0, 0, w, h, 6408, 5121, var2);
      return var2;
   }

   private static void writePng(ByteBuffer rgba, int w, int h, File out) throws Exception {
      class_1011 var4 = new class_1011(class_1012.field_4997, w, h, false);

      for (int var5 = 0; var5 < h; var5++) {
         for (int var6 = 0; var6 < w; var6++) {
            int var7 = ((h - 1 - var5) * w + var6) * (4);
            int var8 = rgba.get(var7) & (255);
            int var9 = rgba.get(var7 + 1) & (255);
            int var10 = rgba.get(var7 + (2)) & (255);
            int var11 = rgba.get(var7 + (3)) & (255);
            var4.method_61941(var6, var5, var11 << (24) | var8 << (16) | var9 << (8) | var10);
         }
      }

      var4.method_4314(out.toPath());
      var4.close();
   }

   public static void dumpTexture(int glTexId, int w, int h, String outName) {
      class_310 var4 = class_310.method_1551();
      if (var4 != null && glTexId != 0 && w > 0 && h > 0) {
         try {
            int var5 = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer(36160, var5);
            GL30.glFramebufferTexture2D(36160, 36064, 3553, glTexId, 0);
            int var6 = GL30.glCheckFramebufferStatus(36160);
            if (var6 == 36053) {
               writePng(readPixels(w, h), w, h, new File(var4.field_1697, outName));
            } else {
               VieClientMod.LOGGER
                  .warn(
                     "[VieClient] dumpTexture {} incomplete status={}",
                     outName,
                     var6
                  );
            }

            GL30.glBindFramebuffer(36160, 0);
            GL30.glDeleteFramebuffers(var5);
         } catch (Throwable var7) {
            VieClientMod.LOGGER
               .error(
                  "[VieClient] dumpTexture {} failed",
                  outName,
                  var7
               );
         }
      }
   }

   static {
      byte[] var10000 = new byte[22];
      var10000[0] = (byte)(64);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-16);
      var10000[4] = (byte)(-53);
      var10000[5] = (byte)(49);
      var10000[6] = (byte)(-70);
      var10000[7] = (byte)(-37);
      var10000[8] = (byte)(57);
      var10000[9] = (byte)(-116);
      var10000[10] = (byte)(-126);
      var10000[11] = (byte)(-124);
      var10000[12] = (byte)(-106);
      var10000[13] = (byte)(-31);
      var10000[14] = (byte)(-107);
      var10000[15] = (byte)(-107);
      var10000[16] = (byte)(7);
      var10000[17] = (byte)(58);
      var10000[18] = (byte)(-72);
      var10000[19] = (byte)(-37);
      var10000[20] = (byte)(109);
      var10000[21] = (byte)(-63);
      ENABLED = Boolean.getBoolean($$v_s(var10000, -8729231624384557037L));
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
