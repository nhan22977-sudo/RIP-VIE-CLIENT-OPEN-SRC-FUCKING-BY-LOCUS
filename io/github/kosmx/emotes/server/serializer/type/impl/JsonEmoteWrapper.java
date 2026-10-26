package io.github.kosmx.emotes.server.serializer.type.impl;

import com.google.gson.JsonParseException;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.loading.UniversalAnimLoader;
import io.github.kosmx.emotes.server.serializer.type.EmoteSerializerException;
import io.github.kosmx.emotes.server.serializer.type.IReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

public class JsonEmoteWrapper implements IReader {
   @Override
   public Map<String, Animation> read(InputStream inputStream, String filename) throws EmoteSerializerException {
      try {
         Map<String, Animation> deserialized = UniversalAnimLoader.loadAnimations(inputStream);
         if (deserialized == null) {
            throw new IOException("Can't load emote, " + filename + " is empty.");
         } else {
            return Collections.unmodifiableMap(deserialized);
         }
      } catch (JsonParseException | IOException e) {
         throw new EmoteSerializerException("Exception has occurred", this.getExtension(), e);
      }
   }

   @Override
   public String getExtension() {
      return "json";
   }
}
