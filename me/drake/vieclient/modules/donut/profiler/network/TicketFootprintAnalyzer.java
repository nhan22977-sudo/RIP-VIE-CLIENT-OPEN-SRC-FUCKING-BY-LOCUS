package me.drake.vieclient.modules.donut.profiler.network;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import me.drake.vieclient.modules.donut.profiler.EvidenceCandidate;
import me.drake.vieclient.modules.donut.profiler.FinderConfidence;
import me.drake.vieclient.modules.donut.profiler.stats.RollingRobustBaseline;
import me.drake.vieclient.modules.donut.profiler.stats.SpatialCluster;

public final class TicketFootprintAnalyzer {
   private static final double WARM_Z = -3.5;
   private final Map baselines = new HashMap();
   private final Map warmChunks = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void observe(long chunkKey, double latencyMs) {
      this.observe(new TicketFootprintAnalyzer.Sample(chunkKey, latencyMs, 0, 0, 50.0, 0.0, 0.0));
   }

   public synchronized void observe(TicketFootprintAnalyzer.Sample sample) {
      double var2 = sample.deliveryLatencyMs();
      if (Double.isFinite(var2) && !(var2 < 0.0) && Double.isFinite(sample.pingMs()) && Double.isFinite(sample.speed())) {
         String var4 = bucket(sample);
         trimMap(this.baselines, 128);
         RollingRobustBaseline var5 = this.baselines.computeIfAbsent(var4, ignored -> new RollingRobustBaseline(128, 24));
         double var6 = (var2 + Math.max(0.0, sample.readyLatencyMs()) * 0.25) / Math.max(25.0, sample.pingMs());
         if (!var5.ready()) {
            var5.observe(var6);
         } else {
            double var8 = var5.robustZ(var6);
            if (var8 <= -3.5) {
               trimMap(this.warmChunks, 2048);
               this.warmChunks.computeIfAbsent(sample.chunkKey(), ignored -> new TicketFootprintAnalyzer.WarmStats()).record(var8);
            } else {
               var5.observe(var6);
            }
         }
      }
   }

   private static void trimMap(Map map, int maximum) {
      if (map.size() >= maximum) {
         Object var2 = map.keySet().iterator().next();
         map.remove(var2);
      }
   }

   private static String bucket(TicketFootprintAnalyzer.Sample sample) {
      return sample.frontierDistance() / (4)
         + "|"
         + sample.batchSize() / (8)
         + "|"
         + (int)(sample.pingMs() / 50.0)
         + "|"
         + (int)(sample.speed() / 0.75);
   }

   public synchronized List candidates() {
      HashSet var1 = new HashSet();
      this.warmChunks.forEach((key, stats) -> {
         if (stats.count >= (3)) {
            var1.add(key);
         }
      });
      HashSet var2 = new HashSet();
      SpatialCluster.find(var1, 9).forEach(var2::addAll);
      ArrayList var3 = new ArrayList();
      this.warmChunks.forEach((key, stats) -> {
         if (var2.contains(key)) {
            var3.add(candidate(key, stats, FinderConfidence.CONFIRMED));
         } else if (stats.count >= (4)) {
            var3.add(candidate(key, stats, FinderConfidence.POSSIBLE));
         }
      });
      return List.copyOf(var3);
   }

   private static EvidenceCandidate candidate(long key, TicketFootprintAnalyzer.WarmStats stats, FinderConfidence confidence) {
      double var10004 = Math.abs(stats.minimumZ);
      byte[] var10006 = new byte[21];
      var10006[0] = (byte)(77);
      var10006[1] = (byte)(-5);
      var10006[2] = (byte)(110);
      var10006[3] = (byte)(-77);
      var10006[4] = (byte)(58);
      var10006[5] = (byte)(85);
      var10006[6] = (byte)(78);
      var10006[7] = (byte)(-62);
      var10006[8] = (byte)(57);
      var10006[9] = (byte)(-57);
      var10006[10] = (byte)(-112);
      var10006[11] = (byte)(-37);
      var10006[12] = (byte)(41);
      var10006[13] = (byte)(-106);
      var10006[14] = (byte)(96);
      var10006[15] = (byte)(-83);
      var10006[16] = (byte)(26);
      var10006[17] = (byte)(56);
      var10006[18] = (byte)(-91);
      var10006[19] = (byte)(-64);
      var10006[20] = (byte)(-45);
      return new EvidenceCandidate(key, confidence, var10004, stats.count, $$v_s(var10006, -8564439467313344481L));
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

   public record Sample(long chunkKey, double deliveryLatencyMs, int frontierDistance, int batchSize, double pingMs, double speed, double readyLatencyMs) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private static final class WarmStats {
      private int count;
      private double minimumZ;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private void record(double z) {
         this.count++;
         this.minimumZ = Math.min(this.minimumZ, z);
      }
   }
}
