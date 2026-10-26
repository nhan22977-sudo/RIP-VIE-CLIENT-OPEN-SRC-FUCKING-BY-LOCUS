package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ChunkDataEvent;
import me.drake.vieclient.core.event.events.ChunkLoadEvent;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.LightUpdateEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.modules.donut.chunkdebug.ChunkConfidence;
import me.drake.vieclient.modules.donut.chunkdebug.ChunkDebugCache;
import me.drake.vieclient.modules.donut.chunkdebug.ChunkDebugRadarRenderer;
import me.drake.vieclient.modules.donut.chunkdebug.LightPayload;
import me.drake.vieclient.modules.donut.chunkdebug.MachineEvidence;
import me.drake.vieclient.modules.donut.chunkdebug.MachineEvidenceTickBudget;
import me.drake.vieclient.render.fx.FlaggedChunkRenderer;
import me.drake.vieclient.render.fx.WorldProjector;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.DebugChunkToast;
import me.drake.vieclient.util.render.ToastManager;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2672;
import net.minecraft.class_2676;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_374;
import net.minecraft.class_638;

public final class ChunkDebug extends Module {
   private static final int MACHINE_SCAN_CEILING_Y = 1;
   private static final int MAX_MACHINE_SWEEPS_PER_TICK = 8;
   private static final Map MACHINE_COMPONENTS;
   private final BoolSetting toast = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Toast",
         "Show a toast when a chunk is flagged",
         true
      )
   );
   private final BoolSetting chatNotify = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Chat Notify",
         "Write flagged chunks to local chat",
         true
      )
   );
   private final BoolSetting disconnectOnChunk = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Disconnect on Chunk",
         "Disconnect when a chunk is flagged",
         false
      )
   );
   private final BoolSetting tracer = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Tracer",
         "Draw a tier-colored tracer to visible chunk markers",
         true
      )
   );
   private final IntSetting simulationTicks = (IntSetting)this.addSetting(
      new IntSetting(
         "Simulation Ticks",
         "Simulation evaluation ticks before confirming chunk alert",
         6,
         1,
         60
      )
   );
   private final IntSetting radius = (IntSetting)this.addSetting(
      new IntSetting(
         "Radius",
         "Chunk retention and radar radius from center out (chunks)",
         64,
         8,
         64
      )
   );
   private final BoolSetting autoCleanup = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Auto Cleanup",
         "Clean up flagged chunks outside radar radius",
         true
      )
   );
   private final IntSetting alpha = (IntSetting)this.addSetting(
      new IntSetting(
         "Alpha",
         "Opacity of the chunk radar (0-255)",
         140,
         0,
         255
      )
   );
   private final Map pendingSimCandidates = new HashMap();
   private final Map announcedAlerts = new HashMap();
   private final ChunkDebugCache cache = new ChunkDebugCache();
   private final ChunkDebugRadarRenderer radarRenderer = new ChunkDebugRadarRenderer();
   private final FlaggedChunkRenderer radarChunkRenderer = new FlaggedChunkRenderer();
   private final WorldProjector projector = new WorldProjector();
   private final Map cachedAlerts = new HashMap();
   private long lastAlertsVersion = -1L;
   private int lastAlpha = -1;
   private class_638 lastWorld;
   private volatile float vanillaFarPlaneDistance;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ChunkDebug() {
      super(
         "Chunk Debug",
         "Marks anomalous chunk light and grades machine evidence",
         Category.BASE_FINDING
      );
      this.simulationTicks.set(6);
      this.simulationTicks.setVisibleInGui(false);
      this.radius.set(64);
      this.radius.setVisibleInGui(false);
      this.autoCleanup.setVisibleInGui(false);
      this.radarChunkRenderer.setOutlineWidth(0.5F);
      this.radarChunkRenderer.setOutlineAlphaMultiplier(0.35F);
   }

   @Override
   public void onEnable() {
      this.resetForCurrentWorld();
      this.cache.activateIntake();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.cache.deactivateIntake();
      this.cache.resetAlerts();
      this.radarRenderer.reset();
      this.radarChunkRenderer.clear();
      this.cachedAlerts.clear();
      this.pendingSimCandidates.clear();
      this.announcedAlerts.clear();
      this.lastAlertsVersion = -1L;
      this.lastAlpha = -1;
      this.lastWorld = null;
      super.onDisable();
   }

   @EventHandler
   private void onChunkData(ChunkDataEvent event) {
      class_2672 var2 = event.getPacket();
      this.enqueue(var2.method_11523(), var2.method_11524(), LightPayload.copyOf(var2.method_38599()));
   }

   @EventHandler
   private void onLightUpdate(LightUpdateEvent event) {
      class_2676 var2 = event.getPacket();
      this.enqueue(var2.method_11558(), var2.method_11554(), LightPayload.copyOf(var2.method_38600()));
   }

   @EventHandler
   private void onGameJoin(GameJoinEvent event) {
      if (this.isEnabled()) {
         this.resetForCurrentWorld();
      }
   }

   @EventHandler
   private void onChunkLoad(ChunkLoadEvent event) {
      if (this.isEnabled() && this.isInGame() && (Boolean)this.autoCleanup.get()) {
         if (event.isUnloaded()) {
            this.cache.removeChunk(event.getChunkPos().field_9181, event.getChunkPos().field_9180);
         }
      }
   }

   private static long packChunk(int chunkX, int chunkZ) {
      return chunkX & 4294967295L | (long)chunkZ << (32);
   }

   @EventHandler
   private void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE && this.isInGame()) {
         if (mc.field_1687 != this.lastWorld) {
            this.resetForCurrentWorld();
         }

         if ((Boolean)this.autoCleanup.get()) {
            int var2 = mc.field_1724.method_31476().field_9181;
            int var3 = mc.field_1724.method_31476().field_9180;
            this.cache.cleanupOutOfDistance(var2, var3, (Integer)this.radius.get());
         }

         MachineEvidenceTickBudget var19 = new MachineEvidenceTickBudget(8);

         for (ChunkDebugCache.ChunkAlert var4 : this.cache
            .drain(mc.field_1687.method_32890(), mc.field_1687.method_32891(), (chunkX, chunkZ) -> var19.resolve(chunkX, chunkZ, this::machineEvidence))) {
            long var5 = packChunk(var4.chunkX(), var4.chunkZ());
            ChunkConfidence var7 = (ChunkConfidence)this.announcedAlerts.get(var5);
            if (var7 == null || var4.confidence().compareTo(var7) > 0) {
               ChunkDebug.SimCandidate var8 = this.pendingSimCandidates.computeIfAbsent(var5, k -> new ChunkDebug.SimCandidate(var4));
               if (var8.latestAlert == null || var4.confidence().compareTo(var8.latestAlert.confidence()) > 0) {
                  var8.latestAlert = var4;
               }

               var8.updates++;
            }
         }

         int var21 = Math.max(1, (Integer)this.simulationTicks.get());
         int var22 = mc.field_1724.method_31476().field_9181;
         int var23 = mc.field_1724.method_31476().field_9180;
         Iterator var6 = this.pendingSimCandidates.entrySet().iterator();

         while (var6.hasNext()) {
            Entry var24 = (Entry)var6.next();
            long var25 = (Long)var24.getKey();
            ChunkDebug.SimCandidate var10 = (ChunkDebug.SimCandidate)var24.getValue();
            var10.ticksObserved++;
            int var11 = var10.latestAlert.chunkX();
            int var12 = var10.latestAlert.chunkZ();
            if (Math.abs(var11 - var22) <= (Integer)this.radius.get() && Math.abs(var12 - var23) <= (Integer)this.radius.get()) {
               int var13 = var10.latestAlert.confidence() == ChunkConfidence.HIGH ? Math.min(3, var21) : var21;
               if (var10.ticksObserved >= var13) {
                  class_2818 var14 = mc.field_1687.method_8497(var11, var12);
                  boolean var15 = false;
                  ChunkConfidence var16 = var10.latestAlert.confidence();
                  if (var14 != null) {
                     int var17 = var14.method_12214().size();
                     boolean var18 = var10.latestAlert.cluster() != null && var10.latestAlert.cluster().isStrong();
                     if (var10.updates >= 2 || var18 || var17 > 0) {
                        var15 = true;
                        if (var18 && var17 >= 2) {
                           var16 = ChunkConfidence.HIGH;
                        } else if (var16 == ChunkConfidence.LOW && (var10.updates >= 3 || var17 > 0)) {
                           var16 = ChunkConfidence.MEDIUM;
                        }
                     } else if (var10.ticksObserved >= var21 * 2) {
                        var15 = true;
                     }
                  } else if (var10.updates >= 2 || var10.ticksObserved >= var21) {
                     var15 = true;
                  }

                  if (var15) {
                     ChunkConfidence var26 = (ChunkConfidence)this.announcedAlerts.get(var25);
                     if (var26 == null || var16.compareTo(var26) > 0) {
                        this.announcedAlerts.put(var25, var16);
                        ChunkDebugCache.ChunkAlert var27 = new ChunkDebugCache.ChunkAlert(
                           var11,
                           var12,
                           var10.latestAlert.sectionY(),
                           var10.latestAlert.track(),
                           var10.latestAlert.status(),
                           var16,
                           var10.updates,
                           var10.latestAlert.cluster()
                        );
                        this.announce(var27);
                     }

                     var6.remove();
                  } else if (var10.ticksObserved > var21 * 3) {
                     var6.remove();
                  }
               }
            } else {
               var6.remove();
            }
         }
      }
   }

   @EventHandler
   private void onPostBlit(PostBlitRenderEvent event) {
      if (this.isInGame() && this.isEnabled()) {
         int var2 = Math.max(0, Math.min(255, (Integer)this.alpha.get()));
         long var3 = this.cache.getAlertsVersion();
         if (var3 != this.lastAlertsVersion || var2 != this.lastAlpha) {
            this.lastAlertsVersion = var3;
            this.lastAlpha = var2;
            this.cachedAlerts.clear();
            this.cachedAlerts.putAll(this.cache.getAlertHistory());
            this.radarChunkRenderer.syncAlerts(this.cachedAlerts, var2, 63.0, 16.0);
         }

         if (!this.radarChunkRenderer.getVisuals().isEmpty()) {
            if (this.projector.snapshot()) {
               try (NanoVGFrame var5 = NanoVGRenderer.frame()) {
                  if (!var5.isActive()) {
                     return;
                  }

                  this.radarChunkRenderer.renderNanoVG(this.projector);
               }
            }
         }
      }
   }

   @EventHandler
   private void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D && this.isInGame() && !this.radarChunkRenderer.getVisuals().isEmpty()) {
         int var2 = Math.max(0, Math.min(255, (Integer)this.alpha.get()));
         this.radarRenderer.advance(event.getFrameTime());
         class_243 var3 = mc.field_1773.method_19418().method_71156();
         boolean var4 = this.projector.snapshot();

         for (FlaggedChunkRenderer.FlaggedChunkVisual var6 : this.radarChunkRenderer.getVisuals()) {
            if (!(var6.getAlpha() <= 0.005F)) {
               long var7 = var6.getId();
               int var9 = (int)var7;
               int var10 = (int)(var7 >>> (32));
               ChunkConfidence var11 = (ChunkConfidence)this.cachedAlerts.get(var7);
               if (var11 == null) {
                  var11 = this.cache.confidenceAt(var7);
               }

               if (var11 == null) {
                  var11 = ChunkConfidence.LOW;
               }

               double var12 = var9 * 16.0 + 8.0;
               double var14 = var10 * 16.0 + 8.0;
               double var16 = Math.hypot(var12 - var3.field_1352, var14 - var3.field_1350);
               int var18 = (int)(var6.getAlpha() * var2);
               if ((Boolean)this.tracer.get()) {
                  this.radarRenderer.drawTracer(var12, var14, var11, var18, event.getTickDelta());
               }

               if (!var4 || !(this.projector.getCameraDepth(var12, 75.0, var14) < -48.0)) {
                  double var19 = ((Integer)this.radius.get()).intValue() * 16.0 + 48.0;
                  if (var16 <= var19) {
                     this.radarRenderer.render(var7, var11, var18, var16);
                  }
               }
            }
         }
      }
   }

   public boolean hasAlertedChunks() {
      return this.cache.hasAlertedChunks();
   }

   public float requiredFarPlaneDistance(double playerX, double playerZ, float currentDistance) {
      return this.cache.requiredFarPlaneDistance(playerX, playerZ, currentDistance);
   }

   public void setVanillaFarPlaneDistance(float distance) {
      this.vanillaFarPlaneDistance = Math.max(0.0F, distance);
   }

   private void announce(ChunkDebugCache.ChunkAlert alert) {
      if (alert != null && alert.status() != null) {
         String var2 = "§b[Chunk Debug] §f" + alert.confidence() + " §7" + alert.chunkX() + ", " + alert.chunkZ();
         if ((Boolean)this.chatNotify.get()) {
            mc.field_1724.method_7353(class_2561.method_43470(var2), false);
         }

         ToastManager.pushNotification(
            "Chunk ",
            "Debug " + alert.confidence(),
            alert.chunkX() + ", " + alert.chunkZ(),
            "basefinding",
            "Flagged",
            true
         );
         if ((Boolean)this.toast.get() && mc.method_1566() != null) {
            class_1923 var3 = new class_1923(alert.chunkX(), alert.chunkZ());
            mc.execute(() -> {
               class_374 var10000 = mc.method_1566();
               byte[] var10003 = new byte[17];
               var10003[0] = (byte)(111);
               var10003[1] = (byte)(-14);
               var10003[2] = (byte)(105);
               var10003[3] = (byte)(-80);
               var10003[4] = (byte)(8);
               var10003[5] = (byte)(37);
               var10003[6] = (byte)(-53);
               var10003[7] = (byte)(10);
               var10003[8] = (byte)(38);
               var10003[9] = (byte)(-41);
               var10003[10] = (byte)(-125);
               var10003[11] = (byte)(-42);
               var10003[12] = (byte)(93);
               var10003[13] = (byte)(-78);
               var10003[14] = (byte)(-46);
               var10003[15] = (byte)(121);
               var10003[16] = (byte)(24);
               var10000.method_1999(new DebugChunkToast($$v_s(var10003, 5155737404030832649L), var3, new class_1799(class_1802.field_8725)));
            });
         }

         if ((Boolean)this.disconnectOnChunk.get() && mc.method_1562() != null) {
            this.disconnectOnChunk.set(false);
            mc.method_1562()
               .method_48296()
               .method_10747(
                  class_2561.method_43470(
                     "Chunk Debug flagged a chunk"
                  )
               );
         }
      }
   }

   private MachineEvidence machineEvidence(int chunkX, int chunkZ) {
      if (!this.isInGame()) {
         return MachineEvidence.NONE;
      }

      class_2818 var3 = mc.field_1687.method_8497(chunkX, chunkZ);
      if (var3 == null) {
         return MachineEvidence.NONE;
      }

      class_2826[] var4 = var3.method_12006();
      if (var4 != null && var4.length != 0) {
         int var5 = var3.method_31607();
         MachineEvidence.Builder var6 = null;
         int var7 = chunkX << (4);
         int var8 = chunkZ << (4);

         for (int var9 = 0; var9 < var4.length; var9++) {
            int var10 = var5 + (var9 << (4));
            if (var10 >= 1) {
               break;
            }

            class_2826 var11 = var4[var9];
            if (var11 != null && !var11.method_38292() && var11.method_19523(state -> MACHINE_COMPONENTS.containsKey(state.method_26204()))) {
               if (var6 == null) {
                  var6 = MachineEvidence.builder();
               }

               int var12 = Math.min(16, 1 - var10);

               for (int var13 = 0; var13 < var12; var13++) {
                  int var14 = var10 + var13;

                  for (int var15 = 0; var15 < (16); var15++) {
                     for (int var16 = 0; var16 < (16); var16++) {
                        class_2248 var17 = var11.method_12254(var16, var13, var15).method_26204();
                        MachineEvidence.ComponentType var18 = (MachineEvidence.ComponentType)MACHINE_COMPONENTS.get(var17);
                        if (var18 != null) {
                           var6.add(var7 + var16, var14, var8 + var15, var18);
                        }
                     }
                  }
               }
            }
         }

         return var6 != null ? var6.build() : MachineEvidence.NONE;
      } else {
         return MachineEvidence.NONE;
      }
   }

   private void enqueue(int chunkX, int chunkZ, LightPayload payload) {
      if (this.isEnabled() && mc.field_1687 != null) {
         if (mc.field_1687 != this.lastWorld) {
            this.resetForCurrentWorld();
         }

         this.cache.offer(chunkX, chunkZ, payload, System.nanoTime());
      }
   }

   private void resetForCurrentWorld() {
      this.cache.resetWorld();
      this.lastWorld = mc.field_1687;
      this.vanillaFarPlaneDistance = 0.0F;
      this.radarRenderer.reset();
      this.radarChunkRenderer.clear();
      this.cachedAlerts.clear();
      this.pendingSimCandidates.clear();
      this.announcedAlerts.clear();
      this.lastAlertsVersion = -1L;
      this.lastAlpha = -1;
   }

   static {
      Entry[] var10000 = new Entry[11];
      var10000[0] = Map.entry(class_2246.field_10282, MachineEvidence.ComponentType.OBSERVER);
      var10000[1] = Map.entry(class_2246.field_10560, MachineEvidence.ComponentType.PISTON);
      var10000[2] = Map.entry(class_2246.field_10615, MachineEvidence.ComponentType.STICKY_PISTON);
      var10000[3] = Map.entry(class_2246.field_10450, MachineEvidence.ComponentType.REPEATER);
      var10000[4] = Map.entry(class_2246.field_10377, MachineEvidence.ComponentType.COMPARATOR);
      var10000[5] = Map.entry(class_2246.field_10091, MachineEvidence.ComponentType.REDSTONE_WIRE);
      var10000[6] = Map.entry(class_2246.field_10312, MachineEvidence.ComponentType.HOPPER);
      var10000[7] = Map.entry(class_2246.field_10200, MachineEvidence.ComponentType.DISPENSER);
      var10000[8] = Map.entry(class_2246.field_10228, MachineEvidence.ComponentType.DROPPER);
      var10000[9] = Map.entry(class_2246.field_10524, MachineEvidence.ComponentType.REDSTONE_LAMP);
      var10000[10] = Map.entry(class_2246.field_10179, MachineEvidence.ComponentType.NOTE_BLOCK);
      MACHINE_COMPONENTS = Map.ofEntries(var10000);
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

   private static final class SimCandidate {
      ChunkDebugCache.ChunkAlert latestAlert;
      int ticksObserved = 0;
      int updates = 1;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      SimCandidate(ChunkDebugCache.ChunkAlert alert) {
         this.latestAlert = alert;
      }
   }
}
