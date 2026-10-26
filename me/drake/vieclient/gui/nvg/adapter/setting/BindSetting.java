package me.drake.vieclient.gui.nvg.adapter.setting;

import me.drake.vieclient.core.setting.KeybindSetting;
import me.drake.vieclient.gui.nvg.adapter.module.Module;

public final class BindSetting extends Setting {
   private final boolean moduleKey;
   private final Module module;
   private final KeybindSetting keybindSetting;
   private final me.drake.vieclient.core.setting.BindSetting coreBindSetting;
   private int value;
   private boolean listening = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BindSetting(CharSequence name, int value, boolean isModule) {
      this(name, valuex, isModule, null, null, null);
   }

   public BindSetting(CharSequence name, int value, boolean isModule, Module module) {
      this(name, valuex, isModule, modulex, null, null);
   }

   public BindSetting(CharSequence name, int value, boolean isModule, Module module, KeybindSetting keybindSetting) {
      this(name, valuex, isModule, modulex, keybindSettingx, null);
   }

   public BindSetting(
      CharSequence name, int value, boolean isModule, Module module, KeybindSetting keybindSetting, me.drake.vieclient.core.setting.BindSetting coreBindSetting
   ) {
      super(name);
      this.moduleKey = isModule;
      this.module = modulex;
      this.keybindSetting = keybindSettingx;
      this.coreBindSetting = coreBindSettingx;
      this.value = valuex;
   }

   public static int toVieKey(int k) {
      return k <= (-100) ? (900) - k : k;
   }

   public static int toLegacyKey(int k) {
      return k >= (1000) ? -(k - (1000)) - (100) : k;
   }

   public static int toMeilaaKey(int k) {
      return toLegacyKey(k);
   }

   public boolean isModuleKey() {
      return this.moduleKey;
   }

   public boolean isListening() {
      return this.listening;
   }

   public void setListening(boolean l) {
      this.listening = l;
   }

   public void toggleListening() {
      this.listening = !this.listening;
   }

   public int getDefaultValue() {
      return 0;
   }

   public int getValue() {
      if (this.coreBindSetting != null) {
         return toMeilaaKey((Integer)this.coreBindSetting.get());
      } else if (this.keybindSetting != null) {
         return toMeilaaKey((Integer)this.keybindSetting.get());
      } else {
         return this.moduleKey && this.module != null ? toMeilaaKey(this.module.rawKeybind()) : this.value;
      }
   }

   public void setValue(int a) {
      if (this.coreBindSetting != null) {
         this.coreBindSetting.set(toVieKey(a));
      } else if (this.keybindSetting != null) {
         this.keybindSetting.set(toVieKey(a));
      } else if (this.moduleKey && this.module != null) {
         this.module.setKeybind(a);
      } else {
         this.value = toMeilaaKey(toVieKey(a));
      }
   }

   public BindSetting setDescription(CharSequence s) {
      super.setDescription(s);
      return this;
   }

   @Override
   public void resetValue() {
      this.setValue(this.getDefaultValue());
   }
}
