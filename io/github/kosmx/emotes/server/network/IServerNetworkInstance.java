package io.github.kosmx.emotes.server.network;

import io.github.kosmx.emotes.api.proxy.INetworkInstance;

public interface IServerNetworkInstance extends INetworkInstance {
   default void closeConnection() {
   }

   default boolean trackPlayState() {
      return true;
   }

   EmotePlayTracker getEmoteTracker();
}
