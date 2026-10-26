package me.drake.vieclient.gui.nvg.adapter.setting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import me.drake.vieclient.core.setting.EnumSetting;

public final class ModeSetting extends Setting {
   private final EnumSetting wrapped;
   private final Supplier supplier;
   private Enum standalone;
   public int index;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ModeSetting(CharSequence name, Enum defaultValue, Class type) {
      this(name, defaultValue, type, null);
   }

   public ModeSetting(CharSequence name, Enum defaultValue, Class type, Supplier supplier) {
      super(name);
      this.wrapped = new EnumSetting(name.toString(), "", defaultValue, (Enum[])type.getEnumConstants());
      this.supplier = supplierx;
      this.standalone = defaultValue;
      this.index = defaultValue.ordinal();
   }

   public ModeSetting(EnumSetting wrapped) {
      super(wrappedx.getName());
      this.wrapped = wrappedx;
      this.supplier = null;
      this.standalone = wrappedx != null ? (Enum)wrappedx.get() : null;
      this.index = this.standalone != null ? this.standalone.ordinal() : 0;
   }

   private EnumSetting getWrapped() {
      if (this.supplier != null) {
         try {
            EnumSetting var1 = (EnumSetting)this.supplier.get();
            if (var1 != null) {
               return var1;
            }
         } catch (Throwable var2) {
         }
      }

      return this.wrapped;
   }

   public Enum getValue() {
      EnumSetting var1 = this.getWrapped();
      if (var1 != null && var1.get() != null) {
         Enum var2 = (Enum)var1.get();
         this.standalone = var2;
         this.index = var2.ordinal();
         return var2;
      } else {
         return this.standalone;
      }
   }

   public void setMode(Enum e) {
      Enum var2 = e;
      EnumSetting var3 = this.getWrapped();
      if (var3 != null) {
         var3.set(var2);
      }

      this.standalone = var2;
      this.index = var2.ordinal();
   }

   public void setModeIndex(int index) {
      Enum[] var2 = this.possible();
      if (indexx >= 0 && indexx < var2.length) {
         this.setMode(var2[indexx]);
      }
   }

   public int getModeIndex() {
      Enum var1 = this.getValue();
      List var2 = this.getPossibleValues();
      int var3 = var2.indexOf(var1);
      return var3 >= 0 ? var3 : var1.ordinal();
   }

   public int getOriginalValue() {
      return this.standalone != null ? this.standalone.ordinal() : 0;
   }

   public void cycleUp() {
      this.cycle(false);
   }

   public void cycleDown() {
      this.cycle(true);
   }

   public void cycle(boolean reverse) {
      EnumSetting var2 = this.getWrapped();
      if (var2 != null) {
         var2.cycle(reverse);
         this.standalone = (Enum)var2.get();
         this.index = this.standalone != null ? this.standalone.ordinal() : 0;
      } else {
         Enum[] var3 = this.possible();
         if (var3.length != 0) {
            int var4 = this.getValue() != null ? this.getValue().ordinal() : 0;
            int var5 = (var4 + (reverse ? -1 : 1) + var3.length) % var3.length;
            this.setMode(var3[var5]);
         }
      }
   }

   public boolean isMode(Enum e) {
      try {
         return this.getValue() == e;
      } catch (Throwable var3) {
         return false;
      }
   }

   private Enum[] possible() {
      EnumSetting var1 = this.getWrapped();
      if (var1 != null && var1.getValues() != null) {
         return var1.getValues();
      } else {
         return this.standalone != null ? (Enum[])this.standalone.getDeclaringClass().getEnumConstants() : new Enum[0];
      }
   }

   public List getPossibleValues() {
      return Arrays.asList(this.possible());
   }

   public String getDisplayName(Enum value) {
      EnumSetting var2 = this.getWrapped();
      return var2 != null ? var2.getDisplayName(value) : value.name();
   }

   public String getDisplayName() {
      return this.getDisplayName(this.getValue());
   }

   @Override
   public void resetValue() {
      EnumSetting var1 = this.getWrapped();
      Enum var2 = var1 != null ? (Enum)var1.getDefaultValue() : ((Enum[])this.standalone.getDeclaringClass().getEnumConstants())[0];
      this.setMode(var2);
   }

   public ModeSetting setDescription(CharSequence s) {
      super.setDescription(s);
      return this;
   }
}
