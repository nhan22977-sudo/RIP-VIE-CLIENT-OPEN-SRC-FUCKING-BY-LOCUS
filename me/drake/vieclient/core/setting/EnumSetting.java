package me.drake.vieclient.core.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class EnumSetting extends Setting {
   private final Class enumClass;
   private final Map displayNames = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public EnumSetting(String name, String description, Enum defaultValue) {
      super(name, description, defaultValue);
      this.enumClass = defaultValue.getDeclaringClass();
   }

   public EnumSetting withDisplayName(Enum value, String displayName) {
      this.displayNames.put(value, displayName);
      return this;
   }

   public String getDisplayName() {
      return this.getDisplayName((Enum)this.get());
   }

   public String getDisplayName(Enum value) {
      return this.displayNames.getOrDefault(value, value.name());
   }

   public EnumSetting(String name, String description, Enum defaultValue, Enum[] values) {
      this(name, description, defaultValue);
   }

   public void cycle() {
      this.cycle(false);
   }

   public void cycle(boolean reverse) {
      Enum[] var2 = (Enum[])this.enumClass.getEnumConstants();
      int var3 = reverse ? -1 : 1;
      int var4 = (((Enum)this.get()).ordinal() + var3 + var2.length) % var2.length;
      this.set(var2[var4]);
   }

   public Enum[] getValues() {
      return (Enum[])this.enumClass.getEnumConstants();
   }

   public Class getEnumClass() {
      return this.enumClass;
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive(((Enum)this.get()).name());
   }

   @Override
   public void fromJson(JsonElement json) {
      if (json != null && !json.isJsonNull()) {
         String var2 = null;
         Integer var3 = null;
         if (json.isJsonPrimitive()) {
            JsonPrimitive var4 = json.getAsJsonPrimitive();
            if (var4.isNumber()) {
               var3 = var4.getAsInt();
            } else {
               var2 = var4.getAsString();
            }
         } else if (json.isJsonObject()) {
            JsonObject var11 = json.getAsJsonObject();
            if (var11.has("value")
               && var11.get("value").isJsonPrimitive()) {
               JsonPrimitive var5 = var11.getAsJsonPrimitive("value");
               if (var5.isNumber()) {
                  var3 = var5.getAsInt();
               } else {
                  var2 = var5.getAsString();
               }
            } else if (var11.has("name")
               && var11.get("name").isJsonPrimitive()) {
               var2 = var11.get("name").getAsString();
            } else if (var11.has("mode")
               && var11.get("mode").isJsonPrimitive()) {
               var2 = var11.get("mode").getAsString();
            } else if (var11.has("index")
               && var11.get("index").isJsonPrimitive()) {
               var3 = var11.get("index").getAsInt();
            }
         }

         Enum[] var12 = (Enum[])this.enumClass.getEnumConstants();
         if (var3 != null && var3 >= 0 && var3 < var12.length) {
            this.set(var12[var3]);
         } else {
            if (var2 != null) {
               try {
                  this.set(Enum.valueOf(this.enumClass, var2));
               } catch (IllegalArgumentException var10) {
                  for (Enum var9 : var12) {
                     if (var9.name().equalsIgnoreCase(var2)) {
                        this.set(var9);
                        return;
                     }
                  }

                  if ("DEFAULT".equalsIgnoreCase(var2) && var12.length > 0) {
                     this.set(var12[0]);
                  }
               }
            }
         }
      }
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }
}
