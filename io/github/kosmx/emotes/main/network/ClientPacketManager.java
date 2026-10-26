package io.github.kosmx.emotes.main.network;

import io.github.kosmx.emotes.api.events.client.ClientNetworkEvents;
import io.github.kosmx.emotes.api.proxy.EmotesProxyManager;
import io.github.kosmx.emotes.api.proxy.INetworkInstance;
import io.github.kosmx.emotes.arch.network.client.ClientNetwork;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.main.EmoteHolder;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public final class ClientPacketManager extends EmotesProxyManager {
   public static void init() {
      setManager(new ClientPacketManager());
   }

   private ClientPacketManager() {
   }

   public static void send(EmotePacket.Builder packetBuilder, UUID target) {
      ClientNetworkEvents.PACKET_SEND.invoker().onPacketSend(packetBuilder);
      boolean isMainActive = ClientNetwork.INSTANCE.isActive();
      if (isMainActive) {
         sendMessageVia(ClientNetwork.INSTANCE, packetBuilder, target);
      }

      if (!isMainActive || isInstanceOutdatedForStreaming(ClientNetwork.INSTANCE)) {
         for (INetworkInstance network : networkInstances) {
            if (network.isActive()) {
               sendMessageVia(network, packetBuilder.copy(), target);
            }
         }
      }
   }

   private static void sendMessageVia(INetworkInstance network, EmotePacket.Builder packetBuilder, UUID target) {
      if (target == null || !network.isServerTrackingPlayState()) {
         if (!network.sendPlayerID()) {
            packetBuilder.removePlayerID();
         }

         try {
            packetBuilder.setSizeLimit(network.maxDataSize(), false);
            packetBuilder.setVersion(network.getRemoteVersions());
            network.sendMessage(packetBuilder, target);
         } catch (IOException ex) {
            CommonData.LOGGER.error("Error while sending packet via {}!", network, ex);
         }
      }
   }

   @Override
   protected void dispatchReceive(EmotePacket packet, UUID player, INetworkInstance networkInstance) {
      try {
         if (!networkInstance.trustReceivedPlayer()) {
            packet.data.player = null;
         }

         if (player != null) {
            packet.data.player = player;
         }

         if (packet.data.player == null && packet.data.purpose.playerBound) {
            throw new IOException("Didn't received any player information");
         }

         try {
            ClientEmotePlay.executeMessage(packet.data, networkInstance);
         } catch (Exception e) {
            CommonData.LOGGER.error("Critical error has occurred while receiving emote!", e);
         }
      } catch (IOException e) {
         CommonData.LOGGER.warn("Error while receiving packet!", e);
      }
   }

   @Deprecated(forRemoval = true)
   public static boolean isRemoteAvailable() {
      return ClientNetwork.INSTANCE.isActive();
   }

   @Deprecated(forRemoval = true)
   public static boolean isRemoteTracking() {
      return isRemoteAvailable() && ClientNetwork.INSTANCE.isServerTrackingPlayState();
   }

   public static boolean isAvailableProxy() {
      for (INetworkInstance instance : networkInstances) {
         if (instance.isActive()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void onDisconnectFromServer(INetworkInstance networkInstance) {
      if (networkInstance == null) {
         throw new NullPointerException("network instance must be non-null");
      }

      EmoteHolder.clearEmotes(networkInstance);
   }

   public static boolean isInstanceOutdatedForStreaming(INetworkInstance instance) {
      return isInstanceOutdated(instance, (byte)-103) || isInstanceOutdated(instance, (byte)3);
   }

   public static boolean isInstanceOutdated(INetworkInstance instance, byte packet) {
      Map<Byte, Byte> versions = instance.getRemoteVersions();
      return !versions.containsKey(packet) ? true : versions.get(packet) < EmotePacket.defaultVersions.get(packet);
   }
}
