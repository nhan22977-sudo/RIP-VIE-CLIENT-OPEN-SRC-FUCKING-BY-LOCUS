package me.drake.vieclient.core.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class StringSetting extends Setting {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public StringSetting(String name, String description, String defaultValue) {
      super(name, description, defaultValue);
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive((String)this.get());
   }

   @Override
   public void fromJson(JsonElement j) {
      if (j != null && j.isJsonPrimitive()) {
         this.set(j.getAsString());
      }
   }
}
