package io.github.kosmx.emotes.main.emotePlay.instances;

import net.minecraft.class_1111;
import net.minecraft.class_1140;
import net.minecraft.class_1146;
import net.minecraft.class_1148;
import net.minecraft.class_5819;
import org.jetbrains.annotations.NotNull;

public class EmotecraftSoundEvents extends class_1146 {
   protected final class_1111 sound;

   public EmotecraftSoundEvents(class_1111 sound) {
      super(sound.method_4767(), "Emotecraft");
      this.sound = sound;
   }

   @NotNull
   public class_1111 method_4887(class_5819 randomSource) {
      return this.sound;
   }

   public int method_4894() {
      return this.sound.method_4894();
   }

   public void method_4885(class_1148<class_1111> accessor) {
   }

   public void method_18188(class_1140 engine) {
   }
}
