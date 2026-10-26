package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import me.drake.vieclient.chunkfinder.SeedSimulationEngine;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.render.fx.FlaggedChunkRenderer;
import me.drake.vieclient.render.fx.WorldProjector;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.RenderUtil;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;

abstract class AbstractChunkAnalyzerModule extends Module {
   protected static final long FALLBACK_SEED = 6608149111735331168L;
   protected static final int FLAG_PLATE_Y = 63;
   protected static final int MIN_RENDER_DISTANCE_RADIUS = 2;
   protected static final byte CODE_AIR = 100;
   protected static final byte CODE_FILL = 101;
   protected static final Set LIKELY_FILL_BLOCKS;
   protected final IntSetting minY;
   protected final IntSetting maxY;
   protected final IntSetting sensitivity;
   protected final IntSetting alpha;
   protected final ColorSetting chunkColor;
   protected final BoolSetting tracers;
   protected final IntSetting scanIntervalMs;
   protected final IntSetting flagTtlSeconds;
   protected final IntSetting accumulatorStaleSeconds;
   private final AtomicBoolean scanRunning = new AtomicBoolean(false);
   private final ExecutorService scannerExecutor = Executors.newSingleThreadExecutor(r -> {
      Thread var2 = new Thread(r, this.executorThreadName());
      var2.setDaemon(true);
      return var2;
   });
   private final AtomicInteger phaseCounter = new AtomicInteger(0);
   private volatile long lastScanAtMs;
   private String lastWorldKey = "";
   private final Set flaggedChunks = ConcurrentHashMap.newKeySet();
   private final Map flaggedUntilMs = new ConcurrentHashMap();
   private final Map tracerByChunk = new ConcurrentHashMap();
   private final Map phaseAccumulators = new ConcurrentHashMap();
   private final FlaggedChunkRenderer chunkRenderer = new FlaggedChunkRenderer();
   private final WorldProjector projector = new WorldProjector();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   protected AbstractChunkAnalyzerModule(String name, String description, int defaultMinY, int defaultMaxY, int defaultColor) {
      super(name, description, Category.SMP_UTILITIES);
      this.minY = (IntSetting)this.addSetting(
         new IntSetting(
            "MinY",
            "Scan minimum Y",
            defaultMinY,
            -64,
            256
         )
      );
      this.maxY = (IntSetting)this.addSetting(
         new IntSetting(
            "MaxY",
            "Scan maximum Y",
            defaultMaxY,
            -64,
            320
         )
      );
      this.sensitivity = (IntSetting)this.addSetting(
         new IntSetting(
            "Sensitivity",
            "Detection sensitivity",
            12,
            1,
            25
         )
      );
      this.alpha = (IntSetting)this.addSetting(
         new IntSetting(
            "Alpha",
            "Flagged chunk opacity",
            95,
            10,
            255
         )
      );
      this.chunkColor = (ColorSetting)this.addSetting(
         new ColorSetting(
            "ChunkColor",
            "Flagged chunk color",
            defaultColor
         )
      );
      this.tracers = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Tracers",
            "Draw tracers to flagged chunks",
            true
         )
      );
      this.scanIntervalMs = (IntSetting)this.addSetting(
         (IntSetting)hidden(
            new IntSetting(
               "ScanIntervalMs",
               "Milliseconds between scans",
               900,
               200,
               5000
            )
         )
      );
      this.flagTtlSeconds = (IntSetting)this.addSetting(
         (IntSetting)hidden(
            new IntSetting(
               "FlagTtlSeconds",
               "Seconds to keep flagged chunks without refresh",
               60,
               10,
               240
            )
         )
      );
      this.accumulatorStaleSeconds = (IntSetting)this.addSetting(
         (IntSetting)hidden(
            new IntSetting(
               "AccumulatorStaleSeconds",
               "Phase accumulator stale timeout",
               45,
               10,
               180
            )
         )
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.clearState();
      this.lastWorldKey = this.currentWorldKey();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.clearState();
      this.lastWorldKey = "";
   }

   @Override
   public String getInfo() {
      return Integer.toString(this.flaggedChunks.size());
   }

   @EventHandler
   public final void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE && this.isEnabled() && this.isInGame()) {
         if (!this.isOverworld()) {
            this.clearRuntimeFlagsOnly();
         } else {
            String var2 = this.currentWorldKey();
            if (!var2.equals(this.lastWorldKey)) {
               this.clearState();
               this.lastWorldKey = var2;
               this.onWorldChanged();
            }

            this.pruneAndRebuildFlags(System.currentTimeMillis());
            long var3 = System.currentTimeMillis();
            if (var3 - this.lastScanAtMs >= ((Integer)this.scanIntervalMs.get()).intValue()) {
               this.lastScanAtMs = var3;
               this.scheduleAsyncScan();
            }
         }
      }
   }

   @EventHandler
   public final void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D && this.isEnabled() && this.isInGame() && this.isOverworld()) {
         if ((Boolean)this.tracers.get()) {
            class_1923 var2 = mc.field_1724.method_31476();
            int var3 = this.resolveRadiusChunks(2);

            for (FlaggedChunkRenderer.FlaggedChunkVisual var5 : this.chunkRenderer.getVisuals()) {
               if (!(var5.getAlpha() <= 0.005F)) {
                  int var6 = (int)Math.floor(var5.getCurrentX() / 16.0);
                  int var7 = (int)Math.floor(var5.getCurrentZ() / 16.0);
                  if (Math.abs(var6 - var2.field_9181) <= var3 && Math.abs(var7 - var2.field_9180) <= var3) {
                     long var8 = var5.getId();
                     class_2338 var10 = (class_2338)this.tracerByChunk.get(var8);
                     double var11 = var10 != null ? var5.getCurrentX() + (var10.method_10263() & (15)) + 0.5 : var5.getCurrentX() + 8.0;
                     double var13 = var10 != null ? var10.method_10264() + 0.5 : 63.5;
                     double var15 = var10 != null ? var5.getCurrentZ() + (var10.method_10260() & (15)) + 0.5 : var5.getCurrentZ() + 8.0;
                     int var17 = (int)(var5.getAlpha() * Math.min(255, (Integer)this.alpha.get() + (70)));
                     if (var17 > 0) {
                        int var18 = withAlpha((Integer)this.chunkColor.get(), var17);
                        RenderUtil.drawTracerToPos(var11, var13, var15, var18, event.getTickDelta());
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public final void onPostBlit(PostBlitRenderEvent event) {
      if (this.isEnabled() && this.isInGame() && this.isOverworld()) {
         this.chunkRenderer.syncPositions(this.flaggedChunks, (Integer)this.chunkColor.get(), (Integer)this.alpha.get(), 63.0);
         if (this.projector.snapshot()) {
            try (NanoVGFrame var2 = NanoVGRenderer.frame()) {
               if (!var2.isActive()) {
                  return;
               }

               this.chunkRenderer.renderNanoVG(this.projector);
            }
         }
      }
   }

   protected void onWorldChanged() {
   }

   protected boolean usePhasedSampling() {
      return false;
   }

   protected int phaseCount() {
      return this.usePhasedSampling() ? 8 : 1;
   }

   protected abstract Object createScanContext(AbstractChunkAnalyzerModule.ScanInput var1);

   protected abstract AbstractChunkAnalyzerModule.PhaseMetrics analyzeChunkPhase(
      AbstractChunkAnalyzerModule.ChunkSnapshot var1, AbstractChunkAnalyzerModule.ScanInput var2, Object var3
   );

   protected abstract boolean shouldFlag(AbstractChunkAnalyzerModule.TotalMetrics var1, AbstractChunkAnalyzerModule.ChunkSnapshot var2, Object var3);

   protected String executorThreadName() {
      byte[] var10000 = new byte[23];
      var10000[0] = (byte)(94);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-13);
      var10000[4] = (byte)(-37);
      var10000[5] = (byte)(17);
      var10000[6] = (byte)(33);
      var10000[7] = (byte)(60);
      var10000[8] = (byte)(43);
      var10000[9] = (byte)(-113);
      var10000[10] = (byte)(-123);
      var10000[11] = (byte)(-104);
      var10000[12] = (byte)(-127);
      var10000[13] = (byte)(-51);
      var10000[14] = (byte)(5);
      var10000[15] = (byte)(80);
      var10000[16] = (byte)(29);
      var10000[17] = (byte)(56);
      var10000[18] = (byte)(-31);
      var10000[19] = (byte)(-35);
      var10000[20] = (byte)(107);
      var10000[21] = (byte)(-24);
      var10000[22] = (byte)(106);
      return $$v_s(var10000, 8815698419586945037L);
   }

   protected static byte classifyActual(class_2248 block) {
      if (block != class_2246.field_10124 && block != class_2246.field_10543 && block != class_2246.field_10243) {
         byte var1 = SeedSimulationEngine.blockToCode(block);
         if (var1 != 0) {
            return var1;
         } else {
            return (byte)(LIKELY_FILL_BLOCKS.contains(block) ? 101 : 0);
         }
      } else {
         return (byte)(100);
      }
   }

   protected static boolean isTargetExpected(byte code) {
      return code == 1 || code == (2) || code == (3) || code == (4);
   }

   protected static boolean isComparableNatural(byte code) {
      return code == 1
         || code == (2)
         || code == (3)
         || code == (4)
         || code == (5)
         || code == (6);
   }

   protected static long parseSeed(String value) {
      if (value != null && !value.isBlank()) {
         try {
            return Long.parseLong(value.trim());
         } catch (NumberFormatException var2) {
            return 6608149111735331168L;
         }
      } else {
         return 6608149111735331168L;
      }
   }

   protected static int withAlpha(int rgb, int alpha) {
      int var2 = Math.max(0, Math.min(255, alphax));
      return var2 << (24) | rgb & (16777215);
   }

   protected static Setting hidden(Setting setting) {
      setting.setVisibleInGui(false);
      return setting;
   }

   private void scheduleAsyncScan() {
      if (this.scanRunning.compareAndSet(false, true)) {
         AbstractChunkAnalyzerModule.ScanInput var1 = this.captureScanInput();
         if (var1.chunks.isEmpty()) {
            this.scanRunning.set(false);
         } else {
            CompletableFuture.<AbstractChunkAnalyzerModule.ScanOutput>supplyAsync(() -> this.runScan(var1), this.scannerExecutor)
               .whenComplete((output, throwable) -> {
                  Runnable var4 = () -> {
                     try {
                        if (this.isEnabled()) {
                           if (throwable == null && output != null) {
                              this.applyScanOutput(var1, output);
                           }

                           this.pruneAndRebuildFlags(System.currentTimeMillis());
                           return;
                        }
                     } finally {
                        this.scanRunning.set(false);
                     }
                  };
                  if (mc != null) {
                     mc.execute(var4);
                  } else {
                     var4.run();
                  }
               });
         }
      }
   }

   private AbstractChunkAnalyzerModule.ScanOutput runScan(AbstractChunkAnalyzerModule.ScanInput input) {
      Object var2 = this.createScanContext(input);
      HashMap var3 = new HashMap();

      for (Entry var5 : input.chunks.entrySet()) {
         long var6 = (Long)var5.getKey();
         AbstractChunkAnalyzerModule.ChunkSnapshot var8 = (AbstractChunkAnalyzerModule.ChunkSnapshot)var5.getValue();
         AbstractChunkAnalyzerModule.PhaseMetrics var9 = this.analyzeChunkPhase(var8, input, var2);
         var3.put(var6, new AbstractChunkAnalyzerModule.PhaseSample(input.phase, var9.compared, var9.mismatches, var9.score, var9.firstMismatch));
      }

      return new AbstractChunkAnalyzerModule.ScanOutput(var3, var2);
   }

   private void applyScanOutput(AbstractChunkAnalyzerModule.ScanInput input, AbstractChunkAnalyzerModule.ScanOutput output) {
      long var3 = System.currentTimeMillis();
      long var5 = Math.max(1000L, ((Integer)this.flagTtlSeconds.get()).intValue() * 1000L);
      long var7 = Math.max(5000L, ((Integer)this.accumulatorStaleSeconds.get()).intValue() * 1000L);
      int var9 = this.phaseCount();

      for (Entry var11 : output.samples.entrySet()) {
         long var12 = (Long)var11.getKey();
         AbstractChunkAnalyzerModule.PhaseSample var14 = (AbstractChunkAnalyzerModule.PhaseSample)var11.getValue();
         AbstractChunkAnalyzerModule.ChunkSnapshot var15 = (AbstractChunkAnalyzerModule.ChunkSnapshot)input.chunks.get(var12);
         if (var15 != null) {
            AbstractChunkAnalyzerModule.PhaseAccumulator var16 = this.phaseAccumulators
               .computeIfAbsent(var12, ignored -> new AbstractChunkAnalyzerModule.PhaseAccumulator(var9));
            var16.setPhase(var14.phase, var14.compared, var14.mismatches, var14.score, var14.firstMismatch, var3);
            if (var16.isComplete()) {
               AbstractChunkAnalyzerModule.TotalMetrics var17 = new AbstractChunkAnalyzerModule.TotalMetrics(
                  var16.totalCompared(), var16.totalMismatches(), var16.totalScore(), var16.anyMismatchPoint()
               );
               if (this.shouldFlag(var17, var15, output.context)) {
                  this.flaggedUntilMs.put(var12, var3 + var5);
                  if (var17.firstMismatch != null) {
                     this.tracerByChunk.put(var12, var17.firstMismatch);
                  }
               }
            }
         }
      }

      class_1923 var18 = mc.field_1724.method_31476();
      int var19 = this.resolveRadiusChunks(2);
      this.phaseAccumulators.entrySet().removeIf(entry -> {
         long var7x = (Long)entry.getKey();
         AbstractChunkAnalyzerModule.PhaseAccumulator var9x = (AbstractChunkAnalyzerModule.PhaseAccumulator)entry.getValue();
         if (var3 - var9x.updatedMs > var7) {
            return true;
         }

         int var10 = class_1923.method_8325(var7x);
         int var11x = class_1923.method_8332(var7x);
         return Math.abs(var10 - var18.field_9181) > var19 || Math.abs(var11x - var18.field_9180) > var19;
      });
   }

   private void pruneAndRebuildFlags(long now) {
      if (this.isInGame() && this.isOverworld()) {
         class_1923 var3 = mc.field_1724.method_31476();
         int var4 = this.resolveRadiusChunks(2);
         this.flaggedUntilMs
            .entrySet()
            .removeIf(
               entry -> {
                  long var5 = (Long)entry.getKey();
                  long var7 = (Long)entry.getValue();
                  if (var7 <= now) {
                     return true;
                  }

                  int var9 = class_1923.method_8325(var5);
                  int var10 = class_1923.method_8332(var5);
                  return Math.abs(var9 - var3.field_9181) <= var4 && Math.abs(var10 - var3.field_9180) <= var4
                     ? mc.field_1687.method_2935().method_12126(var9, var10, false) == null
                     : true;
               }
            );
         this.tracerByChunk.keySet().removeIf(key -> !this.flaggedUntilMs.containsKey(key));
         this.flaggedChunks.clear();

         for (long var6 : this.flaggedUntilMs.keySet()) {
            this.flaggedChunks.add(new class_1923(class_1923.method_8325(var6), class_1923.method_8332(var6)));
         }
      } else {
         this.clearRuntimeFlagsOnly();
      }
   }

   private AbstractChunkAnalyzerModule.ScanInput captureScanInput() {
      if (this.isInGame() && this.isOverworld()) {
         int var1 = (Integer)this.minY.get();
         int var2 = (Integer)this.maxY.get();
         int var3 = Math.min(var1, var2);
         int var4 = Math.max(var1, var2);
         var3 = Math.max(var3, mc.field_1687.method_31607());
         var4 = Math.min(var4, mc.field_1687.method_31600());
         if (var3 > var4) {
            return AbstractChunkAnalyzerModule.ScanInput.EMPTY;
         }

         int var5 = this.resolveRadiusChunks(2);
         class_1923 var6 = mc.field_1724.method_31476();
         HashMap var7 = new HashMap();

         for (int var8 = var6.field_9181 - var5; var8 <= var6.field_9181 + var5; var8++) {
            for (int var9 = var6.field_9180 - var5; var9 <= var6.field_9180 + var5; var9++) {
               class_2818 var10 = mc.field_1687.method_2935().method_12126(var8, var9, false);
               if (var10 != null) {
                  AbstractChunkAnalyzerModule.ChunkSnapshot var11 = this.snapshotChunk(var10, var3, var4);
                  if (var11 != null) {
                     var7.put(class_1923.method_8331(var8, var9), var11);
                  }
               }
            }
         }

         int var14 = this.phaseCount() == 1 ? 0 : this.phaseCounter.getAndIncrement() & (7);
         return new AbstractChunkAnalyzerModule.ScanInput(var7, var3, var4, var14);
      } else {
         return AbstractChunkAnalyzerModule.ScanInput.EMPTY;
      }
   }

   private AbstractChunkAnalyzerModule.ChunkSnapshot snapshotChunk(class_2818 chunk, int scanMinY, int scanMaxY) {
      int var4 = scanMaxY - scanMinY + 1;
      if (var4 <= 0) {
         return null;
      }

      byte[] var5 = new byte[(256) * var4];
      class_2826[] var6 = chunk.method_12006();
      int var7 = chunk.method_31607();
      int var8 = 0;

      for (int var9 = scanMinY; var9 <= scanMaxY; var9++) {
         int var10 = var9 - var7 >> (4);
         class_2826 var11 = var10 >= 0 && var10 < var6.length ? var6[var10] : null;
         int var12 = var9 & (15);

         for (int var13 = 0; var13 < (16); var13++) {
            for (int var14 = 0; var14 < (16); var14++) {
               class_2248 var15;
               if (var11 != null && !var11.method_38292()) {
                  class_2680 var16 = var11.method_12254(var13, var12, var14);
                  var15 = var16.method_26204();
               } else {
                  var15 = class_2246.field_10124;
               }

               var5[var8++] = classifyActual(var15);
            }
         }
      }

      return new AbstractChunkAnalyzerModule.ChunkSnapshot(chunk.method_12004().field_9181, chunk.method_12004().field_9180, scanMinY, scanMaxY, var5);
   }

   private String currentWorldKey() {
      return mc != null && mc.field_1687 != null ? mc.field_1687.method_27983().method_29177().toString() : "";
   }

   protected boolean isOverworld() {
      return this.isInGame() && mc.field_1687.method_27983() == class_1937.field_25179;
   }

   private int resolveRadiusChunks(int extraMargin) {
      int var2 = mc.field_1690.method_38521();
      return Math.max(2, var2 + Math.max(0, extraMargin));
   }

   private void clearRuntimeFlagsOnly() {
      this.flaggedChunks.clear();
      this.flaggedUntilMs.clear();
      this.tracerByChunk.clear();
      this.chunkRenderer.clear();
   }

   private void clearState() {
      this.clearRuntimeFlagsOnly();
      this.phaseAccumulators.clear();
      this.phaseCounter.set(0);
      this.lastScanAtMs = 0L;
   }

   static {
      class_2248[] var10000 = new class_2248[15];
      var10000[0] = class_2246.field_10445;
      var10000[1] = class_2246.field_29031;
      var10000[2] = class_2246.field_10056;
      var10000[3] = class_2246.field_10416;
      var10000[4] = class_2246.field_10065;
      var10000[5] = class_2246.field_10552;
      var10000[6] = class_2246.field_28900;
      var10000[7] = class_2246.field_29222;
      var10000[8] = class_2246.field_28896;
      var10000[9] = class_2246.field_29223;
      var10000[10] = class_2246.field_28892;
      var10000[11] = class_2246.field_10093;
      var10000[12] = class_2246.field_10346;
      var10000[13] = class_2246.field_10289;
      var10000[14] = class_2246.field_10360;
      LIKELY_FILL_BLOCKS = Set.of(var10000);
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

   protected record ChunkSnapshot(int chunkX, int chunkZ, int minY, int maxY, byte[] codes) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      protected byte codeAt(int localX, int y, int localZ) {
         if (localX < 0 || localX > (15) || localZ < 0 || localZ > (15)) {
            return 0;
         } else if (y >= this.minY && y <= this.maxY) {
            int var4 = this.maxY - this.minY + 1;
            int var5 = y - this.minY;
            int var6 = var5 * (16) * (16) + localX * (16) + localZ;
            return var6 >= 0 && var6 < (256) * var4 ? this.codes[var6] : 0;
         } else {
            return 0;
         }
      }
   }

   private static final class PhaseAccumulator {
      private final int[] comparedByPhase;
      private final int[] mismatchByPhase;
      private final double[] scoreByPhase;
      private final class_2338[] mismatchPointByPhase;
      private final int expectedMask;
      private int filledMask;
      private long updatedMs;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private PhaseAccumulator(int phaseCount) {
         int var2 = Math.max(1, phaseCount);
         this.comparedByPhase = new int[var2];
         this.mismatchByPhase = new int[var2];
         this.scoreByPhase = new double[var2];
         this.mismatchPointByPhase = new class_2338[var2];
         this.expectedMask = var2 >= (31) ? -1 : (1 << var2) - 1;
      }

      private void setPhase(int phase, int compared, int mismatches, double score, class_2338 firstMismatch, long now) {
         if (phase >= 0 && phase < this.comparedByPhase.length) {
            this.comparedByPhase[phase] = Math.max(0, compared);
            this.mismatchByPhase[phase] = Math.max(0, mismatches);
            this.scoreByPhase[phase] = Math.max(0.0, score);
            this.mismatchPointByPhase[phase] = firstMismatch;
            this.filledMask |= 1 << phase;
            this.updatedMs = now;
         }
      }

      private boolean isComplete() {
         return this.filledMask == this.expectedMask;
      }

      private int totalCompared() {
         int var1 = 0;

         for (int var5 : this.comparedByPhase) {
            var1 += var5;
         }

         return var1;
      }

      private int totalMismatches() {
         int var1 = 0;

         for (int var5 : this.mismatchByPhase) {
            var1 += var5;
         }

         return var1;
      }

      private double totalScore() {
         double var1 = 0.0;

         for (double var6 : this.scoreByPhase) {
            var1 += var6;
         }

         return var1;
      }

      private class_2338 anyMismatchPoint() {
         for (class_2338 var4 : this.mismatchPointByPhase) {
            if (var4 != null) {
               return var4;
            }
         }

         return null;
      }
   }

   protected record PhaseMetrics(int compared, int mismatches, double score, class_2338 firstMismatch) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record PhaseSample(int phase, int compared, int mismatches, double score, class_2338 firstMismatch) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   protected record ScanInput(Map<Long, AbstractChunkAnalyzerModule.ChunkSnapshot> chunks, int minY, int maxY, int phase) {
      private static final AbstractChunkAnalyzerModule.ScanInput EMPTY = new AbstractChunkAnalyzerModule.ScanInput(Map.of(), 0, -1, 0);
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private final class ScanOutput {
      private final Map samples;
      private final Object context;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private ScanOutput(Map samples, Object context) {
         this.samples = samplesx;
         this.context = contextx;
      }
   }

   protected record TotalMetrics(int compared, int mismatches, double score, class_2338 firstMismatch) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
