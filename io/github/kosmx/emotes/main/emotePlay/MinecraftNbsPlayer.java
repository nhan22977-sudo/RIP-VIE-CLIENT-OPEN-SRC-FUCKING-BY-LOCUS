package io.github.kosmx.emotes.main.emotePlay;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import io.github.kosmx.emotes.arch.screen.utils.UnsafeMannequin;
import io.github.kosmx.emotes.common.nbsplayer.NbsPlayer;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.class_1113;
import net.minecraft.class_11890;
import net.minecraft.class_310;
import net.raphimc.noteblocklib.format.nbs.model.event.NbsSoundStopperEvent;
import net.raphimc.noteblocklib.model.note.Note;
import net.raphimc.noteblocklib.model.song.Song;
import net.raphimc.noteblocklib.util.TimerHack;

public class MinecraftNbsPlayer extends NbsPlayer {
   private final Queue<MinecraftNbsPlayer.PlayingNote> playingNotes = new ConcurrentLinkedQueue<>();
   private final boolean hasSoundStoppers;
   protected final class_11890 avatar;

   public MinecraftNbsPlayer(PlayerAnimationController controller, Song song) {
      super(song, controller);
      this.avatar = controller.getAvatar();
      this.hasSoundStoppers = song.getEvents().testEach(NbsSoundStopperEvent.class::isInstance);
   }

   public void start(int delay, int tick) {
      TimerHack.ENABLED = false;
      super.start(delay, tick);
   }

   @Override
   protected boolean shouldTick() {
      if (this.avatar instanceof UnsafeMannequin) {
         return super.shouldTick();
      }

      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null && !this.avatar.method_31481()) {
         return !mc.method_1493() && !this.avatar.method_5756(mc.field_1724) && super.shouldTick();
      }

      this.stop();
      return false;
   }

   @Override
   protected void playNote(Note note) {
      class_1113 sound = InstrumentConventer.getInstrument(note, this.avatar.method_73189());
      if (this.hasSoundStoppers) {
         this.playingNotes.add(new MinecraftNbsPlayer.PlayingNote(note, sound));
      }

      class_310 mc = class_310.method_1551();
      mc.execute(() -> mc.method_1483().method_4873(sound));
   }

   @Override
   protected void handleEvent(NbsSoundStopperEvent event) {
      class_310 mc = class_310.method_1551();
      this.playingNotes.removeIf(playing -> {
         if (!event.shouldStop(playing.note())) {
            return false;
         }

         mc.execute(() -> mc.method_1483().method_4870(playing.sound()));
         return true;
      });
   }

   @Override
   public void stop() {
      super.stop();
      this.playingNotes.clear();
   }

   private record PlayingNote(Note note, class_1113 sound) {
   }
}
