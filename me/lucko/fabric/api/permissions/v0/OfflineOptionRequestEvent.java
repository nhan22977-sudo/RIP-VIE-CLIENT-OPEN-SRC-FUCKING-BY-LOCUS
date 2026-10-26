package me.lucko.fabric.api.permissions.v0;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import org.jetbrains.annotations.NotNull;

public interface OfflineOptionRequestEvent {
   Event<OfflineOptionRequestEvent> EVENT = EventFactory.createArrayBacked(OfflineOptionRequestEvent.class, callbacks -> (uuid, key) -> {
      CompletableFuture<Optional<String>> res = CompletableFuture.completedFuture(Optional.empty());

      for (OfflineOptionRequestEvent callback : callbacks) {
         res = res.thenCompose(value -> value.isPresent() ? CompletableFuture.completedFuture((Optional<String>)value) : callback.onOptionRequest(uuid, key));
      }

      return res;
   });

   @NotNull
   CompletableFuture<Optional<String>> onOptionRequest(@NotNull UUID var1, @NotNull String var2);
}
