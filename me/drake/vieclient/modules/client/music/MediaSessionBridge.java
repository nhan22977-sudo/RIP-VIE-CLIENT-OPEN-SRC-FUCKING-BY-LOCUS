package me.drake.vieclient.modules.client.music;

import java.util.Optional;

public interface MediaSessionBridge extends AutoCloseable {
   Optional latest();

   @Override
   void close();
}
