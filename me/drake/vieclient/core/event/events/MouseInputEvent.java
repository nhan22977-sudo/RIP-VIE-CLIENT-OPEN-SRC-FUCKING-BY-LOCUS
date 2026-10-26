package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.Event;
import net.minecraft.class_11909;
import net.minecraft.class_11910;

public class MouseInputEvent extends Event {
   private final class_11910 mouseInput;
   private final int action;
   private final class_11909 click;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MouseInputEvent(class_11910 mouseInput, int action) {
      this.mouseInput = mouseInputx;
      this.action = actionx;
      this.click = new class_11909(0.0, 0.0, mouseInputx);
   }

   public class_11910 getMouseInput() {
      return this.mouseInput;
   }

   public class_11909 getClick() {
      return this.click;
   }

   public int getAction() {
      return this.action;
   }
}
