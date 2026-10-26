package io.github.kosmx.emotes.main.network;

import com.zigythebird.playeranim.util.ClientUtil;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.event.EventResult;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.api.events.client.ClientEmoteAPI;
import io.github.kosmx.emotes.api.events.client.ClientEmoteEvents;
import io.github.kosmx.emotes.api.proxy.INetworkInstance;
import io.github.kosmx.emotes.arch.EmotecraftClientMod;
import io.github.kosmx.emotes.arch.network.client.ClientNetwork;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.network.PacketTask;
import io.github.kosmx.emotes.common.network.objects.NetData;
import io.github.kosmx.emotes.main.EmoteHolder;
import it.unimi.dsi.fastutil.Pair;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.minecraft.class_11890;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_746;
import org.jetbrains.annotations.Nullable;

public class ClientEmotePlay extends ClientEmoteAPI {
   private static final Map<UUID, ClientEmotePlay.QueueEntry> QUEUE = new ConcurrentHashMap<>();
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !ClientEmotePlay.class.desiredAssertionStatus();

   public static void clientStartLocalEmote(EmoteHolder emoteHolder) {
      clientStartLocalEmote(emoteHolder.getEmote());
   }

   public static boolean clientStartLocalEmote(Animation emote) {
      return clientStartLocalEmote(emote, 0.0F);
   }

   public static boolean clientStartLocalEmote(Animation emote, float tick) {
      class_746 player = ClientUtil.getClientPlayer();
      if (player.emotecraft$isForcedEmote()) {
         return false;
      }

      EmotePacket.Builder packetBuilder = new EmotePacket.Builder();
      packetBuilder.configureToStreamEmote(emote, player.method_5667());
      packetBuilder.configureEmoteTick(tick);
      ClientPacketManager.send(packetBuilder, null);
      ClientEmoteEvents.EMOTE_PLAY.invoker().onEmotePlay(emote, tick, player.method_5667());
      player.emotecraft$playEmote(emote, tick, false);
      return true;
   }

   public static void clientRepeatLocalEmote(Animation emote, float tick, UUID target) {
      EmotePacket.Builder packetBuilder = new EmotePacket.Builder();
      packetBuilder.configureToStreamEmote(emote, ClientUtil.getClientPlayer().method_5667()).configureEmoteTick(tick);
      ClientPacketManager.send(packetBuilder, target);
   }

   public static boolean clientStopLocalEmote() {
      return ClientUtil.getClientPlayer().isPlayingEmote()
         ? clientStopLocalEmote(ClientUtil.getClientPlayer().emotecraft$getEmote().getCurrentAnimationInstance())
         : false;
   }

   public static boolean isForcedEmote() {
      return ClientUtil.getClientPlayer().emotecraft$isForcedEmote();
   }

   public static boolean clientStopLocalEmote(@Nullable Animation emoteData) {
      if (emoteData != null && !ClientUtil.getClientPlayer().emotecraft$isForcedEmote()) {
         EmotePacket.Builder packetBuilder = new EmotePacket.Builder();
         packetBuilder.configureToSendStop(emoteData.uuid(), ClientUtil.getClientPlayer().method_5667());
         ClientPacketManager.send(packetBuilder, null);
         ClientUtil.getClientPlayer().stopEmote();
         ClientEmoteEvents.LOCAL_EMOTE_STOP.invoker().onEmoteStop();
         return true;
      } else {
         return false;
      }
   }

