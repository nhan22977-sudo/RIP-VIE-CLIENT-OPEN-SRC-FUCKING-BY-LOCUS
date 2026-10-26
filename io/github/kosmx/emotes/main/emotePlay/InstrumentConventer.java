package io.github.kosmx.emotes.main.emotePlay;

import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.main.emotePlay.instances.SoundDirectInstance;
import io.github.kosmx.emotes.main.emotePlay.instances.SoundEventInstance;
import java.io.File;
import java.util.Locale;
import net.minecraft.class_1111;
import net.minecraft.class_1113;
import net.minecraft.class_1144;
import net.minecraft.class_243;
import net.minecraft.class_2766;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3300;
import net.minecraft.class_3414;
import net.minecraft.class_7923;
import net.raphimc.noteblocklib.format.minecraft.MinecraftInstrument;
import net.raphimc.noteblocklib.format.nbs.model.NbsCustomInstrument;
import net.raphimc.noteblocklib.model.note.Instrument;
import net.raphimc.noteblocklib.model.note.Note;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InstrumentConventer {
   public static class_1113 getInstrument(Note note, class_243 pos) {
      Instrument rawInstrument = note.getInstrument();
      if (rawInstrument instanceof MinecraftInstrument instrument) {
         class_2766 noteBlock = convertToNoteBlock(instrument.mcId(), instrument.mcSoundName());
         return createForNoteBlock(noteBlock, pos, note.getVolume(), note.getPitch());
      } else if (rawInstrument instanceof NbsCustomInstrument instrument) {
         String file = instrument.getSoundFilePathOr("").replace(File.separatorChar, '/');
         if (file.endsWith(".ogg")) {
            file = file.substring(0, file.length() - 4);
         }

         file = file.replace("Custom/", "").toLowerCase(Locale.ROOT);
         class_2960 sound = parseSoundFile(class_310.method_1551().method_1478(), file);
         if (sound != null) {
            return new SoundDirectInstance(sound, note.getVolume(), note.getPitch(), pos);
         }

         class_3414 event = parseSoundName(instrument.getNameOr(""));
         if (event != null) {
            return new SoundEventInstance(event, note.getVolume(), note.getPitch(), pos);
         }

         CommonData.LOGGER.warn("Failed parse custom instrument: name={}, file={}", instrument.getNameOr(""), file);
         return new SoundDirectInstance(class_1144.field_5592, note.getVolume(), note.getPitch(), pos);
      } else {
         CommonData.LOGGER.warn("Unsupported instrument type: {}", rawInstrument == null ? null : rawInstrument.getClass().getName());
         return createForNoteBlock(class_2766.field_12648, pos, note.getVolume(), note.getPitch());
      }
   }

   private static class_2766 convertToNoteBlock(int mcId, String mcSoundName) {
      class_2766[] instruments = class_2766.values();

      for (class_2766 instrument : instruments) {
         boolean soundEventMatch = false;

         try {
            soundEventMatch = mcSoundName.equalsIgnoreCase(((class_3414)instrument.method_11886().comp_349()).comp_3319().method_12832());
         } catch (Exception var9) {
         }

         if (soundEventMatch || mcSoundName.equalsIgnoreCase("block.note_block." + instrument.method_15434())) {
            return instrument;
         }
      }

      return mcId >= 0 && mcId < instruments.length ? instruments[mcId] : class_2766.field_12648;
   }

   private static class_1113 createForNoteBlock(class_2766 instrument, class_243 pos, float volume, float pitch) {
      return new SoundEventInstance((class_3414)instrument.method_11886().comp_349(), volume, pitch, pos);
   }

   @Nullable
   public static class_2960 parseSoundFile(class_3300 manager, String file) {
      class_2960 first = class_2960.method_12829(file);
      if (first != null && manager.method_14486(class_1111.field_40575.method_45112(first)).isPresent()) {
         return first;
      }

      int namespaceIndex = file.indexOf("/");
      String namespace = namespaceIndex != -1 ? file.substring(0, namespaceIndex) : file;
      String path = namespaceIndex != -1 && file.startsWith(namespace) ? file.substring(namespace.length() + 1) : file;
      class_2960 second = class_2960.method_43902(namespace, path);
      return second != null && manager.method_14486(class_1111.field_40575.method_45112(second)).isPresent() ? second : null;
   }

   @Nullable
   public static class_3414 parseSoundName(@NotNull String name) {
      class_2960 first = class_2960.method_12829(name);
      if (first != null && class_7923.field_41172.method_10250(first)) {
         return (class_3414)class_7923.field_41172.method_63535(first);
      } else {
         return name.contains(".firework.") ? parseSoundName(name.replace(".firework.", ".firework_rocket.")) : null;
      }
   }
}
