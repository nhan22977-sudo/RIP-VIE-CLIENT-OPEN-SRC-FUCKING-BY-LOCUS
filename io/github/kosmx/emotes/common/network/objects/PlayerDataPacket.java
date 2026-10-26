package io.github.kosmx.emotes.common.network.objects;

import com.zigythebird.playeranimcore.network.NetworkUtils;
import io.netty.buffer.ByteBuf;

public class PlayerDataPacket extends AbstractNetworkPacket {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !PlayerDataPacket.class.desiredAssertionStatus();

   @Override
   public byte getID() {
      return 1;
   }

   @Override
   public byte getVer() {
      return 1;
   }

   @Override
   public void read(ByteBuf byteBuf, NetData config, byte version) {
      config.player = NetworkUtils.readUuid(byteBuf);
      if (version >= 1) {
         config.isForced = byteBuf.readByte() != 0;
      }
   }

   @Override
   public void write(ByteBuf byteBuf, NetData config, byte version) {
      assert config.player != null;
      NetworkUtils.writeUuid(byteBuf, config.player);
      if (version >= 1) {
         byteBuf.writeByte(config.isForced ? 1 : 0);
      }
   }

   @Override
   public boolean doWrite(NetData config) {
      return config.player != null;
   }
}
