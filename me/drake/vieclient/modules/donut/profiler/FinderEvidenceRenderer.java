package me.drake.vieclient.modules.donut.profiler;

import java.util.Collection;
import me.drake.vieclient.modules.donut.chunkprobe.ChunkKey;
import me.drake.vieclient.util.RenderUtil;

public final class FinderEvidenceRenderer {
   private static final double PLATE_Y = 63.0;
   private static final int CONFIRMED_RGB = 16711680;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private FinderEvidenceRenderer() {
   }

   public static void render(Collection values, int possibleColor, int alpha) {
      int var3 = Math.max(5, Math.min(255, alpha));

      for (FinderEvidence var5 : values) {
         int var6 = var5.confidence() == FinderConfidence.CONFIRMED ? 16711680 : possibleColor & (16777215);
         int var7 = var6 >> (16) & (255);
         int var8 = var6 >> (8) & (255);
         int var9 = var6 & (255);
         int var10 = var3 << (24) | var7 << (16) | var8 << (8) | var9;
         int var11 = -16777216 | var7 << (16) | var8 << (8) | var9;
         double var12 = ChunkKey.unpackX(var5.chunkKey()) * 16.0;
         double var14 = ChunkKey.unpackZ(var5.chunkKey()) * 16.0;
         double var16 = var12 + 16.0;
         double var18 = var14 + 16.0;
         RenderUtil.drawFlatPlaneSingleSided(var12, var14, var16, var18, 63.0, var10);
         RenderUtil.drawFlatPlaneBorderThin(var12, var14, var16, var18, 63.0, var11);
      }
   }
}
