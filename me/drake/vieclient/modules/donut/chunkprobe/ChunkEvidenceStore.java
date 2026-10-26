package me.drake.vieclient.modules.donut.chunkprobe;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ChunkEvidenceStore {
   public static final int MAX_FLAGS = 512;
   public static final long DEFAULT_TTL_MS = 120000L;
   private final LinkedHashMap evidence = new LinkedHashMap(16, 0.75F, true);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ChunkEvidenceStore.Update put(long chunkKey, Confidence confidence, double score, long updatedMs) {
      if (confidence != null && confidence != Confidence.NONE) {
         long var8 = Math.max(0L, updatedMs);
         ChunkEvidenceStore.ChunkEvidence var10 = (ChunkEvidenceStore.ChunkEvidence)this.evidence.get(chunkKey);
         boolean var11 = var10 == null;
         boolean var12 = var10 != null && var10.confidence() == Confidence.POSSIBLE && confidence == Confidence.HIGH;
         ChunkEvidenceStore.ChunkEvidence var13;
         if (var10 != null && var10.confidence() == Confidence.HIGH && confidence == Confidence.POSSIBLE) {
            var13 = new ChunkEvidenceStore.ChunkEvidence(chunkKey, Confidence.HIGH, var10.score(), var8);
         } else if (var10 != null && var10.confidence() == confidence) {
            var13 = new ChunkEvidenceStore.ChunkEvidence(chunkKey, confidence, Math.max(var10.score(), score), var8);
         } else {
            var13 = new ChunkEvidenceStore.ChunkEvidence(chunkKey, confidence, score, var8);
         }

         this.evidence.put(chunkKey, var13);
         this.trimToCapacity();
         return new ChunkEvidenceStore.Update(var13, var11, var12);
      } else {
         return new ChunkEvidenceStore.Update(null, false, false);
      }
   }

   public Optional get(long chunkKey) {
      return Optional.ofNullable((ChunkEvidenceStore.ChunkEvidence)this.evidence.get(chunkKey));
   }

   public Collection snapshot() {
      return List.copyOf(this.evidence.values());
   }

   public int size() {
      return this.evidence.size();
   }

   public void remove(long chunkKey) {
      this.evidence.remove(chunkKey);
   }

   public void retainAll(Set chunkKeys) {
      this.evidence.keySet().retainAll(chunkKeys);
   }

   public void pruneOutside(int centerX, int centerZ, int radius) {
      int var4 = Math.max(0, radius);
      this.evidence.keySet().removeIf(key -> Math.max(Math.abs(ChunkKey.unpackX(key) - centerX), Math.abs(ChunkKey.unpackZ(key) - centerZ)) > var4);
   }

   public void pruneExpired(long nowMs) {
      long var3 = Math.max(0L, nowMs);
      this.evidence.values().removeIf(value -> var3 - value.updatedMs() > 120000L);
   }

   public void clear() {
      this.evidence.clear();
   }

   private void trimToCapacity() {
      while (this.evidence.size() > (512)) {
         Long var1 = (Long)this.evidence.keySet().iterator().next();
         this.evidence.remove(var1);
      }
   }

   public record ChunkEvidence(long chunkKey, Confidence confidence, double score, long updatedMs) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record Update(ChunkEvidenceStore.ChunkEvidence evidence, boolean newChunk, boolean promoted) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
