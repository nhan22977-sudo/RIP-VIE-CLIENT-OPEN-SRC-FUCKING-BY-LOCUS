package io.github.kosmx.emotes.server.serializer.type;

import com.zigythebird.playeranimcore.animation.Animation;
import java.io.InputStream;
import java.util.Map;

public interface IReader extends ISerializer {
   Map<String, Animation> read(InputStream var1, String var2) throws EmoteSerializerException;

   default boolean canRead(String fileName) {
      return fileName != null && fileName.endsWith("." + this.getExtension());
   }
}
