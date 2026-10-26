package me.drake.vieclient.modules.client.music;

import java.util.Optional;

public interface MusicProvider extends AutoCloseable {
   String id();

   Optional poll();

   @Override
   default void close() {
   }
}
