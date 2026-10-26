package io.github.kosmx.emotes.mc.services;

import io.github.kosmx.emotes.api.services.IEmotecraftService;
import io.github.kosmx.emotes.common.tools.ServiceLoaderUtil;
import io.github.kosmx.emotes.mc.services.impl.VanillaPermissionService;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.class_12094;
import net.minecraft.class_2168;
import net.minecraft.class_12087.class_12089;
import org.jetbrains.annotations.NotNull;

public interface IPermissionService extends IEmotecraftService {
   IPermissionService INSTANCE = ServiceLoaderUtil.loadService(IPermissionService.class, VanillaPermissionService::new);

   default Predicate<class_2168> require(@NotNull String permission, class_12094 defaultValue) {
      Objects.requireNonNull(permission, "permission");
      return player -> this.check(player, permission, defaultValue);
   }

   default boolean check(@NotNull class_2168 source, @NotNull String permission, class_12094 defaultValue) {
      return this.getPermissionValue(source, permission).orElseGet(() -> source.method_75037().hasPermission(new class_12089(defaultValue)));
   }

   Optional<Boolean> getPermissionValue(@NotNull class_2168 var1, @NotNull String var2);
}
