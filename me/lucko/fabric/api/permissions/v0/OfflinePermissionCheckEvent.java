package me.lucko.fabric.api.permissions.v0;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.util.TriState;
import org.jetbrains.annotations.NotNull;

public interface OfflinePermissionCheckEvent {
   Event<OfflinePermissionCheckEvent> EVENT = EventFactory.createArrayBacked(
      OfflinePermissionCheckEvent.class,
      callbacks -> (uuid, permission) -> {
         CompletableFuture<TriState> res = CompletableFuture.completedFuture(TriState.DEFAULT);

         for (OfflinePermissionCheckEvent callback : callbacks) {
            res = res.thenCompose(
               triState -> triState != TriState.DEFAULT ? CompletableFuture.completedFuture(triState) : callback.onPermissionCheck(uuid, permission)
            );
         }

         return res;
      }
   );

   @NotNull
   CompletableFuture<TriState> onPermissionCheck(@NotNull UUID var1, @NotNull String var2);
}
