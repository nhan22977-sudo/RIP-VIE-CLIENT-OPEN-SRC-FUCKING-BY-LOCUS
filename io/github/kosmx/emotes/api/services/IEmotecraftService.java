package io.github.kosmx.emotes.api.services;

public interface IEmotecraftService {
   boolean isActive();

   default int getPriority() {
      return 0;
   }

   default String getName() {
      return this.getClass().getName();
   }
}
