package io.github.kosmx.emotes.server.serializer;

import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.tools.MathHelper;
import io.github.kosmx.emotes.common.tools.ServiceLoaderUtil;
import io.github.kosmx.emotes.common.tools.UUIDMap;
import io.github.kosmx.emotes.server.config.Serializer;
import io.github.kosmx.emotes.server.serializer.type.EmoteSerializerException;
import io.github.kosmx.emotes.server.serializer.type.IReader;
import io.github.kosmx.emotes.server.serializer.type.IWriter;
import io.github.kosmx.emotes.server.services.InstanceService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public class UniversalEmoteSerializer {
   public static final List<IReader> READERS = ServiceLoaderUtil.loadServicesSorted(IReader.class).toList();
   public static final List<IWriter> WRITERS = ServiceLoaderUtil.loadServicesSorted(IWriter.class).toList();
   public static final UUIDMap<Animation> SERVER_EMOTES = new UUIDMap<>();
   public static final UUIDMap<Animation> HIDDEN_SERVER_EMOTES = new UUIDMap<>();
   private static final Comparator<IWriter> WRITTER_COMPARATOR = Comparator.<IWriter>comparingInt(w -> w.onlyEmoteFile() ? 0 : 1)
      .thenComparingInt(w -> w.possibleDataLoss() ? 0 : 1);

   public static Map<String, Animation> readData(InputStream inputStream, @Nullable String filename, String format) throws EmoteSerializerException {
      return readData(inputStream, Objects.requireNonNullElse(filename, "emote." + format));
   }

   public static Map<String, Animation> readData(InputStream inputStream, String fileName) throws EmoteSerializerException {
      if (fileName != null && !fileName.isEmpty()) {
         try {
            return findReader(fileName).orElseThrow(() -> new EmoteSerializerException("No known reader for format", fileName)).read(inputStream, fileName);
         } catch (EmoteSerializerException e) {
            throw e;
         } catch (Throwable cause) {
            throw new EmoteSerializerException("Error has occurred while serializing an emote", fileName, cause);
         }
      } else {
         throw new IllegalArgumentException("filename can not be null if no format type was given");
      }
   }

   public static Optional<IReader> findReader(String fileName) {
      return READERS.stream().filter(reader -> reader.canRead(fileName)).findFirst();
   }

   public static IWriter findWriter(@Nullable String fileName) throws EmoteSerializerException {
      return WRITERS.stream()
         .filter(writer -> fileName == null || writer.canWrite(fileName))
         .max(WRITTER_COMPARATOR)
         .orElseThrow(() -> new EmoteSerializerException("No writer has been found!", null));
   }

   public static void writeKeyframeAnimation(OutputStream stream, Animation emote, String fileName) throws EmoteSerializerException {
      IWriter writer = findWriter(fileName);
      if (writer.possibleDataLoss()) {
         CommonData.LOGGER.warn("Writing in {} format may result in data loss or incorrect playback of the final file!", writer.getExtension());
      }

      writer.write(emote, stream, fileName);
   }

   public static UUIDMap<Animation> loadEmotes() {
      SERVER_EMOTES.clear();
      HIDDEN_SERVER_EMOTES.clear();
      serializeInternalJson("waving");
      serializeInternalJson("clap");
      serializeInternalJson("crying");
      serializeInternalJson("point");
      serializeInternalJson("here");
      serializeInternalJson("palm");
      serializeInternalJson("backflip");
      serializeInternalJson("roblox_potion_dance");
      serializeInternalJson("kazotsky_kick");
      serializeInternalJson("twerk");
      serializeInternalJson("club_penguin_dance");
      Path path = InstanceService.INSTANCE.getExternalEmoteDir();
      if (!Files.isDirectory(path)) {
         try {
            Files.createDirectories(path);
         } catch (IOException var2) {
         }
      }

      EmoteSerializer.serializeEmotes(Serializer.getConfig().loadEmotesServerSide.get() ? SERVER_EMOTES : HIDDEN_SERVER_EMOTES, path);
      Path serverEmotesDir = path.resolve("server");
      if (Files.isDirectory(serverEmotesDir)) {
         EmoteSerializer.serializeEmotes(SERVER_EMOTES, serverEmotesDir);
      }

      return getLoadedEmotes();
   }

   private static void serializeInternalJson(String name) {
      if (Serializer.getConfig().loadBuiltinEmotes.get()) {
         try (InputStream stream = UniversalEmoteSerializer.class.getClassLoader().getResourceAsStream("assets/emotecraft/emotes/" + name + ".json")) {
            Map<String, Animation> emotes = readData(stream, name + ".json");

            for (Animation emote : emotes.values()) {
               emote.data().put("isBuiltin", true);
               InputStream iconStream = UniversalEmoteSerializer.class.getClassLoader().getResourceAsStream("assets/emotecraft/emotes/" + name + ".png");
               if (iconStream != null) {
                  emote.data().put("iconData", MathHelper.readFromIStream(iconStream));
                  iconStream.close();
               }
            }

            HIDDEN_SERVER_EMOTES.addAll(emotes.values());
         } catch (EmoteSerializerException | IOException e) {
            CommonData.LOGGER.warn("Failed to load built-in emote!", e);
         }
      }
   }

   @Nullable
   public static Animation getEmote(UUID uuid) {
      return SERVER_EMOTES.getOrDefault(uuid, HIDDEN_SERVER_EMOTES.get(uuid));
   }

   public static UUIDMap<Animation> getLoadedEmotes() {
      UUIDMap<Animation> map = new UUIDMap<>();
      map.putAll(HIDDEN_SERVER_EMOTES);
      map.putAll(SERVER_EMOTES);
      return map;
   }

   public static Stream<EmotePacket> preparePackets(Map<Byte, Byte> compatibilityMap) {
      return SERVER_EMOTES.values().stream().map(emote -> {
         try {
            return new EmotePacket.Builder().configureToSaveEmote(emote).setSizeLimit(1048576, false).setVersion(compatibilityMap).build();
         } catch (Throwable e) {
            CommonData.LOGGER.warn("Failed to prepare emote packet!", e);
            return null;
         }
      }).filter(Objects::nonNull);
   }
}
