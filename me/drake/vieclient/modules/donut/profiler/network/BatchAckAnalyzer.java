package me.drake.vieclient.modules.donut.profiler.network;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.drake.vieclient.modules.donut.profiler.EvidenceCandidate;
import me.drake.vieclient.modules.donut.profiler.FinderConfidence;
import me.drake.vieclient.modules.donut.profiler.stats.RollingRobustBaseline;
import me.drake.vieclient.modules.donut.profiler.stats.SpatialCluster;

public final class BatchAckAnalyzer {
   private static final int MAX_ORDINAL_BANDS = 64;
   private static final int MAX_CHALLENGE_WINDOWS = 8;
   private static final int MAX_WINDOW_OBSERVATIONS = 1024;
   private final Map normalOrdinalBaselines = new HashMap();
   private final LinkedHashMap challenges = new LinkedHashMap();
   private final Map responses = new HashMap();
   private long observationSequence;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public synchronized void observeNormal(int ordinalBand, double offsetMs) {
      if (ordinalBand >= 0 && Double.isFinite(offsetMs) && !(offsetMs <= 0.0)) {
         if (this.normalOrdinalBaselines.size() >= (64) && !this.normalOrdinalBaselines.containsKey(ordinalBand)) {
            this.normalOrdinalBaselines.remove(this.normalOrdinalBaselines.keySet().iterator().next());
         }

         this.normalOrdinalBaselines.computeIfAbsent(ordinalBand, ignored -> new RollingRobustBaseline(64, 8)).observe(offsetMs);
      }
   }

   public synchronized void observeChallenge(long chunkKey, int ordinalBand, long generation, float multiplier, double offsetMs) {
      if (ordinalBand >= 0 && generation > 0L && Float.isFinite(multiplier) && Double.isFinite(offsetMs) && !(offsetMs <= 0.0)) {
         if (this.challenges.size() >= (8) && !this.challenges.containsKey(generation)) {
            this.challenges.remove(this.challenges.keySet().iterator().next());
         }

         BatchAckAnalyzer.ChallengeWindow var9 = this.challenges.computeIfAbsent(generation, ignored -> new BatchAckAnalyzer.ChallengeWindow(multiplier));
         if (!(Math.abs(var9.multiplier - multiplier) > 0.001F)) {
            var9.observations.put(chunkKey, new BatchAckAnalyzer.ChallengeObservation(chunkKey, ordinalBand, offsetMs));
            if (var9.observations.size() > (1024)) {
               var9.observations.remove(var9.observations.keySet().iterator().next());
            }
         }
      }
   }

   public synchronized void finishChallenge(long generation) {
      BatchAckAnalyzer.ChallengeWindow var3 = (BatchAckAnalyzer.ChallengeWindow)this.challenges.remove(generation);
      if (var3 != null) {
         ArrayList var4 = new ArrayList();

         for (BatchAckAnalyzer.ChallengeObservation var6 : var3.observations.values()) {
            RollingRobustBaseline var7 = (RollingRobustBaseline)this.normalOrdinalBaselines.get(var6.ordinalBand());
            if (var7 != null && var7.ready()) {
               double var8 = var7.median();
               if (!(var8 <= 0.0)) {
                  double var10 = (var6.offsetMs() - var8) / var8;
                  var4.add(new BatchAckAnalyzer.MatchedResponse(var6.chunkKey(), var8, var6.offsetMs(), var10));
               }
            }
         }

         if (!var4.isEmpty()) {
            List var12 = var4.stream().map(BatchAckAnalyzer.MatchedResponse::change).sorted().toList();
            double var13 = (Double)var12.get(var12.size() / (2));

            for (BatchAckAnalyzer.MatchedResponse var9 : var4) {
               this.observePair(var9.chunkKey(), var9.baselineMs(), var9.challengeMs(), var3.multiplier, var13, generation);
            }
         }
      }
   }

   public synchronized void observePair(long chunkKey, double baselineMs, double challengeMs, float multiplier, double globalChange) {
      this.observePair(chunkKey, baselineMs, challengeMs, multiplier, globalChange, ++this.observationSequence);
   }

