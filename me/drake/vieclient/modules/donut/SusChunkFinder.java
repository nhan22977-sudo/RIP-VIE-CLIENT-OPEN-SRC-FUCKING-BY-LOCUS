package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ChunkLoadEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.render.fx.FlaggedChunkRenderer;
import me.drake.vieclient.render.fx.WorldProjector;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_638;

public final class SusChunkFinder extends Module {
   private static final Predicate GEODE_BLOCK_PREDICATE = state -> state.method_27852(class_2246.field_27159)
      || state.method_27852(class_2246.field_27160)
      || state.method_27852(class_2246.field_27114)
      || state.method_27852(class_2246.field_27161)
      || state.method_27852(class_2246.field_27164)
      || state.method_27852(class_2246.field_27163)
      || state.method_27852(class_2246.field_27162)
      || state.method_27852(class_2246.field_29032);
   private static final double PLATE_Y = 63.0;
   private static final double PLATE_HEIGHT = 0.05;
   private static final int MAX_CAPTURE_PER_TICK = 4;
   private final IntSetting alpha = (IntSetting)this.addSetting(
      new IntSetting(
         "Alpha",
         "Chunk fill opacity",
         50,
         0,
         100
      )
   );
   private final IntSetting sensitivity = (IntSetting)this.addSetting(
      new IntSetting(
         "Sensitivity",
         "Cluster hits needed for grown geode",
         6,
         1,
         20
      )
   );
   private final IntSetting simulationDistance = (IntSetting)this.addSetting(
      new IntSetting(
         "Simulation Distance",
         "Triangulation radius in chunks",
         4,
         2,
         16
      )
   );
   private final BoolSetting smartAdjustment = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Smart Adjustment",
         "Keep only the highest probability candidate chunks",
         true
      )
   );
   private final BoolSetting filterAmethyst = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Amethyst",
         "Flag chunks using amethyst geode growth & light leaks",
         true
      )
   );
   private final BoolSetting filterVine = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Vine",
         "Flag chunks with overgrown vines",
         true
      )
   );
   private final BoolSetting filterBeeNest = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Bee Nest",
         "Flag chunks with bee nest/hive honey growth",
         true
      )
   );
   private final BoolSetting filterCacao = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Cacao",
         "Flag chunks with mature cocoa growth",
         true
      )
   );
   private final BoolSetting filterSeagrass = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Seagrass",
         "Flag chunks with tall seagrass growth",
         false
      )
   );
   private final BoolSetting filterDripstone = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Dripstone",
         "Flag chunks with growing pointed dripstone",
         false
      )
   );
   private final BoolSetting filterCaveVine = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Cave Vine",
         "Flag chunks with overgrown cave vines/berries",
         true
      )
   );
   private final BoolSetting filterKelp = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Kelp",
         "Flag chunks with surface-reaching mature kelp",
         false
      )
   );
   private final BoolSetting filterBamboo = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Bamboo",
         "Flag chunks with fully grown tall bamboo",
         false
      )
   );
   private final ConcurrentLinkedQueue scanQueue = new ConcurrentLinkedQueue();
   private final Set queuedChunks = ConcurrentHashMap.newKeySet();
   private final Set scannedChunks = ConcurrentHashMap.newKeySet();
   private final Map hits = new ConcurrentHashMap();
   private final Set geodeChunks = ConcurrentHashMap.newKeySet();
   private final Set confirmedGrown = ConcurrentHashMap.newKeySet();
   private final Set ungrown = ConcurrentHashMap.newKeySet();
   private final Set growthSusChunks = ConcurrentHashMap.newKeySet();
   private final Map growthScores = new ConcurrentHashMap();
   private final Set definiteGrowthSus = ConcurrentHashMap.newKeySet();
   private final ExecutorService scanner = Executors.newSingleThreadExecutor(runnable -> {
      byte[] var10003 = new byte[24];
      var10003[0] = (byte)(109);
      var10003[1] = (byte)(-13);
      var10003[2] = (byte)(121);
      var10003[3] = (byte)(-67);
      var10003[4] = (byte)(11);
      var10003[5] = (byte)(62);
      var10003[6] = (byte)(11);
      var10003[7] = (byte)(-77);
      var10003[8] = (byte)(7);
      var10003[9] = (byte)(-113);
      var10003[10] = (byte)(-105);
      var10003[11] = (byte)(-125);
      var10003[12] = (byte)(76);
      var10003[13] = (byte)(-94);
      var10003[14] = (byte)(37);
      var10003[15] = (byte)(-51);
      var10003[16] = (byte)(62);
      var10003[17] = (byte)(36);
      var10003[18] = (byte)(-89);
      var10003[19] = (byte)(-125);
      var10003[20] = (byte)(-92);
      var10003[21] = (byte)(-60);
      var10003[22] = (byte)(95);
      var10003[23] = (byte)(-29);
      Thread var1 = new Thread(runnable, $$v_s(var10003, -761801174130896834L));
      var1.setDaemon(true);
      var1.setPriority(4);
      return var1;
   });
   private final ExecutorService triangulationWorker = Executors.newSingleThreadExecutor(runnable -> {
      byte[] var10003 = new byte[27];
      var10003[0] = (byte)(109);
      var10003[1] = (byte)(-13);
      var10003[2] = (byte)(121);
      var10003[3] = (byte)(-67);
      var10003[4] = (byte)(11);
      var10003[5] = (byte)(62);
      var10003[6] = (byte)(11);
      var10003[7] = (byte)(-77);
      var10003[8] = (byte)(7);
      var10003[9] = (byte)(-113);
      var10003[10] = (byte)(-105);
      var10003[11] = (byte)(-125);
      var10003[12] = (byte)(76);
      var10003[13] = (byte)(-94);
      var10003[14] = (byte)(50);
      var10003[15] = (byte)(-41);
      var10003[16] = (byte)(34);
      var10003[17] = (byte)(43);
      var10003[18] = (byte)(-94);
      var10003[19] = (byte)(-55);
      var10003[20] = (byte)(-94);
      var10003[21] = (byte)(-53);
      var10003[22] = (byte)(95);
      var10003[23] = (byte)(-7);
      var10003[24] = (byte)(-54);
      var10003[25] = (byte)(125);
      var10003[26] = (byte)(-6);
      Thread var1 = new Thread(runnable, $$v_s(var10003, -761801174130896834L));
      var1.setDaemon(true);
      var1.setPriority(4);
      return var1;
   });
   private final AtomicBoolean scanRunning = new AtomicBoolean();
   private final AtomicBoolean triangulationRunning = new AtomicBoolean();
   private volatile boolean triangulationPending = false;
   private Object lastWorld;
   private volatile long stateGeneration;
   private int tickCounter;
   private final List cachedVisibleChunks = new ArrayList();
   private volatile boolean dirty = true;
   private int lastPlayerChunkX = Integer.MIN_VALUE;
   private int lastPlayerChunkZ = Integer.MIN_VALUE;
   private int lastAlpha = -1;
   private int lastSensitivity = -1;
   private int lastSimDist = -1;
   private boolean lastSmartAdj = false;
   private int lastFilterMask = 0;
   private String cachedInfo = "";
   private final FlaggedChunkRenderer chunkRenderer = new FlaggedChunkRenderer();
   private final WorldProjector projector = new WorldProjector();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SusChunkFinder() {
      super(
         "SusChunkFinder",
         "Finds suspicious geode regions",
         Category.BASE_FINDING
      );
      this.chunkRenderer.setOutlineWidth(0.45F);
      this.chunkRenderer.setOutlineAlphaMultiplier(0.22F);
      this.chunkRenderer.setOutlineColorRgb(-31355);
   }

   public boolean isSmartAdjustment() {
      return (Boolean)this.smartAdjustment.get();
   }

   public void setSmartAdjustment(boolean enabled) {
      this.smartAdjustment.set(enabled);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.clearState();
      if (this.isInGame()) {
         this.lastWorld = mc.field_1687;
         this.enqueueNearbyLoadedChunks();
         this.requestAsyncTriangulation();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.clearState();
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent event) {
      if (this.isEnabled()) {
         class_1923 var2 = event.getChunkPos();
         if (event.isLoaded()) {
            this.enqueueChunk(var2.field_9181, var2.field_9180);
         } else {
            this.unloadChunk(var2);
         }
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.POST && this.isEnabled() && this.isInGame()) {
         if (mc.field_1687 != this.lastWorld) {
            this.clearState();
            this.lastWorld = mc.field_1687;
            this.enqueueNearbyLoadedChunks();
         }

         if (++this.tickCounter % (10) == 0) {
            this.enqueueNearbyLoadedChunks();
         }

         class_1923 var2 = mc.field_1724.method_31476();
         if (var2.field_9181 != this.lastPlayerChunkX || var2.field_9180 != this.lastPlayerChunkZ) {
            this.lastPlayerChunkX = var2.field_9181;
            this.lastPlayerChunkZ = var2.field_9180;
            this.pruneFarChunks();
            this.dirty = true;
         }

         int var3 = this.computeFilterMask();
         boolean var4 = (Integer)this.sensitivity.get() != this.lastSensitivity;
         if (var4
            || (Integer)this.simulationDistance.get() != this.lastSimDist
            || (Boolean)this.smartAdjustment.get() != this.lastSmartAdj
            || var3 != this.lastFilterMask) {
            this.lastSensitivity = (Integer)this.sensitivity.get();
            this.lastSimDist = (Integer)this.simulationDistance.get();
            this.lastSmartAdj = (Boolean)this.smartAdjustment.get();
            if (var3 != this.lastFilterMask || var4) {
               this.lastFilterMask = var3;
               this.scannedChunks.clear();
               this.enqueueNearbyLoadedChunks();
            }

            this.dirty = true;
         }

         this.drainScanQueue();
         if (this.dirty) {
            this.requestAsyncTriangulation();
         }
      }
   }

   private int computeFilterMask() {
      int var1 = 0;
      if ((Boolean)this.filterAmethyst.get()) {
         var1 |= 1;
      }

      if ((Boolean)this.filterVine.get()) {
         var1 |= 2;
      }

      if ((Boolean)this.filterBeeNest.get()) {
         var1 |= 4;
      }

      if ((Boolean)this.filterCacao.get()) {
         var1 |= 8;
      }

      if ((Boolean)this.filterSeagrass.get()) {
         var1 |= 16;
      }

      if ((Boolean)this.filterDripstone.get()) {
         var1 |= 32;
      }

      if ((Boolean)this.filterCaveVine.get()) {
         var1 |= 64;
      }

      if ((Boolean)this.filterKelp.get()) {
         var1 |= 128;
      }

      if ((Boolean)this.filterBamboo.get()) {
         var1 |= 256;
      }

      return var1;
   }

   private void enqueueNearbyLoadedChunks() {
      if (this.isInGame()) {
         class_1923 var1 = mc.field_1724.method_31476();
         int var2 = Math.max(1, mc.field_1690.method_38521());

         for (int var3 = var1.field_9181 - var2; var3 <= var1.field_9181 + var2; var3++) {
            for (int var4 = var1.field_9180 - var2; var4 <= var1.field_9180 + var2; var4++) {
               long var5 = class_1923.method_8331(var3, var4);
               if (!this.scannedChunks.contains(var5)
                  && !this.queuedChunks.contains(var5)
                  && mc.field_1687.method_2935().method_12126(var3, var4, false) != null) {
                  this.enqueueChunk(var3, var4);
               }
            }
         }
      }
   }

   private void enqueueChunk(int x, int z) {
      long var3 = class_1923.method_8331(x, z);
      if (!this.scannedChunks.contains(var3) && this.queuedChunks.add(var3)) {
         this.scanQueue.offer(var3);
      }
   }

   private void drainScanQueue() {
      if (this.scanRunning.compareAndSet(false, true)) {
         class_638 var1 = mc.field_1687;
         ArrayList var2 = new ArrayList();

         try {
            long var3 = System.nanoTime();

            for (int var5 = 0; var5 < 4 && (var5 <= 0 || System.nanoTime() - var3 <= 1500000L); var5++) {
               Long var6 = (Long)this.scanQueue.poll();
               if (var6 == null) {
                  break;
               }

               this.queuedChunks.remove(var6);
               this.scannedChunks.add(var6);
               int var7 = class_1923.method_8325(var6);
               int var8 = class_1923.method_8332(var6);
               class_2818 var9 = var1.method_2935().method_12126(var7, var8, false);
               if (var9 != null) {
                  if ((Boolean)this.filterAmethyst.get()) {
                     if (mayContainGeode(var9, var1)) {
                        var2.add(SusChunkScan.capture(var9, var1));
                     } else if (this.hits.remove(var6) != null
                        || this.geodeChunks.remove(var6)
                        || this.confirmedGrown.remove(var6)
                        || this.ungrown.remove(var6)) {
                        this.dirty = true;
                     }
                  }

                  boolean var10 = (Boolean)this.filterBeeNest.get()
                     || (Boolean)this.filterVine.get()
                     || (Boolean)this.filterCaveVine.get()
                     || (Boolean)this.filterKelp.get()
                     || (Boolean)this.filterBamboo.get()
                     || (Boolean)this.filterCacao.get()
                     || (Boolean)this.filterDripstone.get()
                     || (Boolean)this.filterSeagrass.get();
                  if (var10) {
                     SusGrowthScan.GrowthEvaluation var11 = SusGrowthScan.evaluateChunk(
                        var9,
                        (Boolean)this.filterBeeNest.get(),
                        (Boolean)this.filterVine.get(),
                        (Boolean)this.filterCaveVine.get(),
                        (Boolean)this.filterKelp.get(),
                        (Boolean)this.filterBamboo.get(),
                        (Boolean)this.filterCacao.get(),
                        (Boolean)this.filterDripstone.get(),
                        (Boolean)this.filterSeagrass.get()
                     );
                     if (var11.isSus() || var11.score() > 0) {
                        Integer var12 = this.growthScores.put(var6, var11.score());
                        boolean var13 = this.growthSusChunks.contains(var6);
                        boolean var14 = this.definiteGrowthSus.contains(var6);
                        if (var11.isSus()) {
                           this.growthSusChunks.add(var6);
                           this.definiteGrowthSus.add(var6);
                        } else {
                           this.growthSusChunks.remove(var6);
                           this.definiteGrowthSus.remove(var6);
                        }

                        if (var12 == null || var12 != var11.score() || var13 != var11.isSus() || var14 != var11.isSus()) {
                           this.dirty = true;
                        }
                     } else if (this.growthScores.remove(var6) != null || this.growthSusChunks.remove(var6) || this.definiteGrowthSus.remove(var6)) {
                        this.dirty = true;
                     }
                  } else if (this.growthScores.remove(var6) != null || this.growthSusChunks.remove(var6) || this.definiteGrowthSus.remove(var6)) {
                     this.dirty = true;
                  }
               }
            }
         } catch (Throwable var15) {
            this.scanRunning.set(false);
            VieClientMod.LOGGER
               .error(
                  "[SusChunkFinder] Failed to capture chunk data",
                  var15
               );
            return;
         }

         if (var2.isEmpty()) {
            this.scanRunning.set(false);
         } else {
            int var16 = scanThreshold((Integer)this.sensitivity.get());
            long var4 = this.stateGeneration;
            this.scanner
               .execute(
                  () -> {
                     ArrayList var6x = new ArrayList(var2.size());
                     Throwable var7x = null;

                     try {
                        for (SusChunkScan.ChunkSnapshot var9x : var2) {
                           var6x.add(SusChunkScan.analyze(var9x, var16));
                        }
                     } catch (Throwable var10x) {
                        var7x = var10x;
                     }

                     Throwable var11x = var7x;
                     mc.execute(
                        () -> {
                           try {
                              if (var11x != null) {
                                 VieClientMod.LOGGER
                                    .error(
                                       "[SusChunkFinder] Failed to analyze captured chunk data",
                                       var11x
                                    );
                              } else if (this.isEnabled() && mc.field_1687 == var1 && this.stateGeneration == var4) {
                                 for (SusChunkScan.Result var7xx : var6x) {
                                    this.applyResult(var7xx);
                                 }
                              }
                           } finally {
                              if (this.stateGeneration == var4) {
                                 this.scanRunning.set(false);
                              }
                           }
                        }
                     );
                  }
               );
         }
      }
   }

   private static boolean mayContainGeode(class_2818 chunk, class_638 world) {
      if (chunk == null) {
         return false;
      }

      class_2826[] var2 = chunk.method_12006();
      if (var2 != null && var2.length != 0) {
         int var3 = world.method_32891();
         int var4 = (-4) - var3;
         int var5 = (4) - var3;
         var4 = Math.max(0, var4);
         var5 = Math.min(var2.length - 1, var5);

         for (int var6 = var4; var6 <= var5; var6++) {
            class_2826 var7 = var2[var6];
            if (var7 != null && !var7.method_38292() && var7.method_19523(GEODE_BLOCK_PREDICATE)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void applyResult(SusChunkScan.Result result) {
      long var2 = result.chunkKey();
      boolean var4 = false;
      switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$SusChunkScan$Classification[result.classification().ordinal()]) {
         case 1:
            Integer var10 = this.hits.put(var2, result.hits());
            boolean var12 = this.geodeChunks.add(var2);
            boolean var14 = this.ungrown.remove(var2);
            boolean var16;
            if (!result.clusterPositions().isEmpty() && result.visibleBudding() != 0) {
               var16 = this.confirmedGrown.remove(var2);
            } else {
               var16 = this.confirmedGrown.add(var2);
            }

            if (var10 == null || var10 != result.hits() || var12 || var14 || var16) {
               var4 = true;
            }
            break;
         case 2:
            Integer var9 = this.hits.put(var2, result.hits());
            boolean var11 = this.geodeChunks.add(var2);
            boolean var13 = this.confirmedGrown.remove(var2);
            boolean var15 = this.ungrown.add(var2);
            if (var9 == null || var9 != result.hits() || var11 || var13 || var15) {
               var4 = true;
            }
            break;
         case 3:
            boolean var5 = this.hits.remove(var2) != null;
            boolean var6 = this.geodeChunks.remove(var2);
            boolean var7 = this.confirmedGrown.remove(var2);
            boolean var8 = this.ungrown.remove(var2);
            if (var5 || var6 || var7 || var8) {
               var4 = true;
            }
      }

      if (var4) {
         this.dirty = true;
      }
   }

   private void unloadChunk(class_1923 position) {
      long var2 = class_1923.method_8331(position.field_9181, position.field_9180);
      this.queuedChunks.remove(var2);
      this.scannedChunks.remove(var2);
      this.scanQueue.remove(var2);
      boolean var4 = this.hits.remove(var2) != null;
      boolean var5 = this.geodeChunks.remove(var2);
      boolean var6 = this.confirmedGrown.remove(var2);
      boolean var7 = this.ungrown.remove(var2);
      boolean var8 = this.growthSusChunks.remove(var2);
      boolean var9 = this.growthScores.remove(var2) != null;
      boolean var10 = this.definiteGrowthSus.remove(var2);
      if (var4 || var5 || var6 || var7 || var8 || var9 || var10) {
         this.dirty = true;
      }
   }

   private void pruneFarChunks() {
      class_1923 var1 = mc.field_1724.method_31476();
      int var2 = Math.max(1, mc.field_1690.method_38521());
      boolean var3 = false;
      if (this.hits.keySet().removeIf(key -> outside(key, var1, var2))) {
         var3 = true;
      }

      if (this.geodeChunks.removeIf(key -> outside(key, var1, var2))) {
         var3 = true;
      }

      if (this.confirmedGrown.removeIf(key -> outside(key, var1, var2))) {
         var3 = true;
      }

      if (this.ungrown.removeIf(key -> outside(key, var1, var2))) {
         var3 = true;
      }

      if (this.growthSusChunks.removeIf(key -> outside(key, var1, var2))) {
         var3 = true;
      }

      if (this.growthScores.keySet().removeIf(key -> outside(key, var1, var2))) {
         var3 = true;
      }

      if (this.definiteGrowthSus.removeIf(key -> outside(key, var1, var2))) {
         var3 = true;
      }

      this.scannedChunks.removeIf(key -> outside(key, var1, var2));
      this.queuedChunks.removeIf(key -> outside(key, var1, var2));
      this.scanQueue.removeIf(key -> outside(key, var1, var2));
      if (var3) {
         this.dirty = true;
      }
   }

   private void clearState() {
      this.stateGeneration++;
      this.scanRunning.set(false);
      this.triangulationRunning.set(false);
      this.triangulationPending = false;
      this.scanQueue.clear();
      this.queuedChunks.clear();
      this.scannedChunks.clear();
      this.hits.clear();
      this.geodeChunks.clear();
      this.confirmedGrown.clear();
      this.ungrown.clear();
      this.growthSusChunks.clear();
      this.growthScores.clear();
      this.definiteGrowthSus.clear();
      this.cachedVisibleChunks.clear();
      this.cachedInfo = "";
      this.dirty = true;
      this.tickCounter = 0;
      this.lastWorld = null;
      this.chunkRenderer.clear();
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (this.isEnabled() && this.isInGame()) {
         if (this.dirty) {
            this.requestAsyncTriangulation();
         }

         if ((Integer)this.alpha.get() != this.lastAlpha) {
            this.lastAlpha = (Integer)this.alpha.get();
            int var2 = Math.max(5, Math.min(255, (int)(this.lastAlpha * 2.55F)));
            this.chunkRenderer.syncKeys(this.cachedVisibleChunks, -57826, var2, 63.0);
         }

         if (!this.chunkRenderer.getVisuals().isEmpty()) {
            if (this.projector.snapshot()) {
               try (NanoVGFrame var7 = NanoVGRenderer.frame()) {
                  if (!var7.isActive()) {
                     return;
                  }

                  this.chunkRenderer.renderNanoVG(this.projector);
               }
            }
         }
      }
   }

   private void requestAsyncTriangulation() {
      this.dirty = false;
      if (this.isEnabled() && this.isInGame()) {
         if (!this.triangulationRunning.compareAndSet(false, true)) {
            this.triangulationPending = true;
         } else {
            this.triangulationPending = false;
            long var1 = this.stateGeneration;
            class_1923 var3 = mc.field_1724.method_31476();
            int var4 = Math.max(1, mc.field_1690.method_38521());
            int var5 = (Integer)this.sensitivity.get();
            int var6 = (Integer)this.simulationDistance.get();
            boolean var7 = (Boolean)this.smartAdjustment.get();
            boolean var8 = (Boolean)this.filterAmethyst.get();
            int var9 = var3.field_9181;
            int var10 = var3.field_9180;
            Map var11 = Map.copyOf(this.hits);
            Set var12 = Set.copyOf(this.geodeChunks);
            Set var13 = Set.copyOf(this.confirmedGrown);
            Map var14 = Map.copyOf(this.growthScores);
            Set var15 = Set.copyOf(this.definiteGrowthSus);
            int var16 = scanThreshold(var5);
            HashSet var17 = new HashSet();
            HashSet var18 = new HashSet();

            for (long var20 : var12) {
               int var22 = var11.getOrDefault(var20, 0);
               boolean var23 = var22 <= 20 ? var22 >= var5 : var22 >= var16;
               if (!var23 && (var22 != 0 || !var13.contains(var20) || var5 > 4)) {
                  var18.add(var20);
               } else {
                  var17.add(var20);
               }
            }

            int var24 = 40 + (Math.max(1, Math.min(20, var5)) - 1) * 80 / 19;
            HashSet var25 = new HashSet();

            for (Entry var26 : var14.entrySet()) {
               int var27 = (Integer)var26.getValue() + (var15.contains(var26.getKey()) ? 35 : 0);
               if (var27 >= var24) {
                  var25.add((Long)var26.getKey());
               }
            }

            this.triangulationWorker.execute(() -> {
               try {
                  if (this.stateGeneration == var1) {
                     int var16x = var11.values().stream().anyMatch(v -> v > (20)) ? var16 : var5;
                     List var17x = var8 ? SusChunkGeometry.calculateAdaptiveRegions(var11, var17, var18, var14, var16x, var6, true) : List.of();
                     ArrayList var18x = new ArrayList();
                     HashSet var19 = new HashSet();

                     for (SusChunkGeometry.Region var21 : var17x) {
                        for (int var22x = var21.minChunkX(); var22x <= var21.maxChunkX(); var22x++) {
                           for (int var23x = var21.minChunkZ(); var23x <= var21.maxChunkZ(); var23x++) {
                              long var24x = class_1923.method_8331(var22x, var23x);
                              if (Math.max(Math.abs(var22x - var9), Math.abs(var23x - var10)) <= var4 && var19.add(var24x)) {
                                 var18x.add(var24x);
                              }
                           }
                        }
                     }

                     for (long var31 : var25) {
                        int var36 = class_1923.method_8325(var31);
                        int var37 = class_1923.method_8332(var31);
                        if (Math.max(Math.abs(var36 - var9), Math.abs(var37 - var10)) <= var4 && var19.add(var31)) {
                           var18x.add(var31);
                        }
                     }

                     Map var30 = SusChunkGeometry.calculateChunkProbabilities(var17x, var11, var14, var25, var18x);
                     if (var7 && !var18x.isEmpty()) {
                        List var32 = SusChunkGeometry.filterSmartCandidates(var18x, var30, var5);
                        var18x.clear();
                        var18x.addAll(var32);
                     }

                     String var33;
                     if (var8) {
                        if (var17x.size() == 1 && var25.isEmpty()) {
                           SusChunkGeometry.Region var35 = (SusChunkGeometry.Region)var17x.getFirst();
                           var33 = var35.widthChunks() + "x" + var35.heightChunks() + "c (" + var35.geodeCount() + "g)";
                        } else {
                           int var34 = var17.size();
                           var33 = !var25.isEmpty() ? var34 + "g + " + var25.size() + " growth" : var34 + " geodes";
                        }
                     } else {
                        var33 = var25.size() + " sus chunks";
                     }

                     mc.execute(() -> {
                        if (this.stateGeneration == var1 && this.isEnabled()) {
                           this.cachedVisibleChunks.clear();
                           this.cachedVisibleChunks.addAll(var18x);
                           this.cachedInfo = var33;
                           this.lastAlpha = (Integer)this.alpha.get();
                           int var5x = Math.max(5, Math.min(255, (int)(this.lastAlpha * 2.55F)));
                           this.chunkRenderer.syncKeys(this.cachedVisibleChunks, -57826, var5x, 63.0);
                        }
                     });
                     return;
                  }
               } finally {
                  this.triangulationRunning.set(false);
                  if (this.triangulationPending && this.stateGeneration == var1 && this.isEnabled()) {
                     mc.execute(this::requestAsyncTriangulation);
                  }
               }
            });
         }
      }
   }

   private static boolean outside(long key, class_1923 player, int radius) {
      return Math.max(Math.abs(class_1923.method_8325(key) - player.field_9181), Math.abs(class_1923.method_8332(key) - player.field_9180)) > radius;
   }

   private static int scanThreshold(int sensitivity) {
      int var1 = Math.max(1, Math.min(20, sensitivityx));
      return (50) + (var1 - 1) * (50) / (19);
   }

   private static int withOpacity(int color, int percent) {
      int var2 = (color >>> (24)) * Math.max(0, Math.min(100, percent)) / (100);
      return var2 << (24) | color & (16777215);
   }

   @Override
   public String getInfo() {
      return this.cachedInfo;
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
