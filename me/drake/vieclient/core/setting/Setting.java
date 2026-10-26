package me.drake.vieclient.core.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import me.drake.vieclient.core.i18n.Localization;

public class Setting {
   private final String name;
   private final String description;
   private final Object defaultValue;
   private Object value;
   private boolean visibleInGui = true;
   private BooleanSupplier visibilityCondition = () -> true;
   private Consumer onChanged;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Setting(String name, String description, Object defaultValue) {
      this.name = namex;
      this.description = descriptionx;
      this.defaultValue = defaultValuex;
      this.value = defaultValuex;
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return Localization.getSettingDescription(this.name, this.description);
   }

   public Object getDefaultValue() {
      return this.defaultValue;
   }

   public Object get() {
      return this.value;
   }

   public void set(Object value) {
      Object var2 = this.value;
      this.value = valuex;
      if (this.onChanged != null && !valuex.equals(var2)) {
         this.onChanged.accept(valuex);
      }
   }

   public void reset() {
      this.set(this.defaultValue);
   }

   public Setting onChanged(Consumer callback) {
      if (this.onChanged != null) {
         Consumer var2 = this.onChanged;
         this.onChanged = val -> {
            var2.accept(val);
            callback.accept(val);
         };
      } else {
         this.onChanged = callback;
      }

      return this;
   }

   public boolean isVisibleInGui() {
      try {
         return this.visibleInGui && (this.visibilityCondition == null || this.visibilityCondition.getAsBoolean());
      } catch (Throwable var2) {
         return this.visibleInGui;
      }
   }

   public boolean isDeclaredVisibleInGui() {
      return this.visibleInGui;
   }

   public Setting setVisibleInGui(boolean visibleInGui) {
      this.visibleInGui = visibleInGuix;
      return this;
   }

   public Setting visibleWhen(BooleanSupplier condition) {
      this.visibilityCondition = condition == null ? () -> true : condition;
      return this;
   }

   public JsonElement toJson() {
      if (this.value instanceof Boolean var4) {
         return new JsonPrimitive(var4);
      } else if (this.value instanceof Number var3) {
         return new JsonPrimitive(var3);
      } else {
         return this.value instanceof String var1 ? new JsonPrimitive(var1) : new JsonPrimitive(this.value.toString());
      }
   }

   public void fromJson(JsonElement json) {
      if (json != null && !json.isJsonNull()) {
         try {
            if (this.value instanceof Boolean) {
               this.set(json.getAsBoolean());
            } else if (this.value instanceof Integer) {
               this.set(json.getAsInt());
            } else if (this.value instanceof Double) {
               this.set(json.getAsDouble());
            } else if (this.value instanceof Float) {
               this.set(json.getAsFloat());
            } else if (this.value instanceof String) {
               this.set(json.getAsString());
            }
         } catch (Exception var3) {
         }
      }
   }
}
