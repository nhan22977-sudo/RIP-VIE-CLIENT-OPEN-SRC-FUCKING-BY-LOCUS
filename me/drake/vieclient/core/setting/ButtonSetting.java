package me.drake.vieclient.core.setting;

import com.google.gson.JsonElement;

public class ButtonSetting extends Setting {
   private final Runnable action;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ButtonSetting(String name, String description, Runnable action) {
      super(name, description, false);
      this.action = actionx;
   }

   public void press() {
      if (this.action != null) {
         this.action.run();
      }
   }

   @Override
   public JsonElement toJson() {
      return null;
   }

   @Override
   public void fromJson(JsonElement json) {
   }
}
