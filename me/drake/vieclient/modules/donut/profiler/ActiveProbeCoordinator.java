package me.drake.vieclient.modules.donut.profiler;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ActiveProbeCoordinator {
   private static final ActiveProbeCoordinator INSTANCE = new ActiveProbeCoordinator();
   private final Object lock = new Object();
   private ActiveProbeCoordinator.Lease current;
   private long nextAllowedMs;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static ActiveProbeCoordinator get() {
      return INSTANCE;
   }

   public Optional tryAcquire(String owner, long nowMs, long cooldownMs, Runnable restore) {
      if (owner != null && !owner.isBlank()) {
         long var7 = Math.max(0L, nowMs);
         long var9 = Math.max(0L, cooldownMs);
         synchronized (this.lock) {
            if (this.current == null && var7 >= this.nextAllowedMs) {
               this.current = new ActiveProbeCoordinator.Lease(this, owner, var7, var9, restore == null ? () -> {} : restore);
               return Optional.of(this.current);
            } else {
               return Optional.empty();
            }
         }
      } else {
         return Optional.empty();
      }
   }

   public Optional currentOwner() {
      synchronized (this.lock) {
         return this.current == null ? Optional.empty() : Optional.of(this.current.owner());
      }
   }

   public void cancelAll() {
      ActiveProbeCoordinator.Lease var1;
      synchronized (this.lock) {
         var1 = this.current;
      }

      if (var1 != null) {
         var1.close();
      }
   }

   private void release(ActiveProbeCoordinator.Lease lease) {
      Runnable var2 = null;
      synchronized (this.lock) {
         if (this.current != lease) {
            return;
         }

         this.current = null;
         this.nextAllowedMs = Math.max(this.nextAllowedMs, lease.acquiredAtMs() + lease.cooldownMs());
         var2 = lease.restore();
      }

      var2.run();
   }

   public static final class Lease implements AutoCloseable {
      private final ActiveProbeCoordinator coordinator;
      private final String owner;
      private final long acquiredAtMs;
      private final long cooldownMs;
      private final Runnable restore;
      private final AtomicBoolean closed = new AtomicBoolean();
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private Lease(ActiveProbeCoordinator coordinator, String owner, long acquiredAtMs, long cooldownMs, Runnable restore) {
         this.coordinator = coordinatorx;
         this.owner = ownerx;
         this.acquiredAtMs = acquiredAtMsx;
         this.cooldownMs = cooldownMsx;
         this.restore = restorex;
      }

      public String owner() {
         return this.owner;
      }

      @Override
      public void close() {
         if (this.closed.compareAndSet(false, true)) {
            this.coordinator.release(this);
         }
      }

      private long acquiredAtMs() {
         return this.acquiredAtMs;
      }

      private long cooldownMs() {
         return this.cooldownMs;
      }

      private Runnable restore() {
         return this.restore;
      }
   }
}
