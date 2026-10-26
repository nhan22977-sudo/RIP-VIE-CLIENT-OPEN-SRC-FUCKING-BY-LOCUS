package me.drake.vieclient.render.nvg;

import java.util.concurrent.atomic.AtomicLong;

public final class NanoVGResourceGeneration {
   private static final NanoVGResourceGeneration GLOBAL = new NanoVGResourceGeneration();
   private final AtomicLong value = new AtomicLong();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public long current() {
      return this.value.get();
   }

   public long invalidate() {
      return this.value.incrementAndGet();
   }

   public static long currentGlobal() {
      return GLOBAL.current();
   }

   public static long invalidateGlobal() {
      return GLOBAL.invalidate();
   }
}
