package io.github.kosmx.emotes.fabric.executor;

import io.github.kosmx.emotes.server.services.InstanceService;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public class FabricEmotesMain implements InstanceService {
   @Override
   public Path getGameDirectory() {
      return FabricLoader.getInstance().getGameDir();
   }

   @Override
   public Path getConfigPath() {
      return FabricLoader.getInstance().getConfigDir().resolve("emotecraft.json");
   }

   @Override
   public boolean isActive() {
      return true;
   }
}
