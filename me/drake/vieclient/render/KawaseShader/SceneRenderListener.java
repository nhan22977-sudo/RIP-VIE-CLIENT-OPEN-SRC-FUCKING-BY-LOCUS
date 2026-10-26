package me.drake.vieclient.render.KawaseShader;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.VieClientMod;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.class_310;

public class SceneRenderListener {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static void register() {
      try {
         HudRenderCallback.EVENT.register((HudRenderCallback)(drawContext, tickCounter) -> {
            class_310 var2 = class_310.method_1551();
            if (var2 != null) {
               try {
                  if (KawaseBlurManager.isInitialized()) {
                     KawaseBlurManager.startCapture();
                     KawaseBlurManager.endCapture();
                  }
               } catch (Exception var4) {
                  byte[] var10001 = new byte[29];
                  var10001[0] = (byte)(99);
                  var10001[1] = (byte)(-52);
                  var10001[2] = (byte)(117);
                  var10001[3] = (byte)(-69);
                  var10001[4] = (byte)(-86);
                  var10001[5] = (byte)(40);
                  var10001[6] = (byte)(-114);
                  var10001[7] = (byte)(75);
                  var10001[8] = (byte)(62);
                  var10001[9] = (byte)(-42);
                  var10001[10] = (byte)(-71);
                  var10001[11] = (byte)(-42);
                  var10001[12] = (byte)(-7);
                  var10001[13] = (byte)(-55);
                  var10001[14] = (byte)(-117);
                  var10001[15] = (byte)(118);
                  var10001[16] = (byte)(26);
                  var10001[17] = (byte)(47);
                  var10001[18] = (byte)(-94);
                  var10001[19] = (byte)(-54);
                  var10001[20] = (byte)(60);
                  var10001[21] = (byte)(-58);
                  var10001[22] = (byte)(-105);
                  var10001[23] = (byte)(24);
                  var10001[24] = (byte)(-31);
                  var10001[25] = (byte)(123);
                  var10001[26] = (byte)(-8);
                  var10001[27] = (byte)(35);
                  var10001[28] = (byte)(5);
                  VieClientMod.LOGGER.error($$v_s(var10001, 496691528554762269L), var4);
               }
            }
         });
         VieClientMod.LOGGER
            .info(
               "[VieClient] Scene render listener registered"
            );
      } catch (Exception var1) {
         VieClientMod.LOGGER
            .error(
               "[VieClient] Failed to register scene render listener",
               var1
            );
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
