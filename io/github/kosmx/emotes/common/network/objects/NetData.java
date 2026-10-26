package io.github.kosmx.emotes.common.network.objects;

import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.common.network.PacketTask;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public final class NetData {
   public PacketTask purpose = PacketTask.UNKNOWN;
   @Nullable
   public UUID stopEmoteID = null;
   @Nullable
   public Animation emoteData = null;
   public float tick = 0.0F;
   public boolean valid;
   public final ByteSet skippedPackets = new ByteOpenHashSet();
   public boolean versionsUpdated = false;
   public final Map<Byte, Byte> versions = new HashMap<>();
   @Nullable
   public UUID player = null;
   public boolean isForced = false;
   public int sizeLimit = 1048576;
   public boolean strictSizeLimit = true;
   final Map<String, Object> extraData = new HashMap<>();

   public boolean prepareAndValidate() {
      if (this.emoteData != null && !this.extraData.isEmpty()) {
         this.emoteData.data().data().putAll(this.extraData);
      }

      if (this.purpose == PacketTask.UNKNOWN) {
         return false;
      } else if (this.purpose == PacketTask.STOP && this.stopEmoteID == null) {
         return false;
      } else if (this.purpose == PacketTask.STREAM && this.emoteData == null) {
         return false;
      } else {
         return this.purpose == PacketTask.CONFIG && !this.versionsUpdated ? false : this.emoteData == null || this.stopEmoteID == null;
      }
   }

   public NetData copy() {
      NetData data = new NetData();
      data.purpose = this.purpose;
      data.stopEmoteID = this.stopEmoteID;
      data.emoteData = this.emoteData;
      data.tick = this.tick;
      data.valid = this.valid;
      data.versionsUpdated = this.versionsUpdated;
      data.versions.putAll(this.versions);
      data.player = this.player;
      data.sizeLimit = this.sizeLimit;
      data.isForced = this.isForced;
      return data;
   }

   @Override
   public String toString() {
      return "NetData{purpose="
         + this.purpose
         + ", stopEmoteID="
         + this.stopEmoteID
         + ", emoteData="
         + this.emoteData
         + ", startingAt="
         + this.tick
         + ", player="
         + this.player
         + "}";
   }
}
