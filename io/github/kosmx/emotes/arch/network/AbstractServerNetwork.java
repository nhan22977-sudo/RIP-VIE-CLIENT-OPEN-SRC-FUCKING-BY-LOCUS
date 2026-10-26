package io.github.kosmx.emotes.arch.network;

import io.github.kosmx.emotes.api.proxy.INetworkInstance;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.server.network.EmotePlayTracker;
import io.github.kosmx.emotes.server.network.IServerNetworkInstance;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_11890;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractServerNetwork implements INetworkInstance, IServerNetworkInstance {
   private final EmotePlayTracker emotePlayTracker = new EmotePlayTracker();

   @NotNull
   protected abstract EmotesMixinConnection getServerConnection();

   @NotNull
   protected abstract class_11890 getAvatar();

   @Override
   public Map<Byte, Byte> getRemoteVersions() {
      return this.getServerConnection().emotecraft$getRemoteVersions();
   }

   @Override
   public void setVersions(Map<Byte, Byte> map) {
      this.getServerConnection().emotecraft$setVersions(map);
   }

   @Override
   public boolean isServerTrackingPlayState() {
      return true;
   }

   @Override
   public int maxDataSize() {
      return 1048560;
   }

   @Override
   public EmotePlayTracker getEmoteTracker() {
      return this.emotePlayTracker;
   }

   @Override
   public void sendMessage(EmotePacket.Builder builder, @Nullable UUID target) {
      this.sendPlayMessage(builder.setVersion(this.getRemoteVersions()).build());
   }

   public abstract void sendPlayMessage(EmotePacket var1);
}
