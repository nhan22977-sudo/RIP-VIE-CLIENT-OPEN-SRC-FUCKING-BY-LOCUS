package io.github.kosmx.emotes.server.serializer.type.impl;

import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.server.config.Serializer;
import io.github.kosmx.emotes.server.serializer.type.EmoteSerializerException;
import io.github.kosmx.emotes.server.serializer.type.IReader;
import java.io.InputStream;
import java.util.Map;

public class QuarkReaderWrapper implements IReader {
   @Override
   public Map<String, Animation> read(InputStream stream, String filename) throws EmoteSerializerException {
      throw new EmoteSerializerException("Quark error", this.getExtension(), new UnsupportedOperationException("quark is not supported"));
   }

   @Override
   public String getExtension() {
      return "emote";
   }

   @Override
   public boolean isActive() {
      return IReader.super.isActive() && Serializer.getConfig().enableQuark.get();
   }
}
