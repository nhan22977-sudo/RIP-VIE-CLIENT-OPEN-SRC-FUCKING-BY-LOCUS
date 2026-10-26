package me.drake.vieclient.core.setting;

import java.util.Arrays;
import java.util.List;

public final class ModeSetting extends EnumSetting {
   public int index;
   private final List possibleValues;
   private final int originalValue;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ModeSetting(String name, String description, Enum defaultValue, Class type) {
      super(name, description, defaultValue);
      this.possibleValues = Arrays.asList((Enum[])type.getEnumConstants());
      this.index = this.possibleValues.indexOf(defaultValue);
      this.originalValue = this.index;
   }

   public Enum get() {
      return (Enum)super.get();
   }

   public Enum getValue() {
      return (Enum)super.get();
   }

   public void setMode(Enum value) {
      Enum var2 = value;
      this.index = this.possibleValues.indexOf(var2);
      super.set(var2);
   }

   public void setModeIndex(int index) {
      this.index = indexx;
      if (indexx >= 0 && indexx < this.possibleValues.size()) {
         super.set((Enum)this.possibleValues.get(indexx));
      }
   }

   public int getModeIndex() {
      return this.possibleValues.indexOf(this.get());
   }

   public int getOriginalValue() {
      return this.originalValue;
   }

   public void set(Enum value) {
      super.set(value);
      this.index = this.possibleValues.indexOf(value);
   }

   public void cycleUp() {
      this.cycle(false);
      this.index = this.possibleValues.indexOf(this.get());
   }

   public void cycleDown() {
      this.cycle(true);
      this.index = this.possibleValues.indexOf(this.get());
   }

   public boolean isMode(Enum value) {
      return this.get() == value;
   }

   public List getPossibleValues() {
      return this.possibleValues;
   }
}
