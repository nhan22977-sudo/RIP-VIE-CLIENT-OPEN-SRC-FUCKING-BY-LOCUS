package me.drake.vieclient.modules.donut.profiler;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public final class DonutProfilerService implements AutoCloseable {
   private static final DonutProfilerService INSTANCE = new DonutProfilerService(1, 256);
   private final ThreadPoolExecutor executor;
   private final Set activeSources = ConcurrentHashMap.newKeySet();
   private final Map listeners = new ConcurrentHashMap();
   private final CopyOnWriteArrayList stores = new CopyOnWriteArrayList();
   private final CopyOnWriteArrayList sessionResets = new CopyOnWriteArrayList();
   private final Object lifecycleLock = new Object();
   private final AtomicLong sessionSequence = new AtomicLong();
   private final AtomicLong contextSequence = new AtomicLong();
   private final AtomicLong listenerFailures = new AtomicLong();
   private final AtomicLong droppedSamples = new AtomicLong();
   private final AtomicLong queueLossGeneration = new AtomicLong();
   private final ReentrantLock rejectionLock = new ReentrantLock();
   private final ThreadLocal dispatchSession = new ThreadLocal();
   private final ThreadLocal dispatchContext = new ThreadLocal();
   private final ThreadLocal dispatchLossGeneration = new ThreadLocal();
   private volatile long sessionId;
   private volatile long contextId;
   private volatile String serverId = "";
   private volatile String dimensionId = "";
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public DonutProfilerService(int workers, int queueCapacity) {
      this.executor = new ThreadPoolExecutor(
         Math.max(1, workers), Math.max(1, workers), 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(Math.max(1, queueCapacity)), runnable -> {
            byte[] var10003 = new byte[18];
            var10003[0] = (byte)(76);
            var10003[1] = (byte)(-13);
            var10003[2] = (byte)(121);
            var10003[3] = (byte)(-13);
            var10003[4] = (byte)(17);
            var10003[5] = (byte)(-88);
            var10003[6] = (byte)(18);
            var10003[7] = (byte)(-30);
            var10003[8] = (byte)(38);
            var10003[9] = (byte)(-113);
            var10003[10] = (byte)(-108);
            var10003[11] = (byte)(-124);
            var10003[12] = (byte)(66);
            var10003[13] = (byte)(121);
            var10003[14] = (byte)(61);
            var10003[15] = (byte)(-125);
            var10003[16] = (byte)(15);
            var10003[17] = (byte)(56);
            Thread var1 = new Thread(runnable, $$v_s(var10003, -4647967925209935841L));
            var1.setDaemon(true);
            return var1;
         }, (rejected, pool) -> {
            if (!this.rejectionLock.tryLock()) {
               this.recordQueueLoss();
            } else {
               try {
                  if (pool.isShutdown()) {
                     this.recordQueueLoss();
                     return;
                  }

                  Runnable var3 = pool.getQueue().poll();
                  if (var3 != null) {
                     this.recordQueueLoss();
                  }

                  this.stampAcceptedGeneration(rejected);
                  if (!pool.getQueue().offer(rejected)) {
                     this.recordQueueLoss();
                     return;
                  }

                  if (pool.isShutdown() && pool.getQueue().remove(rejected)) {
                     this.recordQueueLoss();
                  }
               } finally {
                  this.rejectionLock.unlock();
               }
            }
         }
      );
   }

   public static DonutProfilerService get() {
      return INSTANCE;
   }

   public void acquire(String sourceId) {
      if (sourceId != null && !sourceId.isBlank()) {
         this.activeSources.add(sourceId);
      }
   }

   public void release(String sourceId) {
      if (sourceId != null) {
         this.activeSources.remove(sourceId);
      }
   }

   public boolean active() {
      return !this.activeSources.isEmpty();
   }

   public DonutProfilerService.Subscription subscribe(Class type, Consumer listener) {
      CopyOnWriteArrayList var3 = this.listeners.computeIfAbsent(type, ignored -> new CopyOnWriteArrayList());
      var3.add(listener);
      return () -> var3.remove(listener);
   }

   public void publish(ProfilerSample sample) {
      if (this.active() && sample != null && !this.executor.isShutdown()) {
         long var2 = this.sessionId;
         long var4 = this.contextId;
         this.executor.execute(new DonutProfilerService.DispatchTask(sample, var2, var4, this.queueLossGeneration.get()));
      }
   }

   public void registerStore(FinderEvidenceStore store) {
      if (store != null) {
         this.stores.addIfAbsent(store);
      }
   }

   public void registerSessionReset(Runnable reset) {
      if (reset != null) {
         this.sessionResets.addIfAbsent(reset);
      }
   }

   public long beginSession(String serverId) {
      synchronized (this.lifecycleLock) {
         this.clearStores();
         this.serverId = serverIdx == null ? "" : serverIdx;
         this.sessionId = this.sessionSequence.incrementAndGet();
         this.dimensionId = "";
         this.contextId = this.contextSequence.incrementAndGet();
         this.resetAnalyzers();
         return this.sessionId;
      }
   }

   public void endSession() {
      synchronized (this.lifecycleLock) {
         this.clearStores();
         this.serverId = "";
         this.sessionId = this.sessionSequence.incrementAndGet();
         this.dimensionId = "";
         this.contextId = this.contextSequence.incrementAndGet();
         this.resetAnalyzers();
         this.executor.getQueue().clear();
      }
   }

   public long sessionId() {
      return this.sessionId;
   }

   public String serverId() {
      return this.serverId;
   }

   public long listenerFailures() {
      return this.listenerFailures.get();
   }

   public long droppedSamples() {
      return this.droppedSamples.get();
   }

   public long queueLossGeneration() {
      return this.queueLossGeneration.get();
   }

   public long currentDispatchSession() {
      Long var1 = (Long)this.dispatchSession.get();
      return var1 == null ? this.sessionId : var1;
   }

   public long currentDispatchContext() {
      Long var1 = (Long)this.dispatchContext.get();
      return var1 == null ? this.contextId : var1;
   }

   public long currentDispatchLossGeneration() {
      Long var1 = (Long)this.dispatchLossGeneration.get();
      return var1 == null ? this.queueLossGeneration.get() : var1;
   }

   public boolean updateDimension(String dimension) {
      synchronized (this.lifecycleLock) {
         String var3 = dimension == null ? "" : dimension;
         if (var3.equals(this.dimensionId)) {
            return false;
         }

         this.dimensionId = var3;
         this.contextId = this.contextSequence.incrementAndGet();
         this.executor.getQueue().clear();
         this.resetAnalyzers();
         return true;
      }
   }

   public boolean awaitIdleForTest(Duration timeout) {
      if (this.executor.isShutdown()) {
         return true;
      }

      try {
         Future var2 = this.executor.submit(() -> {});
         var2.get(Math.max(1L, timeout.toMillis()), TimeUnit.MILLISECONDS);
         return true;
      } catch (Exception var3) {
         return false;
      }
   }

   @Override
   public void close() {
      this.recordQueueLoss(this.executor.shutdownNow().size());
      this.listeners.clear();
      this.activeSources.clear();
      this.stores.clear();
      this.sessionResets.clear();
   }

   private void dispatch(ProfilerSample sample, long expectedSession, long expectedContext, long acceptedLossGeneration) {
      synchronized (this.lifecycleLock) {
         if (expectedSession == this.sessionId && expectedContext == this.contextId) {
            this.dispatchSession.set(expectedSession);
            this.dispatchContext.set(expectedContext);
            this.dispatchLossGeneration.set(acceptedLossGeneration);

            try {
               this.listeners.forEach((type, typeListeners) -> {
                  if (type.isInstance(sample)) {
                     for (Consumer var5 : typeListeners) {
                        try {
                           var5.accept(sample);
                        } catch (Throwable var7) {
                           this.listenerFailures.incrementAndGet();
                        }
                     }
                  }
               });
            } finally {
               this.dispatchSession.remove();
               this.dispatchContext.remove();
               this.dispatchLossGeneration.remove();
            }
         }
      }
   }

   private void recordQueueLoss() {
      this.recordQueueLoss(1L);
   }

   private void recordQueueLoss(long count) {
      if (count > 0L) {
         this.droppedSamples.addAndGet(count);
         this.queueLossGeneration.addAndGet(count);
      }
   }

   private void stampAcceptedGeneration(Runnable runnable) {
      if (runnable instanceof DonutProfilerService.DispatchTask var2) {
         var2.acceptedLossGeneration = this.queueLossGeneration.get();
      }
   }

   private void clearStores() {
      for (FinderEvidenceStore var2 : this.stores) {
         var2.clear();
      }
   }

   private void resetAnalyzers() {
      for (Runnable var2 : this.sessionResets) {
         try {
            var2.run();
         } catch (Throwable var4) {
            this.listenerFailures.incrementAndGet();
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

   private final class DispatchTask implements Runnable {
      private final ProfilerSample sample;
      private final long expectedSession;
      private final long expectedContext;
      private volatile long acceptedLossGeneration;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private DispatchTask(ProfilerSample sample, long expectedSession, long expectedContext, long acceptedLossGeneration) {
         this.sample = samplex;
         this.expectedSession = expectedSessionx;
         this.expectedContext = expectedContextx;
         this.acceptedLossGeneration = acceptedLossGenerationx;
      }

      @Override
      public void run() {
         if (this.expectedSession == DonutProfilerService.this.sessionId && this.expectedContext == DonutProfilerService.this.contextId) {
            DonutProfilerService.this.dispatch(this.sample, this.expectedSession, this.expectedContext, this.acceptedLossGeneration);
         }
      }
   }

   @FunctionalInterface
   public interface Subscription extends AutoCloseable {
      @Override
      void close();
   }
}
