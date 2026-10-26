package me.drake.vieclient.modules.donut.chunkprobe;

import java.util.Arrays;
import java.util.List;

public record RawChunkProbe(int chunkX, int chunkZ, int payloadBytes, long receivedNanos, List<RawChunkProbe.Section> sections) {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public RawChunkProbe(int chunkX, int chunkZ, int payloadBytes, long receivedNanos, List sections) {
      sections = List.copyOf(sections);
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
      this.payloadBytes = payloadBytes;
      this.receivedNanos = receivedNanos;
      this.sections = sections;
   }

   public long chunkKey() {
      return ChunkKey.pack(this.chunkX, this.chunkZ);
   }

   public record Section(
      int sectionY, int nonEmptyBlockCount, int bitsPerEntry, int[] paletteRawIds, int[] labels, boolean allMaskedDeepslate, boolean canonicalUniformMask
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public Section(
         int sectionY, int nonEmptyBlockCount, int bitsPerEntry, int[] paletteRawIds, int[] labels, boolean allMaskedDeepslate, boolean canonicalUniformMask
      ) {
         paletteRawIds = (int[])paletteRawIds.clone();
         labels = (int[])labels.clone();
         this.sectionY = sectionY;
         this.nonEmptyBlockCount = nonEmptyBlockCount;
         this.bitsPerEntry = bitsPerEntry;
         this.paletteRawIds = paletteRawIds;
         this.labels = labels;
         this.allMaskedDeepslate = allMaskedDeepslate;
         this.canonicalUniformMask = canonicalUniformMask;
      }

      public int[] paletteRawIds() {
         return (int[])this.paletteRawIds.clone();
      }

      public int[] labels() {
         return (int[])this.labels.clone();
      }

      public int residualAir() {
         return this.nonEmptyBlockCount >= 0 && this.nonEmptyBlockCount <= (4096)
            ? (4096) - this.nonEmptyBlockCount
            : -1;
      }

      public int distinctLabels() {
         return (int)Arrays.stream(this.labels).distinct().count();
      }
   }
}
