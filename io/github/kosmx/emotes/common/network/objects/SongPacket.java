package io.github.kosmx.emotes.common.network.objects;

import io.github.kosmx.emotes.common.nbsplayer.LegacyNBSPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.raphimc.noteblocklib.NoteBlockLib;
import net.raphimc.noteblocklib.format.SongFormat;
import net.raphimc.noteblocklib.format.nbs.model.NbsSong;
import net.raphimc.noteblocklib.model.song.Song;

public class SongPacket extends AbstractNetworkPacket {
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !SongPacket.class.desiredAssertionStatus();

   @Override
   public byte getID() {
      return 3;
   }

   @Override
   public byte getVer() {
      return 2;
   }

   @Override
   public void read(ByteBuf buf, NetData config, byte version) throws IOException {
      Song song = (Song)(switch (version) {
         case 1 -> {
            NbsSong var13 = LegacyNBSPacket.read(buf);
            yield var13;
         }
         case 2 -> {
            Song var12;
            try (InputStream is = new ByteBufInputStream(buf)) {
               var12 = NoteBlockLib.readSong(is, SongFormat.NBS);
            } catch (Exception e) {
               throw new IOException(e);
            }

            yield var12;
         }
         default -> {
            Object var5 = null;
            yield var5;
         }
      });
      config.extraData.put("song", song);
   }

   @Override
   public void write(ByteBuf buf, NetData config, byte version) throws IOException {
      assert config.emoteData != null;
      Song song = (Song)config.emoteData.data().getRaw("song");
      if (version > 1) {
         try (OutputStream os = new ByteBufOutputStream(buf)) {
            NoteBlockLib.writeSong(song, os);
         } catch (Exception e) {
            throw new IOException(e);
         }
      } else {
         LegacyNBSPacket.write((NbsSong)song, buf);
      }
   }

   @Override
   public boolean doWrite(NetData config) {
      return config.versions.get(this.getID()) != 0 && config.emoteData != null && config.emoteData.data().has("song");
   }

   @Override
   public boolean isOptional() {
      return true;
   }
}
