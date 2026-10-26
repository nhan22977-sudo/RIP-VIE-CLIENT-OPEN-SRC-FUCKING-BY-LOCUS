package io.github.kosmx.emotes.server.network;

import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.event.EventResult;
import io.github.kosmx.emotes.api.events.server.ServerEmoteAPI;
import io.github.kosmx.emotes.api.events.server.ServerEmoteEvents;
import io.github.kosmx.emotes.api.proxy.AbstractNetworkInstance;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.network.PacketBound;
import io.github.kosmx.emotes.common.network.objects.NetData;
import io.github.kosmx.emotes.server.config.Serializer;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.Pair;
import java.io.IOException;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractServerEmotePlay<P extends IServerNetworkInstance> extends ServerEmoteAPI {
   protected boolean doValidate() {
      return Serializer.getConfig().validateEmote.get();
   }

   protected abstract UUID getUUIDFromPlayer(P var1);

   protected abstract P getPlayerFromUUID(UUID var1);

   public void receiveMessage(ByteBuf bytes, P instance) throws IOException {
      this.receiveMessage(new EmotePacket(bytes, PacketBound.SERVER), instance);
   }

   public void receiveMessage(EmotePacket packet, P instance) throws IOException {
      CommonData.LOGGER.trace("[emotes server] Received data from: {} data: {}", instance, packet);
      switch (packet.data.purpose) {
         case STOP:
            this.stopEmote(instance, packet.data);
            break;
         case CONFIG:
            instance.setVersions(packet.data.versions);
            instance.presenceResponse();
            break;
         case STREAM:
            this.handleStreamEmote(packet.data, instance);
            break;
         case UNKNOWN:
         default:
            throw new IOException("Unknown packet task");
      }
   }

   protected void handleStreamEmote(NetData data, P instance) throws IOException {
      if (!data.valid && this.doValidate()) {
         EventResult result = ServerEmoteEvents.EMOTE_VERIFICATION.invoker().verify(data.emoteData, this.getUUIDFromPlayer(instance));
         if (result != EventResult.FAIL) {
            EmotePacket.Builder stopMSG = new EmotePacket.Builder()
               .configureToSendStop(data.emoteData.uuid())
               .configureTarget(this.getUUIDFromPlayer(instance))
               .setSizeLimit(1048576, true);
            if (instance != null) {
               instance.sendMessage(stopMSG, null);
            }

            return;
         }
      }

      if (data.player != null && instance.trackPlayState()) {
         CommonData.LOGGER.warn("Player {} does not respect server-side emote tracking", instance);
      }

      if (instance.getEmoteTracker().isForced()) {
         CommonData.LOGGER.warn("Player {} is disobeying force play flag and tried to override it", instance);
      } else {
         this.streamEmote(data, instance, false, true);
      }
   }

   protected void streamEmote(NetData data, P player, boolean isForced, boolean isFromPlayer) {
      player.getEmoteTracker().setPlayedEmote(data.emoteData, isForced);
      ServerEmoteEvents.EMOTE_PLAY.invoker().onEmotePlay(data.emoteData, data.tick, this.getUUIDFromPlayer(player));
      data.isForced = isForced;
      data.player = this.getUUIDFromPlayer(player);
      data.strictSizeLimit = false;
      this.sendForEveryoneElse(data, player);
      if (!isFromPlayer) {
         this.sendForPlayer(data, player, player);
      }
   }

   protected void stopEmote(P player, @Nullable NetData originalMessage) {
      Pair<Animation, Float> emote = player.getEmoteTracker().getPlayedEmote();
      player.getEmoteTracker().setPlayedEmote(null, false);
      if (emote != null) {
         ServerEmoteEvents.EMOTE_STOP_BY_USER.invoker().onStopEmote(((Animation)emote.left()).uuid(), this.getUUIDFromPlayer(player));
         NetData data = new EmotePacket.Builder().configureToSendStop(((Animation)emote.left()).uuid(), this.getUUIDFromPlayer(player)).build().data;
         this.sendForEveryoneElse(data, player);
         if (originalMessage == null) {
            data.isForced = true;
            this.sendForPlayer(data, player, player);
         }
      }
   }

   public void playerStartTracking(P tracked, P tracker) {
      if (tracked != null && tracker != null) {
         Pair<Animation, Float> playedEmote = tracked.getEmoteTracker().getPlayedEmote();
         if (playedEmote != null) {
            this.sendForPlayer(
               new EmotePacket.Builder()
                  .configureToStreamEmote((Animation)playedEmote.left())
                  .configureEmoteTick((Float)playedEmote.right())
                  .configureTarget(this.getUUIDFromPlayer(tracked))
                  .build()
                  .data,
               tracked,
               tracker
            );
         }
      }
   }

   @Override
   protected void setPlayerPlayingEmoteImpl(UUID player, @Nullable Animation emoteData, float tick, boolean isForced) {
      if (emoteData != null) {
         EmotePacket packet = new EmotePacket.Builder().configureToStreamEmote(emoteData).configureEmoteTick(tick).build();
         this.streamEmote(packet.data, this.getPlayerFromUUID(player), isForced, false);
      } else {
         this.stopEmote(this.getPlayerFromUUID(player), null);
      }
   }

   @Override
   protected Pair<Animation, Float> getPlayedEmoteImpl(UUID player) {
      return this.getPlayerFromUUID(player).getEmoteTracker().getPlayedEmote();
   }

   @Override
   protected boolean isForcedEmoteImpl(UUID player) {
      return this.getPlayerFromUUID(player).getEmoteTracker().isForced();
   }

   @Deprecated
   public void presenceResponse(AbstractNetworkInstance instance, boolean trackPlayState) {
      try {
         instance.sendMessage(this.getS2CConfigPacket(trackPlayState), null);
      } catch (IOException e) {
         CommonData.LOGGER.error("Failed to send config to client!", e);
      }

      if (instance.getRemoteVersions().getOrDefault(17, (byte)0) >= 0) {
         UniversalEmoteSerializer.preparePackets(instance.getRemoteVersions()).forEach(buffer -> instance.sendMessage(buffer, null));
      }
   }

   public EmotePacket.Builder getS2CConfigPacket(boolean trackPlayState) {
      NetData configData = new EmotePacket.Builder().configureToConfigExchange().build().data;
      if (trackPlayState) {
         configData.versions.put((byte)-128, (byte)1);
      }

      return new EmotePacket.Builder(configData);
   }

   protected abstract void sendForEveryoneElse(NetData var1, P var2);

   protected void sendForPlayer(NetData data, P player, P target) {
      if (target.isActive()) {
         try {
            EmotePacket.Builder packetBuilder = new EmotePacket.Builder(data.copy());
            packetBuilder.setVersion(target.getRemoteVersions());
            target.sendMessage(packetBuilder, null);
         } catch (Exception e) {
            CommonData.LOGGER.warn("Failed to send packet!", e);
         }
      }
   }

   public static AbstractServerEmotePlay getInstance() {
      return (AbstractServerEmotePlay)ServerEmoteAPI.INSTANCE;
   }
}
