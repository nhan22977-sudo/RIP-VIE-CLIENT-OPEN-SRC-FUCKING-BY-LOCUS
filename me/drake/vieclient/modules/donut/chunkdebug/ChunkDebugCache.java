package me.drake.vieclient.modules.donut.chunkdebug;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

public final class ChunkDebugCache {
   public static final int MAX_DRAIN_PER_TICK = 4096;
   static final int MAX_CACHED_SNAPSHOTS = 8192;
   static final int MAX_ALERTED_CHUNKS = 512;
   private static final int DETECTION_CUTOFF_Y = 1;
   private static final int SECTION_HEIGHT = 16;
   private static final long MIN_CONFIRMATION_GAP_NANOS = 1000000000L;
   private static final long MAX_CONFIRMATION_GAP_NANOS = 90000000000L;
   private static final int MAX_PENDING_CONFIRMATIONS = 8192;
   private static final long CONFIRMATION_CLEANUP_INTERVAL_NANOS = 1000000000L;
   private static final float FAR_PLANE_MARGIN = 256.0F;
   private static final float FAR_PLANE_CAP = 3.0E7F;
   private final Object intakeLock = new Object();
   private final PacketIntake intake = new PacketIntake();
   private final LinkedHashMap snapshots = new LinkedHashMap(16, 0.75F, true);
   private final LinkedHashMap alertHistory = new LinkedHashMap(16, 0.75F, true);
   private final LinkedHashMap confirmations = new LinkedHashMap(16, 0.75F, true);
   private boolean acceptingPackets = true;
   private long lastConfirmationCleanupNanos = Long.MIN_VALUE;
   private long alertsVersion = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public boolean offer(int chunkX, int chunkZ, LightPayload payload, long arrivalNanos) {
      if (payload == null) {
         return false;
      }

      synchronized (this.intakeLock) {
         return this.acceptingPackets && this.intake.offer(new PacketIntake.PendingLight(chunkX, chunkZ, payload, arrivalNanos));
      }
   }

   public List drain(int sectionCount, int bottomSectionY, BiFunction machineEvidence) {
      ArrayList var4 = new ArrayList();

      PacketIntake.PendingLight var5;
      for (int var6 = 0; var6 < (4096) && (var5 = this.intake.poll()) != null; var6++) {
         this.ingest(var5, sectionCount, bottomSectionY, machineEvidence, var4);
      }

      return var4;
   }

   public long getAlertsVersion() {
      return this.alertsVersion;
   }

   public Map getAlertHistory() {
      return Collections.unmodifiableMap(this.alertHistory);
   }

   public void resetAlerts() {
      if (!this.alertHistory.isEmpty() || !this.confirmations.isEmpty()) {
         this.alertsVersion++;
      }

      this.alertHistory.clear();
      this.confirmations.clear();
      this.lastConfirmationCleanupNanos = Long.MIN_VALUE;
   }

   public void removeChunk(int chunkX, int chunkZ) {
      this.removeChunk(packChunk(chunkX, chunkZ));
   }

   public void removeChunk(long chunkKey) {
      if (this.alertHistory.remove(chunkKey) != null) {
         this.alertsVersion++;
      }

      this.snapshots.remove(chunkKey);
      this.confirmations.keySet().removeIf(k -> k.chunkKey() == chunkKey);
   }

   public void cleanupOutOfDistance(int playerChunkX, int playerChunkZ, int renderDistance) {
      boolean var4 = false;
      Iterator var5 = this.alertHistory.entrySet().iterator();

      while (var5.hasNext()) {
         long var6 = (Long)((Entry)var5.next()).getKey();
         int var8 = (int)var6;
         int var9 = (int)(var6 >>> (32));
         if (Math.abs(var8 - playerChunkX) > renderDistance || Math.abs(var9 - playerChunkZ) > renderDistance) {
            var5.remove();
            var4 = true;
            this.snapshots.remove(var6);
            this.confirmations.keySet().removeIf(k -> k.chunkKey() == var6);
         }
      }

      if (var4) {
         this.alertsVersion++;
      }
   }

   public void resetWorld() {
      this.snapshots.clear();
      this.resetAlerts();
      synchronized (this.intakeLock) {
         this.intake.clear();
      }
   }

   public void deactivateIntake() {
      synchronized (this.intakeLock) {
         this.acceptingPackets = false;
         this.intake.clear();
      }
   }

   public void activateIntake() {
      synchronized (this.intakeLock) {
         this.acceptingPackets = true;
      }
   }

   public boolean hasAlertedChunks() {
      return !this.alertHistory.isEmpty();
   }

   public List alertedChunkKeys() {
      return List.copyOf(this.alertHistory.keySet());
   }

