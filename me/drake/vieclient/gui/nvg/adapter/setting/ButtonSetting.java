package me.drake.vieclient.gui.nvg.adapter.setting;

public class ButtonSetting extends Setting {
   private final me.drake.vieclient.core.setting.ButtonSetting wrapped;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ButtonSetting(me.drake.vieclient.core.setting.ButtonSetting wrapped) {
      super(wrappedx.getName());
      this.wrapped = wrappedx;
   }

   public void press() {
      this.wrapped.press();
   }
}
