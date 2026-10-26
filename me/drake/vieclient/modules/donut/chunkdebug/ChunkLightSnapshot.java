package me.drake.vieclient.modules.donut.chunkdebug;

import java.util.Arrays;

final class ChunkLightSnapshot {
   final int sectionCount;
   final int bottomSectionY;
   final SectionStatus[] block;
   final SectionStatus[] sky;
   final long[] blockUpdatedNanos;
   final long[] skyUpdatedNanos;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   ChunkLightSnapshot(int sectionCount, int bottomSectionY) {
      this.sectionCount = sectionCountx;
      this.bottomSectionY = bottomSectionYx;
      int var3 = sectionCountx + (2);
      this.block = new SectionStatus[var3];
      this.sky = new SectionStatus[var3];
      this.blockUpdatedNanos = new long[var3];
      this.skyUpdatedNanos = new long[var3];
      Arrays.fill(this.block, SectionStatus.UNKNOWN);
      Arrays.fill(this.sky, SectionStatus.UNKNOWN);
   }
}
