package me.drake.vieclient.gui.nvg.adapter.setting;

public class StringSetting extends Setting {
   private final me.drake.vieclient.core.setting.StringSetting wrapped;
   public String value;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public StringSetting(CharSequence name, String a) {
      super(name);
      this.wrapped = null;
      this.value = a;
   }

   public StringSetting(me.drake.vieclient.core.setting.StringSetting wrapped) {
      super(wrappedx.getName());
      this.wrapped = wrappedx;
      this.value = (String)wrappedx.get();
   }

   public void setValue(String a) {
      if (this.wrapped != null) {
         this.wrapped.set(a);
      }

      this.value = a;
   }

   public String getValue() {
      return this.wrapped != null ? (String)this.wrapped.get() : this.value;
   }

   public StringSetting setDescription(CharSequence s) {
      super.setDescription(s);
      return this;
   }

   @Override
   public void resetValue() {
      if (this.wrapped != null) {
         this.setValue((String)this.wrapped.getDefaultValue());
      } else {
         this.value = "";
      }
   }
}
