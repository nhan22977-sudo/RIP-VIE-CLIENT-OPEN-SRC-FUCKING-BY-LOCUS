package io.github.kosmx.emotes.main.emotePlay.instances;

import net.minecraft.class_1102;
import net.minecraft.class_1144;
import net.minecraft.class_1146;
import net.minecraft.class_243;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_5819;
import org.jetbrains.annotations.NotNull;

public class SoundEventInstance extends class_1102 {
   private static final class_5819 RANDOM_SOURCE = class_5819.method_43050();

   public SoundEventInstance(class_3414 soundEvent, float volume, float pitch, class_243 pos) {
      super(soundEvent, class_3419.field_15248, RANDOM_SOURCE);
      this.field_5442 = volume;
      this.field_5441 = pitch;
      this.field_5439 = pos.method_10216();
      this.field_5450 = pos.method_10214();
      this.field_5449 = pos.method_10215();
   }

   @NotNull
   public class_1146 method_4783(class_1144 manager) {
      super.method_4783(manager);
      return new EmotecraftSoundEvents(this.field_5444);
   }
}
