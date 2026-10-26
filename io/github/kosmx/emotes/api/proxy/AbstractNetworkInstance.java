package io.github.kosmx.emotes.api.proxy;

import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.EmotePacket;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractNetworkInstance implements INetworkInstance {
   private final HashMap<Byte, Byte> versions = new HashMap<>(EmotePacket.defaultVersions);

   public void sendMessage(EmotePacket packet, @Nullable UUID target) {
      throw new UnsupportedOperationException("You should have implemented send emote feature");
   }

   @Override
   public void sendMessage(EmotePacket.Builder builder, @Nullable UUID target) throws IOException {
      this.sendMessage(builder.build(), target);
   }

   public void receiveMessage(EmotePacket packet) {
      this.receiveMessage(packet, null);
   }

   protected void disconnect() {
      EmotesProxyManager.disconnectInstance(this);
   }

   @Override
   public void setVersions(Map<Byte, Byte> map) {
      this.versions.clear();
      this.versions.putAll(map);
   }

   public HashMap<Byte, Byte> getRemoteVersions() {
      return this.versions;
   }

   @Override
   public boolean isServerTrackingPlayState() {
      return this.versions.containsKey((byte)-128) && this.versions.get(-128) != 0;
   }

   @Override
   public void sendC2SConfig(Consumer<EmotePacket.Builder> consumer) {
      EmotePacket.Builder packetBuilder = new EmotePacket.Builder();
      packetBuilder.configureToConfigExchange();

      try {
         consumer.accept(packetBuilder);
      } catch (Exception e) {
         CommonData.LOGGER.warn("Error while writing packet!", e);
      }
   }
}
