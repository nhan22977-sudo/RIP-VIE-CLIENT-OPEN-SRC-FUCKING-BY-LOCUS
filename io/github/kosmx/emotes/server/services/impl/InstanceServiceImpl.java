package io.github.kosmx.emotes.server.services.impl;

import io.github.kosmx.emotes.server.services.InstanceService;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InstanceServiceImpl implements InstanceService {
   @Override
   public Path getGameDirectory() {
      return Paths.get("");
   }

   @Override
   public int getPriority() {
      return -1000;
   }

   @Override
   public boolean isActive() {
      return true;
   }
}
