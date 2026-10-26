package io.github.kosmx.emotes.arch.network;

import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.network.objects.NetData;
import io.github.kosmx.emotes.server.network.AbstractServerEmotePlay;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.class_11890;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3244;

public final class CommonServerNetworkHandler extends AbstractServerEmotePlay<AbstractServerNetwork> {
   private final Map<UUID, AvatarServerPlayNetwork> nonPlayers = new WeakHashMap<>();

   public void receiveMessage(EmotePacket packet, class_1657 player) {
      if (player instanceof class_3222 serverPlayer) {
         try {
            this.receiveMessage(packet, getHandler(serverPlayer.field_13987));
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public static ModdedServerPlayNetwork getHandler(class_3244 handler) {
      return ((EmotesMixinNetwork)handler).emotecraft$getServerNetworkInstance();
   }

   public void receiveStreamMessage(EmotePacket packet, class_1657 player) {
      if (player instanceof class_3222 serverPlayer) {
         this.receiveStreamMessage(serverPlayer, getHandler(serverPlayer.field_13987), packet);
      }
   }

   public void receiveStreamMessage(class_3222 player, ModdedServerPlayNetwork handler, EmotePacket packet) {
      player.field_13987.method_52396(class_2561.method_43470("This server does not support streaming!"));
   }

   protected UUID getUUIDFromPlayer(AbstractServerNetwork player) {
      return player.getAvatar().method_5667();
   }

   protected AbstractServerNetwork getPlayerFromUUID(UUID player) {
      class_3222 serverPlayer = NetworkPlatformTools.INSTANCE.getServer().method_3760().method_14602(player);
      if (serverPlayer != null) {
         return this.getPlayerNetworkInstance(serverPlayer);
      }

      if (!this.nonPlayers.containsKey(player)) {
         for (class_3218 level : NetworkPlatformTools.INSTANCE.getServer().method_3738()) {
            if (level.method_66347(player) instanceof class_11890 avatar) {
               this.nonPlayers.put(player, new AvatarServerPlayNetwork(avatar));
               break;
            }
         }
      }

      return this.nonPlayers.get(player);
   }

   public AbstractServerNetwork getPlayerNetworkInstance(class_3222 player) {
      return ((EmotesMixinNetwork)player.field_13987).emotecraft$getServerNetworkInstance();
   }

   protected void sendForEveryoneElse(NetData data, AbstractServerNetwork player) {
      for (class_3222 target : NetworkPlatformTools.INSTANCE.getTrackedBy(player.getAvatar())) {
         AbstractServerNetwork targetInstance = this.getPlayerNetworkInstance(target);
         if (targetInstance != player && NetworkPlatformTools.INSTANCE.canSendPlay(target, NetworkPlatformTools.EMOTE_CHANNEL_ID.comp_2242())) {
            this.sendForPlayer(data, player, targetInstance);
         }
      }
   }

   public static CommonServerNetworkHandler getInstance() {
      return (CommonServerNetworkHandler)AbstractServerEmotePlay.getInstance();
   }
}
