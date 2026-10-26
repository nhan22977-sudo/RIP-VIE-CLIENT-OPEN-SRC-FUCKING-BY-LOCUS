package me.drake.vieclient.modules.donut.profiler.network;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import me.drake.vieclient.modules.donut.chunkprobe.ChunkKey;
import me.drake.vieclient.modules.donut.profiler.EvidenceCandidate;
import me.drake.vieclient.modules.donut.profiler.FinderConfidence;

public final class ChunkRetentionAnalyzer {
   private static final int MAX_TRACKED = 512;
   private final Map unloadTimes = new HashMap();
   private final Map warmReloads = new HashMap();
   private final ArrayDeque recentReloads = new ArrayDeque();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void unloaded(long chunkKey, long nowMs) {
      if (this.unloadTimes.size() >= (512) && !this.unloadTimes.containsKey(chunkKey)) {
         Long var5 = this.unloadTimes.entrySet().stream().min(Entry.comparingByValue()).map(Entry::getKey).orElse(null);
         if (var5 != null) {
            this.unloadTimes.remove(var5);
         }
      }

      this.unloadTimes.put(chunkKey, Math.max(0L, nowMs));
   }

   public synchronized void ready(long chunkKey, long nowMs, double latencyMs) {
      Long var7 = (Long)this.unloadTimes.remove(chunkKey);
      if (var7 != null && nowMs > var7 && Double.isFinite(latencyMs) && !(latencyMs < 0.0)) {
         long var8 = nowMs - var7;
         HashMap var10 = new HashMap();
         this.recentReloads
            .stream()
            .filter(control -> control.chunkKey != chunkKey)
            .filter(control -> neighbors(chunkKey, control.chunkKey))
            .filter(control -> comparableDuration(var8, control.durationMs))
            .filter(control -> Math.abs(control.readyMs - nowMs) <= 1000L)
            .forEach(control -> var10.putIfAbsent(control.chunkKey, control));
         if (var10.size() >= (2)) {
            ArrayList var11 = new ArrayList();
            var10.values().forEach(control -> var11.add(control.latencyMs));
            var11.sort(Comparator.naturalOrder());
            double var12 = (Double)var11.get(var11.size() / (2));
            double var14 = latencyMs / Math.max(0.001, var12);
            if (var14 <= 0.5) {
               if (this.warmReloads.size() >= (512) && !this.warmReloads.containsKey(chunkKey)) {
                  this.warmReloads.remove(this.warmReloads.keySet().iterator().next());
               }

               this.warmReloads.computeIfAbsent(chunkKey, ignored -> new ChunkRetentionAnalyzer.WarmReloadStats()).record(var14);
            }
         }

         this.recentReloads.addLast(new ChunkRetentionAnalyzer.Reload(chunkKey, var8, latencyMs, nowMs));

         while (this.recentReloads.size() > (512)) {
            this.recentReloads.removeFirst();
         }
      }
   }

   public synchronized Optional evidence(long chunkKey) {
      ChunkRetentionAnalyzer.WarmReloadStats var3 = (ChunkRetentionAnalyzer.WarmReloadStats)this.warmReloads.get(chunkKey);
      return var3 != null && var3.count >= (2) ? Optional.of(candidate(chunkKey, var3)) : Optional.empty();
   }

   public synchronized List candidates() {
      return this.warmReloads
         .entrySet()
         .stream()
         .filter(entry -> ((ChunkRetentionAnalyzer.WarmReloadStats)entry.getValue()).count >= (2))
         .map(entry -> candidate((Long)entry.getKey(), (ChunkRetentionAnalyzer.WarmReloadStats)entry.getValue()))
         .toList();
   }

   private static EvidenceCandidate candidate(long key, ChunkRetentionAnalyzer.WarmReloadStats stats) {
      FinderConfidence var10003 = stats.count >= (3) ? FinderConfidence.CONFIRMED : FinderConfidence.POSSIBLE;
      double var10004 = 1.0 - stats.minimumRatio;
      byte[] var10006 = new byte[17];
      var10006[0] = (byte)(77);
      var10006[1] = (byte)(-5);
      var10006[2] = (byte)(110);
      var10006[3] = (byte)(-77);
      var10006[4] = (byte)(-74);
      var10006[5] = (byte)(-124);
      var10006[6] = (byte)(-116);
      var10006[7] = (byte)(123);
      var10006[8] = (byte)(55);
      var10006[9] = (byte)(-48);
      var10006[10] = (byte)(-55);
      var10006[11] = (byte)(-125);
      var10006[12] = (byte)(-83);
      var10006[13] = (byte)(81);
      var10006[14] = (byte)(-83);
      var10006[15] = (byte)(22);
      var10006[16] = (byte)(14);
      return new EvidenceCandidate(key, var10003, var10004, stats.count, $$v_s(var10006, 2875894855516803103L));
   }

   private static boolean comparableDuration(long a, long b) {
      return Math.abs(a - b) <= Math.max(a, b) * 0.2;
   }

   private static boolean neighbors(long a, long b) {
      return Math.abs(ChunkKey.unpackX(a) - ChunkKey.unpackX(b)) <= (2)
         && Math.abs(ChunkKey.unpackZ(a) - ChunkKey.unpackZ(b)) <= (2);
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

   private record Reload(long chunkKey, long durationMs, double latencyMs, long readyMs) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private static final class WarmReloadStats {
      private int count;
      private double minimumRatio = 1.0;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private void record(double ratio) {
         this.count++;
         this.minimumRatio = Math.min(this.minimumRatio, ratio);
      }
   }
}
