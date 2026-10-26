package me.drake.vieclient.gui.nvg.adapter.setting;

public class MinMaxSetting extends Setting {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MinMaxSetting(CharSequence name, double min, double max, double step, double defaultMin, double defaultMax) {
      super(name);
   }

   public double getMinValue() {
      return 0.0;
   }

   public double getMaxValue() {
      return 1.0;
   }

   public double getCurrentMin() {
      return 0.0;
   }

   public double getCurrentMax() {
      return 1.0;
   }

   public void setCurrentMin(double v) {
   }

   public void setCurrentMax(double v) {
   }

   public double getDefaultMin() {
      return 0.0;
   }

   public double getDefaultMax() {
      return 1.0;
   }

   public double getStep() {
      return 1.0;
   }

   public int getMinInt() {
      return 0;
   }

   public int getMinFloat() {
      return 0;
   }

   public int getMinLong() {
      return 0;
   }

   public int getMaxInt() {
      return 1;
   }

   public int getMaxFloat() {
      return 1;
   }

   public int getMaxLong() {
      return 1;
   }

   public double getRandomDoubleInRange() {
      return 0.0;
   }

   public int getRandomIntInRange() {
      return 0;
   }

   public int getRandomFloatInRange() {
      return 0;
   }

   public int getRandomLongInRange() {
      return 0;
   }

   public MinMaxSetting setDescription(CharSequence s) {
      super.setDescription(s);
      return this;
   }
}
