package io.github.kosmx.emotes.arch.network.fabric;

import io.github.kosmx.emotes.arch.network.NetworkPlatformTools;
import io.github.kosmx.emotes.fabric.EmotecraftFabricMod;
import java.util.Collection;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.class_1297;
import net.minecraft.class_2960;
import net.minecraft.class_3222;
import net.minecraft.class_8610;
import net.minecraft.server.MinecraftServer;

public final class NetworkPlatformToolsImpl implements NetworkPlatformTools {
   @Override
   public boolean canSendPlay(class_3222 player, class_2960 channel) {
      return ServerPlayNetworking.canSend(player, channel);
   }

   @Override
   public boolean canSendConfig(class_8610 player, class_2960 channel) {
      return ServerConfigurationNetworking.canSend(player, channel);
   }

   @Override
   public Collection<class_3222> getTrackedBy(class_1297 entity) {
      return PlayerLookup.tracking(entity);
   }

   @Override
   public MinecraftServer getServer() {
      return EmotecraftFabricMod.SERVER_INSTANCE;
   }
}
