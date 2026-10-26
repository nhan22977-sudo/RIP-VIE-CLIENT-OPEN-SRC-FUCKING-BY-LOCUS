package me.drake.vieclient.modules.donut.profiler.network;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import me.drake.vieclient.modules.donut.profiler.ActiveProbeCoordinator;

public final class BatchAckController {
   public static final float MIN_ACK = 0.01F;
   public static final float MAX_ACK = 64.0F;
   private static final int MAX_SEQUENCE_LENGTH = 16;
   private static final float[] EMPTY_SEQUENCE = new float[0];
   private static final BatchAckController INSTANCE = new BatchAckController(ActiveProbeCoordinator.get());
   private final ActiveProbeCoordinator coordinator;
   private volatile ActiveProbeCoordinator.Lease lease;
   private volatile long challengeEndsMs;
   private volatile float multiplier = 1.0F;
   private volatile boolean collecting;
   private volatile float[] sequence = EMPTY_SEQUENCE;
   private volatile int sequenceIndex;
   private BatchAckController.ProbeHandle activeHandle;
   private long challengeGeneration;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BatchAckController(ActiveProbeCoordinator coordinator) {
      this.coordinator = coordinatorx;
   }

   public static BatchAckController get() {
      return INSTANCE;
   }

   public synchronized Optional beginChallenge(long nowMs, long durationMs, float multiplier) {
      float[] var10003 = new float[2];
      var10003[0] = multiplierx;
      var10003[1] = multiplierx;
      return this.beginSequence(nowMs, durationMs, var10003);
   }

   public synchronized Optional beginSequence(long nowMs, long durationMs, float... multipliers) {
      if (this.lease != null) {
         return Optional.empty();
      }

      float[] var6 = copySequence(multipliers);
      if (var6.length == 0) {
         this.restore();
         return Optional.empty();
      }

      long var7 = Math.max(0L, nowMs);
      long var9 = saturatedAdd(var7, Math.max(1L, durationMs));

      try {
         byte[] var10001 = new byte[9];
         var10001[0] = (byte)(88);
         var10001[1] = (byte)(-5);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-67);
         var10001[4] = (byte)(127);
         var10001[5] = (byte)(4);
         var10001[6] = (byte)(-38);
         var10001[7] = (byte)(-32);
         var10001[8] = (byte)(57);
         Optional var11 = this.coordinator.tryAcquire($$v_s(var10001, -6072831769563639777L), var7, 30000L, this::restore);
         if (var11.isEmpty()) {
            this.restore();
            return Optional.empty();
         } else {
            this.sequence = var6;
            this.sequenceIndex = 0;
            this.multiplier = var6[0];
            this.challengeEndsMs = var9;
            this.collecting = false;
            this.lease = (ActiveProbeCoordinator.Lease)var11.orElseThrow();
            this.challengeGeneration++;
            this.activeHandle = new BatchAckController.ProbeHandle(this, this.challengeGeneration);
            return Optional.of(this.activeHandle);
         }
      } catch (RuntimeException var12) {
         this.restore();
         return Optional.empty();
      }
   }

   public synchronized float adjust(float vanilla, long nowMs) {
      boolean var4 = this.challenging(nowMs);
      if (!Float.isFinite(vanilla)) {
         return 0.01F;
      } else if (!var4) {
         return vanilla;
      } else if (this.sequenceIndex >= this.sequence.length) {
         this.cancelActive();
         return vanilla;
      } else {
         this.multiplier = this.sequence[this.sequenceIndex++];
         this.collecting = true;
         return clamp(vanilla * this.multiplier);
      }
   }

   public synchronized boolean challenging(long nowMs) {
      ActiveProbeCoordinator.Lease var3 = this.lease;
      if (var3 == null) {
         return false;
      }

      if (nowMs <= this.challengeEndsMs) {
         return true;
      }

      this.cancelActive();
      return false;
   }

   public synchronized float multiplier() {
      return this.multiplier;
   }

   public synchronized boolean collecting() {
      return this.lease != null && this.collecting;
   }

   public synchronized BatchAckController.PhaseSnapshot snapshot() {
      return new BatchAckController.PhaseSnapshot(this.challengeGeneration, this.lease != null && this.collecting, this.multiplier);
   }

   synchronized void forceResetForSession() {
      this.cancelActive();
   }

   private synchronized void cancel(BatchAckController.ProbeHandle handle) {
      if (handle != null && this.activeHandle == handle && handle.generation() == this.challengeGeneration) {
         this.cancelActive();
      }
   }

   private synchronized void cancelActive() {
      ActiveProbeCoordinator.Lease var1 = this.lease;
      this.restore();
      if (var1 != null) {
         var1.close();
      }
   }

   private synchronized void restore() {
      this.lease = null;
      this.challengeEndsMs = 0L;
      this.multiplier = 1.0F;
      this.collecting = false;
      this.sequence = EMPTY_SEQUENCE;
      this.sequenceIndex = 0;
      this.activeHandle = null;
   }

   private static float[] copySequence(float[] multipliers) {
      if (multipliers != null && multipliers.length != 0) {
         for (float var4 : multipliers) {
            if (!Float.isFinite(var4)) {
               return EMPTY_SEQUENCE;
            }
         }

         int var5 = Math.min(16, multipliers.length);
         float[] var6 = new float[var5];

         for (int var7 = 0; var7 < var5; var7++) {
            var6[var7] = Math.max(0.5F, Math.min(1.5F, multipliers[var7]));
         }

         return var6;
      } else {
         return EMPTY_SEQUENCE;
      }
   }

   private static long saturatedAdd(long left, long right) {
      return Long.MAX_VALUE - left < right ? Long.MAX_VALUE : left + right;
   }

   private static float clamp(float value) {
      return !Float.isFinite(value) ? 0.01F : Math.max(0.01F, Math.min(64.0F, value));
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

   public record PhaseSnapshot(long generation, boolean collecting, float multiplier) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public static final class ProbeHandle implements AutoCloseable {
      private final BatchAckController controller;
      private final long generation;
      private final AtomicBoolean closed = new AtomicBoolean();
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private ProbeHandle(BatchAckController controller, long generation) {
         this.controller = controllerx;
         this.generation = generationx;
      }

      public long generation() {
         return this.generation;
      }

      @Override
      public void close() {
         if (this.closed.compareAndSet(false, true)) {
            this.controller.cancel(this);
         }
      }
   }
}
