package me.drake.vieclient.gui.nvg.adapter.setting;

import me.drake.vieclient.core.setting.BoolSetting;

public class BooleanSetting extends Setting {
   private final BoolSetting wrapped;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BooleanSetting(CharSequence name, boolean value) {
      super(name);
      this.wrapped = new BoolSetting(name.toString(), "", value);
   }

   public BooleanSetting(BoolSetting wrapped) {
      super(wrappedx.getName());
      this.wrapped = wrappedx;
   }

   public void toggle() {
      this.wrapped.toggle();
   }

   public void setValue(boolean a) {
      this.wrapped.set(a);
   }

   public boolean getDefaultValue() {
      return (Boolean)this.wrapped.getDefaultValue();
   }

   public boolean getValue() {
      return (Boolean)this.wrapped.get();
   }

   public BooleanSetting setDescription(CharSequence s) {
      super.setDescription(s);
      return this;
   }

   @Override
   public void resetValue() {
      this.wrapped.set((Boolean)this.wrapped.getDefaultValue());
   }

   @Override
   public boolean isVisible() {
      return this.wrapped != null ? this.wrapped.isVisibleInGui() : super.isVisible();
   }

   public BooleanSetting setVisible(boolean visible) {
      super.setVisible(visible);
      if (this.wrapped != null) {
         this.wrapped.setVisibleInGui(visible);
      }

      return this;
   }

   public BoolSetting raw() {
      return this.wrapped;
   }
}
