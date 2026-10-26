package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.Event;
import net.minecraft.class_11908;

public class KeyInputEvent extends Event {
   private final class_11908 input;
   private final int action;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public KeyInputEvent(class_11908 input, int action) {
      this.input = inputx;
      this.action = actionx;
   }

   public class_11908 getInput() {
      return this.input;
   }

   public int getAction() {
      return this.action;
   }
}
