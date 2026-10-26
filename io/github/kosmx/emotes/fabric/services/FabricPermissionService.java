package io.github.kosmx.emotes.fabric.services;

import io.github.kosmx.emotes.mc.services.IPermissionService;
import java.util.Optional;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_2168;
import org.jetbrains.annotations.NotNull;

public class FabricPermissionService implements IPermissionService {
   @Override
   public Optional<Boolean> getPermissionValue(@NotNull class_2168 source, @NotNull String permission) {
      return Permissions.getPermissionValue(source, permission).map(b -> b);
   }

   @Override
   public boolean isActive() {
      return FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");
   }
}
