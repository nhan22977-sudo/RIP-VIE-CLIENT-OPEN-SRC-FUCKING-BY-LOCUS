package io.github.kosmx.emotes.arch.network;

import io.github.kosmx.emotes.arch.mixin.ServerCommonPacketListenerAccessor;
import io.github.kosmx.emotes.common.network.EmotePacket;
import net.minecraft.class_11890;
import net.minecraft.class_3244;
import org.jetbrains.annotations.NotNull;

public final class ModdedServerPlayNetwork extends AbstractServerNetwork {
   @NotNull
   private final class_3244 serverGamePacketListener;

   public ModdedServerPlayNetwork(@NotNull class_3244 serverGamePacketListener) {
      this.serverGamePacketListener = serverGamePacketListener;
   }

   @NotNull
   @Override
   protected EmotesMixinConnection getServerConnection() {
      return (EmotesMixinConnection)((ServerCommonPacketListenerAccessor)this.serverGamePacketListener).getConnection();
   }

   @NotNull
   @Override
   protected class_11890 getAvatar() {
      return this.serverGamePacketListener.field_14140;
   }

   @Override
   public boolean isActive() {
      return true;
   }

   @Override
   public void sendPlayMessage(EmotePacket packet) {
      this.serverGamePacketListener.method_14364(NetworkPlatformTools.playPacket(packet));
   }
}
