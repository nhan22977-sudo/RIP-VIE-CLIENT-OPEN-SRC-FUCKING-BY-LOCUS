package io.github.kosmx.emotes.arch.network.client;

import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.api.proxy.AbstractNetworkInstance;
import io.github.kosmx.emotes.arch.network.EmotePacketPayload;
import io.github.kosmx.emotes.arch.network.NetworkPlatformTools;
import io.github.kosmx.emotes.arch.screen.ingame.FastMenuScreen;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.network.PacketTask;
import io.github.kosmx.emotes.common.tools.ServiceLoaderUtil;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.main.network.ClientPacketManager;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_8710;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ClientNetwork extends AbstractNetworkInstance {
   public static ClientNetwork INSTANCE = ServiceLoaderUtil.loadServices(ClientNetwork.class).findAny().orElseThrow();
   private boolean isConfiguredNormally;

   @Override
   public boolean isActive() {
      return this.isServerChannelOpen(NetworkPlatformTools.EMOTE_CHANNEL_ID.comp_2242());
   }

   @Override
   public void sendMessage(EmotePacket.Builder builder, @Nullable UUID target) throws IOException {
      if (target != null) {
         builder.configureTarget(target);
      }

      EmotePacket writer = builder.build();
      this.sendMessage(writer, null);
      if (writer.data.emoteData != null && writer.data.emoteData.data().has("song") && writer.data.skippedPackets.contains((byte)3)) {
         PlatformTools.addToast(class_2561.method_43471("emotecraft.song_too_big_to_send"));
      }
   }

   @Override
   public void sendMessage(EmotePacket byteBuffer, @Nullable UUID target) {
      this.sendPlayPacket(EmotePacketPayload.playPacket(byteBuffer));
   }

   public abstract boolean isServerChannelOpen(class_2960 var1);

   public abstract void sendPlayPacket(class_8710 var1);

   public void receiveStreamMessage(@NotNull EmotePacket packet, @Nullable Consumer<class_8710> configPacketConsumer) throws IOException {
      CommonData.LOGGER.error("Streaming message received!");
   }

   public void receiveConfigMessage(@NotNull EmotePacket packet, @NotNull Consumer<class_8710> consumer) throws IOException {
      if (packet.data.purpose == PacketTask.CONFIG) {
         this.setVersions(packet.data.versions);
         this.sendC2SConfig(p -> consumer.accept(EmotePacketPayload.playPacket(p.build())));
         this.onConfigurationDone();
      } else if (packet.data.purpose == PacketTask.FILE) {
         EmoteHolder.addEmoteToList(packet.data.emoteData, this);
      } else {
         CommonData.LOGGER.warn("Invalid emotes packet type in configuration phase: {}", packet.data.purpose);
      }
   }

   @Deprecated
   public void configureOnPlay(@NotNull Consumer<class_8710> consumer) {
      if (!this.isConfiguredNormally && this.isActive()) {
         CommonData.LOGGER.warn("The server failed to configure the client, attempting to configure...");
         this.sendC2SConfig(p -> consumer.accept(EmotePacketPayload.playPacket(p.build())));
      }
   }

   @Override
   public void disconnect() {
      super.disconnect();
      this.isConfiguredNormally = false;
   }

   @Override
   public int maxDataSize() {
      return super.maxDataSize() - 16;
   }

   public void onConfigurationDone() {
      this.isConfiguredNormally = true;
      if (ClientPacketManager.isInstanceOutdatedForStreaming(this)) {
         PlatformTools.addToast(FastMenuScreen.WARN_DIFFERENT_SERVER);
      }
   }
}
