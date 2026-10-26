package io.github.kosmx.emotes.arch.screen.utils;

import io.github.kosmx.emotes.mc.McUtils;
import java.util.Optional;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import net.minecraft.class_350;
import net.minecraft.class_437;

public enum EmotecraftTexture {
   HEADER_SEPARATOR(class_437.field_49895, class_437.field_49897),
   FOOTER_SEPARATOR(class_437.field_49896, class_437.field_49898),
   MENU_LIST_BACKGROUND(class_350.field_49478, class_350.field_49892);

   private final class_2960 minecraft;
   private final class_2960 minecraftInWorld;
   private final class_2960 emotecraft;
   private final class_2960 emotecraftInWorld;

   EmotecraftTexture(class_2960 minecraft, class_2960 minecraftInWorld) {
      this(minecraft, minecraftInWorld, McUtils.newIdentifier(minecraft.method_12832()), McUtils.newIdentifier(minecraftInWorld.method_12832()));
   }

   EmotecraftTexture(class_2960 minecraft, class_2960 minecraftInWorld, class_2960 emotecraft, class_2960 emotecraftInWorld) {
      this.minecraft = minecraft;
      this.minecraftInWorld = minecraftInWorld;
      this.emotecraft = emotecraft;
      this.emotecraftInWorld = emotecraftInWorld;
   }

   public class_2960 identifier(boolean inWorld) {
      class_3300 manager = class_310.method_1551().method_1478();
      if (inWorld) {
         Optional<class_3298> resourceInWorld = manager.method_14486(this.emotecraftInWorld);
         if (resourceInWorld.isPresent()) {
            return this.emotecraftInWorld;
         }

         Optional<class_3298> resource = manager.method_14486(this.emotecraft);
         return resource.isPresent() ? this.emotecraft : this.minecraftInWorld;
      } else {
         Optional<class_3298> resource = manager.method_14486(this.emotecraft);
         return resource.isEmpty() ? this.minecraft : this.emotecraft;
      }
   }

   // $VF: synthetic method
   private static EmotecraftTexture[] $values() {
      return new EmotecraftTexture[]{HEADER_SEPARATOR, FOOTER_SEPARATOR, MENU_LIST_BACKGROUND};
   }
}
