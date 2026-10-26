package io.github.kosmx.emotes.arch.network.client.fabric;

import io.github.kosmx.emotes.arch.network.client.ClientNetwork;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.class_2960;
import net.minecraft.class_8710;

public final class ClientNetworkImpl extends ClientNetwork {
   @Override
   public boolean isServerChannelOpen(class_2960 id) {
      return ClientPlayNetworking.canSend(id);
   }

   @Override
   public void sendPlayPacket(class_8710 payload) {
      ClientPlayNetworking.send(payload);
   }
}
