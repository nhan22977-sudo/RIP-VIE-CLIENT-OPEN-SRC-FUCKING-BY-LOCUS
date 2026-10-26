package io.github.kosmx.emotes.common.nbsplayer;

import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.enums.State;
import io.github.kosmx.emotes.common.CommonData;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.raphimc.noteblocklib.format.nbs.model.NbsSong;
import net.raphimc.noteblocklib.format.nbs.model.event.NbsSoundStopperEvent;
import net.raphimc.noteblocklib.model.event.Event;
import net.raphimc.noteblocklib.model.note.Note;
import net.raphimc.noteblocklib.model.song.Song;
import net.raphimc.noteblocklib.player.SongPlayer;
import org.jetbrains.annotations.Nullable;

public abstract class NbsPlayer extends SongPlayer {
   private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(5, Thread.ofVirtual().name("Emotecraft-NBSplayer-", 0L).factory());
   @Nullable
   protected final AnimationController controller;
   protected int loopCount = 0;
   private boolean firstSongPlayed;

   public NbsPlayer(Song song, @Nullable AnimationController controller) {
      super(song);
      this.controller = controller;
      this.setCustomScheduler(EXECUTOR);
   }

   protected void playNotes(List<Note> notes) {
      if (this.isRunning()) {
         this.firstSongPlayed = true;

         for (Note note : notes) {
            this.playNote(note);
         }
      }
   }

   protected void preTick() {
      this.setPaused(!this.isRunning() || !this.shouldTick());
   }

   protected boolean shouldTick() {
      if (this.controller == null) {
         return true;
      } else if (!this.controller.isActive()) {
         this.stop();
         return false;
      } else {
         return this.controller.getAnimationState() == State.RUNNING;
      }
   }

   protected abstract void playNote(Note var1);

   protected void handleEvents(List<Event> events) {
      if (this.isRunning()) {
         for (Event event : events) {
            if (event instanceof NbsSoundStopperEvent stopperEvent) {
               this.handleEvent(stopperEvent);
            }
         }
      }
   }

   protected abstract void handleEvent(NbsSoundStopperEvent var1);

   protected void onSongFinished() {
      super.onSongFinished();
      if (this.getSong() instanceof NbsSong nbsSong && nbsSong.isLoop() && (this.loopCount < nbsSong.getMaxLoopCount() || nbsSong.getMaxLoopCount() == 0)) {
         this.loopCount++;
         this.start((int)(1000.0F / this.getCurrentTicksPerSecond()), nbsSong.getLoopStartTick());
      }
   }

   public void stop() {
      super.stop();
      this.firstSongPlayed = false;
   }

   public boolean isFirstSongPlayed() {
      return this.firstSongPlayed;
   }

   protected void onTickException(Throwable e) {
      CommonData.LOGGER.warn("An error occurred while playing nbs!", e);
      this.stop();
   }

   @Nullable
   public String getNowPlaying() {
      String name = this.getSong().getTitleOrFileNameOr(null);
      if (name != null && !name.isEmpty()) {
         String author = this.getSong().getAuthorOr(this.getSong().getOriginalAuthorOr(null));
         return author != null && !author.isEmpty() ? String.format("%s — %s", author, name) : name;
      } else {
         return null;
      }
   }
}
