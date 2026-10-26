package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import net.minecraft.class_2338;

final class TunnelTurnLoopWatchdog {
   private final int turnThreshold;
   private final int sampleWindow;
   private class_2338 position;
   private int completedBreaks;
   private int refreshTurns;
   private int samples;
   private boolean straightLockIssued;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   TunnelTurnLoopWatchdog(int sampleWindow, int turnThreshold) {
      this.sampleWindow = Math.max(1, sampleWindowx);
      this.turnThreshold = Math.max(1, turnThresholdx);
   }

   TunnelTurnLoopWatchdog.Outcome observe(class_2338 currentPosition, int currentCompletedBreaks, int currentRefreshTurns) {
      if (this.position == null) {
         this.reset(currentPosition, currentCompletedBreaks, currentRefreshTurns);
         return TunnelTurnLoopWatchdog.Outcome.NONE;
      } else {
         this.samples++;
         if (!this.position.equals(currentPosition) || this.completedBreaks != currentCompletedBreaks) {
            this.reset(currentPosition, currentCompletedBreaks, currentRefreshTurns);
            this.straightLockIssued = false;
            return TunnelTurnLoopWatchdog.Outcome.NONE;
         } else if (this.samples < this.sampleWindow) {
            return TunnelTurnLoopWatchdog.Outcome.NONE;
         } else if (this.straightLockIssued) {
            this.reset(currentPosition, currentCompletedBreaks, currentRefreshTurns);
            return TunnelTurnLoopWatchdog.Outcome.ESCAPE;
         } else if (currentRefreshTurns - this.refreshTurns < this.turnThreshold) {
            this.reset(currentPosition, currentCompletedBreaks, currentRefreshTurns);
            return TunnelTurnLoopWatchdog.Outcome.NONE;
         } else {
            this.rewindWindow(currentPosition, currentCompletedBreaks, currentRefreshTurns);
            this.straightLockIssued = true;
            return TunnelTurnLoopWatchdog.Outcome.LOCK_STRAIGHT;
         }
      }
   }

   void reset(class_2338 currentPosition, int currentCompletedBreaks, int currentRefreshTurns) {
      this.position = currentPosition == null ? null : currentPosition.method_10062();
      this.completedBreaks = currentCompletedBreaks;
      this.refreshTurns = currentRefreshTurns;
      this.samples = 0;
      this.straightLockIssued = false;
   }

   private void rewindWindow(class_2338 currentPosition, int currentCompletedBreaks, int currentRefreshTurns) {
      this.position = currentPosition == null ? null : currentPosition.method_10062();
      this.completedBreaks = currentCompletedBreaks;
      this.refreshTurns = currentRefreshTurns;
      this.samples = 0;
   }

   enum Outcome {
      NONE,
      LOCK_STRAIGHT,
      ESCAPE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TunnelTurnLoopWatchdog.Outcome[] $values() {
         TunnelTurnLoopWatchdog.Outcome[] var10000 = new TunnelTurnLoopWatchdog.Outcome[3];
         var10000[0] = NONE;
         var10000[1] = LOCK_STRAIGHT;
         var10000[2] = ESCAPE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         NONE = new TunnelTurnLoopWatchdog.Outcome();
         var10002 = new byte[13];
         var10002[0] = (byte)(116);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(95);
         var10002[3] = (byte)(-107);
         var10002[4] = (byte)(30);
         var10002[5] = (byte)(71);
         var10002[6] = (byte)(45);
         var10002[7] = (byte)(98);
         var10002[8] = (byte)(17);
         var10002[9] = (byte)(-21);
         var10002[10] = (byte)(-93);
         var10002[11] = (byte)(-66);
         var10002[12] = (byte)(77);
         LOCK_STRAIGHT = new TunnelTurnLoopWatchdog.Outcome();
         var10002 = new byte[6];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-55);
         var10002[2] = (byte)(95);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(17);
         var10002[5] = (byte)(81);
         ESCAPE = new TunnelTurnLoopWatchdog.Outcome();
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
}
