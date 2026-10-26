package io.github.kosmx.emotes.fabric.network;

import io.github.kosmx.emotes.arch.network.EmotePacketPayload;
import io.github.kosmx.emotes.arch.network.NetworkPlatformTools;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.class_2540;
import net.minecraft.class_9139;
import net.minecraft.class_8710.class_9154;

public class PayloadTypeRegistator {
   public static void init() {
      register(NetworkPlatformTools.EMOTE_CHANNEL_ID, EmotePacketPayload.EMOTE_CHANNEL_READER_S2C, EmotePacketPayload.EMOTE_CHANNEL_READER_C2S);
      register(NetworkPlatformTools.STREAM_CHANNEL_ID, EmotePacketPayload.STREAM_CHANNEL_READER_S2C, EmotePacketPayload.STREAM_CHANNEL_READER_C2S);
   }

   private static void register(
      class_9154<EmotePacketPayload> type, class_9139<class_2540, EmotePacketPayload> s2c, class_9139<class_2540, EmotePacketPayload> c2s
   ) {
      PayloadTypeRegistry.configurationS2C().register(type, s2c);
      PayloadTypeRegistry.configurationC2S().register(type, c2s);
      PayloadTypeRegistry.playS2C().register(type, s2c);
      PayloadTypeRegistry.playC2S().register(type, c2s);
   }
}
