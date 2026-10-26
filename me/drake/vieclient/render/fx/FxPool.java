package me.drake.vieclient.render.fx;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

public final class FxPool {
   private final Deque idle;
   private final Supplier factory;
   private final int maxIdle;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FxPool(Supplier factory, int maxIdle) {
      this.factory = factoryx;
      this.maxIdle = Math.max(1, maxIdlex);
      this.idle = new ArrayDeque(Math.min(this.maxIdle, 64));
   }

   public Object obtain() {
      Object var1 = this.idle.pollLast();
      return var1 != null ? var1 : this.factory.get();
   }

   public void release(Object obj) {
      if (obj != null) {
         if (this.idle.size() < this.maxIdle) {
            this.idle.addLast(obj);
         }
      }
   }

   public void clear() {
      this.idle.clear();
   }

   public int idleCount() {
      return this.idle.size();
   }
}
