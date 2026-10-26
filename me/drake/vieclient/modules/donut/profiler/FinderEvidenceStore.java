package me.drake.vieclient.modules.donut.profiler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class FinderEvidenceStore {
   public static final int DEFAULT_CAPACITY = 512;
   private final int capacity;
   private final Map values = new LinkedHashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FinderEvidenceStore() {
      this(512);
   }

   public FinderEvidenceStore(int capacity) {
      this.capacity = Math.max(1, capacityx);
   }

   public synchronized FinderEvidenceStore.UpdateResult put(
      FinderEvidenceStore.SessionDimension dimension, long chunkKey, FinderConfidence confidence, double score, int sampleCount, String reason, long updatedMs
   ) {
      byte[] var10001 = new byte[9];
      var10001[0] = (byte)(93);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(113);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(117);
      var10001[5] = (byte)(19);
      var10001[6] = (byte)(39);
      var10001[7] = (byte)(30);
      var10001[8] = (byte)(63);
      Objects.requireNonNull(dimension, $$v_s(var10001, 6435009046825390108L));
      var10001 = new byte[10];
      var10001[0] = (byte)(90);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(114);
      var10001[3] = (byte)(-72);
      var10001[4] = (byte)(114);
      var10001[5] = (byte)(4);
      var10001[6] = (byte)(43);
      var10001[7] = (byte)(31);
      var10001[8] = (byte)(50);
      var10001[9] = (byte)(-57);
      Objects.requireNonNull(confidence, $$v_s(var10001, 6435009046825390108L));
      String var11 = reason == null ? "" : reason;
      long var12 = Math.max(0L, updatedMs);
      LinkedHashMap var14 = this.values.computeIfAbsent(dimension, ignored -> new LinkedHashMap());
      FinderEvidence var15 = (FinderEvidence)var14.get(chunkKey);
      boolean var16 = confidence == FinderConfidence.CONFIRMED && (var15 == null || var15.confidence() != FinderConfidence.CONFIRMED);
      FinderConfidence var17 = var15 != null && var15.confidence() == FinderConfidence.CONFIRMED ? FinderConfidence.CONFIRMED : confidence;
      double var18 = var15 == null ? score : Math.max(var15.score(), score);
      int var20 = var15 == null ? Math.max(0, sampleCount) : Math.max(var15.sampleCount(), Math.max(0, sampleCount));
      long var21 = var15 == null ? var12 : var15.firstSeenMs();
      String var23 = var15 != null && score < var15.score() ? var15.reason() : var11;
      FinderEvidence var24 = new FinderEvidence(chunkKey, var17, var18, var20, var21, var12, var23);
      var14.put(chunkKey, var24);
      this.trimToCapacity();
      return new FinderEvidenceStore.UpdateResult(var24, var16);
   }

   public synchronized Optional get(FinderEvidenceStore.SessionDimension dimension, long chunkKey) {
      Map var4 = (Map)this.values.get(dimension);
      return var4 == null ? Optional.empty() : Optional.ofNullable((FinderEvidence)var4.get(chunkKey));
   }

   public synchronized List snapshot(FinderEvidenceStore.SessionDimension dimension) {
      Map var2 = (Map)this.values.get(dimension);
      return var2 == null ? List.of() : List.copyOf(var2.values());
   }

   public synchronized void clearSession(long sessionId) {
      this.values.keySet().removeIf(key -> key.sessionId() == sessionId);
   }

   public synchronized void pruneOutOfRange(FinderEvidenceStore.SessionDimension dimension, int centerChunkX, int centerChunkZ, int radius) {
      LinkedHashMap var5 = (LinkedHashMap)this.values.get(dimension);
      if (var5 != null) {
         int var6 = Math.max(0, radius);
         var5.entrySet().removeIf(entry -> {
            int var4 = (int)((Long)entry.getKey() & 4294967295L);
            int var5x = (int)((Long)entry.getKey() >>> (32));
            return Math.max(Math.abs(var4 - centerChunkX), Math.abs(var5x - centerChunkZ)) > var6;
         });
         if (var5.isEmpty()) {
            this.values.remove(dimension);
         }
      }
   }

   public synchronized void clear() {
      this.values.clear();
   }

   private void trimToCapacity() {
      while (this.size() > this.capacity) {
         FinderEvidenceStore.EvidenceLocation var1 = this.allEvidence()
            .stream()
            .min(
               Comparator.<FinderEvidenceStore.EvidenceLocation>comparingInt(value -> value.evidence().confidence() == FinderConfidence.POSSIBLE ? 0 : 1)
                  .thenComparingLong(value -> value.evidence().updatedMs())
                  .thenComparingDouble(value -> value.evidence().score())
            )
            .orElse(null);
         if (var1 == null) {
            return;
         }

         LinkedHashMap var2 = (LinkedHashMap)this.values.get(var1.dimension());
         if (var2 != null) {
            var2.remove(var1.evidence().chunkKey());
            if (var2.isEmpty()) {
               this.values.remove(var1.dimension());
            }
         }
      }
   }

   private int size() {
      int var1 = 0;

      for (Map var3 : this.values.values()) {
         var1 += var3.size();
      }

      return var1;
   }

   private List allEvidence() {
      ArrayList var1 = new ArrayList();
      this.values
         .forEach(
            (dimension, dimensionValues) -> dimensionValues.values()
               .forEach(evidence -> var1.add(new FinderEvidenceStore.EvidenceLocation(dimension, evidence)))
         );
      return var1;
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

   private record EvidenceLocation(FinderEvidenceStore.SessionDimension dimension, FinderEvidence evidence) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record SessionDimension(long sessionId, String dimensionId) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public SessionDimension(long sessionId, String dimensionId) {
         dimensionId = dimensionId == null ? "" : dimensionId;
         this.sessionId = sessionId;
         this.dimensionId = dimensionId;
      }
   }

   public record UpdateResult(FinderEvidence evidence, boolean becameConfirmed) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
