package me.lucko.fabric.api.permissions.v0;

import net.minecraft.class_12094;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_2168;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3532;

class Util {
   static class_2168 commandSourceFromEntity(class_1297 entity) {
      if (entity instanceof class_3222) {
         return ((class_3222)entity).method_64396();
      } else {
         class_1937 world = entity.method_73183();
         if (world instanceof class_3218) {
            return entity.method_5671((class_3218)world);
         } else {
            throw new IllegalArgumentException("Entity '" + entity + "' is not a server entity. Try passing a CommandSource directly instead.");
         }
      }
   }

   static class_12094 permissionLevelFromInt(int level) {
      return class_12094.method_75027(class_3532.method_15340(level, 0, class_12094.field_63200.method_75026()));
   }
}
