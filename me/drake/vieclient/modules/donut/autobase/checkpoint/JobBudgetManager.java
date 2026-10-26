package me.drake.vieclient.modules.donut.autobase.checkpoint;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.drake.vieclient.VieClientMod;
import net.minecraft.class_2338;

public class JobBudgetManager {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private long maxContinuousWorkMs = 600000L;
   private long maxJobWallTimeMs = 1800000L;
   private long noProgressTimeoutMs = 45000L;
   private int maxRetriesPerAction = 2;
   private long planningBudgetNs = 2000000L;
   private long jobStartNano = 0L;
   private long continuousSessionStartNano = 0L;
   private long lastProgressNano = 0L;
   private long accumulatedWorkDurationNs = 0L;
   private boolean running = false;
   private boolean paused = false;
   private final Map retryCounts = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void startJob() {
      long var1 = System.nanoTime();
      this.jobStartNano = var1;
      this.continuousSessionStartNano = var1;
      this.lastProgressNano = var1;
      this.accumulatedWorkDurationNs = 0L;
      this.running = true;
      this.paused = false;
      this.retryCounts.clear();
   }

   public void pauseJob() {
      if (this.running && !this.paused) {
         this.accumulatedWorkDurationNs = this.accumulatedWorkDurationNs + (System.nanoTime() - this.continuousSessionStartNano);
         this.paused = true;
      }
   }

   public void resumeJob() {
      if (this.running && this.paused) {
         long var1 = System.nanoTime();
         this.continuousSessionStartNano = var1;
         this.lastProgressNano = var1;
         if (this.accumulatedWorkDurationNs / 1000000L >= this.maxJobWallTimeMs) {
            this.accumulatedWorkDurationNs = 0L;
         }

         this.paused = false;
      }
   }

   public void stopJob() {
      this.running = false;
      this.paused = false;
   }

   public void recordProgress() {
      this.lastProgressNano = System.nanoTime();
   }

   public String checkLimits() {
      if (this.running && !this.paused) {
         long var1 = System.nanoTime();
         long var3 = (var1 - this.lastProgressNano) / 1000000L;
         if (var3 > this.noProgressTimeoutMs) {
            return "NO_PROGRESS_TIMEOUT (No progress for " + var3 / 1000L + "s)";
         }

         long var5 = (var1 - this.continuousSessionStartNano) / 1000000L;
         if (var5 > this.maxContinuousWorkMs) {
            return "MAX_CONTINUOUS_WORK_REACHED (Exceeded continuous work limit of " + this.maxContinuousWorkMs / 60000L + "m)";
         }

         long var7 = (this.accumulatedWorkDurationNs + (var1 - this.continuousSessionStartNano)) / 1000000L;
         return var7 > this.maxJobWallTimeMs ? "MAX_WALL_TIME_REACHED (Exceeded total job wall time of " + this.maxJobWallTimeMs / 60000L + "m)" : null;
      } else {
         return null;
      }
   }

   public boolean registerRetry(class_2338 pos) {
      int var2 = this.retryCounts.getOrDefault(pos, 0) + 1;
      this.retryCounts.put(pos, var2);
      return var2 <= this.maxRetriesPerAction;
   }

   public int getRetryCount(class_2338 pos) {
      return this.retryCounts.getOrDefault(pos, 0);
   }

   public void clearRetry(class_2338 pos) {
      this.retryCounts.remove(pos);
   }

   public long getElapsedWallTimeMs() {
      return !this.running ? 0L : (System.nanoTime() - this.jobStartNano) / 1000000L;
   }

   public long getRemainingContinuousWorkMs() {
      if (!this.running) {
         return this.maxContinuousWorkMs;
      }

      long var1 = this.paused ? 0L : (System.nanoTime() - this.continuousSessionStartNano) / 1000000L;
      return Math.max(0L, this.maxContinuousWorkMs - var1);
   }

