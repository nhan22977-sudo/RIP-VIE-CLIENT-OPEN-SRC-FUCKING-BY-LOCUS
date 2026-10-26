package me.drake.vieclient.core.setting;

public final class BindSetting extends Setting {
   private final boolean moduleKey;
   private boolean listening;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BindSetting(String name, String description, int value) {
      super(name, description, value);
      this.moduleKey = false;
   }

   public BindSetting(String name, String description, int value, boolean isModule) {
      super(name, description, value);
      this.moduleKey = isModule;
   }

   public boolean isModuleKey() {
      return this.moduleKey;
   }

   public boolean isListening() {
      return this.listening;
   }

   public void setListening(boolean listening) {
      this.listening = listeningx;
   }

   public void setValue(int a) {
      this.set(a);
   }

   public void toggleListening() {
      this.listening = !this.listening;
   }
}
