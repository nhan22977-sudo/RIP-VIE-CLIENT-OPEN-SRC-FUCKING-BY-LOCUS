package io.github.kosmx.emotes.main.config;

import io.github.kosmx.emotes.common.SerializableConfig;
import io.github.kosmx.emotes.common.tools.BiMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.class_5498;
import net.minecraft.class_3675.class_306;

public class ClientConfig extends SerializableConfig {
   public final List<SerializableConfig.ConfigEntry<?>> legacy = new ArrayList<>();
   public final SerializableConfig.ConfigEntry<Boolean> oldChooseWheel = new SerializableConfig.ConfigEntry<>("oldChooseWheel", false, false, this.legacy);
   public final SerializableConfig.ConfigEntry<Boolean> showIconsIfPossible = new SerializableConfig.ConfigEntry<>(
      "showIconsIfPossible", false, false, this.legacy
   );
   public final SerializableConfig.EnumConfigEntry<CloseWheel> closeWheelType = new SerializableConfig.EnumConfigEntry<>(
      "closeWheelType", CloseWheel.PRESS, true, this.basics
   );
   public final SerializableConfig.ConfigEntry<Boolean> enableWheelKeyboardNav = new SerializableConfig.ConfigEntry<>(
      "enableWheelKeyboardNav", true, false, this.basics
   );
   public final SerializableConfig.EnumConfigEntry<class_5498> cameraType = new SerializableConfig.EnumConfigEntry(
      "cameraType", class_5498.field_26666, true, this.basics
   );
   public final SerializableConfig.ConfigEntry<Boolean> checkPose = new SerializableConfig.ConfigEntry<>("checkPose", true, true, this.expert);
   public final SerializableConfig.ConfigEntry<Boolean> alwaysOpenEmoteScreen = new SerializableConfig.ConfigEntry<>(
      "alwaysOpenScreen", false, true, this.basics
   );
   public final SerializableConfig.ConfigEntry<Boolean> displayNowPlaying = new SerializableConfig.ConfigEntry<>("displayNowPlaying", true, false, this.expert);
   public final SerializableConfig.ConfigEntry<Boolean> alwaysValidate = new SerializableConfig.ConfigEntry<>("alwaysValidateEmote", false, true, this.expert);
   public final SerializableConfig.ConfigEntry<Boolean> enablePlayerSafety = new SerializableConfig.ConfigEntry<>("playersafety", true, true, this.expert);
   public final SerializableConfig.ConfigEntry<Float> stopThreshold = new SerializableConfig.FloatConfigEntry(
      "stopthreshold", "stopThreshold", 0.04F, true, this.expert, "options.generic_value", -3.912F, 8.0F, 0.0F
   ) {
      @Override
      public double getConfigVal() {
         return Math.log(this.get().floatValue());
      }

      @Override
      public void setConfigVal(double newVal) {
         this.set((float)Math.exp(newVal));
      }
   };
   public final SerializableConfig.ConfigEntry<Float> yRatio = new SerializableConfig.FloatConfigEntry(
      "yratio", "yRatio", 0.75F, true, this.expert, "options.percent_value", 0.0F, 100.0F, 1.0F
   ) {
      @Override
      public double getConfigVal() {
         return this.get() * 100.0F;
      }

      @Override
      public void setConfigVal(double newVal) {
         this.set((float)(newVal / 100.0));
      }

      @Override
      public double getTextVal() {
         return this.getConfigVal();
      }
   };
   public final SerializableConfig.ConfigEntry<Boolean> showHiddenConfig = new SerializableConfig.ConfigEntry<>(
      "showHiddenConfig", false, true, this.expert, false
   );
   public BiMap<UUID, class_306> emoteKeyMap = new BiMap<>();
   public UUID[][] fastMenuEmotes = new UUID[15][8];

   public ClientConfig() {
      this.loadEmotesServerSide.set(false);
   }

   @Override
   public void iterate(Consumer<SerializableConfig.ConfigEntry<?>> consumer) {
      super.iterate(consumer);
      this.legacy.forEach(consumer);
   }
}
