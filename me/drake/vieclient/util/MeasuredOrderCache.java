package me.drake.vieclient.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

public final class MeasuredOrderCache {
   private final List input = new ArrayList();
   private final List sorted = new ArrayList();
   private final List view = Collections.unmodifiableList(this.sorted);
   private final IdentityHashMap measurements = new IdentityHashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public List update(Iterable items, Predicate visible, ToDoubleFunction measure) {
      boolean var4 = false;
      boolean var5 = false;
      int var6 = 0;

      for (Object var8 : items) {
         if (visible.test(var8)) {
            if (var6 == this.input.size()) {
               this.input.add(var8);
               var4 = true;
               var5 = true;
            } else if (this.input.get(var6) != var8) {
               this.input.set(var6, var8);
               var4 = true;
               var5 = true;
            }

            var6++;
            double var9 = measure.applyAsDouble(var8);
            Double var11 = (Double)this.measurements.get(var8);
            if (var11 == null || Double.compare(var11, var9) != 0) {
               this.measurements.put(var8, var9);
               var4 = true;
            }
         }
      }

      if (var6 < this.input.size()) {
         this.input.subList(var6, this.input.size()).clear();
         var4 = true;
         var5 = true;
      }

      if (var4) {
         this.sorted.clear();
         this.sorted.addAll(this.input);
         this.sorted.sort((a, b) -> Double.compare((Double)this.measurements.get(b), (Double)this.measurements.get(a)));
         if (var5) {
            this.measurements.keySet().removeIf(item -> !containsIdentity(this.input, item));
         }
      }

      return this.view;
   }

   private static boolean containsIdentity(List items, Object target) {
      for (Object var3 : items) {
         if (var3 == target) {
            return true;
         }
      }

      return false;
   }
}
