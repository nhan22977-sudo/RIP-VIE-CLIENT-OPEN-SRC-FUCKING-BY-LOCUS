package io.github.kosmx.emotes.api.events.client;

import com.zigythebird.playeranimcore.event.Event;
import io.github.kosmx.emotes.common.network.EmotePacket;

public class ClientNetworkEvents {
   public static final Event<ClientNetworkEvents.PacketSendEvent> PACKET_SEND = new Event<>(listeners -> packet -> {
      for (ClientNetworkEvents.PacketSendEvent listener : listeners) {
         listener.onPacketSend(packet);
      }
   });

   @FunctionalInterface
   public interface PacketSendEvent {
      void onPacketSend(EmotePacket.Builder var1);
   }
}
