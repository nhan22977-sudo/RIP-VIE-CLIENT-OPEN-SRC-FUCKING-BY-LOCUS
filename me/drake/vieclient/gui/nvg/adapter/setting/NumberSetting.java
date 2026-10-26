package me.drake.vieclient.gui.nvg.adapter.setting;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;

public final class NumberSetting extends Setting {
   private final me.drake.vieclient.core.setting.Setting wrapped;
   private final double min;
   private final double max;
   private final double format;
   private double value;
   private DoubleSupplier liveGet = null;
   private DoubleConsumer liveSet = null;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public NumberSetting(CharSequence name, double min, double max, double defaultValue, double format) {
      super(name);
      this.min = minx;
      this.max = maxx;
      this.format = formatx;
      this.value = defaultValue;
      this.wrapped = null;
      this.liveGet = null;
      this.liveSet = null;
   }

   public NumberSetting(CharSequence name, double min, double max, double defaultValue, double format, DoubleSupplier liveGet, DoubleConsumer liveSet) {
      super(name);
      this.min = minx;
      this.max = maxx;
      this.format = formatx;
      this.value = defaultValue;
      this.wrapped = null;
      this.liveGet = liveGetx;
      this.liveSet = liveSetx;
   }

   public NumberSetting(DoubleSetting wrapped) {
      super(wrappedx.getName());
      this.wrapped = wrappedx;
      this.min = wrappedx.getSliderMin();
      this.max = wrappedx.getSliderMax();
      this.format = wrappedx.getStep();
      this.value = (Double)wrappedx.get();
   }

   public NumberSetting(IntSetting wrapped) {
      super(wrappedx.getName());
      this.wrapped = wrappedx;
      this.min = wrappedx.getMin();
      this.max = wrappedx.getMax();
      this.format = wrappedx.getStep();
      this.value = ((Integer)wrappedx.get()).intValue();
   }

   public double getValue() {
      if (this.liveGet != null) {
         return this.liveGet.getAsDouble();
      } else if (this.wrapped instanceof DoubleSetting var3) {
         return (Double)var3.get();
      } else {
         return this.wrapped instanceof IntSetting var1 ? ((Integer)var1.get()).intValue() : this.value;
      }
   }

   public double getDefaultValue() {
      if (this.wrapped instanceof DoubleSetting var3) {
         return (Double)var3.getDefaultValue();
      } else {
         return this.wrapped instanceof IntSetting var1 ? ((Integer)var1.getDefaultValue()).intValue() : this.value;
      }
   }

   public double getFormat() {
      return this.format;
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public double getSliderFraction() {
      return this.max <= this.min ? 0.0 : Math.max(0.0, Math.min(1.0, (this.getValue() - this.min) / (this.max - this.min)));
   }

   public int getIntValue() {
      return (int)this.getValue();
   }

   public float getFloatValue() {
      return (float)this.getValue();
   }

   public long getLongValue() {
      return (long)this.getValue();
   }

   @Override
   public void resetValue() {
      if (this.liveSet != null) {
         this.liveSet.accept(this.getDefaultValue());
      } else {
         if (this.wrapped instanceof DoubleSetting var1) {
            var1.set((Double)var1.getDefaultValue());
         } else if (this.wrapped instanceof IntSetting var2) {
            var2.set((Integer)var2.getDefaultValue());
         } else {
            this.value = this.getDefaultValue();
         }
      }
   }

   public void setValue(double newValue) {
      if (this.liveSet != null) {
         this.liveSet.accept(newValue);
      } else {
         if (this.wrapped instanceof DoubleSetting var3) {
            var3.set(newValue);
         } else if (this.wrapped instanceof IntSetting var4) {
            var4.set((int)Math.round(newValue));
         } else {
            this.value = Math.max(this.min, Math.min(this.max, newValue));
         }
      }
   }

   public void getValue(double newValue) {
      this.setValue(newValue);
   }

   public NumberSetting getValue(CharSequence s) {
      super.setDescription(s);
      return this;
   }

   public NumberSetting setDescription(CharSequence s) {
      super.setDescription(s);
      return this;
   }
}