   static void executeMessage(NetData data, INetworkInstance networkInstance) throws NullPointerException {
      CommonData.LOGGER.trace("[emotes client] Received message: {}", data);
      if (data.purpose == null) {
         CommonData.LOGGER.error("Packet execution is not possible without a purpose");
      } else {
         switch ((PacketTask)Objects.requireNonNull(data.purpose)) {
            case STREAM:
               assert data.emoteData != null;
               if (data.valid || !PlatformTools.getConfig().alwaysValidate.get()) {
                  receivePlayPacket(data.emoteData, data.player, data.tick, data.isForced);
               }
               break;
            case STOP:
               class_11890 player = PlatformTools.getAvatarFromUUID(data.player);
               assert data.stopEmoteID != null;
               if (player != null) {
                  ClientEmoteEvents.EMOTE_STOP.invoker().onEmoteStop(data.stopEmoteID, player.method_5667());
                  player.stopEmote(data.stopEmoteID);
                  if (player.isMainAvatar() && !data.isForced) {
                     PlatformTools.addToast(class_2561.method_43471("emotecraft.blockedEmote"));
                  }
               } else {
                  QUEUE.remove(data.player);
               }
               break;
            case CONFIG:
               networkInstance.setVersions(Objects.requireNonNull(data.versions));
               CommonData.LOGGER.warn("Legacy versions was received: {}", data.versions);
               if (networkInstance instanceof ClientNetwork base) {
                  base.onConfigurationDone();
               }
               break;
            case FILE:
               EmoteHolder.addEmoteToList(data.emoteData, networkInstance);
               break;
            case UNKNOWN:
            default:
               CommonData.LOGGER.error("Packet execution is not possible: {} purpose!", data.purpose);
         }
      }
   }

   static void receivePlayPacket(Animation emoteData, UUID player, float tick, boolean isForced) {
      class_11890 playerEntity = PlatformTools.getAvatarFromUUID(player);
      if (isEmoteAllowed(emoteData, player)) {
         EventResult result = ClientEmoteEvents.EMOTE_VERIFICATION.invoker().verify(emoteData, player);
         if (result == EventResult.FAIL) {
            return;
         }

         if (playerEntity != null) {
            ClientEmoteEvents.EMOTE_PLAY.invoker().onEmotePlay(emoteData, tick, player);
            playerEntity.emotecraft$playEmote(emoteData, tick, isForced);
         } else {
            QUEUE.put(player, new ClientEmotePlay.QueueEntry(emoteData, tick, EmotecraftClientMod.getTick()));
         }
      }
   }

   public static boolean isEmoteAllowed(Animation emoteData, UUID player) {
      return !PlatformTools.getConfig().enablePlayerSafety.get() || !class_310.method_1551().method_29042(player);
   }

   @Nullable
   public static Pair<Animation, Float> getEmoteForUUID(UUID uuid) {
      if (QUEUE.containsKey(uuid)) {
         ClientEmotePlay.QueueEntry entry = QUEUE.get(uuid);
         Animation emoteData = entry.emoteData;
         float tick = entry.beginTick - entry.receivedTick + EmotecraftClientMod.getTick();
         QUEUE.remove(uuid);
         return !emoteData.isPlayingAt(tick) ? null : Pair.of(emoteData, tick);
      } else {
         return null;
      }
   }

   public static void checkQueue() {
      int currentTick = EmotecraftClientMod.getTick();
      QUEUE.forEach(
         (uuid, entry) -> {
            if (!entry.emoteData.isPlayingAt(entry.beginTick + currentTick) && entry.beginTick + currentTick > 0.0F
               || EmotecraftClientMod.getTick() - entry.receivedTick > 24000) {
               QUEUE.remove(uuid);
            }
         }
      );
   }

   @Override
   protected boolean playEmoteImpl(Animation animation, float tick) {
      return animation != null ? clientStartLocalEmote(animation, tick) : clientStopLocalEmote();
   }

   @Override
   protected Collection<Animation> clientEmoteListImpl() {
      return EmoteHolder.list.values().stream().map(EmoteHolder::getEmote).collect(Collectors.toList());
   }

   static class QueueEntry {
      final Animation emoteData;
      final float beginTick;
      final int receivedTick;

      QueueEntry(Animation emoteData, float begin, int received) {
         this.emoteData = emoteData;
         this.beginTick = begin;
         this.receivedTick = received;
      }
   }
}
