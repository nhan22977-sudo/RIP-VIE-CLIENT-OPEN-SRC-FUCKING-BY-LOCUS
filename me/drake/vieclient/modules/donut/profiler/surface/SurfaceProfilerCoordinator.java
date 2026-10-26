package me.drake.vieclient.modules.donut.profiler.surface;

import java.util.function.Consumer;
import me.drake.vieclient.modules.donut.profiler.DonutProfilerService;
import net.minecraft.class_310;

public final class SurfaceProfilerCoordinator {
   private static final SurfaceProfilerCoordinator INSTANCE = new SurfaceProfilerCoordinator(
      new SurfaceProfilerSampler(), DonutProfilerService.get()::publish, 256, 64
   );
   private final SurfaceProfilerSampler sampler;
   private final Consumer publisher;
   private final int blockBudget;
   private final int entityBudget;
   private long lastCapturedTick = Long.MIN_VALUE;
   private SurfaceProfilerSample lastSample;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SurfaceProfilerCoordinator(SurfaceProfilerSampler sampler, Consumer publisher, int blockBudget, int entityBudget) {
      this.sampler = samplerx;
      this.publisher = publisherx;
      this.blockBudget = Math.max(0, blockBudgetx);
      this.entityBudget = Math.max(0, entityBudgetx);
   }

   public static SurfaceProfilerCoordinator get() {
      return INSTANCE;
   }

   public synchronized SurfaceProfilerSample captureOnce(long clientTick, SurfaceProfilerSampler.SnapshotInput input) {
      if (this.lastSample != null && clientTick == this.lastCapturedTick) {
         return this.lastSample;
      }

      this.lastCapturedTick = clientTick;
      this.lastSample = this.sampler.capture(input, this.blockBudget, this.entityBudget);
      this.publisher.accept(this.lastSample);
      return this.lastSample;
   }

   public synchronized void reset() {
      this.lastCapturedTick = Long.MIN_VALUE;
      this.lastSample = null;
   }

   public SurfaceProfilerSample captureClientTick() {
      class_310 var1 = class_310.method_1551();
      return var1.field_1724 != null && var1.field_1687 != null ? this.captureOnce(var1.field_1724.field_6012, new MinecraftSurfaceSnapshotInput(var1)) : null;
   }

   static {
      DonutProfilerService.get().registerSessionReset(INSTANCE::reset);
   }
}
