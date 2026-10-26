package me.drake.vieclient.modules.donut.profiler.stats;

import java.util.Arrays;

public final class RollingRobustBaseline {
   private static final double MAD_FLOOR = 0.001;
   private final double[] values;
   private final int minimumSamples;
   private int size;
   private int next;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public RollingRobustBaseline(int capacity, int minimumSamples) {
      this.values = new double[Math.max(1, capacity)];
      this.minimumSamples = Math.max(1, Math.min(this.values.length, minimumSamplesx));
   }

   public void observe(double value) {
      if (Double.isFinite(value)) {
         this.values[this.next] = value;
         this.next = (this.next + 1) % this.values.length;
         if (this.size < this.values.length) {
            this.size++;
         }
      }
   }

   public boolean ready() {
      return this.size >= this.minimumSamples;
   }

   public int size() {
      return this.size;
   }

   public double median() {
      return this.size == 0 ? 0.0 : median(this.copySorted());
   }

   public double mad() {
      if (this.size == 0) {
         return 0.001;
      }

      double var1 = this.median();
      double[] var3 = new double[this.size];

      for (int var4 = 0; var4 < this.size; var4++) {
         var3[var4] = Math.abs(this.values[var4] - var1);
      }

      Arrays.sort(var3);
      return Math.max(0.001, median(var3));
   }

   public double robustZ(double value) {
      return 0.6744897501960817 * (value - this.median()) / this.mad();
   }

   private double[] copySorted() {
      double[] var1 = Arrays.copyOf(this.values, this.size);
      Arrays.sort(var1);
      return var1;
   }

   private static double median(double[] sorted) {
      int var1 = sorted.length / (2);
      return (sorted.length & 1) == 1 ? sorted[var1] : (sorted[var1 - 1] + sorted[var1]) * 0.5;
   }
}
