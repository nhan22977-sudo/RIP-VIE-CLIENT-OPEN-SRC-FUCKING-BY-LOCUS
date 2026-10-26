package io.github.kosmx.emotes.main.emotePlay;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.RawAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.event.CustomKeyFrameEvents;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.KeyFrameData;
import com.zigythebird.playeranimcore.enums.PlayState;
import com.zigythebird.playeranimcore.enums.State;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.screen.utils.UnsafeMannequin;
import net.minecraft.class_11890;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_5498;
import net.raphimc.noteblocklib.model.song.Song;
import org.jetbrains.annotations.Nullable;

public class EmotePlayer extends PlayerAnimationController {
   @Nullable
   private MinecraftNbsPlayer song;
   public boolean perspective = false;
   public boolean muteNbs = false;

   public EmotePlayer(class_11890 avatar) {
      super(avatar, (controller, state, animSetter) -> PlayState.STOP);
   }

   @Override
   protected void setAnimation(RawAnimation rawAnimation, float startAnimFrom) {
      State state = this.getAnimationState();
      super.setAnimation(rawAnimation, startAnimFrom);
      if (this.getCurrentAnimationInstance() != null) {
         this.animationState = state;
      }
   }

   @Override
   protected void setupNewAnimation() {
      super.setupNewAnimation();
      Animation emote = this.getCurrentAnimationInstance();
      if (this.song != null) {
         this.song.stop();
      }

      if (emote != null && emote.data().has("song")) {
         this.song = new MinecraftNbsPlayer(this, emote.data().<Song>get("song").orElseThrow());
      } else {
         this.song = null;
      }
   }

   @Override
   public void stop() {
      super.stop();
      this.stopTriggeredAnimation();
      this.animationQueue.clear();
      this.internalStop();
   }

   @Override
   public void process(AnimationData state) {
      super.process(state);
      if (!this.animationState.isActive()) {
         this.internalStop();
      }
   }

   private void internalStop() {
      class_5498 changeTo = (class_5498)PlatformTools.getConfig().cameraType.get();
      if (this.perspective && !changeTo.method_31034() && PlatformTools.getCameraType() == changeTo) {
         class_310.method_1551().field_1690.method_31043(class_5498.field_26664);
         this.perspective = false;
      }

      if (this.song != null) {
         this.song.stop();
      }
   }

   public static boolean isRunningEmote(@Nullable EmotePlayer emote) {
      return emote != null && emote.isActive();
   }

   @Override
   protected <T extends KeyFrameData> void handleCustomKeyframe(
      T[] keyframes,
      CustomKeyFrameEvents.@Nullable CustomKeyFrameHandler<T> main,
      CustomKeyFrameEvents.CustomKeyFrameHandler<T> event,
      float animationTick,
      AnimationData animationData
   ) {
      if (!(this.avatar instanceof UnsafeMannequin)) {
         super.handleCustomKeyframe(keyframes, main, event, animationTick, animationData);
      }
   }

   @Override
   protected void applyCustomPivotPoints() {
      if (this.song != null && !this.song.isFirstSongPlayed() && this.isActive() && !this.song.isRunning() && !this.muteNbs) {
         if (PlatformTools.getConfig().displayNowPlaying.get() && !(this.avatar instanceof UnsafeMannequin)) {
            String nowPlaying = this.song.getNowPlaying();
            if (nowPlaying != null) {
               class_310.method_1551().field_1705.method_1732(class_2561.method_43470(nowPlaying));
            }
         }

         this.song.setPaused(this.getAnimationState() == State.PAUSED);
         this.song.start();
      }

      super.applyCustomPivotPoints();
   }

   @Override
   public void pause() {
      super.pause();
      if (this.song != null) {
         this.song.setPaused(true);
      }
   }

   @Override
   public void unpause() {
      super.unpause();
      if (this.song != null) {
         this.song.setPaused(false);
      }
   }
}
