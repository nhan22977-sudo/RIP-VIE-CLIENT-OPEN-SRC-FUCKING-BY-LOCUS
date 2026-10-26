package me.drake.vieclient.core.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class DoubleSetting extends Setting {
   private final double min;
   private final double max;
   private final double sliderMin;
   private final double sliderMax;
   private final double step;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public DoubleSetting(String name, String description, double defaultValue, double min, double max) {
      this(name, description, defaultValue, minx, maxx, minx, maxx, 0.1);
   }

   public DoubleSetting(String name, String description, double defaultValue, double min, double max, double step) {
      this(name, description, defaultValue, minx, maxx, minx, maxx, stepx);
   }

   public DoubleSetting(String name, String description, double defaultValue, double min, double max, double sliderMin, double sliderMax, double step) {
      super(name, description, defaultValue);
      this.min = minx;
      this.max = maxx;
      this.sliderMin = sliderMinx;
      this.sliderMax = sliderMaxx;
      this.step = stepx;
   }

   public void set(Double value) {
      double var2 = Math.max(this.min, Math.min(this.max, value));
      if (this.step > 0.0) {
         var2 = Math.round(var2 / this.step) * this.step;
      }

      super.set(var2);
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public double getSliderMin() {
      return this.sliderMin;
   }

   public double getSliderMax() {
      return this.sliderMax;
   }

   public double getStep() {
      return this.step;
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive((Number)this.get());
   }

   @Override
   public void fromJson(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         this.set(json.getAsDouble());
      }
   }
}
