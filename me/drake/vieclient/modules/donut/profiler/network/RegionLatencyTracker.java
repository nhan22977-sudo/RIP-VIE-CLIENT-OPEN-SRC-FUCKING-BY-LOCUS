package me.drake.vieclient.modules.donut.profiler.network;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public final class RegionLatencyTracker {
   private static final long MAX_WINDOW_MS = 2000L;
   private static final RegionLatencyTracker INSTANCE = new RegionLatencyTracker();
   private final Map pending = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static RegionLatencyTracker get() {
      return INSTANCE;
   }

   public synchronized void sent(int x, int y, int z, long nowMs, double connectionBaselineMs) {
      this.prune(nowMs);
      if (y >= 0) {
         this.pending.put(new RegionLatencyTracker.Position(x, y, z), new RegionLatencyTracker.PendingAction(Math.max(0L, nowMs), connectionBaselineMs));
      }
   }

   public synchronized Optional received(int x, int y, int z, long nowMs) {
      if (y < 0) {
         return Optional.empty();
      }

      RegionLatencyTracker.PendingAction var6 = (RegionLatencyTracker.PendingAction)this.pending.remove(new RegionLatencyTracker.Position(x, y, z));
      if (var6 == null) {
         return Optional.empty();
      }

      long var7 = nowMs - var6.sentMs();
      return var7 >= 0L && var7 <= 2000L
         ? Optional.of(new RegionLatencySample(x, y, z, var7, var6.connectionBaselineMs(), Math.max(0L, nowMs) * 1000000L))
         : Optional.empty();
   }

   public synchronized void clear() {
      this.pending.clear();
   }

   private void prune(long nowMs) {
      Iterator var3 = this.pending.values().iterator();

      while (var3.hasNext()) {
         RegionLatencyTracker.PendingAction var4 = (RegionLatencyTracker.PendingAction)var3.next();
         if (nowMs - var4.sentMs() > 2000L) {
            var3.remove();
         }
      }
   }

   private record PendingAction(long sentMs, double connectionBaselineMs) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record Position(int x, int y, int z) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
