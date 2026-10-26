package me.drake.vieclient.core.setting;

import java.util.Random;

public class MinMaxSetting extends Setting {
   private final double min;
   private final double max;
   private final double step;
   private final double defaultMin;
   private final double defaultMax;
   private double currentMin;
   private double currentMax;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MinMaxSetting(String name, double min, double max, double step, double defaultMin, double defaultMax) {
      super(name, "", defaultMinx);
      this.min = minx;
      this.max = maxx;
      this.step = stepx;
      this.currentMin = defaultMinx;
      this.currentMax = defaultMaxx;
      this.defaultMin = defaultMinx;
      this.defaultMax = defaultMaxx;
   }

   public int getMinInt() {
      return (int)this.currentMin;
   }

   public float getMinFloat() {
      return (float)this.currentMin;
   }

   public long getMinLong() {
      return (long)this.currentMin;
   }

   public int getMaxInt() {
      return (int)this.currentMax;
   }

   public float getMaxFloat() {
      return (float)this.currentMax;
   }

   public long getMaxLong() {
      return (long)this.currentMax;
   }

   public double getMinValue() {
      return this.min;
   }

   public double getMaxValue() {
      return this.max;
   }

   public double getCurrentMin() {
      return this.currentMin;
   }

   public double getCurrentMax() {
      return this.currentMax;
   }

   public double getDefaultMin() {
      return this.defaultMin;
   }

   public double getDefaultMax() {
      return this.defaultMax;
   }

   public double getStep() {
      return this.step;
   }

   public double getRandomDoubleInRange() {
      return this.getCurrentMax() > this.getCurrentMin() ? new Random().nextDouble(this.getCurrentMin(), this.getCurrentMax()) : this.getCurrentMin();
   }

   public int getRandomIntInRange() {
      return this.getCurrentMax() > this.getCurrentMin() ? new Random().nextInt(this.getMinInt(), this.getMaxInt()) : this.getMinInt();
   }

   public float getRandomFloatInRange() {
      return this.getCurrentMax() > this.getCurrentMin() ? new Random().nextFloat(this.getMinFloat(), this.getMaxFloat()) : this.getMinFloat();
   }

   public long getRandomLongInRange() {
      return this.getCurrentMax() > this.getCurrentMin() ? new Random().nextLong(this.getMinLong(), this.getMaxLong()) : this.getMinLong();
   }

   public void setCurrentMin(double value) {
      double var3 = 1.0 / this.step;
      this.currentMin = Math.round(Math.max(this.min, Math.min(this.max, value)) * var3) / var3;
   }

   public void setCurrentMax(double value) {
      double var3 = 1.0 / this.step;
      this.currentMax = Math.round(Math.max(this.min, Math.min(this.max, value)) * var3) / var3;
   }
}
