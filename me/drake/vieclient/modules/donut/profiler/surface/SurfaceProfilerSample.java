package me.drake.vieclient.modules.donut.profiler.surface;

import java.util.List;
import me.drake.vieclient.modules.donut.profiler.ProfilerSample;

public record SurfaceProfilerSample(
   List<SurfaceProfilerSample.ChunkSurfaceSample> chunks,
   List<SurfaceProfilerSample.MobFeatureSample> mobs,
   List<SurfaceProfilerSample.PhantomMotionSample> phantoms,
   int blockReads,
   int entityReads,
   int entityCandidates,
   long capturedNanos
) implements ProfilerSample {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SurfaceProfilerSample(List chunks, List mobs, List phantoms, int blockReads, int entityReads, int entityCandidates, long capturedNanos) {
      chunks = List.copyOf(chunks);
      mobs = List.copyOf(mobs);
      phantoms = List.copyOf(phantoms);
      this.chunks = chunks;
      this.mobs = mobs;
      this.phantoms = phantoms;
      this.blockReads = blockReads;
      this.entityReads = entityReads;
      this.entityCandidates = entityCandidates;
      this.capturedNanos = capturedNanos;
   }

   public record ChunkSurfaceSample(
      long chunkKey,
      String contextBucket,
      int firstFamilyAged,
      int secondFamilyAged,
      int thirdFamilyAged,
      int comparableBlocks,
      double coverage,
      double localPlayerExposure,
      long capturedNanos
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record MobFeatureSample(
      int entityId,
      long chunkKey,
      String entityType,
      String spawnGroup,
      String contextBucket,
      int y,
      int armorPieces,
      int enchantedPieces,
      int effectCount,
      int age,
      boolean customNamed,
      boolean playerEquipped,
      long capturedNanos
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record PhantomMotionSample(int entityId, double x, double z, double velocityX, double velocityZ, long capturedNanos) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
