package io.github.kosmx.emotes.common.network.objects;

import com.zigythebird.playeranimcore.network.AnimationBinary;
import io.netty.buffer.ByteBuf;

public class NewAnimPacket extends AbstractNetworkPacket {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !NewAnimPacket.class.desiredAssertionStatus();

   @Override
   public byte getID() {
      return -103;
   }

   @Override
   public byte getVer() {
      return 5;
   }

   @Override
   public void read(ByteBuf buf, NetData config, byte version) {
      config.tick = buf.readFloat();
      config.emoteData = AnimationBinary.read(buf, version);
      config.valid = true;
   }

   @Override
   public void write(ByteBuf buf, NetData config, byte version) {
      assert config.emoteData != null;
      buf.writeFloat(config.tick);
      AnimationBinary.write(buf, version, config.emoteData);
   }

   @Override
   public boolean doWrite(NetData data) {
      return data.emoteData != null && data.stopEmoteID == null && data.versions.containsKey((byte)-103);
   }
}
