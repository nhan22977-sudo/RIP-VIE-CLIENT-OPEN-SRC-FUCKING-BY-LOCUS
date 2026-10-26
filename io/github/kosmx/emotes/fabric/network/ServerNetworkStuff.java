package io.github.kosmx.emotes.fabric.network;

import io.github.kosmx.emotes.arch.mixin.ServerCommonPacketListenerAccessor;
import io.github.kosmx.emotes.arch.network.CommonServerNetworkHandler;
import io.github.kosmx.emotes.arch.network.ConfigTask;
import io.github.kosmx.emotes.arch.network.EmotePacketPayload;
import io.github.kosmx.emotes.arch.network.EmotesMixinConnection;
import io.github.kosmx.emotes.arch.network.NetworkPlatformTools;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.PacketTask;
import io.github.kosmx.emotes.common.network.objects.NetData;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import java.io.IOException;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents.Configure;
import net.minecraft.class_2561;

public final class ServerNetworkStuff {
   public static void init() {
      PayloadTypeRegistator.init();
      ServerConfigurationConnectionEvents.CONFIGURE.register((Configure)(handler, server) -> {
         if (ServerConfigurationNetworking.canSend(handler, NetworkPlatformTools.EMOTE_CHANNEL_ID)) {
            handler.addTask(new ConfigTask());
         } else {
            CommonData.LOGGER.debug("Client doesn't support emotes, ignoring");
         }
      });
      ServerConfigurationNetworking.registerGlobalReceiver(
         NetworkPlatformTools.EMOTE_CHANNEL_ID,
         (payload, context) -> {
            try {
               NetData message = payload.packet().data;
               if (message.purpose != PacketTask.CONFIG) {
                  throw new IOException("Wrong packet type for config task");
               }

               ((EmotesMixinConnection)((ServerCommonPacketListenerAccessor)context.networkHandler()).getConnection()).emotecraft$setVersions(message.versions);
               UniversalEmoteSerializer.preparePackets(message.versions).map(EmotePacketPayload::playPacket).forEach(context.responseSender()::sendPacket);
               context.networkHandler().completeTask(ConfigTask.TYPE);
            } catch (Exception e) {
               CommonData.LOGGER.error("Invalid Emotecraft packet!", e);
               context.networkHandler()
                  .method_52396(class_2561.method_43470("emotecraft: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName())));
            }
         }
      );
      ServerPlayNetworking.registerGlobalReceiver(
         NetworkPlatformTools.EMOTE_CHANNEL_ID, (buf, context) -> CommonServerNetworkHandler.getInstance().receiveMessage(buf.packet(), context.player())
      );
      ServerPlayNetworking.registerGlobalReceiver(
         NetworkPlatformTools.STREAM_CHANNEL_ID,
         (buf, context) -> CommonServerNetworkHandler.getInstance().receiveStreamMessage(buf.packet(), context.player())
      );
   }
}
