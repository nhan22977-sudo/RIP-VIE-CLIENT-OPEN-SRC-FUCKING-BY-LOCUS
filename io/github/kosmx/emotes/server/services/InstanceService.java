package io.github.kosmx.emotes.server.services;

import io.github.kosmx.emotes.api.services.IEmotecraftService;
import io.github.kosmx.emotes.common.tools.ServiceLoaderUtil;
import io.github.kosmx.emotes.server.config.Serializer;
import io.github.kosmx.emotes.server.services.impl.InstanceServiceImpl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface InstanceService extends IEmotecraftService {
   InstanceService INSTANCE = ServiceLoaderUtil.loadService(InstanceService.class, InstanceServiceImpl::new);

   Path getGameDirectory();

   default Path getExternalEmoteDir() {
      return this.getGameDirectory().resolve(Serializer.getConfig().emotesDir.get());
   }

   default Path getConfigPath() {
      String directoryName = "config";

      try {
         directoryName = System.getProperty("emotecraftConfigDir", "config");
         if (directoryName.equals("pluginDefault")) {
            directoryName = "plugins/emotecraft";
         }
      } catch (Throwable var4) {
      }

      if (!Files.exists(this.getGameDirectory().resolve(directoryName))) {
         try {
            Files.createDirectories(this.getGameDirectory().resolve(directoryName));
         } catch (IOException var3) {
         }
      }

      return this.getGameDirectory().resolve(directoryName).resolve("emotecraft.json");
   }
}
