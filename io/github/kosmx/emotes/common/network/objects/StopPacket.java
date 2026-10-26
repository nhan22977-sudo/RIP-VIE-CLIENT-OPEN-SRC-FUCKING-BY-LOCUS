package io.github.kosmx.emotes.common.network.objects;

import com.zigythebird.playeranimcore.network.NetworkUtils;
import io.netty.buffer.ByteBuf;

public class StopPacket extends AbstractNetworkPacket {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !StopPacket.class.desiredAssertionStatus();

   @Override
   public byte getID() {
      return 10;
   }

   @Override
   public byte getVer() {
      return 1;
   }

   @Override
   public void read(ByteBuf buf, NetData config, byte version) {
      config.stopEmoteID = NetworkUtils.readUuid(buf);
   }

   @Override
   public void write(ByteBuf buf, NetData config, byte version) {
      assert config.stopEmoteID != null;
      NetworkUtils.writeUuid(buf, config.stopEmoteID);
   }

   @Override
   public boolean doWrite(NetData config) {
      return config.stopEmoteID != null;
   }
}
