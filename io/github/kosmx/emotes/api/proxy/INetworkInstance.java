package io.github.kosmx.emotes.api.proxy;

import io.github.kosmx.emotes.common.network.EmotePacket;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public interface INetworkInstance {
   Map<Byte, Byte> getRemoteVersions();

   void setVersions(Map<Byte, Byte> var1);

   @Deprecated
   default void presenceResponse() {
   }

   default boolean sendPlayerID() {
      return false;
   }

   void sendMessage(EmotePacket.Builder var1, @Nullable UUID var2) throws IOException;

   default void receiveMessage(EmotePacket packet, UUID player) {
      EmotesProxyManager.receiveMessage(packet, player, this);
   }

   default void sendC2SConfig(Consumer<EmotePacket.Builder> consumer) {
   }

   default boolean trustReceivedPlayer() {
      return true;
   }

   boolean isActive();

   boolean isServerTrackingPlayState();

   default int maxDataSize() {
      return 1048576;
   }
}
