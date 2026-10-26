package me.drake.vieclient.modules.donut.profiler.surface;

import java.util.ArrayList;

public class SurfaceProfilerSampler {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SurfaceProfilerSample capture(SurfaceProfilerSampler.SnapshotInput input, int blockBudget, int entityBudget) {
      int var4 = Math.max(0, blockBudget);
      int var5 = Math.max(0, entityBudget);
      int var6 = 0;
      int var7 = 0;
      int var8 = (int)(input.clientTick() & 15L);
      long var9 = input.capturedNanos();
      ArrayList var11 = new ArrayList();
      ArrayList var12 = new ArrayList();
      ArrayList var13 = new ArrayList();
      int var14 = input.chunkCount();
      int var15 = var14 == 0 ? 0 : (int)((input.clientTick() >>> 4) % var14);

      for (int var16 = 0; var16 < var14 && var4 > 0; var16++) {
         int var17 = (var15 + var16) % var14;
         SurfaceProfilerSampler.ChunkInput var18 = input.chunk(var17);
         int[] var19 = new int[3];
         int var20 = 0;
         int var21 = 0;

         for (int var22 = 0; var22 < 16 && var4 > 0; var22++) {
            int var23 = input.surfaceY(var18, var8, var22);
            if (var23 >= 0) {
               SurfaceProfilerSampler.BlockFeature var24 = input.blockAt(var18, var8, var23, var22);
               var4--;
               var6++;
               var21++;
               if (var24 != null && var24.family() >= 0 && var24.family() < var19.length) {
                  var20++;
                  if (var24.aged()) {
                     var19[var24.family()]++;
                  }
               }
            }
         }

         if (var21 > 0) {
            var11.add(
               new SurfaceProfilerSample.ChunkSurfaceSample(
                  var18.chunkKey(), var18.contextBucket(), var19[0], var19[1], var19[2], var20, var18.coverage(), var18.localPlayerExposure(), var9
               )
            );
         }
      }

      int var25 = input.entityCount();
      int var26 = var25 == 0 ? 0 : Math.floorMod(input.clientTick(), var25);

      for (int var27 = 0; var27 < var25 && var5 > 0; var27++) {
         int var28 = (var26 + var27) % var25;
         SurfaceProfilerSampler.EntityInput var29 = input.entity(var28);
         var5--;
         var7++;
         if (var29 != null && var29.y() >= 0) {
            if (var29.phantom()) {
               var13.add(new SurfaceProfilerSample.PhantomMotionSample(var29.entityId(), var29.x(), var29.z(), var29.velocityX(), var29.velocityZ(), var9));
            }

            if (var29.spawnGroup() != null && !var29.spawnGroup().isBlank()) {
               var12.add(
                  new SurfaceProfilerSample.MobFeatureSample(
                     var29.entityId(),
                     var29.chunkKey(),
                     var29.entityType(),
                     var29.spawnGroup(),
                     var29.contextBucket(),
                     var29.y(),
                     var29.armorPieces(),
                     var29.enchantedPieces(),
                     var29.effectCount(),
                     var29.age(),
                     var29.customNamed(),
                     var29.playerEquipped(),
                     var9
                  )
               );
            }
         }
      }

      return new SurfaceProfilerSample(var11, var12, var13, var6, var7, var25, var9);
   }

   public record BlockFeature(int family, boolean aged) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record ChunkInput(long chunkKey, String contextBucket, double coverage, double localPlayerExposure) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record EntityInput(
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
      boolean phantom,
      double x,
      double z,
      double velocityX,
      double velocityZ
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public interface SnapshotInput {
      long clientTick();

      long capturedNanos();

      int chunkCount();

      SurfaceProfilerSampler.ChunkInput chunk(int var1);

      int surfaceY(SurfaceProfilerSampler.ChunkInput var1, int var2, int var3);

      SurfaceProfilerSampler.BlockFeature blockAt(SurfaceProfilerSampler.ChunkInput var1, int var2, int var3, int var4);

      int entityCount();

      SurfaceProfilerSampler.EntityInput entity(int var1);
   }
}
