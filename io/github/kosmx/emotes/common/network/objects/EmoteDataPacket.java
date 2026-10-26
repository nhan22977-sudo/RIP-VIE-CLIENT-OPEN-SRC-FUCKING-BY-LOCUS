package io.github.kosmx.emotes.common.network.objects;

import com.zigythebird.playeranimcore.network.LegacyAnimationBinary;
import io.netty.buffer.ByteBuf;
import java.io.IOException;

public class EmoteDataPacket extends AbstractNetworkPacket {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !EmoteDataPacket.class.desiredAssertionStatus();

   @Override
   public void write(ByteBuf buf, NetData config, byte version) {
      assert config.emoteData != null;
      buf.writeInt((int)config.tick);
      LegacyAnimationBinary.write(config.emoteData, buf, version);
   }

   @Override
   public void read(ByteBuf buf, NetData config, byte version) throws IOException {
      config.tick = buf.readInt();
      config.emoteData = LegacyAnimationBinary.read(buf, version);
      config.valid = true;
   }

   @Override
   public byte getID() {
      return 0;
   }

   @Override
   public byte getVer() {
      return (byte)LegacyAnimationBinary.getCurrentVersion();
   }

   @Override
   public boolean doWrite(NetData data) {
      return data.emoteData != null && data.stopEmoteID == null && !data.versions.containsKey((byte)-103);
   }
}
