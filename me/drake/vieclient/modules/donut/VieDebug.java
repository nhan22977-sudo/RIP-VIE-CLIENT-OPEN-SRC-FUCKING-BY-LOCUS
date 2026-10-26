package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ChunkDataEvent;
import me.drake.vieclient.modules.donut.chunkprobe.RawChunkProbe;

public final class VieDebug extends AbstractColumnScanner {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public VieDebug() {
      super(
         "Vie Debug",
         "Phân tích và phát hiện căn cứ ẩn dưới lòng đất.",
         -16718337
      );
   }

   @EventHandler
   private void onChunkData(ChunkDataEvent event) {
      if (this.isEnabled() && this.isInGame()) {
         decodeSections(event.getPacket()).ifPresent(probe -> {
            for (RawChunkProbe.Section var3 : probe.sections()) {
               if (var3.sectionY() < 0) {
                  int[] var4 = var3.paletteRawIds();
                  if (var4.length > 1) {
                     int var5 = 0;

                     for (int var9 : var4) {
                        if (DEEPSLATE_PREDICATE.test(var9)) {
                           var5++;
                        }
                     }

                     if (var5 >= (2) || var4.length > 1 && var3.allMaskedDeepslate()) {
                        this.flagChunk(probe.chunkX(), probe.chunkZ(), "");
                        break;
                     }
                  }
               }
            }
         });
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
