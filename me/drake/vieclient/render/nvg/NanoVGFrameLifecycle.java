package me.drake.vieclient.render.nvg;

import java.util.function.BooleanSupplier;

final class NanoVGFrameLifecycle {
   private final BooleanSupplier begin;
   private final Runnable end;
   private final Runnable abort;
   private final Runnable enterNested;
   private final Runnable exitNested;
   private int depth;
   private long generation;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   NanoVGFrameLifecycle(BooleanSupplier begin, Runnable end, Runnable abort) {
      this(beginx, endx, abortx, () -> {}, () -> {});
   }

   NanoVGFrameLifecycle(BooleanSupplier begin, Runnable end, Runnable abort, Runnable enterNested, Runnable exitNested) {
      this.begin = beginx;
      this.end = endx;
      this.abort = abortx;
      this.enterNested = enterNestedx;
      this.exitNested = exitNestedx;
   }

   synchronized NanoVGFrame acquire() {
      if (this.depth == 0) {
         if (!this.begin.getAsBoolean()) {
            return new NanoVGFrame(this, this.generation, false, false);
         }

         this.generation++;
         this.depth++;
         return new NanoVGFrame(this, this.generation, true, false);
      } else {
         try {
            this.enterNested.run();
         } catch (Throwable var2) {
            return new NanoVGFrame(this, this.generation, false, false);
         }

         this.depth++;
         return new NanoVGFrame(this, this.generation, true, true);
      }
   }

   synchronized void release(long leaseGeneration, boolean isolated) {
      if (this.depth != 0 && leaseGeneration == this.generation) {
         try {
            if (isolated) {
               this.exitNested.run();
            }
         } finally {
            if (--this.depth == 0) {
               this.end.run();
            }
         }
      }
   }

   synchronized void forceAbort() {
      if (this.depth != 0) {
         this.depth = 0;
         this.generation++;
         this.abort.run();
      }
   }

   synchronized boolean isActive() {
      return this.depth > 0;
   }
}
