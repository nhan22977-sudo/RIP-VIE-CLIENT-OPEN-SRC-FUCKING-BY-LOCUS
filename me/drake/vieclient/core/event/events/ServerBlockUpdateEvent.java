package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.Event;
import net.minecraft.class_2338;
import net.minecraft.class_2680;

public final class ServerBlockUpdateEvent extends Event {
   private final class_2338 pos;
   private final class_2680 state;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ServerBlockUpdateEvent(class_2338 pos, class_2680 state) {
      this.pos = posx.method_10062();
      this.state = statex;
   }

   public class_2338 getPos() {
      return this.pos;
   }

   public class_2680 getState() {
      return this.state;
   }
}