   public synchronized List candidates() {
      HashSet var1 = new HashSet(this.responses.keySet());
      var1.removeIf(key -> {
         BatchAckAnalyzer.ResponseStats var2x = (BatchAckAnalyzer.ResponseStats)this.responses.get(key);
         return var2x.lowCount == 0 && var2x.highCount == 0;
      });
      ArrayList var2 = new ArrayList();

      for (Set var4 : SpatialCluster.find(var1, 4)) {
         HashSet var5 = new HashSet();
         HashSet var6 = new HashSet();
         var4.forEach(key -> {
            BatchAckAnalyzer.ResponseStats var4x = (BatchAckAnalyzer.ResponseStats)this.responses.get(key);
            var5.addAll(var4x.lowGenerations);
            var6.addAll(var4x.highGenerations);
         });
         if (var5.size() >= (2) && !var6.isEmpty()) {
            for (long var8 : var4) {
               BatchAckAnalyzer.ResponseStats var10 = (BatchAckAnalyzer.ResponseStats)this.responses.get(var8);
               int var10006 = var10.lowCount + var10.highCount;
               byte[] var10007 = new byte[31];
               var10007[0] = (byte)(88);
               var10007[1] = (byte)(-13);
               var10007[2] = (byte)(120);
               var10007[3] = (byte)(-73);
               var10007[4] = (byte)(-21);
               var10007[5] = (byte)(69);
               var10007[6] = (byte)('시' ^ '싺');
               var10007[7] = (byte)(-64);
               var10007[8] = (byte)(59);
               var10007[9] = (byte)(-51);
               var10007[10] = (byte)(-118);
               var10007[11] = (byte)(-105);
               var10007[12] = (byte)(-83);
               var10007[13] = (byte)(-43);
               var10007[14] = (byte)(15);
               var10007[15] = (byte)(-83);
               var10007[16] = (byte)(30);
               var10007[17] = (byte)(41);
               var10007[18] = (byte)(-92);
               var10007[19] = (byte)(-125);
               var10007[20] = (byte)(72);
               var10007[21] = (byte)(-77);
               var10007[22] = (byte)(126);
               var10007[23] = (byte)(-55);
               var10007[24] = (byte)(-31);
               var10007[25] = (byte)(126);
               var10007[26] = (byte)(-31);
               var10007[27] = (byte)(53);
               var10007[28] = (byte)(101);
               var10007[29] = (byte)(-51);
               var10007[30] = (byte)(79);
               var2.add(new EvidenceCandidate(var8, FinderConfidence.CONFIRMED, var10.maximumChange, var10006, $$v_s(var10007, -7185776740781535201L)));
            }
         }
      }

      return List.copyOf(var2);
   }

   private void observePair(long chunkKey, double baselineMs, double challengeMs, float multiplier, double globalChange, long generation) {
      if (Double.isFinite(baselineMs)
         && Double.isFinite(challengeMs)
         && Double.isFinite(globalChange)
         && !(baselineMs <= 0.0)
         && !(challengeMs <= 0.0)
         && !(Math.abs(globalChange) > 0.15)) {
         double var12 = (challengeMs - baselineMs) / baselineMs;
         if (this.responses.size() >= (2048) && !this.responses.containsKey(chunkKey)) {
            this.responses.remove(this.responses.keySet().iterator().next());
         }

         BatchAckAnalyzer.ResponseStats var14 = this.responses.computeIfAbsent(chunkKey, ignored -> new BatchAckAnalyzer.ResponseStats());
         if (multiplier < 1.0F && var12 >= 0.25) {
            var14.recordLow(var12, generation);
         }

         if (multiplier > 1.0F && var12 <= -0.2) {
            var14.recordHigh(-var12, generation);
         }
      }
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

   private record ChallengeObservation(long chunkKey, int ordinalBand, double offsetMs) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private static final class ChallengeWindow {
      private final float multiplier;
      private final LinkedHashMap observations = new LinkedHashMap();
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private ChallengeWindow(float multiplier) {
         this.multiplier = multiplierx;
      }
   }

   private record MatchedResponse(long chunkKey, double baselineMs, double challengeMs, double change) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private static final class ResponseStats {
      private int lowCount;
      private int highCount;
      private double maximumChange;
      private final HashSet lowGenerations = new HashSet();
      private final HashSet highGenerations = new HashSet();
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private void recordLow(double change, long generation) {
         this.lowCount++;
         if (this.lowGenerations.size() < (2)) {
            this.lowGenerations.add(generation);
         }

         this.maximumChange = Math.max(this.maximumChange, change);
      }

      private void recordHigh(double change, long generation) {
         this.highCount++;
         if (this.highGenerations.isEmpty()) {
            this.highGenerations.add(generation);
         }

         this.maximumChange = Math.max(this.maximumChange, change);
      }
   }
}
