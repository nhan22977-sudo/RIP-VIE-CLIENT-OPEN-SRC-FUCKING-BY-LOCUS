package io.github.kosmx.emotes.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SerializableConfig {
   public final List<SerializableConfig.ConfigEntry<?>> basics = new ArrayList<>();
   public final List<SerializableConfig.ConfigEntry<?>> expert = new ArrayList<>();
   public static final int staticConfigVersion = 4;
   public int configVersion;
   public final SerializableConfig.ConfigEntry<Boolean> validateEmote = new SerializableConfig.ConfigEntry<>("validate", false, true, this.expert);
   public final SerializableConfig.ConfigEntry<Float> validThreshold = new SerializableConfig.FloatConfigEntry(
      "validationThreshold", "validThreshold", 8.0F, true, this.expert, "options.generic_value", 0.2F, 16.0F, 0.0F
   );
   public final SerializableConfig.ConfigEntry<Boolean> loadBuiltinEmotes = new SerializableConfig.ConfigEntry<>(
      "loadbuiltin", "loadBuiltin", true, true, this.basics
   );
   public final SerializableConfig.ConfigEntry<Boolean> loadEmotesServerSide = new SerializableConfig.ConfigEntry<>(
      "emotesFolderOnLogicalServer", false, true, this.expert, true
   );
   public final SerializableConfig.ConfigEntry<Boolean> enableQuark = new SerializableConfig.ConfigEntry<>("quark", "enablequark", false, true, this.basics);
   public final SerializableConfig.ConfigEntry<String> emotesDir = new SerializableConfig.ConfigEntry<>("emotesDirectory", "emotes", false, this.expert, true);

   public void iterate(Consumer<SerializableConfig.ConfigEntry<?>> consumer) {
      this.basics.forEach(consumer);
      this.expert.forEach(consumer);
   }

   public SerializableConfig() {
      this.loadEmotesServerSide.set(true);
   }

   public static class ConfigEntry<T> {
      final String name;
      final String oldConfig;
      T value;
      final T defaultValue;
      public final boolean hasTooltip;
      final boolean isHidden;

      public ConfigEntry(String name, String oldconfig, T defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection, boolean hidden) {
         this.name = name;
         this.oldConfig = oldconfig;
         this.hasTooltip = hasTooltip;
         this.defaultValue = defVal;
         this.value = defVal;
         collection.add(this);
         this.isHidden = hidden;
      }

      public ConfigEntry(String name, String oldconfig, T defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection) {
         this(name, oldconfig, defVal, hasTooltip, collection, false);
      }

      public ConfigEntry(String name, T defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection) {
         this(name, null, defVal, hasTooltip, collection);
      }

      public ConfigEntry(String name, T defVal, List<SerializableConfig.ConfigEntry<?>> collection, boolean hidden) {
         this(name, null, defVal, false, collection, hidden);
      }

      public ConfigEntry(String name, T defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection, boolean hidden) {
         this(name, null, defVal, hasTooltip, collection, hidden);
      }

      public T get() {
         return this.value;
      }

      public void set(T newValue) {
         this.value = newValue;
      }

      public String getName() {
         return this.name;
      }

      public String getOldConfigName() {
         return this.oldConfig;
      }

      public void resetToDefault() {
         this.value = this.defaultValue;
      }

      public boolean showEntry() {
         return !this.isHidden;
      }
   }

   public static class EnumConfigEntry<E extends Enum<E>> extends SerializableConfig.ConfigEntry<E> {
      private final Class<E> enumClass;

      public EnumConfigEntry(String name, String oldconfig, E defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection, boolean hidden) {
         super(name, oldconfig, defVal, hasTooltip, collection, hidden);
         this.enumClass = defVal.getDeclaringClass();
      }

      public EnumConfigEntry(String name, String oldconfig, E defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection) {
         this(name, oldconfig, defVal, hasTooltip, collection, false);
      }

      public EnumConfigEntry(String name, E defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection) {
         this(name, null, defVal, hasTooltip, collection, false);
      }

      public EnumConfigEntry(String name, E defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection, boolean hidden) {
         this(name, null, defVal, hasTooltip, collection, hidden);
      }

      public EnumConfigEntry(String name, E defVal, List<SerializableConfig.ConfigEntry<?>> collection, boolean hidden) {
         this(name, null, defVal, false, collection, hidden);
      }

      public Class<E> getEnumClass() {
         return this.enumClass;
      }

      public void set(E newValue) {
         if (newValue == null) {
            throw new NullPointerException("Enum value cannot be null for " + this.name);
         }

         if (newValue.getDeclaringClass() != this.enumClass) {
            throw new IllegalArgumentException("Wrong enum type for " + this.name + ": " + newValue.getDeclaringClass());
         }

         super.set(newValue);
      }
   }

   public static class FloatConfigEntry extends SerializableConfig.ConfigEntry<Float> {
      private final String formatKey;
      public final float min;
      public final float max;
      public final float step;

      public FloatConfigEntry(
         String name,
         String oldconfig,
         Float defVal,
         boolean hasTooltip,
         List<SerializableConfig.ConfigEntry<?>> collection,
         String formatKey,
         float min,
         float max,
         float step
      ) {
         super(name, oldconfig, defVal, hasTooltip, collection);
         this.formatKey = formatKey;
         this.min = min;
         this.max = max;
         this.step = step;
      }

      public FloatConfigEntry(
         String name, Float defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection, String formatKey, float min, float step, float max
      ) {
         this(name, null, defVal, hasTooltip, collection, formatKey, min, max, step);
      }

      public String getFormatKey() {
         return this.formatKey;
      }

      public double getConfigVal() {
         return Math.sqrt(this.get().floatValue());
      }

      public void setConfigVal(double newVal) {
         this.set((float)Math.pow(newVal, 2.0));
      }

      public double getTextVal() {
         return this.get().floatValue();
      }
   }

   public static class ListConfigEntry<T> extends SerializableConfig.ConfigEntry<List<T>> {
      public ListConfigEntry(
         String name, String oldconfig, List<T> defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection, boolean hidden
      ) {
         super(name, oldconfig, defVal, hasTooltip, collection, hidden);
      }

      public ListConfigEntry(String name, String oldconfig, List<T> defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection) {
         super(name, oldconfig, defVal, hasTooltip, collection);
      }

      public ListConfigEntry(String name, List<T> defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection) {
         super(name, defVal, hasTooltip, collection);
      }

      public ListConfigEntry(String name, List<T> defVal, List<SerializableConfig.ConfigEntry<?>> collection, boolean hidden) {
         super(name, defVal, collection, hidden);
      }

      public ListConfigEntry(String name, List<T> defVal, boolean hasTooltip, List<SerializableConfig.ConfigEntry<?>> collection, boolean hidden) {
         super(name, defVal, hasTooltip, collection, hidden);
      }
   }
}
