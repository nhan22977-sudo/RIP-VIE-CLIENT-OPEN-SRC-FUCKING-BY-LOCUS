package io.github.kosmx.emotes.main.emotePlay.instances;

import net.minecraft.class_1111;
import net.minecraft.class_1113;
import net.minecraft.class_1144;
import net.minecraft.class_1146;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_3419;
import net.minecraft.class_5862;
import net.minecraft.class_5863;
import net.minecraft.class_1111.class_1112;
import net.minecraft.class_1113.class_1114;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoundDirectInstance implements class_1113 {
   private static final class_5863 DEFAULT_FLOAT = class_5862.method_33908(1.0F);
   protected final class_1111 sound;
   protected final float volume;
   protected final float pitch;
   protected final class_243 pos;

   public SoundDirectInstance(class_2960 sound, float volume, float pitch, class_243 pos) {
      this(new class_1111(sound, DEFAULT_FLOAT, DEFAULT_FLOAT, 1, class_1112.field_5474, false, false, 16), volume, pitch, pos);
   }

   public SoundDirectInstance(class_1111 sound, float volume, float pitch, class_243 pos) {
      this.sound = sound;
      this.volume = volume;
      this.pitch = pitch;
      this.pos = pos;
   }

   @NotNull
   public class_2960 method_4775() {
      return this.sound.method_4767();
   }

   @Nullable
   public class_1146 method_4783(class_1144 manager) {
      return new EmotecraftSoundEvents(this.sound);
   }

   @NotNull
   public class_1111 method_4776() {
      return this.sound;
   }

   @NotNull
   public class_3419 method_4774() {
      return class_3419.field_15248;
   }

   public boolean method_4786() {
      return false;
   }

   public boolean method_4787() {
      return false;
   }

   public int method_4780() {
      return 0;
   }

   public float method_4781() {
      return this.volume;
   }

   public float method_4782() {
      return this.pitch;
   }

   public double method_4784() {
      return this.pos.method_10216() + 0.5;
   }

   public double method_4779() {
      return this.pos.method_10214() + 0.5;
   }

   public double method_4778() {
      return this.pos.method_10215() + 0.5;
   }

   @NotNull
   public class_1114 method_4777() {
      return class_1114.field_5476;
   }
}
