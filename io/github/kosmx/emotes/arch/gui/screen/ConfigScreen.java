package io.github.kosmx.emotes.arch.gui.screen;

import com.mojang.serialization.Codec;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.screen.EmoteMenu;
import io.github.kosmx.emotes.arch.screen.ExportMenu;
import io.github.kosmx.emotes.common.SerializableConfig;
import io.github.kosmx.emotes.server.config.Serializer;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_315;
import net.minecraft.class_353;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_4667;
import net.minecraft.class_5244;
import net.minecraft.class_7172;
import net.minecraft.class_7842;
import net.minecraft.class_7919;
import net.minecraft.class_8667;
import net.minecraft.class_7172.class_7173;
import net.minecraft.class_7172.class_7174;
import net.minecraft.class_7172.class_7277;

public class ConfigScreen extends class_4667 {
   private static final class_2561 TITLE = class_2561.method_43471("emotecraft.otherconfig");
   private static final class_2561 CATEGORY_GENERAL = class_2561.method_43471("emotecraft.otherconfig.category.general");
   private static final class_2561 CATEGORY_EXPERT = class_2561.method_43471("emotecraft.otherconfig.category.expert");
   private static final class_2561 CATEGORY_LEGACY = class_2561.method_43471("emotecraft.otherconfig.category.legacy");
   private static final class_2561 RESET_CONFIG_TITLE = class_2561.method_43471("emotecraft.resetConfig.title");
   private static final class_2561 RESET_CONFIG_MSG = class_2561.method_43471("emotecraft.resetConfig.message");
   private static final class_2561 EXPORT = class_2561.method_43471("emotecraft.options.export");
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !ConfigScreen.class.desiredAssertionStatus();

   public ConfigScreen(class_437 parent) {
      super(parent, class_310.method_1551().field_1690, TITLE);
   }

   protected void method_60325() {
      assert this.field_51824 != null;
      this.field_51824.method_58227(Collections.singletonList(new class_7842(CATEGORY_GENERAL, this.field_22793)));
      PlatformTools.getConfig().basics.forEach(entry -> this.addConfigEntry((SerializableConfig.ConfigEntry<?>)entry, this.field_51824));
      this.field_51824.method_58227(Collections.singletonList(new class_7842(CATEGORY_EXPERT, this.field_22793)));
      PlatformTools.getConfig().expert.forEach(entry -> this.addConfigEntry((SerializableConfig.ConfigEntry<?>)entry, this.field_51824));
      this.field_51824.method_58227(Collections.singletonList(new class_7842(CATEGORY_LEGACY, this.field_22793)));
      PlatformTools.getConfig().legacy.forEach(entry -> this.addConfigEntry((SerializableConfig.ConfigEntry<?>)entry, this.field_51824));
   }

   protected void method_31387() {
      class_8667 linearLayout = (class_8667)this.field_49503.method_48996(class_8667.method_52742().method_52735(8));
      linearLayout.method_52736(
         class_4185.method_46430(EmoteMenu.RESET, button -> this.field_22787.method_1507(new class_410(this::resetAll, RESET_CONFIG_TITLE, RESET_CONFIG_MSG)))
            .method_46432(120)
            .method_46431()
      );
      linearLayout.method_52736(class_4185.method_46430(class_5244.field_24334, button -> this.method_25419()).method_46431());
      linearLayout.method_52736(class_4185.method_46430(EXPORT, button -> this.field_22787.method_1507(new ExportMenu(this))).method_46432(120).method_46431());
   }

   private <T> void addConfigEntry(SerializableConfig.ConfigEntry<T> entry, class_353 options) {
      if (entry.showEntry() || PlatformTools.getConfig().showHiddenConfig.get()) {
         class_7277<?> tooltip;
         if (entry.hasTooltip) {
            tooltip = bx -> class_7919.method_47407(class_2561.method_43471("emotecraft.otherconfig." + entry.getName() + ".tooltip"));
         } else {
            tooltip = class_7172.method_42399();
         }

         if (entry.get() instanceof Boolean b) {
            options.method_20406(class_7172.method_41750("emotecraft.otherconfig." + entry.getName(), tooltip, b, aBoolean -> entry.set((T)aBoolean)));
         } else if (entry instanceof SerializableConfig.FloatConfigEntry floatEntry) {
            int mapSize = 1024;
            double range = floatEntry.max - floatEntry.min;
            DecimalFormat formatter = new DecimalFormat("0.00");
            Function<Integer, Double> i2d = integer -> (double)integer.intValue() / mapSize * range + floatEntry.min;
            Function<Double, Integer> d2i = aDouble -> (int)((aDouble - floatEntry.min) / range * mapSize);
            options.method_20406(
               new class_7172(
                  "emotecraft.otherconfig." + floatEntry.getName(),
                  tooltip,
                  (component, object) -> class_315.method_41783(component, class_2561.method_43470(formatter.format(floatEntry.getTextVal()))),
                  new class_7174(0, mapSize),
                  Codec.DOUBLE.xmap(d2i, i2d),
                  d2i.apply(floatEntry.getConfigVal()),
                  integer -> floatEntry.setConfigVal(i2d.apply(integer))
               )
            );
         } else if (entry instanceof SerializableConfig.EnumConfigEntry<?> enumEntry) {
            this.addEnumEntry(options, tooltip, enumEntry);
         }
      }
   }

   private <T extends Enum<T>> void addEnumEntry(class_353 options, class_7277<?> tooltip, SerializableConfig.EnumConfigEntry<T> entry) {
      Class<T> enumClass = entry.getEnumClass();
      T[] values = (T[])enumClass.getEnumConstants();
      Codec<T> codec = Codec.STRING.xmap(s -> Enum.valueOf(enumClass, s), Object::toString);
      options.method_20406(
         new class_7172(
            "emotecraft.otherconfig." + entry.getName(),
            tooltip,
            (component, value) -> class_2561.method_43470(value.name()),
            new class_7173(List.of(values), codec),
            codec,
            entry.get(),
            entry::set
         )
      );
   }

   private void resetAll(boolean bl) {
      if (bl) {
         PlatformTools.getConfig().iterate(SerializableConfig.ConfigEntry::resetToDefault);
      }

      this.field_22787.method_1507(this);
   }

   public void method_25432() {
      Serializer.INSTANCE.saveConfig();
   }
}