   public float requiredFarPlaneDistance(double playerX, double playerZ, float currentDistance) {
      double var6 = currentDistance;

      for (Entry var9 : this.alertHistory.entrySet()) {
         if (var9.getValue() != null) {
            long var10 = (Long)var9.getKey();
            double var12 = ((int)var10 << (4)) + 8.0;
            double var14 = ((int)(var10 >>> (32)) << (4)) + 8.0;
            var6 = Math.max(var6, Math.hypot(var12 - playerX, var14 - playerZ) + 256.0);
         }
      }

      return (float)Math.min(var6, 3.0E7);
   }

   public ChunkConfidence confidenceAt(int chunkX, int chunkZ) {
      return (ChunkConfidence)this.alertHistory.get(packChunk(chunkX, chunkZ));
   }

   public ChunkConfidence confidenceAt(long chunkKey) {
      return (ChunkConfidence)this.alertHistory.get(chunkKey);
   }

   int snapshotCount() {
      return this.snapshots.size();
   }

   int pendingConfirmationCount() {
      return this.confirmations.size();
   }

   int alertedChunkCount() {
      return this.alertHistory.size();
   }

   long droppedCount() {
      return this.intake.droppedCount();
   }

   private void ingest(PacketIntake.PendingLight pending, int sectionCount, int bottomSectionY, BiFunction machineEvidence, List alerts) {
      if (this.shouldExpireConfirmations(pending.arrivalNanos())) {
         this.expireConfirmations(pending.arrivalNanos());
      }

      long var6 = packChunk(pending.chunkX(), pending.chunkZ());
      ChunkLightSnapshot var8 = (ChunkLightSnapshot)this.snapshots.get(var6);
      if (var8 == null || var8.sectionCount != sectionCount || var8.bottomSectionY != bottomSectionY) {
         var8 = new ChunkLightSnapshot(sectionCount, bottomSectionY);
         this.rememberSnapshot(var6, var8);
      }

      LightPayload var9 = pending.payload();
      LightDataClassifier.applyTrack(var9.initedBlock(), var9.uninitedBlock(), var9.blockNibbles(), var8.block, var8.blockUpdatedNanos, pending.arrivalNanos());
      LightDataClassifier.applyTrack(var9.initedSky(), var9.uninitedSky(), var9.skyNibbles(), var8.sky, var8.skyUpdatedNanos, pending.arrivalNanos());
      ChunkDebugCache.MachineEvidenceLease var10 = new ChunkDebugCache.MachineEvidenceLease(machineEvidence, pending.chunkX(), pending.chunkZ());

      for (int var11 = 1; var11 <= sectionCount; var11++) {
         int var12 = (bottomSectionY + var11 - 1) * (16);
         if (var12 < 1) {
            this.inspectTrack(
               pending.chunkX(),
               pending.chunkZ(),
               var6,
               var12,
               ChunkDebugCache.LightTrack.SKY,
               var8.sky[var11],
               var8.skyUpdatedNanos[var11],
               pending.arrivalNanos(),
               var10,
               alerts
            );
            this.inspectTrack(
               pending.chunkX(),
               pending.chunkZ(),
               var6,
               var12,
               ChunkDebugCache.LightTrack.BLOCK,
               var8.block[var11],
               var8.blockUpdatedNanos[var11],
               pending.arrivalNanos(),
               var10,
               alerts
            );
         }
      }
   }

   private void inspectTrack(
      int chunkX,
      int chunkZ,
      long chunkKey,
      int sectionY,
      ChunkDebugCache.LightTrack track,
      SectionStatus status,
      long updatedNanos,
      long arrivalNanos,
      ChunkDebugCache.MachineEvidenceLease lease,
      List alerts
   ) {
      if (updatedNanos == arrivalNanos) {
         ChunkDebugCache.ConfirmationKey var14 = new ChunkDebugCache.ConfirmationKey(chunkKey, sectionY, track);
         if (!isCandidate(status)) {
            this.confirmations.remove(var14);
         } else {
            ChunkConfidence var15 = (ChunkConfidence)this.alertHistory.get(chunkKey);
            if (var15 != ChunkConfidence.HIGH) {
               MachineEvidence var16 = lease.get();
               if (!var16.hasStrongCluster()) {
                  this.confirmations.remove(var14);
               }

               int var17 = 1;
               if (var16.hasStrongCluster()) {
                  ChunkDebugCache.Confirmation var18 = (ChunkDebugCache.Confirmation)this.confirmations.get(var14);
                  if (var18 != null && var18.status() == status) {
                     long var19 = arrivalNanos - var18.lastNanos();
                     if (var19 >= 1000000000L && var19 <= 90000000000L) {
                        var17 = var18.count() + 1;
                     } else if (var19 >= 0L && var19 < 1000000000L) {
                        var17 = var18.count();
                     }
                  }

                  if (var18 == null || arrivalNanos - var18.lastNanos() >= 1000000000L || var18.status() != status) {
                     this.remember(var14, new ChunkDebugCache.Confirmation(status, arrivalNanos, var17));
                  }
               }

               ChunkConfidence var21 = ChunkConfidence.fromEvidence(var16.hasStrongCluster(), var16.hasVeryStrongCluster(), var17);
               if (var15 == null || var21.compareTo(var15) > 0) {
                  this.rememberAlert(chunkKey, var21);
                  alerts.add(new ChunkDebugCache.ChunkAlert(chunkX, chunkZ, sectionY, track, status, var21, var17, var16.strongestCluster()));
               }
            }
         }
      }
   }

