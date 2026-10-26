package com.zigythebird.playeranimcore.molang;

import com.zigythebird.playeranimcore.easing.EasingType;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.runtime.binding.Binding;
import team.unnamed.mocha.runtime.value.ObjectProperty;
import team.unnamed.mocha.runtime.value.ObjectValue;
import team.unnamed.mocha.runtime.value.Value;
import team.unnamed.mocha.util.CaseInsensitiveStringHashMap;

@Binding("math")
public class MochaMathExtensions implements ObjectValue {
   private final Map<String, ObjectProperty> entries = new CaseInsensitiveStringHashMap();
   @Nullable
   private final ObjectValue mochaMath;

   public MochaMathExtensions(@Nullable ObjectProperty property) {
      this((ObjectValue)property.value());
   }

   public MochaMathExtensions(@Nullable ObjectValue mochaMath) {
      this.mochaMath = mochaMath;

      for (EasingType type : EasingType.values()) {
         String name = type.name().toLowerCase(Locale.ROOT);
         if (name.startsWith("ease_")) {
            this.setFunction(name, type);
         }
      }
   }

   public boolean set(@NotNull String name, @Nullable Value value) {
      return this.entries.put(name, ObjectProperty.property(value, false)) == null;
   }

   @Nullable
   public ObjectProperty getProperty(@NotNull String name) {
      ObjectProperty extension = this.entries.get(name);
      return extension == null && this.mochaMath != null ? this.mochaMath.getProperty(name) : extension;
   }
}
