package io.github.kosmx.emotes.mc.services.impl;

import io.github.kosmx.emotes.mc.services.IPermissionService;
import java.util.Optional;
import net.minecraft.class_2168;
import org.jetbrains.annotations.NotNull;

public class VanillaPermissionService implements IPermissionService {
   @Override
   public Optional<Boolean> getPermissionValue(@NotNull class_2168 source, @NotNull String permission) {
      return Optional.empty();
   }

   @Override
   public boolean isActive() {
      return true;
   }

   @Override
   public int getPriority() {
      return -1000;
   }
}
