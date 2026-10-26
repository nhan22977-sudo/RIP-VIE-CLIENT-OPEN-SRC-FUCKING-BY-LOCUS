package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.Event;
import net.minecraft.class_437;

public class ScreenChangeEvent extends Event {
   private final class_437 screen;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ScreenChangeEvent(class_437 screen) {
      this.screen = screenx;
   }

   public class_437 getScreen() {
      return this.screen;
   }
}