   public long getRemainingJobWallTimeMs() {
      if (!this.running) {
         return this.maxJobWallTimeMs;
      }

      long var1 = (System.nanoTime() - this.jobStartNano) / 1000000L;
      return Math.max(0L, this.maxJobWallTimeMs - var1);
   }

   public long getPlanningBudgetNs() {
      return this.planningBudgetNs;
   }

   public boolean saveCheckpoint(File file, String name, class_2338 origin, String rot, String mir, long spent, Set completed) {
      try {
         JobBudgetManager.CheckpointData var9 = new JobBudgetManager.CheckpointData();
         var9.schematicName = name;
         if (origin != null) {
            var9.originX = origin.method_10263();
            var9.originY = origin.method_10264();
            var9.originZ = origin.method_10260();
         }

         var9.rotation = rot;
         var9.mirror = mir;
         var9.elapsedMs = this.getElapsedWallTimeMs();
         var9.totalSpent = spent;
         if (completed != null) {
            for (class_2338 var11 : completed) {
               var9.completedPositions.add(var11.method_10263() + "," + var11.method_10264() + "," + var11.method_10260());
            }
         }

         if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
         }

         try (FileWriter var16 = new FileWriter(file)) {
            GSON.toJson(var9, var16);
         }

         return true;
      } catch (Throwable var15) {
         VieClientMod.LOGGER
            .error(
               "[JobBudgetManager] Failed to save checkpoint: ",
               var15
            );
         return false;
      }
   }

   public JobBudgetManager.CheckpointData loadCheckpoint(File file) {
      if (!file.exists()) {
         return null;
      }

      try (FileReader var2 = new FileReader(file)) {
         return (JobBudgetManager.CheckpointData)GSON.fromJson(var2, JobBudgetManager.CheckpointData.class);
      } catch (Throwable var7) {
         byte[] var10001 = new byte[46];
         var10001[0] = (byte)(98);
         var10001[1] = (byte)(-48);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-68);
         var10001[4] = (byte)(4);
         var10001[5] = (byte)(75);
         var10001[6] = (byte)(127);
         var10001[7] = (byte)(-31);
         var10001[8] = (byte)(52);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-87);
         var10001[11] = (byte)(-105);
         var10001[12] = (byte)(112);
         var10001[13] = (byte)(-121);
         var10001[14] = (byte)(84);
         var10001[15] = (byte)(-101);
         var10001[16] = (byte)(27);
         var10001[17] = (byte)(23);
         var10001[18] = (byte)(-20);
         var10001[19] = (byte)(-24);
         var10001[20] = (byte)(-105);
         var10001[21] = (byte)(-89);
         var10001[22] = (byte)(39);
         var10001[23] = (byte)(-77);
         var10001[24] = (byte)(-27);
         var10001[25] = (byte)(50);
         var10001[26] = (byte)(-32);
         var10001[27] = (byte)(41);
         var10001[28] = (byte)(-18);
         var10001[29] = (byte)(-38);
         var10001[30] = (byte)(12);
         var10001[31] = (byte)(111);
         var10001[32] = (byte)(-67);
         var10001[33] = (byte)(26);
         var10001[34] = (byte)(31);
         var10001[35] = (byte)(22);
         var10001[36] = (byte)(-61);
         var10001[37] = (byte)(-3);
         var10001[38] = (byte)(-48);
         var10001[39] = (byte)(22);
         var10001[40] = (byte)(-98);
         var10001[41] = (byte)(-85);
         var10001[42] = (byte)(42);
         var10001[43] = (byte)(98);
         var10001[44] = (byte)(68);
         var10001[45] = (byte)(102);
         VieClientMod.LOGGER.error($$v_s(var10001, -5901719924598648804L), var7);
         return null;
      }
   }

   public boolean isRunning() {
      return this.running;
   }

   public boolean isPaused() {
      return this.paused;
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

   public static class CheckpointData {
      public String schematicName;
      public int originX;
      public int originY;
      public int originZ;
      public String rotation;
      public String mirror;
      public long elapsedMs;
      public long totalSpent;
      public List completedPositions = new ArrayList();
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
