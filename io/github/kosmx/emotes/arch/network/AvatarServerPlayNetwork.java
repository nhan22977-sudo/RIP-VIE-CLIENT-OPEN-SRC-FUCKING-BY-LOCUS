package io.github.kosmx.emotes.arch.network;

import io.github.kosmx.emotes.common.network.EmotePacket;
import net.minecraft.class_11890;
import net.minecraft.class_1657;
import org.jetbrains.annotations.NotNull;

public final class AvatarServerPlayNetwork extends AbstractServerNetwork {
   @NotNull
   private final class_11890 avatar;

   public AvatarServerPlayNetwork(@NotNull class_11890 avatar) {
      if (avatar instanceof class_1657) {
         throw new UnsupportedOperationException("For players, use ModdedServerPlayNetwork!");
      }

      this.avatar = avatar;
   }

   @NotNull
   @Override
   protected EmotesMixinConnection getServerConnection() {
      throw new UnsupportedOperationException("Only players can have a connection!");
   }

   @NotNull
   @Override
   protected class_11890 getAvatar() {
      return this.avatar;
   }

   @Override
   public boolean isActive() {
      return false;
   }

   @Override
   public void sendPlayMessage(EmotePacket bytes) {
      throw new UnsupportedOperationException("Only players can have a connection!");
   }
}
