package me.drake.vieclient.core.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BoolSetting extends Setting {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BoolSetting(String name, String description, boolean defaultValue) {
      super(name, description, defaultValue);
   }

   public boolean isEnabled() {
      return (Boolean)this.get();
   }

   public void toggle() {
      this.set(!(Boolean)this.get());
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive((Boolean)this.get());
   }

   @Override
   public void fromJson(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         this.set(json.getAsBoolean());
      }
   }
}