   private void remember(ChunkDebugCache.ConfirmationKey key, ChunkDebugCache.Confirmation confirmation) {
      if (!this.confirmations.containsKey(key) && this.confirmations.size() >= (8192)) {
         Iterator var3 = this.confirmations.keySet().iterator();
         if (var3.hasNext()) {
            var3.next();
            var3.remove();
         }
      }

      this.confirmations.put(key, confirmation);
   }

   private void rememberSnapshot(long chunkKey, ChunkLightSnapshot snapshot) {
      if (!this.snapshots.containsKey(chunkKey) && this.snapshots.size() >= (8192)) {
         Iterator var4 = this.snapshots.keySet().iterator();
         if (var4.hasNext()) {
            var4.next();
            var4.remove();
         }
      }

      this.snapshots.put(chunkKey, snapshot);
   }

   private void rememberAlert(long chunkKey, ChunkConfidence confidence) {
      if (!this.alertHistory.containsKey(chunkKey) && this.alertHistory.size() >= (512)) {
         Iterator var4 = this.alertHistory.keySet().iterator();
         if (var4.hasNext()) {
            var4.next();
            var4.remove();
            this.alertsVersion++;
         }
      }

      ChunkConfidence var5 = this.alertHistory.put(chunkKey, confidence);
      if (var5 != confidence) {
         this.alertsVersion++;
      }
   }

   private boolean shouldExpireConfirmations(long arrivalNanos) {
      if (this.lastConfirmationCleanupNanos != Long.MIN_VALUE && arrivalNanos - this.lastConfirmationCleanupNanos < 1000000000L) {
         return false;
      }

      this.lastConfirmationCleanupNanos = arrivalNanos;
      return true;
   }

   private void expireConfirmations(long arrivalNanos) {
      Iterator var3 = this.confirmations.entrySet().iterator();

      while (var3.hasNext()) {
         ChunkDebugCache.Confirmation var4 = (ChunkDebugCache.Confirmation)((Entry)var3.next()).getValue();
         if (arrivalNanos - var4.lastNanos() > 90000000000L) {
            var3.remove();
         }
      }
   }

   private static boolean isCandidate(SectionStatus status) {
      return status == SectionStatus.ZEROED_PRESENT || status == SectionStatus.UNIFORM_MAX;
   }

   private static long packChunk(int chunkX, int chunkZ) {
      return chunkX & 4294967295L | (long)chunkZ << (32);
   }

   public record ChunkAlert(
      int chunkX,
      int chunkZ,
      int sectionY,
      ChunkDebugCache.LightTrack track,
      SectionStatus status,
      ChunkConfidence confidence,
      int confirmations,
      MachineEvidence.Cluster cluster
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record Confirmation(SectionStatus status, long lastNanos, int count) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record ConfirmationKey(long chunkKey, int sectionY, ChunkDebugCache.LightTrack track) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum LightTrack {
      SKY,
      BLOCK;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static ChunkDebugCache.LightTrack[] $values() {
         ChunkDebugCache.LightTrack[] var10000 = new ChunkDebugCache.LightTrack[2];
         var10000[0] = SKY;
         var10000[1] = BLOCK;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-47);
         var10002[2] = (byte)(69);
         SKY = new ChunkDebugCache.LightTrack();
         var10002 = new byte[5];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-99);
         var10002[4] = (byte)(-120);
         BLOCK = new ChunkDebugCache.LightTrack();
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

   private static final class MachineEvidenceLease {
      private final BiFunction source;
      private final int chunkX;
      private final int chunkZ;
      private MachineEvidence resolved;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      MachineEvidenceLease(BiFunction source, int chunkX, int chunkZ) {
         this.source = sourcex;
         this.chunkX = chunkXx;
         this.chunkZ = chunkZx;
      }

      MachineEvidence get() {
         if (this.resolved == null) {
            MachineEvidence var1 = this.source == null ? null : (MachineEvidence)this.source.apply(this.chunkX, this.chunkZ);
            this.resolved = var1 == null ? MachineEvidence.NONE : var1;
         }

         return this.resolved;
      }
   }
}
