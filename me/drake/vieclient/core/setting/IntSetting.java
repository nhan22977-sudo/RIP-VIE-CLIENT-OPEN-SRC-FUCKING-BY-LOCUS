package me.drake.vieclient.core.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class IntSetting extends Setting {
   private final int min;
   private final int max;
   private final int step;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public IntSetting(String name, String description, int defaultValue, int min, int max) {
      this(name, description, defaultValue, minx, maxx, 1);
   }

   public IntSetting(String name, String description, int defaultValue, int min, int max, int step) {
      super(name, description, defaultValue);
      this.min = minx;
      this.max = maxx;
      this.step = stepx;
   }

   public void set(Integer value) {
      int var2 = Math.max(this.min, Math.min(this.max, value));
      if (this.step > 1) {
         var2 = Math.round((float)(var2 - this.min) / this.step) * this.step + this.min;
      }

      super.set(var2);
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   public int getStep() {
      return this.step;
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive((Number)this.get());
   }

   @Override
   public void fromJson(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         this.set(json.getAsInt());
      }
   }
}
