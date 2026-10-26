package me.lucko.fabric.api.permissions.v0;

import java.util.Optional;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.class_2172;
import org.jetbrains.annotations.NotNull;

public interface OptionRequestEvent {
   Event<OptionRequestEvent> EVENT = EventFactory.createArrayBacked(OptionRequestEvent.class, callbacks -> (source, key) -> {
      for (OptionRequestEvent callback : callbacks) {
         Optional<String> value = callback.onOptionRequest(source, key);
         if (value.isPresent()) {
            return value;
         }
      }

      return Optional.empty();
   });

   @NotNull
   Optional<String> onOptionRequest(@NotNull class_2172 var1, @NotNull String var2);
}
