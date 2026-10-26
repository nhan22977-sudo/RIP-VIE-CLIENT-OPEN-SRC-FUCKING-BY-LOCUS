package io.github.kosmx.emotes.mc;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_5244;
import net.minecraft.class_5455;
import net.minecraft.class_8824;
import net.minecraft.class_7225.class_7874;

public class McUtils {
   public static final class_2561 MOD_NAME = class_2561.method_43470("Emotecraft");
   public static final class_2561 SLASH = class_2561.method_43470("/");

   public static class_2561 fromJson(String json, class_7874 registries) {
      if (json != null && !json.isBlank()) {
         try {
            return (class_2561)class_8824.field_46597.parse(registries.method_57093(JsonOps.INSTANCE), JsonParser.parseString(json)).getOrThrow();
         } catch (Throwable e) {
            return class_2561.method_30163(json);
         }
      } else {
         return class_5244.field_39003;
      }
   }

   public static class_2561 fromJson(Object obj) {
      return fromJson(obj, class_5455.field_40585);
   }

   public static class_2561 fromJson(Object obj, class_7874 registries) {
      return switch (obj) {
         case null -> class_5244.field_39003;
         case String string -> fromJson(string, registries);
         case JsonElement element -> (class_2561)class_8824.field_46597.parse(registries.method_57093(JsonOps.INSTANCE), element).getOrThrow();
         default -> throw new IllegalArgumentException("Can not create Text from " + obj.getClass().getName());
      };
   }

   public static class_2960 newIdentifier(String id) {
      return class_2960.method_60655("emotecraft", id);
   }
}
