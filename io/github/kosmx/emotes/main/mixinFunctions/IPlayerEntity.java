package io.github.kosmx.emotes.main.mixinFunctions;

import com.zigythebird.playeranim.util.ClientUtil;
import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.main.emotePlay.EmotePlayer;
import io.github.kosmx.emotes.main.network.ClientEmotePlay;
import java.util.UUID;
import net.minecraft.class_5498;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface IPlayerEntity {
   default void initEmotePerspective() {
      class_5498 changeTo = (class_5498)PlatformTools.getConfig().cameraType.get();
      if (this.isMainAvatar() && !changeTo.method_31034() && PlatformTools.getCameraType().method_31034()) {
         this.emotecraft$getEmote().perspective = true;
         PlatformTools.setCameraType(changeTo);
      }
   }

   default void emotecraft$playEmote(@Nullable Animation emote, float tick, boolean isForced) {
      this.emotecraft$playEmote(emote, Animation.LoopType.DEFAULT, tick, isForced);
   }

   @Internal
   default void emotecraft$playEmote(@Nullable Animation emote, Animation.LoopType loopType, float tick, boolean isForced) {
      throw new NotImplementedException();
   }

   @NotNull
   default EmotePlayer emotecraft$getEmote() {
      throw new NotImplementedException();
   }

   default boolean isPlayingEmote() {
      return EmotePlayer.isRunningEmote(this.emotecraft$getEmote());
   }

   default boolean isMainAvatar() {
      return ClientUtil.getClientPlayer() == this;
   }

   default void stopEmote() {
      this.emotecraft$getEmote().stop();
   }

   default void stopEmote(UUID emoteID) {
      Animation animation = this.emotecraft$getEmote().getCurrentAnimationInstance();
      if (animation != null && animation.uuid().equals(emoteID)) {
         this.stopEmote();
      }
   }

   default boolean emotecraft$isForcedEmote() {
      throw new NotImplementedException();
   }

   default void emotecraft$playerEntersInvalidPose() {
      if (this.isPlayingEmote() && !this.emotecraft$isForcedEmote()) {
         if (PlatformTools.getConfig().checkPose.get()) {
            ClientEmotePlay.clientStopLocalEmote(this.emotecraft$getEmote().getCurrentAnimationInstance());
         }
      }
   }
}
