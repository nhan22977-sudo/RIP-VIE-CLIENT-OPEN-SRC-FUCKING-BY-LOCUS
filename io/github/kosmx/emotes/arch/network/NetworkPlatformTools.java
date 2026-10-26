package io.github.kosmx.emotes.arch.network;

import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.tools.ServiceLoaderUtil;
import io.github.kosmx.emotes.mc.McUtils;
import java.util.Collection;
import net.minecraft.class_1297;
import net.minecraft.class_2596;
import net.minecraft.class_2658;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_8610;
import net.minecraft.class_8710.class_9154;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public interface NetworkPlatformTools {
   NetworkPlatformTools INSTANCE = ServiceLoaderUtil.loadServices(NetworkPlatformTools.class).findAny().orElseThrow();
   class_9154<EmotePacketPayload> EMOTE_CHANNEL_ID = new class_9154(McUtils.newIdentifier("emote"));
   class_9154<EmotePacketPayload> STREAM_CHANNEL_ID = new class_9154(McUtils.newIdentifier("stream"));

   boolean canSendPlay(class_3222 var1, class_2960 var2);

   boolean canSendConfig(class_8610 var1, class_2960 var2);

   Collection<class_3222> getTrackedBy(class_1297 var1);

   @NotNull
   static class_2596<?> createClientboundPacket(@NotNull class_9154<?> id, @NotNull EmotePacket packet) {
      return new class_2658(new EmotePacketPayload(id, packet));
   }

   MinecraftServer getServer();

   @NotNull
   static class_2596<?> playPacket(@NotNull EmotePacket packet) {
      return createClientboundPacket(EMOTE_CHANNEL_ID, packet);
   }

   @NotNull
   static class_2596<?> streamPacket(@NotNull EmotePacket packet) {
      return createClientboundPacket(STREAM_CHANNEL_ID, packet);
   }

   static void tryReceive(@NotNull NetworkPlatformTools.PacketReceiver receiver) {
      try {
         receiver.receive();
      } catch (Exception e) {
         CommonData.LOGGER.error("Invalid Emotecraft packet!", e);
      }
   }

   @FunctionalInterface
   interface PacketReceiver {
      void receive() throws Exception;
   }
}
